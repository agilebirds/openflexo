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
package org.openflexo.wkf.swleditor;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.foundation.wkf.node.ExclusiveEventBasedOperator;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.InclusiveOperator;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;
import org.openflexo.wkf.swleditor.gr.BeginOperationNodeGR;
import org.openflexo.wkf.swleditor.gr.EndOperationNodeGR;
import org.openflexo.wkf.swleditor.gr.OperationNodeGR;
import org.openflexo.wkf.swleditor.gr.OperatorANDGR;
import org.openflexo.wkf.swleditor.gr.OperatorComplexGR;
import org.openflexo.wkf.swleditor.gr.OperatorExclusiveEventBasedGR;
import org.openflexo.wkf.swleditor.gr.OperatorIFGR;
import org.openflexo.wkf.swleditor.gr.OperatorInclusiveGR;
import org.openflexo.wkf.swleditor.gr.OperatorLOOPGR;
import org.openflexo.wkf.swleditor.gr.OperatorORGR;
import org.openflexo.wkf.swleditor.gr.SelfExecOperationNodeGR;

public class OperationPalette extends AbstractWKFPalette {

	private static final Logger logger = Logger.getLogger(OperationPalette.class.getPackage().getName());

	private ContainerValidity DROP_ON_ACTIVITY = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof OperationPetriGraph;
		}
	};

	private ContainerValidity DROP_ON_ACTIVITY_FOR_INTERACTIVE_NODE = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof OperationPetriGraph
					&& !(((OperationPetriGraph) container).getContainer() instanceof SelfExecutableNode)
					&& !(((OperationPetriGraph) container).getContainer() instanceof LOOPOperator);
		}
	};

	private WKFPaletteElement normalOperationElement;

	private WKFPaletteElement selfExecOperationElement;

	private WKFPaletteElement beginNodeElement;

	private WKFPaletteElement endNodeElement;

	private WKFPaletteElement andOperatorElement;

	private WKFPaletteElement orOperatorElement;

	private WKFPaletteElement ifOperatorElement;

	private WKFPaletteElement loopOperatorElement;

	private WKFPaletteElement eXCLUSIVEEVENTBASEDOperatorElement;

	private WKFPaletteElement complexOperatorElement;

	private WKFPaletteElement iNCLUSIVEOperatorElement;

	public OperationPalette() {
		super(300, 170, "operation");

		normalOperationElement = makeNormalOperationElement(10, 0, 80, 40);
		selfExecOperationElement = makeSelfExecOperationElement(110, 0);
		beginNodeElement = makeBeginNodeElement(160, 0);
		endNodeElement = makeEndNodeElement(210, 0);

		andOperatorElement = makeANDOperatorElement(10, 60);
		orOperatorElement = makeOROperatorElement(82, 60);
		ifOperatorElement = makeIFOperatorElement(154, 60);
		loopOperatorElement = makeLOOPOperatorElement(226, 60);

		iNCLUSIVEOperatorElement = makeINCLUSIVEOperatorElement(46, 105);
		eXCLUSIVEEVENTBASEDOperatorElement = makeEXCLUSIVEEVENTBASEDOperatorElement(118, 105);
		complexOperatorElement = makeCOMPLEXOperatorElement(190, 105);

		makePalettePanel();
	}

	public WKFPaletteElement getNormalOperationElement() {
		return normalOperationElement;
	}

	public WKFPaletteElement getSelfExecOperationElement() {
		return selfExecOperationElement;
	}

	public WKFPaletteElement getBeginNodeElement() {
		return beginNodeElement;
	}

	public WKFPaletteElement getEndNodeElement() {
		return endNodeElement;
	}

	public WKFPaletteElement getAndOperatorElement() {
		return andOperatorElement;
	}

	public WKFPaletteElement getOrOperatorElement() {
		return orOperatorElement;
	}

	public WKFPaletteElement getIfOperatorElement() {
		return ifOperatorElement;
	}

	public WKFPaletteElement getEXCLUSIVEEVENTBASEDOperatorElement() {
		return eXCLUSIVEEVENTBASEDOperatorElement;
	}

	public WKFPaletteElement getComplexOperatorElement() {
		return complexOperatorElement;
	}

	public WKFPaletteElement getINCLUSIVEOperatorElement() {
		return iNCLUSIVEOperatorElement;
	}

	public WKFPaletteElement getLoopOperatorElement() {
		return loopOperatorElement;
	}

	private WKFPaletteElement makeNormalOperationElement(int x, int y, int width, int height) {
		final OperationNode node = new OperationNode((FlexoProcess) null);
		node.setName(node.getDefaultName());
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setWidth(width, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setHeight(height, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new OperationNodeGR(node, null, true), DROP_ON_ACTIVITY_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeBeginNodeElement(int x, int y) {
		final OperationNode node = new OperationNode((FlexoProcess) null);
		node.setNodeType(NodeType.BEGIN);
		node.setName(node.getDefaultName());
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelY(50, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new BeginOperationNodeGR(node, null, true), DROP_ON_ACTIVITY);
	}

	private WKFPaletteElement makeEndNodeElement(int x, int y) {
		final OperationNode node = new OperationNode((FlexoProcess) null);
		node.setNodeType(NodeType.END);
		node.setName(node.getDefaultName());
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelY(50, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new EndOperationNodeGR(node, null, true), DROP_ON_ACTIVITY);
	}

	private WKFPaletteElement makeSelfExecOperationElement(int x, int y) {
		final SelfExecutableOperationNode node = new SelfExecutableOperationNode((FlexoProcess) null);
		node.setName("EXEC");
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelY(50, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new SelfExecOperationNodeGR(node, null, true), DROP_ON_ACTIVITY);
	}

	private WKFPaletteElement makeLOOPOperatorElement(int x, int y) {
		final LOOPOperator operator = new LOOPOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(28, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorLOOPGR(operator, null, true), DROP_ON_ACTIVITY);
	}

	private WKFPaletteElement makeANDOperatorElement(int x, int y) {
		final ANDOperator operator = new ANDOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(28, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorANDGR(operator, null, true), DROP_ON_ACTIVITY);
	}

	private WKFPaletteElement makeOROperatorElement(int x, int y) {
		final OROperator operator = new OROperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(28, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorORGR(operator, null, true), DROP_ON_ACTIVITY);
	}

	private WKFPaletteElement makeIFOperatorElement(int x, int y) {
		final IFOperator operator = new IFOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(28, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorIFGR(operator, null, true), DROP_ON_ACTIVITY);
	}

	private WKFPaletteElement makeINCLUSIVEOperatorElement(int x, int y) {
		final InclusiveOperator operator = new InclusiveOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(30, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorInclusiveGR(operator, null, true), DROP_ON_ACTIVITY);
	}

	private WKFPaletteElement makeEXCLUSIVEEVENTBASEDOperatorElement(int x, int y) {
		final ExclusiveEventBasedOperator operator = new ExclusiveEventBasedOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(30, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorExclusiveEventBasedGR(operator, null, true), DROP_ON_ACTIVITY);
	}

	private WKFPaletteElement makeCOMPLEXOperatorElement(int x, int y) {
		final ComplexOperator operator = new ComplexOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(30, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorComplexGR(operator, null, true), DROP_ON_ACTIVITY);
	}
}
