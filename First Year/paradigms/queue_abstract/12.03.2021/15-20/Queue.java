package queue;

//Model:
//    [a[0], a[1], a[2], .... , a[n - 1]]
//    n >= 0 - is size of queue
// INV: \forall i \in [0; n) :  a[i] != null


public interface Queue {
    /*
    + + enqueue – добавить элемент в очередь;
    + + dequeue – удалить и вернуть первый элемент в очереди;
    + + element – первый элемент в очереди;
    + + size – текущий размер очереди;
    + + isEmpty – является ли очередь пустой;
    + clear – удалить все элементы из очереди.
     */


    // Pre: elem != null
    // Post: n' = n + 1 && a[n]' == elem
    // \forall i \in [0; n) :  a[i]' = a[i]
    void enqueue(Object element);

    // Pre: n > 0
    // Post: n' = n - 1
    // \forall i \in [0; n') :  a[i]' = a[i]
    // R == a[0]
    Object dequeue();

    // Pre: n > 0
    // Post: R == a[0] && n' = n
    // && \forall i \in [0; n) :  a[i]' = a[i]
    Object element();

    // Post: R == n && n' = n &&
    // \forall i \in [0; n) :  a[i]' = a[i]
    int size();

    // Post: R == (n == 0) && n' = n
    // && \forall i \in [0; n) :  a[i]' = a[i]
    boolean isEmpty();

    // Post: n' = 0
    // && \forall elem \in a :  elem == null
    void clear();

}
