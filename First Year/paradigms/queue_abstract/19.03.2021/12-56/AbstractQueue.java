package queue;

import java.util.Objects;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    public void enqueue(Object element) {
        Objects.requireNonNull(element);
        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);

    public Object dequeue() {
        assert size > 0;
        Object result = element();
        size--;
        dequeueImpl();
        return result;
    }

    protected abstract void dequeueImpl();

    public Object element() {
        assert size > 0;
        return elementImpl();
    }

    protected abstract Object elementImpl();

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
        clearImpl();
    }

    protected abstract void clearImpl();

    /*
    0 - getNth(n) – создать очередь, содержащую каждый n-й элемент, считая с 1
    1 - removeNth(n) – создать очередь, содержащую каждый n-й элемент, и удалить их из исходной очереди
    2 - dropNth(n) – удалить каждый n-й элемент из исходной очереди
    */

    private Queue functionNth(int n, boolean drop) {
        Queue queue = createNew();
        int sz = size;
        for (int i = 0; i < sz; i++) {
            Object elem = this.dequeue();
            if ((i + 1) % n != 0) {
                this.enqueue(elem);
                continue;
            }
            if (!drop) this.enqueue(elem);
            queue.enqueue(elem);
        }

        return queue;
    }

    public Queue getNth(int n) {
        return functionNth(n, false);
    }

    public Queue removeNth(int n) {
        return functionNth(n, true);
    }

    public void dropNth(int n) {
        functionNth(n, true);
    }


    protected abstract Queue createNew();

}
