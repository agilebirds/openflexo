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
package org.openflexo.toolbox;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author gpolet Created on 17 sept. 2005
 */
public class PlaySound {

	public static boolean playSound = true;

	public static void tryToPlaySound(String fileName) {
		if (!playSound) {
			return;
		}
		if (fileName == null) {
			tryToPlayDefaultSound();
			return;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			file = new FileResource(fileName);
		}
		try {
			Class soundFile = Class.forName("org.openflexo.sound.SoundFile");
			Method m = soundFile.getMethod("getNewDynamicSoundFile", new Class[] { File.class });
			Object o = m.invoke(null, new Object[] { file });
			Class soundPlayer = Class.forName("org.openflexo.sound.SoundPlayer");
			m = soundPlayer.getMethod("getPlayer", (Class[]) null);
			Object player = m.invoke(null, (Object[]) null);
			m = soundPlayer.getMethod("play", new Class[] { soundFile });
			m.invoke(player, new Object[] { o });

		} catch (ClassNotFoundException e) {
			playSound = false;
			return;
		} catch (SecurityException e) {
			playSound = false;
			return;
		} catch (IllegalArgumentException e) {
			playSound = false;
			return;
		} catch (IllegalAccessException e) {
			playSound = false;
			return;
		} catch (NoSuchMethodException e) {
			playSound = false;
			return;
		} catch (InvocationTargetException e) {
			playSound = false;
			return;
		}
	}

	/**
	 * @param fieldName
	 *            TODO
	 * 
	 */
	public static void tryToPlaySoundWithFieldNamed(String fieldName) {
		try {
			if (!playSound) {
				return;
			}
			if (fieldName == null) {
				fieldName = "DEFAULT_SOUND_FILE";
			}
			Class soundFile = Class.forName("org.openflexo.sound.SoundFile");
			Field field = soundFile.getField(fieldName);
			Object o = field.get(null);
			Class soundPlayer = Class.forName("org.openflexo.sound.SoundPlayer");
			Method m = soundPlayer.getMethod("getPlayer", (Class[]) null);
			Object player = m.invoke(null, (Object[]) null);
			m = soundPlayer.getMethod("play", new Class[] { soundFile });
			m.invoke(player, new Object[] { o });

		} catch (NoSuchFieldException e) {
			playSound = false;
			return;
		} catch (ClassNotFoundException e) {
			playSound = false;
			return;
		} catch (SecurityException e) {
			playSound = false;
			return;
		} catch (IllegalArgumentException e) {
			playSound = false;
			return;
		} catch (IllegalAccessException e) {
			playSound = false;
			return;
		} catch (NoSuchMethodException e) {
			playSound = false;
			return;
		} catch (InvocationTargetException e) {
			playSound = false;
			return;
		}
	}

	public static void tryToPlayRandomSound() {
		try {
			FileResource soundDir = new FileResource("Resources/Sounds");
			File[] f = soundDir.listFiles();
			tryToPlaySound(f[new Random(System.currentTimeMillis()).nextInt(f.length)].getAbsolutePath());
		} catch (Exception e) {
			// e.printStackTrace();
			return;
		}
	}

	// DEFAULT_SOUND_FILE
	public static void tryToPlayDefaultSound() {
		tryToPlaySoundWithFieldNamed(null);
	}
}
