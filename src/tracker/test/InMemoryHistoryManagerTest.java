
package tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.Managers;
import tracker.util.Status;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {


    HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {

        historyManager = Managers.getDefaultHistory();

    }

    @Test
    void addTest(){

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);

        historyManager.add(firstTask);

        List<Task> historyList = historyManager.getHistory();

        assertNotNull(historyList);
        assertEquals(1, historyList.size());

    }

    @Test
    void removeTest(){

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);

        Task secondTask = new Task("Task 2",
                "Вторая задача",
                Status.NEW, "25.02.2023|14:00", 120);

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS, "17.02.2023|12:00", 120,
                firstEpicTask.getId());

        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
                "Вторая подзадача Эпика 1",
                Status.DONE, "17.02.2023|13:00", 120,
                firstEpicTask.getId());

        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
                "Третья подзадача Эпика 1",
                Status.DONE, firstEpicTask.getId());

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(firstEpicTask);
        historyManager.add(firstStep);
        historyManager.add(secondStep);

        List<Task> historyList = historyManager.getHistory();

        // В списке должно быть 5 задач
        assertEquals(5, historyList.size());

        // Удалим одну задачу из середины и обновляем список истории
        historyManager.remove(secondTask.getId());
        historyList = historyManager.getHistory();

        // В списке должно быть 4 задачи и обновляем список истории
        assertEquals(4, historyList.size());

        // Удалим одну задачу из начала
        historyManager.remove(firstTask.getId());
        historyList = historyManager.getHistory();

        // В списке должно быть 3 задачи и обновляем список истории
        assertEquals(3, historyList.size());

        // Удалим одну задачу из конца
        historyManager.remove(secondStep.getId());
        historyList = historyManager.getHistory();

        // В списке должно быть 2 задачи
        assertEquals(2, historyList.size());
    }

    @Test
    void historyList(){

        Task firstTask = new Task("Task 1",
                "Первая задача",
                Status.NEW, "25.02.2023|15:00", 10);

        Task secondTask = new Task("Task 2",
                "Вторая задача",
                Status.NEW, "25.02.2023|14:00", 120);

        Epic firstEpicTask = new Epic("Epic 1",
                "Первый Эпик");

        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1",
                Status.IN_PROGRESS, "17.02.2023|12:00", 120,
                firstEpicTask.getId());


        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(firstEpicTask);
        historyManager.add(firstStep);

        List<Task> historyList = historyManager.getHistory();

        assertNotNull(historyList);
        assertEquals(4, historyList.size());

        historyManager.add(firstTask);

        assertEquals(4, historyList.size());

        // Проверим переместиться ли наша задача в начала списка истории
        assertEquals(secondTask.getName(), historyList.get(historyList.size()-3).getName());

    }
}