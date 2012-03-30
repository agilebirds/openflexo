package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.view.View;
import org.openflexo.toolbox.StringUtils;

public class EditionPatternPatternRole extends PatternRole {

	private ViewPoint viewpoint;
	private String viewpointURI;
	private String _editionPatternTypeURI;
	private CreationScheme creationScheme;

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.EditionPattern;
	}

	@Override
	public String getPreciseType() {
		if (getEditionPatternType() != null) {
			return getEditionPatternType().getName();
		}
		return "EditionPattern";
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

	public String _getEditionPatternTypeURI() {
		return _editionPatternTypeURI;
	}

	public void _setEditionPatternTypeURI(String editionPatternTypeURI) {
		this._editionPatternTypeURI = editionPatternTypeURI;
	}

	public EditionPattern getEditionPatternType() {
		if (StringUtils.isEmpty(_getEditionPatternTypeURI())) {
			return null;
		}
		if (getViewPointLibrary() != null) {
			return getViewPointLibrary().getEditionPattern(_getEditionPatternTypeURI());
		}
		return null;
	}

	public void setEditionPatternType(EditionPattern editionPatternType) {
		_setEditionPatternTypeURI(editionPatternType != null ? editionPatternType.getURI() : null);
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

	public CreationScheme getCreationScheme() {
		return creationScheme;
	}

	public void setCreationScheme(CreationScheme creationScheme) {
		this.creationScheme = creationScheme;
	}

}
