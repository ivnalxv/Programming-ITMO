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

}