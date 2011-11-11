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
package org.openflexo.view.palette;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectableView;
import org.openflexo.selection.SelectionListener;
import org.openflexo.utils.DrawUtils;
import org.openflexo.view.controller.SelectionManagingController;

public abstract class PalettePanel extends JPanel implements SelectionListener {

	protected static final Logger logger = Logger.getLogger(PalettePanel.class.getPackage().getName());

	private boolean isEdited = false;

	private Vector<PaletteElement> _paletteElements;

	protected final FlexoPalette _palette;

	private String _name;

	private Dimension prefSize;

	public PalettePanel(FlexoPalette palette) {
		super();
		_palette = palette;
		_paletteElements = new Vector<PaletteElement>();
	}

	public void addToPaletteElements(PaletteElement paletteElement) {
		_paletteElements.add(paletteElement);
	}

	public PalettePanel delete() {
		((SelectionManagingController) _palette.getController()).getSelectionManager().removeFromSelectionListeners(this);
		return this;
	}

	public boolean isEdited() {
		return isEdited;
	}

	public FlexoPalette getPalette() {
		return _palette;
	}

	private JTabbedPane getParentContainer() {
		if (getParent() != null && getParent().getParent() != null)
			return (JTabbedPane) getParent().getParent().getParent();
		else
			return null;
	}

	private int getIndexForComponent() {
		return getParentContainer().indexOfComponent(this.getParent().getParent());
	}

	protected void setTitle() {
		String title = _name;
		if (isEdited())
			title += " [" + FlexoLocalization.localizedForKey("edited") + "]";
		if (getParentContainer() != null)
			getParentContainer().setTitleAt(getIndexForComponent(), title);
	}

	public void editPalette() {
		logger.info("EditPalette");
		isEdited = true;
		for (Enumeration en = _paletteElements.elements(); en.hasMoreElements();) {
			PaletteElement next = (PaletteElement) en.nextElement();
			next.edit();
		}
		if (_palette.getController() instanceof SelectionManagingController) {
			((SelectionManagingController) _palette.getController()).getSelectionManager().addToSelectionListeners(this);
		}
		setTitle();
		revalidate();
		repaint();
	}

	public void closePaletteEdition() {
		logger.info("ClosePaletteEdition");
		isEdited = false;
		for (Enumeration en = _paletteElements.elements(); en.hasMoreElements();) {
			PaletteElement next = (PaletteElement) en.nextElement();
			next.closeEdition();
		}
		if (_palette.getController() instanceof SelectionManagingController) {
			((SelectionManagingController) _palette.getController()).getSelectionManager().resetSelection();
			((SelectionManagingController) _palette.getController()).getSelectionManager().removeFromSelectionListeners(this);
		}

		setTitle();
		revalidate();
		repaint();
	}

	public void savePalette() {
		logger.info("SavePalette");
		for (Enumeration en = _paletteElements.elements(); en.hasMoreElements();) {
			PaletteElement next = (PaletteElement) en.nextElement();
			if (next.isEdited()) {
				next.save();
			}
		}
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String aName) {
		super.setName(aName);
		_name = aName;
		setTitle();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (isEdited()) {
			paintSelection(g);
		}
	}

	public void paintSelection(Graphics g) {
		if (logger.isLoggable(Level.FINE))
			logger.finer("Drawing selection");
		Graphics2D g2 = (Graphics2D) g;
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
		g2.setColor(Color.BLUE);
		Rectangle selection;

		selection = new Rectangle();
		selection.setSize(getSize());
		selection.setLocation(0, 0);
		g2.fillRect(selection.x, selection.y, 5, 5);
		g2.fillRect(selection.x + selection.width - 5, selection.y, 5, 5);
		g2.fillRect(selection.x, selection.y + selection.height - 5, 5, 5);
		g2.fillRect(selection.x + selection.width - 5, selection.y + selection.height - 5, 5, 5);
	}

	protected SelectableView selectableViewForObject(FlexoModelObject object) {
		for (Enumeration en = _paletteElements.elements(); en.hasMoreElements();) {
			PaletteElement next = (PaletteElement) en.nextElement();
			if (next.getObject() == object)
				return next.getView();
		}
		return null;
	}

	/**
	 * Notified that supplied object has been added to selection
	 * 
	 * @param object
	 *            : the object that has been added to selection
	 */
	@Override
	public void fireObjectSelected(FlexoModelObject object) {
		if (object != null) {
			SelectableView view = selectableViewForObject(object);
			if (view != null) {
				view.setIsSelected(true);
			}
		}
	}

	/**
	 * Notified that supplied object has been removed from selection
	 * 
	 * @param object
	 *            : the object that has been removed from selection
	 */
	@Override
	public void fireObjectDeselected(FlexoModelObject object) {
		if (object != null) {
			SelectableView view = selectableViewForObject(object);
			if (view != null) {
				view.setIsSelected(false);
			}
		}
	}

	/**
	 * Notified selection has been resetted
	 */
	@Override
	public void fireResetSelection() {
		for (Enumeration en = _paletteElements.elements(); en.hasMoreElements();) {
			PaletteElement next = (PaletteElement) en.nextElement();
			if (next.getView() != null)
				next.getView().setIsSelected(false);
		}
	}

	/**
	 * Notified that the selection manager is performing a multiple selection
	 */
	@Override
	public void fireBeginMultipleSelection() {
	}

	/**
	 * Notified that the selection manager has finished to perform a multiple selection
	 */
	@Override
	public void fireEndMultipleSelection() {
	}

	/**
	 * Overrides getPreferredSize
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		if (prefSize != null)
			return prefSize;
		JScrollPane parent = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
		if (parent != null) {
			Dimension d = new Dimension();
			d.width = parent.getWidth() - parent.getInsets().left - parent.getInsets().right;
			if (parent.getVerticalScrollBar() != null && parent.getVerticalScrollBar().isVisible())
				d.width -= parent.getVerticalScrollBar().getWidth();
			if (getComponentCount() > 1) {
				int gap = getLayout() instanceof FlowLayout ? ((FlowLayout) getLayout()).getVgap() : 4;
				for (Component c : getComponents()) {
					d.height = Math.max(c.getY() + c.getHeight() + gap, d.height);
				}
			} else
				d.height = super.getPreferredSize().height;
			return d;
		} else
			return super.getPreferredSize();
	}

	/**
	 * Overrides setPreferredSize
	 * 
	 * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension)
	 */
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		prefSize = preferredSize;
	}
}