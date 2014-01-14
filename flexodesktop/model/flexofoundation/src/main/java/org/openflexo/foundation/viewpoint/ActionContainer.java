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

import java.util.List;
import java.util.Vector;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

public interface ActionContainer {

	@PropertyIdentifier(type = Vector.class)
	public static final String ACTIONS_KEY = "actions";

	@Getter(value = ACTIONS_KEY, cardinality = Cardinality.LIST, inverse = EditionAction.ACTION_CONTAINER_KEY)
	@XMLElement
	public List<EditionAction<?, ?>> getActions();

	@Setter(ACTIONS_KEY)
	public void setActions(List<EditionAction<?, ?>> actions);

	@Adder(ACTIONS_KEY)
	public void addToActions(EditionAction<?, ?> aAction);

	@Remover(ACTIONS_KEY)
	public void removeFromActions(EditionAction<?, ?> aAction);

	public EditionScheme getEditionScheme();

	public BindingModel getBindingModel();

	public BindingModel getInferedBindingModel();

	public int getIndex(EditionAction<?, ?> action);

	public void insertActionAtIndex(EditionAction<?, ?> action, int index);

	public void actionFirst(EditionAction<?, ?> a);

	public void actionUp(EditionAction<?, ?> a);

	public void actionDown(EditionAction<?, ?> a);

	public void actionLast(EditionAction<?, ?> a);

	public <A extends EditionAction<?, ?>> A createAction(Class<A> actionClass, ModelSlot<?> modelSlot);

	public EditionAction<?, ?> deleteAction(EditionAction<?, ?> anAction);

	public void variableAdded(AssignableAction action);
}
