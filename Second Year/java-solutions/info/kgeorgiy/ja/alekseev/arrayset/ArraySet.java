package info.kgeorgiy.ja.alekseev.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    private final SetList<E> setList;
    private final Comparator<? super E> comparator;

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(final Comparator<? super E> comparator) {
        this(Collections.emptyList(), comparator);
    }

    public ArraySet(final Collection<? extends E> collection) {
        this(collection, null);
    }

    public ArraySet(final Collection<? extends E> collection, final Comparator<? super E> comparator) {
        SortedSet<E> set = new TreeSet<>(comparator);
        set.addAll(collection);
        this.setList = new SetList<>(new ArrayList<>(set));
        this.comparator = comparator;
    }

    public ArraySet(final SetList<E> setList, final Comparator<? super E> comparator) {
        this.setList = setList;
        this.comparator = comparator;
    }

    @Override
    public E first() {
        return getElement(0);
    }

    @Override
    public E last() {
        return getElement(size() - 1);
    }

    @Override
    public int size() {
        return setList.size();
    }

    @Override
    public E lower(E element) {
        return getElementOrNull(getIndex(element, true, false));
    }

    @Override
    public E floor(E element) {
        return getElementOrNull(getIndex(element, true, true));
    }

    @Override
    public E ceiling(E element) {
        return getElementOrNull(getIndex(element, false, true));
    }

    @Override
    public E higher(E element) {
        return getElementOrNull(getIndex(element, false, false));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(setList, (E) o, comparator) >= 0;
    }

    /**
     * All other methods such as add(), addAll(), remove(), removeAll(), retainAll()
     * already throw {@link UnsupportedOperationException} because ArraySet extends AbstractSet
     * which extends AbstractCollection which is unmodifiable by default.
     */

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public Iterator<E> iterator() {
        return setList.iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(new SetList<>(setList, true), Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (!isComparable(fromElement, toElement)) {
            throw new IllegalArgumentException();
        }
        int from = getIndex(fromElement, false, fromInclusive);
        int to = getIndex(toElement, true, toInclusive);
        return getSubSet(from, to);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return getSubSet(0, getIndex(toElement, true, inclusive));
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return getSubSet(getIndex(fromElement, false, inclusive), size() - 1);
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @SuppressWarnings("unchecked")
    private boolean isComparable(E first, E second) {
        if (comparator == null) {
            return ((Comparable<E>)first).compareTo(second) <= 0;
        }
        return comparator.compare(first, second) <= 0;
    }

    private E getElement(final int index) {
        final E element = getElementOrNull(index);
        if (element == null) {
            throw new NoSuchElementException();
        }
        return element;
    }

    private E getElementOrNull(final int index) {
        return 0 <= index && index < size() ? setList.get(index) : null;
    }

    private int getIndex(E element, boolean lower, boolean borderIncluded) {
        final int index = Collections.binarySearch(setList, element, comparator);
        if (index >= 0) return index + (borderIncluded ? 0 : (lower ? -1 : 1));
        return -index - 1 - (lower ? 1 : 0);
    }

    private NavigableSet<E> getSubSet(int from, int to) {
        if (from <= to) return new ArraySet<>(setList.subList(from, to + 1), comparator);
        return new ArraySet<>(comparator);
    }

    private static class SetList<T> extends AbstractList<T> implements RandomAccess {
        private boolean reversed = false;
        public final List<T> array;
        private final int start;
        private final int end;

        public SetList(List<T> array) {
            this.array = Collections.unmodifiableList(array);
            this.start = 0;
            this.end = array.size();
        }

        public SetList(SetList<T> setList, boolean toReverse) {
            this(setList.array, setList.start, setList.end, toReverse ^ setList.reversed);
        }

        public SetList(List<T> array, int start, int end, boolean isReversed) {
            this.array = Collections.unmodifiableList(array);
            this.start = start;
            this.end = end;
            this.reversed = isReversed;
        }

        @Override
        public SetList<T> subList(int fromIndex, int toIndex) {
            int from = reversed ? end - 1 - (toIndex - 1) : start + fromIndex;
            int to = reversed ? end - 1 - fromIndex + 1 : start + toIndex;
            return new SetList<>(array, from, to, reversed);
        }

        @Override
        public T get(int index) {
            return reversed ? array.get(end - 1 - index) : array.get(start + index);
        }

        @Override
        public int size() {
            return end - start;
        }
    }

}
