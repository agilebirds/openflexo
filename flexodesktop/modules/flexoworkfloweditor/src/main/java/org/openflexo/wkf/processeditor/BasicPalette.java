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

import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFAnnotation;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFDataObject;
import org.openflexo.foundation.wkf.WKFMessageArtifact;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;
import org.openflexo.foundation.wkf.node.EventNode.TriggerType;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
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
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFont;
import org.openflexo.wkf.processeditor.gr.ActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.AnnotationGR;
import org.openflexo.wkf.processeditor.gr.BeginActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.DataObjectGR;
import org.openflexo.wkf.processeditor.gr.EndActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.EventNodeGR;
import org.openflexo.wkf.processeditor.gr.MessageGR;
import org.openflexo.wkf.processeditor.gr.OperatorANDGR;
import org.openflexo.wkf.processeditor.gr.OperatorIFGR;
import org.openflexo.wkf.processeditor.gr.OperatorLOOPGR;
import org.openflexo.wkf.processeditor.gr.OperatorORGR;
import org.openflexo.wkf.processeditor.gr.PortGR;
import org.openflexo.wkf.processeditor.gr.SelfExecActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.SubProcessNodeGR;
import org.openflexo.wkf.swleditor.SWLEditorConstants;

public class BasicPalette extends AbstractWKFPalette {

	private static final Logger logger = Logger.getLogger(BasicPalette.class.getPackage().getName());

	private ContainerValidity DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof FlexoProcess || container instanceof ActivityPetriGraph || container instanceof ActivityGroup
					|| container instanceof WKFAnnotation;
		}
	};

	private ContainerValidity DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof FlexoProcess || container instanceof ActivityPetriGraph
					&& !(((ActivityPetriGraph) container).getContainer() instanceof SelfExecutableNode)
					|| container instanceof ActivityGroup || container instanceof WKFAnnotation;
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

	private ContainerValidity DROP_ON_PETRI_GRAPH = new ContainerValidity() {
		@Override
		public boolean isContainerValid(WKFObject container) {
			return container instanceof FlexoPetriGraph || container instanceof FlexoProcess || container instanceof WKFArtefact;
		}
	};

	private WKFPaletteElement normalActivityElement;
	private WKFPaletteElement singleInstanceSubProcessNodeElement;

	private WKFPaletteElement defaultStartEventElement;
	private WKFPaletteElement defaultIntermediateEventElement;
	private WKFPaletteElement defaultEndEventElement;

	private WKFPaletteElement andOperatorElement;
	private WKFPaletteElement orOperatorElement;

	private WKFPaletteElement annotation;

	private WKFPaletteElement dataFile;

	private WKFPaletteElement messageIn;

	private WKFPaletteElement messageOut;

	public BasicPalette() {
		super(365, 390, "basic");
		int hgap = 5;
		int vgap = 5;
		int x = hgap, y = vgap;
		normalActivityElement = makeNormalActivityElement(x, y, 150, 90);
		x += normalActivityElement.getGraphicalRepresentation().getViewWidth(1.0) + hgap;

		singleInstanceSubProcessNodeElement = makeSingleInstanceSubProcessNodeElement(x, y, 150, 90);
		y += singleInstanceSubProcessNodeElement.getGraphicalRepresentation().getViewHeight(1.0) + vgap;
		x = hgap;

		orOperatorElement = makeOROperatorElement(x, y);
		x = hgap;
		y += orOperatorElement.getGraphicalRepresentation().getViewHeight(1.0) + vgap;

		defaultStartEventElement = makeDefaultStartEventElement(x, y);
		x += defaultStartEventElement.getGraphicalRepresentation().getViewWidth(1.0) + hgap;

		defaultIntermediateEventElement = makeDefaultIntermediateEventElement(x, y);
		x += defaultIntermediateEventElement.getGraphicalRepresentation().getViewWidth(1.0) + hgap;

		defaultEndEventElement = makeDefaultEndEventElement(x, y);
		x += hgap + defaultEndEventElement.getGraphicalRepresentation().getViewWidth(1.0);

		andOperatorElement = makeANDOperatorElement(x, y);
		y += defaultEndEventElement.getGraphicalRepresentation().getViewHeight(1.0) + vgap;
		x = hgap;

		annotation = makeAnnotationElement(x, y + 15);
		x += hgap + annotation.getGraphicalRepresentation().getViewWidth(1.0);

		dataFile = makeDataFile(x, y);
		x += dataFile.getGraphicalRepresentation().getViewWidth(1.0);

		messageIn = makeMessage(x, y, true);
		x += hgap + messageIn.getGraphicalRepresentation().getViewWidth(1.0);

		messageOut = makeMessage(x, y, false);

		makePalettePanel();
	}

	private WKFPaletteElement makeMessage(int x, int y, boolean b) {
		WKFMessageArtifact message = new WKFMessageArtifact((FlexoProcess) null);
		message.setInitiating(b);
		message.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		message.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(message, new MessageGR(message, null), DROP_ON_PETRI_GRAPH);
	}

	private WKFPaletteElement makeDataFile(int x, int y) {
		WKFDataObject annotation = new WKFDataObject((FlexoProcess) null);
		annotation.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		annotation.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		annotation.setLabelX(40, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		annotation.setLabelY(80, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		annotation.setTextFont(new FlexoFont("Lucida Sans", Font.PLAIN, 10));
		return makePaletteElement(annotation, new DataObjectGR(annotation, null), DROP_ON_PETRI_GRAPH);
	}

	private WKFPaletteElement makeAnnotationElement(int x, int y) {
		WKFAnnotation annotation = new WKFAnnotation((FlexoProcess) null);
		annotation.setText(FlexoLocalization.localizedForKey("process_annotation"));
		annotation.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		annotation.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		annotation.setIsAnnotation();
		annotation.setTextFont(new FlexoFont("Lucida Sans", Font.ITALIC, 10));
		annotation.setIsRounded(false);
		return makePaletteElement(annotation, new AnnotationGR(annotation, null), DROP_ON_PETRI_GRAPH);
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

	private WKFPaletteElement makeOROperatorElement(int x, int y) {
		final OROperator operator = new OROperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setIsResizable(true, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setIsResizable(true, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setX(x, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		operator.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
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

	private WKFPaletteElement makeSingleInstanceSubProcessNodeElement(int x, int y, int width, int height) {
		final SingleInstanceSubProcessNode node = new SingleInstanceSubProcessNode((FlexoProcess) null);
		node.setName(FlexoLocalization.localizedForKey("sub_process_call_activity"));
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
		node.setName(FlexoLocalization.localizedForKey("sequential_sub_process"));
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

	private WKFPaletteElement makeDefaultIntermediateEventElement(int x, int y) {
		final EventNode node = new EventNode((FlexoProcess) null);
		node.setTrigger(TriggerType.NONE);
		node.setEventType(EVENT_TYPE.Intermediate);
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
		port.setY(y, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelX(25, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		port.setLabelY(65, ProcessEditorConstants.BASIC_PROCESS_EDITOR);
		return makePaletteElement(port, new PortGR(port, null), DROP_ON_PORT_REGISTERY);
	}

	public WKFPaletteElement getNormalActivityElement() {
		return normalActivityElement;
	}

	public WKFPaletteElement getOrOperatorElement() {
		return orOperatorElement;
	}

	public WKFPaletteElement getAnnotation() {
		return annotation;
	}

	public WKFPaletteElement getSingleInstanceSubProcessNodeElement() {
		return singleInstanceSubProcessNodeElement;
	}

	public WKFPaletteElement getAndOperatorElement() {
		return andOperatorElement;
	}

}
