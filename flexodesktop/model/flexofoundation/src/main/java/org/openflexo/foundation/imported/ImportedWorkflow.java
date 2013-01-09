package org.openflexo.foundation.imported;

import org.openflexo.foundation.rm.PAMELAStorageResourceData;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement
public interface ImportedWorkflow extends PAMELAStorageResourceData {

	public static final String ORIGINAL_PROJECT_URI = "originalProjectURI";

	@Getter(ORIGINAL_PROJECT_URI)
	@XMLAttribute
	public String getOriginalProjectURI();

	@Setter(ORIGINAL_PROJECT_URI)
	public void setOriginalProjectURI(String originalProjectURI);

	public static abstract class ImportedWorkflowImpl extends PAMELAStorageResourceDataImpl {

	}
}
