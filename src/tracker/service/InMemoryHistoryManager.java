package tracker.service;
import tracker.model.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    // Cписок истории запросов пользователя.
    private final ArrayList<Task> taskListHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        taskListHistory.add(task);
    }

    // Проверяем не пуст ли наш список запросов. Если размер списка больше 10, то удаляем первый элемент
    // печатаем список в консоль. Если список пуст - выводим надпись, что список пуст.
    @Override
    public ArrayList<Task> getHistory() {

        if (!taskListHistory.isEmpty()) {

            for (Task task : taskListHistory) {
                System.out.println(task);
            }
        } else {
            System.out.println("Cписок истории задач пуст");
        }
        return taskListHistory;
    }

    // Немного не поняла замечание, в плане, что мой метод getHistory() и так отдельный метод для вывода списка в консоль,
    // но я реализовала метод обновления списка, чтобы удалять лишние значения в нем не в момент его вывода в консоль,
    // а сразу при его пополнении смотреть.

    public void updateListHistory (){

        if (!taskListHistory.isEmpty()) {

            while (taskListHistory.size() > 10) {
                taskListHistory.remove(0);
            }

        } else {
            System.out.println("Cписок истории задач пуст");
        }
    }

}