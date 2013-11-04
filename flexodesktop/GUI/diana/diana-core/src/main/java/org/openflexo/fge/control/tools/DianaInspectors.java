/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fge.control.tools;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.tools.DianaInspectors.Inspector;
import org.openflexo.fge.view.DianaViewFactory;

/**
 * Represents a tool allowing to manage style inspectors
 * 
 * @author sylvain
 * 
 * @param <C>
 * @param <F>
 * @param <ME>
 */
public abstract class DianaInspectors<C extends Inspector<?>, F extends DianaViewFactory<F, ?>> extends DianaToolImpl<C, F> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaInspectors.class.getPackage().getName());

	public DianaInspectors() {
	}

	@Override
	public DianaInteractiveEditor<?, F, ?> getEditor() {
		return (DianaInteractiveEditor<?, F, ?>) super.getEditor();
	}

	public InspectedForegroundStyle getInspectedForegroundStyle() {
		if (getEditor() != null) {
			return getEditor().getInspectedForegroundStyle();
		}
		return null;
	}

	public InspectedTextStyle getInspectedTextStyle() {
		if (getEditor() != null) {
			return getEditor().getInspectedTextStyle();
		}
		return null;
	}

	public InspectedShadowStyle getInspectedShadowStyle() {
		if (getEditor() != null) {
			return getEditor().getInspectedShadowStyle();
		}
		return null;
	}

	public InspectedBackgroundStyle getInspectedBackgroundStyle() {
		if (getEditor() != null) {
			return getEditor().getInspectedBackgroundStyle();
		}
		return null;
	}

	public InspectedShapeSpecification getInspectedShapeSpecification() {
		if (getEditor() != null) {
			return getEditor().getInspectedShapeSpecification();
		}
		return null;
	}

	public InspectedConnectorSpecification getInspectedConnectorSpecification() {
		if (getEditor() != null) {
			return getEditor().getInspectedConnectorSpecification();
		}
		return null;
	}

	public InspectedLocationSizeProperties getInspectedLocationSizeProperties() {
		if (getEditor() != null) {
			return getEditor().getInspectedLocationSizeProperties();
		}
		return null;
	}

	public abstract Inspector<ForegroundStyle> getForegroundStyleInspector();

	public abstract Inspector<BackgroundStyleFactory> getBackgroundStyleInspector();

	public abstract Inspector<TextStyle> getTextStyleInspector();

	public abstract Inspector<ShadowStyle> getShadowStyleInspector();

	public abstract Inspector<ShapeSpecificationFactory> getShapeInspector();

	public abstract Inspector<ConnectorSpecificationFactory> getConnectorInspector();

	public abstract Inspector<InspectedLocationSizeProperties> getLocationSizeInspector();

	public static interface Inspector<D> {
		public void setData(D data);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

}
