package org.openflexo.rest.client;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.openflexo.AdvancedPrefs;
import org.openflexo.LocalDBAccess;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.module.ProjectLoader;
import org.openflexo.rest.client.WebServiceURLDialog.ServerRestClientParameter;
import org.openflexo.rest.client.model.JobHistory;
import org.openflexo.swing.FlexoSwingUtils;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoServerInstance;
import org.openflexo.view.controller.FlexoServerInstanceManager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;

public class ServerRestService {

	private boolean started = false;

	private class RemoteJobChecker implements Runnable {
		@Override
		public void run() {
			ServerRestClient client = null;
			boolean firstAttempt = true;
			while (client == null) {
				ServerRestClientParameter params = getServerRestClientParameter(!firstAttempt);
				if (params == null) {
					return;
				}
				try {
					client = new ServerRestClient(new URI(params.getWSURL()));
					client.setUserName(params.getWSLogin());
					client.setPassword(params.getWSPassword());
				} catch (URISyntaxException e) {
					e.printStackTrace();
					return;
				}
			}
			Client createClient = client.createClient();
			for (FlexoProject project : projectLoader.getRootProjects()) {
				EntityManager em = LocalDBAccess.getInstance().getEntityManager();
				try {
					TypedQuery<WatchedRemoteJob> query = em
							.createNamedQuery(WatchedRemoteJob.FindByProjectURI.NAME, WatchedRemoteJob.class).setParameter(
									WatchedRemoteJob.FindByProjectURI.PROJECT_URI_PARAM, project.getProjectURI());
					List<WatchedRemoteJob> watchedRemoteJobs = query.getResultList();
					for (WatchedRemoteJob watchedRemoteJob : watchedRemoteJobs) {
						if (watchedRemoteJob instanceof WatchedRemoteDocJob) {
							handleRemoteDocJob(client, createClient, em, (WatchedRemoteDocJob) watchedRemoteJob);
						}
					}
				} finally {
					em.close();
				}
			}
		}

