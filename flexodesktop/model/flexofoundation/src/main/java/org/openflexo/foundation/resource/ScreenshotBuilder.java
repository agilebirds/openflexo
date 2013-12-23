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
package org.openflexo.foundation.resource;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.FlexoSwingUtils;
import org.openflexo.swing.ImageUtils;
import org.openflexo.toolbox.FileResource;

/**
 * Utility class used to manage screenshots
 * 
 * @author sylvain
 * 
 */
public abstract class ScreenshotBuilder<T extends FlexoObject> {

	private static final Logger logger = FlexoLogger.getLogger(ScreenshotBuilder.class.getPackage().getName());

	protected static final String BAD_FILE_NAME_CHARACTERS_REG_EXP = "[^-A-Za-z0-9]";

	protected static final Pattern BAD_FILE_NAME_CHARACTERS_PATTERN = Pattern.compile(BAD_FILE_NAME_CHARACTERS_REG_EXP);

	private static final String REPLACEMENT = "-";

	public abstract String getScreenshotName(T o);

	private String getImageName(String type, String name, long flexoID) {
		return trim(formatImageName(type + "-" + name)) + flexoID;
	}

	private String trim(String name) {
		// Max-length is 255 chars
		// We need to remove 20 characters for the flexoID Long.MAX_VALUE is 20 digits
		// We need to remove 4 chars for the extension (.png)
		// Let's be cautious and add an extra 30 chars security
		if (name.length() > 200) {
			return name.substring(0, 200).trim();
		} else {
			return name;
		}
	}

	private String formatImageName(String imageName) {
		if (imageName == null) {
			return null;
		}
		return BAD_FILE_NAME_CHARACTERS_PATTERN.matcher(imageName).replaceAll(REPLACEMENT);
	}

	public ScreenshotImage<T> getImage(T object) {
		if (object == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Object is null: cannot generate screenshot");
			}
			return getEmptyScreenshot();
		}
		logger.info("Generating screenshot for " + object + " of " + object.getClass().getSimpleName());
		// object.setIgnoreNotifications();

