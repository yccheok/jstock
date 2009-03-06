/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.engine;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author yccheok
 */
public class TSTSearchEngineTest extends TestCase {
    
    public TSTSearchEngineTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of searchAll method, of class TSTSearchEngine.
     */
    public void testSearchAll() {
        TSTSearchEngine<Name> engine = new TSTSearchEngine<Name>();
        engine.put(new Name("Mr Cheok"));   // <--
        engine.put(new Name("miss Lim"));
        engine.put(new Name("mRM"));        // <--
        engine.put(new Name("mr H"));       // <--
        engine.put(new Name("ABCDEFG"));
        assertEquals(3, engine.searchAll("MR").size());
    }

    /**
     * Test of search method, of class TSTSearchEngine.
     */
    public void testSearch() {
        TSTSearchEngine<Name> engine = new TSTSearchEngine<Name>();
        engine.put(new Name("A"));
        engine.put(new Name("AB"));
        engine.put(new Name("ABC"));
        engine.put(new Name("ABCDEF"));
        engine.put(new Name("ABCDEFG"));
        Name name = engine.search("abcdef");
        assertEquals(new Name("ABCDEF"), name);
    }

    /**
     * Test of put method, of class TSTSearchEngine.
     */
    public void testPut() {
        TSTSearchEngine engine = new TSTSearchEngine();
        engine.put(new Name("efg"));
        engine.put(new Name("abc"));
        engine.put(new Name("abc"));
        List<Name> result = engine.searchAll("AB");
        assertEquals(2, result.size());
    }

    /**
     * Test of remove method, of class TSTSearchEngine.
     */
    public void testRemove() {
        System.out.println("remove");
        List<Name> names = new ArrayList<Name>();
        names.add(new Name("a"));
        names.add(new Name("aA"));  // <-- remove
        names.add(new Name("aAa"));
        names.add(new Name("AaaA"));
        TSTSearchEngine<Name> engine = new TSTSearchEngine<Name>(names);
        List<Name> result = engine.searchAll("a");        
        assertEquals(4, result.size());
        engine.remove(new Name("aA"));
        result = engine.searchAll("a");
        assertEquals(3, result.size());

        names = new ArrayList<Name>();
        names.add(new Name("ABC"));
        engine = new TSTSearchEngine<Name>(names);
        result = engine.searchAll("a");
        assertEquals(1, result.size());
        engine.remove(new Name("a"));
        result = engine.searchAll("a");
        assertEquals(1, result.size());
    }

    class Name {
        private String name;

        public Name(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;

            if(!(o instanceof Name))
                return false;

            Name n = (Name)o;
            if (this.name != null) {
                return this.name.equals(n.name);
            }

            return n.name == null;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }
    }
}
