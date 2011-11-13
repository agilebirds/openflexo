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
import java.util.Random;
import java.util.Vector;

import javax.activation.DataHandler;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.imported.FlexoImportedProcessLibraryDelta.ProcessDelta;
import org.openflexo.foundation.imported.action.ConvertIntoLocalProcess;
import org.openflexo.foundation.imported.action.ImportProcessesAction;
import org.openflexo.foundation.imported.action.RefreshImportedProcessAction;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.ws.client.PPMWebService.CLProjectDescriptor;
import org.openflexo.ws.client.PPMWebService.PPMProcess;
import org.openflexo.ws.client.PPMWebService.PPMRole;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
import org.openflexo.ws.client.PPMWebService.PPMWebService_PortType;

public class TestImportedProcesses extends FlexoTestCase {

	private final class PPMWebServiceMock implements PPMWebService_PortType {

		private Vector<PPMProcess> processes;

		public PPMWebServiceMock(Vector<PPMProcess> processes) {
			this.processes = processes;
		}

		@Override
		public PPMProcess[] getProcesses(String login, String md5Password) throws RemoteException, PPMWebServiceAuthentificationException {
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
			return processes.toArray(new PPMProcess[processes.size()]);
		}

		@Override
		public PPMRole[] refreshRoles(String login, String md5Password, String[] uris) throws RemoteException,
				PPMWebServiceAuthentificationException {
			return null;
		}

		@Override
		public CLProjectDescriptor[] getAvailableProjects(String login, String md5Password) throws RemoteException,
				PPMWebServiceAuthentificationException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String uploadPrj(CLProjectDescriptor targetProject, DataHandler zip, String uploadComment, String login)
				throws RemoteException, PPMWebServiceAuthentificationException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public interface DeltaChecker {
		public void checkDelta(ProcessDelta delta);
	}

	private final class RefreshProcessDeltaVisitor implements FlexoImportedProcessLibraryDelta.DeltaVisitor {

		private DeltaChecker checker;

		public RefreshProcessDeltaVisitor(DeltaChecker checker) {
			this.checker = checker;
		}

		private int visitedProcessCount = 0;

		public int getVisitedProcessCount() {
			return visitedProcessCount;
		}

		@Override
		public void visit(ProcessDelta delta) {
			if (checker != null) {
				checker.checkDelta(delta);
			}
			visitedProcessCount++;
		}

	}

	private static String generateRandomProcessURI(FlexoProject project) {
		return project.getURI() + "fmo/FlexoProcessGPO_" + new Random().nextLong();
	}

	private static PPMProcess createProcessWithName(String name, FlexoProject project) {
		PPMProcess p = new PPMProcess();
		p.setName(name);
		p.setUri(generateRandomProcessURI(project));
		p.setVersionUri(p.getUri() + generateRandomProcessURI(project));
		return p;
	}

	private static Vector<PPMProcess> createPPMProcesses(FlexoProject project) {
		Vector<PPMProcess> processesToImport = new Vector<PPMProcess>();
		PPMProcess p = createProcessWithName("Create book", project);
		p.setGeneralDescription("Create a book takes lot of time.");
		p.setBusinessDescription("Forget it dude, you will never get it");
		p.setTechnicalDescription("Using Word will get you mad, using Latex will take you forever to understand");
		p.setUserManualDescription("Use a good old-fashioned typewriter");
		PPMProcess[] sub = new PPMProcess[2];
		sub[0] = createProcessWithName("Review book", project);
		sub[0].setGeneralDescription("Read the book and make comments");
		sub[0].setBusinessDescription("Many stuffs to do");
		sub[0].setTechnicalDescription("There are a hundred ways to do that");
		sub[0].setUserManualDescription("Take the book off the shelf, open it at page one, then turn all the pages, write some relevant comments then close the book and put it back on the shelf.");
		sub[0].setParentProcess(p);
		sub[1] = createProcessWithName("Publish book", project);
		sub[1].setGeneralDescription("Creates soldable copies of the original text of the book");
		sub[1].setBusinessDescription("Publish that book");
		sub[1].setTechnicalDescription("Blabla coucou zut alors pouêt!");
		sub[1].setUserManualDescription("Put it in the copy machine and press 10000 and then start");
		sub[1].setParentProcess(p);
		p.setSubProcesses(sub);
		processesToImport.add(p);
		PPMProcess p1 = createProcessWithName("Create author", project);
		p1.setGeneralDescription("Create an author takes no time.");
		p1.setBusinessDescription("Creates an new author in DB");
		p1.setTechnicalDescription("Creates a new row in the table AUTHOR");
		p1.setUserManualDescription("Go to the section author and press the button 'New'. Then fill in all the fields and then press 'Save'");
		processesToImport.add(p1);
		return processesToImport;
	}

