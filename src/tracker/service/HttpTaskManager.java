
package tracker.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import server.KVTaskClient;
import server.pojo.PojoEpic;
import server.pojo.PojoSubtask;
import server.pojo.PojoTask;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static tracker.util.PojoMappers.pojoTaskToTask;
import static tracker.util.PojoMappers.taskToPojoTask;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    static Gson gson = Managers.getGson();

    public HttpTaskManager(String url, KVTaskClient client) {
        super(new File("resources/taskTracker.txt"));
        this.client = client;
    }

    @Override
    protected void save() {
        super.save();

        List<PojoTask> pojoTaskList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            pojoTaskList.add(taskToPojoTask(value));
        }

        List<PojoTask> pojoHistoryList = new ArrayList<>();
        if (inMemoryHistoryManager.getHistory() != null) {
            for (Task value : getHistory()) {
                pojoHistoryList.add(taskToPojoTask(value));
            }
        }
        try {
            //cериализуем список задач и отправляем на облако


            String taskListJson = gson.toJson(pojoTaskList, new TypeToken<List<PojoTask>>(){}.getType());
            client.put("tasks", taskListJson);

            // сериализуем список истории и отправляем на облако

            String historyListJson = gson.toJson(pojoHistoryList, new TypeToken<List<PojoTask>>(){}.getType());
            client.put("history", historyListJson);

        } catch (IOException | InterruptedException exception){
            throw new RuntimeException();
        }
    }

    public static HttpTaskManager loadFromServer (String url, KVTaskClient client) throws IOException, InterruptedException {

        HttpTaskManager httpTaskManager = new HttpTaskManager(url, client);
        HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        //работаем с списком задач
        String tasks = client.load("tasks");
        String history = client.load("history");


        if (!tasks.equals("")) {
            JsonElement jsonTasks = JsonParser.parseString(tasks);
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

            for (Task task : taskList) {

                if (task instanceof Epic epic) {
                    httpTaskManager.loadEpicTask(epic);
                } else if (task instanceof SubTask subtask) {
                    httpTaskManager.loadSubTask(subtask);
                } else {
                    httpTaskManager.loadTask(task);
                }
            }
        }

        if (!history.equals("")) {
            // работаем с списком истории
            List<PojoTask> pojoHistory = new ArrayList<>();

            JsonElement jsonHistory = JsonParser.parseString(history);
            JsonArray jsonArrayHistory = jsonHistory.getAsJsonArray();

            for (JsonElement jsonElement : jsonArrayHistory) {
                pojoHistory.add(gson.fromJson(jsonElement, PojoTask.class));
            }

            if (!pojoHistory.isEmpty()) {
                for (PojoTask task : pojoHistory) {
                    if (task instanceof PojoEpic epic) {
                        inMemoryHistoryManager.add(httpTaskManager.taskMap.get(epic.getId()));
                    } else if (task instanceof PojoSubtask subtask) {
                        inMemoryHistoryManager.add(httpTaskManager.taskMap.get(subtask.getId()));
                    } else {
                        inMemoryHistoryManager.add(httpTaskManager.taskMap.get(task.getId()));
                    }
                }
            }
        }
        httpTaskManager.setInMemoryHistoryManager(inMemoryHistoryManager);
        //httpTaskManager.save();

        return httpTaskManager;

    }
}