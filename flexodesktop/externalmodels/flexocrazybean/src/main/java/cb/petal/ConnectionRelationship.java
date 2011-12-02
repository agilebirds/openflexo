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
 * Represents Connection_Relationship object
 * 
 * @version $Id: ConnectionRelationship.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class ConnectionRelationship extends Relationship {
	public ConnectionRelationship(PetalNode parent, Collection params) {
		super(parent, "Connection_Relationship", params);
	}

	public ConnectionRelationship() {
		super("Connection_Relationship");
	}

	public boolean getSupplierIsDevice() {
		return getPropertyAsBoolean("supplier_is_device");
	}

	public void setSupplierIsDevice(boolean o) {
		defineProperty("supplier_is_device", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
