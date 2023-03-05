
package tracker.service;
import tracker.model.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;

    private int size = 0;

    private HashMap<Integer, Node> mapHistory = new HashMap<>();

    private static final int LIST_HISTORY_SIZE = 10;

    // Проверяем по ключу есть ли такая нода в мапе истории запросов, если есть то удаляем ее, после этого добавляем ее
    // в конец списка, если же найти ноду не удалось в мапе, то просто добавляем ее в конец списка

    public void clear(){
        head = null;
        tail = null;
        mapHistory.clear();
    }

    @Override
    public void add(Task task) {

        if (mapHistory.containsKey(task.getId())){
            remove(task.getId());
            linkLast(task);
        } else {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {

        if (mapHistory.containsKey(id)) {
            removeNode(mapHistory.get(id));

        }
    }

    //возвращаем результат метод getTask()
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    // Создаем новую ноду и помещаем в нее хвост списка. Создаем новую ноду, где на месте предыдущего элемента будет
    // Хвост нашего списка, а в теле Ноды наша Таск из аргумента, Делаем проверки. Присваиваем хвосту нашего списка
    // значение новой ноды. Заполняем мапу новым значением.

    //Добавить вконец списка
    public void linkLast(Task element) {

        if (size == LIST_HISTORY_SIZE) {
            removeNode(head);
        }

        Node oldTail = tail;
        Node newNode = new Node<>(element, oldTail, null);

        if (oldTail == null) head = newNode;
        else oldTail.next = newNode;

        this.tail = newNode;
        mapHistory.put(element.getId(), newNode);

        size++;
    }

    // Создаем список, если голова списка не равна нулу, создаем новую ноду равную голове списка, пока нода не равна нулу
    // добавляем елемент(дату) этой ноды в список задач, и загружаем в ноду следующую.
    public List<Task> getTasks() {

        List<Task> list = new ArrayList<>();

        if (this.head != null) {

            Node node = this.head;

            while (node != null) {
                list.add(node.data);
                node = node.next;
            }
        } else {

            return null;
        }
        return list;
    }

    // В аргумент метода подаем ноду. Создаем два поля предыдущая нода и следующая. Если предыдущая нода = нулу, значит
    // мы в голове, тогда предыдущая ссылка некст ноды = нулу, а голова равна нект ноде. Аналогично на счет конца списка.
    // Если следующая ссылка некстноды = нулу, то хвост равен предыдущей ноды. Иначе же наша исходная нода находится в
    // середине списка, и тогда меняем ссылки.
    public void removeNode (Node node) {

        if (mapHistory.containsValue(node)) {

            Node removeNode = node;
            Node prevNode = node.prev;
            Node nextNode = node.next;

            if (prevNode == null) {
                nextNode.prev = null;
                this.head = nextNode;

            } else if (nextNode == null) {
                prevNode.next = null;
                this.tail = prevNode;
            } else {
                prevNode.next = nextNode;
                nextNode.prev = prevNode;
            }

            mapHistory.remove(removeNode.data.getId());

            size--;
        }
    }

    private static class Node<E> {
        Task data;
        Node<E> prev;
        Node<E> next;

        public Node(Task data, Node<E> prev, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

}