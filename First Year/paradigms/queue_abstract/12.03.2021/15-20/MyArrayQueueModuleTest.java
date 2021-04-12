package queue;

public class MyArrayQueueModuleTest {
    public static void fill() {
        for (int i = 10; i >= 1; i--) {
            ArrayQueueModule.enqueue(i*10);
            ArrayQueueModule.push(i*10);
        }
    }

    public static void dump() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(
                    "SIZE: " + ArrayQueueModule.size() +
                            " ELEM: " + ArrayQueueModule.element() +
                            " DEQ: " + ArrayQueueModule.dequeue()
            );
        }
    }

    public static void main(String[] args) {
        fill();
        dump();
    }
}
