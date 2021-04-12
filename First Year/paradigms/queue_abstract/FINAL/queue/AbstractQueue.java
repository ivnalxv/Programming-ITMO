package queue;

import java.util.Objects;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    public void enqueue(final Object element) {
        Objects.requireNonNull(element);
        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);

    public Object dequeue() {
        assert size > 0;
        final Object result = element();
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


    private Queue functionNth(final int n, final boolean drop) {
        final Queue queue = createNew();
        final int size = this.size;
        for (int i = 0; i < size; i++) {
            final Object elem = dequeue();
            if ((i + 1) % n != 0) {
                enqueue(elem);
                continue;
            }
            if (!drop) {
                enqueue(elem);
            }
            queue.enqueue(elem);
        }

        return queue;
    }

    public Queue getNth(final int n) {
        return functionNth(n, false);
    }

    public Queue removeNth(final int n) {
        return functionNth(n, true);
    }

    public void dropNth(final int n) {
        functionNth(n, true);
    }


    protected abstract Queue createNew();

}
