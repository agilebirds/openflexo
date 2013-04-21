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
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.view.action.EditionSchemeAction;

public class FetchRequestIterationAction<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends ControlStructureAction<M, MM> {

	private static final Logger logger = Logger.getLogger(FetchRequestIterationAction.class.getPackage().getName());

	private String iteratorName = "item";

	private FetchRequest fetchRequest;

	public FetchRequestIterationAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.FetchRequestIteration;
	}

	public String getIteratorName() {
		return iteratorName;
	}

	public void setIteratorName(String iteratorName) {
		this.iteratorName = iteratorName;
		rebuildInferedBindingModel();
	}

	public FetchRequest<?, ?, ?> getFetchRequest() {
		return fetchRequest;
	}

	public void setFetchRequest(FetchRequest<?, ?, ?> fetchRequest) {
		fetchRequest.setActionContainer(this);
		fetchRequest.setEmbeddingIteration(this);
		this.fetchRequest = fetchRequest;
		// Big hack to prevent XMLCoDe to also append FetchRequest to the list of embedded actions
		// Should be removed either by the fixing of XMLCoDe or by the switch to PAMELA
		if (getActions().contains(fetchRequest)) {
			removeFromActions(fetchRequest);
		}
	}

	public Type getItemType() {
		if (getFetchRequest() != null) {
			return getFetchRequest().getFetchedType();
		}
		return Object.class;
	}

	@Override
	protected BindingModel buildInferedBindingModel() {
		BindingModel returned = super.buildInferedBindingModel();
		returned.addToBindingVariables(new BindingVariable(getIteratorName(), getItemType()) {
			@Override
			public Object getBindingValue(Object target, BindingEvaluationContext context) {
				logger.info("What should i return for " + getIteratorName() + " ? target " + target + " context=" + context);
				return super.getBindingValue(target, context);
			}

			@Override
			public Type getType() {
				return getItemType();
			}
		});
		return returned;
	}

	private List<?> fetchItems(EditionSchemeAction action) {
		if (getFetchRequest() != null) {
			System.out.println("Pour choper mes items, je lance " + getFetchRequest());
			return getFetchRequest().performAction(action);
		}
		return Collections.emptyList();
	}

	@Override
	public Object performAction(EditionSchemeAction action) {
		System.out.println("Perform FetchRequestIterationAction for " + getFetchRequest());
		List<?> items = fetchItems(action);
		System.out.println("Items=" + items);
		if (items != null) {
			for (Object item : items) {
				action.declareVariable(getIteratorName(), item);
				performBatchOfActions(getActions(), action);
			}
		}
		action.dereferenceVariable(getIteratorName());
		return null;
	}

	@Override
	public void addToActions(EditionAction<?, ?, ?> action) {
		// Big hack to prevent XMLCoDe to also append FetchRequest to the list of embedded actions
		// Should be removed either by the fixing of XMLCoDe or by the switch to PAMELA
		if (getFetchRequest() != action) {
			super.addToActions(action);
		}
	}

	@Override
	public String getStringRepresentation() {
		if (getFetchRequest() != null) {
			return getIteratorName() + " : " + getFetchRequest().getStringRepresentation();
		}
		return super.getStringRepresentation();
	}

}
