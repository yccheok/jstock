/*
 * IndicatorProjectManager.java
 *
 * Created on June 9, 2007, 7:38 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import java.util.*;
import java.io.*;

import com.thoughtworks.xstream.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public class IndicatorProjectManager {
    
    /** Creates a new instance of IndicatorProjectManager */
    public IndicatorProjectManager(String directory) {
        File f = new File(directory);
        
        if(f.exists() == false) {
            if(f.mkdir())
            {
                log.info("New directory " + directory + " created.");
            }
            else
            {
                log.error("Fail to create directory " + directory);
            }
        }
        else {
            log.info(directory + " already exists.");
        }
        
        this.directory = directory;
    }

    public int getNumOfProject() {
        return projects.size();
    }
    
    public String getProject(int index) {
        return projects.get(index);
    }
    
    // Overwrite is allowed.
    public boolean addProject(IndicatorDefaultDrawing drawing, String project) {
        if(projects.contains(project) == false)
            projects.add(project); 
                
        try {
            drawing.write(project, getJHotDrawFilename(project), getOperatorIndicatorFilename(project));
        }
        catch(IOException exp) {
            projects.remove(project);
            log.error("", exp);
            return false;
        }
        
        return true;
    }

    private String getJHotDrawFilename(String project) {
        return directory + File.separator + project + "-jhotdraw.xml";
    }
    
    private String getOperatorIndicatorFilename(String project) {
        return directory + File.separator + project + ".xml";
    }
    
    public boolean removeProject(String project) {
        if(new File(getJHotDrawFilename(project)).delete() == false) {
            // No return. We may need to remove a corrupted project. Continue.
            // return false;
        }

        if(new File(getOperatorIndicatorFilename(project)).delete() == false) {
            // No return. We may need to remove a corrupted project. Continue.
            // return false;
        }
        
        return projects.remove(project);
    }
    
    public boolean renameProject(String newProject, String oldProject) {
        if(newProject.equals(oldProject)) {
            // Nothing to be done. We will consider this as success.
            return true;
        }
        
        File oldJHotDrawFile = new File(getJHotDrawFilename(oldProject));
        File oldOperatorIndicatorFile = new File(getOperatorIndicatorFilename(oldProject));
        File newJHotDrawFile = new File(getJHotDrawFilename(newProject));
        File newOperatorIndicatorFile = new File(getOperatorIndicatorFilename(newProject));
        
        if(oldJHotDrawFile.renameTo(newJHotDrawFile) == false)
            return false;

        if(oldOperatorIndicatorFile.renameTo(newOperatorIndicatorFile) == false)
            return false;
        
        final int index = projects.indexOf(oldProject);
        if(index == -1) return false;
        
        boolean status = projects.remove(oldProject);
        if(status == false) return false;        
        
        projects.add(index, newProject);
        
        // ********************************************
        // Hacking way to rename the OperatorIndicator.
        // ********************************************
        org.yccheok.jstock.analysis.OperatorIndicator operatorIndicator = getOperatorIndicator(newProject);
        operatorIndicator.setName(newProject);
        XStream xStream = new XStream();                
        
        OutputStream outputStream = null;
        
        try {
            outputStream = new FileOutputStream(new File(this.getOperatorIndicatorFilename(newProject)));
            xStream.toXML(operatorIndicator, outputStream);            
        }
        catch(com.thoughtworks.xstream.core.BaseException exp) {
            log.error("", exp);
            return false;
        }
        catch(java.io.FileNotFoundException exp) {            
            log.error("", exp);
            return false;
        }
        finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                }
                catch(java.io.IOException exp) {
                    log.error("", exp);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public IndicatorDefaultDrawing getIndicatorDefaultDrawing(String project) {
        if(projects.contains(project) == false) return null;
        
        IndicatorDefaultDrawing drawing = new IndicatorDefaultDrawing();
        
        try {
            drawing.read(this.getJHotDrawFilename(project), this.getOperatorIndicatorFilename(project));
        }
        catch(java.io.IOException exp) {
            log.error("", exp);
            return null;
        }
        
        return drawing;
    }

    public org.yccheok.jstock.analysis.OperatorIndicator getOperatorIndicator(String project) {
        if(projects.contains(project) == false) return null;
        
        File operatorIndicatorFile = new File(getOperatorIndicatorFilename(project));
        
        XStream xStream = new XStream();
        
        try {
            InputStream inputStream = new java.io.FileInputStream(operatorIndicatorFile);            
            org.yccheok.jstock.analysis.OperatorIndicator operatorIndicator = (org.yccheok.jstock.analysis.OperatorIndicator)xStream.fromXML(inputStream);
            return operatorIndicator;
        }
        catch(java.io.FileNotFoundException exp) {
            log.error("", exp);
        }
        catch(com.thoughtworks.xstream.core.BaseException exp) {
            log.error("", exp);
        }        
        
        return null;
    }
    
    public boolean contains(String project) {
        return projects.contains(project);
    }
    
    private static final Log log = LogFactory.getLog(IndicatorProjectManager.class);
    
    private List<String> projects = new ArrayList<String>();
    private String directory;
}
