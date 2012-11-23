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
package org.openflexo.wkf.controller.action;

import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.ShowExecutionControlGraphs;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.wkf.controller.WKFController;

public class ShowExecutionControlGraphsInitializer extends ActionInitializer<ShowExecutionControlGraphs, WKFObject, WKFObject> {

	private static final Logger logger = Logger.getLogger(ShowExecutionControlGraphsInitializer.class.getPackage().getName());

	ShowExecutionControlGraphsInitializer(WKFControllerActionInitializer actionInitializer) {
		super(ShowExecutionControlGraphs.actionType, actionInitializer);
		if (isControlGraphComputationAvailable()) {
			Method initMethod;
			try {
				initMethod = controlGraphFactoriesClass.getMethod("init", (Class[]) null);
				initMethod.invoke(controlGraphFactoriesClass, (Object[]) null);
			} catch (Exception e) {
				logger.warning("Unexpected exception : " + e);
				e.printStackTrace();
			}
		}
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ShowExecutionControlGraphs> getDefaultInitializer() {
		return new FlexoActionInitializer<ShowExecutionControlGraphs>() {
			@Override
			public boolean run(EventObject e, ShowExecutionControlGraphs action) {
				// try{
				// if(action.getFocusedObject() instanceof AbstractActivityNode &&
				// ((AbstractActivityNode)action.getFocusedObject()).isBeginNode()){
				// //StateDiagramBuilder stateDiagram = new StateDiagramBuilder((AbstractActivityNode)action.getFocusedObject());
				// System.out.println(stateDiagram.toString());
				// }
				// } catch(Exception ex){ex.printStackTrace();}
				//
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ShowExecutionControlGraphs> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ShowExecutionControlGraphs>() {
			@Override
			public boolean run(EventObject e, ShowExecutionControlGraphs action) {
				/*
				 * if (action.getFocusedObject() instanceof OperationNode) { OperationNode operationNode = action.getFocusedObject(); if
				 * (operationNode.getComponentInstance().getComponentDefinition() != null) { if (Module.IE_MODULE.isAvailable()) {
				 * ExternalIEModule ieModule = ModuleLoader.getIEModule(); if (ieModule==null) return false; ieModule.focusOn();
				 * ieModule.showScreenInterface(operationNode.getComponentInstance()); if (ToolBox.getPLATFORM() != ToolBox.MACOS)
				 * SwingUtilities.invokeLater(new SwitchToIEJob()); } else { if (logger.isLoggable(Level.WARNING))
				 * logger.warning("Could not launch Interface Editor: IE Module not available !"); } } } return false;
				 */
				logger.info("Viewing control flow graph for " + action.getFocusedObject());
				((WKFController) getController()).showControlGraphViewer();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionEnableCondition getEnableCondition() {
		return new FlexoActionEnableCondition<ShowExecutionControlGraphs, WKFObject, WKFObject>() {
			@Override
			public boolean isEnabled(FlexoActionType<ShowExecutionControlGraphs, WKFObject, WKFObject> actionType, WKFObject object,
					Vector<WKFObject> globalSelection, FlexoEditor editor) {
				return isControlGraphComputationAvailable();
			}
		};
	}

	private boolean isControlGraphComputationAvailable = false;
	private boolean isControlGraphComputationAvailabilityAlreadyComputed = false;
	private Class controlGraphFactoriesClass = null;

	protected boolean isControlGraphComputationAvailable() {
		if (!isControlGraphComputationAvailabilityAlreadyComputed) {
			isControlGraphComputationAvailabilityAlreadyComputed = true;
			try {
				controlGraphFactoriesClass = Class.forName("org.openflexo.foundation.exec.ControlGraphFactories");
			} catch (ClassNotFoundException e) {
				logger.info("Class org.openflexo.foundation.exec.ControlGraphFactories not found");
			}
			isControlGraphComputationAvailable = controlGraphFactoriesClass != null;
		}
		return isControlGraphComputationAvailable;
	}

	/*
	 * private class SwitchToIEJob implements Runnable {
	 * 
	 * SwitchToIEJob() { }
	 * 
	 * public void run() { if (logger.isLoggable(Level.INFO)) logger.info("Switching to IE"); ModuleLoader.switchToModule(Module.IE_MODULE);
	 * if (logger.isLoggable(Level.INFO)) logger.info("Switched to IE done!"); } }
	 */

}
