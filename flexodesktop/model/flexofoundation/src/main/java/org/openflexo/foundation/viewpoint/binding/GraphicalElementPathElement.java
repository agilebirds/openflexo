package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.FinalBindingPathElementImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.localization.FlexoLocalization;

public class GraphicalElementPathElement<T extends ViewObject> implements SimplePathElement<T>, BindingVariable<T> {
	private static final Logger logger = Logger.getLogger(GraphicalElementPathElement.class.getPackage().getName());

	private String name;
	private BindingPathElement parentElement;
	protected Vector<BindingPathElement> allProperties;
	// private GraphicalElementPathElement<ViewObject> parent;
	private FinalBindingPathElementImpl<String> text;

	public GraphicalElementPathElement(String name, BindingPathElement aParentElement) {
		super();
		this.name = name;
		parentElement = aParentElement;
		allProperties = new Vector<BindingPathElement>();
		// parent = new GraphicalElementPathElement<ViewObject>("parent",this);
		// allProperties.add(parent);
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

	public Vector<BindingPathElement> getAllProperties() {
		return allProperties;
	}

	@Override
	public Class<? extends Bindable> getDeclaringClass() {
		if (parentElement != null) {
			return TypeUtils.getBaseClass(parentElement.getType());
		}
		return Bindable.class;
	}

	@Override
	public String getSerializationRepresentation() {
		return getLabel();
	}

	@Override
	public boolean isBindingValid() {
		return true;
	}

	@Override
	public Type getType() {
		return ViewObject.class;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return FlexoLocalization.localizedForKey("view_object");
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public T getBindingValue(Object target, BindingEvaluationContext context) {
		if (target instanceof ViewElement) {
			if (name != null && name.equals("parent")) {
				return (T) ((ViewElement) target).getParent();
			}
		}
		logger.warning("Unexpected " + target + " name=" + name);
		return null;
	}

	@Override
	public void setBindingValue(T value, Object target, BindingEvaluationContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public Bindable getContainer() {
		return null;
	}

	@Override
	public String getVariableName() {
		return getLabel();
	}

	public static class ShapePathElement extends GraphicalElementPathElement<ViewShape> {
		private FinalBindingPathElementImpl<Double> x;
		private FinalBindingPathElementImpl<Double> y;
		private FinalBindingPathElementImpl<Double> width;
		private FinalBindingPathElementImpl<Double> height;

		public ShapePathElement(String name, BindingPathElement aParent) {
			super(name, aParent);
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

		@Override
		public Type getType() {
			return ViewShape.class;
		}

	}

	public static class ConnectorPathElement extends GraphicalElementPathElement<ViewConnector> {
		private GraphicalElementPathElement<ViewShape> fromShape;
		private GraphicalElementPathElement<ViewShape> toShape;

		public ConnectorPathElement(String name, BindingPathElement aParent) {
			super(name, aParent);
			fromShape = new GraphicalElementPathElement<ViewShape>("fromShape", this);
			allProperties.add(fromShape);
			toShape = new GraphicalElementPathElement<ViewShape>("toShape", this);
			allProperties.add(toShape);
		}

		@Override
		public Type getType() {
			return ViewConnector.class;
		}

	}

	public static class ViewPathElement extends GraphicalElementPathElement<View> {
		private FinalBindingPathElementImpl<Double> width;
		private FinalBindingPathElementImpl<Double> height;

		public ViewPathElement(String name, BindingPathElement aParent) {
			super(name, aParent);
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

		@Override
		public Type getType() {
			return View.class;
		}

	}

}