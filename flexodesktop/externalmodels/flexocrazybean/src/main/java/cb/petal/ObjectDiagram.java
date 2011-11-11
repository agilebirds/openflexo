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
 * Represents ObjectDiagram object
 * 
 * @version $Id: ObjectDiagram.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class ObjectDiagram extends Diagram {
	public ObjectDiagram(PetalNode parent, Collection params) {
		super(parent, "ObjectDiagram", params);
	}

	public ObjectDiagram() {
		super("ObjectDiagram");
	}

	// TODO
	@Override
	protected View searchView(String qual_name) {
		throw new RuntimeException("TODO: Not implemented yet");
	}

	public Tag getMechanismRef() {
		return (Tag) getProperty("mechanism_ref");
	}

	public void setMechanismRef(Tag o) {
		defineProperty("mechanism_ref", o);
	}

	@Override
	public String getTitle() {
		return getPropertyAsString("title");
	}

	@Override
	public void setTitle(String o) {
		defineProperty("title", o);
	}

	@Override
	public int getZoom() {
		return getPropertyAsInteger("zoom");
	}

	@Override
	public void setZoom(int o) {
		defineProperty("zoom", o);
	}

	@Override
	public int getMaxHeight() {
		return getPropertyAsInteger("max_height");
	}

	@Override
	public void setMaxHeight(int o) {
		defineProperty("max_height", o);
	}

	@Override
	public int getMaxWidth() {
		return getPropertyAsInteger("max_width");
	}

	@Override
	public void setMaxWidth(int o) {
		defineProperty("max_width", o);
	}

	@Override
	public int getOriginX() {
		return getPropertyAsInteger("origin_x");
	}

	@Override
	public void setOriginX(int o) {
		defineProperty("origin_x", o);
	}

	@Override
	public int getOriginY() {
		return getPropertyAsInteger("origin_y");
	}

	@Override
	public void setOriginY(int o) {
		defineProperty("origin_y", o);
	}

	@Override
	public List getItems() {
		return (List) getProperty("items");
	}

	@Override
	public void setItems(List o) {
		defineProperty("items", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
