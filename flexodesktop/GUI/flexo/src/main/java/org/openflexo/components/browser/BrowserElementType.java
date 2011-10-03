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
package org.openflexo.components.browser;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFolder;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GeneratedCode;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFolder;
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.foundation.cg.templates.CGTemplateSet;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.version.CGFileIntermediateVersion;
import org.openflexo.foundation.cg.version.CGFileReleaseVersion;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.foundation.dkv.Value;
import org.openflexo.foundation.dkv.Domain.KeyList;
import org.openflexo.foundation.dkv.Domain.ValueList;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.DMRepositoryFolder;
import org.openflexo.foundation.dm.DMTranstyper;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.JDKRepository;
import org.openflexo.foundation.dm.WORepository;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.MonitoringScreenDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.widget.IEBIRTWidget;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEBrowserWidget;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IEDynamicImage;
import org.openflexo.foundation.ie.widget.IEFileUploadWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEHeaderWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.foundation.ie.widget.IEMultimediaWidget;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWysiwygWidget;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyFolder;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.view.OEConnector;
import org.openflexo.foundation.view.OEShape;
import org.openflexo.foundation.view.OEShema;
import org.openflexo.foundation.view.OEShemaDefinition;
import org.openflexo.foundation.view.OEShemaFolder;
import org.openflexo.foundation.view.OEShemaLibrary;
import org.openflexo.foundation.viewpoint.CalcDrawingConnector;
import org.openflexo.foundation.viewpoint.CalcDrawingShape;
import org.openflexo.foundation.viewpoint.CalcDrawingShema;
import org.openflexo.foundation.viewpoint.CalcFolder;
import org.openflexo.foundation.viewpoint.CalcLibrary;
import org.openflexo.foundation.viewpoint.CalcPalette;
import org.openflexo.foundation.viewpoint.CalcPaletteElement;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.OntologyCalc;
import org.openflexo.foundation.wkf.DeadLine;
import org.openflexo.foundation.wkf.DeadLineList;
import org.openflexo.foundation.wkf.FlexoImportedProcessLibrary;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.StatusList;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.ExclusiveEventBasedOperator;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.InclusiveOperator;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SWITCHOperator;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.MessageBindings;
import org.openflexo.foundation.wkf.ws.MessageDefinition;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.ws.ExternalWSFolder;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.ws.InternalWSFolder;
import org.openflexo.foundation.ws.InternalWSService;
import org.openflexo.foundation.ws.WSPortType;
import org.openflexo.foundation.ws.WSPortTypeFolder;
import org.openflexo.foundation.ws.WSRepository;
import org.openflexo.foundation.ws.WSRepositoryFolder;
import org.openflexo.icon.CGIconLibrary;
import org.openflexo.icon.DEIconLibrary;
import org.openflexo.icon.DGIconLibrary;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.icon.DREIconLibrary;
import org.openflexo.icon.FPSIconLibrary;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.JavaIconLibrary;
import org.openflexo.icon.OntologyIconLibrary;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.icon.WSEIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * Defines types of browsing elements
 *
 * @author sguerin
 * @author Emmanuel Koch, Blue Pimento Services SPRL
 */
public enum BrowserElementType {

