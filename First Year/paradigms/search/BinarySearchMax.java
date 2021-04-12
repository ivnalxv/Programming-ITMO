package search;

public class BinarySearchMax {

    // Pre: \forall i \in [0; args.length) : args[i] != null && args[i] can be parsed (Integer.parseInt(args[i]))
    // && args != null
    // \exist j : \forall i = 0 .. j - 1 : args[i] < args[i + 1] && \forall i = j .. args.length - 1 : args[i] > args[i+1]
    // Post: int(args[j])
    public static void main(final String[] args) {
        // pre
        final int[] a = new int[args.length];
        // Post: pre && a.length == args.length
        // Pre: pre && a.length == args.length
        int i = 0;
        // Post: pre && i == 1

        // Inv: pre && \forall k \in [0; i): a[k] == int(args[k])
        while (i < args.length) {
            // Pre: Inv && i < args.length
            a[i] = Integer.parseInt(args[i]);
            // Post: pre && a[i] == int(args[i])

            // Pre: Inv && i < args.length && a[i] == int(args[i])
            i++;
            // Post: pre && i = i' + 1
        }
        // Post: \forall i \in [0; args.length) : a[i] != null && a[i] is int
        // \exist j : \forall i = 0 .. j - 1 : a[i] < a[i + 1] && \forall i = j .. a.length - 1 : a[i] > a[i+1]
        //  && a.length >= 0

        //System.out.println(iterativeBinarySearchMax(a));
        System.out.println(recursiveBinarySearchMax(a, -1, a.length - 1));
    }


    // Pre: \forall i \in [0; a.length) : a[i] != null && a[i] is int
    // \exist j : \forall i = 0 .. j - 1 : a[i] < a[i + 1] && \forall i = j .. a.length - 1 : a[i] > a[i+1]
    // \forall i = 0 .. a.length : a[i] <= a[j]; (a[j] is max)
    // a != null  && r - l == (long) r - l && (r - l)/2 + l == (long) (r - l)/2 + l
    // Inv: l < j <= r
    // Post: a[r] == a[j]

    private static int recursiveBinarySearchMax(final int[] a, final int l, final int r) {
        // Pre : l < j <= r
        final int m = (r - l)/2 + l;

        // Pre :l < j <= r && m == (r + l) / 2
        if (r - l > 1) {
            // l < j <= r && m == (r + l) / 2 && r - l > 1
            if (a[m] < a[m + 1]) {
                // Pre: l < j <= r && r - l > 1 && m == (r + l) / 2 && a[m] < a[m + 1]
                // && \forall i = 0 .. m : a[i] < a[j] -> m < j
                // && l < m < j <= r

                // Post: l' < l = m -> r' - l' > r - l
                // r' - l' > r - l

                return recursiveBinarySearchMax(a, m, r);
            }
            // Pre: l < j <= r && r - l > 1 && m == (r + l) / 2 && a[m] >= a[m+1]
            // && \forall i = m + 1 .. a.length - 1 : a[i] >= a[j] -> j <= m
            // && l < j <= m < r

            // Post: r' > m = r -> r' - l' > r - l
            //  r' - l' > r - l

            return recursiveBinarySearchMax(a, l, m);
        }
        // Post: r - l <= 1

        // Pre : l < j <= r && r <= l + 1 && l < j <= r <= l + 1
        // j == l + 1 <= r
        // j == r
        return a[r];
    }

    // Pre: \forall i \in [0; a.length) : a[i] != null && a[i] is int
    // \exist j : \forall i = 0 .. j - 1 : a[i] < a[i + 1] && \forall i = j .. a.length - 1 : a[i] > a[i+1]
    // \forall i = 0 .. a.length : a[i] <= a[j]; (a[j] is max)
    // a != null
    // Post: a[r] == a[j]

    private static int iterativeBinarySearchMax(final int[] a) {
        int l = -1, r = a.length - 1;
        // Post: l == -1 && r ==  a.length - 1

        // Inv : l < j <= r
        while (r - l > 1) {
            // Pre: l == -1 && r == a.length - 1 && r - l > 1 && l < j <= r
            final int m = (r - l)/2 + l;
            // Post : pre && l < j <= r && m == (r + l) / 2

            if (a[m] < a[m + 1]) {
                // Pre : l == -1 && r == a.length - 1 && r - l > 1 && m == (r + l) / 2 && a[m] < a[m + 1] && l < j <= r
                // \forall i = 0 .. m : a[i] < a[j] -> m < j
                // l < m < j <= r
                l = m;

                // Post : r == a.length - 1 && a[m] < a[m + 1] && l == m < j <= r
                // l' < l = m -> r' - l' > r - l
            } else {
                // Pre: l == -1 && r == a.length - 1 && r - l > 1 && m == (r + l) / 2 && a[m] >= a[m + 1] && l < j <= r
                // \forall i = m + 1 .. a.length - 1 : a[i] >= a[j] -> j <= m
                // l < j <= m < r
                r = m;
                // Post : l == -1 && a[m] >= a[m + 1] && l < j <= m == r
                // r' > m = r -> r' - l' > r - l
            }
            // Post :: INV == l < j <= r
            // r' - l' > r - l
        }

        // Pre : l < j <= r && r <= l + 1 && l < j <= r <= l + 1
        // j == l + 1 <= r
        // j == r

        return a[r];
    }
}
