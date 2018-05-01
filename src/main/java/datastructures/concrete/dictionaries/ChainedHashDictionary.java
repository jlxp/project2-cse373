package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private static final int INIT_SIZE = 10;
    private int size;
    
    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashDictionary() {
        this.chains = this.makeArrayOfChains(INIT_SIZE);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }
    
    /* 
     * return the index using hashTable logic in class
     */
    private int getIndex(K key, int tblSize) {
        if (key == null) {
            return 0;
        } else {
            return Math.abs(key.hashCode()) % tblSize;
        }
    }

    /*
     * return the given key's paired value
     * @see datastructures.interfaces.IDictionary#get(java.lang.Object)
     * @throw NoSuchKeyException if there is no given key in the dictionary
     */
    @Override
    public V get(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException("there is no key");
        }
        int index = this.getIndex(key, this.chains.length);
        return this.chains[index].get(key);
    }

    /*
     * put key-value pair into the dictionary
     * if the size of dictionary becomes large enough resize it and put the pair in
     * @see datastructures.interfaces.IDictionary#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(K key, V value) {
        if (this.size >= this.chains.length / 2) {
            IDictionary<K, V>[] temp = this.makeArrayOfChains(this.chains.length * 2);
            for (int i = 0; i < this.chains.length; i++) {
                if (this.chains[i] != null) {
                    for (KVPair<K, V> tempPair : this.chains[i]) {
                        K newKey = tempPair.getKey();
                        V newValue = tempPair.getValue();
                        int index = this.getIndex(newKey, temp.length);                
                        if (temp[index] == null) {
                            temp[index] = new ArrayDictionary<K, V>();
                        }
                        temp[index].put(newKey, newValue);
                    }
                }
            }
            this.chains = temp;
        } 
        int index = this.getIndex(key, this.chains.length);                
        if (this.chains[index] == null) {
            this.chains[index] = new ArrayDictionary<K, V>();
            this.size++;
        } else if (!this.chains[index].containsKey(key)) {
            this.size++;
        }
        this.chains[index].put(key, value); 
    }

    /*
     * remove the pair that matches given key
     * return the value of the pair
     * @see datastructures.interfaces.IDictionary#remove(java.lang.Object)
     * @throw NoSuchKeyException if the dictionary has no given key
     */
    @Override
    public V remove(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException("there is no key");
        }
        
        int index = this.getIndex(key, this.chains.length);                
        this.size--;
        return (V) this.chains[index].remove(key);
    }

    /*
     * return true if the dictionary has the given key; false otherwise
     * @see datastructures.interfaces.IDictionary#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(K key) {
        int index = this.getIndex(key, this.chains.length);  
        return this.chains[index] != null && this.chains[index].containsKey(key);
    }

    /*
     * return the number of the pairs in the dictionary
     * @see datastructures.interfaces.IDictionary#size()
     */
    @Override
    public int size() {
        return this.size;
    }

    /*
     * return iterator of the dictionary
     * @see datastructures.interfaces.IDictionary#iterator()
     */
    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int index;
        private KVPair<K, V> next;
        private Iterator<KVPair<K, V>> currIter;
        
        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.index = 0;
            while (this.index < chains.length && chains[this.index] == null) {
                this.index++;
            }
            if (this.index != this.chains.length) {
                this.currIter = chains[this.index].iterator();
                this.next = this.currIter.next();
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("Dictionary is empty");
            }
            
            KVPair<K, V> current = this.next;
            
            if (this.currIter.hasNext()) {
                this.next = this.currIter.next();
            } else {
                this.index++;
                while (this.index < this.chains.length && chains[this.index] == null) {
                    this.index++;
                }
                if (this.index != this.chains.length) {
                    this.currIter = this.chains[this.index].iterator();
                    this.next = this.currIter.next();
                } else {
                    this.currIter = null;
                    this.next = null;
                }
            }
            return current;
        }
    }
}
