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
 * Super class for all relationships between classes.
 * 
 * @version $Id: Relationship.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class Relationship extends QuiduObject implements HasSupplier, StereoTyped, Labeled, Documented {
	public Relationship(PetalNode parent, String name, Collection params) {
		super(parent, name, params);
	}

	public Relationship(String name) {
		super(name);
	}

	@Override
	public String getSupplier() {
		return getPropertyAsString("supplier");
	}

	@Override
	public void setSupplier(String o) {
		defineProperty("supplier", o);
	}

	@Override
	public String getStereotype() {
		return getPropertyAsString("stereotype");
	}

	@Override
	public void setStereotype(String c) {
		defineProperty("stereotype", c);
	}

	@Override
	public void setLabel(String o) {
		defineProperty("label", o);
	}

	@Override
	public String getLabel() {
		return getPropertyAsString("label");
	}

	@Override
	public void setDocumentation(String o) {
		defineProperty("documentation", o);
	}

	@Override
	public String getDocumentation() {
		return getPropertyAsString("documentation");
	}
}
