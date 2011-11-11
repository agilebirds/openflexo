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
package org.openflexo.ie.view.dkv;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.openflexo.components.tabular.CompoundTabularView;
import org.openflexo.components.tabular.TabularView;
import org.openflexo.components.tabular.TabularViewAction;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.DKVModel.DomainList;
import org.openflexo.foundation.dkv.action.AddDomainAction;
import org.openflexo.foundation.dkv.action.AddKeyAction;
import org.openflexo.foundation.dkv.action.AddLanguageAction;
import org.openflexo.foundation.dkv.action.DKVDelete;
import org.openflexo.foundation.dkv.dm.LanguageAdded;
import org.openflexo.foundation.dkv.dm.LanguageRemoved;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.IESelectionManager;
import org.openflexo.ie.view.dkv.model.DKVDomainTableModel;
import org.openflexo.ie.view.dkv.model.DKVKeyTableModel;
import org.openflexo.selection.SelectionListener;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.SelectionSynchronizedModuleView;

/**
 * @author gpolet
 * 
 */
public class DKVModelView extends CompoundTabularView<DKVModel> implements SelectionSynchronizedModuleView<DKVModel>,
		GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(DKVModelView.class.getPackage().getName());

	private DKVDomainTableModel domainTableModel;
	protected DKVTabularView domainTable;

	private DKVKeyTableModel keyTableModel;
	protected DKVTabularView keyTable;

	public DKVModelView(DKVModel dkvModel, IEController controller) {
		super(dkvModel, controller, "dkv_model");
		dkvModel.addObserver(this);
		addAction(new TabularViewAction(AddLanguageAction.actionType, controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return getDKVModel();
			}
		});
		addAction(new TabularViewAction(AddDomainAction.actionType, controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return getDKVModel();
			}
		});
		addAction(new TabularViewAction(AddKeyAction.actionType, controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return getSelectedDomain();
			}
		});
		addAction(new TabularViewAction(DKVDelete.actionType, controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return null;
			}
		});
		finalizeBuilding();
	}

	public DKVModel getDKVModel() {
		return getModelObject();
	}

	public DomainList getDomainList() {
		return getDKVModel().getDomainList();
	}

	@Override
	protected JComponent buildContentPane() {
		domainTableModel = new DKVDomainTableModel(getDomainList(), getDomainList().getProject());
		addToMasterTabularView(domainTable = new DKVTabularView(getIEController(), domainTableModel, 10));
		keyTableModel = new DKVKeyTableModel(getDKVModel(), null, getDomainList().getProject());
		addToSlaveTabularView(keyTable = new DKVTabularView(getIEController(), keyTableModel, 10), domainTable);
		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, domainTable, keyTable);
	}

	public Domain getSelectedDomain() {
		IESelectionManager sm = getIEController().getIESelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof Domain)) {
			return (Domain) selection.firstElement();
		}
		if ((selection.size() == 1) && (selection.firstElement() instanceof Domain.KeyList)) {
			return ((Domain.KeyList) selection.firstElement()).getDomain();
		}
		if (getSelectedKey() != null) {
			return getSelectedKey().getDomain();
		}
		return null;
	}

	public Key getSelectedKey() {
		IESelectionManager sm = getIEController().getIESelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof Key)) {
			return (Key) selection.firstElement();
		}
		return null;
	}

	public DKVTabularView getDomainTable() {
		return domainTable;
	}

	public DKVTabularView getKeyTable() {
		return keyTable;
	}

	/**
	 * Overrides willShow
	 * 
	 * @see org.openflexo.view.ModuleView#willShow()
	 */
	@Override
	public void willShow() {
	}

	/**
	 * Overrides willHide
	 * 
	 * @see org.openflexo.view.ModuleView#willHide()
	 */
	@Override
	public void willHide() {
	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() {
		return false;
	}

	public IEController getIEController() {
		return (IEController) getController();
	}

	public DKVTabularView findTabularViewContaining(DKVObject anObject) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("findTabularViewContaining() " + this + " obj: " + anObject);
		if (anObject == null)
			return null;
		for (Enumeration<TabularView> en = getMasterTabularViews().elements(); en.hasMoreElements();) {
			DKVTabularView next = (DKVTabularView) en.nextElement();
			if (next.getModel().indexOf(anObject) > -1) {
				return next;
			}
			DKVObject parentObject = anObject.getParent();
			if (next.getModel().indexOf(parentObject) > -1) {
				if (next.getSelectedObjects().contains(parentObject)) {
					next.selectObject(parentObject);
				}
				for (Enumeration en2 = next.getSlaveTabularViews().elements(); en2.hasMoreElements();) {
					DKVTabularView next2 = (DKVTabularView) en2.nextElement();
					if (next2.getModel().indexOf(anObject) > -1) {
						return next2;
					}
				}
			}
		}
		return null;
	}

	public void tryToSelect(DKVObject anObject) {
		DKVTabularView tabView = findTabularViewContaining(anObject);
		if (tabView != null) {
			tabView.selectObject(anObject);
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ObjectDeleted) {
			if (dataModification.oldValue() == getModelObject()) {
				deleteModuleView();
			}
		} else if (dataModification instanceof LanguageAdded) {
			keyTableModel.notifyLanguageAdded(((LanguageAdded) dataModification).getAddedLanguage());
		} else if (dataModification instanceof LanguageRemoved) {
			keyTableModel.notifyLanguageRemoved(((LanguageRemoved) dataModification).getRemovedLanguage());
		}
	}

	@Override
	public DKVModel getRepresentedObject() {
		return getModelObject();
	}

	@Override
	public void deleteModuleView() {
		logger.info("Removing view !");
		getIEController().removeModuleView(this);
	}

	@Override
	public FlexoPerspective<DKVModel> getPerspective() {
		return getIEController().DKV_EDITOR_PERSPECTIVE;
	}

	@Override
	public List<SelectionListener> getSelectionListeners() {
		Vector<SelectionListener> reply = new Vector<SelectionListener>();
		reply.add(this);
		return reply;
	}

}
