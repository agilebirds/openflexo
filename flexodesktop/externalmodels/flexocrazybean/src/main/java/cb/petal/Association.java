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

import java.util.Collection;

/**
 * Represents Association object
 * 
 * @version $Id: Association.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Association extends AccessObject {
	static final long serialVersionUID = 2685090660257573719L;

	public Association(PetalNode parent, Collection params) {
		super(parent, "Association", params);
	}

	public Association() {
		super("Association");
	}

	/**
	 * @return Class or UseCase
	 */
	public QuidObject getFirstClient() {
		return getFirstRole().getReferencedObject();
	}

	/**
	 * @return Class or UseCase
	 */
	public QuidObject getSecondClient() {
		return getSecondRole().getReferencedObject();
	}

	/**
	 * An association contains exactly two roles. Get the first one.
	 */
	public Role getFirstRole() {
		return (Role) getRoles().getElements().get(0);
	}

	/**
	 * An association contains exactly two roles. Get the second one.
	 */
	public Role getSecondRole() {
		return (Role) getRoles().getElements().get(1);
	}

	/**
	 * Register this association internally, i.e. associate it with the attached classes. So the classes can look up the associations
	 * related to them, too.
	 * <p>
	 * If this association has an association class, set its isAssociationClass() flag.
	 */
	@Override
	public void init() {
		super.init();
		getRoot().registerAssociation(this);

		Class clazz = getAssociationClass();
		if (clazz != null) {
			clazz.isAssociationClass(true);
		}
	}

	public Class getAssociationClass() {
		String s = getPropertyAsString("AssociationClass");

		if (s != null) {
			Class clazz = getRoot().getClassByQualifiedName(s);

			if (clazz == null) {
				System.err.println("Warning: Could not find association class " + s + " (forward declaration?)");
			}

			return clazz;
		} else {
			return null;
		}
	}

	public void setAssociationClass(Class o) {
		setAssociationClass(o.getQualifiedName());
	}

	/**
	 * Set association class via its fully qualified name like "Logical View::University::Period".
	 */
	public void setAssociationClass(String qual_name) {
		defineProperty("AssociationClass", qual_name);
	}

	public List getRoles() {
		return (List) getProperty("roles");
	}

	public void setRoles(List o) {
		defineProperty("roles", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
