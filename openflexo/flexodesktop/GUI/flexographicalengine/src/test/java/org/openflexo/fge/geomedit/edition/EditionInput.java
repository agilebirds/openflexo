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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.construction.GeometricConstruction;
import org.openflexo.fge.geomedit.edition.EditionInputMethod.InputComponent;
import org.openflexo.fge.graphics.FGEDrawingGraphics;


public abstract class EditionInput<O extends Object> 
{
	private String inputLabel;

	public Vector<EditionInputMethod> availableMethods;
	public EditionInputMethod activeMethod;
	
	private GeomEditController controller;

	private GeometricConstruction<O> contruction;
	
	public EditionInput(String anInputLabel, GeomEditController aController) 
	{
		super();
		controller = aController;
		inputLabel = anInputLabel;
		availableMethods = new Vector<EditionInputMethod>();
		activeMethod = null;
	}
	
	protected abstract int getPreferredMethodIndex();
	
	private JPanel subPanel;
	
	public void resetControlPanel(JPanel controlPanel)
	{
		if (subPanel != null) {
			controlPanel.remove(subPanel);
			subPanel = null;
		}
	}
	
	public void updateControlPanel (JPanel controlPanel, JPanel availableMethodsPanel)
	{
		availableMethodsPanel.removeAll();
		if (activeMethod == null && getPreferredMethodIndex() < availableMethods.size()) 
			activeMethod = availableMethods.get(getPreferredMethodIndex());
		availableMethodsPanel.add(new JLabel(inputLabel));
		for (final EditionInputMethod method : availableMethods) {
			InputComponent inputComponent = method.getInputComponent();
			availableMethodsPanel.add((JComponent)inputComponent);
			if (method == getActiveMethod()) {
				inputComponent.enableInputComponent();
			}
			else {
				inputComponent.disableInputComponent();
			}
		}
		if (getActiveMethod().hasChildInputs()) {
			subPanel = new JPanel(new BorderLayout());
			JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
			subPanel.add(flowPanel,BorderLayout.WEST);
			controlPanel.add(subPanel,BorderLayout.SOUTH);
			getActiveMethod().getCurrentInput().updateControlPanel(subPanel, flowPanel);	
		}
		else if (subPanel != null) {
			controlPanel.remove(subPanel);
			subPanel = null;
		}
		availableMethodsPanel.revalidate();
		availableMethodsPanel.repaint();
	}

	public String getInputLabel()
	{
		return inputLabel;
	}
	
	public String getActiveMethodLabel()
	{
		if (getActiveMethod() != null)
			return getActiveMethod().getMethodLabel();
		return "No active selection method";
	}
	
	public EditionInputMethod getActiveMethod()
	{
		return activeMethod;
	}

	public void setActiveMethod(EditionInputMethod aMethod)
	{
		if (activeMethod != aMethod) {
			activeMethod = aMethod;
			controller.updateCurrentInput();
		}
	}

	public EditionInputMethod getDerivedActiveMethod()
	{
		if (activeMethod.hasChildInputs()) {
			return activeMethod.getCurrentInput().getDerivedActiveMethod();
		}
		else {
			return activeMethod;
		}
	}

	public GeomEditController getController()
	{
		return controller;
	}

	private O inputData;
	
	public O getInputData()
	{
		if (contruction != null)
			return contruction.getData();
		return inputData;
	}

	public void setInputData(O data)
	{
		inputData = data;
	}

	public void setConstruction(GeometricConstruction<O> aContruction)
	{
		contruction = aContruction;
	}
	
	public GeometricConstruction<? extends O> getConstruction()
	{
		return contruction;
	}
	
	public void done()
	{
		if (getParentInputMethod() != null) {
			getParentInputMethod().nextChildInput();
		}
		else {
			getController().currentInputGiven();
		}
	}
	
	public void endEdition()
	{
	}
	
	public void paint(FGEDrawingGraphics graphics)
	{
	}

	private EditionInputMethod parentInputMethod = null;

	public EditionInputMethod getParentInputMethod()
	{
		return parentInputMethod;
	}

	public void setParentInputMethod(EditionInputMethod aMethod)
	{
		this.parentInputMethod = aMethod;
	}

	public class EndEditionSelection extends EditionInputMethod<O,EditionInput<O>> {

		public EndEditionSelection() {
			super("Done", EditionInput.this);
		}		

		@Override
		public void mouseClicked(MouseEvent e)
		{
			endEdition();
		}

		@Override
		public InputComponent getInputComponent()
		{
			return new EndEditionButton(this);
		}

	}

	
	public class EndEditionButton extends JButton implements InputComponent
	{
		public  EndEditionButton (final EndEditionSelection method)
		{
			super(method.getMethodLabel());
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					method.getEditionInput().endEdition();
				}
			});
		}

		@Override
		public void enableInputComponent()
		{
			setSelected(true);
		}

		@Override
		public void disableInputComponent()
		{
			setSelected(false);
		}
	}

	public abstract boolean endOnRightClick();
	



}