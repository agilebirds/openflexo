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
package org.openflexo.fib.view.container;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.ComponentConstraints;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.GridLayoutConstraints;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.toolbox.StringUtils;

public class FIBPanelView<C extends FIBPanel> extends FIBContainerView<C, JPanel> {

	private static final Logger logger = Logger.getLogger(FIBPanelView.class.getPackage().getName());

	private JPanel panel;

	public FIBPanelView(C model, FIBController controller) {
		super(model, controller);

		updateBorder();
	}

	@Override
	public void updateGraphicalProperties() {
		super.updateGraphicalProperties();
		panel.setOpaque(getComponent().getOpaque());
		panel.setBackground(getComponent().getBackgroundColor());
	}

	public void updateBorder() {
		switch (getComponent().getBorder()) {
		case empty:
			panel.setBorder(BorderFactory.createEmptyBorder(getComponent().getBorderTop() != null ? getComponent().getBorderTop() : 0,
					getComponent().getBorderLeft() != null ? getComponent().getBorderLeft() : 0,
					getComponent().getBorderBottom() != null ? getComponent().getBorderBottom() : 0,
					getComponent().getBorderRight() != null ? getComponent().getBorderRight() : 0));
			break;
		case etched:
			panel.setBorder(BorderFactory.createEtchedBorder());
			break;
		case line:
			panel.setBorder(BorderFactory.createLineBorder(getComponent().getBorderColor() != null ? getComponent().getBorderColor()
					: Color.black));
			break;
		case lowered:
			panel.setBorder(BorderFactory.createLoweredBevelBorder());
			break;
		case raised:
			panel.setBorder(BorderFactory.createRaisedBevelBorder());
			break;
		case titled:
			panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getLocalized(getComponent()
					.getBorderTitle()), TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, getComponent().retrieveValidFont(),
					getComponent().retrieveValidForegroundColor()));
			break;
		case rounded3d:
			panel.setBorder(new RoundedBorder(StringUtils.isNotEmpty(getComponent().getBorderTitle()) ? getLocalized(getComponent()
					.getBorderTitle()) : null, getComponent().getBorderTop() != null ? getComponent().getBorderTop() : 0, getComponent()
					.getBorderLeft() != null ? getComponent().getBorderLeft() : 0,
					getComponent().getBorderBottom() != null ? getComponent().getBorderBottom() : 0,
					getComponent().getBorderRight() != null ? getComponent().getBorderRight() : 0, getComponent().getTitleFont(),
					getComponent().retrieveValidForegroundColor(), getComponent().getDarkLevel()));
			break;
		default:
			break;
		}
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateBorder();
	}

	private void _setPanelLayoutParameters() {
		switch (getComponent().getLayout()) {
		case none:
			panel.setLayout(null);
			break;
		case flow:
			panel.setLayout(new FlowLayout(getComponent().getFlowAlignment().getAlign(), getComponent().getHGap(), getComponent().getVGap()));
			break;
		case border:
			panel.setLayout(new BorderLayout());
			break;
		case grid:
			// logger.info("rows="+getComponent().getRows()+" cols="+getComponent().getCols());
			panel.setLayout(new GridLayout(getComponent().getRows(), getComponent().getCols(), getComponent().getHGap(), getComponent()
					.getVGap()));
			break;
		case box:
			panel.setLayout(new BoxLayout(panel, getComponent().getBoxLayoutAxis().getAxis()));
			break;
		case twocols:
			panel.setLayout(new GridBagLayout());
			break;
		case gridbag:
			panel.setLayout(new GridBagLayout());
			break;
		default:
			break;
		}
	}

	@Override
	protected JPanel createJComponent() {
		panel = new JPanel();
		updateGraphicalProperties();

		_setPanelLayoutParameters();

		return panel;
	}

	@Override
	public synchronized void updateLayout() {
		if (getSubViews() != null) {
			for (FIBView v : getSubViews()) {
				v.delete();
			}
		}
		getResultingJComponent().removeAll();
		_setPanelLayoutParameters();
		buildSubComponents();
		updateDataObject(getDataObject());
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		Vector<FIBComponent> allSubComponents = new Vector<FIBComponent>();
		allSubComponents.addAll(getComponent().getSubComponents());
		// Vector<FIBComponent> allSubComponents = getComponent().getSubComponents();

		if (getComponent().getLayout() == Layout.flow || getComponent().getLayout() == Layout.box
				|| getComponent().getLayout() == Layout.twocols || getComponent().getLayout() == Layout.gridbag) {

			Collections.sort(allSubComponents, new Comparator<FIBComponent>() {
				@Override
				public int compare(FIBComponent o1, FIBComponent o2) {
					ComponentConstraints c1 = o1.getConstraints();
					ComponentConstraints c2 = o2.getConstraints();
					return c1.getIndex() - c2.getIndex();
				}
			});

			int i = 0;
			for (FIBComponent subComponent : allSubComponents) {
				subComponent.getConstraints().setIndexNoNotification(i);
				i++;
			}

			/*System.out.println("Apres le retrieve: ");
			for (FIBComponent c : allSubComponents) {
				if (c.getConstraints() != null) {
					if (!c.getConstraints().hasIndex()) {
						System.out.println("> Index: ? "+c);
					}
					else {
						System.out.println("> Index: "+c.getConstraints().getIndex()+" "+c);
					}
				}
			}

			System.out.println("*********************************************");*/

		}

		if (getComponent().getLayout() == Layout.grid) {

			for (FIBComponent subComponent : getComponent().getSubComponents()) {
				FIBView subView = getController().buildView(subComponent);
				registerViewForComponent(subView, subComponent);
			}

			for (int i = 0; i < getComponent().getRows(); i++) {
				for (int j = 0; j < getComponent().getCols(); j++) {
					registerComponentWithConstraints(_getJComponent(j, i), null);
				}
			}
		}

		else {
			for (FIBComponent subComponent : allSubComponents) {
				FIBView subView = getController().buildView(subComponent);
				registerViewForComponent(subView, subComponent);
				registerComponentWithConstraints(subView.getResultingJComponent(), subComponent.getConstraints());
			}
		}
	}

	@Override
	protected void addJComponent(JComponent c) {

		Object constraint = getConstraints().get(c);

		if (constraint instanceof ComponentConstraints) {
			((ComponentConstraints) constraint).performConstrainedAddition(getJComponent(), c);
		} else {
			super.addJComponent(c);
		}
	}

	@Override
	public JPanel getJComponent() {
		return panel;
	}

	// Special case for GridLayout
	protected JComponent _getJComponent(int col, int row) {
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			GridLayoutConstraints glc = (GridLayoutConstraints) subComponent.getConstraints();
			if (glc.getX() == col && glc.getY() == row) {
				return getController().viewForComponent(subComponent).getResultingJComponent();
			}
		}
		// Otherwise, it's an empty cell
		JPanel returned = new JPanel();
		returned.setOpaque(false);
		return returned;

	}

	@Override
	public void delete() {
		super.delete();
	}

}
