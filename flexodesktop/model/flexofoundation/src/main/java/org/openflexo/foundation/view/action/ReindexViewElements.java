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
package org.openflexo.foundation.view.action;

import java.beans.PropertyChangeSupport;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Re-index view elements relatively to their corresponding EditionPattern.
 * 
 * @author sylvain
 * 
 */
public class ReindexViewElements extends FlexoAction<ReindexViewElements, ViewObject, ViewObject> {

	private static final Logger logger = Logger.getLogger(ReindexViewElements.class.getPackage().getName());

	public static FlexoActionType<ReindexViewElements, ViewObject, ViewObject> actionType = new FlexoActionType<ReindexViewElements, ViewObject, ViewObject>(
			"reindex_contents", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public ReindexViewElements makeNewAction(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
			return new ReindexViewElements(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return (object instanceof View || object instanceof ViewShape);
		}

	};

	static {
		FlexoModelObject.addActionForClass(ReindexViewElements.actionType, View.class);
		FlexoModelObject.addActionForClass(ReindexViewElements.actionType, ViewShape.class);
	}

	ReindexViewElements(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		init();
	}

	private List<EditionPattern> matchingEditionPatterns = new ArrayList<EditionPattern>();
	private HashMap<EditionPattern, OrderedElementList> reorderedElements = new HashMap<EditionPattern, OrderedElementList>();

	/**
	 * First build the data structure supporting new indexing data
	 */
	private void init() {
		for (ViewObject o : getFocusedObject().getChilds()) {
			if (o instanceof ViewElement) {
				ViewElement e = (ViewElement) o;
				if (e.getEditionPattern() != null) {
					if (!matchingEditionPatterns.contains(e.getEditionPattern())) {
						matchingEditionPatterns.add(e.getEditionPattern());
					}
					OrderedElementList orderedList = reorderedElements.get(e.getEditionPattern());
					if (orderedList == null) {
						orderedList = new OrderedElementList(e.getEditionPattern());
						reorderedElements.put(e.getEditionPattern(), orderedList);
					}
					orderedList.add(e);
				}
			}
		}
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Reindex " + getFocusedObject());
	}

	public List<EditionPattern> getMatchingEditionPatterns() {
		return matchingEditionPatterns;
	}

	public OrderedElementList getReorderedElements(EditionPattern editionPattern) {
		return reorderedElements.get(editionPattern);
	}

	public void elementFirst(ViewElement e, EditionPattern ep) {
		System.out.println("First pour " + e + " and " + ep);
		getReorderedElements(ep).elementFirst(e);
	}

	public void elementUp(ViewElement e, EditionPattern ep) {
		System.out.println("Up pour " + e + " and " + ep);
		getReorderedElements(ep).elementUp(e);
	}

	public void elementDown(ViewElement e, EditionPattern ep) {
		System.out.println("Down pour " + e + " and " + ep);
		getReorderedElements(ep).elementDown(e);
	}

	public void elementLast(ViewElement e, EditionPattern ep) {
		System.out.println("Last pour " + e + " and " + ep);
		getReorderedElements(ep).elementLast(e);
	}

	public static class OrderedElementList extends ArrayList<ViewElement> implements HasPropertyChangeSupport {
		private EditionPattern editionPattern;

		private PropertyChangeSupport pcSupport;

		public OrderedElementList(EditionPattern editionPattern) {
			this.editionPattern = editionPattern;
			pcSupport = new PropertyChangeSupport(this);
		}

		public void elementFirst(ViewElement e) {
			System.out.println("First pour " + e + " and " + editionPattern);
			int index = indexOf(e);
			remove(e);
			add(0, e);
			pcSupport.firePropertyChange("indexOf(Object)", index, 0);
		}

		public void elementUp(ViewElement e) {
			System.out.println("Up pour " + e + " and " + editionPattern);
			int index = indexOf(e);
			if (index > 0) {
				remove(e);
				add(index - 1, e);
				pcSupport.firePropertyChange("indexOf(Object)", index, index - 1);
			}
		}

		public void elementDown(ViewElement e) {
			System.out.println("Down pour " + e + " and " + editionPattern);
			int index = indexOf(e);
			if (index < size() - 1) {
				remove(e);
				add(index + 1, e);
				pcSupport.firePropertyChange("indexOf(Object)", index, index + 1);
			}
		}

		public void elementLast(ViewElement e) {
			System.out.println("Last pour " + e + " and " + editionPattern);
			int index = indexOf(e);
			remove(e);
			add(e);
			pcSupport.firePropertyChange("indexOf(Object)", index, size() - 1);
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

	}

}