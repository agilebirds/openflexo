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
package org.openflexo.foundation.rm;

import java.awt.Color;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.diff.merge.MergedDocumentType;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.xml.FlexoXMLMappings;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileFormat.DirectoryFormat;
import org.openflexo.toolbox.FileFormat.TextFileFormat;
import org.openflexo.toolbox.FileFormat.TextSyntax;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents type of a resource
 *
 * @author sguerin
 *
 */
public abstract class ResourceType extends FlexoObject implements StringConvertable, ChoiceList, Serializable {
	protected static final Logger logger = FlexoLogger.getLogger(ResourceType.class.getPackage().getName());

	
	public static TextFileFormat RM_FORMAT,LINKS_FORMAT,WORKFLOW_FORMAT,COMPONENT_LIBRARY_FORMAT,NAVIGATION_MENU_FORMAT,
						PROCESS_FORMAT,OPERATION_COMPONENT_FORMAT,TAB_COMPONENT_FORMAT,MONITORING_SCREEN_FORMAT,MONITORING_COMPONENT_FORMAT,
						GENERATED_CODE_FORMAT,GENERATED_SOURCES_FORMAT,GENERATED_DOC_FORMAT,IMPLEMENTATION_MODEL_FORMAT,TOC_FORMAT,PTOC_FORMAT,
						REUSABLE_COMPONENT_FORMAT,POPUP_COMPONENT_FORMAT,DATA_MODEL_FORMAT,PROJECT_ONTOLOGY_FORMAT,IMPORTED_ONTOLOGY_FORMAT,
						OE_SHEMA_LIBRARY_FORMAT,OE_SHEMA_FORMAT,DKV_FORMAT,WS_LIBRARY_FORMAT;
	public static DirectoryFormat PALETTE_FORMAT, TEMPLATES_FORMAT, INSPECTORS_FORMAT;
	
