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
package org.openflexo.foundation.view;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.dm.OEDataModification;
import org.openflexo.foundation.ontology.dm.ShemaDeleted;
import org.openflexo.foundation.ontology.dm.ShemaFolderDeleted;
import org.openflexo.foundation.ontology.dm.ShemaFolderInserted;
import org.openflexo.foundation.ontology.dm.ShemaInserted;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.xml.VEShemaLibraryBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * @author bmangez <B>Class Description</B>
 */
public class ViewFolder extends ViewLibraryObject implements InspectableObject, Sortable {

	public static final FolderComparator COMPARATOR = new FolderComparator();

	protected static final Logger logger = Logger.getLogger(ViewFolder.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	private String _name;

	// private transient FlexoComponentLibrary _componentLibrary;
	private Vector<ViewFolder> _subFolders;

	private Vector<ViewDefinition> _views;

	private ViewFolder _fatherFolder;

	private int index = -1;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public ViewFolder(ViewLibrary viewLibrary) {
		super(viewLibrary);
		_subFolders = new Vector<ViewFolder>();
		_views = new Vector<ViewDefinition>();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		if (getShemas().size() > 0 && !getShemas().firstElement().isIndexed()) {
			ViewDefinition[] cd = new ViewDefinition[getShemas().size()];
			cd = getShemas().toArray(cd);
			Arrays.sort(cd, ViewDefinition.COMPARATOR);
			for (int i = 0; i < cd.length; i++) {
				cd[i].setIndexValue(i + 1);
			}
		}
		if (getSubFolders().size() > 0 && !getSubFolders().firstElement().isIndexed()) {
			ViewFolder[] folder = new ViewFolder[getSubFolders().size()];
			folder = getSubFolders().toArray(folder);
			Arrays.sort(folder, ViewFolder.COMPARATOR);
			for (int i = 0; i < folder.length; i++) {
				folder[i].setIndexValue(i + 1);
			}
		}
	}

	public int getDepth() {
		if (getFatherFolder() != null) {
			return getFatherFolder().getDepth() + 1;
		} else {
			return 0;
		}
	}

	private static Vector<ViewFolder> getAllSubFoldersForFolder(ViewFolder folder) {
		Vector<ViewFolder> v = new Vector<ViewFolder>();
		if (folder != null) {
			v.add(folder);
			Enumeration<ViewFolder> en = folder.getSubFolders().elements();
			while (en.hasMoreElements()) {
				ViewFolder f = en.nextElement();
				v.addAll(getAllSubFoldersForFolder(f));
			}
		}
		return v;
	}

	public ViewFolder(String folderName, ViewLibrary componentLibrary) {
		this(componentLibrary);
		_name = folderName;
	}

	public ViewFolder(VEShemaLibraryBuilder builder) {
		this(builder.shemaLibrary);
		initializeDeserialization(builder);
	}

	/**
	 * Creates and returns a newly created root process
	 * 
	 * @return a newly created workflow
	 */
	public static ViewFolder createNewRootFolder(ViewLibrary library) {
		if (!library.hasRootFolder()) {
			return createNewFolder(library, null, library.getProject().getProject().getProjectName());

		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot create root folder: a root folder is already declared");
			}
			return null;
		}
	}

