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
package org.openflexo.fib.editor.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.logging.FlexoLogger;

public abstract class PlaceHolder extends JPanel {

	static final Logger logger = FlexoLogger.getLogger(PlaceHolder.class.getPackage().getName());

	private Border focusBorder = BorderFactory.createLineBorder(Color.RED);
	private Border nonFocusBorder = BorderFactory.createEtchedBorder();

	private boolean isFocused = false;
	
	private FIBEditableView view;
	private String text;
	
	public PlaceHolder(FIBEditableView view, String text) 
	{
		super(new BorderLayout());
		this.view = view;
		this.text = text;
		JLabel label = new JLabel(text);
		label.setForeground(Color.DARK_GRAY);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		add(label,BorderLayout.CENTER);
		setBorder(BorderFactory.createEtchedBorder());
	}
	
	public void setFocused(boolean aFlag)
	{
		if (aFlag) {
			isFocused = true;
			setBorder(BorderFactory.createCompoundBorder(focusBorder, nonFocusBorder));
		}
		else {
			isFocused = false;
			setBorder(nonFocusBorder);
		}
	}
	
	public boolean isFocused() 
	{
		return isFocused;
	}

	public FIBEditableView getView() 
	{
		return view;
	}

	@Override
	public String toString() 
	{
		return "PlaceHolder:["+text+"]";
	}

	public abstract void insertComponent(FIBComponent newComponent);

	public void willDelete() 
	{
		getView().getJComponent().remove(this);
		getView().getPlaceHolders().remove(this);
	}

	public void hasDeleted() 
	{
		/*if (getView().getJComponent() instanceof JPanel && ((JPanel)getView().getJComponent()).getLayout() instanceof BorderLayout) {
			System.out.println("Bon, qu'est ce qu'on a la ?");
			BorderLayout bl = (BorderLayout)(((JPanel)getView().getJComponent()).getLayout());
			for (Component c : getView().getJComponent().getComponents()) {
				System.out.println("> Hop: "+c+" "+bl.getConstraints(c));
			}
		}*/
		
		
	}

}
