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
package org.openflexo.foundation.view.diagram.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoViewResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.VEDataModification;
import org.openflexo.foundation.view.ViewDeleted;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.diagram.DiagramMetaModel;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.xml.ViewBuilder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * TODO: This class should be split into 3 notions: View, VirtualModel and DiagramModel
 * 
 * @author sylvain
 * 
 */
public class View extends ViewObject implements XMLStorageResourceData<View>, FlexoModel<View, DiagramMetaModel> {

	private static final Logger logger = Logger.getLogger(View.class.getPackage().getName());

	private FlexoViewResource _resource;
	private ViewPoint _viewpoint;
	private List<ModelSlotInstance> modelSlotInstances;
	private Map<ModelSlot<?, ?>, FlexoModel<?, ?>> modelsMap = new HashMap<ModelSlot<?, ?>, FlexoModel<?, ?>>(); // Do not serialize this.
	private String title;

	private final FlexoProject project;

	public static View newView(String viewName, String viewTitle, ViewPoint viewPoint, RepositoryFolder<FlexoViewResource> folder,
			FlexoProject project) throws InvalidFileNameException {

		FlexoViewResource newViewResource = new FlexoViewResource(project, viewName, new FlexoProjectFile(new File(folder.getFile(),
				viewName + ".view"), project), viewPoint);

		View newView = new View(project);
		newViewResource.setResourceData(newView);
		newView.setResource(newViewResource);

		// And register it to the library
		project.getViewLibrary().registerResource(newViewResource, folder);

		return newView;
	}

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public View(ViewBuilder builder) {
		this(builder.getProject());
		builder.view = this;
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public View(FlexoProject project) {
		super(project);
		this.project = project;
		logger.info("Created new view with project " + project);
		setView(this);
	}

	@Override
	public DiagramMetaModel getMetaModel() {
		return DiagramMetaModel.INSTANCE;
	}

	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		// TODO
		return null;
	}

	@Override
	public FlexoProject getProject() {
		if (getFlexoResource() != null) {
			return super.getProject();
		}
		return project;
	}

	@Override
	public Object getObject(String objectURI) {
		return null;
	}

	public Collection<EditionPatternInstance> getEPInstances(String epName) {
		if (getViewPoint() == null) {
			return Collections.emptyList();
		}
		EditionPattern ep = getViewPoint().getEditionPattern(epName);
		return getEPInstances(ep);
	}

	public Collection<EditionPatternInstance> getEPInstances(EditionPattern ep) {
		Collection<ViewShape> shapes = getChildrenOfType(ViewShape.class);
		Collection<ViewConnector> connectors = getChildrenOfType(ViewConnector.class);
		Collection<EditionPatternInstance> epis = new LinkedHashSet<EditionPatternInstance>();
		for (ViewShape shape : shapes) {
			EditionPatternReference epr = shape.getEditionPatternReference();
			if (epr == null) {
				continue;
			}
			if (/* epr.isPrimaryRole() && */epr.getEditionPattern() == ep) {
				epis.add(epr.getEditionPatternInstance());
			}
		}
		for (ViewConnector conn : connectors) {
			EditionPatternReference epr = conn.getEditionPatternReference();
			if (epr == null) {
				continue;
			}
			if (/* epr.isPrimaryRole() && */epr.getEditionPattern() == ep) {
				epis.add(epr.getEditionPatternInstance());
			}
		}
		return epis;
	}

	public List<EditionPatternInstance> getEPInstancesWithPropertyEqualsTo(String epName, String epProperty, Object value) {
		List<EditionPatternInstance> returned = new ArrayList<EditionPatternInstance>();
		Collection<EditionPatternInstance> epis = getEPInstances(epName);
		for (EditionPatternInstance epi : epis) {
			Object evaluate = epi.evaluate(epProperty);
			if (value == null && evaluate == value || value != null && value.equals(evaluate)) {
				returned.add(epi);
			}
		}
		return returned;
	}

	@Override
	public FlexoViewResource getFlexoResource() {
		return _resource;
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return getFlexoResource();
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		_resource = (FlexoViewResource) resource;
	}

