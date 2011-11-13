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
package org.openflexo.fib.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.fib.model.FIBImage.Parameters;
import org.openflexo.fib.model.FIBModelObject.FIBModelAttribute;

import com.metaphaseeditor.MetaphaseEditorConfiguration.MetaphaseEditorOption;

public class FIBHtmlEditorOption extends FIBModelObject {

	public static enum Parameters implements FIBModelAttribute {
		isVisible, index
	}

	private FIBHtmlEditor editor;

	private boolean isVisible;
	private int index;
	private Vector<String> subOptions;
	private int level;

	public FIBHtmlEditorOption() {
		isVisible = false;
		index = -1;
		subOptions = new Vector<String>();
	}

	@Override
	public void setName(String optionName) {
		super.setName(optionName);
		level = getLevel(optionName);
		if (index == -1)
			index = retrieveIndex(optionName);
		for (String s : FIBHtmlEditor.option_keys) {
			if (s.startsWith(optionName) && !s.equals(optionName)) {
				subOptions.add(s);
			}
		}
	}

	public FIBHtmlEditorOption(String optionName, FIBHtmlEditor editor) {
		this();
		this.editor = editor;
		setName(optionName);
	}

	public int getLevel() {
		return level;
	}

	private static int getLevel(String optionName) {
		int returned = 0;
		String s = optionName;
		while (s.indexOf(".") > -1) {
			returned++;
			try {
				s = s.substring(s.indexOf(".") + 1);
			} catch (ArrayIndexOutOfBoundsException e) {
				return returned;
			}
		}
		return returned;
	}

	protected int retrieveIndex(String optionName) {
		int index = 0;
		int level = getLevel(optionName);

		for (String s : FIBHtmlEditor.option_keys) {
			if (s.equals(optionName))
				return index;
			if (getLevel(s) == level)
				index++;
			else if (optionName.startsWith(s))
				index = 0;
		}
		return -1;
	}

	public FIBHtmlEditor getFIBHtmlEditor() {
		return editor;
	}

	public void setFIBHtmlEditor(FIBHtmlEditor editor) {
		this.editor = editor;
	}

	@Override
	public FIBComponent getRootComponent() {
		if (getFIBHtmlEditor() != null)
			return getFIBHtmlEditor().getRootComponent();
		return null;
	}

	public boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(boolean isVisible) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.isVisible, isVisible);
		if (notification != null) {
			this.isVisible = isVisible;
			hasChanged(notification);
			if (editor != null) {
				for (String o : subOptions) {
					// System.out.println("Also do setIsVisible for "+o);
					FIBHtmlEditorOption option = editor.getOption(o);
					if (option != null)
						option.setIsVisible(isVisible);
				}
				if (isVisible) {
					if (getLevel() == 0) {
						if (!editor.anyLineContains(this) && !editor.getVisibleAndUnusedOptions().contains(this)) {
							// System.out.println("For "+this+" add to visible and unused");
							editor.addToVisibleAndUnusedOptions(this);
						}
					} else {
						if (getParentOption() != null) {
							if (editor.getOptionsInLine1().contains(getParentOption()))
								editor.addToOptionsInLine1(this);
							if (editor.getOptionsInLine2().contains(getParentOption()))
								editor.addToOptionsInLine2(this);
							if (editor.getOptionsInLine3().contains(getParentOption()))
								editor.addToOptionsInLine3(this);
						}
					}
				} else {
					if (editor.getOptionsInLine1().contains(this))
						editor.removeFromOptionsInLine1(this);
					if (editor.getOptionsInLine2().contains(this))
						editor.removeFromOptionsInLine2(this);
					if (editor.getOptionsInLine3().contains(this))
						editor.removeFromOptionsInLine3(this);
					if (editor.getVisibleAndUnusedOptions().contains(this))
						editor.removeFromVisibleAndUnusedOptions(this);
				}
			}
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.index, index);
		if (notification != null) {
			this.index = index;
			hasChanged(notification);
			if (editor != null)
				editor.indexChanged();
		}
	}

	public void setIndexNoEditorNotification(int index) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.index, index);
		if (notification != null) {
			this.index = index;
			hasChanged(notification);
		}
	}

	@Override
	public String toString() {
		return "FIBHtmlEditorOption[" + getName() + "]";
	}

	protected FIBHtmlEditorOption getParentOption() {
		if (getLevel() > 0 && editor != null) {
			String parentOptionName = getName().substring(0, getName().lastIndexOf("."));
			return editor.getOption(parentOptionName);
		}
		return null;
	}

	protected List<FIBHtmlEditorOption> getSubOptions() {
		ArrayList<FIBHtmlEditorOption> returned = new ArrayList<FIBHtmlEditorOption>();
		if (editor != null) {
			for (String s : subOptions) {
				returned.add(editor.getOption(s));
			}
		}
		return returned;
	}

	protected int getLine() {
		if (editor != null) {
			if (editor.getOptionsInLine1().contains(this))
				return 1;
			if (editor.getOptionsInLine2().contains(this))
				return 2;
			if (editor.getOptionsInLine3().contains(this))
				return 3;
		}
		return -1;
	}

	public MetaphaseEditorOption makeMetaphaseEditorOption(int line) {
		return new MetaphaseEditorOption(getName(), index, line);
	}
}
