package org.openflexo.foundation.technologyadapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPatternPatternRole;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.FlexoModelObjectPatternRole;
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.PrimitivePatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPointObject.LanguageRepresentationContext.LanguageRepresentationOutput;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.toolbox.StringUtils;

/**
 * A model slot is a named object providing symbolic access to a model conform to a meta-model (see {@link FlexoMetaModel}). <br>
 * It is defined at viewpoint level. <br>
 * A view (viewpoint instance) binds used slots to their models within the project.
 * 
 * @param <M>
 *            Type of {@link FlexoModel} handled by this ModelSlot
 * @param <MM>
 *            {@link FlexoMetaModel} which model handled by this ModelSlot is conform to
 * 
 * @author Luka Le Roux, Sylvain Guerin
 * @see org.openflexo.foundation.viewpoint.ViewPoint
 * @see org.openflexo.foundation.view.View
 * */
public abstract class ModelSlot<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends NamedViewPointObject {

	private static final Logger logger = Logger.getLogger(ModelSlot.class.getPackage().getName());

	private boolean isRequired;
	private boolean isReadOnly;
	private FlexoMetaModelResource<M, MM> metaModelResource;
	private String metaModelURI;
	private TechnologyAdapter<M, MM> technologyAdapter;
	private ViewPoint viewPoint;
	private VirtualModel<?> virtualModel;

	private List<Class<? extends PatternRole>> availablePatternRoleTypes;
	private List<Class<? extends EditionAction>> availableEditionActionTypes;
	private List<Class<? extends EditionAction>> availableFetchRequestActionTypes;

	protected ModelSlot(ViewPoint viewPoint, TechnologyAdapter<M, MM> technologyAdapter) {
		super((VirtualModel.VirtualModelBuilder) null);
		this.viewPoint = viewPoint;
		this.technologyAdapter = technologyAdapter;
	}

	protected ModelSlot(VirtualModel<?> virtualModel, TechnologyAdapter<M, MM> technologyAdapter) {
		super((VirtualModel.VirtualModelBuilder) null);
		this.virtualModel = virtualModel;
		this.viewPoint = virtualModel.getViewPoint();
		this.technologyAdapter = technologyAdapter;
	}

	protected ModelSlot(VirtualModelBuilder builder) {
		super(builder);

		if (builder != null) {
			this.viewPoint = builder.getViewPoint();
			if (builder.getViewPointLibrary() != null && builder.getViewPointLibrary().getServiceManager() != null
					&& builder.getViewPointLibrary().getServiceManager().getTechnologyAdapterService() != null) {
				this.technologyAdapter = builder.getViewPointLibrary().getServiceManager().getTechnologyAdapterService()
						.getTechnologyAdapter(getTechnologyAdapterClass());
			}
		}
	}

	public ModelSlot(ViewPointBuilder builder) {
		super(builder);

		if (builder != null) {
			this.viewPoint = builder.getViewPoint();
			if (builder.getViewPointLibrary() != null && builder.getViewPointLibrary().getServiceManager() != null
					&& builder.getViewPointLibrary().getServiceManager().getTechnologyAdapterService() != null) {
				this.technologyAdapter = builder.getViewPointLibrary().getServiceManager().getTechnologyAdapterService()
						.getTechnologyAdapter(getTechnologyAdapterClass());
			}
		}
	}

	@Override
	public String getURI() {
		if (getVirtualModel() != null) {
			return getVirtualModel().getURI() + "." + getName();
		}
		return null;
	}

