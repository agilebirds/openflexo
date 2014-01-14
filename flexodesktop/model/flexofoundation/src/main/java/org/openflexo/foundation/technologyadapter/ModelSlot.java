/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
 *
 * This file is part of Openflexo.
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

package org.openflexo.foundation.technologyadapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPatternInstancePatternRole;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.PrimitivePatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

/**
 * A model slot is a named object providing access to a particular data encoded in a given technology A model slot should be seen as a
 * connector.<br>
 * A model slot formalize a contract for accessing to a data
 * 
 * It is defined at viewpoint level. <br>
 * A {@link ModelSlotInstance} binds used slots to some data within the project.
 * 
 * @param <RD>
 *            Type of resource data handled by this ModelSlot
 * 
 * @author Sylvain Guerin
 * @see org.openflexo.foundation.viewpoint.ViewPoint
 * @see org.openflexo.foundation.view.View
 * @see org.openflexo.foundation.view.ModelSlotInstance
 * */
@ModelEntity(isAbstract = true)
@ImplementationClass(ModelSlot.ModelSlotImpl.class)
public interface ModelSlot<RD extends ResourceData<RD>> extends NamedViewPointObject {

	@PropertyIdentifier(type = VirtualModel.class)
	public static final String VIRTUAL_MODEL_KEY = "virtualModel";

	@PropertyIdentifier(type = boolean.class)
	public static final String IS_REQUIRED_KEY = "isRequired";
	@PropertyIdentifier(type = boolean.class)
	public static final String IS_READ_ONLY_KEY = "isReadOnly";

	@Getter(value = VIRTUAL_MODEL_KEY, inverse = VirtualModel.MODEL_SLOTS_KEY)
	public VirtualModel getVirtualModel();

	@Setter(VIRTUAL_MODEL_KEY)
	public void setVirtualModel(VirtualModel virtualModel);

