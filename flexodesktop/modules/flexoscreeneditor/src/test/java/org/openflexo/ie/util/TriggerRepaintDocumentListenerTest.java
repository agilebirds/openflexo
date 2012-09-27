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
package org.openflexo.ie.util;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import junit.framework.TestCase;

public class TriggerRepaintDocumentListenerTest extends TestCase {

	private TestTextField textFieldTargetOnly;
	private TestTextField textFieldSourceAndTarget;
	private JTextField sourceTextField;

	@Override
	protected void setUp() throws Exception {
		textFieldSourceAndTarget = new TestTextField();
		textFieldTargetOnly = new TestTextField();
		sourceTextField = new JTextField();

		textFieldSourceAndTarget.getDocument().addDocumentListener(new TriggerRepaintDocumentListener(textFieldSourceAndTarget));
		sourceTextField.getDocument().addDocumentListener(new TriggerRepaintDocumentListener(textFieldTargetOnly));

		textFieldSourceAndTarget.reset();
		super.setUp();
	}

	public void testSetText() {
		textFieldSourceAndTarget.setText("bla");
		assertRevalidateRepaintCalled(textFieldSourceAndTarget);

		sourceTextField.setText("bla");
		assertRevalidateRepaintCalled(textFieldTargetOnly);
	}

	public void testDocumentInsert() {
		try {
			textFieldSourceAndTarget.getDocument().insertString(0, "a", null);
			sourceTextField.getDocument().insertString(0, "a", null);
		} catch (BadLocationException e) {
			fail("Insert text shouldn't fail. Cause:" + e.getMessage());
		}
		assertRevalidateRepaintCalled(textFieldSourceAndTarget);
		assertRevalidateRepaintCalled(textFieldTargetOnly);
	}

	public void testDocumentRemove() {
		textFieldSourceAndTarget.setText("bla");
		sourceTextField.setText("bla");
		textFieldSourceAndTarget.reset();
		textFieldTargetOnly.reset();
		try {
			textFieldSourceAndTarget.getDocument().remove(0, 1);
			sourceTextField.getDocument().remove(0, 1);
		} catch (BadLocationException e) {
			fail("remove text shouldn't fail. Cause:" + e.getMessage());
		}
		assertRevalidateRepaintCalled(textFieldSourceAndTarget);
		assertRevalidateRepaintCalled(textFieldTargetOnly);
	}

	public void testTargetComponentCannotBeNull() {
		try {
			new TriggerRepaintDocumentListener(null);
			fail("Creation of document listener without targetComponent must fail.");
		} catch (IllegalArgumentException e) {
			// that's expected
		}
	}

	private void assertRevalidateRepaintCalled(TestTextField target) {
		assertTrue("Expected revalidate called, but it wasn't ", target.revalidateCalled);
		assertTrue("Expected repaint called, but it wasn't ", target.repaintCalled);

	}

	private class TestTextField extends JTextField {
		boolean revalidateCalled = false;
		boolean repaintCalled = false;

		@Override
		public void revalidate() {
			super.revalidate();
			revalidateCalled = true;
		}

		@Override
		public void repaint() {
			super.repaint();
			repaintCalled = true;
		}

		void reset() {
			repaintCalled = false;
			revalidateCalled = false;
		}
	}
}
