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
        head = head.next;
    }

    @Override
    public Object elementImpl() {
        return head.value;
    }

    @Override
    public void clearImpl() {
        tail = head = null;
    }

    @Override
    protected LinkedQueue createNew() {
        return new LinkedQueue();
    }
}
