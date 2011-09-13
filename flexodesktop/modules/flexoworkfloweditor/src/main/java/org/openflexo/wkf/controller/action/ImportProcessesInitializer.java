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
package org.openflexo.wkf.controller.action;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.xml.rpc.ServiceException;

import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.ws.client.PPMWebService.PPMProcess;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceClient;


import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.gen.FlexoProcessImageBuilder;
import org.openflexo.foundation.imported.action.ImportProcessesAction;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ReadOnlyCheckboxParameter;


public class ImportProcessesInitializer extends ActionInitializer
{

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public ImportProcessesInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(ImportProcessesAction.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer()
	{
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	PPMWebServiceClient client = null;
	
	@Override
	protected FlexoActionInitializer<ImportProcessesAction> getDefaultInitializer()
	{
		return new FlexoActionInitializer<ImportProcessesAction>()
		{
			@Override
			public boolean run(ActionEvent e, ImportProcessesAction action)
			{
				boolean isFirst = true;
				PPMProcess[] processes = null;
				while(processes==null) {
					client = getController().getWSClient(!isFirst);
					isFirst = false;
					if (client==null)
						return false;// Cancelled
					try {
						processes = client.getProcesses();
					} catch (PPMWebServiceAuthentificationException e1) {
						getController().handleWSException(e1);
					} catch (RemoteException e1) {
						getController().handleWSException(e1);
					} catch (ServiceException e1) {
						getController().handleWSException(e1);
					}
				}
				if(processes!=null){
					Vector<PPMProcess> processesToImport = selectProcessesToImport(processes);
					if(processesToImport!=null && processesToImport.size()>0){
						action.setProcessesToImport(processesToImport);
						return true;
					}
				}
				return false;
			}
		};
	}

	
	private Vector<PPMProcess> selectProcessesToImport(PPMProcess[] processes) {
		Arrays.sort(processes, new Comparator<PPMProcess>() {
			@Override
			public int compare(PPMProcess o1, PPMProcess o2) {
				if (o1.getName()==null) {
					if (o2.getName()==null)
						return 0;
					else
						return -1;
				} else if (o2.getName()==null) {
					return 1;
				} else
					return o1.getName().compareTo(o2.getName());
			}
		});
		ParameterDefinition[] parameters = new ParameterDefinition[processes.length];
		for(int i=0;i<processes.length;i++){
			PPMProcess itemProcess = processes[i];
			if(getProject().getWorkflow().getProcessWithURI(itemProcess.getUri())==null){
				CheckboxParameter cb = new CheckboxParameter("process"+i, itemProcess.getName(), false);
				cb.setLocalizedLabel(itemProcess.getName());
				parameters[i] = cb;
			} else {
				ReadOnlyCheckboxParameter cb = new ReadOnlyCheckboxParameter("process"+i, itemProcess.getName(), true);
				cb.setLocalizedLabel(itemProcess.getName());
				parameters[i] = cb;
			}
		}
		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), FlexoLocalization.localizedForKey("import_processes"), FlexoLocalization.localizedForKey("select_processes_to_import"), parameters);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE){
			Vector<PPMProcess> reply = new Vector<PPMProcess>();
			for(int i=0;i<processes.length;i++){
				if(parameters[i].getBooleanValue() && !(parameters[i] instanceof ReadOnlyCheckboxParameter))
					reply.add(processes[i]);
			}
			return reply;
		}else{
			return null;
		}
	}
	

	@Override
	public WKFController getController() {
		return (WKFController) super.getController();
	}
	
	@Override
	protected FlexoActionFinalizer<ImportProcessesAction> getDefaultFinalizer()
	{
		return new FlexoActionFinalizer<ImportProcessesAction>()
		{
			@Override
			public boolean run(ActionEvent e, ImportProcessesAction action)
			{
				if (action.getImportReport()!=null && action.getImportReport().getProperlyImported().size()>0) {
					/*getController().getSelectionManager().resetSelection();
					for (FIProcess process : action.getImportReport().getProperlyImported().values()) {
						getController().getSelectionManager().addToSelected(process);
					}*/
					getController().getSelectionManager().setSelectedObject(action.getImportReport().getProperlyImported().values().iterator().next());
					if(client!=null)
						FlexoProcessImageBuilder.startBackgroundDownloadOfSnapshots(action.getImportReport().getProperlyImported().values(), client.getWebService_PortType(), client.getLogin(), client.getEncriptedPWD());
				}
				FlexoController.notify(action.getImportReport().toString());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon()
	{
		return WKFIconLibrary.PROCESS_ICON;
	}

}
