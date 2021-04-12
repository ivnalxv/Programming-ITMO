package queue;

import java.util.Arrays;
import java.util.Objects;

//Model:
//    [a[0], a[1], a[2], .... , a[n - 1]]
//    queue.n >= 0 - is size of array
// INV: \forall i \in [0; queue.n) :  queue.a[i] != null

public class ArrayQueueADT {

    private int head = 0, size = 0;
    private Object[] elements = new Object[1];

    public static ArrayQueueADT create(ArrayQueueADT queue) {
        return new ArrayQueueADT();
    }

    // Pre: queue != null && queue.n > 0
    // Post: queue.n' = queue.n - 1
    // \forall i \in [0; queue.n') :  queue.a[i]' = queue.a[i]
    // R == a[0]
    public static Object dequeue(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        assert queue.size > 0;
        queue.size--;
        Object elem = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = (queue.head + 1) % queue.elements.length;
        return elem;
    }

    // Pre: queue != null && queue.n > 0
    // Post: queue.n' = queue.n - 1
    // \forall i \in [0; queue.n') :  queue.a[i]' = queue.a[i + 1]
    // R == queue.a[n - 1]
    public static Object remove(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        assert queue.size > 0;
        queue.size--;
        int tail = (queue.head + queue.size) % queue.elements.length;
        Object elem = queue.elements[tail];
        queue.elements[tail] = null;
        return elem;
    }

    // Pre: elem != null && queue != null
    // Post: queue.n' = queue.n + 1 && queue.a[queue.n]' == element
    // \forall i \in [0; queue.n) :  queue.a[i]' = queue.a[i]
    public static void enqueue(ArrayQueueADT queue, Object elem) {
        Objects.requireNonNull(elem);
        Objects.requireNonNull(queue);

        ensureCapacity(queue, queue.size + 1);
        int tail = (queue.head + queue.size) % queue.elements.length;
        queue.elements[tail] = elem;
        queue.size++;
    }

    // Pre: elem != null && queue != null
    // Post: queue.n' = queue.n + 1
    // queue.a[0]' = elem && \forall i \in [0; queue.n') :  queue.a[i]' = queue.a[i - 1]
    public static void push(ArrayQueueADT queue, Object elem) {
        Objects.requireNonNull(elem);
        Objects.requireNonNull(queue);

        ensureCapacity(queue, queue.size + 1);
        queue.head = (queue.head - 1 + queue.elements.length) % queue.elements.length;
        queue.elements[queue.head] = elem;
        queue.size++;
    }

    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        Objects.requireNonNull(queue);

        if (capacity > queue.elements.length) {
            int tail = (queue.head + queue.size) % queue.elements.length;
            Object[] obj = new Object[2 * capacity];

            if (queue.head < tail || queue.size == 0) {
                System.arraycopy(queue.elements, queue.head, obj, 0, queue.size);
            } else {
                System.arraycopy(queue.elements, queue.head, obj, 0, queue.elements.length - queue.head);
                System.arraycopy(queue.elements, 0, obj, queue.elements.length - queue.head, tail);
            }

            queue.head = 0;
            queue.elements = obj;
        }
    }


    // Pre: queue != null && queue.n > 0
    // Post: R == queue.a[0] && queue.n' = queue.n &&
    // \forall i \in [0; queue.n) :  queue.a[i]' = queue.a[i]
    public static Object element(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        assert queue.size > 0;
        return queue.elements[queue.head];
    }

    // Pre: n > 0 && queue != null
    // Post: R == queue.a[queue.n - 1] && queue.n' == queue.n
    // && \forall i \in [0; queue.n) :  queue.a[i]' = queue.a[i]
    public static Object peek(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        assert queue.size > 0;
        return queue.elements[(queue.head + queue.size - 1) % queue.elements.length];
    }

    // Pre: queue != null
    // Post: R == queue.n && queue.n' = queue.n &&
    // \forall i \in [0; queue.n) :  queue.a[i]' = queue.a[i]
    public static int size(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        return queue.size;
    }

    // Pre: queue != null
    // Post: R == (queue.n == 0) && queue.n' = queue.n &&
    // \forall i \in [0; queue.n) :  queue.a[i]' = queue.a[i]
    public static boolean isEmpty(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        return queue.size == 0;
    }

    // Pre: queue != null
    // Post: queue.n' = 0 &&
    // \forall elem \in a :  elem == null
    public static void clear(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        queue.elements = new Object[1];
        queue.head = 0;
        queue.size = 0;
    }

    // Pre: queue != null
    // Post: R == queue.a && queue.n' = queue.n
    // && \forall i \in [0; queue.n) :  queue.a[i]' = queue.a[i]
    public static Object[] toArray(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        Object[] temp = new Object[queue.size];
        if (queue.size == 0) return temp;
        int tail = (queue.head + queue.size) % queue.elements.length;
        int len = (tail > queue.head ? tail - queue.head : queue.elements.length - queue.head);
        System.arraycopy(queue.elements, queue.head, temp, 0, len);
        System.arraycopy(queue.elements, 0, temp, len, tail > queue.head ? 0 : tail);
        return temp;
    }

    // Pre: queue != null
    // Post: temp == String('[')
    // for elem in queue.a :
    //      if (elem is not last) temp += elem + " ,"
    //         else temp += elem + ']'
    // R == temp
    // && queue.n' = queue.n && \forall i \in [0; queue.n) :  queue.a[i]' = queue.a[i]
    public static String toStr(ArrayQueueADT queue) {
        Objects.requireNonNull(queue);

        return Arrays.toString(toArray(queue));
    }
}
