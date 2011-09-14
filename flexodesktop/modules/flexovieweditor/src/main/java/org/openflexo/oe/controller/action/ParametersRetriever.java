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
package org.openflexo.oe.controller.action;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.AskParametersDialog.ValidationCondition;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.action.DropSchemeAction;
import org.openflexo.foundation.ontology.action.EditionSchemeAction;
import org.openflexo.foundation.ontology.calc.CalcPaletteElement;
import org.openflexo.foundation.ontology.calc.EditionPatternParameter;
import org.openflexo.foundation.ontology.calc.EditionScheme;
import org.openflexo.foundation.ontology.calc.EditionPatternParameter.CustomType;
import org.openflexo.foundation.ontology.calc.EditionPatternParameter.WidgetType;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.InfoLabelParameter;
import org.openflexo.foundation.param.IntegerParameter;
import org.openflexo.foundation.param.LocalizedTextFieldParameter;
import org.openflexo.foundation.param.OntologyIndividualParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.RoleParameter;
import org.openflexo.foundation.param.StaticDropDownParameter;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldAndLabelParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.inspector.LocalizedString;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

public class ParametersRetriever {

	private static final Logger logger = Logger.getLogger(ParametersRetriever.class.getPackage().getName());

	private Vector<URIParameter> uriParametersList;
	private Hashtable<EditionPatternParameter,ParameterDefinition> paramHash;
	protected CalcPaletteElement paletteElement;
	
	public static boolean retrieveParameters(final EditionSchemeAction<?> action, boolean skipDialogWhenPossible)
	{
		if (action.getEditionScheme().getParameters().size() == 0 
				&& action.getEditionScheme().getSkipConfirmationPanel()) return true;
		ParametersRetriever retriever = new ParametersRetriever();
		return retriever._retrieveParameters(action,skipDialogWhenPossible);
	}