	public boolean containsShemas() {
		if (getShemas().size() > 0) {
			return true;
		}
		if (getSubFolders().size() > 0) {
			boolean answer = false;
			Enumeration<ViewFolder> en = getSubFolders().elements();
			while (en.hasMoreElements() && !answer) {
				answer = en.nextElement().containsShemas();
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
	public static ViewFolder createNewFolder(ViewLibrary library, ViewFolder parentFolder, String folderName) {
		ViewFolder newFolder = new ViewFolder(folderName, library);
		newFolder.setFatherFolder(parentFolder);
		if (parentFolder != null) {
			parentFolder.addToSubFolders(newFolder);
		} else {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("NEW ROOT FOLDER");
			}
			library.setRootFolder(newFolder);
		}
		/*
		 * library.notifyObservers(new DataModification( DataModification.COMPONENT_FOLDER_ADDED_TO_LIBRARY, null, newFolder)); if
		 * (parentFolder != null) { parentFolder.setChanged(); parentFolder.notifyObservers(new ComponentFolderInserted( parentFolder,
		 * newFolder)); }
		 */
		return newFolder;
	}

	public Vector<ViewFolder> getAllSubFolders() {
		Vector<ViewFolder> v = new Vector<ViewFolder>();
		v.addAll(getAllSubFoldersForFolder(this));
		v.remove(this);
		return v;
	}

	public ViewFolder getShemaFolderWithName(String folderName) {
		for (ViewFolder folder : getAllSubFolders()) {
			if (folder.getName().equals(folderName)) {
				return folder;
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Could not find folder named " + folderName);
		}
		return null;
	}

	public Vector<FlexoModelObject> getAllChilds() {
		Vector<FlexoModelObject> answer = new Vector<FlexoModelObject>();
		answer.addAll(getSubFolders());
		answer.addAll(getShemas());
		return answer;
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#delete()
	 */
	@Override
	public void delete() {
		getFatherFolder().removeFromSubFolders(this);
		super.delete();
		deleteObservers();
	}

	public boolean delete(ViewDefinition def) {
		if (getShemas().contains(def)) {
			removeFromShemas(def);
			// getComponentLibrary().notifyTreeStructureChanged();
			return true;
		} else {

			Enumeration<ViewFolder> en = getSubFolders().elements();
			while (en.hasMoreElements()) {
				boolean isDeleted = en.nextElement().delete(def);
				if (isDeleted) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean isValidForANewShemaName(String value) {
		if (value == null) {
			return false;
		}
		return getShemaNamed(value) == null;
	}

	public ViewDefinition getShemaNamed(String value) {
		if (value == null) {
			return null;
		}

		String searchedName = value;

		Enumeration<ViewDefinition> en = getShemas().elements();
		ViewDefinition temp = null;
		while (en.hasMoreElements()) {
			temp = en.nextElement();
			if (searchedName.toLowerCase().equals(temp.getName().toLowerCase())) {
				return temp;
			}
		}
		ViewDefinition cur = null;
		Enumeration<ViewFolder> en1 = getSubFolders().elements();
		while (en1.hasMoreElements() && cur == null) {
			cur = en1.nextElement().getShemaNamed(searchedName);
		}

		if (cur != null && cur.getName().toLowerCase().equals(searchedName.toLowerCase())) {
			return cur;
		}
		return null;
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * component library
	 * 
	 * @return the component library object
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getShemaLibrary();
	}

	@Override
	public FlexoProject getProject() {
		return getShemaLibrary().getProject();
	}

	@Deprecated
	public Vector<ViewDefinition> getShemas() {
		return getViews();
	}

	@Deprecated
	public void setShemas(Vector<ViewDefinition> views) {
		setViews(views);
	}

	@Deprecated
	public Vector<ViewDefinition> getAllShemas() {
		return getAllViews();
	}

	public Vector<ViewDefinition> getViews() {
		return _views;
	}

	public void setViews(Vector<ViewDefinition> value) {
		_views = value;
	}

	public Vector<ViewDefinition> getAllViews() {
		Vector<ViewDefinition> v = new Vector<ViewDefinition>();
		Enumeration<ViewFolder> en = getSortedSubFolders();
		while (en.hasMoreElements()) {
			ViewFolder f = en.nextElement();
			v.addAll(f.getAllShemas());
		}
		Enumeration<ViewDefinition> en1 = getSortedShemas();
		while (en1.hasMoreElements()) {
			v.add(en1.nextElement());
		}
		return v;
	}

	public Enumeration<ViewDefinition> getSortedShemas() {
		disableObserving();
		ViewDefinition[] o = FlexoIndexManager.sortArray(getShemas().toArray(new ViewDefinition[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public void addToShemas(ViewDefinition shema) {
		if (shema.getFolder() != null && shema.getFolder() != this) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("UNEXPECTEDELY Move shema " + shema + " from folder " + shema.getFolder().getName() + " to folder "
						+ getName());
			}
			shema.getFolder().removeFromShemas(shema);
		}
		_views.add(shema);
		shema.setFolder(this);
		if (!isDeserializing()) {
			int i = 0;
			for (Enumeration<ViewDefinition> en = getSortedShemas(); en.hasMoreElements(); i++) {
				ViewDefinition sd1 = en.nextElement();
				if (ViewDefinition.COMPARATOR.compare(sd1, shema) > 0) {
					shema.setIndex(i + 1);
					break;
				}
			}
			FlexoIndexManager.reIndexObjectOfArray(getShemas().toArray(new ViewDefinition[0]));
		}
		setChanged();
		notifyObservers(new ShemaInserted(shema, this));
		if (isRootFolder()) {
			getShemaLibrary().setChanged();
			getShemaLibrary().notifyObservers(new ShemaInserted(shema, this));
		}
	}

	public void removeFromShemas(ViewDefinition sub) {
		_views.remove(sub);
		sub.setFolder(null);
		FlexoIndexManager.reIndexObjectOfArray(getShemas().toArray(new ViewDefinition[0]));
		setChanged();
		notifyObservers(new ShemaDeleted(sub));
		if (isRootFolder()) {
			getShemaLibrary().setChanged();
			getShemaLibrary().notifyObservers(new ShemaDeleted(sub));
		}
	}

	public Enumeration<ViewFolder> getSortedSubFolders() {
		disableObserving();
		ViewFolder[] o = FlexoIndexManager.sortArray(getSubFolders().toArray(new ViewFolder[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public Vector<ViewFolder> getSubFolders() {
		return _subFolders;
	}

	public void setSubFolders(Vector<ViewFolder> value) {
		_subFolders = value;
		setChanged();
	}

	public void addToSubFolders(ViewFolder sub) {
		if (!_subFolders.contains(sub)) {
			_subFolders.add(sub);
			sub.setFatherFolder(this);
			if (!isDeserializing()) {
				if (getSubFolders().size() > 0) {
					int i = 0;
					for (Enumeration<ViewFolder> en = getSortedSubFolders(); en.hasMoreElements() && i < getSubFolders().size(); i++) {
						ViewFolder f = getSubFolders().get(i);
						if (ViewFolder.COMPARATOR.compare(f, sub) > 0) {
							sub.setIndex(i + 1);
							break;
						}
					}
					// We don't care if no index has been set, it will then keep its current one which is the last one.
				} else {
					sub.setIndex(1);
				}
				FlexoIndexManager.reIndexObjectOfArray(getSubFolders().toArray(new ViewFolder[0]));
			}
			sub.setShemaLibrary(getShemaLibrary());
			setChanged();
			notifyObservers(new ShemaFolderInserted(sub));
			if (isRootFolder()) {
				getShemaLibrary().setChanged();
				getShemaLibrary().notifyObservers(new ShemaFolderInserted(sub));
			}
		}
	}

	public void removeFromSubFolders(ViewFolder sub) {
		_subFolders.remove(sub);
		FlexoIndexManager.reIndexObjectOfArray(getSubFolders().toArray(new ViewFolder[0]));
		setChanged();
		notifyObservers(new ShemaFolderDeleted(sub));
		if (isRootFolder()) {
			getShemaLibrary().setChanged();
			getShemaLibrary().notifyObservers(new ShemaFolderDeleted(sub));
		}
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String name) throws DuplicateFolderNameException, InvalidNameException {
		if (requireChange(_name, name)) {
			if (getFatherFolder() != null && getFatherFolder().getFolderNamed(name) != null) {
				throw new DuplicateFolderNameException(this, name);
			}
			// There is no reason to dismiss accents
			/*
			 * if (!isDeserializing() && !name.matches(FileUtils.GOOD_CHARACTERS_REG_EXP + "+")) { throw new InvalidNameException(name); }
			 */
			String old = _name;
			_name = name;
			setChanged();
			notifyObservers(new OEDataModification("name", old, name));
		}
	}

	public ViewFolder getFatherFolder() {
		return _fatherFolder;
	}

	public void setFatherFolder(ViewFolder folder) {
		_fatherFolder = folder;
	}

	/**
	 * @param folder
	 * @deprecated use {@link #setFatherFolder(ViewFolder)}
	 */
	@Deprecated
	public void setParent(ViewFolder folder) {
		setFatherFolder(folder);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VE.OE_SHEMA_FOLDER_INSPECTOR;
	}

	@Override
	public String getFullyQualifiedName() {
		return "Folder:" + getName();
	}

	/**
	 * Search in the direct sub-folders of this folder for a folder named <code>name</code> (case insensitive).
	 * 
	 * @param name
	 *            - the name of the direct sub-folder to find
	 * @return the direct sub-folder named <code>name</code> or null if it cannot be found.
	 */
	public ViewFolder getFolderNamed(String name) {
		name = name.toLowerCase();
		Enumeration<ViewFolder> en = getSubFolders().elements();
		while (en.hasMoreElements()) {
			ViewFolder folder = en.nextElement();
			if (folder.getName().toLowerCase().equals(name)) {
				return folder;
			}
		}
		return null;
	}

	public boolean isFatherOf(ViewFolder folder) {
		ViewFolder f = folder.getFatherFolder();
		while (f != null) {
			if (f.equals(this)) {
				return true;
			}
			f = f.getFatherFolder();
		}
		return false;
	}

	public static class FolderComparator implements Comparator<ViewFolder> {
		protected FolderComparator() {
		}

		/**
		 * Overrides compare
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ViewFolder o1, ViewFolder o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	public boolean isRootFolder() {
		return getFatherFolder() == null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "shema_folder";
	}

	public boolean isIndexed() {
		return this.index > -1;
	}

	@Override
	public int getIndex() {
		if (isBeingCloned()) {
			return -1;
		}
		if (index == -1 && getCollection() != null) {
			index = getCollection().length;
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
		if (getIndex() != index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index", null, getIndex());
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
		if (this.index == index) {
			return;
		}
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
	public ViewFolder[] getCollection() {
		if (getFatherFolder() == null) {
			return new ViewFolder[] { this };
		}
		return getFatherFolder().getSubFolders().toArray(new ViewFolder[0]);
	}

	public static class DuplicateFolderNameException extends FlexoException {

		private ViewFolder folder;
		private String name;

		/**
		 * @param folder
		 * @param name
		 */
		public DuplicateFolderNameException(ViewFolder folder, String name) {
			this.folder = folder;
			this.name = name;
		}

		public ViewFolder getFolder() {
			return folder;
		}

		public String getName() {
			return name;
		}

		/**
		 * Overrides getLocalizedMessage
		 * 
		 * @see org.openflexo.foundation.FlexoException#getLocalizedMessage()
		 */
		@Override
		public String getLocalizedMessage() {
			return FlexoLocalization.localizedForKey("duplicate_folder_name");
		}
	}

}