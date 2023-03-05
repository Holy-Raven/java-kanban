
package tracker.service;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.Managers;

import java.time.LocalDateTime;
import java.util.*;

import static tracker.util.Status.*;
import static tracker.util.TaskType.*;

public class InMemoryTaskManager implements TaskManager {

    private static int id = 1;

    protected HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    // Cписок всех задач.
    protected final HashMap<Integer, Task> taskMap = new HashMap<>();
    // Список всех эпик задач.
    protected final HashMap<Integer, Epic> epicMap = new HashMap<>();
    // Список всех подзадач.
    protected final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();

    protected Set<Task> prioritizedMap = new TreeSet<>((o1, o2) -> {

        if (o1.getStartTime() == null || o2.getStartTime() == null) {
            return 1;
        } else {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }

//        try {
//            return o1.getStartTime().compareTo(o2.getStartTime());
//        } catch (NullPointerException e) {
//            return 1;
//        }

    });

    // Создаем поле флаг. Проверяем не пустой ли у нас список, если пустой то значение флага не изменилось и мы добавляем
    // задачу в список. Потом проверяем на условия пересечения задач. Тут у нас может быть два случая выдающих исключение.
    // Первый случай если обрабатываемая задача без старттайм, тогда мы считаем, что пересечения невозможны и добавляем
    // ее в список (она упадет в конец). И второй случай - это если задача из списка без старттайма, это значит что мы
    // прошли весь список до самого конца (где все задачи без старттайма), а значит можем добавить задачу в список. Когда
    // же мы находим пересечение, то меняем значение флага записываем имя проблемной задачи. Проверяем значение флага,
    // если изменение было - выдаем об этом сообщение если нет добавляем файл.
    public void addTaskInSet(Task task) {

        boolean flag = false;

        if (prioritizedMap.isEmpty()) {

        } else {

            try {

                for (Task priorTask : prioritizedMap) {

                    if (task.getStartTime().isBefore(priorTask.getEndTime()) && task.getStartTime().isAfter(priorTask.getStartTime())
                            || task.getStartTime().isBefore(priorTask.getStartTime()) && task.getEndTime().isAfter(priorTask.getStartTime())) {
                        flag = true;
                        break;
                    }
                }

            } catch (NullPointerException e) {
                flag = false;
            }
        }

        if (flag) {
            System.out.println("пересечение во времени, задача " + task.getName() + " не смогла быть добавлена в отсортированный список задач");
        } else {
            prioritizedMap.add(task);
        }
    }

    public void removeTaskFromSet(Task task) {

        if (task != null){
            prioritizedMap.remove(task);
        } else {
            System.out.println("ошибка");
        }

    }

    public void printPrioritizedTasks() {

        for (Task task : getPrioritizedTasks()) {
            System.out.println(task.getStartTimeString() + " - " + task.getEndTimeString() + " " + task.getName());
        }
    }

    public Set<Task> getPrioritizedTasks() {

        Set<Task> setList = null;

        if (!prioritizedMap.isEmpty()) {
            setList = prioritizedMap;
        }

        return setList;
    }

    // Принимаем список из поля inMemoryHistoryManager, проверяем не пустой ли он, и если он не пустой то выводим в
    // консоль все его содержимое. Если список пустой, то говорим что список истории задач пуст.
    @Override
    public void printHistoryList() {

        if (inMemoryHistoryManager.getHistory() != null) {
            System.out.println("История запросов:");
            for (Task task : inMemoryHistoryManager.getHistory()) {
                System.out.println(task);
            }
        } else {
            System.out.println("Cписок истории задач пуст");
        }
    }

    @Override
    public List<Task> getHistory(){
        return inMemoryHistoryManager.getHistory();
    }

    // На вход метода подается задача, если там не null, то загружаем ее в общий список задач. Если на вход пришел
    // null, то выводим пользователю, что задача не найдена.
    @Override
    public void loadTask(Task task) {

        if (task != null) {
            taskMap.put(task.getId(), task);
            instalTaskType(task);
            addTaskInSet(task);
        } else {
            System.out.println("Сбой, задача не найдена.");
        }
    }

    // На вход метода подается Эпик задача, если там не null, то загружаем ее в общий список задач и список эпиков.
    // Обновляем ее статус. Если на вход пришел null, о выводим пользователю, что задача не найдена.
    @Override
    public void loadEpicTask(Epic epic) {

        if (epic != null) {
            taskMap.put(epic.getId(), epic);
            epicMap.put(epic.getId(), epic);

            instalTaskType(epic);
            instalStatusEpic(epic);

        } else {
            System.out.println("Сбой, задача не найдена.");
        }
    }

