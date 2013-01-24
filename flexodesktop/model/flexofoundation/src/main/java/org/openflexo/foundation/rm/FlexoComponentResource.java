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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject.FlexoIDMustBeUnique.DuplicateObjectIDIssue;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.foundation.xml.XMLUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents an abstract component resource
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoComponentResource extends FlexoXMLStorageResource<IEWOComponent> {

	private static final Logger logger = Logger.getLogger(FlexoComponentResource.class.getPackage().getName());

	protected String name;

	public FlexoComponentResource(FlexoProject aProject, String aName, FlexoComponentLibraryResource clResource,
			FlexoProjectFile componentFile) throws InvalidFileNameException {
		this(aProject);
		setName(aName);
		setResourceFile(componentFile);
		addToSynchronizedResources(clResource);
		addToDependentResources(getProject().getFlexoDMResource());
	}

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoComponentResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoComponentResource(FlexoProject aProject) {
		super(aProject, aProject.getServiceManager());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String aName) {
		name = aName;
		setChanged();
	}

	@Override
	public void setResourceData(IEWOComponent component) {
		_resourceData = component;
	}

	public IEWOComponent getWOComponent() {
		return getResourceData();
	}

	public IEWOComponent getWOComponent(FlexoProgress progress) {
		return getResourceData(progress);
	}

	/*
	 * public FlexoResourceData loadResourceData() throws
	 * LoadXMLResourceException, FlexoFileNotFoundException { if
	 * (logger.isLoggable(Level.INFO)) logger.info("Loading component
	 * "+getName()); try { IEWOComponent returnedComponent =
	 * (IEWOComponent)super.loadResourceData();
	 * returnedComponent.setProject(getProject()); return returnedComponent; }
	 * catch (FlexoFileNotFoundException e) { if (logger.isLoggable(Level.INFO))
	 * logger.info ("Creates new component for resource "+this); return
	 * createNewComponent(); } }
	 */

	public final IEWOComponent createNewComponent() {
		return getComponentDefinition().createNewComponent();
	}

	public abstract ComponentDefinition getComponentDefinition();

	@Override
	public IEWOComponent performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadXMLResourceException, FlexoFileNotFoundException, ProjectLoadingCancelledException, MalformedXMLException {
		IEWOComponent component;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Loading WO component " + getName());
		}
		try {
			component = super.performLoadResourceData(progress, loadingHandler);
		} catch (FlexoFileNotFoundException e) {
			// OK, i create the resource by myself !
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Creating new component " + getName());
			}
			component = createNewComponent();
			component.setFlexoResource(this);
			_resourceData = component;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Notify loading for component " + getComponentDefinition().getName());
		}
		getComponentDefinition().notifyWOComponentHasBeenLoaded();
		return component;
	}

	/**
	 * Returns a boolean indicating if this resource needs a builder to be loaded Returns true to indicate that process deserializing
	 * requires a FlexoProcessBuilder instance: in this case: YES !
	 * 
	 * @return boolean
	 */
	@Override
	public boolean hasBuilder() {
		return true;
	}

	@Override
	public Class getResourceDataClass() {
		return IEWOComponent.class;
	}

	/**
	 * Returns the required newly instancied builder if this resource needs a builder to be loaded
	 * 
	 * @return boolean
	 */
	@Override
	public Object instanciateNewBuilder() {
		FlexoComponentBuilder builder = new FlexoComponentBuilder(getComponentDefinition(), this);
		builder.woComponent = _resourceData;
		return builder;
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return true;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		ValidationReport report = getProject().validate();
		for (ValidationIssue issue : report.getValidationIssues()) {
			if (issue instanceof DuplicateObjectIDIssue) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		addToSynchronizedResources(getProject().getFlexoComponentLibraryResource());
		addToDependentResources(getProject().getFlexoDMResource());

		if (getWOComponent() != null) {

			for (Enumeration en = getWOComponent().getAllComponentInstances().elements(); en.hasMoreElements();) {
				ComponentInstance ci = (ComponentInstance) en.nextElement();
				if (ci.getComponentDefinition() != null) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Found dependancy between " + this + " and " + ci.getComponentDefinition().getComponentResource());
					}
					addToDependentResources(ci.getComponentDefinition().getComponentResource());
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Inconsistant data: ComponentInstance refers to an unknown ComponentDefinition "
								+ ci.getComponentName());
					}
				}
			}
		}
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this
	 * resource's dependant resources
	 * 
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		if (resource instanceof FlexoDMResource) {
			FlexoDMResource dmRes = (FlexoDMResource) resource;
			if (getComponentDefinition() != null && dmRes.isLoaded() && getComponentDefinition().getComponentDMEntity() != null) {
				if (!requestDate.before(getComponentDefinition().getComponentDMEntity().getLastUpdate())) {
					if (logger.isLoggable(Level.FINE)) {
						logger.info("OPTIMIST DEPENDANCY CHECKING for COMPONENT " + getComponentDefinition().getName());
						logger.info("entityLastUpdate["
								+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getComponentDefinition().getComponentDMEntity()
										.getLastUpdate()) + "]" + " < requestDate["
								+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(requestDate) + "]");
					}
					return false;
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.info("FAILED / OPTIMIST DEPENDANCY CHECKING for COMPONENT " + getComponentDefinition().getName());
					logger.info("entityLastUpdate["
							+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getComponentDefinition().getComponentDMEntity()
									.getLastUpdate()) + "]" + " > requestDate["
							+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(requestDate) + "]");
				}
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	@Override
	protected boolean convertResourceFileFromVersionToVersion(FlexoVersion v1, FlexoVersion v2) {
		/**
		 * #################################################################### Don't forget to convert IEPalette (Custom widgets palette)as
		 * well # #####################################################################
		 */
		if ((v1.equals(new FlexoVersion("1.0")) || v1.equals(new FlexoVersion("1.1"))) && v2.equals(new FlexoVersion("1.2"))) {
			ComponentConverter2 converter = new ComponentConverter2();
			return converter.conversionWasSucessfull;
		} else if (v1.equals(new FlexoVersion("1.2")) && v2.equals(new FlexoVersion("1.3"))) {
			ComponentConverter3 converter = new ComponentConverter3();
			return converter.conversionWasSucessfull;
		} else if ((v1.equals(new FlexoVersion("2.1")) || v1.equals(new FlexoVersion("2.0")) || v1.equals(new FlexoVersion("1.3")) || v1
				.equals(new FlexoVersion("1.4"))) && v2.equals(new FlexoVersion("2.2"))) {
			ComponentConverter5 converter = new ComponentConverter5();
			return converter.conversionWasSucessfull;
		} else if ((v1.equals(new FlexoVersion("2.3")) || v1.equals(new FlexoVersion("2.2"))) && v2.equals(new FlexoVersion("2.4"))) {
			ComponentConverter6 converter = new ComponentConverter6();
			return converter.conversionWasSucessfull;
		} else if ((v1.equals(new FlexoVersion("2.4")) || v1.equals(new FlexoVersion("2.5"))) && v2.equals(new FlexoVersion("2.6"))) {
			ComponentConverter7 converter = new ComponentConverter7();
			return converter.conversionWasSucessfull;
		} else if ((v1.equals(new FlexoVersion("2.4")) || v1.equals(new FlexoVersion("2.5")) || v1.equals(new FlexoVersion("2.6")))
				&& v2.equals(new FlexoVersion("2.7"))) {
			ComponentConverter8 converter = new ComponentConverter8();
			return converter.conversionWasSucessfull;
		} else if (v1.equals(new FlexoVersion("2.7")) && v2.equals(new FlexoVersion("3.0"))) {
			ComponentConverter9 converter = new ComponentConverter9();
			return converter.conversionWasSucessfull;
		} else {
			return super.convertResourceFileFromVersionToVersion(v1, v2);
		}
	}

	protected class ComponentConverter2 {

		@SuppressWarnings("hiding")
		private final Logger logger = Logger.getLogger(FlexoComponentResource.ComponentConverter2.class.getPackage().getName());

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element rootElement;

		protected ComponentConverter2() {
			super();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();

			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}

		private Element createComponentInstanceElement(String prefix, String aName) {
			Element answer = new Element(prefix + "ComponentInstance");
			answer.setAttribute("componentName", aName);
			return answer;
		}

		private void convert() {
			Iterator tableElementIterator = document.getDescendants(new ElementFilter("IEButton"));
			Vector temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			Enumeration en = temp.elements();
			while (en.hasMoreElements()) {
				Element nextElement = (Element) en.nextElement();
				if (nextElement.getAttributeValue("popupName") != null) {
					nextElement.addContent(createComponentInstanceElement("Popup", nextElement.getAttributeValue("popupName")));
					nextElement.removeAttribute("popupName");
				} else if (nextElement.getAttributeValue("pageName") != null) {
					nextElement.addContent(createComponentInstanceElement("Operation", nextElement.getAttributeValue("pageName")));
					nextElement.removeAttribute("pageName");
				}
			}

			tableElementIterator = document.getDescendants(new ElementFilter("IEHyperlink"));
			temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			en = temp.elements();
			while (en.hasMoreElements()) {
				Element nextElement = (Element) en.nextElement();
				if (nextElement.getAttributeValue("popupName") != null) {
					nextElement.addContent(createComponentInstanceElement("Popup", nextElement.getAttributeValue("popupName")));
					nextElement.removeAttribute("popupName");
				} else if (nextElement.getAttributeValue("pageName") != null) {
					nextElement.addContent(createComponentInstanceElement("Operation", nextElement.getAttributeValue("pageName")));
					nextElement.removeAttribute("pageName");
				}
			}
			tableElementIterator = document.getDescendants(new ElementFilter("IEThumbnail"));
			temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			en = temp.elements();
			while (en.hasMoreElements()) {
				Element nextElement = (Element) en.nextElement();
				if (nextElement.getAttributeValue("woComponentName") != null) {
					nextElement.addContent(createComponentInstanceElement("Thumbnail", nextElement.getAttributeValue("woComponentName")));
					nextElement.removeAttribute("woComponentName");
				}
			}
		}

		private boolean save() {
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}
	}

	protected class ComponentConverter3 {

		@SuppressWarnings("hiding")
		private final Logger logger = Logger.getLogger(FlexoComponentResource.ComponentConverter3.class.getPackage().getName());

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element rootElement;

		protected ComponentConverter3() {
			super();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();

			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}

		private void convert() {
			Iterator tableElementIterator = document.getDescendants(new ElementFilter("IEButton"));
			Vector temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			Enumeration en = temp.elements();
			while (en.hasMoreElements()) {
				Element nextElement = (Element) en.nextElement();
				if (nextElement.getAttributeValue("smallButtonName") != null) {
					nextElement.setAttribute("imageName", createImageFromSmallButton(nextElement.getAttributeValue("smallButtonName")));
					nextElement.removeAttribute("smallButtonName");
				} else if (nextElement.getAttributeValue("bigButtonName") != null) {
					nextElement.setAttribute("imageName", createImageFromBigButton(nextElement.getAttributeValue("bigButtonName")));
					nextElement.removeAttribute("bigButtonName");
				}
			}
		}

		private String createImageFromSmallButton(String iconName) {

			// smallButtonName="Flexo_Icon_MoveUpDown.gif"
			// bigButtonName="Flexo/Flexo_Button_Next.gif"
			// smallButtonName="RTSFoundation/Button_Split.gif"
			// smallButtonName="denali_icon44.gif"
			if (iconName.startsWith("Flexo_")) {
				return ToolBox.replaceStringByStringInString("Flexo", "", iconName);
			}
			if (iconName.startsWith("Flexo/Flexo_")) {
				return ToolBox.replaceStringByStringInString("Flexo/Flexo", "", iconName);
			}
			return iconName;
		}

		private String createImageFromBigButton(String iconName) {
			if (iconName.startsWith("Flexo/Flexo_")) {
				return ToolBox.replaceStringByStringInString("Flexo/Flexo", "", iconName);
			}
			return iconName;
		}

		private boolean save() {
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}
	}

	protected class ComponentConverter8 {

		@SuppressWarnings("hiding")
		private final Logger logger = Logger.getLogger(FlexoComponentResource.ComponentConverter8.class.getPackage().getName());

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element rootElement;

		protected ComponentConverter8() {
			super();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();

			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}

		private void convert() {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Starting conditional button conversion...");
			}
			Iterator buttonIterator = document.getDescendants(new ElementFilter("IEButton"));
			while (buttonIterator.hasNext()) {
				Element element = (Element) buttonIterator.next();
				Attribute imageName = element.getAttribute("imageName");
				if (imageName != null && imageName.getValue().startsWith("_Button_")) {
					String customButtonLabel = imageName.getValue().substring(8, imageName.getValue().length() - 4);
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Replace IEButton by custom button: " + customButtonLabel);
					}
					element.setName("IECustomButton");
					element.setAttribute("inspector", "CustomButton.inspector");
					element.setAttribute("customButtonValue", customButtonLabel);
				}
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Done");
			}
		}

		private boolean save() {
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}
	}

	protected class ComponentConverter7 {

		@SuppressWarnings("hiding")
		private final Logger logger = Logger.getLogger(FlexoComponentResource.ComponentConverter7.class.getPackage().getName());

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element rootElement;

		protected ComponentConverter7() {
			super();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();

			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}

		private void convert() {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Starting conditional bindings conversion...");
			}
			Iterator conditionalIterator = document.getDescendants(new ElementFilter("ConditionalOperator"));
			while (conditionalIterator.hasNext()) {
				Element element = (Element) conditionalIterator.next();
				Element sequence = element.getParentElement();
				Attribute conditional = sequence.getAttribute("binding_conditional");
				if (conditional != null) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Found a conditional: " + conditional);
					}
					sequence.removeAttribute(conditional);
					element.setAttribute(conditional);
				}
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Done");
			}
		}

		private boolean save() {
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}
	}

	protected class ComponentConverter6 {
		@SuppressWarnings("hiding")
		private final Logger logger = Logger.getLogger(FlexoComponentResource.ComponentConverter6.class.getPackage().getName());

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element rootElement;

		protected ComponentConverter6() {
			super();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();

			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}

		private void convert() {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Starting tab container conversion...");
			}
			Iterator tabContainerElementIterator = document.getDescendants(new ElementFilter("IETabContainer"));
			while (tabContainerElementIterator.hasNext()) {
				Element element = (Element) tabContainerElementIterator.next();
				element.removeAttribute("colSpan");
				element.removeAttribute("rowSpan");
				element.setName("IESequenceTab");
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Done");
			}
		}

		private boolean save() {
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}
	}

	protected class ComponentConverter5 {

		@SuppressWarnings("hiding")
		private final Logger logger = Logger.getLogger(FlexoComponentResource.ComponentConverter5.class.getPackage().getName());

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element rootElement;

		protected ComponentConverter5() {
			super();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();

			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}

		private void convert() {
			Iterator tableElementIterator = document.getDescendants(new ThumbnailElementFilter());
			Vector temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			Enumeration en = temp.elements();
			while (en.hasMoreElements()) {
				Element nextElement = (Element) en.nextElement();
				if (nextElement.getAttributeValue("inspector") != null) {
					nextElement.setAttribute("inspector", "Tab.inspector");
				}
				if (nextElement.getAttribute("thumbTitle") != null) {
					nextElement.getAttribute("thumbTitle").setName("tabTitle");
				}
				nextElement.setName("IETab");
			}
			System.err.println("Found " + temp.size() + " TabWidgets in " + getName());
			tableElementIterator = document.getDescendants(new ThumbnailContainerElementFilter());
			int i = 0;
			while (tableElementIterator.hasNext()) {
				Element nextElement = (Element) tableElementIterator.next();
				if (nextElement.getAttributeValue("inspector") != null) {
					nextElement.setAttribute("inspector", "TabContainer.inspector");
				}
				nextElement.setName("IETabContainer");
				i++;
			}
			System.err.println("Found " + i + " tab containers in " + getName());

			tableElementIterator = document.getDescendants(new ThumbnailComponentElementFilter());
			i = 0;
			while (tableElementIterator.hasNext()) {
				Element nextElement = (Element) tableElementIterator.next();
				nextElement.setName("IETabComponent");
				i++;
			}
			System.err.println("Found " + i + " tag ThumbnailComponent in " + getName());

			tableElementIterator = document.getDescendants(new ThumbnailComponentInstanceElementFilter());
			i = 0;
			while (tableElementIterator.hasNext()) {
				Element nextElement = (Element) tableElementIterator.next();
				nextElement.setName("TabComponentInstance");
				i++;
			}
			System.err.println("Found " + i + " tag TabComponentInstance in " + getName());

			Element root = document.getRootElement();
			insertSequence("IESequenceTopComponent", root);

			tableElementIterator = document.getDescendants(new ElementFilter("IEButtonContainer"));
			temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			en = temp.elements();
			while (en.hasMoreElements()) {
				Element nextElement = (Element) en.nextElement();
				nextElement.setName("IESequenceButton");
			}

			insertSequenceInAllBlock(root);

		}

		private void insertSequenceInAllBlock(Element root) {
			Iterator tableElementIterator = root.getDescendants(new ElementFilter("IEBloc"));
			Vector temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			Enumeration en = temp.elements();
			while (en.hasMoreElements()) {
				insertSequenceInOneBloc((Element) en.nextElement());
			}
		}

		private void insertSequenceInOneBloc(Element block) {
			List originalChildren = block.cloneContent();
			Element sequence = new Element("IESequenceWidget");
			block.addContent(sequence);
			Iterator it = originalChildren.iterator();
			Object obj = null;
			int i = 0;
			while (it.hasNext()) {
				obj = it.next();

				if (obj instanceof Element
						&& (((Element) obj).getName().equals("IEButton") || ((Element) obj).getName().equals("IECustomButton"))) {
					block.removeContent((Element) obj);

					sequence.addContent(i, (Element) obj);
					System.err.println("addContent : " + obj + " at index " + i + " new parent is " + ((Element) obj).getParentElement());
					i++;
				}
			}
			System.out.println("sequence have : " + sequence.getContentSize() + " length !");
		}

		private void insertSequence(String seqName, Element parent) {
			List originalChildren = parent.cloneContent();
			Element sequence = new Element(seqName);
			parent.setContent(sequence);
			Iterator it = originalChildren.iterator();
			Object obj = null;
			int i = 0;
			while (it.hasNext()) {
				obj = it.next();

				if (obj instanceof Content) {
					Content c = ((Content) obj).detach();

					sequence.addContent(i, c);
					System.err.println("addContent : " + obj + " at index " + i + " new parent is " + c.getParentElement());
					i++;
				}
			}
			System.out.println("sequence have : " + sequence.getContentSize() + " length !");

		}

		private boolean save() {
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}

		private class ThumbnailComponentElementFilter extends ElementFilter {

			public ThumbnailComponentElementFilter() {
				super("IEThumbnailComponent");
			}
		}

		private class ThumbnailComponentInstanceElementFilter extends ElementFilter {

			public ThumbnailComponentInstanceElementFilter() {
				super("ThumbnailComponentInstance");
			}
		}

		private class ThumbnailElementFilter extends ElementFilter {

			public ThumbnailElementFilter() {
				super("IEThumbnail");
			}
		}

		private class ThumbnailContainerElementFilter extends ElementFilter {

			public ThumbnailContainerElementFilter() {
				super("IEThumbnailContainer");
			}
		}

	}

	protected class ComponentConverter9 {

		@SuppressWarnings("hiding")
		private final Logger logger = Logger.getLogger(FlexoComponentResource.ComponentConverter9.class.getPackage().getName());

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected ComponentConverter9() {
			super();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}

		private void convert() {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Starting top sequence conversion...");
			}
			Iterator sequence = document.getDescendants(new ElementFilter("IESequenceTopComponent"));
			while (sequence.hasNext()) {
				Element element = (Element) sequence.next();
				element.setName("IESequenceWidget");
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Done");
			}
		}

		private boolean save() {
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}
	}

}
