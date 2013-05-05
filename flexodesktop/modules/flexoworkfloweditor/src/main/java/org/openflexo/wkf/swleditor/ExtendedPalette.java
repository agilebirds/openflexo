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
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.WKFAnnotation;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFDataObject;
import org.openflexo.foundation.wkf.WKFDataObject.DataObjectType;
import org.openflexo.foundation.wkf.WKFDataSource;
import org.openflexo.foundation.wkf.WKFStockObject;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.foundation.wkf.node.ExclusiveEventBasedOperator;
import org.openflexo.foundation.wkf.node.InclusiveOperator;
import org.openflexo.foundation.wkf.node.MultipleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.wkf.swleditor.gr.AnnotationGR;
import org.openflexo.wkf.swleditor.gr.DataObjectGR;
import org.openflexo.wkf.swleditor.gr.DataSourceGR;
import org.openflexo.wkf.swleditor.gr.OperatorComplexGR;
import org.openflexo.wkf.swleditor.gr.OperatorExclusiveEventBasedGR;
import org.openflexo.wkf.swleditor.gr.OperatorInclusiveGR;
import org.openflexo.wkf.swleditor.gr.StockObjectGR;
import org.openflexo.wkf.swleditor.gr.SubProcessNodeGR;

public class ExtendedPalette extends AbstractWKFPalette {

	private static final Logger logger = Logger.getLogger(ExtendedPalette.class.getPackage().getName());

