
package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import server.pojo.PojoEpic;
import server.pojo.PojoSubtask;
import server.pojo.PojoTask;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.TaskManager;
import tracker.util.enums.Endpoint;
import tracker.util.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static tracker.service.HttpTaskManager.loadFromServer;
import static tracker.util.PojoMappers.*;

// это наш сервер
public class HttpTaskServer {

    private static final int PORT = 8080;
    private HttpServer httpServer;
    public  TaskManager taskManager;
    private final Gson gson;
    String url = "http://localhost:8078/";


    public HttpTaskServer() throws IOException, InterruptedException {

        KVTaskClient client = new KVTaskClient(url);
        taskManager = loadFromServer(url, client);
        gson = Managers.getGson();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
    }



    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Запускаем HttpTaskServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_ALLTASKS: {
                    getAllTasksHandler(exchange);
                    break;
                }
                case GET_TASKS: {
                    getTasksHandler(exchange);
                    break;
                }
                case GET_EPICS: {
                    getEpicsHandler(exchange);
                    break;
                }
                case GET_SUBTASKS: {
                    getSubtasksHandler(exchange);
                    break;
                }
                case GET_HISTORY: {
                    getHistoryHandler(exchange);
                    break;
                }
                case GET_PRIORITY: {
                    getPrioryTaskHandler(exchange);
                    break;
                }
                case GET_TASK_ID: {
                    getTaskId(exchange);
                    break;
                }
                case GET_EPIC_ID: {
                    getEpicId(exchange);
                    break;
                }
                case GET_SUBTASK_ID: {
                    getSubTaskId(exchange);
                    break;
                }
                case GET_EPIC_SUBTASK_LIST: {
                    getEpicSubtaskList(exchange);
                    break;
                }

                case DELETE_ALLTASKS:{
                    deleteAllTasksHandler(exchange);
                    break;
                }
                case DELETE_ALLEPIC:{
                    deleteAllEpicHandler(exchange);
                    break;
                }
                case DELETE_ALLSUBTASK:{
                    deleteAllSubTaskHandler(exchange);
                    break;
                }
                case DELETE_TASK:{
                    deleteTaskHandler(exchange);
                    break;
                }
                case DELETE_EPIC:{
                    deleteEpicHandler(exchange);
                    break;
                }
                case DELETE_SUBTASK:{
                    deleteSubTaskHandler(exchange);
                    break;
                }

                case POST_TASK:{
                    postTaskHandler(exchange);
                    break;
                }
                case POST_EPIC:{
                    postEpicHandler(exchange);
                    break;
                }
                case POST_SUBTASK:{
                    postSubTaskHandler(exchange);
                    break;
                }

