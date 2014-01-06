package org.openflexo.toolbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.IteratorChain;

public class ChainedCollection<T> implements Collection<T> {

	private List<Collection<? extends T>> collections;
	private List<T> items;

	public ChainedCollection() {
		collections = new ArrayList<Collection<? extends T>>();
		items = new ArrayList<T>();
	}

	public ChainedCollection(T... items) {
		this();
		for (T item : items) {
			this.items.add(item);
		}
	}

	public ChainedCollection(Collection<? extends T>... collections) {
		this();
		for (Collection<? extends T> collection : collections) {
			this.collections.add(collection);
		}
	}

	public void add(Collection<? extends T> itemCollection) {
		this.collections.add(itemCollection);
	}

	@Override
	public boolean add(T item) {
		return this.items.add(item);
	}

	@Override
	public Iterator<T> iterator() {
		List<Iterator<? extends T>> allIterators = new ArrayList<Iterator<? extends T>>();
		for (Collection<? extends T> collection : collections) {
			if (collection.size() > 0) {
				allIterators.add(collection.iterator());
			}
		}
		if (items.size() > 0) {
			allIterators.add(items.iterator());
		}
		return new IteratorChain(allIterators);
	}

	@Override
	public int size() {
		int returned = 0;
		for (Collection<? extends T> collection : collections) {
			returned += collection.size();
		}
		returned += items.size();
		return returned;
	}

	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	@Override
	public boolean contains(Object o) {
		for (Collection<? extends T> collection : collections) {
			if (collection.contains(o)) {
				return true;
			}
		}
		if (items.contains(o)) {
			return true;
		}
		return false;
	}

	@Override
	public Object[] toArray() {
		Object[] returned = new Object[size()];
		int i = 0;
		for (Collection<? extends T> collection : collections) {
			for (T item : collection) {
				returned[i++] = item;
			}
		}
		for (T item : items) {
			returned[i++] = item;
		}
		return returned;
	}

	@Override
	public <T2> T2[] toArray(T2[] a) {
		int i = 0;
		for (Collection<? extends T> collection : collections) {
			for (T item : collection) {
				a[i++] = (T2) item;
			}
		}
		for (T item : items) {
			a[i++] = (T2) item;
		}
		return a;
	}

	@Override
	public boolean remove(Object o) {
		for (Collection<? extends T> collection : collections) {
			if (collection.remove(o)) {
				return true;
			}
		}
		if (items.remove(o)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean returned = false;
		for (T o : c) {
			if (add(o)) {
				returned = true;
			}
		}
		return returned;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean returned = false;
		for (Object o : c) {
			if (remove(o)) {
				returned = true;
			}
		}
		return returned;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<T> e = iterator();
		while (e.hasNext()) {
			if (!c.contains(e.next())) {
				e.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public void clear() {
		collections.clear();
		items.clear();
	}

	// TODO: make JUnit tests
	public static void main(String[] args) {
		List<String> l1 = new ArrayList<String>();
		l1.add("String1");
		l1.add("String2");
		l1.add("String3");
		List<String> l2 = new ArrayList<String>();
		l2.add("String4");
		l2.add("String5");
		System.out.println("ChainedCollection1=");
		ChainedCollection<String> cc1 = new ChainedCollection<String>(l1, l2);
		for (String s : cc1) {
			System.out.println("> " + s);
		}
		ChainedCollection<String> cc2 = new ChainedCollection<String>(cc1);
		cc2.add("String6");
		cc2.add(l1);
		System.out.println("ChainedCollection2=");
		for (String s : cc2) {
			System.out.println("> " + s);
		}
	}

}
