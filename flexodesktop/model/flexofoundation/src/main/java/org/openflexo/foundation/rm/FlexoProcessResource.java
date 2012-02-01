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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.AbstractFilter;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.gen.FlexoProcessImageBuilder;
import org.openflexo.foundation.rm.FlexoProject.FlexoIDMustBeUnique.DuplicateObjectIDIssue;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.TriggerType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoXMLMappings;
import org.openflexo.foundation.xml.XMLUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.xmlcode.ModelEntity;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Represents a process resource
 * 
 * @author sguerin
 * 
 */
public class FlexoProcessResource extends FlexoXMLStorageResource<FlexoProcess> implements Serializable {

	protected static final Logger logger = Logger.getLogger(FlexoProcessResource.class.getPackage().getName());

	protected String name;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoProcessResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoProcessResource(FlexoProject aProject) {
		super(aProject);
	}

	public FlexoProcessResource(FlexoProject aProject, String aName, FlexoWorkflowResource workflowResource, FlexoProjectFile processFile)
			throws InvalidFileNameException {
		this(aProject);
		name = aName;
		setResourceFile(processFile);
		addToSynchronizedResources(workflowResource);
		addToDependentResources(aProject.getFlexoDMResource());
		// addToDependantResources(aProject.getFlexoComponentLibraryResource());
	}

	public FlexoProcessResource(FlexoProject aProject, FlexoProcess aProcess, FlexoWorkflowResource workflowResource,
			FlexoProjectFile processFile) throws InvalidFileNameException {
		this(aProject, aProcess.getName(), workflowResource, processFile);
		_resourceData = aProcess;
		aProcess.setFlexoResource(this);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PROCESS;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String aName) {
		String old = name;
		name = aName;
		setChanged();
		notifyObservers(new NameChanged(old, name));
	}

	@Override
	public Class getResourceDataClass() {
		return FlexoProcess.class;
	}

