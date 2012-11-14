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
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import cb.util.PetalObjectFactory;

/**
 * Represents Class object, there are a lot of convenience methods here for adding super class(es), operations, attributes, etc.
 * 
 * @version $Id: Class.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Class extends Inheritable {
	static final long serialVersionUID = -1146331201133928529L;
	private Vector associations;
	private boolean isAssociationClass;

	public Vector getRelationsAsRole() {
		return associations;
	}

	public void addRelationship(Role rel) {
		if (associations == null) {
			associations = new Vector();
		}
		if (!associations.contains(rel)) {
			associations.add(rel);
		}
	}

	protected Class(PetalNode parent, String name, Collection params) {
		super(parent, name, params);
	}

	public Class(PetalNode parent, Collection params) {
		super(parent, "Class", params);
	}

	public Class() {
		super("Class");
	}

	/**
	 * Intialize this class by registering it by its quid and by its qualified name.
	 */
	@Override
	public void init() {
		super.init();
		getRoot().registerClass(this);
	}

	private boolean compareStereotype(String s) {
		String stereo = getStereotype();

		if (stereo != null) {
			return stereo.toLowerCase().equals(s);
		} else {
			return false;
		}
	}

	/**
	 * @return true if this class has the stereotype "interface" set.
	 */
	public boolean isInterface() {
		return compareStereotype("interface");
	}

	/**
	 * @return true if this class has the stereotype "actor" set.
	 */
	public boolean isActor() {
		return compareStereotype("actor");
	}

	public void isAssociationClass(boolean i) {
		isAssociationClass = i;
	}

	public boolean isAssociationClass() {
		return isAssociationClass;
	}

	/**
	 * @return "Class", "ClassUtility", "InstantiatedClass", etc..
	 */
	public String getClassType() {
		String name = getClass().getName();
		int index = name.lastIndexOf('.');

		if (index < 0) {
			throw new RuntimeException("What class is this: " + name);
		}

		return name.substring(index + 1);
	}

	/**
	 * @return list of super class objects
	 */
	public java.util.List getSuperclasses() {
		return getClassList(getSuperclassList());
	}

	/**
	 * Add super class of this class, i.e. adds InheritanceRelationship to "superclasses" list.
	 * 
	 * @return implicitly created relationship object
	 */
	public InheritanceRelationship addSuperClass(Class clazz) {
		return addSuperClassifier(clazz);
	}

	/**
	 * Add implemented interface to this class, i.e. adds RealizeRelationship to "realized_interfaces" list.
	 * 
	 * @return implicitly created relationship object
	 */
	public RealizeRelationship addImplementedInterface(Class inter) {
		RealizeRelationship rel = PetalObjectFactory.getInstance().createRealizeRelationship(this, inter);
		rel.init(); // Parent is already set

		addToList("realized_interfaces", "realize_rel_list", rel);
		return rel;
	}

	/**
	 * Add used to this class, i.e. adds UsesRelationship to "used_nodes" list.
	 * 
	 * @return implicitly created relationship object
	 */
	public UsesRelationship addUsedClass(Class inter) {
		UsesRelationship rel = PetalObjectFactory.getInstance().createUsesRelationship(this, inter);

		rel.init(); // Parent is already set

		addToList("used_nodes", "uses_relationship_list", rel);
		return rel;
	}

	/**
	 * @return list of used class objects (uses relationship)
	 */
	public java.util.List getUsedClasses() {
		return getClassList(getUsedClassesList());
	}

	/**
	 * @return list of implemented ("realized") Class objects (aka interfaces)
	 */
	public java.util.List getImplementedInterfaces() {
		return getClassList(getRealizedInterfacesList());
	}

	private java.util.List getClassList(List list) {
		if (list == null) {
			return Collections.EMPTY_LIST;
		}

		ArrayList result = new ArrayList();
		PetalFile root = getRoot();

		for (Iterator i = list.getElements().iterator(); i.hasNext();) {
			Relationship r = (Relationship) i.next();

			Class obj = root.getClassByQuidu(r);
			result.add(obj);
		}

		return result;
	}

	/**
	 * @return (first) super class of this class
	 */
	public Class getSuperclass() {
		java.util.List list = getSuperclasses();

		if (list == null || list.size() < 1) {
			return null;
		} else {
			return (Class) list.get(0);
		}
	}

	/**
	 * @return list of associations related to this class if any. this requires that the Association objects have been initialized with
	 *         "init()".
	 * @see Association#init()
	 */
	public java.util.List getAssociations() {
		return getRoot().getAssociations(this);
	}

	/**
	 * @return list of InheritanceRelationship objects
	 */
	@Override
	public List getSuperclassList() {
		return (List) getProperty("superclasses");
	}

	@Override
	public void setSuperclassList(List c) {
		defineProperty("superclasses", c);
	}

	/**
	 * @return list of UsesRelationship objects
	 */
	public List getUsedClassesList() {
		return (List) getProperty("used_nodes");
	}

	public void setUsedClassesList(List c) {
		defineProperty("used_nodes", c);
	}

	/**
	 * @return list of RealizeRelationship objects
	 */
	public List getRealizedInterfacesList() {
		return (List) getProperty("realized_interfaces");
	}

	public void setRealizedInterfacesList(List c) {
		defineProperty("realized_interfaces", c);
	}

	/**
	 * Add an operation to this class.
	 */
	public void addOperation(Operation o) {
		addToList("operations", "Operations", o);
	}

	/**
	 * Add an operation to this class.
	 * 
	 * @return implicitly created operation object
	 */
	public Operation addOperation(String name, String result, String qualifier, String[] param_types, String[] param_names) {
		List list = new List("Parameters");
		Operation op = PetalObjectFactory.getInstance().createOperation(name, result, list);
		op.setExportControl(qualifier);

		if (param_types != null) {
			for (int i = 0; i < param_types.length; i++) {
				String type = param_types[i];
				String n = param_names[i];

				Parameter p = new Parameter(op, java.util.Arrays.asList(new String[] { n }));
				p.setType(type);
				list.add(p);
			}
		}

		op.setParent(this);
		op.init();
		addOperation(op);

		return op;
	}

	public void removeOperation(Operation o) {
		removeFromList("operations", o);
	}

	/**
	 * Add a class attribute aka field to this class.
	 * 
	 * @return implicitly created class attribute
	 */
	public ClassAttribute addClassAttribute(String name, String type, String qualifier) {
		ClassAttribute attr = PetalObjectFactory.getInstance().createClassAttribute(name, type);
		attr.setParent(this);
		attr.setExportControl(qualifier);
		attr.init();
		addClassAttribute(attr);
		return attr;
	}

	/**
	 * Add a class attribute aka field to this class.
	 */
	public ClassAttribute addClassAttribute(String name, String type) {
		return addClassAttribute(name, type, "Private");
	}

	/**
	 * Add a class attribute aka field to this class.
	 */
	public void addClassAttribute(ClassAttribute o) {
		addToList("class_attributes", "class_attribute_list", o);
	}

	public void removeClassAttribute(ClassAttribute o) {
		removeFromList("class_attributes", o);
	}

	/**
	 * @return list of operations of this class
	 */
	public java.util.List getOperations() {
		List list = getOperationList();

		if (list != null) {
			return list.getElements();
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * @return list of class attributes (aka fields) of this class
	 */
	public java.util.List getClassAttributes() {
		List list = getClassAttributeList();

		if (list != null) {
			return list.getElements();
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * @return list of Operation objects
	 */
	public List getOperationList() {
		return (List) getProperty("operations");
	}

	public void setOperationList(List c) {
		defineProperty("operations", c);
	}

	/**
	 * @return list of ClassAttribute objects
	 */
	public List getClassAttributeList() {
		return (List) getProperty("class_attributes");
	}

	public void setClassAttributeList(List c) {
		defineProperty("class_attributes", c);
	}

	public String getLanguage() {
		return getPropertyAsString("language");
	}

	public void setLanguage(String c) {
		defineProperty("language", c);
	}

	public List getParameters() {
		return (List) getProperty("parameters");
	}

	public void setParameters(List o) {
		defineProperty("parameters", o);
	}

	public List getAttributes() {
		return (List) getProperty("attributes");
	}

	public void setAttributes(List o) {
		defineProperty("attributes", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
