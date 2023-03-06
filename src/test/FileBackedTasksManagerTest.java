
package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.FileBackedTasksManager;
import tracker.util.enums.Status;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class FileBackedTasksManagerTest extends TaskManagerTest <FileBackedTasksManager> {

    File file = new File("resources/taskTrackerTest.txt");

    @BeforeEach
    public void beforeEach(){

        taskManager = new FileBackedTasksManager(file);

    }

    @Test
    void loadFromFile() throws IOException {

        FileWriter writer = new FileWriter(file);
        writer.write("");

        // загружаем список задач из памяти должен быть пустой
        List<Integer> tasks = new ArrayList<>(taskManager.getTaskMap().keySet());
        assertTrue(tasks.isEmpty());

        // загружаем менеджер из файла
        taskManager = FileBackedTasksManager.loadFromFile(file);

        // обновляем список задач из памяти должен быть пустой
        assert taskManager != null;
        tasks = new ArrayList<>(taskManager.getTaskMap().keySet());
        assertTrue(tasks.isEmpty());


        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);
        taskManager.loadTask(firstTask);

        Task secondTask = new Task("Task 2",
                "Вторая задача",
                Status.NEW, "27.02.2023|14:00", 120);
        taskManager.loadTask(secondTask);

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");
        taskManager.loadEpicTask(firstEpicTask);

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

        // загружаем список задач из памяти
        tasks = new ArrayList<>(taskManager.getTaskMap().keySet());

        // загружаем список истории из памяти (должен быть 0)
        List<Task> historyList = taskManager.getHistory();
        assertNull(historyList);

        // загружаем менеджер из файла
        taskManager = FileBackedTasksManager.loadFromFile(file);

        // загрузим список из файла
        assert taskManager != null;
        List<Integer> tasksFromFile = new ArrayList<>(taskManager.getTaskMap().keySet());

        // теперь сравним списки
        assertIterableEquals(tasks, tasksFromFile);

        // загружаем список истории из файла (тоже должен быть 0)
        List<Task> historyListFromFile = taskManager.getHistory();
        assertNull(historyListFromFile);

        // делаем запросы
        taskManager.getSubTask(5);
        taskManager.getSubTask(4);
        taskManager.getEpic(3);
        taskManager.getTask(2);
        taskManager.getTask(1);

        // загружаем список истории из памяти
        historyList = taskManager.getHistory();

        // загружаем менеджер из файла
        taskManager = FileBackedTasksManager.loadFromFile(file);

        // загружаем список истории из файла
        assert taskManager != null;
        historyListFromFile = taskManager.getHistory();

        // сравниваем списки по размеру
        assertEquals(historyList, historyListFromFile);

    }
}