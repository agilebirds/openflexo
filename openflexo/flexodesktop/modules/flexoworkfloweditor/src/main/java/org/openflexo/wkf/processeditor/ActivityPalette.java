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

import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFAnnotation;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.ExclusiveEventBasedOperator;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.InclusiveOperator;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
import org.openflexo.foundation.wkf.node.MultipleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.node.SingleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;
import org.openflexo.foundation.wkf.node.EventNode.TriggerType;
import org.openflexo.foundation.wkf.ws.DeletePort;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.wkf.processeditor.gr.ActionNodeGR;
import org.openflexo.wkf.processeditor.gr.ActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.BeginActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.EndActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.EventNodeGR;
import org.openflexo.wkf.processeditor.gr.OperatorANDGR;
import org.openflexo.wkf.processeditor.gr.OperatorComplexGR;
import org.openflexo.wkf.processeditor.gr.OperatorExclusiveEventBasedGR;
import org.openflexo.wkf.processeditor.gr.OperatorIFGR;
import org.openflexo.wkf.processeditor.gr.OperatorInclusiveGR;
import org.openflexo.wkf.processeditor.gr.OperatorLOOPGR;
import org.openflexo.wkf.processeditor.gr.OperatorORGR;
import org.openflexo.wkf.processeditor.gr.PortGR;
import org.openflexo.wkf.processeditor.gr.SelfExecActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.SubProcessNodeGR;


public class ActivityPalette extends AbstractWKFPalette {

	private static final Logger logger = Logger.getLogger(ActivityPalette.class.getPackage().getName());

