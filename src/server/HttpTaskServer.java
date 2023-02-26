package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsServer;
import server.pojo.PojoEpic;
import server.pojo.PojoSubtask;
import server.pojo.PojoTask;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.TaskManager;
import tracker.util.Managers;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static tracker.service.FileBackedTasksManager.loadFromFile;

// это наш сервер
public class HttpTaskServer {

    private static final int PORT = 8080;

    static File file = new File("resources/taskTracker.txt");
    private HttpsServer server;
    private Gson gson;
    static Managers manager;
    static TaskManager taskManager;


    public HttpTaskServer() throws IOException {
        this(loadFromFile(file));
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        HttpTaskServer.taskManager = taskManager;
        manager = new Managers();
        gson = Managers.getGson();
        server = HttpsServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/tasks", this::handleTasks);
    }

    private void handleTasks(HttpExchange exchange) {
        try {
            // Получаем путь
            String path = exchange.getRequestURI().getPath();
            // Получаем запрос
            String requestMethod = exchange.getRequestMethod();

            switch(requestMethod) {
                case "GET": {

                    if (Pattern.matches("^/api/v1/tasks$", path)) {

                        List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());

                        // Изменили в списке task на pojoTask
                        List<PojoTask> pojoTasks = new ArrayList<>();
                        for (Task task : tasks) {
                            pojoTasks.add(taskToPojoTask(task));
                        }

                        String response = gson.toJson(pojoTasks);
                        sendText(exchange, response);
                        return;
                    } else {
                        System.out.println("Идентификатор не корректный");
                        exchange.sendResponseHeaders(405, 0);
                    }

                    if (Pattern.matches("^/api/v1/tasks/task/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/tasks/task/", "");
                        int id = parsePathId(pathId);

                        if(id !=-1){

                            String response = gson.toJson(taskToPojoTask(taskManager.getTask(id)));
                            sendText(exchange, response);

                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Идентификатор не корректный");
                            exchange.sendResponseHeaders(405, 0);
                        }

                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }

//                case "POST": {
//
//                    break;
//                }
//
//                case "PUT": {
//
//                    break;
//                }
//
                case "DELETE" : {

                    if (Pattern.matches("^/api/v1/tasks/task/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/tasks/task/", "");
                        int id = parsePathId(pathId);
                        if(id !=-1){
                            taskManager.removeTask(id);
                            System.out.println("Удалили задачу с идентификатором - " + id);
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Идентификатор не корректный");
                            exchange.sendResponseHeaders(405, 0);
                        }


                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default:{
                    System.out.println("Метод полученный не соответствуем ожиданиям");
                    exchange.sendResponseHeaders(405, 0);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private int parsePathId(String path) {
        try{
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    public void start(){
        System.out.println("Запущен сервер " + PORT);
        System.out.println("http://localhost" + PORT + "/api/v1/tasks");
        server.start();
    }

    public void stop(){
        server.stop(0);
        System.out.println("Сервер остановили на порту " + PORT);
    }

    private String readText (HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);

    }


    public static void main(String[] args) throws IOException {

        HttpTaskServer server1 = new HttpTaskServer(taskManager);
        server1.start();

    }


    static public PojoTask taskToPojoTask(Task task) {

        if (task instanceof Epic epic) {
            PojoEpic pojoEpic = new PojoEpic(epic.getName(), epic.getDescription(), epic.getId(), epic.getTaskType(),
                    epic.getStartTimeString(), epic.getEndTimeString(), epic.getDuration());

            for (Integer subtaskId : epic.getSubTaskList()) {
                pojoEpic.getSubTaskList().add(subtaskId);
            }
            return pojoEpic;
        } else if (task instanceof SubTask subTask) {
            PojoSubtask pojoSubtask = new PojoSubtask(subTask.getName(), subTask.getDescription(), subTask.getId(), subTask.getTaskType(),
                    subTask.getStartTimeString(), subTask.getEndTimeString(), subTask.getDuration(), subTask.getEpic());

            return pojoSubtask;
        } else {
            return new PojoTask(task.getName(), task.getDescription(), task.getId(), task.getTaskType(),
                    task.getStartTimeString(), task.getEndTimeString(), task.getDuration());
        }
    }

}







//package server;
//
//import com.google.gson.*;
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;
//import server.pojo.PojoSubtask;
//import server.pojo.PojoTask;
//import server.pojo.PojoEpic;
//import tracker.model.Epic;
//import tracker.model.SubTask;
//import tracker.model.Task;
//import tracker.service.TaskManager;
//import tracker.util.Managers;
//import java.io.File;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.ArrayList;
//import java.util.List;
//
//
//import static tracker.service.FileBackedTasksManager.loadFromFile;
//
//// это наш сервер
//public class HttpTaskServer {
//
//    private static final int PORT = 8080;
//
//    static public PojoTask taskToPojoTask(Task task) {
//
//        if (task instanceof Epic epic) {
//            PojoEpic pojoEpic = new PojoEpic(epic.getName(), epic.getDescription(), epic.getId(), epic.getTaskType(),
//                    epic.getStartTimeString(), epic.getEndTimeString(), epic.getDuration());
//
//            for (Integer subtaskId : epic.getSubTaskList()) {
//                pojoEpic.getSubTaskList().add(subtaskId);
//            }
//            return pojoEpic;
//        } else if (task instanceof SubTask subTask) {
//            PojoSubtask pojoSubtask = new PojoSubtask(subTask.getName(), subTask.getDescription(), subTask.getId(), subTask.getTaskType(),
//                    subTask.getStartTimeString(), subTask.getEndTimeString(), subTask.getDuration(), subTask.getEpic());
//
//            return pojoSubtask;
//        } else {
//            return new PojoTask(task.getName(), task.getDescription(), task.getId(), task.getTaskType(),
//                    task.getStartTimeString(), task.getEndTimeString(), task.getDuration());
//        }
//    }
//
////    static public PojoEpic epicToPojoEpic(Epic epic) {
////        PojoEpic pojoEpic = new PojoEpic(epic.getName(),epic.getDescription(),epic.getId(),epic.getTaskType(),
////                epic.getStartTimeString(),epic.getEndTimeString(),epic.getDuration());
////
////        for (Integer subtaskId : epic.getSubTaskList()) {
////            pojoEpic.getSubTaskList().add(subtaskId);
////        }
////        return pojoEpic;
////    }
////
////    static public PojoSubtask subTaskPojoToSubTask(SubTask subTask) {
////
////        PojoSubtask subtask = new PojoSubtask(subTask.getName(),subTask.getDescription(),subTask.getId(),subTask.getTaskType(),
////                subTask.getStartTimeString(),subTask.getEndTimeString(),subTask.getDuration(),subTask.getEpic());
////
////        return subtask;
////    }
//
//
//    // cоздаем объект нашего менеджера откуда мы будем брать все данные
//    static File file = new File("resources/taskTracker.txt");
//    static Managers manager = new Managers();
//    //static TaskManager taskManager = manager.getFileBackedTasksManager(file);
//    static TaskManager taskManager = loadFromFile(file);
//
//    public static void main(String[] args) throws IOException {
//
//        // создали веб-сервер
//        HttpServer httpServer = HttpServer.create();
//        // привязали его к порту
//        httpServer.bind(new InetSocketAddress(PORT), 0);
//
//        httpServer.createContext("/tasks/tasks", new getAllTasksHandler());
//        httpServer.createContext("/tasks/task", new getTasksHandler());
//        httpServer.createContext("/tasks/epic", new getEpicsHandler());
//        httpServer.createContext("/tasks/subtask", new getSubtasksHandler());
//        httpServer.createContext("/tasks/history", new getHistoryHandler());
//        httpServer.createContext("/tasks/priory", new getPrioryTaskHandler());
//
//        httpServer.start(); // запускаем сервер
//
//        // Получаем список всех задач
//        getAllTasks();
//        // Получаем список задач
//        getTasks();
//        // Получаем список эпиков
//        getEpics();
//        // Получаем список всех подзадач
//        getSubtasks();
//        // Получаем список истории запросов
//        getHistory();
//        // Список задач в порядке приоритета
//        getPriory();
//
//    }
//
//
//
//    // это наши классы проводники для загрузки данных на сервер
//    static class getAllTasksHandler implements HttpHandler {
//
//        @Override
//        public void handle(HttpExchange httpExchange) throws IOException {
//
//            // Положили наши задачи в новый список.
//            List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());
//            // Изменили в списке task на pojoTask
//            List<PojoTask> pojoTasks = new ArrayList<>();
//
//            for (Task task : tasks) {
//                pojoTasks.add(taskToPojoTask(task));
//            }
//
//            // сериализуем новый список в JSON
//            Gson gson = new Gson();
//
//            String response = gson.toJson(pojoTasks);
//
//            httpExchange.sendResponseHeaders(200, 0);
//
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write(response.getBytes());
//            }
//        }
//    }
//
//    static class getTasksHandler implements HttpHandler {
//
//        @Override
//        public void handle(HttpExchange httpExchange) throws IOException {
//
//            List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());
//
//            List<PojoTask> pojoTask = new ArrayList<>();
//
//            // добавляем в список только Task
//            for (Task task : tasks) {
//                if (task instanceof Epic || task instanceof SubTask) {
//                } else {
//                    pojoTask.add(taskToPojoTask(task));
//                }
//            }
//
//            // сериализуем новый список в JSON
//            Gson gson = new Gson();
//            String response = gson.toJson(pojoTask);
//
//            httpExchange.sendResponseHeaders(200, 0);
//
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write(response.getBytes());
//            }
//        }
//    }
//
//    static class getEpicsHandler implements HttpHandler {
//
//        @Override
//        public void handle(HttpExchange httpExchange) throws IOException {
//
//
//            List<Task> epics = new ArrayList<>(taskManager.getEpicMap().values());
//            List<PojoEpic> pojoEpics = new ArrayList<>();
//
//            for (Task epic : epics) {
//                pojoEpics.add((PojoEpic) taskToPojoTask(epic));
//            }
//
//            Gson gson = new Gson();
//            String response = gson.toJson(pojoEpics);
//
//            httpExchange.sendResponseHeaders(200, 0);
//
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write(response.getBytes());
//            }
//        }
//    }
//
//    static class getSubtasksHandler implements HttpHandler {
//
//        @Override
//        public void handle(HttpExchange httpExchange) throws IOException {
//
//
//            List<Task> subTaskList = new ArrayList<>(taskManager.getSubTaskMap().values());
//            List<PojoSubtask> pojoSubtasks = new ArrayList<>();
//
//            for (Task subTask : subTaskList) {
//                pojoSubtasks.add((PojoSubtask) taskToPojoTask(subTask));
//            }
//
//            Gson gson = new Gson();
//            String response = gson.toJson(pojoSubtasks);
//
//            httpExchange.sendResponseHeaders(200, 0);
//
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write(response.getBytes());
//            }
//        }
//    }
//
//    static class getHistoryHandler implements HttpHandler {
//
//        @Override
//        public void handle(HttpExchange httpExchange) throws IOException {
//
//            List<Task> HistoryList = taskManager.getHistory();
//            List<PojoTask> pojoTasks = new ArrayList<>();
//
//            for (Task task : HistoryList) {
//                pojoTasks.add(taskToPojoTask(task));
//            }
//
//            Gson gson = new Gson();
//            String response = gson.toJson(pojoTasks);
//
//            httpExchange.sendResponseHeaders(200, 0);
//
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write(response.getBytes());
//            }
//        }
//
//    }
//
//    static class getPrioryTaskHandler implements HttpHandler {
//
//        @Override
//        public void handle(HttpExchange httpExchange) throws IOException {
//
//            List<Task> PrioryList = new ArrayList<>(taskManager.getPrioritizedTasks());
//            List<PojoTask> pojoTasks = new ArrayList<>();
//
//            for (Task task : PrioryList) {
//                pojoTasks.add(taskToPojoTask(task));
//            }
//
//            Gson gson = new Gson();
//            String response = gson.toJson(pojoTasks);
//
//            httpExchange.sendResponseHeaders(200, 0);
//
//            try (OutputStream os = httpExchange.getResponseBody()) {
//                os.write(response.getBytes());
//            }
//        }
//
//    }
//
//
//
//    static void getAllTasks(){
//        // HTTP-клиент с настройками по умолчанию
//        HttpClient client = HttpClient.newHttpClient();
//        // создаём экземпляр URI, содержащий адрес нужного ресурса
//        URI uriTasks = URI.create("http://localhost:8080/tasks/tasks");
//        // создаём объект, описывающий HTTP-запрос
//        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
//                .GET()                                  // указываем HTTP-метод запроса
//                .uri(uriTasks)                          // указываем адрес ресурса
//                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                JsonElement jsonElement = JsonParser.parseString(response.body());
//                JsonArray jsonArray = jsonElement.getAsJsonArray();
//                System.out.println("\n Полный список задач: " + jsonArray + "\n");
//
//            } else {
//                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
//            }
//        } catch (NullPointerException | IOException | InterruptedException e) {
//            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
//                    "Проверьте, пожалуйста, адрес и повторите попытку.");
//        }
//    }
//
//    static void getTasks(){
//        // HTTP-клиент с настройками по умолчанию
//        HttpClient client = HttpClient.newHttpClient();
//        // создаём экземпляр URI, содержащий адрес нужного ресурса
//        URI uriTasks = URI.create("http://localhost:8080/tasks/task");
//        // создаём объект, описывающий HTTP-запрос
//        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
//                .GET()                                  // указываем HTTP-метод запроса
//                .uri(uriTasks)                          // указываем адрес ресурса
//                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                JsonElement jsonElement = JsonParser.parseString(response.body());
//                JsonArray jsonArray = jsonElement.getAsJsonArray();
//                System.out.println("Cписок только задач: " + jsonArray + "\n");
//
//            } else {
//                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
//            }
//        } catch (NullPointerException | IOException | InterruptedException e) {
//            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
//                    "Проверьте, пожалуйста, адрес и повторите попытку.");
//        }
//    }
//
//    static void getEpics(){
//        // HTTP-клиент с настройками по умолчанию
//        HttpClient client = HttpClient.newHttpClient();
//        // создаём экземпляр URI, содержащий адрес нужного ресурса
//        URI uriTasks = URI.create("http://localhost:8080/tasks/epic");
//        // создаём объект, описывающий HTTP-запрос
//        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
//                .GET()                                  // указываем HTTP-метод запроса
//                .uri(uriTasks)                          // указываем адрес ресурса
//                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                JsonElement jsonElement = JsonParser.parseString(response.body());
//                JsonArray jsonArray = jsonElement.getAsJsonArray();
//                System.out.println("Список только эпиков: " + jsonArray + "\n");
//
//            } else {
//                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
//            }
//        } catch (NullPointerException | IOException | InterruptedException e) {
//            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
//                    "Проверьте, пожалуйста, адрес и повторите попытку.");
//        }
//    }
//
//    static void getSubtasks(){
//        // HTTP-клиент с настройками по умолчанию
//        HttpClient client = HttpClient.newHttpClient();
//        // создаём экземпляр URI, содержащий адрес нужного ресурса
//        URI uriTasks = URI.create("http://localhost:8080/tasks/subtask");
//        // создаём объект, описывающий HTTP-запрос
//        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
//                .GET()                                  // указываем HTTP-метод запроса
//                .uri(uriTasks)                          // указываем адрес ресурса
//                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                JsonElement jsonElement = JsonParser.parseString(response.body());
//                JsonArray jsonArray = jsonElement.getAsJsonArray();
//                System.out.println("Список только подзадач: " + jsonArray + "\n");
//
//            } else {
//                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
//            }
//        } catch (NullPointerException | IOException | InterruptedException e) {
//            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
//                    "Проверьте, пожалуйста, адрес и повторите попытку.");
//        }
//    }
//
//    static void getHistory(){
//        // HTTP-клиент с настройками по умолчанию
//        HttpClient client = HttpClient.newHttpClient();
//        // создаём экземпляр URI, содержащий адрес нужного ресурса
//        URI uriTasks = URI.create("http://localhost:8080/tasks/history");
//        // создаём объект, описывающий HTTP-запрос
//        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
//                .GET()                                  // указываем HTTP-метод запроса
//                .uri(uriTasks)                          // указываем адрес ресурса
//                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                JsonElement jsonElement = JsonParser.parseString(response.body());
//                JsonArray jsonArray = jsonElement.getAsJsonArray();
//                System.out.println("Cписок истории запросов: " + jsonArray + "\n");
//
//            } else {
//                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
//            }
//        } catch (NullPointerException | IOException | InterruptedException e) {
//            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
//                    "Проверьте, пожалуйста, адрес и повторите попытку.");
//        }
//    }
//
//    static void getPriory(){
//        // HTTP-клиент с настройками по умолчанию
//        HttpClient client = HttpClient.newHttpClient();
//        // создаём экземпляр URI, содержащий адрес нужного ресурса
//        URI uriTasks = URI.create("http://localhost:8080/tasks/priory");
//        // создаём объект, описывающий HTTP-запрос
//        HttpRequest request = HttpRequest.newBuilder()  // получаем экземпляр билдера
//                .GET()                                  // указываем HTTP-метод запроса
//                .uri(uriTasks)                          // указываем адрес ресурса
//                .build();                               // заканчиваем настройку и создаём ("строим") http-запрос
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                JsonElement jsonElement = JsonParser.parseString(response.body());
//                JsonArray jsonArray = jsonElement.getAsJsonArray();
//                System.out.println("Cписок задач в порядке приоритета: " + jsonArray + "\n");
//
//            } else {
//                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
//            }
//        } catch (NullPointerException | IOException | InterruptedException e) {
//            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
//                    "Проверьте, пожалуйста, адрес и повторите попытку.");
//        }
//    }
//
//}