	PROJECT("project", FlexoProject.class, IconLibrary.PROJECT_ICON),
	WORKFLOW("workflow", FlexoWorkflow.class,WKFIconLibrary.WORKFLOW_ICON),
	IMPORTED_PROCESS_LIBRARY("imported_process_library",FlexoImportedProcessLibrary.class,WKFIconLibrary.IMPORTED_PROCESS_LIBRARY_ICON),
	PROCESS("process",FlexoProcess.class,WKFIconLibrary.PROCESS_ICON),
	PROCESS_NODE("process",FlexoProcessNode.class,WKFIconLibrary.PROCESS_ICON),
	PROCESS_FOLDER("process_folder",ProcessFolder.class,WKFIconLibrary.PROCESS_FOLDER_ICON),
	SUBPROCESS_NODE("subprocess_node",SubProcessNode.class,WKFIconLibrary.SUBPROCESS_NODE_ICON),
	ACTIVITY_NODE("activity_node",ActivityNode.class,WKFIconLibrary.ACTIVITY_NODE_ICON),
	OPERATION_NODE("operation_node",OperationNode.class,WKFIconLibrary.OPERATION_NODE_ICON),
	ACTION_NODE("action_node",ActionNode.class,WKFIconLibrary.ACTION_NODE_ICON),
	EVENT_NODE("event_node",EventNode.class,WKFIconLibrary.EVENT_ICON/* defined in element itself */),
	OPERATOR_AND_NODE("and_operator",ANDOperator.class,WKFIconLibrary.OPERATOR_AND_ICON),
	OPERATOR_INCLUSIVE_NODE("inclusive_operator",InclusiveOperator.class,WKFIconLibrary.OPERATOR_INCLUSIVE_ICON),
	OPERATOR_EXCLUSIVE_NODE("exclusive_operator",ExclusiveEventBasedOperator.class,WKFIconLibrary.OPERATOR_EXCLUSIVE_ICON),
	OPERATOR_COMPLEX_NODE("complex_operator",ComplexOperator.class,WKFIconLibrary.OPERATOR_COMPLEX_ICON),
	OPERATOR_OR_NODE("or_operator",OROperator.class,WKFIconLibrary.OPERATOR_OR_ICON),
	OPERATOR_IF_NODE("if_operator",IFOperator.class,WKFIconLibrary.OPERATOR_IF_ICON),
	OPERATOR_LOOP_NODE("loop_operator",LOOPOperator.class,WKFIconLibrary.OPERATOR_LOOP_ICON),
	OPERATOR_SWITCH_NODE("switch_operator",SWITCHOperator.class,WKFIconLibrary.OPERATOR_SWITCH_ICON),
	PRECONDITION("precondition",FlexoPreCondition.class,WKFIconLibrary.PRECONDITION_ICON),
	POSTCONDITION("postcondition",FlexoPostCondition.class,WKFIconLibrary.POSTCONDITION_ICON),
	GROUP("group",WKFGroup.class,WKFIconLibrary.ACTIVITY_GROUP_ICON),
	ROLE("role",Role.class,WKFIconLibrary.ROLE_ICON),
	@Deprecated
	ROLE_LIST("role_list",RoleList.class,WKFIconLibrary.ROLE_LIBRARY_ICON),
	STATUS_LIST("status_list",StatusList.class,WKFIconLibrary.STATUS_ICON),
	STATUS("status",Status.class,STATUS_LIST.getIcon()),
	@Deprecated
	DEADLINE_LIST("deadline_list",DeadLineList.class,null),
	DEADLINE("deadline",DeadLine.class,DEADLINE_LIST.getIcon()),
	COMPONENT("component",ComponentDefinition.class,SEIconLibrary.SCREEN_COMPONENT_ICON),
	OPERATION_COMPONENT("operation_component",OperationComponentDefinition.class,SEIconLibrary.OPERATION_COMPONENT_ICON),
	REUSABLE_COMPONENT("reusable_component",ReusableComponentDefinition.class,SEIconLibrary.REUSABLE_COMPONENT_ICON),
	REUSABLE_WIDGET("reusable_widget",IEReusableWidget.class,SEIconLibrary.REUSABLEWIDGET_ICON),
	COMPONENT_FOLDER("component_folder",FlexoComponentFolder.class,SEIconLibrary.IE_FOLDER_ICON),
	COMPONENT_LIBRARY("component_library",FlexoComponentLibrary.class,SEIconLibrary.COMPONENT_LIBRARY_ICON),
	TAB_COMPONENT("tab_component",TabComponentDefinition.class,SEIconLibrary.TAB_COMPONENT_ICON),
	POPUP_COMPONENT("popup_component",PopupComponentDefinition.class,SEIconLibrary.POPUP_COMPONENT_ICON),
	SCREENCOMPONENTDEFINITION("popup_component",MonitoringScreenDefinition.class,SEIconLibrary.SCREEN_COMPONENT_ICON),
	PORT_REGISTERY("port_registery",PortRegistery.class,WKFIconLibrary.PORT_REGISTERY_ICON),
	PORT("port",FlexoPort.class,WKFIconLibrary.PORT_REGISTERY_ICON),
	MESSAGE("message",MessageBindings.class,IconLibrary.FOLDER_ICON),
	SERVICE_INTERFACE("service_interface",ServiceInterface.class,WKFIconLibrary.PORT_REGISTERY_ICON),
	SERVICE_OPERATION("service_operation",ServiceOperation.class,WKFIconLibrary.PORT_REGISTERY_ICON),
	MESSAGE_DEFINITION("message_definition",MessageDefinition.class,IconLibrary.FOLDER_ICON),
	PORTMAP_REGISTERY("portmap_registery",PortMapRegistery.class,WKFIconLibrary.PORT_REGISTERY_ICON),
	PORTMAP("portmap",FlexoPortMap.class,WKFIconLibrary.PORT_REGISTERY_ICON),
	BLOC("bloc",IEBlocWidget.class,SEIconLibrary.BLOC_ICON),
	BUTTON("button",IEButtonWidget.class,SEIconLibrary.BUTTON_ICON),
	BIRT("birt",IEBIRTWidget.class,SEIconLibrary.BIRT_ICON),
	DROPDOWN("dropdown",IEDropDownWidget.class,SEIconLibrary.DROPDOWN_ICON),
	DYNAMICIMAGE("dynamicImage",IEDynamicImage.class,SEIconLibrary.IMAGE_FILE),
	MULTIMEDIA("multimedia",IEMultimediaWidget.class,SEIconLibrary.SMALL_MULTIMEDIA),
	TEXTFIELD("textfield",IETextFieldWidget.class,SEIconLibrary.TEXTFIELD_ICON),
	TEXTAREA("textarea",IETextAreaWidget.class,SEIconLibrary.TEXTAREA_ICON),
	TAB_CONTAINER("tab_container",IESequenceTab.class,SEIconLibrary.THUMBNAILCONTAINER_ICON),
	TAB_WIDGET("tab_widget",IETabWidget.class,SEIconLibrary.TAB_COMPONENT_ICON),
	STRING("string",IEStringWidget.class,SEIconLibrary.STRING_ICON),
	LABEL("label",IELabelWidget.class,SEIconLibrary.LABEL_ICON),
	WYSIWYG("wysiwyg",IEWysiwygWidget.class,SEIconLibrary.WYSIWYG_ICON),
	HEADER("header",IEHeaderWidget.class,SEIconLibrary.HEADER_ICON),
	HTMLTABLE("html_table",IEHTMLTableWidget.class,SEIconLibrary.HTMLTABLE_ICON),
	TD("td",IETDWidget.class,SEIconLibrary.TD_ICON),
	TR("tr",IETRWidget.class,SEIconLibrary.TR_ICON),
	HYPERLINK("hyperlink",IEHyperlinkWidget.class,SEIconLibrary.HYPERLINK_ICON),
	CHECKBOX("checkbox",IECheckBoxWidget.class,SEIconLibrary.CHECKBOX_ICON),
	REPETITION("repetition",RepetitionOperator.class,SEIconLibrary.REPETITION_ICON),
	CONDITIONAL("conditional",ConditionalOperator.class,SEIconLibrary.CONDITIONAL_ICON),
	SEQUENCE("sequence",IESequence.class,SEIconLibrary.CONDITIONAL_ICON),
	FILEUPLOAD("fileupload",IEFileUploadWidget.class,SEIconLibrary.FILEUPLOAD_ICON),
	BROWSERWIDGET("browser",IEBrowserWidget.class,SEIconLibrary.BROWSER_ICON),
	RADIOBUTTON("radiobutton",IERadioButtonWidget.class,SEIconLibrary.RADIOBUTTON_ICON),
	MENU_ITEM("menuitem",FlexoItemMenu.class,SEIconLibrary.MENUITEM_ICON),
	DM_MODEL("data_model",DMModel.class,DMEIconLibrary.DM_MODEL_ICON),
	DM_REPOSITORY_FOLDER("repository_folder",DMRepositoryFolder.class,DMEIconLibrary.DM_REPOSITORY_FOLDER_ICON),
	DM_REPOSITORY("repository",DMRepository.class,DMEIconLibrary.DM_REPOSITORY_ICON),
	JDK_REPOSITORY("jdk_repository",JDKRepository.class,DMEIconLibrary.JDK_REPOSITORY_ICON),
	WO_REPOSITORY("wo_repository",WORepository.class,DMEIconLibrary.WO_REPOSITORY_ICON),
	EXTERNAL_REPOSITORY("external_repository",ExternalRepository.class,DMEIconLibrary.DM_JAR_REPOSITORY_ICON),
	DM_EOPROTOTYPES_REPOSITORY("eoprototype_repository",EOPrototypeRepository.class,DMEIconLibrary.DM_EOREPOSITORY_ICON),
	DM_EXECUTION_MODEL_REPOSITORY("execution_model_repository",FlexoExecutionModelRepository.class,DMEIconLibrary.DM_EOREPOSITORY_ICON),
	DM_EOREPOSITORY("eo_repository",DMEORepository.class,DMEIconLibrary.DM_EOREPOSITORY_ICON),
	DM_PACKAGE("package",DMPackage.class,DMEIconLibrary.DM_PACKAGE_ICON),
	DM_EOMODEL("eo_model",DMEOModel.class,DMEIconLibrary.DM_EOMODEL_ICON),
	DM_ENTITY("entity",DMEntity.class,DMEIconLibrary.DM_ENTITY_ICON),
	DM_EOENTITY("eo_entity",DMEOEntity.class,DMEIconLibrary.DM_EOENTITY_ICON),
	DM_PROPERTY("property",DMProperty.class,DMEIconLibrary.DM_PROPERTY_ICON),
	DM_METHOD("method",DMMethod.class,DMEIconLibrary.DM_METHOD_ICON),
	DM_EOATTRIBUTE("eo_attribute",DMEOAttribute.class,DMEIconLibrary.DM_EOATTRIBUTE_ICON),
	DM_EORELATIONSHIP("eo_relationship",DMEORelationship.class,DMEIconLibrary.DM_EORELATIONSHIP_ICON),
	DM_TRANSTYPER("transtyper",DMTranstyper.class,DMEIconLibrary.DM_TRANSTYPER_ICON),
	DKV_MODEL("dkv_model",DKVModel.class,SEIconLibrary.DOMAIN_ICON),
	DKV_KEY("key",Key.class,SEIconLibrary.DKV_KEY_ICON),
	DKV_LANGUAGE("language",Language.class,SEIconLibrary.LANGUAGE_ICON),
	DKV_DOMAIN("domain",Domain.class,SEIconLibrary.DOMAIN_ICON),
	DKV_DOMAIN_LIST("domain_list",DKVModel.DomainList.class,SEIconLibrary.LIST_ICON),
	DKV_LANGUAGE_LIST("language_list",DKVModel.LanguageList.class,SEIconLibrary.LIST_ICON),
	DKV_KEY_LIST("dkv_key_list",KeyList.class,SEIconLibrary.LIST_ICON),
	DKV_VALUE_LIST("dkv_value_list",ValueList.class,SEIconLibrary.LIST_ICON),
	DKV_VALUE("dkv_value",Value.class,SEIconLibrary.VALUE_ICON),
	WS_LIBRARY("ws_library",FlexoWSLibrary.class,WSEIconLibrary.WS_LIBRARY_ICON),
	EXTERNAL_WS_SERVICE("external_ws_service",ExternalWSService.class,WSEIconLibrary.EXTERNAL_WS_SERVICE_ICON),
	INTERNAL_WS_SERVICE("internal_ws_service",InternalWSService.class,WSEIconLibrary.INTERNAL_WS_SERVICE_ICON),
	@Deprecated
	/* we use directly a FlexoProcessElement */
	WS_PORTTYPE("ws_portType",WSPortType.class,WSEIconLibrary.WS_PORTTYPE_ICON),
	@Deprecated
	/* we use directly a DMRepsitoryElementType */
	WS_REPOSITORY("ws_repository",WSRepository.class,WSEIconLibrary.WS_REPOSITORY_ICON),
	EXTERNAL_WS_FOLDER("ws_external_ws_folder",ExternalWSFolder.class,WSEIconLibrary.WS_EXTERNAL_FOLDER_ICON),
	INTERNAL_WS_FOLDER("ws_internal_ws_folder",InternalWSFolder.class,WSEIconLibrary.WS_INTERNAL_FOLDER_ICON),
	WS_PORTTYPE_FOLDER("ws_portType_folder",WSPortTypeFolder.class,WSEIconLibrary.WS_PORTTYPE_FOLDER_ICON),
	WS_REPOSITORY_FOLDER("ws_repository_folder",WSRepositoryFolder.class,WSEIconLibrary.WS_REPOSITORY_FOLDER_ICON),
	ER_DIAGRAM("diagram",ERDiagram.class,DMEIconLibrary.DIAGRAM_ICON),
	SOURCE_FILE("source_file",null/*Used outside this scope*/,JavaIconLibrary.FJP_JAVA_FILE_ICON),
	PACKAGE("package_declaration",null/*Used outside this scope*/,JavaIconLibrary.FJP_PACKAGE_ICON),
	IMPORTS("import_declarations",null/*Used outside this scope*/,JavaIconLibrary.FJP_IMPORTS_ICON),
	IMPORT("import_declaration",null/*Used outside this scope*/,JavaIconLibrary.FJP_IMPORT_ICON),
	CLASS("java_class",null/*Used outside this scope*/,JavaIconLibrary.FJP_CLASS_PUBLIC_ICON),
	FIELD("java_field",null/*Used outside this scope*/,JavaIconLibrary.FJP_FIELD_PUBLIC_ICON),
	METHOD("java_method",null/*Used outside this scope*/,JavaIconLibrary.FJP_METHOD_PUBLIC_ICON),
	PARSE_EXCEPTION("parse_error",null/*Used outside this scope*/,CGIconLibrary.UNFIXABLE_ERROR_ICON),
	TEMPLATES("templates",CGTemplates.class,IconLibrary.FOLDER_ICON),
	TEMPLATE_REPOSITORY("templates_repository",CGTemplateRepository.class,IconLibrary.FOLDER_ICON),
	TEMPLATE_SET("templates_set",CGTemplateSet.class,IconLibrary.FOLDER_ICON),
	TEMPLATE_FOLDER("templates_folder",CGTemplateFolder.class,IconLibrary.FOLDER_ICON),
	TEMPLATE_FILE("templates_set",CGTemplate.class,FilesIconLibrary.SMALL_MISC_FILE_ICON),
	GENERATED_CODE("generated_code",GeneratedCode.class,CGIconLibrary.GENERATED_CODE_ICON),
	GENERATED_CODE_REPOSITORY("generated_code_repository",GenerationRepository.class,CGIconLibrary.GENERATED_CODE_REPOSITORY_ICON),
	GENERATED_CODE_FILE("generated_code_file",CGFile.class,FilesIconLibrary.SMALL_MISC_FILE_ICON),
	GENERATED_CODE_FOLDER("generated_code_folder",CGFolder.class,CGIconLibrary.JAVA_SOURCE_FOLDER_ICON),
	GENERATED_CODE_SYMB_DIR("generated_code_symbolic_dir",CGSymbolicDirectory.class,CGIconLibrary.SYMBOLIC_FOLDER_ICON),
	GENERATED_SOURCES("generated_sources",GeneratedSources.class,CGIconLibrary.GENERATED_CODE_ICON),
	SOURCE_REPOSITORY("source_repository",SourceRepository.class,CGIconLibrary.GENERATED_CODE_REPOSITORY_ICON),
	IMPLEMENTATION_MODEL("implementation_model",ImplementationModel.class,CGIconLibrary.GENERATED_CODE_ICON),
	TECHNOLOGY_MODEL_OBJECT("technology_model_object", TechnologyModelObject.class, IconLibrary.FOLDER_ICON),
	UNDEFINED_FOLDER("folder",null/*Used outside this scope*/,IconLibrary.FOLDER_ICON),
	FILE_RELEASE_VERSION("file_versionning",CGFileReleaseVersion.class,FilesIconLibrary.SMALL_MISC_FILE_ICON),
	FILE_INTERMEDIATE_VERSION("generated_code_file_intermediate_version",CGFileIntermediateVersion.class,FilesIconLibrary.SMALL_MISC_FILE_ICON),
	DOC_ITEM_FOLDER("doc_item_folder",null/*Used outside this scope*/,DREIconLibrary.DOC_FOLDER_ICON),
	UNDOCUMENTED_DOC_ITEM("undocumented_doc_item",null/*Used outside this scope*/,DREIconLibrary.UNDOCUMENTED_DOC_ITEM_ICON),
	APPROVING_PENDING_DOC_ITEM("approving_pending_doc_item",null/*Used outside this scope*/,DREIconLibrary.APPROVING_PENDING_DOC_ITEM_ICON),
	AVAILABLE_NEW_VERSION_PENDING_DOC_ITEM("available_new_version_pending_doc_item",null/*Used outside this scope*/,DREIconLibrary.AVAILABLE_NEW_VERSION_PENDING_DOC_ITEM_ICON),
	UP_TO_DATE_DOC_ITEM("available_uptodate_doc_item",null/*Used outside this scope*/,DREIconLibrary.DOC_ITEM_ICON),
	TOC_DATA("toc_data",TOCData.class,DGIconLibrary.GENERATED_DOC_ICON),
	DOC_TYPE("doc_type",DocType.class,CGIconLibrary.TARGET_ICON),
	TOC_ENTRY("toc_entry",TOCEntry.class,DEIconLibrary.TOC_ENTRY),
	TOC_REPOSITORY("toc_repository",TOCRepository.class,DEIconLibrary.TOC_REPOSITORY),
	GENERATED_DOC("generated_doc",GeneratedDoc.class,DGIconLibrary.GENERATED_DOC_ICON),
	GENERATED_DOC_REPOSITORY("generated_doc_repository",DGRepository.class,CGIconLibrary.GENERATED_CODE_REPOSITORY_ICON),
	CVS_REPOSITORY_LIST("cvs_repository_list",null/*Used outside this scope*/,FPSIconLibrary.CVS_REPOSITORY_LIST_ICON),
	CVS_REPOSITORY("cvs_repository",null/*Used outside this scope*/,FPSIconLibrary.CVS_REPOSITORY_ICON),
	CVS_MODULE ("cvs_module",null/*Used outside this scope*/,FPSIconLibrary.CVS_MODULE_ICON),
	CVS_EXPLORER("cvs_explorer",null/*Used outside this scope*/,IconLibrary.CLOCK_ICON),
	SHARED_PROJECT("shared_project",null/*Used outside this scope*/,IconLibrary.PROJECT_ICON),
	CVS_DIRECTORY("cvs_directory",null/*Used outside this scope*/,IconLibrary.FOLDER_ICON),
	CVS_FILE("cvs_file",null/*Used outside this scope*/,FilesIconLibrary.SMALL_MISC_FILE_ICON),
	PROJECT_ONTOLOGY("project_ontology",ProjectOntology.class,OntologyIconLibrary.ONTOLOGY_ICON),
	IMPORTED_ONTOLOGY("imported_ontology",ImportedOntology.class,OntologyIconLibrary.ONTOLOGY_ICON),
	ONTOLOGY_LIBRARY("ontology_library",OntologyLibrary.class,OntologyIconLibrary.ONTOLOGY_LIBRARY_ICON),
	ONTOLOGY_FOLDER("ontology_folder",OntologyFolder.class,IconLibrary.FOLDER_ICON),
	ONTOLOGY_CLASS("ontology_class",OntologyClass.class,OntologyIconLibrary.ONTOLOGY_CLASS_ICON),
	ONTOLOGY_INDIVIDUAL("ontology_individual",OntologyIndividual.class,OntologyIconLibrary.ONTOLOGY_INDIVIDUAL_ICON),
	ONTOLOGY_DATA_PROPERTY("ontology_data_property",OntologyDataProperty.class,OntologyIconLibrary.ONTOLOGY_DATA_PROPERTY_ICON),
	ONTOLOGY_OBJECT_PROPERTY("ontology_object_property",OntologyObjectProperty.class,OntologyIconLibrary.ONTOLOGY_OBJECT_PROPERTY_ICON),
	ONTOLOGY_STATEMENT("ontology_statement",OntologyStatement.class,OntologyIconLibrary.ONTOLOGY_STATEMENT_ICON),
	CALC_LIBRARY("calc_library",CalcLibrary.class,VPMIconLibrary.CALC_LIBRARY_ICON),
	CALC_FOLDER("calc_folder",CalcFolder.class,IconLibrary.FOLDER_ICON),
	ONTOLOGY_CALC("calc",OntologyCalc.class,VPMIconLibrary.CALC_ICON),
	EDITION_PATTERN("edition_pattern",EditionPattern.class,VPMIconLibrary.EDITION_PATTERN_ICON),
	ONTOLOGY_CALC_PALETTE("palette",CalcPalette.class,VPMIconLibrary.CALC_PALETTE_ICON),
	ONTOLOGY_CALC_PALETTE_ELEMENT("palette_element",CalcPaletteElement.class,VEIconLibrary.OE_SHAPE_ICON),
	ONTOLOGY_CALC_DRAWING_SHEMA("calc_drawing_shema",CalcDrawingShema.class,VPMIconLibrary.EXAMPLE_DIAGRAM_ICON),
	ONTOLOGY_CALC_DRAWING_SHAPE("calc_drawing_shape",CalcDrawingShape.class,VPMIconLibrary.CALC_SHAPE_ICON),
	ONTOLOGY_CALC_DRAWING_CONNECTOR("calc_drawing_connector",CalcDrawingConnector.class,VPMIconLibrary.CALC_CONNECTOR_ICON),
	OE_SHEMA_LIBRARY("shema_library",OEShemaLibrary.class,VEIconLibrary.OE_SHEMA_LIBRARY_ICON),
	OE_SHEMA_FOLDER("shema_folder",OEShemaFolder.class,IconLibrary.FOLDER_ICON),
	OE_SHEMA_DEFINITION("shema",OEShemaDefinition.class,VEIconLibrary.OE_SHEMA_ICON),
	OE_SHEMA ("shema",OEShema.class,VEIconLibrary.OE_SHEMA_ICON),
	OE_SHAPE("oe_shape",OEShape.class,VEIconLibrary.OE_SHAPE_ICON),
	OE_CONNECTOR ("oe_connector",OEConnector.class,VEIconLibrary.OE_CONNECTOR_ICON),
	UNKNOWN_OBJECT("unknown_object",FlexoModelObject.class,IconLibrary.QUESTION_ICON);

