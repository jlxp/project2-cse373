package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.ISet;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See ISet for more details on what each method is supposed to do.
 */
public class ChainedHashSet<T> implements ISet<T> {
    // This should be the only field you need
    private IDictionary<T, Boolean> map;

    public ChainedHashSet() {
        // No need to change this method
        this.map = new ChainedHashDictionary<>();
    }

    /*
     * add the given item to the set
     * if already existed, nothing happen
     * @see datastructures.interfaces.ISet#add(java.lang.Object)
     */
    @Override
    public void add(T item) {
        if (!this.contains(item)) {
            this.map.put(item, true);
        }
    }

    /*
     * remove the given item from the set
     * @throw NoSuchElementException if there is no given item at the set
     * @see datastructures.interfaces.ISet#remove(java.lang.Object)
     */
    @Override
    public void remove(T item) {
        if (!this.map.containsKey(item)) {
            throw new NoSuchElementException("item is not found");
        }
        this.map.remove(item);
    }

    /*
     * check if set has given item
     * return true if set has given item, false otherwise
     * @see datastructures.interfaces.ISet#contains(java.lang.Object)
     */
    @Override
    public boolean contains(T item) {
        return this.map.containsKey(item);
    }

    /*
     * return the number of element oof the set
     * @see datastructures.interfaces.ISet#size()
     */
    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Iterator<T> iterator() {
        return new SetIterator<>(this.map.iterator());
    }

    private static class SetIterator<T> implements Iterator<T> {
        // This should be the only field you need
        private Iterator<KVPair<T, Boolean>> iter;

        public SetIterator(Iterator<KVPair<T, Boolean>> iter) {
            // No need to change this method.
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public T next() {
            return (T) this.iter.next().getKey();
        }
    }
}
