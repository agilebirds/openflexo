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
package org.openflexo.foundation.dm;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import junit.framework.AssertionFailedError;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.IntegerStaticBinding;
import org.openflexo.foundation.bindings.MethodCall;
import org.openflexo.foundation.dm.DMEntity.DMTypeVariable;
import org.openflexo.foundation.dm.DMMethod.DMMethodParameter;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.MethodReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.PropertyReference;
import org.openflexo.foundation.dm.action.CreateDMMethod;
import org.openflexo.foundation.dm.action.CreateProjectRepository;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.action.ImportJDKEntity;
import org.openflexo.foundation.dm.action.UpdateLoadableDMEntity;
import org.openflexo.foundation.ie.ComponentInstanceBinding;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.cl.action.AddComponentFolder;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.WKFElementType;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.SetAndOpenOperationComponent;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.toolbox.FileUtils;

public class TestDMType extends FlexoDMTestCase {

	private static final Logger logger = Logger.getLogger(TestDMType.class.getPackage().getName());

	private static FlexoEditor _editor;
	private static FlexoProject _project;
	private static final String TEST_DMTYPE = "TestDMType";

	public static void main(String[] args) {
		String test1 = "coucou";
		Class test1Class = test1.getClass();
		logger.info("test1: " + test1Class + " isArray=" + test1Class.isArray());

		String[] test2 = new String[2];
		Class test2Class = test2.getClass();
		logger.info("test2: " + test2Class + " isArray=" + test2Class.isArray() + " dim=" + DMType.arrayDepth(test2Class));

		String[][] test3 = new String[2][3];
		Class test3Class = test3.getClass();
		logger.info("test3: " + test3Class + " isArray=" + test3Class.isArray() + " dim=" + DMType.arrayDepth(test3Class));

		/*
		 * logger.info(""+DMType.dmTypeConverter.convertFromString("int")._pendingInformations);
		 * logger.info(""+DMType.dmTypeConverter.convertFromString("int[]")._pendingInformations);
		 * logger.info(""+DMType.dmTypeConverter.convertFromString("java.lang.String")._pendingInformations);
		 * logger.info(""+DMType.dmTypeConverter.convertFromString("java.lang.String[][]")._pendingInformations);
		 * logger.info(""+DMType.dmTypeConverter.convertFromString("java.util.Vector<java.lang.String>")._pendingInformations);
		 * logger.info(""
		 * +DMType.dmTypeConverter.convertFromString("java.util.Hashtable<java.lang.String,java.util.Vector<java.io.File>>[][][]"
		 * )._pendingInformations);
		 * logger.info(""+DMType.dmTypeConverter.convertFromString("java.util.Vector<java.util.Hashtable<java.lang.String,java.io.File>>"
		 * )._pendingInformations);
		 * 
		 * DMType.dmTypeConverter.convertFromString("org.openflexo.foundation.action.FlexoExceptionHandler<? super A>");
		 */

		Class test4Class = Test4Class.class;
		Field f = null;
		java.lang.reflect.Type type = null;
		try {
			f = test4Class.getField("test4");
			type = f.getGenericType();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (TypeVariable tv : f.getType().getTypeParameters()) {
			logger.info("TypeVariable: " + tv + " " + tv.getBounds() + " " + tv.getName() + " GD='" + tv.getGenericDeclaration() + "' class=" + tv
					.getClass().getSimpleName());
			for (java.lang.reflect.Type t : tv.getBounds()) {
				logger.info("Bound=" + t + " t=" + t.getClass().getSimpleName());
			}
		}

		logger.info("type=" + type + " of " + type.getClass());

		Class test5Class = FlexoAction.class;
		for (TypeVariable tv : test5Class.getTypeParameters()) {
			logger.info("TypeVariable: " + tv + " " + tv.getBounds() + " " + tv.getName() + " GD='" + tv.getGenericDeclaration() + "' class=" + tv
					.getClass().getSimpleName());
			for (java.lang.reflect.Type t : tv.getBounds()) {
				logger.info("Bound=" + t + " t=" + t.getClass().getSimpleName());
			}
		}
	}

	public TestDMType() {
		super("TestDMType");
	}

	/**
	 * Overrides setUp
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	private static LoadableDMEntity comparableEntity;
	private static LoadableDMEntity vectorEntity;
	private static LoadableDMEntity hashtableEntity;
	private static LoadableDMEntity dictionaryEntity;
	private static LoadableDMEntity abstractListEntity;
	private static LoadableDMEntity abstractCollectionEntity;
	private static LoadableDMEntity listEntity;
	private static LoadableDMEntity enumerationEntity;
	private static LoadableDMEntity iteratorEntity;
	private static LoadableDMEntity nsArrayEntity;

	private static LoadableDMEntity test4ClassEntity;

	public void test0CreateProject() {
		_editor = createProject(TEST_DMTYPE);
		_project = _editor.getProject();
	}

	public void test1PerformSomeChecksOnInitialProject() {
		assertNotNull(_project.getDataModel().getEntityNamed("java.lang.Object"));
		assertNotNull(comparableEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("java.lang.Comparable"));
		assertNotNull(_project.getDataModel().getEntityNamed("java.util.Date"));
		assertNotNull(dictionaryEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("java.util.Dictionary"));
		assertNotNull(hashtableEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("java.util.Hashtable"));
		assertNotNull(vectorEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("java.util.Vector"));
		assertNotNull(abstractListEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("java.util.AbstractList"));
		assertNotNull(abstractCollectionEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("java.util.AbstractCollection"));
		assertNotNull(listEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("java.util.List"));

		assertEquals(1, comparableEntity.getTypeVariables().size());
		assertEquals("T", comparableEntity.getTypeVariables().firstElement().getName());

		assertEquals(1, vectorEntity.getTypeVariables().size());
		assertEquals("E", vectorEntity.getTypeVariables().firstElement().getName());

		// Vector<E> extends AbstractList<E>
		assertEquals(DMType.makeResolvedDMType(abstractListEntity,
				DMType.makeTypeVariableDMType(vectorEntity.getTypeVariables().firstElement())), vectorEntity.getParentType());
		// AbstractList<E> extends AbstractCollection<E>
		assertEquals(
				DMType.makeResolvedDMType(abstractCollectionEntity,
						DMType.makeTypeVariableDMType(abstractListEntity.getTypeVariables().firstElement())),
				abstractListEntity.getParentType());
		// AbstractList<E> implements List<E>
		assertEquals(1, abstractListEntity.getImplementedTypes().size());
		assertEquals(
				DMType.makeResolvedDMType(listEntity, DMType.makeTypeVariableDMType(abstractListEntity.getTypeVariables().firstElement())),
				abstractListEntity.getImplementedTypes().firstElement());

	}

	private static DMProperty foo1;
	private static DMProperty foo2;
	private static DMProperty foo3;
	private static DMProperty foo4;
	private static DMProperty foo5;
	private static DMProperty foo6;
	private static DMProperty foo7;
	private static DMProperty foo8;
	private static DMProperty foo9;
	private static DMProperty test4;

	private static DMMethod method1;
	private static DMMethod method2;
	private static DMMethod remove;

	public void test2ImportTest4Class() {
		ImportJDKEntity importAction = ImportJDKEntity.actionType.makeNewAction(_project.getDataModel().getJDKRepository(), null, _editor);
		importAction.setClassName("Test4Class");
		importAction.setPackageName("org.openflexo.foundation.dm");
		importAction.setImportGetOnlyProperties(false);
		importAction.setImportMethods(true);
		assertTrue(importAction.doAction().hasActionExecutionSucceeded());
		saveProject(_project);

		assertNotNull(test4ClassEntity = (LoadableDMEntity) _project.getDataModel().getDMEntity(Test4Class.class));
		assertEquals(10, test4ClassEntity.getDeclaredProperties().size());
		assertEquals(3, test4ClassEntity.getDeclaredMethods().size());

		assertEquals(2, test4ClassEntity.getTypeVariables().size());
		assertEquals("T1", test4ClassEntity.getTypeVariables().elementAt(0).getName());
		assertEquals("java.io.File,org.openflexo.kvc.KeyValueCoding", test4ClassEntity.getTypeVariables().elementAt(0).getBounds());
		assertEquals("T2", test4ClassEntity.getTypeVariables().elementAt(1).getName());
		assertEquals("java.util.Date", test4ClassEntity.getTypeVariables().elementAt(1).getBounds());

		assertTrue(test4ClassEntity.getParentType().isResolved());
		assertEquals("java.util.Hashtable<java.lang.String, java.util.Hashtable<T1, T2>>", test4ClassEntity.getParentType()
				.getStringRepresentation());

		assertEquals(4, test4ClassEntity.getImplementedTypes().size());
		assertTrue(test4ClassEntity.getImplementedTypes().elementAt(0).isResolved());
		assertEquals("java.util.Map<java.lang.String, java.util.Hashtable<T1, T2>>", test4ClassEntity.getImplementedTypes().elementAt(0)
				.getStringRepresentation());
		assertTrue(test4ClassEntity.getImplementedTypes().elementAt(1).isResolved());
		assertEquals("java.util.Iterator<java.util.Vector<T2>>", test4ClassEntity.getImplementedTypes().elementAt(1)
				.getStringRepresentation());
		assertTrue(test4ClassEntity.getImplementedTypes().elementAt(2).isResolved());
		assertEquals("java.lang.Cloneable", test4ClassEntity.getImplementedTypes().elementAt(2).getStringRepresentation());
		assertTrue(test4ClassEntity.getImplementedTypes().elementAt(3).isResolved());
		assertEquals("java.io.Serializable", test4ClassEntity.getImplementedTypes().elementAt(3).getStringRepresentation());

		assertNotNull(foo1 = test4ClassEntity.getDMProperty("foo1"));
		assertTrue(foo1.getType().isResolved());
		assertTrue(foo1.getType().isString());

		assertNotNull(foo2 = test4ClassEntity.getDMProperty("foo2"));
		assertTrue(foo2.getType().isResolved());
		assertTrue(foo2.getType().getBaseEntity() == _project.getDataModel().getEntityNamed("int"));
		assertTrue(foo2.getType().getDimensions() == 2);

		assertNotNull(foo3 = test4ClassEntity.getDMProperty("foo3"));
		assertTrue(foo3.getType().isResolved());
		assertEquals("java.util.Map<java.io.File, java.util.Hashtable<java.lang.Integer, java.lang.String>[]>", foo3.getType()
				.getStringRepresentation());

		assertNotNull(foo4 = test4ClassEntity.getDMProperty("foo4"));
		assertTrue(foo4.getType().isResolved());
		assertEquals(DMType.KindOfType.TYPE_VARIABLE, foo4.getType().getKindOfType());
		assertEquals("T1", foo4.getType().getTypeVariable().getName());

		assertNotNull(foo5 = test4ClassEntity.getDMProperty("foo5"));
		assertTrue(foo5.getType().isResolved());
		assertEquals(DMType.KindOfType.TYPE_VARIABLE, foo5.getType().getKindOfType());
		assertEquals("T2", foo5.getType().getTypeVariable().getName());

		assertNotNull(foo6 = test4ClassEntity.getDMProperty("foo6"));
		assertTrue(foo6.getType().isResolved());
		assertEquals("java.util.Hashtable<T1, ?>[][]", foo6.getType().getStringRepresentation());

		assertNotNull(foo7 = test4ClassEntity.getDMProperty("foo7"));
		assertTrue(foo7.getType().isResolved());
		assertEquals("java.util.Hashtable<T1, T2>", foo7.getType().getStringRepresentation());

		assertNotNull(foo8 = test4ClassEntity.getDMProperty("foo8"));
		assertTrue(foo8.getType().isResolved());
		assertEquals("java.util.Hashtable<? extends T1, ? super T2>", foo8.getType().getStringRepresentation());

		assertNotNull(foo9 = test4ClassEntity.getDMProperty("foo9"));
		assertTrue(foo9.getType().isResolved());
		assertEquals("java.util.Map<? extends java.io.File, java.util.Hashtable<java.lang.Integer, java.util.Vector<? extends T1>>>[]",
				foo9.getType().getStringRepresentation());

		assertNotNull(test4 = test4ClassEntity.getDMProperty("test4"));
		assertTrue(test4.getType().isResolved());
		assertEquals("java.util.Hashtable<java.lang.String, java.util.Vector<java.io.File>>", test4.getType().getStringRepresentation());

		assertNotNull(method1 = test4ClassEntity
				.getMethod("method1(java.lang.String,java.util.Hashtable<T1, T2>,java.util.Map<java.io.File, java.util.Hashtable<java.lang.Integer, java.lang.String>[]>)"));
		assertEquals(3, method1.getParameters().size());
		assertTrue(method1.getParameters().elementAt(0).getType().isResolved());
		assertTrue(method1.getParameters().elementAt(0).getType().isString());
		assertTrue(method1.getParameters().elementAt(1).getType().isResolved());
		assertEquals("java.util.Hashtable<T1, T2>", method1.getParameters().elementAt(1).getType().getStringRepresentation());
		assertTrue(method1.getParameters().elementAt(2).getType().isResolved());
		assertEquals("java.util.Map<java.io.File, java.util.Hashtable<java.lang.Integer, java.lang.String>[]>", method1.getParameters()
				.elementAt(2).getType().getStringRepresentation());

		assertNotNull(method2 = test4ClassEntity.getMethod("method2(java.util.Vector<? super T1>,java.util.Vector<? extends T2>)"));
		assertEquals(2, method2.getParameters().size());
		assertEquals(DMType.KindOfType.WILDCARD, method2.getParameters().elementAt(0).getType().getParameters().elementAt(0)
				.getKindOfType());
		assertEquals(DMType.KindOfType.WILDCARD, method2.getParameters().elementAt(1).getType().getParameters().elementAt(0)
				.getKindOfType());

		assertNotNull(remove = test4ClassEntity.getMethod("remove()"));
		assertEquals(0, remove.getParameters().size());
	}

	private static DMEntity entity1;
	private static DMEntity entity2;
	private static DMEntity entity3;
	private static DMEntity entity4;
	private static DMProperty property1;
	private static DMProperty property2;
	private static DMProperty property3;
	private static DMProperty property4;
	private static DMProperty p1;
	private static DMProperty p2;
	private static DMProperty p3;
	private static DMProperty p4;

	public void test3ProjectRepository() {
		CreateProjectRepository createProjectRepository = CreateProjectRepository.actionType.makeNewAction(_project.getDataModel(), null,
				_editor);
		createProjectRepository.setNewRepositoryName("TestRepository");
		assertTrue(createProjectRepository.doAction().hasActionExecutionSucceeded());
		ProjectRepository repository = createProjectRepository.getNewRepository();

		/*
		 * Building this model:
		 * 
		 * Entity1<T1> extends Object T1 property1;
		 * 
		 * Entity2<A,B> extends Entity1<Hashtable<A,B>> A property2; B property3;
		 * 
		 * Entity3<C> extends Entity2<C,Vector<C>> C property4;
		 */

		entity1 = new DMEntity(_project.getDataModel(), "Entity1", "test.package", "Entity1", DMType.makeResolvedDMType(_project
				.getDataModel().getDMEntity(Object.class)));
		repository.registerEntity(entity1);
		DMTypeVariable t1 = new DMTypeVariable(_project.getDataModel(), entity1);
		t1.setName("T1");
		entity1.addToTypeVariables(t1);
		property1 = new DMProperty(_project.getDataModel(), "property1", DMType.makeTypeVariableDMType(t1), DMCardinality.SINGLE, false,
				true, DMPropertyImplementationType.PUBLIC_FIELD);
		entity1.registerProperty(property1, true);

		entity2 = new DMEntity(_project.getDataModel(), "Entity2", "test.package", "Entity2", null);
		DMTypeVariable a = new DMTypeVariable(_project.getDataModel(), entity2);
		a.setName("A");
		DMTypeVariable b = new DMTypeVariable(_project.getDataModel(), entity2);
		b.setName("B");
		entity2.addToTypeVariables(a);
		entity2.addToTypeVariables(b);
		entity2.setParentType(
				DMType.makeResolvedDMType(entity1, DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Hashtable.class),
						DMType.makeTypeVariableDMType(a), DMType.makeTypeVariableDMType(b))), true);
		repository.registerEntity(entity2);
		property2 = new DMProperty(_project.getDataModel(), "property2", DMType.makeTypeVariableDMType(a), DMCardinality.SINGLE, false,
				true, DMPropertyImplementationType.PUBLIC_FIELD);
		entity2.registerProperty(property2, true);
		property3 = new DMProperty(_project.getDataModel(), "property3", DMType.makeTypeVariableDMType(b), DMCardinality.SINGLE, false,
				true, DMPropertyImplementationType.PUBLIC_FIELD);
		entity2.registerProperty(property3, true);

		entity3 = new DMEntity(_project.getDataModel(), "Entity3", "test.package", "Entity3", null);
		DMTypeVariable c = new DMTypeVariable(_project.getDataModel(), entity3);
		c.setName("C");
		entity3.addToTypeVariables(c);
		entity3.setParentType(
				DMType.makeResolvedDMType(entity2, DMType.makeTypeVariableDMType(c),
						DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Vector.class), DMType.makeTypeVariableDMType(c))),
				true);
		repository.registerEntity(entity3);
		property4 = new DMProperty(_project.getDataModel(), "property4", DMType.makeTypeVariableDMType(c), DMCardinality.SINGLE, false,
				true, DMPropertyImplementationType.PUBLIC_FIELD);
		entity3.registerProperty(property4, true);

		entity4 = new DMEntity(_project.getDataModel(), "Entity4", "test.package", "Entity4", DMType.makeResolvedDMType(_project
				.getDataModel().getDMEntity(Object.class)));
		repository.registerEntity(entity4);
		p1 = new DMProperty(_project.getDataModel(), "p1", DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class)),
				DMCardinality.SINGLE, false, true, DMPropertyImplementationType.PUBLIC_FIELD);
		entity4.registerProperty(p1, true);
		p2 = new DMProperty(_project.getDataModel(), "p2", DMType.makeResolvedDMType(vectorEntity,
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class))), DMCardinality.SINGLE, false, true,
				DMPropertyImplementationType.PUBLIC_FIELD);
		entity4.registerProperty(p2, true);
		p3 = new DMProperty(_project.getDataModel(), "p3", DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class)),
				DMCardinality.SINGLE, false, true, DMPropertyImplementationType.PUBLIC_FIELD);
		entity4.registerProperty(p3, true);
		p4 = new DMProperty(_project.getDataModel(), "p4", DMType.makeResolvedDMType(entity3,
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class))), DMCardinality.SINGLE, false, true,
				DMPropertyImplementationType.PUBLIC_FIELD);
		entity4.registerProperty(p4, true);

		saveProject(_project);

	}

	public void test4DiscoverNewProperties() {
		assertNotNull(nsArrayEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("com.webobjects.foundation.NSArray"));

		Vector<LoadableDMEntity> entitiesToUpdate = new Vector<LoadableDMEntity>();
		Vector<DMObject> actionContext = new Vector<DMObject>();
		entitiesToUpdate.add(listEntity);
		entitiesToUpdate.add(hashtableEntity);
		entitiesToUpdate.add(vectorEntity);
		entitiesToUpdate.add(nsArrayEntity);
		actionContext.add(listEntity);
		actionContext.add(hashtableEntity);
		actionContext.add(vectorEntity);
		actionContext.add(nsArrayEntity);

		UpdateLoadableDMEntity updateEntity = UpdateLoadableDMEntity.actionType.makeNewAction(_project.getDataModel(), actionContext,
				_editor);
		DMSet dmSet = new DMSet(_project, "classes_to_update", entitiesToUpdate, true, null);
		ClassReference listClass = dmSet.getClassReference(List.class);
		for (PropertyReference pr : listClass.getProperties()) {
			if (pr.getName().equals("iterator")) {
				dmSet.addToSelectedObjects(pr);
			}
		}
		ClassReference vectorClass = dmSet.getClassReference(Vector.class);
		for (PropertyReference pr : vectorClass.getProperties()) {
			if (pr.getName().equals("size") || pr.getName().equals("firstElement") || pr.getName().equals("lastElement") || pr.getName()
					.equals("elements") || pr.getName().equals("isEmpty")) {
				dmSet.addToSelectedObjects(pr);
			}
		}
		for (MethodReference mr : vectorClass.getMethods()) {
			if (mr.getSignature().equals("add(E)") || mr.getSignature().equals("add(int,E)") || mr.getSignature().equals("elementAt(int)") || mr
					.getSignature().equals("remove(java.lang.Object)")) {
				dmSet.addToSelectedObjects(mr);
			}
		}
		ClassReference hashtableClass = dmSet.getClassReference(Hashtable.class);
		for (PropertyReference pr : hashtableClass.getProperties()) {
			if (pr.getName().equals("size") || pr.getName().equals("elements")) {
				dmSet.addToSelectedObjects(pr);
			}
		}
		ClassReference nsArrayClass = dmSet.getClassReference("com.webobjects.foundation.NSArray");
		for (PropertyReference pr : nsArrayClass.getProperties()) {
			if (pr.getName().equals("count")) {
				dmSet.addToSelectedObjects(pr);
			}
		}
		updateEntity.setUpdatedSet(dmSet);
		assertTrue(updateEntity.doAction().hasActionExecutionSucceeded());

		assertNotNull(listEntity.getProperty("iterator"));

		assertNotNull(vectorEntity.getProperty("size"));
		assertNotNull(vectorEntity.getProperty("firstElement"));
		assertNotNull(vectorEntity.getProperty("lastElement"));
		assertNotNull(vectorEntity.getProperty("elements"));
		assertNotNull(vectorEntity.getProperty("isEmpty"));

		assertNotNull(vectorEntity.getMethod("add(E)"));
		assertNotNull(vectorEntity.getMethod("add(int,E)"));
		assertNotNull(vectorEntity.getMethod("elementAt(int)"));
		assertNotNull(vectorEntity.getMethod("remove(java.lang.Object)"));

		assertNotNull(hashtableEntity.getProperty("size"));
		assertNotNull(hashtableEntity.getProperty("elements"));

		assertNotNull(nsArrayEntity.getProperty("count"));

		// Enumeration and Iterator should be now created because requested as property in Vector and Hashtable entities

		assertNotNull(enumerationEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("java.util.Enumeration"));
		assertNotNull(iteratorEntity = (LoadableDMEntity) _project.getDataModel().getEntityNamed("java.util.Iterator"));

		Vector<LoadableDMEntity> entitiesToUpdate2 = new Vector<LoadableDMEntity>();
		Vector<DMObject> actionContext2 = new Vector<DMObject>();
		entitiesToUpdate2.add(enumerationEntity);
		entitiesToUpdate2.add(iteratorEntity);
		actionContext2.add(enumerationEntity);
		actionContext2.add(iteratorEntity);

		UpdateLoadableDMEntity updateEntity2 = UpdateLoadableDMEntity.actionType.makeNewAction(_project.getDataModel(), actionContext2,
				_editor);
		DMSet dmSet2 = new DMSet(_project, "classes_to_update", entitiesToUpdate2, true, null);
		ClassReference enumerationClass = dmSet2.getClassReference(Enumeration.class);
		for (PropertyReference pr : enumerationClass.getProperties()) {
			if (pr.getName().equals("hasMoreElements") || pr.getName().equals("nextElement")) {
				dmSet2.addToSelectedObjects(pr);
			}
		}
		ClassReference iteratorClass = dmSet2.getClassReference(Iterator.class);
		for (PropertyReference pr : iteratorClass.getProperties()) {
			if (pr.getName().equals("hasNext") || pr.getName().equals("next")) {
				dmSet2.addToSelectedObjects(pr);
			}
		}
		updateEntity2.setUpdatedSet(dmSet2);
		assertTrue(updateEntity2.doAction().hasActionExecutionSucceeded());

		assertNotNull(enumerationEntity.getProperty("hasMoreElements"));
		assertNotNull(enumerationEntity.getProperty("nextElement"));

		assertNotNull(iteratorEntity.getProperty("hasNext"));
		assertNotNull(iteratorEntity.getProperty("next"));

		saveProject(_project);
	}

	public static final String TEST_COMPONENT_FOLDER = "TestFolder";
	public static final String TEST_COMPONENT = "TestComponent";

	public void test5CreateOperationComponent() {
		FlexoComponentLibrary cl = _project.getFlexoComponentLibrary();
		AddComponentFolder addComponentFolder = AddComponentFolder.actionType.makeNewAction(cl, null, _editor);
		addComponentFolder.setNewFolderName(TEST_COMPONENT_FOLDER);
		addComponentFolder.doAction();
		assertTrue(addComponentFolder.hasActionExecutionSucceeded());
		FlexoComponentFolder cf = cl.getRootFolder().getFlexoComponentFolderWithName(TEST_COMPONENT_FOLDER);
		assertNotNull(cf);

		AddComponent addComponent = AddComponent.actionType.makeNewAction(cf, null, _editor);
		addComponent.setNewComponentName(TEST_COMPONENT);
		addComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		addComponent.doAction();
		assertTrue(addComponent.hasActionExecutionSucceeded());
		IEOperationComponent oc = _project.getOperationComponent(TEST_COMPONENT);
		assertNotNull(oc);

		OperationComponentDefinition cd = oc.getComponentDefinition();
		ComponentDMEntity componentEntity = cd.getComponentDMEntity();

		DMProperty binding1 = new DMProperty(_project.getDataModel(), "binding1", DMType.makeResolvedDMType(listEntity,
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class))), DMCardinality.SINGLE, false, true,
				DMPropertyImplementationType.PUBLIC_FIELD);
		componentEntity.registerProperty(binding1, true);

		DMProperty binding2 = new DMProperty(_project.getDataModel(), "binding2", DMType.makeResolvedDMType(_project.getDataModel()
				.getDMEntity(Object.class)), DMCardinality.SINGLE, false, true, DMPropertyImplementationType.PUBLIC_FIELD);
		componentEntity.registerProperty(binding2, true);

		DMProperty binding3 = new DMProperty(_project.getDataModel(), "binding3", DMType.makeResolvedDMType(vectorEntity,
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class))), DMCardinality.SINGLE, false, true,
				DMPropertyImplementationType.PUBLIC_FIELD);
		componentEntity.registerProperty(binding3, true);

		DMProperty binding4 = new DMProperty(_project.getDataModel(), "binding4", DMType.makeResolvedDMType(dictionaryEntity,
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class)),
				DMType.makeResolvedDMType(vectorEntity, DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class)))),
				DMCardinality.SINGLE, false, true, DMPropertyImplementationType.PUBLIC_FIELD);
		componentEntity.registerProperty(binding4, true);

		DMProperty binding5 = new DMProperty(_project.getDataModel(), "binding5", DMType.makeResolvedDMType(entity2,
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class)),
				DMType.makeResolvedDMType(vectorEntity, DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(String.class)))),
				DMCardinality.SINGLE, false, true, DMPropertyImplementationType.PUBLIC_FIELD);
		componentEntity.registerProperty(binding5, true);

		DMProperty binding6 = new DMProperty(_project.getDataModel(), "binding6", DMType.makeResolvedDMType(_project.getDataModel()
				.getDMEntity(Integer.TYPE)), DMCardinality.SINGLE, false, true, DMPropertyImplementationType.PUBLIC_FIELD);
		componentEntity.registerProperty(binding6, true);

		DMProperty binding7 = new DMProperty(_project.getDataModel(), "binding7", DMType.makeResolvedDMType(_project.getDataModel()
				.getDMEntity(String.class)), DMCardinality.SINGLE, false, true, DMPropertyImplementationType.PUBLIC_FIELD);
		componentEntity.registerProperty(binding7, true);

		saveProject(_project);

	}

	private static OperationNode operationNode;

	public void test6CreateOperationNodeAndAssociateOperation() {
		DropWKFElement action = DropWKFElement.actionType.makeNewAction(_project.getRootFlexoProcess().getActivityPetriGraph(), null,
				_editor);
		action.setElementType(WKFElementType.NORMAL_ACTIVITY);
		action.setLocation(100, 100);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		ActivityNode activityNode = (ActivityNode) action.getObject();

		OpenOperationLevel openOperationLevel = OpenOperationLevel.actionType.makeNewAction(activityNode, null, _editor);
		assertTrue(openOperationLevel.doAction().hasActionExecutionSucceeded());

		DropWKFElement dropOperation = DropWKFElement.actionType.makeNewAction(activityNode.getOperationPetriGraph(), null, _editor);
		dropOperation.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation.setLocation(100, 100);
		assertTrue(dropOperation.doAction().hasActionExecutionSucceeded());

		operationNode = (OperationNode) dropOperation.getObject();
		logger.info("OperationNode " + operationNode.getName() + " successfully created");

		SetAndOpenOperationComponent setOperationComponent = SetAndOpenOperationComponent.actionType.makeNewAction(operationNode, null,
				_editor);
		setOperationComponent.setNewComponentName(TEST_COMPONENT);
		assertTrue(setOperationComponent.doAction().hasActionExecutionSucceeded());

		try {
			_project.getRootFlexoProcess().setBusinessDataVariableName("myBusinessData");
		} catch (InvalidNameException e) {
			e.printStackTrace();
			fail();
		} catch (DuplicatePropertyNameException e) {
			e.printStackTrace();
			fail();
		}
		_project.getRootFlexoProcess().setBusinessDataType(entity4);

		saveProject(_project);

	}

	public void test7TestBindings() {
		ProcessDMEntity processEntity = operationNode.getProcess().getProcessDMEntity();
		DMProperty businessDataProperty = processEntity.getBusinessDataProperty();

		ComponentInstanceBinding binding1 = operationNode.getComponentInstance().getBinding("binding1");
		BindingValue bv1 = new BindingValue(binding1.getBindingDefinition(), operationNode);
		bv1.setBindingVariable(operationNode.getBindingModel().bindingVariableNamed("processInstance"));
		bv1.setBindingPathElementAtIndex(businessDataProperty, 0);
		bv1.setBindingPathElementAtIndex(p2, 1);
		binding1.setBindingValue(bv1);
		assertTrue(bv1.isBindingValid());

		ComponentInstanceBinding binding2 = operationNode.getComponentInstance().getBinding("binding2");
		BindingValue bv2 = new BindingValue(binding2.getBindingDefinition(), operationNode);
		bv2.setBindingVariable(operationNode.getBindingModel().bindingVariableNamed("processInstance"));
		bv2.setBindingPathElementAtIndex(processEntity.getProperty("activityTasks"), 0);
		bv2.setBindingPathElementAtIndex(listEntity.getProperty("iterator"), 1);
		bv2.setBindingPathElementAtIndex(iteratorEntity.getProperty("next"), 2);
		binding2.setBindingValue(bv2);
		assertTrue(bv2.isBindingValid());

		ComponentInstanceBinding binding3 = operationNode.getComponentInstance().getBinding("binding3");
		BindingValue bv3 = new BindingValue(binding3.getBindingDefinition(), operationNode);
		bv3.setBindingVariable(operationNode.getBindingModel().bindingVariableNamed("processInstance"));
		bv3.setBindingPathElementAtIndex(businessDataProperty, 0);
		bv3.setBindingPathElementAtIndex(p4, 1);
		bv3.setBindingPathElementAtIndex(property1, 2);
		bv3.setBindingPathElementAtIndex(hashtableEntity.getProperty("elements"), 3);
		bv3.setBindingPathElementAtIndex(enumerationEntity.getProperty("nextElement"), 4);
		binding3.setBindingValue(bv3);
		assertTrue(bv3.isBindingValid());

		ComponentInstanceBinding binding4 = operationNode.getComponentInstance().getBinding("binding4");
		BindingValue bv4 = new BindingValue(binding4.getBindingDefinition(), operationNode);
		bv4.setBindingVariable(operationNode.getBindingModel().bindingVariableNamed("processInstance"));
		bv4.setBindingPathElementAtIndex(businessDataProperty, 0);
		bv4.setBindingPathElementAtIndex(p4, 1);
		bv4.setBindingPathElementAtIndex(property1, 2);
		binding4.setBindingValue(bv4);
		assertTrue(bv4.isBindingValid());

		ComponentInstanceBinding binding5 = operationNode.getComponentInstance().getBinding("binding5");
		BindingValue bv5 = new BindingValue(binding5.getBindingDefinition(), operationNode);
		bv5.setBindingVariable(operationNode.getBindingModel().bindingVariableNamed("processInstance"));
		bv5.setBindingPathElementAtIndex(businessDataProperty, 0);
		bv5.setBindingPathElementAtIndex(p4, 1);
		binding5.setBindingValue(bv5);
		assertTrue(bv5.isBindingValid());

		ComponentInstanceBinding binding6 = operationNode.getComponentInstance().getBinding("binding6");
		BindingValue bv6 = new BindingValue(binding6.getBindingDefinition(), operationNode);
		bv6.setBindingVariable(operationNode.getBindingModel().bindingVariableNamed("processInstance"));
		bv6.setBindingPathElementAtIndex(processEntity.getProperty("activityTasks"), 0);
		bv6.setBindingPathElementAtIndex(nsArrayEntity.getProperty("count"), 1);
		binding6.setBindingValue(bv6);
		assertTrue(bv6.isBindingValid());

		ComponentInstanceBinding binding7 = operationNode.getComponentInstance().getBinding("binding7");
		BindingValue bv7 = new BindingValue(binding7.getBindingDefinition(), operationNode);
		bv7.setBindingVariable(operationNode.getBindingModel().bindingVariableNamed("processInstance"));
		bv7.setBindingPathElementAtIndex(businessDataProperty, 0);
		bv7.setBindingPathElementAtIndex(p2, 1);
		DMMethod elementAtMethod = vectorEntity.getMethod("elementAt(int)");
		MethodCall elementAt3 = new MethodCall(bv7, elementAtMethod);
		IntegerStaticBinding three = new IntegerStaticBinding(null, elementAt3, 3);
		elementAt3.setBindingValueForParam(three, elementAtMethod.getParameters().firstElement());
		bv7.setBindingPathElementAtIndex(elementAt3, 2);
		binding7.setBindingValue(bv7);
		assertTrue(bv7.isBindingValid());

		saveProject(_project);

	}

	public void test8TestIsAssignableFrom() {
		boolean returned = true;

		DMType objectType = DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Object.class));
		DMType intType = DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(int.class));
		DMType IntegerType = DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Integer.class));
		DMType NumberType = DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Number.class));

		if (!checkAssignable(intType, objectType)) {
			returned = false; // int instanceof Object
		}
		if (!checkAssignable(IntegerType, objectType)) {
			returned = false; // Integer instanceof Object
		}
		if (!checkAssignable(NumberType, objectType)) {
			returned = false; // Number instanceof Object
		}
		if (!checkAssignable(IntegerType, intType)) {
			returned = false; // Integer instanceof int: test autoboxing
		}
		if (!checkAssignable(intType, IntegerType)) {
			returned = false; // int instanceof Integer: test autoboxing
		}
		if (!checkAssignable(IntegerType, NumberType)) {
			returned = false; // Integer instanceof Number
		}
		if (!checkAssignable(intType, NumberType)) {
			returned = false; // int instanceof Number: autoboxing + inheritance
		}
		if (!checkNotAssignable(NumberType, IntegerType)) {
			returned = false; // Integer NOT instanceof Number
		}

		/*
		 * assertTrue(objectType.isAssignableFrom(intType)); // int instanceof Object assertTrue(objectType.isAssignableFrom(IntegerType));
		 * // Integer instanceof Object assertTrue(objectType.isAssignableFrom(NumberType)); // Number instanceof Object
		 * assertTrue(intType.isAssignableFrom(IntegerType)); // Integer instanceof int: test autoboxing
		 * assertTrue(IntegerType.isAssignableFrom(intType)); // int instanceof Integer: test autoboxing
		 * assertTrue(NumberType.isAssignableFrom(IntegerType)); // Integer instanceof Number
		 * assertTrue(NumberType.isAssignableFrom(intType)); // int instanceof Number: autoboxing + inheritance
		 * assertFalse(IntegerType.isAssignableFrom(NumberType)); // Integer NOT instanceof Number
		 */

		Vector<DMType> objectUpperBounds = new Vector<DMType>();
		objectUpperBounds.add(objectType);
		Vector<DMType> NumberUpperBounds = new Vector<DMType>();
		NumberUpperBounds.add(NumberType);

		DMType undefinedVectorType /* Vector */= DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Vector.class));
		DMType objectVectorType /* Vector<Object> */= DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Vector.class),
				objectType);
		DMType wildcardObjectVectorType /* Vector<? extends Object> */= DMType.makeResolvedDMType(
				_project.getDataModel().getDMEntity(Vector.class), DMType.makeWildcardDMType(objectUpperBounds, null));
		DMType wildcardNumberVectorType /* Vector<? extends Number> */= DMType.makeResolvedDMType(
				_project.getDataModel().getDMEntity(Vector.class), DMType.makeWildcardDMType(NumberUpperBounds, null));
		DMType numberVectorType /* Vector<Number> */= DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Vector.class),
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Number.class)));
		DMType integerVectorType /* Vector<Integer> */= DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Vector.class),
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Integer.class)));

		DMType undefinedListType /* List */= DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(List.class));
		DMType objectListType /* List<Object> */= DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(List.class), objectType);
		DMType wildcardObjectListType /* List<? extends Object> */= DMType.makeResolvedDMType(
				_project.getDataModel().getDMEntity(List.class), DMType.makeWildcardDMType(objectUpperBounds, null));
		DMType wildcardNumberListType /* List<? extends Number> */= DMType.makeResolvedDMType(
				_project.getDataModel().getDMEntity(List.class), DMType.makeWildcardDMType(NumberUpperBounds, null));
		DMType numberListType /* List<Number> */= DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(List.class),
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Number.class)));
		DMType integerListType /* List<Integer> */= DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(List.class),
				DMType.makeResolvedDMType(_project.getDataModel().getDMEntity(Integer.class)));

		// Kept commented as you may check with current java compiler

		/*
		 * Vector undefinedVector = null; Vector<Object> objectVector = null; Vector<? extends Object> wildcardObjectVector = null; Vector<?
		 * extends Number> wildcardNumberVector = null; Vector<Number> NumberVector = null; Vector<Integer> IntegerVector = null;
		 * 
		 * List undefinedList = null; List<Object> objectList = null; List<? extends Object> wildcardObjectList = null; List<? extends
		 * Number> wildcardNumberList = null; List<Number> NumberList = null; List<Integer> IntegerList = null;
		 * 
		 * objectVector = undefinedVector; // Vector -> Vector<Object> SUCCESS undefinedVector = objectVector; // Vector<Object> -> Vector
		 * SUCCESS wildcardObjectVector = objectVector; // Vector<Object> -> Vector<? extends Object> SUCCESS objectVector =
		 * wildcardObjectVector; // Vector<? extends Object> -> Vector<Object> FAILS wildcardObjectVector = wildcardNumberVector; //
		 * Vector<? extends Number> -> Vector<? extends Object> SUCCESS
		 * 
		 * wildcardNumberVector = IntegerVector; // Vector<? extends Integer> -> Vector<? extends Number> SUCCESS IntegerVector =
		 * wildcardNumberVector; // Vector<? extends Number> -> Vector<? extends Integer> FAILS objectVector = IntegerVector; //
		 * Vector<Integer> -> Vector<Object> FAILS NumberVector = IntegerVector; // Vector<Integer> -> Vector<Number> FAILS undefinedVector
		 * = wildcardNumberVector; // Vector<? extends Number> -> Vector SUCCESS
		 * 
		 * undefinedList = undefinedVector; // Vector -> List SUCCESS objectList = objectVector; // Vector<Object> -> List<Object> SUCCESS
		 * IntegerList = IntegerVector; // Vector<Integer> -> List<Integer> SUCCESS objectVector = objectList; // List<Object> ->
		 * Vector<Object> FAILS wildcardObjectList = wildcardObjectVector; // Vector<? extends Object> -> List<? extends Object> SUCCESS
		 * wildcardObjectList = IntegerVector; // Vector<Integer> -> List<? extends Object> SUCCESS wildcardObjectList = NumberVector; //
		 * Vector<Number> -> List<? extends Object> SUCCESS objectList = NumberVector; // List<Object> -> List<? extends Number> FAILS
		 */

		if (!checkAssignable(undefinedVectorType, objectVectorType)) {
			returned = false; // Vector -> Vector<Object> SUCCESS
		}
		if (!checkAssignable(objectVectorType, undefinedVectorType)) {
			returned = false; // Vector<Object> -> Vector SUCCESS
		}
		if (!checkAssignable(objectVectorType, wildcardObjectVectorType)) {
			returned = false; // Vector<Object> -> Vector<? extends Object> SUCCESS
		}
		if (!checkNotAssignable(wildcardObjectVectorType, objectVectorType)) {
			returned = false; // Vector<? extends Object> -> Vector<Object> FAILS
		}
		if (!checkAssignable(wildcardNumberVectorType, wildcardObjectVectorType)) {
			returned = false; // Vector<? extends Number> -> Vector<? extends Object> SUCCESS
		}

		if (!checkAssignable(integerVectorType, wildcardNumberVectorType)) {
			returned = false; // Vector<? extends Integer> -> Vector<? extends Number> SUCCESS
		}
		if (!checkNotAssignable(wildcardNumberVectorType, integerVectorType)) {
			returned = false; // Vector<? extends Number> -> Vector<? extends Integer> FAILS
		}
		if (!checkNotAssignable(integerVectorType, objectVectorType)) {
			returned = false; // Vector<Integer> -> Vector<Object> FAILS
		}
		if (!checkNotAssignable(integerVectorType, numberVectorType)) {
			returned = false; // Vector<Integer> -> Vector<Number> FAILS
		}
		if (!checkAssignable(wildcardNumberVectorType, undefinedVectorType)) {
			returned = false; // Vector<? extends Number> -> Vector SUCCESS
		}

		if (!checkAssignable(undefinedVectorType, undefinedListType)) {
			returned = false; // Vector -> List SUCCESS
		}
		if (!checkAssignable(objectVectorType, objectListType)) {
			returned = false; // Vector<Object> -> List<Object> SUCCESS
		}
		if (!checkAssignable(integerVectorType, integerListType)) {
			returned = false; // Vector<Integer> -> List<Integer> SUCCESS
		}
		if (!checkNotAssignable(objectListType, objectVectorType)) {
			returned = false; // List<Object> -> Vector<Object> FAILS
		}
		if (!checkAssignable(wildcardObjectVectorType, wildcardObjectListType)) {
			returned = false;// Vector<? extends Object> -> List<? extends Object> SUCCESS
		}
		if (!checkAssignable(integerVectorType, wildcardObjectListType)) {
			returned = false; // Vector<Integer> -> List<? extends Object> SUCCESS
		}
		if (!checkAssignable(numberVectorType, wildcardObjectListType)) {
			returned = false; // Vector<Number> -> List<? extends Object> SUCCESS
		}
		if (!checkNotAssignable(numberVectorType, objectListType)) {
			returned = false; // List<Object> -> List<? extends Number> FAILS
		}

		if (!returned) {
			throw new AssertionFailedError("Some assignments failed");
		}

	}

	public void test9CheckTypeDeletion() {
		assertTrue(entity2.getParentBaseEntity() == entity1);
		assertTrue(entity3.getParentBaseEntity() == entity2);
		assertTrue(entity3.getParentType().getTypedWithThisType().contains(entity3));
		CreateDMMethod createMethod = CreateDMMethod.actionType.makeNewAction(entity3, null, _editor);
		assertTrue(createMethod.doAction().hasActionExecutionSucceeded());
		DMMethod method = createMethod.getNewMethod();
		method.setReturnType(DMType.makeResolvedDMType(entity2));
		DMMethodParameter param = null;
		try {
			param = method.createNewParameter();
			param.setType(DMType.makeResolvedDMType(entity2));
			assertTrue(param.getType().getTypedWithThisType().contains(param));
			assertTrue(entity2.isObservedBy(param.getType()));
		} catch (DuplicateMethodSignatureException e) {
			e.printStackTrace();
			fail();
		}
		DMMethodParameter param2 = null;
		try {
			param2 = method.createNewParameter();
			param2.setType(DMType.makeResolvedDMType(entity1));
			assertTrue(entity1.isObservedBy(param2.getType()));
		} catch (DuplicateMethodSignatureException e) {
			e.printStackTrace();
			fail();
		}
		Vector<DMObject> objects = new Vector<DMObject>();
		objects.add(entity2);
		DMDelete deleteEntity2 = DMDelete.actionType.makeNewAction(null, objects, _editor);
		assertTrue(deleteEntity2.doAction().hasActionExecutionSucceeded());
		assertEquals(entity1, entity3.getParentBaseEntity());
		assertEquals(entity1, entity3.getParentBaseEntity());
		assertEquals(entity1, method.getType().getBaseEntity());
		assertEquals(entity1, param.getType().getBaseEntity());
		nullifyReferences();
	}

	private void nullifyReferences() {
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
		_editor = null;
		_project = null;
		abstractCollectionEntity = null;
		abstractListEntity = null;
		comparableEntity = null;
		dictionaryEntity = null;
		entity1 = null;
		entity2 = null;
		entity3 = null;
		entity4 = null;
		enumerationEntity = null;
		foo1 = null;
		foo2 = null;
		foo3 = null;
		foo4 = null;
		foo5 = null;
		foo6 = null;
		foo7 = null;
		foo8 = null;
		foo9 = null;
		hashtableEntity = null;
		iteratorEntity = null;
		listEntity = null;
		method1 = null;
		method2 = null;
		nsArrayEntity = null;
		operationNode = null;
		p1 = null;
		p2 = null;
		p3 = null;
		p4 = null;
		property1 = null;
		property2 = null;
		property3 = null;
		property4 = null;
		remove = null;
		test4 = null;
		test4ClassEntity = null;
		vectorEntity = null;
	}

	private boolean checkAssignable(DMType source, DMType target) {
		try {
			assertTrue(target.isAssignableFrom(source, true));
			logger.info("Type: " + target + " is assignable from " + source);
			return true;
		} catch (AssertionFailedError e) {
			logger.warning("FAILURE : Type: " + target + " should be assignable from " + source);
			return false;
		}
	}

	private boolean checkNotAssignable(DMType source, DMType target) {
		try {
			assertFalse(target.isAssignableFrom(source, true));
			logger.info("Type: " + target + " is NOT assignable from " + source);
			return true;
		} catch (AssertionFailedError e) {
			logger.warning("FAILURE: type: " + target + " should NOT be assignable from " + source);
			return false;
		}
	}
}
