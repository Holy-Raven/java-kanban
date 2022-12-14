import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.Manager;
import tracker.util.Status;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager(1);

        System.out.println("Cоздание отдельных задач");

        Task firstTask = new Task("Закрыть ипотеку",
                "Выплатить всю сумму задолженности",
                manager.getId(),
                Status.NEW);
        manager.loadTask(firstTask);

        Task secondTask = new Task("Купить кошку",
                "Подобрать подходящий экземпляр и приобрести",
                manager.getId(),
                Status.NEW);
        manager.loadTask(secondTask);

        System.out.println("Создание эпика и две подзадачи в нем");

        Epic firstEpicTask = new Epic("Купить Mustang",
                "Shelby GT 500 black edition",
                manager.getId());
        manager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("Стать IT специалистом",
                "Закончить курс Яндекс практикума, познать тайны JAVA",
                manager.getId(),
                Status.DONE,
                firstEpicTask.getId());
        manager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("Устроиться на работу",
                "Получать заработную плату от 150 000 руб.",
                manager.getId(),
                Status.DONE,
                firstEpicTask.getId());
        manager.loadSubTask(secondStep);

        System.out.println("Создание эпика и одной подзадачи в нем");

        Epic secondEpicTask = new Epic("Отдых за границей",
                "Посетить остров мечты - Бали",
                manager.getId());
        manager.loadEpicTask(secondEpicTask);

        SubTask thirdStep = new SubTask("Оформить документы",
                "Необходимо получить загранпаспорт",
                manager.getId(),
                Status.NEW,
                secondEpicTask.getId());
        manager.loadSubTask(thirdStep);

        System.out.println("Удаляем задачи с 1 по 3");

        manager.removeTask(1);
        manager.removeTask(2);
        manager.removeEpicTask(3);
        manager.removeSubTask(7);

        System.out.println("Выводим полный список задач");
        for (Task task: manager.getTaskMap().values()) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("Выводим список эпик задач");
        for (Epic epic : manager.getEpicMap().values()) {
            System.out.println(epic);
        }
        System.out.println();

        System.out.println("Выводим список подзадач");
        for (SubTask subTask : manager.getSubTaskMap().values()) {
            System.out.println(subTask);
        }
        System.out.println();
    }
}