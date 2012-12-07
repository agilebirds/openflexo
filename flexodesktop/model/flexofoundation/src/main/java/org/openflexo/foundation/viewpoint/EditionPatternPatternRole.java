package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.view.ModelObjectActorReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class EditionPatternPatternRole extends PatternRole<EditionPatternReference> {

	private EditionPattern editionPatternType;
	private CreationScheme creationScheme;
	private String _creationSchemeURI;

	public EditionPatternPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

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
	public Class<EditionPatternReference> getAccessedClass() {
		return EditionPatternReference.class;
	}

	@Override
	public boolean getIsPrimaryRole() {
		return false;
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
		// Not relevant
	}

	public EditionPattern getEditionPatternType() {
		if (getCreationScheme() != null) {
			return getCreationScheme().getEditionPattern();
		}
		return editionPatternType;
	}

	public void setEditionPatternType(EditionPattern editionPatternType) {
		if (editionPatternType != this.editionPatternType) {
			this.editionPatternType = editionPatternType;
			if (getCreationScheme() != null && getCreationScheme().getEditionPattern() != editionPatternType) {
				setCreationScheme(null);
			}
			for (EditionScheme s : getEditionPattern().getEditionSchemes()) {
				s.updateBindingModels();
			}
		}
	}

	public String _getCreationSchemeURI() {
		if (getCreationScheme() != null) {
			return getCreationScheme().getURI();
		}
		return _creationSchemeURI;
	}

	public void _setCreationSchemeURI(String uri) {
		if (getViewPointLibrary() != null) {
			creationScheme = (CreationScheme) getViewPointLibrary().getEditionScheme(uri);
			for (EditionScheme s : getEditionPattern().getEditionSchemes()) {
				s.updateBindingModels();
			}
		}
		_creationSchemeURI = uri;
	}

	public CreationScheme getCreationScheme() {
		if (creationScheme == null && _creationSchemeURI != null && getViewPointLibrary() != null) {
			creationScheme = (CreationScheme) getViewPointLibrary().getEditionScheme(_creationSchemeURI);
		}
		return creationScheme;
	}

	public void setCreationScheme(CreationScheme creationScheme) {
		this.creationScheme = creationScheme;
		if (creationScheme != null) {
			_creationSchemeURI = creationScheme.getURI();
		}
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return false;
	}

	@Override
	public ModelObjectActorReference<EditionPatternReference> makeActorReference(EditionPatternReference object,
			EditionPatternReference epRef) {
		return new ModelObjectActorReference<EditionPatternReference>(object, this, epRef);
	}

}
