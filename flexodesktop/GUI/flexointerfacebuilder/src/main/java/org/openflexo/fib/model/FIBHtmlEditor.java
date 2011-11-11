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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.metaphaseeditor.MetaphaseEditorPanel;

public class FIBHtmlEditor extends FIBWidget {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FIBHtmlEditor.class.getPackage().getName());

	protected static String[] option_keys = { MetaphaseEditorPanel.SOURCE_PANEL_KEY, MetaphaseEditorPanel.SOURCE_BUTTON_KEY,

	MetaphaseEditorPanel.PAGE_PANEL_KEY, MetaphaseEditorPanel.OPEN_BUTTON_KEY, MetaphaseEditorPanel.SAVE_BUTTON_KEY,
			MetaphaseEditorPanel.NEW_BUTTON_KEY, MetaphaseEditorPanel.PREVIEW_BUTTON_KEY,

			MetaphaseEditorPanel.EDIT_PANEL_KEY, MetaphaseEditorPanel.CUT_BUTTON_KEY, MetaphaseEditorPanel.COPY_BUTTON_KEY,
			MetaphaseEditorPanel.PASTE_BUTTON_KEY, MetaphaseEditorPanel.PASTE_AS_TEXT_BUTTON_KEY,

			MetaphaseEditorPanel.TOOLS_PANEL_KEY, MetaphaseEditorPanel.PRINT_BUTTON_KEY, MetaphaseEditorPanel.SPELL_CHECK_BUTTON_KEY,

			MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY, MetaphaseEditorPanel.UNDO_BUTTON_KEY, MetaphaseEditorPanel.REDO_BUTTON_KEY,

			MetaphaseEditorPanel.SEARCH_PANEL_KEY, MetaphaseEditorPanel.FIND_BUTTON_KEY, MetaphaseEditorPanel.REPLACE_BUTTON_KEY,

			MetaphaseEditorPanel.FORMAT_PANEL_KEY, MetaphaseEditorPanel.SELECT_ALL_BUTTON_KEY,
			MetaphaseEditorPanel.CLEAR_FORMATTING_BUTTON_KEY,

			MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY, MetaphaseEditorPanel.BOLD_BUTTON_KEY, MetaphaseEditorPanel.ITALIC_BUTTON_KEY,
			MetaphaseEditorPanel.UNDERLINE_BUTTON_KEY, MetaphaseEditorPanel.STRIKE_BUTTON_KEY,

			MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY, MetaphaseEditorPanel.SUB_SCRIPT_BUTTON_KEY,
			MetaphaseEditorPanel.SUPER_SCRIPT_BUTTON_KEY,

			MetaphaseEditorPanel.LIST_PANEL_KEY, MetaphaseEditorPanel.NUMBERED_LIST_BUTTON_KEY, MetaphaseEditorPanel.BULLETED_BUTTON_KEY,

			MetaphaseEditorPanel.BLOCK_PANEL_KEY, MetaphaseEditorPanel.DECREASE_INDENT_BUTTON_KEY,
			MetaphaseEditorPanel.INCREASE_INDENT_BUTTON_KEY, MetaphaseEditorPanel.BLOCK_QUOTE_BUTTON_KEY,
			MetaphaseEditorPanel.DIV_BUTTON_KEY, MetaphaseEditorPanel.PARAGRAPH_BUTTON_KEY,

			MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY, MetaphaseEditorPanel.LEFT_JUSTIFY_BUTTON_KEY,
			MetaphaseEditorPanel.CENTER_JUSTIFY_BUTTON_KEY, MetaphaseEditorPanel.RIGHT_JUSTIFY_BUTTON_KEY,
			MetaphaseEditorPanel.BLOCK_JUSTIFY_BUTTON_KEY,

			MetaphaseEditorPanel.LINK_PANEL_KEY, MetaphaseEditorPanel.LINK_BUTTON_KEY, MetaphaseEditorPanel.UNLINK_BUTTON_KEY,
			MetaphaseEditorPanel.ANCHOR_BUTTON_KEY,

			MetaphaseEditorPanel.MISC_PANEL_KEY, MetaphaseEditorPanel.IMAGE_BUTTON_KEY, MetaphaseEditorPanel.TABLE_BUTTON_KEY,
			MetaphaseEditorPanel.HORIZONTAL_LINE_BUTTON_KEY, MetaphaseEditorPanel.SPECIAL_CHAR_BUTTON_KEY,

			MetaphaseEditorPanel.FONT_PANEL_KEY, MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY, MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY,

			MetaphaseEditorPanel.COLOR_PANEL_KEY, MetaphaseEditorPanel.TEXT_COLOR_BUTTON_KEY,
			MetaphaseEditorPanel.BACKGROUND_COLOR_BUTTON_KEY,

			MetaphaseEditorPanel.ABOUT_PANEL_KEY, MetaphaseEditorPanel.ABOUT_BUTTON_KEY };

	public static enum Parameters implements FIBModelAttribute {
		optionsInLine1,
		optionsInLine2,
		optionsInLine3,
		firstLevelOptionsInLine1,
		firstLevelOptionsInLine2,
		firstLevelOptionsInLine3,
		availableOptions,
		visibleAndUnusedOptions
	}

	private Vector<FIBHtmlEditorOption> availableOptions;
	private Vector<FIBHtmlEditorOption> visibleAndUnusedOptions;

	private Vector<FIBHtmlEditorOption> optionsInLine1;
	private Vector<FIBHtmlEditorOption> optionsInLine2;
	private Vector<FIBHtmlEditorOption> optionsInLine3;
	private Vector<FIBHtmlEditorOption> firstLevelOptionsInLine1;
	private Vector<FIBHtmlEditorOption> firstLevelOptionsInLine2;
	private Vector<FIBHtmlEditorOption> firstLevelOptionsInLine3;

	public FIBHtmlEditor() {
		super();
		buildAvailableOptions();
		optionsInLine1 = new Vector<FIBHtmlEditorOption>();
		optionsInLine2 = new Vector<FIBHtmlEditorOption>();
		optionsInLine3 = new Vector<FIBHtmlEditorOption>();
		firstLevelOptionsInLine1 = new Vector<FIBHtmlEditorOption>();
		firstLevelOptionsInLine2 = new Vector<FIBHtmlEditorOption>();
		firstLevelOptionsInLine3 = new Vector<FIBHtmlEditorOption>();

		/*FIBHtmlEditorOption o1 =  getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY);
		o1.setIsVisible(true);
		addToOptionsInLine1(o1);*/

	}

	public void makeFullHtmlEditor() {
		for (String s : option_keys) {
			FIBHtmlEditorOption option = getOption(s);
			option.setIsVisible(false);
			option.setIsVisible(true);
		}

		addToOptionsInLine1(getOption(MetaphaseEditorPanel.SOURCE_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.PAGE_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.TOOLS_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.SEARCH_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.FORMAT_PANEL_KEY));

		addToOptionsInLine2(getOption(MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.LIST_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.BLOCK_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.LINK_PANEL_KEY));

		addToOptionsInLine3(getOption(MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY));
		addToOptionsInLine3(getOption(MetaphaseEditorPanel.FONT_PANEL_KEY));
		addToOptionsInLine3(getOption(MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY));
		addToOptionsInLine3(getOption(MetaphaseEditorPanel.COLOR_PANEL_KEY));
		addToOptionsInLine3(getOption(MetaphaseEditorPanel.MISC_PANEL_KEY));
		addToOptionsInLine3(getOption(MetaphaseEditorPanel.ABOUT_PANEL_KEY));

	}

	public void makeEmbeddedHtmlEditor() {
		for (String s : option_keys) {
			FIBHtmlEditorOption option = getOption(s);
			option.setIsVisible(false);
			option.setIsVisible(true);
		}

		getOption(MetaphaseEditorPanel.PAGE_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.ABOUT_PANEL_KEY).setIsVisible(false);

		addToOptionsInLine1(getOption(MetaphaseEditorPanel.SOURCE_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.TOOLS_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.SEARCH_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.FORMAT_PANEL_KEY));

		addToOptionsInLine2(getOption(MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.LIST_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.BLOCK_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.LINK_PANEL_KEY));

		addToOptionsInLine3(getOption(MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY));
		addToOptionsInLine3(getOption(MetaphaseEditorPanel.FONT_PANEL_KEY));
		addToOptionsInLine3(getOption(MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY));
		addToOptionsInLine3(getOption(MetaphaseEditorPanel.COLOR_PANEL_KEY));
		addToOptionsInLine3(getOption(MetaphaseEditorPanel.MISC_PANEL_KEY));

	}

	public void makeLightHtmlEditor() {
		for (String s : option_keys) {
			FIBHtmlEditorOption option = getOption(s);
			option.setIsVisible(false);
			option.setIsVisible(true);
		}

		getOption(MetaphaseEditorPanel.SOURCE_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.PAGE_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.TOOLS_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.SEARCH_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.FORMAT_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.BLOCK_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.MISC_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.ABOUT_PANEL_KEY).setIsVisible(false);

		getOption(MetaphaseEditorPanel.HORIZONTAL_LINE_BUTTON_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.SPECIAL_CHAR_BUTTON_KEY).setIsVisible(false);

		addToOptionsInLine1(getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.LIST_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.LINK_PANEL_KEY));

		addToOptionsInLine2(getOption(MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.FONT_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY));
		addToOptionsInLine2(getOption(MetaphaseEditorPanel.COLOR_PANEL_KEY));
	}

	public void makeUltraLightHtmlEditor() {
		for (String s : option_keys) {
			FIBHtmlEditorOption option = getOption(s);
			option.setIsVisible(false);
			option.setIsVisible(true);
		}

		getOption(MetaphaseEditorPanel.SOURCE_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.PAGE_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.TOOLS_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.SEARCH_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.FORMAT_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.LIST_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.BLOCK_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.FONT_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.ABOUT_PANEL_KEY).setIsVisible(false);

		getOption(MetaphaseEditorPanel.ANCHOR_BUTTON_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.TABLE_BUTTON_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.HORIZONTAL_LINE_BUTTON_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.SPECIAL_CHAR_BUTTON_KEY).setIsVisible(false);
		getOption(MetaphaseEditorPanel.STRIKE_BUTTON_KEY).setIsVisible(false);

		addToOptionsInLine1(getOption(MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.COLOR_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.LINK_PANEL_KEY));
		addToOptionsInLine1(getOption(MetaphaseEditorPanel.MISC_PANEL_KEY));
	}

	private void buildAvailableOptions() {
		availableOptions = new Vector<FIBHtmlEditorOption>();
		for (String s : option_keys) {
			availableOptions.add(new FIBHtmlEditorOption(s, this));
		}
		visibleAndUnusedOptions = new Vector<FIBHtmlEditorOption>();
	}

	protected FIBHtmlEditorOption getOption(String key) {
		for (FIBHtmlEditorOption option : availableOptions) {
			if (option.getName().equals(key))
				return option;
		}
		return null;
	}

	private void ensureOptionRegistering(FIBHtmlEditorOption option) {
		if (getOption(option.getName()) == null) {
			availableOptions.add(option);
		} else {
			if (getOption(option.getName()) != option) {
				int index = availableOptions.indexOf(getOption(option.getName()));
				availableOptions.setElementAt(option, index);
			}
		}
	}

	@Override
	public Type getDefaultDataClass() {
		return String.class;
	}

	protected boolean anyLineContains(FIBHtmlEditorOption option) {
		return optionsInLine1.contains(option) || optionsInLine2.contains(option) || optionsInLine3.contains(option);
	}

	public Vector<FIBHtmlEditorOption> getFirstLevelOptionsInLine1() {
		return firstLevelOptionsInLine1;
	}

	public Vector<FIBHtmlEditorOption> getOptionsInLine1() {
		return optionsInLine1;
	}

	public void setOptionsInLine1(Vector<FIBHtmlEditorOption> optionsInLine1) {
		this.optionsInLine1 = optionsInLine1;
	}

	public void addToOptionsInLine1(FIBHtmlEditorOption anOption) {
		ensureOptionRegistering(anOption);
		anOption.setIsVisible(true);
		anOption.setFIBHtmlEditor(this);
		optionsInLine1.add(anOption);
		anOption.setIndexNoEditorNotification(optionsInLine1.indexOf(anOption));
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBHtmlEditorOption>(Parameters.optionsInLine1, anOption));
		if (visibleAndUnusedOptions.contains(anOption))
			removeFromVisibleAndUnusedOptions(anOption);
		for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
			if (subOption.getIsVisible() && !optionsInLine1.contains(subOption))
				addToOptionsInLine1(subOption);
		}
		if (anOption.getLevel() == 0) {
			firstLevelOptionsInLine1.add(anOption);
			anOption.setIndexNoEditorNotification(firstLevelOptionsInLine1.indexOf(anOption));
			setChanged();
			notifyObservers(new FIBAddingNotification<FIBHtmlEditorOption>(Parameters.firstLevelOptionsInLine1, anOption));
		}
	}

	public void addToOptionsInLine1(List<FIBHtmlEditorOption> options) {
		Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
		theOptions.addAll(options);
		for (FIBHtmlEditorOption o : theOptions)
			addToOptionsInLine1(o);
	}

	public void removeFromOptionsInLine1(FIBHtmlEditorOption anOption) {
		optionsInLine1.remove(anOption);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBHtmlEditorOption>(Parameters.optionsInLine1, anOption));
		if (!visibleAndUnusedOptions.contains(anOption))
			addToVisibleAndUnusedOptions(anOption);
		for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
			if (optionsInLine1.contains(subOption))
				removeFromOptionsInLine1(subOption);
		}
		if (anOption.getLevel() == 0) {
			firstLevelOptionsInLine1.remove(anOption);
			setChanged();
			notifyObservers(new FIBRemovingNotification<FIBHtmlEditorOption>(Parameters.firstLevelOptionsInLine1, anOption));
		}
	}

	public void removeFromOptionsInLine1(List<FIBHtmlEditorOption> options) {
		Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
		theOptions.addAll(options);
		for (FIBHtmlEditorOption o : theOptions)
			removeFromOptionsInLine1(o);
	}

	public Vector<FIBHtmlEditorOption> getFirstLevelOptionsInLine2() {
		return firstLevelOptionsInLine2;
	}

	public Vector<FIBHtmlEditorOption> getOptionsInLine2() {
		return optionsInLine2;
	}

	public void setOptionsInLine2(Vector<FIBHtmlEditorOption> optionsInLine2) {
		this.optionsInLine2 = optionsInLine2;
	}

	public void addToOptionsInLine2(FIBHtmlEditorOption anOption) {
		ensureOptionRegistering(anOption);
		anOption.setIsVisible(true);
		anOption.setFIBHtmlEditor(this);
		optionsInLine2.add(anOption);
		anOption.setIndexNoEditorNotification(optionsInLine2.indexOf(anOption));
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBHtmlEditorOption>(Parameters.optionsInLine2, anOption));
		if (visibleAndUnusedOptions.contains(anOption))
			removeFromVisibleAndUnusedOptions(anOption);
		for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
			if (subOption.getIsVisible() && !optionsInLine2.contains(subOption))
				addToOptionsInLine2(subOption);
		}
		if (anOption.getLevel() == 0) {
			firstLevelOptionsInLine2.add(anOption);
			anOption.setIndexNoEditorNotification(firstLevelOptionsInLine2.indexOf(anOption));
			setChanged();
			notifyObservers(new FIBAddingNotification<FIBHtmlEditorOption>(Parameters.firstLevelOptionsInLine2, anOption));
		}
	}

	public void addToOptionsInLine2(List<FIBHtmlEditorOption> options) {
		Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
		theOptions.addAll(options);
		for (FIBHtmlEditorOption o : theOptions)
			addToOptionsInLine2(o);
	}

	public void removeFromOptionsInLine2(FIBHtmlEditorOption anOption) {
		optionsInLine2.remove(anOption);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBHtmlEditorOption>(Parameters.optionsInLine2, anOption));
		if (!visibleAndUnusedOptions.contains(anOption))
			addToVisibleAndUnusedOptions(anOption);
		for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
			if (optionsInLine2.contains(subOption))
				removeFromOptionsInLine2(subOption);
		}
		if (anOption.getLevel() == 0) {
			firstLevelOptionsInLine2.remove(anOption);
			setChanged();
			notifyObservers(new FIBRemovingNotification<FIBHtmlEditorOption>(Parameters.firstLevelOptionsInLine2, anOption));
		}
	}

	public void removeFromOptionsInLine2(List<FIBHtmlEditorOption> options) {
		Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
		theOptions.addAll(options);
		for (FIBHtmlEditorOption o : theOptions)
			removeFromOptionsInLine2(o);
	}

	public Vector<FIBHtmlEditorOption> getFirstLevelOptionsInLine3() {
		return firstLevelOptionsInLine3;
	}

	public Vector<FIBHtmlEditorOption> getOptionsInLine3() {
		return optionsInLine3;
	}

	public void setOptionsInLine3(Vector<FIBHtmlEditorOption> optionsInLine3) {
		this.optionsInLine3 = optionsInLine3;
	}

	public void addToOptionsInLine3(FIBHtmlEditorOption anOption) {
		ensureOptionRegistering(anOption);
		anOption.setIsVisible(true);
		anOption.setFIBHtmlEditor(this);
		optionsInLine3.add(anOption);
		anOption.setIndexNoEditorNotification(optionsInLine3.indexOf(anOption));
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBHtmlEditorOption>(Parameters.optionsInLine3, anOption));
		if (visibleAndUnusedOptions.contains(anOption))
			removeFromVisibleAndUnusedOptions(anOption);
		for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
			if (subOption.getIsVisible() && !optionsInLine3.contains(subOption))
				addToOptionsInLine3(subOption);
		}
		if (anOption.getLevel() == 0) {
			firstLevelOptionsInLine3.add(anOption);
			anOption.setIndexNoEditorNotification(firstLevelOptionsInLine3.indexOf(anOption));
			setChanged();
			notifyObservers(new FIBAddingNotification<FIBHtmlEditorOption>(Parameters.firstLevelOptionsInLine3, anOption));
		}
	}

	public void addToOptionsInLine3(List<FIBHtmlEditorOption> options) {
		Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
		theOptions.addAll(options);
		for (FIBHtmlEditorOption o : theOptions)
			addToOptionsInLine3(o);
	}

	public void removeFromOptionsInLine3(FIBHtmlEditorOption anOption) {
		optionsInLine3.remove(anOption);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBHtmlEditorOption>(Parameters.optionsInLine3, anOption));
		if (!visibleAndUnusedOptions.contains(anOption))
			addToVisibleAndUnusedOptions(anOption);
		for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
			if (optionsInLine3.contains(subOption))
				removeFromOptionsInLine3(subOption);
		}
		if (anOption.getLevel() == 0) {
			firstLevelOptionsInLine3.remove(anOption);
			setChanged();
			notifyObservers(new FIBRemovingNotification<FIBHtmlEditorOption>(Parameters.firstLevelOptionsInLine3, anOption));
		}
	}

	public void removeFromOptionsInLine3(List<FIBHtmlEditorOption> options) {
		Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
		theOptions.addAll(options);
		for (FIBHtmlEditorOption o : theOptions)
			removeFromOptionsInLine3(o);
	}

	public Vector<FIBHtmlEditorOption> getAvailableOptions() {
		return availableOptions;
	}

	public Vector<FIBHtmlEditorOption> getVisibleAndUnusedOptions() {
		return visibleAndUnusedOptions;
	}

	protected void addToVisibleAndUnusedOptions(FIBHtmlEditorOption anOption) {
		if (anOption.getLevel() == 0) {
			// logger.info(">> addToVisibleAndUnusedOptions "+anOption);
			visibleAndUnusedOptions.add(anOption);
			setChanged();
			notifyObservers(new FIBAddingNotification<FIBHtmlEditorOption>(Parameters.visibleAndUnusedOptions, anOption));
		}
	}

	protected void removeFromVisibleAndUnusedOptions(FIBHtmlEditorOption anOption) {
		if (anOption.getLevel() == 0) {
			// logger.info(">> removeFromVisibleAndUnusedOptions "+anOption);
			visibleAndUnusedOptions.remove(anOption);
			setChanged();
			notifyObservers(new FIBRemovingNotification<FIBHtmlEditorOption>(Parameters.visibleAndUnusedOptions, anOption));
		}
	}

	public void indexChanged() {
		setChanged();
		notifyObservers();
	}

}
