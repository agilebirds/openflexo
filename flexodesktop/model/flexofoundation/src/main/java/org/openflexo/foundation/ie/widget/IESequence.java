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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.SubsequenceInserted;
import org.openflexo.foundation.ie.dm.WidgetAddedToSequence;
import org.openflexo.foundation.ie.dm.WidgetRemovedFromSequence;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

public abstract class IESequence<E extends IWidget> extends IEWidget {
	private static final Logger logger = FlexoLogger.getLogger(IESequence.class.getPackage().getName());

	private Vector<E> _children;

	private IESequenceOperator _sequenceOperator;

	private IEOperator operator;

	public IESequence(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		_children = new Vector<E>();
		if (!(parent instanceof IESequenceOperator)) {
			_sequenceOperator = new IESequenceOperator(woComponent, this, prj);
		}
	}

	@SuppressWarnings("unchecked")
	public void simplifySequenceTree() {
		Enumeration en = ((Vector<E>) _children.clone()).elements();
		while (en.hasMoreElements()) {
			E e = (E) en.nextElement();
			if (e.getClass() == this.getClass()) {
				((IESequence) e).simplifySequenceTree();
			}
		}
		if (getOperator() == null && getParent() instanceof IESequence) {
			int index = ((IESequence) getParent()).indexOf(this);
			((IESequence) getParent()).removeFromInnerWidgets(this);
			while (!_children.isEmpty()) {
				E w = _children.lastElement();
				_children.remove(w);
				((IESequence<E>) getParent()).insertElementAt(w, index);

			}
			this.delete();
		}
	}

	@Override
	public String getNiceName() {
		String niceName = getCalculatedLabel();
		if (niceName != null && niceName.trim().length() > 0) {
			return ToolBox.getJavaName(niceName);
		}
		if (getOperator() != null) {
			String post = getClass().getSimpleName();
			if (post.startsWith("IESequence")) {
				post = post.substring("IESequence".length());
			}
			return post + getOperator().getWidgetType();
		}
		return getWidgetType();
	}

	@Override
	public void performOnDeleteOperations() {
		if (getOperator() != null) {
			getOperator().delete();
			return;
		}
		isDeleting = true;
		Enumeration en = ((Vector) _children.clone()).elements();
		while (en.hasMoreElements()) {
			IWidget w = (IWidget) en.nextElement();
			w.delete();
		}
		super.performOnDeleteOperations();
	}

	/**
	 * Returns the number of parents that are of the same class.
	 * 
	 * @return the number of parents that are of the same class
	 */
	public int getSequenceDepth() {
		int i = 0;
		IEObject parent = getParent();
		while (parent != null && parent.getClass() == this.getClass()) {
			i++;
			parent = ((IESequence) parent).getParent();
		}
		return i;
	}

