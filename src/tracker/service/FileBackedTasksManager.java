
package tracker.service;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.util.ManagerSaveException;
import tracker.util.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static tracker.util.Status.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public void loadTask(Task task) {
        super.loadTask(task);
        save();
    }

    @Override
    public void loadEpicTask(Epic epic) {
        super.loadEpicTask(epic);
        save();
    }

    @Override
    public void loadSubTask(SubTask subTask) {
        super.loadSubTask(subTask);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpicTask(int id) {
        super.removeEpicTask(id);
        save();
    }

    @Override
    public void removeSubTask(Integer id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;

    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void save() {

        String title = "id, type, name, status, description, startTime, duration, endTime, taskType, epic\n";

        try (BufferedWriter writter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, false))) {

            writter.write(title,0,title.length());

            for (Task task : taskMap.values()) {
                writter.write(toString(task), 0, toString(task).length());
            }

            if (inMemoryHistoryManager.getHistory() != null){
                writter.write("\n");
                writter.write(historyToString(inMemoryHistoryManager));
            }
        }
        catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла.");
            throw new ManagerSaveException(e);

        }
    }

    // Создаем изменяемую строку, и поочередно добавляем в нее поля нашего таска через запятую
    // Преобразуем в обычную строку и возвращаем
    private static String toString(Task task){

        StringBuilder builder = new StringBuilder();

        //поле 0
        // добавляем в строку id задачи
        builder.append(task.getId()).append(",");

        //поле 1
        // добавляем в строку тип задачи
        switch (task.getTaskType()) {
            case TASK:
                builder.append("TASK,");
                break;
            case EPIC:
                builder.append("EPIC,");
                break;
            case SUBTASK:
                builder.append("SUBTASK,");
        }
        // поле 2
        // добавляем имя задачи
        builder.append(task.getName()).append(",");

        // поле 3
        // добавляем в строку статус задачи
        switch (task.getStatus()) {
            case NEW:
                builder.append("NEW,");
                break;
            case DONE:
                builder.append("DONE,");
                break;
            case IN_PROGRESS:
                builder.append("IN_PROGRESS,");
        }

        // поле 4
        // добавляем описание задачи
        builder.append(task.getDescription()).append(",");

        // поле 5
        // добавляем время начала задачи
        builder.append(task.getStartTimeString()).append(",");

        //поле 6
        // добавляем продолжительность задачи
        builder.append(task.getDuration()).append(",");

        //поле 7
        // ожидаемое время завершения задачи
        builder.append(task.getEndTimeString()).append(",");

        //поле 8 + 9 (если subTask)
        // добавляем в строку тип задачи
        switch (task.getTaskType()) {
            case TASK:
                builder.append("TASK,").append("\n");
                break;
            case EPIC:
                builder.append("EPIC,").append("\n");
                break;
            case SUBTASK:
                SubTask st = (SubTask) task;
                builder.append("SUBTASK,").append(st.getEpic()).append("\n");
        }

        return builder.toString();
    }

    // Предполагается, что исходными данными нашего метода будет работа toString(Task task), а значит текст в строке
    // будет заданного формата. Разбивает строку по запятым на массив подстрок. Определяем к какому типу относится данный
    // объект, и заполняем его поля. Возвращаем созданный объект.
    private static Task fromString(String value) {

        Task task = null;

        String[] arrayField = value.split(",");

        if (arrayField[8].equals("TASK")) {

            if (arrayField[5].equals("время начала не указано")) {
                if (arrayField[3].equals("NEW"))
                    task = new Task(arrayField[2], arrayField[4], NEW);
                if (arrayField[3].equals("DONE"))
                    task = new Task(arrayField[2], arrayField[4], DONE);
                if (arrayField[3].equals("IN_PROGRESS"))
                    task = new Task(arrayField[2], arrayField[4], IN_PROGRESS);
            } else {
                if (arrayField[3].equals("NEW"))
                    task = new Task(arrayField[2], arrayField[4], NEW, arrayField[5], Integer.parseInt(arrayField[6]));
                if (arrayField[3].equals("DONE"))
                    task = new Task(arrayField[2], arrayField[4], DONE, arrayField[5], Integer.parseInt(arrayField[6]));
                if (arrayField[3].equals("IN_PROGRESS"))
                    task = new Task(arrayField[2], arrayField[4], IN_PROGRESS, arrayField[5], Integer.parseInt(arrayField[6]));
            }
        }

        if (arrayField[8].equals("EPIC")) {

            if (arrayField[3].equals("NEW"))
                task = new Epic(arrayField[2], arrayField[4]);
            if (arrayField[3].equals("DONE"))
                task = new Epic(arrayField[2], arrayField[4]);
            if (arrayField[3].equals("IN_PROGRESS"))
                task = new Epic(arrayField[2], arrayField[4]);
        }

        if (arrayField[8].equals("SUBTASK")) {
            if (arrayField[5].equals("время начала не указано")) {
                if (arrayField[3].equals("NEW"))
                    task = new SubTask(arrayField[2], arrayField[4], NEW, Integer.parseInt(arrayField[9]));
                if (arrayField[3].equals("DONE"))
                    task = new SubTask(arrayField[2], arrayField[4], DONE, Integer.parseInt(arrayField[9]));
                if (arrayField[3].equals("IN_PROGRESS"))
                    task = new SubTask(arrayField[2], arrayField[4], IN_PROGRESS, Integer.parseInt(arrayField[9]));
            } else {

                if (arrayField[3].equals("NEW"))
                    task = new SubTask(arrayField[2], arrayField[4], NEW, arrayField[5], Integer.parseInt(arrayField[6]), Integer.parseInt(arrayField[9]));
                if (arrayField[3].equals("DONE"))
                    task = new SubTask(arrayField[2], arrayField[4], DONE, arrayField[5], Integer.parseInt(arrayField[6]), Integer.parseInt(arrayField[9]));
                if (arrayField[3].equals("IN_PROGRESS"))
                    task = new SubTask(arrayField[2], arrayField[4], IN_PROGRESS, arrayField[5], Integer.parseInt(arrayField[6]), Integer.parseInt(arrayField[9]));
            }
        }

        assert task != null;
        task.setId(Integer.parseInt(arrayField[0]));
        instalTaskType(task);

        return task;
    }

    // Создаем изменяемую строку. Обращаемся к списку истории нашего менеджера и для каждой задачи берем его id и записываем
    // в нашу строку через разделитель. Преобразуем изменяемую строку в обычную и возвращаем.
    private static String historyToString(HistoryManager manager) {

        StringBuilder builder = new StringBuilder();

        for (Task task : manager.getHistory()) {
            builder.append(task.getId()).append(",");
        }

        return builder.toString();

    }

    // Создаем список целых чисел. Разбиваем строку на подстроки по разделителю, и каждую подстроку преобразуем в целое
    // число и заносим в наш список. Возвращаем список.
    private static List<Integer> historyFromString(String value){

        List<Integer> arrayId = new ArrayList<>();

        String[] arrayLine = value.split(",");

        for (String str : arrayLine) {
            arrayId.add(Integer.parseInt(str));
        }
        return arrayId;
    }

    public static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        //берем ссылку на файл в формате стринг
        String path = file.getAbsolutePath();
        //список копия таскмапа содержащий строки
        List <String> taskMapCopy = new ArrayList<>();
        //список копия истории запросов
        List<Integer> taskHistoryCopy = null;

        //строка с данными из файла
        String str;

        try {
            str = Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println("Неизвестная ошибка работы с файлами" + e.getMessage());
            return null;
        }

        //разбиваем общую строку на подстроки равным строкам в файле
        String[] allString = str.split("\n");

        //заполняем массив данный списка таскмапы строками, первая строка нам не нужна, как и две последние тоже.

        if (allString.length <= 2) {

            for (int i = 1; i < allString.length; i++) {
                taskMapCopy.add(allString[i]);
            }
        } else if (!allString[allString.length - 2].equals("")) {

            for (int i = 1; i < allString.length; i++) {
                taskMapCopy.add(allString[i]);
            }
        }

        if ((allString.length > 2) && (allString[allString.length - 2].equals(""))) {

            for (int i = 1; i < allString.length - 2; i++) {
                taskMapCopy.add(allString[i]);
            }
        }

        //берем последнюю строку файла и заполняем из нее список содержащий id задач из списка истории
        if (allString.length > 3 && allString[allString.length - 2].equals("")) {
            taskHistoryCopy = historyFromString(allString[allString.length - 1]);
        }

        //работаем со списком таскмапы, полученным из загруженного файла
        for (String taskStr : taskMapCopy) {

            //создаем из каждой строки задачу
            Task task = fromString(taskStr);

            //если получился эпик - обновляем его статус
            if (task.getTaskType().equals(TaskType.EPIC)) {
                fileBackedTasksManager.loadEpicTask((Epic) task);
                fileBackedTasksManager.instalStatusEpic((Epic) task);
            }
            //если получился субтаск - обновляем статус эпика родителя
            else if (task.getTaskType().equals(TaskType.SUBTASK)) {
                fileBackedTasksManager.loadSubTask((SubTask) task);
                fileBackedTasksManager.instalStatusEpic((Epic) fileBackedTasksManager.taskMap.get(((SubTask) task).getEpic()));
            } else {
                fileBackedTasksManager.loadTask(task);
                instalTaskType(task);
            }
        }

        if (taskHistoryCopy != null) {
            for (Integer taskID : taskHistoryCopy) {
                inMemoryHistoryManager.add(fileBackedTasksManager.taskMap.get(taskID));
            }
        }

        fileBackedTasksManager.setInMemoryHistoryManager(inMemoryHistoryManager);


        return fileBackedTasksManager;
    }
