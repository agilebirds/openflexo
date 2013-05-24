package org.openflexo.swing;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

public class FlexoSwingUtils {

	private static class RunnableCallable<V> implements Runnable {
		private Callable<V> callable;

		private V result;

		private Exception exception;

		protected RunnableCallable(Callable<V> callable) {
			super();
			this.callable = callable;
		}

		@Override
		public void run() {
			try {
				result = callable.call();
			} catch (Exception e) {
				exception = e;
			}
		}

		public V getResult() {
			return result;
		}

		public Exception getException() {
			return exception;
		}
	}

	public static void syncRunInEDT(Runnable runnable) throws Exception {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeAndWait(runnable);
		} else {
			runnable.run();
		}
	}

	public static <V> V syncRunInEDT(Callable<V> callable) throws Exception {
		RunnableCallable<V> runnable = new RunnableCallable<V>(callable);
		syncRunInEDT(runnable);
		if (runnable.getException() != null) {
			throw new InvocationTargetException(runnable.getException(), "Error while running callable");
		}
		return runnable.getResult();
	}

}
