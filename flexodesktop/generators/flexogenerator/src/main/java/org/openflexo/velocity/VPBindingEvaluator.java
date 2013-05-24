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
package org.openflexo.velocity;

import org.apache.velocity.util.introspection.VelMethod;
import org.apache.velocity.util.introspection.VelPropertyGet;
import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class VPBindingEvaluator implements VelPropertyGet, VelMethod {

	private static final String PARAM = "evaluatedParam";

	private static final String OBJECT = "object";

	public static ViewPointDataBinding buildBindingForMethodAndParams(String methodName, Object[] args) {
		StringBuilder binding = new StringBuilder();
		binding.append(OBJECT).append('.').append(methodName).append('(');
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				binding.append(',');
			}
			binding.append(PARAM).append(i);
		}
		binding.append(')');
		return new ViewPointDataBinding(binding.toString());
	}

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(VPBindingEvaluator.class
			.getPackage().getName());

	private final ViewPointDataBinding viewPointDataBinding;
	private final BindingEvaluationContext evaluationContext;

	public VPBindingEvaluator(ViewPointDataBinding viewPointDataBinding, BindingEvaluationContext evaluationContext) {
		this.viewPointDataBinding = viewPointDataBinding;
		this.evaluationContext = evaluationContext;
	}

	@Override
	public Object invoke(Object o) throws Exception {
		return viewPointDataBinding.getBindingValue(evaluationContext);
	}

	@Override
	public boolean isCacheable() {
		return true;
	}

	@Override
	public String getMethodName() {
		return viewPointDataBinding.toString();
	}

	@Override
	public Object invoke(final Object object, final Object[] params) throws Exception {

		return viewPointDataBinding.getBindingValue(new BindingEvaluationContext() {

			@Override
			public Object getValue(BindingVariable variable) {
				if (variable.getVariableName().equals(OBJECT)) {
					return object;
				} else if (variable.getVariableName().startsWith(PARAM)) {
					try {
						int i = Integer.parseInt(variable.getVariableName().substring(PARAM.length()));
						if (i < params.length) {
							return params[i];
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				return evaluationContext.getValue(variable);
			}
		});
	}

	@Override
	public Class<?> getReturnType() {
		return TypeUtils.getBaseClass(viewPointDataBinding.getBinding().getAccessedType());
	}

}
