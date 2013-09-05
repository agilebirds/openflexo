package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.VirtualModelInstance;

public class VirtualModelModelSlotPathElement<MS extends ModelSlot> extends SimplePathElement {

	private static final Logger logger = Logger.getLogger(VirtualModelModelSlotPathElement.class.getPackage().getName());

	private MS modelSlot;

	public VirtualModelModelSlotPathElement(BindingPathElement parent, MS modelSlot) {
		super(parent, modelSlot.getName(), modelSlot.getType());
		this.modelSlot = modelSlot;
	}

	@Override
	public String getLabel() {
		return modelSlot.getName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return modelSlot.getDescription();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (target instanceof VirtualModelInstance) {
			VirtualModelInstance mvi = (VirtualModelInstance) target;
			return mvi.getModelSlotInstance(modelSlot).getResourceData();
		}
		logger.warning("Please implement me, target=" + target + " context=" + context);
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		logger.warning("Please implement me, target=" + target + " context=" + context);
	}

}