	@Override
	public String getDefaultInspectorName() {
		if (isConditional()) {
			return Inspectors.IE.CONDITIONAL_SEQUENCE_INSPECTOR;
		} else if (isRepetition()) {
			return Inspectors.IE.REPETITION_SEQUENCE_INSPECTOR;
		} else {
			return null;
		}
	}

	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> answer = new Vector<IObject>();
		if (getOperator() != null) {
			answer.add(getOperator());
		}
		answer.addAll(_children);
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return null;
	}

	public Vector<E> getInnerWidgets() {
		return _children;
	}

	public void setInnerWidgets(Vector<E> v) {
		_children = v;
	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		if (getOperator() != null) {
			if (isConditional()) {
				return Inspectors.IE.CONDITIONAL_SEQUENCE_INSPECTOR;
			} else if (isRepetition()) {
				return Inspectors.IE.REPETITION_SEQUENCE_INSPECTOR;
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Unknwown operator-->Cannot return inspector name");
				}
				return null;
			}
		} else {
			return null;
		}
	}

	public void addToInnerWidgets(E w) {
		if (w instanceof IESequenceOperator) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Tried to add an IESequenceOperator.");
			}
			return;
		} else if (w instanceof IEOperator) {
			if (getOperator() == null) {
				setOperator((IEOperator) w);
			}
		} else {
			insertElementAt(w, _children.size());
			w.setParent(this);
		}
	}

	public void removeFromInnerWidgets(E w) {
		removeFromInnerWidgets(w, true);
	}

	public void removeFromInnerWidgets(E w, boolean deleteIfEmpty) {
		if (w instanceof IESequenceOperator || w instanceof IEOperator) {
			return;
		} else {
			_children.remove(w);
			w.setParent(null);
			refreshIndexes();
			setChanged();
			notifyObservers(new WidgetRemovedFromSequence((IEObject) w));
			if (w instanceof IESequence) {
				setChanged();
				notifyObservers(new SubsequenceRemoved((IESequence) w));
			}
			if (getOperator() != null) {
				getOperator().notifyWidgetRemoval(w);
			}
			if (deleteIfEmpty && _children.size() == 0 && !isDeleting && isSubsequence()) {
				isDeleting = true;
				delete();
			}
		}
	}

	private boolean isDeleting = false;

	public int indexOf(E w) {
		return _children.indexOf(w);
	}

	public int size() {
		return _children.size();
	}

	public int getWidgetCount() {
		return size();
	}

	public void insertElementAt(E o, int i) {
		if (i > _children.size()) {
			i = _children.size();
		}
		_children.insertElementAt(o, i);
		o.setParent(this);
		if (!isDeserializing() && getWOComponent() != null) {
			// TODO: check that when we duplicate component, all widgets receive this notification
			o.setWOComponent(getWOComponent());
		}
		refreshIndexes();
		setChanged();
		if (o instanceof IEWidget) {
			notifyObservers(new WidgetAddedToSequence((IEWidget) o, ((IEWidget) o).getIndex()));
		}
		setChanged();
		if (o instanceof IESequence) {
			notifyObservers(new SubsequenceInserted((IESequence) o));
		}
		if (getOperator() != null) {
			getOperator().notifyWidgetInsertion(o);
		}
	}

	protected void refreshIndexes() {
		Enumeration en = elements();
		int i = 0;
		while (en.hasMoreElements()) {
			((IEWidget) en.nextElement()).setIndex(i);
			i++;
		}
	}

	public Enumeration<E> elements() {
		return _children.elements();
	}

	public Iterator<E> iterator() {
		return _children.iterator();
	}

	public int length() {
		return _children.size();
	}

	@Deprecated
	public IESequenceOperator getSequenceOperator() {
		return _sequenceOperator;
	}

	@Deprecated
	public void setSequenceOperator(IESequenceOperator op_seq) {
		_sequenceOperator = op_seq;
		op_seq.setParent(this);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "sequence";
	}

	public E get(int i) {
		try {
			return _children.get(i);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public void setElementAt(E element, int index) {
		_children.setElementAt(element, index);
		element.setParent(this);
		element.setIndex(index);
	}

	public static IESequence createASubSequence(IESequence actualParentSequence) {
		if (actualParentSequence instanceof IESequenceTopComponent) {
			return new IESequenceTopComponent(actualParentSequence.getWOComponent(), actualParentSequence,
					actualParentSequence.getProject());
		} else if (actualParentSequence instanceof IESequenceTR) {
			IESequenceTR returned = new IESequenceTR(actualParentSequence.getWOComponent(), actualParentSequence,
					actualParentSequence.getProject());
			returned.constraints.gridwidth = ((IESequenceTR) actualParentSequence).getColCount();
			return returned;
		} else if (actualParentSequence instanceof IESequenceTD) {
			return new IESequenceTD(actualParentSequence.getWOComponent(), actualParentSequence, actualParentSequence.getProject());

		} else if (actualParentSequence instanceof IESequenceWidget) {
			return new IESequenceWidget(actualParentSequence.getWOComponent(), actualParentSequence, actualParentSequence.getProject());

		} else if (actualParentSequence instanceof IESequenceTab) {
			return new IESequenceTab(actualParentSequence.getWOComponent(), actualParentSequence, actualParentSequence.getProject());
		}
		return null;
	}

	public boolean hasOperatorConditional() {
		return getOperator() != null && getOperator() instanceof ConditionalOperator;
	}

	public boolean hasOperatorRepetition() {
		return getOperator() != null && getOperator() instanceof RepetitionOperator;
	}

	public boolean isLast(Object obj) {
		return _children.lastElement() != null && _children.lastElement().equals(obj);
	}

	public boolean isFirst(Object obj) {
		return _children.size() > 0 && _children.get(0).equals(obj);
	}

	public Object getNext(Object obj) {
		if (!_children.contains(obj)) {
			return null;
		}
		if (isLast(obj)) {
			return null;
		}
		return _children.elementAt(_children.indexOf(obj) + 1);
	}

	public Object getPrevious(Object obj) {
		if (!_children.contains(obj)) {
			return null;
		}
		if (isFirst(obj)) {
			return null;
		}
		return _children.elementAt(_children.indexOf(obj) - 1);
	}

	public Object getLast() {
		if (_children.size() == 0) {
			return null;
		}
		return _children.lastElement();
	}

	public void replaceWidgetByReusable(E oldWidget, E newWidget) {
		int index = indexOf(oldWidget);
		removeFromInnerWidgets(oldWidget);
		insertElementAt(newWidget, index);
	}

	public boolean addAll(Collection<? extends E> c) {
		return _children.addAll(c);
	}

	public void clear() {
		_children.clear();
	}

	public boolean contains(Object o) {
		return _children.contains(o);
	}

	public boolean containsAll(Collection c) {
		return _children.containsAll(c);
	}

	public boolean isEmpty() {
		return _children.isEmpty();
	}

	public Object[] toArray() {
		return _children.toArray();
	}

	/**
	 * Overrides toArray
	 * 
	 * @see java.util.Collection#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return _children.toArray(a);
	}

	@SuppressWarnings("unchecked")
	public void unwrap() {
		if (isSubsequence()) {
			int insertionIndex = getIndex();
			IESequence<E> parentSequence = (IESequence<E>) getParent();
			while (!_children.isEmpty()) {
				E widget = _children.lastElement();
				_children.remove(widget);
				parentSequence.insertElementAt(widget, insertionIndex);
			}
			delete();
		}
	}

	public void resetOperator() {
		operator = null;
	}

	@Override
	public Vector<IWidget> getAllNonSequenceWidget() {
		Vector<IWidget> v = new Vector<IWidget>();
		Enumeration<E> en = elements();
		while (en.hasMoreElements()) {
			E element = en.nextElement();
			v.addAll(element.getAllNonSequenceWidget());
		}
		return v;
	}

	public IEObject getNonSequenceParent() {
		if (isRoot()) {
			return getParent();
		} else if (getParent() instanceof IESequence) {
			return ((IESequence<E>) getParent()).getNonSequenceParent();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Not good! This sequence is not the root sequence, but parent is not a sequence");
			}
			return getParent();
		}
	}

	public abstract boolean isSubsequence();

	public boolean isRoot() {
		return getParent() == null || !getParent().getClass().isAssignableFrom(this.getClass());
	}

	public IEOperator getOperator() {
		if (!isSubsequence()) {
			if (_sequenceOperator != null && _sequenceOperator.size() > 0) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Found a sequence of operator but this is not a sub-sequence");
				}
			}
			return null;
		}
		if (operator == null && _sequenceOperator != null && _sequenceOperator.size() > 0) {
			operator = _sequenceOperator.get(0);
			operator.setOperatedSequence(this);
			_sequenceOperator = null;
		}
		return operator;
	}

	public void setOperator(IEOperator operator) {
		IEOperator old = this.operator;
		this.operator = operator;
		operator.setOperatedSequence(this);
		setChanged();
		notifyObservers(new OperatorChanged(old, operator));
	}

	public boolean isConditional() {
		return getOperator() != null && getOperator() instanceof ConditionalOperator;
	}

	public boolean isRepetition() {
		return getOperator() != null && getOperator() instanceof RepetitionOperator;
	}

	public IWidget lastObject() {
		if (!(get(size() - 1) instanceof IESequence)) {
			return get(size() - 1);
		} else {
			return ((IESequence) get(size() - 1)).lastObject();
		}
	}

	public IWidget firstObject() {
		if (size() == 0) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("This is not suppose to happen: an empty sequence is left in the model");
			}
			return null;
		}
		if (!(get(0) instanceof IESequence)) {
			return get(0);
		} else {
			return ((IESequence) get(0)).firstObject();
		}
	}

	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent)) {
			return;
		}
		super.setWOComponent(woComponent);
		if (getOperator() != null) {
			getOperator().setWOComponent(woComponent);
		}

		if (_children != null) {// This call is very important because it will update the WOComponent components cache
			Enumeration<E> en = elements();
			while (en.hasMoreElements()) {
				E e = en.nextElement();
				e.setWOComponent(woComponent);
			}
		}
	}

	@Override
	public boolean areComponentInstancesValid() {
		boolean b = true;
		Enumeration<E> en = elements();
		while (en.hasMoreElements() && b) {
			E e = en.nextElement();
			b &= e.areComponentInstancesValid();
		}
		return b;
	}

	@Override
	public void removeInvalidComponentInstances() {
		Enumeration<E> en = elements();
		while (en.hasMoreElements()) {
			E e = en.nextElement();
			e.removeInvalidComponentInstances();
		}
	}

	@Override
	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
		Enumeration<? extends IWidget> en = elements();
		while (en.hasMoreElements()) {
			IWidget element = en.nextElement();
			v.addAll(element.getAllButtonInterface());
		}
		return v;
	}
}
