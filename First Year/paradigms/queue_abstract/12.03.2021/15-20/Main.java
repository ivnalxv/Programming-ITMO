package queue;

import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Check program yourself.");
        System.out.println("Enter commands:");

        Scanner sc = new Scanner(System.in);

        LinkedQueue queue = new LinkedQueue();

        while (true) {
            String s = sc.next();
            if (s.equals("stop")) break;
            if (s.equals("pr")) {
                //ArrayQueueModule.print();
            } else if (s.equals("enq")) {
                String t = sc.next();
                queue.enqueue(t);
            } else if (s.equals("push")) {
                String t = sc.next();
                //queue.push(t);
            } else if (s.equals("elem")) {
                System.out.println(queue.element());
            } else if (s.equals("peek")) {
                //System.out.println(ArrayQueueModule.peek());
            } else if (s.equals("sz")) {
                System.out.println(queue.size());
            } else if (s.equals("emp")) {
                System.out.println(queue.isEmpty());
            } else if (s.equals("cls")) {
                queue.clear();
            } else if (s.equals("deq")) {
                System.out.println(queue.dequeue());
            } else if (s.equals("rem")) {
                //System.out.println(ArrayQueueModule.remove());
            } else if (s.equals("arr")) {
                Object[] temp = ArrayQueueModule.toArray();
                for (int i = 0; i < temp.length; i++) {
                    System.out.print(temp[i] + " ");
                }
                System.out.println();
            } else if (s.equals("str")) {
                System.out.println(ArrayQueueModule.toStr());
            } else {
                System.out.println("Unknown cmd");
            }
        }




    }
}
