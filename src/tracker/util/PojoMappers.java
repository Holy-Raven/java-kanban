
package tracker.util;

import server.pojo.PojoEpic;
import server.pojo.PojoSubtask;
import server.pojo.PojoTask;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.enums.TaskType;

public class PojoMappers {

    public static PojoTask taskToPojoTask(Task task) {

        if (task instanceof Epic epic) {
            PojoEpic pojoEpic;

            if (epic.getStartTimeString() !=null) {
                pojoEpic = new PojoEpic(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus(), epic.getTaskType(),
                        epic.getStartTimeString(), epic.getEndTimeString(), epic.getDuration());
                for (Integer subtaskId : epic.getSubTaskList()) {
                    pojoEpic.getSubTaskList().add(subtaskId);
                }
                return pojoEpic;
            } else {
                pojoEpic = new PojoEpic(epic.getName(), epic.getDescription());
                pojoEpic.setTaskType(TaskType.EPIC);
                return pojoEpic;
            }

        } else if (task instanceof SubTask subTask) {
            PojoSubtask pojoSubtask = new PojoSubtask(subTask.getName(), subTask.getDescription(), subTask.getId(), subTask.getStatus(),subTask.getTaskType(),
                    subTask.getStartTimeString(), subTask.getEndTimeString(), subTask.getDuration(), subTask.getEpic());

            return pojoSubtask;
        } else {
            return new PojoTask(task.getName(), task.getDescription(), task.getId(), task.getStatus(), task.getTaskType(),
                    task.getStartTimeString(), task.getEndTimeString(), task.getDuration());
        }
    }

    public static Task pojoTaskToTask(PojoTask pojoTask){

        Integer id = pojoTask.getId();

        Task task;
        Epic epic;
        SubTask subTask;

        TaskType taskType = pojoTask.getTaskType();

        switch (taskType){

            case EPIC -> {

                PojoEpic pojoEpic = (PojoEpic) pojoTask;

                if (id != 0) {                                  // значит задача уже существует и нам нужно ее обновить
                    epic = new Epic(pojoEpic.getName(), pojoEpic.getDescription(),id, pojoEpic.getStatus(), pojoEpic.getStartTime(), pojoEpic.getDuration());
                } else {                                        // id нет и его нужно назначить в момент загрузки задача в менеджер
                    epic = new Epic(pojoTask.getName(), pojoTask.getDescription());
                }
                return epic;
            }

            case SUBTASK -> {

                PojoSubtask pojoSubTask = (PojoSubtask) pojoTask;

                if (id != 0) {
                    subTask = new SubTask(pojoSubTask.getName(), pojoSubTask.getDescription(),id, pojoSubTask.getStatus(), pojoSubTask.getStartTime(), pojoSubTask.getDuration(), pojoSubTask.getEpicId());
                } else {
                    if (pojoSubTask.getStartTime().equals("время начала не указано")){
                        subTask = new SubTask(pojoSubTask.getName(), pojoTask.getDescription(), pojoSubTask.getStatus(), pojoSubTask.getEpicId());
                    } else {
                        subTask = new SubTask(pojoSubTask.getName(), pojoTask.getDescription(), pojoSubTask.getStatus(), pojoSubTask.getStartTime(), pojoSubTask.getDuration(), pojoSubTask.getEpicId());
                    }
                }
                return subTask;

            } default -> {

                if (id != 0){

                    task = new Task(pojoTask.getName(), pojoTask.getDescription(), id, pojoTask.getStatus(), pojoTask.getStartTime(), pojoTask.getDuration());

                } else {

                    if (pojoTask.getStartTime().equals("время начала не указано")){
                        task = new Task(pojoTask.getName(), pojoTask.getDescription(),pojoTask.getStatus());
                    } else {
                        task = new Task(pojoTask.getName(), pojoTask.getDescription(),pojoTask.getStatus(), pojoTask.getStartTime(), pojoTask.getDuration());
                    }
                }
                return task;
            }
        }
    }

    public static Epic pojoEpicToEpic(PojoEpic pojoEpic){

        Epic epic;
        int id = pojoEpic.getId();

        if (id != 0) {                                  // значит задача уже существует и нам нужно ее обновить
            epic = new Epic(pojoEpic.getName(), pojoEpic.getDescription(),id, pojoEpic.getStatus(), pojoEpic.getStartTime(), pojoEpic.getDuration());
            epic.setSubTaskList(pojoEpic.getSubTaskList());
        } else {                                        // id нет и его нужно назначить в момент загрузки задача в менеджер
            epic = new Epic(pojoEpic.getName(), pojoEpic.getDescription());
        }
        return epic;
    }


}