    // На вход метода подается СубТаск, если там не null, вносим айди задачи в список родительского эпика после
    // этого загружаем ее в общий список задач и список подзадач. Обновляем статус родителя. Если на вход пришел
    // null, о выводим пользователю, что задача не найдена.
    @Override
    public void loadSubTask(SubTask subTask) {

        Task task = subTask;

        if (subTask != null) {
            epicMap.get(subTask.getEpic()).getSubTaskList().add(subTask.getId());
            taskMap.put(subTask.getId(), subTask);
            subTaskMap.put(subTask.getId(), subTask);

            instalTaskType(subTask);
            instalStatusEpic(epicMap.get(subTask.getEpic()));
            addTaskInSet(subTask);

        } else {
            System.out.println("Сбой, задача не найдена.");
        }
    }

    // Получение списка всех задач.
    @Override
    public HashMap<Integer, Task> getTaskMap() { return taskMap; }

    // Получение списка всех эпик задач.
    @Override
    public HashMap<Integer, Epic> getEpicMap() { return epicMap; }

    // Получение списка всех подзадач.
    @Override
    public HashMap<Integer, SubTask> getSubTaskMap() { return subTaskMap; }

    // Удаление всех задач.
    @Override
    public void deleteAllTask() {

        deleteAllEpic();

        List<Integer> list = new ArrayList<>(taskMap.keySet());

        if (!list.isEmpty()) {
            for (Integer id : list) {
                removeTask(id);
            }
        }
        prioritizedMap.clear();
    }

    // Удаление всех эпик задач.
    @Override
    public void deleteAllEpic() {

        List<Integer> epicKey = new ArrayList<>(epicMap.keySet());

        if (!epicKey.isEmpty()) {
            for (Integer keyId : epicKey) {
                removeEpicTask(keyId);
            }
        }
    }

    // Удаление всех подзадач.
    @Override
    public void deleteAllSubTask() {

        List<Integer> subTaskKey = new ArrayList<>(subTaskMap.keySet());

        for (Integer epicId : epicMap.keySet()) {
            epicMap.get(epicId).getSubTaskList().clear();
        }

        if (!subTaskKey.isEmpty()) {
            for (Integer keyId : subTaskKey) {
                removeSubTask(keyId);
            }
        }


    }

    // Установка статуса Эпика
    // Создаем два флага (NEW и DONE), сперва мы проверяем не пустой ли список подзадач внутри эпика, если список пустой
    // то ставим: flagNew = true и flagDone = false. Если список не пустой то, начинаем перебирать объекты списка и
    // смотреть статусы каждой подзадачи, если одновременно выполняются условия, что статус задачи NEW и FlagNew == true,
    // то мы сохраняем значение флага, если же одно из этих условий не верно, то мы меняем flagNew - false, тогда значит
    // статус подзадачи уже не может быть NEW, такую же проверку мы делаем для DONE, по итогу если оба наши флага по
    // завершению метода стали false, то значит статус нашей задачи IN_PROGRESS.
    @Override
    public void instalStatusEpic(Epic epic) {
        boolean flagNew = true;
        boolean flagDone = true;

        if (epic.getSubTaskList().isEmpty()) {
            flagNew = true;
            flagDone = false;

        } else {

            for (Integer id : epic.getSubTaskList()) {

                if (subTaskMap.get(id).getStatus().equals(NEW) && flagNew) {
                    flagNew = true;
                } else {
                    flagNew = false;
                }

                if (subTaskMap.get(id).getStatus().equals(DONE) && flagDone) {
                    flagDone = true;
                } else {
                    flagDone = false;
                }
            }
        }
        if (flagNew) {
            epic.setStatus(NEW);
        } else if (flagDone) {
            epic.setStatus(DONE);
        } else {
            epic.setStatus(IN_PROGRESS);
        }

        instalStartTimeAndDuration(epic);
    }

