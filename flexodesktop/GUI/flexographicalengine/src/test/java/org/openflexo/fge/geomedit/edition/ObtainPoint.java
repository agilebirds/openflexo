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

import java.awt.Color;
import java.awt.event.MouseEvent;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.construction.ControlPointReference;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.LineIntersectionPointConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.geomedit.construction.PointReference;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.xmlcode.StringEncoder;


public class ObtainPoint extends EditionInput<FGEPoint> 
{
	public static int preferredMethodIndex = 0;

	private boolean endOnRightClick = false;
	
	public ObtainPoint(String anInputLabel, GeomEditController controller) 
	{
		super(anInputLabel, controller);

		availableMethods.add(new CursorSelection());
		availableMethods.add(new ControlPointSelection());
		availableMethods.add(new IntersectionSelection());
		availableMethods.add(new KeyboardSelection());
	}

	public ObtainPoint(String anInputLabel, GeomEditController controller, boolean appendEndSelection) 
	{
		this(anInputLabel,controller);
		if (appendEndSelection) availableMethods.add(new EndEditionSelection());
		endOnRightClick = appendEndSelection;
	}
	
	@Override
	protected int getPreferredMethodIndex()
	{
		return preferredMethodIndex;
	}

	@Override
	public void setActiveMethod(EditionInputMethod aMethod)
	{
		super.setActiveMethod(aMethod);
		preferredMethodIndex = availableMethods.indexOf(aMethod);
	}

	public class CursorSelection extends EditionInputMethod<FGEPoint,ObtainPoint> {

		public CursorSelection() {
			super("From cursor", ObtainPoint.this);
		}		

		@Override
		public void mouseClicked(MouseEvent e)
		{
			setConstruction(new ExplicitPointConstruction(getPointLocation(e)));
			done();
			if (endOnRightClick && e.getButton() == MouseEvent.BUTTON3) {
				endEdition();
			}
		}

		@Override
		public InputComponent getInputComponent()
		{
			return new InputComponentButton(CursorSelection.this);
		}

	}

	public class ControlPointSelection extends EditionInputMethod<FGEPoint,ObtainPoint> {

		private ControlPoint focusedControlPoint;
		private GeometricGraphicalRepresentation focusedObject;

		public ControlPointSelection() {
			super("As control point", ObtainPoint.this);
		}		

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (focusedControlPoint != null) {
				if (focusedObject != null) focusedObject.setIsFocused(false);
				if (focusedControlPoint instanceof DraggableControlPoint) {
					setConstruction(new PointReference(((DraggableControlPoint)focusedControlPoint).getExplicitPointConstruction()));
					done();
				}
				else if (focusedControlPoint instanceof ComputedControlPoint){
					setConstruction(new ControlPointReference(
							((ComputedControlPoint)focusedControlPoint).getGraphicalRepresentation(),
							((ComputedControlPoint)focusedControlPoint).getName()));
					done();
				}
				else {
					System.err.println("Don't know what to do with a "+focusedControlPoint);
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			GraphicalRepresentation focused = getFocusRetriever().getFocusedObject(e);

			if (focusedObject != null && focusedObject != focused) {
				focusedObject.setIsFocused(false);
			}

			if (focused instanceof GeometricGraphicalRepresentation) {
				focusedObject = (GeometricGraphicalRepresentation)focused;
				focusedObject.setIsFocused(true);
			}

			ControlArea<?> controlArea = (focused != null ? getFocusRetriever().getFocusedControlAreaForDrawable(focused, e) : null);
			if (controlArea instanceof ControlPoint)
				focusedControlPoint = (ControlPoint)controlArea;
			
			if (!(focusedControlPoint instanceof DraggableControlPoint 
					|| focusedControlPoint instanceof ComputedControlPoint))
				focusedControlPoint = null;
		}

		@Override
		public void paint(FGEDrawingGraphics graphics)
		{
			if (focusedControlPoint != null) {
				graphics.useForegroundStyle(ForegroundStyle.makeStyle(Color.RED));
				graphics.drawControlPoint(focusedControlPoint.getPoint(),FGEConstants.CONTROL_POINT_SIZE);
				graphics.drawRoundArroundPoint(focusedControlPoint.getPoint(),8);
			}
		}

		@Override
		public InputComponent getInputComponent()
		{
			return new InputComponentButton(ControlPointSelection.this);
		}


	}

