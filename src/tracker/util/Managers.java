package tracker.util;

import tracker.service.HistoryManager;
import tracker.service.InMemoryHistoryManager;
import tracker.service.InMemoryTaskManager;
import tracker.service.TaskManager;

public class Managers {
    public TaskManager getDefault() {

        return new InMemoryTaskManager();

    }
    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

}