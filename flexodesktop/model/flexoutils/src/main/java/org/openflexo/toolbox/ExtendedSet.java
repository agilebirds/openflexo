package org.openflexo.toolbox;

import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

public class ExtendedSet<E> extends AbstractSet<E> {

	private transient HashMap<E, E> map;

	public ExtendedSet() {
		map = new HashMap<E, E>();
	}

	/**
	 * Returns an iterator over the elements in this set. The elements are returned in no particular order.
	 * 
	 * @return an Iterator over the elements in this set
	 * @see ConcurrentModificationException
	 */
	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	/**
	 * Returns the number of elements in this set (its cardinality).
	 * 
	 * @return the number of elements in this set (its cardinality)
	 */
	@Override
	public int size() {
		return map.size();
	}

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
	 * 
	 * @return <tt>true</tt> if this set contains no elements
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this set contains the specified element. More formally, returns <tt>true</tt> if and only if this set
	 * contains an element <tt>e</tt> such that <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 * 
	 * @param o
	 *            element whose presence in this set is to be tested
	 * @return <tt>true</tt> if this set contains the specified element
	 */
	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * Adds the specified element to this set if it is not already present. More formally, adds the specified element <tt>e</tt> to this set
	 * if this set contains no element <tt>e2</tt> such that <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>. If this set
	 * already contains the element, the call leaves the set unchanged and returns <tt>false</tt>.
	 * 
	 * @param e
	 *            element to be added to this set
	 * @return <tt>true</tt> if this set did not already contain the specified element
	 */
	@Override
	public boolean add(E e) {
		return map.put(e, e) == null;
	}

	/**
	 * Returns the object equals to the specified element which is present in this Set.
	 * 
	 * @param e
	 * @return
	 */
	public E get(E e) {
		return map.get(e);
	}

	/**
	 * Removes the specified element from this set if it is present. More formally, removes an element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if this set contains such an element. Returns <tt>true</tt> if this
	 * set contained the element (or equivalently, if this set changed as a result of the call). (This set will not contain the element once
	 * the call returns.)
	 * 
	 * @param o
	 *            object to be removed from this set, if present
	 * @return <tt>true</tt> if the set contained the specified element
	 */
	@Override
	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	/**
	 * Removes all of the elements from this set. The set will be empty after this call returns.
	 */
	@Override
	public void clear() {
		map.clear();
	}

	/**
	 * Returns a shallow copy of this <tt>ExtendedSet</tt> instance: the elements themselves are not cloned.
	 * 
	 * @return a shallow copy of this set
	 */
	@Override
	public Object clone() {
		try {
			ExtendedSet<E> newSet = (ExtendedSet<E>) super.clone();
			newSet.map = (HashMap<E, E>) map.clone();
			return newSet;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

}
