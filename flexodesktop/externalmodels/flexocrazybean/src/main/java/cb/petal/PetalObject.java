/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package cb.petal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Super class for all petal objects which have a list of properties. Unfortunately, property names may occur multiply. This can happen,
 * e.g., if there are multiple notes attached to a class. Thus it is not implemented with a HashMap as one might think.
 * 
 * @version $Id: PetalObject.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class PetalObject implements PetalNode {
	static final long serialVersionUID = 7215267546012147332L;

	public static final ArrayList EMPTY = new ArrayList() {
		static final long serialVersionUID = -3029611157463610527L;

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean contains(java.lang.Object o) {
			return false;
		}

		@Override
		public java.lang.Object get(int i) {
			throw new IndexOutOfBoundsException("Index: " + i);
		}
	};

	private java.util.List<String> names = new ArrayList<String>();
	private java.util.List<PetalNode> values = new ArrayList<PetalNode>();
	private String name;
	protected java.util.List<String> params = EMPTY;
	private PetalNode parent;

	/**
	 * @param parent
	 *            node in the petal tree, either another PetalObject or PetalFile
	 * @param name
	 *            of the object, e.g., "ClassCategory"
	 * @param params
	 *            list of parameters, e.g., "Class" "Logical View::templates::Class"
	 */
	protected PetalObject(PetalNode parent, String name, java.util.List<String> params) {
		setParent(parent);
		setName(name);
		setParameterList(params);
	}

	/**
	 * @param parent
	 *            node in the petal tree, either another PetalObject or PetalFile
	 * @param name
	 *            of the object, e.g., "ClassCategory"
	 * @param params
	 *            list of parameters, e.g., "Class" "Logical View::templates::Class"
	 */
	protected PetalObject(PetalNode parent, String name, Collection<String> params) {
		this(parent, name, new ArrayList<String>(params));
	}

	/**
	 * @param name
	 *            of the object, e.g., "ClassCategory"
	 */
	protected PetalObject(String name) {
		setName(name);
	}

	/**
	 * @return shallow copy of object, do not forget to assign a new quid if you want to use it within the same model.
	 */
	@Override
	public java.lang.Object clone() {
		PetalObject obj = null;

		try {
			obj = (PetalObject) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}

		obj.names = new ArrayList<String>(names);
		obj.values = new ArrayList<PetalNode>(values);
		obj.params = params == EMPTY ? EMPTY : new ArrayList<String>(params);

		return obj;
	}

	/**
	 * @return true if the name and all properties are equal without regarding the order of the properties.
	 */
	public boolean equals(Object o) {
		if (o != null && o.getClass() == this.getClass()) {
			PetalObject obj = o;

			if (!this.name.equals(obj.name)) {
				return false;
			}

			if (this.values.size() != obj.values.size()) {
				return false;
			}

			TreeSet n1 = new TreeSet(this.names);
			TreeSet n2 = new TreeSet(obj.names);

			if (!n1.equals(n2)) {
				return false;
			}

			for (Iterator i = names.iterator(), j = values.iterator(); i.hasNext();) {
				String name = (String) i.next();
				PetalNode value1 = (PetalNode) j.next();
				PetalNode value2 = obj.getProperty(name);

				if (!value1.equals(value2)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (String name : names) {
			h += name.hashCode();
		}
		for (PetalNode o : values) {
			h += o.hashCode();
		}
		for (String param : params) {
			h += param.hashCode();
		}
		return h;
	}

	/**
	 * Perform any initial actions after all properties have been set up. Called by PetalParser when all properties have been defined or
	 * when a new object created by the user is added to the model.
	 */
	public void init() {
	}

	public void setParent(PetalNode p) {
		parent = p;
	}

	public PetalNode getParent() {
		return parent;
	}

	@Override
	public String getKind() {
		return "object";
	}

	public void setName(String n) {
		name = n.intern();
	}

	public String getName() {
		return name;
	}

	/**
	 * @return top-level root node of this model
	 */
	public final PetalFile getRoot() {
		PetalNode parent = this.parent;

		while (!(parent instanceof PetalFile)) {
			parent = ((PetalObject) parent).parent;
		}

		return (PetalFile) parent;
	}

	/**
	 * Override property at i, value's "parent" reference points to this object afterwards
	 */
	public final void setProperty(int i, String name, PetalNode value) {
		if (value == null) {
			throw new RuntimeException("Value for " + name + " must not be null");
		}

		names.set(i, name.intern()); // Use intern() to save lots of memory
		values.set(i, value);
	}

	/**
	 * Add a property (which may already exist, Petal files allow to define properties multiply).
	 * 
	 * @return index of property
	 */
	public final int addProperty(String name, PetalNode value) {
		if (value == null) {
			throw new RuntimeException("Value for " + name + " must not be null");
		}

		names.add(name.intern()); // Use intern() to save lots of memory
		values.add(value);

		return names.size() - 1;
	}

	/**
	 * This method is strict in that it does not use equals() to search the list of properties but ==, since values, in particular literals
	 * may occur more than once.
	 */
	public final int indexOf(PetalNode value) {
		int j = 0;
		for (Iterator i = values.iterator(); i.hasNext(); j++) {
			if (i.next() == value) {
				return j;
			}
		}

		return -1;
	}

	/**
	 * This method uses the strict indexOf method to find the value.
	 */
	public final String getPropertyName(PetalNode value) {
		int i = indexOf(value);

		if (i >= 0) {
			return names.get(i);
		} else {
			return null;
		}
	}

	/**
	 * @return number of properties
	 */
	public int getNoProperties() {
		return names.size();
	}

	/**
	 * @return number of properties
	 */
	@Override
	public int getChildCount() {
		return names.size();
	}

	/**
	 * Override property if exists already or add it if not.
	 * 
	 * @return index of property
	 */
	public final int defineProperty(String name, PetalNode value) {
		int index = names.indexOf(name);

		if (index >= 0) {
			setProperty(index, name, value);
			return index;
		} else {
			return addProperty(name, value);
		}
	}

	/**
	 * @return property at given index
	 */
	public final PetalNode getProperty(int i) {
		return values.get(i);
	}

	/**
	 * @return first occurrence of property name
	 */
	public final PetalNode getProperty(String name) {
		int index = names.indexOf(name);

		if (index >= 0) {
			return values.get(index);
		} else {
			return null;
		}
	}

	/**
	 * Override property if exists already or add it if not.
	 */
	public final void defineProperty(String name, String value) {
		ArrayList list = new ArrayList();
		StringTokenizer st = new StringTokenizer(value, "\r\n");

		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}

		StringLiteral lit = new StringLiteral(list);
		lit.setMulti(list.size() > 1);

		defineProperty(name, lit);
	}

	/**
	 * @return given property, or null if it doesn't exist.
	 */
	public String getPropertyAsString(String name) {
		StringLiteral s = (StringLiteral) getProperty(name);

		if (s == null) {
			// System.err.println("No such property: " + name + " for " + this);
			return null;
		}

		return s.getValue();
	}

	/**
	 * Strict variant: If the property does not exist, an exception is thrown
	 */
	static String getPropertyAsString(PetalObject obj, String prop) {
		StringLiteral s = (StringLiteral) obj.getProperty(prop);

		if (s == null) {
			throw new RuntimeException("No property named " + prop + " for " + obj);
		}

		return s.getValue();
	}

	/**
	 * @return given property, or Integer.MIN_VALUE if it doesn't exist.
	 */
	public int getPropertyAsInteger(String name) {
		IntegerLiteral s = (IntegerLiteral) getProperty(name);

		if (s == null) {
			// System.err.println("No such property: " + name + " for " + this);
			return Integer.MIN_VALUE;
		}

		return s.getValue();
	}

	/**
	 * Override property if exists already or add it if not.
	 */
	public void defineProperty(String name, int value) {
		defineProperty(name, new IntegerLiteral(value));
	}

	/**
	 * @return given property, or false if it doesn't exist.
	 */
	public boolean getPropertyAsBoolean(String name) {
		BooleanLiteral s = (BooleanLiteral) getProperty(name);

		if (s == null) {
			// System.err.println("No such property: " + name + " for " + this);
			return false;
		}

		return s.getValue();
	}

	/**
	 * Override property if exists already or add it if not.
	 */
	public void defineProperty(String name, boolean value) {
		defineProperty(name, new BooleanLiteral(value));
	}

	/**
	 * @return given property, or Double.MIN_VALUE if it doesn't exist.
	 */
	public double getPropertyAsFloat(String name) {
		FloatLiteral s = (FloatLiteral) getProperty(name);

		if (s == null) {
			// System.err.println("No such property: " + name + " for " + this);
			return Double.MIN_VALUE;
		}

		return s.getValue();
	}

	/**
	 * Override property if exists already or add it if not.
	 */
	public void defineProperty(String name, double value) {
		defineProperty(name, new FloatLiteral(value));
	}

	/**
	 * Remove property with given name
	 */
	public void removeProperty(String name) {
		removeProperty(names.indexOf(name));
	}

	/**
	 * Remove property at given index
	 */
	public void removeProperty(int index) {
		if (index >= 0) {
			names.remove(index);
			values.remove(index);
		}
	}

	/**
	 * Move property within list of properties, i.e. change order.
	 */
	public void moveProperty(int from, int to) {
		if (from == to) {
			return;
		}

		String name = names.get(from);
		PetalNode value = values.get(from);

		if (from < to) {
			to--;
		}

		removeProperty(from);
		names.add(to, name);
		values.add(to, value);
	}

	/**
	 * @return the longest name in names list, needed for indentation issues
	 */
	public String getLongestName() {
		String max = "";

		for (Iterator i = names.iterator(); i.hasNext();) {
			String s = (String) i.next();

			if (s.length() > max.length()) {
				max = s;
			}
		}

		return max;
	}

	/**
	 * @return all properties with key "name"
	 */
	public ArrayList getProperties(String name) {
		ArrayList list = new ArrayList();

		for (Iterator i = names.iterator(), j = values.iterator(); i.hasNext();) {
			String s = (String) i.next();
			PetalNode o = (PetalNode) j.next();

			if (s.equals(name)) {
				list.add(o);
			}
		}

		return list;
	}

	/**
	 * @return all property names
	 */
	public java.util.List<String> getNames() {
		return new ArrayList<String>(names);
	}

	/**
	 * @return all property values
	 */
	public java.util.List<PetalNode> getPropertyList() {
		return new ArrayList<PetalNode>(values);
	}

	/**
	 * @return array of properties, where obj[i][0] == name and obj[i][1] == property
	 */
	public java.lang.Object[][] getPropertyTuples() {
		java.lang.Object[][] props = new java.lang.Object[names.size()][];

		int k = 0;
		for (Iterator i = names.iterator(), j = values.iterator(); i.hasNext(); k++) {
			props[k] = new java.lang.Object[] { i.next(), j.next() };
		}

		return props;
	}

	public java.util.List<String> getParameterList() {
		return params;
	}

	public void setParameterList(java.util.List<String> params) {
		this.params = params;
	}

	/**
	 * Add object to some given list and create the list if necessary.
	 */
	protected void addToList(String prop_name, String list_name, PetalObject o) {
		List list = (List) getProperty(prop_name);

		if (list == null) {
			list = new List(list_name);
			defineProperty(prop_name, list);
		}

		list.add(o);
	}

	/**
	 * Remove object from some given list.
	 */
	protected void removeFromList(String prop_name, PetalObject o) {
		List list = (List) getProperty(prop_name);

		if (list != null) {
			list.remove(o);
		}
	}

	/**
	 * Get fully qualified name for an object that must implement the Named interface and is contained by further Named objects. Typical
	 * nesting of an object is (Design .. (ClassCategory ... (Class ...)))
	 * 
	 * @see Named
	 * @see Class
	 * @see UseCase
	 * @return String like "Logical View::University::Professor"
	 */
	public String getQualifiedName() {
		PetalNode n = this.getParent();
		String result = ((Named) this).getNameParameter();
		while (!(n instanceof Design) && n instanceof Named) {
			result = ((Named) n).getNameParameter() + "::" + result;
			n = ((PetalObject) n).getParent();
		}

		return result;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("(object " + name);

		for (Iterator i = params.iterator(); i.hasNext();) {
			buf.append(" \"" + i.next() + "\"");
		}

		buf.append("\n");

		for (Iterator i = names.iterator(), j = values.iterator(); i.hasNext();) {
			buf.append(i.next() + "\t" + j.next());

			if (i.hasNext()) {
				buf.append("\n");
			}
		}

		buf.append(")\n");

		return buf.toString();
	}

	@Override
	public abstract void accept(Visitor v);
}