	private ContainerValidity DROP_ON_ROLE_OR_PETRI_GRAPH = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof FlexoPetriGraph || container instanceof FlexoProcess || container instanceof WKFArtefact
					|| container instanceof Role;
		}
	};

	private ContainerValidity DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof Role || container instanceof ActivityPetriGraph
					&& !(((ActivityPetriGraph) container).getContainer() instanceof SelfExecutableNode)
					|| container instanceof ActivityGroup || container instanceof WKFAnnotation;
		}
	};

	private ContainerValidity DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP = new ContainerValidity() {
		@Override
		public boolean isContainerValid(FlexoModelObject container) {
			return container instanceof Role || container instanceof ActivityPetriGraph || container instanceof ActivityGroup
					|| container instanceof WKFAnnotation;
		}
	};

	private WKFPaletteElement dataSource;

	private WKFPaletteElement boundingBox;

	private WKFPaletteElement stockArtefact;

	private WKFPaletteElement multipleInstanceSubProcessNode;

	private WKFPaletteElement inputDocument;

	private WKFPaletteElement outputDocument;

	private WKFPaletteElement collectionDocument;

	public ExtendedPalette() {
		super(340, 330, "extended");
		int hgap = 5;
		int vgap = 5;
		int x = hgap, y = vgap;

		multipleInstanceSubProcessNode = makeMultipleInstanceSubProcessNodeElement(x, y, 150, 90);
		x += multipleInstanceSubProcessNode.getGraphicalRepresentation().getViewWidth(1.0) + hgap;
		boundingBox = makeBoundingBoxElement(x, y, 150, 90);
		x = hgap;
		y += multipleInstanceSubProcessNode.getGraphicalRepresentation().getViewHeight(1.0) + vgap;
		inputDocument = makeDataFile(x, y, DataObjectType.INPUT, false);
		x += hgap + inputDocument.getGraphicalRepresentation().getViewWidth(1.0);
		outputDocument = makeDataFile(x, y, DataObjectType.OUTPUT, false);
		x += hgap + outputDocument.getGraphicalRepresentation().getViewWidth(1.0);
		collectionDocument = makeDataFile(x, y, null, true);
		x = hgap;
		y += collectionDocument.getGraphicalRepresentation().getViewHeight(1.0) + vgap;
		dataSource = makeDataSourceElement(x, y);
		x += hgap + dataSource.getGraphicalRepresentation().getViewWidth(1.0);
		stockArtefact = makeStockElement(x, y);
		x = hgap;
		y += stockArtefact.getGraphicalRepresentation().getViewHeight(1.0) + vgap;
		WKFPaletteElement inclusiveOperator = makeINCLUSIVEOperatorElement(x, y);
		x += hgap + inclusiveOperator.getGraphicalRepresentation().getViewWidth(1.0);
		WKFPaletteElement exclusiveOperator = makeEXCLUSIVEEVENTBASEDOperatorElement(x, y);
		x += hgap + exclusiveOperator.getGraphicalRepresentation().getViewWidth(1.0);
		WKFPaletteElement complexOperator = makeCOMPLEXOperatorElement(x, y);
		makePalettePanel();
	}

	private WKFPaletteElement makeDataFile(int x, int y, DataObjectType type, boolean collection) {
		WKFDataObject annotation = new WKFDataObject((FlexoProcess) null);
		annotation.setType(type);
		annotation.setIsCollection(collection);
		annotation.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setLabelX(40, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setLabelY(80, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setTextFont(new FlexoFont("Lucida Sans", Font.PLAIN, 10));
		return makePaletteElement(annotation, new DataObjectGR(annotation, null), DROP_ON_ROLE_OR_PETRI_GRAPH);
	}

	private WKFPaletteElement makeBoundingBoxElement(int x, int y, int width, int height) {
		final WKFAnnotation annotation = new WKFAnnotation((FlexoProcess) null);
		annotation.setText(FlexoLocalization.localizedForKey("bounding_box"));
		annotation.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setWidth(width, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setHeight(height, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setIsBoundingBox();
		annotation.setIsRounded(true);
		annotation.setTextFont(new FlexoFont("Lucida Sans", Font.ITALIC, 9));
		/*annotation.setIsSolidBackground(false);
		annotation.setIsFloatingLabel(true);*/
		annotation.setLabelX(60, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setLabelY(10, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		// annotation.setTextFont(new FlexoFont("SansSerif",Font.PLAIN, 9), ActivityNodeGR.SWIMMING_LANE_EDITOR);
		return makePaletteElement(annotation, new AnnotationGR(annotation, null), DROP_ON_ROLE_OR_PETRI_GRAPH);
	}

	public WKFPaletteElement makeDataSourceElement(int x, int y) {
		WKFDataSource annotation = new WKFDataSource((FlexoProcess) null);
		annotation.setText(FlexoLocalization.localizedForKey("data_store"));
		annotation.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setLabelX(40, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setLabelY(65, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setTextFont(new FlexoFont("Lucida Sans", Font.PLAIN, 10));
		return makePaletteElement(annotation, new DataSourceGR(annotation, null), DROP_ON_ROLE_OR_PETRI_GRAPH);

	}

	public WKFPaletteElement makeStockElement(int x, int y) {
		WKFStockObject annotation = new WKFStockObject((FlexoProcess) null);
		annotation.setText(FlexoLocalization.localizedForKey("stock"));
		annotation.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setTextFont(new FlexoFont("Lucida Sans", Font.PLAIN, 10));
		annotation.setLabelX(25, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		annotation.setLabelY(45, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(annotation, new StockObjectGR(annotation, null), DROP_ON_ROLE_OR_PETRI_GRAPH);
	}

	private WKFPaletteElement makeINCLUSIVEOperatorElement(int x, int y) {
		final InclusiveOperator operator = new InclusiveOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(30, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorInclusiveGR(operator, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeEXCLUSIVEEVENTBASEDOperatorElement(int x, int y) {
		final ExclusiveEventBasedOperator operator = new ExclusiveEventBasedOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(30, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorExclusiveEventBasedGR(operator, null, true),
				DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	private WKFPaletteElement makeMultipleInstanceSubProcessNodeElement(int x, int y, int width, int height) {
		final MultipleInstanceSubProcessNode node = new MultipleInstanceSubProcessNode((FlexoProcess) null);
		node.setIsSequential(false);
		node.setName(FlexoLocalization.localizedForKey("parallel_sub_process"));
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
		return makePaletteElement(node, gr, DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP_FOR_INTERACTIVE_NODE);
	}

	private WKFPaletteElement makeCOMPLEXOperatorElement(int x, int y) {
		final ComplexOperator operator = new ComplexOperator((FlexoProcess) null);
		operator.setName(operator.getDefaultName());
		operator.setX(x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setY(y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelX(30, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		operator.setLabelY(55, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		return makePaletteElement(operator, new OperatorComplexGR(operator, null, true), DROP_ON_PROCESS_OR_ACTIVITY_PG_OR_ACTIVITY_GROUP);
	}

	public WKFPaletteElement getDataSource() {
		return dataSource;
	}

	public WKFPaletteElement getBoundingBox() {
		return boundingBox;
	}
}
