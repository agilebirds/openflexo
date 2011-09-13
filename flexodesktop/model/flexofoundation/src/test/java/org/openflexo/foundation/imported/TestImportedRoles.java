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
package org.openflexo.foundation.imported;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Random;
import java.util.Vector;

import javax.activation.DataHandler;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.imported.DeltaStatus;
import org.openflexo.foundation.imported.FlexoImportedRoleLibraryDelta;
import org.openflexo.foundation.imported.FlexoImportedRoleLibraryDelta.RoleDelta;
import org.openflexo.foundation.imported.action.ConvertIntoLocalRole;
import org.openflexo.foundation.imported.action.ImportRolesAction;
import org.openflexo.foundation.imported.action.RefreshImportedRoleAction;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.ws.client.PPMWebService.CLProjectDescriptor;
import org.openflexo.ws.client.PPMWebService.PPMProcess;
import org.openflexo.ws.client.PPMWebService.PPMRole;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
import org.openflexo.ws.client.PPMWebService.PPMWebService_PortType;


public class TestImportedRoles extends FlexoTestCase {

	private final class PPMWebServiceMock implements PPMWebService_PortType {
		public PPMWebServiceMock() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public PPMProcess[] getProcesses(String login, String md5Password) throws RemoteException,
				PPMWebServiceAuthentificationException {
			return null;
		}

		@Override
		public PPMRole[] getRoles(String login, String md5Password) throws RemoteException, PPMWebServiceAuthentificationException {
			return null;
		}

		@Override
		public byte[] getScreenshoot(String login, String md5Password, String processVersionURI) throws RemoteException,
				PPMWebServiceAuthentificationException {
			return null;
		}

		@Override
		public PPMProcess[] refreshProcesses(String login, String md5Password, String[] uris) throws RemoteException,
				PPMWebServiceAuthentificationException {
			return null;
		}

		@Override
		public PPMRole[] refreshRoles(String login, String md5Password, String[] uris) throws RemoteException,
				PPMWebServiceAuthentificationException {
			return importedRoles.toArray(new PPMRole[importedRoles.size()]);
		}

		@Override
		public CLProjectDescriptor[] getAvailableProjects(String login,
				String md5Password) throws RemoteException,
				PPMWebServiceAuthentificationException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String uploadPrj(CLProjectDescriptor targetProject,
				DataHandler zip, String uploadComment, String login)
				throws RemoteException, PPMWebServiceAuthentificationException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public interface DeltaChecker {
		public void checkDelta(RoleDelta delta);
	}
	
	private final class RefreshRoleDeltaVisitor implements FlexoImportedRoleLibraryDelta.DeltaVisitor {
		
		private DeltaChecker checker;
		
		public RefreshRoleDeltaVisitor(DeltaChecker checker) {
			this.checker = checker;
		}
		
		private int visitedRoleCount = 0;
		
		public int getVisitedRoleCount() {
			return visitedRoleCount;
		}

		@Override
		public void visit(RoleDelta delta) {
			if (checker!=null)
				checker.checkDelta(delta);
			visitedRoleCount++;
		}
		
	}

	private static String generateRandomRoleURI(FlexoProject project) {
		return project.getURI()+"fmo/RoleGPO_"+new Random().nextLong();
	}
	
	private static Vector<PPMRole> createPPMRoles(FlexoProject project) {
		Vector<PPMRole> rolesToImport = new Vector<PPMRole>();
		PPMRole role = new PPMRole();
		role.setName("Administrator");
		role.setUri(generateRandomRoleURI(project));
		role.setVersionUri(role.getUri()+generateRandomRoleURI(project));
		role.setGeneralDescription("The Administrator");
		role.setUserManualDescription("Treat him with great care");
		rolesToImport.add(role);
		
		role = new PPMRole();
		role.setName("Manager");
		role.setRgbColor(184, 245, 143);
		role.setUri(generateRandomRoleURI(project));
		role.setVersionUri(role.getUri()+generateRandomRoleURI(project));
		rolesToImport.add(role);
		
		role = new PPMRole();
		role.setName("Employee");
		role.setRgbColor(0, 0, 0);
		role.setUri(generateRandomRoleURI(project));
		role.setVersionUri(role.getUri()+generateRandomRoleURI(project));
		rolesToImport.add(role);
		return rolesToImport;
	}
	
	private static Vector<PPMRole> createPPMRoles2(FlexoProject project, String name, int count) {
		Vector<PPMRole> rolesToImport = new Vector<PPMRole>();
		for(int i=0;i<count;i++) {
			PPMRole role = new PPMRole();
			role.setName(name+"-"+(i+1));
			role.setUri(generateRandomRoleURI(project));
			role.setVersionUri(role.getUri()+generateRandomRoleURI(project));
			rolesToImport.add(role);
		}
		return rolesToImport;
	}
	
