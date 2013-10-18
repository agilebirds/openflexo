package org.openflexo.rest.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.openflexo.rest.client.model.Account;
import org.openflexo.rest.client.model.Document;
import org.openflexo.rest.client.model.Job;
import org.openflexo.rest.client.model.JobHistory;
import org.openflexo.rest.client.model.Project;
import org.openflexo.rest.client.model.ProjectVersion;
import org.openflexo.rest.client.model.Session;
import org.openflexo.rest.client.model.User;
import org.openflexo.rest.client.model.UserProject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.uri.UriTemplate;

@Generated(
		value = { "wadl|https://server.openflexo.com/Flexo/rest/application.wadl" },
		comments = "wadl2java, http://wadl.java.net",
		date = "2013-06-27T12:41:47.933+02:00")
public class ServerRestClient {

	/**
	 * The base URI for the resource represented by this proxy
	 * 
	 */
	private URI BASE_URI;

	private String userName;

	private String password;

	public ServerRestClient() {
		try {
			BASE_URI = new URI("https://server.openflexo.com/Flexo/rest");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public ServerRestClient(URI bASE_URI) {
		super();
		BASE_URI = bASE_URI;
	}

	public URI getBASE_URI() {
		return BASE_URI;
	}

	public void setBASE_URI(URI bASE_URI) {
		BASE_URI = bASE_URI;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void throwWebApplicationException(com.sun.jersey.api.client.ClientResponse response) throws WebApplicationExceptionMessage {
		String message = "";
		try {
			message = IOUtils.toString(response.getEntityInputStream(), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new ServerRestClient.WebApplicationExceptionMessage(Response
				.fromResponse(Response.status(response.getClientResponseStatus()).build()).entity(message).build());
	}

	/**
	 * Template method to allow tooling to customize the new Client
	 * 
	 */
	protected void customizeClientConfiguration(ClientConfig cc) {
		cc.getFeatures().put(ClientConfig.FEATURE_XMLROOTELEMENT_PROCESSING, Boolean.TRUE);
	}

	protected void customizeClient(Client client) {
		client.addFilter(new ClientFilter() {

			@Override
			public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
				if (!cr.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					if (userName != null && password != null) {
						try {
							cr.getHeaders().add(HttpHeaders.AUTHORIZATION,
									"Basic " + Base64.encodeBase64String((userName + ":" + password).getBytes("ISO-8859-1")));
						} catch (UnsupportedEncodingException e) {
							// Should never happen
							e.printStackTrace();
						}
					}
				}
				return getNext().handle(cr);
			}
		});
		/*
		client.addFilter(new ClientFilter() {
			
			@Override
			public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
				cr.setAdapter(new AbstractClientRequestAdapter(cr.getAdapter()) {
					
					@Override
					public OutputStream adapt(ClientRequest request, final OutputStream out) throws IOException {
						return new OutputStream() {
							long progress = 0;
							long lastUpdate = 0;
							int stepSize = (int) (length / NUMBER_OF_STATES);
							
							@Override
							public void write(int b) throws IOException {
								out.write(b);
								progress.setProgress(String.format("%1$.2f%1$%", percent, progress, length));
							}
						};
					}
				});
				return getNext().handle(cr);
			}
		})*/
	}

	/**
	 * Template method to allow tooling to override Client factory
	 * 
	 */
	private com.sun.jersey.api.client.Client createClientInstance(ClientConfig cc) {
		Client client = com.sun.jersey.api.client.Client.create(cc);
		customizeClient(client);
		return client;
	}

	/**
	 * Create a new Client instance
	 * 
	 */
	public com.sun.jersey.api.client.Client createClient() {
		ClientConfig cc = new DefaultClientConfig();
		customizeClientConfiguration(cc);
		return createClientInstance(cc);
	}

	public Files files(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new Files(client, baseURI);
	}

	public Files files() {
		return files(createClient(), BASE_URI);
	}

	public Files files(com.sun.jersey.api.client.Client client) {
		return files(client, BASE_URI);
	}

	public ServerRestClient.Projects projects(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.Projects(client, baseURI);
	}

	public ServerRestClient.Projects projects() {
		return projects(createClient(), BASE_URI);
	}

	public ServerRestClient.Projects projects(com.sun.jersey.api.client.Client client) {
		return projects(client, BASE_URI);
	}

	public ServerRestClient.JobsHistory jobsHistory(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.JobsHistory(client, baseURI);
	}

	public ServerRestClient.JobsHistory jobsHistory() {
		return jobsHistory(createClient(), BASE_URI);
	}

	public ServerRestClient.JobsHistory jobsHistory(com.sun.jersey.api.client.Client client) {
		return jobsHistory(client, BASE_URI);
	}

	public ServerRestClient.ProjectsProjectIDVersions projectsProjectIDVersions(com.sun.jersey.api.client.Client client, URI baseURI,
			Integer projectid) {
		return new ServerRestClient.ProjectsProjectIDVersions(client, baseURI, projectid);
	}

	public ServerRestClient.ProjectsProjectIDVersions projectsProjectIDVersions(Integer projectid) {
		return projectsProjectIDVersions(createClient(), BASE_URI, projectid);
	}

	public ServerRestClient.ProjectsProjectIDVersions projectsProjectIDVersions(com.sun.jersey.api.client.Client client, Integer projectid) {
		return projectsProjectIDVersions(client, BASE_URI, projectid);
	}

	public ServerRestClient.Users users(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.Users(client, baseURI);
	}

	public ServerRestClient.Users users() {
		return users(createClient(), BASE_URI);
	}

	public ServerRestClient.Users users(com.sun.jersey.api.client.Client client) {
		return users(client, BASE_URI);
	}

	public ServerRestClient.UserProjects userProjects(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.UserProjects(client, baseURI);
	}

	public ServerRestClient.UserProjects userProjects() {
		return userProjects(createClient(), BASE_URI);
	}

	public ServerRestClient.UserProjects userProjects(com.sun.jersey.api.client.Client client) {
		return userProjects(client, BASE_URI);
	}

	public ServerRestClient.ProjectsProjectIDSessions projectsProjectIDSessions(com.sun.jersey.api.client.Client client, URI baseURI,
			Integer projectid) {
		return new ServerRestClient.ProjectsProjectIDSessions(client, baseURI, projectid);
	}

	public ServerRestClient.ProjectsProjectIDSessions projectsProjectIDSessions(Integer projectid) {
		return projectsProjectIDSessions(createClient(), BASE_URI, projectid);
	}

	public ServerRestClient.ProjectsProjectIDSessions projectsProjectIDSessions(com.sun.jersey.api.client.Client client, Integer projectid) {
		return projectsProjectIDSessions(client, BASE_URI, projectid);
	}

	public ServerRestClient.Jobs jobs(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.Jobs(client, baseURI);
	}

	public ServerRestClient.Jobs jobs() {
		return jobs(createClient(), BASE_URI);
	}

	public ServerRestClient.Jobs jobs(com.sun.jersey.api.client.Client client) {
		return jobs(client, BASE_URI);
	}

	public ServerRestClient.Accounts accounts(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.Accounts(client, baseURI);
	}

	public ServerRestClient.Accounts accounts() {
		return accounts(createClient(), BASE_URI);
	}

	public ServerRestClient.Accounts accounts(com.sun.jersey.api.client.Client client) {
		return accounts(client, BASE_URI);
	}

	public class Accounts {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private Accounts(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public Accounts(com.sun.jersey.api.client.Client client, URI baseUri) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/accounts");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
		}

		public Account getAsAccountXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Account.class);
		}

		public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Account getAsAccountXml(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Account.class);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Account getAsAccountJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Account.class);
		}

		public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Account getAsAccountJson(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Account.class);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Account postXmlAsAccount(Account input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Account.class);
		}

