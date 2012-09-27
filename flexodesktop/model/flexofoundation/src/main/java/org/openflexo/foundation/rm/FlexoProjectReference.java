package org.openflexo.foundation.rm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class FlexoProjectReference implements StringConvertable<FlexoProjectReference> {

	private static final Logger logger = FlexoLogger.getLogger(FlexoModelObjectReference.class.getPackage().getName());

	private static final String SEPARATOR = "|";

	public static interface ReferenceOwner {

		public void projectDeleted(FlexoProjectReference reference);

	}

	public enum ReferenceStatus {
		RESOLVED, UNRESOLVED, UNDECODABLE;
	}

	private ReferenceStatus status = ReferenceStatus.UNRESOLVED;

	private String serializationRepresentation;

	private String projectName;
	private String projectURI;
	private String projectVersion;
	private long projectRevision;

	private FlexoProject referringProject;

	private FlexoProject referredProject;

	public FlexoProjectReference(FlexoProject project, String serializationRepresentation) {
		this.referringProject = project;
		this.serializationRepresentation = serializationRepresentation;
		this.status = ReferenceStatus.UNDECODABLE;
		int start = serializationRepresentation.lastIndexOf(SEPARATOR);
		if (start > -1) {
			try {
				projectRevision = Long.valueOf(serializationRepresentation.substring(start + 1));
				int end = serializationRepresentation.lastIndexOf(SEPARATOR, start - 1);
				if (end > -1) {
					projectVersion = serializationRepresentation.substring(end + 1, start);
					start = end;
					end = serializationRepresentation.lastIndexOf(SEPARATOR, start - 1);
					if (end > -1) {
						projectURI = serializationRepresentation.substring(end + 1, start);
						projectName = serializationRepresentation.substring(0, end);
					} else {
						projectURI = serializationRepresentation.substring(0, end);
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Could not find project name in project reference '" + serializationRepresentation + "'");
						}
					}
					status = ReferenceStatus.UNRESOLVED;
				} else if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not decode project reference: '" + serializationRepresentation + "'");
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	public FlexoProjectReference(FlexoProject referringProject, FlexoProject referredProject) {
		this.referringProject = referringProject;
		this.referredProject = referredProject;
		this.status = ReferenceStatus.RESOLVED;
	}

	public ReferenceStatus getStatus() {
		return status;
	}

	public FlexoProject getProject() throws ProjectLoadingCancelledException {
		if (referredProject == null && serializationRepresentation != null) {
			referredProject = referringProject.loadProject(this);
			if (referredProject != null) {
				status = ReferenceStatus.RESOLVED;
				serializationRepresentation = null;
			}
		}
		return referredProject;
	}

	public static String getSerializationRepresentation(FlexoProject project) {
		return project.getProjectName() + SEPARATOR + project.getURI() + SEPARATOR
				+ (project.getVersion() != null ? project.getVersion() : "") + SEPARATOR + project.getRevision();
	}

	public void delete() {

	}

	@Override
	public String toString() {
		return "FlexoProjectReference " + getSerializationRepresentation();
	}

	public String getSerializationRepresentation() {
		if (referredProject != null) {
			return getSerializationRepresentation(referredProject);
		} else {
			return serializationRepresentation;
		}
	}

	@Override
	public Converter<FlexoProjectReference> getConverter() {
		return referringProject.getProjectReferenceConverter();
	}

	public String getProjectName() {
		if (referredProject != null) {
			return referredProject.getProjectName();
		} else {
			return projectName;
		}
	}

	public String getProjectURI() {
		if (referredProject != null) {
			return referredProject.getProjectURI();
		} else {
			return projectURI;
		}
	}

	public String getProjectVersion() {
		if (referredProject != null) {
			return referredProject.getVersion();
		} else {
			return projectVersion;
		}
	}

	public long getProjectRevision() {
		if (referredProject != null) {
			return referredProject.getRevision();
		} else {
			return projectRevision;
		}
	}

}