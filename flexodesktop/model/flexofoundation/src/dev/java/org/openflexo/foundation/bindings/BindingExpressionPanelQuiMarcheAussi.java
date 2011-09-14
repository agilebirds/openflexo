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
package org.openflexo.foundation.bindings;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.openflexo.antar.expr.ArithmeticBinaryOperator;
import org.openflexo.antar.expr.ArithmeticUnaryOperator;
import org.openflexo.antar.expr.BinaryOperator;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BooleanBinaryOperator;
import org.openflexo.antar.expr.BooleanUnaryOperator;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Operator;
import org.openflexo.antar.expr.OperatorNotSupportedException;
import org.openflexo.antar.expr.UnaryOperator;
import org.openflexo.antar.expr.UnaryOperatorExpression;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.antar.java.JavaExpressionParser;
import org.openflexo.antar.java.JavaExpressionPrettyPrinter;
import org.openflexo.antar.java.JavaGrammar;
import org.openflexo.toolbox.ImageIconResource;


public class BindingExpressionPanelQuiMarcheAussi extends JPanel implements FocusListener {

	Expression _expression;
	
	public static final ImageIcon OK_ICON = new ImageIconResource("Resources/Flexo/OK.gif");
	public static final ImageIcon WARNING_ICON = new ImageIconResource("Resources/Flexo/Warning.gif");
	public static final ImageIcon ERROR_ICON = new ImageIconResource("Resources/Flexo/Error.gif");

	public static final ImageIcon DIVISION_ICON = new ImageIconResource("Resources/Flexo/Operators/Divider.gif");
	public static final ImageIcon MULTIPLICATION_ICON = new ImageIconResource("Resources/Flexo/Operators/Multiplication.gif");
	public static final ImageIcon ADDITION_ICON = new ImageIconResource("Resources/Flexo/Operators/Addition.gif");
	public static final ImageIcon SUBSTRACTION_ICON = new ImageIconResource("Resources/Flexo/Operators/Substraction.gif");
	public static final ImageIcon EQUALS_ICON = new ImageIconResource("Resources/Flexo/Operators/Equals.gif");
	public static final ImageIcon NOT_EQUALS_ICON = new ImageIconResource("Resources/Flexo/Operators/NotEquals.gif");
	public static final ImageIcon LESS_THAN_ICON = new ImageIconResource("Resources/Flexo/Operators/LessThan.gif");
	public static final ImageIcon LESS_THAN_OR_EQUALS_ICON = new ImageIconResource("Resources/Flexo/Operators/LessThanOrEquals.gif");
	public static final ImageIcon GREATER_THAN_ICON = new ImageIconResource("Resources/Flexo/Operators/GreaterThan.gif");
	public static final ImageIcon GREATER_THAN_OR_EQUALS_ICON = new ImageIconResource("Resources/Flexo/Operators/GreaterThanOrEquals.gif");
	public static final ImageIcon AND_ICON = new ImageIconResource("Resources/Flexo/Operators/AND.gif");
	public static final ImageIcon OR_ICON = new ImageIconResource("Resources/Flexo/Operators/OR.gif");
	public static final ImageIcon NOT_ICON = new ImageIconResource("Resources/Flexo/Operators/NOT.gif");

