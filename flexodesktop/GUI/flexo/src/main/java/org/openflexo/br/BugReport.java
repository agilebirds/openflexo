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
package org.openflexo.br;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.FlexoCst;
import org.openflexo.kvc.KVCObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.logging.LogRecords;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;
import org.xml.sax.SAXException;


/**
 * Represents a bug report
 *
 * @author sguerin
 */
public class BugReport extends KVCObject implements XMLSerializable, TableModel
{

    private static final Logger logger = Logger.getLogger(BugReport.class.getPackage().getName());

    public static enum IncidentType {
    	TYPE_INCIDENT_KEY {
    		@Override
			public String getKey() {
    			return "INC";
    		}
    	},
    	TYPE_FUNCTIONALREQUEST_KEY {
    		@Override
			public String getKey() {
    			return "REQ";
    		}
    	},
    	TYPE_SERVICEREQUEST_KEY {
    		@Override
			public String getKey() {
    			return "SERV";
    		}
    	};
    	public abstract String getKey();

    	public String getLocalizedName() {
    		return FlexoLocalization.localizedForKey(name().toLowerCase());
    	}

    	@Override
    	public String toString() {
    		return getLocalizedName();
    	}
    }

    public String identifier;

    public String title;

    public IncidentType type;

    public int impact;

    public int urgency;

    public Date date;

    public String submissionUserName;

    public String assignedUserName;

    public String flexoVersion;

    public String context;

    public String description;

    public String technicalDescription;

    public int severity;

    public Module module;

    public String stacktrace;

    public LogRecords logRecords;

    private String _dateAsString;

    public Vector<BugReportAction> actions;

    private DefaultTableModel model;

    private static Vector<String> availableSeverity = null;

    private static Vector<String> availableStatus = null;

    private static Vector<String> availableUrgency = null;
    private static Vector<String> availableImpact = null;

    public BugReport(Exception e, Module module)
    {
        this(module);
        title="";
        description = "Unexpected exception raised: " + e.getClass().getName() + "\nMessage: " + e.getMessage();
        this.stacktrace = getStackTraceAsString(e.getStackTrace());
        Throwable t = e.getCause();
        while(t!=null){
        	this.stacktrace = this.stacktrace+"\n\nCAUSED BY:\n"+getStackTraceAsString(t.getStackTrace());
        	t = t.getCause();
        }
    }

    public BugReport(Module module)
    {
        this(true);
        this.module = module;
    }

    /**
     * Constructor used for serialization
     *
     */
    public BugReport()
    {
        this(false);
    }

    public BugReport(boolean createFromApplication)
    {
        super();
        type = IncidentType.TYPE_INCIDENT_KEY;
        actions = new Vector<BugReportAction>();
        model = new DefaultTableModel();
        if (createFromApplication) {
            title = "";
            date = new Date();
            submissionUserName = System.getProperty("user.name");
            generateIdentifier();
            flexoVersion = FlexoCst.BUSINESS_APPLICATION_VERSION.toString();
            context = "";
            description = "";
            technicalDescription = "";
            assignedUserName = "";
            severity = 2;
            if (FlexoModule.getActiveModule() != null) {
                this.module = FlexoModule.getActiveModule().getModule();
            }
            logRecords = FlexoLoggingManager.logRecords;
            BugReportAction submittedAction = new BugReportAction();
            submittedAction.status = 0;
            submittedAction.description = "Submitted on " + submittedAction.dateAsString() + " by " + submittedAction.username;
            actions.add(submittedAction);
        }
    }

    private void generateIdentifier()
    {
        String userName = System.getProperty("user.name");
        identifier = userName.substring(0, 2);
        identifier = identifier.toUpperCase();
        File databaseDir = BugReports.getDatabaseDirectory();
        boolean identifierIsFound = false;
        int id = 1;
        String tryWith = identifier + "-" + stringIdentifier(id) + ".brxml";
        while (!identifierIsFound) {
            tryWith = identifier + "-" + stringIdentifier(id) + ".brxml";
            if ((new File(databaseDir, tryWith)).exists()) {
                id++;
            } else {
                identifierIsFound = true;
            }
        }
        identifier = identifier + "-" + stringIdentifier(id);
    }

    private String stringIdentifier(int id)
    {
        String returned = "" + id;
        while (returned.length() < 3) {
            returned = "0" + returned;
        }
        return returned;
    }

    public static String getStackTraceAsString(StackTraceElement[] stackTrace)
    {
        StringBuilder returned = new StringBuilder();
        if (stackTrace != null) {
            for (int i = 0; i < stackTrace.length; i++) {
                returned.append("\tat ").append(stackTrace[i]).append("\n");
            }
        }
        return returned.toString();
    }

    private static XMLMapping _bugReportMapping;

