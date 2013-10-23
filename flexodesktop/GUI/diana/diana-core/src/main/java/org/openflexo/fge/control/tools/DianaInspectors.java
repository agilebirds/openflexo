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
import org.openflexo.fge.view.widget.FIBShapeSelector.ShapeFactory;

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

	// protected InspectedForegroundStyle inspectedForegroundStyle;

	protected BackgroundStyleFactory bsFactory;
	protected ShapeFactory shapeFactory;

	public DianaInspectors() {
		bsFactory = new BackgroundStyleFactory(null);
		shapeFactory = new ShapeFactory(null);
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

	/*@Override
	public void attachToEditor(AbstractDianaEditor<?, F, ?> editor) {
		super.attachToEditor(editor);
		inspectedForegroundStyle = new InspectedForegroundStyle((DianaInteractiveViewer<?, F, ?>) editor);
	}*/

	public abstract Inspector<ForegroundStyle> getForegroundStyleInspector();

	public abstract Inspector<BackgroundStyleFactory> getBackgroundStyleInspector();

	public abstract Inspector<TextStyle> getTextStyleInspector();

	public abstract Inspector<ShadowStyle> getShadowStyleInspector();

	public abstract Inspector<ShapeFactory> getShapeInspector();

	public static interface Inspector<D> {
		public void setData(D data);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		/*if (evt.getPropertyName().equals(ObjectAddedToSelection.EVENT_NAME)
				|| evt.getPropertyName().equals(ObjectRemovedFromSelection.EVENT_NAME)
				|| evt.getPropertyName().equals(SelectionCleared.EVENT_NAME)) {
			updateSelection();
		}*/
	}

	private void updateSelection() {
		// System.out.println("Hop, le FS c'est " + inspectedForegroundStyle);
		// getForegroundStyleInspector().setData(inspectedForegroundStyle);
		// inspectedForegroundStyle.fireSelectionUpdated();
		if (getSelection().size() > 0) {
			// getTextStyleInspector().setData(getSelection().get(0).getTextStyle());
			/*if (getSelectedShapes().size() > 0) {
				getForegroundStyleInspector().setData(getSelectedShapes().get(0).getGraphicalRepresentation().getForeground());
			} else if (getSelectedConnectors().size() > 0) {
				getForegroundStyleInspector().setData(getSelectedConnectors().get(0).getGraphicalRepresentation().getForeground());
			}*/
		} else {
			// getTextStyleInspector().setData(getEditor().getCurrentTextStyle());
			// getForegroundStyleInspector().setData(getEditor().getCurrentForegroundStyle());
		}
		if (getSelectedShapes().size() > 0) {
			shapeFactory.setShape(getSelectedShapes().get(0).getGraphicalRepresentation().getShapeSpecification());
			// getShapeInspector().setData(getSelectedShapes().get(0).getGraphicalRepresentation().getShapeSpecification());
			bsFactory.setBackgroundStyle(getSelectedShapes().get(0).getGraphicalRepresentation().getBackground());
			// getBackgroundStyleInspector().setData(getSelectedShapes().get(0).getGraphicalRepresentation().getBackground());
			// getShadowStyleInspector().setData(getSelectedShapes().get(0).getGraphicalRepresentation().getShadowStyle());
		} else {
			shapeFactory.setShape(getEditor().getCurrentShape());
			// getShapeInspector().setData(getEditor().getCurrentShape());
			bsFactory.setBackgroundStyle(getEditor().getCurrentBackgroundStyle());
			// getBackgroundStyleInspector().setData(getEditor().getCurrentBackgroundStyle());
			// getShadowStyleInspector().setData(getEditor().getCurrentShadowStyle());
		}
	}

}