	private static PPMProcess createPPMProcesses2(FlexoProject project, String name, int count, int depth) {
		String suffix = null;
		if (depth == 0) {
			suffix = "Root";
		} else if (depth == 1) {
			suffix = "Son";
		} else {
			suffix = "GreatSon";
			for (int i = 2; i < depth; i++) {
				suffix = "Great" + suffix;
			}
		}
		PPMProcess p = createProcessWithName(name + suffix, project);
		p.setUri(generateRandomProcessURI(project));
		p.setVersionUri(p.getUri() + generateRandomProcessURI(project));
		if (depth + 1 < count) {
			PPMProcess[] sub = new PPMProcess[1];
			sub[0] = createPPMProcesses2(project, name, count, depth + 1);
			sub[0].setParentProcess(p);
			p.setSubProcesses(sub);
		} else {
			p.setSubProcesses(new PPMProcess[0]);
		}
		return p;
	}

	private static File projectDirectoryFile;
	private static FlexoEditor editor;
	private static Vector<PPMProcess> importedProcesses;

	public TestImportedProcesses() {
		super("Test imported processes");
	}

	public void test0CreateImportedProcessLibrary() {
		editor = createProject("Test import processes");
		FlexoProject project = editor.getProject();
		projectDirectoryFile = project.getProjectDirectory();
	}

	public void test1ImportProcesses() throws SaveResourceException {
		FlexoProject project = editor.getProject();
		importedProcesses = createPPMProcesses(project);
		ImportProcessesAction importProcesses = ImportProcessesAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		importProcesses.setProcessesToImport(importedProcesses);
		importProcesses.doAction();
		assertTrue(importProcesses.hasActionExecutionSucceeded());
		assertNotNull(importProcesses.getImportReport());
		assertEquals(2, importProcesses.getImportReport().getProperlyImported().size());
		assertEquals(2, project.getWorkflow().getImportedProcesses().size());
		verifyImportedProcesses(project, importedProcesses);
		// No we verify that the action refuses to re-import the same processes
		importProcesses = ImportProcessesAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		importProcesses.setProcessesToImport(importedProcesses);
		importProcesses.doAction();
		assertTrue(importProcesses.hasActionExecutionSucceeded());
		assertNotNull(importProcesses.getImportReport());
		assertEquals(0, importProcesses.getImportReport().getProperlyImported().size());
		assertEquals(2, importProcesses.getImportReport().getAlreadyImportedProcesses().size());
		project.save();
		project.close();
		editor = reloadProject(projectDirectoryFile);
		project = editor.getProject();
		verifyImportedProcesses(project, importedProcesses);
	}

