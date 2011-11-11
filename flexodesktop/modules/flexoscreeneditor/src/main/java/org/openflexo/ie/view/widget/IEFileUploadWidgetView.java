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
package org.openflexo.ie.view.widget;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.widget.IEFileUploadWidget;
import org.openflexo.ie.IECst;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IEFileUploadWidgetView extends AbstractInnerTableWidgetView<IEFileUploadWidget> {

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================
	private JTextField _jText;

	private JButton _browseButton;

	private JPanel container;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEFileUploadWidgetView(IEController ieController, IEFileUploadWidget model, boolean addDnDSupport, IEWOComponentView view) {
		super(ieController, model, addDnDSupport, view);
		// _model = model;
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		// layout.setVgap(2);
		setLayout(layout);
		_jText = new JTextField("filename.txt");
		_jText.setEnabled(false);
		_jText.setFont(IECst.WOSTRING_FONT);
		// _jLabel.setAlignmentY(0.5f);
		TransparentMouseListener tml = new TransparentMouseListener(_jText, this);
		_jText.addMouseListener(tml);
		_jText.addMouseMotionListener(tml);
		if (getFileUploadModel().getTooltip() != null) {
			_jText.setToolTipText(getFileUploadModel().getTooltip());
		}
		_browseButton = new JButton();
		_browseButton.setText(FlexoLocalization.localizedForKey("browse", _browseButton));
		_browseButton.setEnabled(false);
		_browseButton.setOpaque(false);
		tml = new TransparentMouseListener(_browseButton, this);
		_browseButton.addMouseListener(tml);
		_browseButton.addMouseMotionListener(tml);
		container = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
		container.add(_jText);
		container.add(_browseButton);
		container.setOpaque(false);
		add(container);
		_browseButton.setBackground(getBackgroundColor());
		setBackground(getBackgroundColor());
	}

	public IEFileUploadWidget getFileUploadModel() {
		return getModel();
	}

	// ==========================================================================
	// ============================= Observer
	// ===================================
	// ==========================================================================

	/**
	 * Overrides getPreferredSize
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		if (getHoldsNextComputedPreferredSize()) {
			Dimension storedSize = storedPrefSize();
			if (storedSize != null)
				return storedSize;
		}
		Dimension d = super.getPreferredSize();
		if (getHoldsNextComputedPreferredSize())
			storePrefSize(d);
		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(FlexoObservable arg0, DataModification modif) {
		if (modif.modificationType() == DataModification.ATTRIBUTE) {
			if (modif.propertyName().equals("colSpan") || modif.propertyName().equals("rowSpan")) {
				getParent().doLayout();
				((JComponent) getParent()).repaint();
			}
		}
		if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
			delete();
		} else
			super.update(arg0, modif);
	}

}
