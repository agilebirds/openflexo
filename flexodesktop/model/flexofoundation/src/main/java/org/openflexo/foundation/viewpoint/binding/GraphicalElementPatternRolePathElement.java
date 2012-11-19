package org.openflexo.foundation.viewpoint.binding;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FinalBindingPathElementImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.ShapePatternRole;

public abstract class GraphicalElementPatternRolePathElement<T extends ViewObject> extends PatternRolePathElement<T> {
	private static final Logger logger = Logger.getLogger(GraphicalElementPatternRolePathElement.class.getPackage().getName());

	protected List<BindingPathElement> allProperties;
	private FinalBindingPathElementImpl<String> text;
	private GraphicalElementPathElement<ViewObject> parent;

	public GraphicalElementPatternRolePathElement(GraphicalElementPatternRole aPatternRole, Bindable container) {
		super(aPatternRole, container);
		allProperties = new Vector<BindingPathElement>();
		parent = new GraphicalElementPathElement<ViewObject>("parent", this);
		allProperties.add(parent);
		text = new FinalBindingPathElementImpl<String>("text", TypeUtils.getBaseClass(getType()), String.class, true, "text") {
			@Override
			public String getBindingValue(Object target, BindingEvaluationContext context) {
				logger.warning("Please implement me");
				return "???";
			}

			@Override
			public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
				logger.warning("Please implement me");
			}
		};
		allProperties.add(text);
	}

	@Override
	public List<BindingPathElement> getAllProperties() {
		return allProperties;
	}

	public static class ShapePatternRolePathElement<E extends Bindable> extends GraphicalElementPatternRolePathElement<ViewShape> {
		private FinalBindingPathElementImpl<Double> x;
		private FinalBindingPathElementImpl<Double> y;
		private FinalBindingPathElementImpl<Double> width;
		private FinalBindingPathElementImpl<Double> height;

		public ShapePatternRolePathElement(ShapePatternRole aPatternRole, E container) {
			super(aPatternRole, container);
			x = new FinalBindingPathElementImpl<Double>("x", TypeUtils.getBaseClass(getType()), Double.class, true, "x") {
				@Override
				public Double getBindingValue(Object target, BindingEvaluationContext context) {
					logger.warning("Please implement me");
					return 0.0;
				}

				@Override
				public void setBindingValue(Double value, Object target, BindingEvaluationContext context) {
					logger.warning("Please implement me");
				}
			};
			allProperties.add(x);
			y = new FinalBindingPathElementImpl<Double>("y", TypeUtils.getBaseClass(getType()), Double.class, true, "y") {
				@Override
				public Double getBindingValue(Object target, BindingEvaluationContext context) {
					logger.warning("Please implement me");
					return 0.0;
				}

				@Override
				public void setBindingValue(Double value, Object target, BindingEvaluationContext context) {
					logger.warning("Please implement me");
				}
			};
			allProperties.add(y);
			width = new FinalBindingPathElementImpl<Double>("width", TypeUtils.getBaseClass(getType()), Double.class, true, "width") {
				@Override
				public Double getBindingValue(Object target, BindingEvaluationContext context) {
					logger.warning("Please implement me");
					return 0.0;
				}

				@Override
				public void setBindingValue(Double value, Object target, BindingEvaluationContext context) {
					logger.warning("Please implement me");
				}
			};
			allProperties.add(width);
			height = new FinalBindingPathElementImpl<Double>("height", TypeUtils.getBaseClass(getType()), Double.class, true, "height") {
				@Override
				public Double getBindingValue(Object target, BindingEvaluationContext context) {
					logger.warning("Please implement me");
					return 0.0;
				}

				@Override
				public void setBindingValue(Double value, Object target, BindingEvaluationContext context) {
					logger.warning("Please implement me");
				}
			};
			allProperties.add(height);
		}
	}

	public static class ConnectorPatternRolePathElement extends GraphicalElementPatternRolePathElement<ViewConnector> {
		private GraphicalElementPathElement<ViewShape> fromShape;
		private GraphicalElementPathElement<ViewShape> toShape;

		public ConnectorPatternRolePathElement(ConnectorPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
			fromShape = new GraphicalElementPathElement<ViewShape>("fromShape", this);
			allProperties.add(fromShape);
			toShape = new GraphicalElementPathElement<ViewShape>("toShape", this);
			allProperties.add(toShape);
		}
	}

}