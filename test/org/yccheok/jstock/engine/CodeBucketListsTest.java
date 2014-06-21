/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yccheok.jstock.engine;

import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author yccheok
 */
public class CodeBucketListsTest extends TestCase {
    
    public CodeBucketListsTest(String testName) {
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
     * Test of get method, of class CodeBucketLists.
     */
    public void testGet() {
        System.out.println("get");
        
        CodeBucketLists instance = new CodeBucketLists(3);
        instance.add(Code.newInstance("0.kl"));
        instance.add(Code.newInstance("0"));
        instance.add(Code.newInstance("1.kl"));
        instance.add(Code.newInstance("1.l"));
        instance.add(Code.newInstance("2.kl"));
        instance.add(Code.newInstance("3.kl"));
        instance.add(Code.newInstance("0.nz"));
        instance.add(Code.newInstance("1.to"));
        instance.add(Code.newInstance("2"));
        instance.add(Code.newInstance("2.nz"));
        instance.add(Code.newInstance("3.nz"));
        instance.add(Code.newInstance("4.to"));
        
        List<Code> codes = instance.get(0);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0.kl"), codes.get(0));
        assertEquals(Code.newInstance("1.kl"), codes.get(1));
        assertEquals(Code.newInstance("2.kl"), codes.get(2));
        
        codes = instance.get(1);
        assertEquals(1, codes.size());
        assertEquals(Code.newInstance("3.kl"), codes.get(0));
        
        codes = instance.get(2);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0"), codes.get(0));
        assertEquals(Code.newInstance("1.l"), codes.get(1));
        assertEquals(Code.newInstance("2"), codes.get(2));
        
        codes = instance.get(3);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0.nz"), codes.get(0));
        assertEquals(Code.newInstance("1.to"), codes.get(1));
        assertEquals(Code.newInstance("2.nz"), codes.get(2));
        
        codes = instance.get(4);
        assertEquals(2, codes.size());
        assertEquals(Code.newInstance("3.nz"), codes.get(0));
        assertEquals(Code.newInstance("4.to"), codes.get(1));         
        
    }

    /**
     * Test of size method, of class CodeBucketLists.
     */
    public void testSize() {
        System.out.println("size");

        CodeBucketLists instance = new CodeBucketLists(3);
        instance.add(Code.newInstance("0.kl"));
        instance.add(Code.newInstance("0"));
        instance.add(Code.newInstance("1.kl"));
        instance.add(Code.newInstance("1.l"));
        instance.add(Code.newInstance("2.kl"));
        instance.add(Code.newInstance("3.kl"));
        instance.add(Code.newInstance("0.nz"));
        instance.add(Code.newInstance("2.to"));
        instance.add(Code.newInstance("2"));
        instance.add(Code.newInstance("3.nz"));
        instance.add(Code.newInstance("4.nz"));
        
        assertEquals(5, instance.size());
    }

    /**
     * Test of add method, of class CodeBucketLists.
     */
    public void testAdd() {
        System.out.println("add");
        CodeBucketLists instance = new CodeBucketLists(3);
        boolean status = instance.add(Code.newInstance("0.kl"));
        
        assertEquals(true, status);
        
        status = instance.add(Code.newInstance("0.kl"));
        
        assertEquals(false, status);
    }

    /**
     * Test of remove method, of class CodeBucketLists.
     */
    public void testRemove() {
        System.out.println("remove");
        
        CodeBucketLists instance = new CodeBucketLists(3);
        instance.add(Code.newInstance("0.kl"));
        instance.add(Code.newInstance("0"));
        instance.add(Code.newInstance("1.kl"));
        instance.add(Code.newInstance("1.l"));
        instance.add(Code.newInstance("2.kl"));
        instance.add(Code.newInstance("3.kl"));
        instance.add(Code.newInstance("0.nz"));
        instance.add(Code.newInstance("1.to"));
        instance.add(Code.newInstance("2"));
        instance.add(Code.newInstance("2.nz"));
        instance.add(Code.newInstance("3.nz"));
        instance.add(Code.newInstance("4.to"));
        
        boolean status = instance.remove(Code.newInstance("no_such_code"));
        assertEquals(false, status);
        
        assertEquals(5, instance.size());
        
        status = instance.remove(Code.newInstance("1.kl"));
        assertEquals(true, status);

        assertEquals(4, instance.size());
        
        status = instance.remove(Code.newInstance("1.kl"));
        assertEquals(false, status);
        
        List<Code> codes = instance.get(0);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0.kl"), codes.get(0));
        assertEquals(Code.newInstance("2.kl"), codes.get(1));
        assertEquals(Code.newInstance("3.kl"), codes.get(2));
                
        codes = instance.get(1);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0"), codes.get(0));
        assertEquals(Code.newInstance("1.l"), codes.get(1));
        assertEquals(Code.newInstance("2"), codes.get(2));
        
        codes = instance.get(2);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0.nz"), codes.get(0));
        assertEquals(Code.newInstance("1.to"), codes.get(1));
        assertEquals(Code.newInstance("2.nz"), codes.get(2));
        
        codes = instance.get(3);
        assertEquals(2, codes.size());
        assertEquals(Code.newInstance("3.nz"), codes.get(0));
        assertEquals(Code.newInstance("4.to"), codes.get(1)); 
        
        status = instance.remove(Code.newInstance("0"));
        assertEquals(true, status);
        assertEquals(4, instance.size());
        
        codes = instance.get(0);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0.kl"), codes.get(0));
        assertEquals(Code.newInstance("2.kl"), codes.get(1));
        assertEquals(Code.newInstance("3.kl"), codes.get(2));
                
        codes = instance.get(1);
        assertEquals(2, codes.size());
        assertEquals(Code.newInstance("1.l"), codes.get(0));
        assertEquals(Code.newInstance("2"), codes.get(1));
        
        codes = instance.get(2);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0.nz"), codes.get(0));
        assertEquals(Code.newInstance("1.to"), codes.get(1));
        assertEquals(Code.newInstance("2.nz"), codes.get(2));
        
        codes = instance.get(3);
        assertEquals(2, codes.size());
        assertEquals(Code.newInstance("3.nz"), codes.get(0));
        assertEquals(Code.newInstance("4.to"), codes.get(1));   
        
        status = instance.remove(Code.newInstance("2"));
        assertEquals(true, status);
        assertEquals(4, instance.size());
        
        codes = instance.get(0);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0.kl"), codes.get(0));
        assertEquals(Code.newInstance("2.kl"), codes.get(1));
        assertEquals(Code.newInstance("3.kl"), codes.get(2));
                
        codes = instance.get(1);
        assertEquals(1, codes.size());
        assertEquals(Code.newInstance("1.l"), codes.get(0));
        
        codes = instance.get(2);
        assertEquals(3, codes.size());
        assertEquals(Code.newInstance("0.nz"), codes.get(0));
        assertEquals(Code.newInstance("1.to"), codes.get(1));
        assertEquals(Code.newInstance("2.nz"), codes.get(2));
        
        codes = instance.get(3);
        assertEquals(2, codes.size());
        assertEquals(Code.newInstance("3.nz"), codes.get(0));
        assertEquals(Code.newInstance("4.to"), codes.get(1));          
    }
    
}
