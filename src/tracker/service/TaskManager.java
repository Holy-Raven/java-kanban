
package tracker.service;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


public interface TaskManager {

    void loadTask(Task task);
    void loadEpicTask(Epic epic);
    void loadSubTask(SubTask subTask);

    HashMap<Integer, Task> getTaskMap();
    HashMap<Integer, Epic> getEpicMap();
    HashMap<Integer, SubTask> getSubTaskMap();

    void deleteAllTask();
    void deleteAllEpic();
    void deleteAllSubTask();

    void instalStatusEpic(Epic epic);

    void updateTask (Task task);
    void updateEpic (Epic epic);
    void updateSubTask (SubTask subTask);

    void removeTask(int id);
    void removeEpicTask(int id);
    void removeSubTask(Integer id);

    Task getTask(int id);
    SubTask getSubTask(int id);
    Epic getEpic(int id);


    Set<Task> getPrioritizedTasks();
    void printPrioritizedTasks();

    List<Task> getHistory();

    void printHistoryList();
}
