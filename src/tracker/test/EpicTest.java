
package tracker.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.service.TaskManager;
import tracker.util.Managers;

import static org.junit.jupiter.api.Assertions.*;
import static tracker.util.Status.*;

class EpicTest {

    Managers manager = new Managers();
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = manager.getDefault();
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
}