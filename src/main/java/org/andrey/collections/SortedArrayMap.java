package org.andrey.collections;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by andrey on 18.03.2017.
 */
public class SortedArrayMap<K, V> implements NavigableMap<K, V> {
    public static final int DEFAULT_CAPACITY = 10;
    public static final int INCREASE_COEF = 2;

    private Entry<K, V>[] array = new Entry[DEFAULT_CAPACITY];
    private Comparator<Entry<K, V>> comparator;
    private Comparator<K> keyComparator;
    private int size = 0;

    public SortedArrayMap(Comparator<K> keyComparator) {
        this.keyComparator = keyComparator;
        this.comparator = (a, b) -> keyComparator.compare(a.getKey(), b.getKey());
    }

    private SortedArrayMap(Comparator<K> keyComparator, Entry<K, V>[] array) {
        this.keyComparator = keyComparator;
        this.comparator = (a, b) -> keyComparator.compare(a.getKey(), b.getKey());
        this.array = array;
        this.size = array.length;
    }

    private int binarySearch(Entry<K, V> key) {
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Entry<K, V> midVal = array[mid];
            int cmp = comparator.compare(midVal, key);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public Entry<K, V> lowerEntry(K key) {
        return null;
    }

    public K lowerKey(K key) {
        return null;
    }

    public Entry<K, V> floorEntry(K key) {
        return null;
    }

    public K floorKey(K key) {
        return null;
    }

    public Entry<K, V> ceilingEntry(K key) {
        return null;
    }

    public K ceilingKey(K key) {
        return null;
    }

    public Entry<K, V> higherEntry(K key) {
        return null;
    }

    public K higherKey(K key) {
        return null;
    }

    public Entry<K, V> firstEntry() {
        return size > 0 ? array[0] : null;
    }

    public Entry<K, V> lastEntry() {
        return size > 0 ? array[size - 1] : null;
    }

    public Entry<K, V> pollFirstEntry() {
        if (size == 0) {
            return null;
        }

        Entry<K, V> entry = array[0];
        remove(entry.key);
        return entry;
    }

    public Entry<K, V> pollLastEntry() {
        if (size == 0) {
            return null;
        }

        Entry<K, V> entry = array[size - 1];
        remove(entry.key);
        return entry;
    }

    public NavigableMap<K, V> descendingMap() {
        Entry<K, V>[] newArray = Arrays.copyOf(array, size);
        Arrays.sort(array, (a, b)-> -comparator.compare(a, b));
        return new SortedArrayMap<>((a, b)-> -keyComparator.compare(a, b), newArray);
    }

    public NavigableSet<K> navigableKeySet() {
        return new KeySet<>(this);
    }

    public NavigableSet<K> descendingKeySet() {
        return new KeySet<>((SortedArrayMap<K, V>) descendingMap());
    }

    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        if (size == 0) {
            return new SortedArrayMap<>(keyComparator);
        }

        int fromIndex = binarySearch(new Entry<K, V>(fromKey));
        int toIndex = binarySearch(new Entry<K, V>(toKey));

        fromIndex = fromIndex < 0 ? -fromIndex - 1 : fromIndex;
        toIndex = toIndex < 0 ? -toIndex - 1 : toIndex;

        fromIndex = fromInclusive ? fromIndex : fromIndex + 1;
        toIndex = toInclusive ? toIndex : toIndex - 1;

        boolean rangeIsCorrect = fromIndex < toIndex && fromIndex >= 0 && toIndex < size;
        if (!rangeIsCorrect) {
            throw new IllegalArgumentException("Incorrect range");
        }

        int newSize = toIndex - fromIndex + 1;

        if (newSize == 0) {
            return new SortedArrayMap<>(keyComparator);
        }

        Entry<K, V>[] newArray = new Entry[newSize];
        System.arraycopy(array, fromIndex, newArray, 0, newSize);
        return new SortedArrayMap<>(keyComparator, newArray);
    }

    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        if (size == 0) {
            return new SortedArrayMap<>(keyComparator);
        }

