package tracker.service;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static tracker.util.Status.*;

public class Manager {
    private int id;

    public Manager(int id) {
        this.id = id;
    }
    // Cписок всех задач.
    private final HashMap <Integer, Task> taskMap = new HashMap<>();
    // Список всех эпик задач.
    private final HashMap <Integer, Epic> epicMap = new HashMap<>();
    // Список всех подзадач.
    private final HashMap <Integer, SubTask> subTaskMap = new HashMap<>();

    // На вход метода подается задача, если там не null, то начинаем выполннение нашего алгоритма. Делаем проверку
    // класса нашей задачи. Проверяем это задача класса Эпик? Если да, то загружаем ее в общий список и список эпик,
    // обновляем ее статус. Если задача класса Субтаск, то вносим айди задачи в список родительского эпика после этого
    // загружаем в общий список задач и подзадач. Тут обновляем статус родителя Субтаска. Если задача и не Эпик и не
    // Субтакс, значит она класса Таск, записываем ее в общий список задач. Если на вход пришел null, о выводим
    // пользователю, что задача не найдена.

    public void loadTask(Task task) {

        if (task != null) {
            if (task instanceof Epic epic) {
                taskMap.put(epic.getId(), epic);
                epicMap.put(epic.getId(), epic);
                instalStatusEpic(epic);
            } else if (task instanceof SubTask subTask) {

                epicMap.get(subTask.getEpic()).getSubTaskList().add(subTask.getId());
                taskMap.put(subTask.getId(), subTask);
                subTaskMap.put(subTask.getId(), subTask);
                instalStatusEpic(epicMap.get(subTask.getEpic()));

            } else {
                taskMap.put(task.getId(), task);
            }
        } else {
            System.out.println("Сбой, задача не найдена.");
        }
    }
    // Получение списка всех задач.
    public HashMap<Integer, Task> getTaskMap() { return taskMap; }

    // Получение списка всех эпик задач.
    public HashMap<Integer, Epic> getEpicMap() { return epicMap; }

    // Получение списка всех подзадач.
    public HashMap<Integer, SubTask> getSubTaskMap() { return subTaskMap; }

    // Удаление всех задач.
    public void deleteAllTask() {
        taskMap.clear();
        epicMap.clear();
        subTaskMap.clear();
    }

    // Удаление всех эпик задач.
    public void deleteAllEpic() {
        epicMap.clear();
        subTaskMap.clear();
    }

    // Удаление всех подзадач.
    public void deleteAllSubTask() {
        subTaskMap.clear();
    }

    // Установка статуса Эпика
    // Создаем два флага (NEW и DONE), сперва мы проверяем не пустой ли список подзадач внутри эпика, если список пустой
    // то ставим: flagNew = true и flagDone = false. Если список не пустой то, начинаем перебирать объекты списка и
    // смотреть статусы каждой подзадачи, если одновременно выполняются условия, что статус задачи NEW и FlagNew == true,
    // то мы сохраняем значение флага, если же одно из этих условий не верно, то мы меняем flagNew - false, тогда значит
    // статус подзадачи уже не может быть NEW, такую же проверку мы делаем для DONE, по итогу если оба наши флага по
    // завершению метода стали false, то значит статус нашей задачи IN_PROGRESS.

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
    }

    // Обновление задачи
    // Сперва по ключу входящей задачи мы проверяем есть ли в общем списке задач такая задача, если находим ее там то
    // обновляем. Мы нашли задачу в общем списке, значит она может быть и в списке эпиков или списке подзадач. Сперва
    // проверяем является ли входящая задача эпиком, если да, то значит она есть в списке эпиков, обновляем ее там.
    // Если задача не является эпиком, то мы идем дальше и проверяем является ли задача подзадачей, если да, то меняем
    // подзадачу в списке подзадач, после этого обновляем статус Эпика-родителя. Если в общем списке нет указанной
    // задачи то, говорим пользователю, что такой задачи нет, заменить не выйдет.

    public void updateTask (Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
            if (task instanceof Epic epic) {
                epicMap.put(epic.getId(), epic);
                instalStatusEpic(epic);
            }
            if (task instanceof SubTask subTask) {
                subTaskMap.put(subTask.getId(), subTask);
                instalStatusEpic(epicMap.get(subTask.getEpic()));
            } else {
                System.out.println("Такой задачи не стоит");
            }
        }
    }

    // Удаление задачи по ID
    // Проверяем, есть ли задача с таким ID в общем списке задач. Тут же проверяем есть ли задача в списке эпиков, если
    // находим, то мы должны удалить все ее подзадачи в общем списке и списке подзадач, после этого удаляем и сам эпик
    // из списка эпиков. Мы можем не найти задачу в списке эпиков, тогда запускаем проверку в списке подзадач, если
    // находим, то берем эту подзадачу и удаляем ее в списке подзадач, обновляем статус эпика. Если в общем списке нет
    // задачи с таким ID то, говорим пользователю, что такой задачи нет, удалить ничего не выйдет.

    public void removeTask(int id) {
        if (taskMap.containsKey(id)) {

            if (epicMap.containsKey(id)) {
                for (Integer idSubTask : epicMap.get(id).getSubTaskList()) {
                    subTaskMap.remove(idSubTask);
                    taskMap.remove(idSubTask);
                }
                epicMap.remove(id);
            }

            if (subTaskMap.containsKey(id)) {
                subTaskMap.remove(id);
                instalStatusEpic(epicMap.get(subTaskMap.get(id).getEpic()));
            }

            if (!epicMap.containsKey(id) && !subTaskMap.containsKey(id)){
                taskMap.remove(id);
            }

        } else {
            System.out.println("Такой задачи не стоит");
        }
    }

    // Получение списка подзадач определенного эпика
    // Обращаемся к списку задач указанного эпика и если он не пустой, выдаем все подзадачи с айди из этого списка,
    // иначе говорим что у данного эпика нет подзадач.

    public List<SubTask> getListSubTaSkForEpic(Epic epic) {
        List<SubTask> listSubTaSkForEpic = new ArrayList<>();

        if (epic.getSubTaskList() != null) {
            for (Integer subTaskId : epic.getSubTaskList()) {
                listSubTaSkForEpic.add(subTaskMap.get(subTaskId));
            }
            return listSubTaSkForEpic;
        }
        System.out.println("Список пуст, у данного эпика нет подзадач");
        return null;
    }

    // Такого метода не было в задании, но мне кажется он еще пригодиться в будущем.
    // true - задача завершена

    public boolean isEndedTask(Task task) {

        boolean flag = false;
        switch (task.getStatus()) {
            case NEW:
                task.setStatus(NEW);
                break;
            case IN_PROGRESS:
                task.setStatus(IN_PROGRESS);
                flag = false;
                break;
            case DONE:
                task.setStatus(DONE);
                flag = true;
        }
        return flag;
    }

    public int getId() {
        return id++;
    }
}