	static {
		RM_FORMAT = FileFormat.registerTextFileFormat("RM","application/flexo/rmxml",TextSyntax.XML,"rmxml");
		LINKS_FORMAT = FileFormat.registerTextFileFormat("LINKS","application/openflexo/rmxml",TextSyntax.XML,"links");
		WORKFLOW_FORMAT = FileFormat.registerTextFileFormat("WORKFLOW","application/openflexo/wkf",TextSyntax.XML,"wkf");
		COMPONENT_LIBRARY_FORMAT = FileFormat.registerTextFileFormat("COMPONENT_LIBRARY","application/openflexo/wolib",TextSyntax.XML,"wolib");
		NAVIGATION_MENU_FORMAT = FileFormat.registerTextFileFormat("MENU","application/openflexo/menu",TextSyntax.XML,"menu");
		PROCESS_FORMAT = FileFormat.registerTextFileFormat("PROCESS","application/openflexo/process",TextSyntax.XML,"xml");
		OPERATION_COMPONENT_FORMAT = FileFormat.registerTextFileFormat("OPERATION_COMPONENT","application/openflexo/operation_component",TextSyntax.XML,"woxml");
		TAB_COMPONENT_FORMAT = FileFormat.registerTextFileFormat("TAB_COMPONENT","application/openflexo/tab_component",TextSyntax.XML,"woxml");
		MONITORING_SCREEN_FORMAT = FileFormat.registerTextFileFormat("MONITORING_SCREEN","application/openflexo/monitoring_screen",TextSyntax.XML,"woxml");
		MONITORING_COMPONENT_FORMAT = FileFormat.registerTextFileFormat("MONITORING_COMPONENT","application/openflexo/monitoring_component",TextSyntax.XML,"woxml");
		REUSABLE_COMPONENT_FORMAT = FileFormat.registerTextFileFormat("REUSABLE_COMPONENT","application/openflexo/reusable_component",TextSyntax.XML,"woxml");
		POPUP_COMPONENT_FORMAT = FileFormat.registerTextFileFormat("POPUP_COMPONENT","application/openflexo/popup_component",TextSyntax.XML,"woxml");
		GENERATED_CODE_FORMAT = FileFormat.registerTextFileFormat("GENERATED_CODE","application/openflexo/cg",TextSyntax.XML,"cg");
		GENERATED_DOC_FORMAT = FileFormat.registerTextFileFormat("GENERATED_DOC","application/openflexo/dg",TextSyntax.XML,"dg");
		GENERATED_SOURCES_FORMAT = FileFormat.registerTextFileFormat("GENERATED_SOURCES","application/openflexo/sg",TextSyntax.XML,"sg");
		IMPLEMENTATION_MODEL_FORMAT = FileFormat.registerTextFileFormat("IMPLEMENTATION_MODEL","application/openflexo/implementation_model",TextSyntax.XML);
		TOC_FORMAT = FileFormat.registerTextFileFormat("TOC","application/openflexo/toc",TextSyntax.XML,"toc");
		//MOS
		PTOC_FORMAT = FileFormat.registerTextFileFormat("PTOC","application/openflexo/ptoc",TextSyntax.XML,"ptoc");
		//
		DATA_MODEL_FORMAT = FileFormat.registerTextFileFormat("DATA_MODEL","application/openflexo/dm",TextSyntax.XML,"dm");
		PROJECT_ONTOLOGY_FORMAT = FileFormat.registerTextFileFormat("PROJECT_ONTOLOGY","application/openflexo/ontology",TextSyntax.XML,"owl");
		IMPORTED_ONTOLOGY_FORMAT = FileFormat.registerTextFileFormat("IMPORTED_ONTOLOGY","application/openflexo/ontology",TextSyntax.XML,"owl");
		OE_SHEMA_LIBRARY_FORMAT = FileFormat.registerTextFileFormat("OE_SHEMA_LIBRARY","application/openflexo/oelib",TextSyntax.XML,"oelib");
		OE_SHEMA_FORMAT = FileFormat.registerTextFileFormat("OE_SHEMA","application/openflexo/shema",TextSyntax.XML,"shema");
		DKV_FORMAT = FileFormat.registerTextFileFormat("DKV","application/openflexo/dkv",TextSyntax.XML,"dkv");
		WS_LIBRARY_FORMAT = FileFormat.registerTextFileFormat("WS_LIBRARY","application/openflexo/ws",TextSyntax.XML,"ws");

		PALETTE_FORMAT = FileFormat.registerDirectoryFormat("PALETTE", "directory/palette", "palette");
		TEMPLATES_FORMAT = FileFormat.registerDirectoryFormat("TEMPLATES", "directory/templates");
		INSPECTORS_FORMAT = FileFormat.registerDirectoryFormat("INSPECTORS", "directory/inspectors");
	}
	
	public static final StringEncoder.Converter<ResourceType> resourceTypeConverter = new Converter<ResourceType>(ResourceType.class) {

		@Override
		public ResourceType convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(ResourceType value) {
			return value.getName();
		}

	};

