package queue;

public class LinkedQueue extends AbstractQueue {
    private Node head, tail;

    private static class Node {
        private final Object value;
        private Node next;

        public Node(Object value, Node prev) {
            assert value != null;

            this.value = value;
            if (prev != null) {
                prev.next = this;
            }
        }
    }

    @Override
    public void enqueueImpl(Object element) {
        tail = new Node(element, tail);

        if (head == null) {
            head = tail;
        }
    }

    @Override
    public void dequeueImpl() {
        if (head == tail) {
            head = null;
        } else {
            head = head.next;
        }
    }

    @Override
    public Object elementImpl() {
        return head.value;
    }

    @Override
    public void clearImpl() {
        tail = head = null;
    }

     /*
    getNth(n) – создать очередь, содержащую каждый n-й элемент, считая с 1
    removeNth(n) – создать очередь, содержащую каждый n-й элемент, и удалить их из исходной очереди
    dropNth(n) – удалить каждый n-й элемент из исходной очереди
     */

    public Queue getNth(int n) {
        LinkedQueue queue = new LinkedQueue();


        return null;
    };

    public void dropNth(int n) {

    };

}
