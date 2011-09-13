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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.table.SpanChanged;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;


/**
 * @author gpolet Created on 12 sept. 2005
 */
public class IERadioButtonWidgetView extends AbstractInnerTableWidgetView<IERadioButtonWidget>
{

	protected JRadioButton button;

	private JPanel container;

	protected boolean isUpdatingModel = false;

	public static final Font TEXTFIELD_FONT = new Font("SansSerif", Font.PLAIN, 10);

	/**
	 * @param ieController
	 * @param model
	 */
	public IERadioButtonWidgetView(IEController ieController, IERadioButtonWidget model, boolean addDnDSupport,
			IEWOComponentView view)
	{
		super(ieController, model, addDnDSupport, view);
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		// layout.setVgap(4);
		setLayout(layout);
		button = new JRadioButton();
		button.setEnabled(true);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				getRadioButtonModel().setValue(button.isSelected());
			}
		});
		button.setFocusable(false);
		button.setOpaque(false);
		// button.setFont(TEXTFIELD_FONT);
		if (getRadioButtonModel().getDescription() != null) {
			button.setToolTipText(getRadioButtonModel().getDescription());
		}
		if (getRadioButtonModel().getDisplayLabel()) {
			button.setText(getRadioButtonModel().getButtonLabel());
			if (getRadioButtonModel().getLabelAlign()) {
				button.setHorizontalTextPosition(SwingConstants.LEFT);
			} else {
				button.setHorizontalTextPosition(SwingConstants.RIGHT);
			}
		}

		TransparentMouseListener tml = new TransparentMouseListener(button, this);
		button.addMouseListener(tml);
		button.addMouseMotionListener(tml);

		button.setSelected(getRadioButtonModel().getValue());
		button.setBackground(getBackgroundColor());
		container = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
		container.setOpaque(false);
		container.add(button);
		add(container);
		setBackground(getBackgroundColor());
	}

	public IERadioButtonWidget getRadioButtonModel()
	{
		return getModel();
	}

	/**
	 * Overrides getPreferredSize
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize()
	{
		if (getHoldsNextComputedPreferredSize()){
			Dimension storedSize = storedPrefSize();
			if(storedSize!=null) {
				return storedSize;
			}
		}
		Dimension d = super.getPreferredSize();
		if (getHoldsNextComputedPreferredSize()) {
			storePrefSize(d);
		}
		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable o, DataModification modif)
	{
		if (modif.modificationType() == DataModification.ATTRIBUTE) {
			if (modif.propertyName().equals(IERadioButtonWidget.ATTRIB_DEFAULTVALUE_NAME)) {
				if (!isUpdatingModel) {
					button.setSelected(getRadioButtonModel().getValue());
				}
				if (getParent() != null) {
					getParent().doLayout();
					((JComponent) getParent()).repaint();
				}
			} else if (modif.propertyName().equals("colSpan") || modif.propertyName().equals("rowSpan")) {
				if (getParent() != null) {
					getParent().doLayout();
					((JComponent) getParent()).repaint();
				}

			} else if (modif.propertyName().equals("labelAlign")) {
				if (getRadioButtonModel().getLabelAlign()) {
					button.setHorizontalTextPosition(SwingConstants.LEFT);
				} else {
					button.setHorizontalTextPosition(SwingConstants.RIGHT);
				}
			} else if (modif.propertyName().equals(IERadioButtonWidget.BUTTON_LABEL) && getRadioButtonModel().getDisplayLabel()) {
				button.setText(getRadioButtonModel().getButtonLabel());
			} else if (modif.propertyName().equals("displayLabel")) {
				if (getRadioButtonModel().getDisplayLabel()) {
					if (getRadioButtonModel().getLabelAlign()) {
						button.setHorizontalTextPosition(SwingConstants.LEFT);
					} else {
						button.setHorizontalTextPosition(SwingConstants.RIGHT);
					}
					button.setText(getRadioButtonModel().getButtonLabel());
				} else {
					button.setText(null);
				}
			}
		} if (modif instanceof SpanChanged) {
			if (getParent() != null) {
				getParent().doLayout();
				((JComponent) getParent()).repaint();
			}
		} else if (modif instanceof WidgetRemovedFromTable && o == getRadioButtonModel()) {
			delete();
		} else {
			super.update(o, modif);
		}
	}
}
