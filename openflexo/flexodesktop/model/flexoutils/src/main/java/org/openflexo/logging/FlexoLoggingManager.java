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
package org.openflexo.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.logging.viewer.FlexoLoggingViewerWindow;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLMapping;
import org.xml.sax.SAXException;


/**
 * Flexo logging manager: manages logs for the application above
 * java.util.logging Also read and parse logs of an expired session of Flexo
 *
 * @author sguerin
 */
public class FlexoLoggingManager
{

    private static XMLMapping _loggingMapping = null;

    private static boolean _isInitialized = false;

    private static boolean _isLoggingWindowShowed = false;

    public static LogRecords logRecords = null;

    private static FlexoLoggingViewerWindow viewer = null;

    private static FlexoLoggingHandler _flexoLoggingHandler;

    public static void forceInitialize(){
    	try{
    		initialize();
    	}catch (Exception e) {
			e.printStackTrace();
			_isInitialized = true;
	        _isLoggingWindowShowed = false;
	        logRecords = new LogRecords();
	        viewer = null;
		}
    }
    public static void initialize() throws SecurityException, IOException
    {
        File f = new File(System.getProperty("user.home"),"Library/Logs/Flexo/");
        if (!f.exists())
            f.mkdirs();
        _isLoggingWindowShowed = false;
        logRecords = new LogRecords();
        viewer = null;
        LogManager.getLogManager().readConfiguration();
        _isInitialized = true;
    }

    public static boolean isInitialized()
    {
        return _isInitialized;
    }

    public static boolean isLoggingWindowShowed()
    {
        return _isLoggingWindowShowed;
    }

    public static void unhandledException(Exception e)
    {
        FlexoLoggingHandler flexoLoggingHandler = getFlexoLoggingHandler();
        if (flexoLoggingHandler != null) {
            flexoLoggingHandler.publishUnhandledException(new java.util.logging.LogRecord(java.util.logging.Level.WARNING, "Unhandled exception occured: "
                    + e.getClass().getName()), e);
        } else {
            Logger.global.warning("Unexpected exception occured: " + e.getClass().getName());
        }
    }

    public static void setFlexoLoggingHandler(FlexoLoggingHandler handler)
    {
        _flexoLoggingHandler = handler;
    }

    public static FlexoLoggingHandler getFlexoLoggingHandler()
    {
        return _flexoLoggingHandler;
    }

    public static XMLMapping getLoggingMapping()
    {
        if (_loggingMapping == null) {
            File loggingModelFile;
            loggingModelFile = new FileResource("Models/LoggingModel.xml");
            if (!loggingModelFile.exists()) {
                System.err.println("File " + loggingModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
                return null;
            } else {
                try {
                    _loggingMapping = new XMLMapping(loggingModelFile);
                } catch (IOException e) {
                    System.out.println(e.toString());
                } catch (SAXException e) {
                    System.out.println(e.toString());
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    System.out.println(e.toString());
                }
            }
        }
        return _loggingMapping;
    }

    public static void showLoggingViewer()
    {
        viewer = new FlexoLoggingViewerWindow(logRecords);
        _isLoggingWindowShowed = true;
        viewer.setVisible(true);
    }

    public static String logsReport()
    {
        try {
            return XMLCoder.encodeObjectWithMapping(logRecords, getLoggingMapping(),StringEncoder.getDefaultInstance());
        } catch (Exception e) {
            e.printStackTrace();
            return "No report available";
        }
    }

    private static boolean _keepLogTrace;

    public static boolean getKeepLogTrace(){
    	return _keepLogTrace;
    }
	public static void setKeepLogTrace(boolean logTrace) {
		_keepLogTrace = logTrace;
	}


	private static int _logCount;

	public static void setLogCount(Integer logCount) {
		_logCount = logCount;
	}
	public static int getLogCount(){
		return _logCount;
	}

}
