
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.TaskManager;
import tracker.util.Managers;
import tracker.util.Status;
import java.io.File;

import static tracker.service.FileBackedTasksManager.loadFromFile;

// Да тут в этом спринте самая большая проблема не только лишь в том, что тут надо много сделать, а в том что я стала писать
// тесты и появилось/выявилось много ошибок в коде, и я считай почти неделю только эти ошибки фиксила. Это конечно классно,
// что я прям поняла для чего НА САМОМ ДЕЛЕ НУЖНЫ ТЕСТЫ, но с учетом этого, было бы здорово этол в отдельный спринт вынести

// Main этот я пару спринтов уже не трогала, у нас ж был еще один в FileBackedTasksManager, я с ним работала.


public class Main {

    public static void main(String[] args) {

        File file = new File("resources/taskTracker.txt");

        Managers manager = new Managers();
        TaskManager taskManager = manager.getFileBackedTasksManager(file);

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

        taskManager.loadTask(firstTask);
        taskManager.loadTask(secondTask);
        taskManager.loadTask(threeTask3);
        taskManager.loadEpicTask(firstEpicTask);
        taskManager.loadSubTask(firstStep);
        taskManager.loadSubTask(secondStep);
        taskManager.loadSubTask(thirdStep);
        taskManager.loadSubTask(fourthStep);
        taskManager.loadEpicTask(secondEpicTask);

        taskManager.getSubTask(5);
        taskManager.getEpic(4);
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.getSubTask(6);
        taskManager.getTask(2);
        taskManager.getSubTask(7);
        taskManager.getEpic(9);

        System.out.println("Удаление задач");
        taskManager.removeTask(1);
        taskManager.removeSubTask(8);


        System.out.println("\nСоздали второй менеджер и загрузили задачи из файла: " + file.getAbsolutePath());
        TaskManager loadFromFile = loadFromFile(file);

        System.out.println();

        assert loadFromFile != null;
        loadFromFile.printHistoryList();

        System.out.println();
        System.out.println("Полный список задач");

        for (Task task: loadFromFile.getTaskMap().values()) {
            System.out.println(task);
        }

        System.out.println("\nОтсортированный список");
        taskManager.printPrioritizedTasks();

    }

}