	public class IntersectionSelection extends EditionInputMethod<FGEPoint,ObtainPoint> {

		public IntersectionSelection() {
			super("As intersection", ObtainPoint.this);
			addChildInput(new ObtainLine("Select first line",ObtainPoint.this.getController()));
			addChildInput(new ObtainLine("Select second line",ObtainPoint.this.getController()));
		}		

		@Override
		public void mouseClicked(MouseEvent e)
		{
			System.out.println("Mouse clicked for intersection selection");
		}

		@Override
		public InputComponent getInputComponent()
		{
			return new InputComponentButton(IntersectionSelection.this);
		}

		@Override
		public PointConstruction retrieveInputDataFromChildInputs()
		{
			((ObtainLine)childInputs.get(0)).getReferencedLine().getGraphicalRepresentation().setIsSelected(false);
			return new LineIntersectionPointConstruction(
					((ObtainLine)childInputs.get(0)).getConstruction(),
					((ObtainLine)childInputs.get(1)).getConstruction());
		}
		
		@Override
		public void paint(FGEDrawingGraphics graphics)
		{
			if (currentChildInputStep == 0) {
				//Nothing to draw
			}
			else if (currentChildInputStep == 1 
					&& ((ObtainLine)childInputs.get(0)).getReferencedLine() != null) {
				((ObtainLine)childInputs.get(0)).getReferencedLine().getGraphicalRepresentation().setIsSelected(true);
			}
		}


	}

	public class KeyboardSelection extends EditionInputMethod<FGEPoint,ObtainPoint> {

		private InputComponentTextField<FGEPoint> inputComponent;

		public KeyboardSelection() {
			super("As", ObtainPoint.this);
		}		

		@Override
		public void mouseClicked(MouseEvent e)
		{
			inputComponent.setData(getPointLocation(e));
		}

		@Override
		public InputComponent getInputComponent()
		{
			if (inputComponent == null) {
				inputComponent = new InputComponentTextField<FGEPoint>(KeyboardSelection.this,new FGEPoint(0,0)) {

					private StringEncoder.Converter<FGEPoint> converter ;

					private StringEncoder.Converter<FGEPoint> getConverter()
					{
						if (converter == null) {
							converter = StringEncoder.getDefaultInstance()._converterForClass(FGEPoint.class);
						}
						return converter;
					}

					@Override
					public int getColumnSize()
					{
						return 8;
					}

					@Override
					public String convertDataToString(FGEPoint data)
					{
						if (data == null) return "";
						return getConverter().convertToString(data);
					}

					@Override
					public FGEPoint convertStringToData(String string)
					{
						if (string == null || string.trim().equals("")) return null;
						return getConverter().convertFromString(string);
					}

					@Override
					public void dataEntered(FGEPoint data)
					{
						setConstruction(new ExplicitPointConstruction(data));
						done();
					}

				};
			}
			return inputComponent;
		}

	}

	@Override
	public void paint(FGEDrawingGraphics graphics)
	{
		super.paint(graphics);
		if ((getActiveMethod() instanceof ControlPointSelection) 
				|| (getActiveMethod() instanceof IntersectionSelection)) {
			getActiveMethod().paint(graphics);
		}
	}
	
	@Override
	public PointConstruction getConstruction()
	{
		return (PointConstruction)super.getConstruction();
	}

	@Override
	public boolean endOnRightClick()
	{
		return endOnRightClick;
	}
	

}