                case UPDATE_TASK:{
                    updateTaskIdHandler(exchange);
                    break;
                }
                case UPDATE_EPIC:{
                    updateEpicIdHandler(exchange);
                    break;
                }
                case UPDATE_SUBTASK:{
                    updateSubTaskIdHandler(exchange);
                    break;
                }

                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 405);
            }
        }

        private Endpoint getEndpoint(String path, String method) {

            switch (method) {
                case "GET": {
                    if (Pattern.matches("^/tasks/tasks$", path))
                        return Endpoint.GET_ALLTASKS;
                    if (Pattern.matches("^/tasks/task$", path))
                        return Endpoint.GET_TASKS;
                    if (Pattern.matches("^/tasks/epic$", path))
                        return Endpoint.GET_EPICS;
                    if (Pattern.matches("^/tasks/subtask$", path))
                        return Endpoint.GET_SUBTASKS;
                    if (Pattern.matches("^/tasks/history$", path))
                        return Endpoint.GET_HISTORY;
                    if (Pattern.matches("^/tasks/priority$", path))
                        return Endpoint.GET_PRIORITY;
                    if (Pattern.matches("^/tasks/task\\d+$", path))
                        return Endpoint.GET_TASK_ID;
                    if (Pattern.matches("^/tasks/epic\\d+$", path))
                        return Endpoint.GET_EPIC_ID;
                    if (Pattern.matches("^/tasks/subtask\\d+$", path))
                        return Endpoint.GET_SUBTASK_ID;
                    if (Pattern.matches("^/tasks/subtask/epic\\d+$", path))
                        return Endpoint.GET_EPIC_SUBTASK_LIST;
                }

                case "DELETE": {
                    if (Pattern.matches("^/tasks/tasks$", path))
                        return Endpoint.DELETE_ALLTASKS;
                    if (Pattern.matches("^/tasks/epic$", path))
                        return Endpoint.DELETE_ALLEPIC;
                    if (Pattern.matches("^/tasks/subtask$", path))
                        return Endpoint.DELETE_ALLSUBTASK;
                    if (Pattern.matches("^/tasks/task\\d+$", path))
                        return Endpoint.DELETE_TASK;
                    if (Pattern.matches("^/tasks/epic\\d+$", path))
                        return Endpoint.DELETE_EPIC;
                    if (Pattern.matches("^/tasks/subtask\\d+$", path))
                        return Endpoint.DELETE_SUBTASK;
                }

                case "POST": {
                    if (Pattern.matches("^/tasks/task$", path))
                        return Endpoint.POST_TASK;
                    if (Pattern.matches("^/tasks/epic$", path))
                        return Endpoint.POST_EPIC;
                    if (Pattern.matches("^/tasks/subtask$", path))
                        return Endpoint.POST_SUBTASK;
                }

                case "PUT": {
                    if (Pattern.matches("^/tasks/task\\d+$", path))
                        return Endpoint.UPDATE_TASK;
                    if (Pattern.matches("^/tasks/epic\\d+$", path))
                        return Endpoint.UPDATE_EPIC;
                    if (Pattern.matches("^/tasks/subtask\\d+$", path))
                        return Endpoint.UPDATE_SUBTASK;
                }

                default: {
                    return Endpoint.UNKNOWN;
                }
            }
        }

        // это наши методы проводники запросы GET
        private void getAllTasksHandler(HttpExchange httpExchange) throws IOException {

            // Положили наши задачи в новый список.
            List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());
            // Изменили в списке task на pojoTask
            List<PojoTask> pojoTasks = new ArrayList<>();

            for (Task task : tasks) {
                pojoTasks.add(taskToPojoTask(task));
            }

            String response = gson.toJson(pojoTasks);
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void getTasksHandler(HttpExchange httpExchange) throws IOException {

            List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());

            List<PojoTask> pojoTask = new ArrayList<>();

            // добавляем в список только Task
            for (Task task : tasks) {
                if (task instanceof Epic || task instanceof SubTask) {
                } else {
                    pojoTask.add(taskToPojoTask(task));
                }
            }

            String response = gson.toJson(pojoTask);
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void getEpicsHandler(HttpExchange httpExchange) throws IOException {

            List<Task> epics = new ArrayList<>(taskManager.getEpicMap().values());
            List<PojoEpic> pojoEpics = new ArrayList<>();

            for (Task epic : epics) {
                pojoEpics.add((PojoEpic) taskToPojoTask(epic));
            }

            String response = gson.toJson(pojoEpics);
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void getSubtasksHandler(HttpExchange httpExchange) throws IOException {

            List<Task> subTaskList = new ArrayList<>(taskManager.getSubTaskMap().values());
            List<PojoSubtask> pojoSubtasks = new ArrayList<>();

            for (Task subTask : subTaskList) {
                pojoSubtasks.add((PojoSubtask) taskToPojoTask(subTask));
            }

            String response = gson.toJson(pojoSubtasks);
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void getHistoryHandler(HttpExchange httpExchange) throws IOException {

            List<Task> HistoryList = taskManager.getHistory();
            List<PojoTask> pojoTasks = new ArrayList<>();

            for (Task task : HistoryList) {
                pojoTasks.add(taskToPojoTask(task));
            }

            String response = gson.toJson(pojoTasks);
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void getPrioryTaskHandler(HttpExchange httpExchange) throws IOException {

            List<Task> PrioryList = new ArrayList<>(taskManager.getPrioritizedTasks());
            List<PojoTask> pojoTasks = new ArrayList<>();

            for (Task task : PrioryList) {
                pojoTasks.add(taskToPojoTask(task));
            }

            String response = gson.toJson(pojoTasks);
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void getTaskId(HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/task", "");

            int id = parsePathId(pathId);

            if (id != -1) {
                PojoTask pojoTask = taskToPojoTask(taskManager.getTask(id));
                String response = gson.toJson(pojoTask);

                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                System.out.println("Идентификатор задачи не корректный");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

        private void getEpicId(HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/epic", "");
            int id = parsePathId(pathId);

            if (id != -1) {
                PojoTask pojoTask = taskToPojoTask(taskManager.getEpic(id));
                String response = gson.toJson(pojoTask);

                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                System.out.println("Идентификатор эпика не корректный");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

        private void getSubTaskId(HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/subtask", "");
            int id = parsePathId(pathId);

            if (id != -1) {
                PojoTask pojoTask = taskToPojoTask(taskManager.getSubTask(id));
                String response = gson.toJson(pojoTask);
                httpExchange.sendResponseHeaders(200, 0);

                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                System.out.println("Идентификатор подзадачи не корректный");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

        private void getEpicSubtaskList(HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/subtask/epic", "");
            int id = parsePathId(pathId);

            if (id != -1) {

                Epic epic = taskManager.getEpic(id);

                List<PojoTask> pojoSubTaskList = new ArrayList<>();

                for (Integer subTaskID : epic.getSubTaskList()) {
                    pojoSubTaskList.add(taskToPojoTask(taskManager.getSubTask(subTaskID)));
                }

                String response = gson.toJson(pojoSubTaskList);

                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                System.out.println("Идентификатор эпика не корректный");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

        // это наши методы проводники DELETE

        private void deleteAllTasksHandler(HttpExchange httpExchange) throws IOException {

            taskManager.deleteAllTask();

            String result = "Все задачи удалены";

            String response = gson.toJson(result);
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void deleteAllEpicHandler(HttpExchange httpExchange) throws IOException {

            taskManager.deleteAllEpic();

            String result = "Все EPIC задачи удалены";

            String response = gson.toJson(result);
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void deleteAllSubTaskHandler(HttpExchange httpExchange) throws IOException {

            taskManager.deleteAllSubTask();

            String result = "Все SubTask задачи удалены";

            String response = gson.toJson(result);
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void deleteTaskHandler(HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/task", "");

            int id = parsePathId(pathId);

            if (id != -1) {

                taskManager.removeTask(id);

                String response = "Task задача ID - " + id + " удалена.";

                httpExchange.sendResponseHeaders(200, 0);

                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                System.out.println("Идентификатор задачи не корректный");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

        private void deleteEpicHandler(HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/epic", "");

            int id = parsePathId(pathId);

            if (id != -1) {

                taskManager.removeEpicTask(id);

                String response = "Epic задача ID - " + id + " удалена.";

                httpExchange.sendResponseHeaders(200, 0);

                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                System.out.println("Идентификатор задачи не корректный");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

        private void deleteSubTaskHandler(HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/subtask", "");

            int id = parsePathId(pathId);

            if (id != -1) {

                taskManager.removeSubTask(id);

                String response = "SubTask задача ID - " + id + " удалена.";

                httpExchange.sendResponseHeaders(200, 0);

                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                System.out.println("Идентификатор задачи не корректный");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

        // это наши методы загрузки POST

        private void postTaskHandler (HttpExchange httpExchange) throws  IOException {

            String body = new String(httpExchange.getRequestBody().readAllBytes());

            PojoTask pojoTask = gson.fromJson(body, PojoTask.class);
            Task task = pojoTaskToTask(pojoTask);

            taskManager.loadTask(task);

            String response = "TASK задача успешно добавлена";
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void postEpicHandler (HttpExchange httpExchange) throws  IOException {

            String body = new String(httpExchange.getRequestBody().readAllBytes());

            PojoEpic pojoEpic = gson.fromJson(body, PojoEpic.class);
            Epic epic = (Epic) pojoTaskToTask(pojoEpic);

            taskManager.loadEpicTask(epic);

            String response = "EPIC задача успешно добавлена";
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void postSubTaskHandler (HttpExchange httpExchange) throws  IOException {

            String body = new String(httpExchange.getRequestBody().readAllBytes());

            PojoSubtask pojoSubtask = gson.fromJson(body, PojoSubtask.class);
            SubTask subTask = (SubTask) pojoTaskToTask(pojoSubtask);

            taskManager.loadSubTask(subTask);

            String response = "SUBTASK задача успешно добавлена";
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        // это наши методы загрузки PUT

        private void updateTaskIdHandler (HttpExchange httpExchange) throws  IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/task", "");

            int id = parsePathId(pathId);

            // список с id только TASK
            List <Integer> taskIdList = new ArrayList<>();

            for (Task taskId : taskManager.getTaskMap().values()) {
                if (taskId instanceof Epic || taskId instanceof SubTask) {
                } else {
                    taskIdList.add(taskId.getId());
                }
            }

            if (id != -1 && taskIdList.contains(id)) {

                String body = new String(httpExchange.getRequestBody().readAllBytes());

                PojoTask pojoTask = gson.fromJson(body, PojoTask.class);
                Task task = pojoTaskToTask(pojoTask);

                task.setId(id);
                taskManager.loadTask(task);

                String response = "TASK задача успешно обновлена добавлена";
                httpExchange.sendResponseHeaders(200, 0);

                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                System.out.println("Идентификатор задачи не корректный");
                httpExchange.sendResponseHeaders(405, 0);
            }


        }

        private void updateEpicIdHandler (HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/epic", "");
            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getEpicMap().containsKey(id)) {

                String body = new String(httpExchange.getRequestBody().readAllBytes());

                PojoEpic pojoEpic = gson.fromJson(body, PojoEpic.class);
                Epic epic = pojoEpicToEpic(pojoEpic);

                epic.setId(id);
                taskManager.loadEpicTask(epic);


                String response = "EPIC задача успешно обновлена";
                httpExchange.sendResponseHeaders(200, 0);

                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }


            } else {
                System.out.println("Идентификатор эпика не корректный");
                httpExchange.sendResponseHeaders(405, 0);


            }

        }

        private void updateSubTaskIdHandler (HttpExchange httpExchange) throws IOException {

            String path = httpExchange.getRequestURI().getPath();
            String pathId = path.replaceFirst("/tasks/subtask", "");
            int id = parsePathId(pathId);

            if (id != -1 && taskManager.getSubTaskMap().containsKey(id)) {

                String body = new String(httpExchange.getRequestBody().readAllBytes());

                PojoSubtask pojoSubtask = gson.fromJson(body, PojoSubtask.class);
                SubTask subTask = (SubTask) pojoTaskToTask(pojoSubtask);

                subTask.setId(id);
                taskManager.loadSubTask(subTask);

                String response = "SUBTASK задача успешно обновлена";
                httpExchange.sendResponseHeaders(200, 0);

                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else {
                System.out.println("Идентификатор подзадачи не корректный");
                httpExchange.sendResponseHeaders(405, 0);
            }
        }

        private void writeResponse(HttpExchange exchange,String responseString, int responseCode) throws IOException {

            if(responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {

                byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(responseCode, bytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }
}