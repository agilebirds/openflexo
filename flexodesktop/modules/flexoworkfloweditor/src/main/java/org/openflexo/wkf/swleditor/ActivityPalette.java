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

import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.WKFAnnotation;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;
import org.openflexo.foundation.wkf.node.EventNode.TriggerType;
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
import org.openflexo.foundation.wkf.ws.DeletePort;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.wkf.swleditor.gr.ActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.AnnotationGR;
import org.openflexo.wkf.swleditor.gr.BeginActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.EndActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.EventNodeGR;
import org.openflexo.wkf.swleditor.gr.OperatorANDGR;
import org.openflexo.wkf.swleditor.gr.OperatorComplexGR;
import org.openflexo.wkf.swleditor.gr.OperatorExclusiveEventBasedGR;
import org.openflexo.wkf.swleditor.gr.OperatorIFGR;
import org.openflexo.wkf.swleditor.gr.OperatorInclusiveGR;
import org.openflexo.wkf.swleditor.gr.OperatorLOOPGR;
import org.openflexo.wkf.swleditor.gr.OperatorORGR;
import org.openflexo.wkf.swleditor.gr.PortGR;
import org.openflexo.wkf.swleditor.gr.SelfExecActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.SubProcessNodeGR;

public class ActivityPalette extends AbstractWKFPalette {

	private static final Logger logger = Logger.getLogger(ActivityPalette.class.getPackage().getName());

