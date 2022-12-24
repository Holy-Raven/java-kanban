import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.service.InMemoryTaskManager;
import tracker.util.Managers;
import tracker.util.Status;

public class Main {

    public static void main(String[] args) {

        Managers manager = new Managers();
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) manager.getDefault();

        System.out.println("Cоздание отдельных задач");

        Task firstTask = new Task("Закрыть ипотеку",
                "Выплатить всю сумму задолженности",
                Status.NEW);
        inMemoryTaskManager.loadTask(firstTask);

        Task secondTask = new Task("Купить кошку",
                "Подобрать подходящий экземпляр и приобрести",
                Status.NEW);
        inMemoryTaskManager.loadTask(secondTask);
        //===================================================================
        System.out.println("Создание эпика и две подзадачи в нем");

        Epic firstEpicTask = new Epic("Купить Mustang",
                "Shelby GT 500 black edition");
        inMemoryTaskManager.loadEpicTask(firstEpicTask);

        SubTask firstStep = new SubTask("Стать IT специалистом",
                "Закончить курс Яндекс практикума, познать тайны JAVA",
                Status.DONE,
                firstEpicTask.getId());
        inMemoryTaskManager.loadSubTask(firstStep);

        SubTask secondStep = new SubTask("Устроиться на работу",
                "Получать заработную плату от 150 000 руб.",
                Status.DONE,
                firstEpicTask.getId());
        inMemoryTaskManager.loadSubTask(secondStep);
        //===================================================================
        System.out.println("Создание эпика и одной подзадачи в нем");

        Epic secondEpicTask = new Epic("Отдых за границей",
                "Посетить остров мечты - Бали");
        inMemoryTaskManager.loadEpicTask(secondEpicTask);

        SubTask thirdStep = new SubTask("Оформить документы",
                "Необходимо получить загранпаспорт",
                Status.NEW,
                secondEpicTask.getId());
        inMemoryTaskManager.loadSubTask(thirdStep);
        //===================================================================
//        System.out.println("Удаление задач с 1 по 3");
//        manager.removeTask(1);
//        manager.removeTask(2);
//        manager.removeEpicTask(3);
//        manager.removeSubTask(7);


        //===================================================================
//      проверяем работу списка просмотров задач

        System.out.println();
        System.out.println("Такие задачи не найдет");
        inMemoryTaskManager.getSubTask(10);
        inMemoryTaskManager.getSubTask(6);
        inMemoryTaskManager.getTask(3);
        inMemoryTaskManager.getEpic(4);
        System.out.println();

        System.out.println("Такие задачи сможем просмотреть");
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(3);     // такая эпик задача есть, но в списке просмотров она уже затёрлась.
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getSubTask(4);  // 10-ая c конца это тут
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getTask(2);
        System.out.println();

        InMemoryTaskManager.getInMemoryHistoryManager().getHistory();

        //===================================================================
//        System.out.println("Выводим полный список задач");
//        for (Task task: manager.getTaskMap().values()) {
//            System.out.println(task);
//        }
//        System.out.println();
//
//        System.out.println("Выводим список эпик задач");
//        for (Epic epic : manager.getEpicMap().values()) {
//            System.out.println(epic);
//        }
//        System.out.println();
//
//        System.out.println("Выводим список подзадач");
//        for (SubTask subTask : manager.getSubTaskMap().values()) {
//            System.out.println(subTask);
//        }

        System.out.println();
    }
}