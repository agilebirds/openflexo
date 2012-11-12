package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

/**
 * This class represent a constraint of graphical feature that is to be applied on a ViewElement
 * 
 * @author sylvain
 * 
 */
public class GraphicalElementSpecification<T, GR extends GraphicalRepresentation<?>> extends EditionPatternObject implements Bindable {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GraphicalElementSpecification.class.getPackage().getName());

	private GraphicalElementPatternRole patternRole;
	private GraphicalFeature<T, GR> feature;
	private String featureName;
	private ViewPointDataBinding value;
	private boolean readOnly;
	private boolean mandatory;
	private BindingDefinition BD;

	// Use it only for deserialization
	public GraphicalElementSpecification(ViewPointBuilder builder) {
		super(builder);
	}

	public GraphicalElementSpecification(GraphicalElementPatternRole patternRole, GraphicalFeature<T, GR> feature, boolean readOnly,
			boolean mandatory) {
		super(null);
		this.patternRole = patternRole;
		this.feature = feature;
		this.readOnly = readOnly;
		BD = new BindingDefinition(feature.getName(), feature.getType(), BindingDefinitionType.GET_SET, mandatory) {
			@Override
			public BindingDefinitionType getBindingDefinitionType() {
				if (getReadOnly()) {
					return BindingDefinitionType.GET;
				} else {
					return BindingDefinitionType.GET_SET;
				}
			}
		};
	}

	public GraphicalFeature<T, GR> getFeature() {
		return feature;
	}

	public String getFeatureName() {
		if (feature == null)
			return featureName;
		return feature.getName();
	}

	public void _setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public ViewPointDataBinding getValue() {
		if (value == null) {
			value = new ViewPointDataBinding(this, feature, getBindingDefinition());
		}
		return value;
	}

	public void setValue(ViewPointDataBinding value) {
		if (value != null) {
			value.setOwner(this);
			value.setBindingAttribute(feature);
			value.setBindingDefinition(getBindingDefinition());
		}
		this.value = value;
	}

	public BindingDefinition getBindingDefinition() {
		return BD;
	}

	public boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean getMandatory() {
		return mandatory;
	}

	public GraphicalElementPatternRole getPatternRole() {
		return patternRole;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return (getPatternRole() != null ? getPatternRole().getEditionPattern() : null);
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
	public String getInspectorName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ViewPoint getViewPoint() {
		return (getEditionPattern() != null ? getEditionPattern().getViewPoint() : null);
	}

	/**
	 * This method is called to extract a value from the model and conform to the related feature, and apply it to graphical representation
	 * 
	 * @param gr
	 * @param element
	 */
	public void applyToGraphicalRepresentation(GR gr, ViewElement element) {
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

		getFeature().applyToGraphicalRepresentation(gr, (T) getValue().getBindingValue(element.getEditionPatternInstance()));
	}

	/**
	 * This method is called to extract a value from the graphical representation and conform to the related feature, and apply it to model
	 * 
	 * @param gr
	 * @param element
	 * @return
	 */
	public T applyToModel(GR gr, ViewElement element) {
		T newValue = getFeature().retrieveFromGraphicalRepresentation(gr);
		getValue().setBindingValue(newValue, element.getEditionPatternInstance());
		return newValue;
	}

}
