package org.openflexo.script;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBParameter;
import org.openflexo.toolbox.ResourceLocator;

public class VisibleForScript {

	public static final boolean DRY_RUN = false;

	private static final String[] FIB_EXTENSIONS = { "fib", "inspector" };

	private void convert() {
		Collection<File> fibs = FileUtils.listFiles(
				ResourceLocator.findProjectDirectoryWithName(new File(System.getProperty("user.dir")), "openflexo"), FIB_EXTENSIONS, true);
		for (File fib : fibs) {
			convertFib(fib);
		}
	}

	private void convertFib(File fib) {
		if (fib.getName().endsWith("Prefs.inspector")) {
			return;
		}
		LineIterator lineIterator;
		try {
			lineIterator = FileUtils.lineIterator(fib, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if (!lineIterator.hasNext() || !lineIterator.nextLine().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
			System.err.println(fib.getAbsolutePath() + " is not a fib");
			return;
		}
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fib);
		if (fibComponent == null) {
			System.err.println(fib.getAbsolutePath() + " could not be loaded");
			return;
		}
		boolean changed = lookupAndFixVisibleFor(fibComponent, fibComponent);
		if (changed) {
			System.err.println(fib.getAbsolutePath() + " has changed");
			if (DRY_RUN) {
				FIBLibrary.saveComponentToStream(fibComponent, fib, new OutputStream() {

					@Override
					public void write(int b) throws IOException {
						System.err.print((char) b);
					}
				});
			} else {
				FIBLibrary.save(fibComponent, fib);
			}
		}
	}

	private boolean lookupAndFixVisibleFor(FIBComponent root, FIBComponent fib) {
		boolean changed = false;
		String parameter = fib.getParameter("visibleFor");
		if (parameter != null) {
			FIBComponent label = root.getComponentNamed(fib.getName() + "Label");
			if (label != null && label.getParameter("visibleFor") == null) {
				label.addToParameters(new FIBParameter("visibleFor", parameter));
				changed = true;
			}
		}
		if (fib instanceof FIBContainer) {
			for (FIBComponent child : ((FIBContainer) fib).getSubComponents()) {
				changed |= lookupAndFixVisibleFor(root, child);
			}
		}
		return changed;
	}

	public static void main(String[] args) {
		new VisibleForScript().convert();
		System.exit(0);
	}

}
