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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;

/**
 * Abstract class representing a fetch request, which is a primitive allowing to browse in the model while configuring requests
 * 
 * @author sylvain
 * 
 */
public abstract class FetchRequest<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, T> extends AssignableAction<M, MM, List<T>> {

	private static final Logger logger = Logger.getLogger(FetchRequest.class.getPackage().getName());

	private Vector<FetchRequestCondition> conditions;

	public FetchRequest(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
		conditions = new Vector<FetchRequestCondition>();
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.FetchRequest;
	}

	public abstract Type getFetchedType();

	@Override
	public Type getAssignableType() {
		return new ParameterizedTypeImpl(List.class, getFetchedType());
	}

	public Vector<FetchRequestCondition> getConditions() {
		return conditions;
	}

	public void setConditions(Vector<FetchRequestCondition> conditions) {
		this.conditions = conditions;
	}

	public void addToConditions(FetchRequestCondition condition) {
		condition.setFetchRequest(this);
		conditions.add(condition);
		setChanged();
		notifyObservers(new DataModification("conditions", null, condition));
	}

	public void removeFromConditions(FetchRequestCondition condition) {
		condition.setFetchRequest(null);
		conditions.remove(condition);
		setChanged();
		notifyObservers(new DataModification("conditions", condition, null));
	}

	public FetchRequestCondition createCondition() {
		FetchRequestCondition newCondition = new FetchRequestCondition(null);
		addToConditions(newCondition);
		return newCondition;
	}

	public void deleteCondition(FetchRequestCondition aCondition) {
		removeFromConditions(aCondition);
	}

	/*@Override
	public BindingFactory getBindingFactory() {
		System.out.println("On me demande la binding factory et je reponds " + super.getBindingFactory());
		System.out.println("VP= " + getViewPoint());
		System.out.println("VM= " + getVirtualModel());
		System.out.println("EP= " + getEditionPattern());
		return super.getBindingFactory();
	}*/

}
