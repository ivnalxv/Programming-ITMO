package queue;



public class ArrayQueue extends AbstractQueue {
    private int head = 0;
    private Object[] elements = new Object[1];

    @Override
    protected void enqueueImpl(Object elem) {
        ensureCapacity(size + 1);
        elements[(head + size) % elements.length] = elem;
    }

    @Override
    protected void dequeueImpl() {
        elements[head] = null;
        head = (head + 1) % elements.length;
    }

    @Override
    protected Object elementImpl() {
        return elements[head];
    }

    @Override
    protected void clearImpl() {
        elements = new Object[1];
        head = 0;
        size = 0;
    }

    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            int tail = (head + size) % elements.length;
            Object[] obj = new Object[2 * capacity];

            if (head < tail || size == 0) {
                System.arraycopy(elements, head, obj, 0, size);
            } else {
                System.arraycopy(elements, head, obj, 0, elements.length - head);
                System.arraycopy(elements, 0, obj, elements.length - head, tail);
            }

            head = 0;
            elements = obj;
        }
    }

    /*
    getNth(n) – создать очередь, содержащую каждый n-й элемент, считая с 1
    removeNth(n) – создать очередь, содержащую каждый n-й элемент, и удалить их из исходной очереди
    dropNth(n) – удалить каждый n-й элемент из исходной очереди
    */


    @Override
    protected ArrayQueue createNew() {
        return new ArrayQueue();
    }


    public void pr() {
        System.out.println("HEAD: " + head);
        System.out.println("SIZE: " + size);
        for (int i = 0; i < elements.length; i++) {
            System.out.print(elements[i] + " ");
        }
        System.out.println();
    }


    public void prN(int n) {
        ((ArrayQueue) this.getNth(n)).pr();
    }


}
