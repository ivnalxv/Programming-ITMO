package queue;

public class MyArrayQueueTest {
    public static void fill(ArrayQueue queue) {
        for (int i = 10; i >= 1; i--) {
            queue.enqueue(i*10);
            //queue.push(i*10);
        }
    }

    public static void dump(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(
                    "SIZE: " + queue.size() +
                            " ELEM: " + queue.element() +
                            " DEQ: " + queue.dequeue()
            );
        }
    }

    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue();

        fill(queue);
        dump(queue);
    }
}
