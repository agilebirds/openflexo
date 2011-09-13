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
package org.openflexo.fib.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingExpression;
import org.openflexo.antar.binding.BindingExpressionFactory;
import org.openflexo.antar.binding.BindingValue;
import org.openflexo.antar.binding.StaticBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingExpression.BindingValueConstant;
import org.openflexo.antar.binding.BindingExpression.BindingValueVariable;
import org.openflexo.antar.expr.ArithmeticBinaryOperator;
import org.openflexo.antar.expr.ArithmeticUnaryOperator;
import org.openflexo.antar.expr.BinaryOperator;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BooleanBinaryOperator;
import org.openflexo.antar.expr.BooleanUnaryOperator;
import org.openflexo.antar.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Operator;
import org.openflexo.antar.expr.OperatorNotSupportedException;
import org.openflexo.antar.expr.SymbolicConstant;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.UnaryOperator;
import org.openflexo.antar.expr.UnaryOperatorExpression;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.antar.pp.ExpressionPrettyPrinter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.MouseOverButton;

public class BindingExpressionPanel extends JPanel implements FocusListener {

    static final Logger logger = Logger.getLogger(BindingExpressionPanel.class.getPackage().getName());

    BindingExpression _bindingExpression;

	protected static ImageIcon iconForOperator(Operator op)
	{
		if (op == ArithmeticBinaryOperator.ADDITION) {
			return FIBIconLibrary.ADDITION_ICON;
		} else if (op == ArithmeticBinaryOperator.SUBSTRACTION) {
			return FIBIconLibrary.SUBSTRACTION_ICON;
		} else if (op == ArithmeticBinaryOperator.MULTIPLICATION) {
			return FIBIconLibrary.MULTIPLICATION_ICON;
		} else if (op == ArithmeticBinaryOperator.DIVISION) {
			return FIBIconLibrary.DIVISION_ICON;
		} else if (op == ArithmeticBinaryOperator.POWER) {
			return FIBIconLibrary.POWER_ICON;
		} else if (op == BooleanBinaryOperator.EQUALS) {
			return FIBIconLibrary.EQUALS_ICON;
		} else if (op == BooleanBinaryOperator.NOT_EQUALS) {
			return FIBIconLibrary.NOT_EQUALS_ICON;
		} else if (op == BooleanBinaryOperator.LESS_THAN) {
			return FIBIconLibrary.LESS_THAN_ICON;
		} else if (op == BooleanBinaryOperator.LESS_THAN_OR_EQUALS) {
			return FIBIconLibrary.LESS_THAN_OR_EQUALS_ICON;
		} else if (op == BooleanBinaryOperator.GREATER_THAN) {
			return FIBIconLibrary.GREATER_THAN_ICON;
		} else if (op == BooleanBinaryOperator.GREATER_THAN_OR_EQUALS) {
			return FIBIconLibrary.GREATER_THAN_OR_EQUALS_ICON;
		} else if (op == BooleanBinaryOperator.AND) {
			return FIBIconLibrary.AND_ICON;
		} else if (op == BooleanBinaryOperator.OR) {
			return FIBIconLibrary.OR_ICON;
		} else if (op == BooleanUnaryOperator.NOT) {
			return FIBIconLibrary.NOT_ICON;
		} else if (op == ArithmeticUnaryOperator.UNARY_MINUS) {
			return FIBIconLibrary.SUBSTRACTION_ICON;
		} else if (op == ArithmeticUnaryOperator.SIN) {
			return FIBIconLibrary.SIN_ICON;
		} else if (op == ArithmeticUnaryOperator.ASIN) {
			return FIBIconLibrary.ASIN_ICON;
		} else if (op == ArithmeticUnaryOperator.COS) {
			return FIBIconLibrary.COS_ICON;
		} else if (op == ArithmeticUnaryOperator.ACOS) {
			return FIBIconLibrary.ACOS_ICON;
		} else if (op == ArithmeticUnaryOperator.TAN) {
			return FIBIconLibrary.TAN_ICON;
		} else if (op == ArithmeticUnaryOperator.ATAN) {
			return FIBIconLibrary.ATAN_ICON;
		} else if (op == ArithmeticUnaryOperator.EXP) {
			return FIBIconLibrary.EXP_ICON;
		} else if (op == ArithmeticUnaryOperator.LOG) {
			return FIBIconLibrary.LOG_ICON;
		} else if (op == ArithmeticUnaryOperator.SQRT) {
			return FIBIconLibrary.SQRT_ICON;
		}
		return null;
	}
	
	public BindingExpressionPanel(BindingExpression bindingExpression)
	{
		super();
		converter = (bindingExpression != null ? bindingExpression.getConverter() : null);
		//converter = AbstractBinding.bindingExpressionConverter;
		setLayout(new BorderLayout());
		_bindingExpression = bindingExpression;
		init();
	}
	
