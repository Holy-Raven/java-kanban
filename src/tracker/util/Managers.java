package tracker.util;

import tracker.service.*;

import java.io.File;

public class Managers {

    public TaskManager getDefault() {

        return new InMemoryTaskManager();

    }

    public TaskManager getFileBackedTasksManager(File file) {

        return new FileBackedTasksManager(file);
    }


    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

}