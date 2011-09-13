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
package org.openflexo.swing;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * This class provides a common way to create and maintain a panel containing JButton stored with
 * a FlowLayout. Support provided by this class are mainly focus traversable policy management. Buttons
 * stored in this panel are automatically managed according to a common focus management scheme which could
 * be exported to parent components with <pre>applyFocusTraversablePolicyTo(Container,boolean)</pre>
 * 
 * @author sylvain
 *
 */
public class ButtonsControlPanel extends JPanel {

	Vector<JButton> _buttons;
	
	public ButtonsControlPanel()
	{
		super();
		setLayout(new FlowLayout());
		_buttons = new Vector<JButton>();
        setFocusTraversalPolicyProvider(true);
        setFocusTraversalPolicy(new ButtonsControlPanelFocusTraversalPolicy());

	}
	
	public JButton addButton(String buttonName, ActionListener listener)
	{
		final JButton returned = new JButton();
		returned.setText(localizedForKeyAndButton(buttonName, returned));
		returned.addActionListener(listener);
		returned.setFocusable(true);
		returned.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				returned.setSelected(true);
			}
			@Override
			public void focusLost(FocusEvent e) {
				returned.setSelected(false);
			}			
		});
		returned.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					returned.doClick();
				}
			}
		});
		_buttons.add(returned);
		add(returned);
		return returned;
	}
	
	@Override
	public void remove(Component comp)
	{
		super.remove(comp);
		if (_buttons.contains(comp)) _buttons.remove(comp);
	}
	
	public void requestFocusInFirstButton()
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Component button = getFocusTraversalPolicy().getFirstComponent(ButtonsControlPanel.this);
				if (button != null) {
					button.requestFocus();
				}
			}
		});
	}

	public void applyFocusTraversablePolicyTo(Container container, boolean requestFocus)
	{
		
		container.setFocusTraversalPolicyProvider(true);
		container.setFocusTraversalPolicy(getFocusTraversalPolicy());
		
		Set<AWTKeyStroke> set = container.getFocusTraversalKeys  ( KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS ) ; 
		set = new HashSet<AWTKeyStroke>(set) ; 
		KeyStroke right = KeyStroke.getKeyStroke ("RIGHT"); 
		set.add(right); 
		KeyStroke down = KeyStroke.getKeyStroke ("DOWN"); 
		set.add(down) ; 
		KeyStroke tab = KeyStroke.getKeyStroke("TAB"); 
		set.add(tab) ; 
		container.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set); 
		set = container.getFocusTraversalKeys  ( KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS ) ; 
		set = new HashSet<AWTKeyStroke>(set) ; 
		KeyStroke left = KeyStroke.getKeyStroke("LEFT") ; 
		set.add(left); 
		KeyStroke up = KeyStroke.getKeyStroke("UP") ; 
		set.add(up); 
		container.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set);

		if (requestFocus) requestFocusInFirstButton();

	}

	class ButtonsControlPanelFocusTraversalPolicy extends FocusTraversalPolicy
	{

		@Override
		public Component getComponentAfter(Container aContainer, Component aComponent) 
		{
			if (allButtonsAreDisabled()) return null;
			JButton returned = null;
			int index = _buttons.indexOf(aComponent);
			if (index > -1) {
				int returnedIndex = index+1;
				if (returnedIndex == _buttons.size()) returnedIndex = 0;
				returned = _buttons.elementAt(returnedIndex);
			}
			if (returned == null) return null;
			if (returned.isEnabled()) return returned;
			else return getComponentAfter(aContainer,returned);
		}

		@Override
		public Component getComponentBefore(Container aContainer, Component aComponent) 
		{
			if (allButtonsAreDisabled()) return null;
			JButton returned = null;
			int index = _buttons.indexOf(aComponent);
			if (index > -1) {
				int returnedIndex = index-1;
				if (returnedIndex == -1) returnedIndex = _buttons.size()-1;
				returned = _buttons.elementAt(returnedIndex);
			}
			if (returned == null) return null;
			if (returned.isEnabled()) return returned;
			else return getComponentBefore(aContainer,returned);
		}

		@Override
		public Component getDefaultComponent(Container aContainer) 
		{
			return getFirstComponent(aContainer);
		}

		@Override
		public Component getFirstComponent(Container aContainer) 
		{
			if (allButtonsAreDisabled()) return null;
			JButton returned = null;
			if (_buttons.size() > 0) returned = _buttons.firstElement();
			if (returned == null) return null;
			if (returned.isEnabled()) return returned;
			else return getComponentAfter(aContainer,returned);
		}

		@Override
		public Component getLastComponent(Container aContainer) 
		{
			if (allButtonsAreDisabled()) return null;
			JButton returned = null;
			if (_buttons.size() > 0) returned = _buttons.lastElement();
			if (returned == null) return null;
			if (returned.isEnabled()) return returned;
			else return getComponentBefore(aContainer,returned);
		}
		
		private boolean allButtonsAreDisabled()
		{
			for (JButton b : _buttons) if (b.isEnabled()) return false;
			return true;
		}
		
	}
	
	// Override if required
	public String localizedForKeyAndButton(String key, JButton component)
	{
		return key;
	}
}
