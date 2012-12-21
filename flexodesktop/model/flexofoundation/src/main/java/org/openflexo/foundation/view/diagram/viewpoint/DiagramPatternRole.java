package org.openflexo.foundation.view.diagram.viewpoint;

import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.view.ModelObjectActorReference;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.localization.FlexoLocalization;

public class DiagramPatternRole extends PatternRole<View> {

	private ViewPoint viewpoint;
	private String viewpointURI;

	public DiagramPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.Diagram;
	}

	@Override
	public String getPreciseType() {
		if (getViewpoint() != null) {
			return getViewpoint().getName();
		}
		return FlexoLocalization.localizedForKey("diagram");
	}

	@Override
	public Class<View> getAccessedClass() {
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

	public ViewPoint getViewpoint() {
		if (viewpoint == null && viewpointURI != null && getViewPointLibrary() != null) {
			viewpoint = getViewPointLibrary().getViewPoint(viewpointURI);
		}
		return viewpoint;
	}

	public void setViewpoint(ViewPoint viewpoint) {
		this.viewpoint = viewpoint;
		if (viewpoint != null) {
			viewpointURI = viewpoint.getURI();
		}
	}

	public String _getViewpointURI() {
		if (getViewpoint() != null) {
			return getViewpoint().getURI();
		}
		return viewpointURI;
	}

	public void _setViewpointURI(String uri) {
		if (getViewPointLibrary() != null) {
			viewpoint = getViewPointLibrary().getViewPoint(uri);
		}
		viewpointURI = uri;
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
