package org.openflexo.model3;

import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity
public interface C extends A {

	@Implementation
	public abstract class CImpl implements C {
		@Override
		public String getFoo2() {
			return "foo2 in class C, foo=" + getFoo();
		}

		@Override
		public void setFoo2(String foo) {
			System.out.println("setFoo2 in class C with " + foo);
		}

		@Override
		public void methodExecution() {
			System.out.println("Executing methodExecutionInClassC");
		}
	}
}
