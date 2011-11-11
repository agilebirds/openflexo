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
 * Super class for all relationship view objects.
 * 
 * @version $Id: RelationshipView.java,v 1.2 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class RelationshipView extends QuiduView implements HasQuidu {
	static final long serialVersionUID = -7371455691568399461L;

	protected RelationshipView(PetalNode parent, String name, Collection params, int tag) {
		super(parent, name, params, tag);
	}

	protected RelationshipView(String name) {
		super(name);
	}

	public void setLabel(ItemLabel label) {
		defineProperty("label", label);
	}

	public ItemLabel getLabel() {
		return (ItemLabel) getProperty("label");
	}

	public void setStereotype(SegLabel label) {
		defineProperty("stereotype", label);
	}

	public SegLabel getStereotype() {
		PetalNode node = getProperty("stereotype");

		// May be boolean otherwise, brrrr
		return (node instanceof SegLabel) ? (SegLabel) node : null;
	}
}