    public static XMLMapping getBRMapping()
    {
        if (_bugReportMapping == null) {
            File bugReportModelFile;
            bugReportModelFile = new FileResource("Models/BugReportModel.xml");
            if (!bugReportModelFile.exists()) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("File " + bugReportModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
                return null;
            } else {
                try {
                    _bugReportMapping = new XMLMapping(bugReportModelFile);
                } catch (InvalidModelException e) {
                    // Warns about the exception
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                    e.printStackTrace();
                } catch (IOException e) {
                    // Warns about the exception
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                    e.printStackTrace();
                } catch (SAXException e) {
                    // Warns about the exception
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    // Warns about the exception
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                    e.printStackTrace();
                }
            }
        }
        return _bugReportMapping;
    }

    public void saveToFile(File file)
    {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            XMLCoder.encodeObjectWithMapping(this, getBRMapping(), out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile()
    {
        File directoryFile;
        directoryFile = new FileResource("BugReportsDatabase");
        return new File(directoryFile, identifier + ".brxml");
    }

    public void save()
    {
        saveToFile(getFile());
    }

    public String getXMLRepresentation()
    {
        try {
            return XMLCoder.encodeObjectWithMapping(this, getBRMapping());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Vector<String> getAvailableSeverity()
    {
        if (availableSeverity == null) {
            availableSeverity = new Vector<String>();
            availableSeverity.add("Critical");
            availableSeverity.add("Major");
            availableSeverity.add("Minor");
            availableSeverity.add("GUI");
            availableSeverity.add("Missing feature");
            availableSeverity.add("Nice-to-have feature");
        }
        return availableSeverity;
    }

    public static Vector getAvailableImpact()
    {
        if (availableImpact == null) {
        	availableImpact = new Vector<String>();
        	availableImpact.add("Low");
        	availableImpact.add("Medium");
        	availableImpact.add("High");
        }
        return availableImpact;
    }
    public static Vector getAvailableUrgency()
    {
        if (availableUrgency == null) {
        	availableUrgency = new Vector<String>();
        	availableUrgency.add("Low");
        	availableUrgency.add("Medium");
        	availableUrgency.add("High");
        }
        return availableUrgency;
    }
    public static Vector<String> getAvailableStatus()
    {
        if (availableStatus == null) {
            availableStatus = new Vector<String>();
            availableStatus.add("Submitted");
            availableStatus.add("Opened");
            availableStatus.add("Fixed");
            availableStatus.add("Reopened");
            availableStatus.add("Unresolved");
            availableStatus.add("Non-reproducible");
        }
        return availableStatus;
    }

    public String severityAsString()
    {
        return getAvailableSeverity().get(severity);
    }

    public String statusAsString()
    {
        return getAvailableStatus().get(getStatus());
    }

    public int getStatus()
    {
        if (actions.size() > 0) {
            return actions.lastElement().status;
        }
        return 0;
    }

    public String dateAsString()
    {
        if (_dateAsString == null) {
            StringEncoder.setDateFormat("dd/MM/yyyy HH:mm:ss");
            _dateAsString = StringEncoder.getDateRepresentation(date);
        }
        return _dateAsString;
    }

    public BugReportAction elementAt(int row)
    {
        return actions.elementAt(row);
    }

    @Override
	public int getRowCount()
    {
        return actions.size();
    }

    @Override
	public int getColumnCount()
    {
        return 3;
    }

    @Override
	public String getColumnName(int arg0)
    {
        switch (arg0) {
        case 0:
            return "User";
        case 1:
            return "Date";
        case 2:
            return "Status";
        default:
            return null;
        }
    }

    @Override
	public Class<String> getColumnClass(int arg0)
    {
        return String.class;
    }

    @Override
	public boolean isCellEditable(int arg0, int arg1)
    {
        return false;
    }

    @Override
	public Object getValueAt(int row, int col)
    {
        BugReportAction record =actions.elementAt(row);
        switch (col) {
        case 0:
            return record.username;
        case 1:
            return record.dateAsString();
        case 2:
            return record.statusAsString();

        default:
            return "";
        }
    }

    @Override
	public void setValueAt(Object arg0, int arg1, int arg2)
    {

    }

    @Override
	public void addTableModelListener(TableModelListener arg0)
    {
        model.addTableModelListener(arg0);
    }

    @Override
	public void removeTableModelListener(TableModelListener arg0)
    {
        model.removeTableModelListener(arg0);
    }

    /**
     *
     */
    public void addAction()
    {
        actions.add(new BugReportAction());
        model.fireTableDataChanged();
    }

    /**
     * @param _action
     */
    public void removeAction(BugReportAction _action)
    {
        actions.remove(_action);
        model.fireTableDataChanged();
    }

    /**
     * @return
     */
    public boolean isValid()
    {
        if (title==null || title.trim().length()==0)
            return false;
        if (description==null|| description.trim().length()==0)
            return false;
        return true;
    }

}