//
//    public static void main(String[] args) {
//
//        File file = new File("resources/taskTracker.txt");
//
//        Managers manager = new Managers();
//        TaskManager taskManager = manager.getFileBackedTasksManager(file);
//
//        Task firstTask = new Task("Task 1",
//                "Первая задача",
//                Status.NEW, "25.02.2023|15:00", 10);
//
//        Task secondTask = new Task("Task 2",
//                "Вторая задача",
//                Status.NEW, "25.02.2023|14:00", 120);
//
//        Task threeTask3 = new Task("Task 3",
//                "Третья задача",
//                Status.NEW);
//
//        Epic firstEpicTask = new Epic("Epic 1",
//                "Первый Эпик");
//
//        SubTask firstStep = new SubTask("SubTask 1 Epic 1",
//                "Первая подзадача Эпика 1",
//                Status.IN_PROGRESS,"17.02.2023|12:00", 120,
//                firstEpicTask.getId());
//
//        SubTask secondStep = new SubTask("SubTask 2 Epic 1",
//                "Вторая подзадача Эпика 1",
//                Status.DONE,"17.02.2023|13:00", 120,
//                firstEpicTask.getId());
//
//        SubTask thirdStep = new SubTask("SubTask 3 Epic 1",
//                "Третья подзадача Эпика 1",
//                Status.DONE,firstEpicTask.getId());
//
//        SubTask fourthStep = new SubTask("SubTask 4 Epic 1",
//                "Четвертая подзадача Эпика 1",
//                Status.DONE,"19.02.2023|13:00", 10,
//                firstEpicTask.getId());
//
//        Epic secondEpicTask = new Epic("Epic 2",
//                "Второй Эпик");
//
//        taskManager.loadTask(firstTask);
//        taskManager.loadTask(secondTask);
//        taskManager.loadTask(threeTask3);
//        taskManager.loadEpicTask(firstEpicTask);
//        taskManager.loadSubTask(firstStep);
//        taskManager.loadSubTask(secondStep);
//        taskManager.loadSubTask(thirdStep);
//        taskManager.loadSubTask(fourthStep);
//        taskManager.loadEpicTask(secondEpicTask);
//
//        taskManager.getSubTask(5);
//        taskManager.getEpic(4);
//        taskManager.getTask(2);
//        taskManager.getTask(1);
//        taskManager.getSubTask(6);
//        taskManager.getTask(2);
//        taskManager.getSubTask(7);
//        taskManager.getEpic(9);
//
//        //System.out.println("Удаление задач");
//        taskManager.removeTask(1);
//        taskManager.removeSubTask(8);
//
//        System.out.println("\nСоздали второй менеджер и загрузили задачи из файла: " + file.getAbsolutePath());
//        TaskManager loadFromFile = loadFromFile(file);
//
//        System.out.println();
//
//        assert loadFromFile != null;
//        loadFromFile.printHistoryList();
//
//        System.out.println();
//        System.out.println("Полный список задач");
//
//        for (Task task: loadFromFile.getTaskMap().values()) {
//            System.out.println(task);
//        }
//
//        System.out.println("\nОтсортированный список");
//        taskManager.printPrioritizedTasks();
//
//    }

}