    //Проверяем не пустой ли у нас список подзадач в эпике, если пустой то говорим что нет старта и продолжительност = 0
    //Если список не пустой, то создаем две переменной класса Локал и одну с длительностью, и проходя цикл находим наиранний
    //старт в наших подзадачах И наипоздний конец, а так же сумму длительности все х подзадач. Потом загружаем все в наш эпик.
    public void instalStartTimeAndDuration(Epic epic) {

        LocalDateTime dataStartTime;
        LocalDateTime dataEndTime;
        int duration;

        if (epic.getSubTaskList().isEmpty()) {
            epic.setStartTimeString("время начала не указано");
            epic.setDuration(0);
        } else {

            dataStartTime = taskMap.get(epic.getSubTaskList().get(0)).getStartTime();
            dataEndTime = taskMap.get(epic.getSubTaskList().get(0)).getEndTime();
            duration = taskMap.get(epic.getSubTaskList().get(0)).getDuration();

            for (int i = 1; i < epic.getSubTaskList().size(); i++) {

                try {
                    if (taskMap.get(epic.getSubTaskList().get(i)).getStartTime().isBefore(dataStartTime)) {
                        dataStartTime = taskMap.get(epic.getSubTaskList().get(i)).getStartTime();
                    }

                    duration += taskMap.get(epic.getSubTaskList().get(i)).getDuration();

                    if (taskMap.get(epic.getSubTaskList().get(i)).getEndTime().isAfter(dataEndTime)) {
                        dataEndTime = taskMap.get(epic.getSubTaskList().get(i)).getEndTime();
                    }
                } catch (NullPointerException ignored) {

                }
            }

            epic.setStartTime(dataStartTime);
            epic.setDuration(duration);
            epic.setEndTime(dataEndTime);
        }
    }

    public static void instalTaskType(Task task){

        if (task instanceof Epic){
            task.setTaskType(EPIC);
        } else if (task instanceof SubTask){
            task.setTaskType(SUBTASK);
        } else {
            task.setTaskType(TASK);
        }

    }

    // Сперва по ключу входящей задачи мы проверяем есть ли в общем списке задач такая задача, и есть ли она вообще -
    // не пришел ли на вход метода null, если находим ее там, то обновляем в общем списке. Если на вход пришел null,
    // то выводим пользователю, что задача не найдена.
    @Override
    public void updateTask (Task task) {

        if (task != null && taskMap.containsKey(task.getId())) {
            instalTaskType(task);
            taskMap.put(task.getId(), task);
        } else {
            //System.out.println("Сбой, задача не найдена.");
        }
    }

    // Сперва по ключу входящей задачи мы проверяем есть ли в общем списке задач такая задача, и есть ли она вообще -
    // не пришел ли на вход метода null, если находим ее там то обновляем в общем списке и списке эпиков. После этого
    // обновляем и статус задачи. Если на вход пришел null, то выводим пользователю, что задача не найдена.
    @Override
    public void updateEpic (Epic epic) {

        if (epic != null && taskMap.containsKey(epic.getId())) {
            instalTaskType(epic);
            taskMap.put(epic.getId(), epic);
            epicMap.put(epic.getId(), epic);
            instalStatusEpic(epic);
        } else {
            //System.out.println("Сбой, задача не найдена.");
        }
    }

    // Сперва по ключу входящей задачи мы проверяем есть ли в общем списке задач такая задача, и есть ли она вообще -
    // не пришел ли на вход метода null, если находим ее там то обновляем подзадачу в общем списке и списке подзадач,
    // после этого обновляем статус Эпика-родителя. Если на вход пришел null, то выводим пользователю, что задача
    // не найдена.
    @Override
    public void updateSubTask (SubTask subTask) {

        if (subTask != null && taskMap.containsKey(subTask.getId())) {
            instalTaskType(subTask);
            taskMap.put(subTask.getId(), subTask);
            subTaskMap.put(subTask.getId(), subTask);
            instalStatusEpic(epicMap.get(subTask.getEpic()));
        } else {
            // System.out.println("Сбой, задача не найдена.");
        }
    }

    // Удаление задачи по ID
    // Проверяем, есть ли задача с таким ID в общем списке задач если находим, то удаляем. И удаляем из списка просмотров.
    // Если в списке нет задачи с таким ID то, говорим пользователю, что такой задачи нет, удалить ничего не выйдет.
    @Override
    public void removeTask(int id) {

        if (taskMap.containsKey(id)) {

            removeTaskFromSet(taskMap.get(id));           //Удаляем задачу из списка приоритетов
            taskMap.remove(id);

            //inMemoryHistoryManager.remove(id);

        } else {
            System.out.println("Сбой, задача не найдена.");
        }
    }