	private boolean _retrieveParameters(final EditionSchemeAction<?> action, boolean skipDialogWhenPossible)
	{
		paletteElement = (action instanceof DropSchemeAction ? ((DropSchemeAction)action).getPaletteElement() : null);
		EditionScheme editionScheme = action.getEditionScheme();
		
		Vector<ParameterDefinition> parameters = new Vector<ParameterDefinition>();

		uriParametersList = new Vector<URIParameter>();
		paramHash = new Hashtable<EditionPatternParameter, ParameterDefinition>();
		
		String description = editionScheme.getDescription();
		if (description == null) description = editionScheme.getEditionPattern().getDescription();
		
		parameters.add(new InfoLabelParameter("infoLabel", "description", description));
		
		for (final EditionPatternParameter parameter : editionScheme.getParameters()) {
			ParameterDefinition param = null;
			if (parameter.getWidget() == WidgetType.TEXT_FIELD) {
				if (parameter.getUniqueURI()) {
					param = new URIParameter(parameter, action);
					uriParametersList.add((URIParameter)param);
					action.getParameterValues().put(parameter.getName(), parameter.getDefaultValue(paletteElement));
				}
				else {
					param = new TextFieldParameter(parameter.getName(), parameter.getLabel(), parameter.getDefaultValue(paletteElement), 40) {
				       	@Override
			        	public void setValue(String value)
			        	{
			        		super.setValue(value);
			        		action.getParameterValues().put(parameter.getName(), value);
							for (URIParameter p : uriParametersList) {
								if (p.getDependancyParameters() != null && p.getDependancyParameters().contains(this)) {
									p.updateURIName();
								}
							}
			        	}
					};
					action.getParameterValues().put(parameter.getName(), parameter.getDefaultValue(paletteElement));
				}
			}
			else if (parameter.getWidget() == WidgetType.LOCALIZED_TEXT_FIELD) {
				LocalizedString ls = new LocalizedString(parameter.getDefaultValue(paletteElement),Language.ENGLISH);
					param = new LocalizedTextFieldParameter(parameter.getName(), parameter.getLabel(), ls, 40) {
				       	@Override
			        	public void setValue(LocalizedString value)
			        	{
			        		super.setValue(value);
			        		action.getParameterValues().put(parameter.getName(), value);
			        	}
					};
					action.getParameterValues().put(parameter.getName(), ls);
			}
			else if (parameter.getWidget() == WidgetType.TEXT_AREA) {
				param = new TextAreaParameter(parameter.getName(), parameter.getLabel(), parameter.getDefaultValue(paletteElement), 40, 5) {
			       	@Override
		        	public void setValue(String value)
		        	{
		        		super.setValue(value);
		        		action.getParameterValues().put(parameter.getName(), value);
		        	}
				};
	       		action.getParameterValues().put(parameter.getName(), parameter.getDefaultValue(paletteElement));
			}
			else if (parameter.getWidget() == WidgetType.INTEGER) {
				int defaultValue = 0;
				try {
					defaultValue = Integer.parseInt(parameter.getDefaultValue(paletteElement));
				}
				catch (NumberFormatException e) {
					// Don't care
				}
				param = new IntegerParameter(parameter.getName(), parameter.getLabel(), defaultValue) {
			       	@Override
		        	public void setValue(Integer value)
		        	{
		        		super.setValue(value);
		        		action.getParameterValues().put(parameter.getName(), value);
						for (URIParameter p : uriParametersList) {
							if (p.getDependancyParameters() != null && p.getDependancyParameters().contains(this)) {
								p.updateURIName();
							}
						}
		        	}
				};
	       		action.getParameterValues().put(parameter.getName(), Integer.parseInt(parameter.getDefaultValue(paletteElement)));
			}
			else if (parameter.getWidget() == WidgetType.CHECKBOX) {
				param = new CheckboxParameter(
						parameter.getName(), 
						parameter.getLabel(), 
						parameter.getDefaultValue(paletteElement).equalsIgnoreCase("true") 
						|| parameter.getDefaultValue(paletteElement).equalsIgnoreCase("yes")) {
			       	@Override
		        	public void setValue(Boolean value)
		        	{
		        		super.setValue(value);
		        		action.getParameterValues().put(parameter.getName(), value);
		        	}
				};
	       		action.getParameterValues().put(
	       				parameter.getName(), 
	       				parameter.getDefaultValue(paletteElement).equalsIgnoreCase("true") 
	       				|| parameter.getDefaultValue(paletteElement).equalsIgnoreCase("yes"));
			}
			else if (parameter.getWidget() == WidgetType.DROPDOWN) {
				param = new StaticDropDownParameter<String>(parameter.getName(), parameter.getLabel(), parameter.getValuesList(),parameter.getDefaultValue()) {
			       	@Override
		        	public void setValue(String value)
		        	{
		        		super.setValue(value);
		        		action.getParameterValues().put(parameter.getName(), value);
		        	}
				};
	       		action.getParameterValues().put(parameter.getName(), parameter.getDefaultValue(paletteElement));
			}
			else if (parameter.getWidget() == WidgetType.CUSTOM) {
				if (parameter.getCustomType() == CustomType.OntologyIndividual) {
					OntologyClass ontologyClass = parameter.getConcept();
					OntologyIndividual defaultIndividual = null;
					if (ontologyClass != null && ontologyClass.getIndividuals().size() > 0) {
						defaultIndividual = ontologyClass.getIndividuals().firstElement();
					}
					param = new OntologyIndividualParameter(parameter.getName(), parameter.getLabel(),ontologyClass,defaultIndividual) {
						@Override
						public void setValue(OntologyIndividual value) {
			        		super.setValue(value);
			        		logger.info("Set as parameter "+parameter.getName()+" value="+value);
			        		action.getParameterValues().put(parameter.getName(), value);
						};
					};
					if (defaultIndividual != null) {
		        		logger.info("Set as parameter "+parameter.getName()+" value="+defaultIndividual);
						action.getParameterValues().put(parameter.getName(), defaultIndividual);
					}
				}
				else if (parameter.getCustomType() == CustomType.FlexoRole) {
					param = new RoleParameter(parameter.getName(), parameter.getLabel(),null) {
						@Override
						public void setValue(Role value) {
			        		super.setValue(value);
			        		System.out.println("Set as parameter "+parameter.getName()+" value="+value);
			        		action.getParameterValues().put(parameter.getName(), value);
						};
					};
				}
			}
			if (param != null) {
				parameters.add(param);
				paramHash.put(parameter,param);
				if (StringUtils.isNotEmpty(parameter.getConditional())) {
					// TODO: use AnTAR instead of bullshit from Inspector lib
	            	String hackDeLaMort = ToolBox.replaceStringByStringInString("|", " OR ", parameter.getConditional());
	            	hackDeLaMort = ToolBox.replaceStringByStringInString("'", "", hackDeLaMort);
					
					param.setConditional(hackDeLaMort);
					param.setDepends(getDepends(parameter.getConditional(), action));
	            	//System.out.println("hackDeLaMort="+hackDeLaMort);
	            	//System.out.println("depends="+getDepends(parameter.getConditional(), action));
				}
			}
		}
		
		for (URIParameter p : uriParametersList) {
			p.buildDependancies();
			p.updateURIName();
		}

		if (skipDialogWhenPossible) {
			boolean isValid = true;
			for (URIParameter param : uriParametersList) {
				if (param._parameter.evaluateCondition(action.getParameterValues())) {
					if (StringUtils.isEmpty(param.getValue())) {
						// declared_uri_must_be_specified_please_enter_uri
						isValid = false;
					}
					else if (action.getProject().getProjectOntologyLibrary().isDuplicatedURI(action.getProject().getProjectOntology().getURI(),param.getValue())) {
						// declared_uri_must_be_unique_please_choose_an_other_uri
						isValid = false;
					}
					else if (!action.getProject().getProjectOntologyLibrary().testValidURI(action.getProject().getProjectOntology().getURI(),param.getValue())) {
						// declared_uri_is_not_well_formed_please_choose_an_other_uri
						isValid = false;
					}
				}
			}
			if (isValid) return true;
			// We need to open dialog anyway
		}
		
		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
				action.getProject(), 
				editionScheme.getEditionPattern().getName(), 
				"", // No need for extra title
				new ValidationCondition() {
					@Override
					public boolean isValid(ParametersModel model) {
						for (URIParameter param : uriParametersList) {
							if (param._parameter.evaluateCondition(action.getParameterValues())) {
								if (StringUtils.isEmpty(param.getValue())) {
									setErrorMessage(FlexoLocalization.localizedForKey("declared_uri_must_be_specified_please_enter_uri"));
									return false;
								}
								if (action.getProject().getProjectOntologyLibrary().isDuplicatedURI(action.getProject().getProjectOntology().getURI(),param.getValue())) {
									setErrorMessage(FlexoLocalization.localizedForKey("declared_uri_must_be_unique_please_choose_an_other_uri"));
									return false;
								}
								else if (!action.getProject().getProjectOntologyLibrary().testValidURI(action.getProject().getProjectOntology().getURI(),param.getValue())) {
									setErrorMessage(FlexoLocalization.localizedForKey("declared_uri_is_not_well_formed_please_choose_an_other_uri"));
									return false;
								}
							}
						}
						return true;
					}
				},
				parameters);

