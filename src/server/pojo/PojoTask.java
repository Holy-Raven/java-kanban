
package server.pojo;

import tracker.util.TaskType;

public class PojoTask {

    private String name;
    private String description;
    private int id;
    private TaskType taskType;
    private String startTime;
    private String endTime;
    private int duration;

    public PojoTask(String name, String description, int id, TaskType taskType, String startTime, String endTime, int duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.taskType = taskType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


}