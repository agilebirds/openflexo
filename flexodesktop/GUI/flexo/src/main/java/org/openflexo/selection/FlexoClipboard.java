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
package org.openflexo.selection;

import java.awt.Point;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openflexo.foundation.FlexoObject;

/**
 * FlexoClipboard is intented to be the object working with the SelectionManager and storing copied, cutted and pasted objects. Handled
 * objects are instances implementing {@link org.openflexo.selection.SelectableView}. This class is abstract and must be subclassed
 * depending on the context (WKF or IE).
 * 
 * @author sguerin
 */
public abstract class FlexoClipboard {

	private static final Logger logger = Logger.getLogger(FlexoClipboard.class.getPackage().getName());

	protected SelectionManager _selectionManager;

	protected Vector<? extends FlexoObject> _clipboardedObjects;

	protected JMenuItem _copyMenuItem;

	protected JMenuItem _pasteMenuItem;

	protected JMenuItem _cutMenuItem;

	protected boolean _isCopyEnabled;

	protected boolean _isPasteEnabled;

	protected boolean _isCutEnabled;

	protected Point defaultPastingPoint = new Point(100, 100);

	protected JComponent defaultPastingContainer = null;

	public FlexoClipboard(SelectionManager aSelectionManager, JMenuItem copyMenuItem, JMenuItem pasteMenuItem, JMenuItem cutMenuItem) {
		super();
		_selectionManager = aSelectionManager;
		_copyMenuItem = copyMenuItem;
		_pasteMenuItem = pasteMenuItem;
		_cutMenuItem = cutMenuItem;
		_clipboardedObjects = new Vector();
		setCopyEnabled(false);
		setPasteEnabled(false);
		setCutEnabled(false);
	}

	protected void setCopyEnabled(boolean aBoolean) {
		// aBoolean = ((FlexoActionType)_copyMenuItem.getAction()).isEnabled(_selectionManager.getFocusedObject(),
		// _selectionManager.getSelection());
		if (_copyMenuItem != null) {
			_copyMenuItem.setEnabled(aBoolean);
		}
		_isCopyEnabled = aBoolean;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("COPY enabled = " + aBoolean);
		}
	}

	protected void setPasteEnabled(boolean aBoolean) {
		// aBoolean = ((FlexoActionType)_pasteMenuItem.getAction()).isEnabled(_selectionManager.getFocusedObject(),
		// _selectionManager.getSelection());
		if (_pasteMenuItem != null) {
			_pasteMenuItem.setEnabled(aBoolean);
		}
		_isPasteEnabled = aBoolean;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("PASTE enabled = " + aBoolean);
		}
	}

	protected void setCutEnabled(boolean aBoolean) {
		// aBoolean = ((FlexoActionType)_cutMenuItem.getAction()).isEnabled(_selectionManager.getFocusedObject(),
		// _selectionManager.getSelection());
		if (_cutMenuItem != null) {
			_cutMenuItem.setEnabled(aBoolean);
		}
		_isCutEnabled = aBoolean;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CUT enabled = " + aBoolean);
		}
	}

	public boolean hasCopiedData() {
		return _isPasteEnabled;
	}

	public boolean performSelectionCopy(Vector<? extends FlexoObject> currentlySelectedObjects) {
		if (_isCopyEnabled) {
			if (isCurrentSelectionValidForCopy(currentlySelectedObjects)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Copy is VALID");
				}
				if (performCopyOfSelection(currentlySelectedObjects)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Copy has been SUCCESSFULLY performed");
					}
					setPasteEnabled(true);
					return true;
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Copy has FAILED");
					}
					return false;
				}
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Copy is INVALID");
				}
			}
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Sorry, COPY disabled");
			}
		}
		return false;
	}

	public boolean performSelectionPaste() {
		if (_isPasteEnabled) {
			performSelectionPaste(_selectionManager.getPasteContext(), _selectionManager.getPastingGraphicalContext());
			return true;
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Sorry, PASTE disabled");
			}
			return false;
		}
	}

	public boolean performSelectionCut(Vector<? extends FlexoObject> currentlySelectedObjects) {
		if (_isCutEnabled) {
			return performSelectionCopy(currentlySelectedObjects);
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Sorry, CUT disabled");
			}
			return false;
		}
	}

	protected abstract void performSelectionPaste(FlexoObject pastingContext, PastingGraphicalContext graphicalContext);

	protected abstract boolean isCurrentSelectionValidForCopy(Vector<? extends FlexoObject> currentlySelectedObjects);

	protected abstract boolean performCopyOfSelection(Vector<? extends FlexoObject> currentlySelectedObjects);

}
