package org.flexo.model2;

import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;

@ModelEntity
public interface TestContainer<T extends TestEmbedded> {

	public static final String EMBEDDED = "embedded";

	@Getter(value = EMBEDDED)
	@Embedded
	public T getEmbedded();

	@Setter(value = EMBEDDED)
	public void setEmbedded(T embedded);

}