	private JTextArea expressionTA;
	private JPanel controls;
	private ExpressionInnerPanel rootExpressionPanel;
	
	private enum ExpressionParsingStatus
	{
		UNDEFINED,
		VALID,
		SYNTAXICALLY_VALID,
		INVALID
	}
	
	private ExpressionParsingStatus status = ExpressionParsingStatus.UNDEFINED;
	private String message = UNDEFINED_EXPRESSION_MESSAGE;
	
	private static final String UNDEFINED_EXPRESSION_MESSAGE = "please_define_expression";
	private static final String UNDEFINED_OPERAND_FOR_OPERATOR = "undefined_operand_for_operator";
	private static final String VALID_EXPRESSION = "expression_is_valid_and_conform_to_required_type";
	private static final String VALID_EXPRESSION_BUT_MISMATCH_TYPE = "expression_is_valid_but_not_conform_to_requested_type_(found:($0)_expected:($1))";
	
	private JLabel statusIcon;
	private JLabel messageLabel;
	private JTextArea evaluationTA;
	protected JPanel evaluationPanel;
	
	protected ExpressionPrettyPrinter pp = new DefaultExpressionPrettyPrinter();
	protected BindingExpressionFactory converter;
	
	private ExpressionInnerPanel focusReceiver = null;
	
	public void setEditedExpression(BindingExpression bindingExpression)
	{
		converter = (bindingExpression != null ? bindingExpression.getConverter() : null);
		_bindingExpression = bindingExpression;
		if (bindingExpression != null) {
			_setEditedExpression(bindingExpression.getExpression());
			if ((rootExpressionPanel.getRepresentedExpression() == null) 
					|| !rootExpressionPanel.getRepresentedExpression().equals(bindingExpression.getExpression())) {
				rootExpressionPanel.setRepresentedExpression(bindingExpression.getExpression());
			}
		}
		update();
	}
	
	protected void setEditedExpression(Expression expression)
	{
		_setEditedExpression(expression);
		update();
		fireEditedExpressionChanged(_bindingExpression);
	}
	
	private void _setEditedExpression(Expression expression)
	{
		if (_bindingExpression != null) {
			_bindingExpression.setExpression(expression);
		}
		_checkEditedExpression();
	}
	
	protected void _checkEditedExpression()
	{
		Operator undefinedOperator = null;
		
		if (_bindingExpression == null) {
			return;
		}
		
		if ((evaluationPanel != null) && (evaluationTA != null) && evaluationPanel.isVisible()) {
			evaluationTA.setText(FlexoLocalization.localizedForKey("cannot_evaluate"));
		}

		if (_bindingExpression.getExpression() != null) {
			if (expressionIsUndefined(_bindingExpression.getExpression())) {
				status = ExpressionParsingStatus.UNDEFINED;
				message = FlexoLocalization.localizedForKey(UNDEFINED_EXPRESSION_MESSAGE);
				return;
			}
			else {
				for (Expression e : _bindingExpression.getExpression().getAllAtomicExpressions()) {
					if (e instanceof BindingValueConstant) {
						BindingValueConstant c = (BindingValueConstant)e;
						if ((c.getStaticBinding() == null) || !c.getStaticBinding().isBindingValid()) {
							message = FlexoLocalization.localizedForKey("invalid_value")+" "+c.getConstant();
							status = ExpressionParsingStatus.INVALID;
							return;
						}
					}
					if (e instanceof BindingValueVariable) {
						BindingValueVariable v = (BindingValueVariable)e;
						if ((v.getBindingValue() == null) || !v.getBindingValue().isBindingValid()) {
							message = FlexoLocalization.localizedForKey("invalid_binding")+" "+v.getVariable();
							status = ExpressionParsingStatus.INVALID;
							return;
						}
					}
				}
			}
		}

		if (_bindingExpression.getExpression() == null) {
			message = FlexoLocalization.localizedForKey("cannot_parse")+" "+_bindingExpression.getUnparsableValue();
			status = ExpressionParsingStatus.INVALID;
		}		
		else if ((undefinedOperator = firstOperatorWithUndefinedOperand(_bindingExpression.getExpression())) != null) {
			status = ExpressionParsingStatus.INVALID;
			try {
				message = FlexoLocalization.localizedForKey(UNDEFINED_OPERAND_FOR_OPERATOR)+" "+ undefinedOperator.getLocalizedName()+ " ["+pp.getSymbol(undefinedOperator)+"]";
			} catch (OperatorNotSupportedException e) {
				message = FlexoLocalization.localizedForKey(UNDEFINED_OPERAND_FOR_OPERATOR)+" "+ undefinedOperator.getLocalizedName()+ " [?]";
			}
		}
		else {
			try {
				EvaluationType evaluationType = _bindingExpression.getEvaluationType();
				
				if ((_bindingExpression != null) 
						&& (_bindingExpression.getBindingDefinition() != null)
						&& (_bindingExpression.getBindingDefinition().getType() != null)) {
					EvaluationType wantedEvaluationType = TypeUtils.kindOfType(_bindingExpression.getBindingDefinition().getType());
					if ((wantedEvaluationType == EvaluationType.LITERAL)
							|| (evaluationType == wantedEvaluationType) 
							|| ((wantedEvaluationType == EvaluationType.ARITHMETIC_FLOAT) && (evaluationType == EvaluationType.ARITHMETIC_INTEGER))) {
						status = ExpressionParsingStatus.VALID;
						message = FlexoLocalization.localizedForKey(VALID_EXPRESSION)+" : "+evaluationType.getLocalizedName();
					}
					else {
						status = ExpressionParsingStatus.SYNTAXICALLY_VALID;
						message = FlexoLocalization.localizedForKeyWithParams(VALID_EXPRESSION_BUT_MISMATCH_TYPE,evaluationType.getLocalizedName(),wantedEvaluationType.getLocalizedName());
					}
				}
				else {
					status = ExpressionParsingStatus.VALID;
					message = FlexoLocalization.localizedForKey(VALID_EXPRESSION)+" : "+evaluationType.getLocalizedName();
				}
				
				if ((evaluationPanel != null) && (evaluationTA != null) && evaluationPanel.isVisible() && (_bindingExpression != null)) {
					BindingExpression evaluatedExpression = _bindingExpression.evaluate();
					if (evaluatedExpression.getExpression() != null) {
						evaluationTA.setText(evaluatedExpression.getExpression().toString());
					}
				}
				
			} catch (TypeMismatchException e) {
				status = ExpressionParsingStatus.INVALID;
				message = e.getHTMLLocalizedMessage();
			}
			
		}
	}
	
