package org.openflexo.foundation.rm;

import java.util.logging.Logger;

import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class FlexoProjectReference implements StringConvertable<FlexoProjectReference> {

	private static final Logger logger = FlexoLogger.getLogger(FlexoModelObjectReference.class.getPackage().getName());

	public static interface ReferenceOwner {

		public void notifyProjectLoaded(FlexoProjectReference reference);

		public void objectCantBeFound(FlexoProjectReference reference);

		public void objectDeleted(FlexoProjectReference reference);

	}

	public enum ReferenceStatus {
		RESOLVED, UNRESOLVED, NOT_FOUND, RESOURCE_NOT_FOUND, DELETED
	}

	private ReferenceStatus status = ReferenceStatus.UNRESOLVED;

	private String serializationRepresentation;

	private FlexoProject referringProject;

	private FlexoProject referredProject;

	public FlexoProjectReference(FlexoProject project, String serializationRepresentation) {
		this.referringProject = project;
		this.serializationRepresentation = serializationRepresentation;
		this.status = ReferenceStatus.UNRESOLVED;
	}

	public FlexoProjectReference(FlexoProject referringProject, FlexoProject referredProject) {
		this.referringProject = referringProject;
		this.referredProject = referredProject;
		this.status = ReferenceStatus.RESOLVED;
	}

	public FlexoProject getProject() {
		if (referredProject == null && serializationRepresentation != null) {
			referredProject = referringProject.loadProject(serializationRepresentation);
			if (referredProject != null) {
				serializationRepresentation = null;
			}
		}
		return referredProject;
	}

	public void delete() {

	}

	@Override
	public String toString() {
		return "FlexoProjectReference ";
	}

	@Override
	public Converter<? extends FlexoProjectReference> getConverter() {
		return null;
	}

}