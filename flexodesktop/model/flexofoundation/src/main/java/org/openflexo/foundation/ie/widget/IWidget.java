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
package org.openflexo.foundation.ie.widget;

import java.util.Vector;

import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;

/**
 * @author gpolet
 * 
 */
public interface IWidget extends IObject {
	public IEObject getParent();

	public void setParent(IEObject parent);

	public void setIndex(int index);

	public int getIndex();

	public Vector<IWidget> getAllNonSequenceWidget();

	public void delete();

	public void removeFromContainer();

	public void setWOComponent(IEWOComponent component);

	public String getRawRowKeyPath();

	public boolean areComponentInstancesValid();

	public void removeInvalidComponentInstances();

	public IEWOComponent getWOComponent();

	public Vector<IEHyperlinkWidget> getAllButtonInterface();

	public HTMLListDescriptor getHTMLListDescriptor();
}
