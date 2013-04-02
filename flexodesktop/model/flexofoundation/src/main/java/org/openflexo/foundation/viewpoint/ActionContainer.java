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

import java.util.Vector;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;

public interface ActionContainer {

	public EditionScheme getEditionScheme();

	public BindingModel getBindingModel();

	public BindingModel getInferedBindingModel();

	public Vector<EditionAction<?, ?, ?>> getActions();

	public void setActions(Vector<EditionAction<?, ?, ?>> actions);

	public void addToActions(EditionAction<?, ?, ?> action);

	public void removeFromActions(EditionAction<?, ?, ?> action);

	public int getIndex(EditionAction<?, ?, ?> action);

	public void insertActionAtIndex(EditionAction<?, ?, ?> action, int index);

	public void actionFirst(EditionAction<?, ?, ?> a);

	public void actionUp(EditionAction<?, ?, ?> a);

	public void actionDown(EditionAction<?, ?, ?> a);

	public void actionLast(EditionAction<?, ?, ?> a);

	public <A extends EditionAction<M, MM, ?>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> A createAction(
			Class<A> actionClass, ModelSlot<M, MM> modelSlot);

	/*public AddShape createAddShapeAction();

	public AddClass createAddClassAction();

	public AddIndividual createAddIndividualAction();

	public AddObjectPropertyStatement createAddObjectPropertyStatementAction();

	public AddDataPropertyStatement createAddDataPropertyStatementAction();

	public AddIsAStatement createAddIsAPropertyAction();

	public AddRestrictionStatement createAddRestrictionAction();

	public AddConnector createAddConnectorAction();

	public DeclarePatternRole createDeclarePatternRoleAction();

	public GraphicalAction createGraphicalAction();

	public CreateDiagram createAddDiagramAction();

	public AddEditionPattern createAddEditionPatternAction();

	public ConditionalAction createConditionalAction();

	public IterationAction createIterationAction();

	public DeleteAction createDeleteAction();*/

	public EditionAction<?, ?, ?> deleteAction(EditionAction<?, ?, ?> anAction);

	public void variableAdded(AssignableAction action);
}
