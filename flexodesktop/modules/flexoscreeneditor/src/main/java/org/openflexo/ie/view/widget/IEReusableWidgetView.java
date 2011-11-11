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
package org.openflexo.ie.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.PartialComponentInstance;
import org.openflexo.foundation.ie.cl.PartialComponentDefinition;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.ie.view.IEReusableWidgetComponentView;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.Layoutable;
import org.openflexo.ie.view.controller.IEController;

public class IEReusableWidgetView<D extends IEReusableWidget<C, CI>, CI extends PartialComponentInstance, C extends PartialComponentDefinition>
		extends IEWidgetView<D> {

	private IEReusableWidgetComponentView _reusableWidgetComponentView;

	public IEReusableWidgetView(IEController ieController, D model, boolean addDnDSupport, IEWOComponentView view) {
		super(ieController, model, addDnDSupport, view);
		this.setBackground(Color.WHITE);
		_reusableWidgetComponentView = new IEReusableWidgetComponentView(getIEController(), model.getReusableComponentInstance());
		;
		setLayout(new BorderLayout());
		add(_reusableWidgetComponentView);
		ieController.getSelectionManager().addToSelectionListeners(_reusableWidgetComponentView);
		validate();
	}

	@Override
	public IEWidgetView findViewForModel(IEObject object) {
		return _reusableWidgetComponentView.findViewForModel(object);
	}

	@Override
	public void delete() {
		getIEController().getIESelectionManager().removeFromSelectionListeners(getReusableWidgetComponentView());
		super.delete();
	}

	/**
	 * Overrides propagateResize
	 * 
	 * @see org.openflexo.ie.view.widget.IEWidgetView#propagateResize()
	 */
	@Override
	public void propagateResize() {
		Component[] c = getComponents();
		for (int i = 0; i < c.length; i++) {
			if (c[i] instanceof Layoutable)
				((Layoutable) c[i]).propagateResize();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		if (getHoldsNextComputedPreferredSize()) {
			Dimension storedSize = storedPrefSize();
			if (storedSize != null)
				return storedSize;
		}
		Dimension d = _reusableWidgetComponentView.getPreferredSize();
		if (getHoldsNextComputedPreferredSize())
			storePrefSize(d);
		return d;
	}

	@Override
	public void update(FlexoObservable o, DataModification arg) {
		if (arg instanceof ObjectDeleted && o == getModel()) {
			delete();
		} else
			super.update(o, arg);

	}

	public CI getComponentInstance() {
		return getModel().getReusableComponentInstance();
	}

	public IEReusableWidgetComponentView getReusableWidgetComponentView() {
		return _reusableWidgetComponentView;
	}

	/*public void setHTMLTableWidget(IEHTMLTableWidget htmlTable) {
		if(getViewForRootWidget() instanceof IESequenceTRWidgetView){
		Enumeration<IETDWidgetView> en = ((IESequenceTRWidgetView)getViewForRootWidget()).getAllTDView().elements();
		while(en.hasMoreElements()){
			en.nextElement().setHTMLTableWidget(htmlTable);
		}
		}
		
	}*/
}
