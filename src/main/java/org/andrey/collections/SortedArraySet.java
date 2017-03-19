package org.andrey.collections;

import java.util.*;

/**
 * Created by andrey on 18.03.2017.
 */
public class SortedArraySet<T> implements NavigableSet<T> {
    private SortedArrayMap<T, Boolean> map;

    public SortedArraySet(SortedArrayMap<T, Boolean> map) {
        this.map = map;
    }

    @Override
    public T lower(T t) {
        return map.lowerKey(t);
    }

    @Override
    public T floor(T t) {
        return map.floorKey(t);
    }

    @Override
    public T ceiling(T t) {
        return map.ceilingKey(t);
    }

    @Override
    public T higher(T t) {
        return map.higherKey(t);
    }

    @Override
    public T pollFirst() {
        return map.pollFirstEntry().getKey();
    }

    @Override
    public T pollLast() {
        return map.pollLastEntry().getKey();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public Iterator<T> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override
    public boolean add(T t) {
        return map.put(t, Boolean.TRUE);
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream()
                .reduce(true, (flag, el)->flag && map.containsKey(el), (a, b)->a && b);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.stream()
                .forEach((el)->map.put(el, Boolean.TRUE));
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return c.stream()
                .reduce(true, (flag, el) -> flag && c.remove(el), (a, b)-> a && b);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public NavigableSet<T> descendingSet() {
        return new SortedArraySet<>((SortedArrayMap<T, Boolean>) map.descendingMap());
    }

    @Override
    public Iterator<T> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        return new SortedArraySet<>((SortedArrayMap<T, Boolean>) map.subMap(fromElement, fromInclusive, toElement, toInclusive));
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        return new SortedArraySet<>((SortedArrayMap<T, Boolean>) map.headMap(toElement, inclusive));
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        return new SortedArraySet<>((SortedArrayMap<T, Boolean>) map.tailMap(fromElement, inclusive));
    }

    @Override
    public Comparator<? super T> comparator() {
        return map.comparator();
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return new SortedArraySet<>((SortedArrayMap<T, Boolean>) map.subMap(fromElement, toElement));
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return new SortedArraySet<>((SortedArrayMap<T, Boolean>) map.headMap(toElement));
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return new SortedArraySet<>((SortedArrayMap<T, Boolean>) map.tailMap(fromElement));
    }

    @Override
    public T first() {
        return map.firstKey();
    }

    @Override
    public T last() {
        return map.lastKey();
    }
}
