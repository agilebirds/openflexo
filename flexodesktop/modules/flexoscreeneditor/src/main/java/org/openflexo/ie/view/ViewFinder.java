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
package org.openflexo.ie.view;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.ie.view.widget.IEWidgetView;

/**
 * Utility class allowing to retrieve views from a model
 * 
 * @author sguerin
 * 
 */
public class ViewFinder {

	private static final Logger logger = Logger.getLogger(ViewFinder.class.getPackage().getName());

	public static IEWOComponentView getRootView(JComponent component) {
		IEWOComponentView returned;

		Component parent = component.getParent();
		if (parent == null) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Could not retrieve Root View for component " + component + " [NO PARENT]");
			returned = null;
		} else if (parent instanceof IEWOComponentView) {
			returned = (IEWOComponentView) parent;
		} else if (parent instanceof IEViewManaging) {
			returned = ((IEViewManaging) parent).getRootView();
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Could not retrieve Root View for component " + component + " [PARENT=" + parent.getClass().getName() + "]");
			returned = null;
		}
		return returned;
	}

	public static IEWidgetView findViewForModel(IEViewManaging component, IEObject object) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("findViewForModel called for " + object.getClass().getName() + " in " + component.getClass().getName());

		// First search inside

		IEWidgetView lookInside = component.internallyFindViewForModel(object);
		if (lookInside != null) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Internally found !");
			return lookInside;
		}
		// OK, let's look outside
		if (component.getRootView() != null) {
			IEWidgetView lookOutside = component.getRootView().internallyFindViewForModel(object);
			if (lookOutside != null) {
				if (logger.isLoggable(Level.FINE))
					logger.fine("Externally found !");
				return lookOutside;
			}
		}
		if (logger.isLoggable(Level.WARNING))
			logger.warning("Could not find view for model " + object);
		return null;
	}

	public static IEWidgetView internallyFindViewForModel(JComponent component, IEObject object) {
		Component[] internalComponents = component.getComponents();

		for (int i = 0; i < internalComponents.length; i++) {
			if (internalComponents[i] instanceof IEWidgetView) {
				IEWidgetView next = (IEWidgetView) internalComponents[i];
				if (next.getModel().equals(object)) {
					return next;
				}
			}
			if (internalComponents[i] instanceof IEViewManaging) {
				IEViewManaging next = (IEViewManaging) internalComponents[i];
				IEWidgetView tryDeeper = next.internallyFindViewForModel(object);
				if (tryDeeper != null) {
					return tryDeeper;
				}
			}
		}
		return null;
	}

}
