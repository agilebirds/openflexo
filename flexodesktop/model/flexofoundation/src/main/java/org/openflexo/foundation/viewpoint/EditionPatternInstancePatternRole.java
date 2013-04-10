package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.ModelObjectActorReference;

public class EditionPatternInstancePatternRole extends PatternRole<EditionPatternInstance> {

	private EditionPattern editionPatternType;
	private CreationScheme creationScheme;
	private String _creationSchemeURI;
	private String _editionPatternTypeURI;

	public EditionPatternInstancePatternRole(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		return EditionPatternInstanceType.getEditionPatternInstanceType(getEditionPatternType());
	}

	@Override
	public String getPreciseType() {
		if (getEditionPatternType() != null) {
			return getEditionPatternType().getName();
		}
		return "EditionPattern";
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
		if (editionPatternType == null && _editionPatternTypeURI != null && getViewPoint() != null) {
			editionPatternType = getViewPoint().getEditionPattern(_editionPatternTypeURI);
		}
		return editionPatternType;
	}

	public void setEditionPatternType(EditionPattern editionPatternType) {
		if (editionPatternType != this.editionPatternType) {
			this.editionPatternType = editionPatternType;
			if (getCreationScheme() != null && getCreationScheme().getEditionPattern() != editionPatternType) {
				setCreationScheme(null);
			}
			if (getEditionPattern() != null) {
				for (EditionScheme s : getEditionPattern().getEditionSchemes()) {
					s.updateBindingModels();
				}
			}
		}
	}

	@Override
	public void finalizePatternRoleDeserialization() {
		super.finalizePatternRoleDeserialization();
		if (editionPatternType == null && _editionPatternTypeURI != null && getViewPoint() != null) {
			editionPatternType = getViewPoint().getEditionPattern(_editionPatternTypeURI);
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

	public String _getEditionPatternTypeURI() {
		if (getEditionPatternType() != null) {
			return getEditionPatternType().getURI();
		}
		return _editionPatternTypeURI;
	}

	public void _setEditionPatternTypeURI(String uri) {
		if (getViewPoint() != null) {
			editionPatternType = getViewPoint().getEditionPattern(uri);
		}
		_editionPatternTypeURI = uri;
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
	public ModelObjectActorReference<EditionPatternInstance> makeActorReference(EditionPatternInstance object, EditionPatternInstance epi) {
		return new ModelObjectActorReference<EditionPatternInstance>(object, this, epi);
	}

	@Override
	public VirtualModelModelSlot<?, ?> getModelSlot() {
		VirtualModelModelSlot<?, ?> returned = (VirtualModelModelSlot<?, ?>) super.getModelSlot();
		if (returned == null) {
			if (getVirtualModel() != null && getVirtualModel().getModelSlots(VirtualModelModelSlot.class).size() > 0) {
				return getVirtualModel().getModelSlots(VirtualModelModelSlot.class).get(0);
			}
		}
		return returned;
	}

	public VirtualModelModelSlot<?, ?> getVirtualModelModelSlot() {
		return getModelSlot();
	}

	public void setVirtualModelModelSlot(VirtualModelModelSlot<?, ?> modelSlot) {
		setModelSlot(modelSlot);
	}

}