		private void handleRemoteDocJob(ServerRestClient client, Client createClient, EntityManager em, WatchedRemoteDocJob watchedRemoteJob) {
			UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("originalJobId", watchedRemoteJob.getRemoteJobId());
			List<JobHistory> jobHistory = client.jobsHistory(createClient, builder.build()).getAsXml(new GenericType<List<JobHistory>>() {
			});
			if (jobHistory.size() > 0) {
				String uuid = jobHistory.get(0).getJobResult();
				ClientResponse response = client.files(createClient, client.getBASE_URI()).getAsOctetStream(uuid, ClientResponse.class);
				String fileName = uuid;
				List<String> list = response.getHeaders().get("content-disposition");
				for (String string : list) {
					int indexOf = string.toLowerCase().indexOf("filename");
					if (indexOf > -1) {
						string = string.substring(indexOf + "filename".length());
						indexOf = string.indexOf('=');
						fileName = string.substring(indexOf + 1).trim();
					}
				}
				File file = new File(watchedRemoteJob.getSaveToFolder(), fileName);
				file.getParentFile().mkdirs();
				InputStream input = null;
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(file);
					input = response.getEntity(InputStream.class);
					IOUtils.copy(input, fos);
					em.getTransaction().begin();
					em.remove(watchedRemoteJob);
					em.getTransaction().commit();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (ClientHandlerException e) {
					e.printStackTrace();
				} catch (UniformInterfaceException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(input);
					IOUtils.closeQuietly(fos);
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}
					if (watchedRemoteJob.isOpenDocument()) {
						if (Desktop.isDesktopSupported()) {
							try {
								Desktop.getDesktop().open(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	private class ServerRestClientParameterProvider {

		private class ParameterCallable implements Callable<ServerRestClientParameter> {

			private ServerRestClientParameter params;

			public ParameterCallable(ServerRestClientParameter params) {
				super();
				this.params = params;
			}

			@Override
			public ServerRestClientParameter call() throws Exception {
				WebServiceURLDialog data = new WebServiceURLDialog();
				data.setClientParameter(params);
				FIBDialog<WebServiceURLDialog> dialog = FIBDialog.instanciateAndShowDialog(WebServiceURLDialog.FIB_FILE, data,
						FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
				if (dialog.getStatus() == Status.VALIDATED) {
					if (params.getWSInstance() != null && !params.getWSInstance().getID().equals(FlexoServerInstance.OTHER_ID)) {
						params.setWSURL(params.getWSInstance().getRestURL());
					}
					String password = params.getWSPassword();
					params.setWSPassword(params.getWSPassword());
					if (params.getWSURL() == null) {

					}
					try {
						new URI(params.getWSURL());
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
						FlexoController.notify(e1.getMessage());
						AdvancedPrefs.setRememberAndDontAskWebServiceParamsAnymore(false);
						AdvancedPrefs.save();
						return null;
					}
					if (params.getWSInstance() != null) {
						AdvancedPrefs.setWebServiceInstance(params.getWSInstance().getID());
					}
					if (params.getWSURL() != null) {
						AdvancedPrefs.setWebServiceUrl(params.getWSURL());
					}
					if (params.getWSLogin() != null) {
						AdvancedPrefs.setWebServiceLogin(params.getWSLogin());
					}
					if (params.getWSPassword() != null) {
						AdvancedPrefs.setWebServicePassword(params.getWSPassword());
					}
					AdvancedPrefs.setRememberAndDontAskWebServiceParamsAnymore(params.getRemember());
					AdvancedPrefs.save();
					return params;
				} else {
					return null;
				}
			}

		}

		public ServerRestClientParameter getServerRestClientParameter(boolean forceDialog) throws Error {

			ModelFactory factory;
			try {
				factory = new ModelFactory(ServerRestClientParameter.class);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
				throw new Error("Improperly configured PPMWSClientParameter. ", e);
			}
			FlexoServerInstance instance = FlexoServerInstanceManager.getInstance().getAddressBook()
					.getInstanceWithID(AdvancedPrefs.getWebServiceInstance());
			ServerRestClientParameter params = factory.newInstance(ServerRestClientParameter.class);
			params.setWSInstance(instance);
			params.setWSLogin(AdvancedPrefs.getWebServiceLogin());
			params.setWSPassword(AdvancedPrefs.getWebServicePassword());
			params.setWSURL(AdvancedPrefs.getWebServiceUrl());
			params.setRemember(AdvancedPrefs.getRememberAndDontAskWebServiceParamsAnymore());
			if (params.getWSInstance() != null && !params.getWSInstance().getID().equals(FlexoServerInstance.OTHER_ID)) {
				params.setWSURL(params.getWSInstance().getRestURL());
			}
			if (forceDialog || !params.getRemember() || params.getWSURL() == null || params.getWSLogin() == null
					|| params.getWSPassword() == null || !isWSUrlValid(params.getWSURL()) || urlSeemsIncorrect(params.getWSURL())) {
				try {
					params = FlexoSwingUtils.syncRunInEDT(new ParameterCallable(params));
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			return params;
		}

		private boolean isWSUrlValid(final String wsURL) {
			if (wsURL == null) {
				return false;
			}
			try {
				new URI(wsURL);
			} catch (URISyntaxException e) {
				return false;
			}
			return wsURL != null
					&& (wsURL.toLowerCase().startsWith("http://") && wsURL.charAt(7) != '/' || wsURL.toLowerCase().startsWith("https://")
							&& wsURL.charAt(8) != '/');
		}

		private boolean urlSeemsIncorrect(String wsURL) {
			if (wsURL == null) {
				return true;
			}
			try {
				new URI(wsURL);
			} catch (URISyntaxException e) {
				return true;
			}
			try {
				URL url = new URL(wsURL);
				// Bug 1007330 Fix
				return url.getAuthority() == null;
			} catch (MalformedURLException e) {
				return true;
			}
		}

	}

	private ScheduledFuture<?> scheduledFuture;
	private final ProjectLoader projectLoader;
	private ServerRestClientParameterProvider parameterProvider;

	public ServerRestService(ProjectLoader projectLoader) {
		this.projectLoader = projectLoader;
		parameterProvider = new ServerRestClientParameterProvider();
	}

	public ServerRestClient getWSClient(boolean forceDialog) {
		ServerRestClientParameter params = getServerRestClientParameter(forceDialog);
		// now that we have the parameters. We have to invoke the WS
		ServerRestClient client;
		try {
			client = new ServerRestClient(new URI(params.getWSURL()));
			client.setUserName(params.getWSLogin());
			client.setPassword(params.getWSPassword());
			return client;
		} catch (URISyntaxException e) {
			// Should not happen
			e.printStackTrace();
			return null;
		}
	}

	public ServerRestClientParameter getServerRestClientParameter(boolean forceDialog) {
		return parameterProvider.getServerRestClientParameter(forceDialog);
	}

	public void init() {
		if (started) {
			return;
		}
		started = true;
		scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new RemoteJobChecker(), 0, 15,
				TimeUnit.SECONDS);
	}

	public void stop() {
		if (started) {
			scheduledFuture.cancel(true);
			started = false;
		}
	}
}
