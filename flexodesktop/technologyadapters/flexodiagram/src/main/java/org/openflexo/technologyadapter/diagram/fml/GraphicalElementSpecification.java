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
public class GraphicalElementSpecification<T, GR extends GraphicalRepresentation> extends EditionPatternObject implements Bindable {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GraphicalElementSpecification.class.getPackage().getName());

	private GraphicalElementPatternRole patternRole;
	private GraphicalFeature<T, GR> feature;
	private String featureName;
	private DataBinding<String> value;
	private boolean readOnly;
	private boolean mandatory;

	// Use it only for deserialization
	public GraphicalElementSpecification(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	public GraphicalElementSpecification(GraphicalElementPatternRole patternRole, GraphicalFeature<T, GR> feature, boolean readOnly,
			boolean mandatory) {
		super(null);
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

	public GraphicalElementPatternRole getPatternRole() {
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
	public void applyToGraphicalRepresentation(GR gr, DiagramElement element) {
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
			getFeature().applyToGraphicalRepresentation(gr, (T) getValue().getBindingValue(element.getEditionPatternInstance()));
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
	public T applyToModel(GR gr, DiagramElement element) {
		T newValue = getFeature().retrieveFromGraphicalRepresentation(gr);
		try {
			getValue().setBindingValue(newValue, element.getEditionPatternInstance());
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