	/**
	 * Creates and return a new {@link PatternRole} of supplied class.<br>
	 * This responsability is delegated to the technology-specific {@link ModelSlot} which manages with introspection its own
	 * {@link PatternRole} types
	 * 
	 * @param patternRoleClass
	 * @return
	 */
	public abstract <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass);

	/**
	 * Return default name for supplied pattern role class
	 * 
	 * @param patternRoleClass
	 * @return
	 */
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		if (EditionPatternPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "editionPattern";
		} else if (FlexoModelObjectPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "modelObject";
		} else if (PrimitivePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "primitive";
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return "???";
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	public VirtualModel<?> getVirtualModel() {
		return virtualModel;
	}

	public void setVirtualModel(VirtualModel<?> virtualModel) {
		this.virtualModel = virtualModel;
	}

	@Override
	public ViewPoint getViewPoint() {
		if (getVirtualModel() != null) {
			return getVirtualModel().getViewPoint();
		}
		return viewPoint;
	}

	public void setViewPoint(ViewPoint viewPoint) {
		this.viewPoint = viewPoint;
	}

	public final FlexoResource<M> createProjectSpecificEmptyModel(View view, String filename, String modelUri,
			FlexoResource<MM> metaModelResource) {
		return getTechnologyAdapter().createEmptyModel(view.getProject(), filename, modelUri, metaModelResource,
				technologyAdapter.getTechnologyContextManager());
	};

	public final FlexoResource<M> createSharedEmptyModel(FlexoResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoResource<MM> metaModelResource) {
		if (resourceCenter instanceof FileSystemBasedResourceCenter) {
			return getTechnologyAdapter().createEmptyModel((FileSystemBasedResourceCenter) resourceCenter, relativePath, filename,
					modelUri, metaModelResource, technologyAdapter.getTechnologyContextManager());
		} else {
			logger.warning("Cannot create shared model in a non file-system-based resource center");
			return null;
		}
	};

	/**
	 * Instantiate new action of required type<br>
	 * Default implementation. Override when required.
	 * 
	 * @param actionClass
	 * @return
	 */
	public <A extends EditionAction<M, MM, ?>> A createAction(Class<A> actionClass) {
		Class[] constructorParams = new Class[1];
		constructorParams[0] = VirtualModel.VirtualModelBuilder.class;
		try {
			Constructor<A> c = actionClass.getConstructor(constructorParams);
			return c.newInstance(null);
		} catch (SecurityException e) {
			logger.warning("Unexpected SecurityException " + e);
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			logger.warning("Unexpected NoSuchMethodException " + e);
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			logger.warning("Unexpected IllegalArgumentException " + e);
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			logger.warning("Unexpected InstantiationException " + e);
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			logger.warning("Unexpected InvocationTargetException " + e);
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			logger.warning("Unexpected InvocationTargetException " + e);
			e.printStackTrace();
			return null;
		}
	}

	public FlexoMetaModelResource<M, MM> getMetaModelResource() {
		if (metaModelResource == null && StringUtils.isNotEmpty(metaModelURI) && getInformationSpace() != null) {
			metaModelResource = (FlexoMetaModelResource<M, MM>) getInformationSpace().getMetaModelWithURI(metaModelURI);
			logger.info("Looked-up " + metaModelResource);
		}
		// Temporary hack to lookup parent slot (to be refactored)
		if (metaModelResource == null && getVirtualModel() != null && getViewPoint() != null) {
			if (getViewPoint().getModelSlot(getName()) != null) {
				return (FlexoMetaModelResource<M, MM>) getViewPoint().getModelSlot(getName()).getMetaModelResource();
			}
		}
		return metaModelResource;
	}

	public void setMetaModelResource(FlexoMetaModelResource<M, MM> metaModelResource) {
		this.metaModelResource = metaModelResource;
	}

	public String getMetaModelURI() {
		if (metaModelResource != null) {
			return metaModelResource.getURI();
		}
		return metaModelURI;
	}

	public void setMetaModelURI(String metaModelURI) {
		this.metaModelURI = metaModelURI;
	}

	public boolean getIsReadOnly() {
		return isReadOnly;
	}

	public void setIsReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public BindingModel getBindingModel() {
		return viewPoint.getBindingModel();
	}

	@Override
	public String getLanguageRepresentation(LanguageRepresentationContext context) {
		// Voir du cote de GeneratorFormatter pour formatter tout ca
		LanguageRepresentationOutput out = new LanguageRepresentationOutput(context);
		out.append("ModelSlot " + getName() + " type=" + getClass().getSimpleName() + " conformTo=\"" + getMetaModelURI() + "\""
				+ " required=" + getIsRequired() + " readOnly=" + getIsReadOnly() + ";");
		return out.toString();
	}

	public final TechnologyAdapter<M, MM> getTechnologyAdapter() {
		return technologyAdapter;
	}

	public abstract Class<? extends TechnologyAdapter<M, MM>> getTechnologyAdapterClass();

	@Deprecated
	public abstract BindingVariable makePatternRolePathElement(PatternRole<?> pr, Bindable container);

	/*public static BindingVariable<?> makePatternRolePathElement2(PatternRole pr, Bindable container) {
		if (pr instanceof OntologicObjectPatternRole) {
			if (pr instanceof ClassPatternRole) {
				return new OntologicClassPatternRolePathElement((ClassPatternRole) pr, container);
			} else if (pr instanceof IndividualPatternRole) {
				return new OntologicIndividualPatternRolePathElement((IndividualPatternRole) pr, container);
			} else if (pr instanceof ObjectPropertyPatternRole) {
				return new OntologicObjectPropertyPatternRolePathElement((ObjectPropertyPatternRole) pr, container);
			} else if (pr instanceof DataPropertyPatternRole) {
				return new OntologicDataPropertyPatternRolePathElement((DataPropertyPatternRole) pr, container);
			} else if (pr instanceof PropertyPatternRole) {
				return new OntologicPropertyPatternRolePathElement((PropertyPatternRole) pr, container);
			} else if (pr instanceof DataPropertyPatternRole) {
				return new OntologicDataPropertyPatternRolePathElement((DataPropertyPatternRole) pr, container);
			} else {
				logger.warning("Unexpected " + pr);
				return null;
			}
		} else if (pr instanceof EditionPatternPatternRole) {
			return new EditionPatternPathElement(pr.getPatternRoleName(), ((EditionPatternPatternRole) pr).getEditionPatternType(),
					container);
		} else {
			return null;
		}
	}*/

	public List<Class<? extends PatternRole>> getAvailablePatternRoleTypes() {
		if (availablePatternRoleTypes == null) {
			availablePatternRoleTypes = computeAvailablePatternRoleTypes();
		}
		return availablePatternRoleTypes;
	}

	private List<Class<? extends PatternRole>> computeAvailablePatternRoleTypes() {
		availablePatternRoleTypes = new ArrayList<Class<? extends PatternRole>>();
		Class<?> cl = getClass();
		if (cl.isAnnotationPresent(DeclarePatternRoles.class)) {
			DeclarePatternRoles allPatternRoles = cl.getAnnotation(DeclarePatternRoles.class);
			for (DeclarePatternRole patternRoleDeclaration : allPatternRoles.value()) {
				availablePatternRoleTypes.add(patternRoleDeclaration.value());
			}
		}
		// availablePatternRoleTypes.add(EditionPatternPatternRole.class);
		// availablePatternRoleTypes.add(FlexoModelObjectPatternRole.class);
		// availablePatternRoleTypes.add(PrimitivePatternRole.class);
		return availablePatternRoleTypes;
	}

	public List<Class<? extends EditionAction>> getAvailableEditionActionTypes() {
		if (availableEditionActionTypes == null) {
			availableEditionActionTypes = computeAvailableEditionActionTypes();
		}
		return availableEditionActionTypes;
	}

	private List<Class<? extends EditionAction>> computeAvailableEditionActionTypes() {
		availableEditionActionTypes = new ArrayList<Class<? extends EditionAction>>();
		Class<?> cl = getClass();
		if (cl.isAnnotationPresent(DeclareEditionActions.class)) {
			DeclareEditionActions allEditionActions = cl.getAnnotation(DeclareEditionActions.class);
			for (DeclareEditionAction patternRoleDeclaration : allEditionActions.value()) {
				availableEditionActionTypes.add(patternRoleDeclaration.value());
			}
		}
		return availableEditionActionTypes;
	}

	public List<Class<? extends EditionAction>> getAvailableFetchRequestActionTypes() {
		if (availableFetchRequestActionTypes == null) {
			availableFetchRequestActionTypes = computeAvailableFetchRequestActionTypes();
		}
		return availableFetchRequestActionTypes;
	}

	private List<Class<? extends EditionAction>> computeAvailableFetchRequestActionTypes() {
		availableFetchRequestActionTypes = new ArrayList<Class<? extends EditionAction>>();
		Class<?> cl = getClass();
		if (cl.isAnnotationPresent(DeclareFetchRequests.class)) {
			DeclareFetchRequests allFetchRequestActions = cl.getAnnotation(DeclareFetchRequests.class);
			for (DeclareFetchRequest fetchRequestDeclaration : allFetchRequestActions.value()) {
				availableFetchRequestActionTypes.add(fetchRequestDeclaration.value());
			}
		}
		return availableFetchRequestActionTypes;
	}

	/**
	 * Creates and return a new {@link EditionAction} of supplied class.<br>
	 * This responsability is delegated to the technology-specific {@link ModelSlot} which manages with introspection its own
	 * {@link EditionAction} types
	 * 
	 * @param editionActionClass
	 * @return
	 */
	public abstract <EA extends EditionAction<?, ?, ?>> EA makeEditionAction(Class<EA> editionActionClass);

	/**
	 * Creates and return a new {@link FetchRequest} of supplied class.<br>
	 * This responsability is delegated to the technology-specific {@link ModelSlot} which manages with introspection its own
	 * {@link FetchRequest} types
	 * 
	 * @param fetchRequestClass
	 * @return
	 */
	public abstract <FR extends FetchRequest<?, ?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass);

	public abstract ModelSlotInstanceConfiguration<? extends ModelSlot<M, MM>> createConfiguration(CreateVirtualModelInstance<?> action);

}
