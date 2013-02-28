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
 * Implements a timer task which recursively watch a directory looking for changes on file system
 * 
 * @author sylvain
 * 
 */
public abstract class DirectoryWatcher extends TimerTask {

	private NodeDirectoryWatcher rootDirectoryWatcher;

	private static class NodeDirectoryWatcher {

		private DirectoryWatcher watcher;
		private File directory;
		private HashMap<File, Long> lastModified = new HashMap<File, Long>();
		private HashMap<File, NodeDirectoryWatcher> subNodes = new HashMap<File, NodeDirectoryWatcher>();

		private NodeDirectoryWatcher(File directory, DirectoryWatcher watcher, boolean notifyAdding) {
			// System.out.println("Init NodeDirectoryWatcher on " + directory);
			this.directory = directory;
			this.watcher = watcher;
			for (File f : directory.listFiles()) {
				lastModified.put(f, new Long(f.lastModified()));
				if (f.isDirectory()) {
					subNodes.put(f, new NodeDirectoryWatcher(f, watcher, notifyAdding));
				}
				if (notifyAdding) {
					watcher.fileAdded(f);
				}
			}
		}

		private void watch() {
			HashSet<File> checkedFiles = new HashSet<File>();

			// scan the files and check for modification/addition
			for (File f : directory.listFiles()) {
				Long current = lastModified.get(f);
				checkedFiles.add(f);
				if (current == null) {
					// new file
					lastModified.put(f, new Long(f.lastModified()));
					watcher.fileAdded(f);
					if (f.isDirectory()) {
						subNodes.put(f, new NodeDirectoryWatcher(f, watcher, true));
					}
				} else if (current.longValue() != f.lastModified()) {
					// modified file
					lastModified.put(f, new Long(f.lastModified()));
					watcher.fileModified(f);
				}
			}

			// now check for deleted files
			Set<File> ref = ((HashMap<File, Long>) lastModified.clone()).keySet();
			ref.removeAll(checkedFiles);
			Iterator<File> it = ref.iterator();
			while (it.hasNext()) {
				File deletedFile = it.next();
				lastModified.remove(deletedFile);
				if (subNodes.get(deletedFile) != null) {
					subNodes.get(deletedFile).delete();
				}
				subNodes.remove(deletedFile);
				watcher.fileDeleted(deletedFile);
			}

			for (NodeDirectoryWatcher w : subNodes.values()) {
				w.watch();
			}
		}

		private void delete() {
			for (File f : lastModified.keySet()) {
				watcher.fileDeleted(f);
			}
			for (NodeDirectoryWatcher w : subNodes.values()) {
				w.delete();
			}
		}
	}

	public DirectoryWatcher(File directory) {
		super();
		rootDirectoryWatcher = new NodeDirectoryWatcher(directory, this, false);
		System.out.println("Started DirectoryWatcher on " + directory + " ...");
	}

	@Override
	public final void run() {
		rootDirectoryWatcher.watch();
	}

	protected abstract void fileModified(File file);

	protected abstract void fileAdded(File file);

	protected abstract void fileDeleted(File file);

	public static void main(String[] args) {
		TimerTask task = new DirectoryWatcher(new File("/Users/sylvain/Temp")) {
			@Override
			protected void fileModified(File file) {
				System.out.println("File MODIFIED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
			}

			@Override
			protected void fileAdded(File file) {
				System.out.println("File ADDED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
			}

			@Override
			protected void fileDeleted(File file) {
				System.out.println("File DELETED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, new Date(), 1000);

	}
}
