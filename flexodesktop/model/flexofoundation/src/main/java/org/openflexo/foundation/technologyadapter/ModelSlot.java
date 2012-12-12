package org.openflexo.foundation.technologyadapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPatternPatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.PropertyPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicClassPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicDataPropertyPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicIndividualPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicObjectPropertyPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicPropertyPatternRolePathElement;

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
 * @see org.openflexo.foundation.view.diagram.model.View
 * */
public abstract class ModelSlot<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends NamedViewPointObject {

	private static final Logger logger = Logger.getLogger(ModelSlot.class.getPackage().getName());

	private boolean isRequired;
	private boolean isReadOnly;
	private MM metaModel;

	private TechnologyAdapter<M, MM> technologyAdapter;

	private ViewPoint viewPoint;

	protected ModelSlot(ViewPoint viewPoint, TechnologyAdapter<M, MM> technologyAdapter) {
		super(null);
		this.viewPoint = viewPoint;
		this.technologyAdapter = technologyAdapter;
	}

	protected ModelSlot(ViewPointBuilder builder) {
		super(builder);
		if (builder != null) {
			this.viewPoint = builder.getViewPoint();
		}
	}

	@Override
	public String getURI() {
		return getViewPoint().getURI() + "." + getName();
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	@Override
	public ViewPoint getViewPoint() {
		return viewPoint;
	}

	public final M createEmptyModel(View view, MM metaModel) {
		return getTechnologyAdapter().createEmptyModel(view.getProject(), metaModel, technologyAdapter.getTechnologyContextManager());
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
		constructorParams[0] = ViewPointBuilder.class;
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

	public MM getMetaModel() {
		return metaModel;
	}

	public void setMetaModel(MM metaModel) {
		this.metaModel = metaModel;
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
	public String getLanguageRepresentation() {
		return "ModelSlot " + getName() + " conformTo " + getMetaModel().getURI() + " access=READ cardinality=ONE";
	}

	public final TechnologyAdapter<M, MM> getTechnologyAdapter() {
		return technologyAdapter;
	}

	public BindingVariable<?> makePatternRolePathElement(PatternRole<?> pr, Bindable container) {
		// TODO Auto-generated method stub
		return null;
	}

	public static BindingVariable<?> makePatternRolePathElement2(PatternRole pr, Bindable container) {
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
	}

}
