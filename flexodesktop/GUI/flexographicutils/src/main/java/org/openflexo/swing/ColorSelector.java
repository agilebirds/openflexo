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
public class ColorSelector extends CustomPopup<Color> implements ChangeListener {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(ColorSelector.class.getPackage().getName());

	private Color _revertValue;

	protected ColorDetailsPanel _selectorPanel;
	protected ColorSelectionModel _csm;

	public ColorSelector(ColorSelectionModel csm) {
		super(csm.getSelectedColor());
		_csm = csm;
		_csm.addChangeListener(this);
		setRevertValue(csm.getSelectedColor());
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
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ColorDetailsPanel makeCustomPanel(Color editedObject) {
		return new ColorDetailsPanel(editedObject);
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

		protected ColorDetailsPanel(Color editedColor) {
			super();

			if (editedColor == null) {
				editedColor = Color.WHITE;
			}
			_csm.setSelectedColor(editedColor);
			colorChooser = new JColorChooser(_csm);

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
	public Color getEditedObject() {
		if (_csm != null) {
			return _csm.getSelectedColor();
		}
		return null;
	}

	@Override
	public void setEditedObject(Color color) {
		_csm.setSelectedColor(color);
		super.setEditedObject(color);
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
		_csm.removeChangeListener(this);
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
		private JPanel insidePanel;

		protected ColorPreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			setPreferredSize(new Dimension(40, 19));
			setMinimumSize(new Dimension(40, 19));
			insidePanel = new JPanel(new BorderLayout());
			add(insidePanel, BorderLayout.CENTER);
			update();
		}

		protected void update() {
			insidePanel.setBackground(getEditedObject());
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		getFrontComponent().update();
	}

}