		if (dialog.getStatus()==AskParametersDialog.VALIDATE) {
			return true;
		}
		else {
			return false;
		}
 
	}

	
	public class URIParameter extends TextFieldAndLabelParameter
	{

		private EditionPatternParameter _parameter;
		private EditionSchemeAction<?> _action;
		private Expression baseExpression;
		private Vector<ParameterDefinition> dependancyParameters;
		
		public URIParameter(EditionPatternParameter parameter, EditionSchemeAction action)
		{
			super(parameter.getName(), parameter.getLabel(), "<not set yet>", 40);
			_parameter = parameter;
			_action = action;
			buildDependancies();
		}

		protected void buildDependancies()
		{
			if (_parameter.getBase() != null) {
				DefaultExpressionParser parser = new DefaultExpressionParser();
				try {
					baseExpression = parser.parse(_parameter.getBase());
					dependancyParameters = new Vector<ParameterDefinition>();
					Vector<EditionPatternParameter> depends = extractDepends(_parameter.getBase(), _action);
					for (EditionPatternParameter p : depends) {
						if (p!= null) dependancyParameters.add(paramHash.get(p));
						//System.out.println("Param URI "+parameter+" depends of "+paramHash.get(p)+" p="+p);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
       	@Override
    	public void setValue(String value)
    	{
    		super.setValue(value);
    		_action.getParameterValues().put(_parameter.getName(), value);
			//System.out.println("Param URI "+_parameter+" (name="+_parameter.getName()+" takes value "+value);
   	}

		public Vector<ParameterDefinition> getDependancyParameters()
		{
			return dependancyParameters;
		}

		public void updateURIName() 
		{
			String baseProposal = getURIName();
			String proposal = baseProposal;
			if (!StringUtils.isEmpty(baseProposal)) {
				Integer i = null;
				while (_action.getProject().getProjectOntologyLibrary().isDuplicatedURI(
						_action.getProject().getProjectOntology().getURI(),proposal)) {
					if (i == null) i= 1; else i++;
					proposal = baseProposal+i;
				}
			}
			setValue(proposal);			
		}

		@Override
		public String getAdditionalLabel() 
		{
			return getURI();
		}
		
		public String getURI() 
		{
			return _action.getProject().getProjectOntology().makeURI(getURIName());
		}
		
		public String getURIName() 
		{
			if (baseExpression == null) {
				return _parameter.getDefaultValue(paletteElement);
			}
			Hashtable<String,Object> paramValues = new Hashtable<String,Object>();
			for (String s : _action.getParameterValues().keySet()) {
				String value = _action.getParameterValues().get(s).toString();
				paramValues.put(s,JavaUtils.getClassName(value));
			}
			return evaluateExpression(paramValues);
		}
		
		private String evaluateExpression(final Hashtable<String,Object> parameterValues)
		{
			if (baseExpression == null) return "";
			try {
				Expression evaluation = baseExpression.evaluate(parameterValues);
				if (evaluation instanceof StringConstant) {
					return ((StringConstant)evaluation).getValue();
				}
				logger.warning("Inconsistant data: expected StringConstant, found "+evaluation.getClass().getName());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} 
			return "";
		}


	}
	
	private String getDepends(String anExpression, final EditionSchemeAction action)
	{
		String depends = "";
		boolean isFirst = true;
		for (final EditionPatternParameter parameter : extractDepends(anExpression,action)) {
			depends += (isFirst?"":",")+parameter.getName();
			isFirst = false;
		}
		return depends;
	}

	Vector<EditionPatternParameter> extractDepends(String anExpression, final EditionSchemeAction action)
	{
		EditionScheme editionScheme = action.getEditionScheme();
		Vector<EditionPatternParameter> returned = new Vector<EditionPatternParameter>();
		try {
			System.out.println("Expression: "+anExpression);
			Vector<Variable> variables = Expression.extractVariables(anExpression);
			for (Variable v : variables) {
				returned.add(editionScheme.getParameter(v.getName()));
				System.out.println("depends: "+v.getName()+" param: "+editionScheme.getParameter(v.getName()));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TypeMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returned;
	}


}
