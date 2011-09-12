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
package org.openflexo.inspector.widget;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.KVUtil;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.letparser.BooleanValue;
import org.openflexo.letparser.DoubleValue;
import org.openflexo.letparser.Expression;
import org.openflexo.letparser.IntValue;
import org.openflexo.letparser.Operator;
import org.openflexo.letparser.ParseException;
import org.openflexo.letparser.Parser;
import org.openflexo.letparser.StringValue;
import org.openflexo.letparser.Token;
import org.openflexo.letparser.Value;
import org.openflexo.letparser.Word;
import org.openflexo.xmlcode.StringConvertable;


// TODO: use AnTAR
public class WidgetConditional {

    static final Logger logger = Logger.getLogger(WidgetConditional.class.getPackage().getName());

    protected PropertyModel _propertyModel;
    private String _depends;
    private Expression _conditional;
    private Vector<String> _dependsProperties;

    public WidgetConditional(PropertyModel propertyModel) 
    {
        _dependsProperties = new Vector<String>();
        _propertyModel = propertyModel;
        _depends = propertyModel.depends;
        if (_depends != null) {
            StringTokenizer st = new StringTokenizer(_depends,",");
            while (st.hasMoreTokens()) {
                _dependsProperties.add(st.nextToken());
            }
        }
        if (propertyModel.conditional != null) {
            try {
            	//logger.info("Parse: "+propertyModel.conditional);
                _conditional = Parser.parseExpression(propertyModel.conditional);
                if (logger.isLoggable(Level.FINE))
                    logger.fine( ("Conditional: "+_conditional));
            } catch (ParseException e) {
                logger.warning ("Invalid conditional for property "+propertyModel.name+": "+e.getMessage());
            }
        }
    }
    
    /*public boolean checkConditionalValue(InspectableObject inspectable)
    {
         if (_conditional != null && !_conditional.equals("")) {
            try {
                StringTokenizer or = new StringTokenizer(_conditional, "|");
                while (or.hasMoreTokens()) {
                    // OR
                    StringTokenizer or_and = new StringTokenizer(or.nextToken().trim(), "&");
                    boolean and = true;
                    while (or_and.hasMoreTokens() && and) {
                        // AND
                        String cond = or_and.nextToken().trim();
                        boolean negate = false;
                        if (cond.indexOf("!=") != -1) {
                            negate = true;
                        }
                        StringTokenizer stk = new StringTokenizer(cond, "!=");
                        String attribute = stk.nextToken().trim();
                        String value = stk.nextToken().trim();
                        Object attributeValue = inspectable.objectForKey(attribute);
                        if (attributeValue instanceof KeyValueCoding) {
                            if (_propertyModel.hasFormatter()) {
                                attributeValue = getStringRepresentation(attributeValue);
                            }
                        }
                        if (attributeValue == null && value.equals("null"))
                            and = true;
                        else if (attributeValue != null && attributeValue.equals(value))
                            and = true;
                        else
                            and = false;

                        if (negate)
                            and = !and;
                    }
                    if (and == true) {
                        return true;
                    }
                }
                // if none OR exists => return true.
                return false;
            } catch (Exception e) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Exception in DenaliWidget.checkConditionalValue (" + _conditional + ") for:" + _propertyModel.label + "");
                e.printStackTrace();
            }
        }
        return true;
    }*/

    public boolean dependsOfProperty (String propertyName)
    {
        for (Enumeration en=_dependsProperties.elements(); en.hasMoreElements();) {
            String nextProperty = (String)en.nextElement();
            if (nextProperty.equals(propertyName)) return true;
        }
        return false;
    }

     public boolean isVisible(InspectableObject inspectable)
    {
         if (_conditional == null) return true;
        return evaluate(_conditional,inspectable);
    }

    private boolean evaluate (Expression expression, InspectableObject object)
    {
        Vector<KeyValueInstance> parameters = new Vector<KeyValueInstance>();
        for (Enumeration en=_dependsProperties.elements(); en.hasMoreElements();) {
            String nextProperty = (String)en.nextElement();
            if (KVUtil.hasValueForKey(object,nextProperty)) {
            	// Current inspected object might not have this property, ignore it then
            	parameters.add(new KeyValueInstance(object,nextProperty));
            }
             //System.out.println("Add param for "+nextProperty);
         }
        boolean returned = evaluate(expression,parameters);
        if (logger.isLoggable(Level.FINE))
            logger.fine("Evaluate "+expression+" : returns "+returned);
        return returned;
   }

