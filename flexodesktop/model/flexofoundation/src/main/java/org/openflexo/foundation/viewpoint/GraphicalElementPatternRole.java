package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;

public abstract class GraphicalElementPatternRole extends PatternRole implements Bindable {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GraphicalElementPatternRole.class.getPackage().getName());

	private boolean readOnlyLabel;
	private ViewPointDataBinding label;

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

	public ViewPointDataBinding getLabel() {
		if (label == null) {
			label = new ViewPointDataBinding(this, GraphicalElementBindingAttribute.label, getLabelBindingDefinition());
		}
		return label;
	}

	public void setLabel(ViewPointDataBinding label) {
		label.setOwner(this);
		label.setBindingAttribute(GraphicalElementBindingAttribute.label);
		label.setBindingDefinition(getLabelBindingDefinition());
		this.label = label;
	}

	public boolean getReadOnlyLabel() {
		return readOnlyLabel;
	}

	public void setReadOnlyLabel(boolean readOnlyLabel) {
		this.readOnlyLabel = readOnlyLabel;
	}

	@Override
	public BindingFactory getBindingFactory() {
		return getEditionPattern().getInspector().getBindingFactory();
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionPattern().getInspector().getBindingModel();
	}

	public boolean getIsPrimaryRepresentationRole() {
		if (getEditionPattern() == null) {
			return false;
		}
		return (getEditionPattern().getPrimaryRepresentationRole() == this);
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

}