	private static File projectDirectoryFile;
	private static FlexoEditor editor;
	static Vector<PPMRole> importedRoles;

	public TestImportedRoles() {
		super("Test imported roles");
	}

	public void test0CreateImportedRoleLibrary() {
		editor = createProject("Test import roles");
		FlexoProject project = editor.getProject();
		projectDirectoryFile = project.getProjectDirectory();
		assertNull(project.getWorkflow().getImportedRoleList());
		assertNotNull(project.getWorkflow().getImportedRoleList(true));
	}

	public void test1ImportRoles() throws SaveResourceException {
		FlexoProject project = editor.getProject();
		importedRoles = createPPMRoles(project);
		ImportRolesAction importRoles = ImportRolesAction.actionType.makeNewAction(project.getImportedRoleList(), null, editor);
		importRoles.setRolesToImport(importedRoles);
		importRoles.doAction();
		assertTrue(importRoles.hasActionExecutionSucceeded());
		assertNotNull(importRoles.getImportReport());
		assertEquals(3, importRoles.getImportReport().getProperlyImported().size());
		assertEquals(3, project.getImportedRoleList().getRoles().size());
		verifyImportedRoles(project, importedRoles);
		// No we verify that the action refuses to re-import the same roles
		importRoles = ImportRolesAction.actionType.makeNewAction(project.getImportedRoleList(), null, editor);
		importRoles.setRolesToImport(importedRoles);
		importRoles.doAction();
		assertTrue(importRoles.hasActionExecutionSucceeded());
		assertNotNull(importRoles.getImportReport());
		assertEquals(0, importRoles.getImportReport().getProperlyImported().size());
		assertEquals(3, importRoles.getImportReport().getAlreadyImportedRoles().size());
		project.save();
		project.close();
		editor = reloadProject(projectDirectoryFile);
		project = editor.getProject();
		verifyImportedRoles(project, importedRoles);
	}

