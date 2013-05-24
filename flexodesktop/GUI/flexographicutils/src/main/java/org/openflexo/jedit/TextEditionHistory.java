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
package org.openflexo.jedit;

import java.util.LinkedList;

public class TextEditionHistory {
	public static int MAX_UNDO_LEVELS = 10;

	private JEditTextArea _textArea;
	private LinkedList<String> list;
	private int maxUndoLevels;
	private int index;

	protected TextEditionHistory(JEditTextArea textArea) {
		super();
		_textArea = textArea;
		list = new LinkedList<String>();
		maxUndoLevels = MAX_UNDO_LEVELS;
		index = -1;
	}

	public void retain() {
		if (index > -1 && list.size() - 1 > index) {
			while (index < list.size() - 1) {
				list.removeLast();
			}
		}
		while (list.size() >= maxUndoLevels) {
			list.remove();
		}
		list.add(_textArea.getText());
		index++;
	}

	public boolean isUndoable() {
		return index > 0;
	}

	public void undo() {
		if (isUndoable()) {
			index--;
			_textArea.setText(list.get(index));
		}
	}

	public boolean isRedoable() {
		return index < list.size() - 1;
	}

	public void redo() {
		if (isRedoable()) {
			index++;
			_textArea.setText(list.get(index));
		}
	}
}
