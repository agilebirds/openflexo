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
package org.openflexo.foundation.viewpoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.rm.ViewPointResourceImpl;
import org.openflexo.foundation.rm.VirtualModelResource;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.binding.EditionPatternBindingFactory;
import org.openflexo.foundation.viewpoint.dm.VirtualModelCreated;
import org.openflexo.foundation.viewpoint.dm.VirtualModelDeleted;
import org.openflexo.toolbox.ChainedCollection;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringEncoder;

/**
 * In the Openflexo Viewpoint Architecture a {@link ViewPoint} partitions the set preoccupations of the stakeholders so that issues related
 * to such preoccupation subsets can be addressed separately. Viewpoints provide the convention, rules and modelling technologies for
 * constructing, presenting and analysing Views. It can address one or several existing models or metamodels (until now, only ontologies,
 * but will be extended to other modelling languages). Openflexo Viewpoints can also federate models together (according to a particular
 * interpretation depending on the associated Viewpoint semantics) . Viewpoints also propose dedicated tools for presenting and manipulating
 * data in the particular context of some stakeholder’s preoccupations.
 * 
 * An Openflexo View is the instantiation of a particular Viewpoint with its own Objective relevant to some of the preoccupations of the
 * Viewpoint.
 * 
 * A Viewpoint addresses some preoccupations of the real world. A View is defined for a given objective and for a particular stakeholder or
 * observer.
 * 
 * A Viewpoint provides:
 * <ul>
 * <li>model extensions to model information relevant to a given context;</li>
 * <li>manipulation primitives (EditionSchemes) involving one or many models;</li>
 * <li>tools to create and edit models using model manipulation primitives;</li>
 * <li>tools to import existing models;</li>
 * <li>graphical representation of manipulated models, with dedicated graphical editors (diagrams, tabular and textual views).</li>
 * </ul>
 * 
 * @author sylvain
 * 
 */
public class ViewPoint extends NamedViewPointObject implements XMLStorageResourceData<ViewPoint> {

	private static final Logger logger = Logger.getLogger(ViewPoint.class.getPackage().getName());

	// TODO: We must find a better solution
	static {
		StringEncoder.getDefaultInstance()._addConverter(DataBinding.CONVERTER);
	}

	// private String viewPointURI;

	private LocalizedDictionary localizedDictionary;
	private ViewPointLibrary _library;
	// private List<ModelSlot> modelSlots;
	private List<VirtualModel> virtualModels;
	private ViewPointResource resource;
	private BindingModel bindingModel;
	private final EditionPatternBindingFactory bindingFactory = new EditionPatternBindingFactory(this);

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private final ChainedCollection<ViewPointObject> validableObjects = null;

	public static ViewPoint newViewPoint(String baseName, String viewpointURI, File viewpointDir, ViewPointLibrary library) {
		ViewPointResource vpRes = ViewPointResourceImpl.makeViewPointResource(baseName, viewpointURI, viewpointDir, library);
		ViewPoint viewpoint = new ViewPoint();
		vpRes.setResourceData(viewpoint);
		viewpoint.setResource(vpRes);
		// And register it to the library
		library.registerViewPoint(vpRes);

		viewpoint.init(baseName, library);
		viewpoint.loadViewpointMetaModels();
		viewpoint.save();
		return viewpoint;
	}

	private void loadViewpointMetaModels() {
		logger.warning("loadViewpointMetaModels() : not implemented yet");
	}

	public FlexoVersion getModelVersion() {
		return getResource().getModelVersion();
	}

	public void setModelVersion(FlexoVersion aVersion) {
	}

	// Used during deserialization, do not use it
	public ViewPoint(ViewPointBuilder builder) {
		super(builder);
		if (builder != null) {
			builder.setViewPoint(this);
			resource = builder.resource;
		}
		// modelSlots = new ArrayList<ModelSlot>();
		virtualModels = new ArrayList<VirtualModel>();
	}

	// Used during deserialization, do not use it
	private ViewPoint() {
		this(null);
	}

	public void init(String baseName, /* File viewpointDir, File xmlFile,*/ViewPointLibrary library/*, ViewPointFolder folder*/) {
		logger.info("Registering viewpoint " + baseName + " URI=" + getViewPointURI());

		setName(baseName);
		_library = library;

		loadViewpointMetaModels();

		/*for (VirtualModel vm : getVirtualModels()) {
			for (EditionPattern ep : vm.getEditionPatterns()) {
				for (PatternRole<?> pr : ep.getPatternRoles()) {
					if (pr instanceof ShapePatternRole) {
						((ShapePatternRole) pr).tryToFindAGR();
					}
				}
			}
		}*/
	}

	@Override
	public String getFullyQualifiedName() {
		return getURI();
	}

