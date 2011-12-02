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
package org.openflexo.search.view;

import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * This class is a utility class to make a JTextComponent useable with the search.
 * 
 * @author gpolet
 * 
 */
public class TextComponentAdapater implements ITextComponent {

	private JTextComponent textComponent;

	public TextComponentAdapater(JTextComponent component) {
		this.textComponent = component;
	}

	@Override
	public void addCaretListener(CaretListener listener) {
		textComponent.addCaretListener(listener);
	}

	@Override
	public int getCaretPosition() {
		return textComponent.getCaretPosition();
	}

	@Override
	public boolean isEditable() {
		return textComponent.isEditable();
	}

	@Override
	public void removeCaretListener(CaretListener listener) {
		textComponent.removeCaretListener(listener);
	}

	@Override
	public void select(int start, int end) {
		textComponent.select(start, end);
	}

	@Override
	public Document getDocument() {
		return textComponent.getDocument();
	}

	@Override
	public String getSelectedText() {
		return textComponent.getSelectedText();
	}

	@Override
	public int getSelectionEnd() {
		return textComponent.getSelectionEnd();
	}

	@Override
	public int getSelectionStart() {
		return textComponent.getSelectionStart();
	}

}
