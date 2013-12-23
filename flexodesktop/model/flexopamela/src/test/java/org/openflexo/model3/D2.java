package org.openflexo.model3;

import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity
public interface D2 extends B, C {

	@Implementation
	public abstract class D2Impl implements D2 {
		@Override
		public String getFoo() {
			return "foo in class D2";
		}

		@Override
		public void setFoo2(String foo) {
			System.out.println("setFoo2 in class D2 with " + foo);
		}

		@Override
		public void methodExecution() {
			System.out.println("Executing methodExecutionInClassD2");
		}
	}

}
