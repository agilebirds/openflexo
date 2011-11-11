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
 * Represents RoleView object
 * 
 * @version $Id: RoleView.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class RoleView extends QuiduView implements SegLabeled {
	static final long serialVersionUID = -903647066739783608L;

	public RoleView(PetalNode parent, Collection params, int tag) {
		super(parent, "RoleView", params, tag);
	}

	public RoleView() {
		super("RoleView");
	}

	public Tag getParentView() {
		return (Tag) getProperty("Parent_View");
	}

	public void setParentView(Tag o) {
		defineProperty("Parent_View", o);
	}

	@Override
	public SegLabel getLabel() {
		return (SegLabel) getProperty("label");
	}

	@Override
	public void setLabel(SegLabel o) {
		defineProperty("label", o);
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
