package queue;

public class MyQueueTest {
    public static void fill(Queue queue) {
        for (int i = 10; i >= 1; i--) {
            queue.enqueue(i*10);
            //queue.push(i*10);
        }
    }

    public static void dump(Queue queue) {
        while (!queue.isEmpty()) {
            System.out.println(
                    "SIZE: " + queue.size() +
                            " ELEM: " + queue.element() +
                            " DEQ: " + queue.dequeue()
            );
        }
    }

    public static void main(String[] args) {
        Queue queue = new ArrayQueue();

        fill(queue);
        dump(queue);
    }
}
