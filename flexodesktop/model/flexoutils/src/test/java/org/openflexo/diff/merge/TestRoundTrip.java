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

public class TestRoundTrip {

	/*public static void main(String[] args) 
	{
		File generatedFile1 = new FileResource("TestRoundTrip/Generated1.txt");
		File acceptedFile1 = new FileResource("TestRoundTrip/Accepted1.txt");
		File generatedFile2 = new FileResource("TestRoundTrip/Generated2.txt");
		
		try {
			DiffReport report0 = ComputeDiff.diff(generatedFile1,acceptedFile1);
			
			final JDialog dialog = new JDialog((Frame)null,true);
			
			JButton closeButton = new JButton("Close");
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
					System.exit(0);
				}
			});
			
			JPanel panel = new JPanel(new BorderLayout());
			
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.add(new DiffPanel(report0,TokenMarkerStyle.Java,"GeneratedFile1","AcceptedFile1","No diffs",true),"Initial");

			Merge merge = new Merge(
					new DiffSource(generatedFile1),
					new DiffSource(generatedFile2),
					new DiffSource(acceptedFile1),
					DefaultMergedDocumentType.JAVA);
			
			MergeEditor editor = new MergeEditor(merge) {
				@Override
				public void done() {
					dialog.dispose();
				}
			};

			tabbedPane.add(editor,"Generation merge");			
			
			panel.add(tabbedPane,BorderLayout.CENTER);
			panel.add(closeButton,BorderLayout.SOUTH);
			
			dialog.setPreferredSize(new Dimension(1000,800));
			dialog.getContentPane().add(panel);
			dialog.validate();
			dialog.pack();
			dialog.setVisible(true);
			dialog.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
