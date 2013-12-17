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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;

/**
 * Abstract class representing a fetch request, which is a primitive allowing to browse in the model while configuring requests
 * 
 * @author sylvain
 * 
 */
public abstract class FetchRequest<MS extends ModelSlot<?>, T> extends AssignableAction<MS, List<T>> {

	private static final Logger logger = Logger.getLogger(FetchRequest.class.getPackage().getName());

	private Vector<FetchRequestCondition> conditions;

	// null in fetch request is not embedded in an iteration
	private FetchRequestIterationAction embeddingIteration;

	public FetchRequest() {
		super();
		conditions = new Vector<FetchRequestCondition>();
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		if (getAssignation().isSet()) {
			out.append(getAssignation().toString() + " = ", context);
		}
		out.append(getClass().getSimpleName(), context);
		return out.toString();
	}

	protected String getWhereClausesFMLRepresentation(FMLRepresentationContext context) {
		if (conditions.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("where ");
			if (conditions.size() > 1) {
				sb.append("(");
			}
			boolean isFirst = true;
			for (FetchRequestCondition c : conditions) {
				sb.append(c.getCondition().toString() + (isFirst ? "" : " and "));
			}
			if (conditions.size() > 1) {
				sb.append(")");
			}
			return sb.toString();
		}
		return null;
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
		FetchRequestCondition newCondition = new FetchRequestCondition();
		addToConditions(newCondition);
		return newCondition;
	}

	public void deleteCondition(FetchRequestCondition aCondition) {
		removeFromConditions(aCondition);
	}

	public List<T> filterWithConditions(List<T> fetchResult, final EditionSchemeAction action) {
		if (getConditions().size() == 0) {
			return fetchResult;
		} else {
			// System.out.println("Filtering with " + getConditions() + " fetchResult=" + fetchResult);
			List<T> returned = new ArrayList<T>();
			for (final T proposedFetchResult : fetchResult) {
				boolean takeIt = true;
				for (FetchRequestCondition condition : getConditions()) {
					if (!condition.evaluateCondition(proposedFetchResult, action)) {
						takeIt = false;
						// System.out.println("I dismiss " + proposedFetchResult + " because of " + condition.getCondition() + " valid="
						// + condition.getCondition().isValid());
						break;
					}
				}
				if (takeIt) {
					returned.add(proposedFetchResult);
					// System.out.println("I take " + proposedFetchResult);
				} else {
				}
			}
			return returned;
		}
	}

	public FetchRequestIterationAction getEmbeddingIteration() {
		return embeddingIteration;
	}

	public void setEmbeddingIteration(FetchRequestIterationAction embeddingIteration) {
		this.embeddingIteration = embeddingIteration;
	}

}
