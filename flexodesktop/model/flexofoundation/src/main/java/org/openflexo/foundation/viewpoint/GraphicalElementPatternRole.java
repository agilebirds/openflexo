package org.openflexo.foundation.viewpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.viewpoint.GraphicalElementAction.ActionMask;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.dm.GraphicalElementActionInserted;
import org.openflexo.foundation.viewpoint.dm.GraphicalElementActionRemoved;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

public abstract class GraphicalElementPatternRole extends PatternRole implements Bindable {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GraphicalElementPatternRole.class.getPackage().getName());

	private boolean readOnlyLabel;
	private ViewPointDataBinding label;

	private String exampleLabel = "label";

	protected Vector<GraphicalElementSpecification<?, ?>> grSpecifications;

	private Vector<GraphicalElementAction> actions;

	public GraphicalElementPatternRole(ViewPointBuilder builder) {
		super(builder);
		initDefaultSpecifications();
	}

	protected void initDefaultSpecifications() {
		grSpecifications = new Vector<GraphicalElementSpecification<?, ?>>();
		for (GraphicalFeature<?, ?> GF : AVAILABLE_FEATURES) {
			grSpecifications.add(new GraphicalElementSpecification(this, GF, false, true));
		}
	}

	public abstract Object getGraphicalRepresentation();

	public abstract void setGraphicalRepresentation(Object graphicalRepresentation);

	public abstract void _setGraphicalRepresentationNoNotification(Object graphicalRepresentation);

	public static enum GraphicalElementBindingAttribute implements InspectorBindingAttribute {
		label
	}

	private BindingDefinition LABEL;

	public BindingDefinition getLabelBindingDefinition() {
		if (LABEL == null) {
			LABEL = new BindingDefinition("label", String.class, BindingDefinitionType.GET_SET, false) {
				@Override
				public BindingDefinitionType getBindingDefinitionType() {
					if (getReadOnlyLabel()) {
						return BindingDefinitionType.GET;
					} else {
						return BindingDefinitionType.GET_SET;
					}
				}
			};
		}
		return LABEL;
	}

	// Convenient method to access spec for label feature
	public ViewPointDataBinding getLabel() {
		return getGraphicalElementSpecification(LABEL_FEATURE).getValue();
	}

	// Convenient method to access spec for label feature
	public void setLabel(ViewPointDataBinding label) {
		getGraphicalElementSpecification(LABEL_FEATURE).setValue(label);
	}

	// Convenient method to access read-only property for spec for label feature
	public boolean getReadOnlyLabel() {
		return getGraphicalElementSpecification(LABEL_FEATURE).getReadOnly();
	}

	// Convenient method to access read-only property for spec for label feature
	public void setReadOnlyLabel(boolean readOnlyLabel) {
		getGraphicalElementSpecification(LABEL_FEATURE).setReadOnly(readOnlyLabel);
	}

	@Override
	public BindingFactory getBindingFactory() {
		return getEditionPattern().getInspector().getBindingFactory();
	}

	/*@Override
	public BindingModel getBindingModel() {
		return getEditionPattern().getInspector().getBindingModel();
	}*/

	public boolean getIsPrimaryRepresentationRole() {
		if (getEditionPattern() == null) {
			return false;
		}
		return getEditionPattern().getPrimaryRepresentationRole() == this;
	}

	public void setIsPrimaryRepresentationRole(boolean isPrimary) {
		if (getEditionPattern() == null) {
			return;
		}
		if (isPrimary) {
			getEditionPattern().setPrimaryRepresentationRole(this);
		} else {
			getEditionPattern().setPrimaryRepresentationRole(null);
		}
	}

	public boolean isIncludedInPrimaryRepresentationRole() {
		return getIsPrimaryRepresentationRole();
	}

	@Override
	public boolean getIsPrimaryRole() {
		return getIsPrimaryRepresentationRole();
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
		setIsPrimaryRepresentationRole(isPrimary);
	}

	public boolean containsShapes() {
		for (ShapePatternRole role : getEditionPattern().getShapePatternRoles()) {
			if (role.getParentShapePatternRole() == this) {
				return true;
			}
		}
		return false;
	}

	public String getExampleLabel() {
		return exampleLabel;
	}

	public void setExampleLabel(String exampleLabel) {
		this.exampleLabel = exampleLabel;
	}

	public Vector<GraphicalElementAction> getActions() {
		return actions;
	}

	public void setActions(Vector<GraphicalElementAction> someActions) {
		actions = someActions;
	}

	public void addToActions(GraphicalElementAction anAction) {
		anAction.setGraphicalElementPatternRole(this);
		actions.add(anAction);
		setChanged();
		notifyObservers(new GraphicalElementActionInserted(anAction, this));
	}

	public void removeFromActions(GraphicalElementAction anAction) {
		anAction.setGraphicalElementPatternRole(null);
		actions.remove(anAction);
		setChanged();
		notifyObservers(new GraphicalElementActionRemoved(anAction, this));
	}

	public List<GraphicalElementAction.ActionMask> getReferencedMasks() {
		ArrayList<GraphicalElementAction.ActionMask> returned = new ArrayList<GraphicalElementAction.ActionMask>();
		for (GraphicalElementAction a : getActions()) {
			if (!returned.contains(a.getActionMask())) {
				returned.add(a.getActionMask());
			}
		}
		return returned;
	}

	public List<GraphicalElementAction> getActions(ActionMask mask) {
		ArrayList<GraphicalElementAction> returned = new ArrayList<GraphicalElementAction>();
		for (GraphicalElementAction a : getActions()) {
			if (a.getActionMask() == mask) {
				returned.add(a);
			}
		}
		return returned;
	}

	public GraphicalElementAction createAction() {
		GraphicalElementAction newAction = new GraphicalElementAction(null);
		addToActions(newAction);
		return newAction;
	}

	public GraphicalElementAction deleteAction(GraphicalElementAction anAction) {
		removeFromActions(anAction);
		anAction.delete();
		return anAction;
	}

	public Vector<GraphicalElementSpecification<?, ?>> getGrSpecifications() {
		return grSpecifications;
	}

	public GraphicalElementSpecification<?, ?> getGraphicalElementSpecification(String featureName) {
		for (GraphicalElementSpecification<?, ?> spec : grSpecifications) {
			if (spec.getFeatureName().equals(featureName)) {
				return spec;
			}
		}
		return null;
	}

	public GraphicalElementSpecification<?, ?> getGraphicalElementSpecification(GraphicalFeature<?, ?> feature) {
		if (feature != null) {
			return getGraphicalElementSpecification(feature.getName());
		}
		return null;
	}

	public Vector<GraphicalElementSpecification<?, ?>> _getDeclaredGRSpecifications() {
		Vector<GraphicalElementSpecification<?, ?>> returned = new Vector<GraphicalElementSpecification<?, ?>>();
		for (GraphicalElementSpecification<?, ?> spec : grSpecifications) {
			if (spec.getValue().isSet()) {
				returned.add(spec);
			}
		}
		return returned;
	}

	public void _setDeclaredGRSpecifications(Vector<GraphicalElementSpecification<?, ?>> someSpecs) {
		for (GraphicalElementSpecification<?, ?> s : someSpecs) {
			_addToDeclaredGRSpecifications(s);
		}
	}

	public void _addToDeclaredGRSpecifications(GraphicalElementSpecification<?, ?> aSpec) {
		GraphicalElementSpecification<?, ?> existingSpec = getGraphicalElementSpecification(aSpec.getFeatureName());
		if (existingSpec == null) {
			logger.warning("Cannot find any GraphicalElementSpecification matching " + aSpec.getFeatureName() + ". Ignoring...");
		} else {
			existingSpec.setValue(aSpec.getValue());
			existingSpec.setReadOnly(aSpec.getReadOnly());
		}
	}

	public void _removeFromDeclaredGRSpecifications(GraphicalElementSpecification<?, ?> aSpec) {
		GraphicalElementSpecification<?, ?> existingSpec = getGraphicalElementSpecification(aSpec.getFeatureName());
		if (existingSpec == null) {
			logger.warning("Cannot find any GraphicalElementSpecification matching " + aSpec.getFeatureName() + ". Ignoring...");
		} else {
			existingSpec.setValue(null);
		}
	}

	public static GraphicalFeature<String, GraphicalRepresentation<?>> LABEL_FEATURE = new GraphicalFeature<String, GraphicalRepresentation<?>>(
			"label", GraphicalRepresentation.Parameters.text, String.class) {
		@Override
		public String retrieveFromGraphicalRepresentation(GraphicalRepresentation<?> gr) {
			return gr.getText();
		}

		@Override
		public void applyToGraphicalRepresentation(GraphicalRepresentation<?> gr, String value) {
			gr.setText(value);
		}
	};

	public static GraphicalFeature<Boolean, GraphicalRepresentation<?>> VISIBLE_FEATURE = new GraphicalFeature<Boolean, GraphicalRepresentation<?>>(
			"visible", GraphicalRepresentation.Parameters.isVisible, Boolean.class) {
		@Override
		public Boolean retrieveFromGraphicalRepresentation(GraphicalRepresentation<?> gr) {
			return gr.getIsVisible();
		}

		@Override
		public void applyToGraphicalRepresentation(GraphicalRepresentation<?> gr, Boolean value) {
			gr.setIsVisible(value);
		}
	};

	public static GraphicalFeature<?, ?>[] AVAILABLE_FEATURES = { LABEL_FEATURE, VISIBLE_FEATURE };

}
