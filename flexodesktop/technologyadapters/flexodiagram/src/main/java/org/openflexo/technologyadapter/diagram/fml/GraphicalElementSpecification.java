package org.openflexo.technologyadapter.diagram.fml;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;

/**
 * This class represent a constraint of graphical feature that is to be applied on a DiagramElement
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(GraphicalElementSpecification.GraphicalElementSpecificationImpl.class)
@XMLElement(xmlTag="GRSpec")
public interface GraphicalElementSpecification<T, GR extends GraphicalRepresentation> extends EditionPatternObject,Bindable{

@PropertyIdentifier(type=String.class)
public static final String FEATURE_NAME_KEY = "featureName";
@PropertyIdentifier(type=DataBinding.class)
public static final String VALUE_KEY = "value";
@PropertyIdentifier(type=boolean.class)
public static final String READ_ONLY_KEY = "readOnly";

@Getter(value=FEATURE_NAME_KEY)
@XMLAttribute
public String getFeatureName();

@Setter(FEATURE_NAME_KEY)
public void _setFeatureName(String featureName);


@Getter(value=VALUE_KEY)
@XMLAttribute
public DataBinding getValue();

@Setter(VALUE_KEY)
public void setValue(DataBinding value);


@Getter(value=READ_ONLY_KEY,defaultValue = "false")
@XMLAttribute
public boolean getReadOnly();

@Setter(READ_ONLY_KEY)
public void setReadOnly(boolean readOnly);


public static abstract  class GraphicalElementSpecification<T, GRImpl extends GraphicalRepresentation> extends EditionPatternObjectImpl implements GraphicalElementSpecification<T, GR
{

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GraphicalElementSpecification.class.getPackage().getName());

	private GraphicalElementPatternRole<?, GR> patternRole;
	private GraphicalFeature<T, GR> feature;
	private String featureName;
	private DataBinding<String> value;
	private boolean readOnly;
	private boolean mandatory;

	// Use it only for deserialization
	public GraphicalElementSpecificationImpl() {
		super();
	}

	public GraphicalElementSpecificationImpl(GraphicalElementPatternRole<?, GR> patternRole, GraphicalFeature<T, GR> feature, boolean readOnly,
			boolean mandatory) {
		super();
		this.patternRole = patternRole;
		this.feature = feature;
		this.readOnly = readOnly;
		this.mandatory = mandatory;
	}

	@Override
	public String getURI() {
		return null;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	public GraphicalFeature<T, GR> getFeature() {
		return feature;
	}

	public String getFeatureName() {
		if (feature == null) {
			return featureName;
		}
		return feature.getName();
	}

	public void _setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public DataBinding<String> getValue() {
		if (value == null) {
			value = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET_SET);
			value.setBindingName(featureName);
			value.setMandatory(mandatory);
			value.setBindingDefinitionType(getReadOnly() ? BindingDefinitionType.GET : BindingDefinitionType.GET_SET);
		}
		return value;
	}

	public void setValue(DataBinding<String> value) {
		if (value != null) {
			value.setOwner(this);
			value.setDeclaredType(String.class);
			value.setBindingName(featureName);
			value.setMandatory(mandatory);
			value.setBindingDefinitionType(getReadOnly() ? BindingDefinitionType.GET : BindingDefinitionType.GET_SET);
		}
		this.value = value;
	}

	public boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		getValue().setBindingDefinitionType(getReadOnly() ? BindingDefinitionType.GET : BindingDefinitionType.GET_SET);
		notifiedBindingChanged(getValue());
	}

	public boolean getMandatory() {
		return mandatory;
	}

	public GraphicalElementPatternRole<?, GR> getPatternRole() {
		return patternRole;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return getPatternRole() != null ? getPatternRole().getEditionPattern() : null;
	}

	@Override
	public BindingFactory getBindingFactory() {
		return getEditionPattern().getInspector().getBindingFactory();
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionPattern().getInspector().getBindingModel();
	}

	@Override
	public VirtualModel getVirtualModel() {
		return getEditionPattern() != null ? getEditionPattern().getVirtualModel() : null;
	}

	/**
	 * This method is called to extract a value from the model and conform to the related feature, and apply it to graphical representation
	 * 
	 * @param gr
	 * @param element
	 */
	// public void applyToGraphicalRepresentation(GR gr, DiagramElement<GR> element) {
	public void applyToGraphicalRepresentation(EditionPatternInstance epi, GraphicalElementPatternRole<?, GR> patternRole) {
		/*if (getValue().toString().equals(
				"(property.label.asString + ((inputAttributeReference.value != \"\") ? (\"=\" + inputAttributeReference.value) : \"\"))")) {
			System.out.println("value=" + getValue());
			System.out.println("hasBinding=" + getValue().hasBinding());
			System.out.println("valid=" + getValue().isValid());
			System.out.println("reason=" + getValue().getBinding().invalidBindingReason());
			System.out.println("EPI=" + element.getEditionPatternInstance().debug());
			System.out.println("Result=" + getValue().getBindingValue(element.getEditionPatternInstance()));
			System.out.println("Hop");
		}*/

		try {
			DiagramElement<GR> diagramElement = epi.getPatternActor(patternRole);
			getFeature().applyToGraphicalRepresentation((GR) diagramElement.getGraphicalRepresentation(),
					(T) getValue().getBindingValue(epi));
			// getFeature().applyToGraphicalRepresentation(gr, (T) getValue().getBindingValue(element.getEditionPatternInstance()));
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is called to extract a value from the graphical representation and conform to the related feature, and apply it to model
	 * 
	 * @param gr
	 * @param element
	 * @return
	 */
	public T applyToModel(EditionPatternInstance epi, GraphicalElementPatternRole<?, GR> patternRole) {
		DiagramElement<GR> diagramElement = epi.getPatternActor(patternRole);
		T newValue = getFeature().retrieveFromGraphicalRepresentation((GR) diagramElement.getGraphicalRepresentation());
		try {
			getValue().setBindingValue(newValue, epi);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NotSettableContextException e) {
			e.printStackTrace();
		}
		return newValue;
	}

}
}