	/**
	 * Return the URI of the {@link ViewPoint}<br>
	 * The convention for URI are following: <viewpoint_uri>/<virtual_model_name>#<edition_pattern_name>.<edition_scheme_name> <br>
	 * eg<br>
	 * http://www.mydomain.org/MyViewPoint/MyVirtualModel#MyEditionPattern.MyEditionScheme
	 * 
	 * @return String representing unique URI of this object
	 */
	@Override
	public String getURI() {
		return getViewPointURI();
	}

	public String getViewPointURI() {
		if (getResource() != null) {
			return getResource().getURI();
		}
		return null;
	}

	public void setViewPointURI(String vpURI) {
		if (vpURI != null) {
			// We prevent ',' so that we can use it as a delimiter in tags.
			vpURI = vpURI.replace(",", "");
		}
		if (getResource() != null) {
			getResource().setURI(vpURI);
		}
	}

	@Override
	public String toString() {
		return "ViewPoint:" + getViewPointURI();
	}

	@Override
	public String getName() {
		if (getResource() != null) {
			return getResource().getName();
		}
		return super.getName();
	}

	@Override
	public void setName(String name) {
		if (requireChange(getName(), name)) {
			if (getResource() != null) {
				getResource().setName(name);
			} else {
				super.setName(name);
			}
		}
	}

	public FlexoVersion getVersion() {
		if (getResource() != null) {
			return getResource().getVersion();
		}
		return null;
	}

	public void setVersion(FlexoVersion aVersion) {
		if (requireChange(getVersion(), aVersion)) {
			if (getResource() != null) {
				getResource().setVersion(aVersion);
			}
		}
	}

	@Override
	public ViewPointLibrary getViewPointLibrary() {
		return _library;
	}

	@Override
	public ViewPoint getViewPoint() {
		return this;
	}

	private boolean isLoading = false;

	/**
	 * Load eventually unloaded VirtualModels<br>
	 * After this call return, we can assert that all {@link VirtualModel} are loaded.
	 */
	private synchronized void loadVirtualModelsWhenUnloaded() {
		if (isLoading) {
			return;
		}
		isLoading = true;
		for (org.openflexo.foundation.resource.FlexoResource<?> r : getResource().getContents()) {
			if (r instanceof VirtualModelResource) {
				((VirtualModelResource) r).getVirtualModel();
			}
		}
		isLoading = false;
	}

