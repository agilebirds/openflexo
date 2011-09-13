package org.openflexo.fge;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.DefaultBindingFactory;
import org.openflexo.antar.binding.FinalBindingPathElementImpl;
import org.openflexo.antar.binding.KeyValueLibrary;
import org.openflexo.antar.binding.KeyValueProperty;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
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

	/*@Override
	public BindingPathElement getBindingPathElement(BindingPathElement father, String propertyName) 
	{
		if (father instanceof ComponentsBindingVariable) {
			for (ComponentPathElement c : ((ComponentsBindingVariable) father).getComponents()) {
				if (propertyName.equals(c.getLabel())) return c;
			}
			return null;
		}
		if (father instanceof ComponentPathElement) {
			return ((ComponentPathElement) father).getBindingPathElement(propertyName);
		}
		return null;
	}

	@Override
	public List<? extends BindingPathElement> getAccessibleBindingPathElements(BindingPathElement father) 
	{
		if (father instanceof ComponentsBindingVariable) {
			return ((ComponentsBindingVariable) father).getComponents();
		}
		if (father instanceof ComponentPathElement) {
			return ((ComponentPathElement) father).getAccessibleBindingPathElements();
		}
		return EMPTY_LIST;
	}*/
	
	private static boolean isAllowedProperty(String propertyName)
	{
		for (GRParameter p : allowedPropertiesInBindings) {
			if (propertyName.equals(p.name()))
				return true;
		}
		return false;
	}
	
	/*@Override
	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements(BindingPathElement father)
	{
		return EMPTY_LIST;
	}*/
	
	public static class VariableBindingPathElement extends FinalBindingPathElementImpl implements SimplePathElement
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
	    
	    @Override
	    public Object evaluateBinding(Object target, BindingEvaluationContext context)
	    {
 	    	return variable.getValue();
	    }
	    
	}
	
	/**
	 * Modelize a binding variable which is an access to all components
	 * in the whole hierarchy
	 * 
	 * @author sylvain
	 *
	 */
	public static class ComponentsBindingVariable extends BindingVariableImpl
	{

		private GraphicalRepresentation<?> owner;
		private Vector<ComponentPathElement> components;
 
		public ComponentsBindingVariable(GraphicalRepresentation<?> owner)
		{
			super(owner, "components", new ParameterizedTypeImpl(Vector.class, GraphicalRepresentation.class));
			this.owner = owner;
			components = new Vector<GRBindingFactory.ComponentPathElement>();
			Iterator<GraphicalRepresentation> it = owner.getRootGraphicalRepresentation().allContainedGRIterator();
			while (it.hasNext()) {
				GraphicalRepresentation subComponent = it.next();
				components.add(new ComponentPathElement(subComponent.getIdentifier(),subComponent));
			}
		}

		public GraphicalRepresentation<?> getOwner() {
			return owner;
		}

		public Vector<ComponentPathElement> getComponents() {
			return components;
		}
		
		public BindingPathElement getBindingPathElement(String propertyName)
		{
			for (ComponentPathElement c : getComponents()) {
				if (propertyName.equals(c.getLabel())) return c;
			}
			return null;
		}
		
		public List<? extends BindingPathElement> getAccessibleBindingPathElements()
		{
			return getComponents();
		}

		public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements()
		{
			return EMPTY_LIST;
		}

	}

	public static class ComponentPathElement implements SimplePathElement, BindingVariable
	{
		private String pathElementName;
		private GraphicalRepresentation gr;
		private ComponentPathElement parent;
		private IndexPathElement index;
		private Hashtable<String,VariableBindingPathElement> variables;
		
		public ComponentPathElement(String pathElementName, GraphicalRepresentation gr)
		{
			this.pathElementName = pathElementName;
			this.gr = gr;
			index = new IndexPathElement(gr);
			if (gr.getParentGraphicalRepresentation() != null) 
				parent = new ComponentPathElement("parent", gr.getParentGraphicalRepresentation());
			variables = new Hashtable<String, GRBindingFactory.VariableBindingPathElement>();
			for (GRVariable v : getComponent().getVariables()) {
				variables.put(v.getName(), new VariableBindingPathElement(v));
			}
		}
		
		public IndexPathElement getIndex() {
			return index;
		}
		
		public ComponentPathElement getParent() {
			return parent;
		}

		public GraphicalRepresentation<?> getComponent() {
			return gr;
		}
		
		@Override
		public Class getDeclaringClass() {
			return Vector.class;
		}

		@Override
		public Type getType() {
			return gr.getClass();
		}

		@Override
		public String getSerializationRepresentation() {
			return pathElementName;
		}

		@Override
		public boolean isBindingValid() {
			return true;
		}

		@Override
		public String getLabel() {
			return getSerializationRepresentation();
		}

		@Override
		public String getTooltipText(Type resultingType) {
			return getSerializationRepresentation();
		}

		@Override
		public boolean isSettable() {
			return false;
		}

		@Override
		public Bindable getContainer() {
			return gr;
		}

		@Override
		public String getVariableName() {
			return pathElementName;
		}
		
		public BindingPathElement getBindingPathElement(String propertyName) 
		{
			if (propertyName.equals("index")) return getIndex();
			if (propertyName.equals("parent")) return getParent();
			for (GRParameter p : allowedPropertiesInBindings) {
				if (isAllowedProperty(propertyName))
					return KeyValueLibrary.getKeyValueProperty(getType(), propertyName);			
			}
			return variables.get(propertyName);
		}

		public List<? extends BindingPathElement> getAccessibleBindingPathElements() 
		{
			List<BindingPathElement> returned = new ArrayList<BindingPathElement>();
			if (!getComponent().isRootGraphicalRepresentation()) {
				returned.add(getIndex());
				returned.add(getParent());
			}
			List<? extends BindingPathElement> all = KeyValueLibrary.getAccessibleProperties(getType());
			for (BindingPathElement bpe : all) {
				if (isAllowedProperty(((KeyValueProperty)bpe).getName())) returned.add(bpe);
			}
			returned.addAll(variables.values());
			return returned;
		}
		
		@Override
		public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements() 
		{
			return EMPTY_LIST;
		}
		
		@Override
		public Object evaluateBinding(Object target, BindingEvaluationContext context) 
		{
 			return gr;
		}
	}
	
	public static class IndexPathElement extends FinalBindingPathElementImpl implements SimplePathElement
	{
		private GraphicalRepresentation gr;
		
		public IndexPathElement(GraphicalRepresentation gr)
		{
			this.gr = gr;
		}
		
		public GraphicalRepresentation<?> getComponent() {
			return gr;
		}
		
		@Override
		public Class getDeclaringClass() {
			return gr.getClass();
		}

		@Override
		public Type getType() {
			return Integer.TYPE;
		}

		@Override
		public String getSerializationRepresentation() {
			return "index";
		}

		@Override
		public boolean isBindingValid() {
			return true;
		}

		@Override
		public String getLabel() {
			return getSerializationRepresentation();
		}

		@Override
		public String getTooltipText(Type resultingType) {
			return getSerializationRepresentation();
		}

		@Override
		public boolean isSettable() {
			return false;
		}
		
		@Override
		public Object evaluateBinding(Object target, BindingEvaluationContext context) 
		{
			return gr.getIndex();
		}
		
	}
}
