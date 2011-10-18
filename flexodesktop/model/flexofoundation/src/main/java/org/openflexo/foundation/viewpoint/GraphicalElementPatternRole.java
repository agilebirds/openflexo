package org.openflexo.foundation.viewpoint;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;
import org.openflexo.foundation.viewpoint.inspector.InspectorDataBinding;


public abstract class GraphicalElementPatternRole extends PatternRole implements Bindable {

	private boolean readOnlyLabel;
	private InspectorDataBinding label;

	
 	public abstract Object getGraphicalRepresentation(); 

	public abstract void setGraphicalRepresentation(Object graphicalRepresentation); 
	
	public abstract void _setGraphicalRepresentationNoNotification(Object graphicalRepresentation); 

	public static enum GraphicalElementBindingAttribute implements InspectorBindingAttribute
	{
		label
	}

	private BindingDefinition LABEL;
	
	public BindingDefinition getLabelBindingDefinition()
	{
		if (LABEL == null) {
			LABEL = new BindingDefinition("label", String.class, BindingDefinitionType.GET_SET, false) {
				@Override
				public BindingDefinitionType getBindingDefinitionType() {
					if (getReadOnlyLabel()) return BindingDefinitionType.GET;
					else return BindingDefinitionType.GET_SET;
				}
			};
		}
		return LABEL;
	}

	public InspectorDataBinding getLabel() 
	{
		if (label == null) label = new InspectorDataBinding(this,GraphicalElementBindingAttribute.label,getLabelBindingDefinition());
		return label;
	}

	public void setLabel(InspectorDataBinding label) 
	{
		label.setOwner(this);
		label.setBindingAttribute(GraphicalElementBindingAttribute.label);
		label.setBindingDefinition(getLabelBindingDefinition());
		this.label = label;
	}
	
	public boolean getReadOnlyLabel() 
	{
		return readOnlyLabel;
	}

	public void setReadOnlyLabel(boolean readOnlyLabel) 
	{
		this.readOnlyLabel = readOnlyLabel;
	}

	@Override
	public BindingFactory getBindingFactory() 
	{
		return getEditionPattern().getInspector().getBindingFactory();
	}
	
	@Override
	public BindingModel getBindingModel() 
	{
		return getEditionPattern().getInspector().getBindingModel();
	}
	

}
