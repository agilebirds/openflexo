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

import org.openflexo.foundation.viewpoint.inspector.CheckboxInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.ClassInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.DataPropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
import org.openflexo.foundation.viewpoint.inspector.IndividualInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.IntegerInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.ObjectPropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.PropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.TextAreaInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.TextFieldInspectorEntry;
import org.openflexo.foundation.viewpoint.rm.VirtualModelResource;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.converter.RelativePathFileConverter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * {@link ModelFactory} used to handle VirtualModel models<br>
 * One instance is declared for a {@link VirtualModelResource}
 * 
 * @author sylvain
 * 
 */
public class VirtualModelModelFactory extends ModelFactory {

	public VirtualModelModelFactory(VirtualModelResource virtualModelResource) throws ModelDefinitionException {
		super(ModelContextLibrary.getModelContext(VirtualModel.class));
		addConverter(new RelativePathFileConverter(virtualModelResource.getDirectory()));
	}

	public VirtualModel newVirtualModel() {
		return newInstance(VirtualModel.class);
	}

	public SynchronizationScheme newSynchronizationScheme() {
		return newInstance(SynchronizationScheme.class);
	}

	public EditionPatternConstraint newEditionPatternConstraint() {
		return newInstance(EditionPatternConstraint.class);
	}

	public CreationScheme newCreationScheme() {
		return newInstance(CreationScheme.class);
	}

	public CloningScheme newCloningScheme() {
		return newInstance(CloningScheme.class);
	}

	public ActionScheme newActionScheme() {
		return newInstance(ActionScheme.class);
	}

	public NavigationScheme newNavigationScheme() {
		return newInstance(NavigationScheme.class);
	}

	public DeletionScheme newDeletionScheme() {
		return newInstance(DeletionScheme.class);
	}

	public AddEditionPatternInstanceParameter newAddEditionPatternInstanceParameter(EditionSchemeParameter p) {
		AddEditionPatternInstanceParameter returned = newInstance(AddEditionPatternInstanceParameter.class);
		returned.setParam(p);
		return returned;
	}

	public DeleteEditionPatternInstanceParameter newDeleteEditionPatternInstanceParameter(EditionSchemeParameter p) {
		DeleteEditionPatternInstanceParameter returned = newInstance(DeleteEditionPatternInstanceParameter.class);
		returned.setParam(p);
		return returned;
	}

	public DataPropertyAssertion newDataPropertyAssertion() {
		return newInstance(DataPropertyAssertion.class);
	}

	public ObjectPropertyAssertion newObjectPropertyAssertion() {
		return newInstance(ObjectPropertyAssertion.class);
	}

	public EditionPatternBehaviouralFacet newEditionPatternBehaviouralFacet(EditionPattern editionPattern) {
		EditionPatternBehaviouralFacet returned = newInstance(EditionPatternBehaviouralFacet.class);
		returned.setEditionPattern(editionPattern);
		return returned;
	}

	public EditionPatternStructuralFacet newEditionPatternStructuralFacet(EditionPattern editionPattern) {
		EditionPatternStructuralFacet returned = newInstance(EditionPatternStructuralFacet.class);
		returned.setEditionPattern(editionPattern);
		return returned;
	}

	public URIParameter newURIParameter() {
		return newInstance(URIParameter.class);
	}

	public TextFieldParameter newTextFieldParameter() {
		return newInstance(TextFieldParameter.class);
	}

	public TextAreaParameter newTextAreaParameter() {
		return newInstance(TextAreaParameter.class);
	}

	public IntegerParameter newIntegerParameter() {
		return newInstance(IntegerParameter.class);
	}

	public CheckboxParameter newCheckboxParameter() {
		return newInstance(CheckboxParameter.class);
	}

	public DropDownParameter newDropDownParameter() {
		return newInstance(DropDownParameter.class);
	}

	public IndividualParameter newIndividualParameter() {
		return newInstance(IndividualParameter.class);
	}

	public ClassParameter newClassParameter() {
		return newInstance(ClassParameter.class);
	}

	public PropertyParameter newPropertyParameter() {
		return newInstance(PropertyParameter.class);
	}

	public ObjectPropertyParameter newObjectPropertyParameter() {
		return newInstance(ObjectPropertyParameter.class);
	}

	public DataPropertyParameter newDataPropertyParameter() {
		return newInstance(DataPropertyParameter.class);
	}

