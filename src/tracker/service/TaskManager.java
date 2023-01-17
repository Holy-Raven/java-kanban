package tracker.service;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;


import java.util.HashMap;

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
    void updateTask (Epic epic);
    void updateSubTask (SubTask subTask);
    public void printHistoryList();
    void removeTask(int id);
    void removeEpicTask(int id);
    void removeSubTask(Integer id);

    Task getTask(int id);
    SubTask getSubTask(int id);
    Epic getEpic(int id);

}