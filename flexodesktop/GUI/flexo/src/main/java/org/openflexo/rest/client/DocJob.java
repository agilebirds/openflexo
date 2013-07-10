package org.openflexo.rest.client;

import java.io.File;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.factory.AccessibleProxyObject;

@ModelEntity
public interface DocJob extends AccessibleProxyObject {

	public static final String JOB_ID = "jobId";
	public static final String FOLDER = "folder";

	@XMLAttribute
	@Getter(JOB_ID)
	public Integer getJobId();

	@Setter(JOB_ID)
	public void setJobId(Integer jobId);

	@XMLAttribute
	@Getter(FOLDER)
	public File getFolder();

	@Setter(FOLDER)
	public void setFolder(File folder);
}