        return subMap(array[0].key, true, toKey, inclusive);
    }

    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        if (size == 0) {
            return new SortedArrayMap<>(keyComparator);
        }

        return subMap(fromKey, inclusive, array[size - 1].key, inclusive);
    }

    public Comparator<? super K> comparator() {
        return keyComparator;
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    public SortedMap<K, V> headMap(K toKey) {
        return headMap(toKey, false);
    }

    public SortedMap<K, V> tailMap(K fromKey) {
        return tailMap(fromKey, true);
    }

    public K firstKey() {
        return size > 0 ? array[0].key : null;
    }

    public K lastKey() {
        return size > 0 ? array[size - 1].key : null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(Object key) {
        int index = binarySearch(new Entry<K, V>((K) key));
        return index >= 0;
    }

    public boolean containsValue(Object value) {
        return Arrays
                .stream(array)
                .filter((e)->e.value == null && value == null || e.value != null && e.value.equals(value))
                .findAny()
                .isPresent();
    }

    public V get(Object key) {
        int index = binarySearch(new Entry<>((K) key));
        return index >= 0 ? array[index].value : null;
    }

    private void ensureSize(int req) {
        if (req > array.length) {
            array = Arrays.copyOf(array, size * INCREASE_COEF);
        }
    }

    public V put(K key, V value) {
        Entry<K, V> entity = new Entry<K, V>(key, value);
        int index = binarySearch(entity);
        int fixedIndex = index < 0 ? -index - 1 : index;

        if (index >= 0) {
            Entry<K, V> old = array[index];
            array[index].value = value;
            return old.value;
        }

        if (fixedIndex == size) {
            ensureSize(size + 1);
            array[size++] = entity;
            return null;
        }

        ensureSize(size + 1);
        System.arraycopy(array, fixedIndex, array, fixedIndex + 1, size - fixedIndex);
        array[fixedIndex] = entity;
        size++;
        return null;
    }

    public V remove(Object key) {
        int index = binarySearch(new Entry<>((K) key));

        if (index < 0) {
            return null;
        }

        V old = array[index].value;
        if (index == size - 1) {
            array[index] = null;
        } else {
            System.arraycopy(array, index + 1, array, index, size - index - 1);
        }
        size--;
        return old;
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        map
            .entrySet()
            .stream()
            .forEach((e) -> put(e.getKey(), e.getValue()));
    }

    public void clear() {
        array = new Entry[DEFAULT_CAPACITY];
    }

    public Set<K> keySet() {
        return Arrays
                .stream(array)
                .map(Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Collection<V> values() {
        return Arrays
                .stream(array)
                .map(Entry::getValue)
                .collect(Collectors.toSet());
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return Arrays
                .stream(array)
                .collect(Collectors.toSet());
    }

    public class KeySet<K> extends AbstractSet<K> implements NavigableSet<K> {
        private SortedArrayMap<K, V> map;

        public KeySet(SortedArrayMap<K, V> map) {
            this.map = map;
        }

        @Override
        public K lower(K k) {
            return map.lowerKey(k);
        }

        @Override
        public K floor(K k) {
            return map.floorKey(k);
        }

        @Override
        public K ceiling(K k) {
            return map.ceilingKey(k);
        }

        @Override
        public K higher(K k) {
            return map.higherKey(k);
        }

        @Override
        public K pollFirst() {
            return map.pollFirstEntry().getKey();
        }

        @Override
        public K pollLast() {
            return map.pollLastEntry().getKey();
        }


        @Override
        public Iterator<K> iterator() {
            return map.keySet().iterator();
        }

        @Override
        public NavigableSet<K> descendingSet() {
            return new KeySet<K>((SortedArrayMap<K, V>) map.descendingMap());
        }

        @Override
        public Iterator<K> descendingIterator() {
            return descendingSet().iterator();
        }

        @Override
        public NavigableSet<K> subSet(K fromElement, boolean fromInclusive, K toElement, boolean toInclusive) {
            return new KeySet<>((SortedArrayMap<K, V>) map.subMap(fromElement, fromInclusive, toElement, toInclusive));
        }

        @Override
        public NavigableSet<K> headSet(K toElement, boolean inclusive) {
            return new KeySet<>((SortedArrayMap<K, V>) map.headMap(toElement, inclusive));
        }

        @Override
        public NavigableSet<K> tailSet(K fromElement, boolean inclusive) {
            return new KeySet<>((SortedArrayMap<K, V>) map.tailMap(fromElement, inclusive));
        }

        @Override
        public Comparator<? super K> comparator() {
            return map.keyComparator;
        }

        @Override
        public SortedSet<K> subSet(K fromElement, K toElement) {
            return new KeySet<>((SortedArrayMap<K, V>) map.subMap(fromElement, toElement));
        }

        @Override
        public SortedSet<K> headSet(K toElement) {
            return new KeySet<>((SortedArrayMap<K, V>) map.headMap(toElement));
        }

        @Override
        public SortedSet<K> tailSet(K fromElement) {
            return new KeySet<>((SortedArrayMap<K, V>) map.tailMap(fromElement));
        }

        @Override
        public K first() {
            return map.firstKey();
        }

        @Override
        public K last() {
            return map.lastKey();
        }

        @Override
        public int size() {
            return map.size();
        }
    }
    
    public class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public Entry(K key) {
            this.key = key;
        }

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public String toString() {
            return key + " = " + value;
        }
    }
}
