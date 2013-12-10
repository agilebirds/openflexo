package org.openflexo.antar.binding;

import java.util.logging.Logger;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.logging.FlexoLogger;

/**
 * A listener that tracks evalutated value of a list DataBinding, given a run-time context given by a {@link BindingEvaluationContext}<br>
 * Modifications are detected from the evaluation of the content of the list
 * 
 * @author sylvain
 * 
 */
public abstract class BindingValueArrayChangeListener<T> extends BindingValueChangeListener<T[]> {

	private static final Logger logger = FlexoLogger.getLogger(BindingValueArrayChangeListener.class.getName());

	private T[] lastKnownValues = null;

	public BindingValueArrayChangeListener(DataBinding<T[]> dataBinding, BindingEvaluationContext context) {
		super(dataBinding, context);
	}

	public void delete() {
		super.delete();
	}

	protected void fireChange(Object source) {

		T[] newValue;
		try {
			newValue = evaluateValue();
		} catch (NullReferenceException e) {
			logger.warning("Could not evaluate " + getDataBinding() + " with context " + getContext()
					+ " because NullReferenceException has raised");
			newValue = null;
		}
		if (newValue != lastNotifiedValue) {
			lastNotifiedValue = newValue;
			bindingValueChanged(source, newValue);
		} else {
			// Arrays are sames, but values inside arrays, may have changed

			if (lastKnownValues == null || newValue == null) {
				// We continue
			} else if (lastKnownValues.length != newValue.length) {
				// We continue
			} else {
				boolean valuesAreSame = true;
				for (int i = 0; i < lastKnownValues.length; i++) {
					if (!lastKnownValues[i].equals(newValue[i])) {
						valuesAreSame = false;
					}
				}
				if (!valuesAreSame) {
					bindingValueChanged(source, newValue);
				}
			}
		}
	}
}
