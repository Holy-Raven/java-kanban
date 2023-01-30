import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.TaskManager;
import tracker.util.Managers;
import tracker.util.Status;

public class Main {

    public static void main(String[] args) {

        Managers manager = new Managers();

        TaskManager taskManager = manager.getDefault();

        //System.out.println("Cоздание отдельных задач");

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW);
        taskManager.loadTask(firstTask);

        Task secondTask = new Task("Task 2",
                "Вторая задача",
                Status.NEW);
        taskManager.loadTask(secondTask);
        //===================================================================
        //System.out.println("Создание эпика и три подзадачи в нем");

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS,
                firstEpicTask.getId());
        taskManager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                Status.DONE,
                firstEpicTask.getId());
        taskManager.loadSubTask(secondStep);

        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
                "Третья подзадача Эпика 1",
                Status.DONE,
                firstEpicTask.getId());
        taskManager.loadSubTask(thirdStep);

        //===================================================================
        //System.out.println("Создание эпика без подзадач в нем");

        Epic secondEpicTask = new Epic("Epic 2",
                "Второй Эпик");
        taskManager.loadEpicTask(secondEpicTask);

        //===================================================================


        //===================================================================
        //проверяем работу списка просмотров задач
//        System.out.println();
//        System.out.println("Такие задачи не найдет");
//
//        taskManager.getSubTask(10);
//        taskManager.getSubTask(6);
//        taskManager.getTask(3);
//        taskManager.getEpic(4);
//        System.out.println();

        //System.out.println("Такие задачи сможем просмотреть");
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getTask(1);
        taskManager.getSubTask(4);  // 10-ая c конца это тут
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getSubTask(5);
        taskManager.getEpic(3);
        taskManager.getEpic(7);
        taskManager.getTask(1);
        taskManager.getSubTask(4);
        taskManager.getTask(2);
        taskManager.getSubTask(6);

        taskManager.printHistoryList();
        System.out.println();

//        System.out.println("Удаление задач");
//        //taskManager.removeTask(2);
//        taskManager.removeEpicTask(7);
//        taskManager.removeSubTask(4);
//        System.out.println();
//
//        taskManager.printHistoryList();

        //===================================================================
//        System.out.println("Выводим полный список задач");
//        for (Task task: taskManager.getTaskMap().values()) {
//            System.out.println(task);
//        }
//        System.out.println();
//
//        System.out.println("Выводим список эпик задач");
//        for (Epic epic : inMemoryTaskManager.getEpicMap().values()) {
//            System.out.println(epic);
//        }
//        System.out.println();
//
//        System.out.println("Выводим список подзадач");
//        for (SubTask subTask : inMemoryTaskManager.getSubTaskMap().values()) {
//            System.out.println(subTask);
//        }
//
//        System.out.println();

    }
}
