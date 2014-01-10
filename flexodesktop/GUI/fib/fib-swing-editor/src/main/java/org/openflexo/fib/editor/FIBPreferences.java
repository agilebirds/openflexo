package org.openflexo.fib.editor;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.openflexo.model.StringConverterLibrary;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.converter.AWTRectangleConverter;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.toolbox.FileResource;

public class FIBPreferences {

	private static final String FIB = "FIB";

	public static final String FRAME = "Frame";
	public static final String PALETTE = "Palette";
	public static final String INSPECTOR = "Inspector";
	public static final String LAST_DIR = "LastDirectory";
	public static final String LAST_FILES_COUNT = "LAST_FILES_COUNT";
	public static final String LAST_FILE = "LAST_FILE";

	private static final Preferences prefs = Preferences.userRoot().node(FIB);

	private static AWTRectangleConverter RECTANGLE_CONVERTER = new AWTRectangleConverter();
	private static Converter<File> FILE_CONVERTER = StringConverterLibrary.getInstance().getConverter(File.class);

	private static Rectangle getPreferredBounds(String key, Rectangle def) {
		String s = prefs.get(key, null);
		if (s == null) {
			return def;
		}
		return RECTANGLE_CONVERTER.convertFromString(s, null);
	}

	public static void addPreferenceChangeListener(PreferenceChangeListener pcl) {
		prefs.addPreferenceChangeListener(pcl);
	}

	public static void removePreferenceChangeListener(PreferenceChangeListener pcl) {
		prefs.removePreferenceChangeListener(pcl);
	}

	private static void setPreferredBounds(String key, Rectangle value) {
		prefs.put(key, RECTANGLE_CONVERTER.convertToString(value));
	}

	private static File getPreferredFile(String key, File def) {
		String s = prefs.get(key, null);
		if (s == null) {
			return def;
		}
		try {
			return FILE_CONVERTER.convertFromString(s, null);
		} catch (InvalidDataException e) {
			return def;
		}
	}

	private static void setPreferredFile(String key, File value) {
		prefs.put(key, FILE_CONVERTER.convertToString(value));
	}

	public static Rectangle getFrameBounds() {
		return getPreferredBounds(FRAME, new Rectangle(0, 0, 1000, 800));
	}

	public static void setFrameBounds(Rectangle bounds) {
		setPreferredBounds(FRAME, bounds);
	}

	public static Rectangle getInspectorBounds() {
		return getPreferredBounds(INSPECTOR, new Rectangle(1000, 400, 400, 400));
	}

	public static void setInspectorBounds(Rectangle bounds) {
		setPreferredBounds(INSPECTOR, bounds);
	}

	public static Rectangle getPaletteBounds() {
		return getPreferredBounds(PALETTE, new Rectangle(1000, 0, 400, 400));
	}

	public static void setPaletteBounds(Rectangle bounds) {
		setPreferredBounds(PALETTE, bounds);
	}

	public static File getLastDirectory() {
		return getPreferredFile(LAST_DIR, new FileResource("TestFIB"));
	}

	public static void setLastDirectory(File file) {
		setPreferredFile(LAST_DIR, file);
	}

	public static int getLastFileCount() {
		return prefs.getInt(LAST_FILES_COUNT, 10);
	}

	public static void setLastFileCount(int count) {
		prefs.putInt(LAST_FILES_COUNT, count);
	}

	public static List<File> getLastFiles() {
		List<File> list = new ArrayList<File>();
		for (int i = 0; i < getLastFileCount(); i++) {
			File file = getPreferredFile(LAST_FILE + i, null);
			if (file != null) {
				list.add(file);
			}
		}
		return list;
	}

	public static void setLastFiles(List<File> files) {
		for (int i = 0; i < files.size(); i++) {
			setPreferredFile(LAST_FILE + i, files.get(i));
		}
	}

	public static void setLastFile(File file) {
		List<File> files = getLastFiles();
		if (files.contains(file)) {
			files.remove(file);
		} else if (files.size() == getLastFileCount()) {
			files.remove(getLastFileCount() - 1);
		}
		files.add(0, file);
		setLastFiles(files);
		setLastDirectory(file.getParentFile());
	}
}