		public <T> T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Account postJsonAsAccount(Account input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Account.class);
		}

		public <T> T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.Accounts.Id id(Integer id) {
			return new ServerRestClient.Accounts.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public ServerRestClient.Accounts.IdSummary idSummary(Integer id) {
			return new ServerRestClient.Accounts.IdSummary(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public class Id {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Id(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/accounts/{id}");
				} else {
					template.append("accounts/{id}");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Accounts.Id setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Accounts.Id(_client, copyUriBuilder, copyMap);
			}

			public Account getAsAccountXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Account.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Account getAsAccountJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Account.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
			}

			public <T> T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T delete(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Account putXmlAsAccount(Account input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Account.class);
			}

			public <T> T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putXml(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Account putJsonAsAccount(Account input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Account.class);
			}

			public <T> T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putJson(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdSummary {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdSummary(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/summary");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/accounts/{id}/summary");
				} else {
					template.append("accounts/{id}/summary");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Accounts.IdSummary setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Accounts.IdSummary(_client, copyUriBuilder, copyMap);
			}

			public Account getAsAccountXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Account.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Account getAsAccountJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Account.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}
		}

	}

	public ServerRestClient.Documents documents(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.Documents(client, baseURI);
	}

	public ServerRestClient.Documents documents() {
		return documents(createClient(), BASE_URI);
	}

	public ServerRestClient.Documents documents(com.sun.jersey.api.client.Client client) {
		return documents(client, BASE_URI);
	}

	public class Documents {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private Documents(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public Documents(com.sun.jersey.api.client.Client client, URI baseUri) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/documents");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
		}

		public Document getAsDocumentXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Document.class);
		}

		public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Document getAsDocumentXml(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Document.class);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Document getAsDocumentJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Document.class);
		}

		public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Document getAsDocumentJson(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Document.class);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Document postXmlAsDocument(Document input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Document.class);
		}

		public <T> T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Document postJsonAsDocument(Document input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Document.class);
		}

		public <T> T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.Documents.Id id(Integer id) {
			return new ServerRestClient.Documents.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public ServerRestClient.Documents.IdSummary idSummary(Integer id) {
			return new ServerRestClient.Documents.IdSummary(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public class Id {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Id(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/documents/{id}");
				} else {
					template.append("documents/{id}");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Documents.Id setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Documents.Id(_client, copyUriBuilder, copyMap);
			}

			public Document getAsDocumentXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Document.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Document getAsDocumentJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Document.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
			}

			public <T> T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T delete(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Document putXmlAsDocument(Document input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Document.class);
			}

			public <T> T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putXml(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Document putJsonAsDocument(Document input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Document.class);
			}

			public <T> T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putJson(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdSummary {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdSummary(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/summary");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/documents/{id}/summary");
				} else {
					template.append("documents/{id}/summary");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Documents.IdSummary setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Documents.IdSummary(_client, copyUriBuilder, copyMap);
			}

			public Document getAsDocumentXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Document.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Document getAsDocumentJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Document.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

	}

	public class Files {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private Files(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public Files(com.sun.jersey.api.client.Client client, URI baseUri) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/files");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
		}

		public <T> T getAsOctetStream(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/octet-stream");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {

			}
			return response.getEntity(returnType);
		}

		public <T> T getAsOctetStream(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/octet-stream");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {

				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public <T> T getAsOctetStream(String uuid, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (uuid == null) {
			}
			if (uuid != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("uuid", uuid);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("uuid", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/octet-stream");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsOctetStream(String uuid, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (uuid == null) {
			}
			if (uuid != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("uuid", uuid);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("uuid", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/octet-stream");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

	}

	public class Jobs {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private Jobs(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public Jobs(com.sun.jersey.api.client.Client client, URI baseUri) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/jobs");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
		}

		public Job getAsJobXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Job.class);
		}

		public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Job getAsJobXml(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Job.class);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Job getAsJobJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Job.class);
		}

		public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Job getAsJobJson(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Job.class);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Job postXmlAsJob(Job input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Job.class);
		}

		public <T> T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Job postJsonAsJob(Job input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Job.class);
		}

		public <T> T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.Jobs.Id id(Integer id) {
			return new ServerRestClient.Jobs.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public ServerRestClient.Jobs.IdSummary idSummary(Integer id) {
			return new ServerRestClient.Jobs.IdSummary(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public class Id {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Id(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/jobs/{id}");
				} else {
					template.append("jobs/{id}");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Jobs.Id setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Jobs.Id(_client, copyUriBuilder, copyMap);
			}

			public Job getAsJobXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Job.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Job getAsJobJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Job.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
			}

			public <T> T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T delete(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Job putXmlAsJob(Job input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Job.class);
			}

			public <T> T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putXml(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Job putJsonAsJob(Job input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Job.class);
			}

			public <T> T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putJson(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdSummary {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdSummary(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/summary");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/jobs/{id}/summary");
				} else {
					template.append("jobs/{id}/summary");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Jobs.IdSummary setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Jobs.IdSummary(_client, copyUriBuilder, copyMap);
			}

			public Job getAsJobXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Job.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Job getAsJobJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Job.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

	}

	public class JobsHistory {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private JobsHistory(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public JobsHistory(com.sun.jersey.api.client.Client client, URI baseUri) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/jobs/history");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
		}

		public JobHistory postXmlAsJobHistory(JobHistory input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(JobHistory.class);
		}

		public <T> T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public JobHistory postJsonAsJobHistory(JobHistory input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(JobHistory.class);
		}

		public <T> T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public JobHistory getAsJobHistoryXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(JobHistory.class);
		}

		public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public JobHistory getAsJobHistoryXml(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(JobHistory.class);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public JobHistory getAsJobHistoryJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(JobHistory.class);
		}

		public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public JobHistory getAsJobHistoryJson(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(JobHistory.class);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.JobsHistory.JobsIdHistory jobsIdHistory(Integer id) {
			return new ServerRestClient.JobsHistory.JobsIdHistory(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public ServerRestClient.JobsHistory.Id id(Integer id) {
			return new ServerRestClient.JobsHistory.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public ServerRestClient.JobsHistory.IdSummary idSummary(Integer id) {
			return new ServerRestClient.JobsHistory.IdSummary(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public class Id {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Id(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/jobs/history/{id}");
				} else {
					template.append("jobs/history/{id}");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.JobsHistory.Id setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.JobsHistory.Id(_client, copyUriBuilder, copyMap);
			}

			public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
			}

			public <T> T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T delete(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public JobHistory putXmlAsJobHistory(JobHistory input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(JobHistory.class);
			}

			public <T> T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putXml(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public JobHistory putJsonAsJobHistory(JobHistory input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(JobHistory.class);
			}

			public <T> T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putJson(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdSummary {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdSummary(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/summary");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/jobs/history/{id}/summary");
				} else {
					template.append("jobs/history/{id}/summary");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.JobsHistory.IdSummary setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.JobsHistory.IdSummary(_client, copyUriBuilder, copyMap);
			}

			public JobHistory getAsJobHistoryXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(JobHistory.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public JobHistory getAsJobHistoryJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(JobHistory.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class JobsIdHistory {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private JobsIdHistory(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public JobsIdHistory(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("/jobs/{id}/history");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public JobsIdHistory(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/jobs/history/jobs/{id}/history");
				} else {
					template.append("jobs/history/jobs/{id}/history");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.JobsHistory.JobsIdHistory setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.JobsHistory.JobsIdHistory(_client, copyUriBuilder, copyMap);
			}

			public JobHistory getAsJobHistoryXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(JobHistory.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public JobHistory getAsJobHistoryJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(JobHistory.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}
		}
	}

	public class Projects {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private Projects(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public Projects(com.sun.jersey.api.client.Client client, URI baseUri) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/projects");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
		}

		public Project getAsProjectXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Project.class);
		}

		public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Project getAsProjectXml(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Project.class);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Project getAsProjectJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Project.class);
		}

		public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Project getAsProjectJson(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Project.class);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Project postXmlAsProject(Project input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Project.class);
		}

		public <T> T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Project postJsonAsProject(Project input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Project.class);
		}

		public <T> T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.Projects.Id id(Integer id) {
			return new ServerRestClient.Projects.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public ServerRestClient.Projects.IdSummary idSummary(Integer id) {
			return new ServerRestClient.Projects.IdSummary(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public class Id {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Id(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/projects/{id}");
				} else {
					template.append("projects/{id}");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Projects.Id setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Projects.Id(_client, copyUriBuilder, copyMap);
			}

			public Project getAsProjectXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Project.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Project getAsProjectJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Project.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
			}

			public <T> T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T delete(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Project putXmlAsProject(Project input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Project.class);
			}

			public <T> T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putXml(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Project putJsonAsProject(Project input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Project.class);
			}

			public <T> T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putJson(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdSummary {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdSummary(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/summary");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/projects/{id}/summary");
				} else {
					template.append("projects/{id}/summary");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Projects.IdSummary setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Projects.IdSummary(_client, copyUriBuilder, copyMap);
			}

			public Project getAsProjectXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Project.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Project getAsProjectJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Project.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

	}

	public class ProjectsProjectIDSessions {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private ProjectsProjectIDSessions(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public ProjectsProjectIDSessions(com.sun.jersey.api.client.Client client, URI baseUri, Integer projectid) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/projects/{projectID}/sessions");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
			_templateAndMatrixParameterValues.put("projectID", projectid);
		}

		/**
		 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
		 * 
		 */
		public ProjectsProjectIDSessions(com.sun.jersey.api.client.Client client, URI uri) {
			_client = client;
			StringBuilder template = new StringBuilder(BASE_URI.toString());
			if (template.charAt(template.length() - 1) != '/') {
				template.append("/projects/{projectID}/sessions");
			} else {
				template.append("projects/{projectID}/sessions");
			}
			_uriBuilder = UriBuilder.fromPath(template.toString());
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
			UriTemplate uriTemplate = new UriTemplate(template.toString());
			HashMap<String, String> parameters = new HashMap<String, String>();
			uriTemplate.match(uri.toString(), parameters);
			_templateAndMatrixParameterValues.putAll(parameters);
		}

		/**
		 * Get projectID
		 * 
		 */
		public Integer getProjectid() {
			return (Integer) _templateAndMatrixParameterValues.get("projectID");
		}

		/**
		 * Duplicate state and set projectID
		 * 
		 */
		public ServerRestClient.ProjectsProjectIDSessions setProjectid(Integer projectid) {
			Map<String, Object> copyMap;
			copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
			UriBuilder copyUriBuilder = _uriBuilder.clone();
			copyMap.put("projectID", projectid);
			return new ServerRestClient.ProjectsProjectIDSessions(_client, copyUriBuilder, copyMap);
		}

		public Session getAsSessionXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Session.class);
		}

		public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Session getAsSessionXml(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Session.class);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Session getAsSessionJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Session.class);
		}

		public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Session getAsSessionJson(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Session.class);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Session postXmlAsSession(Session input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Session.class);
		}

		public <T> T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Session postJsonAsSession(Session input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(Session.class);
		}

		public <T> T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.ProjectsProjectIDSessions.Id id(Integer id) {
			return new ServerRestClient.ProjectsProjectIDSessions.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues),
					id);
		}

		public ServerRestClient.ProjectsProjectIDSessions.IdSummary idSummary(Integer id) {
			return new ServerRestClient.ProjectsProjectIDSessions.IdSummary(_client,
					_uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public class Id {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Id(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/projects/{projectID}/sessions/{id}");
				} else {
					template.append("projects/{projectID}/sessions/{id}");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.ProjectsProjectIDSessions.Id setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.ProjectsProjectIDSessions.Id(_client, copyUriBuilder, copyMap);
			}

			public Session getAsSessionXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Session.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Session getAsSessionJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Session.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
			}

			public <T> T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T delete(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Session putXmlAsSession(Session input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Session.class);
			}

			public <T> T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putXml(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Session putJsonAsSession(Session input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Session.class);
			}

			public <T> T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putJson(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdSummary {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdSummary(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/summary");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/projects/{projectID}/sessions/{id}/summary");
				} else {
					template.append("projects/{projectID}/sessions/{id}/summary");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.ProjectsProjectIDSessions.IdSummary setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.ProjectsProjectIDSessions.IdSummary(_client, copyUriBuilder, copyMap);
			}

			public Session getAsSessionXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Session.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public Session getAsSessionJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(Session.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

	}

	public class ProjectsProjectIDVersions {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private ProjectsProjectIDVersions(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public ProjectsProjectIDVersions(com.sun.jersey.api.client.Client client, URI baseUri, Integer projectid) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/projects/{projectID}/versions");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
			_templateAndMatrixParameterValues.put("projectID", projectid);
		}

		/**
		 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
		 * 
		 */
		public ProjectsProjectIDVersions(com.sun.jersey.api.client.Client client, URI uri) {
			_client = client;
			StringBuilder template = new StringBuilder(BASE_URI.toString());
			if (template.charAt(template.length() - 1) != '/') {
				template.append("/projects/{projectID}/versions");
			} else {
				template.append("projects/{projectID}/versions");
			}
			_uriBuilder = UriBuilder.fromPath(template.toString());
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
			UriTemplate uriTemplate = new UriTemplate(template.toString());
			HashMap<String, String> parameters = new HashMap<String, String>();
			uriTemplate.match(uri.toString(), parameters);
			_templateAndMatrixParameterValues.putAll(parameters);
		}

		/**
		 * Get projectID
		 * 
		 */
		public Integer getProjectid() {
			return (Integer) _templateAndMatrixParameterValues.get("projectID");
		}

		/**
		 * Duplicate state and set projectID
		 * 
		 */
		public ServerRestClient.ProjectsProjectIDVersions setProjectid(Integer projectid) {
			Map<String, Object> copyMap;
			copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
			UriBuilder copyUriBuilder = _uriBuilder.clone();
			copyMap.put("projectID", projectid);
			return new ServerRestClient.ProjectsProjectIDVersions(_client, copyUriBuilder, copyMap);
		}

		public ProjectVersion getAsProjectVersionXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(ProjectVersion.class);
		}

		public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ProjectVersion getAsProjectVersionXml(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(ProjectVersion.class);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ProjectVersion getAsProjectVersionJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(ProjectVersion.class);
		}

		public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ProjectVersion getAsProjectVersionJson(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(ProjectVersion.class);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public <T> T postMultipartFormDataAsXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("multipart/form-data");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postMultipartFormDataAsXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("multipart/form-data");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public <T> T postMultipartFormDataAsJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("multipart/form-data");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postMultipartFormDataAsJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("multipart/form-data");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ProjectVersion postXmlAsProjectVersion(ProjectVersion input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(ProjectVersion.class);
		}

		public <T> T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ProjectVersion postJsonAsProjectVersion(ProjectVersion input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(ProjectVersion.class);
		}

		public <T> T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.ProjectsProjectIDVersions.IdFile idFile(Integer id) {
			return new ServerRestClient.ProjectsProjectIDVersions.IdFile(_client,
					_uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public ServerRestClient.ProjectsProjectIDVersions.IdFileLength idFileLength(Integer id) {
			return new ServerRestClient.ProjectsProjectIDVersions.IdFileLength(_client,
					_uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public ServerRestClient.ProjectsProjectIDVersions.Next next() {
			return new ServerRestClient.ProjectsProjectIDVersions.Next(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues));
		}

		public ServerRestClient.ProjectsProjectIDVersions.Id id(Integer id) {
			return new ServerRestClient.ProjectsProjectIDVersions.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues),
					id);
		}

		public ServerRestClient.ProjectsProjectIDVersions.IdSummary idSummary(Integer id) {
			return new ServerRestClient.ProjectsProjectIDVersions.IdSummary(_client,
					_uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public class Id {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Id(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/projects/{projectID}/versions/{id}");
				} else {
					template.append("projects/{projectID}/versions/{id}");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.ProjectsProjectIDVersions.Id setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.ProjectsProjectIDVersions.Id(_client, copyUriBuilder, copyMap);
			}

			public ProjectVersion getAsProjectVersionXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(ProjectVersion.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public ProjectVersion getAsProjectVersionJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(ProjectVersion.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
			}

			public <T> T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T delete(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public ProjectVersion putXmlAsProjectVersion(ProjectVersion input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(ProjectVersion.class);
			}

			public <T> T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putXml(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public ProjectVersion putJsonAsProjectVersion(ProjectVersion input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(ProjectVersion.class);
			}

			public <T> T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putJson(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdFile {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdFile(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdFile(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/file");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdFile(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/projects/{projectID}/versions/{id}/file");
				} else {
					template.append("projects/{projectID}/versions/{id}/file");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.ProjectsProjectIDVersions.IdFile setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.ProjectsProjectIDVersions.IdFile(_client, copyUriBuilder, copyMap);
			}

			public <T> T getAsOctetStream(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/octet-stream");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsOctetStream(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/octet-stream");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdFileLength {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdFileLength(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdFileLength(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/file/length");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdFileLength(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/projects/{projectID}/versions/{id}/file/length");
				} else {
					template.append("projects/{projectID}/versions/{id}/file/length");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.ProjectsProjectIDVersions.IdFileLength setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.ProjectsProjectIDVersions.IdFileLength(_client, copyUriBuilder, copyMap);
			}

			public <T> T getAsTextPlain(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("text/plain");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsTextPlain(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("text/plain");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdSummary {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdSummary(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI baseUri, Integer id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/summary");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/projects/{projectID}/versions/{id}/summary");
				} else {
					template.append("projects/{projectID}/versions/{id}/summary");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public Integer getId() {
				return (Integer) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.ProjectsProjectIDVersions.IdSummary setId(Integer id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.ProjectsProjectIDVersions.IdSummary(_client, copyUriBuilder, copyMap);
			}

			public ProjectVersion getAsProjectVersionXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(ProjectVersion.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public ProjectVersion getAsProjectVersionJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(ProjectVersion.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}
		}

		public class Next {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Next(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Next(com.sun.jersey.api.client.Client client, URI baseUri) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("next");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
			}

			public <T> T getAsTextPlain(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("text/plain");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsTextPlain(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("text/plain");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

	}

	public class UserProjects {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private UserProjects(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public UserProjects(com.sun.jersey.api.client.Client client, URI baseUri) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/userProjects");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
		}

		public UserProject getAsUserProjectXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(UserProject.class);
		}

		public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public UserProject getAsUserProjectXml(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(UserProject.class);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public UserProject getAsUserProjectJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(UserProject.class);
		}

		public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public UserProject getAsUserProjectJson(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(UserProject.class);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public UserProject postXmlAsUserProject(UserProject input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(UserProject.class);
		}

		public <T> T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public UserProject postJsonAsUserProject(UserProject input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(UserProject.class);
		}

		public <T> T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.UserProjects.Id id(String id) {
			return new ServerRestClient.UserProjects.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public class Id {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Id(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI baseUri, String id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/userProjects/{id}");
				} else {
					template.append("userProjects/{id}");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public String getId() {
				return (String) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.UserProjects.Id setId(String id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.UserProjects.Id(_client, copyUriBuilder, copyMap);
			}

			public UserProject getAsUserProjectXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(UserProject.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public UserProject getAsUserProjectJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(UserProject.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
			}

			public <T> T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T delete(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public UserProject putXmlAsUserProject(UserProject input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(UserProject.class);
			}

			public <T> T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putXml(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public UserProject putJsonAsUserProject(UserProject input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(UserProject.class);
			}

			public <T> T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putJson(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdSummary {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdSummary(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI baseUri, String id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/summary");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/userProjects/{id}/summary");
				} else {
					template.append("userProjects/{id}/summary");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public String getId() {
				return (String) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.UserProjects.IdSummary setId(String id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.UserProjects.IdSummary(_client, copyUriBuilder, copyMap);
			}

			public UserProject getAsUserProjectXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(UserProject.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public UserProject getAsUserProjectJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(UserProject.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

	}

	public class Users {

		private com.sun.jersey.api.client.Client _client;
		private UriBuilder _uriBuilder;
		private Map<String, Object> _templateAndMatrixParameterValues;

		private Users(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
			_client = client;
			_uriBuilder = uriBuilder.clone();
			_templateAndMatrixParameterValues = map;
		}

		/**
		 * Create new instance using existing Client instance, and a base URI and any parameters
		 * 
		 */
		public Users(com.sun.jersey.api.client.Client client, URI baseUri) {
			_client = client;
			_uriBuilder = UriBuilder.fromUri(baseUri);
			_uriBuilder = _uriBuilder.path("/users");
			_templateAndMatrixParameterValues = new HashMap<String, Object>();
		}

		public User getAsUserXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(User.class);
		}

		public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public User getAsUserXml(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(User.class);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public User getAsUserJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(User.class);
		}

		public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public User getAsUserJson(Integer start, Integer end, String sort) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(User.class);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			if (start == null) {
			}
			if (start != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("start", (Object[]) null);
			}
			if (end == null) {
			}
			if (end != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("end", (Object[]) null);
			}
			if (sort == null) {
			}
			if (sort != null) {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
			} else {
				localUriBuilder = localUriBuilder.replaceQueryParam("sort", (Object[]) null);
			}
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public User postXmlAsUser(User input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(User.class);
		}

		public <T> T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postXml(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			resourceBuilder = resourceBuilder.type("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public User postJsonAsUser(User input) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(User.class);
		}

		public <T> T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (response.getStatus() >= 400) {
				throwWebApplicationException(response);
			}
			return response.getEntity(returnType);
		}

		public <T> T postJson(Object input, Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			resourceBuilder = resourceBuilder.type("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.Users.Id id(String id) {
			return new ServerRestClient.Users.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
		}

		public class Id {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private Id(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI baseUri, String id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public Id(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/users/{id}");
				} else {
					template.append("users/{id}");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public String getId() {
				return (String) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Users.Id setId(String id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Users.Id(_client, copyUriBuilder, copyMap);
			}

			public User getAsUserXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(User.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public User getAsUserJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(User.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
			}

			public <T> T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T delete(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);

					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public User putXmlAsUser(User input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(User.class);
			}

			public <T> T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putXml(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				resourceBuilder = resourceBuilder.type("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public User putJsonAsUser(User input) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(User.class);
			}

			public <T> T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T putJson(Object input, Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				resourceBuilder = resourceBuilder.type("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

		public class IdSummary {

			private com.sun.jersey.api.client.Client _client;
			private UriBuilder _uriBuilder;
			private Map<String, Object> _templateAndMatrixParameterValues;

			private IdSummary(com.sun.jersey.api.client.Client client, UriBuilder uriBuilder, Map<String, Object> map) {
				_client = client;
				_uriBuilder = uriBuilder.clone();
				_templateAndMatrixParameterValues = map;
			}

			/**
			 * Create new instance using existing Client instance, and a base URI and any parameters
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI baseUri, String id) {
				_client = client;
				_uriBuilder = UriBuilder.fromUri(baseUri);
				_uriBuilder = _uriBuilder.path("{id}/summary");
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				_templateAndMatrixParameterValues.put("id", id);
			}

			/**
			 * Create new instance using existing Client instance, and the URI from which the parameters will be extracted
			 * 
			 */
			public IdSummary(com.sun.jersey.api.client.Client client, URI uri) {
				_client = client;
				StringBuilder template = new StringBuilder(BASE_URI.toString());
				if (template.charAt(template.length() - 1) != '/') {
					template.append("/users/{id}/summary");
				} else {
					template.append("users/{id}/summary");
				}
				_uriBuilder = UriBuilder.fromPath(template.toString());
				_templateAndMatrixParameterValues = new HashMap<String, Object>();
				UriTemplate uriTemplate = new UriTemplate(template.toString());
				HashMap<String, String> parameters = new HashMap<String, String>();
				uriTemplate.match(uri.toString(), parameters);
				_templateAndMatrixParameterValues.putAll(parameters);
			}

			/**
			 * Get id
			 * 
			 */
			public String getId() {
				return (String) _templateAndMatrixParameterValues.get("id");
			}

			/**
			 * Duplicate state and set id
			 * 
			 */
			public ServerRestClient.Users.IdSummary setId(String id) {
				Map<String, Object> copyMap;
				copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
				UriBuilder copyUriBuilder = _uriBuilder.clone();
				copyMap.put("id", id);
				return new ServerRestClient.Users.IdSummary(_client, copyUriBuilder, copyMap);
			}

			public User getAsUserXml() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(User.class);
			}

			public <T> T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsXml(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/xml");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

			public User getAsUserJson() {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(User.class);
			}

			public <T> T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (response.getStatus() >= 400) {
					throwWebApplicationException(response);
				}
				return response.getEntity(returnType);
			}

			public <T> T getAsJson(Class<T> returnType) {
				UriBuilder localUriBuilder = _uriBuilder.clone();
				com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
						.buildFromMap(_templateAndMatrixParameterValues));
				com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
				resourceBuilder = resourceBuilder.accept("application/json");
				com.sun.jersey.api.client.ClientResponse response;
				response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					if (response.getStatus() >= 400) {
						throwWebApplicationException(response);
					}
				}
				if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
					return response.getEntity(returnType);
				} else {
					return returnType.cast(response);
				}
			}

		}

	}

	/**
	 * Workaround for JAX_RS_SPEC-312
	 * 
	 */
	private class WebApplicationExceptionMessage extends WebApplicationException {

		private WebApplicationExceptionMessage(Response response) {
			super(response);
		}

		/**
		 * Workaround for JAX_RS_SPEC-312
		 * 
		 */
		@Override
		public String getMessage() {
			Response response = getResponse();
			Response.Status status = Response.Status.fromStatusCode(response.getStatus());
			if (status != null) {
				return response.getStatus() + " " + status.getReasonPhrase();
			} else {
				return Integer.toString(response.getStatus());
			}
		}

		@Override
		public String toString() {
			String s = "javax.ws.rs.WebApplicationException";
			String message = getLocalizedMessage();
			return s + ": " + message;
		}

	}

}
