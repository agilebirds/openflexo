package org.openflexo.model3;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;

@ModelEntity
public interface A {

	public static final String FOO = "foo";
	public static final String FOO2 = "foo2";

	@Getter(value = FOO)
	public String getFoo();

	@Setter(value = FOO)
	public void setFoo(String foo);

	@Getter(value = FOO2)
	public String getFoo2();

	@Setter(value = FOO2)
	public void setFoo2(String foo);

	public void methodExecution();
}
