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
 * Represents InheritTreeView object
 * 
 * @version $Id: InheritTreeView.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class InheritTreeView extends QuiduView {
	public InheritTreeView(PetalNode parent, Collection params, int tag) {
		super(parent, "InheritTreeView", params, tag);
	}

	public InheritTreeView() {
		super("InheritTreeView");
	}

	public List getVertices() {
		return (List) getProperty("vertices");
	}

	public void setVertices(List o) {
		defineProperty("vertices", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
