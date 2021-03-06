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
 * Represents Instantiated_Class object
 * 
 * @version $Id: InstantiatedClass.java,v 1.2 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class InstantiatedClass extends DerivedClass {
	public InstantiatedClass(PetalNode parent, Collection params) {
		super(parent, "Instantiated_Class", params);
	}

	public InstantiatedClass() {
		super("Instantiated_Class");
	}

	public void setActualParameter(String o) {
		params.set(0, o);
	}

	public String getActualParameter() {
		return params.get(0);
	}

	public String getModule() {
		return getPropertyAsString("module");
	}

	public void setModule(String o) {
		defineProperty("module", o);
	}

	public InstantiationRelationship getInstantiationRelationship() {
		return (InstantiationRelationship) getProperty("instantiation_relationship");
	}

	public void setInstantiationRelationship(InstantiationRelationship o) {
		defineProperty("instantiation_relationship", o);
	}

	public String getNonclassname() {
		return getPropertyAsString("nonclassname");
	}

	public void setNonclassname(String o) {
		defineProperty("nonclassname", o);
	}
}
