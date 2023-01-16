import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.InMemoryTaskManager;
import tracker.util.Managers;
import tracker.util.Status;

public class Main {

    public static void main(String[] args) {

        Managers manager = new Managers();
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) manager.getDefault();

        //System.out.println("Cоздание отдельных задач");

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW);
        inMemoryTaskManager.loadTask(firstTask);

        Task secondTask = new Task("Task 2",
                "Вторая задача",
                Status.NEW);
        inMemoryTaskManager.loadTask(secondTask);
        //===================================================================
        //System.out.println("Создание эпика и три подзадачи в нем");

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        inMemoryTaskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS,
                firstEpicTask.getId());
        inMemoryTaskManager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                Status.DONE,
                firstEpicTask.getId());
        inMemoryTaskManager.loadSubTask(secondStep);

        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
                "Третья подзадача Эпика 1",
                Status.DONE,
                firstEpicTask.getId());
        inMemoryTaskManager.loadSubTask(thirdStep);

        //===================================================================
        //System.out.println("Создание эпика без подзадач в нем");

        Epic secondEpicTask = new Epic("Epic 2",
                "Второй Эпик");
        inMemoryTaskManager.loadEpicTask(secondEpicTask);

        //===================================================================


        //===================================================================
        //проверяем работу списка просмотров задач
//        System.out.println();
//        System.out.println("Такие задачи не найдет");
//
//        inMemoryTaskManager.getSubTask(10);
//        inMemoryTaskManager.getSubTask(6);
//        inMemoryTaskManager.getTask(3);
//        inMemoryTaskManager.getEpic(4);
//        System.out.println();

        System.out.println("Такие задачи сможем просмотреть");
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getSubTask(4);  // 10-ая c конца это тут
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getSubTask(5);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.getEpic(7);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getSubTask(6);

        inMemoryTaskManager.printHistoryList();
        System.out.println();

        System.out.println("Удаление задач");
        //inMemoryTaskManager.removeTask(1);
        inMemoryTaskManager.removeTask(2);
        inMemoryTaskManager.removeEpicTask(7);
        inMemoryTaskManager.removeSubTask(4);
        System.out.println();

        inMemoryTaskManager.printHistoryList();

        //===================================================================
//        System.out.println("Выводим полный список задач");
//        for (Task task: inMemoryTaskManager.getTaskMap().values()) {
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