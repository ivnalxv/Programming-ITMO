package queue;

public class LinkedQueue extends AbstractQueue {
    private Node head, tail;

    private static class Node {
        private final Object value;
        private final Node prev;

        public Node(Object value, Node prev) {
            assert value != null;

            this.value = value;
            this.prev = prev;
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
        }
        tail = tail.prev;
    }

    @Override
    public Object elementImpl() {
        return tail.value;
    }

    @Override
    public void clearImpl() {
        tail = head = null;
    }

}
