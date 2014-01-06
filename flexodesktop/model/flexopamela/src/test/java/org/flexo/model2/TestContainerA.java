package org.flexo.model2;

import org.openflexo.model.annotations.ModelEntity;

@ModelEntity
public interface TestContainerA extends TestContainer<TestEmbeddedA> {

	/*@Override
	@Getter(value = EMBEDDED)
	@Embedded
	public TestEmbeddedA getEmbedded();

	@Override
	@Setter(value = EMBEDDED)
	public void setEmbedded(TestEmbeddedA embedded);*/

}
