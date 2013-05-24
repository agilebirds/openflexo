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
package org.openflexo.wkf.processeditor;

import java.util.logging.Logger;

import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActionType;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.foundation.wkf.node.ExclusiveEventBasedOperator;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.InclusiveOperator;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.wkf.processeditor.gr.ActionNodeGR;
import org.openflexo.wkf.processeditor.gr.BeginActionNodeGR;
import org.openflexo.wkf.processeditor.gr.EndActionNodeGR;
import org.openflexo.wkf.processeditor.gr.OperatorANDGR;
import org.openflexo.wkf.processeditor.gr.OperatorComplexGR;
import org.openflexo.wkf.processeditor.gr.OperatorExclusiveEventBasedGR;
import org.openflexo.wkf.processeditor.gr.OperatorIFGR;
import org.openflexo.wkf.processeditor.gr.OperatorInclusiveGR;
import org.openflexo.wkf.processeditor.gr.OperatorLOOPGR;
import org.openflexo.wkf.processeditor.gr.OperatorORGR;
import org.openflexo.wkf.processeditor.gr.SelfExecActionNodeGR;

public class ActionPalette extends AbstractWKFPalette {

	private static final Logger logger = Logger.getLogger(ActionPalette.class.getPackage().getName());

	private ContainerValidity DROP_ON_OPERATION = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof ActionPetriGraph;
		}
	};

	private ContainerValidity DROP_ON_OPERATION_FOR_INTERACTIVE_NODE = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof ActionPetriGraph && !(((ActionPetriGraph) container).getContainer() instanceof SelfExecutableNode)
					&& !(((ActionPetriGraph) container).getContainer() instanceof LOOPOperator);
		}
	};

	public WKFPaletteElement getSelfExecActionElement() {
		return selfExecActionElement;
	}

	public WKFPaletteElement getBeginNodeElement() {
		return beginNodeElement;
	}

	public WKFPaletteElement getEndNodeElement() {
		return endNodeElement;
	}

	public WKFPaletteElement getFlexoActionElement() {
		return flexoActionElement;
	}

	public WKFPaletteElement getDisplayActionElement() {
		return displayActionElement;
	}

	public WKFPaletteElement getANDOperatorElement() {
		return aNDOperatorElement;
	}

	public WKFPaletteElement getINCLUSIVEOperatorElement() {
		return iNCLUSIVEOperatorElement;
	}

	public WKFPaletteElement getOROperatorElement() {
		return oROperatorElement;
	}

	public WKFPaletteElement getEXCLUSIVEEVENTBASEDOperatorElement() {
		return eXCLUSIVEEVENTBASEDOperatorElement;
	}

	public WKFPaletteElement getIFOperatorElement() {
		return iFOperatorElement;
	}

	public WKFPaletteElement getLOOPOperatorElement() {
		return lOOPOperatorElement;
	}

	private WKFPaletteElement selfExecActionElement;

	private WKFPaletteElement beginNodeElement;

	private WKFPaletteElement endNodeElement;

	private WKFPaletteElement flexoActionElement;

	private WKFPaletteElement displayActionElement;

	private WKFPaletteElement aNDOperatorElement;

	private WKFPaletteElement iNCLUSIVEOperatorElement;

	private WKFPaletteElement oROperatorElement;

	private WKFPaletteElement eXCLUSIVEEVENTBASEDOperatorElement;

	private WKFPaletteElement iFOperatorElement;

	private WKFPaletteElement lOOPOperatorElement;

	private WKFPaletteElement complexOperatorElement;

	public ActionPalette() {
		super(300, 170, "action");

		selfExecActionElement = makeSelfExecActionElement(10, 0);
		beginNodeElement = makeBeginNodeElement(60, 0);
		endNodeElement = makeEndNodeElement(110, 0);
		flexoActionElement = makeFlexoActionElement(165, 0);
		displayActionElement = makeDisplayActionElement(230, 0);

		aNDOperatorElement = makeANDOperatorElement(10, 60);
		oROperatorElement = makeOROperatorElement(82, 60);
		iFOperatorElement = makeIFOperatorElement(154, 60);
		lOOPOperatorElement = makeLOOPOperatorElement(226, 60);

		iNCLUSIVEOperatorElement = makeINCLUSIVEOperatorElement(46, 105);
		eXCLUSIVEEVENTBASEDOperatorElement = makeEXCLUSIVEEVENTBASEDOperatorElement(118, 105);
		complexOperatorElement = makeCOMPLEXOperatorElement(190, 105);

		makePalettePanel();
	}

	private WKFPaletteElement makeFlexoActionElement(int x, int y) {
		final ActionNode node = new ActionNode((FlexoProcess) null);
		node.setActionType(ActionType.FLEXO_ACTION);
		node.setName(FlexoLocalization.localizedForKey("WORKFLOW"));
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(50, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new ActionNodeGR(node, null, true), DROP_ON_OPERATION_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeDisplayActionElement(int x, int y) {
		final ActionNode node = new ActionNode((FlexoProcess) null);
		node.setActionType(ActionType.DISPLAY_ACTION);
		node.setName(FlexoLocalization.localizedForKey("DISPLAY"));
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(50, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new ActionNodeGR(node, null, true), DROP_ON_OPERATION_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeBeginNodeElement(int x, int y) {
		final ActionNode node = new ActionNode((FlexoProcess) null);
		node.setNodeType(NodeType.BEGIN);
		node.setName(node.getDefaultName());
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(50, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new BeginActionNodeGR(node, null, true), DROP_ON_OPERATION);
	}

	private WKFPaletteElement makeEndNodeElement(int x, int y) {
		final ActionNode node = new ActionNode((FlexoProcess) null);
		node.setNodeType(NodeType.END);
		node.setName(node.getDefaultName());
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(50, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new EndActionNodeGR(node, null, true), DROP_ON_OPERATION);
	}

	private WKFPaletteElement makeSelfExecActionElement(int x, int y) {
		final SelfExecutableActionNode node = new SelfExecutableActionNode((FlexoProcess) null);
		node.setName("EXEC");
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(50, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new SelfExecActionNodeGR(node, null, true), DROP_ON_OPERATION);
	}

	private WKFPaletteElement makeANDOperatorElement(int x, int y) {
		final ANDOperator operator = new ANDOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(28, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorANDGR(operator, null, true), DROP_ON_OPERATION);
	}

	private WKFPaletteElement makeINCLUSIVEOperatorElement(int x, int y) {
		final InclusiveOperator operator = new InclusiveOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(30, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorInclusiveGR(operator, null, true), DROP_ON_OPERATION);
	}

	private WKFPaletteElement makeEXCLUSIVEEVENTBASEDOperatorElement(int x, int y) {
		final ExclusiveEventBasedOperator operator = new ExclusiveEventBasedOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(30, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorExclusiveEventBasedGR(operator, null, true), DROP_ON_OPERATION);
	}

	private WKFPaletteElement makeOROperatorElement(int x, int y) {
		final OROperator operator = new OROperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(28, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorORGR(operator, null, true), DROP_ON_OPERATION);
	}

	private WKFPaletteElement makeIFOperatorElement(int x, int y) {
		final IFOperator operator = new IFOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(28, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorIFGR(operator, null, true), DROP_ON_OPERATION);
	}

	private WKFPaletteElement makeLOOPOperatorElement(int x, int y) {
		final LOOPOperator operator = new LOOPOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(28, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorLOOPGR(operator, null, true), DROP_ON_OPERATION);
	}

	private WKFPaletteElement makeCOMPLEXOperatorElement(int x, int y) {
		final ComplexOperator operator = new ComplexOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(30, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorComplexGR(operator, null, true), DROP_ON_OPERATION);
	}
}
