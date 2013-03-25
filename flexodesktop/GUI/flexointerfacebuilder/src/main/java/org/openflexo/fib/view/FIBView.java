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
package org.openflexo.fib.view;

import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBComponentDynamicModel;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.widget.FIBReferencedComponentWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizationListener;

public abstract class FIBView<M extends FIBComponent, J extends JComponent> implements LocalizationListener {

	private static final Logger logger = Logger.getLogger(FIBView.class.getPackage().getName());

	protected static final int TOP_COMPENSATING_BORDER = 3;
	protected static final int BOTTOM_COMPENSATING_BORDER = TOP_COMPENSATING_BORDER;
	protected static final int LEFT_COMPENSATING_BORDER = 5;
	protected static final int RIGHT_COMPENSATING_BORDER = LEFT_COMPENSATING_BORDER;

	private M component;
	private FIBController controller;
	private final FIBComponentDynamicModel dynamicModel;

	protected Vector<FIBView> subViews;

	private boolean visible = true;

	private boolean isDeleted = false;

	private JScrollPane scrolledComponent;

	private FIBReferencedComponentWidget embeddingComponent;

	public FIBView(M model, FIBController controller) {
		super();
		this.controller = controller;
		component = model;

		subViews = new Vector<FIBView>();

		dynamicModel = createDynamicModel();

		controller.registerView(this);

	}

	public void delete() {

		logger.fine("@@@@@@@@@ Delete view for component " + getComponent());

		if (isDeleted) {
			return;
		}
		logger.fine("Delete view for component " + getComponent());
		if (subViews != null) {
			for (FIBView v : subViews) {
				v.delete();
			}
			subViews.clear();
			subViews = null;
		}
		if (controller != null) {
			controller.unregisterView(this);
		}
		if (dynamicModel != null) {
			dynamicModel.delete();
		}
		isDeleted = true;
		component = null;
		controller = null;
	}

	public JComponent getJComponentForObject(FIBComponent component) {
		if (getComponent() == component) {
			return getJComponent();
		} else {
			for (FIBView v : getSubViews()) {
				JComponent j = v.getJComponentForObject(component);
				if (j != null) {
					return j;
				}
			}
		}
		return null;
	}

