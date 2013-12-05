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
package org.openflexo.antar.expr;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.Function;
import org.openflexo.antar.binding.Function.FunctionArgument;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.SettableBindingEvaluationContext;
import org.openflexo.antar.binding.SettableBindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TargetObject;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;

/**
 * Represents a binding path, as formed by an access to a binding variable and a path of BindingPathElement<br>
 * A BindingValue may be settable is the last BindingPathElement is itself settable
 * 
 * @author sylvain
 * 
 */
public class BindingValue extends Expression implements PropertyChangeListener, Cloneable {

	private static final Logger logger = Logger.getLogger(BindingValue.class.getPackage().getName());

	private final ArrayList<Object> EMPTY_LIST = new ArrayList<Object>();

	public static abstract class AbstractBindingPathElement {
	}

	public static class NormalBindingPathElement extends AbstractBindingPathElement {
		public String property;

		public NormalBindingPathElement(String aProperty) {
			property = aProperty;
		}

		@Override
		public String toString() {
			return "Normal[" + property + "]";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NormalBindingPathElement) {
				NormalBindingPathElement e = (NormalBindingPathElement) obj;
				return property.equals(e.property);
			}
			return super.equals(obj);
		}
	}

	public static class MethodCallBindingPathElement extends AbstractBindingPathElement {
		public String method;
		public List<Expression> args;

		public MethodCallBindingPathElement(String aMethod, List<Expression> someArgs) {
			method = aMethod;
			args = someArgs;
		}

		@Override
		public String toString() {
			return "Call[" + method + "(" + args + ")" + "]";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MethodCallBindingPathElement) {
				MethodCallBindingPathElement e = (MethodCallBindingPathElement) obj;
				return method.equals(e.method) && args.equals(e.args);
			}
			return super.equals(obj);
		}

	}

	private List<AbstractBindingPathElement> parsedBindingPath;

	private BindingVariable bindingVariable;
	private ArrayList<BindingPathElement> bindingPath;

	private boolean needsAnalysing = true;
	private boolean analysingSuccessfull = true;
	private BindingModel analyzedWithBindingModel = null;

	private DataBinding<?> dataBinding;

	private String invalidBindingReason = "not analyzed yet";

	public BindingValue() {
		this(new ArrayList<AbstractBindingPathElement>());
	}

	public BindingValue(List<AbstractBindingPathElement> aBindingPath) {
		super();
		this.parsedBindingPath = aBindingPath;
		bindingVariable = null;
		bindingPath = new ArrayList<BindingPathElement>();
		needsAnalysing = true;
		analysingSuccessfull = true;
	}

	public BindingValue(String stringToParse) throws ParseException {
		this(parse(stringToParse));
	}

	@Override
	public BindingValue clone() {
		try {
			return (BindingValue) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<AbstractBindingPathElement> parse(String stringToParse) throws ParseException {
		Expression e = ExpressionParser.parse(stringToParse);
		if (e instanceof BindingValue) {
			return ((BindingValue) e).getParsedBindingPath();
		} else {
			throw new ParseException("Not parseable as a BindingValue: " + stringToParse);
		}
	}

	public DataBinding<?> getDataBinding() {
		return dataBinding;
	}

	public void setDataBinding(DataBinding<?> dataBinding) {
		this.dataBinding = dataBinding;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	public List<BindingPathElement> getBindingPath() {
		return bindingPath;
	}

	/**
	 * @param element
	 * @param i
	 */
	public Type addBindingPathElement(BindingPathElement element) {
		int index = bindingPath.size();
		setBindingPathElementAtIndex(element, index);
		analysingSuccessfull = _checkBindingPathValid();
		return element.getType();
	}

	/**
	 * @param element
	 * @param i
	 */
	public void setBindingPathElementAtIndex(BindingPathElement element, int i) {

		// logger.info("setBindingPathElementAtIndex " + element + " index=" + i);

		if (i < bindingPath.size() && bindingPath.get(i) == element) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Set property " + element + " at index " + i);
		}
		if (i < bindingPath.size()) {
			bindingPath.set(i, element);
			int size = bindingPath.size();
			for (int j = i + 1; j < size; j++) {
				bindingPath.remove(i + 1);
			}
		} else if (i == bindingPath.size()) {
			bindingPath.add(element);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not set property at index " + i);
			}
		}
		analysingSuccessfull = _checkBindingPathValid();
		parsedBindingPath.clear();
	}

	public BindingPathElement getBindingPathElementAtIndex(int i) {
		if (i < bindingPath.size()) {
			return bindingPath.get(i);
		}
		return null;
	}

	public int getBindingPathElementCount() {
		return bindingPath.size();
	}

	public void removeBindingPathElementAfter(BindingPathElement requestedLast) {
		if (bindingPath != null && bindingPath.get(bindingPath.size() - 1) != null
				&& bindingPath.get(bindingPath.size() - 1).equals(requestedLast)) {
			return;
		} else if (bindingPath != null && bindingPath.get(bindingPath.size() - 1) != null) {
			parsedBindingPath.clear();
			bindingPath.remove(bindingPath.size() - 1);
			removeBindingPathElementAfter(requestedLast);
		}
		analysingSuccessfull = _checkBindingPathValid();
	}

	public void removeBindingPathAt(int index) {
		bindingPath.remove(index);
		analysingSuccessfull = _checkBindingPathValid();
	}

	/**
	 * Return the last binding path element, which is the binding variable itself if the binding path is empty, or the last binding path
	 * element registered in the binding path
	 * 
	 * @return
	 */
	public BindingPathElement getLastBindingPathElement() {
		if (getBindingPath() != null && getBindingPath().size() > 0) {
			return getBindingPath().get(getBindingPath().size() - 1);
		}
		return getBindingVariable();
	}

	/**
	 * Return boolean indicating if supplied element is equals to the last binding path element
	 * 
	 * @param element
	 * @return
	 */
	public boolean isLastBindingPathElement(BindingPathElement element) {

		if (bindingPath.size() == 0) {
			return element.equals(getBindingVariable());
		}

		return bindingPath.get(bindingPath.size() - 1).equals(element);
	}

	/*public boolean isLastBindingPathElement(BindingPathElement element, int index) {

		System.out.println("est ce que " + element + " est bien le dernier et a l'index " + index);

		if (index == 0) {
			return (element.equals(getBindingVariable()));
		}

		if (bindingPath.size() < 1) {
			return false;
		}

		System.out.println("Reponse: " + (bindingPath.get(bindingPath.size() - 1).equals(element) && index == bindingPath.size()));

		return bindingPath.get(bindingPath.size() - 1).equals(element) && index == bindingPath.size();
	}*/

	public BindingVariable getBindingVariable() {
		return bindingVariable;
	}

	public void setBindingVariable(BindingVariable bindingVariable) {
		internallySetBindingVariable(bindingVariable);
		bindingPath.clear();
		parsedBindingPath.clear();
		analysingSuccessfull = _checkBindingPathValid();
	}

	private void internallySetBindingVariable(BindingVariable aBindingVariable) {
		if (bindingVariable != aBindingVariable) {
			// logger.info("Ici pour " + this + " with " + aBindingVariable + " (was " + bindingVariable + ")");
			// if (false) {
			// logger.info("Ici ");
			// Thread.dumpStack();
			// }
			if (bindingVariable != null) {
				bindingVariable.getPropertyChangeSupport().removePropertyChangeListener(BindingVariable.TYPE, this);
				bindingVariable.getPropertyChangeSupport().removePropertyChangeListener(BindingVariable.VARIABLE_NAME, this);
			}
			bindingVariable = aBindingVariable;
			if (bindingVariable != null) {
				bindingVariable.getPropertyChangeSupport().addPropertyChangeListener(BindingVariable.TYPE, this);
				bindingVariable.getPropertyChangeSupport().addPropertyChangeListener(BindingVariable.VARIABLE_NAME, this);
			}
			// System.out.println("Pour " + bindingVariable + " j'ai comme listeners: "
			// + bindingVariable.getPropertyChangeSupport().getPropertyChangeListeners(BindingVariable.VARIABLE_NAME).length);
			// if (bindingVariable.getPropertyChangeSupport().getPropertyChangeListeners(BindingVariable.VARIABLE_NAME).length > 1000) {
			// System.out.println("OK, on arrete la");
			// PropertyChangeListener[] all = bindingVariable.getPropertyChangeSupport().getPropertyChangeListeners(
			// BindingVariable.VARIABLE_NAME);
			// System.out.println("Hop");
			// }
			// }
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// if (false) {
		// logger.info("Et la avec " + evt);
		// Thread.dumpStack();
		// }
		if (evt.getPropertyName().equals(BindingVariable.VARIABLE_NAME) || evt.getPropertyName().equals(BindingVariable.TYPE)) {
			markedAsToBeReanalized();
		}
	}

	public Type getAccessedType() {
		if (isValid() && getLastBindingPathElement() != null) {
			return getLastBindingPathElement().getType();
		}
		return null;
	}

	public Type getAccessedTypeNoValidityCheck() {
		if (getLastBindingPathElement() != null) {
			return getLastBindingPathElement().getType();
		}
		return null;
	}

	public List<AbstractBindingPathElement> getParsedBindingPath() {
		return parsedBindingPath;
	}

	public boolean containsAMethodCall() {
		for (AbstractBindingPathElement e : getParsedBindingPath()) {
			if (e instanceof MethodCallBindingPathElement) {
				return true;
			}
		}
		return false;
	}

	public String getVariableName() {
		return getBindingVariable().getVariableName();
	}

	public boolean isSimpleVariable() {
		return getParsedBindingPath().size() == 1 && getParsedBindingPath().get(0) instanceof NormalBindingPathElement;
	}

	public boolean isCompoundBinding() {
		for (BindingPathElement e : getBindingPath()) {
			if (e instanceof FunctionPathElement) {
				return true;
			}
		}
		return false;
	}

	/*@Override
	public Expression evaluate(EvaluationContext context) throws TypeMismatchException {
		if (containsAMethodCall()) {
			ArrayList<AbstractBindingPathElement> newBindingPath = new ArrayList<AbstractBindingPathElement>();
			for (AbstractBindingPathElement e : getBindingPath()) {
				if (e instanceof NormalBindingPathElement) {
					newBindingPath.add(new NormalBindingPathElement(((NormalBindingPathElement) e).property));
				} else if (e instanceof MethodCallBindingPathElement) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					for (Expression arg : ((MethodCallBindingPathElement) e).args) {
						newArgs.add(arg.evaluate(context));
					}
					newBindingPath.add(new MethodCallBindingPathElement(((MethodCallBindingPathElement) e).method, newArgs));
				}
			}
			return new BindingValueAsExpression(newBindingPath);
		}
		return this;
	}*/

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {

		boolean hasBeenTransformed = false;
		ArrayList<AbstractBindingPathElement> newBindingPath = new ArrayList<AbstractBindingPathElement>();
		for (AbstractBindingPathElement e : getParsedBindingPath()) {
			if (e instanceof NormalBindingPathElement) {
				newBindingPath.add(new NormalBindingPathElement(((NormalBindingPathElement) e).property));
			} else if (e instanceof MethodCallBindingPathElement) {
				ArrayList<Expression> newArgs = new ArrayList<Expression>();
				for (Expression arg : ((MethodCallBindingPathElement) e).args) {
					Expression transformedExpression = arg.transform(transformer);
					newArgs.add(transformedExpression);
					if (!transformedExpression.equals(arg)) {
						hasBeenTransformed = true;
					}
				}
				newBindingPath.add(new MethodCallBindingPathElement(((MethodCallBindingPathElement) e).method, newArgs));
			}
		}
		BindingValue bv = null;
		if (hasBeenTransformed) {
			bv = new BindingValue(newBindingPath);
			bv.setDataBinding(getDataBinding());
		} else {
			// bv = clone();
			// Sylvain
			// This might be a little bit tricky
			// If some problems happen, rollback to bv = clone() implementation
			bv = this;
		}
		return transformer.performTransformation(bv);
	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		visitor.visit(this);
	}

	@Override
	public EvaluationType getEvaluationType() {
		return TypeUtils.kindOfType(getAccessedType());
	}

	@Override
	protected Vector<Expression> getChilds() {
		return null;
	}

	public boolean isSettable() {
		if (getLastBindingPathElement() instanceof SettableBindingPathElement) {
			return ((SettableBindingPathElement) getLastBindingPathElement()).isSettable();
		}
		return false;
	}

	public boolean isValid() {
		return isValid(getDataBinding());
	}

	public void markedAsToBeReanalized() {
		// needsToBeReanalized = true;
		needsAnalysing = true;
		dataBinding.markedAsToBeReanalized();
	}

	public boolean isValid(DataBinding<?> dataBinding) {

		setDataBinding(dataBinding);
		if (dataBinding == null) {
			invalidBindingReason = "binding value has no referenced data binding";
			return false;
		}

		if (dataBinding.getOwner() == null) {
			invalidBindingReason = "binding value referenced data binding has no owner";
			return false;
		}

		if (dataBinding.getOwner().getBindingModel() == null) {
			invalidBindingReason = "binding value referenced data binding owner has no binding model";
			return false;
		}

		if (dataBinding.getOwner().getBindingFactory() == null) {
			invalidBindingReason = "binding value referenced data binding owner has no binding factory";
			return false;
		}

		if (dataBinding.getOwner().getBindingModel() != analyzedWithBindingModel) {
			needsAnalysing = true;
		}

		if (needsAnalysing) {
			buildBindingPathFromParsedBindingPath(dataBinding);
		}

		if (!analysingSuccessfull && !needsAnalysing) {
			// if (!analysingSuccessfull && !needsToBeReanalized) {
			return false;
		}

		needsAnalysing = false;
		// needsToBeReanalized = false;

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Is BindingValue valid ?");
		}

		if (getBindingVariable() == null) {
			invalidBindingReason = "binding value has no binding variable";
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because _bindingVariable is null");
			}
			return false;
		}
		if (!_checkBindingPathValid()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because binding path not valid");
			}
			return false;
		}

		/*if (!TypeUtils.isTypeAssignableFrom(dataBinding.getDeclaredType(), getLastBindingPathElement().getType())) {
			invalidBindingReason = "wrong type: type " + dataBinding.getDeclaredType() + " is not assignable from "
					+ getLastBindingPathElement().getType();
			return false;
		}*/

		return true;
	}

	public String invalidBindingReason() {
		return invalidBindingReason;
	}

	private boolean _checkBindingPathValid() {
		if (getBindingVariable() == null) {
			invalidBindingReason = "binding variable is null";
			return false;
		}
		Type currentType = getBindingVariable().getType();
		BindingPathElement currentElement = getBindingVariable();
		if (currentType == null) {
			invalidBindingReason = "currentType is null";
			return false;
		}

		for (int i = 0; i < bindingPath.size(); i++) {
			BindingPathElement element = bindingPath.get(i);
			if (element instanceof FunctionPathElement) {
				// We have to check that all arguments are valid
				FunctionPathElement functionPathElement = (FunctionPathElement) element;
				if (functionPathElement.getFunction() == null) {
					invalidBindingReason = "invalid function";
					return false;
				} else {
					// System.out.println("Checking for functionPathElement= " + functionPathElement);
					for (FunctionArgument arg : functionPathElement.getFunction().getArguments()) {
						DataBinding<?> argValue = functionPathElement.getParameter(arg);
						// System.out.println("Checking " + argValue + " valid=" + argValue.isValid());
						if (argValue == null) {
							invalidBindingReason = "Parameter value for function: " + functionPathElement.getFunction() + " : "
									+ "invalid null argument " + arg.getArgumentName();
							return false;
						}
						if (!argValue.isValid()) {
							invalidBindingReason = "Parameter value for function: " + functionPathElement.getFunction() + " : "
									+ "invalid argument " + arg.getArgumentName() + " reason=" + argValue.invalidBindingReason();
							return false;
						}
					}
				}
			}
			if (!TypeUtils.isTypeAssignableFrom(currentElement.getType(), element.getParent().getType(), true)) {
				invalidBindingReason = "Mismatched: " + currentElement.getType() + " and " + element.getParent().getType();
				return false;
			}
			currentElement = element;
			currentType = currentElement.getType();
		}

		/*if (!TypeUtils.isResolved(currentType)) {
			return false;
		}*/

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BindingValue) {
			BindingValue e = (BindingValue) obj;
			return dataBinding == e.dataBinding && getParsedBindingPath().equals(e.getParsedBindingPath());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Called to perform analysis of a parsed binding path in the particular context provided by the {@link DataBinding} this
	 * {@link BindingValue} is related to.
	 * 
	 * @param dataBinding
	 * @return
	 */
	public boolean buildBindingPathFromParsedBindingPath(DataBinding<?> dataBinding) {

		// logger.info("buildBindingPathFromParsedBindingPath() for " + getParsedBindingPath());

		if (dataBinding.getOwner() == null) {
			logger.warning("DataBinding has no owner");
			invalidBindingReason = "DataBinding has no owner";
			return false;
		}

		if (dataBinding.getOwner().getBindingModel() == null) {
			logger.warning("DataBinding owner has no binding model, owner=" + dataBinding.getOwner());
			invalidBindingReason = "DataBinding owner has no binding model, binding=" + dataBinding + " owner=" + dataBinding.getOwner();
			return false;
		}

		if (dataBinding.getOwner().getBindingFactory() == null) {
			logger.warning("DataBinding owner has no binding factory, owner=" + dataBinding.getOwner());
			invalidBindingReason = "DataBinding owner has no binding factory, binding=" + dataBinding + " owner=" + dataBinding.getOwner();
			return false;
		}

		needsAnalysing = false;
		analyzedWithBindingModel = dataBinding.getOwner().getBindingModel();
		setDataBinding(dataBinding);
		bindingVariable = null;
		bindingPath = new ArrayList<BindingPathElement>();
		if (getDataBinding() != null && getParsedBindingPath().size() > 0
				&& getParsedBindingPath().get(0) instanceof NormalBindingPathElement) {
			// Seems to be valid
			internallySetBindingVariable(dataBinding.getOwner().getBindingModel()
					.bindingVariableNamed(((NormalBindingPathElement) getParsedBindingPath().get(0)).property));
			BindingPathElement current = bindingVariable;
			// System.out.println("Found binding variable " + bindingVariable);
			if (bindingVariable == null) {
				invalidBindingReason = "cannot find binding variable "
						+ ((NormalBindingPathElement) getParsedBindingPath().get(0)).property;
				analysingSuccessfull = false;
				return false;
			}
			int i = 0;
			for (AbstractBindingPathElement pathElement : getParsedBindingPath()) {
				if (i > 0) {
					if (pathElement instanceof NormalBindingPathElement) {
						SimplePathElement newPathElement = dataBinding.getOwner().getBindingFactory()
								.makeSimplePathElement(current, ((NormalBindingPathElement) pathElement).property);
						if (newPathElement != null) {
							bindingPath.add(newPathElement);
							current = newPathElement;
							// System.out.println("> SIMPLE " + pathElement);
						} else {
							analysingSuccessfull = false;
							invalidBindingReason = "cannot find property " + ((NormalBindingPathElement) pathElement).property
									+ " for element " + current + " and type " + TypeUtils.simpleRepresentation(current.getType())
									+ " owner=" + dataBinding.getOwner() + " factory=" + dataBinding.getOwner().getBindingFactory();
							return false;
						}
					} else if (pathElement instanceof MethodCallBindingPathElement) {
						MethodCallBindingPathElement methodCall = (MethodCallBindingPathElement) pathElement;
						List<DataBinding<?>> args = new ArrayList<DataBinding<?>>();
						int argIndex = 0;
						for (Expression arg : methodCall.args) {
							DataBinding<?> argDataBinding = new DataBinding<Object>(dataBinding.getOwner(), Object.class,
									DataBinding.BindingDefinitionType.GET);
							argDataBinding.setBindingName("arg" + argIndex);
							argDataBinding.setExpression(arg);
							// argDataBinding.setDeclaredType(Object.class);
							// IMPORTANT/HACK: following statement (call to isValid()) is required to get access to analyzed type and
							// declares it
							// TODO: find a better solution
							argDataBinding.isValid();
							if (argDataBinding.getAnalyzedType() != null) {
								argDataBinding.setDeclaredType(argDataBinding.getAnalyzedType());
							}
							args.add(argDataBinding);
							argIndex++;
						}
						Function function = dataBinding.getOwner().getBindingFactory()
								.retrieveFunction(current.getType(), ((MethodCallBindingPathElement) pathElement).method, args);
						if (function != null) {
							FunctionPathElement newPathElement = dataBinding.getOwner().getBindingFactory()
									.makeFunctionPathElement(current, function, args);
							if (newPathElement != null) {
								bindingPath.add(newPathElement);
								current = newPathElement;
								// System.out.println("> FUNCTION " + pathElement);
							} else {
								invalidBindingReason = "cannot find method " + ((MethodCallBindingPathElement) pathElement).method
										+ " for type " + TypeUtils.simpleRepresentation(current.getType());
								analysingSuccessfull = false;
								return false;
							}
						} else {
							invalidBindingReason = "cannot find method " + ((MethodCallBindingPathElement) pathElement).method
									+ " for type " + TypeUtils.simpleRepresentation(current.getType());
							analysingSuccessfull = false;
							return false;
						}
					} else {
						logger.warning("Unexpected " + pathElement);
						invalidBindingReason = "unexpected path element: " + pathElement;
						analysingSuccessfull = false;
						return false;
					}
				}
				i++;
			}
			analysingSuccessfull = true;
		} else {
			logger.warning("Invalid binding value " + this);
			// Thread.dumpStack();
			analysingSuccessfull = false;
		}

		return analysingSuccessfull;
	}

	public Object getBindingValue(BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException,
			InvocationTargetTransformException {

		// System.out.println("  > evaluate BindingValue " + this + " in context " + context);
		if (isValid() && context != null) {
			Object current = context.getValue(getBindingVariable());
			if (current == null) {
				// If the binding variable is null, just return null
				return null;
			}
			BindingPathElement previous = getBindingVariable();
			for (BindingPathElement e : getBindingPath()) {
				if (current == null) {
					throw new NullReferenceException("NullReferenceException while evaluating " + this + ": null occured when evaluating "
							+ previous);
				}
				current = e.getBindingValue(current, context);
				previous = e;
			}
			// System.out.println("  > return "+current);
			return current;
		}
		return null;
	}

	public void setBindingValue(Object value, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException,
			InvocationTargetTransformException, NotSettableContextException {

		// logger.info("setBindingValue() for " + this + " with " + value + " context=" + context);
		// logger.info("valid=" + isValid());
		// logger.info("isSettable=" + isSettable());

		if (!isValid()) {
			return;
		}

		if (!isSettable()) {
			return;
		}

		if (getBindingPath().size() == 0 && getBindingVariable() != null) {
			// This is a simple assignation
			if (context instanceof SettableBindingEvaluationContext) {
				((SettableBindingEvaluationContext) context).setValue(value, getBindingVariable());
				return;
			} else {
				throw new NotSettableContextException(getBindingVariable(), context);
			}
		}

		// System.out.println("Sets value: "+value);
		// System.out.println("Binding: "+getStringRepresentation());

		BindingPathElement lastEvaluatedPathElement = getBindingVariable();
		Object lastEvaluated = null;
		Object returned = context.getValue(getBindingVariable());

		// System.out.println("For variable "+_bindingVariable+" object is "+returned);

		try {
			for (BindingPathElement element : getBindingPath()) {
				if (element != getLastBindingPathElement()) {
					// System.out.println("Apply "+element);
					lastEvaluatedPathElement = element;
					returned = element.getBindingValue(returned, context);
					if (returned == null) {
						throw new NullReferenceException("null occured when evaluating " + lastEvaluatedPathElement + " from "
								+ lastEvaluated);
					}
					lastEvaluated = returned;
					// System.out.println("Obtain "+returned);
				}
			}
			if (returned == null) {
				throw new NullReferenceException("null occured when evaluating " + lastEvaluatedPathElement + " from " + lastEvaluated);
			}

			// logger.info("returned="+returned);
			// logger.info("lastElement="+getBindingPath().lastElement());

			if (getLastBindingPathElement() instanceof SettableBindingPathElement
					&& ((SettableBindingPathElement) getLastBindingPathElement()).isSettable()) {
				// System.out.println("Et finalement on applique " + getLastBindingPathElement() + " sur " + returned);
				((SettableBindingPathElement) getLastBindingPathElement()).setBindingValue(value, returned, context);
			} else {
				logger.warning("Binding " + this + " is not settable");
			}
		} catch (InvalidObjectSpecificationException e) {
			logger.warning("InvalidObjectSpecificationException raised while evaluating SET " + this + " : " + e.getMessage());
			// System.out.println("returned="+returned);
			// System.out.println("value="+value);
			// System.out.println("((KeyValueProperty)getBindingPath().lastElement()).getName()="+((KeyValueProperty)getBindingPath().lastElement()).getName());
		}

	}

	/**
	 * Build and return a list of objects involved in the computation of this data binding with supplied binding evaluation context
	 * 
	 * @param context
	 * @return
	 */
	public List<Object> getConcernedObjects(BindingEvaluationContext context) {
		if (!isValid()) {
			return EMPTY_LIST;
		}
		if (!isSettable()) {
			return EMPTY_LIST;
		}

		List<Object> returned = new ArrayList<Object>();

		Object current = context.getValue(getBindingVariable());
		returned.add(current);

		for (BindingPathElement element : getBindingPath()) {
			if (element != getLastBindingPathElement()) {
				try {
					current = element.getBindingValue(current, context);
				} catch (TypeMismatchException e) {
					// silently escape
				} catch (NullReferenceException e) {
					// silently escape
				} catch (InvocationTargetTransformException e) {
					// silently escape
				}
				if (current == null) {
					return returned;
				} else {
					returned.add(current);
				}
			}
		}
		if (current == null) {
			return null;
		}

		return returned;
	}

	/**
	 * Build and return a list of target objects involved in the computation of this data binding with supplied binding evaluation context<br>
	 * Those target objects are the combination of an object and the property name involved by this denoted data binding
	 * 
	 * @param context
	 * @return
	 */
	public List<TargetObject> getTargetObjects(BindingEvaluationContext context) {
		if (!isValid()) {
			return null;
		}

		ArrayList<TargetObject> returned = new ArrayList<TargetObject>();

		Object current = context.getValue(getBindingVariable());

		returned.add(new TargetObject(context, getBindingVariable().getVariableName()));

		if (current == null) {
			return returned;
		}

		try {
			for (BindingPathElement element : getBindingPath()) {
				returned.add(new TargetObject(current, element.getLabel()));
				try {
					current = element.getBindingValue(current, context);
				} catch (TypeMismatchException e) {
					// We silently escape...
					// e.printStackTrace();
				} catch (NullReferenceException e) {
					// We silently escape...
					// e.printStackTrace();
				} catch (InvocationTargetTransformException e) {
					// We silently escape...
					// e.printStackTrace();
				}
				if (element instanceof FunctionPathElement) {
					FunctionPathElement functionPathElement = (FunctionPathElement) element;
					for (FunctionArgument arg : functionPathElement.getArguments()) {
						DataBinding<?> value = functionPathElement.getParameter(arg);
						returned.addAll(value.getTargetObjects(context));
					}
				}
				if (current == null) {
					return returned;
				}
			}
		} catch (InvalidObjectSpecificationException e) {
			logger.info("While computing getTargetObjects() for " + this + " with evaluation context=" + context);
			logger.warning(e.getMessage());
			return returned;
		}

		return returned;
	}

	public void debug() {
		System.out.println("parsedBindingPath=" + parsedBindingPath);
		System.out.println("bvar=" + bindingVariable);
		System.out.println("bpath=" + bindingPath);
		System.out.println("needsAnalysing=" + needsAnalysing);
		System.out.println("analysingSuccessfull=" + analysingSuccessfull);
	}
}