	public void test2RefreshImportedProcesses() throws SaveResourceException {
		FlexoProject project = editor.getProject();

		// First we try without changing anything
		RefreshImportedProcessAction refresh = RefreshImportedProcessAction.actionType.makeNewAction(project.getFlexoWorkflow(), null,
				editor);
		refresh.setWebService(new PPMWebServiceMock(importedProcesses));
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		RefreshProcessDeltaVisitor visitor = new RefreshProcessDeltaVisitor(new DeltaChecker() {
			@Override
			public void checkDelta(ProcessDelta delta) {
				assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(4, visitor.getVisitedProcessCount());

		// Then we change a single attribute and check that we have one update
		final PPMProcess updatedProcess = importedProcesses.firstElement();
		updatedProcess.setName(updatedProcess.getName() + "coucou");
		refresh = RefreshImportedProcessAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		refresh.setWebService(new PPMWebServiceMock(importedProcesses));
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		visitor = new RefreshProcessDeltaVisitor(new DeltaChecker() {
			@Override
			public void checkDelta(ProcessDelta delta) {
				if (delta.getPPMProcess() == updatedProcess) {
					assertEquals(DeltaStatus.UPDATED, delta.getStatus());
				} else {
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
				}
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(4, visitor.getVisitedProcessCount());

		// Then we change the structure to see that we have one process deleted and another added
		final PPMProcess childProcess = updatedProcess.getSubProcesses()[0];
		childProcess.setParentProcess(null);
		PPMProcess[] subs = new PPMProcess[updatedProcess.getSubProcesses().length - 1];
		for (int i = 1; i < updatedProcess.getSubProcesses().length; i++) {
			subs[i - 1] = updatedProcess.getSubProcesses()[i];
		}
		updatedProcess.setSubProcesses(subs);
		importedProcesses.add(childProcess);
		refresh = RefreshImportedProcessAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		refresh.setWebService(new PPMWebServiceMock(importedProcesses));
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		visitor = new RefreshProcessDeltaVisitor(new DeltaChecker() {
			@Override
			public void checkDelta(ProcessDelta delta) {
				if (delta.getPPMProcess() == updatedProcess) {
					assertEquals(DeltaStatus.UPDATED, delta.getStatus());
				} else if (delta.getPPMProcess() == childProcess) {
					assertEquals(DeltaStatus.NEW, delta.getStatus());
				} else if (delta.getFiProcess() != null && delta.getFiProcess().getURI().equals(childProcess.getUri())
						&& delta.getPPMProcess() == null) {
					assertEquals(DeltaStatus.DELETED, delta.getStatus());
					// assertTrue(delta.getFiProcess().isDeleted());
					assertNull(delta.getFiProcess().getParentProcess());
					// Tests delta parenting
					assertNotNull(delta.getParent());// Parent delta should not be null for child processes
				} else {
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
				}
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(5, visitor.getVisitedProcessCount());// Here we have 5 because of the four already imported + the new one!
		verifyImportedProcesses(project, importedProcesses);
		project.save();
		project.close();
		editor = reloadProject(projectDirectoryFile);
		project = editor.getProject();
		final FlexoProject project2 = project;
		verifyImportedProcesses(project, importedProcesses);
		// Let's delete a top level process
		importedProcesses.remove(childProcess);
		refresh = RefreshImportedProcessAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		refresh.setWebService(new PPMWebServiceMock(importedProcesses));
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		visitor = new RefreshProcessDeltaVisitor(new DeltaChecker() {
			@Override
			public void checkDelta(ProcessDelta delta) {
				if (delta.getFiProcess() != null && delta.getFiProcess().getURI().equals(childProcess.getUri())
						&& delta.getPPMProcess() == null) {
					assertEquals(DeltaStatus.DELETED, delta.getStatus());
					assertTrue(delta.getFiProcess().isDeletedOnServer());
					assertNotNull(project2.getFlexoWorkflow().getImportedProcessWithURI(childProcess.getUri()));
				} else {
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
				}
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(4, visitor.getVisitedProcessCount());
		verifyImportedProcesses(project, importedProcesses);

		// Let's try to see if we 'undelete' a process on the server, the model updates the flag properly
		importedProcesses.add(childProcess);
		refresh = RefreshImportedProcessAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		refresh.setWebService(new PPMWebServiceMock(importedProcesses));
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		visitor = new RefreshProcessDeltaVisitor(new DeltaChecker() {
			@Override
			public void checkDelta(ProcessDelta delta) {
				if (delta.getPPMProcess() == childProcess) {
					assertEquals(DeltaStatus.UPDATED, delta.getStatus());
					assertFalse(delta.getFiProcess().isDeletedOnServer());
					// Tests delta parenting
					assertNull(delta.getParent());
				} else {
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
				}
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(4, visitor.getVisitedProcessCount());
		verifyImportedProcesses(project, importedProcesses);

		project.save();
		project.close();
	}

	public void test3ConvertProcessesIntoLocalProcesses() {
		editor = reloadProject(projectDirectoryFile);
		FlexoProject project = editor.getProject();
		verifyImportedProcesses(project, importedProcesses);

		Vector<PPMProcess> importedProcess2 = new Vector<PPMProcess>();
		final PPMProcess firstProcess = createPPMProcesses2(project, "FirstProcess", 10, 0);
		final PPMProcess secondProcess = createPPMProcesses2(project, "SecondProcess", 5, 0);
		importedProcess2.add(firstProcess);
		importedProcess2.add(secondProcess);
		ImportProcessesAction importProcesses = ImportProcessesAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		importProcesses.setProcessesToImport(importedProcess2);
		importProcesses.doAction();
		assertTrue(importProcesses.hasActionExecutionSucceeded());
		assertEquals(2, importProcesses.getImportReport().getProperlyImported().size());

		RefreshImportedProcessAction refresh = RefreshImportedProcessAction.actionType.makeNewAction(project.getFlexoWorkflow(), null,
				editor);
		refresh.setWebService(new PPMWebServiceMock(importedProcesses));
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		RefreshProcessDeltaVisitor visitor = new RefreshProcessDeltaVisitor(new DeltaChecker() {
			@Override
			public void checkDelta(ProcessDelta delta) {
				FlexoProcess rootParent = getTopLevelParent(delta.getFiProcess());
				if (rootParent.getURI().equals(firstProcess.getUri()) || rootParent.getURI().equals(secondProcess.getUri())) {
					assertEquals(DeltaStatus.DELETED, delta.getStatus());
					assertTrue(delta.getFiProcess().isDeletedOnServer());
					assertFalse(delta.getFiProcess().isDeleted());
					assertTrue(delta.getFiProcess().isImported());
				} else {
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
				}
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		assertEquals(15 + 4, visitor.getVisitedProcessCount());

		FlexoProcess firstImportedProcess = project.getWorkflow().getImportedProcessWithURI(firstProcess.getUri());
		FlexoProcess secondImportedProcess = project.getWorkflow().getImportedProcessWithURI(secondProcess.getUri());
		ConvertIntoLocalProcess convert = ConvertIntoLocalProcess.actionType.makeNewAction(firstImportedProcess.getSubProcesses()
				.firstElement(), null, editor);
		convert.doAction();
		assertFalse(convert.hasActionExecutionSucceeded());// Here we check that sub-processes cannot be converted

		convertProcess(project, firstImportedProcess, firstProcess);
		convertProcess(project, secondImportedProcess, secondProcess);
		project.close();
		FileUtils.deleteDir(project.getProjectDirectory());
	}

	public void test4TestProcessHierarchyChanges() {
		FlexoEditor editor = createProject("Test import process hierarchy changes");
		FlexoProject project = editor.getProject();
		/**
		 * 3 roots: A, B and C -A |--> E -B | -C |--> D
		 */
		final PPMProcess a = createProcessWithName("A", project);
		final PPMProcess b = createProcessWithName("B", project);
		final PPMProcess c = createProcessWithName("C", project);
		final PPMProcess d = createProcessWithName("D", project);
		final PPMProcess e = createProcessWithName("E", project);

		e.setParentProcess(a);
		a.setSubProcesses(new PPMProcess[] { e });

		d.setParentProcess(c);
		c.setSubProcesses(new PPMProcess[] { d });

		Vector<PPMProcess> processToImport = new Vector<PPMProcess>();
		processToImport.add(a);
		processToImport.add(b);

		ImportProcessesAction importProcesses = ImportProcessesAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		importProcesses.setProcessesToImport(processToImport);
		importProcesses.doAction();
		assertTrue(importProcesses.hasActionExecutionSucceeded());

		/**
		 * 2 roots: A and C (B is moved under E!) -A |--> E | |-->B | -C |--> D
		 */
		b.setParentProcess(e);
		e.setSubProcesses(new PPMProcess[] { b });

		processToImport.clear();
		processToImport.add(c);
		importProcesses = ImportProcessesAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		importProcesses.setProcessesToImport(processToImport);
		importProcesses.doAction();
		assertTrue(importProcesses.hasActionExecutionSucceeded());
		FlexoProcess processA = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(a.getUri());
		FlexoProcess processB = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(b.getUri());
		FlexoProcess processC = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(c.getUri());
		FlexoProcess processD = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(d.getUri());
		FlexoProcess processE = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(e.getUri());
		checkParenthood(processA, processE);
		checkParenthood(processC, processD);
		processToImport.clear();
		processToImport.add(a);
		processToImport.add(c);
		RefreshImportedProcessAction refresh = RefreshImportedProcessAction.actionType.makeNewAction(project.getFlexoWorkflow(), null,
				editor);
		refresh.setWebService(new PPMWebServiceMock(processToImport));
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		RefreshProcessDeltaVisitor visitor = new RefreshProcessDeltaVisitor(new DeltaChecker() {
			@Override
			public void checkDelta(ProcessDelta delta) {
				if (delta.getPPMProcess() == c || delta.getPPMProcess() == d) {
					assertEquals(DeltaStatus.UNCHANGED, delta.getStatus());
				} else if (delta.getPPMProcess() == b) {
					assertEquals(DeltaStatus.NEW, delta.getStatus());
				} else if (delta.getPPMProcess() == a || delta.getPPMProcess() == e) {
					assertEquals(DeltaStatus.UPDATED, delta.getStatus());
				} else if (delta.getFiProcess().getURI().equals(b.getUri())) {
					assertEquals(DeltaStatus.DELETED, delta.getStatus());
					assertFalse(delta.getFiProcess().isDeleted());// B has moved!
					assertFalse(delta.getFiProcess().getFlexoResource().isDeleted());
				}
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		FlexoProcess processB2 = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(b.getUri());
		assertEquals(processB, processB2); // We check that we have still the same FlexoProcess
		checkParenthood(processA, processE);
		checkParenthood(processE, processB2);
		checkParenthood(processC, processD);
		assertNull(project.getFlexoWorkflow().getImportedProcessWithURI(b.getUri()));
		verifyImportedProcesses(project, processToImport);
		project.close();
		FileUtils.deleteDir(project.getProjectDirectory());
	}

	public void test5TestProcessHierarchyChanges() throws SaveResourceException {
		FlexoEditor editor = createProject("Test import process hierarchy changes");
		FlexoProject project = editor.getProject();
		/**
		 * 2 roots: A, B -A -B
		 */
		final PPMProcess a = createProcessWithName("A", project);
		final PPMProcess b = createProcessWithName("B", project);
		final PPMProcess c = createProcessWithName("C", project);
		Vector<PPMProcess> processToImport = new Vector<PPMProcess>();
		processToImport.add(a);
		processToImport.add(b);

		ImportProcessesAction importProcesses = ImportProcessesAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		importProcesses.setProcessesToImport(processToImport);
		importProcesses.doAction();
		assertTrue(importProcesses.hasActionExecutionSucceeded());

		/**
		 * 1 root: A -A |--> C | |-->B
		 */
		b.setParentProcess(c);
		c.setSubProcesses(new PPMProcess[] { b });
		c.setParentProcess(a);
		a.setSubProcesses(new PPMProcess[] { c });

		processToImport.clear();
		processToImport.add(a);
		FlexoProcess processA = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(a.getUri());
		FlexoProcess processB = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(b.getUri());
		RefreshImportedProcessAction refresh = RefreshImportedProcessAction.actionType.makeNewAction(project.getFlexoWorkflow(), null,
				editor);
		refresh.setWebService(new PPMWebServiceMock(processToImport));
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		RefreshProcessDeltaVisitor visitor = new RefreshProcessDeltaVisitor(new DeltaChecker() {
			@Override
			public void checkDelta(ProcessDelta delta) {
				if (delta.getPPMProcess() == b || delta.getPPMProcess() == c) {
					assertEquals(DeltaStatus.NEW, delta.getStatus());
				} else if (delta.getPPMProcess() == a) {
					assertEquals(DeltaStatus.UPDATED, delta.getStatus());
				} else if (delta.getFiProcess().getURI().equals(b.getUri())) {
					assertEquals(DeltaStatus.DELETED, delta.getStatus());
					assertFalse(delta.getFiProcess().isDeleted());// B has moved!
					assertFalse(delta.getFiProcess().getFlexoResource().isDeleted());
				}
			}
		});
		refresh.getLibraryDelta().visit(visitor);
		FlexoProcess processC = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(c.getUri());
		FlexoProcess processB2 = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(b.getUri());
		assertEquals(processB, processB2); // We check that we have still the same FlexoProcess
		checkParenthood(processC, processB2);
		checkParenthood(processA, processC);
		assertNull(project.getFlexoWorkflow().getImportedProcessWithURI(b.getUri()));
		verifyImportedProcesses(project, processToImport);
		project.save();
		project.close();
		project = reloadProject(project.getProjectDirectory()).getProject();
		verifyImportedProcesses(project, processToImport);
		project.close();
		FileUtils.deleteDir(project.getProjectDirectory());
	}

	public void test6TestProcessHierarchyChanges() throws SaveResourceException {
		FlexoEditor editor = createProject("Test import process hierarchy changes");
		FlexoProject project = editor.getProject();
		/**
		 * 2 roots: A, E -A |--> B | |-->C | |-->D -E
		 */
		final PPMProcess a = createProcessWithName("A", project);
		final PPMProcess b = createProcessWithName("B", project);
		final PPMProcess c = createProcessWithName("C", project);
		final PPMProcess d = createProcessWithName("D", project);
		final PPMProcess e = createProcessWithName("E", project);
		Vector<PPMProcess> processToImport = new Vector<PPMProcess>();
		b.setParentProcess(a);
		a.setSubProcesses(new PPMProcess[] { b });
		c.setParentProcess(b);
		b.setSubProcesses(new PPMProcess[] { c });
		d.setParentProcess(c);
		c.setSubProcesses(new PPMProcess[] { d });

		processToImport.add(a);
		processToImport.add(e);

		ImportProcessesAction importProcesses = ImportProcessesAction.actionType.makeNewAction(project.getFlexoWorkflow(), null, editor);
		importProcesses.setProcessesToImport(processToImport);
		importProcesses.doAction();
		assertTrue(importProcesses.hasActionExecutionSucceeded());
		verifyImportedProcesses(project, processToImport);
		FlexoProcess processA = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(a.getUri());
		FlexoProcess processB = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(b.getUri());
		FlexoProcess processC = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(c.getUri());
		FlexoProcess processD = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(d.getUri());
		FlexoProcess processE = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(e.getUri());

		checkParenthood(processA, processB);
		checkParenthood(processB, processC);
		checkParenthood(processC, processD);

		/**
		 * 2 roots: A, E -A |--> B -E |-->C
		 */
		b.setSubProcesses(null);
		c.setSubProcesses(null);

		c.setParentProcess(e);
		e.setSubProcesses(new PPMProcess[] { c });

		RefreshImportedProcessAction refresh = RefreshImportedProcessAction.actionType.makeNewAction(project.getFlexoWorkflow(), null,
				editor);
		refresh.setWebService(new PPMWebServiceMock(processToImport));
		refresh.doAction();
		assertTrue(refresh.hasActionExecutionSucceeded());
		FlexoProcess processC2 = project.getFlexoWorkflow().getRecursivelyImportedProcessWithURI(c.getUri());
		assertEquals(processC, processC2); // We check that we have still the same FlexoProcess
		checkParenthood(processA, processB);
		checkParenthood(processE, processC2);
		checkNonParenthood(processB, processC);
		assertFalse(processC.isDeleted());
		assertFalse(processC.getFlexoResource().isDeleted());
		assertEquals(0, processC.getSubProcesses().size());
		assertEquals(0, processC.getProcessNode().getSubProcesses().size());

		assertTrue(processD.isDeleted());
		assertTrue(processD.getFlexoResource().isDeleted());
		verifyImportedProcesses(project, processToImport);
		project.save();
		project.close();
		project = reloadProject(project.getProjectDirectory()).getProject();
		verifyImportedProcesses(project, processToImport);
		TestImportedProcesses.editor = null;
		projectDirectoryFile = null;
		importedProcesses = null;
	}

	/**
	 * @param parent
	 * @param child
	 */
	private void checkParenthood(FlexoProcess parent, FlexoProcess child) {
		assertEquals(parent, child.getParentProcess());
		assertEquals(parent.getProcessNode(), child.getProcessNode().getFatherProcessNode());
		assertTrue(parent.getSubProcesses().contains(child));
		assertTrue(parent.getProcessNode().getSubProcesses().contains(child.getProcessNode()));
	}

	/**
	 * @param parent
	 * @param child
	 */
	private void checkNonParenthood(FlexoProcess parent, FlexoProcess child) {
		assertFalse(parent == child.getParentProcess());
		assertFalse(parent.getProcessNode() == child.getProcessNode().getFatherProcessNode());
		assertFalse(parent.getSubProcesses().contains(child));
		assertFalse(parent.getProcessNode().getSubProcesses().contains(child.getProcessNode()));
	}

	private void convertProcess(FlexoProject project, FlexoProcess processToConvert, PPMProcess sourcePPMProcess) {
		assertNotNull(project.getFlexoWorkflow().getImportedProcessWithURI(sourcePPMProcess.getUri()));
		assertTrue(processToConvert.getURI().equals(sourcePPMProcess.getUri()));
		assertTrue(processToConvert.getVersionURI().equals(sourcePPMProcess.getVersionUri()));
		// Converting first process
		ConvertIntoLocalProcess convert = ConvertIntoLocalProcess.actionType.makeNewAction(processToConvert, null, editor);
		convert.doAction();
		assertTrue(convert.hasActionExecutionSucceeded());
		assertNull(project.getFlexoWorkflow().getImportedProcessWithURI(sourcePPMProcess.getUri()));
		assertFalse(processToConvert.getURI().equals(sourcePPMProcess.getUri()));
		assertFalse(processToConvert.getVersionURI().equals(sourcePPMProcess.getVersionUri()));
		assertEquals(processToConvert.getSubProcesses().size(), sourcePPMProcess.getSubProcesses().length);
		checkConvertedProcesses(processToConvert);
	}

	private void checkConvertedProcesses(FlexoProcess processToConvert) {
		assertNotNull(processToConvert.getActivityPetriGraph());
		assertNotNull(processToConvert.getProcessDMEntity());
		assertFalse(processToConvert.isImported());
		for (FlexoProcess p : processToConvert.getSubProcesses()) {
			checkConvertedProcesses(p);
		}
	}

	/**
	 * @param project
	 * @param processesToImport
	 */
	private void verifyImportedProcesses(FlexoProject project, Vector<PPMProcess> processesToImport) {
		assertEquals(processesToImport.size(), project.getFlexoWorkflow().getNotDeletedImportedProcesses().size());
		for (PPMProcess p : processesToImport) {
			FlexoProcess process = project.getFlexoWorkflow().getImportedProcessWithURI(p.getUri());
			assertNull(process.getActivityPetriGraph());
			assertNull(process.getPortRegistery());
			assertNull(process.getStatusList());
			assertNull(process.getProcessDMEntity());
			assertNotNull(process);
			assertTrue(process.isImported());
			assertTrue(process.isEquivalentTo(p));
			assertEquals(process.getProcessNode().getSubProcesses().size(), process.getSubProcesses().size());
			if (p.getSubProcesses() != null) {
				assertEquals(p.getSubProcesses().length, process.getSubProcesses().size());
			} else {
				assertEquals(0, process.getSubProcesses().size());
			}
		}
		// Check equivalencies
		for (FlexoProcess p : project.getFlexoWorkflow().getImportedProcesses()) {
			if (!p.isDeletedOnServer()) { // Deleted objects on server cannot be equal to their equivalent PPM object
				// Including children
				assertTrue(p.isEquivalentTo(p.getEquivalentPPMProcess(true), true));
				// Excluding children
				assertTrue(p.isEquivalentTo(p.getEquivalentPPMProcess(false), false));
			}
		}
	}

	protected FlexoProcess getTopLevelParent(FlexoProcess p) {
		if (p.getParentProcess() == null) {
			return p;
		} else {
			return getTopLevelParent(p.getParentProcess());
		}
	}
}
