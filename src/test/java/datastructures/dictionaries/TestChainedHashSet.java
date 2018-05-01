package datastructures.dictionaries;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Assert;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.ISet;
import org.junit.Test;


public class TestChainedHashSet {

    private static final long SECOND = 1000;

    protected <T> ISet<T> newSet() {
        return new ChainedHashSet<>();
    }
    
    @Test(timeout=SECOND)
    public void basicSetAddTest() {
        ISet<String> set = this.newSet();
        
        set.add("hello");
        
        assertEquals(set.size(), 1);
        
        set.add("World");
        
        assertEquals(set.size(), 2);
        
        set.add("hello");
        
        assertEquals(set.size(), 2); 
        
        assertTrue(set.contains("hello"));
        assertTrue(set.contains("World"));
        assertFalse(set.contains(null));
    }
    
    @Test(timeout=SECOND)
    public void basicSetRemoveTest() {
        ISet<Integer> set = this.newSet();
        
        set.add(1);
        set.add(2);
        set.add(3);
        
        set.remove(3);
        assertEquals(set.size(), 2); 
        set.remove(2);
        assertEquals(set.size(), 1); 
        
        set.remove(1);
        assertEquals(set.size(), 0);
        
        assertFalse(set.contains(1));
        assertFalse(set.contains(2));
        assertFalse(set.contains(3));
        assertFalse(set.contains(null));
    }
    
    @Test(timeout=SECOND)
    public void basicIterTest() {
        ISet<Integer> set = this.newSet();
        
        set.add(1);
        set.add(2);
        set.add(3);
        
        Iterator<Integer> iter = set.iterator();
        
        for (int i = 0; i < set.size(); i++) {
            assertTrue(iter.hasNext());
            assertEquals(i+1, (int) iter.next());
        }
        
        assertEquals(3, set.size());
    }
    
    @Test(timeout=SECOND)
    public void nullTest() {
        ISet<Integer> set = this.newSet();
        
        set.add(null);
        
        assertTrue(set.contains(null));
    }
    
    @Test(timeout=SECOND)
    public void iterEndCorrectTest() {
        ISet<Integer> set = this.newSet();
        
        set.add(1);
        set.add(2);
        set.add(3);
        
        Iterator<Integer> iter = set.iterator();
        
        for (int i = 0; i < set.size(); i++) {
            for (int j = 0; j < 1000; j++) {
                assertTrue(iter.hasNext());
            }
            iter.next();
        }

        for (int j = 0; j < 1000; j++) {
            assertFalse(iter.hasNext());
        }

        try {
            iter.next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException ex) {
            // This is ok; deliberately empty
        }
    }
}
