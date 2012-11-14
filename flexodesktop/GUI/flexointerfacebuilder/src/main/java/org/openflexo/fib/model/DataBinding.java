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
package org.openflexo.fib.model;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Function;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.fib.model.FIBModelObject.FIBModelAttribute;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class DataBinding implements StringConvertable<DataBinding> {

	private static final Logger logger = Logger.getLogger(FIBComponent.class.getPackage().getName());

	public static DataBinding.DataBindingConverter CONVERTER = new DataBindingConverter();

	public static class DataBindingConverter extends Converter<DataBinding> {
		public DataBindingConverter() {
			super(DataBinding.class);
		}

		@Override
		public DataBinding convertFromString(String value) {
			return new DataBinding(value);
		}

		@Override
		public String convertToString(DataBinding value) {
			return value.toString();
		};
	}

	@Override
	public Converter<? extends DataBinding> getConverter() {
		return CONVERTER;
	}

	private FIBModelObject owner;
	private FIBModelAttribute bindingAttribute;
	private String unparsedBinding;
	private BindingDefinition bindingDefinition;
	private AbstractBinding binding;

	// private Exception newIntanceException;

	public DataBinding(FIBModelObject owner, FIBModelAttribute attribute, BindingDefinition df) {
		// newIntanceException = new Exception("Create instance with owner "+owner);
		setOwner(owner);
		setBindingAttribute(attribute);
		setBindingDefinition(df);
	}

	public DataBinding(String unparsed) {
		// newIntanceException = new Exception("Create instance with "+unparsed);
		unparsedBinding = unparsed;
	}

	public Object execute(BindingEvaluationContext context) {
		return getBindingValue(context);
	}

	public Object getBindingValue(BindingEvaluationContext context) {
		// logger.info("getBindingValue() "+this);
		if (getBinding() != null) {
			try {
				return getBinding().getBindingValue(context);
			} catch (TypeMismatchException e) {
				return null;
			} catch (NullReferenceException e) {
				return null;
			}
		}
		return null;
	}

	public void setBindingValue(Object value, BindingEvaluationContext context) {
		if (getBinding() != null && getBinding().isSettable()) {
			getBinding().setBindingValue(value, context);
		}
	}

	@Override
	public String toString() {
		if (binding != null) {
			return binding.getStringRepresentation();
		}
		return unparsedBinding;
	}

	public BindingDefinition getBindingDefinition() {
		return bindingDefinition;
	}

	public void setBindingDefinition(BindingDefinition bindingDefinition) {
		this.bindingDefinition = bindingDefinition;
	}

	public AbstractBinding getBinding() {
		if (binding == null) {
			finalizeDeserialization();
		}
		return binding;
	}

	public AbstractBinding getBinding(boolean silentMode) {
		if (binding == null) {
			finalizeDeserialization(silentMode);
		}
		return binding;
	}

	/*public void setBinding(AbstractBinding binding) 
	{
		this.binding = binding;
	}*/

	public void setBinding(AbstractBinding value) {
		AbstractBinding oldValue = this.binding;
		if (oldValue == null) {
			if (value == null) {
				return; // No change
			} else {
				this.binding = value;
				unparsedBinding = value != null ? value.getStringRepresentation() : null;
				updateDependancies();
				if (bindingAttribute != null) {
					owner.notify(new FIBAttributeNotification<AbstractBinding>(bindingAttribute, oldValue, value));
				}
				owner.notifyBindingChanged(this);
				return;
			}
		} else {
			if (oldValue.equals(value)) {
				return; // No change
			} else {
				this.binding = value;
				unparsedBinding = value != null ? value.getStringRepresentation() : null;
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Binding takes now value " + value);
				}
				updateDependancies();
				if (bindingAttribute != null) {
					owner.notify(new FIBAttributeNotification<AbstractBinding>(bindingAttribute, oldValue, value));
				}
				owner.notifyBindingChanged(this);
				return;
			}
		}
	}

	public boolean hasBinding() {
		return binding != null;
	}

	public boolean isValid() {
		return getBinding() != null && getBinding().isBindingValid();
	}

	public boolean isValid(boolean silentMode) {
		return getBinding(silentMode) != null && getBinding(silentMode).isBindingValid();
	}

	public boolean isSet() {
		return unparsedBinding != null || binding != null;
	}

	public boolean isUnset() {
		return unparsedBinding == null && binding == null;
	}

	public String getUnparsedBinding() {
		return unparsedBinding;
	}

	public void setUnparsedBinding(String unparsedBinding) {
		this.unparsedBinding = unparsedBinding;
		binding = null;
	}

	public Bindable getBindable() {
		return getOwner();
	}

	public BindingFactory getBindingFactory() {
		return getBindable().getBindingFactory();
	}

	public FIBModelObject getOwner() {
		return owner;
	}

	public void setOwner(FIBModelObject owner) {
		this.owner = owner;
	}

	protected void finalizeDeserialization(boolean silentMode) {
		if (getUnparsedBinding() == null) {
			return;
		}

		/*if (getUnparsedBinding().equals("data.isAcceptableAsSubProcess(SubProcess.component.candidateValue)")) {
			System.out.println("finalizeDeserialization for "+getUnparsedBinding());
			System.out.println("Owner: "+getOwner()+" of "+getOwner().getClass());
			System.out.println("BindingModel: "+getOwner().getBindingModel());
		}*/

		// System.out.println("BindingModel: "+getOwner().getBindingModel());
		if (getBindable() != null) {
			BindingFactory factory = getBindingFactory();
			binding = factory.convertFromString(getUnparsedBinding(), getBindable());
			binding.setBindingDefinition(getBindingDefinition());
			// System.out.println(">>>>>>>>>>>>>> Binding: "+binding.getStringRepresentation()+" owner="+binding.getOwner());
			// System.out.println("binding.isBindingValid()="+binding.isBindingValid());
		}

		if (binding == null) {
			logger.warning("Unexpected null binding for [" + getUnparsedBinding() + "]");
		}

		if (binding != null && !binding.isBindingValid()) {
			if (!silentMode) {
				logger.warning("Binding not valid: " + binding + " for owner " + getOwner() + " context="
						+ (getOwner() != null ? getOwner().getRootComponent() : null) + "reason=" + binding.invalidBindingReason());
				// binding.debugIsBindingValid();
				// Dev note: Uncomment following to get more informations
				// logger.warning("Binding not valid: " + binding + " for owner " + getOwner() + " context="
				// + (getOwner() != null ? (getOwner()).getRootComponent() : null));
				// logger.info("BindingModel=" + getOwner().getBindingModel());
				// logger.info("BindingFactory=" + getOwner().getBindingFactory());
				// binding.debugIsBindingValid();
				// BindingExpression.logger.setLevel(Level.FINE);
				// binding = AbstractBinding.abstractBindingConverter.convertFromString(getUnparsedBinding());
				// binding.setBindingDefinition(getBindingDefinition());
				// binding.isBindingValid();
				// (new Exception("prout")).printStackTrace();
				// System.exit(-1);
			}
		}

		updateDependancies();
	}

	protected void finalizeDeserialization() {
		finalizeDeserialization(true);
		if (owner != null && hasBinding() && isValid()) {
			owner.notifyBindingChanged(this);
		}
	}

	protected void updateDependancies() {
		if (getOwner() instanceof FIBComponent) {

			if (binding == null) {
				return;
			}

			Vector<Expression> primitives;
			try {
				primitives = Expression.extractPrimitives(binding.getStringRepresentation());

				FIBComponent component = (FIBComponent) getOwner();
				FIBComponent rootComponent = component.getRootComponent();
				Iterator<FIBComponent> subComponents = rootComponent.subComponentIterator();
				while (subComponents.hasNext()) {
					FIBComponent next = subComponents.next();
					if (next != getOwner()) {
						if (next instanceof FIBWidget && ((FIBWidget) next).getData() != null && ((FIBWidget) next).getData().isSet()) {
							String data = ((FIBWidget) next).getData().toString();
							if (data != null) {
								for (Expression p : primitives) {
									String primitiveValue = null;
									if (p instanceof Variable) {
										primitiveValue = ((Variable) p).getName();
									}
									if (p instanceof Function) {
										primitiveValue = ((Function) p).getName();
									}
									if (primitiveValue != null && primitiveValue.startsWith(data)) {
										// try {
										component.declareDependantOf(next);
										/*} catch (DependancyLoopException e) {
											logger.warning("DependancyLoopException raised while declaring dependancy (data lookup)"
													+ "in the context of binding: " + binding.getStringRepresentation() + " primitive: "
													+ primitiveValue + " component: " + component + " dependency: " + next + " data: "
													+ data + " message: " + e.getMessage());
										}*/
									}
								}

							}
						}
						if (next.getName() != null) {
							for (Expression p : primitives) {
								String primitiveValue = null;
								if (p instanceof Variable) {
									primitiveValue = ((Variable) p).getName();
								}
								if (p instanceof Function) {
									primitiveValue = ((Function) p).getName();
								}
								if (primitiveValue != null && StringUtils.isNotEmpty(next.getName())
										&& primitiveValue.startsWith(next.getName())) {
									// try {
									component.declareDependantOf(next);
									/*} catch (DependancyLoopException e) {
										logger.warning("DependancyLoopException raised while declaring dependancy (name lookup)"
												+ "in the context of binding: " + binding.getStringRepresentation() + " primitive: "
												+ primitiveValue + " component: " + component + " dependency: " + next + " message: "
												+ e.getMessage());
									}*/
								}
							}
						}
					}
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public FIBModelAttribute getBindingAttribute() {
		return bindingAttribute;
	}

	public void setBindingAttribute(FIBModelAttribute bindingAttribute) {
		this.bindingAttribute = bindingAttribute;
	}

}