	private boolean expressionIsUndefined(Expression expression) 
	{
		return ((expression instanceof BindingValueVariable)
				&& ((BindingValueVariable)expression).getName().trim().equals(""));
	}
	
	private Operator firstOperatorWithUndefinedOperand(Expression expression)
	{
		if (expression instanceof BindingValueVariable) {
			return null;
		} else if (expression instanceof BindingValueConstant) {
			return null;
		} else if (expression instanceof BinaryOperatorExpression) {
			Expression leftOperand = ((BinaryOperatorExpression)expression).getLeftArgument();
			if (expressionIsUndefined(leftOperand)) {
				return ((BinaryOperatorExpression)expression).getOperator();
			}
			Operator returned = firstOperatorWithUndefinedOperand(leftOperand);
			if (returned == null) {
				Expression rightOperand = ((BinaryOperatorExpression)expression).getRightArgument();
				if (expressionIsUndefined(rightOperand)) {
					return ((BinaryOperatorExpression)expression).getOperator();
				}
				return firstOperatorWithUndefinedOperand(rightOperand);
			}
		}
		else if (expression instanceof UnaryOperatorExpression) {
			Expression operand = ((UnaryOperatorExpression)expression).getArgument();
			if (expressionIsUndefined(operand)) {
				return ((UnaryOperatorExpression)expression).getOperator();
			}
			return firstOperatorWithUndefinedOperand(operand);
		}
		return null;
	}
	
	public BindingExpression getEditedExpression()
	{
		return _bindingExpression;
	}
	
	protected void expressionMayHaveBeenEdited() 
	{
		if (_bindingExpression == null) {
			return;
		}
		
		try {
			Expression newExpression;
			if (expressionTA.getText().trim().equals("") && (_bindingExpression.getOwner() instanceof Bindable)) {
				newExpression = new BindingExpression.BindingValueVariable("",_bindingExpression.getOwner());
			}
			else {
				if (converter == null) {
					logger.warning("Could not access to BindingExpressionConverter");
					return;
				}
				if ((_bindingExpression != null) && (_bindingExpression.getOwner() instanceof Bindable)) {
					converter.setBindable(_bindingExpression.getOwner());
				}
				newExpression = converter.parseExpressionFromString(expressionTA.getText());
			}
			if (!newExpression.equals(_bindingExpression.getExpression()) || (status == ExpressionParsingStatus.INVALID)) {
				_setEditedExpression(newExpression);
				rootExpressionPanel.setRepresentedExpression(_bindingExpression.getExpression());
				update();
				fireEditedExpressionChanged(_bindingExpression);
			}
		} catch (ParseException e) {
			message = "ERROR: cannot parse "+expressionTA.getText();
			status = ExpressionParsingStatus.INVALID;
			updateAdditionalInformations();
		}
	}