    private Value evaluateToken(Token token, Vector<KeyValueInstance> parameters)
    {
        if (token instanceof Expression) {
            return new BooleanValue(evaluate((Expression)token,parameters));
        }
        else if (token instanceof Word) {
            String replacedString = ((Word)token).getValue();
            
            if (replacedString.equals("null")) return new StringValue("null");
            
            /*for (Enumeration en = parameters.elements(); en.hasMoreElements();) {
                KeyValueInstance param = (KeyValueInstance)en.nextElement();
                String newValueStringRepresentation = param.getStringRepresentation();
                String keyPath = param.getKeyPath();
                //System.out.println("replace "+keyPath+" by "+newValueStringRepresentation);
                System.out.println("param._inspectable: "+param._inspectable);
                System.out.println("param._keyPath: "+param._keyPath);
                System.out.println("param._value: "+param._value);
                System.out.println("replacedString[BEFORE]: "+replacedString);
                System.out.println("newValueStringRepresentation: "+newValueStringRepresentation);
                replacedString = ToolBox.replaceStringByStringInString(keyPath, newValueStringRepresentation, replacedString);
                System.out.println("replacedString[AFTER]: "+replacedString);
             }*/
            
            Object targetValue = null;
            boolean targetHasBeenAnalysed = false;
            if (replacedString.indexOf(".") > -1) {
                String targetName = replacedString.substring(0, replacedString.indexOf("."));
                String keyPath = replacedString.substring(replacedString.indexOf(".")+1);
                KeyValueInstance kvInstance = null;
                for (KeyValueInstance aKVInstance : parameters) {
                   	if (aKVInstance.getKeyPath().equals(targetName)) kvInstance = aKVInstance;
                }
                if (kvInstance != null) {
                    targetValue = kvInstance.getValueForKey(keyPath);
                    targetHasBeenAnalysed = true;
                }
            }
           // else {
            if (targetValue == null) {
                KeyValueInstance kvInstance = null;
                for (KeyValueInstance aKVInstance : parameters) {
                    if (aKVInstance.getKeyPath().equals(replacedString)) kvInstance = aKVInstance;
                }
                if (kvInstance != null) {
                    targetValue = kvInstance.getValue();
                    targetHasBeenAnalysed = true;
                }
                else {
                    targetValue = replacedString;
                    targetHasBeenAnalysed = true;
                }
             }
            
            //System.out.println("replacedString="+replacedString);
            //System.out.println("targetValue="+targetValue+" of "+(targetValue != null ? targetValue.getClass().getSimpleName() : "null"));
            
            if (targetHasBeenAnalysed) {
                if (targetValue == null) {
                    return new StringValue("null");
                }
                else if (targetValue instanceof Boolean) {
                    return new BooleanValue((Boolean)targetValue);
                }
                else if (targetValue instanceof Integer) {
                    return new IntValue((Integer)targetValue);
                }
                else if (targetValue instanceof Double) {
                    return new DoubleValue((Double)targetValue);
                }
                else if (targetValue instanceof StringConvertable) {
                    return new StringValue(((StringConvertable)targetValue).getConverter().convertToString(targetValue));
                }
                else if (targetValue instanceof Enum) {
                	return new StringValue(((Enum)targetValue).name());
                }
                else if (targetValue instanceof String) {
                    return new StringValue((String)targetValue);
                }
                return new StringValue(targetValue.toString());
           }
            else {
                logger.warning("Could not analyse "+replacedString);
                return new StringValue(replacedString);
           }
            
         }
        else if (token instanceof Value) {
            return (Value)token;
        }
        return null;
    }
    
    private boolean evaluate (Expression expression, Vector<KeyValueInstance> parameters)
    {
    	Value leftValue = evaluateToken(expression.getLeftOperand(),parameters);
        Value rightValue = evaluateToken(expression.getRightOperand(),parameters);
               
        if (expression.getOperator() == Operator.AND) {
            if ((leftValue instanceof BooleanValue) && (rightValue instanceof BooleanValue)) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Evaluate AND in "+expression+" : left:"+leftValue+" right:"+rightValue+" returns: "+(((BooleanValue)leftValue).getBooleanValue() && ((BooleanValue)rightValue).getBooleanValue()));
                return (((BooleanValue)leftValue).getBooleanValue() && ((BooleanValue)rightValue).getBooleanValue());
            }
            else {
                logger.warning("Incompatible operands: "+leftValue.getClass().getName()+" AND "+rightValue.getClass().getName());
                return true;
            }
        }
        
