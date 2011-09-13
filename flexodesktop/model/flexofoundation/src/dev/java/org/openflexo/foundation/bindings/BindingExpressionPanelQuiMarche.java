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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.openflexo.antar.expr.BinaryOperator;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.OperatorNotSupportedException;
import org.openflexo.antar.expr.UnaryOperator;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.antar.java.JavaExpressionParser;
import org.openflexo.antar.java.JavaExpressionPrettyPrinter;
import org.openflexo.antar.java.JavaGrammar;


public class BindingExpressionPanelQuiMarche extends JPanel implements FocusListener {

	Expression _expression;
	
	public BindingExpressionPanelQuiMarche(Expression expression)
	{
		super();
		setLayout(new BorderLayout());
		_expression = expression;
		init();
	}
	
	private JTextArea expressionTA;
	private JPanel controls;
	private ExpressionInnerPanel rootExpressionPanel;
	
	void expressionEdited() {
		try {
			Expression newExpression = parser.parse(expressionTA.getText());
			_expression = newExpression;
			rootExpressionPanel.setRepresentedExpression(_expression);
			update();
		} catch (ParseException e) {
			System.out.println("ERROR: cannot parse "+expressionTA.getText());
		}
	}
	
	private void init()
	{
		expressionTA = new JTextArea(3,50);
		expressionTA.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					expressionEdited();
				}
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					expressionEdited();
				}
			}
		});
		expressionTA.addFocusListener(new FocusAdapter(){
			public void focusLost(FocusEvent e) {
				expressionEdited();
			}
		});

		add(expressionTA,BorderLayout.NORTH);
		rootExpressionPanel = new ExpressionInnerPanel(_expression) {
			public void representedExpressionChanged(Expression newExpression) {
				_expression = newExpression;
				update();
			}
		};
		focusReceiver = rootExpressionPanel;
		add(rootExpressionPanel,BorderLayout.CENTER);
		controls = new JPanel();
		controls.setLayout(new FlowLayout());
		for (final BinaryOperator o : grammar.getAllSupportedBinaryOperators()) {
			JButton b;
			try {
				b = new JButton(grammar.getSymbol(o));
			} catch (OperatorNotSupportedException e1) {
				b = new JButton("?");
			}
			controls.add(b);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					appendBinaryOperator(o);
				}
			});
		}
		for (final UnaryOperator o : grammar.getAllSupportedUnaryOperators()) {
			JButton b;
			try {
				b = new JButton(grammar.getSymbol(o));
			} catch (OperatorNotSupportedException e1) {
				b = new JButton("?");
			}
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
	
	private JavaExpressionPrettyPrinter pp = new JavaExpressionPrettyPrinter();
	JavaExpressionParser parser = new JavaExpressionParser();
	
	void update()
	{
		updateExpressionTA();
		revalidate();
		repaint();
	}
	
	void updateExpressionTA()
	{
		expressionTA.setText(pp.getStringRepresentation(_expression));
	}
	
	protected static enum KindOfExpression { BASIC, BINARY_OPERATOR, UNARY_OPERATOR };
	
	protected abstract class ExpressionInnerPanel extends JPanel
	{
		
		Expression _representedExpression;
		
		private JTextField variableOrConstantTextField;
		JComboBox currentOperatorCB;

		//private int depth;
		
		protected ExpressionInnerPanel(Expression expression/*, int depth*/)
		{
			super();
			//this.depth = depth;
			_representedExpression = expression;
			update();
			addFocusListeners();
		}
		
		/*protected Color getDepthColor()
		{
			int grayLevel = 255-depth*10;
			return new Color(grayLevel,grayLevel,grayLevel);
		}*/
		
		private void addFocusListeners()
		{
			addFocusListenersToAllComponentsOf(this);			
		}
		
		private void addFocusListenersToAllComponentsOf(Component c)
		{
			c.addFocusListener(BindingExpressionPanelQuiMarche.this);
			if (c instanceof Container) {
				Container container = (Container)c;
				for (Component c2 : container.getComponents()) addFocusListenersToAllComponentsOf(c2);
			}
		}
		
		void textChanged()
		{
			System.out.println("Text has changed for "+variableOrConstantTextField.getText());
			try {
				Expression newExpression = parser.parse(variableOrConstantTextField.getText());
				setRepresentedExpression(newExpression);
			} catch (ParseException e) {
				System.out.println("ERROR: cannot parse "+variableOrConstantTextField.getText());
			}
		}
		
		private void addBinaryExpressionVerticalLayout()
		{
			setLayout(new BorderLayout());

			BinaryOperatorExpression exp = (BinaryOperatorExpression)_representedExpression;
			JPanel operatorPanel = new JPanel();
			operatorPanel.setLayout(new BorderLayout());
			//operatorPanel.setOpaque(true);
			//operatorPanel.setBackground(getDepthColor());
			//System.out.println("Color is "+getDepthColor());
			currentOperatorCB = new JComboBox(grammar.getAllSupportedBinaryOperators());
			currentOperatorCB.setSelectedItem(exp.getOperator());
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
					BindingExpressionPanelQuiMarche.this.update();
				}			
			});
			operatorPanel.add(currentOperatorCB,BorderLayout.CENTER);
			add(operatorPanel,BorderLayout.WEST);
			operatorPanel.setBorder(BorderFactory.createEtchedBorder());
			JPanel argsPanel = new JPanel();
			
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
		
			argsPanel.setLayout(gridbag);
			
			ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getLeftArgument()/*,depth+1*/){
				public void representedExpressionChanged(Expression newExpression) {
					if (_representedExpression instanceof BinaryOperatorExpression) {
						((BinaryOperatorExpression)_representedExpression).setLeftArgument(newExpression);
					}
				}
			};
			
			c.weightx = 1.0;               
			c.weighty = 1.0;               
			//c.gridwidth = 1;               
	        //c.gridheight = 1;
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.BOTH;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(leftArg, c);
			argsPanel.add(leftArg);

			ExpressionInnerPanel rightArg = new ExpressionInnerPanel(exp.getRightArgument()/*,depth+1*/){
				public void representedExpressionChanged(Expression newExpression) {
					if (_representedExpression instanceof BinaryOperatorExpression) {
						((BinaryOperatorExpression)_representedExpression).setRightArgument(newExpression);
					}
				}
			};
			
			c.weightx = 1.0;               
			c.weighty = 1.0;               
			//c.gridwidth = 1;               
	        //c.gridheight = 1;
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.BOTH;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(rightArg, c);
			argsPanel.add(rightArg);

			add(argsPanel,BorderLayout.CENTER);
			
			isHorizontal = false;
		}
		
		private boolean isHorizontal = true;
		
		private void addBinaryExpressionHorizontalLayout()
		{
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
		
			setLayout(gridbag);

			BinaryOperatorExpression exp = (BinaryOperatorExpression)_representedExpression;

			currentOperatorCB = new JComboBox(grammar.getAllSupportedBinaryOperators());
			currentOperatorCB.setSelectedItem(exp.getOperator());
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
					BindingExpressionPanelQuiMarche.this.update();
				}			
			});
				
			
			ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getLeftArgument()){
				public void representedExpressionChanged(Expression newExpression) {
					if (_representedExpression instanceof BinaryOperatorExpression) {
						((BinaryOperatorExpression)_representedExpression).setLeftArgument(newExpression);
					}
				}
			};
			
			c.weightx = 1.0;               
			c.weighty = 1.0;               
			//c.gridwidth = 1;               
	        //c.gridheight = 1;
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.BOTH;
			gridbag.setConstraints(leftArg, c);
			add(leftArg);

			c.weightx = 0.0;               
			c.weighty = 1.0;               
			//c.gridwidth = 1;               
	        //c.gridheight = 1;
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.VERTICAL;
			gridbag.setConstraints(currentOperatorCB, c);
			add(currentOperatorCB);

			ExpressionInnerPanel rightArg = new ExpressionInnerPanel(exp.getRightArgument()){
				public void representedExpressionChanged(Expression newExpression) {
					if (_representedExpression instanceof BinaryOperatorExpression) {
						((BinaryOperatorExpression)_representedExpression).setRightArgument(newExpression);
					}
				}
			};
			
			c.weightx = 1.0;               
			c.weighty = 1.0;               
			//c.gridwidth = 1;               
	        //c.gridheight = 1;
			c.anchor = GridBagConstraints.NORTH;
	        c.fill = GridBagConstraints.BOTH;
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
			removeAll();
			if (_representedExpression instanceof Variable || _representedExpression instanceof Constant) {
				setLayout(new BorderLayout());
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
				add(variableOrConstantTextField,BorderLayout.CENTER);
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
				
				/*BinaryOperatorExpression exp = (BinaryOperatorExpression)_representedExpression;
				JPanel operatorPanel = new JPanel();
				operatorPanel.setLayout(new BorderLayout());
				//operatorPanel.setOpaque(true);
				//operatorPanel.setBackground(getDepthColor());
				//System.out.println("Color is "+getDepthColor());
				currentOperatorCB = new JComboBox(grammar.getAllSupportedBinaryOperators());
				currentOperatorCB.setSelectedItem(exp.getOperator());
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
					}			
				});
				operatorPanel.add(currentOperatorCB,BorderLayout.CENTER);
				add(operatorPanel,BorderLayout.WEST);
				operatorPanel.setBorder(BorderFactory.createEtchedBorder());
				JPanel argsPanel = new JPanel();
				
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
			
				argsPanel.setLayout(gridbag);
				
				ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getLeftArgument()){
					public void representedExpressionChanged(Expression newExpression) {
						if (_representedExpression instanceof BinaryOperatorExpression) {
							((BinaryOperatorExpression)_representedExpression).setLeftArgument(newExpression);
						}
					}
				};
				
				c.weightx = 1.0;               
				c.weighty = 1.0;               
				//c.gridwidth = 1;               
		        //c.gridheight = 1;
				c.anchor = GridBagConstraints.NORTH;
		        c.fill = GridBagConstraints.BOTH;
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridbag.setConstraints(leftArg, c);
				argsPanel.add(leftArg);

				ExpressionInnerPanel rightArg = new ExpressionInnerPanel(exp.getRightArgument()){
					public void representedExpressionChanged(Expression newExpression) {
						if (_representedExpression instanceof BinaryOperatorExpression) {
							((BinaryOperatorExpression)_representedExpression).setRightArgument(newExpression);
						}
					}
				};
				
				c.weightx = 1.0;               
				c.weighty = 1.0;               
				//c.gridwidth = 1;               
		        //c.gridheight = 1;
				c.anchor = GridBagConstraints.NORTH;
		        c.fill = GridBagConstraints.BOTH;
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridbag.setConstraints(rightArg, c);
				argsPanel.add(rightArg);

				add(argsPanel,BorderLayout.CENTER);*/
			}
				else {
					
					addBinaryExpressionHorizontalLayout();
					
					/*
					BinaryOperatorExpression exp = (BinaryOperatorExpression)_representedExpression;

					currentOperatorCB = new JComboBox(grammar.getAllSupportedBinaryOperators());
					currentOperatorCB.setSelectedItem(exp.getOperator());
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
						}			
					});
						
					GridBagLayout gridbag = new GridBagLayout();
					GridBagConstraints c = new GridBagConstraints();
				
					setLayout(gridbag);
					
					ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getLeftArgument()){
						public void representedExpressionChanged(Expression newExpression) {
							if (_representedExpression instanceof BinaryOperatorExpression) {
								((BinaryOperatorExpression)_representedExpression).setLeftArgument(newExpression);
							}
						}
					};
					
					c.weightx = 1.0;               
					c.weighty = 1.0;               
					//c.gridwidth = 1;               
			        //c.gridheight = 1;
					c.anchor = GridBagConstraints.NORTH;
			        c.fill = GridBagConstraints.BOTH;
					gridbag.setConstraints(leftArg, c);
					add(leftArg);

					c.weightx = 0.0;               
					c.weighty = 1.0;               
					//c.gridwidth = 1;               
			        //c.gridheight = 1;
					c.anchor = GridBagConstraints.NORTH;
			        c.fill = GridBagConstraints.VERTICAL;
					gridbag.setConstraints(currentOperatorCB, c);
					add(currentOperatorCB);

					ExpressionInnerPanel rightArg = new ExpressionInnerPanel(exp.getRightArgument()){
						public void representedExpressionChanged(Expression newExpression) {
							if (_representedExpression instanceof BinaryOperatorExpression) {
								((BinaryOperatorExpression)_representedExpression).setRightArgument(newExpression);
							}
						}
					};
					
					c.weightx = 1.0;               
					c.weighty = 1.0;               
					//c.gridwidth = 1;               
			        //c.gridheight = 1;
					c.anchor = GridBagConstraints.NORTH;
			        c.fill = GridBagConstraints.BOTH;
					c.gridwidth = GridBagConstraints.REMAINDER;
					gridbag.setConstraints(rightArg, c);
					add(rightArg);

					*/
				}
			}
			
				
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
			BindingExpressionPanelQuiMarche.this.updateExpressionTA();
		}
		
		public abstract void representedExpressionChanged(Expression newExpression);
	}
	
	//private JTextField variableOrConstantTextField;
	
	//private JPanel operatorControl;
	//private JComboBox binaryOperatorsCB;
	//private JComboBox unaryOperatorsCB;
	//private JButton deleteButton;
	
	JavaGrammar grammar = new JavaGrammar();
	
	/*private void update()
	{
		removeAll();
		if (_expression instanceof Variable) {
			variableOrConstantTextField = new JTextField(((Variable)_expression).getName());
			add(variableOrConstantTextField,BorderLayout.CENTER);
		}
		else if (_expression instanceof Constant) {
			variableOrConstantTextField = new JTextField(((Constant)_expression).toString());
			add(variableOrConstantTextField,BorderLayout.CENTER);
		}
		else if (_expression instanceof BinaryOperatorExpression) {
			BinaryOperatorExpression exp = (BinaryOperatorExpression)_expression;
			JPanel operatorPanel = new JPanel();
			operatorPanel.setLayout(new BorderLayout());
			JComboBox currentOperatorCB = new JComboBox(grammar.getAllSupportedBinaryOperators());
			currentOperatorCB.setSelectedItem(exp.getOperator());
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
				}			
			});
			add(currentOperatorCB,BorderLayout.WEST);
			JPanel argsPanel = new JPanel();
			argsPanel.setLayout(new GridLayout(2,1));
			argsPanel.add(new BindingExpressionPanel(exp.getLeftArgument()));
			argsPanel.add(new BindingExpressionPanel(exp.getRightArgument()));
			add(argsPanel,BorderLayout.CENTER);
		}
		operatorControl = new JPanel(new FlowLayout());
		binaryOperatorsCB = new JComboBox(grammar.getAllSupportedBinaryOperators());
		binaryOperatorsCB.setRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				try {
					return new JLabel(grammar.getSymbol((BinaryOperator)value));
				} catch (OperatorNotSupportedException e) {
					return new JLabel("?");
				}
			}			
		});
		binaryOperatorsCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				appendBinaryOperator((BinaryOperator)binaryOperatorsCB.getSelectedItem());
			}			
		});
		unaryOperatorsCB = new JComboBox(grammar.getAllSupportedUnaryOperators());
		unaryOperatorsCB.setRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				try {
					return new JLabel(grammar.getSymbol((UnaryOperator)value));
				} catch (OperatorNotSupportedException e) {
					return new JLabel("?");
				}
			}			
		});
		unaryOperatorsCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				appendUnaryOperator((UnaryOperator)binaryOperatorsCB.getSelectedItem());
			}			
		});
		deleteButton = new JButton("X");
		operatorControl.add(binaryOperatorsCB);
		operatorControl.add(unaryOperatorsCB);
		operatorControl.add(deleteButton);
		
		add(operatorControl,BorderLayout.EAST);
		
		revalidate();
		repaint();
	}*/
	
	void appendBinaryOperator(BinaryOperator operator) 
	{
		System.out.println("appendBinaryOperator "+operator);
		if (focusReceiver != null) {
			Expression newExpression = new BinaryOperatorExpression(operator,focusReceiver.getRepresentedExpression(),new Variable(""));
			focusReceiver.setRepresentedExpression(newExpression);
		}
	}

	void appendUnaryOperator(UnaryOperator operator) 
	{
	}
	
	private ExpressionInnerPanel focusReceiver = null;
	
	public void focusGained(FocusEvent e) {
		focusReceiver = (ExpressionInnerPanel)SwingUtilities.getAncestorOfClass(ExpressionInnerPanel.class, (Component)e.getSource());
		System.out.println("Focus gained by expression "+focusReceiver.getRepresentedExpression());
	}
	public void focusLost(FocusEvent e) {
		// Dont care
	}
}
