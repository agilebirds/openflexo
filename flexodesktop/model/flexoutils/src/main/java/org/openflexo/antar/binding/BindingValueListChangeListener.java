package org.openflexo.antar.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

/**
 * A listener that tracks evalutated value of a list DataBinding, given a run-time context given by a {@link BindingEvaluationContext}<br>
 * Modifications are detected from the evaluation of the content of the list (even if the list is the same, but if values inside the list
 * are not the same, then change is fired)
 * 
 * @author sylvain
 * 
 */
public abstract class BindingValueListChangeListener<T2, T extends List<T2>> extends BindingValueChangeListener<T> {

	private static final Logger logger = FlexoLogger.getLogger(BindingValueListChangeListener.class.getName());

	private List<T2> lastKnownValues = null;

	public BindingValueListChangeListener(DataBinding<T> dataBinding, BindingEvaluationContext context) {
		super(dataBinding, context);
	}

	public void delete() {
		super.delete();
	}

	protected void fireChange(Object source) {

		T newValue = evaluateValue();
		if (newValue != lastNotifiedValue) {
			lastNotifiedValue = newValue;
			bindingValueChanged(source, newValue);
		} else {
			// Lists are sames, but values inside lists, may have changed
			if ((lastKnownValues == null) || (!lastKnownValues.equals(newValue))) {
				lastKnownValues = (newValue != null ? new ArrayList<T2>(newValue) : null);
				bindingValueChanged(source, newValue);
			}
		}
	}
}
