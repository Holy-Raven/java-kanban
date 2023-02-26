package server;


import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.TaskManager;
import tracker.util.Managers;
import tracker.util.Status;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tracker.service.FileBackedTasksManager.loadFromFile;

class HttpTaskServerTest {

    File file = new File("resources/taskTracker.txt");

    private HttpTaskServer httpTaskServer;
    private final Gson gson = Managers.getGson();

    Managers manager;
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() throws IOException {

        manager = new Managers();
        taskManager = manager.getDefault();
        httpTaskServer = new HttpTaskServer();

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

        taskManager = loadFromFile(file);

        httpTaskServer.start();


    }

    @AfterEach
    void afterEach(){
        httpTaskServer.stop();
    }


    @Test
    void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .GET()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

    }

}