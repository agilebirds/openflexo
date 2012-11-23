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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.gen.ScreenshotGenerator.ScreenshotImage;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.dm.ComponentNameChanged;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ImageInfo;

/**
 * @author sylvain
 * 
 */
public class ScreenshotResource extends FlexoGeneratedResource<ScreenshotResource.ScreenshotData> implements FlexoObserver,
		FlexoModelObjectReference.ReferenceOwner {
	protected static final Logger logger = FlexoLogger.getLogger(ScreenshotResource.class.getPackage().getName());

	public static final ImageType SCREENSHOT_TYPE = ImageType.PNG;

	public static final String DOTTED_SCREENSHOT_EXTENSION = "." + SCREENSHOT_TYPE.getExtension();

	// private FlexoModelObject source;

	private String sourceReferenceString;
	private FlexoModelObjectReference<FlexoModelObject> sourceReference;

	private boolean willBeDeleted = false;

	private String name;

	protected Rectangle trimInfo;

	@Override
	public String getName() {
		return name;
	}

	/**
	 *
	 */
	private void checkResourceNameIsUpToDate() {
		if (getModelObject() != null) {
			String s = ScreenshotGenerator.getScreenshotName(getModelObject());
			if (name != null && !name.equals(s) && project != null && !project.isDeserializing() && !project.isSerializing()) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Renaming resource " + name + " to " + s);
				}
				setName(s);
			}
		}
	}

	@Override
	public void setName(String aName) {
		if (aName == null) {
			return;
		}
		if (name != null && name.equals(aName)) {
			return;
		}
		String oldName = name;
		name = aName;
		try {
			if (resourceFile != null && !getFile().getName().equals(aName + DOTTED_SCREENSHOT_EXTENSION)
					&& !getFile().getName().equals(aName)) {
				if (!getFile().exists()) {
					try {
						getFile().createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				boolean b = renameFileTo(name + DOTTED_SCREENSHOT_EXTENSION);
				if (!b && logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not rename screenshot resource from" + getFile().getName() + " to " + name);
				}
				if (b) {
					getProject().renameResource(this, name);
				}
			}
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
			name = oldName;
			delete();
		} catch (InvalidFileNameException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("InvalidFileName occured while trying to rename screenshot with name " + name + ". Deleting it.");
			}
			e.printStackTrace();
			name = oldName;
			delete();
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("An exception occured while trying to rename screenshot with name " + name + ". Deleting it.");
			}
			e.printStackTrace();
			name = oldName;
			delete();
		}
	}

	public static ScreenshotResource createNewScreenshotForObject(FlexoModelObject o) {
		/**
		 * Little help if you want to create a screenshot for an object: *
		 * org.openflexo.foundation.rm.ScreenshotResource.createNewScreenshotForObject(FlexoModelObject) *
		 * org.openflexo.foundation.gen.ScreenshotGenerator.getScreenshotName(Object) *
		 * org.openflexo.foundation.gen.ScreenshotGenerator.getImage(FlexoModelObject) (twice) *
		 * org.openflexo.dg.latex.ScreenshotsGenerator.getScreenshot(FlexoModelObject) *
		 * org.openflexo.dg.latex.ScreenshotsGenerator.buildResourcesAndSetGenerators(DGRepository, Vector<CGRepositoryFileResource>) * and
		 * in the method of the module that will generate the screenshot Clearly this could be improved but I don't have time for it.
		 */
		ScreenshotResource ret = new ScreenshotResource(o.getProject());
		String name = ScreenshotGenerator.getScreenshotName(o);
		ret.setName(name);
		if (o instanceof FlexoProcess) {
			ret.setSource(o);
		} else if (o instanceof AbstractActivityNode) {
			ret.setSource(o);
		} else if (o instanceof IEWOComponent) {
			IEWOComponent comp = (IEWOComponent) o;
			ret.setSource(comp.getComponentDefinition()); // We use the component definition as the source but we depend of the
															// WOComponentResource (@see #rebuildDependancies())
		} else if (o instanceof ComponentDefinition) {
			ComponentDefinition comp = (ComponentDefinition) o;
			ret.setSource(comp); // We use the component definition as the source but we depend of the WOComponentResource (@see
									// #rebuildDependancies())
		} else if (o instanceof OperationNode) {
			OperationNode node = (OperationNode) o;
			ret.setSource(node);
		} else if (o instanceof LOOPOperator) {
			ret.setSource(o);
		} else if (o instanceof ERDiagram) {
			ret.setSource(o);
		} else if (o instanceof RoleList) {
			ret.setSource(o);
		} else if (o instanceof FlexoWorkflow) {
			ret.setSource(o);
		} else if (o instanceof ViewDefinition) {
			ret.setSource(o);
		} else if (o instanceof View) {
			ret.setSource(((View) o).getShemaDefinition());
		} else {
			logger.warning("Could not create screenshot for " + o);
			return null;
		}
		ret.rebuildDependancies();
		FlexoProjectFile file = new FlexoProjectFile(ProjectRestructuration.GENERATED_DOC_DIR + "/" + name + DOTTED_SCREENSHOT_EXTENSION);
		try {
			ret.setResourceFile(file);
		} catch (InvalidFileNameException e1) {
			file = new FlexoProjectFile(FileUtils.getValidFileName(file.getRelativePath()));
			try {
				ret.setResourceFile(file);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Invalid file name: " + file.getRelativePath() + ". This should never happen.");
				}
				return null;
			}
		}
		ret.setName(name);
		try {
			o.getProject().registerResource(ret);
		} catch (DuplicateResourceException e) {
			// Warns about the exception
			logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * @param builder
	 */
	public ScreenshotResource(FlexoProjectBuilder builder) {
		super(builder);
		builder.notifyResourceLoading(this);
	}

	/**
	 * @param aProject
	 */
	public ScreenshotResource(FlexoProject aProject) {
		super(aProject);
	}

	/**
	 * Overrides generateResourceData
	 * 
	 * @see org.openflexo.foundation.rm.FlexoGeneratedResource#createResourceData()
	 */
	/*
	 * protected FlexoResourceData createResourceData() { FlexoResourceData data =
	 * new ScreenshotData(getFile()); data.setProject(getProject()); try {
	 * data.setFlexoResource(this); } catch (DuplicateResourceException e) { }
	 * resourceData = data; return data; }
	 */

	/**
	 * Overrides getResourceType
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getResourceType()
	 */
	@Override
	public ResourceType getResourceType() {
		return ResourceType.SCREENSHOT;
	}

	/**
	 * Overrides isDeleted
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#isDeleted()
	 */
	@Override
	public boolean isDeleted() {
		return super.isDeleted();
	}

	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		if (resource instanceof FlexoProcessResource) {
			if (!requestDate.before(((FlexoProcessResource) resource).getFlexoProcess().getLastUpdate())) {
				return false;
			}
		} else if (resource instanceof FlexoComponentResource) {
			if (!checkOptimisticDependancyForComponent(resource, requestDate)) {
				return false;
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	/**
	 * @param resource
	 * @param requestDate
	 */
	private boolean checkOptimisticDependancyForComponent(FlexoResource resource, Date requestDate) {
		if (lastUpdateDateIsBefore(((FlexoComponentResource) resource).getResourceData(), requestDate)) {
			boolean depends = false;
			Vector<TabComponentDefinition> tabs = new Vector<TabComponentDefinition>();
			((FlexoComponentResource) resource).getResourceData().getAllTabComponents(tabs);
			for (TabComponentDefinition tab : tabs) {
				if (tab.isLoaded()) {
					depends |= !lastUpdateDateIsBefore(tab.getWOComponent(), requestDate);
				}
			}
			return depends;
		}
		return true;
	}

	private boolean lastUpdateDateIsBefore(IEWOComponent component, Date requestDate) {
		return component.getLastUpdate() != null && !requestDate.before(component.getLastUpdate());
	}

	@Override
	public boolean checkIntegrity() {
		return getSourceReference() != null
				&& (getModelObject() != null || sourceReference.getResource() != null && sourceReference.getResource().getIsLoading());
	}

	public FlexoModelObject getModelObject() {
		if (getSourceReference() != null) {
			return sourceReference.getObject(true);
		} else {
			if (!willBeDeleted) {
				willBeDeleted = true;
				this.delete(false);
			}
		}
		return null;
	}

	@Override
	public boolean isGeneratedResourceDataReadable() {
		return true;
	}

	@Override
	public ScreenshotData readGeneratedResourceData() {
		return createGeneratedResourceData();
	}

	@Override
	protected ScreenshotData createGeneratedResourceData() {
		return new ScreenshotData();
	}

	public ScreenshotData getScreenshotData() {
		return getGeneratedResourceData();
	}

	/**
	 * Overrides setResourceFile
	 * 
	 * @see org.openflexo.foundation.rm.FlexoFileResource#setResourceFile(org.openflexo.foundation.utils.FlexoProjectFile)
	 */
	@Override
	public void setResourceFile(FlexoProjectFile aFile) throws InvalidFileNameException {
		if (aFile.getRelativePath() != null && !aFile.getRelativePath().toLowerCase().endsWith(DOTTED_SCREENSHOT_EXTENSION)) {
			String relPath = "";
			if (aFile.getRelativePath().indexOf('/') > -1) {
				relPath = aFile.getRelativePath().substring(0, aFile.getRelativePath().lastIndexOf('/') + 1);
			}
			if (name != null) {
				aFile.setRelativePath(relPath + name + DOTTED_SCREENSHOT_EXTENSION);
			}
		}
		super.setResourceFile(aFile);
	}

	/**
	 * Overrides finalizeGeneration
	 * 
	 * @see org.openflexo.foundation.rm.FlexoGeneratedResource#finalizeGeneration()
	 */
	@Override
	public void finalizeGeneration() {
		super.finalizeGeneration();
		resetGeneratedResourceData();
	}

	public class ScreenshotData implements GeneratedResourceData {

		protected ScreenshotImage data;

		public BufferedImage getImage() {
			if (data != null) {
				return data.image;
			}
			return null;
		}

		/**
         *
         */
		public ScreenshotData() {
		}

		/**
		 * Overrides getFlexoResource
		 * 
		 * @see org.openflexo.foundation.rm.FlexoResourceData#getFlexoResource()
		 */
		@Override
		public ScreenshotResource getFlexoResource() {
			return ScreenshotResource.this;
		}

		public ScreenshotResource getScreenshotResource() {
			return getFlexoResource();
		}

		/**
		 * Overrides setFlexoResource
		 * 
		 * @see org.openflexo.foundation.rm.FlexoResourceData#setFlexoResource(org.openflexo.foundation.rm.FlexoResource)
		 */
		@Override
		public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {

		}

		/**
		 * Overrides getProject
		 * 
		 * @see org.openflexo.foundation.rm.FlexoResourceData#getProject()
		 */
		@Override
		public FlexoProject getProject() {
			return getFlexoResource().getProject();
		}

		/**
		 * Overrides setProject
		 * 
		 * @see org.openflexo.foundation.rm.FlexoResourceData#setProject(org.openflexo.foundation.rm.FlexoProject)
		 */
		@Override
		public void setProject(FlexoProject aProject) {
		}

		@Override
		public void generate() {
			generateScreenshot();
		}

		@Override
		public void regenerate() {
			generateScreenshot();
		}

		private void generateScreenshot() {
			data = ScreenshotGenerator.getImage(getModelObject());
			if (data != null) {
				trimInfo = data.trimInfo;
			}
		}

		/**
		 * @throws FlexoException
		 * 
		 */
		@Override
		public void writeToFile(File aFile) throws FlexoException {
			if (data == null) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Called write to file without having called generate on screenshot resource data: " + getFlexoResource());
				}
				return;
			}
			/*
			 * if (needsGeneration) generateScreenshot();
			 */
			File image = aFile;
			File path = image.getParentFile();
			if (!path.exists()) {
				path.mkdirs();
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Writing file " + image.getAbsolutePath());
			}
			try {
				ImageUtils.saveImageToFile(data.image, image, SCREENSHOT_TYPE);
			} catch (Exception e) {
				throw new FlexoException("Error while writing image to " + image.getAbsolutePath(), e);
			}
		}

	}

	/**
	 * Overrides generateData
	 * 
	 * @see org.openflexo.foundation.rm.FlexoGeneratedResource#generateData()
	 */
	/*
	 * protected void generateData() throws GenerateResourceException {
	 * getScreenshotData().generateScreenshot(); }
	 */

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof NameChanged) {
			if (getModelObject() == observable || getModelObject() instanceof AbstractActivityNode
					&& observable == ((AbstractActivityNode) getModelObject()).getProcess() || getModelObject() instanceof OperationNode
					&& observable == ((OperationNode) getModelObject()).getProcess() || getModelObject() instanceof OperationNode
					&& observable == ((OperationNode) getModelObject()).getAbstractActivityNode()) {
				checkResourceNameIsUpToDate();
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("Renamed screenshot due to a rename in the workflow");
				}
			}
		} else if (dataModification instanceof ComponentNameChanged && observable == getModelObject()) {
			checkResourceNameIsUpToDate();
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Renamed screenshot due to component rename.");
			}
		} else if (dataModification instanceof ObjectDeleted && ((ObjectDeleted) dataModification).getDeletedObject() == getModelObject()) {
			delete(false);
		}
	}

	private boolean isObserving = false;

	public void startObserving() {
		if (isObserving) {
			return;
		}
		if (getModelObject() != null) {
			getModelObject().addObserver(this);
			if (getModelObject() instanceof AbstractActivityNode) {
				((AbstractActivityNode) getModelObject()).getProcess().addObserver(this);
			} else if (getModelObject() instanceof OperationNode) {
				((OperationNode) getModelObject()).getProcess().addObserver(this);
				((OperationNode) getModelObject()).getAbstractActivityNode().addObserver(this);
			}
			isObserving = true;
		}
	}

	private void stopObserving() {
		if (getModelObject() != null) {
			getModelObject().deleteObserver(this);
		}
		if (getModelObject() instanceof AbstractActivityNode && ((AbstractActivityNode) getModelObject()).getProcess() != null) {
			((AbstractActivityNode) getModelObject()).getProcess().deleteObserver(this);
		}
		isObserving = false;
	}

	/**
	 * Delete this resource. Delete file is flag deleteFile is true.
	 */
	@Override
	public synchronized void delete(boolean deleteFile) {
		willBeDeleted = true;
		if (sourceReference != null) {
			sourceReference.delete();
		}
		stopObserving();
		super.delete(deleteFile);
	}

	/**
	 * Overrides rebuildDependancies
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#rebuildDependancies()
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		if (getModelObject() != null && getModelObject().getXMLResourceData() != null
				&& getModelObject().getXMLResourceData().getFlexoResource() != null) {
			if (!(getModelObject() instanceof ComponentDefinition) && !(getModelObject() instanceof ViewDefinition)) {
				addToDependentResources(getModelObject().getXMLResourceData().getFlexoResource());
			}
		}
		if (getModelObject() instanceof ComponentDefinition) {
			// On the next line we pass true because the WDLDateAssistantPopup is virtually created but does not
			// create its res resource automatically.
			FlexoComponentResource compRes = ((ComponentDefinition) getModelObject()).getComponentResource(true);
			if (compRes != null) {
				addToDependentResources(compRes);
			}
		}
		if (getModelObject() instanceof ViewDefinition) {
			FlexoOEShemaResource viewRes = ((ViewDefinition) getModelObject()).getShemaResource(false);
			if (viewRes != null) {
				addToDependentResources(viewRes);
			}
		}
	}

	public Rectangle getTrimInfo() {
		if (trimInfo == null) {
			ImageInfo ii = new ImageInfo();
			FileInputStream fis = null;
			if (getFile() != null && getFile().exists()) {
				try {
					ii.setInput(fis = new FileInputStream(getFile()));
					ii.check();
					trimInfo = new Rectangle(0, 0, ii.getWidth(), ii.getHeight());
					ii = null;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return trimInfo;
	}

	public void setTrimInfo(Rectangle trimInfo) {
		this.trimInfo = trimInfo;
	}

	public FlexoModelObjectReference<FlexoModelObject> getSourceReference() {
		if (sourceReference == null && sourceReferenceString != null) {
			if (getProject() != null) {
				setSourceReference(getProject().getObjectReferenceConverter().convertFromString(sourceReferenceString));
				sourceReferenceString = null;
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("No project on screenshot: " + getName());
				}
			}
		}
		return sourceReference;
	}

	private void setSourceReference(FlexoModelObjectReference<FlexoModelObject> sourceReference) {
		if (sourceReference == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Trying to set a null source reference, this is not allowed!");
			}
		}
		this.sourceReference = sourceReference;
		if (sourceReference != null) {
			sourceReference.setSerializeClassName(true);
			sourceReference.setOwner(this);
		}
	}

	public String getSourceReferenceString() {
		if (sourceReference != null) {
			if (getProject() != null) {
				return getProject().getObjectReferenceConverter().convertToString(sourceReference);
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("No project on screenshot: " + getName());
				}
			}
		}
		return sourceReferenceString;
	}

	public void setSourceReferenceString(String sourceReferenceString) {
		this.sourceReferenceString = sourceReferenceString;
	}

	private void setSource(FlexoModelObject o) {
		setSourceReference(new FlexoModelObjectReference<FlexoModelObject>(o));
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference reference) {
		checkResourceNameIsUpToDate();
		startObserving();
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference reference) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find object for reference: " + reference + ", deleting resource " + this);
		}
		if (!willBeDeleted) {
			willBeDeleted = true;
			this.delete(false);
		}
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference reference) {
		if (!willBeDeleted) {
			willBeDeleted = true;
			this.delete(false);
		}
	}

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference reference) {
		setChanged();
	}
}