	public static ResourceType get(String typeName) {
		for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
			ResourceType temp = (ResourceType) e.nextElement();
			if (temp.getName().equals(typeName)) {
				return temp;
			}
		}

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find ResourceType named " + typeName);
		}
		return null;
	}

	private Vector<ResourceType> _availableValues = null;

	@Override
	public Vector<ResourceType> getAvailableValues() {
		if (_availableValues == null) {
			_availableValues = new Vector<ResourceType>();
			_availableValues.add(RM);
			_availableValues.add(WORKFLOW);
			_availableValues.add(COMPONENT_LIBRARY);
			_availableValues.add(NAVIGATION_MENU);
			_availableValues.add(PROCESS);
			_availableValues.add(OPERATION_COMPONENT);
			_availableValues.add(TAB_COMPONENT);
			_availableValues.add(MONITORING_SCREEN);
			_availableValues.add(CUSTOM_INSPECTORS);
			_availableValues.add(GENERATED_CODE);
			_availableValues.add(GENERATED_DOC);
			_availableValues.add(TOC);
			//MOS
			_availableValues.add(PTOC);
			//
			_availableValues.add(REUSABLE_COMPONENT);
			_availableValues.add(POPUP_COMPONENT);
			_availableValues.add(CUSTOM_TEMPLATES);
			_availableValues.add(DATA_MODEL);
			_availableValues.add(EOMODEL);
			_availableValues.add(JAR);
			_availableValues.add(DKV_MODEL);
			_availableValues.add(SCREENSHOT);
			_availableValues.add(WSDL);
			_availableValues.add(WS_LIBRARY);
			_availableValues.add(JAVA_FILE);
			_availableValues.add(API_FILE);
			_availableValues.add(WO_FILE);
			_availableValues.add(RESOURCE_FILE);
			_availableValues.add(LATEX_FILE);
			_availableValues.add(HTML_FILE);
			_availableValues.add(JS_FILE);
			_availableValues.add(TEXT_FILE);
			_availableValues.add(SYSTEM_FILE);
			_availableValues.add(ANT_FILE);
			_availableValues.add(PLIST_FILE);
			_availableValues.add(WEBSERVER);
			_availableValues.add(BPEL);
			_availableValues.add(XSD);
			_availableValues.add(CSS_FILE);
		}
		return _availableValues;
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return resourceTypeConverter;
	}

	@Override
	public String toString() {
		return getName();
	}

	public static Vector<ResourceType> availableValues() {
		return RM.getAvailableValues();
	}

	public static final ResourceType RM = new RMResourceType();

	public static final ResourceType LINKS = new LinksResourceType();

	public static final ResourceType WORKFLOW = new WorkflowResourceType();

	public static final ResourceType COMPONENT_LIBRARY = new ComponentLibraryResourceType();

	public static final ResourceType NAVIGATION_MENU = new NavigationMenuResourceType();

	public static final ResourceType PROCESS = new ProcessResourceType();

	public static final ResourceType OPERATION_COMPONENT = new OperationComponentResourceType();

	public static final ResourceType TAB_COMPONENT = new TabComponentResourceType();

	public static final ResourceType MONITORING_SCREEN = new MonitoringScreenResourceType();

	public static final ResourceType MONITORING_COMPONENT = new MonitoringComponentResourceType();

	public static final ResourceType CUSTOM_INSPECTORS = new CustomInspectorsResourceType();

	public static final ResourceType GENERATED_CODE = new GeneratedCodeResourceType();

	public static final ResourceType GENERATED_SOURCES = new GeneratedSourcesResourceType();
	public static final ResourceType IMPLEMENTATION_MODEL = new ImplementationModelResourceType();

	public static final ResourceType GENERATED_DOC = new GeneratedDocResourceType();

	public static final ResourceType TOC = new TOCResourceType();
	
	//MOS
	public static final ResourceType PTOC = new PTOCResourceType();

	public static final ResourceType REUSABLE_COMPONENT = new ReusableComponentResourceType();

	public static final ResourceType POPUP_COMPONENT = new PopupComponentResourceType();

	public static final ResourceType CUSTOM_TEMPLATES = new CustomTemplatesResourceType();

	public static final ResourceType DATA_MODEL = new DMResourceType();

	public static final ResourceType EOMODEL = new EOModelResourceType();

	public static final ResourceType PROJECT_ONTOLOGY = new ProjectOntologyResourceType();
	public static final ResourceType IMPORTED_ONTOLOGY = new ImportedOntologyResourceType();
	public static final ResourceType OE_SHEMA_LIBRARY = new OEShemaLibraryResourceType();
	public static final ResourceType OE_SHEMA = new OEShemaResourceType();

	public static final ResourceType JAR = new JarResourceType();

	public static final ResourceType DKV_MODEL = new DKVResourceType();

	public static final ResourceType SCREENSHOT = new ScreenshotResourceType();

	public static final ResourceType WSDL = new WSDLResourceType();
	public static final ResourceType WEBSERVER = new WebServerResourceType();
	public static final ResourceType BPEL = new BPELResourceType();
	public static final ResourceType XSD = new XSDResourceType();

	public static final ResourceType WS_LIBRARY = new WSLibraryResourceType();

	public static final ResourceType CSS_FILE = new CSSFileResourceType();
	public static final ResourceType LATEX_FILE = new LatexFileResourceType();
	public static final ResourceType HTML_FILE = new HTMLFileResourceType();
	public static final ResourceType JS_FILE = new JSFileResourceType();
	public static final ResourceType DOCXXML_FILE = new DocxXmlFileResourceType();
	/**
	 * @author MOSTAFA
	 * MOS
	 */
	public static final ResourceType PPTXXML_FILE = new PptxXmlFileResourceType();
	//
	public static final ResourceType PLIST_FILE = new PListResourceType();
	public static final ResourceType JAVA_FILE = new JavaFileResourceType();
	public static final ResourceType API_FILE = new APIFileResourceType();
	public static final ResourceType WO_FILE = new WOFileResourceType();
	public static final ResourceType RESOURCE_FILE = new ResourceFileResourceType();
	public static final ResourceType TEXT_FILE = new TextFileResourceType();
	public static final ResourceType SYSTEM_FILE = new SystemFileResourceType();
	public static final ResourceType ANT_FILE = new AntFileResourceType();

	public static final ResourceType FILE_RESOURCE = new FileResourceResourceType();

	public static final ResourceType CG_TEMPLATES = new TemplatesResourceType();

	protected ResourceType() {
		super();
	}

	private static class RMResourceType extends ResourceType {
		RMResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "RM";
		}

		@Override
		public FileFormat getFormat() {
			return RM_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getRMMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.GRAY;
		}

	}

	private static class LinksResourceType extends ResourceType {
		LinksResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "LINKS";
		}

		@Override
		public FileFormat getFormat() {
			return LINKS_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getRMMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.GRAY;
		}

	}

	private static class CSSFileResourceType extends ResourceType {
		CSSFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "CSS_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.CSS;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class WorkflowResourceType extends ResourceType {
		WorkflowResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "WORKFLOW";
		}

		@Override
		public FileFormat getFormat() {
			return WORKFLOW_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getWorkflowMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.GREEN;
		}
	}

	private static class ComponentLibraryResourceType extends ResourceType {
		ComponentLibraryResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "COMPONENT_LIBRARY";
		}

		@Override
		public FileFormat getFormat() {
			return COMPONENT_LIBRARY_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getComponentLibraryMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE;
		}

	}

	private static class NavigationMenuResourceType extends ResourceType {
		NavigationMenuResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "NAVIGATION_MENU";
		}

		@Override
		public FileFormat getFormat() {
			return NAVIGATION_MENU_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getNavigationMenuMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.YELLOW;
		}
	}

	private static class TOCResourceType extends ResourceType {
		TOCResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "TOC_RESOURCE";
		}

		@Override
		public FileFormat getFormat() {
			return TOC_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getNavigationMenuMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.MAGENTA;
		}
	}
	
	/**
	 * MOS
	 */
	private static class PTOCResourceType extends ResourceType {
		PTOCResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "PTOC_RESOURCE";
		}

		@Override
		public FileFormat getFormat() {
			return PTOC_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getNavigationMenuMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.MAGENTA;
		}
	}
	//

	private static class ProcessResourceType extends ResourceType {
		ProcessResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "PROCESS";
		}

		@Override
		public FileFormat getFormat() {
			return PROCESS_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getWKFMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.GREEN.brighter();
		}

	}

	private static class OperationComponentResourceType extends ResourceType {
		OperationComponentResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "OPERATION_COMPONENT";
		}

		@Override
		public FileFormat getFormat() {
			return OPERATION_COMPONENT_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getIEMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE.brighter();
		}

	}

	private static class TabComponentResourceType extends ResourceType {
		TabComponentResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "THUMBNAIL_COMPONENT";
		}

		@Override
		public FileFormat getFormat() {
			return TAB_COMPONENT_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getIEMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE.brighter();
		}

	}

	private static class CopiedFileResourceType extends ResourceType {
		CopiedFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "COPIED_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.UNKNOWN;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.LIGHT_GRAY;
		}

	}

	private static class ReusableComponentResourceType extends ResourceType {
		ReusableComponentResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "PARTIAL_COMPONENT";
		}

		@Override
		public FileFormat getFormat() {
			return REUSABLE_COMPONENT_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getIEMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE.brighter();
		}

	}

	private static class PopupComponentResourceType extends ResourceType {
		PopupComponentResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "POPUP_COMPONENT";
		}

		@Override
		public FileFormat getFormat() {
			return POPUP_COMPONENT_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getIEMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE.brighter();
		}
	}

	private static class PListResourceType extends ResourceType {
		PListResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "PROPERTY_LIST";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.PLIST;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class MonitoringScreenResourceType extends ResourceType {
		MonitoringScreenResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "MONITORING_SCREEN";
		}

		@Override
		public FileFormat getFormat() {
			return MONITORING_SCREEN_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getIEMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE.brighter();
		}
	}

	private static class MonitoringComponentResourceType extends ResourceType {
		MonitoringComponentResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "MONITORING_COMPONENT";
		}

		@Override
		public FileFormat getFormat() {
			return MONITORING_COMPONENT_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getIEMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE.brighter();
		}
	}

	private static class CustomInspectorsResourceType extends ResourceType {
		CustomInspectorsResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "CUSTOM_INSPECTORS";
		}

		@Override
		public FileFormat getFormat() {
			return INSPECTORS_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class GeneratedCodeResourceType extends ResourceType {
		GeneratedCodeResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "GENERATED_CODE";
		}

		@Override
		public FileFormat getFormat() {
			return GENERATED_CODE_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getGeneratedCodeMapping();
		}

	}

	private static class GeneratedSourcesResourceType extends ResourceType {
		GeneratedSourcesResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "GENERATED_SOURCES";
		}

		@Override
		public FileFormat getFormat() {
			return GENERATED_SOURCES_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getGeneratedCodeMapping();
		}

	}
	
	private static class ImplementationModelResourceType extends ResourceType {
		ImplementationModelResourceType() {
			super();
		}

		@Override
		public String getName() 
		{
			return "IMPLEMENTATION_MODEL";
		}

		@Override
		public FileFormat getFormat() 
		{
			return IMPLEMENTATION_MODEL_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getImplementationModelMapping();
		}

	}
	
	

	private static class GeneratedDocResourceType extends ResourceType {
		GeneratedDocResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "GENERATED_DOC";
		}

		@Override
		public FileFormat getFormat() {
			return GENERATED_DOC_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getGeneratedCodeMapping();
		}

	}

	private static class CustomTemplatesResourceType extends ResourceType {
		CustomTemplatesResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "CUSTOM_TEMPLATES";
		}

		@Override
		public FileFormat getFormat() {
			return TEMPLATES_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

	}

	private static class DMResourceType extends ResourceType {
		DMResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "DATA_MODEL";
		}

		@Override
		public FileFormat getFormat() {
			return DATA_MODEL_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getDMMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.ORANGE;
		}

	}

	private static class DKVResourceType extends ResourceType {
		DKVResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "DOMAIN_KEY_VALUE";
		}

		@Override
		public FileFormat getFormat() {
			return DKV_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getDKVMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE.darker();
		}

	}

	private static class EOModelResourceType extends ResourceType {
		EOModelResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "EOMODEL";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.EOMODEL;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.RED;
		}

	}

	private static class ImportedOntologyResourceType extends ResourceType {
		ImportedOntologyResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "IMPORTED_ONTOLOGY";
		}

		@Override
		public FileFormat getFormat() {
			return IMPORTED_ONTOLOGY_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE.brighter();
		}

	}

	private static class ProjectOntologyResourceType extends ResourceType {
		ProjectOntologyResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "PROJECT_ONTOLOGY";
		}

		@Override
		public FileFormat getFormat() {
			return PROJECT_ONTOLOGY_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.BLUE;
		}

	}

	private static class OEShemaLibraryResourceType extends ResourceType {
		OEShemaLibraryResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "SHEMA_LIBRARY";
		}

		@Override
		public FileFormat getFormat() {
			return OE_SHEMA_LIBRARY_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getShemaLibraryMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.MAGENTA;
		}

	}

	private static class OEShemaResourceType extends ResourceType {
		OEShemaResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "SHEMA";
		}

		@Override
		public FileFormat getFormat() {
			return OE_SHEMA_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getShemaMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.MAGENTA;
		}

	}

	private static class JarResourceType extends ResourceType {
		JarResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "JAR";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.JAR;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.YELLOW.darker();
		}

	}

	private static class WebServerResourceType extends ResourceType {
		WebServerResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "WEBSERVER";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.UNKNOWN;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.PINK;
		}

	}

	private static class WSDLResourceType extends ResourceType {
		WSDLResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "WSDL";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.WSDL;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.PINK.brighter();
		}

	}

	private static class BPELResourceType extends ResourceType {
		BPELResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "BPEL";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.BPEL;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.PINK.darker();
		}
	}

	private static class XSDResourceType extends ResourceType {
		XSDResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "XSD";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.XSD;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.ORANGE.brighter();
		}

	}

	private static class WSLibraryResourceType extends ResourceType {
		WSLibraryResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "WS_LIBRARY";
		}

		@Override
		public FileFormat getFormat() {
			return WS_LIBRARY_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return true;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return mappings.getWSMapping();
		}

		@Override
		public Color getMainColor() {
			return Color.ORANGE.darker();
		}
	}

	private static class JavaFileResourceType extends ResourceType {
		JavaFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "JAVA_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.JAVA;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

		@Override
		public Color getMainColor() {
			return Color.MAGENTA.brighter();
		}

	}

	private static class LatexFileResourceType extends ResourceType {
		LatexFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "LATEX_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.LATEX;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class HTMLFileResourceType extends ResourceType {
		HTMLFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "HTML_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.HTML;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class JSFileResourceType extends ResourceType {
		JSFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "JS_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.JS;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class DocxXmlFileResourceType extends ResourceType {
		DocxXmlFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "DOCXXML_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.DOCXML;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}
	
	/**
	 * MOS
	 * @author MOSTAFA
	 *
	 */
	private static class PptxXmlFileResourceType extends ResourceType {
		PptxXmlFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "PPTXXML_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.PPTXML;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}
	//

	private static class APIFileResourceType extends ResourceType {
		APIFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "API_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.API;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class WOFileResourceType extends ResourceType {
		WOFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "WO_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.WO;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

	}

	private static class ResourceFileResourceType extends ResourceType {
		ResourceFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "RESOURCE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.UNKNOWN;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

	}

	private static class TextFileResourceType extends ResourceType {
		TextFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "TEXT_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.TEXT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

	}

	private static class SystemFileResourceType extends ResourceType {
		SystemFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "SYSTEM_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.SYSTEM;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}

	}

	private static class AntFileResourceType extends ResourceType {
		AntFileResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "ANT_FILE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.ANT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class ScreenshotResourceType extends ResourceType {

		ScreenshotResourceType() {
			super();
		}

		/**
		 * Overrides getName
		 *
		 * @see org.openflexo.foundation.rm.ResourceType#getName()
		 */
		@Override
		public String getName() {
			return "SCREENSHOT";
		}

		/**
		 * Overrides getFormat
		 *
		 * @see org.openflexo.foundation.rm.ResourceType#getFormat()
		 */
		@Override
		public FileFormat getFormat() {
			return FileFormat.PNG;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class TemplatesResourceType extends ResourceType {
		TemplatesResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "TEMPLATES";
		}

		@Override
		public FileFormat getFormat() {
			return TEMPLATES_FORMAT;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	private static class FileResourceResourceType extends ResourceType {
		FileResourceResourceType() {
			super();
		}

		@Override
		public String getName() {
			return "FILE_RESOURCE";
		}

		@Override
		public FileFormat getFormat() {
			return FileFormat.UNKNOWN;
		}

		@Override
		public boolean isFlexoXMLStorageResource() {
			return false;
		}

		@Override
		public XMLMapping getMapping(FlexoXMLMappings mappings) {
			return null;
		}
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName().toLowerCase());
	}

	public Color getMainColor() {
		return Color.LIGHT_GRAY;
	}

	public abstract String getName();

	public abstract FileFormat getFormat();

	public abstract boolean isFlexoXMLStorageResource();

	public abstract XMLMapping getMapping(FlexoXMLMappings mappings);

	/*protected static final ImageIcon PROJECT_ICON = new ImageIconResource("Resources/WKF/SmallFlexo.gif");
	protected static final ImageIcon WORKFLOW_ICON = new ImageIconResource("Resources/Flexo/Library_WKF.gif");
	protected static final ImageIcon PROCESS_ICON = new ImageIconResource("Resources/WKF/SmallProcess.gif");
	public static final ImageIcon MENUITEM_ICON = new ImageIconResource("Resources/IE/Small_HEADER.gif");
	public static final ImageIcon COMPONENT_LIBRARY_ICON = new ImageIconResource("Resources/Flexo/Library_IE.gif");
	public static final ImageIcon REUSABLECOMPONENT_ICON = new ImageIconResource("Resources/IE/ReusableComponent.gif");
	public static final ImageIcon OPERATION_COMPONENT_ICON = new ImageIconResource("Resources/WKF/SmallOperationComponent.gif");
	public static final ImageIcon POPUP_COMPONENT_ICON = new ImageIconResource("Resources/WKF/SmallPopupComponent.gif");
	public static final ImageIcon SCREEN_COMPONENT_ICON = new ImageIconResource("Resources/WKF/SmallPopupComponent.gif");
	public static final ImageIcon TAB_COMPONENT_ICON = new ImageIconResource("Resources/IE/SmallTabComponent.gif");
	protected static final ImageIcon DM_MODEL_ICON = new ImageIconResource("Resources/DM/DataModel.gif");
	protected static final ImageIcon DM_EOMODEL_ICON = new ImageIconResource("Resources/DM/DMEOModel.gif");
	protected static final ImageIcon OWL_ICON = new ImageIconResource("Resources/OE/Ontology.gif");
	protected static final ImageIcon SHEMA_LIBRARY_ICON = new ImageIconResource("Resources/OE/OEShemaLibrary.gif");
	protected static final ImageIcon SHEMA_ICON = new ImageIconResource("Resources/OE/OEShema.gif");

	public static final ImageIcon WS_REPOSITORY_ICON = new ImageIconResource("Resources/WS/smallWSRepository.gif");
	public static final ImageIcon WS_LIBRARY_ICON = new ImageIconResource("Resources/Flexo/Library_WS.gif");

	public static final ImageIcon DKV_ICON = new ImageIconResource("Resources/IE/Domain.gif");

	public static final ImageIcon JAR_ICON = new ImageIconResource("Resources/DM/SmallExternalRepository.gif");

	public static final ImageIcon GENERATED_CODE_ICON = new ImageIconResource("Resources/CG/GeneratedCode.gif");
	public static final ImageIcon GENERATED_DOC_ICON = new ImageIconResource("Resources/Module/DG_A.gif");
	public static final ImageIcon LATEX_FILE_ICON = new ImageIconResource("Resources/DG/SmallLatexFile.gif");
	public static final ImageIcon JAVA_FILE_ICON = new ImageIconResource("Resources/CG/JavaFile.gif");
	public static final ImageIcon WO_FILE_ICON = new ImageIconResource("Resources/CG/WOFile.gif");
	public static final ImageIcon API_FILE_ICON = new ImageIconResource("Resources/CG/APIFile.gif");
	public static final ImageIcon TEXT_FILE_ICON = new ImageIconResource("Resources/CG/TextFile.gif");
	public static final ImageIcon ANT_FILE_ICON = new ImageIconResource("Resources/CG/AntFile.gif");
	public static final ImageIcon UNKNOWN_FILE_ICON = new ImageIconResource("Resources/CG/UnknownFile.gif");

	public static final ImageIcon FOLDER_ICON = new ImageIconResource("Resources/IE/Folder_IE.gif");
	public static final ImageIcon FILE_ICON = new ImageIconResource("Resources/FileResourceSmall.gif");
	public static final ImageIcon JPG_ICON = new ImageIconResource("Resources/Flexo/JPGIcon.gif");
	public static final ImageIcon IMPORTED_LIB_ICON = new ImageIconResource("Resources/FI/Library_FI.gif");*/

	public static final ResourceType COPIED_FILE = new CopiedFileResourceType();

	private MergedDocumentType _mergedDocumentType = null;

	public MergedDocumentType getDefaultMergedDocumentType() 
	{
		return DefaultMergedDocumentType.getMergedDocumentType(getFormat());
	}

	public MergedDocumentType getMergedDocumentType() {
		if (_mergedDocumentType == null) {
			return getDefaultMergedDocumentType();
		}
		return _mergedDocumentType;
	}

	public void setMergedDocumentType(MergedDocumentType mergedDocumentType) {
		_mergedDocumentType = mergedDocumentType;
	}

}
