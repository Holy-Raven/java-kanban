package tracker.util;

import tracker.service.*;

public class Managers {

    public TaskManager getDefault() {

        // return new InMemoryTaskManager();

        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

}
