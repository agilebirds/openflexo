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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.ws.DeletePort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.localization.FlexoLocalization;

public class AddPort extends FlexoAction<AddPort,WKFObject,WKFObject> 
{

	private static final Logger logger = Logger.getLogger(AddPort.class.getPackage().getName());

	public static final String NEW_PORT = "new_port";
	public static final String DELETE_PORT = "delete_port";
	public static final String IN_PORT = "in_port";
	public static final String IN_OUT_PORT = "in_out_port";
	public static final String OUT_PORT = "out_port";

	public static enum CreatedPortType implements StringConvertable
	{
		NEW_PORT,
		DELETE_PORT,
		IN_PORT,
		OUT_PORT,
		IN_OUT_PORT;

		public String getUnlocalizedStringRepresentation() 
		{
			if (this == NEW_PORT) return "new_instance_port";
			else if (this == DELETE_PORT) return "delete_instance_port";
			else if (this == IN_PORT) return "in_port";
			else if (this == OUT_PORT) return "out_port";
			else if (this == IN_OUT_PORT) return "in_out_port";
			return "???";
		}

		public String getStringRepresentation()
		{
			return FlexoLocalization.localizedForKey(getUnlocalizedStringRepresentation());
		}

		@Override
		public StringEncoder.Converter getConverter()
		{
			return portTypeConverter;
		}
		public static final StringEncoder.Converter portTypeConverter = new Converter<CreatedPortType>(CreatedPortType.class) {

			@Override
			public CreatedPortType convertFromString(String value)
			{
				for (CreatedPortType cs : values()) {
					if (cs.getStringRepresentation().equals(value)) return cs;
				}
				return null;
			}

			@Override
			public String convertToString(CreatedPortType value)
			{
				return value.getStringRepresentation();
			}

		};

	}

	public static class AddPortActionType extends FlexoActionType<AddPort,WKFObject,WKFObject>
	{
		private CreatedPortType _type;

		protected AddPortActionType (
				String actionName, CreatedPortType type)
		{
			super(actionName,FlexoActionType.newMenu,
					FlexoActionType.newMenuGroup4,
					FlexoActionType.ADD_ACTION_TYPE);
			_type = type;
		}

		/**
		 * Factory method
		 */
		 @Override
		public AddPort makeNewAction(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) 
		 {
			 return new AddPort(this,focusedObject, globalSelection,editor);
		 }

		 @Override
		protected boolean isVisibleForSelection(WKFObject focusedObject, Vector<WKFObject> globalSelection) 
		 {
			 return isEnabledForSelection(focusedObject,globalSelection);
		 }

		 @Override
		protected boolean isEnabledForSelection(WKFObject focusedObject, Vector<WKFObject> globalSelection) 
		 {
			 return focusedObject != null 
					  && !focusedObject.getProcess().isImported();
		 }

		 public CreatedPortType getCreatedNodeType() 
		 {
			 return _type;
		 }

	}

	public static final AddPortActionType createPort = new AddPortActionType("create_new_port...",null);

	public static final AddPortActionType createNewPort = new AddPortActionType("create_new_instance_port",CreatedPortType.NEW_PORT);
	public static final AddPortActionType createDeletePort = new AddPortActionType("create_delete_instance_port",CreatedPortType.DELETE_PORT);
	public static final AddPortActionType createInPort = new AddPortActionType("create_in_port",CreatedPortType.IN_PORT);
	public static final AddPortActionType createOutPort = new AddPortActionType("create_out_port",CreatedPortType.OUT_PORT);
	public static final AddPortActionType createInOutPort = new AddPortActionType("create_in_out_port",CreatedPortType.IN_OUT_PORT);

	static {
		FlexoModelObject.addActionForClass (createPort, FlexoProcess.class);
		FlexoModelObject.addActionForClass (createNewPort, FlexoProcess.class);
		FlexoModelObject.addActionForClass (createDeletePort, FlexoProcess.class);
		FlexoModelObject.addActionForClass (createInPort, FlexoProcess.class);
		FlexoModelObject.addActionForClass (createOutPort, FlexoProcess.class);
		FlexoModelObject.addActionForClass (createInOutPort, FlexoProcess.class);

		FlexoModelObject.addActionForClass (createPort, PortRegistery.class);
		FlexoModelObject.addActionForClass (createNewPort, PortRegistery.class);
		FlexoModelObject.addActionForClass (createDeletePort, PortRegistery.class);
		FlexoModelObject.addActionForClass (createInPort, PortRegistery.class);
		FlexoModelObject.addActionForClass (createOutPort, PortRegistery.class);
		FlexoModelObject.addActionForClass (createInOutPort, PortRegistery.class);

		FlexoModelObject.addActionForClass (createPort, FlexoPort.class);
		FlexoModelObject.addActionForClass (createNewPort, FlexoPort.class);
		FlexoModelObject.addActionForClass (createDeletePort, FlexoPort.class);
		FlexoModelObject.addActionForClass (createInPort, FlexoPort.class);
		FlexoModelObject.addActionForClass (createOutPort, FlexoPort.class);
		FlexoModelObject.addActionForClass (createInOutPort, FlexoPort.class);
	}



	private CreatedPortType _newPortType;
	private String _newPortName;
	private FlexoPort _newPort;

	private double posX = -1;
	private double posY = -1;
	private boolean editNodeLabel = false;

