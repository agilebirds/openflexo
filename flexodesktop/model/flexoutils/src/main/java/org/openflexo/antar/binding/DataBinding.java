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
package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Representation of a data binding.<br>
 * A data binding is defined as an symbolic expression defined on a model abstraction (the binding model), which can be evaluated given a
 * {@link BindingEvaluationContext}.
 * 
 * @author sylvain
 * 
 * @param <T>
 */
public class DataBinding<T> implements StringConvertable<DataBinding> {

	private static final Logger logger = Logger.getLogger(DataBinding.class.getPackage().getName());

	public static DataBinding.DataBindingConverter CONVERTER = new DataBindingConverter();

	public static class DataBindingConverter extends Converter<DataBinding> {
		public DataBindingConverter() {
			super(DataBinding.class);
		}

		@Override
		public DataBinding<?> convertFromString(String value) {
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

	private Bindable owner;
	private BindingAttribute bindingAttribute;
	private String unparsedBinding;
	private BindingDefinition bindingDefinition;
	private AbstractBinding binding;

	public DataBinding(Bindable owner, Type type, BindingDefinitionType bdType) {
		super();
		setOwner(owner);
		setBindingDefinition(new BindingDefinition("unnamed", type, bdType, true));
	}

	public DataBinding(Bindable owner, BindingAttribute attribute, Type type, BindingDefinitionType bdType) {
		setOwner(owner);
		setBindingAttribute(attribute);
		setBindingDefinition(new BindingDefinition(attribute.name(), type, bdType, true));
	}

	public DataBinding(String unparsed, Bindable owner, Type type, BindingDefinitionType bdType) {
		this(owner, type, bdType);
		setUnparsedBinding(unparsed);
	}

	public DataBinding(String unparsed, Bindable owner, BindingAttribute attribute, Type type, BindingDefinitionType bdType) {
		this(owner, attribute, type, bdType);
		setUnparsedBinding(unparsed);
	}

	public DataBinding(String unparsed) {
		super();
		setUnparsedBinding(unparsed);
	}

	public T getBindingValue(BindingEvaluationContext context) {
		// logger.info("getBindingValue() "+this);
		if (getBinding() != null) {
			try {
				return (T) getBinding().getBindingValue(context);
			} catch (TypeMismatchException e) {
				return null;
			} catch (NullReferenceException e) {
				return null;
			}
		}
		return null;
	}

	public void setBindingValue(T value, BindingEvaluationContext context) {
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
		/*if (bindingDefinition == null) {
			System.out.println("Binding " + this + " has been set with NULL binding definition !!!");
		}*/
	}

	public AbstractBinding getBinding() {
		if (binding == null && !isDeserializing) {
			finalizeDeserialization();
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
				notifyBindingChanged(oldValue, value);
				return;
			}
		} else {
			if (oldValue.equals(value)) {
				return; // No change
			} else {
				this.binding = value;
				unparsedBinding = value != null ? value.getStringRepresentation() : null;
				logger.info("Binding takes now value " + value);
				updateDependancies();
				notifyBindingChanged(oldValue, value);
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
	}

	public Bindable getOwner() {
		return owner;
	}

	public void setOwner(Bindable owner) {
		this.owner = owner;
	}

	private boolean isDeserializing = false;

	protected void finalizeDeserialization() {
		if (getUnparsedBinding() == null) {
			return;
		}

		if (isDeserializing) {
			return;
		}

		isDeserializing = true;

		// System.out.println("BindingModel: "+getOwner().getBindingModel());
		if (getOwner() != null) {
			BindingFactory factory = getOwner().getBindingFactory();
			if (factory != null) {
				factory.setBindable(getOwner());
				binding = factory.convertFromString(getUnparsedBinding());
				binding.setBindingDefinition(getBindingDefinition());
				// System.out.println(">>>>>>>>>>>>>> Binding: "+binding.getStringRepresentation()+" owner="+binding.getOwner());
				// System.out.println("binding.isBindingValid()="+binding.isBindingValid());
			}
		}

		if (binding != null && !binding.isBindingValid()) {
			logger.warning("Binding not valid: " + binding + " for owner " + getOwner() + " context=" + getOwner());
			/*logger.info("BindingModel=" + getOwner().getBindingModel());
			BindingFactory factory = getOwner().getBindingFactory();
			logger.info("BindingFactory=" + factory);
			logger.info("Reason: " + binding.debugIsBindingValid());*/
		}

		updateDependancies();

		isDeserializing = false;
	}

	protected void updateDependancies() {
		if (binding == null) {
			return;
		}

		// logger.info("Searching dependancies for "+this);

		/*InspectorEntry component = getOwner();
		List<TargetObject> targetList = binding.getTargetObjects(owner);
		if (targetList != null) {
			for (TargetObject o : targetList) {
				//System.out.println("> "+o.target+" for "+o.propertyName);
				if (o.target instanceof InspectorEntry) {
					InspectorEntry c = (InspectorEntry)o.target;
					InspectorBindingAttribute param = InspectorBindingAttribute.valueOf(o.propertyName);
					logger.info("OK, found "+getBindingAttribute()+" of "+getOwner()+" depends of "+param+" , "+c);
					try {
						component.declareDependantOf(c,getBindingAttribute(),param);
					} catch (DependancyLoopException e) {
						logger.warning("DependancyLoopException raised while declaring dependancy (data lookup)"
								+"in the context of binding: "+binding.getStringRepresentation()
								+" component: "+component
								+" dependancy: "+c
								+" message: "+e.getMessage());
					}
				}
			}
		}*/

		// Vector<Expression> primitives;
		// try {

		/*primitives = Expression.extractPrimitives(binding.getStringRepresentation());

			GraphicalRepresentation component = getOwner();
			GraphicalRepresentation rootComponent = component.getRootGraphicalRepresentation();
			
			for (Expression p : primitives) {
				if (p instanceof Variable) {
					String fullVariable = ((Variable)p).getName(); 
					if (fullVariable.indexOf(".") > 0) {
						String identifier = fullVariable.substring(0,fullVariable.indexOf("."));
						String parameter = fullVariable.substring(fullVariable.indexOf(".")+1);
						logger.info("identifier="+identifier);
						logger.info("parameter="+parameter);
						Iterator<GraphicalRepresentation> allComponents = rootComponent.allGRIterator();
						while (allComponents.hasNext()) {
							GraphicalRepresentation<?> next = allComponents.next();
							if (next != getOwner()) {
								if (identifier.equals(next.getIdentifier())) {
									for (GRParameter param : next.getAllParameters()) {
										if (param.name().equals(parameter)) {
											logger.info("OK, found "+getBindingAttribute()+" of "+getOwner()+" depends of "+param+" , "+next);
											try {
												component.declareDependantOf(next,getBindingAttribute(),param);
											} catch (DependancyLoopException e) {
												logger.warning("DependancyLoopException raised while declaring dependancy (data lookup)"
														+"in the context of binding: "+binding.getStringRepresentation()
														+" fullVariable: "+fullVariable
														+" component: "+component
														+" dependancy: "+next
														+" identifier: "+next.getIdentifier()
														+" message: "+e.getMessage());
											}
										}
									}
								}
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
		*/
	}

	public BindingAttribute getBindingAttribute() {
		return bindingAttribute;
	}

	public void setBindingAttribute(BindingAttribute bindingAttribute) {
		this.bindingAttribute = bindingAttribute;
	}

	private void notifyBindingChanged(AbstractBinding oldValue, AbstractBinding newValue) {
		// TODO
		logger.warning("Please implement me");
		/*if (bindingAttribute != null) {
			owner.notifyChange(bindingAttribute, oldValue, value);
		}
		owner.notifyBindingChanged(this);*/
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataBinding) {
			if (toString() == null) {
				return false;
			}
			return toString().equals(obj.toString());
		} else {
			return super.equals(obj);
		}
	}

	public static interface BindingAttribute {
		public String name();
	}
}