	public void test2RefreshImportedRoles() throws SaveResourceException {
		FlexoProject project = editor.getProject();
		
		// First we try without changing anything
		RefreshImportedRoleAction refresh = RefreshImportedRoleAction.actionType.makeNewAction(project.getImportedRoleList(), null, editor);
		refresh.setWebService(new PPMWebServiceMock());
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		RefreshRoleDeltaVisitor visitor = new RefreshRoleDeltaVisitor(new DeltaChecker(){
			@Override
			public void checkDelta(RoleDelta delta) {
				assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(3,visitor.getVisitedRoleCount());
		
		// Then we change a single attribute and check that we have one update
		final PPMRole updatedRole = importedRoles.firstElement();
		updatedRole.setName(updatedRole.getName()+"coucou");
		refresh = RefreshImportedRoleAction.actionType.makeNewAction(project.getImportedRoleList(), null, editor);
		refresh.setWebService(new PPMWebServiceMock());
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		visitor = new RefreshRoleDeltaVisitor(new DeltaChecker(){
			@Override
			public void checkDelta(RoleDelta delta) {
				if (delta.getPPMRole()==updatedRole)
					assertEquals(DeltaStatus.UPDATED, delta.getStatus());
				else
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(3,visitor.getVisitedRoleCount());

		// Then we create a new role and delete another one 
		final PPMRole newRole = new PPMRole();
		newRole.setName("Administrator");
		newRole.setUri(generateRandomRoleURI(project));
		newRole.setVersionUri(newRole.getUri()+generateRandomRoleURI(project));
		newRole.setGeneralDescription("The Administrator");
		newRole.setUserManualDescription("Treat him with great care");
		importedRoles.add(newRole);
		final PPMRole deletedRole = importedRoles.remove(0);
		assertNotNull(project.getImportedRoleList().getImportedObjectWithURI(deletedRole.getUri()));
		refresh = RefreshImportedRoleAction.actionType.makeNewAction(project.getImportedRoleList(), null, editor);
		refresh.setWebService(new PPMWebServiceMock());
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		final FlexoProject project2 = project;
		visitor = new RefreshRoleDeltaVisitor(new DeltaChecker(){
			@Override
			public void checkDelta(RoleDelta delta) {
				if (delta.getPPMRole()==newRole) {
					assertEquals(DeltaStatus.NEW, delta.getStatus());
				} else if (delta.getFiRole()!=null && delta.getFiRole().getURI().equals(deletedRole.getUri()) && delta.getPPMRole()==null) {
					assertEquals(DeltaStatus.DELETED, delta.getStatus());
					assertTrue(delta.getFiRole().isDeletedOnServer());
					assertNotNull(project2.getImportedRoleList().getImportedObjectWithURI(delta.getFiRole().getURI()));
				} else
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(4,visitor.getVisitedRoleCount());
		verifyImportedRoles(project, importedRoles);
		project.save();
		project.close();
		editor = reloadProject(projectDirectoryFile);
		project = editor.getProject();
		verifyImportedRoles(project, importedRoles);
		// We check that the deleted role is still marked as deleted
		assertTrue(project.getImportedRoleList().getImportedObjectWithURI(deletedRole.getUri()).isDeletedOnServer());
		// Let's try to see if we 'undelete' a role on the server, the model updates the flag properly
		importedRoles.add(deletedRole);
		refresh = RefreshImportedRoleAction.actionType.makeNewAction(project.getImportedRoleList(), null, editor);
		refresh.setWebService(new PPMWebServiceMock());
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		visitor = new RefreshRoleDeltaVisitor(new DeltaChecker(){
			@Override
			public void checkDelta(RoleDelta delta) {
				if (delta.getPPMRole()==deletedRole) {
					assertEquals(DeltaStatus.UPDATED, delta.getStatus());
					assertFalse(delta.getFiRole().isDeletedOnServer());
				} else
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
			}
		});
		verifyImportedRoles(project, importedRoles);
		project.save();
		project.close();
	}
	
	public void test3ConvertRolesIntoLocalRoles() {
		editor = reloadProject(projectDirectoryFile);
		FlexoProject project = editor.getProject();
		verifyImportedRoles(project, importedRoles);
		
		Vector<PPMRole> importedRoles2 = createPPMRoles2(project, "Roles to convert", 5);
		ImportRolesAction importRoles = ImportRolesAction.actionType.makeNewAction(project.getFlexoWorkflow().getImportedRoleList(), null, editor);
		importRoles.setRolesToImport(importedRoles2);
		importRoles.doAction();
		assertTrue(importRoles.hasActionExecutionSucceeded());
		assertEquals(5, importRoles.getImportReport().getProperlyImported().size());
		
		final Collection<Role> roles = importRoles.getImportReport().getProperlyImported().values();
		RefreshImportedRoleAction refresh = RefreshImportedRoleAction.actionType.makeNewAction(project.getFlexoWorkflow().getImportedRoleList(), null, editor);
		refresh.setWebService(new PPMWebServiceMock());
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		RefreshRoleDeltaVisitor visitor = new RefreshRoleDeltaVisitor(new DeltaChecker(){
			@Override
			public void checkDelta(RoleDelta delta) {
				if (roles.contains(delta.getFiRole())) {
					assertEquals(DeltaStatus.DELETED, delta.getStatus());
					assertTrue(delta.getFiRole().isDeletedOnServer());
					assertFalse(delta.getFiRole().isDeleted());
					assertTrue(delta.getFiRole().isImported());
				} else {
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
				}
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(5+4,visitor.getVisitedRoleCount());
		
		for(PPMRole r: importedRoles2)
			convertRole(project, project.getWorkflow().getImportedRoleList().getImportedObjectWithURI(r.getUri()),r);
	}
	
	private void convertRole(FlexoProject project, Role roleToConvert, PPMRole sourcePPM) {
		assertNotNull(roleToConvert);
		assertTrue(roleToConvert.getURI().equals(sourcePPM.getUri()));
		assertTrue(roleToConvert.getVersionURI().equals(sourcePPM.getVersionUri()));
		//Converting first process
		ConvertIntoLocalRole convert = ConvertIntoLocalRole.actionType.makeNewAction(roleToConvert, null, editor);
		convert.doAction();
		assertTrue(convert.hasActionExecutionSucceeded());
		assertNull(project.getFlexoWorkflow().getImportedProcessWithURI(sourcePPM.getUri()));
		assertFalse(roleToConvert.getURI().equals(sourcePPM.getUri()));
		assertFalse(roleToConvert.getVersionURI().equals(sourcePPM.getVersionUri()));
	}
	
	/**
	 * @param project
	 * @param importedRoles
	 */
	private void verifyImportedRoles(FlexoProject project, Vector<PPMRole> importedRoles) {
		for(PPMRole r:importedRoles) {
			Role role = project.getImportedRoleList().getImportedObjectWithURI(r.getUri());
			assertNotNull(role);
			assertTrue(role.isEquivalentTo(r));
		}
		// Check equivalencies
		for(Role r:project.getImportedRoleList().getRoles()) {
			if (!r.isDeletedOnServer())// Deleted objects on server cannot be equal to their equivalent PPM objec
				assertTrue(r.isEquivalentTo(r.getEquivalentPPMRole()));
		}
	}
}