	protected static ImageIcon iconForOperator(Operator op)
	{
		if (op == ArithmeticBinaryOperator.ADDITION) return ADDITION_ICON;
		else if (op == ArithmeticBinaryOperator.SUBSTRACTION) return SUBSTRACTION_ICON;
		else if (op == ArithmeticBinaryOperator.MULTIPLICATION) return MULTIPLICATION_ICON;
		else if (op == ArithmeticBinaryOperator.DIVISION) return DIVISION_ICON;
		else if (op == BooleanBinaryOperator.EQUALS) return EQUALS_ICON;
		else if (op == BooleanBinaryOperator.NOT_EQUALS) return NOT_EQUALS_ICON;
		else if (op == BooleanBinaryOperator.LESS_THAN) return LESS_THAN_ICON;
		else if (op == BooleanBinaryOperator.LESS_THAN_OR_EQUALS) return LESS_THAN_OR_EQUALS_ICON;
		else if (op == BooleanBinaryOperator.GREATER_THAN) return GREATER_THAN_ICON;
		else if (op == BooleanBinaryOperator.GREATER_THAN_OR_EQUALS) return GREATER_THAN_OR_EQUALS_ICON;
		else if (op == BooleanBinaryOperator.AND) return AND_ICON;
		else if (op == BooleanBinaryOperator.OR) return OR_ICON;
		else if (op == BooleanUnaryOperator.NOT) return NOT_ICON;
		else if (op == ArithmeticUnaryOperator.UNARY_MINUS) return SUBSTRACTION_ICON;
		return null;
	}
	
	public BindingExpressionPanelQuiMarcheAussi(Expression expression)
	{
		super();
		setLayout(new BorderLayout());
		_setEditedExpression(expression);
		init();
	}
	
	private JTextArea expressionTA;
	private JPanel controls;
	private ExpressionInnerPanel rootExpressionPanel;
	
	public void setEditedExpression(Expression expression)
	{
		// NE pas faire ca, sinon on a un soucis de reference
		//if (expression == null || !expression.equals(_expression)) {
			_setEditedExpression(expression);
			update();
		//}
	}
	
	private void _setEditedExpression(Expression expression)
	{
		_expression = expression;
		_checkEditedExpression();
	}
	
	protected void _checkEditedExpression()
	{
		System.out.println("Je regarde... l'expression "+pp.getStringRepresentation(_expression));
		
		Operator undefinedOperator = null;
		if (expressionIsUndefined(_expression)) {
			status = ExpressionParsingStatus.UNDEFINED;
			message = UNDEFINED_EXPRESSION_MESSAGE;
		}
		else if ((undefinedOperator = firstOperatorWithUndefinedOperand(_expression)) != null) {
			status = ExpressionParsingStatus.INVALID;
			try {
				message = UNDEFINED_OPERAND_FOR_OPERATOR+" "+ pp.getSymbol(undefinedOperator);
			} catch (OperatorNotSupportedException e) {
				message = UNDEFINED_OPERAND_FOR_OPERATOR+" ?";
			}
		}
		else {
			status = ExpressionParsingStatus.VALID;
			message = VALID_EXPRESSION;
		}
	}
	
	private boolean expressionIsUndefined(Expression expression) 
	{
		return ((expression instanceof Variable)
				&& ((Variable)expression).getName().trim().equals(""));
	}
	
	private Operator firstOperatorWithUndefinedOperand(Expression expression)
	{
		if (expression instanceof Variable) return null;
		else if (expression instanceof Constant) return null;
		else if (expression instanceof BinaryOperatorExpression) {
			Expression leftOperand = ((BinaryOperatorExpression)expression).getLeftArgument();
			if (expressionIsUndefined(leftOperand)) return ((BinaryOperatorExpression)expression).getOperator();
			Operator returned = firstOperatorWithUndefinedOperand(leftOperand);
			if (returned == null) {
				Expression rightOperand = ((BinaryOperatorExpression)expression).getRightArgument();
				if (expressionIsUndefined(rightOperand)) return ((BinaryOperatorExpression)expression).getOperator();
				return firstOperatorWithUndefinedOperand(rightOperand);
			}
		}
		else if (expression instanceof UnaryOperatorExpression) {
			Expression operand = ((UnaryOperatorExpression)expression).getArgument();
			if (expressionIsUndefined(operand)) return ((UnaryOperatorExpression)expression).getOperator();
			return firstOperatorWithUndefinedOperand(operand);
		}
		return null;
	}
	
	public Expression getEditedExpression()
	{
		return _expression;
	}
	