	private ContainerValidity DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof FlexoProcess || container instanceof ActivityPetriGraph || container instanceof ActivityGroup || container instanceof WKFAnnotation;
		}
	};

	private ContainerValidity DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof FlexoProcess || container instanceof ActivityPetriGraph && !(((ActivityPetriGraph) container)
					.getContainer() instanceof SelfExecutableNode) || container instanceof ActivityGroup || container instanceof WKFAnnotation;
		}
	};

	private ContainerValidity DROP_ON_PROCESS_OR_ACTIVITY_PG = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof FlexoProcess || container instanceof ActivityPetriGraph || container instanceof WKFAnnotation;
		}
	};

	private ContainerValidity DROP_ON_PORT_REGISTERY = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof PortRegistery;
		}
	};

	private WKFPaletteElement normalActivityElement;
	private WKFPaletteElement selfExecutableActivityElement;
	// private WKFPaletteElement beginActivityElement;
	// private WKFPaletteElement endActivityElement;
	private WKFPaletteElement defaultStartEventElement;
	private WKFPaletteElement defaultEndEventElement;
	private WKFPaletteElement andOperatorElement;
	private WKFPaletteElement orOperatorElement;
	private WKFPaletteElement inclusiveOperatorElement;
	private WKFPaletteElement exclusiveEventBaseOperatorElement;
	private WKFPaletteElement complexOperatorElement;
	private WKFPaletteElement ifOperatorElement;
	private WKFPaletteElement loopOperatorElement;

	public ActivityPalette() {
		super(300, 320, "activity");

		normalActivityElement = makeNormalActivityElement(15, 0, 80, 40);
		selfExecutableActivityElement = makeSelfExecActivityElement(120, 0);
		// beginActivityElement = makeBeginNodeElement(175, 10);
		// endActivityElement = makeEndNodeElement(230, 0);
		defaultStartEventElement = makeDefaultStartEventElement(175, 0);
		defaultEndEventElement = makeDefaultEndEventElement(230, 0);

		andOperatorElement = makeANDOperatorElement(10, 60);
		orOperatorElement = makeOROperatorElement(82, 60);
		ifOperatorElement = makeIFOperatorElement(154, 60);
		loopOperatorElement = makeLOOPOperatorElement(226, 60);

		inclusiveOperatorElement = makeINCLUSIVEOperatorElement(46, 100);
		exclusiveEventBaseOperatorElement = makeEXCLUSIVEEVENTBASEDOperatorElement(118, 100);
		complexOperatorElement = makeCOMPLEXOperatorElement(190, 100);
		/*
		 * makeAnnotationElement(180, 62, 90, 25, FlexoLocalization.localizedForKey("process_annotation")); makeBoundingBoxElement(180, 88,
		 * 90, 25, FlexoLocalization.localizedForKey("bounding_box"));
		 */

		singleInstanceSubProcessNodeElement = makeSingleInstanceSubProcessNodeElement(15, 165, 115, 60);
		multipleInstanceSubProcessNodeElement = makeMultipleInstanceSubProcessNodeElement(150, 165, 115, 60);
		loopSubProcessNodeElement = makeLoopSubProcessNodeElement(15, 240, 115, 60);
		makeWSCallSubProcessNodeElement = makeWSCallSubProcessNodeElement(150, 240, 115, 60);

		/*
		 * makeNewPortElement(15, 295, FlexoLocalization.localizedForKey("new")); makeInPortElement(70, 295,
		 * FlexoLocalization.localizedForKey("input")); makeOutPortElement(125, 295, FlexoLocalization.localizedForKey("output"));
		 * makeInOutPortElement(180, 295, FlexoLocalization.localizedForKey("in-out")); makeDeletePortElement(235, 295,
		 * FlexoLocalization.localizedForKey("delete"));
		 */
		makePalettePanel();
	}

	private WKFPaletteElement makeNormalActivityElement(int x, int y, int width, int height) {
		final ActivityNode node = new ActivityNode((FlexoProcess) null);
		node.setName(node.getDefaultName());
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setWidth(width, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setHeight(height, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new ActivityNodeGR(node, null, true) {
			@Override
			public String getSubLabel() {
				return "<role>";
			}
			// @Override
			// public org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder getBorder() {
			// return new ShapeBorder(0, 0, 0, 0);
			// }
		}, DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeSelfExecActivityElement(int x, int y) {
		final SelfExecutableActivityNode node = new SelfExecutableActivityNode((FlexoProcess) null);
		node.setName("EXEC");
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(50, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new SelfExecActivityNodeGR(node, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeANDOperatorElement(int x, int y) {
		final ANDOperator operator = new ANDOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(28, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorANDGR(operator, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeINCLUSIVEOperatorElement(int x, int y) {
		final InclusiveOperator operator = new InclusiveOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(30, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorInclusiveGR(operator, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeEXCLUSIVEEVENTBASEDOperatorElement(int x, int y) {
		final ExclusiveEventBasedOperator operator = new ExclusiveEventBasedOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(30, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorExclusiveEventBasedGR(operator, null, true),
				DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeOROperatorElement(int x, int y) {
		final OROperator operator = new OROperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(28, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorORGR(operator, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeIFOperatorElement(int x, int y) {
		final IFOperator operator = new IFOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(28, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorIFGR(operator, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeLOOPOperatorElement(int x, int y) {
		final LOOPOperator operator = new LOOPOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(28, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorLOOPGR(operator, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeCOMPLEXOperatorElement(int x, int y) {
		final ComplexOperator operator = new ComplexOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelX(30, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setLabelY(55, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(operator, new OperatorComplexGR(operator, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeSingleInstanceSubProcessNodeElement(int x, int y, int width, int height) {
		final SingleInstanceSubProcessNode node = new SingleInstanceSubProcessNode((FlexoProcess) null);
		node.setName("Single S/P");
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setWidth(width, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setHeight(height, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		SubProcessNodeGR gr = new SubProcessNodeGR(node, null, true) {
			@Override
			public String getSubLabel() {
				return "<role>";
			}
		};
		return makePaletteElement(node, gr, DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement singleInstanceSubProcessNodeElement;

	private WKFPaletteElement multipleInstanceSubProcessNodeElement;

	private WKFPaletteElement loopSubProcessNodeElement;

	private WKFPaletteElement makeWSCallSubProcessNodeElement;

	private WKFPaletteElement makeMultipleInstanceSubProcessNodeElement(int x, int y, int width, int height) {
		final MultipleInstanceSubProcessNode node = new MultipleInstanceSubProcessNode((FlexoProcess) null);
		node.setIsSequential(false);
		node.setName("Multiple S/P");
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setWidth(width, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setHeight(height, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		SubProcessNodeGR gr = new SubProcessNodeGR(node, null, true) {
			@Override
			public String getSubLabel() {
				return "<role>";
			}
		};
		return makePaletteElement(node, gr, DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeLoopSubProcessNodeElement(int x, int y, int width, int height) {
		final LoopSubProcessNode node = new LoopSubProcessNode((FlexoProcess) null);
		node.setName("Loop S/P");
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setWidth(width, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setHeight(height, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		SubProcessNodeGR gr = new SubProcessNodeGR(node, null, true) {
			@Override
			public String getSubLabel() {
				return "<role>";
			}
		};
		return makePaletteElement(node, gr, DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeWSCallSubProcessNodeElement(int x, int y, int width, int height) {
		final WSCallSubProcessNode node = new WSCallSubProcessNode((FlexoProcess) null);
		node.setName("Web Service");
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setWidth(width, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setHeight(height, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		SubProcessNodeGR gr = new SubProcessNodeGR(node, null, true) {
			@Override
			public String getSubLabel() {
				return "<role>";
			}
		};
		return makePaletteElement(node, gr, DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeBeginNodeElement(int x, int y) {
		final ActivityNode node = new ActivityNode((FlexoProcess) null);
		node.setNodeType(NodeType.BEGIN);
		node.setName(node.getDefaultName());
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(40, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new BeginActivityNodeGR(node, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeEndNodeElement(int x, int y) {
		final ActivityNode node = new ActivityNode((FlexoProcess) null);
		node.setNodeType(NodeType.END);
		node.setName(node.getDefaultName());
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(50, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new EndActivityNodeGR(node, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeDefaultStartEventElement(int x, int y) {
		final EventNode node = new EventNode((FlexoProcess) null);
		node.setTrigger(TriggerType.NONE);
		node.setEventType(EVENT_TYPE.Start);
		node.setName(node.getDefaultName());
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(50, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new EventNodeGR(node, null), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeDefaultEndEventElement(int x, int y) {
		final EventNode node = new EventNode((FlexoProcess) null);
		node.setTrigger(TriggerType.NONE);
		node.setEventType(EVENT_TYPE.End);
		node.setName(node.getDefaultName());
		node.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		node.setLabelY(50, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(node, new EventNodeGR(node, null), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeNewPortElement(int x, int y, String portName) {
		final NewPort port = new NewPort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelY(65, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	private WKFPaletteElement makeInPortElement(int x, int y, String portName) {
		final InPort port = new InPort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelY(65, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	private WKFPaletteElement makeOutPortElement(int x, int y, String portName) {
		final OutPort port = new OutPort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelY(65, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	private WKFPaletteElement makeInOutPortElement(int x, int y, String portName) {
		final InOutPort port = new InOutPort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelY(65, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	private WKFPaletteElement makeDeletePortElement(int x, int y, String portName) {
		final DeletePort port = new DeletePort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setY(y, ActivityNodeGR.BASIC_PROCESS_EDITOR);
		port.setLabelX(25, ActionNodeGR.BASIC_PROCESS_EDITOR);
		port.setLabelY(65, ActionNodeGR.BASIC_PROCESS_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	public WKFPaletteElement getNormalActivityElement() {
		return normalActivityElement;
	}

	public WKFPaletteElement getSelfExecutableActivityElement() {
		return selfExecutableActivityElement;
	}

	// public WKFPaletteElement getEndActivityElement() {
	// return endActivityElement;
	// }

	public WKFPaletteElement getOrOperatorElement() {
		return orOperatorElement;
	}

	public WKFPaletteElement getInclusiveOperatorElement() {
		return inclusiveOperatorElement;
	}

	public WKFPaletteElement getExclusiveEventBaseOperatorElement() {
		return exclusiveEventBaseOperatorElement;
	}

	public WKFPaletteElement getComplexOperatorElement() {
		return complexOperatorElement;
	}

	public WKFPaletteElement getIfOperatorElement() {
		return ifOperatorElement;
	}

	public WKFPaletteElement getLoopOperatorElement() {
		return loopOperatorElement;
	}

	public WKFPaletteElement getSingleInstanceSubProcessNodeElement() {
		return singleInstanceSubProcessNodeElement;
	}

	public WKFPaletteElement getMultipleInstanceSubProcessNodeElement() {
		return multipleInstanceSubProcessNodeElement;
	}

	public WKFPaletteElement getLoopSubProcessNodeElement() {
		return loopSubProcessNodeElement;
	}

	public WKFPaletteElement getMakeWSCallSubProcessNodeElement() {
		return makeWSCallSubProcessNodeElement;
	}

	// public WKFPaletteElement getBeginActivityElement() {
	// return beginActivityElement;
	// }

	public WKFPaletteElement getAndOperatorElement() {
		return andOperatorElement;
	}

}
