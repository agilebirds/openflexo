package org.openflexo.model3;

import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity
public interface B extends A {

	@Implementation
	public static abstract class BImpl implements B {
		@Override
		public String getFoo() {
			return "foo in class B";
		}

		@Override
		public void setFoo2(String foo) {
			System.out.println("setFoo2 in class B with " + foo);
		}

		@Override
		public void methodExecution() {
			System.out.println("Executing methodExecutionInClassB");
		}
	}

}