	@Getter(value = IS_REQUIRED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsRequired();

	@Setter(IS_REQUIRED_KEY)
	public void setIsRequired(boolean isRequired);

	@Getter(value = IS_READ_ONLY_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsReadOnly();

	@Setter(IS_READ_ONLY_KEY)
	public void setIsReadOnly(boolean isReadOnly);

	public static abstract class ModelSlotImpl<RD extends ResourceData<RD>> extends NamedViewPointObjectImpl implements ModelSlot<RD> {

		private static final Logger logger = Logger.getLogger(ModelSlot.class.getPackage().getName());

		private boolean isRequired;
		private boolean isReadOnly;
		private TechnologyAdapter technologyAdapter;
		private ViewPoint viewPoint;
		private VirtualModel virtualModel;

		private List<Class<? extends PatternRole<?>>> availablePatternRoleTypes;
		private List<Class<? extends EditionAction<?, ?>>> availableEditionActionTypes;
		private List<Class<? extends EditionAction<?, ?>>> availableFetchRequestActionTypes;

		/*protected ModelSlot(ViewPoint viewPoint, TechnologyAdapter technologyAdapter) {
		super((VirtualModel.VirtualModelBuilder) null);
		this.viewPoint = viewPoint;
		this.technologyAdapter = technologyAdapter;
		}*/

		protected ModelSlotImpl(VirtualModel virtualModel, TechnologyAdapter technologyAdapter) {
			super();
			this.virtualModel = virtualModel;
			this.viewPoint = virtualModel.getViewPoint();
			this.technologyAdapter = technologyAdapter;
		}

		protected ModelSlotImpl() {
			super();

			/*if (builder != null) {
			this.viewPoint = builder.getViewPoint();
			if (builder.getViewPointLibrary() != null && builder.getViewPointLibrary().getServiceManager() != null
					&& builder.getViewPointLibrary().getServiceManager().getTechnologyAdapterService() != null) {
				this.technologyAdapter = builder.getViewPointLibrary().getServiceManager().getTechnologyAdapterService()
						.getTechnologyAdapter(getTechnologyAdapterClass());
			}
			}*/
		}

		/*public ModelSlotImpl(ViewPointBuilder builder) {
		super(builder);

		if (builder != null) {
			this.viewPoint = builder.getViewPoint();
			if (builder.getViewPointLibrary() != null && builder.getViewPointLibrary().getServiceManager() != null
					&& builder.getViewPointLibrary().getServiceManager().getTechnologyAdapterService() != null) {
				this.technologyAdapter = builder.getViewPointLibrary().getServiceManager().getTechnologyAdapterService()
						.getTechnologyAdapter(getTechnologyAdapterClass());
			}
		}
		}*/

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
			if (EditionPatternInstancePatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "editionPattern";
			} else if (PrimitivePatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "primitive";
			}
			logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
			return "???";
		}

		@Override
		public VirtualModel getVirtualModel() {
			return virtualModel;
		}

		@Override
		public void setVirtualModel(VirtualModel virtualModel) {
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

		public abstract Type getType();

		/**
		 * Instantiate new action of required type<br>
		 * Default implementation. Override when required.
		 * 
		 * @param actionClass
		 * @return
		 */
		public <A extends EditionAction<?, ?>> A createAction(Class<A> actionClass) {
			Class[] constructorParams = new Class[0];
			// constructorParams[0] = VirtualModel.VirtualModelBuilder.class;
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

		@Override
		public boolean getIsReadOnly() {
			return isReadOnly;
		}

		@Override
		public void setIsReadOnly(boolean isReadOnly) {
			this.isReadOnly = isReadOnly;
		}

		@Override
		public boolean getIsRequired() {
			return isRequired;
		}

		@Override
		public void setIsRequired(boolean isRequired) {
			this.isRequired = isRequired;
		}

		@Override
		public BindingModel getBindingModel() {
			return viewPoint.getBindingModel();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append("ModelSlot " + getName() + " type=" + getClass().getSimpleName() + "\"" + " required=" + getIsRequired()
					+ " readOnly=" + getIsReadOnly() + ";", context);
			return out.toString();
		}

		public TechnologyAdapter getTechnologyAdapter() {
			return technologyAdapter;
		}

		public abstract Class<? extends TechnologyAdapter> getTechnologyAdapterClass();

		public List<Class<? extends PatternRole<?>>> getAvailablePatternRoleTypes() {
			if (availablePatternRoleTypes == null) {
				availablePatternRoleTypes = computeAvailablePatternRoleTypes();
			}
			return availablePatternRoleTypes;
		}

		private List<Class<? extends PatternRole<?>>> computeAvailablePatternRoleTypes() {
			availablePatternRoleTypes = new ArrayList<Class<? extends PatternRole<?>>>();
			Class<?> cl = getClass();
			if (cl.isAnnotationPresent(DeclarePatternRoles.class)) {
				DeclarePatternRoles allPatternRoles = cl.getAnnotation(DeclarePatternRoles.class);
				for (DeclarePatternRole patternRoleDeclaration : allPatternRoles.value()) {
					availablePatternRoleTypes.add(patternRoleDeclaration.patternRoleClass());
				}
			}
			// availablePatternRoleTypes.add(EditionPatternPatternRole.class);
			// availablePatternRoleTypes.add(FlexoModelObjectPatternRole.class);
			// availablePatternRoleTypes.add(PrimitivePatternRole.class);
			return availablePatternRoleTypes;
		}

		public List<Class<? extends EditionAction<?, ?>>> getAvailableEditionActionTypes() {
			if (availableEditionActionTypes == null) {
				availableEditionActionTypes = computeAvailableEditionActionTypes();
			}
			return availableEditionActionTypes;
		}

		private List<Class<? extends EditionAction<?, ?>>> computeAvailableEditionActionTypes() {
			availableEditionActionTypes = new ArrayList<Class<? extends EditionAction<?, ?>>>();
			Class<?> cl = getClass();
			if (cl.isAnnotationPresent(DeclareEditionActions.class)) {
				DeclareEditionActions allEditionActions = cl.getAnnotation(DeclareEditionActions.class);
				for (DeclareEditionAction patternRoleDeclaration : allEditionActions.value()) {
					availableEditionActionTypes.add(patternRoleDeclaration.editionActionClass());
				}
			}
			return availableEditionActionTypes;
		}

		public List<Class<? extends EditionAction<?, ?>>> getAvailableFetchRequestActionTypes() {
			if (availableFetchRequestActionTypes == null) {
				availableFetchRequestActionTypes = computeAvailableFetchRequestActionTypes();
			}
			return availableFetchRequestActionTypes;
		}

		private List<Class<? extends EditionAction<?, ?>>> computeAvailableFetchRequestActionTypes() {
			availableFetchRequestActionTypes = new ArrayList<Class<? extends EditionAction<?, ?>>>();
			Class<?> cl = getClass();
			if (cl.isAnnotationPresent(DeclareFetchRequests.class)) {
				DeclareFetchRequests allFetchRequestActions = cl.getAnnotation(DeclareFetchRequests.class);
				for (DeclareFetchRequest fetchRequestDeclaration : allFetchRequestActions.value()) {
					availableFetchRequestActionTypes.add(fetchRequestDeclaration.fetchRequestClass());
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
		public abstract <EA extends EditionAction<?, ?>> EA makeEditionAction(Class<EA> editionActionClass);

		/**
		 * Creates and return a new {@link FetchRequest} of supplied class.<br>
		 * This responsability is delegated to the technology-specific {@link ModelSlot} which manages with introspection its own
		 * {@link FetchRequest} types
		 * 
		 * @param fetchRequestClass
		 * @return
		 */
		public abstract <FR extends FetchRequest<?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass);

		public abstract ModelSlotInstanceConfiguration<? extends ModelSlot<RD>, RD> createConfiguration(CreateVirtualModelInstance<?> action);

		/**
		 * A Model Slot is responsible for URI mapping
		 * 
		 * @param msInstance
		 * @param o
		 * @return URI as String
		 */

		public abstract String getURIForObject(ModelSlotInstance<? extends ModelSlot<RD>, RD> msInstance, Object o);

		/**
		 * A Model Slot is responsible for URI mapping
		 * 
		 * @param msInstance
		 * @param objectURI
		 * @return the Object
		 */

		public abstract Object retrieveObjectWithURI(ModelSlotInstance<? extends ModelSlot<RD>, RD> msInstance, String objectURI);

		/**
		 * Return first found class matching supplied class.<br>
		 * Returned class is generally the specialized class related to a particular technology
		 * 
		 * @param patternRoleClass
		 * @return
		 */
		public <PR extends PatternRole<?>> Class<? extends PR> getPatternRoleClass(Class<PR> patternRoleClass) {
			for (Class<?> patternRoleType : getAvailablePatternRoleTypes()) {
				if (patternRoleClass.isAssignableFrom(patternRoleType)) {
					return (Class<? extends PR>) patternRoleType;
				}
			}
			return null;
		}

		/**
		 * Return first found class matching supplied class.<br>
		 * Returned class is generally the specialized class related to a particular technology
		 * 
		 * @param patternRoleClass
		 * @return
		 */
		public <EA extends EditionAction> Class<? extends EA> getEditionActionClass(Class<EA> editionActionClass) {
			for (Class editionActionType : getAvailableEditionActionTypes()) {
				if (editionActionClass.isAssignableFrom(editionActionType)) {
					return editionActionType;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + getName();
		}

		public String getModelSlotDescription() {
			return getTechnologyAdapter().getName();
		}

		@Override
		public Object performSuperGetter(String propertyIdentifier) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void performSuperSetter(String propertyIdentifier, Object value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperAdder(String propertyIdentifier, Object value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperRemover(String propertyIdentifier, Object value) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object performSuperGetter(String propertyIdentifier, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void performSuperSetter(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperAdder(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperRemover(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperSetModified(boolean modified) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object performSuperFinder(String finderIdentifier, Object value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object performSuperFinder(String finderIdentifier, Object value, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isSerializing() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isDeserializing() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setModified(boolean modified) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean equalsObject(Object obj) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean hasKey(String key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean performSuperDelete(Object... context) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean performSuperUndelete() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void performSuperDelete(Class<?> modelEntityInterface, Object... context) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean delete(Object... context) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean undelete() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object cloneObject() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object cloneObject(Object... context) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isCreatedByCloning() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isBeingCloned() {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
