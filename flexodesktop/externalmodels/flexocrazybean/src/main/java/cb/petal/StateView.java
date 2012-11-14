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
 * Represents StateView object
 * 
 * @version $Id: StateView.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class StateView extends QuiduView {
	public StateView(PetalNode parent, Collection params, int tag) {
		super(parent, "StateView", params, tag);
	}

	public StateView() {
		super("StateView");
	}

	public void setIDParameter(String o) {
		params.set(1, o);
	}

	public String getIDParameter() {
		return params.get(1);
	}

	public ItemLabel getLabel() {
		return (ItemLabel) getProperty("label");
	}

	public void setLabel(ItemLabel o) {
		defineProperty("label", o);
	}

	public Compartment getCompartment() {
		return (Compartment) getProperty("compartment");
	}

	public void setCompartment(Compartment o) {
		defineProperty("compartment", o);
	}

	public Tag getParentView() {
		return (Tag) getProperty("Parent_View");
	}

	public void setParentView(Tag o) {
		defineProperty("Parent_View", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
