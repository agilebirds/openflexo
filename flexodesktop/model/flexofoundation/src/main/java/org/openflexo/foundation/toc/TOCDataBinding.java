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
package org.openflexo.foundation.toc;

import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class TOCDataBinding implements StringConvertable<TOCDataBinding> {

	private static final Logger logger = Logger.getLogger(TOCDataBinding.class.getPackage().getName());

	public static TOCDataBinding.DataBindingConverter CONVERTER = new DataBindingConverter();

	public static class DataBindingConverter extends Converter<TOCDataBinding> {
		public DataBindingConverter() {
			super(TOCDataBinding.class);
		}

		@Override
		public TOCDataBinding convertFromString(String value) {
			return new TOCDataBinding(value);
		}

		@Override
		public String convertToString(TOCDataBinding value) {
			return value.toString();
		};
	}

	@Override
	public Converter<? extends TOCDataBinding> getConverter() {
		return CONVERTER;
	}

	private Bindable owner;
	private TOCBindingAttribute bindingAttribute;
	private String unparsedBinding;
	private BindingDefinition bindingDefinition;
	private AbstractBinding binding;

	public TOCDataBinding(Bindable owner, TOCBindingAttribute attribute, BindingDefinition df) {
		setOwner(owner);
		setBindingAttribute(attribute);
		setBindingDefinition(df);
	}

	public TOCDataBinding(String unparsed) {
		unparsedBinding = unparsed;
	}

	public Object getBindingValue(BindingEvaluationContext context) {
		// logger.info("getBindingValue() "+this);
		if (getBinding() != null) {
			return getBinding().getBindingValue(context);
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

	/*public void setBinding(AbstractBinding binding) 
	{
		this.binding = binding;
	}*/

	public void setBinding(AbstractBinding value) {
		System.out.println("TOCDataBinding: Set binding with " + value);
		AbstractBinding oldValue = this.binding;
		if (oldValue == null) {
			if (value == null) {
				return; // No change
			} else {
				this.binding = value;
				unparsedBinding = value != null ? value.getStringRepresentation() : null;
				updateDependancies();
				if (bindingAttribute != null && owner instanceof TOCEntry) {
					((TOCEntry) owner).notifyChange(bindingAttribute, oldValue, value);
				}
				if (owner instanceof TOCEntry) {
					((TOCEntry) owner).notifyBindingChanged(this);
				}
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
				if (bindingAttribute != null && owner instanceof TOCEntry) {
					((TOCEntry) owner).notifyChange(bindingAttribute, oldValue, value);
				}
				if (owner instanceof TOCEntry) {
					((TOCEntry) owner).notifyBindingChanged(this);
				}
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

	protected void finalizeDeserialization() {
		if (getUnparsedBinding() == null) {
			return;
		}

		// System.out.println("BindingModel: "+getOwner().getBindingModel());
		if (getOwner() != null) {
			BindingFactory factory = getOwner().getBindingFactory();
			binding = factory.convertFromString(getUnparsedBinding(), getOwner());
			binding.setBindingDefinition(getBindingDefinition());
			// System.out.println(">>>>>>>>>>>>>> Binding: "+binding.getStringRepresentation()+" owner="+binding.getOwner());
			// System.out.println("binding.isBindingValid()="+binding.isBindingValid());
		}

		if (binding != null && !binding.isBindingValid()) {
			logger.warning("Binding not valid: " + binding + " for owner " + getOwner() + " context=" + getOwner());
			/*logger.info("BindingModel="+getOwner().getBindingModel());
			BindingExpression.logger.setLevel(Level.FINE);
			binding = factory.convertFromString(getUnparsedBinding());
			binding.setBindingDefinition(getBindingDefinition());
			binding.isBindingValid();*/
		}

		updateDependancies();
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

	public TOCBindingAttribute getBindingAttribute() {
		return bindingAttribute;
	}

	public void setBindingAttribute(TOCBindingAttribute bindingAttribute) {
		this.bindingAttribute = bindingAttribute;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TOCDataBinding) {
			if (toString() == null) {
				return false;
			}
			return toString().equals(obj.toString());
		} else {
			return super.equals(obj);
		}
	}

	public static interface TOCBindingAttribute {

	}

}