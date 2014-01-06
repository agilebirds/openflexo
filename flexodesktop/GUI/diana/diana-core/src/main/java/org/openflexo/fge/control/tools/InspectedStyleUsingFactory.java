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
import java.beans.PropertyChangeListener;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.GRParameter;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.model.factory.KeyValueCoding;

/**
 * 
 * @author sylvain
 * 
 * @param <F>
 * @param <S>
 * @param <ST>
 */
public abstract class InspectedStyleUsingFactory<F extends StyleFactory<S, ST>, S extends KeyValueCoding, ST> extends InspectedStyle<S> {

	private F styleFactory;

	private FactoryPropertyChangeListener factoryListener;

	public InspectedStyleUsingFactory(DianaInteractiveViewer<?, ?, ?> controller, F styleFactory) {
		super(controller, styleFactory.makeNewStyle(null));
		this.styleFactory = styleFactory;
		factoryListener = new FactoryPropertyChangeListener();
		styleFactory.getPropertyChangeSupport().addPropertyChangeListener(factoryListener);
	}

	@Override
	public boolean delete() {
		styleFactory.getPropertyChangeSupport().removePropertyChangeListener(factoryListener);
		return super.delete();
	}

	public F getStyleFactory() {
		return styleFactory;
	}

	@Override
	public S cloneStyle() {
		return styleFactory.makeNewStyle(null);
	}

	protected Class<? extends S> getInspectedStyleClass() {
		if (getSelection().size() == 0) {
			return (Class<? extends S>) getStyleFactory().getCurrentStyle().getClass();
		} else {
			S style = getStyle(getSelection().get(0));
			if (style != null) {
				return (Class<? extends S>) style.getClass();
			}
			return null;
		}
	}

	public <T> T getPropertyValue(GRParameter<T> parameter) {
		InspectedStyle<? extends S> currentlyInspected = styleFactory.getCurrentStyle();
		if (parameter.getDeclaringClass().isAssignableFrom(currentlyInspected.getClass())) {
			return currentlyInspected.getPropertyValue(parameter);
		}
		return null;
	}

	@Override
	public <T> void setPropertyValue(GRParameter<T> parameter, T value) {
		InspectedStyle<? extends S> currentlyInspected = styleFactory.getCurrentStyle();
		if (parameter.getDeclaringClass().isAssignableFrom(currentlyInspected.getClass())) {
			currentlyInspected.setPropertyValue(parameter, value);
		}
	}

	@Override
	protected <T> void fireChangedProperty(GRParameter<T> parameter) {
		InspectedStyle<? extends S> currentlyInspected = styleFactory.getCurrentStyle();
		if (parameter.getDeclaringClass().isAssignableFrom(currentlyInspected.getClass())) {
			currentlyInspected.fireChangedProperty(parameter);
		}
	}

	public ST getStyleType() {

		if (getSelection().size() == 0) {
			return getStyleType(getDefaultValue());
		} else {
			return getStyleType(getStyle(getSelection().get(0)));
		}
	}

	protected abstract ST getStyleType(S style);

	public void fireSelectionUpdated() {
		// System.out.println("Selection mise a jour, je veux un " + getStyleType() + " alors que je suis a "
		// + getStyleFactory().getStyleType());

		if (requireChange(getStyleFactory().getStyleType(), getStyleType())) {
			getStyleFactory().setStyleType(getStyleType());
		}
		super.fireSelectionUpdated();
	}

	protected void applyNewStyleTypeToSelection(ST newStyleType) {
		// System.out.println("Changing for " + newStyleType);
		for (DrawingTreeNode<?, ?> n : getSelection()) {
			S nodeStyle = getStyle(n);
			if (getStyleType(nodeStyle) != newStyleType) {
				applyNewStyle(newStyleType, n);
			}
		}
	}

	protected abstract void applyNewStyle(ST aStyleType, DrawingTreeNode<?, ?> node);

	protected class FactoryPropertyChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			System.out.println("Tiens, on me previent que " + evt + " pour " + evt.getPropertyName());
			if (evt.getPropertyName().equals(StyleFactory.STYLE_CLASS_CHANGED)) {
				if (getSelection().size() == 0) {
					// In this case style type should be applied as default value, which should be recomputed
					setDefaultValue(cloneStyle());
				} else {
					applyNewStyleTypeToSelection((ST) evt.getNewValue());
				}
				// We should now force notify all properties related to new style
				for (GRParameter<?> p : GRParameter.getGRParameters(getInspectedStyleClass())) {
					forceFireChangedProperty(p);
				}
			}

			/*Class<?> inspectedStyleClass = getStyleFactory().getCurrentStyle().getClass();
			GRParameter param = GRParameter.getGRParameter(inspectedStyleClass, evt.getPropertyName());
			System.out.println("Found param = " + param);
			if (param != null) {
				setPropertyValue(param, evt.getNewValue());
			}*/
		}
	}

}
