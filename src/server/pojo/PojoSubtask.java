package server.pojo;

import tracker.util.TaskType;

public class PojoSubtask extends PojoTask {

    private int epicId;

    public PojoSubtask (String name,
                        String description,
                        int id,
                        TaskType taskType,
                        String startTime,
                        String endTime,
                        int duration,
                        int epicId
    ) {

        super(name, description, id, taskType, startTime, endTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}