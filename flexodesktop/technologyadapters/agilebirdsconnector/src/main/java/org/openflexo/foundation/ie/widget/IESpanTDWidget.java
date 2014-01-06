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

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * @author gpolet
 * 
 */
public class IESpanTDWidget extends IETDWidget {

	private IETDWidget spanner;

	public IESpanTDWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, null, builder.getProject());
		initializeDeserialization(builder);
	}

	/**
	 * @param woComponent
	 * @param parent
	 * @param prj
	 */
	public IESpanTDWidget(IEWOComponent woComponent, IESequenceTD parent, IETDWidget spanner, FlexoProject prj) {
		super(woComponent, parent, prj);
		this.spanner = spanner;
		if (spanner != null) {
			this.spanner.addToSpannedTD(this);
		}
	}

	public IETDWidget getSpanner() {
		return spanner;
	}

	/**
	 * Returns true if the current span TD is on the same row than the spanner
	 * 
	 * @return true if yLocation = spanner.yLocation
	 */
	public boolean isOnRowSide() {
		return getYLocation() == getSpanner().getYLocation();
	}

	/**
	 * Returns true if the current span TD is on the same column than the spanner
	 * 
	 * @return true if xLocation = spanner.xLocation
	 */
	public boolean isOnColSide() {
		return getXLocation() == getSpanner().getXLocation();
	}

	/**
	 * Overrides makeRealDelete
	 * 
	 * @see org.openflexo.foundation.ie.widget.IETDWidget#makeRealDelete(boolean)
	 */
	@Override
	public void makeRealDelete(boolean notify) {
		if (getSpanner() != null) {
			getSpanner().removeFromSpannedTD(this);
		}
		super.makeRealDelete(notify);
	}

	/**
     * 
     */
	public void replaceByNormalTD() {
		getParent().replaceByNormalTD(this);
	}

	/**
	 * Overrides notifyDisplayNeedsRefresh
	 * 
	 * @see org.openflexo.foundation.ie.widget.IETDWidget#notifyDisplayNeedsRefresh()
	 */
	@Override
	public void notifyDisplayNeedsRefresh() {
		// Span TD are not represented, therefore, display that don't exist don't need a refresh
	}

}
