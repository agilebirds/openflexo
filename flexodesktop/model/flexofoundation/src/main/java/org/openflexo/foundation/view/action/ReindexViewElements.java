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
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.model.ViewElement;
import org.openflexo.foundation.view.diagram.model.ViewObject;
import org.openflexo.foundation.view.diagram.model.ViewShape;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementSpecification;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;

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
		public boolean isVisibleForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return object instanceof View || object instanceof ViewShape;
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

	public boolean skipDialog = false;

	private ViewObject container = null;

	private List<EditionPattern> matchingEditionPatterns = new ArrayList<EditionPattern>();
	private HashMap<EditionPattern, OrderedElementList> reorderedElements = new HashMap<EditionPattern, OrderedElementList>();

	/**
	 * First build the data structure supporting new indexing data
	 */
	private void init() {

		matchingEditionPatterns = getContainedEditionPattern(getContainer());

		for (ViewObject o : getContainer().getChilds()) {
			if (o instanceof ViewElement) {
				ViewElement e = (ViewElement) o;
				if (e.getEditionPattern() != null) {
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

	public void initAsUpReindexing(ViewElement elementToBeReindexed) {
		skipDialog = true;
		if (elementToBeReindexed.getEditionPattern() != null) {
			container = elementToBeReindexed.getParent();
			init();
			if (elementToBeReindexed.getIndexRelativeToEPType() > 0) {
				elementUp(elementToBeReindexed, elementToBeReindexed.getEditionPattern());
			}
		}
	}

	public void initAsDownReindexing(ViewElement elementToBeReindexed) {
		skipDialog = true;
		if (elementToBeReindexed.getEditionPattern() != null) {
			container = elementToBeReindexed.getParent();
			init();
			if (elementToBeReindexed.getIndexRelativeToEPType() < getContainer().getChildsOfType(elementToBeReindexed.getEditionPattern())
					.size() - 1) {
				elementDown(elementToBeReindexed, elementToBeReindexed.getEditionPattern());
			}
		}
	}

	private static List<EditionPattern> getContainedEditionPattern(ViewObject element) {
		ArrayList<EditionPattern> returned = new ArrayList<EditionPattern>();
		// EditionPattern of current container is excluded, as we can
		// find some other graphical elements representing same EP
		EditionPattern excludedEditionPattern = null;
		if (element instanceof ViewElement) {
			excludedEditionPattern = ((ViewElement) element).getEditionPattern();
		}
		for (ViewObject o : element.getChilds()) {
			if (o instanceof ViewElement) {
				ViewElement e = (ViewElement) o;
				if (e.getEditionPattern() != null) {
					if (!returned.contains(e.getEditionPattern()) && e.getEditionPattern() != excludedEditionPattern) {
						returned.add(e.getEditionPattern());
					}
				}
			}
		}
		return returned;
	}

	public ViewObject getContainer() {
		if (container == null) {
			if (getContainedEditionPattern(getFocusedObject()).size() > 0) {
				container = getFocusedObject();
			} else {
				container = getFocusedObject().getParent();
			}
		}
		return container;
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		for (EditionPattern ep : getMatchingEditionPatterns()) {
			OrderedElementList l = reorderedElements.get(ep);
			List<ViewElement> currentList = getContainer().getChildsOfType(ep);
			for (ViewElement e : l) {
				int oldIndex = currentList.indexOf(e);
				int newIndex = l.indexOf(e);
				if (oldIndex != newIndex) {
					logger.info("Re-index " + e + " from index " + oldIndex + " to " + newIndex);
					e.setIndexRelativeToEPType(newIndex);
				}
			}
		}
	}

	public List<EditionPattern> getMatchingEditionPatterns() {
		return matchingEditionPatterns;
	}

	public String getExplicitDescription(ViewElement element) {
		EditionPattern ep = element.getEditionPattern();
		EditionPatternInstance epi = element.getEditionPatternInstance();
		if (ep == null) {
			return "null";
		}
		for (GraphicalElementPatternRole pr : ep.getPatternRoles(GraphicalElementPatternRole.class)) {
			GraphicalElementSpecification labelSpec = pr.getGraphicalElementSpecification(GraphicalElementPatternRole.LABEL_FEATURE);
			if (labelSpec != null) {
				String returned = (String) labelSpec.getValue().getBindingValue(epi);
				if (StringUtils.isNotEmpty(returned)) {
					return returned;
				}
			}
		}
		return element.toString();
	}

	public OrderedElementList getReorderedElements(EditionPattern editionPattern) {
		return reorderedElements.get(editionPattern);
	}

	public void elementFirst(ViewElement e, EditionPattern ep) {
		getReorderedElements(ep).elementFirst(e);
	}

	public void elementUp(ViewElement e, EditionPattern ep) {
		getReorderedElements(ep).elementUp(e);
	}

	public void elementDown(ViewElement e, EditionPattern ep) {
		getReorderedElements(ep).elementDown(e);
	}

	public void elementLast(ViewElement e, EditionPattern ep) {
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
			logger.fine("First for " + e + " and " + editionPattern);
			int index = indexOf(e);
			remove(e);
			add(0, e);
			pcSupport.firePropertyChange("indexOf(Object)", index, 0);
		}

		public void elementUp(ViewElement e) {
			logger.fine("Up for " + e + " and " + editionPattern);
			int index = indexOf(e);
			if (index > 0) {
				remove(e);
				add(index - 1, e);
				pcSupport.firePropertyChange("indexOf(Object)", index, index - 1);
			}
		}

		public void elementDown(ViewElement e) {
			logger.fine("Down for " + e + " and " + editionPattern);
			int index = indexOf(e);
			if (index < size() - 1) {
				remove(e);
				add(index + 1, e);
				pcSupport.firePropertyChange("indexOf(Object)", index, index + 1);
			}
		}

		public void elementLast(ViewElement e) {
			logger.fine("Last for " + e + " and " + editionPattern);
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
