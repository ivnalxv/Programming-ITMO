package queue;

public class MyArrayQueueADTTest {
    public static void fill(ArrayQueueADT queue) {
        for (int i = 10; i >= 1; i--) {
            ArrayQueueADT.enqueue(queue, i*10);
            ArrayQueueADT.push(queue, i*10);
        }
    }

    public static void dump(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println(
                    "SIZE: " + ArrayQueueADT.size(queue) +
                            " ELEM: " + ArrayQueueADT.element(queue) +
                            " DEQ: " + ArrayQueueADT.dequeue(queue)
            );
        }
    }

    public static void main(String[] args) {
        ArrayQueueADT queue = new ArrayQueueADT();

        fill(queue);
        dump(queue);
    }
}
