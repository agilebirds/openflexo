package org.openflexo.foundation.technologyadapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPatternPatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.PropertyPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicClassPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicDataPropertyPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicIndividualPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicObjectPropertyPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.OntologicObjectPatternRolePathElement.OntologicPropertyPatternRolePathElement;

/**
 * Provides default implementation of the {@link ModelSlot} interface.
 * 
 * @author Luka Le Roux, Sylvain Guerin
 * 
 * @param <M>
 *            Type of {@link FlexoModel} handled by this ModelSlot
 * @param <MM>
 *            {@link FlexoMetaModel} which model handled by this ModelSlot is conform to
 * 
 * @see org.openflexo.foundation.viewpoint.ViewPoint
 * @see org.openflexo.foundation.view.diagram.model.View
 */
public abstract class ModelSlotImpl<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends ViewPointObject implements
		ModelSlot<M, MM> {

	private static final Logger logger = Logger.getLogger(ModelSlotImpl.class.getPackage().getName());

	private String name;
	private boolean isRequired;
	private boolean isReadOnly;
	private MM metaModel;

	private TechnologyAdapter<M, MM> technologyAdapter;

	private ViewPoint viewPoint;

	protected ModelSlotImpl(ViewPoint viewPoint, TechnologyAdapter<M, MM> technologyAdapter) {
		super(null);
		this.viewPoint = viewPoint;
		this.technologyAdapter = technologyAdapter;
	}

	protected ModelSlotImpl(ViewPointBuilder builder) {
		super(builder);
		if (builder != null) {
			this.viewPoint = builder.getViewPoint();
		}
	}

	@Override
	public ViewPoint getViewPoint() {
		return viewPoint;
	}

	@Override
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
	@Override
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

	@Override
	public MM getMetaModel() {
		return metaModel;
	}

	@Override
	public void setMetaModel(MM metaModel) {
		this.metaModel = metaModel;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
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

	@Deprecated
	@Override
	public String getInspectorName() {
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		return viewPoint.getBindingModel();
	}

	@Override
	public String getLanguageRepresentation() {
		return "ModelSlot " + getName() + " conformTo " + getMetaModel().getURI() + " access=READ cardinality=ONE";
	}

	@Override
	public final TechnologyAdapter<M, MM> getTechnologyAdapter() {
		return technologyAdapter;
	}

	@Override
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
