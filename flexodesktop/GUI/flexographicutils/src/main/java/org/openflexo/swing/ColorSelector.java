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

package org.openflexo.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.localization.FlexoLocalization;

/**
 * Widget allowing to view and edit a Color
 * 
 * @author sguerin
 * 
 */
public class ColorSelector extends CustomPopup<Color> implements ColorSelectionModel {

	static final Logger logger = Logger.getLogger(ColorSelector.class.getPackage().getName());

	private Color _revertValue;

	protected ColorDetailsPanel _selectorPanel;

	public ColorSelector() {
		super(Color.WHITE);
		setFocusable(true);
	}

	@Override
	public void setRevertValue(Color oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = new Color(oldValue.getRed(), oldValue.getGreen(), oldValue.getBlue());
		} else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	public Color getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ColorDetailsPanel createCustomPanel(Color editedObject) {
		_selectorPanel = makeCustomPanel();
		return _selectorPanel;
	}

	protected ColorDetailsPanel makeCustomPanel() {
		return new ColorDetailsPanel();
	}

	@Override
	public void updateCustomPanel(Color editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	public ColorSelector getJComponent() {
		return this;
	}

	public class ColorDetailsPanel extends ResizablePanel {
		private JColorChooser colorChooser;
		private JButton _applyButton;
		private JButton _cancelButton;
		private JPanel _controlPanel;

		protected ColorDetailsPanel() {
			super();

			colorChooser = new JColorChooser(ColorSelector.this);

			setLayout(new BorderLayout());
			add(colorChooser, BorderLayout.CENTER);

			_controlPanel = new JPanel();
			_controlPanel.setLayout(new FlowLayout());
			_controlPanel.add(_applyButton = new JButton(FlexoLocalization.localizedForKey("ok")));
			_controlPanel.add(_cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel")));
			_applyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					apply();
				}
			});
			_cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});
			add(_controlPanel, BorderLayout.SOUTH);
		}

		public void update() {
			// _csm.setSelectedColor(getEditedObject());
			// colorChooser.setColor(getEditedObject());
		}

		@Override
		public Dimension getDefaultSize() {
			return getPreferredSize();
		}

		public void delete() {
		}
	}

	@Override
	public void setEditedObject(Color color) {
		super.setEditedObject(color);
		for (ChangeListener l : listenerList.getListeners(ChangeListener.class)) {
			l.stateChanged(new ChangeEvent(this));
		}
	}

	@Override
	public void apply() {
		setRevertValue(getEditedObject());
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	public ColorDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected ColorPreviewPanel buildFrontComponent() {
		return new ColorPreviewPanel();
	}

	@Override
	public ColorPreviewPanel getFrontComponent() {
		return (ColorPreviewPanel) super.getFrontComponent();
	}

	/*@Override
	protected Border getDownButtonBorder()
	{
		return BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(1,1,1,1),
				BorderFactory.createRaisedBevelBorder());
	}*/

	protected class ColorPreviewPanel extends JPanel {

		protected ColorPreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			setPreferredSize(new Dimension(40, 19));
			setMinimumSize(new Dimension(40, 19));
			update();
		}

		protected void update() {
			setBackground(getEditedObject());
			repaint();
		}
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		listenerList.add(ChangeListener.class, listener);
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		listenerList.remove(ChangeListener.class, listener);
	}

	@Override
	public Color getSelectedColor() {
		if (getEditedObject() != null) {
			return getEditedObject();
		} else {
			return Color.WHITE;
		}
	}

	@Override
	public void setSelectedColor(Color color) {
		setEditedObject(color);
	}

}
