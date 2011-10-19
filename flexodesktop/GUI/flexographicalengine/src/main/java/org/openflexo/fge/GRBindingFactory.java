package org.openflexo.fge;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.DefaultBindingFactory;
import org.openflexo.antar.binding.FinalBindingPathElement;
import org.openflexo.antar.binding.KeyValueLibrary;
import org.openflexo.antar.binding.KeyValueProperty;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.GraphicalRepresentation.Parameters;
import org.openflexo.toolbox.ToolBox;

public class GRBindingFactory extends DefaultBindingFactory {

	private static final Logger logger = Logger.getLogger(GRBindingFactory.class.getPackage().getName());

	private static GRParameter[] allowedPropertiesInBindings
	= { Parameters.layer, Parameters.text, Parameters.relativeTextX, Parameters.relativeTextY,
		Parameters.absoluteTextX, Parameters.absoluteTextY, Parameters.isVisible,
		ShapeGraphicalRepresentation.Parameters.x, ShapeGraphicalRepresentation.Parameters.y,
		ShapeGraphicalRepresentation.Parameters.width, ShapeGraphicalRepresentation.Parameters.height,
		ShapeGraphicalRepresentation.Parameters.minimalWidth, ShapeGraphicalRepresentation.Parameters.minimalHeight,
		ShapeGraphicalRepresentation.Parameters.maximalWidth, ShapeGraphicalRepresentation.Parameters.maximalHeight};

	private static List<BindingPathElement> EMPTY_LIST = new ArrayList<BindingPathElement>();

	private static Class<? extends GraphicalRepresentation<?>> DECLARING_CLASS;