	static final Logger logger = Logger.getLogger(BrowserElementType.class.getPackage().getName());

	private String name;
	private Class<? extends FlexoModelObject> modelObjectClass;
	private ImageIcon icon;

	private BrowserElementType(String name, Class<? extends FlexoModelObject> modelObjectClass, ImageIcon icon){
		this.name = name;
		this.modelObjectClass = modelObjectClass;
		this.icon = icon;
	}

	public String getName(){
		return name;
	}

	public Class<? extends FlexoModelObject> getModelObjectClass(){
		return modelObjectClass;
	}

	public ImageIcon getIcon(){
		return icon;
	}

	public Icon getExpandedIcon() {
		return null;
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	private static final Hashtable<String, BrowserElementType> typeForClass = new Hashtable<String, BrowserElementType>();

	public static BrowserElementType getBrowserElementTypeForClassName(String className) {
		BrowserElementType returned = typeForClass.get(className);
		if (returned!=null) {
			return returned;
		}
		Class<?> klass = null;
		try {
			klass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Class "+className+" cannot be found on classpath.");
			}
		}
		if (klass!=null) {
			returned = findElementTypeByClass(klass);
		} else {
			returned = findElementTypeByClassName(className);
		}
		typeForClass.put(className, returned);
		return returned;
	}

	private static BrowserElementType findElementTypeByClassName(String className) {
		for(BrowserElementType type:values()) {
			if ((type.getModelObjectClass()!=null) && className.equals(type.getModelObjectClass().getName())) {
				return type;
			}
		}
		return UNKNOWN_OBJECT;
	}

	private static BrowserElementType findElementTypeByClass(Class<?> klass) {
		BrowserElementType best = UNKNOWN_OBJECT;
		for(BrowserElementType type:values()) {
			if (type.getModelObjectClass()!=null) {
				if (type.getModelObjectClass()==klass) {
					// Exact match, we will not find a better choice
					best=type;
					break;
				}
				if (type.getModelObjectClass().isAssignableFrom(klass)) {
					// Super class
					if (best.getModelObjectClass().isAssignableFrom(type.getModelObjectClass())) {
						// We found a better match
						best = type;
					}
				}
			}
		}
		return best;
	}

}
