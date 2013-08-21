
package org.openflexo.rest.client.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.uri.UriTemplate;

@Generated(value = {
    "wadl|http://localhost:9090/Flexo/rest/application.wadl",
    "customization|file:/D:/Work/Git/openflexo/flexodesktop/GUI/flexo/rest-client-bindings.xml"
}, comments = "wadl2java, http://wadl.java.net", date = "2013-08-21T17:41:54.354+02:00")
public class Localhost_FlexoRest {

    /**
     * The base URI for the resource represented by this proxy
     * 
     */
    public final static URI BASE_URI;

    static {
        URI originalURI = URI.create("http://localhost:9090/Flexo/rest/");
        // Look up to see if we have any indirection in the local copy
        // of META-INF/java-rs-catalog.xml file, assuming it will be in the
        // oasis:name:tc:entity:xmlns:xml:catalog namespace or similar duck type
        java.io.InputStream is = Localhost_FlexoRest.class.getResourceAsStream("/META-INF/jax-rs-catalog.xml");
        if (is!=null) {
            try {
                // Ignore the namespace in the catalog, can't use wildcard until
                // we are sure we have XPath 2.0
                String found = javax.xml.xpath.XPathFactory.newInstance().newXPath().evaluate(
                    "/*[name(.) = 'catalog']/*[name(.) = 'uri' and @name ='" + originalURI +"']/@uri", 
                    new org.xml.sax.InputSource(is)); 
                if (found!=null && found.length()>0) {
                    originalURI = java.net.URI.create(found);
                }
                
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                try {
                    is.close();
                } catch (java.io.IOException e) {
                }
            }
        }
        BASE_URI = originalURI;
    }

    public static Localhost_FlexoRest.Users users(com.sun.jersey.api.client.Client client, URI baseURI) {
        return new Localhost_FlexoRest.Users(client, baseURI);
    }

    /**
     * Template method to allow tooling to customize the new Client
     * 
     */
    private static void customizeClientConfiguration(ClientConfig cc) {
    }

    /**
     * Template method to allow tooling to override Client factory
     * 
     */
    private static com.sun.jersey.api.client.Client createClientInstance(ClientConfig cc) {
        return com.sun.jersey.api.client.Client.create(cc);
    }

    /**
     * Create a new Client instance
     * 
     */
    public static com.sun.jersey.api.client.Client createClient() {
        ClientConfig cc = new DefaultClientConfig();
        customizeClientConfiguration(cc);
        return createClientInstance(cc);
    }

    public static Localhost_FlexoRest.Users users() {
        return users(createClient(), BASE_URI);
    }

    public static Localhost_FlexoRest.Users users(com.sun.jersey.api.client.Client client) {
        return users(client, BASE_URI);
    }

    public static Localhost_FlexoRest.Jobs jobs(com.sun.jersey.api.client.Client client, URI baseURI) {
        return new Localhost_FlexoRest.Jobs(client, baseURI);
    }

    public static Localhost_FlexoRest.Jobs jobs() {
        return jobs(createClient(), BASE_URI);
    }

    public static Localhost_FlexoRest.Jobs jobs(com.sun.jersey.api.client.Client client) {
        return jobs(client, BASE_URI);
    }

    public static Localhost_FlexoRest.ProjectsProjectIDVersions projectsProjectIDVersions(com.sun.jersey.api.client.Client client, URI baseURI, Integer projectid) {
        return new Localhost_FlexoRest.ProjectsProjectIDVersions(client, baseURI, projectid);
    }

    public static Localhost_FlexoRest.ProjectsProjectIDVersions projectsProjectIDVersions(Integer projectid) {
        return projectsProjectIDVersions(createClient(), BASE_URI, projectid);
    }

    public static Localhost_FlexoRest.ProjectsProjectIDVersions projectsProjectIDVersions(com.sun.jersey.api.client.Client client, Integer projectid) {
        return projectsProjectIDVersions(client, BASE_URI, projectid);
    }

