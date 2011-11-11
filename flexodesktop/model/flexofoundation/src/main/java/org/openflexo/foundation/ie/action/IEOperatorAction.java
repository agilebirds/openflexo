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
package org.openflexo.foundation.ie.action;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.SingleWidgetComponent;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.util.IEWidgetComparator;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;

public abstract class IEOperatorAction extends FlexoAction {

	public IEOperatorAction(FlexoActionType actionType, FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public static Vector sel(Object o, Vector v) {
		if (o instanceof IEOperator) {
			o = ((IEOperator) o).getOperatedSequence();
		}
		Vector answer;
		if (v == null)
			answer = new Vector();
		else
			answer = (Vector) v.clone();
		Enumeration en = answer.elements();
		while (en.hasMoreElements()) {
			Object element = en.nextElement();
			if (element instanceof IEOperator) {
				answer.remove(element);
				answer.add(((IEOperator) element).getOperatedSequence());
			}
		}
		if (!answer.contains(o))
			answer.add(o);
		return answer;
	}

	abstract Logger logger();

	public static boolean isSelectionValid(Vector v) {
		Enumeration en = v.elements();
		Object temp = null;
		int maxIndex = -1;
		int minIndex = Integer.MAX_VALUE;
		Object parent = null;
		while (en.hasMoreElements()) {
			temp = en.nextElement();
			if (!(temp instanceof IEWidget)) {
				return false;
			}
			// TODO: When SequenceTD will be supported, this will need to be
			// removed
			if (temp instanceof IETDWidget)
				return false;
			if (temp instanceof IETabWidget)
				if (((IESequenceTab) ((IETabWidget) temp).getParent()).isSubsequence() || v.size() > 1)
					return false;
			if (parent == null)
				parent = ((IEWidget) temp).getParent();
			else {
				if (!parent.equals(((IEWidget) temp).getParent()))
					return false;
			}
			minIndex = Math.min(minIndex, ((IEWidget) temp).getIndex());
			maxIndex = Math.max(maxIndex, ((IEWidget) temp).getIndex());
		}
		return maxIndex - minIndex + 1 == v.size();
	}

	public IESequence findSequenceSurrounding(boolean forceSubsequence) {
		Vector v = sel(getFocusedObject(), getGlobalSelection());
		if (v.size() == 1) {
			IEObject parent = ((IEWidget) v.get(0)).getParent();
			if (parent instanceof IESequence) {

				// here we have to create a subSequence and returning it;
				IESequence actualParentSequence = (IESequence) parent;
				IEWidget w = (IEWidget) v.get(0);
				int insertionIndex = w.getIndex();
				actualParentSequence.removeFromInnerWidgets(w, false);
				IESequence newSequence = IESequence.createASubSequence(actualParentSequence);
				newSequence.addToInnerWidgets(w);
				newSequence.setIndex(insertionIndex);
				w.setIndex(0);
				logger().info(
						"inserting a new sequence : " + newSequence + " at index:" + newSequence.getIndex() + " in " + actualParentSequence);
				actualParentSequence.insertElementAt(newSequence, insertionIndex);
				sequenceIsNew = true;
				return newSequence;
			} else {
				if (parent instanceof SingleWidgetComponent) {
					IESequence newSequence = new IESequenceWidget((SingleWidgetComponent) parent, parent, parent.getProject());
					newSequence.addToInnerWidgets(((SingleWidgetComponent) parent).getRootWidget());
					newSequence.setIndex(0);
					((SingleWidgetComponent) parent).getRootWidget().setIndex(0);
					return newSequence;
				} else {
					if (logger().isLoggable(Level.WARNING))
						logger().warning("parent of single-selection is of type:" + parent.getClass());
					return null;
				}
			}
		} else {
			IEObject parent = ((IEWidget) v.get(0)).getParent();
			if (parent instanceof IESequence) {
				IESequence actualParentSequence = (IESequence) parent;
				IESequence newSequence = IESequence.createASubSequence(actualParentSequence);
				Vector sortedSelection = IEWidgetComparator.sortWidgets(v);
				int i = 0;
				int insertionIndex = ((IEWidget) sortedSelection.get(0)).getIndex();
				newSequence.setIndex(insertionIndex);
				Enumeration en = sortedSelection.elements();
				while (en.hasMoreElements()) {
					IEWidget w = (IEWidget) en.nextElement();
					actualParentSequence.removeFromInnerWidgets(w, false);
					w.setIndex(i);
					i++;
					newSequence.addToInnerWidgets(w);
				}
				actualParentSequence.insertElementAt(newSequence, insertionIndex);
				sequenceIsNew = true;
				return newSequence;

			} else {
				if (logger().isLoggable(Level.WARNING))
					logger().warning("parent of multi-selection is of type:" + parent.getClass());
				return null;
			}
		}

	}

	protected boolean sequenceIsNew = false;
}