	/**
	 * Return all VirtualModel of a given class.<br>
	 * If onlyFinalInstances is set to true, only instances of supplied class (and not specialized classes) are retrieved
	 * 
	 * @return
	 */
	public <VM extends VirtualModel> List<VM> getVirtualModels(Class<VM> virtualModelClass, boolean onlyFinalInstances) {
		List<VM> returned = new ArrayList<VM>();
		for (VirtualModel vm : getVirtualModels()) {
			if (onlyFinalInstances) {
				if (virtualModelClass.equals(vm.getClass())) {
					returned.add((VM) vm);
				}
			} else {
				if (virtualModelClass.isAssignableFrom(vm.getClass())) {
					returned.add((VM) vm);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all "plain" {@link VirtualModel} defined in this {@link ViewPoint} (does NOT return subclasses of {@link VirtualModel})
	 * 
	 * @return
	 */
	public List<VirtualModel> getPlainVirtualModels() {
		return getVirtualModels(VirtualModel.class, true);
	}

	/**
	 * Return all {@link DiagramSpecification} defined in this {@link ViewPoint}
	 * 
	 * @return
	 */
	/*public List<DiagramSpecification> getDiagramSpecifications() {
		return getVirtualModels(DiagramSpecification.class, true);
	}*/

	/**
	 * Return all {@link VirtualModel} defined in this {@link ViewPoint}
	 * 
	 * @return
	 */
	public List<VirtualModel> getVirtualModels() {
		loadVirtualModelsWhenUnloaded();
		return virtualModels;
	}

	public void setVirtualModels(Vector<VirtualModel> virtualModels) {
		loadVirtualModelsWhenUnloaded();
		this.virtualModels = virtualModels;
	}

	public void addToVirtualModels(VirtualModel virtualModel) {
		loadVirtualModelsWhenUnloaded();
		virtualModel.setViewPoint(this);
		virtualModels.add(virtualModel);
		setChanged();
		notifyObservers(new VirtualModelCreated(virtualModel));
	}

	public void removeFromVirtualModels(VirtualModel virtualModel) {
		loadVirtualModelsWhenUnloaded();
		virtualModel.setViewPoint(null);
		virtualModels.remove(virtualModel);
		setChanged();
		notifyObservers(new VirtualModelDeleted(virtualModel));
	}

	/**
	 * Return {@link VirtualModel} with supplied name or URI
	 * 
	 * @return
	 */
	public VirtualModel getVirtualModelNamed(String virtualModelName) {
		loadVirtualModelsWhenUnloaded();
		for (VirtualModel vm : getVirtualModels()) {
			if (vm.getName().equals(virtualModelName)) {
				return vm;
			}
			if (vm.getURI().equals(virtualModelName)) {
				return vm;
			}
		}
		return null;
	}

	/**
	 * Return {@link DiagramSpecification} with supplied name or URI
	 * 
	 * @return
	 */
	/*public DiagramSpecification getDiagramSpecificationNamed(String diagramSpecificationName) {
		loadVirtualModelsWhenUnloaded();
		for (VirtualModel vm : getVirtualModels()) {
			if (vm instanceof DiagramSpecification) {
				if (vm.getName().equals(diagramSpecificationName)) {
					return (DiagramSpecification) vm;
				}
				if (vm.getURI().equals(diagramSpecificationName)) {
					return (DiagramSpecification) vm;
				}
			}
		}
		return null;
	}*/

	/**
	 * Return EditionPattern matching supplied id represented as a string, which could be either the name of EditionPattern, or its URI
	 * 
	 * @param editionPatternId
	 * @return
	 */
	public EditionPattern getEditionPattern(String editionPatternId) {
		for (VirtualModel vm : getVirtualModels()) {
			EditionPattern returned = vm.getEditionPattern(editionPatternId);
			if (returned != null) {
				return returned;
			}
		}
		// logger.warning("Not found EditionPattern:" + editionPatternId);
		return null;
	}

	// Return a default diagram specification (temporary hack to ensure compatibility with old versions)
	/*@Deprecated
	public DiagramSpecification getDefaultDiagramSpecification() {
		loadVirtualModelsWhenUnloaded();
		for (VirtualModel vm : getVirtualModels()) {
			if (vm instanceof DiagramSpecification) {
				return (DiagramSpecification) vm;
			}
		}
		// OK, lets create a default one
		// DiagramSpecification ds = new DiagramSpecification(this);
		// addToVirtualModels(ds);
		return null;
	}*/

	@Override
	public LocalizedDictionary getLocalizedDictionary() {
		if (localizedDictionary == null) {
			localizedDictionary = new LocalizedDictionary((ViewPointBuilder) null);
			localizedDictionary.setOwner(this);
		}
		return localizedDictionary;
	}

	public void setLocalizedDictionary(LocalizedDictionary localizedDictionary) {
		localizedDictionary.setOwner(this);
		this.localizedDictionary = localizedDictionary;
	}

	@Deprecated
	public static String findOntologyImports(File aFile) {
		Document document;
		try {
			logger.fine("Try to find URI for " + aFile);
			document = FlexoXMLFileResourceImpl.readXMLFile(aFile);
			Element root = FlexoXMLFileResourceImpl.getElement(document, "Ontology");
			if (root != null) {
				Element importElement = FlexoXMLFileResourceImpl.getElement(document, "imports");
				if (importElement != null) {
					Iterator it = importElement.getAttributes().iterator();
					while (it.hasNext()) {
						Attribute at = (Attribute) it.next();
						if (at.getName().equals("resource")) {
							// System.out.println("Returned " + at.getValue());
							String returned = at.getValue();
							if (StringUtils.isNotEmpty(returned)) {
								return returned;
							}
						}
					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.fine("Returned null");
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		if (bindingModel == null) {
			createBindingModel();
		}
		return bindingModel;
	}

	public void updateBindingModel() {
		logger.fine("updateBindingModel()");
		bindingModel = null;
		createBindingModel();
	}

	private void createBindingModel() {
		bindingModel = new BindingModel();
		for (VirtualModel vm : getVirtualModels()) {
			bindingModel.addToBindingVariables(new BindingVariable(vm.getName(), Object.class));
		}
	}

	@Override
	public EditionPatternBindingFactory getBindingFactory() {
		return bindingFactory;
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		return ViewPointLibrary.VALIDATION_MODEL;
	}

	// ==========================================================================
	// ============================== Model Slots ===============================
	// ==========================================================================

	/*public void setModelSlots(List<ModelSlot> modelSlots) {
		this.modelSlots = modelSlots;
	}

	public List<ModelSlot> getModelSlots() {
		return modelSlots;
	}

	public void addToModelSlots(ModelSlot modelSlot) {
		modelSlots.add(modelSlot);
		modelSlot.setViewPoint(this);
		setChanged();
		notifyObservers(new ModelSlotAdded(modelSlot, this));
	}

	public void removeFromModelSlots(ModelSlot modelSlot) {
		modelSlots.remove(modelSlot);
		modelSlot.setViewPoint(null);
		setChanged();
		notifyObservers(new ModelSlotRemoved(modelSlot, this));
	}

	public <MS extends ModelSlot> List<MS> getModelSlots(Class<MS> msType) {
		List<MS> returned = new ArrayList<MS>();
		for (ModelSlot ms : getModelSlots()) {
			if (TypeUtils.isTypeAssignableFrom(msType, ms.getClass())) {
				returned.add((MS) ms);
			}
		}
		return returned;
	}

	public ModelSlot getModelSlot(String modelSlotName) {
		for (ModelSlot ms : getModelSlots()) {
			if (ms.getName().equals(modelSlotName)) {
				return ms;
			}
		}
		return null;
	}

	public List<ModelSlot> getRequiredModelSlots() {
		List<ModelSlot> requiredModelSlots = new ArrayList<ModelSlot>();
		for (ModelSlot modelSlot : getModelSlots()) {
			if (modelSlot.getIsRequired()) {
				requiredModelSlots.add(modelSlot);
			}
		}
		return modelSlots;
	}*/

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		// Voir du cote de GeneratorFormatter pour formatter tout ca
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);

		/*for (FlexoMetaModelResource<?, ?> mm : getAllMetaModels()) {
			out.append("import " + mm.getURI() + ";", context);
			out.append(StringUtils.LINE_SEPARATOR, context);
		}*/

		out.append("ViewDefinition " + getName() + " uri=\"" + getURI() + "\"", context);
		out.append(" {" + StringUtils.LINE_SEPARATOR, context);

		/*for (ModelSlot modelSlot : getModelSlots()) {
			// if (modelSlot.getMetaModelResource() != null) {
			out.append(modelSlot.getFMLRepresentation(context), context);
			// }
		}*/

		out.append(StringUtils.LINE_SEPARATOR, context);
		for (VirtualModel vm : getVirtualModels()) {
			out.append(vm.getFMLRepresentation(context), context, 1);
			out.append(StringUtils.LINE_SEPARATOR, context, 1);
		}
		out.append("}" + StringUtils.LINE_SEPARATOR, context);
		return out.toString();
	}

	@Override
	public Collection<VirtualModel> getEmbeddedValidableObjects() {
		return getVirtualModels();
	}

	// Implementation of XMLStorageResourceData

	@Override
	public FlexoStorageResource<ViewPoint> getFlexoResource() {
		return (FlexoStorageResource<ViewPoint>) getResource();
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		setResource((ViewPointResource) resource);
	}

	@Override
	public ViewPointResource getResource() {
		return resource;
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<ViewPoint> resource) {
		this.resource = (ViewPointResource) resource;
	}

	@Override
	public ViewPointResource getFlexoXMLFileResource() {
		return getResource();
	}

	@Override
	public void save() {
		logger.info("Saving ViewPoint to " + getResource().getFile().getAbsolutePath() + "...");

		try {
			getResource().save(null);
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean delete() {

		logger.info("Deleting ViewPoint " + this);

		// Unregister the viewpoint resource from the viewpoint library
		if (getResource() != null && getViewPointLibrary() != null) {
			getViewPointLibrary().unregisterViewPoint(getResource());
		}

		// Delete viewpoint
		super.delete();

		// Delete observers
		deleteObservers();

		return true;
	}

	/**
	 * This is the builder used to deserialize VirtualModel objects.
	 * 
	 * @author sylvain
	 * 
	 */
	public static class ViewPointBuilder {
		private ViewPoint viewPoint;
		private FlexoVersion modelVersion;
		private final ViewPointLibrary viewPointLibrary;
		ViewPointResource resource;

		public ViewPointBuilder(ViewPointLibrary vpLibrary, ViewPointResource resource) {
			this.viewPointLibrary = vpLibrary;
			this.resource = resource;
		}

		public ViewPointBuilder(ViewPointLibrary vpLibrary, ViewPoint viewPoint) {
			this.viewPoint = viewPoint;
			this.viewPointLibrary = vpLibrary;
			this.resource = viewPoint.getResource();
		}

		public ViewPointBuilder(ViewPointLibrary vpLibrary, ViewPointResource resource, FlexoVersion modelVersion) {
			this.modelVersion = modelVersion;
			this.viewPointLibrary = vpLibrary;
			this.resource = resource;
		}

		public ViewPointLibrary getViewPointLibrary() {
			return viewPointLibrary;
		}

		public FlexoVersion getModelVersion() {
			return modelVersion;
		}

		public ViewPoint getViewPoint() {
			return viewPoint;
		}

		public void setViewPoint(ViewPoint viewPoint) {
			this.viewPoint = viewPoint;
		}
	}

}
