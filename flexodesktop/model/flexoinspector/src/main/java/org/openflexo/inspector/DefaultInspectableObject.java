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
package org.openflexo.inspector;

import java.util.HashSet;
import java.util.Vector;

import org.openflexo.inspector.model.TabModel;
import org.openflexo.kvc.KVCObservableObject;


public abstract class DefaultInspectableObject extends KVCObservableObject implements InspectableObject {

	private HashSet<InspectorObserver> _inspectorObservers = new HashSet<InspectorObserver>();

	@Override
	public void deleteInspectorObserver(InspectorObserver obs)
	{
		_inspectorObservers.remove(obs);
	}

	@Override
	public void addInspectorObserver(InspectorObserver obs)
	{
		if (obs == null) {
			throw new NullPointerException();
		}
		if (!_inspectorObservers.contains(obs)) {
			_inspectorObservers.add(obs);
		}
	}

	@Override
	public void notifyObservers(Object arg)
	{
		boolean hasChanged = hasChanged();

		super.notifyObservers(arg);

		Object[] arrLocal;

		synchronized (this) {
			if (!hasChanged) {
				return;
			}
			arrLocal = _inspectorObservers.toArray();
		}
		// Notify all Inspector observers
		if (arg instanceof InspectableModification) {
			for (int i = arrLocal.length - 1; i >= 0; i--) {
				((InspectorObserver) arrLocal[i]).update((InspectableObject) this, (InspectableModification)arg);
			}
		}
	}

	@Override
	public synchronized void deleteObservers()
	{
		super.deleteObservers();
		_inspectorObservers.clear();
	}

	@Override
	public int countObservers()
	{
		return super.countObservers() + _inspectorObservers.size();
	}

	@Override
	public boolean isDeleted()
	{
		return false;
	}

	@Override
	public abstract String getInspectorName();

	@Override
	public String getInspectorTitle()
	{
		return null;
	}

	@Override
	public Vector<TabModel> inspectionExtraTabs() {
		// TODO Auto-generated method stub
		return null;
	}
}
