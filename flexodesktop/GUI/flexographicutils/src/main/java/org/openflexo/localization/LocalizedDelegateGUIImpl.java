package org.openflexo.localization;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Provides a default implementation for a localized delegate enriched with the possibility to connect it with a SWING graphical editor
 * 
 * @author sylvain
 * 
 */
public class LocalizedDelegateGUIImpl extends LocalizedDelegateImpl {

	private static final Logger logger = Logger.getLogger(FlexoLocalization.class.getPackage().getName());

	private LocalizedEditor localizedEditor = null;
	private LocalizedEditorModel _editorModel;

	public LocalizedDelegateGUIImpl(File localizedDirectory, LocalizedDelegate parent) {
		super(localizedDirectory, parent);
	}

	public LocalizedEditorModel getEditorModel() {
		if (_editorModel == null) {
			_editorModel = new LocalizedEditorModel(this);
		}
		return _editorModel;
	}

	public void showLocalizedEditor() {
		if (localizedEditor == null) {
			localizedEditor = new LocalizedEditor(this);
		}
		localizedEditor.setVisible(true);
	}

	public Vector<String> buildAllKeys() {
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
	}

	public Vector<String> buildAllWarningKeys() {
		Vector<String> returned = new Vector<String>();
		for (Enumeration e1 = getLocalizedDictionaries().elements(); e1.hasMoreElements();) {
			Properties next = (Properties) e1.nextElement();
			for (Enumeration e2 = next.keys(); e2.hasMoreElements();) {
				String nextKey = (String) e2.nextElement();
				if (nextKey.length() > 0) {
					if (!returned.contains(nextKey)) {
						String value = next.getProperty(nextKey);
						if (!LocalizedEditorModel.isKeyValid(nextKey)) {
							returned.add(nextKey);
						} else if (LocalizedEditorModel.isValueValid(nextKey, value)) {
						} else {
							returned.add(nextKey);
						}
					}
				}
			}
		}
		return returned;
	}

	public Vector<Character> getAllFirstChar() {
		Vector<Character> returned = new Vector<Character>();
		for (Enumeration e1 = getLocalizedDictionaries().elements(); e1.hasMoreElements();) {
			Properties next = (Properties) e1.nextElement();
			for (Enumeration e2 = next.keys(); e2.hasMoreElements();) {
				String nextKey = (String) e2.nextElement();
				if (nextKey.length() > 0) {
					char firstChar = Character.toUpperCase(nextKey.charAt(0));
					if (!returned.contains(firstChar)) {
						returned.add(firstChar);
					}
				}
			}
		}
		return returned;
	}

}
