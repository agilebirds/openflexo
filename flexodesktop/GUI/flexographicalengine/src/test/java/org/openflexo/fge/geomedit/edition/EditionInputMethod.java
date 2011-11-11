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
package org.openflexo.fge.geomedit.edition;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.construction.GeometricConstruction;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.fge.view.listener.DrawingViewMouseListener;

public abstract class EditionInputMethod<O extends Object, I extends EditionInput<O>> extends DrawingViewMouseListener {
	private String methodLabel;
	private I editionInput;

	public EditionInputMethod(String aMethodLabel, I anInput) {
		super(anInput.getController().getDrawingGraphicalRepresentation(), anInput.getController().getDrawingView());
		methodLabel = aMethodLabel;
		editionInput = anInput;
	}

	public I getEditionInput() {
		return editionInput;
	}

	public String getMethodLabel() {
		return methodLabel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public FGEPoint getPointLocation(MouseEvent e) {
		Point ptInView = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getController().getDrawingView());
		FGEPoint returned = new FGEPoint();
		returned.x = (ptInView.x) / getController().getScale();
		returned.y = (ptInView.y) / getController().getScale();
		return returned;
	}

	public void paint(FGEDrawingGraphics graphics) {
	}

	public abstract InputComponent getInputComponent();

	public static interface InputComponent {
		public void enableInputComponent();

		public void disableInputComponent();
	}

	public static class InputComponentButton extends JButton implements InputComponent {
		public InputComponentButton(final EditionInputMethod method) {
			super(method.getMethodLabel());
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					method.getEditionInput().setActiveMethod(method);
				}
			});
		}

		@Override
		public void enableInputComponent() {
			setSelected(true);
		}

		@Override
		public void disableInputComponent() {
			setSelected(false);
		}
	}

	public static abstract class InputComponentTextField<O extends Object> extends JPanel implements InputComponent {
		private JTextField tf;
		private JButton activateButton;
		private JButton okButton;

		public InputComponentTextField(final EditionInputMethod<O, ? extends EditionInput<O>> method, O initData) {
			super(new FlowLayout(FlowLayout.CENTER, 5, 0));
			tf = new JTextField(getColumnSize());
			tf.setText(convertDataToString(initData));
			tf.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dataEntered(convertStringToData(tf.getText()));
				}
			});
			tf.setEnabled(false);
			activateButton = new JButton(method.getMethodLabel());
			activateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					method.getEditionInput().setActiveMethod(method);
				}
			});
			okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dataEntered(convertStringToData(tf.getText()));
				}
			});
			okButton.setEnabled(false);
			add(activateButton);
			add(tf);
			add(okButton);
		}

		@Override
		public void enableInputComponent() {
			activateButton.setSelected(true);
			tf.setEnabled(true);
			okButton.setEnabled(true);
		}

		@Override
		public void disableInputComponent() {
			activateButton.setSelected(false);
			tf.setEnabled(false);
			okButton.setEnabled(false);
		}

		public abstract int getColumnSize();

		public abstract String convertDataToString(O data);

		public abstract O convertStringToData(String string);

		public abstract void dataEntered(O data);

		public void setData(O data) {
			tf.setText(convertDataToString(data));
		}
	}

	public static abstract class InputComponentComboBox<E extends Enum> extends JPanel implements InputComponent {
		private JComboBox comboBox;
		private JButton activateButton;
		private JButton okButton;

		public InputComponentComboBox(final EditionInputMethod<E, ? extends EditionInput<E>> method, E initData) {
			super(new FlowLayout(FlowLayout.CENTER, 5, 0));
			comboBox = new JComboBox(initData.getDeclaringClass().getEnumConstants());
			comboBox.setSelectedItem(initData);
			comboBox.setEnabled(false);
			activateButton = new JButton(method.getMethodLabel());
			activateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					method.getEditionInput().setActiveMethod(method);
				}
			});
			okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dataEntered((E) comboBox.getSelectedItem());
				}
			});
			okButton.setEnabled(false);
			add(activateButton);
			add(comboBox);
			add(okButton);
		}

		@Override
		public void enableInputComponent() {
			activateButton.setSelected(true);
			comboBox.setEnabled(true);
			okButton.setEnabled(true);
		}

		@Override
		public void disableInputComponent() {
			activateButton.setSelected(false);
			comboBox.setEnabled(false);
			okButton.setEnabled(false);
		}

		public abstract void dataEntered(E data);

		public void setData(E data) {
			comboBox.setSelectedItem(data);
		}
	}

	private boolean hasChildInputs = false;
	public int currentChildInputStep;
	public Vector<EditionInput> childInputs;

	protected void addChildInput(EditionInput input) {
		hasChildInputs = true;
		currentChildInputStep = 0;
		if (childInputs == null)
			childInputs = new Vector<EditionInput>();
		childInputs.add(input);
		input.setParentInputMethod(this);
	}

	public boolean hasChildInputs() {
		return hasChildInputs;
	}

	public EditionInput getCurrentInput() {
		return childInputs.get(currentChildInputStep);
	}

	public boolean nextChildInput() {
		currentChildInputStep++;
		if (currentChildInputStep >= childInputs.size()) {
			getEditionInput().setConstruction(retrieveInputDataFromChildInputs());
			getEditionInput().done();
			// getEditionInput().getController().updateCurrentInput();
			return true;
		}
		getEditionInput().getController().updateCurrentInput();
		return false;
	}

	public GeometricConstruction<O> retrieveInputDataFromChildInputs() {
		return null;
	}

}