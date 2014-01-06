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
package org.openflexo.foundation.ie.widget;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.HTMLListDescriptorCollection;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IETopComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.TopComponentContainer;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.dm.TopComponentInserted;
import org.openflexo.foundation.ie.dm.TopComponentRemoved;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

@Deprecated
public class IESequenceTopComponent extends IESequence<IETopComponent> implements IETopComponent {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(IESequenceTopComponent.class.getPackage().getName());

	@Deprecated
	public IESequenceTopComponent(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IESequenceTopComponent(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	@Override
	public void addToInnerWidgets(IETopComponent w) {
		super.addToInnerWidgets(w);

	}

	/**
	 * Overrides removeFromInnerWidgets
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#removeFromInnerWidgets(org.openflexo.foundation.ie.widget.IWidget, boolean)
	 */
	@Override
	public void removeFromInnerWidgets(IETopComponent w, boolean deleteIfEmpty) {
		super.removeFromInnerWidgets(w, deleteIfEmpty);
		TopComponentRemoved tcr = new TopComponentRemoved(w);
		setChanged();
		notifyObservers(tcr);
		if (getParent() instanceof TopComponentContainer) {
			((TopComponentContainer) getParent()).notifyTopComponentRemoved(tcr);
		}
		// getWOComponent().getAbstractComponentDefinition().notifyStructureChanged();
	}

	@Override
	public void insertElementAt(IETopComponent o, int i) {
		super.insertElementAt(o, i);
		TopComponentInserted tci = new TopComponentInserted(o);
		setChanged();
		notifyObservers(tci);
		if (getParent() instanceof TopComponentContainer) {
			((TopComponentContainer) getParent()).notifyTopComponentInserted(tci);
		}
	}

	@Override
	public boolean isSubsequence() {
		return getParent() instanceof IESequenceTopComponent;
	}

	public HTMLListDescriptorCollection getAllHTMLTableList() {
		HTMLListDescriptorCollection v = new HTMLListDescriptorCollection();
		Enumeration en = elements();
		while (en.hasMoreElements()) {
			IETopComponent tcc = (IETopComponent) en.nextElement();
			if (tcc instanceof IEBlocWidget) {
				IEBlocWidget w = (IEBlocWidget) tcc;
				HTMLListDescriptor d = HTMLListDescriptor.createInstanceForBloc(w);
				if (d != null) {
					v.add(d);
				}
			} else if (tcc instanceof IESequenceTopComponent) {
				v.addAll(((IESequenceTopComponent) tcc).getAllHTMLTableList());
			}
		}
		return v;
	}

	/**
	 * Overrides getAllNonSequenceWidget
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#getAllNonSequenceWidget()
	 */
	@Override
	public Vector<IWidget> getAllNonSequenceWidget() {
		Vector<IWidget> v = new Vector<IWidget>();
		Enumeration<IETopComponent> en = elements();
		while (en.hasMoreElements()) {
			IETopComponent element = en.nextElement();
			if (element instanceof IESequenceTab) {
				v.add(element);
			} else {
				v.addAll(element.getAllNonSequenceWidget());
			}
		}
		return v;
	}

	@Override
	public Vector<IESequenceTab> getAllTabContainers() {
		Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
		Enumeration en = elements();
		while (en.hasMoreElements()) {
			IETopComponent top = (IETopComponent) en.nextElement();
			if (top instanceof IESequenceTab) {
				reply.add((IESequenceTab) top);
			}
			reply.addAll(top.getAllTabContainers());
		}
		return reply;

	}

	public Vector<TabComponentDefinition> getAvailableTabs() {
		Vector<TabComponentDefinition> reply = new Vector();
		Enumeration<IETopComponent> en = elements();
		while (en.hasMoreElements()) {
			IETopComponent top = en.nextElement();
			if (top instanceof IESequenceTab) {
				IESequenceTab w = (IESequenceTab) top;
				Enumeration en1 = w.getAllTabs().elements();
				while (en1.hasMoreElements()) {
					IETabWidget tab = (IETabWidget) en1.nextElement();
					reply.add(tab.getTabComponentDefinition());
				}
			} else if (top instanceof IESequenceTopComponent) {
				reply.addAll(((IESequenceTopComponent) top).getAvailableTabs());
			}
		}
		return reply;
	}

	/**
	 * Overrides getTitle
	 * 
	 * @see org.openflexo.foundation.ie.IETopComponent#getTitle()
	 */
	@Override
	public String getTitle() {
		return getLabel();
	}
}
