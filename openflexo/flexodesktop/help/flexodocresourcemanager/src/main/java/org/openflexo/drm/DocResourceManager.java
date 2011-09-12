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
package org.openflexo.drm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.drm.action.ApproveVersion;
import org.openflexo.drm.action.RefuseVersion;
import org.openflexo.drm.action.SubmitVersion;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.xml.sax.SAXException;


public class DocResourceManager {

    private static final Logger logger = Logger.getLogger(DocResourceManager.class.getPackage().getName());

    public static final String DOC_RESOURCE_CENTER = "FlexoDocResourceCenter";
    public static final String FLEXO_MODEL = "FlexoModel";
    public static final String FLEXO_TOOL_SET = "FlexoToolSet";
    public static final String ABSTRACT_MODULE = "general module";
    public static final String ABSTRACT_MAIN_PANE = "general main pane";
    public static final String ABSTRACT_CONTROL_PANEL = "general control panel";
    public static final String ABSTRACT_LEFT_VIEW = "general left view";
    public static final String ABSTRACT_RIGHT_VIEW = "general right view";

    /**
     * Hashtable storing edited DocItemVersion (not yet stored in DocItem), and
     * where keys are DocItem instances
     */
    private Hashtable<DocItem,DocItemVersion> _editedDocItems;
    private Vector<DocItemVersion> _versionsToEventuallySave;
    private DocSubmissionReport _sessionSubmissions;

    private static DocResourceManager _instance = null;

    private DocResourceCenter docResourceCenter;

    public static DocResourceManager instance()
    {
        if (_instance == null) {
            _instance = new DocResourceManager();
        }
        return _instance;
    }

    private DocResourceManager()
    {
        super();
        _editedDocItems = new Hashtable<DocItem,DocItemVersion>();
        _versionsToEventuallySave = new Vector<DocItemVersion>();
        _instance = this;
        load();
    }

   private FlexoEditor _editor;

    private static XMLMapping _drmMapping;