    // Проверяем есть ли задача в списке эпиков, если находим, то мы должны удалить все ее подзадачи в общем списке,
    // списке подзадач и истории просмотров, после этого удаляем и сам эпик из списка эпиков, списка задач и истории
    // просмотров.  Если в списке нет задачи с таким ID то, говорим пользователю, что такой задачи нет, удалить ничего не выйдет.
    @Override
    public void removeEpicTask(int id) {

        if (epicMap.containsKey(id)) {

            for (Integer idSubTask : epicMap.get(id).getSubTaskList()) {
                removeTaskFromSet(taskMap.get(idSubTask));           //Удаляем подзадачу из списка приоритетов
                subTaskMap.remove(idSubTask);
                taskMap.remove(idSubTask);
                inMemoryHistoryManager.remove(idSubTask);
            }

            removeTaskFromSet(taskMap.get(id));                      //Удаляем эпик из списка приоритетов
            taskMap.remove(id);
            epicMap.remove(id);

            inMemoryHistoryManager.remove(id);

        } else {
            System.out.println("Сбой, задача не найдена.");
        }
    }

    // Проверяем есть ли задача в списке подзадач, если находим, то записываем id эпика родителя в отдельную переменную,
    // после этого удаляем id подзадачи из внутреннего списка id подзадач в эпике родителе. Удаляем подзадачу из общего
    // списка, списка подзадач и истории просмотров. Обновляем статус эпика родителя. Если в списке нет задачи с таким ID
    // то, говорим пользователю, что такой задачи нет, удалить ничего не выйдет.
    @Override
    public void removeSubTask(Integer id) {

        if (subTaskMap.containsKey(id)) {

            Integer idEpicParent = subTaskMap.get(id).getEpic();
            epicMap.get(idEpicParent).getSubTaskList().remove(id);

            removeTaskFromSet(taskMap.get(id));           //Удаляем задачу из списка приоритетов

            taskMap.remove(id);
            subTaskMap.remove(id);

            inMemoryHistoryManager.remove(id);            //удаляем подзадачу из списка просмотров
            instalStatusEpic(epicMap.get(idEpicParent));


        } else {
            System.out.println("Сбой, задача не найдена.");
        }
    }

    // Выдать Task по его id
    // создаем объект Task. Обращаемся в полный список задач, если задача с указанным айди не null и не является ни Epic
    // ни subTask то записываем значение ячейки в returnTask. Обновляем список истории запросов и выводим запрос на экран.
    // Если какое-то условие не выполнено, выводим на экран, что такой Task задачи не найдено.
    @Override
    public Task getTask(int id) {
        Task returnTask = null;

        if ((taskMap.get(id) != null) && !(taskMap.get(id) instanceof SubTask) && !(taskMap.get(id) instanceof Epic)) {
            returnTask =  taskMap.get(id);
            inMemoryHistoryManager.add(taskMap.get(id));
        }

        return returnTask;
    }

    // Выдать subTask по его id
    // создаем объект subTask. Обращаемся в полный список задач, если задача с указанным айди не null и является
    // subTask то записываем значение ячейки в returnSubTask. Обновляем список истории запросов и выводим запрос на экран.
    // Если какое-то условие не выполнено, выводим на экран, что такой subTask задачи не найдено.
    @Override
    public SubTask getSubTask(int id) {

        SubTask returnSubTask = null;

        if (taskMap.get(id) != null && taskMap.get(id) instanceof SubTask subTask) {
            returnSubTask =  subTask;
            inMemoryHistoryManager.add(taskMap.get(id));
        }

        return returnSubTask;

    }

    // Выдать subTask по его id
    // создаем объект Epic. Обращаемся в полный список задач, если задача с указанным айди не null и является Epic
    // то записываем значение ячейки в returnSubTask. Обновляем список истории запросов и выводим запрос на экран.
    // Если какое-то условие не выполнено, выводим на экран, что такой Epic задачи не найдено.
    @Override
    public Epic getEpic(int id) {
        Epic returnEpic = null;

        if (taskMap.get(id) != null && taskMap.get(id) instanceof Epic epic){
            returnEpic = epic;
            inMemoryHistoryManager.add(taskMap.get(id));
        }
        return returnEpic;
    }

    public void setInMemoryHistoryManager(HistoryManager inMemoryHistoryManager) {
        this.inMemoryHistoryManager = inMemoryHistoryManager;
    }

    public static int getId() {
        return id++;
    }
}