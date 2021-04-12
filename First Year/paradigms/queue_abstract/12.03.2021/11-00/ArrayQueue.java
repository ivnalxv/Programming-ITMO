package queue;

import java.util.Arrays;
import java.util.Objects;

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

    // Pre: n > 0
    // Post: n' = n - 1
    // \forall i \in [0; n') :  a[i]' = a[i + 1]
    // R = a[n - 1]
    public  Object remove() {
        assert size > 0;
        size--;
        int tail = (head + size) % elements.length;
        Object elem = elements[tail];
        elements[tail] = null;
        return elem;
    }


    // Pre: elem != null
    // Post: n' = n + 1
    // a[0]' = elem && \forall i \in [0; n') :  a[i]' = a[i - 1]
    public  void push(Object elem) {
        Objects.requireNonNull(elem);
        ensureCapacity(size + 1);
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = elem;
        size++;
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


    // Pre: n > 0
    // Post: R == a[n - 1] && n' == n
    // && \forall i \in [0; n) :  a[i]' = a[i]
    public Object peek() {
        assert size > 0;
        return elements[(head + size - 1) % elements.length];
    }

    // Post: R == n && n' = n &&
    // \forall i \in [0; n) :  a[i]' = a[i]
    public int size() {
        return size;
    }


    // Post: R == (n == 0) && n' = n
    // && \forall i \in [0; n) :  a[i]' = a[i]
    public boolean isEmpty() {
        return size == 0;
    }


    // Post: R == a && n' = n
    // && \forall i \in [0; n) :  a[i]' = a[i]
    public Object[] toArray() {
        Object[] temp = new Object[size];
        if (size == 0) return temp;
        int tail = (head + size) % elements.length;
        int len = (tail > head ? tail - head : elements.length - head);
        System.arraycopy(elements, head, temp, 0, len);
        System.arraycopy(elements, 0, temp, len, tail > head ? 0 : tail);
        return temp;
    }

    // Post: temp == String('[')
    // for elem in a :
    //      if (elem is not last) temp += elem + " ,"
    //         else temp += elem + ']'
    // R == temp
    // && n' = n && \forall i \in [0; n) :  a[i]' = a[i]
    public String toStr() {
        return Arrays.toString(toArray());
    }


}
