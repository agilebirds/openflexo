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

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * 
 * @author sylvain
 * 
 */
public abstract class DirectoryWatcher extends TimerTask {
	private String path;
	private File filesArray[];
	private HashMap<File, Long> dir = new HashMap<File, Long>();

	public DirectoryWatcher(String path) {
		super();
		this.path = path;
		filesArray = new File(path).listFiles();
		for (int i = 0; i < filesArray.length; i++) {
			dir.put(filesArray[i], new Long(filesArray[i].lastModified()));
		}
	}

	@Override
	public final void run() {
		HashSet<File> checkedFiles = new HashSet<File>();
		filesArray = new File(path).listFiles();

		// scan the files and check for modification/addition
		for (int i = 0; i < filesArray.length; i++) {
			Long current = dir.get(filesArray[i]);
			checkedFiles.add(filesArray[i]);
			if (current == null) {
				// new file
				dir.put(filesArray[i], new Long(filesArray[i].lastModified()));
				onChange(filesArray[i], "add");
			} else if (current.longValue() != filesArray[i].lastModified()) {
				// modified file
				dir.put(filesArray[i], new Long(filesArray[i].lastModified()));
				onChange(filesArray[i], "modify");
			}
		}

		// now check for deleted files
		Set<File> ref = ((HashMap<File, Long>) dir.clone()).keySet();
		ref.removeAll(checkedFiles);
		Iterator<File> it = ref.iterator();
		while (it.hasNext()) {
			File deletedFile = it.next();
			dir.remove(deletedFile);
			onChange(deletedFile, "delete");
		}
	}

	protected abstract void onChange(File file, String action);

	public static void main(String[] args) {
		TimerTask task = new DirectoryWatcher("/Users/sylvain/Temp") {
			@Override
			protected void onChange(File file, String action) {
				// here we code the action on a change
				System.out.println("File " + file.getName() + " action: " + action);
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, new Date(), 1000);

	}
}
