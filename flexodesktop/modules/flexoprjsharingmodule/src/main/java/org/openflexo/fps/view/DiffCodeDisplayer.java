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
package org.openflexo.fps.view;

import java.awt.BorderLayout;
import java.awt.event.FocusListener;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSStatus;
import org.openflexo.fps.controller.FPSController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.diff.DiffPanel;
import org.openflexo.toolbox.TokenMarkerStyle;


public class DiffCodeDisplayer extends CodeDisplayer {

	private static final Logger logger = Logger.getLogger(DiffCodeDisplayer.class.getPackage().getName());

	//protected DiffCodeDisplayerComponent _component;
	public DiffCodeDisplayer(CVSFile cvsFile, FPSController controller)
	{
		super(cvsFile,controller);
	}
	
	@Override
	public JComponent getComponent()
	{
		return (JComponent)_component;
	}

	protected interface DiffCodeDisplayerComponent extends CodeDisplayerComponent
	{
		@Override
		public void update();
	}

	@Override
	protected CodeDisplayerComponent buildComponent()
	{
		if (!getFileFormat().isBinary()) {
			return _component = new ASCIIFileDiffCodeDisplayer();
		}
		else {
			return super.buildComponent();
		}
	}
	
	@Override
	public void update()
	{
		if (_component != null) {
			_component.update();
		}
	}
	
	public String getContentOnRepository()
	{
		String returned = getCVSFile().getContentOnRepository();
		if (returned == null) {
			return FlexoLocalization.localizedForKey("unable_to_retrieve_remote_content_for")
			+" "+getCVSFile().getFile().getAbsolutePath()+" - "+getCVSFile().getRepositoryRevision();
		} else {
			return returned;
		}
	}

	protected class ASCIIFileDiffCodeDisplayer extends JPanel implements DiffCodeDisplayerComponent
	{
		protected DiffPanel _diffPanel;
		protected DiffReport _diffReport;
		
		protected ASCIIFileDiffCodeDisplayer()
		{
			super(new BorderLayout());
			update();
		}
		
		@Override
		public void setEditable(boolean isEditable)
		{
			// Not editable anyway
		}
		
		@Override
		public void setEditedContent(CVSFile file) 
		{
			// Not editable anyway
		}
		
		@Override
		public String getEditedContent() 
		{
			// Interface: this component is not supposed to be editable
			return null;
		}
		
		@Override
		public void update()
		{
			removeAll();
			String leftLabel = FlexoLocalization.localizedForKey("file_on_disk");
			if (getCVSFile().getStatus() == CVSStatus.LocallyModified) {
				leftLabel += " "+FlexoLocalization.localizedForKey("based_on_revision")+" "+getCVSFile().getRevision();
			}
			else if (getCVSFile().getStatus() == CVSStatus.MarkedAsMerged) {
				leftLabel += " ("+FlexoLocalization.localizedForKey("merge_of")+" "
				+getCVSFile().getRevisionOnWhichContentOnDiskBeforeMergeWasBasedOn()
				+" "+FlexoLocalization.localizedForKey("and")+" "+getCVSFile().getRevision()+")";
			}
			else {
				leftLabel += (getCVSFile().getRevision()!=null?" - "+getCVSFile().getRevision():"");
			}
			String rightLabel = FlexoLocalization.localizedForKey("remote_file_on_cvs_repository")
			+(getCVSFile().getRepositoryRevision()!=null?" - "+getCVSFile().getRepositoryRevision():"");
			boolean isLeftOriented = true;
			if (getCVSFile().getStatus() == CVSStatus.LocallyModified) {
				_diffReport = ComputeDiff.diff(getContentOnDisk(),getContentOnRepository());
			}
			else if (getCVSFile().getStatus() == CVSStatus.MarkedAsMerged) {
				_diffReport = ComputeDiff.diff(getContentOnDisk(),getContentOnRepository());
			}
			else if (getCVSFile().getStatus() == CVSStatus.RemotelyModified) {
				_diffReport = ComputeDiff.diff(getContentOnDisk(),getContentOnRepository());
			}
			else if (getCVSFile().getStatus() == CVSStatus.LocallyAdded) {
				_diffReport = ComputeDiff.diff(getContentOnDisk(),FlexoLocalization.localizedForKey("locally_added_file"));
			}
			else if (getCVSFile().getStatus() == CVSStatus.LocallyRemoved) {
				_diffReport = ComputeDiff.diff(FlexoLocalization.localizedForKey("locally_removed_file"),getContentOnRepository());
			}
			else if (getCVSFile().getStatus() == CVSStatus.RemotelyRemoved) {
				_diffReport = ComputeDiff.diff(getContentOnDisk(),FlexoLocalization.localizedForKey("remotely_removed_file"));
			}
			else if (getCVSFile().getStatus() == CVSStatus.RemotelyAdded) {
				_diffReport = ComputeDiff.diff(FlexoLocalization.localizedForKey("remotely_added_file"),getContentOnRepository());
			}
			else {
				logger.warning("I should never access here: status="+getCVSFile().getStatus());
				return;
			}
			_diffPanel = new DiffPanel(
					_diffReport,
					getTokenMarkerStyle(),
					leftLabel,
					rightLabel,
					FlexoLocalization.localizedForKey("no_structural_changes"),
					isLeftOriented);
            _diffPanel.validate();
			add(_diffPanel,BorderLayout.CENTER);
			validate();
		}
		
		protected TokenMarkerStyle getTokenMarkerStyle()
		{
			return DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle();
		}
		
		@Override
		public void addToFocusListener(FocusListener aFocusListener) 
		{
			_diffPanel.getLeftTextArea().addFocusListener(aFocusListener);
			_diffPanel.getRightTextArea().addFocusListener(aFocusListener);
		}
	}

	public int getChangesCount()
	{
		if (_component instanceof ASCIIFileDiffCodeDisplayer) {
			return ((ASCIIFileDiffCodeDisplayer)_component)._diffReport.getChanges().size();
		}
		return 0;
	}

	public int getAdditionChangeCount()
	{
		if (_component instanceof ASCIIFileDiffCodeDisplayer) {
			return ((ASCIIFileDiffCodeDisplayer)_component)._diffReport.getAdditionChangeCount();
		}
		return 0;
	}
			
	public int getRemovalChangeCount()
	{
		if (_component instanceof ASCIIFileDiffCodeDisplayer) {
			return ((ASCIIFileDiffCodeDisplayer)_component)._diffReport.getRemovalChangeCount();
		}
		return 0;
	}
			
	public int getModificationChangeCount()
	{
		if (_component instanceof ASCIIFileDiffCodeDisplayer) {
			return ((ASCIIFileDiffCodeDisplayer)_component)._diffReport.getModificationChangeCount();
		}
		return 0;
	}

}
