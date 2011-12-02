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
package org.openflexo.components.browser;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.openflexo.components.browser.view.BrowserActionEvent;
import org.openflexo.components.browser.view.BrowserActionListener;

/**
 * Please comment this class
 * 
 * @author sguerin
 * @deprecated
 * 
 */
@Deprecated
public class BrowserAction {

	private BrowserElementType elementType;

	private String actionName;

	private ImageIcon actionIcon;

	private Vector _listeners;

	public BrowserAction(BrowserElementType elementType, String actionName) {
		super();
		this.actionName = actionName;
		this.elementType = elementType;
		_listeners = new Vector();
	}

	public BrowserAction(BrowserElementType elementType, String actionName, ImageIcon actionIcon) {
		this(elementType, actionName);
		this.actionIcon = actionIcon;
	}

	public String getActionName() {
		return actionName;
	}

	public ImageIcon getActionIcon() {
		return actionIcon;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public BrowserElementType getElementType() {
		return elementType;
	}

	public void setElementType(BrowserElementType elementType) {
		this.elementType = elementType;
	}

	public void addBrowserActionListener(BrowserActionListener aListener) {
		_listeners.add(aListener);
	}

	public void removeBrowserActionListener(BrowserActionListener aListener) {
		_listeners.remove(aListener);
	}

	public void fireBrowserActionEvent(BrowserElement element, JFrame frame) {
		BrowserActionEvent event = new BrowserActionEvent(element, frame);
		for (Enumeration e = _listeners.elements(); e.hasMoreElements();) {
			BrowserActionListener listener = (BrowserActionListener) e.nextElement();
			listener.browserActionPerformed(event);
		}
	}

}
