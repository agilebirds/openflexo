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
package org.openflexo.diff.merge;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.MergeChange.MergeChangeSource;
import org.openflexo.diff.merge.MergeChange.MergeChangeType;
import org.openflexo.toolbox.FileResource;

/**
 * Test some pathologic cases
 * 
 * @author sylvain
 * 
 */
public class TestMerge2 extends TestCase {

	public void test0() throws IOException {
		File original = new FileResource("TestMerge/TestMerge0-original.java");
		File left = new FileResource("TestMerge/TestMerge0-left.java");
		File right = new FileResource("TestMerge/TestMerge0-right.java");
		Merge merge = new Merge(new DiffSource(original), new DiffSource(left), new DiffSource(right), DefaultMergedDocumentType.JAVA);
		assertEquals(merge.getChanges().size(), 0);
		assertFalse(merge.isReallyConflicting());
	}

	public void test1() throws IOException {
		File original = new FileResource("TestMerge/TestMerge1-original.java");
		File left = new FileResource("TestMerge/TestMerge1-left.java");
		File right = new FileResource("TestMerge/TestMerge1-right.java");
		Merge merge = new Merge(new DiffSource(original), new DiffSource(left), new DiffSource(right), DefaultMergedDocumentType.JAVA);
		assertEquals(merge.getChanges().size(), 9);
		assertFalse(merge.isReallyConflicting());
		assertChange(merge.getChanges().get(0), MergeChangeSource.Left, MergeChangeType.Removal, 23, 22, 23, 33, 23, 33);
		assertChange(merge.getChanges().get(1), MergeChangeSource.Left, MergeChangeType.Addition, 25, 25, 36, 35, 36, 35);
		assertChange(merge.getChanges().get(2), MergeChangeSource.Right, MergeChangeType.Addition, 91, 90, 101, 100, 101, 105);
		assertChange(merge.getChanges().get(3), MergeChangeSource.Right, MergeChangeType.Modification, 98, 98, 108, 108, 113, 114);
		assertChange(merge.getChanges().get(4), MergeChangeSource.Left, MergeChangeType.Addition, 100, 100, 110, 109, 116, 115);
		assertChange(merge.getChanges().get(5), MergeChangeSource.Right, MergeChangeType.Removal, 103, 103, 112, 112, 118, 117);
		assertChange(merge.getChanges().get(6), MergeChangeSource.Left, MergeChangeType.Modification, 106, 106, 115, 115, 120, 120);
		assertChange(merge.getChanges().get(7), MergeChangeSource.Left, MergeChangeType.Modification, 130, 130, 139, 139, 144, 144);
		assertChange(merge.getChanges().get(8), MergeChangeSource.Left, MergeChangeType.Modification, 134, 134, 143, 143, 148, 148);
	}

	public void test2() throws IOException {
		File original = new FileResource("TestMerge/TestMerge2-original.java");
		File left = new FileResource("TestMerge/TestMerge2-left.java");
		File right = new FileResource("TestMerge/TestMerge2-right.java");
		Merge merge = new Merge(new DiffSource(original), new DiffSource(left), new DiffSource(right), DefaultMergedDocumentType.JAVA);
		assertEquals(merge.getChanges().size(), 14);
		assertTrue(merge.isReallyConflicting());
		assertChange(merge.getChanges().get(0), MergeChangeSource.Left, MergeChangeType.Removal, 7, 6, 7, 7, 7, 7);
		assertChange(merge.getChanges().get(1), MergeChangeSource.Right, MergeChangeType.Addition, 10, 9, 11, 10, 11, 11);
		assertChange(merge.getChanges().get(2), MergeChangeSource.Conflict, MergeChangeType.Modification, 13, 13, 14, 13, 15, 15);
		assertChange(merge.getChanges().get(3), MergeChangeSource.Left, MergeChangeType.Addition, 19, 19, 19, 18, 21, 20);
		assertChange(merge.getChanges().get(4), MergeChangeSource.Right, MergeChangeType.Addition, 35, 34, 34, 33, 36, 39);
		assertChange(merge.getChanges().get(5), MergeChangeSource.Conflict, MergeChangeType.Modification, 79, 79, 78, 77, 84, 85);
		assertChange(merge.getChanges().get(6), MergeChangeSource.Left, MergeChangeType.Addition, 86, 86, 84, 83, 92, 91);
		assertChange(merge.getChanges().get(7), MergeChangeSource.Right, MergeChangeType.Addition, 102, 101, 99, 98, 107, 109);
		assertChange(merge.getChanges().get(8), MergeChangeSource.Left, MergeChangeType.Modification, 129, 129, 126, 126, 137, 137);
		assertChange(merge.getChanges().get(9), MergeChangeSource.Conflict, MergeChangeType.Modification, 135, 138, 132, 135, 143, 146);
		assertChange(merge.getChanges().get(10), MergeChangeSource.Right, MergeChangeType.Addition, 140, 139, 137, 136, 148, 148);
		assertChange(merge.getChanges().get(11), MergeChangeSource.Left, MergeChangeType.Addition, 141, 141, 138, 137, 150, 149);
		assertChange(merge.getChanges().get(12), MergeChangeSource.Conflict, MergeChangeType.Modification, 143, 148, 139, 144, 151, 156);
		assertChange(merge.getChanges().get(13), MergeChangeSource.Right, MergeChangeType.Addition, 156, 155, 152, 151, 164, 164);
	}

