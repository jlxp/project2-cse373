package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import misc.exceptions.NotYetImplementedException;

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
    
    private int getIndex(K key, int size) {
        if (key == null) {
            return 0;
        } else {
            return Math.abs(key.hashCode()) % size;
        }
    }

    @Override
    public V get(K key) {
        if(!this.containsKey(key)) {
            throw new NoSuchKeyException("there is no key");
        }
        int index = this.getIndex(key, this.chains.length);
        return this.chains[index].get(key);
    }

    
    @Override
    public void put(K key, V value) {
        if (this.size >= this.chains.length / 2) {
            IDictionary<K,V>[] temp = this.makeArrayOfChains(this.chains.length * 2);
            for (int i = 0; i < this.chains.length; i++) {
                if (this.chains[i] != null) {
                    for(KVPair<K,V> tempPair : this.chains[i]) {
                        K newKey = tempPair.getKey();
                        V newValue = tempPair.getValue();
                        int index = this.getIndex(newKey, temp.length);                
                        if(temp[index] == null) {
                            temp[index] = new ArrayDictionary<K,V>();
                        }
                        temp[index].put(newKey, newValue);
                    }
                }
            }
            this.chains = temp;
        } 
        int index = this.getIndex(key, this.chains.length);                
        if(this.chains[index] == null) {            
            this.chains[index] = new ArrayDictionary<K,V>();
            this.size++;
        } else if (!this.chains[index].containsKey(key)) {
            this.size++;
        }
        this.chains[index].put(key, value); 
    }

    @Override
    public V remove(K key) {
        if(!this.containsKey(key)) {
            throw new NoSuchKeyException("there is no key");
        }
        
        int index = this.getIndex(key, this.chains.length);                
        this.size--;
        return (V) this.chains[index].remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        int index = this.getIndex(key, this.chains.length);  
        return this.chains[index] != null && this.chains[index].containsKey(key);
    }

    @Override
    public int size() {
        return this.size;
    }

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
        private KVPair<K,V> next;
        private int vertical;
        
        
        
        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.index = 0;
            while (index < chains.length && chains[index] == null) {
                this.index++;
            }
            this.next = null;
            int vertical = 1;

        }

        @Override
        public boolean hasNext() {
            return this.index != this.chains.length;
        }

        @Override
        public KVPair<K, V> next() {
            if(!this.hasNext()) {
                throw new NoSuchElementException("Dictionary is empty");
            } 
            KVPair<K,V> current = this.next;
            
            while(this.index < this.chains.length && chains[index] == null) {
                index++;
            }
            
            if (this.index == this.chains.length) {
                this.next = null;
            } else {
                for (int i = 0; i < this.vertical; i++) {
                    this.next = this.chains[index].iterator().next();
                }
                if(this.chains[index].iterator().hasNext()) {
                    this.next = this.chains[index].iterator().next();
                    this.vertical++;
                } else {
                    this.vertical = 0;
                    this.index++;
                    while(this.index < this.chains.length && chains[index] == null) {
                        index++;
                    }
                    if (this.index == this.chains.length) {
                        this.next = null;
                    } else {
                        this.next = this.chains[index].iterator().next();
                    } 
                }
                
            }
            return next; 
            
            
//            this.current = this.next;
//            for (int i = 0; i < this.vertical; i++) {
//                this.next = this.chains[index].iterator().next();
//            }
//            if(this.chains[index].iterator().hasNext()) {
//                this.next = this.chains[index].iterator().next();
//                this.vertical++;
//            } else {
//                this.vertical = 0;
//                this.index++;
//                while (index < chains.length && chains[index] == null) {
//                    this.index++;
//                }
//                if (this.index == this.chains.length) {
//                    this.next = null;
//                } else {
//                    this.next = this.chains[index].iterator().next(); 
//                    this.vertical = 1;
//                }
//            }
//            return this.current;
        }
    }
}
