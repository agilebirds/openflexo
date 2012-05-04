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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.dm.ShemaNameChanged;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoOEShemaLibraryResource;
import org.openflexo.foundation.rm.FlexoOEShemaResource;
import org.openflexo.foundation.rm.FlexoOperationComponentResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.xml.VEShemaLibraryBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ReservedKeyword;

public class ViewDefinition extends ViewLibraryObject implements Sortable {

	protected static final Logger logger = FlexoLogger.getLogger(ViewDefinition.class.getPackage().getName());

	private ViewFolder _folder;
	private String _shemaName;
	private int index = -1;
	private ViewPoint _calc;
	private ViewPointLibrary _calcLibrary;
	private boolean lookupDone = false;
	private String viewPointURI;

	public ViewDefinition(VEShemaLibraryBuilder builder) throws DuplicateResourceException {
		this(null, builder.shemaLibrary, null, builder.getProject(), false);
		initializeDeserialization(builder);
		_calcLibrary = builder.viewPointLibrary;
	}

	public ViewDefinition(String aShemaName, ViewLibrary shemaLibrary, ViewFolder aFolder, FlexoProject project, boolean checkUnicity)
			throws DuplicateResourceException {
		super(shemaLibrary);
		_shemaName = aShemaName;
		if (aFolder != null) {
			aFolder.addToShemas(this);
		}
		shemaLibrary.handleNewShemaCreated(this);

		if (checkUnicity) {
			String resourceIdentifier = FlexoOperationComponentResource.resourceIdentifierForName(aShemaName);
			if (project != null && project.isRegistered(resourceIdentifier)) {
				if (aFolder != null) {
					aFolder.removeFromShemas(this);
				}
				throw new DuplicateResourceException(resourceIdentifier);
			}
		}
	}

	@Override
	public void delete() {
		if (getFolder() != null) {
			getFolder().removeFromShemas(this);
		}
		super.delete();
	}

	@Override
	public String getName() {
		return _shemaName;
	}

	@Override
	public void setName(String aName) throws DuplicateResourceException, InvalidNameException {
		setShemaName(aName);
	}

