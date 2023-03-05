
package tracker.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.KVTaskClient;
import tracker.service.*;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {

        KVTaskClient client = new KVTaskClient("http://localhost:8078/");
        return new HttpTaskManager("http://localhost:8078/", client);
    }

    public static TaskManager getFileBackedTasksManager(File file) {

        return FileBackedTasksManager.loadFromFile(file);
    }


    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }
}