        else if (expression.getOperator() == Operator.OR) {
            if ((leftValue instanceof BooleanValue) && (rightValue instanceof BooleanValue)) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Evaluate OR in "+expression+" : left:"+leftValue+" right:"+rightValue+" returns: "+(((BooleanValue)leftValue).getBooleanValue() || ((BooleanValue)rightValue).getBooleanValue()));
               return (((BooleanValue)leftValue).getBooleanValue() || ((BooleanValue)rightValue).getBooleanValue());
            }
            else {
                logger.warning("Incompatible operands: "+leftValue.getClass().getName()+" OR "+rightValue.getClass().getName());
                return true;
            }
        }
        
        else if (expression.getOperator() == Operator.EQU) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("Evaluate EQU in "+expression+" : left:"+leftValue+" right:"+rightValue+" returns: "+( (leftValue==null && rightValue==null ) || (leftValue!=null && leftValue.equals(rightValue))));
           return (leftValue==null && rightValue==null ) || leftValue.equals(rightValue);
        }
        
        else if (expression.getOperator() == Operator.NEQ) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("Evaluate NEQ in "+expression+" : left:"+leftValue+" right:"+rightValue+" returns: "+leftValue.equals(rightValue));
           return !leftValue.equals(rightValue);
        }
        
        else {
            logger.warning("Unknown operator: "+expression.getOperator());
            return true;
        }
        
    }
    
    protected class KeyValueInstance 
    {
        private InspectableObject _inspectable;
        private String _keyPath;
        private Object _value;
        private PropertyModel _propModel;
       
        protected KeyValueInstance (InspectableObject inspectable, String keyPath)
        {
             _inspectable = inspectable;
            _keyPath = keyPath;
            _value = KVUtil.getValueForKey(inspectable,keyPath);
            _propModel = null;
            /*System.out.println("Inspectable: "+inspectable);
            System.out.println("keyPath: "+keyPath);
            System.out.println("_value: "+_value);*/
        }

        public InspectableObject getInspectable() 
        {
            return _inspectable;
        }

        public String getKeyPath() 
        {
            return _keyPath;
        }

        public Object getValue() 
        {
            return _value;
        }

        public Object getValueForKey(String aKeyPath) 
        {
            if (_value == null) return null;
            if (_value instanceof KeyValueCoding) {
                return KVUtil.getValueForKey((KeyValueCoding)_value,aKeyPath);
            }
            logger.warning("Could not access "+aKeyPath+" for "+_value);
            return null;
        }

        public PropertyModel getPropertyModel() 
        {
        		if ((_propertyModel == null) || (_propertyModel.getInspectorModel() == null)) 
        			return null;
            if (_propModel == null) {
                _propModel = _propertyModel.getInspectorModel().getPropertyNamed(_keyPath);                
            }
            if (_propModel == null) {
                logger.warning("Cannot find PropertyModel for "+_keyPath);  
               // _propertyModel.getInspectorModel().debug();
             }
            
            return _propModel;
        }

       public String getStringRepresentation()
        {
           if (_value == null) return "null";
            if (_value instanceof String) {
                return (String) _value;
            } else if (_value instanceof StringConvertable) {
                return ((StringConvertable) _value).getConverter().convertToString(_value);
            } else if ((_value instanceof KeyValueCoding) && (getPropertyModel() != null) && (getPropertyModel().hasIdentifier())) {
                return getPropertyModel().getIdentifiedObject((KeyValueCoding) _value);
            } else if (_value instanceof Number){
                return _value.toString();
            } else if (_value instanceof Boolean){
                return _value.toString();
            }else {
                /*if (logger.isLoggable(Level.WARNING))
                    logger.warning("There is an error in some configuration file :\n the property named '" + _keyPath
                            + "' has no string representation formatter ! Object is a "+(_value!=null?_value.getClass().getName():"null"));*/
                return _value.toString();
            }
        }
        
    }
}
