
package org.openflexo.rest.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openflexo.rest.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Account_QNAME = new QName("http://www.agilebirds.com/openflexo", "Account");
    private final static QName _Slave_QNAME = new QName("http://www.agilebirds.com/openflexo", "Slave");
    private final static QName _Project_QNAME = new QName("http://www.agilebirds.com/openflexo", "Project");
    private final static QName _Job_QNAME = new QName("http://www.agilebirds.com/openflexo", "Job");
    private final static QName _User_QNAME = new QName("http://www.agilebirds.com/openflexo", "User");
    private final static QName _ProjectVersion_QNAME = new QName("http://www.agilebirds.com/openflexo", "ProjectVersion");
    private final static QName _Session_QNAME = new QName("http://www.agilebirds.com/openflexo", "Session");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openflexo.rest.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link User }
     * 
     */
    public User createUser() {
        return new User();
    }

    /**
     * Create an instance of {@link Job }
     * 
     */
    public Job createJob() {
        return new Job();
    }

    /**
     * Create an instance of {@link Session }
     * 
     */
    public Session createSession() {
        return new Session();
    }

    /**
     * Create an instance of {@link ProjectVersion }
     * 
     */
    public ProjectVersion createProjectVersion() {
        return new ProjectVersion();
    }

    /**
     * Create an instance of {@link Slave }
     * 
     */
    public Slave createSlave() {
        return new Slave();
    }

    /**
     * Create an instance of {@link Account }
     * 
     */
    public Account createAccount() {
        return new Account();
    }

    /**
     * Create an instance of {@link Project }
     * 
     */
    public Project createProject() {
        return new Project();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Account }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.agilebirds.com/openflexo", name = "Account")
    public JAXBElement<Account> createAccount(Account value) {
        return new JAXBElement<Account>(_Account_QNAME, Account.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Slave }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.agilebirds.com/openflexo", name = "Slave")
    public JAXBElement<Slave> createSlave(Slave value) {
        return new JAXBElement<Slave>(_Slave_QNAME, Slave.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Project }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.agilebirds.com/openflexo", name = "Project")
    public JAXBElement<Project> createProject(Project value) {
        return new JAXBElement<Project>(_Project_QNAME, Project.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Job }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.agilebirds.com/openflexo", name = "Job")
    public JAXBElement<Job> createJob(Job value) {
        return new JAXBElement<Job>(_Job_QNAME, Job.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link User }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.agilebirds.com/openflexo", name = "User")
    public JAXBElement<User> createUser(User value) {
        return new JAXBElement<User>(_User_QNAME, User.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProjectVersion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.agilebirds.com/openflexo", name = "ProjectVersion")
    public JAXBElement<ProjectVersion> createProjectVersion(ProjectVersion value) {
        return new JAXBElement<ProjectVersion>(_ProjectVersion_QNAME, ProjectVersion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Session }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.agilebirds.com/openflexo", name = "Session")
    public JAXBElement<Session> createSession(Session value) {
        return new JAXBElement<Session>(_Session_QNAME, Session.class, null, value);
    }

}
