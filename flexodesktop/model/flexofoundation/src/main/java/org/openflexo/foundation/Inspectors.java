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
package org.openflexo.foundation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.toolbox.FileResource;


/**
 * Defines all the inspectors used in the project
 * 
 * @author sguerin
 * 
 */
public class Inspectors {
	protected static final Logger logger = Logger.getLogger(Inspectors.class.getPackage().getName());

	public static final COMMONInspectors COMMON = new COMMONInspectors();

	public static final WKFInspectors WKF = new WKFInspectors();

	public static final IEInspectors IE = new IEInspectors();

	public static final DMInspectors DM = new DMInspectors();

	public static final WSEInspectors WSE = new WSEInspectors();

	public static final DREInspectors DRE = new DREInspectors();

	public static final GeneratorInspectors GENERATORS = new GeneratorInspectors();

	public static final CGInspectors CG = new CGInspectors();

	public static final DEInspectors DE = new DEInspectors();

	public static final DGInspectors DG = new DGInspectors();

	public static final SGInspectors SG = new SGInspectors();

	public static final FPSInspectors FPS = new FPSInspectors();

	public static final WKFCGInspectors WKF_CG = new WKFCGInspectors();

	public static final VEInspectors VE = new VEInspectors();

	public static final VPMInspectors VPM = new VPMInspectors();

	public static final XXXInspectors XXX = new XXXInspectors();

	/**
	 * @param inspectorName
	 * @return
	 */
	public static InspectorGroup inspectorGroupForInspector(String inspectorName) {
		if (COMMON.containsInspector(inspectorName)) {
			return COMMON;
		}
		if (WKF.containsInspector(inspectorName)) {
			return WKF;
		}
		if (IE.containsInspector(inspectorName)) {
			return IE;
		}
		if (DM.containsInspector(inspectorName)) {
			return DM;
		}
		if (WSE.containsInspector(inspectorName)) {
			return WSE;
		}
		if (DRE.containsInspector(inspectorName)) {
			return DRE;
		}
		if (GENERATORS.containsInspector(inspectorName)) {
			return GENERATORS;
		}
		if (CG.containsInspector(inspectorName)) {
			return CG;
		}
		if (DE.containsInspector(inspectorName)) {
			return DE;
		}
		if (DG.containsInspector(inspectorName)) {
			return DG;
		}
		if (FPS.containsInspector(inspectorName)) {
			return FPS;
		}
		if (WKF_CG.containsInspector(inspectorName)) {
			return WKF_CG;
		}
		return null;
	}

	public static abstract class DefaultInspectorGroup implements InspectorGroup {

		private Vector<String> _allInspectors;
		private File _inspectorDirectory;

		public abstract String getName();

		public boolean containsInspector(String inspectorName) {
			return getAllInspectorNames().contains(inspectorName);
		}

