package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.FinalBindingPathElementImpl;
import org.openflexo.antar.binding.KeyValueLibrary;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.localization.FlexoLocalization;

public abstract class GraphicalElementPathElement<T extends ViewObject> implements SimplePathElement<T>, BindingVariable<T> {
	private static final Logger logger = Logger.getLogger(GraphicalElementPathElement.class.getPackage().getName());

	private String name;
	private BindingPathElement parentElement;
	protected List<BindingPathElement> allProperties;
	// private GraphicalElementPathElement<ViewObject> parent;
	private FinalBindingPathElementImpl<String> text;

	public GraphicalElementPathElement(String name, BindingPathElement aParentElement) {
		super();
		this.name = name;
		allProperties = new ArrayList<BindingPathElement>(KeyValueLibrary.getAccessibleProperties(TypeUtils.getBaseClass(getType())));
	}

	public List<BindingPathElement> getAllProperties() {
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

		public ShapePathElement(String name, BindingPathElement aParent) {
			super(name, aParent);
		}

		@Override
		public Type getType() {
			return ViewShape.class;
		}

	}

	public static class ConnectorPathElement extends GraphicalElementPathElement<ViewConnector> {

		public ConnectorPathElement(String name, BindingPathElement aParent) {
			super(name, aParent);
		}

		@Override
		public Type getType() {
			return ViewConnector.class;
		}

	}

	public static class ViewPathElement extends GraphicalElementPathElement<View> {

		public ViewPathElement(String name, BindingPathElement aParent) {
			super(name, aParent);
		}

		@Override
		public Type getType() {
			return View.class;
		}

	}

}