    public static XMLMapping getDRMMapping()
     {
         if (_drmMapping == null) {
             StringEncoder.getDefaultInstance()._addConverter(DocItemVersion.Version.converter);
             File drmModelFile;
             drmModelFile = new FileResource("Models/DRMModel.xml");
             if (!drmModelFile.exists()) {
                 if (logger.isLoggable(Level.WARNING))
                     logger.warning("File " + drmModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
                 return null;
             } else {
                 try {
                     _drmMapping = new XMLMapping(drmModelFile);
                 } catch (InvalidModelException e) {
                     // Warns about the exception
                     if (logger.isLoggable(Level.WARNING))
                         logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                     e.printStackTrace();
                 } catch (IOException e) {
                     // Warns about the exception
                     if (logger.isLoggable(Level.WARNING))
                         logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                     e.printStackTrace();
                 } catch (SAXException e) {
                     // Warns about the exception
                     if (logger.isLoggable(Level.WARNING))
                         logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                     e.printStackTrace();
                 } catch (ParserConfigurationException e) {
                     // Warns about the exception
                     if (logger.isLoggable(Level.WARNING))
                         logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                     e.printStackTrace();
                 }
             }
         }
         return _drmMapping;
     }

    private void load()
    {
        StringEncoder.getDefaultInstance()._addConverter(ActionType.actionTypeConverter);
        try {
            FileInputStream in = new FileInputStream(getDRMFile());
            docResourceCenter = (DocResourceCenter) XMLDecoder.decodeObjectWithMapping(in, getDRMMapping(),new DRMBuilder());
            _sessionSubmissions = new DocSubmissionReport(docResourceCenter);
            in.close();
            docResourceCenter.clearIsModified(true);
            /*
            getAbstractModuleItem();
            getAbstractMainPaneItem();
            getAbstractControlPanelItem();
            getAbstractLeftViewItem();
            getAbstractRightViewItem();*/
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Exception "+e.getMessage()+" occured while loading DocResourceCenter");
            e.printStackTrace();
        }
    }

    public void save()
    {
        isSaving = true;
        docResourceCenter.save();
        for (Enumeration en=_versionsToEventuallySave.elements(); en.hasMoreElements();) {
            DocItemVersion next = (DocItemVersion)en.nextElement();
            if (next.needsSaving()) {
                next.save();
            }
        }
        docResourceCenter.clearIsModified(false);
       isSaving = false;
    }

    public DocSubmissionReport getSessionSubmissions()
    {
        return _sessionSubmissions;
    }

   public boolean needSaving()
    {
	   if (docResourceCenter==null)
		   return false;
        if (docResourceCenter.isModified()) return true;
        for (Enumeration en=_versionsToEventuallySave.elements(); en.hasMoreElements();) {
            DocItemVersion next = (DocItemVersion)en.nextElement();
            if (next.needsSaving()) {
                return true;
            }
        }
        return false;
    }

   public DocResourceCenter getDocResourceCenter()
   {
       return docResourceCenter;
   }

   private File drmFile;
   private boolean isSaving;

   public File getDRMFile()
    {
        if (drmFile == null) {
            drmFile = new FileResource("DocResourceCenter.xml");
            if (drmFile.exists()) {
            	if (logger.isLoggable(Level.INFO))
            		logger.info("Found DRM File : "+drmFile.getAbsolutePath());
            } else {
            	if (logger.isLoggable(Level.INFO))
            		logger.info("DRM File not found: "+drmFile.getAbsolutePath());
            } 
        }
        if ((!drmFile.exists()) && (!isSaving)) {
            drmFile = new File(getDocResourceCenterDirectory(),"DocResourceCenter.xml");
            if (logger.isLoggable(Level.WARNING))
                logger.warning("DocResourceCenter.xml not found. Creates new DocResourceCenter");
            docResourceCenter = DocResourceCenter.createDefaultDocResourceCenter();
            save();
        }
        return drmFile;
    }

   private static File drmDirectory;

   public static File getDocResourceCenterDirectory()
   {
    if (drmDirectory == null) {
        drmDirectory = instance().getDRMFile().getParentFile();
        if (logger.isLoggable(Level.INFO))
            logger.info("Doc Resource Center Directory: "+drmDirectory.getAbsolutePath());
    }
    return drmDirectory;
   }

     public String getXMLRepresentation()
    {
        try {
            return XMLCoder.encodeObjectWithMapping(docResourceCenter, getDRMMapping());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

     public static void main(String[] args)
     {
         instance();
     }

     public DocItem importInspector(InspectorGroup inspectorGroup, String inspectorName, InspectorModel inspectorModel)
    {
        DocItem inspectorDocItem = getDocResourceCenter().getItemNamed(inspectorName);
        DocItemFolder inspectorGroupFolder;
        if (inspectorDocItem == null) {
            DocItemFolder modelFolder = getDocResourceCenter().getModelFolder();
            String inspectorGroupName = inspectorGroup.getName();
            inspectorGroupFolder = modelFolder.getItemFolderNamed(inspectorGroupName);
            if (inspectorGroupFolder == null) {
                inspectorGroupFolder = DocItemFolder.createDocItemFolder(inspectorGroupName,"No description",modelFolder,getDocResourceCenter());
            }
            if (inspectorGroupFolder.getItemNamed(inspectorName) == null) {
                logger.fine("Add entry for "+inspectorName+" in documentation !");
                inspectorDocItem = DocItem.createDocItem(inspectorName,"No description",inspectorGroupFolder,getDocResourceCenter(),false);
                InspectorModel parentInspector = inspectorModel.getSuperInspector();
                if (parentInspector != null) {
                    String superInspectorName = inspectorModel.superInspectorName;
                    superInspectorName = superInspectorName.substring(0,superInspectorName.lastIndexOf(".inspector"));
                    DocItem parentItem = getDocItemForInspector(superInspectorName);
                    if (parentItem == null) {
                        parentItem = importInspector(inspectorGroup,superInspectorName,parentInspector);
                    }
                    parentItem.addToInheritanceChildItems(inspectorDocItem);
                }
            }
        }
        else {
            logger.fine("Found entry for "+inspectorName+" in documentation !");
           inspectorGroupFolder = inspectorDocItem.getFolder();
        }
        if (inspectorDocItem != null) {
            for (Enumeration en=inspectorModel.getTabs().elements(); en.hasMoreElements();) {
                TabModel tabModel = (TabModel)en.nextElement();
                for (Enumeration en2=tabModel.getProperties().elements(); en2.hasMoreElements();) {
                    PropertyModel propertyModel = (PropertyModel)en2.nextElement();
                    String propName = inspectorName+"-"+propertyModel.name;
                    DocItem propertyDocItem = inspectorGroupFolder.getItemNamed(propName);
                    if (propertyDocItem == null) {
                        logger.fine("Add entry for "+propName+" in documentation !");
                        propertyDocItem = DocItem.createDocItem(propName,"No description",inspectorGroupFolder,getDocResourceCenter(),true);
                        inspectorDocItem.addToEmbeddingChildItems(propertyDocItem);
                    }
                    else {
                        logger.fine("Found entry for "+propName+" in documentation !");
                    }
                }
            }
        }
        return inspectorDocItem;
     }

    protected DocItem getDocItemForInspector(String inspectorName)
    {
        return getDocResourceCenter().getItemNamed(inspectorName);
    }

    public boolean isEdited (DocItem docItem)
    {
        return (getEditedVersion(docItem) != null);
    }

    public boolean isSubmitting (DocItem docItem)
    {
        return ((getEditedVersion(docItem) != null)
        && (!docItem.getVersions().contains(getEditedVersion(docItem))));
    }

    public DocItemVersion getEditedVersion (DocItem docItem)
    {
        return _editedDocItems.get(docItem);
    }

     public void beginVersionSubmission (DocItem docItem, Language language)
     {
         DocItemVersion version = DocItemVersion.createVersion(docItem,new DocItemVersion.Version(),language,"","",getDocResourceCenter());
         _editedDocItems.put(docItem,version);
         _versionsToEventuallySave.add(version);
     }

     public DocItemAction endVersionSubmission (DocItem docItem)
     {
     	logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
    	// TODO: Please implement this better later
       	// Used editor will be null
         SubmitVersion action = (SubmitVersion)SubmitVersion.actionType.makeNewAction(docItem,null);
         action.setAuthor(getUser());
         action.setVersion(getEditedVersion(docItem));
         action.doAction();
         _editedDocItems.remove(docItem);
         return action.getNewAction();
     }

     public DocItemVersion beginVersionReview (DocItemVersion docItemVersion)
     {
         DocItemVersion.Version lastVersionId = docItemVersion.getVersion();
         DocItemVersion.Version newVersionId = DocItemVersion.Version.versionByIncrementing(lastVersionId,0,0,1);
         DocItemVersion version = DocItemVersion.createVersion(docItemVersion.getItem(),newVersionId,docItemVersion.getLanguage(),"","",getDocResourceCenter());
         version.setShortHTMLDescription(docItemVersion.getShortHTMLDescription());
         version.setFullHTMLDescription(docItemVersion.getFullHTMLDescription());
         _editedDocItems.put(docItemVersion.getItem(),version);
         _versionsToEventuallySave.add(version);
         return version;
     }

     public DocItemAction endVersionReview (DocItem docItem)
     {
     	logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
    	// TODO: Please implement this better later
       	// Used editor will be null
         SubmitVersion action = (SubmitVersion)SubmitVersion.actionType.makeNewAction(docItem,null);
         action.setAuthor(getUser());
         action.setVersion(getEditedVersion(docItem));
         action.doAction();
         _editedDocItems.remove(docItem);
         return action.getNewAction();

         /*DocItemAction returned = docItem.reviewVersion(getEditedVersion(docItem),getUser(),getDocResourceCenter());
         _editedDocItems.remove(docItem);
         return returned;*/
     }

    public DocItemAction approveVersion(DocItemVersion version)
    {
    	logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
    	// TODO: Please implement this better later
       	// Used editor will be null
        ApproveVersion action = (ApproveVersion)ApproveVersion.actionType.makeNewAction(version.getItem(),null);
        action.setAuthor(getUser());
        action.setVersion(version);
        action.doAction();
        return action.getNewAction();

        //return version.getItem().approveVersion(version,getUser(),getDocResourceCenter());
    }

    public DocItemAction refuseVersion(DocItemVersion version)
    {
    	logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
    	// TODO: Please implement this better later
       	// Used editor will be null
        RefuseVersion action = (RefuseVersion)RefuseVersion.actionType.makeNewAction(version.getItem(),null);
        action.setAuthor(getUser());
        action.setVersion(version);
        action.doAction();
        return action.getNewAction();

        //return version.getItem().refuseVersion(version,getUser(),getDocResourceCenter());
    }

    public Author getUser()
    {
        return getDocResourceCenter().getUser();
    }

    public Language getLanguage(org.openflexo.localization.Language language)
    {
        return getDocResourceCenter().getLanguageNamed(language.getName());
    }

    public void editVersion(DocItemVersion version)
    {
       _editedDocItems.put(version.getItem(),version);
        _versionsToEventuallySave.add(version);
    }

    public void stopEditVersion(DocItemVersion version)
    {
       _editedDocItems.remove(version.getItem());
    }

    // ================================================
    // ============ Access to documentation ===========
    // ================================================

    public DocItem getDocResourceCenterItem()
    {
        return getDocResourceCenter().getItemNamed(DOC_RESOURCE_CENTER);
    }

    public DocItem getFlexoModelItem()
    {
        return getDocResourceCenter().getItemNamed(FLEXO_MODEL);
    }

    public DocItem getFlexoToolSetItem()
    {
        return getDocResourceCenter().getItemNamed(FLEXO_TOOL_SET);
    }

    public DocItem getAbstractModuleItem()
    {
        DocItemFolder f = getDocResourceCenter().getFTSFolder().getItemFolderNamed(ABSTRACT_MODULE);
        if (f==null) {
            f=DocItemFolder.createDocItemFolder(DocResourceManager.ABSTRACT_MODULE,"Description of what is a module",getDocResourceCenter().getFTSFolder(),getDocResourceCenter());
        }
        DocItem it = getDocResourceCenter().getItemNamed(ABSTRACT_MODULE);
        if (it==null) {
            f.createDefaultPrimaryDocItem();
        }
        return it;
    }

    public DocItem getAbstractMainPaneItem()
    {
        DocItem item = getDocResourceCenter().getItemNamed(ABSTRACT_MAIN_PANE);
        if (item==null) {
            item = DocItem.createDocItem(DocResourceManager.ABSTRACT_MAIN_PANE,"Description of what is the main pane",getAbstractModuleItem().getFolder(),getDocResourceCenter(),false);
            getAbstractModuleItem().addToEmbeddingChildItems(item);
        }
        return item;
    }

    public DocItem getAbstractControlPanelItem()
    {
        DocItem item = getDocResourceCenter().getItemNamed(ABSTRACT_CONTROL_PANEL);
        if (item==null) {
            item = DocItem.createDocItem(DocResourceManager.ABSTRACT_CONTROL_PANEL,"Description of what is the control panel",getAbstractModuleItem().getFolder(),getDocResourceCenter(),false);
            getAbstractModuleItem().addToEmbeddingChildItems(item);
        }
        return item;
    }

    public DocItem getAbstractLeftViewItem()
    {
        DocItem item = getDocResourceCenter().getItemNamed(ABSTRACT_LEFT_VIEW);
        if (item==null) {
            item = DocItem.createDocItem(DocResourceManager.ABSTRACT_LEFT_VIEW,"Description of what is the left view",getAbstractModuleItem().getFolder(),getDocResourceCenter(),false);
            getAbstractModuleItem().addToEmbeddingChildItems(item);
        }
        return item;
    }

    public DocItem getAbstractRightViewItem()
    {
        DocItem item = getDocResourceCenter().getItemNamed(ABSTRACT_RIGHT_VIEW);
        if (item==null) {
            item = DocItem.createDocItem(DocResourceManager.ABSTRACT_RIGHT_VIEW,"Description of what is the right view",getAbstractModuleItem().getFolder(),getDocResourceCenter(),false);
            getAbstractModuleItem().addToEmbeddingChildItems(item);
        }
        return item;
    }

    public DocItem getDocItemFor (InspectableObject inspectableObject)
    {
        String inspectorName = inspectableObject.getInspectorName();
        if (inspectorName == null) return null;
        if (inspectorName.equals(Inspectors.IE.POPUP_COMPONENT_DEFINITION_INSPECTOR)) inspectorName = Inspectors.IE.POPUP_COMPONENT_INSPECTOR;
        if (inspectorName.equals(Inspectors.IE.OPERATION_COMPONENT_DEFINITION_INSPECTOR)) inspectorName = Inspectors.IE.OPERATION_COMPONENT_INSPECTOR;
        if (inspectorName.equals(Inspectors.IE.TAB_COMPONENT_DEFINITION_INSPECTOR)) inspectorName = Inspectors.IE.TAB_COMPONENT_INSPECTOR;
        String itemIdentifier = inspectorName.substring(0,inspectorName.lastIndexOf(".inspector"));
        return getDocResourceCenter().getItemNamed(itemIdentifier);
    }

    public DocItem getDocItemFor (InspectorModel inspectorModel)
    {
        String itemIdentifier = inspectorModel.inspectorName;
         return getDocResourceCenter().getItemNamed(itemIdentifier);
    }

    public DocItem getDocItemFor (PropertyModel propertyModel)
    {
        InspectorModel inspectorModel = propertyModel.getInspectorModel();

        InspectorModel currentInspectorModel = propertyModel.getInspectorModel();
        DocItem returned = null;

        while (returned == null && currentInspectorModel != null) {
            String itemIdentifier = currentInspectorModel.inspectorName+"-"+propertyModel.name;
            returned = getDocResourceCenter().getItemNamed(itemIdentifier);
            currentInspectorModel = currentInspectorModel.getSuperInspector();
        }

        return returned;

       /* String itemIdentifier = inspectorModel.inspectorName+"-"+propertyModel.name;
        return getDocResourceCenter().getItemNamed(itemIdentifier);*/
   }

    public DocItem getDocItemWithId (String itemIdentifier)
    {
        return getDocResourceCenter().getItemNamed(itemIdentifier);
   }

    public static DocItem getDocItem (String itemIdentifier)
    {
        return instance().getDocItemWithId(itemIdentifier);
   }

    // ================================================
    // ============ Import facilities ===========
    // ================================================


    public void importDocSubmissionReport(DocSubmissionReport docSubmissionReport, Vector actionsToImport)
    {
        Vector actions;
        if (actionsToImport == null) {
            actions = docSubmissionReport.getSubmissionActions();
        }
        else {
            actions = actionsToImport;
        }
        for (Enumeration en=actions.elements(); en.hasMoreElements();) {
            DocItemAction action = (DocItemAction)en.nextElement();
            importDocSubmissionAction(action);
        }
    }

    private void importDocSubmissionAction(DocItemAction action)
    {
        logger.info("Import action "+action.getLocalizedName());
        DocItem parsedItem = action.getItem();
        DocItem existingItem = getDocResourceCenter().getItemNamed(parsedItem.getIdentifier());
        if (existingItem != null) {
            DocItemVersion.Version newVersion = action.getVersion().getVersion();
            while (existingItem.getVersion(newVersion) != null) {
                logger.info("Version "+newVersion+" already exist");
                newVersion = DocItemVersion.Version.versionByIncrementing(newVersion,0,0,1);
                logger.info("Using version "+newVersion);
                action.getVersion().setVersion(newVersion);
           }
            for (Enumeration en=getDocResourceCenter().getLanguages().elements(); en.hasMoreElements();) {
                Language lang = (Language)en.nextElement();
                if (parsedItem.getTitle(lang) != null) {
                    existingItem.setTitle(parsedItem.getTitle(lang),lang);
                }
            }
            action.setItem(existingItem);
            action.getVersion().setItem(existingItem);
            existingItem.addToActions(action);
            existingItem.addToVersions(action.getVersion());
            _versionsToEventuallySave.add(action.getVersion());
            action.getVersion().setNeedsSaving();
        }
        else {
            logger.warning("Unable to import action: item is not locally registered ("+parsedItem.getIdentifier()+"). Please implement this feature.");
        }
    }

	public FlexoEditor getEditor()
	{
		if (_editor == null) _editor = new DefaultFlexoEditor();
		return _editor;
	}

	public void setEditor(FlexoEditor editor)
	{
		_editor = editor;
	}

    // ================================================
    // ============ Validation management =============
    // ================================================

    private DRMValidationModel _drmValidationModel;

    public ValidationModel getDRMValidationModel()
    {
        if (_drmValidationModel == null) {
            _drmValidationModel = new DRMValidationModel();
        }
        return _drmValidationModel;
    }


}
