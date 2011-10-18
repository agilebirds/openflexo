package org.openflexo.foundation.viewpoint.inspector;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FinalBindingPathElementImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.ShapePatternRole;

public abstract class GraphicalElementPatternRolePathElement<E extends Bindable,T extends ViewObject> extends PatternRolePathElement<E,T>
{
	private static final Logger logger = Logger.getLogger(GraphicalElementPatternRolePathElement.class.getPackage().getName());

	protected List<BindingPathElement> allProperties;
	private FinalBindingPathElementImpl<T,String> text;
	private GraphicalElementPathElement<ViewObject> parent;

	public GraphicalElementPatternRolePathElement(GraphicalElementPatternRole aPatternRole, E container) 
	{
		super(aPatternRole,container);
		allProperties = new Vector<BindingPathElement>();
		parent = new GraphicalElementPathElement<ViewObject>("parent",this);
		allProperties.add(parent);
		text = new FinalBindingPathElementImpl<T,String>("text",TypeUtils.getBaseClass(getType()),String.class,true,"text") {
			@Override
			public String getBindingValue(T target, BindingEvaluationContext context) 
			{
				logger.warning("Please implement me");
				return "???";
			}
		    @Override
		    public void setBindingValue(String value, T target, BindingEvaluationContext context) 
		    {
				logger.warning("Please implement me");
		    }
		};
		allProperties.add(text);
	}

	@Override
	public List<BindingPathElement> getAllProperties() 
	{
		return allProperties;
	}
	
	public static class ShapePatternRolePathElement<E extends Bindable> extends GraphicalElementPatternRolePathElement<E,ViewShape>
	{
		private FinalBindingPathElementImpl<ViewShape,Double> x;
		private FinalBindingPathElementImpl<ViewShape,Double> y;
		private FinalBindingPathElementImpl<ViewShape,Double> width;
		private FinalBindingPathElementImpl<ViewShape,Double> height;

		public ShapePatternRolePathElement(ShapePatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
			x = new FinalBindingPathElementImpl<ViewShape,Double>("x",TypeUtils.getBaseClass(getType()),Double.class,true,"x") {
				@Override
				public Double getBindingValue(ViewShape target, BindingEvaluationContext context) 
				{
					logger.warning("Please implement me");
					return 0.0;
				}
			    @Override
			    public void setBindingValue(Double value, ViewShape target, BindingEvaluationContext context) 
			    {
					logger.warning("Please implement me");
			    }
			};
			allProperties.add(x);
			y = new FinalBindingPathElementImpl<ViewShape,Double>("y",TypeUtils.getBaseClass(getType()),Double.class,true,"y") {
				@Override
				public Double getBindingValue(ViewShape target, BindingEvaluationContext context) 
				{
					logger.warning("Please implement me");
					return 0.0;
				}
			    @Override
			    public void setBindingValue(Double value, ViewShape target, BindingEvaluationContext context) 
			    {
					logger.warning("Please implement me");
			    }
			};
			allProperties.add(y);
			width = new FinalBindingPathElementImpl<ViewShape,Double>("width",TypeUtils.getBaseClass(getType()),Double.class,true,"width") {
				@Override
				public Double getBindingValue(ViewShape target, BindingEvaluationContext context) 
				{
					logger.warning("Please implement me");
					return 0.0;
				}
			    @Override
			    public void setBindingValue(Double value, ViewShape target, BindingEvaluationContext context) 
			    {
					logger.warning("Please implement me");
			    }
			};
			allProperties.add(width);
			height = new FinalBindingPathElementImpl<ViewShape,Double>("height",TypeUtils.getBaseClass(getType()),Double.class,true,"height") {
				@Override
				public Double getBindingValue(ViewShape target, BindingEvaluationContext context) 
				{
					logger.warning("Please implement me");
					return 0.0;
				}
			    @Override
			    public void setBindingValue(Double value, ViewShape target, BindingEvaluationContext context) 
			    {
					logger.warning("Please implement me");
			    }
			};
			allProperties.add(height);
		}
	}

	public static class ConnectorPatternRolePathElement<E extends Bindable> extends GraphicalElementPatternRolePathElement<E,ViewConnector>
	{
		private GraphicalElementPathElement<ViewShape> fromShape;
		private GraphicalElementPathElement<ViewShape> toShape;

		public ConnectorPatternRolePathElement(ConnectorPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
			fromShape = new GraphicalElementPathElement<ViewShape>("fromShape",this);
			allProperties.add(fromShape);
			toShape = new GraphicalElementPathElement<ViewShape>("toShape",this);
			allProperties.add(toShape);
		}
	}


}