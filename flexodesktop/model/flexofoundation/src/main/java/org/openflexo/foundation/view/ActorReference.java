package org.openflexo.foundation.view;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.XMLMapping;

public abstract class ActorReference<T> extends VirtualModelInstanceObject {
	private PatternRole<T> patternRole;
	private String patternRoleName;
	private ModelSlot<?, ?> modelSlot;
	private FlexoProject _project;
	private EditionPatternReference _patternReference;

	protected ActorReference(FlexoProject project) {
		super(project);
		_project = project;
	}

	public ModelSlot<?, ?> getModelSlot() {
		return modelSlot;
	}

	public void setModelSlot(ModelSlot<?, ?> modelSlot) {
		this.modelSlot = modelSlot;
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public XMLMapping getXMLMapping() {
		// Not defined in this context, since this is cross-model object
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		// Not defined in this context, since this is cross-model object
		return null;
	}

	public abstract T retrieveObject();

	public EditionPatternReference getPatternReference() {
		return _patternReference;
	}

	public void setPatternReference(EditionPatternReference _patternReference) {
		this._patternReference = _patternReference;
	}

	public PatternRole<T> getPatternRole() {
		if (patternRole == null && _patternReference != null && StringUtils.isNotEmpty(patternRoleName)) {
			patternRole = _patternReference.getEditionPattern().getPatternRole(patternRoleName);
		}
		return patternRole;
	}

	public void setPatternRole(PatternRole<T> patternRole) {
		this.patternRole = patternRole;
	}

	public String getPatternRoleName() {
		return patternRole.getPatternRoleName();
	}

	public void setPatternRoleName(String patternRoleName) {
		this.patternRoleName = patternRoleName;
	}

	@Override
	public VirtualModelInstance<?, ?> getVirtualModelInstance() {
		if (getPatternReference() != null) {
			return getPatternReference().getEditionPatternInstance().getVirtualModelInstance();
		}
		return null;
	}
}