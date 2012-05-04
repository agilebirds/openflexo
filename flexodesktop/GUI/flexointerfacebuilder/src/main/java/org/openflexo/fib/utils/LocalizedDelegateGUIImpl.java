package org.openflexo.fib.utils;

import java.awt.Color;
import java.awt.Image;
import java.awt.Window;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.toolbox.FileResource;

/**
 * Provides a default implementation for a localized delegate enriched with the possibility to connect it with a SWING graphical editor
 * allowing search features
 * 
 * @author sylvain
 * 
 */
public class LocalizedDelegateGUIImpl extends LocalizedDelegateImpl {

	private static final Logger logger = Logger.getLogger(FlexoLocalization.class.getPackage().getName());

	public static final File LOCALIZED_EDITOR_FIB = new FileResource("Fib/LocalizedEditor.fib");

	private SearchMode searchMode = SearchMode.Contains;
	private String searchedString;

	public LocalizedDelegateGUIImpl(File localizedDirectory, LocalizedDelegate parent) {
		super(localizedDirectory, parent);
	}

	public void showLocalizedEditor(Window parentFrame) {
		FIBComponent localizedEditorComponent = FIBLibrary.instance().retrieveFIBComponent(LOCALIZED_EDITOR_FIB);
		FIBDialog dialog = FIBDialog.instanciateComponent(localizedEditorComponent, this, parentFrame, true,
				FlexoLocalization.getMainLocalizer());
	}

	public void showParentLocalizedEditor() {
		if (getParent() instanceof LocalizedDelegateGUIImpl) {
			((LocalizedDelegateGUIImpl) getParent()).showLocalizedEditor(null);
		}
	}

	/*public Vector<String> buildAllKeys() {
		Vector<String> returned = new Vector<String>();
		for (Enumeration e1 = getLocalizedDictionaries().elements(); e1.hasMoreElements();) {
			Properties next = (Properties) e1.nextElement();
			for (Enumeration e2 = next.keys(); e2.hasMoreElements();) {
				String nextKey = (String) e2.nextElement();
				if (!returned.contains(nextKey)) {
					returned.add(nextKey);
				}
			}
		}
		return returned;
	}

	public Vector<String> buildAllKeys(char aChar) {
		char lc = Character.toLowerCase(aChar);
		char uc = Character.toUpperCase(aChar);
		Vector<String> returned = new Vector<String>();
		for (Enumeration e1 = getLocalizedDictionaries().elements(); e1.hasMoreElements();) {
			Properties next = (Properties) e1.nextElement();
			for (Enumeration e2 = next.keys(); e2.hasMoreElements();) {
				String nextKey = (String) e2.nextElement();
				if (nextKey.length() > 0) {
					if ((!returned.contains(nextKey)) && ((nextKey.charAt(0) == lc) || (nextKey.charAt(0) == uc))) {
						returned.add(nextKey);
					}
				}
			}
		}
		return returned;
	}*/

	public Icon getIconForEntry(Entry entry) {
		if (entry.hasInvalidValue()) {
			return UtilsIconLibrary.WARNING_ICON;
		}
		return null;
	}

	public Color getColorForFrenchValue(Entry entry) {
		if (!entry.isFrenchValueValid()) {
			return Color.ORANGE;
		}
		// No specific color in this case
		return null;
	}

	public Color getColorForEnglishValue(Entry entry) {
		if (!entry.isEnglishValueValid()) {
			return Color.ORANGE;
		}
		// No specific color in this case
		return null;
	}

	public Color getColorForDutchValue(Entry entry) {
		if (!entry.isDutchValueValid()) {
			return Color.ORANGE;
		}
		// No specific color in this case
		return null;
	}

	public Image getFrenchIconImage() {
		return UtilsIconLibrary.FR_FLAG.getImage();
	}

	public Image getEnglishIconImage() {
		return UtilsIconLibrary.UK_FLAG.getImage();
	}

	public Image getDutchIconImage() {
		return UtilsIconLibrary.NE_FLAG.getImage();
	}

	public SearchMode getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
		searchMatchingEntries();
	}

	public String getSearchedString() {
		return searchedString;
	}

	public void setSearchedString(String searchedString) {
		this.searchedString = searchedString;
		searchMatchingEntries();
	}

	public void searchMatchingEntries() {
		computeMatchingEntries(getSearchedString(), getSearchMode());
	}

	public void apply() {
		logger.info("Applying localized to GUI");
		FlexoLocalization.updateGUILocalized();
	}

}