	@Override
	public org.openflexo.foundation.resource.FlexoResource<View> getResource() {
		return getFlexoResource();
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<View> resource) {
		try {
			setFlexoResource((FlexoResource) resource);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() throws SaveResourceException {
		getFlexoResource().saveResourceData();
	}

	@Override
	public String getClassNameKey() {
		return "oe_shema";
	}

	@Override
	public String getName() {
		if (getFlexoResource() != null) {
			return getFlexoResource().getName();
		}
		return null;
	}

	// TODO: big issue with renaming, don't call this !!!
	@Override
	public void setName(String name) throws DuplicateResourceException, InvalidNameException {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) throws DuplicateResourceException, InvalidNameException {
		String oldTitle = this.title;
		if (requireChange(oldTitle, title)) {
			this.title = title;
			setChanged();
			notifyObservers(new VEDataModification("title", oldTitle, title));
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getFullyQualifiedName() + "." + getName();
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getShemaMapping();
	}

	public ViewPoint getViewPoint() {
		if (getFlexoResource() != null) {
			return getFlexoResource().getViewPoint();
		}
		return null;
	}

	@Override
	public boolean isContainedIn(ViewObject o) {
		return o == this;
	}

	@Override
	public String getDisplayableDescription() {
		return "View " + getName() + (getViewPoint() != null ? " (calc " + getViewPoint().getName() + ")" : "");
	}

	/**
	 * @return
	 */
	public static final String getTypeName() {
		return "VIEW";
	}

	@Override
	public String toString() {
		return "View[name=" + getName() + "/viewpoint=" + getViewPoint().getName() + "/hash=" + Integer.toHexString(hashCode()) + "]";
	}

	// ==========================================================================
	// ============================== Model Slots ===============================
	// ==========================================================================

	/**
	 * This is the binding point between a {@link ModelSlot} and its concretization in a {@link View} through notion of
	 * {@link ModelSlotInstance}
	 * 
	 * @param modelSlot
	 * @return
	 */
	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> ModelSlotInstance<M, MM> getModelSlotInstance(
			ModelSlot<M, MM> modelSlot) {
		// TODO
		logger.warning("Please implement me");
		return null;
	}

	public void setModelSlotInstances(List<ModelSlotInstance> instances) {
		this.modelSlotInstances = instances;
		modelsMap.clear();
		for (ModelSlotInstance model : instances) {
			modelsMap.put(model.getModelSlot(), model.getModel());
		}
	}

	public List<ModelSlotInstance> getModelSlotInstances() {
		return modelSlotInstances;
	}

	public void removeFromModelSlotInstance(ModelSlotInstance instance) {
		modelSlotInstances.remove(instance);
		modelsMap.remove(instance.getModelSlot());
	}

	public void addToModelSlotInstances(ModelSlotInstance instance) {
		Iterator<ModelSlotInstance> it = modelSlotInstances.iterator();
		while (it.hasNext()) {
			if (it.next().getModelSlot().equals(instance.getModelSlot())) {
				it.remove();
			}
		}
		modelSlotInstances.add(instance);
		modelsMap.put(instance.getModelSlot(), instance.getModel());
	}

	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> void setModel(ModelSlot<M, MM> modelSlot, M model) {
		modelsMap.put(modelSlot, model);
		for (ModelSlotInstance instance : modelSlotInstances) {
			if (instance.getModelSlot().equals(modelSlot)) {
				instance.setModel(model);
				return;
			}
		}
		ModelSlotInstance<M, MM> instance = new ModelSlotInstance<M, MM>(this, modelSlot);
		instance.setModel(model);
		modelSlotInstances.add(instance);
	}

	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> M getModel(ModelSlot<M, MM> modelSlot, boolean createIfDoesNotExist) {
		M model = (M) modelsMap.get(modelSlot);
		if (createIfDoesNotExist && model == null) {
			try {
				org.openflexo.foundation.resource.FlexoResource<M> modelResource = modelSlot.createEmptyModel(this,
						modelSlot.getMetaModelResource());
				model = modelResource.getResourceData(null);
				setModel(modelSlot, model);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceDependencyLoopException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return model;
	}

	public <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> M getModel(ModelSlot<M, MM> modelSlot) {
		return getModel(modelSlot, true);
	}

	public Set<FlexoMetaModel> getAllMetaModels() {
		Set<FlexoMetaModel> allMetaModels = new HashSet<FlexoMetaModel>();
		for (ModelSlotInstance instance : getModelSlotInstances()) {
			if (instance.getModelSlot() != null && instance.getModelSlot().getMetaModelResource() != null) {
				allMetaModels.add(instance.getModelSlot().getMetaModelResource().getMetaModelData());
			}
		}
		return allMetaModels;
	}

	public Set<FlexoModel<?, ?>> getAllModels() {
		Set<FlexoModel<?, ?>> allModels = new HashSet<FlexoModel<?, ?>>();
		for (ModelSlotInstance instance : getModelSlotInstances()) {
			if (instance.getModel() != null) {
				allModels.add(instance.getModel());
			}
		}
		return allModels;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final void delete() {
		// tests on this deleted object
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("delete: View " + getName());
		}
		if (getFlexoResource() != null) {
			getFlexoResource().delete();
		}

		if (getFlexoResource() != null) {
			getFlexoResource().delete();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("View " + getName() + " has no ViewDefinition associated!");
			}
		}

		super.delete();

		setChanged();
		notifyObservers(new ViewDeleted(this));
		deleteObservers();
	}

	public ViewLibrary getViewLibrary() {
		return getProject().getViewLibrary();
	}

	public RepositoryFolder<FlexoViewResource> getFolder() {
		if (getFlexoResource() != null) {
			return getViewLibrary().getParentFolder(getFlexoResource());
		}
		return null;
	}

}
