package org.openflexo.model3;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.factory.AccessibleProxyObject;

@ModelEntity
public interface IntegerStorage extends AccessibleProxyObject {

	public static final String STORED_VALUE = "storedValue";

	@Getter(value = STORED_VALUE, defaultValue = "-1")
	public int getStoredValue();

	@Setter(value = STORED_VALUE)
	public void setStoredValue(int aValue);

	public void reset();

	@Implementation
	public abstract class IntegerStorageImpl implements IntegerStorage {
		@Override
		public void reset() {
			setStoredValue(0);
		}

		@Override
		public void setStoredValue(int aValue) {
			performSuperSetter(STORED_VALUE, aValue);
			System.out.println("Sets stored value to be " + aValue);
		}
	}
}
