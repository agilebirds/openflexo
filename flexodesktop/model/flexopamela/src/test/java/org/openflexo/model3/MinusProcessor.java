package org.openflexo.model3;

import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity
public interface MinusProcessor extends IntegerStorage {

	public int processMinus(int value);

	@Implementation
	public abstract class MinusProcessorImpl implements MinusProcessor {
		@Override
		public int processMinus(int value) {
			setStoredValue(getStoredValue() - value);
			return getStoredValue();
		}
	}

}
