package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.view.View;
import org.openflexo.localization.FlexoLocalization;

public class DiagramPatternRole extends PatternRole {

	private ViewPoint viewpoint;
	private String viewpointURI;

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.Diagram;
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("diagram");
	}

	@Override
	public Class<?> getAccessedClass() {
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
		return viewpoint;
	}

	public void setViewpoint(ViewPoint viewpoint) {
		this.viewpoint = viewpoint;
	}

	public String _getViewpointURI() {
		if (getViewpoint() != null)
			return getViewpoint().getURI();
		return viewpointURI;
	}

	public void _setViewpointURI(String uri) {
		if (getViewPointLibrary() != null) {
			viewpoint = getViewPointLibrary().getViewPoint(uri);
		}
		viewpointURI = uri;
	}

}
