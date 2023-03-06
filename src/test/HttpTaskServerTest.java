
package test;


import com.google.gson.*;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import server.KVServer;
import server.pojo.PojoEpic;
import server.pojo.PojoSubtask;
import server.pojo.PojoTask;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.TaskManager;
import tracker.util.Managers;
import tracker.util.enums.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tracker.service.InMemoryTaskManager.instalTaskType;
import static tracker.util.PojoMappers.*;

class HttpTaskServerTest {

    static Gson gson = Managers.getGson();
    static KVServer kvServer;
    static HttpTaskServer httpTaskServer;
    static TaskManager taskManager;


    @BeforeAll
    public static void beforeAll () throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();

        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        taskManager = httpTaskServer.getTaskManager();
    }

    @BeforeEach
    public void beforeEach(){

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

    }

    @AfterEach
    public void afterEach() {
        taskManager.clear();
    }

    @AfterAll
    public static void afterAll(){
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void getTasks() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/tasks");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .GET()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        // Список задач из менеджера
        List<Task> taskMap = new ArrayList<>(taskManager.getTaskMap().values());

        // Получили ответ ввиде джейсон файла, десериализваоли его и записали результат в отдельный список
        JsonElement jsonTasks = JsonParser.parseString(response.body());
        JsonArray jsonArrayTasks = jsonTasks.getAsJsonArray();

        List<Task> taskList = new ArrayList<>();

        for (JsonElement jsonArrayTask : jsonArrayTasks) {

            if (jsonArrayTask.toString().contains("taskType\":\"EPIC")) {
                taskList.add(pojoTaskToTask(gson.fromJson(jsonArrayTask, PojoEpic.class)));
            } else if (jsonArrayTask.toString().contains("taskType\":\"SUBTASK")) {
                taskList.add(pojoTaskToTask(gson.fromJson(jsonArrayTask, PojoSubtask.class)));
            } else {
                taskList.add(pojoTaskToTask(gson.fromJson(jsonArrayTask, PojoTask.class)));
            }
        }

        // сравниваем списки
        assertEquals(taskMap, taskList);

    }

    @Test
    void  getEpic() throws  IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .GET()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        // Список эпиков из менеджера
        List<Epic> epicMap = new ArrayList<>(taskManager.getEpicMap().values());

        // Получили ответ ввиде джейсон файла, десериализваоли его и записали результат в отдельный список
        JsonElement jsonEpics = JsonParser.parseString(response.body());
        JsonArray jsonArrayTasks = jsonEpics.getAsJsonArray();

        List<Epic> epicList = new ArrayList<>();

        for (JsonElement jsonArrayTask : jsonArrayTasks) {

            if (jsonArrayTask.toString().contains("taskType\":\"EPIC")) {
                epicList.add(pojoEpicToEpic(gson.fromJson(jsonArrayTask, PojoEpic.class)));
            }

        }

        assertEquals(epicMap, epicList);
    }

    @Test
    void  getSubTask() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .GET()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        // Список эпиков из менеджера
        List<SubTask> subTaskMap = new ArrayList<>(taskManager.getSubTaskMap().values());

        // Получили ответ ввиде джейсон файла, десериализваоли его и записали результат в отдельный список
        JsonElement jsonSubtask = JsonParser.parseString(response.body());
        JsonArray jsonArrayTasks = jsonSubtask.getAsJsonArray();

        List<SubTask> subTaskList = new ArrayList<>();

        for (JsonElement jsonArrayTask : jsonArrayTasks) {

            if (jsonArrayTask.toString().contains("taskType\":\"SUBTASK")) {
                subTaskList.add((SubTask) pojoTaskToTask(gson.fromJson(jsonArrayTask, PojoSubtask.class)));
            }

        }

        assertEquals(subTaskMap, subTaskList);

    }

    @Test
    void  getTaskId() throws IOException, InterruptedException {

        // исходя из загруженных данных возьмем задачу 2
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/task2");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .GET()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Получили ответ ввиде джейсон файла, десериализваоли его и записали результат в отдельный объект

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Task newTask = pojoTaskToTask(gson.fromJson(jsonObject, PojoTask.class));

        // сравниваем задачи
        assertEquals(taskManager.getTaskMap().get(2), newTask);

    }

    @Test
    void getEpicId() throws  IOException, InterruptedException {

        // исходя из загруженных данных возьмем эпик задачу 4
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/epic4");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .GET()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Получили ответ ввиде джейсон файла, десериализваоли его и записали результат в отдельный объект

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Epic newEpic = pojoEpicToEpic(gson.fromJson(jsonObject, PojoEpic.class));

        // сравниваем задачи
        assertEquals(taskManager.getTaskMap().get(4), newEpic);

    }

    @Test
    void  getSubTaskId() throws  IOException, InterruptedException {

        // исходя из загруженных данных возьмем подзадачу 5
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/subtask5");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .GET()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Получили ответ ввиде джейсон файла, десериализваоли его и записали результат в отдельный объект

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        SubTask subTask = (SubTask) pojoTaskToTask(gson.fromJson(jsonObject, PojoSubtask.class));

        // сравниваем задачи
        assertEquals(taskManager.getTaskMap().get(5), subTask);

    }

    @Test
    void  getHistory() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .GET()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        // Список задач из менеджера
        List<Task> history = taskManager.getHistory();

        // Получили ответ ввиде джейсон файла, десериализваоли его и записали результат в отдельный список
        JsonElement jsonTasks = JsonParser.parseString(response.body());
        JsonArray jsonArrayTasks = jsonTasks.getAsJsonArray();

        List<Task> historyList = new ArrayList<>();

        for (JsonElement jsonArrayTask : jsonArrayTasks) {

            if (jsonArrayTask.toString().contains("taskType\":\"EPIC")) {
                historyList.add(pojoTaskToTask(gson.fromJson(jsonArrayTask, PojoEpic.class)));
            } else if (jsonArrayTask.toString().contains("taskType\":\"SUBTASK")) {
                historyList.add(pojoTaskToTask(gson.fromJson(jsonArrayTask, PojoSubtask.class)));
            } else {
                historyList.add(pojoTaskToTask(gson.fromJson(jsonArrayTask, PojoTask.class)));
            }
        }

        // сравниваем списки
        assertEquals(history, historyList);

    }

    @Test
    void  getPriority() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/priority");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .GET()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        // Список задач из менеджера
        List<Task> priority = new ArrayList<>(taskManager.getPrioritizedTasks());

        // Получили ответ ввиде джейсон файла, десериализваоли его и записали результат в отдельный список
        JsonElement jsonTasks = JsonParser.parseString(response.body());
        JsonArray jsonArrayTasks = jsonTasks.getAsJsonArray();

        List<Task> priorityList = new ArrayList<>();

        for (JsonElement jsonArrayTask : jsonArrayTasks) {

            if (jsonArrayTask.toString().contains("taskType\":\"SUBTASK")) {
                priorityList.add(pojoTaskToTask(gson.fromJson(jsonArrayTask, PojoSubtask.class)));
            } else {
                priorityList.add(pojoTaskToTask(gson.fromJson(jsonArrayTask, PojoTask.class)));
            }
        }

        // сравниваем списки
        assertEquals(priority, priorityList);


    }

    @Test
    void  deleteTasks() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriTasks)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(taskManager.getTaskMap().isEmpty());
        assertTrue(taskManager.getEpicMap().isEmpty());
        assertTrue(taskManager.getSubTaskMap().isEmpty());

    }

    @Test
    void  deleteEpics() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriTasks)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());


        assertTrue(taskManager.getEpicMap().isEmpty());
        assertTrue(taskManager.getSubTaskMap().isEmpty());

    }

    @Test
    void  deleteSubTask() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriTasks)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(taskManager.getSubTaskMap().isEmpty());

    }

    @Test
    void  deleteTaskId () throws IOException, InterruptedException {

        // исходя из загруженных данных удалим задачу 2
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/task2");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .DELETE()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        client.send(request, HttpResponse.BodyHandlers.ofString());

        // сравниваем задачи
        assertFalse(taskManager.getTaskMap().containsKey(2));

    }

    @Test
    void  deleteEpicId () throws IOException, InterruptedException {

        // исходя из загруженных данных удалим эпик 4
        // выгрузим список его подзадач

        List <Integer> list = taskManager.getEpic(4).getSubTaskList();

        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/epic4");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .DELETE()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        client.send(request, HttpResponse.BodyHandlers.ofString());

        // есть ли задача в общем списке и списке эпиков
        assertFalse(taskManager.getTaskMap().containsKey(4));
        assertFalse(taskManager.getEpicMap().containsKey(4));


        // посмотрим удалились ли подзадачи из списка
        for (Integer id : list) {
            assertFalse(taskManager.getTaskMap().containsKey(id));
        }

    }

    @Test
    void  deleteSubTaskId () throws IOException, InterruptedException {

        // исходя из загруженных данных удалим подзадачу 5
        HttpClient client = HttpClient.newHttpClient();
        URI uriTasks = URI.create("http://localhost:8080/tasks/subtask5");
        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
                .DELETE()                                  // указываем HTTP-метод запроса
                .uri(uriTasks)                          // указываем адрес ресурса
                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос

        client.send(request, HttpResponse.BodyHandlers.ofString());

        // есть ли задача в общем списке и списке подзадач
        assertFalse(taskManager.getTaskMap().containsKey(5));
        assertFalse(taskManager.getSubTaskMap().containsKey(5));

        // есть ли подзадача в списке подзадач эпика родителя
        assertFalse(taskManager.getEpic(4).getSubTaskList().contains(5));



    }

    @Test
    void loadTask() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        Task newTask = new Task("Task 4",
                "Новая задача",
                Status.NEW, "30.02.2023|15:00", 30);

        instalTaskType(newTask);

        String req = gson.toJson(taskToPojoTask(newTask));

        URI uriTasks = URI.create("http://localhost:8080/tasks/task");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(req))
                .uri(uriTasks)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getTask(10).getDescription(), "Новая задача");

    }

    @Test
    void loadEpic() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        Epic newEpicTask = new Epic("Epic 3",
                "Новый эпик");

        instalTaskType(newEpicTask);

        String req = gson.toJson(taskToPojoTask(newEpicTask));

        URI uriTasks = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(req))
                .uri(uriTasks)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getEpic(11).getDescription(), "Новый эпик");
    }

    @Test
    void loadSubTask () throws  IOException, InterruptedException {


        HttpClient client = HttpClient.newHttpClient();

        SubTask newStep = new SubTask("SubTask 5 Epic 1",
                "Новая подзадача Эпика 1",
                Status.DONE,4);

        instalTaskType(newStep);

        String req = gson.toJson(taskToPojoTask(newStep));

        int beforeLength = taskManager.getEpic(4).getSubTaskList().size();

        URI uriTasks = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(req))
                .uri(uriTasks)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());


        int afterLength = taskManager.getEpic(4).getSubTaskList().size();

        assertEquals(beforeLength + 1, afterLength);
        assertEquals(taskManager.getSubTask(10).getDescription(), "Новая подзадача Эпика 1");
    }

    @Test
    void  updateTask() throws IOException, InterruptedException {

        // изменим вторую задачу.

        HttpClient client = HttpClient.newHttpClient();

        Task newTask = new Task("Task 2",
                "Вторая задача обновлена",
                Status.NEW, "26.02.2023|14:00", 120);

        instalTaskType(newTask);

        String req = gson.toJson(taskToPojoTask(newTask));

        URI uriTasks = URI.create("http://localhost:8080/tasks/task2");

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(req))
                .uri(uriTasks)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getTask(2).getDescription(), "Вторая задача обновлена");
    }

    @Test
    void  updateEpic() throws IOException, InterruptedException {

        // изменим эпик id 4.
        HttpClient client = HttpClient.newHttpClient();

        Epic newTask = new Epic("Epic 1",
                "Первый Эпик обновлен");

        instalTaskType(newTask);

        String req = gson.toJson(taskToPojoTask(newTask));

        URI uriTasks = URI.create("http://localhost:8080/tasks/epic4");

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(req))
                .uri(uriTasks)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getEpic(4).getDescription(), "Первый Эпик обновлен");
    }

    @Test
    void  updateSubTask() throws IOException, InterruptedException {

        // изменим субтаск id 5.
        HttpClient client = HttpClient.newHttpClient();

        SubTask newTask = new SubTask("SubTask 1 Epic 1",
                "Первая подзадача Эпика 1 Обновлена",
                Status.IN_PROGRESS,"17.02.2023|12:00", 120,
                4);


        instalTaskType(newTask);

        String req = gson.toJson(taskToPojoTask(newTask));

        URI uriTasks = URI.create("http://localhost:8080/tasks/subtask5");

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(req))
                .uri(uriTasks)
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getSubTask(5).getDescription(), "Первая подзадача Эпика 1 Обновлена");
    }

}

