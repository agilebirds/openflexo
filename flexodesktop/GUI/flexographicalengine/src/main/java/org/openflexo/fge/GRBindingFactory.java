package org.openflexo.fge;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.DefaultBindingFactory;
import org.openflexo.antar.binding.KeyValueProperty;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fge.GraphicalRepresentation.GRBindingVariable;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.GraphicalRepresentation.Parameters;
import org.openflexo.toolbox.ToolBox;

public class GRBindingFactory extends DefaultBindingFactory {

	private static GRParameter[] allowedPropertiesInBindings
	= { Parameters.layer, Parameters.text, Parameters.relativeTextX, Parameters.relativeTextY,
		Parameters.absoluteTextX, Parameters.absoluteTextY, Parameters.isVisible, 
		ShapeGraphicalRepresentation.Parameters.x, ShapeGraphicalRepresentation.Parameters.y,
		ShapeGraphicalRepresentation.Parameters.width, ShapeGraphicalRepresentation.Parameters.height,
		ShapeGraphicalRepresentation.Parameters.minimalWidth, ShapeGraphicalRepresentation.Parameters.minimalHeight,
		ShapeGraphicalRepresentation.Parameters.maximalWidth, ShapeGraphicalRepresentation.Parameters.maximalHeight};

	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	@Override
	public BindingPathElement getBindingPathElement(BindingPathElement father, String propertyName) 
	{
		if (father instanceof GRBindingVariable) {
			for (GRParameter p : allowedPropertiesInBindings) {
				if (isAllowedProperty(propertyName))
					return super.getBindingPathElement(father, propertyName);
			}
			for (GRVariable v : ((GraphicalRepresentation.GRBindingVariable)father).getGraphicalRepresentation().getVariables()) {
				if (propertyName.equals(v.getName())) return new VariableBindingPathElement(v);
			}
		}
		return null;
	}

	@Override
	public List<? extends BindingPathElement> getAccessibleBindingPathElements(BindingPathElement father) 
	{
		if (father instanceof GRBindingVariable) {
			List<BindingPathElement> returned = new ArrayList<BindingPathElement>();
			List<? extends BindingPathElement> all = super.getAccessibleBindingPathElements(father);
			for (BindingPathElement bpe : all) {
				if (isAllowedProperty(((KeyValueProperty)bpe).getName())) returned.add(bpe);
			}
			for (GRVariable v : ((GraphicalRepresentation.GRBindingVariable)father).getGraphicalRepresentation().getVariables()) {
				returned.add(new VariableBindingPathElement(v));
			}
			return returned;
		}
		return EMPTY_LIST;
	}
	
	private boolean isAllowedProperty(String propertyName)
	{
		for (GRParameter p : allowedPropertiesInBindings) {
			if (propertyName.equals(p.name()))
				return true;
		}
		return false;
	}
	
	@Override
	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements(BindingPathElement father)
	{
		return EMPTY_LIST;
	}
	
	public class VariableBindingPathElement implements SimplePathElement
	{
		private GRVariable variable;
		
		public VariableBindingPathElement(GRVariable variable) 
		{
			this.variable = variable;
		}
		
		@Override
		public Class getDeclaringClass() 
		{
			return GraphicalRepresentation.class;
		}

		@Override
		public String getSerializationRepresentation() 
		{
			return variable.getName();
		}

		@Override
		public Type getType() 
		{
			return variable.getType().getType();
		}

		@Override
		public boolean isBindingValid()
		{
			return true;
		}
		
		@Override
		public String getLabel() 
		{
			return variable.getName();
		}
		
		@Override
		public String getTooltipText(Type resultingType) 
		{
			String returned = "<html>";
			String resultingTypeAsString;
			if (resultingType!=null) {
				resultingTypeAsString = TypeUtils.simpleRepresentation(resultingType);
				resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
				resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
			}
			else {
				resultingTypeAsString = "???";
			}
			returned += "<p><b>"+resultingTypeAsString+" "+variable.getName()+"</b></p>";
			//returned += "<p><i>"+(property.getDescription()!=null?property.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
			returned += "</html>";
			return returned;
		}
		
	    @Override
	    public boolean isSettable()
	    {
	    	return true;
	    }
	    
	}
}
