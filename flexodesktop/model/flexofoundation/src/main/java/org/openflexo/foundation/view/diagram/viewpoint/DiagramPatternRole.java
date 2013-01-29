package org.openflexo.foundation.view.diagram.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.view.ModelObjectActorReference;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.localization.FlexoLocalization;

public class DiagramPatternRole extends PatternRole<View> {

	private DiagramSpecification diagramSpecification;

	public DiagramPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public String getPreciseType() {
		if (getDiagramSpecification() != null) {
			return getDiagramSpecification().getName();
		}
		return FlexoLocalization.localizedForKey("diagram");
	}

	@Override
	public Type getType() {
		return View.class;
	}

	@Override
	public boolean getIsPrimaryRole() {
		return false;
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
		// Not relevant
	}

	public DiagramSpecification getDiagramSpecification() {
		return diagramSpecification;
	}

	public void setDiagramSpecification(DiagramSpecification diagramSpecification) {
		this.diagramSpecification = diagramSpecification;
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return false;
	}

	@Override
	public ModelObjectActorReference<View> makeActorReference(View object, EditionPatternReference epRef) {
		return new ModelObjectActorReference<View>(object, this, epRef);
	}
}
