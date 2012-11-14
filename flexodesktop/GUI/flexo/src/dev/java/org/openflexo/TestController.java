/**
 * 
 */
package org.openflexo;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JMenuItem;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.selection.FlexoClipboard;
import org.openflexo.selection.PastingGraphicalContext;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.listener.SelectionManagingKeyEventListener;
import org.openflexo.view.menu.FlexoMenuBar;

public class TestController extends FlexoController implements SelectionManagingController {
	private static final Logger logger = Logger.getLogger(SelectionManager.class.getPackage().getName());

	private final TestController.TestSelectionManager selectionManager;
	private final TestFrame frame;
	protected TestMenuBar menuBar;
	protected TestKeyEventListener keyEventListener;

	public TestController(InteractiveFlexoEditor projectEditor, FlexoModule module) throws Exception {
		super(projectEditor, module);
		menuBar = (TestMenuBar) createAndRegisterNewMenuBar();
		frame = new TestFrame("test", keyEventListener = new TestKeyEventListener(), menuBar);
		selectionManager = new TestSelectionManager();
		init(frame, keyEventListener, menuBar);
		initWithEmptyPanel();
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new TestMainPane(getEmptyPanel(), getMainFrame(), this);
	}

	@Override
	public TestMainPane getMainPane() {
		return (TestMainPane) super.getMainPane();
	}

	public TestFrame getMainFrame() {
		return frame;
	}

	@Override
	protected TestMenuBar createNewMenuBar() {
		return new TestMenuBar();
	}

	@Override
	public String getWindowTitleforObject(FlexoModelObject object) {
		return object.toString();
	}

	@Override
	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

	@Override
	public void selectAndFocusObject(FlexoModelObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initInspectors() {
		super.initInspectors();
		selectionManager.addObserver(getSharedInspectorController());
	}

	public class TestSelectionManager extends SelectionManager {
		public TestSelectionManager() {
			super(TestController.this);
			_clipboard = new TestClipboard(this, menuBar.getEditMenu(TestController.this).copyItem,
					menuBar.getEditMenu(TestController.this).pasteItem, menuBar.getEditMenu(TestController.this).cutItem);
		}

		@Override
		public FlexoModelObject getPasteContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FlexoModelObject getRootFocusedObject() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean performSelectionSelectAll() {
			// TODO Auto-generated method stub
			return false;
		}

		public class TestClipboard extends FlexoClipboard {
			public TestClipboard(SelectionManager aSelectionManager, JMenuItem copyMenuItem, JMenuItem pasteMenuItem, JMenuItem cutMenuItem) {
				super(aSelectionManager, copyMenuItem, pasteMenuItem, cutMenuItem);
			}

			@Override
			protected boolean isCurrentSelectionValidForCopy(Vector<? extends FlexoModelObject> currentlySelectedObjects) {
				return false;
			}

			@Override
			protected boolean performCopyOfSelection(Vector<? extends FlexoModelObject> currentlySelectedObjects) {
				return false;
			}

			@Override
			protected void performSelectionPaste(FlexoModelObject pastingContext, PastingGraphicalContext graphicalContext) {
			}
		}

	}

	public class TestFrame extends FlexoFrame implements FlexoActionSource {

		// ==========================================================================
		// ============================= Constructor
		// ================================
		// ==========================================================================

		/**
		 * Constructor for WKFFrame
		 */
		public TestFrame(String title, TestKeyEventListener keyEventListener, TestMenuBar menuBar) throws HeadlessException {
			super(title, TestController.this, keyEventListener, menuBar);
			_keyEventListener = keyEventListener;
			_menuBar = menuBar;
			getContentPane().setLayout(new BorderLayout());
			setSize(100, 100);
			getContentPane().add(new JLabel("prout"), BorderLayout.CENTER);
		}

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			super.update(observable, dataModification);
		}

		/**
		 * @return Returns the controller.
		 */
		@Override
		public TestController getController() {
			return (TestController) super.getController();
		}

		@Override
		public void dispose() {
			_keyEventListener = null;
			_menuBar = null;
			super.dispose();
		}

	}

	public class TestKeyEventListener extends SelectionManagingKeyEventListener {

		public TestKeyEventListener() {
			super(TestController.this);
		}

		@Override
		public TestController getController() {
			return (TestController) super.getController();
		}

		@Override
		protected SelectionManager getSelectionManager() {
			return getController().getSelectionManager();
		}

		@Override
		public InteractiveFlexoEditor getEditor() {
			return getController().getEditor();
		}

	}

	public class TestMenuBar extends FlexoMenuBar {

		public TestMenuBar() {
			super(TestController.this, Module.TEST_MODULE);
		}

	}

	public class TestMainPane extends FlexoMainPane implements GraphicalFlexoObserver {
		public TestMainPane(ModuleView moduleView, TestFrame mainFrame, TestController controller) {
			super(moduleView, mainFrame, controller);
			showLeftView();
		}

		@Override
		protected FlexoModelObject getParentObject(FlexoModelObject object) {
			if (object instanceof FlexoProcess) {
				return ((FlexoProcess) object).getParentProcess();
			}
			return null;
		}
	}

}