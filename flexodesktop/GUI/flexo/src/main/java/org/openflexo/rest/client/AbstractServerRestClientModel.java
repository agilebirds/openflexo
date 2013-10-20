package org.openflexo.rest.client;

import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.Holder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openflexo.components.ProgressWindow;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.client.WebServiceURLDialog.ServerRestClientParameter;
import org.openflexo.rest.client.model.Account;
import org.openflexo.rest.client.model.Project;
import org.openflexo.rest.client.model.ProjectVersion;
import org.openflexo.rest.client.model.Session;
import org.openflexo.rest.client.model.User;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.utils.CancelException;
import org.openflexo.utils.TooManyFailedAttemptException;
import org.openflexo.view.controller.FlexoController;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class AbstractServerRestClientModel implements HasPropertyChangeSupport {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(AbstractServerRestClientModel.class
			.getPackage().getName());
	private static final String STATUS = "status";

	private String status;

	protected interface Progress {
		public void increment(String message);

		public void setSteps(int steps);
	}

	public interface ServerRestClientOperation {
		/**
		 * Performs the operation using the provided <code>client</code> and notifies the user through the provided <code>progress</code>.
		 * 
		 * @param client
		 *            the REST client to use
		 * @param progress
		 *            the progress callback to notify of the operation progress
		 * @throws IOException
		 *             in case an IOException occurs (mainly SocketException, but could also be related to disk errors)
		 * @throws WebApplicationException
		 *             in case an error occurs during a remote operation.
		 */
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException;

		/**
		 * The general title for this operation
		 * 
		 * @return the general title for this operation
		 */
		public String getLocalizedTitle();

		/**
		 * Number of steps required for this operation. It should match the number of times the method {@link Progress#increment(String)} is
		 * called. If the value is negative or zero (preferably zero), the progress is indeterminate.
		 * 
		 * @return the number of steps required for this operation.
		 */
		public int getSteps();
	}

	public class UpdateUserOperation implements ServerRestClientOperation {
		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			try {
				User user = client.users().id(client.getUserName()).getAsUserXml();
				if (user != null && user.getClientAccount() != null) {
					progress.increment(FlexoLocalization.localizedForKey("retrieving_account_info"));
					setAccount(client.accounts().idSummary(user.getClientAccount()).getAsAccountXml());
				}
				setUser(user);
			} finally {
				if (getUser() == null) {
					setStatus(FlexoLocalization.localizedForKey("could_not_identify_user"));
				}
			}
		}

		@Override
		public int getSteps() {
			return 2;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("retrieving_user_information");
		}

	}

	public class DownloadProjectVersion implements ServerRestClientOperation {

		private final Project project;
		private final ProjectVersion version;

		private boolean success = false;
		private File file;

		private long bytesRead = 0;
		private long lastByteProgressReport = 0;
		private boolean startEditionSession;

		public DownloadProjectVersion(Project project, ProjectVersion version, boolean startEditionSession) {
			super();
			this.project = project;
			this.version = version;
			this.startEditionSession = startEditionSession;
		}

		@Override
		public void doOperation(ServerRestClient client, final Progress progress) throws IOException, WebApplicationException {
			Client createClient = client.createClient();
			if (startEditionSession) {
				Session session = new Session();
				session.setUser(getUser());
				session.setVersion(version);
				try {
					session.setDownloadDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
				} catch (DatatypeConfigurationException e1) {
					e1.printStackTrace();
				}
				client.projectsProjectIDSessions(createClient, version.getProject()).postXmlAsSession(session);
			}
			ClientResponse response = client.projectsProjectIDVersions(createClient, version.getProject()).idFile(version.getVersionID())
					.getAsOctetStream(ClientResponse.class);
			if (response.getStatus() >= 400) {
				String message = "";
				try {
					message = IOUtils.toString(response.getEntityInputStream(), "utf-8");
				} catch (IOException e) {
					e.printStackTrace();
				}
				throw new WebApplicationException(Response.fromResponse(Response.status(response.getClientResponseStatus()).build())
						.entity(message).build());
			}
			List<String> list = response.getHeaders().get("Content-Length");
			long length = -1;
			if (list != null) {
				for (String string : list) {
					try {
						length = Long.valueOf(string);
						break;
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
			if (length < 1) {
				String fileLength = client.projectsProjectIDVersions(version.getProject()).idFileLength(version.getVersionID())
						.getAsTextPlain(String.class);
				try {
					length = Long.valueOf(fileLength);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			file = File.createTempFile(StringUtils.rightPad(project.getName() + "_" + version.getVersionNumber(), 3, '_'), ".zip");
			bytesRead = 0;
			final int stepSize = length > 0 ? (int) length / 1000 : -1;
			FileOutputStream fos = new FileOutputStream(file) {

				@Override
				public void write(byte[] b) throws IOException {
					super.write(b);
					bytesRead += b.length;
					updateDisplay(progress, stepSize);
				}

				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					super.write(b, off, len);
					bytesRead += len;
					updateDisplay(progress, stepSize);
				}

				@Override
				public void write(int b) throws IOException {
					super.write(b);
					bytesRead++;
					updateDisplay(progress, stepSize);
				}

				private void updateDisplay(final Progress progress, final int stepSize) {
					while (stepSize > 0 && bytesRead - lastByteProgressReport > stepSize) {
						double percent = (double) bytesRead / (10 * stepSize);
						percent = Math.min(percent, 100.0);
						progress.increment(String.format("%1$.2f", percent) + "%");
						lastByteProgressReport += stepSize;
					}
					lastByteProgressReport = bytesRead;
				}
			};
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			try {
				IOUtils.copy(response.getEntity(InputStream.class), bos);
			} finally {
				IOUtils.closeQuietly(bos);
			}
			success = true;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("downloading") + " " + project.getName() + " " + version.getVersionNumber();
		}

		@Override
		public int getSteps() {
			return 1000;
		}

		public File getFile() {
			if (success) {
				return file;
			}
			return null;
		}

	}

	protected static final String DELETED = "deleted";
	private static final String ACCOUNT = "account";
	private static final String USER = "user";
	private final ServerRestService serverRestService;
	private final Window owner;
	private User user;
	private Account account;
	protected PropertyChangeSupport pcSupport;

	public AbstractServerRestClientModel(ServerRestService serverRestService, Window owner) {
		super();
		this.serverRestService = serverRestService;
		this.owner = owner;
		this.pcSupport = new PropertyChangeSupport(this);
	}

	public void delete() {
		pcSupport.firePropertyChange(DELETED, false, true);
	}

	public boolean handleWSException(final Exception e) throws InterruptedException {
		if (!SwingUtilities.isEventDispatchThread()) {
			final Holder<Boolean> returned = new Holder<Boolean>();
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						returned.value = _handleWSException(e);
					}
				});
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
				return false;
			}
			return returned.value != null && returned.value;
		}
		return _handleWSException(e);
	}

	private boolean _handleWSException(Throwable e) {
		if (e instanceof RuntimeException && e.getCause() != null) {
			e = e.getCause();
		}
		if (e.getCause() instanceof TooManyFailedAttemptException) {
			throw (TooManyFailedAttemptException) e.getCause();
		}
		if (e.getCause() instanceof CancelException) {
			throw (CancelException) e.getCause();
		}
		if (logger.isLoggable(Level.SEVERE)) {
			logger.log(Level.SEVERE, "An error ocurred " + (e.getMessage() == null ? "no message" : e.getMessage()), e);
		}
		if (e.getMessage() != null && e.getMessage().startsWith("redirect")) {
			String location = null;
			if (e.getMessage().indexOf("Location") > -1) {
				location = e.getMessage().substring(e.getMessage().indexOf("Location") + 9).trim();
			}
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_connect_to_web_sevice") + ": "
					+ FlexoLocalization.localizedForKey("the_url_seems_incorrect")
					+ (location != null ? "\n" + FlexoLocalization.localizedForKey("try_with_this_one") + " " + location : ""));
			return false;
		}
		if (e instanceof WebApplicationException) {
			WebApplicationException wae = (WebApplicationException) e;
			Object entity = wae.getResponse().getEntity();
			switch (wae.getResponse().getStatus()) {
			case 500:
				return FlexoController.confirm(FlexoLocalization.localizedForKey("webservice_remote_error") + entity + "\n"
						+ FlexoLocalization.localizedForKey("would_you_like_to_try_again?"));
			case 401:
				return FlexoController.confirm(FlexoLocalization.localizedForKey("unauthorized_action_on_the_server") + entity + "\n"
						+ FlexoLocalization.localizedForKey("would_you_like_to_try_again?"));
			default:
				if (entity != null) {
					return FlexoController.confirm(entity.toString() + "\n"
							+ FlexoLocalization.localizedForKey("would_you_like_to_try_again?"));
				} else {
					return FlexoController.confirm(FlexoLocalization.localizedForKey("unexpected_error_occured_while_connecting_to_server")
							+ "\n" + FlexoLocalization.localizedForKey("would_you_like_to_try_again?"));
				}
			}
		}
		if (e.getCause() instanceof ConnectException) {
			return FlexoController.confirm(FlexoLocalization.localizedForKey("connection_error")
					+ (e.getCause().getMessage() != null ? " (" + e.getCause().getMessage() + ")" : "") + "\n"
					+ FlexoLocalization.localizedForKey("would_you_like_to_try_again?"));
		} else if (e.toString() != null && e.toString().startsWith("javax.net.ssl.SSLHandshakeException")) {
			return FlexoController.confirm(FlexoLocalization.localizedForKey("connection_error") + ": " + e + "\n"
					+ FlexoLocalization.localizedForKey("would_you_like_to_try_again?"));
		} else if (e instanceof SocketTimeoutException) {
			return FlexoController.confirm(FlexoLocalization.localizedForKey("connection_timeout") + "\n"
					+ FlexoLocalization.localizedForKey("would_you_like_to_try_again?"));
		} else if (e instanceof IOException || e.getCause() instanceof IOException) {
			IOException ioEx = (IOException) (e instanceof IOException ? e : e.getCause());
			return FlexoController.confirm(FlexoLocalization.localizedForKey("connection_error") + ": "
					+ FlexoLocalization.localizedForKey(ioEx.getClass().getSimpleName()) + " " + ioEx.getMessage() + "\n"
					+ FlexoLocalization.localizedForKey("would_you_like_to_try_again?"));
		} else {
			if (e.getMessage() != null && e.getMessage().indexOf("Content is not allowed in prolog") > -1) {
				FlexoController
						.notify("Check your connection url in FlexoPreferences > Advanced.\n It seems wrong.\nsee logs for details.");
				return false;
			} else {
				return FlexoController
						.confirm(FlexoLocalization.localizedForKey("webservice_remote_error")
								+ " \n"
								+ (e.getMessage() == null || "java.lang.NullPointerException".equals(e.getMessage()) ? "Check your connection parameters.\nThe service may be temporary unavailable."
										: e.getMessage()) + "\n" + FlexoLocalization.localizedForKey("would_you_like_to_try_again?"));
			}
		}
		/*FlexoController.notify(FlexoLocalization.localizedForKey("webservice_connection_failed"));
		FlexoController.notify(FlexoLocalization.localizedForKey("webservice_authentification_failed") + ": "
				+ FlexoLocalization.localizedForKey(e.getMessage1()));*/
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		pcSupport.firePropertyChange(STATUS, null, status);
	}

	protected ServerRestClient getServerRestClient(boolean forceDialog) {
		ServerRestClientParameter params = serverRestService.getServerRestClientParameter(forceDialog);
		if (params == null) {
			return null;
		}
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

	public ServerRestService getServerRestService() {
		return serverRestService;
	}

	public void performOperationsInSwingWorker(ServerRestClientOperation... operations) {
		performOperationsInSwingWorker(true, false, operations);
	}

	public void performOperationsInSwingWorker(final boolean useProgressWindow, final boolean synchronous,
			final ServerRestClientOperation... operations) {
		if (useProgressWindow) {
			ProgressWindow.makeProgressWindow("", operations.length);
		}
		final JDialog dialog = synchronous ? new JDialog(useProgressWindow ? ProgressWindow.instance() : owner) : null;
		if (dialog != null) {
			dialog.setUndecorated(true);
			dialog.setSize(0, 0);
			dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		}
		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {

			@Override
			protected Void doInBackground() throws Exception {
				performOperations(useProgressWindow, operations);
				return null;
			}

			@Override
			protected void done() {
				super.done();
				if (dialog != null) {
					dialog.setVisible(false);
					dialog.dispose();
				} else {
					if (useProgressWindow) {
						ProgressWindow.hideProgressWindow();
					}
				}
			}
		};
		worker.execute();
		if (dialog != null) {
			dialog.setVisible(true);
			if (useProgressWindow) {
				ProgressWindow.hideProgressWindow();
			}
		}
	}

	public void goOnline() {
		serverRestService.setOffline(false);
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	public User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
		pcSupport.firePropertyChange(USER, null, user);
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
		pcSupport.firePropertyChange(ACCOUNT, null, account);
	}

	public void performOperations(final boolean useProgressWindow, final ServerRestClientOperation... operations)
			throws InterruptedException {
		boolean firstAttempt = true;
		try {
			for (final ServerRestClientOperation operation : operations) {
				boolean done = false;
				while (!done) {
					ServerRestClient client = getServerRestClient(!firstAttempt);
					if (client == null) {
						return;
					}
					if (useProgressWindow) {
						ProgressWindow.setProgressInstance(operation.getLocalizedTitle());
						int steps = operation.getSteps();
						ProgressWindow.resetSecondaryProgressInstance(steps);
					}
					try {
						operation.doOperation(client, new Progress() {
							@Override
							public void increment(String message) {
								if (useProgressWindow) {
									ProgressWindow.setSecondaryProgressInstance(message);
								}
							}

							@Override
							public void setSteps(int steps) {
								if (useProgressWindow) {
									ProgressWindow.makeProgressWindow(operation.getLocalizedTitle(), steps);
								}
							}

						});
						firstAttempt = true;
						done = true;
					} catch (WebApplicationException e) {
						e.printStackTrace();
						if (!handleWSException(e)) {
							return;
						}
						firstAttempt = false;
					} catch (IOException e) {
						e.printStackTrace();
						if (!handleWSException(e)) {
							return;
						}
						firstAttempt = false;
					} catch (RuntimeException e) {
						e.printStackTrace();
						if (!handleWSException(e)) {
							return;
						}
						firstAttempt = false;
					}
				}
			}
		} finally {
			if (useProgressWindow) {
				ProgressWindow.hideProgressWindow();
			}
		}

		return;
	}
}