    public static Localhost_FlexoRest.ProjectsProjectIDSessions projectsProjectIDSessions(com.sun.jersey.api.client.Client client, URI baseURI, Integer projectid) {
        return new Localhost_FlexoRest.ProjectsProjectIDSessions(client, baseURI, projectid);
    }

    public static Localhost_FlexoRest.ProjectsProjectIDSessions projectsProjectIDSessions(Integer projectid) {
        return projectsProjectIDSessions(createClient(), BASE_URI, projectid);
    }

    public static Localhost_FlexoRest.ProjectsProjectIDSessions projectsProjectIDSessions(com.sun.jersey.api.client.Client client, Integer projectid) {
        return projectsProjectIDSessions(client, BASE_URI, projectid);
    }

    public static Localhost_FlexoRest.JobsHistory jobsHistory(com.sun.jersey.api.client.Client client, URI baseURI) {
        return new Localhost_FlexoRest.JobsHistory(client, baseURI);
    }

    public static Localhost_FlexoRest.JobsHistory jobsHistory() {
        return jobsHistory(createClient(), BASE_URI);
    }

    public static Localhost_FlexoRest.JobsHistory jobsHistory(com.sun.jersey.api.client.Client client) {
        return jobsHistory(client, BASE_URI);
    }

    public static Localhost_FlexoRest.Accounts accounts(com.sun.jersey.api.client.Client client, URI baseURI) {
        return new Localhost_FlexoRest.Accounts(client, baseURI);
    }

    public static Localhost_FlexoRest.Accounts accounts() {
        return accounts(createClient(), BASE_URI);
    }

    public static Localhost_FlexoRest.Accounts accounts(com.sun.jersey.api.client.Client client) {
        return accounts(client, BASE_URI);
    }

    public static Localhost_FlexoRest.Files files(com.sun.jersey.api.client.Client client, URI baseURI) {
        return new Localhost_FlexoRest.Files(client, baseURI);
    }

    public static Localhost_FlexoRest.Files files() {
        return files(createClient(), BASE_URI);
    }

    public static Localhost_FlexoRest.Files files(com.sun.jersey.api.client.Client client) {
        return files(client, BASE_URI);
    }

    public static Localhost_FlexoRest.Projects projects(com.sun.jersey.api.client.Client client, URI baseURI) {
        return new Localhost_FlexoRest.Projects(client, baseURI);
    }

    public static Localhost_FlexoRest.Projects projects() {
        return projects(createClient(), BASE_URI);
    }

    public static Localhost_FlexoRest.Projects projects(com.sun.jersey.api.client.Client client) {
        return projects(client, BASE_URI);
    }