	@Override
	public FlexoProcess performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadXMLResourceException, ProjectLoadingCancelledException, MalformedXMLException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Loading process " + getName() + " file=" + getFileName() + " ID=" + getResourceIdentifier());
		}
		FlexoProcess process;
		try {
			process = super.performLoadResourceData(progress, loadingHandler);
		} catch (FlexoFileNotFoundException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("File " + getFile().getName() + " NOT found, removing resource !");
			}
			delete();
			return null;
		}

		return process;
	}

	public FlexoProcess getFlexoProcess() {
		return getResourceData();
	}

	/**
	 * Returns a boolean indicating if this resource needs a builder to be loaded Returns true to indicate that process deserializing
	 * requires a FlexoProcessBuilder instance
	 * 
	 * @return boolean
	 */
	@Override
	public boolean hasBuilder() {
		return true;
	}

	/**
	 * Overrides saveResourceData
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#saveResourceData()
	 */
	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveXMLResourceException, SaveResourcePermissionDeniedException {
		StringEncoder encoder = getProject() != null ? getProject().getStringEncoder() : StringEncoder.getDefaultInstance();
		String s = encoder._getDateFormat();
		try {
			encoder._setDateFormat("HH:mm:ss dd/MM/yyyy SSS");
			super.saveResourceData(clearIsModified);
		} catch (SaveXMLResourceException e) {
			throw e;
		} catch (SaveResourcePermissionDeniedException e) {
			throw e;
		} finally {
			if (s != null) {
				encoder._setDateFormat(s);
			}
		}

		try {
			FlexoProcessImageBuilder.writeSnapshot(getFlexoProcess());
		} catch (Exception e) {
			logger.warning("Save image snapshot for process " + getFlexoProcess().getName() + e.getMessage());
		}

	}

	/*protected StorageResourceData tryToLoadResourceDataWithVersion(FlexoVersion version) throws XMLOperationException, JDOMException
	{
	    FlexoProcessNode processNode = getProject().getFlexoWorkflow()._getFlexoProcessNodeWithName(getName());
	    if (processNode == null) {
	        if (logger.isLoggable(Level.WARNING)) {
	            logger.warning("Could not find process node associated with process " + getName());
	            delete();
	            return null;
	        }
	    }
	    return super.tryToLoadResourceDataWithVersion(version);
	}*/

	/**
	 * Returns the required newly instancied FlexoProcessBuilder
	 * 
	 * @return boolean
	 */
	@Override
	public Object instanciateNewBuilder() {
		FlexoProcessBuilder returned = new FlexoProcessBuilder(this);
		returned.defaultProcessName = getName();
		returned.setProject(getProject());
		returned.workflow = getProject().getFlexoWorkflow();
		return returned;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();

		addToSynchronizedResources(getProject().getFlexoWorkflowResource());
		addToDependentResources(getProject().getFlexoDMResource());

		/* GPO: the following line has been commented because it is unnecessary at this time
		 * Indeed, process depends on components (through component instances) but they donnot
		 * depend on the component library. Typically, this means that when a component is
		 * deleted, if the process does not use that component, the component has no interest in
		 * the notification of the component deletion. */
		// addToDependantResources(getProject().getFlexoComponentLibraryResource());

		if (getFlexoProcess() != null) {

			for (Enumeration en = getFlexoProcess().getAllSubProcessNodes().elements(); en.hasMoreElements();) {
				SubProcessNode node = (SubProcessNode) en.nextElement();
				if (node.getSubProcess() != null && node.getSubProcess() != getFlexoProcess()) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Found dependancy between " + this + " and " + node.getSubProcess().getFlexoResource());
					}
					addToDependentResources(node.getSubProcess().getFlexoResource());
				}
			}

			for (Enumeration en = getFlexoProcess().getAllEmbeddedOperationNodes().elements(); en.hasMoreElements();) {
				OperationNode node = (OperationNode) en.nextElement();
				if (node.getComponentInstance() != null && node.getComponentInstance().getComponentDefinition() != null) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Found dependancy between " + this + " and "
								+ node.getComponentInstance().getComponentDefinition().getComponentResource());
					}
					node.getComponentInstance().rebuildDependancies();
					if (node.getTabOperationComponentInstance() != null
							&& node.getTabOperationComponentInstance().getComponentDefinition() != null) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Found dependancy between " + this + " and "
									+ node.getTabOperationComponentInstance().getComponentDefinition().getComponentResource());
						}
						node.getTabOperationComponentInstance().rebuildDependancies();
					}
				}
				for (ActionNode action : node.getAllActionNodes()) {
					if (action.getTabActionComponentInstance() != null
							&& action.getTabActionComponentInstance().getComponentDefinition() != null) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Found dependancy between " + this + " and "
									+ action.getTabActionComponentInstance().getComponentDefinition().getComponentResource());
						}
						action.getTabActionComponentInstance().rebuildDependancies();
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
			if (isLoaded() && dmRes.isLoaded() && getFlexoProcess().getProcessDMEntity() != null) {
				if (!requestDate.before(getFlexoProcess().getProcessDMEntity().getLastUpdate())) {
					if (logger.isLoggable(Level.FINE)) {
						logger.info("OPTIMIST DEPENDANCY CHECKING for PROCESS " + getFlexoProcess().getName());
						logger.info("entityLastUpdate["
								+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getFlexoProcess().getProcessDMEntity().getLastUpdate())
								+ "]" + " < requestDate[" + new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(requestDate) + "]");
					}
					return false;
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.info("FAILED / OPTIMIST DEPENDANCY CHECKING for PROCESS " + getFlexoProcess().getName());
					logger.info("entityLastUpdate["
							+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getFlexoProcess().getProcessDMEntity().getLastUpdate())
							+ "]" + " > requestDate[" + new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(requestDate) + "]");
				}
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	/**
	 * Manually converts resource file from version v1 to version v2. This method implements conversion from v0.1 to v1.0 This method
	 * implements conversion from 2.0 to v2.1
	 * 
	 * @param v1
	 * @param v2
	 * @return boolean indicating if conversion was sucessfull
	 */
	@Override
	protected boolean convertResourceFileFromVersionToVersion(FlexoVersion v1, FlexoVersion v2) {
		/*if (v1.equals(new FlexoVersion("0.1"))
		        && v2.equals(new FlexoVersion("1.0"))) {
		    ProcessConverter1 converter = new ProcessConverter1();
		    return converter.conversionWasSucessfull;
		} else if (v1.equals(new FlexoVersion("2.0"))
		        && v2.equals(new FlexoVersion("2.1"))) {
		    ProcessConverter2 converter = new ProcessConverter2();
		    return converter.conversionWasSucessfull;
		} else if (v2.equals(new FlexoVersion("3.4"))) {
			ProcessConverter3 converter = new ProcessConverter3();
			return converter.conversionWasSucessfull;
		} else*/if (v1.equals(new FlexoVersion("4.0")) && v2.equals("5.0")) {
			ProcessConverter5 converter = new ProcessConverter5();
			return converter.conversionWasSucessfull;
		} else if (v1.equals(new FlexoVersion("5.0")) && v2.equals("6.0")) {
			ProcessConverter6 converter = new ProcessConverter6();
			return converter.conversionWasSucessfull;
		} else {
			return super.convertResourceFileFromVersionToVersion(v1, v2);
		}
	}

	/*protected class ProcessConverter1
	{

	    protected boolean conversionWasSucessfull = false;

	    protected Document document;

	    protected Element processElement;

	    private Element lastOperationComponentDefinition;

	    private Element popupListElement;

	    protected ProcessConverter1()
	    {
	        super();
	        // roles = new Hashtable();
	        // pendingRoles = new Hashtable();
	        try {
	            document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
	            convert();
	            conversionWasSucessfull = save();
	        } catch (Exception e) {
	            // Warns about the exception
	            if (logger.isLoggable(Level.WARNING))
	                logger.warning("Exception raised: " + e.getClass().getName()
	                        + ". See console for details.");
	            e.printStackTrace();
	        }
	    }

	    private void convert()
	    {
	        renameRootElement();
	        restructurePetriGraphs(processElement);
	        restructureProperties(processElement);
	    }

	    private void renameRootElement()
	    {
	        processElement = document.getRootElement();
	        processElement.setName("FlexoProcess");
	        // processElement.setAttribute("name","unnamed");
	        Element processPropertiesElement = new Element("ProcessProperties");
	        processElement.addContent(processPropertiesElement);
	        popupListElement = new Element("PopupComponentList");
	        processElement.addContent(popupListElement);
	    }

	    private void restructurePetriGraphs(Element element)
	    {
	        List activityPetriGraphs = element.getChildren("dwgraph");
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("restructurePetriGraphs() for " + element + " with "
	                    + activityPetriGraphs.size() + " graph");
	        if (activityPetriGraphs.size() == 1) {
	            restructurePetriGraph((Element) activityPetriGraphs.get(0), element);
	        }
	    }

	    private void restructurePetriGraph(Element pgElement, Element parentElement)
	    {
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("restructurePetriGraph() for " + pgElement);
	        try {
	            Attribute level = pgElement.getAttribute("level");
	            int lev;
	            lev = level.getIntValue();
	            if (lev == 0) {
	                pgElement.setName("ActivityPetriGraph");
	            } else if (lev == 1) {
	                pgElement.setName("OperationPetriGraph");
	            } else if (lev == 2) {
	                pgElement.setName("ActionPetriGraph");
	            }
	            pgElement.removeAttribute(level);

	            Vector newAttributes = new Vector();

	            List attrList = ((Element) pgElement.clone()).getAttributes();
	            for (int i = 0; i < attrList.size(); i++) {
	                Attribute attr = (Attribute) attrList.get(i);
	                if ((attr.getName().equals("superClassGenSubPath"))
	                        || (attr.getName().equals("pageGenSubPath"))
	                        || (attr.getName().equals("operationGenSubPath"))
	                        || (attr.getName().equals("tabGenSubPath"))
	                        || (attr.getName().equals("popupGenSubPath"))
	                        || (attr.getName().equals("componentPrefix"))) {
	                    Attribute keptAttribute = new Attribute(attr.getName(), attr.getValue());
	                    newAttributes.add(keptAttribute);
	                }
	                if (attr.getName().equals("cssSheet")) {
	                    Attribute keptAttribute = new Attribute(attr.getName(), "???");
	                    if (attr.getValue().equals("FlexoMasterStyle.css")) {
	                        keptAttribute.setValue(FlexoCSS.FLEXO.getName());
	                    } else if (attr.getValue().equals("ContentoMasterStyle.css")) {
	                        keptAttribute.setValue(FlexoCSS.CONTENTO.getName());
	                    } else if (attr.getValue().equals("OmniscioMasterStyle.css")) {
	                        keptAttribute.setValue(FlexoCSS.OMNISCIO.getName());
	                    } else if (attr.getValue().equals("Electrabel.css")) {
	                        keptAttribute.setValue(FlexoCSS.CONTENTO.getName());
	                    }
	                    newAttributes.add(keptAttribute);
	                }
	                if (!((attr.getName().equals("width")) || (attr.getName().equals("height"))
	                        || (attr.getName().equals("posiX")) || (attr.getName().equals("posiY"))
	                        || (attr.getName().equals("nColumn"))
	                        || (attr.getName().equals("textColor")) || (attr.getName()
	                        .equals("backColor")))) {
	                    pgElement.removeAttribute(pgElement.getAttribute(attr.getName()));
	                }
	            }
	            if (parentElement != null) {
	                for (int i = 0; i < newAttributes.size(); i++) {
	                    parentElement.setAttribute((Attribute) newAttributes.get(i));
	                }
	            }

	            restructurePetriNodes(pgElement);

	        } catch (Exception e) {
	            if (logger.isLoggable(Level.WARNING))
	                logger.warning("Exception raised: " + e.getClass().getName()
	                        + ". See console for details.");
	            e.printStackTrace();
	        }
	    }

	    private void restructurePetriNodes(Element pgElement)
	    {
	        Vector newNodes = new Vector();
	        List allNodes = pgElement.getChildren("flexonode");
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("restructurePetriNodes() for " + pgElement + " with " + allNodes.size()
	                    + " nodes");
	        for (int i = 0; i < allNodes.size(); i++) {
	            Element oldNodeElement = (Element) allNodes.get(i);
	            if (logger.isLoggable(Level.FINE))
	                logger.fine("Go for node " + oldNodeElement + "with index " + i
	                        + " activityPetriNodes.size()=" + allNodes.size());
	            Element newNodeElement = restructurePetriNode(oldNodeElement);
	            newNodes.add(newNodeElement);
	        }
	        pgElement.removeChildren("flexonode");
	        for (int i = 0; i < newNodes.size(); i++) {
	            pgElement.addContent((Element) newNodes.elementAt(i));
	        }
	    }

	    private Element restructurePetriNode(Element nodeElement)
	    {
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("restructurePetriNode() for " + nodeElement);
	        Element returnedElement = (Element) nodeElement.clone();
	        try {
	            Attribute level = returnedElement.getAttribute("level");
	            int lev;
	            lev = level.getIntValue();
	            if (lev == 0) {
	                returnedElement.setName("ActivityNode");
	            } else if (lev == 1) {
	                returnedElement.setName("OperationNode");
	            } else if (lev == 2) {
	                returnedElement.setName("ActionNode");
	            }
	            returnedElement.removeAttribute(level);
	            Attribute nameAttribute = returnedElement.getAttribute("name");
	            if (nameAttribute != null) {
	                nameAttribute.setName("nodeName");
	            }
	            Attribute descriptionAttribute = returnedElement.getAttribute("nodeDesc");
	            if (descriptionAttribute != null) {
	                descriptionAttribute.setName("description");
	            }
	            Attribute typeAttribute = returnedElement.getAttribute("activityType");
	            if (typeAttribute != null) {
	                typeAttribute.setName("nodeType");
	                if (typeAttribute.getValue().equals("normalActivity")) {
	                    typeAttribute.setValue(NodeType.NORMAL.getName());
	                } else if (typeAttribute.getValue().equals("beginningActivity")) {
	                    typeAttribute.setValue(NodeType.BEGIN.getName());
	                } else if (typeAttribute.getValue().equals("pseudoBeginningActivity")) {
	                    typeAttribute.setValue(NodeType.PSEUDO_BEGIN.getName());
	                } else if (typeAttribute.getValue().equals("endingActivity")) {
	                    typeAttribute.setValue(NodeType.END.getName());
	                } else if (typeAttribute.getValue().equals("pseudoEndingActivity")) {
	                    typeAttribute.setValue(NodeType.PSEUDO_END.getName());
	                } else if (typeAttribute.getValue().equals("interOperation")) {
	                    typeAttribute.setValue(NodeType.INTER.getName());
	                }
	            }
	            Attribute roleAttribute = returnedElement.getAttribute("role");
	            if (roleAttribute != null) {
	                roleAttribute.setName("role");
	            }
	            Attribute isWaitingAttribute = returnedElement.getAttribute("iswaiting");
	            if (isWaitingAttribute != null) {
	                isWaitingAttribute.setName("isWaiting");
	            }
	            Attribute isSAAttribute = returnedElement.getAttribute("selfactivated");
	            if (isSAAttribute != null) {
	                isSAAttribute.setName("isSelfActivated");
	            }
	            Attribute actionTypeAttribute = returnedElement.getAttribute("actionType");
	            if (actionTypeAttribute != null) {
	                actionTypeAttribute.setName("actionType");
	                if (lev != 2) {
	                    returnedElement.removeAttribute(actionTypeAttribute);
	                } else {
	                    if (actionTypeAttribute.getValue().equals("Default")) {
	                        actionTypeAttribute.setValue(ActionType.DEFAULT.getName());
	                    } else if (actionTypeAttribute.getValue().equals("CreateSubProcess")) {
	                        actionTypeAttribute.setValue(ActionType.CREATE_SUB_PROCESS.getName());
	                    } else if (actionTypeAttribute.getValue().equals("ExecuteSubProcessNode")) {
	                        actionTypeAttribute.setValue(ActionType.EXECUTE_SUB_PROCESS.getName());
	                    } else if (actionTypeAttribute.getValue().equals("selfActivated")) {
	                        actionTypeAttribute.setValue(ActionType.SELF_ACTIVATED.getName());
	                    }

	                }
	            }
	            Attribute woCompAttribute = returnedElement.getAttribute("woComponentName");
	            if (woCompAttribute != null) {
	                woCompAttribute.setName("WOComponentName");
	                if (lev == 1) {
	                    Element operationComponentDefinition = new Element(
	                            "OperationComponentDefinition");
	                    operationComponentDefinition.setAttribute(new Attribute("name",
	                            woCompAttribute.getValue()));
	                    returnedElement.addContent(operationComponentDefinition);
	                    lastOperationComponentDefinition = operationComponentDefinition;
	                    if (logger.isLoggable(Level.FINE))
	                        logger.fine("Found operation component definition "
	                                + woCompAttribute.getValue());
	                } else if (lev == 2) {
	                    Attribute isThumbnailAttribute = returnedElement
	                            .getAttribute("isThumbnail");
	                    if ((isThumbnailAttribute != null)
	                            && (isThumbnailAttribute.getValue().equalsIgnoreCase("true"))) {
	                        Element thumbnailComponentDefinition = new Element(
	                                "ThumbnailComponentDefinition");
	                        thumbnailComponentDefinition.setAttribute(new Attribute("name",
	                                woCompAttribute.getValue()));
	                        lastOperationComponentDefinition
	                                .addContent(thumbnailComponentDefinition);
	                        if (logger.isLoggable(Level.FINE))
	                            logger.fine("Found thumbnail component definition "
	                                    + woCompAttribute.getValue());
	                    } else {
	                        Element popupComponentDefinition = new Element(
	                                "PopupComponentDefinition");
	                        popupComponentDefinition.setAttribute(new Attribute("name",
	                                woCompAttribute.getValue()));
	                        popupListElement.addContent(popupComponentDefinition);
	                        if (logger.isLoggable(Level.FINE))
	                            logger.fine("Found popup component definition "
	                                    + woCompAttribute.getValue());
	                    }
	                }
	            }
	            Attribute classNameAttribute = returnedElement.getAttribute("className");
	            if (classNameAttribute != null) {
	                returnedElement.removeAttribute(classNameAttribute);
	            }
	            Attribute classnameAttribute = returnedElement.getAttribute("classname");
	            if (classnameAttribute != null) {
	                returnedElement.removeAttribute(classnameAttribute);
	            }
	            Element nodeProperties = new Element("NodeProperties");
	            List attrList = ((Element) returnedElement.clone()).getAttributes();
	            for (int i = 0; i < attrList.size(); i++) {
	                Attribute attr = (Attribute) attrList.get(i);
	                if ((!attr.getName().equals("nodeName"))
	                        && (!attr.getName().equals("description"))
	                        && (!attr.getName().equals("nodeType"))
	                        && (!attr.getName().equals("role"))
	                        && (!attr.getName().equals("actionType"))
	                        // && ((!attr.getName().equals("WOComponentName") &&
	                        // (lev == 1)))
	                        && (!attr.getName().equals("className"))
	                        && (!attr.getName().equals("isWaiting"))
	                        && (!attr.getName().equals("isSelfActivated"))
	                        && (!attr.getName().equals("classname"))) {
	                    Class type;
	                    if (!((attr.getName().equals("width")) || (attr.getName().equals("height"))
	                            || (attr.getName().equals("posiX"))
	                            || (attr.getName().equals("posiY"))
	                            || (attr.getName().equals("textColor")) || (attr.getName()
	                            .equals("backColor")))) {
	                        if (attr.getName().equals("iswaiting")) {
	                            type = Boolean.class;
	                        } else {
	                            type = String.class;
	                        }
	                        Element newElement = new Element(attr.getName());
	                        newElement.setAttribute(new Attribute("className", type.getName()));
	                        newElement.addContent(new Text(attr.getValue()));
	                        returnedElement.removeAttribute(returnedElement.getAttribute(attr
	                                .getName()));
	                        nodeProperties.addContent(newElement);
	                    }
	                }
	            }
	            returnedElement.addContent(nodeProperties);

	            List petriGraphs = returnedElement.getChildren("dwgraph");
	            if (petriGraphs.size() == 1) {
	                restructurePetriGraph((Element) petriGraphs.get(0), null);
	            }

	            restructurePreConditions(returnedElement);
	            restructurePostConditions(returnedElement);

	            return returnedElement;

	        } catch (Exception e) {
	            if (logger.isLoggable(Level.WARNING))
	                logger.warning("Exception raised: " + e.getClass().getName()
	                        + ". See console for details.");
	            e.printStackTrace();
	            return null;
	        }
	    }

	    private void restructurePreConditions(Element nodeElement)
	    {
	        Vector newPreConditions = new Vector();
	        List allPreConditions = nodeElement.getChildren("dwprecondition");
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("restructurePreConditions() for " + nodeElement + " with "
	                    + allPreConditions.size() + " preconditions");
	        for (int i = 0; i < allPreConditions.size(); i++) {
	            Element oldPreConditionElement = (Element) allPreConditions.get(i);
	            Element newPreConditionElement = restructurePreCondition(oldPreConditionElement);
	            newPreConditions.add(newPreConditionElement);
	        }
	        nodeElement.removeChildren("dwprecondition");
	        for (int i = 0; i < newPreConditions.size(); i++) {
	            nodeElement.addContent((Element) newPreConditions.elementAt(i));
	        }
	    }

	    private Element restructurePreCondition(Element preConditionElement)
	    {
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("restructurePreCondition() for " + preConditionElement);
	        Element returnedElement = (Element) preConditionElement.clone();
	        try {
	            returnedElement.setName("FlexoPreCondition");
	            Attribute idAttr = returnedElement.getAttribute("ID");
	            idAttr.setName("preConditionId");
	            Attribute classNameAttribute = returnedElement.getAttribute("className");
	            if (classNameAttribute != null) {
	                returnedElement.removeAttribute(classNameAttribute);
	            }
	            Element preConditionProperties = new Element("PreConditionProperties");
	            List attrList = ((Element) returnedElement.clone()).getAttributes();
	            for (int i = 0; i < attrList.size(); i++) {
	                Attribute attr = (Attribute) attrList.get(i);
	                if (!attr.getName().equals("preConditionId")) {
	                    if (!((attr.getName().equals("width")) || (attr.getName().equals("height"))
	                            || (attr.getName().equals("posiX"))
	                            || (attr.getName().equals("posiY"))
	                            || (attr.getName().equals("textColor"))
	                            || (attr.getName().equals("fixedLocation"))
	                            || (attr.getName().equals("initTokenNbr")) || (attr.getName()
	                            .equals("backColor")))) {
	                        Class type = String.class;
	                        Element newElement = new Element(attr.getName());
	                        newElement.setAttribute(new Attribute("className", type.getName()));
	                        newElement.addContent(new Text(attr.getValue()));
	                        returnedElement.removeAttribute(returnedElement.getAttribute(attr
	                                .getName()));
	                        preConditionProperties.addContent(newElement);
	                    }
	                }
	            }
	            returnedElement.addContent(preConditionProperties);

	            return returnedElement;

	        } catch (Exception e) {
	            if (logger.isLoggable(Level.WARNING))
	                logger.warning("Exception raised: " + e.getClass().getName()
	                        + ". See console for details.");
	            e.printStackTrace();
	            return null;
	        }
	    }

	    private void restructurePostConditions(Element nodeElement)
	    {
	        Vector newPostConditions = new Vector();
	        List allPostConditions = nodeElement.getChildren("dwpostcondition");
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("restructurePostConditions() for " + nodeElement + " with "
	                    + allPostConditions.size() + " postconditions");
	        for (int i = 0; i < allPostConditions.size(); i++) {
	            Element oldPostConditionElement = (Element) allPostConditions.get(i);
	            Element newPostConditionElement = restructurePostCondition(oldPostConditionElement);
	            newPostConditions.add(newPostConditionElement);
	        }
	        nodeElement.removeChildren("dwpostcondition");
	        for (int i = 0; i < newPostConditions.size(); i++) {
	            nodeElement.addContent((Element) newPostConditions.elementAt(i));
	        }
	    }

	    private Element restructurePostCondition(Element postConditionElement)
	    {
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("restructurePostCondition() for " + postConditionElement);
	        Element returnedElement = (Element) postConditionElement.clone();
	        try {
	            returnedElement.setName("FlexoPostCondition");
	            Attribute idAttr = returnedElement.getAttribute("nextPreconditionID");
	            idAttr.setName("nextPreconditionId");
	            Attribute classNameAttribute = returnedElement.getAttribute("className");
	            if (classNameAttribute != null) {
	                returnedElement.removeAttribute(classNameAttribute);
	            }
	            Attribute postConditionType = returnedElement.getAttribute("postConditionType");
	            if (postConditionType.getValue().equals("Token")) {
	                postConditionType.setValue(PostConditionType.TOKEN.getName());
	            } else if (postConditionType.getValue().equals("NextOperation")) {
	                postConditionType.setValue(PostConditionType.NEXT_OPERATION.getName());
	            } else if (postConditionType.getValue().equals("NewInstance")) {
	                postConditionType.setValue(PostConditionType.NEW_INSTANCE.getName());
	            }

	            Element postConditionProperties = new Element("PostConditionProperties");
	            List attrList = ((Element) returnedElement.clone()).getAttributes();
	            for (int i = 0; i < attrList.size(); i++) {
	                Attribute attr = (Attribute) attrList.get(i);
	                if (!attr.getName().equals("nextPreconditionId")) {
	                    if (!((attr.getName().equals("width")) || (attr.getName().equals("height"))
	                            || (attr.getName().equals("posiX"))
	                            || (attr.getName().equals("posiY"))
	                            || (attr.getName().equals("textColor"))
	                            || (attr.getName().equals("delay"))
	                            || (attr.getName().equals("tokenIncrem"))
	                            || (attr.getName().equals("postConditionType")) || (attr.getName()
	                            .equals("backColor")))) {
	                        Class type = String.class;
	                        Element newElement = new Element(attr.getName());
	                        newElement.setAttribute(new Attribute("className", type.getName()));
	                        newElement.addContent(new Text(attr.getValue()));
	                        returnedElement.removeAttribute(returnedElement.getAttribute(attr
	                                .getName()));
	                        postConditionProperties.addContent(newElement);
	                    }
	                }
	            }
	            returnedElement.addContent(postConditionProperties);

	            return returnedElement;

	        } catch (Exception e) {
	            if (logger.isLoggable(Level.WARNING))
	                logger.warning("Exception raised: " + e.getClass().getName()
	                        + ". See console for details.");
	            e.printStackTrace();
	            return null;
	        }
	    }

	    private void restructureProperties(Element aProcessElement)
	    {
	        Vector newNodes = new Vector();
	        List allProperties = aProcessElement.getChildren("property");
	        for (int i = 0; i < allProperties.size(); i++) {
	            Element oldNodeElement = (Element) allProperties.get(i);
	            Element newNodeElement = restructureProperty(oldNodeElement);
	            newNodes.add(newNodeElement);
	        }
	        aProcessElement.removeChildren("property");
	        for (int i = 0; i < newNodes.size(); i++) {
	            aProcessElement.addContent((Element) newNodes.elementAt(i));
	        }
	    }

	    private Element restructureProperty(Element propertyElement)
	    {
	        boolean statusConversionIsDone = false;
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("restructureProperty() for " + propertyElement);
	        Element returnedElement = (Element) propertyElement.clone();
	        try {
	            returnedElement.setName("FlexoProcessPropertyList");
	            Element xmltagElement = (Element) returnedElement.getChildren("xmltagname").get(0);
	            String xmlTag = xmltagElement.getValue();
	            if (xmlTag.equals("role")) {
	                Element rolesElement = new Element("RoleList");
	                Element listElement = (Element) returnedElement.getChildren("roles").get(0);
	                List elements = listElement.getChildren();
	                for (int i = 0; i < elements.size(); i++) {
	                    Element next = (Element) elements.get(i);
	                    next.setName("Role");
	                    rolesElement.addContent((Element) next.clone());
	                }
	                return rolesElement;
	            } else if (xmlTag.equals("deadline")) {
	                Element dlElement = new Element("DeadLineList");
	                Element listElement = (Element) returnedElement.getChildren("deadlines").get(0);
	                List elements = listElement.getChildren();
	                for (int i = 0; i < elements.size(); i++) {
	                    Element next = (Element) elements.get(i);
	                    next.setName("DeadLine");
	                    dlElement.addContent((Element) next.clone());
	                }
	                return dlElement;
	            } else if (xmlTag.equals("dwstatus")) {
	                Element statusElement = new Element("StatusList");
	                Element listElement = (Element) returnedElement.getChildren("dwstatuss").get(0);
	                List elements = listElement.getChildren();
	                for (int i = 0; i < elements.size(); i++) {
	                    Element next = (Element) elements.get(i);
	                    next.setName("Status");
	                    statusElement.addContent((Element) next.clone());
	                }
	                statusConversionIsDone = true;
	                return statusElement;
	            } else if ((xmlTag.equals("status") && !statusConversionIsDone)) {
	                Element statusElement = new Element("StatusList");
	                Element listElement = (Element) returnedElement.getChildren("statuss").get(0);
	                List elements = listElement.getChildren();
	                for (int i = 0; i < elements.size(); i++) {
	                    Element next = (Element) elements.get(i);
	                    next.setName("Status");
	                    statusElement.addContent((Element) next.clone());
	                }
	                return statusElement;
	            } else {
	                Element defaultElement = (Element) returnedElement.getChildren(
	                        "default" + xmlTag + "s").get(0);
	                defaultElement.setName("DefaultPropertyListElement");
	                Element listElement = (Element) returnedElement.getChildren(xmlTag + "s")
	                        .get(0);
	                List elements = listElement.getChildren();
	                for (int i = 0; i < elements.size(); i++) {
	                    Element next = (Element) elements.get(i);
	                    Element newElement = new Element("PropertyListElement");
	                    Element propsElement = new Element("Properties");
	                    for (int j = 0; j < next.getChildren().size(); j++) {
	                        propsElement.addContent((Element) ((Element) next.getChildren().get(j))
	                                .clone());
	                    }
	                    newElement.addContent(propsElement);
	                    returnedElement.addContent(newElement);
	                }
	                returnedElement.removeChildren(xmlTag + "s");
	            }
	            return returnedElement;

	        } catch (Exception e) {
	            if (logger.isLoggable(Level.WARNING))
	                logger.warning("Exception raised: " + e.getClass().getName()
	                        + ". See console for details.");
	            e.printStackTrace();
	            return null;
	        }
	    }

	    private boolean save()
	    {
	        FileWritingLock lock = willWriteOnDisk();
	        boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
	        hasWrittenOnDisk(lock);
	        return returned;
	   }
	}*/

	/*protected class ProcessConverter2
	{

	    protected boolean conversionWasSucessfull = false;

	    protected Document document;

	    protected Element processElement;

	    protected ProcessConverter2()
	    {
	        super();
	        // roles = new Hashtable();
	        // pendingRoles = new Hashtable();
	        try {
	            document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
	            FlexoComponentFolder processFolder = convert();
	            conversionWasSucessfull = save();
	            processFolder.setComponentPrefix(getFlexoProcess().getComponentPrefix());
	        } catch (Exception e) {
	            // Warns about the exception
	            if (logger.isLoggable(Level.WARNING))
	                logger.warning("Exception raised: " + e.getClass().getName()
	                        + ". See console for details.");
	            e.printStackTrace();
	        }
	    }

	    private FlexoComponentFolder convert() throws DuplicateResourceException
	    {
	        FlexoComponentFolder processFolder = new FlexoComponentFolder(getName(), getProject()
	                .getFlexoComponentLibrary());

	        getProject().getFlexoComponentLibrary().getRootFolder().addToSubFolders(processFolder);
	        processThumbnail(processFolder);
	        processPopup(processFolder);
	        processOperationComponent(processFolder);
	        processFolder.setGenerationRelativePath("src/" + getName());
	        return processFolder;
	    }

	    private void processOperationComponent(FlexoComponentFolder defaultFolder)
	    {
	        Iterator tableElementIterator = document.getDescendants(new ElementFilter(
	                "OperationComponentDefinition"));
	        while (tableElementIterator.hasNext()) {
	            Element nextElement = (Element) tableElementIterator.next();
	            String thumbnailWOName = nextElement.getAttributeValue("name");
	            if (thumbnailWOName != null) {
	                ComponentDefinition def = null;
	                try {
	                    def = new OperationComponentDefinition(thumbnailWOName, defaultFolder
	                            .getComponentLibrary(), defaultFolder, getProject());

	                } catch (DuplicateResourceException e) {
	                    if (logger.isLoggable(Level.INFO))
	                        logger.info("Operation : " + thumbnailWOName
	                                + " is used more than once");
	                }
	                if (def != null) {
	                    FlexoComponentFolder.convertComponent(def);
	                } else {
	                    FlexoResource res = getProject().resourceForKey(
	                            FlexoOperationComponentResource
	                                    .resourceIdentifierForName(thumbnailWOName));
	                    FlexoComponentFolder.convertComponent(res);
	                }
	            }
	        }
	        tableElementIterator = document.getDescendants(new ElementFilter(
	                "OperationComponentDefinition"));
	        while (tableElementIterator.hasNext()) {
	            Element nextElement = (Element) tableElementIterator.next();
	            nextElement.setAttribute("componentName", nextElement.getAttributeValue("name"));
	            nextElement.setName("OperationComponentInstance");
	        }
	    }

	    private void processThumbnail(FlexoComponentFolder defaultFolder)
	    {
	        Iterator tableElementIterator = document.getDescendants(new ElementFilter(
	                "ThumbnailComponentDefinition"));
	        while (tableElementIterator.hasNext()) {
	            Element nextElement = (Element) tableElementIterator.next();
	            String thumbnailWOName = nextElement.getAttributeValue("name");
	            if (thumbnailWOName != null) {
	                ComponentDefinition def = null;
	                try {
	                    def = new TabComponentDefinition(thumbnailWOName, defaultFolder
	                            .getComponentLibrary(), defaultFolder, getProject());

	                } catch (DuplicateResourceException e) {
	                    // TODO Auto-generated catch block
	                    // e.printStackTrace();
	                }
	                if (def != null) {
	                    FlexoComponentFolder.convertComponent(def);
	                } else {
	                    FlexoResource res = getProject().resourceForKey(
	                            FlexoThumbnailComponentResource
	                                    .resourceIdentifierForName(thumbnailWOName));
	                    FlexoComponentFolder.convertComponent(res);
	                }
	            }
	        }
	        Vector elementToClear = new Vector();
	        tableElementIterator = document.getDescendants(new ElementFilter(
	                "OperationComponentDefinition"));
	        while (tableElementIterator.hasNext()) {
	            elementToClear.add((Element) tableElementIterator.next());
	            // nextElement.getChildren().clear();
	        }
	        Enumeration en = elementToClear.elements();
	        while (en.hasMoreElements()) {
	            ((Element) en.nextElement()).getChildren().clear();
	        }
	    }

	    private void processPopup(FlexoComponentFolder defaultFolder)
	    {
	        Iterator tableElementIterator = document.getDescendants(new ElementFilter(
	                "PopupComponentDefinition"));
	        while (tableElementIterator.hasNext()) {
	            Element nextElement = (Element) tableElementIterator.next();
	            String thumbnailWOName = nextElement.getAttributeValue("name");
	            if (thumbnailWOName != null) {
	                ComponentDefinition def = null;
	                try {
	                    def = new PopupComponentDefinition(thumbnailWOName, defaultFolder
	                            .getComponentLibrary(), defaultFolder, getProject());

	                } catch (DuplicateResourceException e) {
	                    // TODO Auto-generated catch block
	                    // e.printStackTrace();
	                }
	                if (def != null) {
	                    FlexoComponentFolder.convertComponent(def);
	                } else {
	                    FlexoResource res = getProject().resourceForKey(
	                            FlexoPopupComponentResource
	                                    .resourceIdentifierForName(thumbnailWOName));
	                    FlexoComponentFolder.convertComponent(res);
	                }
	            }
	        }
	        document.getRootElement().removeChild("PopupComponentList");
	    }

	    private boolean save()
	    {
	        FileWritingLock lock = willWriteOnDisk();
	        boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
	        hasWrittenOnDisk(lock);
	        return returned;
	    }
	}*/

	/*protected class ProcessConverter3
	{

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element processElement;

		protected ProcessConverter3()
		{
			super();
			// roles = new Hashtable();
			// pendingRoles = new Hashtable();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName()
							+ ". See console for details.");
				e.printStackTrace();
			}
		}

		private void convert() throws DuplicateResourceException {
			int counter = 0;
			Vector<Element> elementsWithStatusChild = new Vector<Element>();
			Iterator actionElementIterator = document.getDescendants(new ElementFilter("ActionNode"));
			while (actionElementIterator.hasNext()) {
				Element elem = (Element) actionElementIterator.next();
				Element status = elem.getChild("Status");
				if (status != null) {
					counter++;
					if (status.getText() != null)
						elem.setAttribute("statusAsString", status.getText());
					else if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not find status text in tags <Status> " + status);
					elementsWithStatusChild.add(elem);
				}
			}
			if (logger.isLoggable(Level.INFO))
				logger.info("Converted " + counter + " next status for ActionNodes");
			counter = 0;
			Iterator operationElementIterator = document.getDescendants(new ElementFilter("OperationNode"));
			while (operationElementIterator.hasNext()) {
				Element elem = (Element) operationElementIterator.next();
				Element status = elem.getChild("Status");
				if (status != null) {
					counter++;
					if (status.getText() != null)
						elem.setAttribute("statusAsString", status.getText());
					else if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not find status text in tags <Status> " + status);
					elementsWithStatusChild.add(elem);
				}
			}
			if (logger.isLoggable(Level.INFO))
				logger.info("Converted " + counter + " next status for OperationNodes");
			counter = 0;
			Iterator activityElementIterator = document.getDescendants(new ElementFilter("ActivityNode"));
			while (activityElementIterator.hasNext()) {
				Element elem = (Element) activityElementIterator.next();
				Element status = elem.getChild("Status");
				if (status != null) {
					counter++;
					if (status.getText() != null)
						elem.setAttribute("statusAsString", status.getText());
					else if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not find status text in tags <Status> " + status);
					elementsWithStatusChild.add(elem);
				}
			}
			if (logger.isLoggable(Level.INFO))
				logger.info("Converted " + counter + " next status for ActivityNodes");
			counter = 0;
			// SubProcessNode
			Iterator subProcessElementIterator = document.getDescendants(new ElementFilter("SubProcessNode"));
			while (subProcessElementIterator.hasNext()) {
				Element elem = (Element) subProcessElementIterator.next();
				Element status = elem.getChild("Status");
				if (status != null) {
					counter++;
					if (status.getText() != null)
						elem.setAttribute("statusAsString", status.getText());
					else if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not find status text in tags <Status> " + status);
					elementsWithStatusChild.add(elem);
				}
			}
			if (logger.isLoggable(Level.INFO))
				logger.info("Converted " + counter + " next status for SubProcessNodes");
			Iterator<Element> i = elementsWithStatusChild.iterator();
			while (i.hasNext()) {
				Element elem = i.next();
				elem.removeChild("Status");
			}
		}

		private boolean save()
		{
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}
	}

	protected class ProcessConverter4
	{

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element processElement;

		protected ProcessConverter4()
		{
			super();
			// roles = new Hashtable();
			// pendingRoles = new Hashtable();
			try {
				document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				convert();
				conversionWasSucessfull = save();
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName()
							+ ". See console for details.");
				e.printStackTrace();
			}
		}

		private void convert() throws DuplicateResourceException
		{
			XMLMapping mapping = getXmlMappings().getMappingForClassAndVersion(getResourceDataClass(), new FlexoVersion("4.0"));

			int counter = 0;
			Iterator operatorElementIterator = document.getDescendants(new ElementFilter("OperatorNode"));
			while (operatorElementIterator.hasNext()) {
				Element elem = (Element) operatorElementIterator.next();
				String operatorType = elem.getAttributeValue("operatorType");
				String newName = "UNKNOWNOperator";
				if (operatorType.equalsIgnoreCase(OperatorType.AND.getName())) {
					newName = "ANDOperator";
				}
				if (operatorType.equalsIgnoreCase(OperatorType.OR.getName())) {
					newName = "OROperator";
				}
				if (operatorType.equalsIgnoreCase(OperatorType.IF.getName())) {
					newName = "IFOperator";
				}
				if (operatorType.equalsIgnoreCase(OperatorType.SWITCH.getName())) {
					newName = "SWITCHOperator";
				}
				if (operatorType.equalsIgnoreCase(OperatorType.LOOP.getName())) {
					newName = "LOOPOperator";
				}
				elem.setName(newName);
				String id = elem.getAttributeValue("id");
				logger.info("Converted OperatorNode to "+newName+" for id="+id);

				if (operatorType.equalsIgnoreCase(OperatorType.IF.getName())) {
					Iterator containedEdgesIterator = elem.getDescendants(new ElementFilter());
					while (containedEdgesIterator.hasNext()) {
						Element edgeElem = (Element) containedEdgesIterator.next();
						ModelEntity entity = mapping.entityWithXMLTag(edgeElem.getName());
						if (entity != null) {
							Class elementClass = entity.getRelatedClass();
							if (OperatorOutEdge.class.isAssignableFrom(elementClass)) {
								if (edgeElem.getAttribute("outputValue") != null) {
									if (edgeElem.getAttributeValue("outputValue").equals("true")) edgeElem.setName("PositiveEvaluation"+edgeElem.getName());
									if (edgeElem.getAttributeValue("outputValue").equals("false")) edgeElem.setName("NegativeEvaluation"+edgeElem.getName());
								}
								logger.info("Also renamed "+edgeElem);
							}
						}
					}
				}

				Iterator referencesIterator = document.getDescendants(new ElementFilter("StartOperatorNode"));
				while (referencesIterator.hasNext()) {
					Element refElement = (Element) referencesIterator.next();
					if (refElement.getAttributeValue("idref") != null
							&& id.equals(refElement.getAttributeValue("idref"))) {
						refElement.setName("Start"+newName);
						logger.info("Also converted StartOperatorNode to "+"Start"+newName+" for id="+id);
					}
				}
				referencesIterator = document.getDescendants(new ElementFilter("NextOperatorNode"));
				while (referencesIterator.hasNext()) {
					Element refElement = (Element) referencesIterator.next();
					if (refElement.getAttributeValue("idref") != null
							&& id.equals(refElement.getAttributeValue("idref"))) {
						refElement.setName("Next"+newName);
						logger.info("Also converted NextOperatorNode to "+"Next"+newName+" for id="+id);
					}
				}
				counter++;
			}
			if (logger.isLoggable(Level.INFO))
				logger.info("Converted " + counter + " operators");

			counter = 0;
			FlexoWorkflow wkf = getProject().getFlexoWorkflow();

			for (Enumeration en = wkf.allLocalProcessNodes(); en.hasMoreElements();)
			{
	            FlexoProcessNode next = (FlexoProcessNode) en.nextElement();
	            if (next.getName().equals(getName()))
	            {
	            	try
	            	{
	            		next.setIndex(Integer.parseInt(document.getRootElement().getAttributeValue("docIndex")));
	            	}
	            	catch(NumberFormatException e)
	            	{
	            		logger.log(Level.WARNING, "Doc index cannot be parsed for process " + getName(), e);
	            	}
	            	break;
	            }
	        }

			Iterator roleList = document.getDescendants(new ElementFilter("RoleList"));
			Hashtable<String,Role> rolesWithPrevioursIdentifier = new Hashtable<String, Role>();
			while (roleList.hasNext()) {
				Element roleListElem = (Element) roleList.next();
				Iterator roleIterator = roleListElem.getDescendants(new ElementFilter("Role"));
				while (roleIterator.hasNext()) {
					Element roleElem = (Element) roleIterator.next();
					Element labelElem = roleElem.getChild("label");
					Element colorElem = roleElem.getChild("color");
					String id = roleElem.getAttributeValue("id");
					if (id==null) {
						if (logger.isLoggable(Level.WARNING))
							logger.warning("The following element has no id: "+roleElem+"\nIt will not be converted.");
						continue;
					}
					Role role = new Role(wkf, labelElem.getTextTrim());
					role.setUserIdentifier(roleElem.getAttributeValue("userID"));
					try {
						role.setFlexoID(Long.valueOf(roleElem.getAttributeValue("flexoID")));
					} catch (NumberFormatException e2) {
						e2.printStackTrace();
					}
					role.setColor(new FlexoColor(colorElem.getTextTrim()));
					role.setDescription(roleElem.getAttributeValue("description"));
					role.setDontEscapeLatex(Boolean.valueOf(roleElem.getAttributeValue("dontEscapeLatex")));
					role.setDontGenerate(Boolean.valueOf(roleElem.getAttributeValue("dontGenerate")));
					try {
						role.setIndexValue(Integer.valueOf(roleElem.getAttributeValue("docIndex")));
					} catch (NumberFormatException e1) {
						e1.printStackTrace();
					}
					try {
						wkf.getRoleList().addToRoles(role);
						rolesWithPrevioursIdentifier.put(id, role);
						counter++;
					} catch (DuplicateRoleException e) {
						// Unlikely to happen, but who can be sure nowadays
						if (logger.isLoggable(Level.WARNING))
							logger.warning("Role with name "+role.getName()+" was already in role list.");
					}
				}
				roleList.remove();
			}

			if (logger.isLoggable(Level.INFO))
				logger.info("Converted "+counter+" roles");

			counter = 0;
			Iterator subProcessNodeElementIterator = document.getDescendants(new ElementFilter("SubProcessNode"));
			while (subProcessNodeElementIterator.hasNext()) {
				Element elem = (Element) subProcessNodeElementIterator.next();
				String spNodeType = elem.getAttributeValue("type");
				String newName = "UNKNOWNType";
				if (spNodeType.equalsIgnoreCase(SubProcessType.FORK.getName())) {
					newName = "MultipleInstanceSubProcessNode";
					elem.setAttribute("isSequential", "false");
				}
				if (spNodeType.equalsIgnoreCase(SubProcessType.LOOP.getName())) {
					newName = "MultipleInstanceSubProcessNode";
					elem.setAttribute("isSequential", "true");
				}
				if (spNodeType.equalsIgnoreCase(SubProcessType.SINGLE.getName())) {
					newName = "SingleInstanceSubProcessNode";
				}

				// Now we must loop-up the process, hu....
				String processName = elem.getAttributeValue("subProcessName");
				FlexoProcessResource fileResource = getProject().getFlexoProcessResource(processName);
				try {
					Document processDocument = XMLUtils.getJDOMDocument(fileResource.getFile());
					Element root = processDocument.getRootElement();
					if (root.getAttributeValue("isWebService").equals("true")) {
						newName = "WSCallSubProcessNode";
					}
					String idRef = root.getAttributeValue("id");
					elem.setAttribute("subProcess", "PROCESS."+processName+"#"+idRef);
				} catch (Exception e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Exception raised: " + e.getClass().getName()
								+ ". See console for details.");
					e.printStackTrace();
				}

				elem.setName(newName);

				String id = elem.getAttributeValue("id");
				logger.info("Converted SubProcessNode to "+newName+" for id="+id);



				Iterator referencesIterator = document.getDescendants(new ElementFilter("StartSubProcessNode"));
				while (referencesIterator.hasNext()) {
					Element refElement = (Element) referencesIterator.next();
					if (refElement.getAttributeValue("idref") != null
							&& id.equals(refElement.getAttributeValue("idref"))) {
						refElement.setName("Start"+newName);
						logger.info("Also converted StartSubProcessNode to "+"Start"+newName+" for id="+id);
					}
				}
				referencesIterator = document.getDescendants(new ElementFilter("AttachedSubProcessNode"));
				while (referencesIterator.hasNext()) {
					Element refElement = (Element) referencesIterator.next();
					if (refElement.getAttributeValue("idref") != null
							&& id.equals(refElement.getAttributeValue("idref"))) {
						refElement.setName("Attached"+newName);
						logger.info("Also converted AttachedSubProcessNode to "+"Attached"+newName+" for id="+id);
					}
				}
				counter++;
			}
			if (logger.isLoggable(Level.INFO))
				logger.info("Converted " + counter + " sub-process nodes");

			String BASIC_PROCESS_EDITOR = "bpe";

			Hashtable<Element,Element> elementsToAdd = new Hashtable<Element,Element>();
			Iterator elementIterator = document.getDescendants(new ElementFilter());
			while (elementIterator.hasNext()) {
				Element elem = (Element) elementIterator.next();
				if (elem.getAttribute("idref") != null) continue;
				ModelEntity entity = mapping.entityWithXMLTag(elem.getName());
				if (entity != null) {
					Class elementClass = entity.getRelatedClass();
					if (WKFObject.class.isAssignableFrom(elementClass)) {
						Element gpElement = new Element("GraphicalProperties");
						// Some color are to recover ?
						if (PetriGraphNode.class.isAssignableFrom(elementClass)) {
							if (elem.getAttribute("backColor") != null) {
								Element bgColorElement = new Element(WKFObject.BG_COLOR+"_"+BASIC_PROCESS_EDITOR);
								bgColorElement.setAttribute("className",FlexoColor.class.getName());
								bgColorElement.setText(elem.getAttributeValue("backColor"));
								gpElement.addContent(bgColorElement);
							}
							if (elem.getAttribute("textColor") != null) {
								Element textColorElement = new Element(WKFObject.TEXT_COLOR+"_"+BASIC_PROCESS_EDITOR);
								textColorElement.setAttribute("className",FlexoColor.class.getName());
								textColorElement.setText(elem.getAttributeValue("textColor"));
								gpElement.addContent(textColorElement);
							}
							if (elem.getAttribute("roleTextColor") != null) {
								Element roleTextColorElement = new Element("roleTextColor"+"_"+BASIC_PROCESS_EDITOR);
								roleTextColorElement.setAttribute("className",FlexoColor.class.getName());
								roleTextColorElement.setText(elem.getAttributeValue("roleTextColor"));
								gpElement.addContent(roleTextColorElement);
							}
							if (elem.getAttribute("componentTextColor") != null) {
								Element componentTextColorElement = new Element("componentTextColor"+"_"+BASIC_PROCESS_EDITOR);
								componentTextColorElement.setAttribute("className",FlexoColor.class.getName());
								componentTextColorElement.setText(elem.getAttributeValue("componentTextColor"));
								gpElement.addContent(componentTextColorElement);
							}
						}
						// PosX and PosY ???
						if (PetriGraphNode.class.isAssignableFrom(elementClass)
								|| FlexoPort.class.isAssignableFrom(elementClass)
								|| OperatorNode.class.isAssignableFrom(elementClass)
								|| FlexoPetriGraph.class.isAssignableFrom(elementClass)
								|| PortRegistery.class.isAssignableFrom(elementClass)
								|| PortMapRegistery.class.isAssignableFrom(elementClass)) {
							if (elem.getAttribute("posiX") != null) {
								Element posXElement = new Element(WKFObject.POSX+"_"+BASIC_PROCESS_EDITOR);
								posXElement.setAttribute("className",Double.class.getName());
								posXElement.setText(elem.getAttributeValue("posiX"));
								gpElement.addContent(posXElement);
							}
							if (elem.getAttribute("posiY") != null) {
								Element posYElement = new Element(WKFObject.POSY+"_"+BASIC_PROCESS_EDITOR);
								posYElement.setAttribute("className",Double.class.getName());
								posYElement.setText(elem.getAttributeValue("posiY"));
								gpElement.addContent(posYElement);
							}
						}
						// Width and Height ???
						if ((AbstractActivityNode.class.isAssignableFrom(elementClass)
								&& !SelfExecutableActivityNode.class.isAssignableFrom(elementClass)
								&& !elem.getAttributeValue("nodeType").equals("BEGIN")
								&& !elem.getAttributeValue("nodeType").equals("END"))
								|| (OperationNode.class.isAssignableFrom(elementClass)
										&& !SelfExecutableOperationNode.class.isAssignableFrom(elementClass)
										&& !elem.getAttributeValue("nodeType").equals("BEGIN")
										&& !elem.getAttributeValue("nodeType").equals("END"))
										|| FlexoPetriGraph.class.isAssignableFrom(elementClass)
										|| PortRegistery.class.isAssignableFrom(elementClass)) {
							if (elem.getAttribute("width") != null) {
								Element widthElement = new Element(WKFObject.WIDTH+"_"+BASIC_PROCESS_EDITOR);
								widthElement.setAttribute("className",Double.class.getName());
								widthElement.setText(elem.getAttributeValue("width"));
								gpElement.addContent(widthElement);
							}
							if (elem.getAttribute("height") != null) {
								Element heightElement = new Element(WKFObject.HEIGHT+"_"+BASIC_PROCESS_EDITOR);
								heightElement.setAttribute("className",Double.class.getName());
								heightElement.setText(elem.getAttributeValue("height"));
								gpElement.addContent(heightElement);
							}
							if ((AbstractActivityNode.class.isAssignableFrom(elementClass)
									&& !SelfExecutableActivityNode.class.isAssignableFrom(elementClass)
									&& !elem.getAttributeValue("nodeType").equals("BEGIN")
									&& !elem.getAttributeValue("nodeType").equals("END"))) {
								// Let's recover the roles
								Element roleElem = elem.getChild("Role");
								if (roleElem!=null && roleElem.getAttributeValue("idref")!=null) {
									String ref = roleElem.getAttributeValue("idref");
									Role matchingRole = rolesWithPrevioursIdentifier.get(ref);
									if (matchingRole!=null) {
										String roleIdentifier = FlexoModelObjectReference.getSerializationRepresentationForObject(matchingRole, false);
										elem.setAttribute("role", roleIdentifier);
									} else {
										if (logger.isLoggable(Level.WARNING))
											logger.warning("Could not recover role with identifier: "+ref);
									}
								}
							}
						}
						// visibility is to recover ?
						if (FlexoPetriGraph.class.isAssignableFrom(elementClass)) {
							if (elem.getAttribute("isVisible") != null) {
								Element visibilityElement = new Element(WKFObject.VISIBILITY);
								visibilityElement.setAttribute("className",Boolean.class.getName());
								visibilityElement.setText(elem.getAttributeValue("isVisible"));
								gpElement.addContent(visibilityElement);
							}
						}

						// Label position is to recover ?
						if (AbstractNode.class.isAssignableFrom(elementClass)) {
							if (elem.getAttribute("nodeLabelPosX") != null) {
								Element labelXElement = new Element(AbstractNode.LABEL_POSX+"_"+BASIC_PROCESS_EDITOR);
								labelXElement.setAttribute("className",Double.class.getName());
								labelXElement.setText(elem.getAttributeValue("nodeLabelPosX"));
								gpElement.addContent(labelXElement);
							}
							if (elem.getAttribute("nodeLabelPosY") != null) {
								Element labelYElement = new Element(AbstractNode.LABEL_POSY+"_"+BASIC_PROCESS_EDITOR);
								labelYElement.setAttribute("className",Double.class.getName());
								labelYElement.setText(elem.getAttributeValue("nodeLabelPosY"));
								gpElement.addContent(labelYElement);
							}
						}


						if (elem.getAttribute("backColor") != null) elem.removeAttribute("backColor");
						if (elem.getAttribute("fgColor") != null) elem.removeAttribute("fgColor");
						if (elem.getAttribute("textColor") != null) elem.removeAttribute("textColor");
						if (elem.getAttribute("roleTextColor") != null) elem.removeAttribute("roleTextColor");
						if (elem.getAttribute("componentTextColor") != null) elem.removeAttribute("componentTextColor");
						if (elem.getAttribute("posiX") != null) elem.removeAttribute("posiX");
						if (elem.getAttribute("posiY") != null) elem.removeAttribute("posiY");
						if (elem.getAttribute("width") != null) elem.removeAttribute("width");
						if (elem.getAttribute("height") != null) elem.removeAttribute("height");
						if (elem.getAttribute("isVisible") != null) elem.removeAttribute("isVisible");
						if (elem.getAttribute("nodeLabelPosX") != null) elem.removeAttribute("nodeLabelPosX");
						if (elem.getAttribute("nodeLabelPosY") != null) elem.removeAttribute("nodeLabelPosY");

						if (elem.getAttribute("fixedLocation") != null) elem.removeAttribute("fixedLocation");
						if (elem.getAttribute("inducedLocation") != null) elem.removeAttribute("inducedLocation");
						if (elem.getAttribute("deducedLocation") != null) elem.removeAttribute("deducedLocation");
						if (elem.getAttribute("inducedDeducedLocation") != null) elem.removeAttribute("inducedDeducedLocation");
						if (elem.getAttribute("labelPosition") != null) elem.removeAttribute("labelPosition");
						if (elem.getAttribute("inducedLabelPosition") != null) elem.removeAttribute("inducedLabelPosition");
						if (elem.getAttribute("deducedLabelPosition") != null) elem.removeAttribute("deducedLabelPosition");
						if (elem.getAttribute("inducedDeducedLabelPosition") != null) elem.removeAttribute("inducedDeducedLabelPosition");

						if (elem.getAttribute("displayLabel") != null) elem.removeAttribute("displayLabel");

						elementsToAdd.put(elem, gpElement);
					}
				}
			}
			for (Element elt : elementsToAdd.keySet()) {
				elt.addContent(elementsToAdd.get(elt));
				logger.info("Recovered graphical properties for "+elt.getName());
			}

			if (logger.isLoggable(Level.INFO))
				logger.info("Converting message edges");
			// Not sure those exists, but let's do it anyway
			Iterator extMsgInEdgeIterator = document.getDescendants(new ElementFilter("ExternalMessageInEdge"));
			while (extMsgInEdgeIterator.hasNext()) {
				Element extMsgInEdge = (Element) extMsgInEdgeIterator.next();
				extMsgInEdge.setName("ExternalFlexoNodeMessageInEdge");
			}
			Iterator extMsgOutEdgeIterator = document.getDescendants(new ElementFilter("ExternalMessageOutEdge"));
			while (extMsgOutEdgeIterator.hasNext()) {
				Element extMsgOutEdge = (Element) extMsgOutEdgeIterator.next();
				extMsgOutEdge.setName("ExternalFlexoNodeMessageOutEdge");
			}

			// Incoming ones
			extMsgInEdgeIterator = document.getDescendants(new ElementFilter("IncomingExternalMessageInEdge"));
			while (extMsgInEdgeIterator.hasNext()) {
				Element extMsgInEdge = (Element) extMsgInEdgeIterator.next();
				extMsgInEdge.setName("IncomingExternalFlexoNodeMessageInEdge");
			}
			extMsgOutEdgeIterator = document.getDescendants(new ElementFilter("IncomingExternalMessageOutEdge"));
			while (extMsgOutEdgeIterator.hasNext()) {
				Element extMsgOutEdge = (Element) extMsgOutEdgeIterator.next();
				extMsgOutEdge.setName("IncomingExternalFlexoNodeMessageOutEdge");
			}

			// Outgoing ones
			extMsgInEdgeIterator = document.getDescendants(new ElementFilter("OutgoingExternalMessageInEdge"));
			while (extMsgInEdgeIterator.hasNext()) {
				Element extMsgInEdge = (Element) extMsgInEdgeIterator.next();
				extMsgInEdge.setName("OutgoingExternalFlexoNodeMessageInEdge");
			}
			extMsgOutEdgeIterator = document.getDescendants(new ElementFilter("OutgoingExternalMessageOutEdge"));
			while (extMsgOutEdgeIterator.hasNext()) {
				Element extMsgOutEdge = (Element) extMsgOutEdgeIterator.next();
				extMsgOutEdge.setName("OutgoingExternalFlexoNodeMessageOutEdge");
			}

			if (logger.isLoggable(Level.INFO))
				logger.info("Converting tab component instances");
			Iterator tabComponentInstanceElementIterator = document.getDescendants(new ElementFilter("TabOperationComponentInstance"));
			while (tabComponentInstanceElementIterator.hasNext()) {
				Element tab = (Element) tabComponentInstanceElementIterator.next();
				tab.setName("TabComponentInstance");
			}
			tabComponentInstanceElementIterator = document.getDescendants(new ElementFilter("TabActionInstance"));
			while (tabComponentInstanceElementIterator.hasNext()) {
				Element tab = (Element) tabComponentInstanceElementIterator.next();
				tab.setName("TabComponentInstance");
			}

		}

		private boolean save()
		{
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}
	}
	 */
	protected class ProcessConverter5 {

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element processElement;

		protected ProcessConverter5() {
			super();
			// roles = new Hashtable();
			// pendingRoles = new Hashtable();
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

		private void convert() throws DuplicateResourceException {
			int count = 0;
			XMLMapping processMapping = new FlexoXMLMappings().getWKFMapping();

			// 1. Convert all Next... to End...
			Iterator nextElementIterator = document.getDescendants(new AbstractFilter() {
				@Override
				public boolean matches(Object obj) {
					if (obj instanceof Element) {
						Element el = (Element) obj;
						return el.getName().startsWith("Next");
					} else {
						return false;
					}
				}
			});
			while (nextElementIterator.hasNext()) {
				Element elem = (Element) nextElementIterator.next();
				elem.setName("End" + elem.getName().substring("Next".length()));
				count++;
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Converted " + count + " ending elements");
			}

			count = 0;
			// 2. Convert all precondition attached to an EventNode and move all the incoming edges to the incoming edges of the event.
			Hashtable<String, Element> elementsWithID = new Hashtable<String, Element>();
			Hashtable<String, Vector<Element>> elementsWithIDRef = new Hashtable<String, Vector<Element>>();
			Vector<ModelEntity> eventEntities = processMapping.entityForClass(EventNode.class).getAllChildrenEntities();
			final Vector<String> eventXMLTags = new Vector<String>();
			for (ModelEntity entity : eventEntities) {
				eventXMLTags.add(entity.getDefaultXmlTag());
			}
			Iterator elementIterator = document.getDescendants(new AbstractFilter() {
				@Override
				public boolean matches(Object obj) {
					return obj instanceof Element
							&& (((Element) obj).getAttribute("id") != null || ((Element) obj).getAttribute("idref") != null);
				}
			});
			while (elementIterator.hasNext()) {
				Element elem = (Element) elementIterator.next();
				if (elem.getAttribute("id") != null) {
					elementsWithID.put(elem.getAttributeValue("id"), elem);
				} else {
					String idRef = elem.getAttributeValue("idref");
					Vector<Element> v;
					if (elementsWithIDRef.get(idRef) != null) {
						v = elementsWithIDRef.get(idRef);
					} else {
						elementsWithIDRef.put(idRef, v = new Vector<Element>());
					}
					v.add(elem);
				}
			}
			swapUnalignedPreconditions(elementsWithID, elementsWithIDRef);

			Enumeration<String> ids = elementsWithID.keys();
			while (ids.hasMoreElements()) {
				String key = ids.nextElement();
				Element elem = elementsWithID.get(key);
				if (elem.getName().equals("FlexoPreCondition")) {
					for (String string : eventXMLTags) {
						if (elem.getParentElement() == null) {
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("Found pre condition " + elem + " " + key + " without parent");
							}
							break;
						}
						if (elem.getParentElement().getName().equals(string)) {
							handlePreConditionAttachedToEvent(elem, elementsWithID, elementsWithIDRef);
							count++;
							break;
						}
					}
				}
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Converted " + count + " pre-conditions attached to an event");
			}

			count = 0;
			// 3. Convert all external edges
			Iterator externalEdgesIterator = document.getDescendants(new AbstractFilter() {
				@Override
				public boolean matches(Object obj) {
					if (obj instanceof Element) {
						Element el = (Element) obj;

						return el.getName().endsWith("ExternalFlexoNodeMessageInEdge")
								|| el.getName().endsWith("ExternalOperatorMessageInEdge")
								|| el.getName().endsWith("ExternalFlexoNodeMessageOutEdge")
								|| el.getName().endsWith("ExternalOperatorMessageOutEdge");

					} else {
						return false;
					}
				}
			});
			while (externalEdgesIterator.hasNext()) {
				Element edge = (Element) externalEdgesIterator.next();
				String suffix = edge.getName().endsWith("InEdge") ? "ExternalMessageInEdge" : "ExternalMessageOutEdge";
				edge.setName(edge.getName().substring(0, edge.getName().lastIndexOf("External")) + suffix);
				count++;
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Converted " + count + " external message edges");
			}

			count = 0;
			// 4. Convert all operator edges
			Iterator operatorEdgesIterator = document.getDescendants(new AbstractFilter() {
				@Override
				public boolean matches(Object obj) {
					if (obj instanceof Element) {
						Element el = (Element) obj;

						return el.getName().endsWith("OperatorInEdge") || el.getName().endsWith("OperatorOutEdge")
								|| el.getName().endsWith("OperatorInterEdge");

					} else {
						return false;
					}
				}
			});
			while (operatorEdgesIterator.hasNext()) {
				Element edge = (Element) operatorEdgesIterator.next();
				edge.setName(edge.getName().substring(0, edge.getName().lastIndexOf("Operator")) + "TokenEdge");
				count++;
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Converted " + count + " operator edges");
			}

			// 5. Convert all pre-conditions wrongly attached
			count = 0;
			Iterator preConditionsIterator = document.getDescendants(new AbstractFilter() {
				@Override
				public boolean matches(Object obj) {
					if (obj instanceof Element) {
						Element el = (Element) obj;

						return el.getName().endsWith("FlexoPreCondition");

					} else {
						return false;
					}
				}
			});
			while (preConditionsIterator.hasNext()) {
				Element pre = (Element) preConditionsIterator.next();
				Iterator attached = pre.getDescendants(new AbstractFilter() {
					@Override
					public boolean matches(Object obj) {
						return obj instanceof Element && ((Element) obj).getName().startsWith("AttachedBegin");
					}
				});
				while (attached.hasNext()) {
					Element attachedBeginNode = (Element) attached.next();
					if (attachedBeginNode.getAttributeValue("idref") != null) {
						Element e = elementsWithID.get(attachedBeginNode.getAttributeValue("idref"));
						if (processMapping.entityWithXMLTag(e.getName()) != processMapping.entityWithXMLTag(attachedBeginNode.getName())) {
							attachedBeginNode.setName(processMapping.entityWithXMLTag(e.getName()).getXmlTags("AttachedBegin")[0]);
							count++;
						}
					} else {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Pre condition is attached to a node that is not serialized somewhere else!");
						}
					}
				}
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Converted " + count + " attached begin nodes");
			}
		}

		private void swapUnalignedPreconditions(Hashtable<String, Element> elementsWithID,
				Hashtable<String, Vector<Element>> elementsWithIDRef) {
			Enumeration<String> idRefs = elementsWithIDRef.keys();
			while (idRefs.hasMoreElements()) {
				String key = idRefs.nextElement();
				Vector<Element> v = elementsWithIDRef.get(key);
				for (Element element : v) {
					if (element.getName().equals("FlexoPreCondition")) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Found unaligned precondition with id " + idRefs);
						}
						// Unaligned precondition (all refs are supposed to start with Next...)
						Element orig = elementsWithID.get(key);
						Element origFather = orig.getParentElement();
						Element elementFather = element.getParentElement();
						origFather.removeContent(orig);
						elementFather.removeContent(element);
						origFather.addContent(element);
						elementFather.addContent(orig);
						element.setName(orig.getName());
						orig.setName("FlexoPreCondition");
					}
				}
			}
		}

		private void handlePreConditionAttachedToEvent(Element pre, Hashtable<String, Element> preAndEdgesWithID,
				Hashtable<String, Vector<Element>> preAndEdgesWithIDRef) {
			Element event = pre.getParentElement();
			Iterator incomingEdgesIterator = pre.getDescendants(new AbstractFilter() {
				@Override
				public boolean matches(Object obj) {
					if (obj instanceof Element) {
						Element el = (Element) obj;

						return el.getName().startsWith("Incoming");
					} else {
						return false;
					}
				}
			});
			Vector<Element> incomingEdges = new Vector<Element>();
			while (incomingEdgesIterator.hasNext()) {
				Element edge = (Element) incomingEdgesIterator.next();
				incomingEdges.add(edge);
			}
			for (Element edge : incomingEdges) {
				Element serializingEdgeElement = null;
				if (edge.getAttributeValue("id") != null) {
					serializingEdgeElement = edge;
				} else {
					serializingEdgeElement = preAndEdgesWithID.get(edge.getAttributeValue("idref"));
					if (serializingEdgeElement == null) {
						if (logger.isLoggable(Level.SEVERE)) {
							logger.severe("Could not find serializing edge element with id " + edge.getAttributeValue("idref"));
						}
						continue;
					}
				}

				boolean success = false;
				// 1. We remove the reference to the precondition
				Element refToPreCondition = serializingEdgeElement.getChild("EndFlexoPreCondition");
				success = serializingEdgeElement.removeContent(refToPreCondition);

				if (!success) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not remove " + refToPreCondition + " from its edge");
					}
				}
				// 2. We move the edge to the event.
				pre.removeContent(edge);
				event.addContent(edge);

				// 3. We add the event as the end of the edge
				Element refToEvent = new Element("End" + event.getName());
				refToEvent.setAttribute("idref", event.getAttributeValue("id"));
				serializingEdgeElement.addContent(refToEvent);

			}
			// We remove the pre condition from its father
			event.removeContent(pre);
		}

		private boolean save() {
			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils.saveXMLFile(document, getResourceFile().getFile());
			hasWrittenOnDisk(lock);
			return returned;
		}
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

	protected class ProcessConverter6 {

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element processElement;

		protected ProcessConverter6() {
			super();
			// roles = new Hashtable();
			// pendingRoles = new Hashtable();
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

		private void convert() throws DuplicateResourceException {
			// TODO
			// pour chaque sous-type de event node :
			// transformer l'Element specifique en un élément "EventNode"
			// positionner correctement l'attribut eventType (Start, Intermediate, End)
			// positionner correctement le trigger (en fonction du sous-type)
			// attention au mailOut, Timer et TimeOut qui avaient des attributs spécifiques a conserver !

			Iterator it = document.getDescendants(new ElementNameFilter("DefaultStartEvent"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Start");
				el.setAttribute("trigger", TriggerType.NONE.name());
			}
			it = document.getDescendants(new ElementNameFilter("DefaultEndEvent"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "End");
				el.setAttribute("trigger", TriggerType.NONE.name());
			}
			it = document.getDescendants(new ElementNameFilter("Timer"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Start");
				el.setAttribute("trigger", TriggerType.TIMER.name());
			}
			it = document.getDescendants(new ElementNameFilter("TimeOut"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Intermediate");
				el.setAttribute("trigger", TriggerType.TIMER.name());
			}
			it = document.getDescendants(new ElementNameFilter("MailIn"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Intermediate");
				el.setAttribute("trigger", TriggerType.MESSAGE.name());
			}
			it = document.getDescendants(new ElementNameFilter("MailOut"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Intermediate");
				el.setAttribute("isCatching", "false");
				el.setAttribute("trigger", TriggerType.MESSAGE.name());
			}
			it = document.getDescendants(new ElementNameFilter("FaultHandler"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Intermediate");
				el.setAttribute("trigger", TriggerType.ERROR.name());
			}
			it = document.getDescendants(new ElementNameFilter("FaultThrower"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "End");
				el.setAttribute("trigger", TriggerType.ERROR.name());
			}
			it = document.getDescendants(new ElementNameFilter("CancelThrower"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "End");
				el.setAttribute("trigger", TriggerType.CANCEL.name());
			}
			it = document.getDescendants(new ElementNameFilter("CancelHandler"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Intermediate");
				el.setAttribute("trigger", TriggerType.CANCEL.name());
			}
			it = document.getDescendants(new ElementNameFilter("CheckPoint"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Intermediate");
				el.setAttribute("trigger", TriggerType.NONE.name());
			}
			it = document.getDescendants(new ElementNameFilter("Revert"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Intermediate");
				el.setAttribute("trigger", TriggerType.NONE.name());
			}
			it = document.getDescendants(new ElementNameFilter("CompensateThrower"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "End");
				el.setAttribute("trigger", TriggerType.COMPENSATION.name());
			}
			it = document.getDescendants(new ElementNameFilter("CompensateHandler"));
			while (it.hasNext()) {
				Element el = (Element) it.next();
				el.setName("EventNode");
				el.setAttribute("eventType", "Intermediate");
				el.setAttribute("trigger", TriggerType.COMPENSATION.name());
			}
		}

		private class ElementNameFilter extends AbstractFilter {
			private final String elname;

			public ElementNameFilter(String elementName) {
				super();
				elname = elementName;
			}

			@Override
			public boolean matches(Object obj) {
				if (obj instanceof Element) {
					return ((Element) obj).getName().equals(elname);
				} else {
					return false;
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

}
