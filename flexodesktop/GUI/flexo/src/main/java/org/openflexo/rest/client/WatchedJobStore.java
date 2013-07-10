package org.openflexo.rest.client;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
public interface WatchedJobStore {

	public static final String PENDING_DOC_JOBS = "pendingDocJobs";

	@XMLElement
	@Getter(value = PENDING_DOC_JOBS, cardinality = Cardinality.LIST)
	public List<DocJob> getPendingDocJobs();

	@Setter(PENDING_DOC_JOBS)
	public void setPendingDocJobs(List<DocJob> docJobs);

	@Adder(PENDING_DOC_JOBS)
	public void addToPendingJobs(DocJob docJob);

	@Setter(PENDING_DOC_JOBS)
	public void removeFromPendingJobs(DocJob docJob);
}