	private void setShemaName(String name) throws DuplicateResourceException, InvalidNameException {
		if (_shemaName != null && !_shemaName.equals(name) && name != null && !isDeserializing()) {
			if (!name.matches(IERegExp.JAVA_CLASS_NAME_REGEXP)) {
				throw new InvalidNameException();
			}

			if (ReservedKeyword.contains(name)) {
				throw new InvalidNameException();
			}
			if (getProject() != null) {
				ViewDefinition cd = getShemaLibrary().getShemaNamed(name);
				if (cd != null && cd != this) {
					throw new DuplicateResourceException(getShemaResource(false));
				}
				FlexoOEShemaResource resource = getShemaResource();
				if (resource != null) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Renaming shema resource !");
					}
					try {
						getProject().renameResource(resource, name);
					} catch (DuplicateResourceException e1) {
						throw e1;
					}
				}
				String oldShemaName = _shemaName;
				_shemaName = name;
				setChanged();
				notifyObservers(new ShemaNameChanged("name", this, oldShemaName, name));
			}
		} else {
			_shemaName = name;
		}
	}

	public Collection<EditionPatternInstance> getEPInstances(String epName) {
		if (getShema() != null) {
			return getShema().getEPInstances(epName);
		} else {
			return Collections.emptyList();
		}
	}

	public FlexoOEShemaResource getShemaResource() {
		return getShemaResource(true);
	}

	public FlexoOEShemaResource getShemaResource(boolean createIfNotExists) {
		if (getProject() != null) {
			FlexoOEShemaResource returned = getProject().getShemaResource(getName());
			if (returned == null && createIfNotExists) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Creating new shema resource !");
				}
				FlexoOEShemaLibraryResource libRes = getProject().getFlexoShemaLibraryResource();
				File shemaFile = new File(ProjectRestructuration.getExpectedOntologyDirectory(getProject().getProjectDirectory()),
						getName() + ProjectRestructuration.SHEMA_EXTENSION);
				FlexoProjectFile resourceShemaFile = new FlexoProjectFile(shemaFile, getProject());
				FlexoOEShemaResource shemaRes = null;
				try {
					shemaRes = new FlexoOEShemaResource(getProject(), getName(), libRes, resourceShemaFile);
				} catch (InvalidFileNameException e1) {
					boolean ok = false;
					for (int i = 0; i < 100 && !ok; i++) {
						try {
							shemaFile = new File(ProjectRestructuration.getExpectedOntologyDirectory(getProject().getProjectDirectory()),
									FileUtils.getValidFileName(getName()) + i + ProjectRestructuration.SHEMA_EXTENSION);
							resourceShemaFile = new FlexoProjectFile(shemaFile, getProject());
							shemaRes = new FlexoOEShemaResource(getProject(), getName(), libRes, resourceShemaFile);
							ok = true;
						} catch (InvalidFileNameException e) {
						}
					}
					if (!ok) {
						shemaFile = new File(ProjectRestructuration.getExpectedOntologyDirectory(getProject().getProjectDirectory()),
								FileUtils.getValidFileName(getName()) + getFlexoID() + ".shema");
						resourceShemaFile = new FlexoProjectFile(shemaFile, getProject());
						try {
							shemaRes = new FlexoOEShemaResource(getProject(), getName(), libRes, resourceShemaFile);
						} catch (InvalidFileNameException e) {
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("This should really not happen.");
							}
							return null;
						}
					}
				}
				if (shemaRes == null) {
					return null;
				}
				shemaRes.setResourceData(createNewShema());

				try {
					shemaRes.getResourceData().setFlexoResource(shemaRes);
					getProject().registerResource(shemaRes);
				} catch (DuplicateResourceException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
					return null;
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Registered shema " + getName() + " file: " + shemaFile);
				}
				returned = shemaRes;
			}
			return returned;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not access to project !");
			}
		}
		return null;
	}

	public View createNewShema() {
		return new View(this, getProject());
	}

	public void notifyShemaHasBeenLoaded() {
		/*setChanged(false);
		if (logger.isLoggable(Level.FINE))
		    logger.fine("Notify observers that WO has been loaded");
		notifyObservers(new ShemaLoaded(this));*/
	}

	public ViewFolder getFolder() {
		return _folder;
	}

	public void setFolder(ViewFolder aFolder) {
		_folder = aFolder;
		if (!isDeserializing()) {
			if (_folder == null) {
				this.index = -1;
			} else {
				this.index = _folder.getShemas().size();
			}
		}
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
		if (!isDeserializing() && !isCreatedByCloning() && getFolder() != null) {
			getFolder().setChanged();
			getFolder().notifyObservers(new ChildrenOrderChanged());
		}
	}

	public static final ShemaComparator COMPARATOR = new ShemaComparator();

	public static class ShemaComparator implements Comparator<ViewDefinition> {
		protected ShemaComparator() {
		}

		/**
		 * Overrides compare
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ViewDefinition o1, ViewDefinition o2) {
			return o1.getName().compareTo(o2.getName());
		}

	}

	@Override
	public String getClassNameKey() {
		return "shema_definition";
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getFullyQualifiedName() + ".SHEMA." + getName();
	}

	/**
	 * Overrides getCollection
	 * 
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public ViewDefinition[] getCollection() {
		if (getFolder() == null) {
			return null;
		}
		return getFolder().getShemas().toArray(new ViewDefinition[0]);
	}

	public boolean isLoaded() {
		return hasShemaResource() && getShemaResource().isLoaded();
	}

	public boolean hasShemaResource() {

		return getShemaResource(false) != null;
	}

	public View getShema() {
		return getShema(null);
	}

	public View getShema(FlexoProgress progress) {
		return getShemaResource().getResourceData(progress);
	}

	public static class DuplicateShemaNameException extends FlexoException {

		private String name;

		public DuplicateShemaNameException(String newShemaName) {
			this.name = newShemaName;
		}

		public String getName() {
			return name;
		}
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VE.OE_SHEMA_DEFINITION_INSPECTOR;
	}

	public ViewPoint getViewPoint() {
		if (_calc == null && !lookupDone) {
			_calc = _calcLibrary.getViewPoint(viewPointURI);
			lookupDone = true;
		}
		return _calc;
	}

	public void setViewPoint(ViewPoint viewPoint) {
		_calc = viewPoint;
		setChanged();
		viewPointURI = null;
	}

	@Deprecated
	public ViewPoint getCalc() {
		return getViewPoint();
	}

	@Deprecated
	public void setCalc(ViewPoint viewPoint) {
		setViewPoint(viewPoint);
	}

	// Don't use it, serialization only
	public String _getCalcURI() {
		if (getViewPoint() != null) {
			return getViewPoint().getViewPointURI();
		}
		return viewPointURI;
	}

	// Don't use it, deserialization only
	public void _setCalcURI(String ontologyCalcUri) {
		viewPointURI = ontologyCalcUri;
	}

}