	public void test3() throws IOException {
		File original = new FileResource("TestMerge/TestMerge3-original.java");
		File left = new FileResource("TestMerge/TestMerge3-left.java");
		File right = new FileResource("TestMerge/TestMerge3-right.java");
		Merge merge = new Merge(new DiffSource(original), new DiffSource(left), new DiffSource(right), DefaultMergedDocumentType.JAVA);
		assertEquals(merge.getChanges().size(), 9);
		assertTrue(merge.isReallyConflicting());
		assertChange(merge.getChanges().get(0), MergeChangeSource.Conflict, MergeChangeType.Modification, 2, 4, 2, 4, 2, 4);
		assertChange(merge.getChanges().get(1), MergeChangeSource.Right, MergeChangeType.Modification, 7, 7, 7, 7, 7, 7);
		assertChange(merge.getChanges().get(2), MergeChangeSource.Left, MergeChangeType.Modification, 17, 17, 17, 17, 17, 17);
		assertChange(merge.getChanges().get(3), MergeChangeSource.Left, MergeChangeType.Addition, 23, 23, 23, 22, 23, 22);
		assertChange(merge.getChanges().get(4), MergeChangeSource.Conflict, MergeChangeType.Modification, 27, 32, 26, 31, 26, 31);
		assertChange(merge.getChanges().get(5), MergeChangeSource.Right, MergeChangeType.Addition, 35, 34, 34, 33, 34, 37);
		assertChange(merge.getChanges().get(6), MergeChangeSource.Left, MergeChangeType.Addition, 77, 80, 76, 75, 80, 79);
		assertChange(merge.getChanges().get(7), MergeChangeSource.Conflict, MergeChangeType.Modification, 154, 154, 149, 149, 153, 153);
		assertChange(merge.getChanges().get(8), MergeChangeSource.Conflict, MergeChangeType.Modification, 159, 159, 154, 154, 158, 158);
	}

	private void assertChange(MergeChange change, MergeChangeSource changeSource, MergeChangeType changeType, int first0, int last0,
			int first1, int last1, int first2, int last2) {
		assertEquals(change.getMergeChangeSource(), changeSource);
		assertEquals(change.getMergeChangeType(), changeType);
		assertEquals(first0, change.getFirst0());
		assertEquals(first1, change.getFirst1());
		assertEquals(first2, change.getFirst2());
		assertEquals(last0, change.getLast0());
		assertEquals(last1, change.getLast1());
		assertEquals(last2, change.getLast2());
	}

	/*	public static void main(String[] args) 
		{
			File file2 = new FileResource("TestMerge/TestMerge4-2");
			File file1 = new FileResource("TestMerge/TestMerge4-1");
			File file3 = new FileResource("TestMerge/TestMerge4-3");

			final JDialog dialog = new JDialog((Frame)null,true);

			JPanel panel = new JPanel(new BorderLayout());

			final JTabbedPane tabbedPane = new JTabbedPane();

			tabbedPane.add(makeMergeTabbedPane(file2, file1, file3,DefaultMergedDocumentType.JAVA),"2-1-3"); // OK
			tabbedPane.add(makeMergeTabbedPane(file2, file3, file1,DefaultMergedDocumentType.JAVA),"2-3-1"); // OK
			tabbedPane.add(makeMergeTabbedPane(file3, file2, file1,DefaultMergedDocumentType.JAVA),"3-2-1"); // OK
			tabbedPane.add(makeMergeTabbedPane(file3, file1, file2,DefaultMergedDocumentType.JAVA),"3-1-2"); // OK
			tabbedPane.add(makeMergeTabbedPane(file1, file2, file3,DefaultMergedDocumentType.JAVA),"1-2-3");
			tabbedPane.add(makeMergeTabbedPane(file1, file3, file2,DefaultMergedDocumentType.JAVA),"1-3-2");

			panel.add(tabbedPane,BorderLayout.CENTER);

			JButton closeButton = new JButton("Exit");
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
					System.exit(0);
				}
			});

			JButton editButton = new JButton("Edit");
			editButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JTabbedPane mergeTabbedPane = (JTabbedPane)tabbedPane.getSelectedComponent();
					DefaultMergePanel currentMergePanel = (DefaultMergePanel)mergeTabbedPane.getComponentAt(0);
					editMerge((Merge)currentMergePanel.getMerge());
				}
			});

			JPanel controlPanel = new JPanel(new FlowLayout());
			controlPanel.add(closeButton);
			controlPanel.add(editButton);
			
			panel.add(controlPanel,BorderLayout.SOUTH);

			dialog.setPreferredSize(new Dimension(1000,800));
			dialog.getContentPane().add(panel);
			dialog.pack();
			dialog.setVisible(true);
		}

		private static JTabbedPane makeMergeTabbedPane(File original, File left, File right, MergedDocumentType docType)
		{
			Merge merge = null;
			DiffReport diffLeft = null;
			DiffReport diffRight = null;
			try {
				merge = new Merge(new DiffSource(original),new DiffSource(left),new DiffSource(right),docType);
				diffLeft = ComputeDiff.diff(left,original);
				diffRight = ComputeDiff.diff(original,right);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.add(new DefaultMergePanel(merge,docType.getStyle()),"Merge");
			tabbedPane.add(new DiffPanel(diffLeft,docType.getStyle()),"Diffs between left and original");
			tabbedPane.add(new DiffPanel(diffRight,docType.getStyle()),"Diffs between original and right");

			return tabbedPane;
		}

		public static void editMerge(Merge merge)
		{
			final JDialog dialog = new JDialog((Frame)null,true);

			JPanel panel = new JPanel(new BorderLayout());
			MergeEditor editor = new MergeEditor(merge) {
				@Override
				public void done() {
					dialog.dispose();
				}
			};
			panel.add(editor,BorderLayout.CENTER);

			dialog.setPreferredSize(new Dimension(1000,800));
			dialog.getContentPane().add(panel);
			dialog.pack();
			dialog.setVisible(true);
		}*/

}
