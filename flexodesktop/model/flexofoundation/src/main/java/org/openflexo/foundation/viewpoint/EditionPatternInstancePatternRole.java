package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.ModelObjectActorReference;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;


@ModelEntity
@ImplementationClass(EditionPatternInstancePatternRole.EditionPatternInstancePatternRoleImpl.class)
@XMLElement
public interface EditionPatternInstancePatternRole extends PatternRole<EditionPatternInstance>{

@PropertyIdentifier(type=String.class)
public static final String EDITION_PATTERN_TYPE_URI_KEY = "editionPatternTypeURI";
@PropertyIdentifier(type=String.class)
public static final String CREATION_SCHEME_URI_KEY = "creationSchemeURI";

@Getter(value=EDITION_PATTERN_TYPE_URI_KEY)
@XMLAttribute
public String _getEditionPatternTypeURI();

@Setter(EDITION_PATTERN_TYPE_URI_KEY)
public void _setEditionPatternTypeURI(String editionPatternTypeURI);


@Getter(value=CREATION_SCHEME_URI_KEY)
@XMLAttribute
public String _getCreationSchemeURI();

@Setter(CREATION_SCHEME_URI_KEY)
public void _setCreationSchemeURI(String creationSchemeURI);


public static abstract  class EditionPatternInstancePatternRoleImpl extends PatternRole<EditionPatternInstance>Impl implements EditionPatternInstancePatternRole
{

	private static final Logger logger = Logger.getLogger(EditionPatternInstancePatternRole.class.getPackage().getName());

	private EditionPattern editionPatternType;
	private CreationScheme creationScheme;
	private String _creationSchemeURI;
	private String _editionPatternTypeURI;

	public EditionPatternInstancePatternRoleImpl() {
		super();
		// logger.severe("############# Created EditionPatternInstancePatternRole " + Integer.toHexString(hashCode()) + " model version="
		// + builder.getModelVersion() + " file=" + builder.resource.getFile().getAbsolutePath());
	}

	/*@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		logger.severe("############# Finalized EditionPatternInstancePatternRole " + Integer.toHexString(hashCode()) + toString());
	}*/

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("PatternRole " + getName() + " as EditionPatternInstance conformTo " + getPreciseType() + ";", context);
		return out.toString();
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

	/*@Override
	public boolean getIsPrimaryRole() {
		return false;
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
		// Not relevant
	}*/

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
	public VirtualModelModelSlot getModelSlot() {
		VirtualModelModelSlot returned = (VirtualModelModelSlot) super.getModelSlot();
		if (returned == null) {
			if (getVirtualModel() != null && getVirtualModel().getModelSlots(VirtualModelModelSlot.class).size() > 0) {
				return getVirtualModel().getModelSlots(VirtualModelModelSlot.class).get(0);
			}
		}
		return returned;
	}

	public VirtualModelModelSlot getVirtualModelModelSlot() {
		return getModelSlot();
	}

	public void setVirtualModelModelSlot(VirtualModelModelSlot modelSlot) {
		setModelSlot(modelSlot);
	}

}
}