	public JComponent geDynamicJComponentForObject(FIBComponent component) {
		if (getComponent() == component) {
			return getDynamicJComponent();
		} else {
			for (FIBView v : getSubViews()) {
				JComponent j = v.geDynamicJComponentForObject(component);
				if (j != null) {
					return j;
				}
			}
		}
		return null;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public FIBController getController() {
		return controller;
	}

	public BindingEvaluationContext getBindingEvaluationContext() {
		if (getParentView() != null) {
			return getParentView().getBindingEvaluationContext();
		}
		if (getComponent() != null && getComponent().getName() != null && getComponent().getName().equals("DropSchemePanel")) {
			if (getEmbeddingComponent() == null) {
				System.out.println("for DropSchemePanel embedding component is " + getEmbeddingComponent());
			}
		}
		/*if (getComponent() != null && getComponent().getName() != null && getComponent().getName().equals("DropSchemeWidget")) {
			System.out.println("for DropSchemeWidget embedding component is " + getEmbeddingComponent());
		}*/
		if (getEmbeddingComponent() != null) {
			return getEmbeddingComponent().getEmbeddedBindingEvaluationContext();
		}
		return getController();
	}

	public final Object getDataObject() {
		return getController().getDataObject();
	}

	public final M getComponent() {
		return component;
	}

	public abstract void updateDataObject(Object anObject);

	public abstract void updateLanguage();

	/**
	 * Return the effective base component to be added to swing hierarchy This component may be encapsulated in a JScrollPane
	 * 
	 * @return JComponent
	 */
	public abstract JComponent getJComponent();

	/**
	 * Return the dynamic JComponent, ie the component on which dynamic is applied, and were actions are effective
	 * 
	 * @return J
	 */
	public abstract J getDynamicJComponent();

	/**
	 * Return the effective component to be added to swing hierarchy This component may be the same as the one returned by
	 * {@link #getJComponent()} or a encapsulation in a JScrollPane
	 * 
	 * @return JComponent
	 */
	public JComponent getResultingJComponent() {
		if (getComponent().getUseScrollBar()) {
			if (scrolledComponent == null) {
				scrolledComponent = new JScrollPane(getJComponent(), getComponent().getVerticalScrollbarPolicy().getPolicy(),
						getComponent().getHorizontalScrollbarPolicy().getPolicy());
				scrolledComponent.setOpaque(false);
				scrolledComponent.getViewport().setOpaque(false);
				scrolledComponent.setBorder(BorderFactory.createEmptyBorder());
			}
			return scrolledComponent;
		} else {
			return getJComponent();
		}
	}

	/**
	 * This method is called to update view representing a FIBComponent.<br>
	 * Callers are all the components that have been updated during current update loop. If the callers contains the component itself, does
	 * nothing and return.
	 * 
	 * @param callers
	 *            all the components that have been previously updated during current update loop
	 * @return a flag indicating if component has been updated
	 */
	public boolean update(List<FIBComponent> callers) {
		if (callers.contains(getComponent())) {
			return false;
		}
		updateVisibility();
		return true;
	}

	protected abstract boolean checkValidDataPath();

	public final boolean isComponentVisible() {
		/*if (getComponent().getName() != null && getComponent().getName().equals("DropSchemePanel")) {
			System.out.println("Bon, je me demande si c'est visible");
			System.out.println("getComponent().getVisible()=" + getComponent().getVisible());
			System.out.println("valid=" + getComponent().getVisible().isValid());
			System.out.println("getBindingEvaluationContext=" + getBindingEvaluationContext());
			try {
				System.out.println("result=" + getComponent().getVisible().getBindingValue(getBindingEvaluationContext()));
				DataBinding<Object> binding1 = new DataBinding<Object>("data", getComponent(), Object.class, BindingDefinitionType.GET);
				System.out.println("data=" + binding1.getBindingValue(getBindingEvaluationContext()));
				DataBinding<Object> binding2 = new DataBinding<Object>("EditionActionBrowser.selected", getComponent(), Object.class,
						BindingDefinitionType.GET);
				System.out.println("EditionActionBrowser.selected=" + binding2.getBindingValue(getBindingEvaluationContext()));
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/

		if (getParentView() != null && !getParentView().isComponentVisible()) {
			return false;
		}

		boolean componentVisible = true;
		if (getComponent().getVisible() != null && getComponent().getVisible().isSet()) {
			try {
				Boolean isVisible = getComponent().getVisible().getBindingValue(getBindingEvaluationContext());
				if (isVisible != null) {
					componentVisible = isVisible;
				}
			} catch (TypeMismatchException e) {
				logger.warning("Unable to evaluate " + getComponent().getVisible());
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// NullReferenceException is allowed, in this case, default visibility is true
				componentVisible = true;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				componentVisible = true;
			}
		}
		return componentVisible;
	}

	public final boolean hasValue() {
		return component.getData() != null && component.getData().isSet();
	}

	protected boolean isVisible() {
		return visible;
	}

	protected void updateVisibility() {
		if (isComponentVisible() != visible) {
			visible = !visible;
			getResultingJComponent().setVisible(visible);
			if (getResultingJComponent().getParent() instanceof JComponent) {
				((JComponent) getResultingJComponent().getParent()).revalidate();
			} else if (getResultingJComponent().getParent() != null) {
				getResultingJComponent().getParent().validate();
			}
			if (getResultingJComponent().getParent() != null) {
				getResultingJComponent().getParent().repaint();
			}
			if (visible) {
				for (FIBView<?, ?> view : subViews) {
					view.updateVisibility();
				}
			}
			if (getDynamicModel() != null) {
				getDynamicModel().setVisible(visible);
			}
		}
	}

	public FIBComponentDynamicModel<?> createDynamicModel() {
		if (getComponent().getDataType() != null) {
			logger.fine("Create dynamic model " + this + " for " + getComponent());
			return buildDynamicModel(TypeUtils.getBaseClass(getComponent().getDataType()));
		}
		return null;
	}

	private <T> FIBComponentDynamicModel<T> buildDynamicModel(Class<T> type) {
		return new FIBComponentDynamicModel<T>((T) getDefaultData());
	}

	public Object getDefaultData() {
		return null;
	}

	public FIBComponentDynamicModel getDynamicModel() {
		return dynamicModel;
	}

	public void notifyDynamicModelChanged() {
		// System.out.println("notifyDynamicModelChanged()");
		if (getComponent() != null) {
			Iterator<FIBComponent> it = getComponent().getMayAltersIterator();
			while (it.hasNext()) {
				FIBComponent c = it.next();
				logger.fine("Because dynamic model change, now update " + c);
				FIBView view = getController().viewForComponent(c);
				if (view != null) {
					view.updateDataObject(getDataObject());
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Unexpected null view when retrieving view for " + c);
					}
				}
			}
		} else {
			logger.warning("Unexpected null component");
		}
	}

	public FIBView<?, ?> getParentView() {
		if (getComponent().getParent() != null) {
			return getController().viewForComponent(getComponent().getParent());
		}
		return null;
	}

	public Vector<FIBView> getSubViews() {
		return subViews;
	}

	public Font getFont() {
		if (getComponent() != null) {
			return getComponent().retrieveValidFont();
		}
		return null;
	}

	public abstract void updateFont();

	public String getLocalized(String key) {
		if (getController().getLocalizerForComponent(getComponent()) != null) {
			return FlexoLocalization.localizedForKey(getController().getLocalizerForComponent(getComponent()), key);
		}
		return key;
	}

	public boolean isSelectableComponent() {
		return this instanceof FIBSelectable;
	}

	public FIBSelectable getSelectableComponent() {
		if (isSelectableComponent()) {
			return (FIBSelectable) this;
		}
		return null;
	}

	@Override
	public void languageChanged(Language language) {
		updateLanguage();
	}

	public void updateGraphicalProperties() {
		updatePreferredSize();
		updateMaximumSize();
		updateMinimumSize();
		updateBackgroundColor();
		updateForegroundColor();
	}

	protected void updatePreferredSize() {
		if (getComponent().definePreferredDimensions()) {
			Dimension preferredSize = getJComponent().getPreferredSize();
			if (getComponent().getWidth() != null) {
				preferredSize.width = getComponent().getWidth();
			}
			if (getComponent().getHeight() != null) {
				preferredSize.height = getComponent().getHeight();
			}
			getJComponent().setPreferredSize(preferredSize);
		}
	}

	protected void updateMinimumSize() {
		if (getComponent().defineMinDimensions()) {
			Dimension minSize = getJComponent().getMinimumSize();
			if (getComponent().getMinWidth() != null) {
				minSize.width = getComponent().getMinWidth();
			}
			if (getComponent().getMinHeight() != null) {
				minSize.height = getComponent().getMinHeight();
			}
			getJComponent().setMinimumSize(minSize);
		}
	}

	protected void updateMaximumSize() {
		if (getComponent().defineMaxDimensions()) {
			Dimension maxSize = getJComponent().getMaximumSize();
			if (getComponent().getMaxWidth() != null) {
				maxSize.width = getComponent().getMaxWidth();
			}
			if (getComponent().getMaxHeight() != null) {
				maxSize.height = getComponent().getMaxHeight();
			}
			getJComponent().setMinimumSize(maxSize);
		}
	}

	protected void updateBackgroundColor() {
		getJComponent().setOpaque(getComponent().getOpaque());
		getJComponent().setBackground(getComponent().getBackgroundColor());
	}

	protected void updateForegroundColor() {
		getJComponent().setForeground(getComponent().getForegroundColor());
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}

	public static boolean notEquals(Object o1, Object o2) {
		return !equals(o1, o2);
	}

	public FIBReferencedComponentWidget getEmbeddingComponent() {
		return embeddingComponent;
	}

	public void setEmbeddingComponent(FIBReferencedComponentWidget embeddingComponent) {
		if (getComponent() != null && getComponent().getName() != null && getComponent().getName().equals("DropSchemePanel")) {
			System.out.println("Set emmbedding component for DropSchemePanel with " + embeddingComponent);
		}
		this.embeddingComponent = embeddingComponent;
	}
}