	static {
		try {
			DECLARING_CLASS = (Class<? extends GraphicalRepresentation<?>>) Class.forName(GraphicalRepresentation.class.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			DECLARING_CLASS = null;
		}
	}

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
			if (propertyName.equals(p.name())) {
				return true;
			}
		}
		return false;
	}

	/*@Override
	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements(BindingPathElement father)
	{
		return EMPTY_LIST;
	}*/

	public static class VariableBindingPathElement implements SimplePathElement<GraphicalRepresentation<?>,Object>,FinalBindingPathElement<GraphicalRepresentation<?>,Object>
	{
		private GRVariable variable;

		public VariableBindingPathElement(GRVariable variable)
		{
			this.variable = variable;
		}

		@Override
		public Class<? extends GraphicalRepresentation<?>> getDeclaringClass() {
			return DECLARING_CLASS;
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
		public Object getBindingValue(GraphicalRepresentation<?> target, BindingEvaluationContext context)
		{
			return variable.getValue();
		}

		@Override
		public void setBindingValue(Object value, GraphicalRepresentation<?> target, BindingEvaluationContext context)
		{
			logger.info("Please implement me");
		}
	}

	/**
	 * Modelize a binding variable which is an access to all components
	 * in the whole hierarchy
	 * 
	 * @author sylvain
	 *
	 */
	public static class ComponentsBindingVariable extends BindingVariableImpl<GraphicalRepresentation<?>,Vector<GraphicalRepresentation<?>>>
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
				components.add(new ComponentPathElement<GraphicalRepresentation<?>>(subComponent.getIdentifier(),subComponent,owner));
			}
		}

		public GraphicalRepresentation<?> getOwner() {
			return owner;
		}

		public Vector<ComponentPathElement> getComponents() {
			return components;
		}

	}

	public static class ComponentPathElement<E extends Bindable> implements SimplePathElement<E,GraphicalRepresentation<?>>, BindingVariable<E,GraphicalRepresentation<?>>
	{
		private String pathElementName;
		private GraphicalRepresentation<?> gr;
		private ComponentPathElement<GraphicalRepresentation<?>> parent;
		private IndexPathElement index;
		private Hashtable<String,VariableBindingPathElement> variables;
		private E container;

		public ComponentPathElement(String pathElementName, GraphicalRepresentation<?> gr, E container)
		{
			this.pathElementName = pathElementName;
			this.gr = gr;
			this.container = container;
			index = new IndexPathElement(gr);
			if (gr.getParentGraphicalRepresentation() != null) {
				parent = new ComponentPathElement<GraphicalRepresentation<?>>("parent", gr.getParentGraphicalRepresentation(),gr);
			}
			variables = new Hashtable<String, GRBindingFactory.VariableBindingPathElement>();
			for (GRVariable v : getComponent().getVariables()) {
				variables.put(v.getName(), new VariableBindingPathElement(v));
			}
		}

		public IndexPathElement getIndex() {
			return index;
		}

		public ComponentPathElement<GraphicalRepresentation<?>> getParent() {
			return parent;
		}

		public GraphicalRepresentation<?> getComponent() {
			return gr;
		}

		@Override
		public Class<E> getDeclaringClass() {
			return (Class<E>)container.getClass();
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
		public E getContainer()
		{
			return container;
		}

		@Override
		public String getVariableName() {
			return pathElementName;
		}


		@Override
		public GraphicalRepresentation<?> getBindingValue(E target, BindingEvaluationContext context)
		{
			return gr;
		}

		@Override
		public void setBindingValue(GraphicalRepresentation<?> value, E target, BindingEvaluationContext context)
		{
			// Not settable
		}

	}

	public static class IndexPathElement implements SimplePathElement<GraphicalRepresentation<?>,Integer>,FinalBindingPathElement<GraphicalRepresentation<?>,Integer>
	{
		private GraphicalRepresentation<?> gr;

		public IndexPathElement(GraphicalRepresentation<?> gr)
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
		public Integer getBindingValue(GraphicalRepresentation<?> target, BindingEvaluationContext context)
		{
			return gr.getIndex();
		}

		@Override
		public void setBindingValue(Integer value, GraphicalRepresentation<?> target, BindingEvaluationContext context)
		{
			// Not settable
		}
	}

	@Override
	public BindingPathElement getBindingPathElement(BindingPathElement father, String propertyName)
	{
		if (father instanceof ComponentsBindingVariable) {
			for (ComponentPathElement<?> c : ((ComponentsBindingVariable)father).getComponents()) {
				if (propertyName.equals(c.getLabel())) {
					return c;
				}
			}
			return null;
		}
		else if (father instanceof ComponentPathElement) {
			if (propertyName.equals("index")) {
				return ((ComponentPathElement<?>)father).getIndex();
			}
			if (propertyName.equals("parent")) {
				return ((ComponentPathElement<?>)father).getParent();
			}
			for (GRParameter p : allowedPropertiesInBindings) {
				if (isAllowedProperty(propertyName)) {
					return KeyValueLibrary.getKeyValueProperty(((ComponentPathElement<?>)father).getType(), propertyName);
				}
			}
			return ((ComponentPathElement<?>)father).variables.get(propertyName);
		}
		return super.getBindingPathElement(father, propertyName);
	}

	@Override
	public List<? extends BindingPathElement> getAccessibleBindingPathElements(BindingPathElement father)
	{
		if (father instanceof ComponentsBindingVariable) {
			return ((ComponentsBindingVariable)father).getComponents();
		}
		else if (father instanceof ComponentPathElement) {
			List<BindingPathElement> returned = new ArrayList<BindingPathElement>();
			if (!((ComponentPathElement<?>)father).getComponent().isRootGraphicalRepresentation()) {
				returned.add(((ComponentPathElement<?>)father).getIndex());
				returned.add(((ComponentPathElement<?>)father).getParent());
			}
			List<? extends BindingPathElement> all = KeyValueLibrary.getAccessibleProperties(((ComponentPathElement<?>)father).getType());
			for (BindingPathElement bpe : all) {
				if (isAllowedProperty(((KeyValueProperty)bpe).getName())) {
					returned.add(bpe);
				}
			}
			returned.addAll(((ComponentPathElement<?>)father).variables.values());
			return returned;
		}
		return super.getAccessibleBindingPathElements(father);
	}

	@Override
	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements(BindingPathElement father)
	{
		if (father instanceof ComponentsBindingVariable) {
			return EMPTY_LIST;
		}
		else if (father instanceof ComponentPathElement) {
			return EMPTY_LIST;
		}
		return super.getAccessibleCompoundBindingPathElements(father);
	}


}
