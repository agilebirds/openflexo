package org.openflexo.tm.hibernate.impl;

import org.junit.Before;
import org.junit.Test;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.ProjectRepository;
import org.openflexo.foundation.dm.action.CreateDMEntityEnum;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.implmodel.CreateImplementationModel;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.ImplementationModelDefinition;
import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.tm.persistence.impl.HibernateEnum;
import org.openflexo.tm.persistence.impl.HibernateEnumValue;
import org.openflexo.tm.persistence.impl.HibernateModel;
import org.openflexo.tm.persistence.impl.PersistenceImplementation;
import org.openflexo.toolbox.ToolBox;

import javax.naming.InvalidNameException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class HibernateEnumTest {

    private FlexoProject project;
    private FlexoEditor editor;

    @Before
    public void createPrj() {
        ToolBox.setPlatform();
        FlexoLoggingManager.forceInitialize();
        File _projectDirectory = null;
        try {
            File tempFile = File.createTempFile("HibernateEnumTest", "");
            _projectDirectory = new File(tempFile.getParentFile(), tempFile.getName() + ".prj");
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        editor = FlexoResourceManager.initializeNewProject(_projectDirectory, EDITOR_FACTORY, null);
        project = editor.getProject();
    }

    @Test
    public void testWatchedRepository() {
        ProjectRepository nonPersistentRepository = ProjectRepository.createNewProjectRepository("testRepository", project.getDataModel());
        DMPackage pack = nonPersistentRepository.createPackage("eu.bleupimento");
        CreateDMEntityEnum createDMEntityEnum = (CreateDMEntityEnum) CreateDMEntityEnum.actionType.makeNewAction(pack, null, editor).doAction();
        DMEntity newEntity = createDMEntityEnum.getNewEntity();
        DMProperty p1 = newEntity.createDMProperty("enumValue1", DMType.makeStringDMType(project), DMPropertyImplementationType.PUBLIC_FIELD);

        GeneratedSources generatedSources = GeneratedSources.createNewGeneratedSources(project);

        ImplementationModelDefinition implModelDef = createImplementationModelDefinition(generatedSources, "implModelTest");

        ImplementationModel implementationModel = new ImplementationModel(implModelDef, project);
        PersistenceImplementation hibernateImplementation = createHibernateImplementation(implementationModel);
        HibernateModel hibernateModel = createHibernateModel("myHibernateModel", hibernateImplementation, nonPersistentRepository);

        //now ensure hibernateModel contains stuff derived from the repository
        assertEquals("Expecting one enum",1,hibernateModel.getHibernateEnumContainer().getHibernateEnums().size());
        HibernateEnum hibernateEnum = hibernateModel.getHibernateEnumContainer().getHibernateEnums().get(0);
        assertEquals("Expecting one value in the enum.",hibernateEnum.getHibernateEnumValues().size(),1);
        HibernateEnumValue hibernateEnumValue = hibernateEnum.getHibernateEnumValues().get(0);
        assertLinked(hibernateEnumValue, p1);
        assertLinked(hibernateEnum, newEntity);
        assertHibernateEnumFullSynchronized(hibernateEnum, newEntity);
        //Now let's create a new enumValue in FlexoModel
        DMProperty p2 = newEntity.createDMProperty("enumValue2", DMType.makeStringDMType(project), DMPropertyImplementationType.PUBLIC_FIELD);
        assertHibernateEnumFullSynchronized(hibernateEnum, newEntity);
        //Now let's put a description in Flexo and ensure we are still synchronized
        p1.setDescription("some text");
        assertHibernateEnumFullSynchronized(hibernateEnum, newEntity);
        assertEquals("EnumValues are supposed to be synchronized and so description must be the same.","some text",hibernateEnumValue.getDescription());
        //Now let's change the description in the hibernate model and check that attribute description is no more synchronized
        hibernateEnumValue.setDescription("other text");
        assertFalse(hibernateEnumValue.getDerivedAttributeSynchronizationStatusForKey("description"));
        try{
            assertHibernateEnumFullSynchronized(hibernateEnum, newEntity);
            fail("Shouldn't be full synchronized now.");
        }catch(AssertionError e){
            //that's expected.
            e.printStackTrace();
        }
        //now deleting p1 to found our fullSynchronized status
        p1.delete();
        assertHibernateEnumFullSynchronized(hibernateEnum, newEntity);
    }

    private void assertHibernateEnumFullSynchronized(HibernateEnum hibernateEnum, DMEntity entityEnum){
        assertLinked(hibernateEnum, entityEnum);
        assertEquals("hibernateEnum don't have same size as Flexo enum entity.", hibernateEnum.getHibernateEnumValues().size(), entityEnum.getProperties().size());
        assertEquals("hibernateEnum and FlexoEntity mismatch : name don't match", hibernateEnum.getName(), hibernateEnum.getDerivedName());
        assertEquals("hibernateEnum and FlexoEntity mismatch : description don't match", hibernateEnum.getDescription(), hibernateEnum.getDerivedDescription());
        for(HibernateEnumValue hibernateEnumValue:hibernateEnum.getHibernateEnumValues()){
            assertHibernateEnumValueFullSynchronized(hibernateEnumValue,hibernateEnumValue.getLinkedFlexoModelObject());
        }
    }

    private void assertHibernateEnumValueFullSynchronized(HibernateEnumValue hibernateEnumValue, DMProperty property){
        assertNotNull("Cannot check full synchronize on a null hibernateEnumValue",hibernateEnumValue);
        assertNotNull("Cannot check full synchronize with a null flexoModelObject",hibernateEnumValue);
        assertLinked(hibernateEnumValue, property);
        assertEquals("hibernateEnumValue and FlexoEntity mismatch : name don't match", hibernateEnumValue.getName(), hibernateEnumValue.getDerivedName());
        assertEquals("hibernateEnumValue and FlexoEntity mismatch : description don't match", hibernateEnumValue.getDescription(), hibernateEnumValue.getDerivedDescription());
    }

    private void assertLinked(LinkableTechnologyModelObject linkable, Object expectedLinkedObject){
        assertEquals("Was expecting that "+linkable+" linked with :"+expectedLinkedObject+". But it is linked with "+linkable.getLinkedFlexoModelObject(),
                expectedLinkedObject,linkable.getLinkedFlexoModelObject());
    }

    private ImplementationModelDefinition createImplementationModelDefinition(GeneratedSources generatedSources, String newModelName){
          CreateImplementationModel createImplementationModel = CreateImplementationModel.actionType.makeNewAction(generatedSources, null, editor);
        createImplementationModel.skipDialog = true;
        createImplementationModel.newModelName = newModelName;
        createImplementationModel.doAction();

        return createImplementationModel.getNewImplementationModelDefinition();
    }
    private PersistenceImplementation createHibernateImplementation(ImplementationModel implementationModel){
         PersistenceImplementation hibernateImplementation = null;
        try {
            hibernateImplementation = new PersistenceImplementation(implementationModel);
        } catch (TechnologyModuleCompatibilityCheckException e) {
           e.printStackTrace();
            fail("Fail to create HibernateImplementation " + e.getMessage());
        }
        return hibernateImplementation;
    }
    private HibernateModel createHibernateModel(String hibernateModelName, PersistenceImplementation hibernateImplementation, ProjectRepository watchedRepository) {
        HibernateModel hibernateModel = null;
        try {
            hibernateModel = HibernateModel.createNewHibernateModel(hibernateModelName, hibernateImplementation, watchedRepository);
        } catch (DuplicateResourceException e) {
            e.printStackTrace();
            fail("Fail to create HibernateModel " + e.getMessage());
        } catch (InvalidNameException e) {
            e.printStackTrace();
            fail("Fail to create HibernateModel " + e.getMessage());
        }
        return hibernateModel;
    }

    protected static final FlexoEditor.FlexoEditorFactory EDITOR_FACTORY = new FlexoEditor.FlexoEditorFactory() {
        @Override
        public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
            return new FlexoTestEditor(project);
        }
    };

    public static class FlexoTestEditor extends DefaultFlexoEditor {
        public FlexoTestEditor(FlexoProject project) {
            super(project);
        }

        @Override
        public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionInitializer<? super A> getInitializerFor(
                FlexoActionType<A, T1, T2> actionType) {
            FlexoActionInitializer<A> init = new FlexoActionInitializer<A>() {

                @Override
                public boolean run(ActionEvent event, A action) {
                    boolean reply = action.getActionType().isEnabled(action.getFocusedObject(), action.getGlobalSelection(),
                            FlexoTestEditor.this);
                    if (!reply) {
                        System.err.println("ACTION NOT ENABLED :" + action.getClass() + " on object "
                                + (action.getFocusedObject() != null ? action.getFocusedObject().getClass() : "null focused object"));
                    }
                    return reply;
                }

            };
            return init;
        }
    }
}
