package queue;

// Model:
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

    // Pre: element != null
    // Post: n = n' + 1
    //  && a[n] == element
    //  && \forall i \in [0; n') :  a[i] = a'[i]
    void enqueue(Object element);


    // Pre: n > 0
    // Post: n = n' - 1
    //  && \forall i \in [0; n) :  a[i] = a'[i + 1]
    // && R == a'[0]
    Object dequeue();


    // Pre: n > 0
    // Post: n = n' && R == a[0]
    //  && \forall i \in [0; n) :  a[i] = a'[i]
    Object element();


    // Post: n = n' && && R == n
    //  && \forall i \in [0; n) :  a[i] = a'[i]
    int size();


    // Post: n = n' && && R == (n == 0)
    //  && \forall i \in [0; n) :  a[i] = a'[i]
    boolean isEmpty();


    // Post: n = 0
    //  && \forall elem \in a :  elem == null
    void clear();


    // Pre: (k is argument) k \in N (Natural numbers)
    // Post: R == [a[k - 1], a[2 * k - 1], ... , a[k * floor(n/k) - 1]]
    //  && n = n'
    //  && \forall i \in [0; n) :  a[i] = a'[i]
    Queue getNth(int n);


    // Pre: (k is argument) k \in N (Natural numbers)
    // Post: R == [a'[k - 1], a'[2 * k - 1], ... , a'[k * floor(n/k) - 1]]
    //  && n = n' - floor(n/k)
    //  && \forall i \in [0; n) :  a[i] = a'[i + floor(i/k)]
    Queue removeNth(int n);


    // Pre: (k is argument) k \in N (Natural numbers)
    // Post: n = n' - floor(n/k)
    //  && \forall i \in [0; n) :  a[i] = a'[i + floor(i/k)]
    void dropNth(int n);

}
