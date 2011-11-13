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
 * Represents Focus_Of_Control object
 * 
 * @version $Id: FocusOfControl.java,v 1.3 2011/09/12 11:46:49 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class FocusOfControl extends PetalObject implements Tagged {
	private int tag = -1;

	public FocusOfControl(PetalNode parent, Collection params, int tag) {
		super(parent, "Focus_Of_Control", params);
		setTag(tag);
	}

	public FocusOfControl() {
		super("Focus_Of_Control");
	}

	@Override
	public void setTag(int t) {
		tag = t;
	}

	@Override
	public int getTag() {
		return tag;
	}

	public Location getLocation() {
		return (Location) getProperty("location");
	}

	public void setLocation(Location o) {
		defineProperty("location", o);
	}

	public String getIconStyle() {
		return getPropertyAsString("icon_style");
	}

	public void setIconStyle(String o) {
		defineProperty("icon_style", o);
	}

	public Tag getInterObjView() {
		return (Tag) getProperty("InterObjView");
	}

	public void setInterObjView(Tag o) {
		defineProperty("InterObjView", o);
	}

	public int getHeight() {
		return getPropertyAsInteger("height");
	}

	public void setHeight(int o) {
		defineProperty("height", o);
	}

	public int getYCoord() {
		return getPropertyAsInteger("y_coord");
	}

	public void setYCoord(int o) {
		defineProperty("y_coord", o);
	}

	public boolean getNested() {
		return getPropertyAsBoolean("Nested");
	}

	public void setNested(boolean o) {
		defineProperty("Nested", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