	private ContainerValidity DROP_ON_ROLE = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof Role;
		}
	};

	private ContainerValidity DROP_ON_PORT_REGISTERY = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof PortRegistery;
		}
	};

	private ContainerValidity DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof Role || container instanceof ActivityPetriGraph || container instanceof ActivityGroup
					|| container instanceof WKFAnnotation;
		}
	};

	private ContainerValidity DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof Role || container instanceof ActivityPetriGraph
					&& !(((ActivityPetriGraph) container).getContainer() instanceof SelfExecutableNode)
					|| container instanceof ActivityGroup || container instanceof WKFAnnotation;
		}
	};

	private ContainerValidity DROP_ON_ROLE_OR_ACTIVITY_PG = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof Role || container instanceof ActivityPetriGraph || container instanceof WKFAnnotation;
		}
	};

	private WKFPaletteElement inclusiveOperatorElement;

	private WKFPaletteElement exclusiveEventBaseOperatorElement;

	public ActivityPalette() {
		super(300, 320, "activity");

		normalActivityElement = makeNormalActivityElement(15, 0, 80, 40);
		selfExecActivityElement = makeSelfExecActivityElement(120, 0);
		// beginNodeElement = makeBeginNodeElement(175, 10);
		// endNodeElement = makeEndNodeElement(230, 0);
		defaultStartEventElement = makeDefaultStartEventElement(175, 0);
		defaultEndEventElement = makeDefaultEndEventElement(230, 0);

		andOperatorElement = makeANDOperatorElement(10, 60);
		oROperatorElement = makeOROperatorElement(82, 60);
		iFOperatorElement = makeIFOperatorElement(154, 60);
		lOOPOperatorElement = makeLOOPOperatorElement(226, 60);

		inclusiveOperatorElement = makeINCLUSIVEOperatorElement(46, 100);
		exclusiveEventBaseOperatorElement = makeEXCLUSIVEEVENTBASEDOperatorElement(118, 100);
		complexOperatorElement = makeCOMPLEXOperatorElement(190, 100);

		singleInstanceSubProcessNodeElement = makeSingleInstanceSubProcessNodeElement(15, 170, 115, 50);
		multipleInstanceSubProcessNodeElement = makeMultipleInstanceSubProcessNodeElement(150, 170, 115, 50);
		loopSubProcessNodeElement = makeLoopSubProcessNodeElement(15, 245, 115, 50);
		wScallSubProcessNodeElement = makeWSCallSubProcessNodeElement(150, 245, 115, 50);
		/*
		 * makeNewPortElement(15, 315, FlexoLocalization.localizedForKey("new")); makeInPortElement(70, 315,
		 * FlexoLocalization.localizedForKey("input")); makeOutPortElement(125, 315, FlexoLocalization.localizedForKey("output"));
		 * makeInOutPortElement(180, 315, FlexoLocalization.localizedForKey("in-out")); makeDeletePortElement(235, 315,
		 * FlexoLocalization.localizedForKey("delete"));
		 */
		makePalettePanel();
	}

	private WKFPaletteElement makeNormalActivityElement(int x, int y, int width, int height) {
		final ActivityNode node = new ActivityNode((FlexoProcess) null);
		node.setName(node.getDefaultName());
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setWidth(width, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setHeight(height, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new ActivityNodeGR(node, null, true),
				DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeSelfExecActivityElement(int x, int y) {
		final SelfExecutableActivityNode node = new SelfExecutableActivityNode((FlexoProcess) null);
		node.setName("EXEC");
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelY(50, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new SelfExecActivityNodeGR(node, null, true), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeANDOperatorElement(int x, int y) {
		final ANDOperator operator = new ANDOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(28, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorANDGR(operator, null, true), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeOROperatorElement(int x, int y) {
		final OROperator operator = new OROperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(28, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorORGR(operator, null, true), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeIFOperatorElement(int x, int y) {
		final IFOperator operator = new IFOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(28, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorIFGR(operator, null, true), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeLOOPOperatorElement(int x, int y) {
		final LOOPOperator operator = new LOOPOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(28, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorLOOPGR(operator, null, true), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeSingleInstanceSubProcessNodeElement(int x, int y, int width, int height) {
		final SingleInstanceSubProcessNode node = new SingleInstanceSubProcessNode((FlexoProcess) null);
		node.setName("Single S/P");
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setWidth(width, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setHeight(height, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		SubProcessNodeGR gr = new SubProcessNodeGR(node, null, true) {
			@Override
			public String getRoleLabel() {
				return "<role>";
			}
		};
		return makePaletteElement(node, gr, DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	// private ShapeBorder REDUCED_SP_BORDER = new ShapeBorder(5,5,5,5);

	private WKFPaletteElement wScallSubProcessNodeElement;

	private WKFPaletteElement loopSubProcessNodeElement;

	private WKFPaletteElement normalActivityElement;

	private WKFPaletteElement selfExecActivityElement;

	// private WKFPaletteElement beginNodeElement;
	//
	// private WKFPaletteElement endNodeElement;
	private WKFPaletteElement defaultStartEventElement;
	private WKFPaletteElement defaultEndEventElement;
	private WKFPaletteElement andOperatorElement;

	private WKFPaletteElement oROperatorElement;

	private WKFPaletteElement iFOperatorElement;

	private WKFPaletteElement lOOPOperatorElement;

	private WKFPaletteElement singleInstanceSubProcessNodeElement;

	private WKFPaletteElement multipleInstanceSubProcessNodeElement;

	private WKFPaletteElement complexOperatorElement;

	private WKFPaletteElement makeMultipleInstanceSubProcessNodeElement(int x, int y, int width, int height) {
		final MultipleInstanceSubProcessNode node = new MultipleInstanceSubProcessNode((FlexoProcess) null);
		node.setIsSequential(false);
		node.setName("Multiple S/P");
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setWidth(width, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setHeight(height, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		SubProcessNodeGR gr = new SubProcessNodeGR(node, null, true) {
			@Override
			public String getRoleLabel() {
				return "<role>";
			}
		};
		return makePaletteElement(node, gr, DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeLoopSubProcessNodeElement(int x, int y, int width, int height) {
		final LoopSubProcessNode node = new LoopSubProcessNode((FlexoProcess) null);
		node.setName("Loop S/P");
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setWidth(width, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setHeight(height, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		SubProcessNodeGR gr = new SubProcessNodeGR(node, null, true) {
			@Override
			public String getRoleLabel() {
				return "<role>";
			}
		};
		return makePaletteElement(node, gr, DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeWSCallSubProcessNodeElement(int x, int y, int width, int height) {
		final WSCallSubProcessNode node = new WSCallSubProcessNode((FlexoProcess) null);
		node.setName("Web Service");
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setWidth(width, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setHeight(height, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		SubProcessNodeGR gr = new SubProcessNodeGR(node, null, true) {
			@Override
			public String getRoleLabel() {
				return "<role>";
			}
		};
		return makePaletteElement(node, gr, DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeBeginNodeElement(int x, int y) {
		final ActivityNode node = new ActivityNode((FlexoProcess) null);
		node.setNodeType(NodeType.BEGIN);
		node.setName(node.getDefaultName());
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelY(40, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new BeginActivityNodeGR(node, null, true), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeEndNodeElement(int x, int y) {
		final ActivityNode node = new ActivityNode((FlexoProcess) null);
		node.setNodeType(NodeType.END);
		node.setName(node.getDefaultName());
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelY(50, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new EndActivityNodeGR(node, null, true), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeDefaultStartEventElement(int x, int y) {
		final EventNode node = new EventNode((FlexoProcess) null);
		node.setTrigger(TriggerType.NONE);
		node.setEventType(EVENT_TYPE.Start);
		node.setName(node.getDefaultName());
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelY(50, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new EventNodeGR(node, null), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeDefaultEndEventElement(int x, int y) {
		final EventNode node = new EventNode((FlexoProcess) null);
		node.setTrigger(TriggerType.NONE);
		node.setEventType(EVENT_TYPE.End);
		node.setName(node.getDefaultName());
		node.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		node.setLabelY(50, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(node, new EventNodeGR(node, null), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeCOMPLEXOperatorElement(int x, int y) {
		final ComplexOperator operator = new ComplexOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(30, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorComplexGR(operator, null, true), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeINCLUSIVEOperatorElement(int x, int y) {
		final InclusiveOperator operator = new InclusiveOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(30, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorInclusiveGR(operator, null, true), DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeEXCLUSIVEEVENTBASEDOperatorElement(int x, int y) {
		final ExclusiveEventBasedOperator operator = new ExclusiveEventBasedOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(30, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorExclusiveEventBasedGR(operator, null, true),
				DROP_ON_ROLE_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeAnnotationElement(int x, int y, int width, int height, String text) {
		final WKFAnnotation annotation = new WKFAnnotation((FlexoProcess) null);
		annotation.setText(text);
		annotation.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setWidth(width, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setHeight(height, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setIsAnnotation();
		annotation.setTextFont(new FlexoFont("SansSerif", Font.ITALIC, 11));
		annotation.setIsRounded(false);
		/*
		 * annotation.setIsSolidBackground(false); annotation.setShowBounds(false); annotation.setIsFloatingLabel(false);
		 * annotation.setTextFont(new FlexoFont("SansSerif",Font.ITALIC, 11), ActivityNodeGR.SWIMMING_LANE_EDITOR);
		 */
		return makePaletteElement(annotation, new AnnotationGR(annotation, null), DROP_ON_ROLE);
	}

	private WKFPaletteElement makeBoundingBoxElement(int x, int y, int width, int height, String text) {
		final WKFAnnotation annotation = new WKFAnnotation((FlexoProcess) null);
		annotation.setText(text);
		annotation.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setWidth(width, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setHeight(height, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setIsBoundingBox();
		annotation.setIsRounded(true);
		annotation.setTextFont(new FlexoFont("SansSerif", Font.ITALIC, 10));
		/*
		 * annotation.setIsSolidBackground(false); annotation.setIsFloatingLabel(true);
		 */
		annotation.setLabelX(60, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setLabelY(36, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		// annotation.setTextFont(new FlexoFont("SansSerif",Font.PLAIN, 9), ActivityNodeGR.SWIMMING_LANE_EDITOR);
		return makePaletteElement(annotation, new AnnotationGR(annotation, null), DROP_ON_ROLE);
	}

	private WKFPaletteElement makeNewPortElement(int x, int y, String portName) {
		final NewPort port = new NewPort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelY(65, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	private WKFPaletteElement makeInPortElement(int x, int y, String portName) {
		final InPort port = new InPort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelY(65, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	private WKFPaletteElement makeOutPortElement(int x, int y, String portName) {
		final OutPort port = new OutPort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelY(65, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	private WKFPaletteElement makeInOutPortElement(int x, int y, String portName) {
		final InOutPort port = new InOutPort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelY(65, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	private WKFPaletteElement makeDeletePortElement(int x, int y, String portName) {
		final DeletePort port = new DeletePort((FlexoProcess) null);
		port.setName(portName);
		port.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		port.setLabelY(65, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	public WKFPaletteElement getWScallSubProcessNodeElement() {
		return wScallSubProcessNodeElement;
	}

	public WKFPaletteElement getLoopSubProcessNodeElement() {
		return loopSubProcessNodeElement;
	}

	public WKFPaletteElement getNormalActivityElement() {
		return normalActivityElement;
	}

	public WKFPaletteElement getSelfExecActivityElement() {
		return selfExecActivityElement;
	}

	// public WKFPaletteElement getBeginNodeElement() {
	// return beginNodeElement;
	// }
	//
	// public WKFPaletteElement getEndNodeElement() {
	// return endNodeElement;
	// }

	public WKFPaletteElement getAndOperatorElement() {
		return andOperatorElement;
	}

	public WKFPaletteElement getOROperatorElement() {
		return oROperatorElement;
	}

	public WKFPaletteElement getIFOperatorElement() {
		return iFOperatorElement;
	}

	public WKFPaletteElement getLOOPOperatorElement() {
		return lOOPOperatorElement;
	}

	public WKFPaletteElement getSingleInstanceSubProcessNodeElement() {
		return singleInstanceSubProcessNodeElement;
	}

	public WKFPaletteElement getMultipleInstanceSubProcessNodeElement() {
		return multipleInstanceSubProcessNodeElement;
	}

	public WKFPaletteElement getComplexOperatorElement() {
		return complexOperatorElement;
	}
}