		try {
			ScreenshotComponentRunnable componentRunnable = new ScreenshotComponentRunnable(object);
			JComponent component = null;
			try {
				component = FlexoSwingUtils.syncRunInEDT(componentRunnable);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (component == null) {
				return getEmptyScreenshot();
			}
			ScreenshotImageRunnable runnable = new ScreenshotImageRunnable(component, object);
			ScreenshotImage i = null;
			try {
				i = FlexoSwingUtils.syncRunInEDT(runnable);
				FlexoSwingUtils.syncRunInEDT(new ScreenshotFinalizeRunnable(component, object));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (i == null) {
				return getEmptyScreenshot();
			}
			return i;
		} finally {
			// object.resetIgnoreNotifications();
		}
	}

	/**
	 * Represents the screenshot image
	 * 
	 * @author sylvain
	 * 
	 */
	public static class ScreenshotImage<T extends FlexoObject> {
		public BufferedImage image;
		public Rectangle trimInfo;
		private final T object;

		public ScreenshotImage(T object) {
			this.object = object;
		}

		public T getObject() {
			return object;
		}
	}

	private class ScreenshotComponentRunnable implements Callable<JComponent> {
		private final T object;

		protected ScreenshotComponentRunnable(T object) {
			this.object = object;
		}

		@Override
		public JComponent call() {
			return getScreenshotComponent(object);
		}

	}

	private class ScreenshotImageRunnable implements Callable<ScreenshotImage<T>> {
		private final T object;
		private final JComponent component;

		protected ScreenshotImageRunnable(JComponent component, T object) {
			this.component = component;
			this.object = object;
		}

		@Override
		public ScreenshotImage<T> call() {
			return createImageForComponent(object, component, true);
		}

	}

	private class ScreenshotFinalizeRunnable implements Callable<Void> {
		private final T object;
		private final JComponent component;

		protected ScreenshotFinalizeRunnable(JComponent component, T object) {
			this.component = component;
			this.object = object;
		}

		@Override
		public Void call() {
			finalizeScreenshotGeneration(component, object);
			return null;
		}

	}

	public abstract JComponent getScreenshotComponent(T object);

	private ScreenshotImage<T> createImageForComponent(T object, JComponent c, boolean trim) {
		JFrame frame = new JFrame();
		try {
			BufferedImage bi = null;
			c.setOpaque(true);
			c.setBackground(Color.WHITE);
			frame.setBackground(Color.WHITE);
			frame.setUndecorated(true);
			frame.getContentPane().add(c);
			frame.pack();
			bi = ImageUtils.createImageFromComponent(c);
			ScreenshotImage<T> i;
			if (trim) {
				i = trimImage(object, bi);
			} else {
				i = new ScreenshotImage<T>(object);
				i.image = bi;
				i.trimInfo = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
			}
			return i;
		} finally {
			if (frame.getContentPane() != null) {
				frame.getContentPane().removeAll();
			}
			frame.dispose();
		}
	}

	/**
	 * Might be overriden (called at the end of screenshot capture)
	 * 
	 * @param c
	 * @param object
	 */
	public void finalizeScreenshotGeneration(JComponent c, T object) {
	}

	public ScreenshotImage<T> makeImage(T object, BufferedImage bi) {
		ScreenshotImage<T> i = new ScreenshotImage<T>(object);
		i.image = bi;
		i.trimInfo = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
		return i;
	}

	public ScreenshotImage<T> makeImage(T object, BufferedImage bi, int left, int top, int width, int height) {
		ScreenshotImage<T> i = new ScreenshotImage<T>(object);
		i.image = bi.getSubimage(left, top, width, height);
		i.trimInfo = new Rectangle(left, top, width, height);
		return i;
	}

	public ScreenshotImage<T> trimImage(T object, BufferedImage bi) {
		// Trim operation to remove white borders.
		int border = 10;
		int top = -1;
		int bottom = -1;
		int left = -1;
		int right = -1;
		for (int i = bi.getWidth() - 1; i > 0 && right == -1; i--) {
			for (int j = 0; j < bi.getHeight() && right == -1; j++) {
				int color = bi.getRGB(i, j);
				if (color != -1) {
					right = i;
				}
			}
		}

		for (int i = 0; i < bi.getWidth() && left == -1; i++) {
			for (int j = 0; j < bi.getHeight() && left == -1; j++) {
				int color = bi.getRGB(i, j);
				if (color != -1) {
					left = i;
				}
			}
		}
		for (int j = 0; j < bi.getHeight() && top == -1; j++) {
			for (int i = 0; i < bi.getWidth() && top == -1; i++) {

				int color = bi.getRGB(i, j);
				if (color != -1) {
					top = j;
				}
			}
		}

		for (int j = bi.getHeight() - 1; j > 0 && bottom == -1; j--) {
			for (int i = bi.getWidth() - 1; i > 0 && bottom == -1; i--) {

				int color = bi.getRGB(i, j);
				if (color != -1) {
					bottom = j;
				}
			}
		}
		left = Math.max(left - border, 0);
		top = Math.max(top - border, 0);
		right = Math.min(right - left + border, bi.getWidth() - 1 - left);
		bottom = Math.min(bottom - top + border, bi.getHeight() - 1 - top);
		ScreenshotImage i = new ScreenshotImage<T>(object);
		i.image = bi.getSubimage(left, top, right, bottom);
		i.trimInfo = new Rectangle(left, top, right, bottom);
		return i;
	}

	/**
	 * @return
	 */
	private ScreenshotImage<T> getEmptyScreenshot() {
		File f = new FileResource("LatexExtras/EmptyScreenshot.jpg");
		if (f.exists()) {
			try {
				FileInputStream fis = new FileInputStream(f);
				BufferedImage bi = ImageIO.read(fis);
				ScreenshotImage<T> i = new ScreenshotImage<T>(null);
				i.image = bi;
				i.trimInfo = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
				return i;
			} catch (FileNotFoundException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("File " + f.getAbsolutePath() + " does not exist.");
				}
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Error trying to read file " + f.getAbsolutePath());
				}
			}
		}
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("Cannot find " + f.getAbsolutePath() + " returning null");
		}
		return null;
	}

}
