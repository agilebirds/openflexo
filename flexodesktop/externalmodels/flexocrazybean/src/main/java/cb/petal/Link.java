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
 * Represents Link object
 * 
 * @version $Id: Link.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Link extends QuidObject {
	public Link(PetalNode parent, Collection params) {
		super(parent, "Link", params);
	}

	public Link() {
		super("Link");
	}

	public String getSupplier() {
		return getPropertyAsString("supplier");
	}

	public void setSupplier(String o) {
		defineProperty("supplier", o);
	}

	public String getSupplierContainment() {
		return getPropertyAsString("supplier_containment");
	}

	public void setSupplierContainment(String o) {
		defineProperty("supplier_containment", o);
	}

	public String getClientContainment() {
		return getPropertyAsString("client_containment");
	}

	public void setClientContainment(String o) {
		defineProperty("client_containment", o);
	}

	public List getMessages() {
		return (List) getProperty("messages");
	}

	public void setMessages(List o) {
		defineProperty("messages", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
