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
package org.openflexo.utils;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;

import org.openflexo.GeneralPreferences;
import org.openflexo.prefs.FlexoPreferences;

public class FlexoSplitPaneLocationSaver implements PropertyChangeListener {

	private JSplitPane splitPane;
	private String id;

	public FlexoSplitPaneLocationSaver(JSplitPane pane, String id) {
		this(pane, id, null);
	}

	public FlexoSplitPaneLocationSaver(JSplitPane pane, final String id, final Double defaultDividerLocation) {
		this.splitPane = pane;
		this.id = id;
		layoutSplitPaneWhenShowing(id, defaultDividerLocation);
	}

	public void layoutSplitPaneWhenShowing(final String id, final Double defaultDividerLocation) {
		splitPane.addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
					layoutSplitPane(id, defaultDividerLocation);
					splitPane.removeHierarchyListener(this);
				}
			}
		});
	}

	private void layoutSplitPane(String id, Double defaultDividerLocation) {
		if (GeneralPreferences.getDividerLocationForSplitPaneWithID(id) >= 0) {
			splitPane.setDividerLocation(GeneralPreferences.getDividerLocationForSplitPaneWithID(id));
		} else if (defaultDividerLocation != null) {
			splitPane.setDividerLocation(defaultDividerLocation);
		} else {
			splitPane.resetToPreferredSizes();
		}
		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		saveLocationInPreferenceWhenPossible();
	}

	private Thread locationSaver;

	protected synchronized void saveLocationInPreferenceWhenPossible() {
		if (!splitPane.isVisible()) {
			return;
		}
		if (locationSaver != null) {
			locationSaver.interrupt();// Resets thread sleep
			return;
		}

		locationSaver = new Thread(new Runnable() {
			/**
			 * Overrides run
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				boolean go = true;
				while (go) {
					try {
						go = false;
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						go = true;// interruption is used to reset sleep.
					}
				}
				saveLocationInPreference();
			}
		}, "Splitpane location saver for " + id);
		locationSaver.start();
	}

	protected void saveLocationInPreference() {
		int value = splitPane.getDividerLocation();
		if (value > splitPane.getMaximumDividerLocation()) {
			value = splitPane.getMaximumDividerLocation();
		} else if (value < splitPane.getMinimumDividerLocation()) {
			value = splitPane.getMinimumDividerLocation();
		}
		GeneralPreferences.setDividerLocationForSplitPaneWithID(value, id);
		FlexoPreferences.savePreferences(true);
		locationSaver = null;
	}

}
