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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.localization.FlexoLocalization;

/**
 * Widget allowing to view and edit a Font
 * 
 * @author sguerin
 * 
 */
public class FontSelector extends CustomPopup<Font> implements ChangeListener {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FontSelector.class.getPackage().getName());

	private Font _revertValue;

	protected FontDetailsPanel _selectorPanel;
	protected FontSelectionModel _fsm;

	public static Font DEFAULT_FONT = new JPanel().getFont();

	public FontSelector(FontSelectionModel fsm) {
		super(fsm.getSelectedFont() != null ? fsm.getSelectedFont() : DEFAULT_FONT);
		_fsm = fsm;
		if (fsm.getSelectedFont() == null) {
			fsm.setSelectedFont(DEFAULT_FONT);
		}
		_fsm.addChangeListener(this);
		setRevertValue(fsm.getSelectedFont());
		setFocusable(true);
		getFrontComponent().update();
	}

	@Override
	public void delete() {
		super.delete();
		_fsm.removeChangeListener(this);
	}

	@Override
	public void setRevertValue(Font oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = new Font(oldValue.getFontName(), oldValue.getStyle(), oldValue.getSize());
		} else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	public Font getRevertValue() {
		return _revertValue;
	}

	@Override
	protected FontDetailsPanel createCustomPanel(Font editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected FontDetailsPanel makeCustomPanel(Font editedObject) {
		return new FontDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(Font editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	public FontSelector getJComponent() {
		return this;
	}

	public class FontDetailsPanel extends ResizablePanel {
		private JFontChooser fontChooser;
		private JButton _applyButton;
		private JButton _cancelButton;
		private JPanel _controlPanel;

		protected FontDetailsPanel(Font editedFont) {
			super();

			_fsm.setSelectedFont(editedFont);

			fontChooser = new JFontChooser(_fsm);

			setLayout(new BorderLayout());
			add(fontChooser, BorderLayout.CENTER);

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
		}

		@Override
		public Dimension getDefaultSize() {
			return getPreferredSize();
		}

		public void delete() {
		}
	}

	@Override
	public Font getEditedObject() {
		if (_fsm != null) {
			if (_fsm.getSelectedFont() != null) {
				return _fsm.getSelectedFont();
			} else {
				return DEFAULT_FONT;
			}
		}
		return DEFAULT_FONT;
	}

	@Override
	public void setEditedObject(Font font) {
		if (font == null) {
			font = DEFAULT_FONT;
		}
		_fsm.setSelectedFont(font);
		super.setEditedObject(font);
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
		_fsm.removeChangeListener(this);
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	public FontDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected FontPreviewPanel buildFrontComponent() {
		return new FontPreviewPanel();
	}

	@Override
	public FontPreviewPanel getFrontComponent() {
		return (FontPreviewPanel) super.getFrontComponent();
	}

	/*@Override
	protected Border getDownButtonBorder()
	{
		return BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(1,1,1,1),
				BorderFactory.createRaisedBevelBorder());
	}*/

	protected class FontPreviewPanel extends JPanel {
		private JLabel previewLabel;

		protected FontPreviewPanel() {
			super(new BorderLayout());
			// setBorder(BorderFactory.createEtchedBorder(Color.GRAY,Color.LIGHT_GRAY));
			setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
			previewLabel = new JLabel();
			add(previewLabel, BorderLayout.CENTER);
			update();
			// setPreferredSize(new Dimension(40,19));
		}

		protected void update() {
			// logger.info("Update front panel with "+JFontChooser.fontDescription(getEditedObject())+" font="+getEditedObject());
			previewLabel.setFont(getEditedObject());
			previewLabel.setText(JFontChooser.fontDescription(getEditedObject()));
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		getFrontComponent().update();
	}

	public static interface FontSelectionModel {
		Font getSelectedFont();

		void setSelectedFont(Font font);

		void addChangeListener(ChangeListener listener);

		void removeChangeListener(ChangeListener listener);
	}

}
