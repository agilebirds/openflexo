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
package org.openflexo.foundation.ie.cl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.cl.action.AddComponentFolder;
import org.openflexo.foundation.ie.cl.dm.ComponentFolderDeleted;
import org.openflexo.foundation.ie.cl.dm.ComponentFolderInserted;
import org.openflexo.foundation.ie.dm.ComponentInserted;
import org.openflexo.foundation.ie.dm.ComponentRemoved;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.foundation.xml.XMLUtils;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * @author bmangez <B>Class Description</B>
 */
public class FlexoComponentFolder extends IECLObject implements
		MutableTreeNode, InspectableObject, Sortable {

	public static final FolderComparator COMPARATOR = new FolderComparator();

	protected static final Logger logger = Logger
			.getLogger(FlexoComponentFolder.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	private String _name;

	// private transient FlexoComponentLibrary _componentLibrary;
	private Vector<FlexoComponentFolder> _subFolders;

	private Vector<ComponentDefinition> _components;

	private FlexoComponentFolder _fatherFolder;

	private String componentPrefix;

	private FolderType folderType;

	private boolean _isSelectedForGeneration = true;

	private String generationRelativePath;

	private int index = -1;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public FlexoComponentFolder(FlexoComponentLibrary componentLibrary) {
		super(componentLibrary);
		_subFolders = new Vector<FlexoComponentFolder>();
		_components = new Vector<ComponentDefinition>();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		if (getComponents().size() > 0
				&& !getComponents().firstElement().isIndexed()) {
			ComponentDefinition[] cd = new ComponentDefinition[getComponents()
					.size()];
			cd = getComponents().toArray(cd);
			Arrays.sort(cd, ComponentDefinition.COMPARATOR);
			for (int i = 0; i < cd.length; i++) {
				cd[i].setIndexValue(i + 1);
			}
		}
		if (getSubFolders().size() > 0
				&& !getSubFolders().firstElement().isIndexed()) {
			FlexoComponentFolder[] folder = new FlexoComponentFolder[getSubFolders()
					.size()];
			folder = getSubFolders().toArray(folder);
			Arrays.sort(folder, FlexoComponentFolder.COMPARATOR);
			for (int i = 0; i < folder.length; i++) {
				folder[i].setIndexValue(i + 1);
			}
		}
	}

	@Override
    protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super
				.getSpecificActionListForThatClass();
		returned.add(AddComponent.actionType);
		returned.add(AddComponentFolder.actionType);
		return returned;
	}

	private static Vector<FlexoComponentFolder> getAllSubFoldersForFolder(
			FlexoComponentFolder folder) {
		Vector<FlexoComponentFolder> v = new Vector<FlexoComponentFolder>();
		if (folder != null) {
			v.add(folder);
			Enumeration en = folder.getSubFolders().elements();
			while (en.hasMoreElements()) {
				FlexoComponentFolder f = (FlexoComponentFolder) en
						.nextElement();
				v.addAll(getAllSubFoldersForFolder(f));
			}
		}
		return v;
	}

	/**
	 * Creates a new FlexoComponentFolder with default values (public API
	 * outside XML serialization)
	 *
	 * @param workflow
	 * @throws DuplicateResourceException
	 */
	public FlexoComponentFolder(String folderName,
			FlexoComponentLibrary componentLibrary) {
		this(componentLibrary);
		_name = folderName;
		generationRelativePath = "src/main/java/" + folderName;
		// setComponentLibrary(lib);
	}

	public FlexoComponentFolder(FlexoComponentLibraryBuilder builder) {
		this(builder.componentLibrary);
		initializeDeserialization(builder);
		// setComponentLibrary(builder.componentLibrary);
		// builder.currentFolder = this;
	}

	/**
	 * Creates and returns a newly created root process
	 *
	 * @return a newly created workflow
	 */
	public static FlexoComponentFolder createNewRootFolder(
			FlexoComponentLibrary library) {
		if (!library.hasRootFolder()) {
			return createNewFolder(library, null, library.getProject()
					.getProject().getProjectName());

		} else {
			if (logger.isLoggable(Level.WARNING))
				logger
						.warning("Cannot create root folder: a root folder is already declared");
			return null;
		}
	}

	public boolean containsComponents() {
		if (getComponents().size() > 0)
			return true;
		if (getSubFolders().size() > 0) {
			boolean answer = false;
			Enumeration en = getSubFolders().elements();
			while (en.hasMoreElements() && !answer) {
				answer = ((FlexoComponentFolder) en.nextElement())
						.containsComponents();
			}
			return answer;
		}
		return false;
	}

	/**
	 * Creates and returns a newly created folder
	 *
	 * @return a newly created folder
	 * @throws DuplicateResourceException
	 */
	public static FlexoComponentFolder createNewFolder(
			FlexoComponentLibrary library, FlexoComponentFolder parentFolder,
			String folderName) {
		FlexoComponentFolder newFolder = new FlexoComponentFolder(folderName,
				library);
		newFolder.setParent(parentFolder);
		if (parentFolder != null) {
			parentFolder.addToSubFolders(newFolder);
		} else {
			if (logger.isLoggable(Level.INFO))
				logger.info("NEW ROOT FOLDER");
			library.setRootFolder(newFolder);
		}
		/*library.notifyObservers(new DataModification(
				DataModification.COMPONENT_FOLDER_ADDED_TO_LIBRARY, null,
				newFolder));
		if (parentFolder != null) {
			parentFolder.setChanged();
			parentFolder.notifyObservers(new ComponentFolderInserted(
					parentFolder, newFolder));
		}*/
		return newFolder;
	}

	public Vector<FlexoComponentFolder> getAllSubFolders() {
		Vector<FlexoComponentFolder> v = new Vector<FlexoComponentFolder>();
		v.addAll(getAllSubFoldersForFolder(this));
		v.remove(this);
		return v;
	}

	public FlexoComponentFolder getFlexoComponentFolderWithName(
			String folderName) {
		for (FlexoComponentFolder folder : getAllSubFolders()) {
			if (folder.getName().equals(folderName)) {
				return folder;
			}
		}
		if (logger.isLoggable(Level.FINE))
			logger.fine("Could not find folder named " + folderName);
		return null;
	}

	public Vector<FlexoModelObject> getAllChilds() {
		Vector<FlexoModelObject> answer = new Vector<FlexoModelObject>();
		answer.addAll(getSubFolders());
		answer.addAll(getComponents());
		return answer;
	}

	/**
	 * Overrides delete
	 * @see org.openflexo.foundation.FlexoModelObject#delete()
	 */
	@Override
	public void delete()
	{
	    getFatherFolder().removeFromSubFolders(this);
	    super.delete();
	    deleteObservers();
	}

	public boolean delete(ComponentDefinition def) {
		if (getComponents().contains(def)) {
			removeFromComponents(def);
			//getComponentLibrary().notifyTreeStructureChanged();
			return true;
		} else {

			Enumeration en = getSubFolders().elements();
			while (en.hasMoreElements()) {
				boolean isDeleted = ((FlexoComponentFolder) en.nextElement())
						.delete(def);
				if (isDeleted)
					return true;
			}
			return false;
		}
	}

	public boolean isValidForANewComponentName(String value) {
		if (value == null)
			return false;
		return getComponentNamed(value) == null;
	}

	public ComponentDefinition getComponentNamed(String value) {
		if (value == null)
			return null;

		String searchedName = value;

		// Petite bidouille pour que la notation avec les thumbnails marche
		// quand meme !
		// Ben, il faudrait que tu solutionnes le pb a la source !
		if (value.lastIndexOf("#") > -1) {
			if (logger.isLoggable(Level.WARNING))
				logger
						.warning("Tab notation with '#' is deprecated and should be replaced by tab_name only ! See Ben to do it !");
			StringTokenizer st = new StringTokenizer(value, "#");
			String newValue = null;
			while (st.hasMoreTokens()) {
				newValue = st.nextToken();
			}
			searchedName = newValue;
		}

		Enumeration en = getComponents().elements();
		ComponentDefinition temp = null;
		while (en.hasMoreElements()) {
			temp = (ComponentDefinition) en.nextElement();
			if (searchedName.toLowerCase().equals(temp.getName().toLowerCase())) {
				return temp;
			}
		}
		ComponentDefinition cur = null;
		en = getSubFolders().elements();
		while (en.hasMoreElements() && cur == null) {
			cur = ((FlexoComponentFolder) en.nextElement())
					.getComponentNamed(searchedName);
		}

		if (cur != null
				&& cur.getComponentName().toLowerCase().equals(
						searchedName.toLowerCase()))
			return cur;
		return null;
	}

	/**
	 * Returns reference to the main object in which this XML-serializable
	 * object is contained relating to storing scheme: here it's the component
	 * library
	 *
	 * @return the component library object
	 */
	@Override
    public XMLStorageResourceData getXMLResourceData() {
		return getComponentLibrary();
	}

	public Vector<OperationComponentDefinition> getOperationsComponentList() {
		Vector<OperationComponentDefinition> answer = new Vector<OperationComponentDefinition>();
		Enumeration en = getComponents().elements();
		ComponentDefinition cur = null;
		while (en.hasMoreElements()) {
			cur = (ComponentDefinition) en.nextElement();
			if (cur instanceof OperationComponentDefinition)
				answer.add((OperationComponentDefinition) cur);
		}
		en = getSubFolders().elements();
		while (en.hasMoreElements()) {
			answer.addAll(((FlexoComponentFolder) en.nextElement())
					.getOperationsComponentList());
		}
		return answer;
	}

	public Vector<TabComponentDefinition> getTabComponentList() {
		Vector<TabComponentDefinition> answer = new Vector<TabComponentDefinition>();
		Enumeration en = getComponents().elements();
		ComponentDefinition cur = null;
		while (en.hasMoreElements()) {
			cur = (ComponentDefinition) en.nextElement();
			if (cur instanceof TabComponentDefinition)
				answer.add((TabComponentDefinition) cur);
		}
		en = getSubFolders().elements();
		while (en.hasMoreElements()) {
			answer.addAll(((FlexoComponentFolder) en.nextElement())
					.getTabComponentList());
		}
		return answer;
	}

	public Vector<PopupComponentDefinition> getPopupsComponentList() {
		Vector<PopupComponentDefinition> answer = new Vector<PopupComponentDefinition>();
		Enumeration en = getComponents().elements();
		ComponentDefinition cur = null;
		while (en.hasMoreElements()) {
			cur = (ComponentDefinition) en.nextElement();
			if (cur instanceof PopupComponentDefinition)
				answer.add((PopupComponentDefinition) cur);
		}
		en = getSubFolders().elements();
		while (en.hasMoreElements()) {
			answer.addAll(((FlexoComponentFolder) en.nextElement())
					.getPopupsComponentList());
		}
		return answer;
	}

	@Override
    public FlexoProject getProject() {
		return getComponentLibrary().getProject();
	}

	public Vector<ComponentDefinition> getComponents() {
		return _components;
	}

	public void setComponents(Vector<ComponentDefinition> value) {
		_components = value;
	}

	public Vector<ComponentDefinition> getAllComponents() {
		Vector<ComponentDefinition> v = new Vector<ComponentDefinition>();
		Enumeration<FlexoComponentFolder> en = getSortedSubFolders();
		while (en.hasMoreElements()) {
			FlexoComponentFolder f = en.nextElement();
			v.addAll(f.getAllComponents());
		}
		Enumeration<ComponentDefinition> en1 = getSortedComponents();
		while(en1.hasMoreElements())
			v.add(en1.nextElement());
		return v;
	}

	public Enumeration<ComponentDefinition> getSortedComponents() {
		disableObserving();
		ComponentDefinition[] o = FlexoIndexManager.sortArray(getComponents().toArray(new ComponentDefinition[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public void addToComponents(ComponentDefinition cd) {
		if ((cd.getFolder() != null) && (cd.getFolder() != this)) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("UNEXPECTEDELY Move component " + cd
						+ " from folder " + cd.getFolder().getName()
						+ " to folder " + getName());
			cd.getFolder().removeFromComponents(cd);
		}
		_components.add(cd);
		cd.setFolder(this);
		if (!isDeserializing()) {
			int i = 0;
			for (Enumeration<ComponentDefinition> en = getSortedComponents(); en.hasMoreElements(); i++) {
				ComponentDefinition cd1 = en.nextElement();
				if (ComponentDefinition.COMPARATOR.compare(cd1, cd) > 0) {
					cd.setIndex(i+1);
					break;
				}
			}
			FlexoIndexManager.reIndexObjectOfArray(getComponents().toArray(new ComponentDefinition[0]));
		}
		setChanged();
		notifyObservers(new ComponentInserted(cd, this));
	}

	public void removeFromComponents(ComponentDefinition sub)
	{
		_components.remove(sub);
		sub.setFolder(null);
		FlexoIndexManager.reIndexObjectOfArray(getComponents().toArray(new ComponentDefinition[0]));
		setChanged();
		notifyObservers(new ComponentRemoved(sub, this));
	}

	public Enumeration<FlexoComponentFolder> getSortedSubFolders()
	{
		disableObserving();
		FlexoComponentFolder[] o = FlexoIndexManager.sortArray(getSubFolders().toArray(new FlexoComponentFolder[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public Vector<FlexoComponentFolder> getSubFolders() {
		return _subFolders;
	}

	public void setSubFolders(Vector<FlexoComponentFolder> value) {
		_subFolders = value;
		setChanged();
	}

	public void addToSubFolders(FlexoComponentFolder sub) {
		if (!_subFolders.contains(sub)) {
			_subFolders.add(sub);
			sub.setFatherFolder(this);
			if (!isDeserializing()) {
				if (getSubFolders().size() > 0) {
					int i = 0;
					for (Enumeration<FlexoComponentFolder> en = getSortedSubFolders(); en.hasMoreElements()&& i<getSubFolders().size(); i++ ) {
						FlexoComponentFolder f = getSubFolders().get(i);
						if (FlexoComponentFolder.COMPARATOR.compare(f, sub) > 0) {
							sub.setIndex(i+1);
							break;
						}
					}
                    // We don't care if no index has been set, it will then keep its current one which is the last one.
				} else
					sub.setIndex(1);
				FlexoIndexManager.reIndexObjectOfArray(getSubFolders()
						.toArray(new FlexoComponentFolder[0]));
			}
			sub.setComponentLibrary(getComponentLibrary());
			setChanged();
			notifyObservers(new ComponentFolderInserted(sub));
		}
	}

	public void removeFromSubFolders(FlexoComponentFolder sub) {
		_subFolders.remove(sub);
		FlexoIndexManager.reIndexObjectOfArray(getSubFolders().toArray(new FlexoComponentFolder[0]));
		setChanged();
		notifyObservers(new ComponentFolderDeleted(sub));
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String name) throws DuplicateFolderNameException,
			InvalidNameException {
		if (getFatherFolder() != null
				&& getFatherFolder().getFolderNamed(name) != null)
			throw new DuplicateFolderNameException(this, name);
		if (!isDeserializing()
				&& !name.matches(FileUtils.GOOD_CHARACTERS_REG_EXP + "+")) {
			throw new InvalidNameException(name);
		}
		String old = _name;
		_name = name;
		setChanged();
		notifyObservers(new IEDataModification("name", old, name));
	}

	@Override
	public void insert(MutableTreeNode arg0, int arg1) {
		if (arg0 instanceof FlexoComponentFolder) {
			addToSubFolders((FlexoComponentFolder) arg0);
		}

	}

	@Override
	public void remove(int arg0) {
		if (arg0 < _subFolders.size()) {
			removeFromSubFolders(_subFolders.get(arg0));
		} else {
			removeFromComponents(_components.get(arg0 - _subFolders.size()));
		}
	}

	@Override
	public void remove(MutableTreeNode arg0) {
		if (arg0 instanceof FlexoComponentFolder) {
			removeFromSubFolders((FlexoComponentFolder) arg0);
		} else {
			removeFromComponents((ComponentDefinition) arg0);
		}
	}

	@Override
	public void removeFromParent() {
		_fatherFolder.remove(this);
	}

	@Override
	public void setParent(MutableTreeNode arg0) {
		if (arg0 != null) {
			setComponentLibrary(((FlexoComponentFolder) arg0)
					.getComponentLibrary());
		}
		_fatherFolder = (FlexoComponentFolder) arg0;
		if (!isDeserializing())
			if (_fatherFolder == null)
				this.index = -1;
			else
				this.index = _fatherFolder.getSubFolders().size();

	}

	public FlexoComponentFolder getFatherFolder() {
		return _fatherFolder;
	}

	public void setFatherFolder(FlexoComponentFolder folder) {
		setParent(folder);
	}

	@Override
	public void setUserObject(Object arg0) {
		// to implement TreeNode interface

	}

	@Override
	public Enumeration children() {
		Vector v = new Vector();
		v.addAll(getSubFolders());
		v.addAll(getComponents());
		return v.elements();
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int arg0) {
		if (arg0 < _subFolders.size()) {
			return _subFolders.get(arg0);
		} else {
			return (TreeNode) _components.get(arg0 - _subFolders.size());
		}
	}

	@Override
	public int getChildCount() {
		return _subFolders.size() + _components.size();
	}

	@Override
	public int getIndex(TreeNode arg0) {
		return getFatherFolder().getSubFolders().indexOf(this);
	}

	@Override
	public FlexoComponentFolder getParent() {
		return getFatherFolder();
	}

	@Override
	public boolean isLeaf() {
		return _subFolders.size() == 0 && _components.size() == 0;
	}

	public String getComponentPrefix() {
		if (componentPrefix==null){
			if (getProject().getProjectName().length() > 2)
				componentPrefix = getProject().getProjectName().substring(0, 3).toUpperCase();
	        else
	        	componentPrefix = getProject().getProjectName().toUpperCase();
		}
		return componentPrefix;
	}

	public void setComponentPrefix(String componentPrefix) {
		this.componentPrefix = componentPrefix;
		setChanged();
	}

	public void convertAllComponent() {
		Enumeration en = getComponents().elements();
		while (en.hasMoreElements()) {
			convertComponent((ComponentDefinition) en.nextElement());
		}
		en = getSubFolders().elements();
		while (en.hasMoreElements()) {
			((FlexoComponentFolder) en.nextElement()).convertAllComponent();
		}
	}

	public static void convertComponent(ComponentDefinition def) {
		ComponentConverter2 converter = new ComponentConverter2(def
				.getComponentResource());
		if (converter.conversionWasSucessfull) {
			System.out.println("SUCCES IN converting " + def.getComponentName()
					+ " in resource :"
					+ def.getComponentResource().getResourceIdentifier());

		} else {
			System.out.println("FAILURE IN converting "
					+ def.getComponentName() + " in resource :"
					+ def.getComponentResource().getResourceIdentifier());
		}
	}

	public static void convertComponent(FlexoResource res) {
		if (res instanceof FlexoFileResource) {
			System.out.println("converting  resource :"
					+ res.getResourceIdentifier());
			ComponentConverter2 converter = new ComponentConverter2(
					(FlexoFileResource) res);
			if (converter.conversionWasSucessfull) {
				System.out.println("SUCCES IN converting  resource :"
						+ res.getResourceIdentifier());

			} else {
				System.out.println("FAILURE IN converting resource :"
						+ res.getResourceIdentifier());
			}
		} else {
			System.out.println("FAILURE IN converting resource :"
					+ res.getResourceIdentifier() + " NOT A FILE RESOURCE");

		}
	}

	protected static class ComponentConverter2 {

		protected boolean conversionWasSucessfull = false;

		protected Document document;

		protected Element rootElement;

		protected FlexoFileResource res;

		protected ComponentConverter2(FlexoFileResource _res) {
			super();
			res = _res;
			try {
				document = XMLUtils.getJDOMDocument(res.getFile());
				convert();
				conversionWasSucessfull = save();

			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: "
							+ e.getClass().getName()
							+ ". See console for details.");
				e.printStackTrace();
			}
		}

		private Element createComponentInstanceElement(String prefix,
				String aName) {
			Element answer = new Element(prefix + "ComponentInstance");
			answer.setAttribute("componentName", aName);
			return answer;
		}

		private void convert() {
			Iterator tableElementIterator = document
					.getDescendants(new ElementFilter("IEButton"));
			Vector temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			System.out.println("convert " + temp.size() + " IEButtons");
			Enumeration en = temp.elements();
			while (en.hasMoreElements()) {
				Element nextElement = (Element) en.nextElement();
				if (nextElement.getAttributeValue("popupName") != null) {
					nextElement
							.addContent(createComponentInstanceElement("Popup",
									nextElement.getAttributeValue("popupName")));
					nextElement.removeAttribute("popupName");
				} else if (nextElement.getAttributeValue("pageName") != null) {
					nextElement.addContent(createComponentInstanceElement(
							"Operation", nextElement
									.getAttributeValue("pageName")));
					nextElement.removeAttribute("pageName");
				}
			}

			tableElementIterator = document.getDescendants(new ElementFilter(
					"IEHyperlink"));
			temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			System.out.println("convert " + temp.size() + " IEHyperlink");
			en = temp.elements();
			while (en.hasMoreElements()) {
				Element nextElement = (Element) en.nextElement();
				if (nextElement.getAttributeValue("pageName") != null) {
					nextElement.addContent(createComponentInstanceElement(
							"Operation", nextElement
									.getAttributeValue("pageName")));
					nextElement.removeAttribute("pageName");
				} else if (nextElement.getAttributeValue("pageName") != null) {
					nextElement.addContent(createComponentInstanceElement(
							"Operation", nextElement
									.getAttributeValue("pageName")));
					nextElement.removeAttribute("pageName");
				}
			}
			tableElementIterator = document.getDescendants(new ElementFilter(
					"IEThumbnail"));
			temp = new Vector();
			while (tableElementIterator.hasNext()) {
				temp.add(tableElementIterator.next());
			}
			System.out.println("convert " + temp.size() + " Thumbnails");
			en = temp.elements();
			while (en.hasMoreElements()) {
				Element nextElement = (Element) en.nextElement();
				if (nextElement.getAttributeValue("woComponentName") != null) {
					nextElement.addContent(createComponentInstanceElement(
							"Thumbnail", nextElement
									.getAttributeValue("woComponentName")));
					nextElement.removeAttribute("woComponentName");
				}
			}
		}

		private boolean save() {
			return XMLUtils.saveXMLFile(document, res.getFile());
		}
	}

	@Override
	public String getInspectorName() {
		return "Folder.inspector";
	}

	public String getGenerationRelativePath() {
		return this.generationRelativePath;
	}

	public void setGenerationRelativePath(String relPath) {
		this.generationRelativePath = relPath;
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is
	 * NOT a recursive method
	 *
	 * @return a Vector of IEObject instances
	 */
	@Override
    public Vector<IObject> getEmbeddedIEObjects() {
		Vector answer = new Vector();
		answer.addAll(getSubFolders());
		answer.addAll(getComponents());
		return answer;
	}

	@Override
    public String getFullyQualifiedName() {
		return "Folder:" + getName();
	}

	/**
	 * Returns a flag indicating if this object is valid according to default
	 * validation model
	 *
	 * @return boolean
	 */
	@Override
	public boolean isValid() {
		return isValid(getDefaultValidationModel());
	}

	/**
	 * Returns a flag indicating if this object is valid according to specified
	 * validation model
	 *
	 * @return boolean
	 */
	@Override
	public boolean isValid(ValidationModel validationModel) {
		return validationModel.isValid(this);
	}

	/**
	 * Validates this object by building new ValidationReport object Default
	 * validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate() {
		return validate(getDefaultValidationModel());
	}

	/**
	 * Validates this object by building new ValidationReport object Supplied
	 * validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate(ValidationModel validationModel) {
		return validationModel.validate(this);
	}

	/**
	 * Validates this object by appending eventual issues to supplied
	 * ValidationReport. Default validation model is used to perform this
	 * validation.
	 *
	 * @param report,
	 *            a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report) {
		validate(report, getDefaultValidationModel());
	}

	/**
	 * Validates this object by appending eventual issues to supplied
	 * ValidationReport. Supplied validation model is used to perform this
	 * validation.
	 *
	 * @param report,
	 *            a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report,
			ValidationModel validationModel) {
		validationModel.validate(this, report);
	}

	public static class RootFolderMustHaveAPrefix extends ValidationRule {
		public RootFolderMustHaveAPrefix() {
			super(FlexoComponentFolder.class, "root_folder_must_have_prefix");
		}

		@Override
        public ValidationIssue applyValidation(final Validable object) {
			final FlexoComponentFolder folder = (FlexoComponentFolder) object;
			if ((folder.getFatherFolder() == null)
					&& (folder.getComponentPrefix() == null || folder
							.getComponentPrefix().equals(""))) {
				ValidationError error = new ValidationError(this, object,
						"folder_($object.name)_has_no_component_prefix");

				return error;
			}
			return null;
		}
	}

	/**
	 * Search in the direct sub-folders of this folder for a folder named
	 * <code>name</code> (case insensitive).
	 *
	 * @param name -
	 *            the name of the direct sub-folder to find
	 * @return the direct sub-folder named <code>name</code> or null if it
	 *         cannot be found.
	 */
	public FlexoComponentFolder getFolderNamed(String name) {
		name = name.toLowerCase();
		Enumeration<FlexoComponentFolder> en = getSubFolders().elements();
		while (en.hasMoreElements()) {
			FlexoComponentFolder folder = en
					.nextElement();
			if (folder.getName().toLowerCase().equals(name))
				return folder;
		}
		return null;
	}

	public boolean isFatherOf(FlexoComponentFolder folder) {
		FlexoComponentFolder f = folder.getFatherFolder();
		while (f != null) {
			if (f.equals(this)) {
				return true;
			}
			f = f.getFatherFolder();
		}
		return false;
	}

	public static class FolderComparator implements
			Comparator<FlexoComponentFolder> {
		protected FolderComparator() {
		}

		/**
		 * Overrides compare
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(FlexoComponentFolder o1, FlexoComponentFolder o2) {
			return (o1).getName().compareTo(
					(o2).getName());
		}
	}

	public FlexoComponentFolder getFolderTyped(FolderType type) {
		FlexoComponentFolder retval = null;
		Enumeration en = getAllSubFolders().elements();
		while (en.hasMoreElements()) {
			FlexoComponentFolder f = (FlexoComponentFolder) en.nextElement();
			if (f.getFolderType() == type)
				return f;
			else
				retval = f.getFolderTyped(type);
		}
		if (isRootFolder() && retval == null) {
			retval = createNewFolder(getComponentLibrary(), this,
					FlexoLocalization.localizedForKey(type.getName()
							.toLowerCase()));
			retval.setFolderType(type);
		}
		return retval;
	}

	public boolean isRootFolder() {
		return getParent() == null;
	}

	public FolderType getFolderType() {
		return folderType;
	}

	public void setFolderType(FolderType folderType) {
		this.folderType = folderType;
	}

	/**
	 * Overrides getClassNameKey
	 *
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
    public String getClassNameKey() {
		return "component_folder";
	}

	public boolean isIndexed() {
		return this.index > -1;
	}

	@Override
	public int getIndex() {
	    if (isBeingCloned())
            return -1;
		if (index == -1 && getCollection() != null) {
			index = getCollection().length ;
			FlexoIndexManager.reIndexObjectOfArray(getCollection());
		}
		return index;
	}

	@Override
	public void setIndex(int index) {
		if (isDeserializing() || isCreatedByCloning()) {
			setIndexValue(index);
			return;
		}
		FlexoIndexManager.switchIndexForKey(this.index, index, this);
		if (getIndex()!=index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index",null,getIndex());
			dm.setReentrant(true);
			notifyObservers(dm);
		}
	}

	@Override
	public int getIndexValue() {
		return getIndex();
	}

	@Override
	public void setIndexValue(int index) {
        if (this.index==index)
            return;
		int old = this.index;
		this.index = index;
		setChanged();
		notifyModification("index", old, index);
		if (!isDeserializing() && !isCreatedByCloning() && getFatherFolder() != null) {
			getFatherFolder().setChanged();
			getFatherFolder().notifyObservers(new ChildrenOrderChanged());
		}
	}

	/**
	 * Overrides getCollection
	 *
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public FlexoComponentFolder[] getCollection() 
	{
		if (getFatherFolder() == null)
			return new FlexoComponentFolder[]{this};
		return getFatherFolder().getSubFolders().toArray(new FlexoComponentFolder[0]);
	}

}