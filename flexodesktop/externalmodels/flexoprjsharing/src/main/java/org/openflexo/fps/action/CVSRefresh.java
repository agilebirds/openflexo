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
package org.openflexo.fps.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSConstants;
import org.openflexo.fps.CVSExplorable;
import org.openflexo.fps.CVSExplorer;
import org.openflexo.fps.CVSExplorerListener;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.FlexoAuthentificationException;
import org.openflexo.localization.FlexoLocalization;

public class CVSRefresh extends CVSAction<CVSRefresh,FPSObject> implements CVSExplorerListener
{

    private static final Logger logger = Logger.getLogger(CVSRefresh.class.getPackage().getName());

    public static FlexoActionType<CVSRefresh,FPSObject,FPSObject> actionType 
    = new FlexoActionType<CVSRefresh,FPSObject,FPSObject> 
    ("refresh",FlexoActionType.defaultGroup,FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public CVSRefresh makeNewAction(FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) 
        {
            return new CVSRefresh(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(FPSObject object, Vector<FPSObject> globalSelection) 
        {
            return (object instanceof CVSRepositoryList) 
            || (object instanceof CVSRepository)
            || (object instanceof CVSModule);
        }

        @Override
		protected boolean isEnabledForSelection(FPSObject object, Vector<FPSObject> globalSelection) 
        {
            return isVisibleForSelection(object,globalSelection);
        }
                
    };
    
    static {
        FlexoModelObject.addActionForClass (actionType, FPSObject.class);
    }
 
     CVSRefresh (FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor)
    {
    	super(actionType, focusedObject, globalSelection, editor);
    }

     private Vector<CVSExplorer> explorers;

     private boolean timeOutReceived =false;
     private long lastReception;
     private static final long TIME_OUT = CVSConstants.TIME_OUT; // 60 s
     private int explorersToWait = 0;
     private Vector<CVSExplorable> explorersToNotify = new Vector<CVSExplorable>();
     private Vector<CVSExplorable> explorableFailed = new Vector<CVSExplorable>();
     private HashMap<FlexoException, CVSExplorer> repositoryInformationRequired = new HashMap<FlexoException, CVSExplorer>();
     
    @Override
	protected void doAction(Object context) throws IOFlexoException, FlexoAuthentificationException
    {
    	logger.info ("CVSRefresh");
    	
    	explorers = new Vector<CVSExplorer>();
    	
    	if (getFocusedObject() instanceof CVSRepositoryList) {
    		for (CVSRepository rep : ((CVSRepositoryList)getFocusedObject()).getCVSRepositories()) {
    			logger.info ("CVSRefresh for "+rep);
    			explorers.add(rep.exploreRepository(this));
    		}
    	}
    	else if (getFocusedObject() instanceof CVSRepository) {
    		explorers.add(((CVSRepository)getFocusedObject()).exploreRepository(this));
    	}
    	else if (getFocusedObject() instanceof CVSModule) {
    		explorers.add(((CVSModule)getFocusedObject()).exploreModule(this));
    	}
    	explorersToWait = explorers.size();
    	waitResponses();
    	if (explorersToWait > 0) {
    		timeOutReceived = true;
    		logger.warning("Exploration finished with time-out expired: still waiting for "+explorersToWait+" files");
    	}
    }

    private void waitResponses()
    {
    	setProgress(FlexoLocalization.localizedForKey("waiting_for_responses"));
    	resetSecondaryProgress(explorersToWait);
    	
    	lastReception = System.currentTimeMillis();
    	
    	while (explorersToWait > 0 && System.currentTimeMillis() - lastReception < TIME_OUT) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (this) {
				while (repositoryInformationRequired.size()>0) {
					Iterator<FlexoException> i = repositoryInformationRequired.keySet().iterator();
					while (i.hasNext()) {
						FlexoException e = i.next();
						i.remove();
						handleException(repositoryInformationRequired.get(e), e);
					}
				}
				while (explorersToNotify.size() > 0) {
					CVSExplorable explorable = explorersToNotify.firstElement();
					explorersToNotify.removeElementAt(0);
					setSecondaryProgress(FlexoLocalization.localizedForKey("received_response_for") + " " + explorable);
				}
			}
		}
    	
     }
    
	@Override
	public synchronized void exploringFailed(CVSExplorable explorable, CVSExplorer explorer, Exception exception)
	{
		if (exception instanceof FlexoAuthentificationException || exception instanceof FlexoUnknownHostException) {
			if (getExceptionHandler() != null) {
				repositoryInformationRequired.put((FlexoException)exception, explorer); 
				return;
			}
		}
		if (logger.isLoggable(Level.WARNING))
			logger.warning("Failed to explorer "+explorable);
		explorersToWait--;
    	explorersToNotify.add(explorable);
    	lastReception = System.currentTimeMillis();
		explorableFailed.add(explorable);
	}

	/**
	 * @param explorer
	 * @param exception
	 */
	private void handleException(CVSExplorer explorer, FlexoException exception) {
		
		if (getExceptionHandler().handleException(exception, this)) {
			// Means that a new password was supplied
			// So we try again
			explorer.explore();
			return;
		}
	}

	@Override
	public synchronized void exploringSucceeded(CVSExplorable explorable, CVSExplorer explorer) 
	{
		logger.info("Exploring "+explorable+" was successfull");
    	explorersToWait--;
    	explorersToNotify.add(explorable);
    	lastReception = System.currentTimeMillis();
	}

	public boolean hasReceivedTimeout() {
		return timeOutReceived;
	}

	public Vector<CVSExplorable> getExplorableFailed() {
		return explorableFailed;
	}

 }
