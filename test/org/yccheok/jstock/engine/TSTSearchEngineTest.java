/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import junit.framework.TestCase;

/**
 *
 * @author yccheok
 */
public class TSTSearchEngineTest extends TestCase {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("org.yccheok.jstock.engine.test");

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
        {
        TSTSearchEngine<Name> engine = new TSTSearchEngine<Name>();
        engine.put(new Name("Mr Cheok"));   // <--
        engine.put(new Name("miss Lim"));
        engine.put(new Name("mRM"));        // <--
        engine.put(new Name("mr H"));       // <--
        engine.put(new Name("ABCDEFG"));
        assertEquals(3, engine.searchAll("MR").size());
        }

        {
        TSTSearchEngine<Name> engine = new TSTSearchEngine<Name>();
        engine.put(new Name(bundle.getString("wo_men")));           // <--
        engine.put(new Name(bundle.getString("ta_men")));
        engine.put(new Name(bundle.getString("wo_men_de")));        // <--
        engine.put(new Name(bundle.getString("wo_men_de_jia")));    // <--
        engine.put(new Name(bundle.getString("ni_hao_ma")));
        assertEquals(3, engine.searchAll(bundle.getString("wo")).size());
        }
    }

    /**
     * Test of search method, of class TSTSearchEngine.
     */
    public void testSearch() {
        {
        TSTSearchEngine<Name> engine = new TSTSearchEngine<Name>();
        engine.put(new Name("A"));
        engine.put(new Name("AB"));
        engine.put(new Name("ABC"));
        engine.put(new Name("ABCDEF"));
        engine.put(new Name("ABCDEFG"));
        Name name = engine.search("abcdef");
        assertEquals(new Name("ABCDEF"), name);
        }
        {
        TSTSearchEngine<Name> engine = new TSTSearchEngine<Name>();
        engine.put(new Name(bundle.getString("wo")));
        engine.put(new Name(bundle.getString("wo_men")));
        engine.put(new Name(bundle.getString("wo_men_de")));
        engine.put(new Name(bundle.getString("wo_men_de_jia")));
        engine.put(new Name(bundle.getString("wo_men_de_jia_li")));
        Name name = engine.search(bundle.getString("wo_men_de_jia"));
        assertEquals(new Name(bundle.getString("wo_men_de_jia")), name);
        }
    }

    /**
     * Test of put method, of class TSTSearchEngine.
     */
    public void testPut() {
        {
        TSTSearchEngine engine = new TSTSearchEngine();
        engine.put(new Name("efg"));
        engine.put(new Name("abc"));
        // Duplication will be ignored.
        engine.put(new Name("abc"));
        List<Name> result = engine.searchAll("AB");
        assertEquals(1, result.size());
        }
        {
        TSTSearchEngine engine = new TSTSearchEngine();
        engine.put(new Name("efg"));
        engine.put(new Name("abc"));
        // Different case do not considered as duplication.
        engine.put(new Name("abC"));
        List<Name> result = engine.searchAll("AB");
        assertEquals(2, result.size());
        }
        {
        TSTSearchEngine engine = new TSTSearchEngine();
        engine.put(new Name(bundle.getString("ni_men")));
        engine.put(new Name(bundle.getString("wo_men")));
        // Duplication will be ignored.
        engine.put(new Name(bundle.getString("wo_men")));
        List<Name> result = engine.searchAll(bundle.getString("wo_men"));
        assertEquals(1, result.size());
        }
    }

    /**
     * Test of remove method, of class TSTSearchEngine.
     */
    public void testRemove() {
        // Test for single element.
        {
        List<Name> names = new ArrayList<Name>();
        names.add(new Name("MSFT"));  // <-- remove
        TSTSearchEngine<Name> engine = new TSTSearchEngine<Name>(names);
        List<Name> result = engine.searchAll("M");
        assertEquals(1, result.size());
        engine.remove(new Name("MSFT"));
        result = engine.searchAll("M");
        assertEquals(0, result.size());
        }

        {
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
        {
        List<Name> names = new ArrayList<Name>();
        names.add(new Name(bundle.getString("wo")));
        names.add(new Name(bundle.getString("wo_men")));  // <-- remove
        names.add(new Name(bundle.getString("wo_men_de")));
        names.add(new Name(bundle.getString("wo_men_de_jia")));
        TSTSearchEngine<Name> engine = new TSTSearchEngine<Name>(names);
        List<Name> result = engine.searchAll(bundle.getString("wo"));
        assertEquals(4, result.size());
        engine.remove(new Name(bundle.getString("wo_men")));
        result = engine.searchAll(bundle.getString("wo"));
        assertEquals(3, result.size());

        names = new ArrayList<Name>();
        names.add(new Name(bundle.getString("wo_men_de")));
        engine = new TSTSearchEngine<Name>(names);
        result = engine.searchAll(bundle.getString("wo"));
        assertEquals(1, result.size());
        engine.remove(new Name(bundle.getString("wo")));
        result = engine.searchAll(bundle.getString("wo"));
        assertEquals(1, result.size());
        }
    }

    class Name {
        private final String name;

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
