import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.Manager;
import tracker.util.Status;

public class Main {

    // Небольшая заметка, когда стала делать метод loadTask не поняла как мне отделить одни объекты от других.
    // Пока не нашла instanceof - я решения не видела, очень грустно, что нам про него не рассказали заранее.
    // В теории наконец рассказали про switch применила его с enum, про них, кстати, тоже не рассказывали, узнала
    // о них с вопросов в пачке, как сделать без enum тоже не представляю, если первый спринт понравился тем, что
    // можно было сделать только со знаниями из теории, то тут не так.

    public static void main(String[] args) {

        Manager manager = new Manager(1);

        System.out.println("Cоздание отдельных задач");

        Task firstTask = new Task("Закрыть ипотеку", "Выплатить всю сумму задолжности", manager.getId(),
                Status.NEW);
        manager.loadTask(firstTask);
        Task secondTask = new Task("Купить кошку", "Подобрать подходящий экземпляр и приобрести",
                manager.getId(), Status.NEW);
        manager.loadTask(secondTask);

        System.out.println("Создание эпика и две подзадачи в нем");

        Epic firstEpicTask = new Epic("Купить Mustang",  "Shelby GT 500 black edition", manager.getId());
        manager.loadTask(firstEpicTask);

        SubTask firstStep = new SubTask("Стать IT специалистом", "Закончить курс Яндекс практикума," +
                " познать тайны JAVA", manager.getId(), Status.NEW, firstEpicTask);
        manager.loadTask(firstStep);
        SubTask secondStep = new SubTask("Устроиться на работу",
                "Получать заработную плату от 150 000 руб.", manager.getId(), Status.NEW, firstEpicTask);
        manager.loadTask(secondStep);

        System.out.println("Создание эпика и одной подзадачи в нем");

        Epic secondEpicTask = new Epic("Отдых за границей",  "Посетить остров мечты - Бали",
                manager.getId());
        manager.loadTask(secondEpicTask);

        SubTask thirdStep = new SubTask("Оформить документы", "Необходимо получить загранпаспорт",
                manager.getId(), Status.NEW, secondEpicTask);
        manager.loadTask(thirdStep);

        manager.removeTask(1);
        manager.removeTask(2);
        manager.removeTask(3);

        for (Task task: manager.getTaskMap().values()) {
            System.out.println(task);
        }
        System.out.println();

        for (Epic epic : manager.getEpicMap().values()) {
            System.out.println(epic);
        }
        System.out.println();

        for (SubTask subTask : manager.getSubTaskMap().values()) {
            System.out.println(subTask);
        }
        System.out.println();
    }
}