	private String graphicalContext; // eg BPE, SWL ...

	AddPort (AddPortActionType actionType, WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
		_newPortType = actionType.getCreatedNodeType();
	}

	public FlexoProcess getProcess() 
	{
		if (getFocusedObject() != null)  {
			return (getFocusedObject()).getProcess();
		}
		return null;
	}

	public CreatedPortType getNewPortType() 
	{
		return _newPortType;
	}

	public void setNewPortType(CreatedPortType newPortType) 
	{
		if (newPortType != _newPortType) _newPortName = null; // Reset name if type changed
		_newPortType = newPortType;
	}

	private void setPositionWhenRequired()
	{
		if (getGraphicalContext() != null) {
			_newPort.setX(posX, getGraphicalContext());
			_newPort.setY(posY, getGraphicalContext());
			_newPort.setLabelX(25, getGraphicalContext());
			_newPort.setLabelY(55, getGraphicalContext());
		}        		
	}

	@Override
	protected void doAction(Object context) 
	{
		logger.info ("Add port");
		if ((getProcess() != null) && (!getProcess().isImported())) {
			if (getNewPortType() == CreatedPortType.NEW_PORT) {
				_newPort = new NewPort(getProcess(), getNewPortName());
				setPositionWhenRequired();
				getProcess().getPortRegistery().addToNewPorts((NewPort) _newPort);
			}
			else if (getNewPortType() == CreatedPortType.DELETE_PORT) {
				_newPort = new DeletePort(getProcess(), getNewPortName());
				setPositionWhenRequired();
				getProcess().getPortRegistery().addToDeletePorts((DeletePort) _newPort);
			}
			else if (getNewPortType() == CreatedPortType.IN_PORT) {
				_newPort = new InPort(getProcess(), getNewPortName());
				setPositionWhenRequired();
				getProcess().getPortRegistery().addToInPorts((InPort) _newPort);
			}
			else if (getNewPortType() == CreatedPortType.IN_OUT_PORT) {
				_newPort = new InOutPort(getProcess(), getNewPortName());
				setPositionWhenRequired();
				getProcess().getPortRegistery().addToInOutPorts((InOutPort) _newPort);
			}
			else if (getNewPortType() == CreatedPortType.OUT_PORT) {
				_newPort = new OutPort(getProcess(), getNewPortName());
				setPositionWhenRequired();
				getProcess().getPortRegistery().addToOutPorts((OutPort) _newPort);
			}
			else {
				logger.warning("Invalid port type !");
				return;
			}

			_newPort.getPortRegistery().setIsVisible(true);
		}
		else {
			logger.warning("Focused process is null !");
		}
	}

	public FlexoPort getNewPort() 
	{
		return _newPort;
	}

	public String getNewPortName() 
	{
		if (_newPortName == null && getFocusedObject() != null) {
			if (getNewPortType() == CreatedPortType.NEW_PORT) {
				return getProcess().findNextInitialName("NEW_PORT", NewPort.getDefaultInitialName());
			}
			else if (getNewPortType() == CreatedPortType.DELETE_PORT) {
				return getProcess().findNextInitialName("DELETE_PORT", DeletePort.getDefaultInitialName());
			}
			else if (getNewPortType() == CreatedPortType.IN_PORT) {
				return getProcess().findNextInitialName("IN_PORT", InPort.getDefaultInitialName());
			}
			else if (getNewPortType() == CreatedPortType.IN_OUT_PORT) {
				return getProcess().findNextInitialName("IN_OUT_PORT", InOutPort.getDefaultInitialName());
			}
			else if (getNewPortType() == CreatedPortType.OUT_PORT) {
				return getProcess().findNextInitialName("OUT_PORT", OutPort.getDefaultInitialName());
			}
			else {
				return FlexoPort.getDefaultInitialName();
			}
		}
		return _newPortName;
	}

	public void setNewPortName(String newPortName) 
	{
		_newPortName = newPortName;
		newPortNameInitialized = true;
	}

	private boolean newPortNameInitialized = false;

	public boolean isNewPortNameInitialized()
	{
		return newPortNameInitialized;
	}

	/*private Point newNodeLocationInParentContainer;

	public Point getNewNodeLocationInParentContainer()
	{
		return newNodeLocationInParentContainer;
	}

	public void setNewNodeLocationInParentContainer(Point newNodeLocationInParentContainer) 
	{
		this.newNodeLocationInParentContainer = newNodeLocationInParentContainer;
	}*/

	public double getPosX()
	{
		return posX;
	}

	public void setPosX(double posX)
	{
		this.posX = posX;
		hasBeenLocated = true;
	}

	public double getPosY()
	{
		return posY;
	}

	public void setPosY(double posY)
	{
		this.posY = posY;
		hasBeenLocated = true;
	}

	public void setLocation (double posX, double posY)
	{
		setPosX(posX);
		setPosY(posY);
	}

	private boolean hasBeenLocated = false;

	public boolean hasBeenLocated()
	{
		return hasBeenLocated;
	}
	
	
	public boolean getEditNodeLabel()
	{
		return editNodeLabel;
	}

	public void setEditNodeLabel(boolean editNodeLabel)
	{
		this.editNodeLabel = editNodeLabel;
	}

	public String getGraphicalContext() {
		return graphicalContext;
	}

	public void setGraphicalContext(String graphicalContext) {
		this.graphicalContext = graphicalContext;
	}



}
