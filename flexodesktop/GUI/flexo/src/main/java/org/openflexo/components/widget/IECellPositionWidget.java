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
package org.openflexo.components.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.openflexo.ColorCst;
import org.openflexo.FlexoCst;
import org.openflexo.swing.VerticalLayout;

import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class IECellPositionWidget extends CustomInspectorWidget<String> {

	protected static final Logger logger = FlexoLogger.getLogger(IECellPositionWidget.class.getPackage().getName());

	private JPanel component;

	private RadioButtonMatrixPanel radioPanel;

	/**
	 * @param model
	 */
	public IECellPositionWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		component = new JPanel(new BorderLayout());
		component.setBackground(ColorCst.GUI_BACK_COLOR);
		component.add(radioPanel = new RadioButtonMatrixPanel(), BorderLayout.WEST);
		JPanel p = new JPanel();
		p.setOpaque(false);
		component.add(p, BorderLayout.CENTER);
	}

	/**
	 * Overrides getDefaultType
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#getDefaultType()
	 */
	@Override
	public Class getDefaultType() {
		return null;
	}

	/**
	 * Overrides getDynamicComponent
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#getDynamicComponent()
	 */
	@Override
	public JComponent getDynamicComponent() {
		return component;
	}

	/**
	 * Overrides updateModelFromWidget
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#updateModelFromWidget()
	 */
	@Override
	public void updateModelFromWidget() {
		radioPanel.apply();
		super.updateModelFromWidget();
	}

	/**
	 * Overrides getModel
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#getModel()
	 */
	@Override
	public IETDWidget getModel() {
		return (IETDWidget) super.getModel();
	}

	/**
	 * Overrides updateWidgetFromModel
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#updateWidgetFromModel()
	 */
	@Override
	public void updateWidgetFromModel() {
		if (getModel() != null)
			radioPanel.update();
	}

	protected class RadioButtonMatrixPanel extends JPanel {

		protected JRadioButton topLeft;
		protected JRadioButton topMiddle;
		protected JRadioButton topRight;
		protected JRadioButton middleLeft;
		protected JRadioButton middleMiddle;
		protected JRadioButton middleRight;
		protected JRadioButton bottomLeft;
		protected JRadioButton bottomMiddle;
		protected JRadioButton bottomRight;

		/**
         * 
         */
		protected RadioButtonMatrixPanel() {
			super(new VerticalLayout(0, 0, 0));
			setBackground(ColorCst.GUI_BACK_COLOR);
			init();
		}

		private void init() {
			ButtonGroup bg = new ButtonGroup();
			// TOP PANEL
			JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
			top.setOpaque(false);
			topLeft = new JRadioButton();
			topLeft.setOpaque(false);
			topLeft.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (topLeft.isSelected()) {
						getModel().setVerticalAlignement(IETDWidget.VALIGN_TOP);
						getModel().setAlignement(IETDWidget.ALIGN_LEFT);
					}
				}

			});
			top.add(topLeft);
			topMiddle = new JRadioButton();
			topMiddle.setOpaque(false);
			topMiddle.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (topMiddle.isSelected()) {
						getModel().setVerticalAlignement(IETDWidget.VALIGN_TOP);
						getModel().setAlignement(IETDWidget.ALIGN_CENTER);
					}
				}

			});
			top.add(topMiddle);
			topRight = new JRadioButton();
			topRight.setOpaque(false);
			topRight.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (topRight.isSelected()) {
						getModel().setVerticalAlignement(IETDWidget.VALIGN_TOP);
						getModel().setAlignement(IETDWidget.ALIGN_RIGHT);
					}
				}

			});
			top.add(topRight);
			add(top);

			// MIDDLE PANEL
			JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 4));
			middle.setOpaque(false);
			middleLeft = new JRadioButton();
			middleLeft.setOpaque(false);
			middleLeft.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (middleLeft.isSelected()) {
						getModel().setVerticalAlignement(IETDWidget.VALIGN_MIDDLE);
						getModel().setAlignement(IETDWidget.ALIGN_LEFT);
					}
				}

			});
			middle.add(middleLeft);
			middleMiddle = new JRadioButton();
			middleMiddle.setOpaque(false);
			middleMiddle.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (middleMiddle.isSelected()) {
						getModel().setVerticalAlignement(IETDWidget.VALIGN_MIDDLE);
						getModel().setAlignement(IETDWidget.ALIGN_CENTER);
					}
				}

			});
			middle.add(middleMiddle);
			middleRight = new JRadioButton();
			middleRight.setOpaque(false);
			middleRight.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (middleRight.isSelected()) {
						getModel().setVerticalAlignement(IETDWidget.VALIGN_MIDDLE);
						getModel().setAlignement(IETDWidget.ALIGN_RIGHT);
					}
				}

			});
			middle.add(middleRight);
			add(middle);

			// BOTTOM PANEL
			JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
			bottom.setOpaque(false);
			bottomLeft = new JRadioButton();
			bottomLeft.setOpaque(false);
			bottomLeft.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (bottomLeft.isSelected()) {
						getModel().setVerticalAlignement(IETDWidget.VALIGN_BOTTOM);
						getModel().setAlignement(IETDWidget.ALIGN_LEFT);
					}
				}

			});
			bottom.add(bottomLeft);
			bottomMiddle = new JRadioButton();
			bottomMiddle.setOpaque(false);
			bottomMiddle.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (bottomMiddle.isSelected()) {
						getModel().setVerticalAlignement(IETDWidget.VALIGN_BOTTOM);
						getModel().setAlignement(IETDWidget.ALIGN_CENTER);
					}
				}

			});
			bottom.add(bottomMiddle);
			bottomRight = new JRadioButton();
			bottomRight.setOpaque(false);
			bottomRight.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (bottomRight.isSelected()) {
						getModel().setVerticalAlignement(IETDWidget.VALIGN_BOTTOM);
						getModel().setAlignement(IETDWidget.ALIGN_RIGHT);
					}
				}

			});
			bottom.add(bottomRight);
			add(bottom);
			bg.add(topLeft);
			bg.add(topMiddle);
			bg.add(topRight);
			bg.add(middleLeft);
			bg.add(middleMiddle);
			bg.add(middleRight);
			bg.add(bottomLeft);
			bg.add(bottomMiddle);
			bg.add(bottomRight);
			Color c = UIManager.getLookAndFeelDefaults().getColor("TabbedPane.contentAreaColor");
			if (c == null)
				c = ColorCst.SELECTED_LINES_TABULAR_VIEW_COLOR;
			Color bColor = UIManager.getLookAndFeelDefaults().getColor("Tree.selectionBorderColor");
			if (bColor == null)
				bColor = new Color(99, 130, 191);
			TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(c));
			b.setTitle(FlexoLocalization.localizedForKey("cell", b));
			b.setTitleFont(DenaliWidget.DEFAULT_LABEL_FONT);
			b.setTitleColor(bColor);
			setBorder(b);
		}

		protected void apply() {
			if (topLeft.isSelected()) {
				getModel().setVerticalAlignement(IETDWidget.VALIGN_TOP);
				getModel().setAlignement(IETDWidget.ALIGN_LEFT);
			} else if (topMiddle.isSelected()) {
				getModel().setVerticalAlignement(IETDWidget.VALIGN_TOP);
				getModel().setAlignement(IETDWidget.ALIGN_CENTER);
			} else if (topRight.isSelected()) {
				getModel().setVerticalAlignement(IETDWidget.VALIGN_TOP);
				getModel().setAlignement(IETDWidget.ALIGN_RIGHT);
			} else if (middleLeft.isSelected()) {
				getModel().setVerticalAlignement(IETDWidget.VALIGN_MIDDLE);
				getModel().setAlignement(IETDWidget.ALIGN_LEFT);
			} else if (middleMiddle.isSelected()) {
				getModel().setVerticalAlignement(IETDWidget.VALIGN_MIDDLE);
				getModel().setAlignement(IETDWidget.ALIGN_CENTER);
			} else if (middleRight.isSelected()) {
				getModel().setVerticalAlignement(IETDWidget.VALIGN_MIDDLE);
				getModel().setAlignement(IETDWidget.ALIGN_RIGHT);
			} else if (bottomLeft.isSelected()) {
				getModel().setVerticalAlignement(IETDWidget.VALIGN_BOTTOM);
				getModel().setAlignement(IETDWidget.ALIGN_LEFT);
			} else if (bottomMiddle.isSelected()) {
				getModel().setVerticalAlignement(IETDWidget.VALIGN_BOTTOM);
				getModel().setAlignement(IETDWidget.ALIGN_CENTER);
			} else if (bottomRight.isSelected()) {
				getModel().setVerticalAlignement(IETDWidget.VALIGN_BOTTOM);
				getModel().setAlignement(IETDWidget.ALIGN_RIGHT);
			} else if (logger.isLoggable(Level.WARNING))
				logger.warning("There are no checkboxes selected or somebody just messed up something here ;-)");
		}

		protected void update() {
			if (IETDWidget.VALIGN_TOP.equals(getModel().getVerticalAlignement())) {
				if (IETDWidget.ALIGN_RIGHT.equals(getModel().getAlignement()))
					topRight.setSelected(true);
				else if (IETDWidget.ALIGN_CENTER.equals(getModel().getAlignement()))
					topMiddle.setSelected(true);
				else
					topLeft.setSelected(true);
			} else if (IETDWidget.VALIGN_BOTTOM.equals(getModel().getVerticalAlignement())) {
				if (IETDWidget.ALIGN_RIGHT.equals(getModel().getAlignement()))
					bottomRight.setSelected(true);
				else if (IETDWidget.ALIGN_CENTER.equals(getModel().getAlignement()))
					bottomMiddle.setSelected(true);
				else
					bottomLeft.setSelected(true);
			} else {
				if (IETDWidget.ALIGN_RIGHT.equals(getModel().getAlignement()))
					middleRight.setSelected(true);
				else if (IETDWidget.ALIGN_CENTER.equals(getModel().getAlignement()))
					middleMiddle.setSelected(true);
				else
					middleLeft.setSelected(true);
			}

		}
	}

}