		/**
		 * {@inheritDoc}
		 */
		public List<String> getAllInspectorNames() {
			if (_allInspectors == null) {
				_allInspectors = new Vector<String>();
				Field[] fields = getClass().getDeclaredFields();

				for (Field field : fields) {
					String value;
					try {
						value = (String) field.get(this);
						_allInspectors.add(value);
					} catch (IllegalArgumentException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING))
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING))
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
						e.printStackTrace();
					} catch (Exception e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING))
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
						e.printStackTrace();
					}
				}
			}
			return _allInspectors;
		}

		/**
		 * {@inheritDoc}
		 */
		public InputStream getInspectorStream(String inspectorName) {

			File nextFile = new File(getInspectorDirectory(), inspectorName);
			if (!nextFile.exists()) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("File " + nextFile.getAbsolutePath() + " not found, search everywere");
				nextFile = new FileResource("Config/Inspector/" + inspectorName);
			}

			if (nextFile.exists()) {
				try {
					return new FileInputStream(nextFile);
				} catch (FileNotFoundException e) {
					// Will not happen
					e.printStackTrace();
				}
			}
			if (logger.isLoggable(Level.WARNING))
				logger.warning("File " + nextFile.getAbsolutePath() + " REALLY not found");
			return null;
		}

		protected File getInspectorDirectory() {
			if (_inspectorDirectory == null) {
				_inspectorDirectory = new FileResource(getExpectedProjectName() + "/src/main/resources/Config/Inspector");
				if (!_inspectorDirectory.exists()) {
					_inspectorDirectory = new FileResource("Config/Inspector");
					if (!_inspectorDirectory.exists())
						logger.warning("Inspector directory NOT FOUND ! (searched " + getExpectedProjectName() + "/src/main/resources/Config/Inspector)");
				} else
					logger.info("Found " + _inspectorDirectory);
			}
			return _inspectorDirectory;
		}

		protected abstract String getExpectedProjectName();

	}

	private static final String DOCInspector = "DocForModelObject.inspector";

	public static File getDocInspectorFile() {
		return new File(COMMON.getInspectorDirectory(), DOCInspector);
	}

	// ==========================================================================
	// =============================== COMMON Inspectors ===========================
	// ==========================================================================

	public static class COMMONInspectors extends DefaultInspectorGroup {

		public String PROJECT_INSPECTOR = "Project.inspector";

		public String MODEL_OBJECT_INSPECTOR = "ModelObject.inspector";

		public String IMPORTED_MODEL_OBJECT_INSPECTOR = "ImportedModelObject.inspector";

		@Override
		public String getName() {
			return "COMMON";
		}

		@Override
		protected String getExpectedProjectName() {
			return "Flexo";
		}
	}

	// ==========================================================================
	// =============================== WKF Inspectors ===========================
	// ==========================================================================

	public static class WKFCGInspectors extends DefaultInspectorGroup {

		@Override
		public String getName() {
			return "WKF-CG";
		}

		public String FLEXO_PROCESS_CONTROL_FLOW_GRAPH_INSPECTOR = "FlexoProcessControlFlowGraph.inspector";
		public String FLEXO_NODE_CONTROL_FLOW_GRAPH_INSPECTOR = "FlexoNodeControlFlowGraph.inspector";
		public String OPERATOR_NODE_CONTROL_FLOW_GRAPH_INSPECTOR = "OperatorNodeControlFlowGraph.inspector";
		public String PRE_CONDITION_CONTROL_FLOW_GRAPH_INSPECTOR = "PreConditionControlFlowGraph.inspector";
		public String EDGE_CONTROL_FLOW_GRAPH_INSPECTOR = "EdgeControlFlowGraph.inspector";

		@Override
		protected String getExpectedProjectName() {
			return "FlexoWorkflowEditor";
		}
	}

	public static class WKFInspectors extends DefaultInspectorGroup {

		@Override
		public String getName() {
			return "WKF";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoWorkflowEditor";
		}

		public String WORKFLOW_INSPECTOR = "Workflow.inspector";

		public String PROCESS_FOLDER_INSPECTOR = "ProcessFolder.inspector";

		public String WKF_REPRESENTABLE_INSPECTOR = "WKFRepresentable.inspector";

		public String ABSTRACT_ACTIVITY_NODE_INSPECTOR = "AbstractActivityNode.inspector";

		public String ACTION_NODE_INSPECTOR = "ActionNode.inspector";

		public String DISPLAY_ACTION_NODE_INSPECTOR = "DisplayActionNode.inspector";

		public String FLEXO_ACTION_NODE_INSPECTOR = "FlexoActionNode.inspector";

		public String ACTIVITY_NODE_INSPECTOR = "ActivityNode.inspector";

		public String BEGIN_NODE_INSPECTOR = "BeginNode.inspector";

		//public String START_EVENT_INSPECTOR = "StartEvent.inspector";

		//public String END_EVENT_INSPECTOR = "EndEvent.inspector";

		public String END_NODE_INSPECTOR = "EndNode.inspector";

		public String BEGIN_ACTIVITY_NODE_INSPECTOR = "BeginActivityNode.inspector";

		public String END_ACTIVITY_NODE_INSPECTOR = "EndActivityNode.inspector";

		public String BEGIN_OPERATION_NODE_INSPECTOR = "BeginOperationNode.inspector";

		public String END_OPERATION_NODE_INSPECTOR = "EndOperationNode.inspector";

		public String FLEXO_PROCESS_INSPECTOR = "FlexoProcess.inspector";

		public String NODE_INSPECTOR = "Node.inspector";

		public String EVENT_NODE_INSPECTOR = "EventNode.inspector";

		public String FLEXO_NODE_INSPECTOR = "FlexoNode.inspector";

		public String PRE_CONDITION_INSPECTOR = "PreCondition.inspector";

		public String SUB_PROCESS_NODE_INSPECTOR = "SubProcessNode.inspector";
		public String MULTIPLE_INSTANCE_SUB_PROCESS_NODE_INSPECTOR = "MultipleInstanceSubProcessNode.inspector";
		public String SINGLE_INSTANCE_SUB_PROCESS_NODE_INSPECTOR = "SingleInstanceSubProcessNode.inspector";
		public String LOOP_SUB_PROCESS_NODE_INSPECTOR = "LoopSubProcessNode.inspector";
		public String WS_CALL_SUB_PROCESS_NODE_INSPECTOR = "WSCallSubProcessNode.inspector";

		public String OPERATION_NODE_INSPECTOR = "OperationNode.inspector";

		public String OPERATOR_NODE_INSPECTOR = "OperatorNode.inspector";
		public String OPERATOR_AND_INSPECTOR = "OperatorANDNode.inspector";
		public String OPERATOR_OR_INSPECTOR = "OperatorORNode.inspector";
		public String OPERATOR_INCLUSIVE_INSPECTOR = "OperatorInclusiveNode.inspector";
		public String OPERATOR_COMPLEX_INSPECTOR = "OperatorComplexNode.inspector";
		public String OPERATOR_EXCLUSIVE_EVENT_INSPECTOR = "OperatorExclusiveEventNode.inspector";
		public String OPERATOR_IF_INSPECTOR = "OperatorIFNode.inspector";
		public String OPERATOR_SWITCH_INSPECTOR = "OperatorSWITCHNode.inspector";
		public String OPERATOR_LOOP_INSPECTOR = "OperatorLOOPNode.inspector";

		public String STATUS_INSPECTOR = "Status.inspector";

		public String ROLE_INSPECTOR = "Role.inspector";

		public String ROLE_SPECIALIZATION_INSPECTOR = "RoleSpecialization.inspector";

		public String ROLE_LIST_INSPECTOR = "RoleList.inspector";

		public String DEADLINE_INSPECTOR = "DeadLine.inspector";

		public String EDGE_INSPECTOR = "Edge.inspector";

		public String ASSOCIATION_INSPECTOR = "Association.inspector";

		public String POST_CONDITION_INSPECTOR = "PostCondition.inspector";

		public String TOKEN_EDGE_INSPECTOR = "TokenEdge.inspector";

		/*
		 * public String OPERATOR_EDGE_INSPECTOR = "OperatorEdge.inspector";
		 * 
		 * public String OPERATOR_IN_EDGE_INSPECTOR = "OperatorInEdge.inspector";
		 * 
		 * public String OPERATOR_OUT_EDGE_INSPECTOR = "OperatorOutEdge.inspector";
		 * 
		 * //public String OPERATOR_IF_OUT_EDGE_INSPECTOR = "OperatorIFOutEdge.inspector";
		 * 
		 * public String OPERATOR_INTER_EDGE_INSPECTOR = "OperatorInterEdge.inspector";
		 * 
		 * public String OPERATOR_IF_OUT_MESSAGE_EDGE_INSPECTOR = "OperatorIFOutMessageEdge.inspector";
		 * 
		 * public String OPERATOR_OUT_MESSAGE_EDGE_INSPECTOR = "OperatorOutMessageEdge.inspector";
		 */

		public String MESSAGE_EDGE_INSPECTOR = "MessageEdge.inspector";

		public String INTERNAL_MESSAGE_IN_EDGE_INSPECTOR = "InternalMessageInEdge.inspector";

		public String INTERNAL_MESSAGE_OUT_EDGE_INSPECTOR = "InternalMessageOutEdge.inspector";

		public String EXTERNAL_MESSAGE_IN_EDGE_INSPECTOR = "ExternalMessageInEdge.inspector";

		public String EXTERNAL_MESSAGE_IN_EDGE_FOR_NEW_PORT_INSPECTOR = "ExternalMessageInEdgeForNewPort.inspector";

		public String EXTERNAL_MESSAGE_OUT_EDGE_INSPECTOR = "ExternalMessageOutEdge.inspector";

		public String FORWARD_WS_EDGE_INSPECTOR = "ForwardWSEdge.inspector";

		public String BACKWARD_WS_EDGE_INSPECTOR = "BackwardWSEdge.inspector";

		public String TRANSFER_WS_EDGE_INSPECTOR = "TransferWSEdge.inspector";

		public String PORT_INSPECTOR = "Port.inspector";
		public String ABSTRACT_IN_PORT_INSPECTOR = "AbstractInPort.inspector";
		public String NEW_PORT_INSPECTOR = "NewPort.inspector";
		public String DELETE_PORT_INSPECTOR = "DeletePort.inspector";
		public String IN_PORT_INSPECTOR = "InPort.inspector";
		public String IN_OUT_PORT_INSPECTOR = "InOutPort.inspector";
		public String OUT_PORT_INSPECTOR = "OutPort.inspector";

		public String PORTMAP_INSPECTOR = "PortMap.inspector";

		public String MESSAGE_INSPECTOR = "Message.inspector";

		public String PORT_REGISTERY_INSPECTOR = "PortRegistery.inspector";

		public String PORTMAP_REGISTERY_INSPECTOR = "PortMapRegistery.inspector";

		//public String MAIL_IN_INSPECTOR = "MailIn.inspector";

		//public String MAIL_OUT_INSPECTOR = "MailOut.inspector";

		//public String TIMER_INSPECTOR = "Timer.inspector";

		//public String TIME_OUT_INSPECTOR = "TimeOut.inspector";

		//public String FAULT_THROWER_INSPECTOR = "FaultThrower.inspector";

		//public String FAULT_HANDLER_INSPECTOR = "FaultHandler.inspector";

		//public String CANCEL_THROWER_INSPECTOR = "CancelThrower.inspector";

		//public String CANCEL_HANDLER_INSPECTOR = "CancelHandler.inspector";

		//public String COMPENSATE_THROWER_INSPECTOR = "CompensateThrower.inspector";

		//public String COMPENSATE_HANDLER_INSPECTOR = "CompensateHandler.inspector";

		//public String CHECKPOINT_INSPECTOR = "Checkpoint.inspector";

		//public String REVERT_INSPECTOR = "Revert.inspector";

		public String SELF_EXECUTABLE_ACTIVITY_INSPECTOR = "SelfExecutableActivityNode.inspector";

		public String SELF_EXECUTABLE_OPERATION_INSPECTOR = "SelfExecutableOperationNode.inspector";

		public String SELF_EXECUTABLE_ACTION_INSPECTOR = "SelfExecutableActionNode.inspector";

		public String SERVICE_INTERFACE_INSPECTOR = "ServiceInterface.inspector";

		public String SERVICE_OPERATION_INSPECTOR = "ServiceOperation.inspector";
		public String OUT_SERVICE_OPERATION_INSPECTOR = "OutServiceOperation.inspector";
		public String IN_SERVICE_OPERATION_INSPECTOR = "InServiceOperation.inspector";
		public String INOUT_SERVICE_OPERATION_INSPECTOR = "InOutServiceOperation.inspector";

		public String ACTIVITY_GROUP_INSPECTOR = "ActivityGroup.inspector";
		public String ARTEFACT_INSPECTOR = "Artefact.inspector";
		public String ANNOTATION_INSPECTOR = "Annotation.inspector";
		public String DATA_SOURCE_INSPECTOR = "DataSource.inspector";
		public String STOCK_INSPECTOR = "StockObject.inspector";
		public String DATA_OBJECT_INSPECTOR = "DataObject.inspector";
		public String BOUNDING_BOX_INSPECTOR = "BoundingBox.inspector";

		public String IMPORTED_ROLE = "ImportedRole.inspector";
		public String IMPORTED_PROCESS = "ImportedProcess.inspector";

	}

	// ==========================================================================
	// =============================== IE Inspectors ============================
	// ==========================================================================

	public static class IEInspectors extends DefaultInspectorGroup {

		public static final String COMPONENT_LIBRARY_INSPECTOR = "ComponentLibrary.inspector";

		@Override
		public String getName() {
			return "IE";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoInterfaceEditor";
		}

		public String COMPONENT_DEFINITION_INSPECTOR = "ComponentDefinition.inspector";
		public String MENU_ITEM_INSPECTOR = "FlexoItemMenu.inspector";

		public String TAB_COMPONENT_DEFINITION_INSPECTOR = "TabComponentDefinition.inspector";

		public String OPERATION_COMPONENT_DEFINITION_INSPECTOR = "OperationComponentDefinition.inspector";
		public String MONITORING_SCREEN_DEFINITION_INSPECTOR = "ScreenComponentDefinition.inspector";
		public String MONITORING_SCREEN_INSPECTOR = "ScreenComponent.inspector";

		public String POPUP_COMPONENT_DEFINITION_INSPECTOR = "PopupComponentDefinition.inspector";

		public String REUSABLE_COMPONENT_DEFINITION_INSPECTOR = "ReusableComponentDefinition.inspector";

		public String ABSTRACT_WIDGET_INSPECTOR = "AbstractWidget.inspector";

		public String BLOC_INSPECTOR = "Bloc.inspector";

		public String BROWSER_INSPECTOR = "Browser.inspector";

		public String BUTTON_CONTAINER_INSPECTOR = "ButtonContainer.inspector";

		public String CHECKBOX_INSPECTOR = "CheckBox.inspector";

		public String IMAGE_INSPECTOR = "Image.inspector";

		public String MULTIMEDIA_INSPECTOR = "Multimedia.inspector";

		public String RADIOBUTTON_INSPECTOR = "RadioButton.inspector";

		public String CONTROL_WIDGET_INSPECTOR = "ControlWidget.inspector";

		public String DROPDOWN_INSPECTOR = "DropDown.inspector";

		public String EDITABLE_TEXT_WIDGET_INSPECTOR = "EditableTextWidget.inspector";

		public String HTML_TABLE_INSPECTOR = "HTMLTable.inspector";

		public String HEADER_INSPECTOR = "Header.inspector";

		public String HYPERLINK_INSPECTOR = "Hyperlink.inspector";

		public String INNER_TABLE_WIDGET_INSPECTOR = "InnerTableWidget.inspector";

		public String LABEL_INSPECTOR = "Label.inspector";

		public String FILE_UPLOAD_INSPECTOR = "FileUpload.inspector";

		public String LIST_INSPECTOR = "List.inspector";

		public String NON_EDITABLE_TEXT_WIDGET_INSPECTOR = "NonEditableTextWidget.inspector";

		public String OPERATION_COMPONENT_INSPECTOR = "OperationComponent.inspector";

		public String POPUP_COMPONENT_INSPECTOR = "PopupComponent.inspector";

		public String REUSABLE_COMPONENT_INSPECTOR = "ReusableComponent.inspector";

		public String BUTTON_INSPECTOR = "Button.inspector";

		public String BIRT_INSPECTOR = "BIRTWidget.inspector";;

		public String STRING_INSPECTOR = "String.inspector";

		public String TD_INSPECTOR = "TD.inspector";

		public String TR_INSPECTOR = "TR.inspector";

		public String TABLE_INSPECTOR = "Table.inspector";

		public String TEXTAREA_INSPECTOR = "TextArea.inspector";

		public String TEXTFIELD_INSPECTOR = "TextField.inspector";

		public String TAB_INSPECTOR = "Tab.inspector";

		public String TAB_COMPONENT_INSPECTOR = "TabComponent.inspector";

		public String TAB_CONTAINER_INSPECTOR = "TabContainer.inspector";

		public String WO_COMPONENT_INSPECTOR = "WOComponent.inspector";

		public String WIDGET_INSPECTOR = "Widget.inspector";

		public String WYSIWYG_INSPECTOR = "Wysiwyg.inspector";

		public String FOLDER_INSPECTOR = "Folder.inspector";

		public String DOMAIN_INSPECTOR = "Domain.inspector";

		public String VALUE_INSPECTOR = "Value.inspector";

		public String KEY_INSPECTOR = "Key.inspector";

		public String LANGUAGE_INSPECTOR = "Language.inspector";

		public String DKV_OBJECT_INSPECTOR = "DKVObject.inspector";

		// public String CONDITIONAL_INSPECTOR = "Conditional.inspector";

		// public String REPETITION_INSPECTOR = "Repetition.inspector";

		public String REUSABLE_WIDGET_INSPECTOR = "ReusableWidget.inspector";

		public String CONDITIONAL_SEQUENCE_INSPECTOR = "ConditionalSequence.inspector";

		public String REPETITION_SEQUENCE_INSPECTOR = "RepetitionSequence.inspector";

	}

	// ==========================================================================
	// =============================== DM Inspectors ============================
	// ==========================================================================

	public static class DMInspectors extends DefaultInspectorGroup {

		@Override
		public String getName() {
			return "DM";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoDataModelEditor";
		}

		public String DM_OBJECT_INSPECTOR = "DMObject.inspector";

		public String DM_RO_OBJECT_INSPECTOR = "DMROObject.inspector";

		public String DM_MODEL_INSPECTOR = "DMModel.inspector";

		public String DM_REPOSITORY_INSPECTOR = "DMRepository.inspector";

		public String DM_WOREPOSITORY_INSPECTOR = "DMWORepository.inspector";
		public String DM_COMPONENTS_REPOSITORY_INSPECTOR = "DMComponentRepository.inspector";

		public String DM_RO_REPOSITORY_INSPECTOR = "DMRORepository.inspector";

		public String DM_REPOSITORY_FOLDER_INSPECTOR = "DMRepositoryFolder.inspector";

		public String DM_PACKAGE_INSPECTOR = "DMPackage.inspector";

		public String DM_RO_PACKAGE_INSPECTOR = "DMROPackage.inspector";

		public String DM_ENTITY_INSPECTOR = "DMEntity.inspector";

		public String DM_RO_ENTITY_INSPECTOR = "DMROEntity.inspector";

		public String DM_PROPERTY_INSPECTOR = "DMProperty.inspector";

		public String DM_RO_PROPERTY_INSPECTOR = "DMROProperty.inspector";

		public String DM_METHOD_INSPECTOR = "DMMethod.inspector";

		public String DM_RO_METHOD_INSPECTOR = "DMROMethod.inspector";

		public String DM_EO_REPOSITORY_INSPECTOR = "DMEORepository.inspector";

		public String DM_RO_EO_REPOSITORY_INSPECTOR = "DMROEORepository.inspector";

		public String DM_EO_MODEL_INSPECTOR = "DMEOModel.inspector";

		public String DM_RO_EO_MODEL_INSPECTOR = "DMROEOModel.inspector";

		public String DM_EO_ENTITY_INSPECTOR = "DMEOEntity.inspector";

		public String DM_RO_EO_ENTITY_INSPECTOR = "DMROEOEntity.inspector";

		public String DM_EO_PROPERTY_INSPECTOR = "DMEOProperty.inspector";

		public String DM_RO_EO_PROPERTY_INSPECTOR = "DMROEOProperty.inspector";

		public String DM_EO_ATTRIBUTE_INSPECTOR = "DMEOAttribute.inspector";

		public String DM_RO_EO_ATTRIBUTE_INSPECTOR = "DMROEOAttribute.inspector";

		public String DM_EO_RELATIONSHIP_INSPECTOR = "DMEORelationship.inspector";

		public String DM_EO_FLATTEN_RELATIONSHIP_INSPECTOR = "DMEOFlattenRelationship.inspector";

		public String DM_RO_EO_RELATIONSHIP_INSPECTOR = "DMROEORelationship.inspector";

		public String DM_EO_PROTOTYPE_INSPECTOR = "DMEOPrototype.inspector";

		public String DM_RO_EO_PROTOTYPE_INSPECTOR = "DMROEOPrototype.inspector";

		public String DM_TRANSTYPER_INSPECTOR = "DMTranstyper.inspector";

		public String ER_DIAGRAM_INSPECTOR = "ERDiagram.inspector";

	}

	// ==========================================================================
	// =============================== WSE Inspectors ============================
	// ==========================================================================

	public static class WSEInspectors extends DefaultInspectorGroup {
		@Override
		public String getName() {
			return "WSE";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoWebServiceEditor";
		}

		public String WSSERVICE_INSPECTOR = "WSService.inspector";
		public String WSOBJECT_INSPECTOR = "WSObject.inspector";
		public String WSFLEXOPROCESS_INSPECTOR = "WSFlexoProcess.inspector";
		public String WSPORT_INSPECTOR = "WSPort.inspector";
		public String WSINPORT_INSPECTOR = "WSInPort.inspector";
		public String WSOUTPORT_INSPECTOR = "WSOutPort.inspector";
		public String WSINOUTPORT_INSPECTOR = "WSInOutPort.inspector";
		public String WSABSTRACTINPORT_INSPECTOR = "WSAbstractInPort.inspector";
		public String WSEXTERNALSERVICE_INSPECTOR = "WSExternalService.inspector";
		public String WSINTERNALSERVICE_INSPECTOR = "WSInternalService.inspector";
		public String WSMESSAGEDEFINITION_INSPECTOR = "WSMessageDefinition.inspector";
	}

	// ==========================================================================
	// =============================== DRE Inspectors ============================
	// ==========================================================================

	public static class DREInspectors extends DefaultInspectorGroup {
		@Override
		public String getName() {
			return "DRE";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoDocEditorModule";
		}

		public String DOC_ITEM_FOLDER_INSPECTOR = "DocItemFolder.inspector";

		public String DOC_ITEM_INSPECTOR = "DocItem.inspector";
	}

	// ==========================================================================
	// =============================== CG Inspectors ============================
	// ==========================================================================

	public static class GeneratorInspectors extends DefaultInspectorGroup {
		public String CG_FOLDER_INSPECTOR = "CGFolder.inspector";
		public String CG_FILE_INSPECTOR = "CGFile.inspector";
		public String CG_SYMB_DIR_INSPECTOR = "CGSymbDir.inspector";
		public String BEFORE_FIRST_RELEASE_INSPECTOR = "BeforeFirstRelease.inspector";
		public String FILE_RELEASE_VERSION_INSPECTOR = "CGFileReleaseVersion.inspector";
		public String FILE_INTERMEDIATE_VERSION_INSPECTOR = "CGFileIntermediateVersion.inspector";
		public String CG_RELEASE_INSPECTOR = "CGRelease.inspector";
		public String CG_TEMPLATE_FILE = "CGTemplateFile.inspector";
		public String CG_TARGET_SPECIFIC_TEMPLATE_SET = "CGTargetSpecificTemplateSet.inspector";
		public String CG_CUSTOM_TEMPLATE_REPOSITORY = "CGCustomTemplateRepository.inspector";
		public String CG_TEMPLATES = "CGTemplates.inspector";

		@Override
		public String getName() {
			return "Generator";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoGenerator";
		}
	}

	public static class DEInspectors extends DefaultInspectorGroup {
		public String DE_TOC_REPOSITORY_INSPECTOR = "TOCRepository.inspector";
		public String DE_TOC_ENTRY_INSPECTOR = "TOCEntry.inspector";

		@Override
		public String getName() {
			return "DE";
		}

		@Override
		protected String getExpectedProjectName() {
			// Note: Since DGModule uses inspectors of DEModule, we need to put the inspectors in Flexo so that they are always packaged
			// DEModule and DGModule cannot be package together
			return "Flexo";
		}
	}

	public static class DGInspectors extends DefaultInspectorGroup {
		public String GENERATED_DOC_INSPECTOR = "GeneratedDoc.inspector";
		public String DG_REPOSITORY_LATEX_INSPECTOR = "DGLatexRepository.inspector";
		public String DG_REPOSITORY_HTML_INSPECTOR = "DGHTMLRepository.inspector";
		public String DG_REPOSITORY_DOCX_INSPECTOR = "DGDocxRepository.inspector";
		/**
		 * MOS
		 * @author MOSTAFA
		 * TODO_MOS
		 */
		public String DG_REPOSITORY_PPTX_INSPECTOR = "DGPptxRepository.inspector";
		//
		public String DG_REPOSITORY_INSPECTOR = "DGRepository.inspector";
		public String DG_APPLICATION_TEMPLATE_REPOSITORY = "DGApplicationTemplateRepository.inspector";

		@Override
		public String getName() {
			return "DG";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoDocGeneratorModule";
		}
	}

	public static class CGInspectors extends DefaultInspectorGroup {
		@Override
		public String getName() {
			return "CG";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoCodeGeneratorModule";
		}

		public String GENERATED_CODE_INSPECTOR = "GeneratedCode.inspector";
		public String CG_REPOSITORY_INSPECTOR = "CGRepository.inspector";

		public String CG_JAVA_FILE_INSPECTOR = "CGJavaFile.inspector";
		// Java parsing inspectors
		public String JAVA_SOURCE_INSPECTOR = "FJPJavaSource.inspector";
		public String JAVA_PACKAGE_INSPECTOR = "FJPPackageDeclaration.inspector";
		public String JAVA_IMPORTS_INSPECTOR = "FJPImportDeclarations.inspector";
		public String JAVA_IMPORT_INSPECTOR = "FJPImportDeclaration.inspector";
		public String JAVA_ENTITY_INSPECTOR = "FJPJavaEntity.inspector";
		public String JAVA_CLASS_INSPECTOR = "FJPJavaClass.inspector";
		public String JAVA_METHOD_INSPECTOR = "FJPJavaMethod.inspector";
		public String JAVA_FIELD_INSPECTOR = "FJPJavaField.inspector";

		public String CG_APPLICATION_TEMPLATE_REPOSITORY = "CGApplicationTemplateRepository.inspector";

	}

	public static class SGInspectors extends DefaultInspectorGroup {
		@Override
		public String getName() {
			return "SG";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoSourceGeneratorModule";
		}

		public String GENERATED_SOURCES_INSPECTOR = "GeneratedSources.inspector";
		public String SOURCE_REPOSITORY_INSPECTOR = "SGRepository.inspector";

		public String SG_JAVA_FILE_INSPECTOR = "SGJavaFile.inspector";

		// Java parsing inspectors
		public String JAVA_SOURCE_INSPECTOR = "FJPJavaSource.inspector";
		public String JAVA_PACKAGE_INSPECTOR = "FJPPackageDeclaration.inspector";
		public String JAVA_IMPORTS_INSPECTOR = "FJPImportDeclarations.inspector";
		public String JAVA_IMPORT_INSPECTOR = "FJPImportDeclaration.inspector";
		public String JAVA_ENTITY_INSPECTOR = "FJPJavaEntity.inspector";
		public String JAVA_CLASS_INSPECTOR = "FJPJavaClass.inspector";
		public String JAVA_METHOD_INSPECTOR = "FJPJavaMethod.inspector";
		public String JAVA_FIELD_INSPECTOR = "FJPJavaField.inspector";

		public String SG_APPLICATION_TEMPLATE_REPOSITORY = "SGApplicationTemplateRepository.inspector";
	}

	// ==========================================================================
	// =============================== FPS Inspectors ============================
	// ==========================================================================

	public static class FPSInspectors extends DefaultInspectorGroup {
		@Override
		public String getName() {
			return "FPS";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoPrjSharingModule";
		}

		public String CVS_REPOSITORY_LIST_INSPECTOR = "CVSRepositoryList.inspector";
		public String CVS_REPOSITORY_INSPECTOR = "CVSRepository.inspector";
		public String CVS_MODULE_INSPECTOR = "CVSModule.inspector";
		public String SHARED_PROJECT_INSPECTOR = "SharedProject.inspector";
		public String CVS_DIRECTORY_INSPECTOR = "CVSDirectory.inspector";
		public String CVS_FILE_INSPECTOR = "CVSFile.inspector";

	}

	// ==========================================================================
	// =============================== OE Inspectors ============================
	// ==========================================================================

	public static class VEInspectors extends DefaultInspectorGroup {
		@Override
		public String getName() {
			return "OE";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoOntologyEditor";
		}

		public String FLEXO_ONTOLOGY_INSPECTOR = "Ontology.inspector";
		public String PROJECT_ONTOLOGY_INSPECTOR = "ProjectOntology.inspector";
		public String ONTOLOGY_LIBRARY_INSPECTOR = "OntologyLibrary.inspector";
		public String IMPORTED_ONTOLOGY_INSPECTOR = "ImportedOntology.inspector";

		public String ONTOLOGY_OBJECT_INSPECTOR = "OntologyObject.inspector";
		public String ONTOLOGY_CLASS_INSPECTOR = "OntologyClass.inspector";
		public String ONTOLOGY_INDIVIDUAL_INSPECTOR = "OntologyIndividual.inspector";
		public String ONTOLOGY_DATA_PROPERTY_INSPECTOR = "OntologyDataProperty.inspector";
		public String ONTOLOGY_OBJECT_PROPERTY_INSPECTOR = "OntologyObjectProperty.inspector";
		public String ONTOLOGY_CLASS_READ_ONLY_INSPECTOR = "OntologyClassRO.inspector";
		public String ONTOLOGY_INDIVIDUAL_READ_ONLY_INSPECTOR = "OntologyIndividualRO.inspector";
		public String ONTOLOGY_DATA_PROPERTY_READ_ONLY_INSPECTOR = "OntologyDataPropertyRO.inspector";
		public String ONTOLOGY_OBJECT_PROPERTY_READ_ONLY_INSPECTOR = "OntologyObjectPropertyRO.inspector";
		public String ONTOLOGY_STATEMENT_INSPECTOR = "OntologyStatement.inspector";

		public String OE_SHEMA_LIBRARY_INSPECTOR = "OEShemaLibrary.inspector";
		public String OE_SHEMA_FOLDER_INSPECTOR = "OEShemaFolder.inspector";
		public String OE_SHEMA_INSPECTOR = "OEShema.inspector";
		public String OE_SHEMA_DEFINITION_INSPECTOR = "OEShemaDefinition.inspector";
		public String OE_SHAPE_INSPECTOR = "OEShape.inspector";
		public String OE_CONNECTOR_INSPECTOR = "OEConnector.inspector";

	}

	// ==========================================================================
	// =============================== OE Inspectors ============================
	// ==========================================================================

	public static class VPMInspectors extends DefaultInspectorGroup {
		@Override
		public String getName() {
			return "CED";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoCalcEditor";
		}

		public String CALC_LIBRARY_INSPECTOR = "CalcLibrary.inspector";

		public String ONTOLOGY_CALC_INSPECTOR = "OntologyCalc.inspector";

		public String CALC_PALETTE_INSPECTOR = "CalcPalette.inspector";
		public String CALC_PALETTE_ELEMENT_INSPECTOR = "CalcPaletteElement.inspector";

		public String EDITION_PATTERN_INSPECTOR = "EditionPattern.inspector";
		public String PATTERN_ROLE_INSPECTOR = "PatternRole.inspector";
		public String SHAPE_PATTERN_ROLE_INSPECTOR = "ShapePatternRole.inspector";
		public String CONNECTOR_PATTERN_ROLE_INSPECTOR = "ConnectorPatternRole.inspector";

		public String EDITION_PATTERN_PARAMETER_INSPECTOR = "EditableConceptParameter.inspector";

		public String DROP_SCHEME_INSPECTOR = "DropScheme.inspector";
		public String LINK_SCHEME_INSPECTOR = "LinkScheme.inspector";
		public String ACTION_SCHEME_INSPECTOR = "ActionScheme.inspector";

		public String ADD_SHAPE_INSPECTOR = "AddShape.inspector";
		public String ADD_CONNECTOR_INSPECTOR = "AddConnector.inspector";
		public String ADD_SHEMA_INSPECTOR = "AddShema.inspector";
		public String ADD_INDIVIDUAL_INSPECTOR = "AddIndividual.inspector";
		public String ADD_CLASS_INSPECTOR = "AddClass.inspector";
		public String DATA_PROPERTY_ASSERTION_INSPECTOR = "DataPropertyAssertion.inspector";
		public String OBJECT_PROPERTY_ASSERTION_INSPECTOR = "ObjectPropertyAssertion.inspector";
		public String ADD_OBJECT_PROPERTY_INSPECTOR = "AddObjectProperty.inspector";
		public String ADD_IS_A_PROPERTY_INSPECTOR = "AddIsAProperty.inspector";
		public String ADD_RESTRICTION_INSPECTOR = "AddRestriction.inspector";
		public String GO_TO_OBJECT_INSPECTOR = "GoToObject.inspector";

		public String CALC_DRAWING_SHEMA_INSPECTOR = "CalcDrawingShema.inspector";
		public String CALC_DRAWING_SHAPE_INSPECTOR = "CalcDrawingShape.inspector";
		public String CALC_DRAWING_CONNECTOR_INSPECTOR = "CalcDrawingConnector.inspector";

	}

	// ==========================================================================
	// =============================== XXX Inspectors ============================
	// ==========================================================================

	public static class XXXInspectors extends DefaultInspectorGroup {
		@Override
		public String getName() {
			return "XXX";
		}

		@Override
		protected String getExpectedProjectName() {
			return "FlexoNewModule";
		}

		// Example module: contains no inspectors
	}

}