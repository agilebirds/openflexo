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

import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class ViewPointDataBinding implements StringConvertable<ViewPointDataBinding> {

	private static final Logger logger = Logger.getLogger(ViewPointDataBinding.class.getPackage().getName());

	public static ViewPointDataBinding.DataBindingConverter CONVERTER = new DataBindingConverter();

	public static class DataBindingConverter extends Converter<ViewPointDataBinding> {
		public DataBindingConverter() {
			super(ViewPointDataBinding.class);
		}

		@Override
		public ViewPointDataBinding convertFromString(String value) {
			return new ViewPointDataBinding(value);
		}

		@Override
		public String convertToString(ViewPointDataBinding value) {
			return value.toString();
		};
	}

	@Override
	public Converter<? extends ViewPointDataBinding> getConverter() {
		return CONVERTER;
	}

	private ViewPointObject owner;
	private InspectorBindingAttribute bindingAttribute;
	private String unparsedBinding;
	private BindingDefinition bindingDefinition;
	private AbstractBinding binding;

	public ViewPointDataBinding(ViewPointObject owner, InspectorBindingAttribute attribute, BindingDefinition df) {
		setOwner(owner);
		setBindingAttribute(attribute);
		setBindingDefinition(df);
		/*if (df == null) {
			System.out.println("Binding " + this + " has been created with NULL binding definition !!!");
		}*/
	}

	public ViewPointDataBinding(String unparsed) {
		unparsedBinding = unparsed;
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
				if (bindingAttribute != null) {
					owner.notifyChange(bindingAttribute, oldValue, value);
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
				logger.info("Binding takes now value " + value);
				updateDependancies();
				if (bindingAttribute != null) {
					owner.notifyChange(bindingAttribute, oldValue, value);
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

	public ViewPointObject getOwner() {
		return owner;
	}

	public void setOwner(ViewPointObject owner) {
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
				binding = factory.convertFromString(getUnparsedBinding(), getOwner());
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

	public InspectorBindingAttribute getBindingAttribute() {
		return bindingAttribute;
	}

	public void setBindingAttribute(InspectorBindingAttribute bindingAttribute) {
		this.bindingAttribute = bindingAttribute;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ViewPointDataBinding) {
			if (toString() == null) {
				return false;
			}
			return toString().equals(obj.toString());
		} else {
			return super.equals(obj);
		}
	}
}
