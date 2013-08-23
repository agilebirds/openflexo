package org.openflexo.rest.client;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.openflexo.rest.client.model.User;
import org.openflexo.rest.client.model.UserType;
import org.openflexo.swing.FlexoSwingUtils;
import org.openflexo.toolbox.ZipUtils;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoServerInstance;
import org.openflexo.view.controller.FlexoServerInstanceManager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

public class ServerRestService {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(ServerRestService.class.getPackage()
			.getName());

	private static final List<String> HTML_FILE_ORDER = Arrays.asList("index.html", "main.html");

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
			User currentUser = client.users(createClient).id(client.getUserName()).getAsUserXml();
			for (FlexoProject project : projectLoader.getRootProjects()) {
				EntityManager em = LocalDBAccess.getInstance().getEntityManager();
				try {
					TypedQuery<WatchedRemoteJob> query;
					if (currentUser.getUserType() == UserType.ADMIN) {
						query = em.createNamedQuery(WatchedRemoteJob.FindByProjectURI.NAME, WatchedRemoteJob.class).setParameter(
								WatchedRemoteJob.FindByProjectURI.PROJECT_URI_PARAM, project.getProjectURI());
					} else {
						query = em.createNamedQuery(WatchedRemoteJob.FindByProjectURIAndLogin.NAME, WatchedRemoteJob.class)
								.setParameter(WatchedRemoteJob.FindByProjectURIAndLogin.PROJECT_URI_PARAM, project.getProjectURI())
								.setParameter(WatchedRemoteJob.FindByProjectURIAndLogin.LOGIN_PARAM, currentUser.getLogin());
					}
					List<WatchedRemoteJob> watchedRemoteJobs = query.getResultList();
					for (WatchedRemoteJob watchedRemoteJob : watchedRemoteJobs) {
						try {
							if (watchedRemoteJob instanceof WatchedRemoteDocJob) {
								handleRemoteDocJob(client, createClient, em, (WatchedRemoteDocJob) watchedRemoteJob);
							}
						} catch (SocketException e) {
							// Issues with connection (no Internet, server down, etc...)
							return;
						} catch (IOException e) {
							logger.log(Level.WARNING, "IOException while trying to handle ", e);

						} catch (WebApplicationException e) {
							if (e.getResponse().getStatus() == com.sun.jersey.api.client.ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
								AdvancedPrefs.setRememberAndDontAskWebServiceParamsAnymore(false);
								AdvancedPrefs.save();
							}
						} catch (Exception e) {
							logger.log(Level.WARNING, "Unexpected exception while trying to handle ", e);
							if (em.getTransaction().isActive()) {
								em.getTransaction().rollback();
							}
							em.getTransaction().begin();
							watchedRemoteJob.setFailedAttempt(watchedRemoteJob.getFailedAttempt() + 1);
							em.getTransaction().commit();
						}
					}
				} finally {
					try {
						if (em.getTransaction() != null && em.getTransaction().isActive()) {
							em.getTransaction().rollback();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					em.close();
				}
			}
		}

		private void handleRemoteDocJob(ServerRestClient client, Client createClient, EntityManager em, WatchedRemoteDocJob watchedRemoteJob)
				throws Exception {
			UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("originalJobId", watchedRemoteJob.getRemoteJobId());
			List<JobHistory> jobHistory = client.jobsHistory(createClient, builder.build()).getAsXml(new GenericType<List<JobHistory>>() {
			});
			if (jobHistory.size() > 0) {
				JobHistory history = jobHistory.get(0);
				String uuid = history.getJobResult();
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

				File saveToFile = null;
				File fileToOpen = null;
				InputStream input = null;
				FileOutputStream fos = null;
				try {
					File saveToFolder = new File(watchedRemoteJob.getSaveToFolder());
					if (watchedRemoteJob.isUnzip()) {
						saveToFile = File.createTempFile(StringUtils.rightPad(fileName, 3, '_'), ".zip");
					} else {
						saveToFile = new File(saveToFolder, fileName);
						fileToOpen = saveToFile;
					}
					saveToFile.getParentFile().mkdirs();
					fos = new FileOutputStream(saveToFile);
					input = response.getEntity(InputStream.class);
					IOUtils.copy(input, fos);
					if (watchedRemoteJob.isUnzip()) {
						ZipUtils.unzip(saveToFile, saveToFolder);
						Collection<File> listFiles = FileUtils.listFiles(saveToFolder, new String[] { "html" }, false);
						if (listFiles.size() == 0) {
							listFiles = FileUtils.listFiles(saveToFolder, new String[] { "html" }, true);
						}
						if (listFiles.size() == 0) {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("Could not find html file in zip extracted at: " + saveToFolder.getAbsolutePath());
							}
							fileToOpen = saveToFile;
						} else if (listFiles.size() == 1) {
							fileToOpen = listFiles.iterator().next();
						} else {
							fileToOpen = null;
							int curIndex = Integer.MAX_VALUE;
							for (File f : listFiles) {
								if (fileToOpen == null) {
									fileToOpen = f;
								} else {
									int index = HTML_FILE_ORDER.indexOf(f.getName().toLowerCase());
									if (index > -1 && index < curIndex) {
										curIndex = index;
										fileToOpen = f;
									}
								}
							}
						}
					}
					em.getTransaction().begin();
					em.remove(watchedRemoteJob);
					em.getTransaction().commit();
				} finally {
					IOUtils.closeQuietly(input);
					IOUtils.closeQuietly(fos);
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}
					if (watchedRemoteJob.isOpenDocument()) {
						if (Desktop.isDesktopSupported()) {
							try {
								Desktop.getDesktop().open(saveToFile);
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
