
package org.openflexo.rest.client.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openflexo.rest.client.model package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openflexo.rest.client.model
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
     * Create an instance of {@link Slave }
     * 
     */
    public Slave createSlave() {
        return new Slave();
    }

    /**
     * Create an instance of {@link TocEntryDefinition }
     * 
     */
    public TocEntryDefinition createTocEntryDefinition() {
        return new TocEntryDefinition();
    }

    /**
     * Create an instance of {@link ProjectVersion }
     * 
     */
    public ProjectVersion createProjectVersion() {
        return new ProjectVersion();
    }

    /**
     * Create an instance of {@link UserProject }
     * 
     */
    public UserProject createUserProject() {
        return new UserProject();
    }

    /**
     * Create an instance of {@link UserProjectPK }
     * 
     */
    public UserProjectPK createUserProjectPK() {
        return new UserProjectPK();
    }

    /**
     * Create an instance of {@link Session }
     * 
     */
    public Session createSession() {
        return new Session();
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
     * Create an instance of {@link JobHistory }
     * 
     */
    public JobHistory createJobHistory() {
        return new JobHistory();
    }

}
