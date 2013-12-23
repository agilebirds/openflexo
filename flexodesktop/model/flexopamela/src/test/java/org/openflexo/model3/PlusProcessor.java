package org.openflexo.model3;

import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity
public interface PlusProcessor extends IntegerStorage {

	public int processPlus(int value);

	@Implementation
	public abstract class PlusProcessorImpl implements PlusProcessor {
		@Override
		public int processPlus(int value) {
			setStoredValue(getStoredValue() + value);
			return getStoredValue();
		}
	}

}
