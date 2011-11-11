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
package org.openflexo.generator.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.ActionGroup;
import org.openflexo.foundation.action.ActionMenu;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.logging.FlexoLogger;

/**
 * Abstract class for actions applying on a set of CGFile
 * 
 * @author sylvain
 * 
 */
public abstract class MultipleFileGCAction<A extends MultipleFileGCAction<A>> extends GCAction<A, CGObject> {
	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(MultipleFileGCAction.class.getPackage().getName());

	public static abstract class MultipleFileGCActionType<A extends MultipleFileGCAction<?>> extends FlexoActionType<A, CGObject, CGObject> {
		protected MultipleFileGCActionType(String actionName, ActionMenu actionMenu, ActionGroup actionGroup, int actionCategory) {
			super(actionName, actionMenu, actionGroup, actionCategory);
		}

		protected MultipleFileGCActionType(String actionName, ActionGroup actionGroup, int actionCategory) {
			super(actionName, actionGroup, actionCategory);
		}

		@Override
		protected boolean isVisibleForSelection(CGObject focusedObject, Vector<CGObject> globalSelection) {
			if (focusedObject instanceof AbstractCGFileVersion) {
				return false;
			}
			Vector<CGObject> topLevelObjects = getSelectedTopLevelObjects(focusedObject, globalSelection);
			for (CGObject obj : topLevelObjects) {
				if (obj instanceof GeneratedOutput) {
					return false;
				}
			}
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(CGObject focusedObject, Vector<CGObject> globalSelection) {
			GenerationRepository repository = getRepository(focusedObject, globalSelection);
			if (repository == null) {
				return false;
			}
			AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator(repository);
			if ((pg == null) || (!pg.hasBeenInitialized())) {
				return false;
			}
			Vector<AbstractCGFile> selectedFiles = getSelectedCGFilesOnWhyCurrentActionShouldApply(focusedObject, globalSelection);
			return selectedFiles.size() > 0;
		}

		protected Vector<AbstractCGFile> getSelectedCGFilesOnWhyCurrentActionShouldApply(CGObject focusedObject,
				Vector<CGObject> globalSelection) {
			Vector<AbstractCGFile> selectedFiles = getSelectedCGFiles(focusedObject, globalSelection);
			Vector<AbstractCGFile> returned = new Vector<AbstractCGFile>();
			for (AbstractCGFile file : selectedFiles) {
				if (!file.getMarkedAsDoNotGenerate() && accept(file)) {
					returned.add(file);
				}
			}

			return returned;
		}

		protected abstract boolean accept(AbstractCGFile aFile);
	}

	/*	public void actionStarted(CGFileRunnable runnable) {
			if (getFlexoProgress()!=null)
				getFlexoProgress().setProgress(runnable.getLocalizedName());
		}
		
		public void actionEnded(CGFileRunnable runnable) {
			
		}
		
		public void actionFailed(CGFileRunnable runnable, Exception e, String message) {
			
		}
		
		public abstract class CGFileRunnable implements Runnable {
			protected AbstractCGFile file;
			
			public CGFileRunnable(AbstractCGFile file) {
				this.file = file;
			}
			
			public void notifyActionStarted() {
				actionStarted(this);
			}
			
			public void notifyActionEnded() {
				actionEnded(this);
			}
			
			public void notifyActionFailed(Exception e, String message) {
				actionFailed(this, e, message);
			}
			
			public abstract String getLocalizedName();
		}
		
		private ThreadPoolExecutor threadPool;
		
		public abstract boolean requiresThreadPool();
		
		public boolean allJobsAreDone() {
			return threadPool.isTerminated();
		}
		*/
	protected MultipleFileGCAction(final MultipleFileGCActionType<A> actionType, CGObject focusedObject, Vector<CGObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		/*if (requiresThreadPool())
			threadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
					new ThreadFactory() {
						public Thread newThread(Runnable r) {
							return new Thread(r);
						}
					}) {
				@Override
				protected void beforeExecute(Thread t, Runnable r) {
					if (r instanceof MultipleFileGCAction<?>.CGFileRunnable)
						actionStarted((CGFileRunnable) r);
					if (r instanceof MultipleFileGCAction<?>.CGFileRunnable)
						t.setName(actionType.getLocalizedName() + " on " + ((MultipleFileGCAction<?>.CGFileRunnable) r).file.getFileName());
					super.beforeExecute(t, r);
				}

				@Override
				protected void afterExecute(Runnable r, Throwable t) {
					super.afterExecute(r, t);
					if (r instanceof MultipleFileGCAction<?>.CGFileRunnable)
						actionEnded((CGFileRunnable) r);
				}

			};*/
	}

	private Vector<AbstractCGFile> files;

	@Override
	public MultipleFileGCActionType<A> getActionType() {
		return (MultipleFileGCActionType<A>) super.getActionType();
	}

	protected Vector<AbstractCGFile> getSelectedCGFilesOnWhyCurrentActionShouldApply() {
		if (files == null) {
			files = getActionType().getSelectedCGFilesOnWhyCurrentActionShouldApply(getFocusedObject(), getGlobalSelection());
		}
		return files;
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		// TODO : explain why there is nothing here
	}

	// public abstract void performActionOnFile(AbstractCGFile file);

	/*    public void addJob(MultipleFileGCAction<?>.CGFileRunnable job) {
	    	threadPool.execute(job);
	    }
	    
	    public void waitForAllJobsToComplete() {
	    	/*
			 * The next line does not stop everything. It simply prevents new job from being added to the pool and allows the method
			 * isTerminated() to return true once all jobs are completed
			 */
	/*		threadPool.shutdown();
			while (!threadPool.isTerminated()) {
				synchronized (this) {
					try {
						wait(300);
						getEditor().performPendingActions();
					} catch (InterruptedException e) {
					}
				}
			}

	    }
	*/
}