	private void init()
	{
		expressionTA = new JTextArea(3,50);
		expressionTA.setLineWrap(true);
		//expressionTA.setWrapStyleWord(true);
		expressionTA.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					expressionMayHaveBeenEdited();
				}
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					expressionMayHaveBeenEdited();
				}
			}
		});
		expressionTA.addFocusListener(new FocusAdapter(){
			@Override
			public void focusLost(FocusEvent e) {
				expressionMayHaveBeenEdited();
			}
		});
		/*expressionTA.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				expressionMayHaveBeenEdited();
			}
		});*/

		statusIcon = new JLabel();
		messageLabel = new JLabel("",SwingConstants.LEFT);
		messageLabel.setFont(new Font("SansSerif", Font.ITALIC, 9));
		
		evaluationPanel = new JPanel(new BorderLayout());
		evaluationTA = new JTextArea(1,20);
		evaluationTA.setEditable(false);
		evaluationTA.setLineWrap(true);

		evaluationPanel.add(new JLabel(FlexoLocalization.localizedForKey("evaluation")+"  "),BorderLayout.WEST);
		evaluationPanel.add(evaluationTA,BorderLayout.CENTER);
		
		final MouseOverButton showEvaluationButton = new MouseOverButton();
		showEvaluationButton.setBorder(BorderFactory.createEmptyBorder());
		showEvaluationButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
		showEvaluationButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
		showEvaluationButton.setToolTipText(FlexoLocalization.localizedForKey("show_evaluation"));
		showEvaluationButton.addActionListener(new ActionListener() {
           @Override
		public void actionPerformed(ActionEvent e)
           {
        	   if (!evaluationPanel.isVisible()) {
        		   showEvaluationButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_ICON);
        		   showEvaluationButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
        		   showEvaluationButton.setToolTipText(FlexoLocalization.localizedForKey("hide_evaluation"));  
        		   evaluationPanel.setVisible(true);
        		   _checkEditedExpression();
        	   }
        	   else {
        		   showEvaluationButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
        		   showEvaluationButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
        		   showEvaluationButton.setToolTipText(FlexoLocalization.localizedForKey("show_evaluation"));        		   
        		   evaluationPanel.setVisible(false);
        	   }
            }
       });

		JPanel statusAndMessageLabel = new JPanel(new BorderLayout());
		statusAndMessageLabel.add(statusIcon,BorderLayout.WEST);
		statusAndMessageLabel.add(messageLabel,BorderLayout.CENTER);
		statusAndMessageLabel.add(showEvaluationButton,BorderLayout.EAST);
		

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(expressionTA,BorderLayout.NORTH);
		topPanel.add(statusAndMessageLabel,BorderLayout.CENTER);
		topPanel.add(evaluationPanel,BorderLayout.SOUTH);

		evaluationPanel.setVisible(false);
		
		add(topPanel,BorderLayout.NORTH);
		rootExpressionPanel = new ExpressionInnerPanel(_bindingExpression.getExpression()) {
			@Override
			public void representedExpressionChanged(Expression newExpression) {
				setEditedExpression(newExpression);
			}
		};
		focusReceiver = rootExpressionPanel;
		add(new JScrollPane(rootExpressionPanel),BorderLayout.CENTER);
		controls = new JPanel(new BorderLayout());
		
		final JPanel commonControls = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		final JPanel mathControls = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		
		commonControls.add(createOperatorGroupPanel(
				"logical", 
				BooleanBinaryOperator.AND,
				BooleanBinaryOperator.OR,
				BooleanUnaryOperator.NOT));
		
		commonControls.add(createOperatorGroupPanel(
				"comparison", 
				BooleanBinaryOperator.EQUALS,
				BooleanBinaryOperator.NOT_EQUALS,
				BooleanBinaryOperator.LESS_THAN,
				BooleanBinaryOperator.LESS_THAN_OR_EQUALS,
				BooleanBinaryOperator.GREATER_THAN,
				BooleanBinaryOperator.GREATER_THAN_OR_EQUALS));
		
		commonControls.add(createOperatorGroupPanel(
				"arithmetic", 
				ArithmeticBinaryOperator.ADDITION,
				ArithmeticBinaryOperator.SUBSTRACTION,
				ArithmeticBinaryOperator.MULTIPLICATION,
				ArithmeticBinaryOperator.DIVISION,
				ArithmeticUnaryOperator.UNARY_MINUS));
		
		final MouseOverButton moreButton = new MouseOverButton();
		moreButton.setBorder(BorderFactory.createEmptyBorder());
		moreButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
		moreButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
		moreButton.setToolTipText(FlexoLocalization.localizedForKey("show_more_operators"));
		moreButton.addActionListener(new ActionListener() {
           @Override
		public void actionPerformed(ActionEvent e)
           {
        	   if (!mathControls.isVisible()) {
        		   moreButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_ICON);
        		   moreButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
        		   moreButton.setToolTipText(FlexoLocalization.localizedForKey("hide_extra_operators"));  
        		   commonControls.remove(moreButton);
        		   mathControls.add(moreButton);
        		   mathControls.setVisible(true);
        	   }
        	   else {
        		   moreButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
        		   moreButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
        		   moreButton.setToolTipText(FlexoLocalization.localizedForKey("show_more_operators"));        		   
        		   mathControls.remove(moreButton);
        		   commonControls.add(moreButton);
        		   mathControls.setVisible(false);
        	   }
            }
       });

		commonControls.add(moreButton);
		
		mathControls.add(createOperatorGroupPanel(
				"scientific", 
				ArithmeticBinaryOperator.POWER,
				ArithmeticUnaryOperator.SQRT,
				ArithmeticUnaryOperator.EXP,
				ArithmeticUnaryOperator.LOG));
		
		mathControls.add(createOperatorGroupPanel(
				"trigonometric", 
				ArithmeticUnaryOperator.SIN,
				ArithmeticUnaryOperator.ASIN,
				ArithmeticUnaryOperator.COS,
				ArithmeticUnaryOperator.ACOS,
				ArithmeticUnaryOperator.TAN,
				ArithmeticUnaryOperator.ATAN));

		controls.add(commonControls,BorderLayout.CENTER);
		controls.add(mathControls,BorderLayout.SOUTH);

		mathControls.setVisible(false);
		
		add(controls,BorderLayout.SOUTH);
		update();
	}
	
	private JPanel createOperatorGroupPanel (String title, Operator...operators)
	{
		JPanel returned = new JPanel();
		returned.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
		
		for (final Operator o : operators) {
			JButton b = new JButton(iconForOperator(o));
			b.setBorder(BorderFactory.createEmptyBorder());
			b.setToolTipText(o.getLocalizedName());
			returned.add(b);
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (o instanceof UnaryOperator) {
						appendUnaryOperator((UnaryOperator)o);
					} else if (o instanceof BinaryOperator) {
						appendBinaryOperator((BinaryOperator)o);
					}
				}
			});
		}
		
		returned.setBorder(BorderFactory.createTitledBorder(null,FlexoLocalization.localizedForKey(title),TitledBorder.CENTER,TitledBorder.TOP,new Font("SansSerif", Font.ITALIC, 8)));
		return returned;
	}
	
	protected void update()
	{
		_checkEditedExpression();
		updateExpressionTextArea();
		updateAdditionalInformations();
		revalidate();
		repaint();
	}
	
	protected void fireEditedExpressionChanged(BindingExpression expression)
	{
		// Override if required
	}
	
	protected void updateExpressionTextArea()
	{ 
		if (_bindingExpression == null) {
			return;
		}
		expressionTA.setText(pp.getStringRepresentation(_bindingExpression.getExpression()));
		if (status == ExpressionParsingStatus.UNDEFINED) {
			statusIcon.setIcon(FIBIconLibrary.WARNING_ICON);
		} else if (status == ExpressionParsingStatus.INVALID) {
			statusIcon.setIcon(FIBIconLibrary.ERROR_ICON);
		} else if (status == ExpressionParsingStatus.SYNTAXICALLY_VALID) {
			statusIcon.setIcon(FIBIconLibrary.WARNING_ICON);
		} else if (status == ExpressionParsingStatus.VALID) {
			statusIcon.setIcon(FIBIconLibrary.OK_ICON);
		}
		messageLabel.setText(message);
	}
	
	protected void updateAdditionalInformations()
	{ 
		if (status == ExpressionParsingStatus.UNDEFINED) {
			statusIcon.setIcon(FIBIconLibrary.WARNING_ICON);
		} else if (status == ExpressionParsingStatus.INVALID) {
			statusIcon.setIcon(FIBIconLibrary.ERROR_ICON);
		} else if (status == ExpressionParsingStatus.SYNTAXICALLY_VALID) {
			statusIcon.setIcon(FIBIconLibrary.WARNING_ICON);
		} else if (status == ExpressionParsingStatus.VALID) {
			statusIcon.setIcon(FIBIconLibrary.OK_ICON);
		}
		messageLabel.setText(message);
	}
	
	protected abstract class ExpressionInnerPanel extends JPanel
	{
		
		protected Expression _representedExpression;
		
		private BindingSelector _bindingSelector;
		
		//private JTextField variableOrConstantTextField;
		
		protected ExpressionInnerPanel(Expression expression)
		{
			super();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Build new ExpressionInnerPanel with "+(expression != null ? expression.toString() : "null"));
			}
			_representedExpression = expression;
			update();
			//addFocusListeners();
		}
		
		private void addFocusListeners()
		{
			addFocusListenersToAllComponentsOf(this);			
		}
		
		private void addFocusListenersToAllComponentsOf(Component c)
		{
			c.addFocusListener(BindingExpressionPanel.this);
			if (c instanceof Container) {
				Container container = (Container)c;
				for (Component c2 : container.getComponents()) {
					addFocusListenersToAllComponentsOf(c2);
				}
			}
		}
		
		private void removeFocusListeners()
		{
			removeFocusListenersToAllComponentsOf(this);			
		}
		
		private void removeFocusListenersToAllComponentsOf(Component c)
		{
			c.removeFocusListener(BindingExpressionPanel.this);
			if (c instanceof Container) {
				Container container = (Container)c;
				for (Component c2 : container.getComponents()) {
					removeFocusListenersToAllComponentsOf(c2);
				}
			}
		}
		
		/*protected void textChanged()
		{
			try {
				Expression newExpression;
				if (variableOrConstantTextField.getText().trim().equals("")) {
					newExpression = new Variable("");
				}
				else {
					newExpression = parser.parse(variableOrConstantTextField.getText());
				}
				setRepresentedExpression(newExpression);
				//System.out.println("Text has changed for "+variableOrConstantTextField.getText()+" parsed as "+newExpression);
			} catch (ParseException e) {
				System.out.println("ERROR: cannot parse "+variableOrConstantTextField.getText());
			}
		}*/
		
		protected class OperatorPanel extends JPanel
		{
			private final JButton currentOperatorIcon;

			protected OperatorPanel(Operator operator) 
			{
				super();
				setLayout(new BorderLayout());

				currentOperatorIcon = new JButton(iconForOperator(operator));
				currentOperatorIcon.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
				currentOperatorIcon.setToolTipText(operator.getLocalizedName());

				add(currentOperatorIcon,BorderLayout.CENTER);
			}
		}
		
		
		private void addBinaryExpressionVerticalLayout()
		{
			GridBagLayout gridbag2 = new GridBagLayout();
			GridBagConstraints c2 = new GridBagConstraints();
			setLayout(gridbag2);

			final BinaryOperatorExpression exp = (BinaryOperatorExpression)_representedExpression;
			final ExpressionInnerPanel me = this;
			
			OperatorPanel operatorPanel = new OperatorPanel(exp.getOperator());
			
			c2.weightx = 0.0;               
			c2.weighty = 0.0;               
			c2.anchor = GridBagConstraints.CENTER;
	        c2.fill = GridBagConstraints.VERTICAL;
			gridbag2.setConstraints(operatorPanel, c2);
			add(operatorPanel);

			operatorPanel.setBorder(BorderFactory.createEtchedBorder());
			JPanel argsPanel = new JPanel();
			
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
		
			argsPanel.setLayout(gridbag);
			
			ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getLeftArgument()){
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setLeftArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};
			
			c.weightx = 1.0;               
			c.weighty = 1.0;               
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(leftArg, c);
			argsPanel.add(leftArg);

			ExpressionInnerPanel rightArg = new ExpressionInnerPanel(exp.getRightArgument()/*,depth+1*/){
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setRightArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};
			
			c.weightx = 1.0;               
			c.weighty = 1.0;               
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(rightArg, c);
			argsPanel.add(rightArg);

			c2.weightx = 1.0;               
			c2.weighty = 0.0;               
			c2.anchor = GridBagConstraints.NORTH;
	        c2.fill = GridBagConstraints.BOTH;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			gridbag2.setConstraints(argsPanel, c2);
			add(argsPanel);

			Box box = Box.createHorizontalBox();
			c2.weightx = 1.0;               
			c2.weighty = 1.0;               
			c2.anchor = GridBagConstraints.SOUTH;
	        c2.fill = GridBagConstraints.BOTH;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			gridbag2.setConstraints(box, c2);
			add(box);
			
			isHorizontallyLayouted = false;
		}
		
		private boolean isHorizontallyLayouted = true;
		
		private void addBinaryExpressionHorizontalLayout()
		{
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
		
			setLayout(gridbag);

			final BinaryOperatorExpression exp = (BinaryOperatorExpression)_representedExpression;
			final ExpressionInnerPanel me = this;

			OperatorPanel operatorPanel = new OperatorPanel(exp.getOperator());
			
			ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getLeftArgument()){
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setLeftArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};
			
			c.weightx = 1.0;               
			c.weighty = 1.0;               
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.HORIZONTAL;
			gridbag.setConstraints(leftArg, c);
			add(leftArg);

			c.weightx = 0.0;               
			c.weighty = 1.0;               
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.NONE;
			gridbag.setConstraints(operatorPanel, c);
			add(operatorPanel);

			ExpressionInnerPanel rightArg = new ExpressionInnerPanel(exp.getRightArgument()){
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setRightArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};
			
			c.weightx = 1.0;               
			c.weighty = 1.0;               
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(rightArg, c);
			add(rightArg);

			isHorizontallyLayouted = true;

		}
		
		private void addUnaryExpressionHorizontalLayout()
		{
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
		
			setLayout(gridbag);

			final UnaryOperatorExpression exp = (UnaryOperatorExpression)_representedExpression;
			final ExpressionInnerPanel me = this;

			OperatorPanel operatorPanel = new OperatorPanel(exp.getOperator());
			
			c.weightx = 0.0;               
			c.weighty = 1.0;               
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.NONE;
			gridbag.setConstraints(operatorPanel, c);
			add(operatorPanel);

			ExpressionInnerPanel arg = new ExpressionInnerPanel(exp.getArgument()){
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};
			
			c.weightx = 1.0;               
			c.weighty = 1.0;               
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(arg, c);
			add(arg);

			isHorizontallyLayouted = true;
		}
		
		private void update()
		{
			ExpressionInnerPanel parent = (ExpressionInnerPanel)SwingUtilities.getAncestorOfClass(ExpressionInnerPanel.class, this);
			if ((parent != null) && parent.isHorizontallyLayouted && (parent._representedExpression.getDepth() > 1)) {
				parent.update();
				return;
			}
			removeFocusListeners();
			removeAll();
			
			if (_representedExpression instanceof SymbolicConstant) {
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				setLayout(gridbag);
				JLabel symbolicConstantLabel = new JLabel(((SymbolicConstant)_representedExpression).getSymbol());
				c.weightx = 0.0;               
				c.weighty = 1.0;               
				c.anchor = GridBagConstraints.NORTH;
		        c.fill = GridBagConstraints.NONE;
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridbag.setConstraints(symbolicConstantLabel, c);
				add(symbolicConstantLabel);
			}
			
			/*else if (_representedExpression instanceof Variable || _representedExpression instanceof Constant) {
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				setLayout(gridbag);
				variableOrConstantTextField = new JTextField();
				variableOrConstantTextField.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						textChanged();
					}
				});
				variableOrConstantTextField.addFocusListener(new FocusAdapter(){
					public void focusLost(FocusEvent e) {
						textChanged();
					}
				});
				c.weightx = 1.0;               
				c.weighty = 1.0;               
				c.anchor = GridBagConstraints.NORTH;
		        c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridbag.setConstraints(variableOrConstantTextField, c);
				add(variableOrConstantTextField);

				if (_representedExpression instanceof Variable) {
					variableOrConstantTextField.setText(((Variable)_representedExpression).getName());
				}
				else if (_representedExpression instanceof Constant) {
					variableOrConstantTextField.setText(((Constant)_representedExpression).toString());
				}
			}*/

			else if ((_representedExpression instanceof BindingExpression.BindingValueVariable) 
					|| (_representedExpression instanceof BindingExpression.BindingValueFunction)
					|| (_representedExpression instanceof BindingExpression.BindingValueConstant)) {
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				setLayout(gridbag);
				AbstractBinding binding = null;
				if (_representedExpression instanceof BindingExpression.BindingValueVariable) {
					binding = ((BindingExpression.BindingValueVariable)_representedExpression).getBindingValue();
				}
				else if (_representedExpression instanceof BindingExpression.BindingValueFunction) {
					binding = ((BindingExpression.BindingValueFunction)_representedExpression).getBindingValue();
				}
				else if (_representedExpression instanceof BindingExpression.BindingValueConstant) {
					binding = ((BindingExpression.BindingValueConstant)_representedExpression).getStaticBinding();
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.fine ("Building BindingSelector with "+binding);
				}
				_bindingSelector = new BindingSelector(binding) {
					@Override
					public void apply() {
						super.apply();
						AbstractBinding newEditedBinding = getEditedObject();
						if (newEditedBinding instanceof StaticBinding) {
							setRepresentedExpression(new BindingValueConstant((StaticBinding)newEditedBinding));
						}
						else if (newEditedBinding instanceof BindingValue) {
							setRepresentedExpression(new BindingValueVariable((BindingValue)newEditedBinding));
						}
						else if (newEditedBinding instanceof BindingExpression) {
							setRepresentedExpression(((BindingExpression)newEditedBinding).getExpression());
						}
					}
					@Override
					public void cancel() {
						super.cancel();
					}
					@Override
					public Dimension getPreferredSize() {
						Dimension parentDim = super.getPreferredSize();
						return new Dimension(100,parentDim.height);
					}
				};
				
				/*if (_representedExpression instanceof BindingExpression.BindingValueVariable
						&& (binding==null || !binding.isBindingValid())) {
					// This binding could not be resolved, just set text
					_bindingSelector.getTextField().setText(((BindingExpression.BindingValueVariable)_representedExpression).getVariable().getName());
					System.out.println("setting textfield to be "+((BindingExpression.BindingValueVariable)_representedExpression).getVariable().getName());
				}*/
				
				if (binding != null) {
					_bindingSelector.setBindingDefinition(binding.getBindingDefinition());
				}
				else {
					_bindingSelector.setBindingDefinition(new BindingDefinition("common",Object.class,BindingDefinitionType.GET, true));
				}
				_bindingSelector.setBindable(_bindingExpression);
				
				//_bindingSelector.setEditedObject(binding);
				if (binding != null) {
					_bindingSelector.setRevertValue(binding.clone());
				}
				/*variableOrConstantTextField = new JTextField();
				variableOrConstantTextField.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						textChanged();
					}
				});
				variableOrConstantTextField.addFocusListener(new FocusAdapter(){
					public void focusLost(FocusEvent e) {
						textChanged();
					}
				});*/
				c.weightx = 1.0;               
				c.weighty = 1.0;               
				c.anchor = GridBagConstraints.NORTH;
		        c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridbag.setConstraints(_bindingSelector, c);
				add(_bindingSelector);

				/*if (_representedExpression instanceof BindingValueVariable) {
					variableOrConstantTextField.setText(((BindingValueVariable)_representedExpression).getName());
				}
				else if (_representedExpression instanceof BindingValueConstant) {
					variableOrConstantTextField.setText(((BindingValueConstant)_representedExpression).toString());
				}*/
			}

			else if (_representedExpression instanceof BinaryOperatorExpression) {
				if (_representedExpression.getDepth() > 1) {
					addBinaryExpressionVerticalLayout();
				}
				else {
					addBinaryExpressionHorizontalLayout();
				}
			}
			
			else if (_representedExpression instanceof UnaryOperatorExpression) {
				addUnaryExpressionHorizontalLayout();
			}

			addFocusListeners();
			revalidate();
			repaint();
		}

		public Expression getRepresentedExpression() {
			return _representedExpression;
		}

		public void setRepresentedExpression(Expression representedExpression) {
			_representedExpression = representedExpression;
			representedExpressionChanged(representedExpression);
			update();
			updateInfos();
		}
		
		private void updateInfos()
		{
			_checkEditedExpression();
			updateExpressionTextArea();
			updateAdditionalInformations();			
		}
		
		public abstract void representedExpressionChanged(Expression newExpression);
	}
	
	protected void appendBinaryOperator(BinaryOperator operator) 
	{
		System.out.println("appendBinaryOperator "+operator);
		if (focusReceiver != null) {
			BindingValueVariable variable = new BindingValueVariable("",_bindingExpression);
			Expression newExpression = new BinaryOperatorExpression(operator,focusReceiver.getRepresentedExpression(),variable);
			/*logger.info("variable="+variable.getBindingValue());
			logger.info("owner="+variable.getBindingValue().getOwner());
			logger.info("bd="+variable.getBindingValue().getBindingDefinition());*/
			focusReceiver.setRepresentedExpression(newExpression);
		}
	}

	protected void appendUnaryOperator(UnaryOperator operator) 
	{
		System.out.println("appendUnaryOperator "+operator);
		if (focusReceiver != null) {
			Expression newExpression = new UnaryOperatorExpression(operator,focusReceiver.getRepresentedExpression());
			focusReceiver.setRepresentedExpression(newExpression);
		}
	}
	
	@Override
	public void focusGained(FocusEvent e)
	{
		focusReceiver = (ExpressionInnerPanel)SwingUtilities.getAncestorOfClass(ExpressionInnerPanel.class, (Component)e.getSource());
		if (focusReceiver != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Focus gained by expression "+focusReceiver.getRepresentedExpression()+" receiver="+focusReceiver);
			}
		}
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		// Dont care
		ExpressionInnerPanel whoLoseFocus = (ExpressionInnerPanel)SwingUtilities.getAncestorOfClass(ExpressionInnerPanel.class, (Component)e.getSource());
		if (whoLoseFocus != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Focus lost by expression "+whoLoseFocus.getRepresentedExpression()+" looser="+whoLoseFocus);
			}
		}
	}
	
}
