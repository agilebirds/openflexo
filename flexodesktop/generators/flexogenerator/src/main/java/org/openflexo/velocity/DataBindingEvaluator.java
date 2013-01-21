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
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.logging.FlexoLogger;

public class DataBindingEvaluator implements VelPropertyGet, VelMethod {

	private static final java.util.logging.Logger logger = FlexoLogger.getLogger(DataBindingEvaluator.class.getPackage().getName());

	private static final String PARAM = "evaluatedParam";

	private static final String OBJECT = "object";

	public static DataBinding<?> buildBindingForMethodAndParams(String methodName, Object[] args) {
		StringBuilder binding = new StringBuilder();
		binding.append(OBJECT).append('.').append(methodName).append('(');
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				binding.append(',');
			}
			binding.append(PARAM).append(i);
		}
		binding.append(')');
		return new DataBinding<Object>(binding.toString());
	}

	private final DataBinding<?> dataBinding;
	private final BindingEvaluationContext evaluationContext;

	public DataBindingEvaluator(DataBinding<?> dataBinding, BindingEvaluationContext evaluationContext) {
		this.dataBinding = dataBinding;
		this.evaluationContext = evaluationContext;
	}

	@Override
	public Object invoke(Object o) throws Exception {
		return dataBinding.getBindingValue(evaluationContext);
	}

	@Override
	public boolean isCacheable() {
		return true;
	}

	@Override
	public String getMethodName() {
		return dataBinding.toString();
	}

	@Override
	public Object invoke(final Object object, final Object[] params) throws Exception {

		return dataBinding.getBindingValue(new BindingEvaluationContext() {

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
		return TypeUtils.getBaseClass(dataBinding.getAnalyzedType());
	}

}