	public TechnologyObjectParameter newTechnologyObjectParameter() {
		return newInstance(TechnologyObjectParameter.class);
	}

	public ListParameter newListParameter() {
		return newInstance(ListParameter.class);
	}

	public EditionPatternInstanceParameter newEditionPatternInstanceParameter() {
		return newInstance(EditionPatternInstanceParameter.class);
	}

	public EditionSchemeParameters newEditionSchemeParameters(EditionScheme editionScheme) {
		EditionSchemeParameters returned = newInstance(EditionSchemeParameters.class);
		returned.setEditionScheme(editionScheme);
		return returned;
	}

	public FetchRequestCondition newFetchRequestCondition() {
		return newInstance(FetchRequestCondition.class);
	}

	public EditionPatternInspector newEditionPatternInspector(EditionPattern ep) {
		EditionPatternInspector returned = newInstance(EditionPatternInspector.class);
		returned.setEditionPattern(ep);
		return returned;
	}

	public TextFieldInspectorEntry newTextFieldInspectorEntry() {
		return newInstance(TextFieldInspectorEntry.class);
	}

	public TextAreaInspectorEntry newTextAreaInspectorEntry() {
		return newInstance(TextAreaInspectorEntry.class);
	}

	public IntegerInspectorEntry newIntegerInspectorEntry() {
		return newInstance(IntegerInspectorEntry.class);
	}

	public CheckboxInspectorEntry newCheckboxInspectorEntry() {
		return newInstance(CheckboxInspectorEntry.class);
	}

	public IndividualInspectorEntry newIndividualInspectorEntry() {
		return newInstance(IndividualInspectorEntry.class);
	}

	public ClassInspectorEntry newClassInspectorEntry() {
		return newInstance(ClassInspectorEntry.class);
	}

	public PropertyInspectorEntry newPropertyInspectorEntry() {
		return newInstance(PropertyInspectorEntry.class);
	}

	public ObjectPropertyInspectorEntry newObjectPropertyInspectorEntry() {
		return newInstance(ObjectPropertyInspectorEntry.class);
	}

	public DataPropertyInspectorEntry newDataPropertyInspectorEntry() {
		return newInstance(DataPropertyInspectorEntry.class);
	}

	public CreateEditionPatternInstanceParameter newCreateEditionPatternInstanceParameter(EditionSchemeParameter p) {
		CreateEditionPatternInstanceParameter returned = newInstance(CreateEditionPatternInstanceParameter.class);
		returned.setParam(p);
		return returned;
	}

	public MatchingCriteria newMatchingCriteria(PatternRole pr) {
		MatchingCriteria returned = newInstance(MatchingCriteria.class);
		returned.setPatternRole(pr);
		return returned;
	}

	public EditionPatternInstancePatternRole newEditionPatternInstancePatternRole() {
		return newInstance(EditionPatternInstancePatternRole.class);
	}

	public AddEditionPatternInstance newAddEditionPatternInstance() {
		return newInstance(AddEditionPatternInstance.class);
	}

	public SelectEditionPatternInstance newSelectEditionPatternInstance() {
		return newInstance(SelectEditionPatternInstance.class);
	}

	public EditionPattern newEditionPattern() {
		return newInstance(EditionPattern.class);
	}

	public DeleteAction newDeleteAction() {
		return newInstance(DeleteAction.class);
	}

	public MatchEditionPatternInstance newMatchEditionPatternInstance() {
		return newInstance(MatchEditionPatternInstance.class);
	}

	public DeclarePatternRole newDeclarePatternRole() {
		return newInstance(DeclarePatternRole.class);
	}

	public ExecutionAction newExecutionAction() {
		return newInstance(ExecutionAction.class);
	}

	public RemoveFromListAction newRemoveFromListAction() {
		return newInstance(RemoveFromListAction.class);
	}

	public AddToListAction newAddToListAction() {
		return newInstance(AddToListAction.class);
	}

	public AssignationAction newAssignationAction() {
		return newInstance(AssignationAction.class);
	}

	public ConditionalAction newConditionalAction() {
		return newInstance(ConditionalAction.class);
	}

	public IterationAction newIterationAction() {
		return newInstance(IterationAction.class);
	}

	public FetchRequestIterationAction newFetchRequestIterationAction() {
		return newInstance(FetchRequestIterationAction.class);
	}

}
