package org.openflexo.foundation.viewpoint.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.KeyValueLibrary;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.ShapePatternRole;

public abstract class GraphicalElementPatternRolePathElement<T extends ViewObject> extends PatternRolePathElement<T> {
	private static final Logger logger = Logger.getLogger(GraphicalElementPatternRolePathElement.class.getPackage().getName());

	private List<BindingPathElement> allProperties;

	public GraphicalElementPatternRolePathElement(GraphicalElementPatternRole aPatternRole, Bindable container) {
		super(aPatternRole, container);
		allProperties = new ArrayList<BindingPathElement>(KeyValueLibrary.getAccessibleProperties(TypeUtils.getBaseClass(getType())));
	}

	@Override
	public List<BindingPathElement> getAllProperties() {
		return allProperties;
	}

	public static class ShapePatternRolePathElement<E extends Bindable> extends GraphicalElementPatternRolePathElement<ViewShape> {

		public ShapePatternRolePathElement(ShapePatternRole aPatternRole, E container) {
			super(aPatternRole, container);
		}
	}

	public static class ConnectorPatternRolePathElement extends GraphicalElementPatternRolePathElement<ViewConnector> {

		public ConnectorPatternRolePathElement(ConnectorPatternRole aPatternRole, Bindable container) {
			super(aPatternRole, container);
		}
	}

}