	protected void expressionMayHaveBeenEdited() 
	{
		System.out.println("expressionMayHaveBeenEdited() ici");
		if (expressionTA.getText().trim().equals("")) {
			//message = UNDEFINED_EXPRESSION_MESSAGE;
			//status = ExpressionParsingStatus.UNDEFINED;
			Expression newExpression = new Variable("");
			_setEditedExpression(newExpression);
			rootExpressionPanel.setRepresentedExpression(_expression);
			update();
		}
		else {
			try {
				Expression newExpression;
				if (expressionTA.getText().trim().equals("")) {
					newExpression = new Variable("");
				}
				else {
					newExpression = parser.parse(expressionTA.getText());
				}
				if (!newExpression.equals(_expression) || status == ExpressionParsingStatus.INVALID) {
					_setEditedExpression(newExpression);
					rootExpressionPanel.setRepresentedExpression(_expression);
					update();
				}
			} catch (ParseException e) {
				message = "ERROR: cannot parse "+expressionTA.getText();
				status = ExpressionParsingStatus.INVALID;
				updateAdditionalInformations();
			}
		}
	}
	
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
	
	private JLabel statusIcon;
	private JLabel messageLabel;
	
	private void init()
	{
		expressionTA = new JTextArea(3,50);
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
			public void focusLost(FocusEvent e) {
				expressionMayHaveBeenEdited();
			}
		});
		expressionTA.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				expressionMayHaveBeenEdited();
			}
		});

		statusIcon = new JLabel();
		messageLabel = new JLabel();
		messageLabel.setFont(new Font("SansSerif", Font.ITALIC, 9));
		
		JPanel statusAndMessageLabel = new JPanel(new FlowLayout());
		statusAndMessageLabel.add(statusIcon);
		statusAndMessageLabel.add(messageLabel);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(expressionTA,BorderLayout.NORTH);
		topPanel.add(statusAndMessageLabel,BorderLayout.WEST);
		
		add(topPanel,BorderLayout.NORTH);
		rootExpressionPanel = new ExpressionInnerPanel(_expression) {
			public void representedExpressionChanged(Expression newExpression) {
				setEditedExpression(newExpression);
			}
		};
		focusReceiver = rootExpressionPanel;
		add(new JScrollPane(rootExpressionPanel),BorderLayout.CENTER);
		controls = new JPanel();
		controls.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
		
		controls.add(createOperatorGroupPanel(
				"logical", 
				BooleanBinaryOperator.AND,
				BooleanBinaryOperator.OR,
				BooleanUnaryOperator.NOT));
		
		
		for (final BinaryOperator o : grammar.getAllSupportedBinaryOperators()) {
			JButton b = new JButton(iconForOperator(o));
			b.setBorder(BorderFactory.createEmptyBorder());
			controls.add(b);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					appendBinaryOperator(o);
				}
			});
		}
		for (final UnaryOperator o : grammar.getAllSupportedUnaryOperators()) {
			JButton b = new JButton(iconForOperator(o));
			b.setBorder(BorderFactory.createEmptyBorder());
			controls.add(b);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					appendUnaryOperator(o);
				}
			});
		}
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
			b.setToolTipText(o.getName());
			returned.add(b);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (o instanceof UnaryOperator)
						appendUnaryOperator((UnaryOperator)o);
					else if (o instanceof BinaryOperator)
						appendBinaryOperator((BinaryOperator)o);
				}
			});
		}
		
		returned.setBorder(BorderFactory.createTitledBorder(null,title,TitledBorder.CENTER,TitledBorder.TOP,new Font("SansSerif", Font.ITALIC, 8)));
		return returned;
	}
	
	protected JavaExpressionPrettyPrinter pp = new JavaExpressionPrettyPrinter();
	protected JavaExpressionParser parser = new JavaExpressionParser();
	
	protected void update()
	{
		_checkEditedExpression();
		updateExpressionTextArea();
		updateAdditionalInformations();
		revalidate();
		repaint();
	}
	
	protected void updateExpressionTextArea()
	{ 
		expressionTA.setText(pp.getStringRepresentation(_expression));
		if (status == ExpressionParsingStatus.UNDEFINED) statusIcon.setIcon(WARNING_ICON);
		else if (status == ExpressionParsingStatus.INVALID) statusIcon.setIcon(ERROR_ICON);
		else if (status == ExpressionParsingStatus.SYNTAXICALLY_VALID) statusIcon.setIcon(WARNING_ICON);
		else if (status == ExpressionParsingStatus.VALID) statusIcon.setIcon(OK_ICON);
		messageLabel.setText(message);
	}
	
	protected void updateAdditionalInformations()
	{ 
		if (status == ExpressionParsingStatus.UNDEFINED) statusIcon.setIcon(WARNING_ICON);
		else if (status == ExpressionParsingStatus.INVALID) statusIcon.setIcon(ERROR_ICON);
		else if (status == ExpressionParsingStatus.SYNTAXICALLY_VALID) statusIcon.setIcon(WARNING_ICON);
		else if (status == ExpressionParsingStatus.VALID) statusIcon.setIcon(OK_ICON);
		messageLabel.setText(message);
	}
	
	protected static enum KindOfExpression { BASIC, BINARY_OPERATOR, UNARY_OPERATOR };
	
	protected abstract class ExpressionInnerPanel extends JPanel
	{
		
		protected Expression _representedExpression;
		
		private JTextField variableOrConstantTextField;
		
		protected ExpressionInnerPanel(Expression expression)
		{
			super();
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
			c.addFocusListener(BindingExpressionPanelQuiMarcheAussi.this);
			if (c instanceof Container) {
				Container container = (Container)c;
				for (Component c2 : container.getComponents()) addFocusListenersToAllComponentsOf(c2);
			}
		}
		
		private void removeFocusListeners()
		{
			removeFocusListenersToAllComponentsOf(this);			
		}
		
		private void removeFocusListenersToAllComponentsOf(Component c)
		{
			c.removeFocusListener(BindingExpressionPanelQuiMarcheAussi.this);
			if (c instanceof Container) {
				Container container = (Container)c;
				for (Component c2 : container.getComponents()) removeFocusListenersToAllComponentsOf(c2);
			}
		}
		
		protected void textChanged()
		{
			try {
				Expression newExpression = parser.parse(variableOrConstantTextField.getText());
				setRepresentedExpression(newExpression);
				System.out.println("Text has changed for "+variableOrConstantTextField.getText()+" parsed as "+newExpression);
			} catch (ParseException e) {
				System.out.println("ERROR: cannot parse "+variableOrConstantTextField.getText());
			}
		}
		
		protected class BinaryOperatorPanel extends JPanel
		{
			private JButton currentOperatorIcon;
			//protected JComboBox currentOperatorCB;

			protected BinaryOperatorPanel(BinaryOperator operator) 
			{
				super();
				setLayout(new BorderLayout());

				currentOperatorIcon = new JButton(iconForOperator(operator));
				currentOperatorIcon.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
				currentOperatorIcon.setToolTipText(operator.getName());

				/*currentOperatorCB = new JComboBox(grammar.getAllSupportedBinaryOperators());
				currentOperatorCB.setSelectedItem(operator);
				currentOperatorCB.setRenderer(new ListCellRenderer() {
					public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
						try {
							return new JLabel(grammar.getSymbol((BinaryOperator)value));
						} catch (OperatorNotSupportedException e) {
							return new JLabel("?");
						}
					}			
				});
				currentOperatorCB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						BinaryOperatorExpression exp = (BinaryOperatorExpression)_representedExpression;
						exp.setOperator((BinaryOperator)currentOperatorCB.getSelectedItem());
						BindingExpressionPanel.this.update();
					}			
				});*/
				
				
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
			
			BinaryOperatorPanel operatorPanel = new BinaryOperatorPanel(exp.getOperator());
			
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
				public void representedExpressionChanged(Expression newExpression) {
					System.out.println("Je suis le pere, et je code "+exp);
					System.out.println("Je recois "+newExpression);
					exp.setLeftArgument(newExpression);
					System.out.println("Je suis le pere, et ca me donne "+exp);
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
				public void representedExpressionChanged(Expression newExpression) {
					exp.setRightArgument(newExpression);
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
			
			isHorizontal = false;
		}
		
		private boolean isHorizontal = true;
		
		private void addBinaryExpressionHorizontalLayout()
		{
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
		
			setLayout(gridbag);

			final BinaryOperatorExpression exp = (BinaryOperatorExpression)_representedExpression;
			final ExpressionInnerPanel me = this;

			BinaryOperatorPanel operatorPanel = new BinaryOperatorPanel(exp.getOperator());
			
			ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getLeftArgument()){
				public void representedExpressionChanged(Expression newExpression) {
					System.out.println("Je suis le pere, et je code "+exp);
					System.out.println("Je recois "+newExpression);
					exp.setLeftArgument(newExpression);
					System.out.println("Je suis le pere, et ca me donne "+exp);
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
				public void representedExpressionChanged(Expression newExpression) {
					System.out.println("RIGHT ARG pour le pere "+exp);
					System.out.println("Je recois "+newExpression);
					exp.setRightArgument(newExpression);
					System.out.println("Je suis le pere, et ca me donne "+exp);
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

		}
		
		private void update()
		{
			ExpressionInnerPanel parent = (ExpressionInnerPanel)SwingUtilities.getAncestorOfClass(ExpressionInnerPanel.class, this);
			if (parent != null && parent.isHorizontal && parent._representedExpression.getDepth() > 1) {
				System.out.println("Le parent doit etre remis bien");
				parent.update();
				return;
			}
			removeFocusListeners();
			removeAll();
			if (_representedExpression instanceof Variable || _representedExpression instanceof Constant) {
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
			}
			else if (_representedExpression instanceof BinaryOperatorExpression) {

				if (_representedExpression.getDepth() > 1) {
					addBinaryExpressionVerticalLayout();
				}
				else {
					addBinaryExpressionHorizontalLayout();
				}
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
			System.out.println("Je vais maintenant representer une expression "+representedExpression);
			System.out.println("Au niveau root, on a "+_expression);
			System.out.println("Je notifie mon pere");
			representedExpressionChanged(representedExpression);
			System.out.println("Au niveau root, on a maintenant "+_expression);
			update();

			/*
			representedExpressionChanged(representedExpression);
			_representedExpression = representedExpression;
			update();*/
			//BindingExpressionPanel.this.updateExpressionTextArea();
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
	
	protected JavaGrammar grammar = new JavaGrammar();
		
	protected void appendBinaryOperator(BinaryOperator operator) 
	{
		System.out.println("appendBinaryOperator "+operator);
		if (focusReceiver != null) {
			Expression newExpression = new BinaryOperatorExpression(operator,focusReceiver.getRepresentedExpression(),new Variable(""));
			focusReceiver.setRepresentedExpression(newExpression);
		}
	}

	protected void appendUnaryOperator(UnaryOperator operator) 
	{
	}
	
	private ExpressionInnerPanel focusReceiver = null;
	
	public void focusGained(FocusEvent e) {
		focusReceiver = (ExpressionInnerPanel)SwingUtilities.getAncestorOfClass(ExpressionInnerPanel.class, (Component)e.getSource());
		if (focusReceiver != null) System.out.println("Focus gained by expression "+focusReceiver.getRepresentedExpression());
	}
	public void focusLost(FocusEvent e) {
		// Dont care
	}
}
