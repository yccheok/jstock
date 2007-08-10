//package com.wallyflint.one_point_one.collections;
// The package statement has been commented out so that every class can reside in the same package. This allows a java 1.1 demo applet to
// run without throwing security exceptions.

package org.yccheok.jstock.engine;

import java.util.NoSuchElementException;

public class DoublyLinkedList {
	
	private DLLNode head, last;
	private int size = 0;
	
	public void addFirst(Object data) {
		DLLNode newNode = new DLLNode();
		newNode.data = data;
		if(size==0) {
			head = newNode;
			last = head;
		} else {
			newNode.nextNode = head;
			head.previousNode = newNode;
			head = newNode;
		}
		size++;
	}
	
	public void addLast(Object data) {
		DLLNode newNode = new DLLNode();
		newNode.data = data;
		if(size==0) {
			head = newNode;
		} else {
			last.nextNode = newNode;
			newNode.previousNode = last;
		}
		last = newNode;
		size++;
	}
	
	public void removeFirst() {
		if(size <= 1) {
			head = null;
			last = null;
		} else {
			DLLNode oldHead = head;
			head = oldHead.nextNode;
			oldHead.nextNode = null;
			head.previousNode = null;
		}
		size--;
	}
	
	public void removeLast() {
		if(size <= 1) {
			head = null;
			last = null;
		} else {
			last = last.previousNode;
			last.nextNode.previousNode = null;
			last.nextNode = null;
		}
		size--;
	}
	
	public int size() {
		return size;
	}
	
	public void clear() {
		DLLNode currentNode = last;
		DLLNode tempNode;
		while(currentNode != null) {
			tempNode = currentNode.previousNode;
			currentNode.nextNode = null;
			currentNode.previousNode = null;
			currentNode.data = null;
			currentNode = tempNode;
		}
		last = null;
		head = null;
		size = 0;
	}
	
	protected class DLLNode {
		protected DLLNode nextNode, previousNode;
		protected Object data;
	}
	
	public DLLIterator iterator() {
		return new DLLIterator();
	}
	
	public class DLLIterator {
		
		private DLLNode currentPreviousNode = null;
		private DLLNode currentNextNode = head;
		
		public boolean hasNext() {
			if(currentNextNode == null) {
				return false;
			} else {
				return(currentNextNode != null);
			}
		}
		
		public boolean hasPrevious() {
			if(currentPreviousNode == null) {
				return false;
			} else {
				return (currentPreviousNode != null);
			}
		}
		
		public Object next() {
			if(currentNextNode == null) throw new NoSuchElementException("Attempt to retrieve next value from " +
				"DoublyLinkedList after all values have already been retrieved. Verify hasNext method returns true " +
				"before calling next method.");
			Object data = currentNextNode.data;
			DLLNode tempNode = currentNextNode;
			currentNextNode = currentNextNode.nextNode;
			currentPreviousNode = tempNode;
			return data;
		}
		
		public Object previous() {
			if(currentPreviousNode == null) throw new NoSuchElementException("Attempt to retrieve previous value from " +
				"head node of DoublyLinkedList. Verify hasPrevious method returns true " +
				"before calling previous method.");
			Object data = currentPreviousNode.data;
			DLLNode tempNode = currentPreviousNode;
			currentPreviousNode = currentPreviousNode.previousNode;
			currentNextNode = tempNode;
			return data;
		}
		
		public void resetToBeginning() {
			currentNextNode = head;
			currentPreviousNode = null;
		}

		public void resetToEnd() {
			currentNextNode = null;
			currentPreviousNode = last;
		}
	}
	
	// ******************************************************************************************************************************
	// *****************************************      from here on down is test code      *******************************************
	// ******************************************************************************************************************************
	/*
	public static class Test {
		public static void main(String[] args) {
			DoublyLinkedList testListOne = new DoublyLinkedList();
			String testObjectOne = "test object one";
			testListOne.addFirst(testObjectOne);
			System.out.println("Size after adding one object by calling addFirst: " + testListOne.size);
			testListOne.removeFirst();
			System.out.println("Then called removeFirst and size is: " + testListOne.size);
			
			testListOne.addLast(testObjectOne);
			System.out.println("Size after adding one object by calling addLast: " + testListOne.size);
			testListOne.removeLast();
			System.out.println("Then called removeLast and size is: " + testListOne.size);
			testListOne.clear();
			testListOne.clear();
			
			testListOne.addFirst(testObjectOne);
			DLLIterator iterator = testListOne.iterator();
			System.out.println("hasNext method of iterator after adding one object by calling addFirst: " + iterator.hasNext());
			System.out.println("hasPrevious method of iterator after adding one object by calling addFirst: " + iterator.hasPrevious());
			String resultString = (String)iterator.next();
			System.out.println("result string pulled out by calling next: " + resultString);
			System.out.println("hasNext method of iterator after calling next: " + iterator.hasNext());
			System.out.println("hasPrevious method of iterator after calling next: " + iterator.hasPrevious());
			resultString = (String)iterator.previous();
			System.out.println("result string pulled out by calling previous: " + resultString);
			testListOne.clear();
			
			System.out.println("");
			
			String testObjectTwo = "test object two";
			String testObjectThree = "test object three";

			testListOne.addFirst(testObjectTwo);
			testListOne.addFirst(testObjectOne);
			iterator.resetToBeginning();
			while(iterator.hasNext()) System.out.println((String)iterator.next());
			testListOne.clear();
			
			System.out.println("");

			testListOne.addLast(testObjectOne);
			testListOne.addLast(testObjectTwo);
			iterator.resetToBeginning();
			while(iterator.hasNext()) System.out.println((String)iterator.next());
			testListOne.clear();
			
			System.out.println("");

			testListOne.addFirst(testObjectThree);
			testListOne.addFirst(testObjectTwo);
			testListOne.addFirst(testObjectOne);
			iterator.resetToBeginning();
			while(iterator.hasNext()) System.out.println((String)iterator.next());
			testListOne.clear();
			
			System.out.println("");

			testListOne.addLast(testObjectOne);
			testListOne.addLast(testObjectTwo);
			testListOne.addLast(testObjectThree);
			iterator.resetToBeginning();
			while(iterator.hasNext()) System.out.println((String)iterator.next());
			testListOne.clear();
			
			System.out.println("");
			
			testListOne.addFirst(testObjectTwo);
			testListOne.addFirst(testObjectOne);
			iterator.resetToEnd();
			while(iterator.hasPrevious()) System.out.println((String)iterator.previous());
			testListOne.clear();
			
			System.out.println("");

			testListOne.addFirst(testObjectThree);
			testListOne.addFirst(testObjectTwo);
			testListOne.addFirst(testObjectOne);
			System.out.println("size after adding three objects: " + testListOne.size());
			iterator.resetToEnd();
			while(iterator.hasPrevious()) System.out.println((String)iterator.previous());
			testListOne.clear();
			
			System.out.println("");
		}
	}
        */
}
