package org.flexo.model2;

import org.openflexo.model.annotations.ModelEntity;

@ModelEntity
public interface TestContainerB extends TestContainer<TestEmbeddedB>, EmbeddingB {

	/*@Override
	@Getter(value = EMBEDDED)
	@Embedded
	public TestEmbeddedB getEmbedded();

	@Override
	@Setter(value = EMBEDDED)
	public void setEmbedded(TestEmbeddedB embedded);*/

}
