package org.openflexo.rest.client;

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
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.openflexo.toolbox.IProgress;

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

	private IProgress progress;

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

	public ServerRestClient.ProjectsProjectIDSessions projectsProjectIDSessions(com.sun.jersey.api.client.Client client, URI baseURI,
			Integer projectid) {
		return new ServerRestClient.ProjectsProjectIDSessions(client, baseURI, projectid);
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
				if (cr.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					if (userName != null && password != null) {
						try {
							cr.getHeaders().add(HttpHeaders.AUTHORIZATION,
									Base64.encodeBase64String((userName + ":" + password).getBytes("ISO-8859-1")));
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

	public ServerRestClient.ProjectsProjectIDSessions projectsProjectIDSessions(Integer projectid) {
		return projectsProjectIDSessions(createClient(), getBASE_URI(), projectid);
	}

	public ServerRestClient.ProjectsProjectIDSessions projectsProjectIDSessions(com.sun.jersey.api.client.Client client, Integer projectid) {
		return projectsProjectIDSessions(client, getBASE_URI(), projectid);
	}

	public ServerRestClient.ProjectsProjectIDVersions projectsProjectIDVersions(com.sun.jersey.api.client.Client client, URI baseURI,
			Integer projectid) {
		return new ServerRestClient.ProjectsProjectIDVersions(client, baseURI, projectid);
	}

	public ServerRestClient.ProjectsProjectIDVersions projectsProjectIDVersions(Integer projectid) {
		return projectsProjectIDVersions(createClient(), getBASE_URI(), projectid);
	}

	public ServerRestClient.ProjectsProjectIDVersions projectsProjectIDVersions(com.sun.jersey.api.client.Client client, Integer projectid) {
		return projectsProjectIDVersions(client, getBASE_URI(), projectid);
	}

	public ServerRestClient.Projects projects(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.Projects(client, baseURI);
	}

	public ServerRestClient.Projects projects() {
		return projects(createClient(), getBASE_URI());
	}

	public ServerRestClient.Projects projects(com.sun.jersey.api.client.Client client) {
		return projects(client, getBASE_URI());
	}

	public ServerRestClient.Accounts accounts(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.Accounts(client, baseURI);
	}

	public ServerRestClient.Accounts accounts() {
		return accounts(createClient(), getBASE_URI());
	}

	public ServerRestClient.Accounts accounts(com.sun.jersey.api.client.Client client) {
		return accounts(client, getBASE_URI());
	}

	public ServerRestClient.Jobs jobs(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.Jobs(client, baseURI);
	}

	public ServerRestClient.Jobs jobs() {
		return jobs(createClient(), getBASE_URI());
	}

	public ServerRestClient.Jobs jobs(com.sun.jersey.api.client.Client client) {
		return jobs(client, getBASE_URI());
	}

	public ServerRestClient.Users users(com.sun.jersey.api.client.Client client, URI baseURI) {
		return new ServerRestClient.Users(client, baseURI);
	}

	public ServerRestClient.Users users() {
		return users(createClient(), getBASE_URI());
	}

	public ServerRestClient.Users users(com.sun.jersey.api.client.Client client) {
		return users(client, getBASE_URI());
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

	public IProgress getProgress() {
		return progress;
	}

	public void setProgress(IProgress progress) {
		this.progress = progress;
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Account postAsAccountXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(Account.class);
		}

		public <T> T postAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Account postAsAccountJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(Account.class);
		}

		public <T> T postAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				StringBuilder template = new StringBuilder(getBASE_URI().toString());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "Account"), Account.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "Account"), Account.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Job postAsJobXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(Job.class);
		}

		public <T> T postAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Job postAsJobJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(Job.class);
		}

		public <T> T postAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				StringBuilder template = new StringBuilder(getBASE_URI().toString());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "Job"), Job.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "Job"), Job.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Project postAsProjectXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(Project.class);
		}

		public <T> T postAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Project postAsProjectJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(Project.class);
		}

		public <T> T postAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				StringBuilder template = new StringBuilder(getBASE_URI().toString());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "Project"), Project.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "Project"), Project.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
			StringBuilder template = new StringBuilder(getBASE_URI().toString());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Session postAsSessionXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(Session.class);
		}

		public <T> T postAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public Session postAsSessionJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(Session.class);
		}

		public <T> T postAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				StringBuilder template = new StringBuilder(getBASE_URI().toString());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "Session"), Session.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "Session"), Session.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
			StringBuilder template = new StringBuilder(getBASE_URI().toString());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ProjectVersion postAsProjectVersionXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(ProjectVersion.class);
		}

		public <T> T postAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ProjectVersion postAsProjectVersionJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(ProjectVersion.class);
		}

		public <T> T postAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public ServerRestClient.ProjectsProjectIDVersions.Id id(Integer id) {
			return new ServerRestClient.ProjectsProjectIDVersions.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues),
					id);
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
				StringBuilder template = new StringBuilder(getBASE_URI().toString());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "ProjectVersion"), ProjectVersion.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "ProjectVersion"), ProjectVersion.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public User postAsUserXml() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(User.class);
		}

		public <T> T postAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsXml(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/xml");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
				}
			}
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				return response.getEntity(returnType);
			} else {
				return returnType.cast(response);
			}
		}

		public User postAsUserJson() {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(User.class);
		}

		public <T> T postAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (response.getStatus() >= 400) {
				throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
			}
			return response.getEntity(returnType);
		}

		public <T> T postAsJson(Class<T> returnType) {
			UriBuilder localUriBuilder = _uriBuilder.clone();
			com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder
					.buildFromMap(_templateAndMatrixParameterValues));
			com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
			resourceBuilder = resourceBuilder.accept("application/json");
			com.sun.jersey.api.client.ClientResponse response;
			response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class);
			if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
				StringBuilder template = new StringBuilder(getBASE_URI().toString());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "User"), User.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
				response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, new JAXBElement(new QName(
						"http://www.agilebirds.com/openflexo", "User"), User.class, input));
				if (response.getStatus() >= 400) {
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
					throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
						throw new ServerRestClient.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus())
								.build());
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
