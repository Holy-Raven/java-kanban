import server.HttpTaskServer;
import server.KVServer;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.enums.Status;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        new KVServer().start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        // загрузка и вызов задач
        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);

        Task secondTask = new Task("Task 2",
                "Вторая задача",
                Status.NEW, "26.02.2023|14:00", 120);

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
                Status.DONE,"18.02.2023|13:00", 120,
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

        httpTaskServer.taskManager.loadTask(firstTask);
        httpTaskServer.taskManager.loadTask(secondTask);
        httpTaskServer.taskManager.loadTask(threeTask3);
        httpTaskServer.taskManager.loadEpicTask(firstEpicTask);
        httpTaskServer.taskManager.loadSubTask(firstStep);
        httpTaskServer.taskManager.loadSubTask(secondStep);
        httpTaskServer.taskManager.loadSubTask(thirdStep);
        httpTaskServer.taskManager.loadSubTask(fourthStep);
        httpTaskServer.taskManager.loadEpicTask(secondEpicTask);

        httpTaskServer.taskManager.getSubTask(5);
        httpTaskServer.taskManager.getEpic(4);
        httpTaskServer.taskManager.getTask(2);
        httpTaskServer.taskManager.getTask(1);
        httpTaskServer.taskManager.getSubTask(6);
        httpTaskServer.taskManager.getTask(2);
        httpTaskServer.taskManager.getSubTask(7);
        httpTaskServer.taskManager.getEpic(9);



        ///////////////////////////////////////////////////////////////////////




//        System.out.println("Удаление задач");
//        taskManager.removeTask(1);
//        taskManager.removeSubTask(8);

        // теперь загружаем информацию из файла в новый менеджер задач.
//
//        System.out.println("\nСоздали второй менеджер и загрузили задачи из файла: " + file.getAbsolutePath());
//        TaskManager loadFromFile = loadFromFile(file);
//
//        System.out.println();
//
//        assert loadFromFile != null;
//        loadFromFile.printHistoryList();
//
//        System.out.println();
//        System.out.println("Полный список задач");
//
//        for (Task task: loadFromFile.getTaskMap().values()) {
//            System.out.println(task);
//        }
//
//        System.out.println("\nОтсортированный список");
//        taskManager.printPrioritizedTasks();

    }

}
