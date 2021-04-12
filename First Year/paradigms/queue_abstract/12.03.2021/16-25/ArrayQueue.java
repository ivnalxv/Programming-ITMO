package queue;



//Model:
//    [a[0], a[1], a[2], .... , a[n - 1]]
//    n >= 0 - is size of queue
// INV: \forall i \in [0; n) :  a[i] != null

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

    public ArrayQueue getNth(int n) {
        ArrayQueue queue = new ArrayQueue();

        for (int i = n - 1; i < size; i += n) {
            queue.enqueue(elements[i]);
        }

        return queue;
    }


    public void dropNth(int n) {
        Object[] newElements = new Object[elements.length];
        for (int i = 0; i <= size/n; i++) {
            int pointer = (head + n * i) % elements.length;
            int len = (i < size/n ? n - 1 : size % n);
            System.arraycopy(elements, pointer, newElements, i * n - i, len);
        }

        elements = newElements;
        size -= size/n;
        head = 0;
    }

    public void pr() {
        System.out.println("HEAD: " + head);
        System.out.println("SIZE: " + size);
        for (int i = 0; i < elements.length; i++) {
            System.out.print(elements[i] + " ");
        }
        System.out.println();
    }

}
