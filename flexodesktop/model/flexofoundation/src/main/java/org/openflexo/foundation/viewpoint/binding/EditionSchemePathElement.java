/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.Function.FunctionArgument;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.InvocationTargetTransformException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.action.ActionSchemeAction;
import org.openflexo.foundation.view.action.ActionSchemeActionType;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ActionScheme;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;

/**
 * Modelize a call for execution of an EditionScheme
 * 
 * @author sylvain
 * 
 */
public class EditionSchemePathElement extends FunctionPathElement {

	static final Logger logger = Logger.getLogger(EditionSchemePathElement.class.getPackage().getName());

	public EditionSchemePathElement(BindingPathElement parent, EditionScheme editionScheme, List<DataBinding<?>> args) {
		super(parent, editionScheme, args);
		if (editionScheme != null) {
			for (FunctionArgument arg : editionScheme.getArguments()) {
				DataBinding<?> argValue = getParameter(arg);
				if (argValue != null && arg != null) {
					argValue.setDeclaredType(arg.getArgumentType());
				}
			}
			setType(editionScheme.getReturnType());
		} else {
			logger.warning("Inconsistent data: null EditionScheme");
		}
	}

	public EditionScheme getEditionScheme() {
		return getFunction();
	}

	@Override
	public EditionScheme getFunction() {
		return (EditionScheme) super.getFunction();
	}

	@Override
	public String getLabel() {
		return getEditionScheme().getSignature();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return getEditionScheme().getDescription();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException,
			InvocationTargetTransformException {

		// System.out.println("evaluate " + getMethodDefinition().getSignature() + " for " + target);

		Object[] args = new Object[getFunction().getArguments().size()];
		int i = 0;

		for (EditionSchemeParameter param : getFunction().getArguments()) {
			try {
				args[i] = TypeUtils.castTo(getParameter(param).getBindingValue(context), param.getType());
			} catch (InvocationTargetException e) {
				throw new InvocationTargetTransformException(e);
			}
			i++;
		}
		try {
			logger.warning("Please implements execution of EditionSchemePathElement here !!!! context=" + context + " of "
					+ context.getClass() + " target=" + target);

			if (context instanceof EditionSchemeAction && target instanceof EditionPatternInstance) {

				EditionSchemeAction action = (EditionSchemeAction) context;
				EditionPatternInstance epi = (EditionPatternInstance) target;
				logger.info("EPI: " + epi);
				ActionSchemeActionType actionType = new ActionSchemeActionType((ActionScheme) getEditionScheme(), epi);
				ActionSchemeAction actionSchemeAction = actionType.makeNewEmbeddedAction(epi.getVirtualModelInstance(), null, action);
				for (EditionSchemeParameter p : getEditionScheme().getParameters()) {
					DataBinding<?> param = getParameter(p);
					Object paramValue = param.getBindingValue(context);
					logger.info("For parameter " + param + " value is " + paramValue);
					if (paramValue != null) {
						actionSchemeAction.setParameterValue(p, paramValue);
					}
				}
				actionSchemeAction.doAction();
				if (actionSchemeAction.hasActionExecutionSucceeded()) {
					logger.info("Successfully performed ActionScheme " + getEditionScheme() + " for " + epi);
					return actionSchemeAction.getEditionPatternInstance();
				}
			}
			// return getMethodDefinition().getMethod().invoke(target, args);
		} catch (IllegalArgumentException e) {
			StringBuffer warningMessage = new StringBuffer("While evaluating edition scheme " + getEditionScheme() + " exception occured: "
					+ e.getMessage());
			warningMessage.append(", object = " + target);
			for (i = 0; i < getFunction().getArguments().size(); i++) {
				warningMessage.append(", arg[" + i + "] = " + args[i]);
			}
			logger.warning(warningMessage.toString());
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