    public static class Accounts {

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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Account.class);
        }

        public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Account.class);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Account.class);
        }

        public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Account.class);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Account.class);
        }

        public<T >T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postXml(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Account.class);
        }

        public<T >T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postJson(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public Localhost_FlexoRest.Accounts.Id id(Integer id) {
            return new Localhost_FlexoRest.Accounts.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
        }

        public static class Id {

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
                if (template.charAt((template.length()- 1))!= '/') {
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
                return ((Integer) _templateAndMatrixParameterValues.get("id"));
            }

            /**
             * Duplicate state and set id
             * 
             */
            public Localhost_FlexoRest.Accounts.Id setId(Integer id) {
                Map<String, Object> copyMap;
                copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
                UriBuilder copyUriBuilder = _uriBuilder.clone();
                copyMap.put("id", id);
                return new Localhost_FlexoRest.Accounts.Id(_client, copyUriBuilder, copyMap);
            }

            public Account getAsAccountXml() {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Account.class);
            }

            public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsXml(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Account.class);
            }

            public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsJson(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
            }

            public<T >T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T delete(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Account.class);
            }

            public<T >T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putXml(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Account.class);
            }

            public<T >T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putJson(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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

    public static class Files {

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

        public<T >T getAsOctetStream(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/octet-stream");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsOctetStream(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/octet-stream");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public<T >T getAsOctetStream(String uuid, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (uuid == null) {
            }
            if (uuid!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("uuid", uuid);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("uuid", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/octet-stream");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsOctetStream(String uuid, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (uuid == null) {
            }
            if (uuid!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("uuid", uuid);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("uuid", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/octet-stream");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

    }

    public static class Jobs {

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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Job.class);
        }

        public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Job.class);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Job.class);
        }

        public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Job.class);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Job.class);
        }

        public<T >T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postXml(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Job.class);
        }

        public<T >T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postJson(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public Localhost_FlexoRest.Jobs.Id id(Integer id) {
            return new Localhost_FlexoRest.Jobs.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
        }

        public static class Id {

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
                if (template.charAt((template.length()- 1))!= '/') {
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
                return ((Integer) _templateAndMatrixParameterValues.get("id"));
            }

            /**
             * Duplicate state and set id
             * 
             */
            public Localhost_FlexoRest.Jobs.Id setId(Integer id) {
                Map<String, Object> copyMap;
                copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
                UriBuilder copyUriBuilder = _uriBuilder.clone();
                copyMap.put("id", id);
                return new Localhost_FlexoRest.Jobs.Id(_client, copyUriBuilder, copyMap);
            }

            public Job getAsJobXml() {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Job.class);
            }

            public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsXml(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Job.class);
            }

            public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsJson(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
            }

            public<T >T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T delete(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Job.class);
            }

            public<T >T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putXml(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Job.class);
            }

            public<T >T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putJson(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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

    public static class JobsHistory {

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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(JobHistory.class);
        }

        public<T >T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postXml(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(JobHistory.class);
        }

        public<T >T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postJson(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(JobHistory.class);
        }

        public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(JobHistory.class);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(JobHistory.class);
        }

        public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(JobHistory.class);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public Localhost_FlexoRest.JobsHistory.JobsIdHistory jobsIdHistory(String id) {
            return new Localhost_FlexoRest.JobsHistory.JobsIdHistory(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
        }

        public Localhost_FlexoRest.JobsHistory.Id id(Integer id) {
            return new Localhost_FlexoRest.JobsHistory.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
        }

        public static class Id {

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
                if (template.charAt((template.length()- 1))!= '/') {
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
                return ((Integer) _templateAndMatrixParameterValues.get("id"));
            }

            /**
             * Duplicate state and set id
             * 
             */
            public Localhost_FlexoRest.JobsHistory.Id setId(Integer id) {
                Map<String, Object> copyMap;
                copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
                UriBuilder copyUriBuilder = _uriBuilder.clone();
                copyMap.put("id", id);
                return new Localhost_FlexoRest.JobsHistory.Id(_client, copyUriBuilder, copyMap);
            }

            public com.sun.jersey.api.client.ClientResponse deleteAsClientResponse() {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
            }

            public<T >T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T delete(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(JobHistory.class);
            }

            public<T >T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putXml(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(JobHistory.class);
            }

            public<T >T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putJson(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                    }
                }
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    return response.getEntity(returnType);
                } else {
                    return returnType.cast(response);
                }
            }

        }

        public static class JobsIdHistory {

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
            public JobsIdHistory(com.sun.jersey.api.client.Client client, URI baseUri, String id) {
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
                if (template.charAt((template.length()- 1))!= '/') {
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
            public String getId() {
                return ((String) _templateAndMatrixParameterValues.get("id"));
            }

            /**
             * Duplicate state and set id
             * 
             */
            public Localhost_FlexoRest.JobsHistory.JobsIdHistory setId(String id) {
                Map<String, Object> copyMap;
                copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
                UriBuilder copyUriBuilder = _uriBuilder.clone();
                copyMap.put("id", id);
                return new Localhost_FlexoRest.JobsHistory.JobsIdHistory(_client, copyUriBuilder, copyMap);
            }

            public<T >T getAsXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("*/*");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsXml(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("*/*");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                    }
                }
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    return response.getEntity(returnType);
                } else {
                    return returnType.cast(response);
                }
            }

            public<T >T getAsJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("*/*");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsJson(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("*/*");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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

    public static class Projects {

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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Project.class);
        }

        public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Project.class);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Project.class);
        }

        public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Project.class);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Project.class);
        }

        public<T >T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postXml(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Project.class);
        }

        public<T >T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postJson(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public Localhost_FlexoRest.Projects.Id id(Integer id) {
            return new Localhost_FlexoRest.Projects.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
        }

        public static class Id {

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
                if (template.charAt((template.length()- 1))!= '/') {
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
                return ((Integer) _templateAndMatrixParameterValues.get("id"));
            }

            /**
             * Duplicate state and set id
             * 
             */
            public Localhost_FlexoRest.Projects.Id setId(Integer id) {
                Map<String, Object> copyMap;
                copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
                UriBuilder copyUriBuilder = _uriBuilder.clone();
                copyMap.put("id", id);
                return new Localhost_FlexoRest.Projects.Id(_client, copyUriBuilder, copyMap);
            }

            public Project getAsProjectXml() {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Project.class);
            }

            public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsXml(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Project.class);
            }

            public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsJson(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
            }

            public<T >T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T delete(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Project.class);
            }

            public<T >T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putXml(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Project.class);
            }

            public<T >T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putJson(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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

    public static class ProjectsProjectIDSessions {

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
            if (template.charAt((template.length()- 1))!= '/') {
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
            return ((Integer) _templateAndMatrixParameterValues.get("projectID"));
        }

        /**
         * Duplicate state and set projectID
         * 
         */
        public Localhost_FlexoRest.ProjectsProjectIDSessions setProjectid(Integer projectid) {
            Map<String, Object> copyMap;
            copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
            UriBuilder copyUriBuilder = _uriBuilder.clone();
            copyMap.put("projectID", projectid);
            return new Localhost_FlexoRest.ProjectsProjectIDSessions(_client, copyUriBuilder, copyMap);
        }

        public Session getAsSessionXml() {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Session.class);
        }

        public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Session.class);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Session.class);
        }

        public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Session.class);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Session.class);
        }

        public<T >T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postXml(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(Session.class);
        }

        public<T >T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postJson(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public Localhost_FlexoRest.ProjectsProjectIDSessions.Id id(Integer id) {
            return new Localhost_FlexoRest.ProjectsProjectIDSessions.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
        }

        public static class Id {

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
                if (template.charAt((template.length()- 1))!= '/') {
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
                return ((Integer) _templateAndMatrixParameterValues.get("id"));
            }

            /**
             * Duplicate state and set id
             * 
             */
            public Localhost_FlexoRest.ProjectsProjectIDSessions.Id setId(Integer id) {
                Map<String, Object> copyMap;
                copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
                UriBuilder copyUriBuilder = _uriBuilder.clone();
                copyMap.put("id", id);
                return new Localhost_FlexoRest.ProjectsProjectIDSessions.Id(_client, copyUriBuilder, copyMap);
            }

            public Session getAsSessionXml() {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Session.class);
            }

            public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsXml(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Session.class);
            }

            public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsJson(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
            }

            public<T >T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T delete(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Session.class);
            }

            public<T >T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putXml(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(Session.class);
            }

            public<T >T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putJson(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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

    public static class ProjectsProjectIDVersions {

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
            if (template.charAt((template.length()- 1))!= '/') {
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
            return ((Integer) _templateAndMatrixParameterValues.get("projectID"));
        }

        /**
         * Duplicate state and set projectID
         * 
         */
        public Localhost_FlexoRest.ProjectsProjectIDVersions setProjectid(Integer projectid) {
            Map<String, Object> copyMap;
            copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
            UriBuilder copyUriBuilder = _uriBuilder.clone();
            copyMap.put("projectID", projectid);
            return new Localhost_FlexoRest.ProjectsProjectIDVersions(_client, copyUriBuilder, copyMap);
        }

        public ProjectVersion getAsProjectVersionXml() {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(ProjectVersion.class);
        }

        public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(ProjectVersion.class);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(ProjectVersion.class);
        }

        public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(ProjectVersion.class);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public<T >T postMultipartFormDataAsXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("multipart/form-data");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postMultipartFormDataAsXml(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("multipart/form-data");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public<T >T postMultipartFormDataAsJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("multipart/form-data");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postMultipartFormDataAsJson(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("multipart/form-data");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(ProjectVersion.class);
        }

        public<T >T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postXml(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(ProjectVersion.class);
        }

        public<T >T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postJson(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public Localhost_FlexoRest.ProjectsProjectIDVersions.IdFile idFile(Integer id) {
            return new Localhost_FlexoRest.ProjectsProjectIDVersions.IdFile(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
        }

        public Localhost_FlexoRest.ProjectsProjectIDVersions.Next next() {
            return new Localhost_FlexoRest.ProjectsProjectIDVersions.Next(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues));
        }

        public Localhost_FlexoRest.ProjectsProjectIDVersions.Id id(Integer id) {
            return new Localhost_FlexoRest.ProjectsProjectIDVersions.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
        }

        public static class Id {

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
                if (template.charAt((template.length()- 1))!= '/') {
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
                return ((Integer) _templateAndMatrixParameterValues.get("id"));
            }

            /**
             * Duplicate state and set id
             * 
             */
            public Localhost_FlexoRest.ProjectsProjectIDVersions.Id setId(Integer id) {
                Map<String, Object> copyMap;
                copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
                UriBuilder copyUriBuilder = _uriBuilder.clone();
                copyMap.put("id", id);
                return new Localhost_FlexoRest.ProjectsProjectIDVersions.Id(_client, copyUriBuilder, copyMap);
            }

            public ProjectVersion getAsProjectVersionXml() {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(ProjectVersion.class);
            }

            public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsXml(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(ProjectVersion.class);
            }

            public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsJson(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
            }

            public<T >T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T delete(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(ProjectVersion.class);
            }

            public<T >T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putXml(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(ProjectVersion.class);
            }

            public<T >T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putJson(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                    }
                }
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    return response.getEntity(returnType);
                } else {
                    return returnType.cast(response);
                }
            }

        }

        public static class IdFile {

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
                if (template.charAt((template.length()- 1))!= '/') {
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
                return ((Integer) _templateAndMatrixParameterValues.get("id"));
            }

            /**
             * Duplicate state and set id
             * 
             */
            public Localhost_FlexoRest.ProjectsProjectIDVersions.IdFile setId(Integer id) {
                Map<String, Object> copyMap;
                copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
                UriBuilder copyUriBuilder = _uriBuilder.clone();
                copyMap.put("id", id);
                return new Localhost_FlexoRest.ProjectsProjectIDVersions.IdFile(_client, copyUriBuilder, copyMap);
            }

            public<T >T getAsOctetStream(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/octet-stream");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsOctetStream(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/octet-stream");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                    }
                }
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    return response.getEntity(returnType);
                } else {
                    return returnType.cast(response);
                }
            }

        }

        public static class Next {

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

            public<T >T getAsTextPlain(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("text/plain");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsTextPlain(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("text/plain");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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

    public static class Users {

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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(User.class);
        }

        public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(User.class);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsXml(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(User.class);
        }

        public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(User.class);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T getAsJson(Integer start, Integer end, String sort, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            if (start == null) {
            }
            if (start!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", start);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("start", ((Object[]) null));
            }
            if (end == null) {
            }
            if (end!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", end);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("end", ((Object[]) null));
            }
            if (sort == null) {
            }
            if (sort!= null) {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", sort);
            } else {
                localUriBuilder = localUriBuilder.replaceQueryParam("sort", ((Object[]) null));
            }
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(User.class);
        }

        public<T >T postXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postXml(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/xml");
            resourceBuilder = resourceBuilder.type("application/xml");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(User.class);
        }

        public<T >T postJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (response.getStatus()>= 400) {
                throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
            }
            return response.getEntity(returnType);
        }

        public<T >T postJson(Object input, Class<T> returnType) {
            UriBuilder localUriBuilder = _uriBuilder.clone();
            com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
            com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
            resourceBuilder = resourceBuilder.accept("application/json");
            resourceBuilder = resourceBuilder.type("application/json");
            com.sun.jersey.api.client.ClientResponse response;
            response = resourceBuilder.method("POST", com.sun.jersey.api.client.ClientResponse.class, input);
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
            }
            if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                return response.getEntity(returnType);
            } else {
                return returnType.cast(response);
            }
        }

        public Localhost_FlexoRest.Users.Id id(String id) {
            return new Localhost_FlexoRest.Users.Id(_client, _uriBuilder.buildFromMap(_templateAndMatrixParameterValues), id);
        }

        public static class Id {

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
                if (template.charAt((template.length()- 1))!= '/') {
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
                return ((String) _templateAndMatrixParameterValues.get("id"));
            }

            /**
             * Duplicate state and set id
             * 
             */
            public Localhost_FlexoRest.Users.Id setId(String id) {
                Map<String, Object> copyMap;
                copyMap = new HashMap<String, Object>(_templateAndMatrixParameterValues);
                UriBuilder copyUriBuilder = _uriBuilder.clone();
                copyMap.put("id", id);
                return new Localhost_FlexoRest.Users.Id(_client, copyUriBuilder, copyMap);
            }

            public User getAsUserXml() {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(User.class);
            }

            public<T >T getAsXml(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsXml(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(User.class);
            }

            public<T >T getAsJson(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T getAsJson(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("GET", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(com.sun.jersey.api.client.ClientResponse.class);
            }

            public<T >T delete(com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T delete(Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("DELETE", com.sun.jersey.api.client.ClientResponse.class);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(User.class);
            }

            public<T >T putXml(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putXml(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/xml");
                resourceBuilder = resourceBuilder.type("application/xml");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(User.class);
            }

            public<T >T putJson(Object input, com.sun.jersey.api.client.GenericType<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (response.getStatus()>= 400) {
                    throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
                }
                return response.getEntity(returnType);
            }

            public<T >T putJson(Object input, Class<T> returnType) {
                UriBuilder localUriBuilder = _uriBuilder.clone();
                com.sun.jersey.api.client.WebResource resource = _client.resource(localUriBuilder.buildFromMap(_templateAndMatrixParameterValues));
                com.sun.jersey.api.client.WebResource.Builder resourceBuilder = resource.getRequestBuilder();
                resourceBuilder = resourceBuilder.accept("application/json");
                resourceBuilder = resourceBuilder.type("application/json");
                com.sun.jersey.api.client.ClientResponse response;
                response = resourceBuilder.method("PUT", com.sun.jersey.api.client.ClientResponse.class, input);
                if (!com.sun.jersey.api.client.ClientResponse.class.isAssignableFrom(returnType)) {
                    if (response.getStatus()>= 400) {
                        throw new Localhost_FlexoRest.WebApplicationExceptionMessage(Response.status(response.getClientResponseStatus()).build());
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
    private static class WebApplicationExceptionMessage
        extends WebApplicationException
    {


        private WebApplicationExceptionMessage(Response response) {
            super(response);
        }

        /**
         * Workaround for JAX_RS_SPEC-312
         * 
         */
        public String getMessage() {
            Response response = getResponse();
            Response.Status status = Response.Status.fromStatusCode(response.getStatus());
            if (status!= null) {
                return (response.getStatus()+(" "+ status.getReasonPhrase()));
            } else {
                return Integer.toString(response.getStatus());
            }
        }

        public String toString() {
            String s = "javax.ws.rs.WebApplicationException";
            String message = getLocalizedMessage();
            return (s +(": "+ message));
        }

    }

}
