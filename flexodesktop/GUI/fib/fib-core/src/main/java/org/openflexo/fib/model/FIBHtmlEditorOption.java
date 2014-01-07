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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.fib.model.FIBHtmlEditor.FIBHtmlEditorImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

import com.metaphaseeditor.MetaphaseEditorConfiguration.MetaphaseEditorOption;

@ModelEntity
@ImplementationClass(FIBHtmlEditorOption.FIBHtmlEditorOptionImpl.class)
@XMLElement(xmlTag = "Option")
public interface FIBHtmlEditorOption extends FIBModelObject {

	@PropertyIdentifier(type = int.class)
	public static final String INDEX_KEY = "index";

	@PropertyIdentifier(type = boolean.class)
	public static final String IS_VISIBLE_KEY = "isVisible";

	@PropertyIdentifier(type = FIBHtmlEditor.class)
	public static final String EDITOR_KEY = "editor";

	@Getter(value = INDEX_KEY, defaultValue = "0")
	@XMLAttribute
	public int getIndex();

	@Setter(INDEX_KEY)
	public void setIndex(int index);

	@Deprecated
	public void setIndexNoEditorNotification(int index);

	@Getter(value = IS_VISIBLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsVisible();

	@Setter(IS_VISIBLE_KEY)
	public void setIsVisible(boolean isVisible);

	@Getter(value = EDITOR_KEY)
	public FIBHtmlEditor getEditor();

	@Setter(EDITOR_KEY)
	public void setEditor(FIBHtmlEditor editor);

	public List<FIBHtmlEditorOption> getSubOptions();

	public int getLevel();

	public MetaphaseEditorOption makeMetaphaseEditorOption(int line);

	public static abstract class FIBHtmlEditorOptionImpl extends FIBModelObjectImpl implements FIBHtmlEditorOption {

		private FIBHtmlEditor editor;

		private boolean isVisible;
		private int index;
		private final Vector<String> subOptions;
		private int level;

		public FIBHtmlEditorOptionImpl() {
			isVisible = false;
			index = -1;
			subOptions = new Vector<String>();
		}

		@Override
		public void setName(String optionName) {
			performSuperSetter(NAME_KEY, optionName);
			level = getLevel(optionName);
			if (index == -1) {
				index = retrieveIndex(optionName);
			}
			for (String s : FIBHtmlEditorImpl.option_keys) {
				if (s.startsWith(optionName) && !s.equals(optionName)) {
					subOptions.add(s);
				}
			}
		}

		public FIBHtmlEditorOptionImpl(String optionName, FIBHtmlEditor editor) {
			this();
			this.editor = editor;
			setName(optionName);
		}

		@Override
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

			for (String s : FIBHtmlEditorImpl.option_keys) {
				if (s.equals(optionName)) {
					return index;
				}
				if (getLevel(s) == level) {
					index++;
				} else if (optionName.startsWith(s)) {
					index = 0;
				}
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
		public FIBComponent getComponent() {
			if (getFIBHtmlEditor() != null) {
				return getFIBHtmlEditor().getComponent();
			}
			return null;
		}

		@Override
		public boolean getIsVisible() {
			return isVisible;
		}

		@Override
		public void setIsVisible(boolean isVisible) {
			FIBPropertyNotification<Boolean> notification = requireChange(IS_VISIBLE_KEY, isVisible);
			if (notification != null) {
				this.isVisible = isVisible;
				hasChanged(notification);
				if (editor != null) {
					for (String o : subOptions) {
						// System.out.println("Also do setIsVisible for "+o);
						FIBHtmlEditorOption option = editor.getOption(o);
						if (option != null) {
							option.setIsVisible(isVisible);
						}
					}
					if (isVisible) {
						if (getLevel() == 0) {
							if (!editor.anyLineContains(this) && !editor.getVisibleAndUnusedOptions().contains(this)) {
								// System.out.println("For "+this+" add to visible and unused");
								editor.addToVisibleAndUnusedOptions(this);
							}
						} else {
							if (getParentOption() != null) {
								if (editor.getOptionsInLine1().contains(getParentOption())) {
									editor.addToOptionsInLine1(this);
								}
								if (editor.getOptionsInLine2().contains(getParentOption())) {
									editor.addToOptionsInLine2(this);
								}
								if (editor.getOptionsInLine3().contains(getParentOption())) {
									editor.addToOptionsInLine3(this);
								}
							}
						}
					} else {
						if (editor.getOptionsInLine1().contains(this)) {
							editor.removeFromOptionsInLine1(this);
						}
						if (editor.getOptionsInLine2().contains(this)) {
							editor.removeFromOptionsInLine2(this);
						}
						if (editor.getOptionsInLine3().contains(this)) {
							editor.removeFromOptionsInLine3(this);
						}
						if (editor.getVisibleAndUnusedOptions().contains(this)) {
							editor.removeFromVisibleAndUnusedOptions(this);
						}
					}
				}
			}
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public void setIndex(int index) {
			FIBPropertyNotification<Integer> notification = requireChange(INDEX_KEY, index);
			if (notification != null) {
				this.index = index;
				hasChanged(notification);
				if (editor != null) {
					editor.indexChanged();
				}
			}
		}

		@Override
		public void setIndexNoEditorNotification(int index) {
			FIBPropertyNotification<Integer> notification = requireChange(INDEX_KEY, index);
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

		@Override
		public List<FIBHtmlEditorOption> getSubOptions() {
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
				if (editor.getOptionsInLine1().contains(this)) {
					return 1;
				}
				if (editor.getOptionsInLine2().contains(this)) {
					return 2;
				}
				if (editor.getOptionsInLine3().contains(this)) {
					return 3;
				}
			}
			return -1;
		}

		@Override
		public MetaphaseEditorOption makeMetaphaseEditorOption(int line) {
			return new MetaphaseEditorOption(getName(), index, line);
		}

		@Override
		public FIBHtmlEditor getEditor() {
			return editor;
		}

		@Override
		public void setEditor(FIBHtmlEditor editor) {
			this.editor = editor;
		}

	}
}
