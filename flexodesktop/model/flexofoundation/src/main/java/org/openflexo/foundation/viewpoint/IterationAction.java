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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.toolbox.StringUtils;

@FIBPanel("Fib/IterationActionPanel.fib")
@ModelEntity
@ImplementationClass(IterationAction.IterationActionImpl.class)
@XMLElement
public interface IterationAction extends ControlStructureAction{

@PropertyIdentifier(type=DataBinding.class)
public static final String ITERATION_KEY = "iteration";
@PropertyIdentifier(type=String.class)
public static final String ITERATOR_NAME_KEY = "iteratorName";

@Getter(value=ITERATION_KEY)
@XMLAttribute
public DataBinding getIteration();

@Setter(ITERATION_KEY)
public void setIteration(DataBinding iteration);


@Getter(value=ITERATOR_NAME_KEY)
@XMLAttribute
public String getIteratorName();

@Setter(ITERATOR_NAME_KEY)
public void setIteratorName(String iteratorName);


public static abstract  class IterationActionImpl extends ControlStructureActionImpl implements IterationAction
{

	private static final Logger logger = Logger.getLogger(IterationAction.class.getPackage().getName());

	private String iteratorName = "item";

	public IterationActionImpl() {
		super();
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("for (" + getIteratorName() + " in " + getIteration().toString(), context);
		out.append(") {", context);
		out.append(StringUtils.LINE_SEPARATOR, context);
		for (EditionAction action : getActions()) {
			out.append(action.getFMLRepresentation(context), context, 1);
			out.append(StringUtils.LINE_SEPARATOR, context);
		}

		out.append("}", context);
		return out.toString();
	}

	private DataBinding<List<?>> iteration;

	public DataBinding<List<?>> getIteration() {
		if (iteration == null) {
			iteration = new DataBinding<List<?>>(this, List.class, BindingDefinitionType.GET);
		}
		return iteration;
	}

	public void setIteration(DataBinding<List<?>> iteration) {
		if (iteration != null) {
			iteration.setOwner(this);
			iteration.setBindingName("iteration");
			iteration.setDeclaredType(List.class);
			iteration.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.iteration = iteration;
		rebuildInferedBindingModel();
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> binding) {
		super.notifiedBindingChanged(binding);
		if (binding == iteration) {
			rebuildInferedBindingModel();
		}
	}

	public String getIteratorName() {
		return iteratorName;
	}

	public void setIteratorName(String iteratorName) {
		this.iteratorName = iteratorName;
		rebuildInferedBindingModel();
	}

	public Type getItemType() {
		if (getIteration() != null && getIteration().isSet()) {
			Type accessedType = getIteration().getAnalyzedType();
			if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
				return ((ParameterizedType) accessedType).getActualTypeArguments()[0];
			}
		}
		return Object.class;
	}

	@Override
	protected BindingModel buildInferedBindingModel() {
		BindingModel returned = super.buildInferedBindingModel();
		returned.addToBindingVariables(new BindingVariable(getIteratorName(), getItemType()) {
			@Override
			public Object getBindingValue(Object target, BindingEvaluationContext context) {
				logger.info("What should i return for " + getIteratorName() + " ? target " + target + " context=" + context);
				return super.getBindingValue(target, context);
			}

			@Override
			public Type getType() {
				return getItemType();
			}
		});
		return returned;
	}

	public List<?> evaluateIteration(EditionSchemeAction action) {
		if (getIteration().isValid()) {
			try {
				return getIteration().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Object performAction(EditionSchemeAction action) {
		List<?> items = evaluateIteration(action);
		if (items != null) {
			for (Object item : items) {
				System.out.println("> working with " + getIteratorName() + "=" + item);
				action.declareVariable(getIteratorName(), item);
				performBatchOfActions(getActions(), action);
			}
		}
		action.dereferenceVariable(getIteratorName());
		return null;
	}

	@Override
	public String getStringRepresentation() {
		if (getIteration().isSet() && getIteration().isValid()) {
			return getIteratorName() + " : " + getIteration();
		}
		return super.getStringRepresentation();
	}

	public static class IterationBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<IterationAction> {
		public IterationBindingIsRequiredAndMustBeValid() {
			super("'iteration'_binding_is_not_valid", IterationAction.class);
		}

		@Override
		public DataBinding<List<?>> getBinding(IterationAction object) {
			return object.getIteration();
		}

	}

}
}
