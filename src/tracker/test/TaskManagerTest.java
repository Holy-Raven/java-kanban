
package tracker.test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.TaskManager;
import tracker.util.Status;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tracker.util.Status.*;

public abstract class TaskManagerTest <T extends TaskManager> {

    public T taskManager;

    @Test
    void loadTaskTest(){

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);
        taskManager.loadTask(firstTask);

        int taskId = firstTask.getId();
        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(savedTask, firstTask, "Задачи не совпадают.");

        List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(firstTask, tasks.get(0), "Задачи не совпадают.");

    }
    @Test
    void loadEpicTest(){


        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        int taskId = firstEpicTask.getId();
        Task savedTask = taskManager.getEpic(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(savedTask, firstEpicTask, "Задачи не совпадают.");

        // создаем списки задач и эпиков
        List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());
        List<Task> epics = new ArrayList<>(taskManager.getEpicMap().values());

        // создаем список подзадач нашего эпика
        List<Integer> subTaskList = firstEpicTask.getSubTaskList();

        // устанавливаем статус нашего эпика и проверяем его
        taskManager.instalStatusEpic(firstEpicTask);
        assertEquals(firstEpicTask.getStatus(),IN_PROGRESS);

        // проверяем не пустые ли наши списки
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertNotNull(epics, "Задачи на возвращаются.");
        assertNotNull(subTaskList, "Задачи на возвращаются.");

        // сколько задач в списке задач, эпиков и списке его подзадач
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(1, subTaskList.size(), "Неверное количество задач.");

        // совпадает ли наш эпик с задачей из наших списков
        assertEquals(firstEpicTask, tasks.get(0), "Задачи не совпадают.");
        assertEquals(firstEpicTask, epics.get(0), "Задачи не совпадают.");

    }
    @Test
    void loadSubTaskTest(){
        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        int taskId = firstStep.getId();
        Task savedTask = taskManager.getSubTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(savedTask, firstStep, "Задачи не совпадают.");

        // создаем список подзадач
        List<Task> subTasks = new ArrayList<>(taskManager.getSubTaskMap().values());

        // создаем поле с Id епика родителя
        int epicId = firstStep.getEpic();

        // Провереям есть айди у епика родителя, берем епик из мапы задач и сравниваем с нашим епиком
        assertNotNull(epicId, "Задача не найдена.");
        assertEquals(firstEpicTask, taskManager.getTaskMap().get(firstEpicTask.getId()), "Задачи не совпадают.");


        // сколько задач в списке подзадач, есть ли в нем наша задача, и не пустой ли он вообще
        assertNotNull(subTasks, "Задачи на возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(firstStep, subTasks.get(0), "Задачи не совпадают.");

    }

    @Test
    void getAllTaskMaps(){

        // создаем тестовые списки
        List <Task> taskList = new ArrayList<>();
        List <Epic> epicList = new ArrayList<>();
        List <SubTask> subTaskList = new ArrayList<>();

        // добавляем 1 таск, 1 эпик с двумя подзадачами.
        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);
        taskManager.loadTask(firstTask);
        // добавляем наш таск в мапу тасков
        taskList.add(firstTask);

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);
        // добавляем наш эпик в мапу тасков и мапу эпиков
        taskList.add(firstEpicTask);
        epicList.add(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                Status.DONE,"17.02.2023|15:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(secondStep);

        // добавляем наши подзадачи в список тасков и список подзадач
        taskList.add(firstStep);
        subTaskList.add(firstStep);
        taskList.add(secondStep);
        subTaskList.add(secondStep);

        // загружаем список значений задач из наших мап менеджера задач
        List <Task> taskMap = new ArrayList<>(taskManager.getTaskMap().values());
        List <Epic> epicMap = new ArrayList<>(taskManager.getEpicMap().values());
        List <SubTask> subTaskMap = new ArrayList<>(taskManager.getSubTaskMap().values());

        // проверяем есть ли значения в наших списках
        assertNotNull(taskMap, "Cписок task не найден.");
        assertNotNull(epicMap, "Список epic не найден.");
        assertNotNull(subTaskMap, "Список subtask не найден.");

        // проверяем идентичны ли наши списки
        assertIterableEquals(taskList, taskMap);
        assertIterableEquals(epicList, epicMap);
        assertIterableEquals(subTaskList, subTaskMap);

    }

    @Test
    void deleteAllTaskTest(){

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);
        taskManager.loadTask(firstTask);

        Task secondTask = new Task("Task 2",
                "Вторая задача",
                Status.NEW, "25.02.2023|18:00", 120);
        taskManager.loadTask(secondTask);

        Task threeTask3 = new Task("Task 3",
                "Третья задача",
                Status.NEW);
        taskManager.loadTask(threeTask3);

        // создаем список всех задач загруженных из менеджера
        List<Task> taskList = new ArrayList<>(taskManager.getTaskMap().values());

        // проверяем не пустой ли он
        assertFalse(taskList.isEmpty(), "Список задач пуст");

        // удаляем все задачи и обновляем наш список задач
        taskManager.deleteAllTask();

        taskList = new ArrayList<>(taskManager.getTaskMap().values());

        // проверяем пуст ли наш cписок
        assertTrue(taskList.isEmpty());

    }
    @Test
    void deleteAllEpicTest() {

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);
        taskManager.loadTask(firstTask);

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS, "17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        // создаем 3 списка, задач, эпиков, подзадач

        List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());
        List<Epic> epics = new ArrayList<>(taskManager.getEpicMap().values());
        List<SubTask> subTasks = new ArrayList<>(taskManager.getSubTaskMap().values());

        // проверяем не пустые ли у нас списки
        assertFalse(tasks.isEmpty());
        assertFalse(epics.isEmpty());
        assertFalse(subTasks.isEmpty());

        // проверяем содержится ли наш эпик в списке задач
        assertTrue(tasks.contains(firstEpicTask));

        taskManager.deleteAllEpic();

        // обновляем списки
        tasks = new ArrayList<>(taskManager.getTaskMap().values());
        epics = new ArrayList<>(taskManager.getEpicMap().values());
        subTasks = new ArrayList<>(taskManager.getSubTaskMap().values());

        // проверяем содержится ли наш эпик в списке задач (теперь быть не должно)
        assertFalse(tasks.contains(firstEpicTask));

        // проверяем пустые ли списки эпиков, и список подзадач
        assertTrue(epics.isEmpty());
        assertTrue(subTasks.isEmpty());

    }
    @Test
    void deleteAllSubtaskTest(){

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);
        taskManager.loadTask(firstTask);

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS, "17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        // записываем айди подзадачи
        int subtaskId = firstStep.getId();

        List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());
        List<Epic> epics = new ArrayList<>(taskManager.getEpicMap().values());
        List<SubTask> subTasks = new ArrayList<>(taskManager.getSubTaskMap().values());
        List<Integer> subTaskInEpic = firstEpicTask.getSubTaskList();

        // проверяем не пустые ли у нас списки
        assertFalse(tasks.isEmpty());
        assertFalse(epics.isEmpty());
        assertFalse(subTasks.isEmpty());
        assertFalse(subTaskInEpic.isEmpty());

        // проверяем содержится ли наша подзадача в списке задач и списке подзадач эпика родителя
        assertTrue(tasks.contains(firstEpicTask));
        assertTrue(subTaskInEpic.contains(subtaskId));

        taskManager.deleteAllSubTask();

        // обновляем списки
        tasks = new ArrayList<>(taskManager.getTaskMap().values());
        subTasks = new ArrayList<>(taskManager.getSubTaskMap().values());
        subTaskInEpic = firstEpicTask.getSubTaskList();

        // проверяем содержится ли наша подзадача в списке задач (теперь быть не должно)
        assertFalse(tasks.contains(firstStep));

        // проверяем пустой ли список подзадач, и список подзадач эпика родителя
        assertTrue(subTasks.isEmpty());
        assertTrue(subTaskInEpic.isEmpty());

    }

    //Тесты класса Epic
    @Test
    void statusNewTaskListIsEmpty() {

        Epic firstEpicTask = new Epic("Epic 1","Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        taskManager.instalStatusEpic(firstEpicTask);
        assertEquals(firstEpicTask.getStatus(),NEW);

    }
    @Test
    void statusNewTaskAllSubtaskNEW() {

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);


        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                NEW,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                NEW,"18.02.2023|13:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(secondStep);

        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
                "Третья подзадача Эпика 1",
                NEW,"19.02.2023|13:00", 10,
                firstEpicTask.getId());
        taskManager.loadSubTask(thirdStep);

        SubTask fourthStep = new SubTask("SubTask 4 Epic 1",
                "Четвертая подзадача Эпика 1",
                NEW,firstEpicTask.getId());
        taskManager.loadSubTask(fourthStep);

        taskManager.instalStatusEpic(firstEpicTask);
        assertEquals(firstEpicTask.getStatus(),NEW);

    }
    @Test
    void statusNewTaskAllSubtaskDONE() {

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);


        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                DONE,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                DONE,"18.02.2023|13:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(secondStep);

        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
                "Третья подзадача Эпика 1",
                DONE,"19.02.2023|13:00", 10,
                firstEpicTask.getId());
        taskManager.loadSubTask(thirdStep);

        SubTask fourthStep = new SubTask("SubTask 4 Epic 1",
                "Четвертая подзадача Эпика 1",
                DONE,firstEpicTask.getId());
        taskManager.loadSubTask(fourthStep);

        taskManager.instalStatusEpic(firstEpicTask);
        assertEquals(firstEpicTask.getStatus(),DONE);

    }
    @Test
    void statusNewTaskAllSubtaskNEW_DONE() {

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);


        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                DONE,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                DONE,"18.02.2023|13:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(secondStep);

        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
                "Третья подзадача Эпика 1",
                NEW,"19.02.2023|13:00", 10,
                firstEpicTask.getId());
        taskManager.loadSubTask(thirdStep);

        SubTask fourthStep = new SubTask("SubTask 4 Epic 1",
                "Четвертая подзадача Эпика 1",
                NEW,firstEpicTask.getId());
        taskManager.loadSubTask(fourthStep);

        taskManager.instalStatusEpic(firstEpicTask);
        assertEquals(firstEpicTask.getStatus(),IN_PROGRESS);

    }
    @Test
    void statusNewTaskAllSubtaskIN_PROGRESS() {

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);


        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                IN_PROGRESS,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                IN_PROGRESS,"18.02.2023|13:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(secondStep);

        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
                "Третья подзадача Эпика 1",
                IN_PROGRESS,"19.02.2023|13:00", 10,
                firstEpicTask.getId());
        taskManager.loadSubTask(thirdStep);

        SubTask fourthStep = new SubTask("SubTask 4 Epic 1",
                "Четвертая подзадача Эпика 1",
                IN_PROGRESS,firstEpicTask.getId());
        taskManager.loadSubTask(fourthStep);

        taskManager.instalStatusEpic(firstEpicTask);
        assertEquals(firstEpicTask.getStatus(),IN_PROGRESS);

    }

    @Test
    void updateTaskTest () {

        // Создаем и добавляем в менеджер 1 таск
        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);
        taskManager.loadTask(firstTask);

        int taskId = firstTask.getId();

        // Cоздаем новую задачу с Id идентичной первой задаче
        Task uploadTask = new Task("New Task 1",
                "Первая задача", taskId,
                Status.NEW, "25.02.2023|15:00", 10);

        taskManager.updateTask(uploadTask);

        //Создаем общий список задач
        List<Task> taskList = new ArrayList<>(taskManager.getTaskMap().values());

        //Проверяем есть ли новая задача в общем списке и списке задач(да)
        assertTrue(taskList.contains(uploadTask));

        // Проверим что будет если подать на входа метода null (аналогично будет и с неверным ID)
        Task nullTask = null;
        taskManager.updateTask(nullTask);

        //Обновим список
        taskList = new ArrayList<>(taskManager.getTaskMap().values());

        // Сейчас в памяти находится задача "uploadtask"
        assertTrue(taskList.contains(uploadTask));
        assertFalse(taskList.contains(nullTask));

        // Проверяем что в памяти находится обновленная задача
        assertEquals(taskManager.getTaskMap().get(taskId).getName(), "New Task 1");


    }
    @Test
    void updateEpicTest () {

        // Создаем и добавляем 1 эпик с одной подзадачей
        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS, "17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        int taskId = firstEpicTask.getId();

        // Cоздаем новую эпик задачу с Id идентичной первой задаче
        Epic uploadEpic = new Epic("New Epic 1",
                "Первый Эпик", taskId);

        taskManager.updateEpic(uploadEpic);

        //Создаем общий список эпиков и задач
        List<Task> taskList_1 = new ArrayList<>(taskManager.getTaskMap().values());
        List <Epic> epicList_1 = new ArrayList<>(taskManager.getEpicMap().values());

        // Проверяем есть ли новый эпик в общем списке и списке эпиков (да)
        assertTrue(taskList_1.contains(uploadEpic));
        assertTrue(epicList_1.contains(uploadEpic));

        // Проверим статус нашего нового епика, добавляли мы его без статуса
        assertEquals(NEW, uploadEpic.getStatus());

        // Проверим что будет если подать на входа метода null (аналогично будет и с неверным ID)
        Epic nullEpic = null;
        taskManager.updateEpic(nullEpic);

        //Обновим список
        taskList_1 = new ArrayList<>(taskManager.getTaskMap().values());
        epicList_1 = new ArrayList<>(taskManager.getEpicMap().values());

        // Сейчас в памяти находится задача "uploadEpic"
        assertTrue(taskList_1.contains(uploadEpic));
        assertTrue(epicList_1.contains(uploadEpic));
        assertFalse(taskList_1.contains(nullEpic));
        assertFalse(epicList_1.contains(nullEpic));

        // Проверяем что в памяти находится обновленная задача
        assertEquals(taskManager.getTaskMap().get(taskId).getName(), "New Epic 1");

    }
    @Test
    void updateSubtaskTest() {

        // Создаем и добавляем 1 эпик с одной подзадачей
        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                NEW, "17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        int taskId = firstStep.getId();

        // Cоздаем новую эпик задачу с Id идентичной первой задаче
        SubTask uploadSubtask = new SubTask("New SubTask 1 Epic 1",
                "Первая подзадача Эпика 1", taskId,
                DONE, "17.02.2023|12:00", 120,
                firstEpicTask.getId());

        taskManager.updateSubTask(uploadSubtask);

        //Создаем общий список задач и подзадач
        List <Task> taskList_2 = new ArrayList<>(taskManager.getTaskMap().values());
        List <SubTask> subTasksList_2 = new ArrayList<>(taskManager.getSubTaskMap().values());

        // Проверяем есть ли новая подзадача в общем списке и списке задач(да)
        assertTrue(taskList_2.contains(uploadSubtask));
        assertTrue(subTasksList_2.contains(uploadSubtask));

        // Проверим статус нашего родителя епика, был NEW должен стать DONE
        assertEquals(DONE, firstEpicTask.getStatus());

        // Проверим что будет если подать на входа метода null (аналогично будет и с неверным ID)
        SubTask nullSubTask = null;
        taskManager.updateSubTask(nullSubTask);

        //Обновим список
        taskList_2 = new ArrayList<>(taskManager.getTaskMap().values());
        subTasksList_2 = new ArrayList<>(taskManager.getSubTaskMap().values());

        // Сейчас в памяти находится задача "uploadSubtask"
        assertTrue(taskList_2.contains(uploadSubtask));
        assertTrue(subTasksList_2.contains(uploadSubtask));
        assertFalse(taskList_2.contains(nullSubTask));
        assertFalse(subTasksList_2.contains(nullSubTask));

        // Проверяем что в памяти находится обновленная задача
        assertEquals(taskManager.getTaskMap().get(taskId).getName(), "New SubTask 1 Epic 1");

    }

    @Test
    void removeTaskTest() {

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);
        taskManager.loadTask(firstTask);

        Task secondTask = new Task("Task 2",
                "Вторая задача",
                Status.NEW, "27.02.2023|14:00", 120);
        taskManager.loadTask(secondTask);

        int taskId = firstTask.getId();
        int failTaskId = 0;                                                   // 0 быть не может, все айди от 1 и выше.

        List<Integer> listId = new ArrayList<>(taskManager.getTaskMap().keySet());

        // проверяем размер списка
        assertEquals(2, listId.size());
        assertTrue(listId.contains(taskId));
        assertFalse(listId.contains(failTaskId));

        taskManager.removeTask(taskId);

        // теперь первой задачи тоже нет в списке
        listId = new ArrayList<>(taskManager.getTaskMap().keySet());

        // проверяем размер списка
        assertEquals(1, listId.size());
        assertFalse(listId.contains(taskId));

    }
    @Test
    void removeEpicTest(){

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        Epic secondEpicTask = new Epic("Epic 2",
                "Второй Эпик");
        taskManager.loadEpicTask(secondEpicTask);


        int taskId = firstEpicTask.getId();
        int failTaskId = 0;                                                   // 0 быть не может, все айди от 1 и выше.

        List<Integer> taskList = new ArrayList<>(taskManager.getTaskMap().keySet());
        List<Integer> listEpic = new ArrayList<>(taskManager.getEpicMap().keySet());

        // проверяем размер списка
        assertEquals(3, taskList.size());
        assertEquals(2, listEpic.size());

        assertTrue(listEpic.contains(taskId));
        assertFalse(listEpic.contains(failTaskId));

        taskManager.removeEpicTask(taskId);

        // теперь первой задачи тоже нет в списке
        taskList = new ArrayList<>(taskManager.getTaskMap().keySet());
        listEpic = new ArrayList<>(taskManager.getEpicMap().keySet());


        // проверяем размер списка, с нашим эпиком должна была удалиться и его подзадача
        assertEquals(1, taskList.size());
        assertEquals(1, listEpic.size());
        assertFalse(listEpic.contains(taskId));

    }
    @Test
    void removeSubtaskTest() {

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                DONE,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                NEW,"18.02.2023|18:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(secondStep);

        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
                "Третья подзадача Эпика 1",
                Status.DONE,"19.02.2023|13:00", 10,
                firstEpicTask.getId());
        taskManager.loadSubTask(thirdStep);


        int taskId = secondStep.getId();
        int failTaskId = 0;                                                   // 0 быть не может, все айди от 1 и выше.


        // создаем списки задач
        List<Integer> taskList = new ArrayList<>(taskManager.getTaskMap().keySet());
        List<Integer> epicList = new ArrayList<>(taskManager.getEpicMap().keySet());
        List<Integer> subtaskList = new ArrayList<>(taskManager.getSubTaskMap().keySet());
        List<Integer> subtaskInEpic = new ArrayList<>(firstEpicTask.getSubTaskList());

        // проверяем их размер
        assertEquals(4, taskList.size());
        assertEquals(1, epicList.size());
        assertEquals(3, subtaskList.size());
        assertEquals(3, subtaskInEpic.size());

        // входит ли наш первый айди в эти списки (кроме списка эпиков)
        assertTrue(taskList.contains(taskId));
        assertTrue(subtaskList.contains(taskId));
        assertTrue(subtaskInEpic.contains(taskId));

        // входит ли наш второй айди в эти списки (кроме списка эпиков)
        assertFalse(taskList.contains(failTaskId));
        assertFalse(subtaskList.contains(failTaskId));
        assertFalse(subtaskInEpic.contains(failTaskId));

        assertEquals(IN_PROGRESS, firstEpicTask.getStatus());

        taskManager.removeSubTask(taskId);

        // Обновили списки
        taskList = new ArrayList<>(taskManager.getTaskMap().keySet());
        epicList = new ArrayList<>(taskManager.getEpicMap().keySet());
        subtaskList = new ArrayList<>(taskManager.getSubTaskMap().keySet());
        subtaskInEpic = new ArrayList<>(firstEpicTask.getSubTaskList());


        assertEquals(3, taskList.size());
        assertEquals(1, epicList.size());
        assertEquals(2, subtaskList.size());
        assertEquals(2, subtaskInEpic.size());

        // Проверим есть ли наша удаленная задача в списках задач и списке подзадач эпика
        assertFalse(taskList.contains(taskId));
        assertFalse(subtaskList.contains(taskId));
        assertFalse(subtaskInEpic.contains(taskId));

        assertEquals(DONE, firstEpicTask.getStatus());

    }

    @Test
    void getTaskTest() {

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);
        taskManager.loadTask(firstTask);

        int taskId = firstTask.getId();
        int taskFailId = 4;

        // Загрузим полученную задачу в новое поле
        Task copyTask = taskManager.getTask(taskId);

        // сравниваем полученную задачу с исходной
        assertEquals(firstTask, copyTask);

        // Попробуем загрузить задачу с Айди котрого нет в менеджере

        copyTask = taskManager.getTask(taskFailId);
        // сравниваем полученную задачу с исходной (Теперь не равно)
        assertNotEquals(firstTask, copyTask);
        // потому что copyTask == null
        assertEquals(null, copyTask);

    }
    @Test
    void getEpicTest() {

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        int taskId = firstEpicTask.getId();
        int taskFailId = 0;

        // Загрузим полученную задачу в новое поле
        Task copyEpic = taskManager.getEpic(taskId);

        // сравниваем полученную задачу с исходной
        assertEquals(firstEpicTask, copyEpic);

        // Попробуем загрузить задачу с Айди которого нет в менеджере
        copyEpic = taskManager.getEpic(taskFailId);
        // сравниваем полученную задачу с исходной (Теперь не равно)
        assertNotEquals(firstEpicTask, copyEpic);
        // потому что copyTask == null
        assertEquals(null, copyEpic);

    }
    @Test
    void getSubTaskTest(){

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS,"17.02.2023|12:00", 120,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        int taskId = firstStep.getId();
        int taskFailId = 0;

        // Загрузим полученную задачу в новое поле
        Task copySubTask = taskManager.getSubTask(taskId);

        // сравниваем полученную задачу с исходной
        assertEquals(firstStep, copySubTask);

        // Попробуем загрузить задачу с Айди которого нет в менеджере
        copySubTask = taskManager.getSubTask(taskFailId);
        // сравниваем полученную задачу с исходной (Теперь не равно)
        assertNotEquals(firstStep, copySubTask);
        // потому что copyTask == null
        assertEquals(null, copySubTask);

    }

    @Test
    void intersectionTaskInSetTest() {

        // Создаем задачи
        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);

        Task secondTask = new Task("Task 2",
                "Вторая задача",
                Status.NEW, "25.02.2023|14:00", 120);

        Task threeTask3 = new Task("Task 3",
                "Третья задача",
                Status.NEW);

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS,"17.02.2023|12:00", 120,
                firstEpicTask.getId());

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                Status.DONE,"17.02.2023|13:00", 120,
                firstEpicTask.getId());

        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
                "Третья подзадача Эпика 1",
                Status.DONE,firstEpicTask.getId());

        SubTask fourthStep = new SubTask("SubTask 4 Epic 1",
                "Четвертая подзадача Эпика 1",
                Status.DONE,"19.02.2023|13:00", 10,
                firstEpicTask.getId());

        Epic secondEpicTask = new Epic("Epic 2",
                "Второй Эпик");

        // Проверим пустой ли наш список изначально
        assertNull(taskManager.getPrioritizedTasks());

        // Загружаем
        taskManager.loadTask(firstTask);
        taskManager.loadTask(secondTask);
        taskManager.loadTask(threeTask3);
        taskManager.loadEpicTask(firstEpicTask);
        taskManager.loadSubTask(firstStep);
        taskManager.loadSubTask(secondStep);
        taskManager.loadSubTask(thirdStep);
        taskManager.loadSubTask(fourthStep);
        taskManager.loadEpicTask(secondEpicTask);

        // Task 2 и SubTask 2 Epic 1 имеют перечения во времени, они не будут добавлены
        // Проверим ести ли зти задачи в нашем списке

        assertFalse(taskManager.getPrioritizedTasks().contains(secondTask));
        assertFalse(taskManager.getPrioritizedTasks().contains(secondTask));

        // Проверим размер нашего отсортированного списка задач, без Task 2 и SubTask 2 Epic 1 должно быть
        // Epic мы сюда не загружаем итого должно быть 5
        assertEquals(5, taskManager.getPrioritizedTasks().size());

        // проверим верно ли установилось время нашего Эпика
        assertEquals(firstEpicTask.getStartTimeString(), firstStep.getStartTimeString());
        assertEquals(firstEpicTask.getEndTimeString(), fourthStep.getEndTimeString());

        // Проверим в нужном ли месте находятся задачи которые не имеют стратового времени, создадим копия списка
        List<Task> list = new ArrayList<>();

        for (Task prioritizedTask : taskManager.getPrioritizedTasks()) {
            list.add(prioritizedTask);
        }

        // Cперва в конец упал  Task 3, потом SubTask 4 и занял последнее место в списке
        assertEquals(threeTask3, list.get(list.size()-2));
        assertEquals(thirdStep, list.get(list.size()-1));

        // Теперь удалим из менеджера одну Task 1
        taskManager.removeTask(firstTask.getId());

        // В нашем списке должно остаться 4 задачи
        assertEquals(4, taskManager.getPrioritizedTasks().size());

    }

}