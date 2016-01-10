/*
 * JStock - Free Stock Market Software
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.yccheok.jstock.gui;

import java.util.*;
import java.io.*;


import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yccheok.jstock.analysis.OperatorIndicator;

/**
 *
 * @author yccheok
 */
public class IndicatorProjectManager {

    /**
     * @return the operatorIndicatorType
     */
    public OperatorIndicator.Type getPreferredOperatorIndicatorType() {
        return preferredOperatorIndicatorType;
    }

    public enum PreInstallStatus {
        Safe,       // Zipped file with correct format.
        Unsafe,     // Zipper file with bad format.
        Collision   // Zipped file with correct format. However, we may overwrite
                    // our existing project.
    }

    /** Creates a new instance of IndicatorProjectManager */
    public IndicatorProjectManager(String directory, OperatorIndicator.Type preferredOperatorIndicatorType) {
        org.yccheok.jstock.gui.Utils.createCompleteDirectoryHierarchyIfDoesNotExist(directory);

        this.preferredOperatorIndicatorType = preferredOperatorIndicatorType;
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
        // Avoid naming crashing.
        final int reserve_word_length = JHOTDRAW_RESERVE_WORD.length();
        if (project.length() >= reserve_word_length) {
            final String substring = project.substring(project.length() - reserve_word_length, project.length());
            if (substring.equalsIgnoreCase(JHOTDRAW_RESERVE_WORD)) {
                return false;
            }
        }

        if (this.contains(project) == false) {
            projects.add(project);
        }
        try {
            drawing.write(project, getJHotDrawFilename(project), getOperatorIndicatorFilename(project));
        }
        catch (IOException exp) {
            projects.remove(project);
            log.error(null, exp);
            return false;
        }
        
        return true;
    }

    private String getJHotDrawFilename(String project) {
        return directory + File.separator + project + JHOTDRAW_RESERVE_WORD + ".xml";
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
        if (newProject.equals(oldProject)) {
            // Nothing to be done. We will consider this as success.
            return true;
        }
        
        File oldJHotDrawFile = new File(getJHotDrawFilename(oldProject));
        File oldOperatorIndicatorFile = new File(getOperatorIndicatorFilename(oldProject));
        File newJHotDrawFile = new File(getJHotDrawFilename(newProject));
        File newOperatorIndicatorFile = new File(getOperatorIndicatorFilename(newProject));
        
        if (oldJHotDrawFile.renameTo(newJHotDrawFile) == false) {
            return false;
        }

        if (oldOperatorIndicatorFile.renameTo(newOperatorIndicatorFile) == false) {
            return false;
        }
        
        final int index = projects.indexOf(oldProject);
        if (index == -1) {
            return false;
        }
        
        boolean status = projects.remove(oldProject);
        if (status == false) {
            return false;
        }
        
        projects.add(index, newProject);
        
        // ********************************************
        // Hacking way to rename the OperatorIndicator.
        // ********************************************
        org.yccheok.jstock.analysis.OperatorIndicator operatorIndicator = getOperatorIndicator(newProject);
        operatorIndicator.setName(newProject);
        return Utils.toXML(operatorIndicator, this.getOperatorIndicatorFilename(newProject));
    }
    
    public IndicatorDefaultDrawing getIndicatorDefaultDrawing(String project) {
        if (this.contains(project) == false) {
            return null;
        }
        final IndicatorDefaultDrawing drawing = new IndicatorDefaultDrawing();
        try {
            drawing.read(this.getJHotDrawFilename(project), this.getOperatorIndicatorFilename(project));
        }
        catch (java.io.IOException exp) {
            log.error(null, exp);
            return null;
        }
        return drawing;
    }

    public org.yccheok.jstock.analysis.OperatorIndicator getOperatorIndicator(String project) {
        if (this.contains(project) == false) {
            return null;
        }
        File operatorIndicatorFile = new File(getOperatorIndicatorFilename(project));
        return Utils.fromXML(org.yccheok.jstock.analysis.OperatorIndicator.class, operatorIndicatorFile);
    }
    
    public boolean contains(String project) {
        // Do not use
        // projects.contains(project);
        // as it is case sensitive.
        // We need case in-sensitive comparison.
        for (String p : projects) {
            if (p.equalsIgnoreCase(project)) {
                return true;
            }
        }
        return false;
    }

    public boolean install(File zipFile) {
        // First, get the project name.
        final String projectName = IndicatorProjectManager.getProjectName(zipFile);
        if (projectName == null) {
            return false;
        }

        final String jHotDrawFilename = projectName + JHOTDRAW_RESERVE_WORD + ".xml";
        final String operatorIndicatorFilename = projectName + ".xml";

        boolean status = true;
        /* projectName, operatorIndicatorFilename, jHotDrawFilename */
        ZipInputStream in = null;
        try {
            in = new ZipInputStream(new FileInputStream(zipFile));
            // Get the first entry
            ZipEntry zipEntry = null;

            while ((zipEntry = in.getNextEntry()) != null)
            {
                final String name = zipEntry.getName();
                String outFilename = null;
                
                if (name.equalsIgnoreCase(operatorIndicatorFilename)) {
                    outFilename = this.getOperatorIndicatorFilename(projectName);
                }
                else if (name.equalsIgnoreCase(jHotDrawFilename)) {
                    outFilename = this.getJHotDrawFilename(projectName);
                }
                else {
                    assert(false);
                }

                OutputStream out = null;

                try {
                    out = new FileOutputStream(outFilename);

                    // Transfer bytes from the ZIP file to the output file
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                finally {
                    org.yccheok.jstock.file.Utils.close(out);
                    org.yccheok.jstock.file.Utils.closeEntry(in);
                }
            }
        }
        catch (IOException ex) {
            log.error(null, ex);
            status = false;
        }
        finally {
            org.yccheok.jstock.file.Utils.close(in);
        }

        if (status == false) {
            new File(this.getOperatorIndicatorFilename(projectName)).delete();
            new File(this.getJHotDrawFilename(projectName)).delete();
            return false;
        }

        final OperatorIndicator operatorIndicator = (OperatorIndicator)Utils.fromXML(OperatorIndicator.class, this.getOperatorIndicatorFilename(projectName));
        if (operatorIndicator == null || operatorIndicator.getType() != this.preferredOperatorIndicatorType) {
            new File(this.getOperatorIndicatorFilename(projectName)).delete();
            new File(this.getJHotDrawFilename(projectName)).delete();
            return false;
        }

        if (this.contains(projectName) == false) {
            this.projects.add(projectName);
        }
        return true;
    }

    public static String getProjectName(File zipFile) {
        final List<String> names = new ArrayList<String>();

        try {
            ZipFile zf = new ZipFile(zipFile);
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
                // Get the entry name
                final ZipEntry zipEntry = (ZipEntry)entries.nextElement();
                final String name = zipEntry.getName();
                final boolean isDirectory = zipEntry.isDirectory();
                if (isDirectory) {
                    return null;
                }
                names.add(name);
            }
        }
        catch (IOException ex) {
            log.error(null, ex);
            return null;
        }

        // operatorIndicatorFilename and jHotDrawFilename
        if (names.size() != 2) {
            return null;
        }

        String projectName = null;
        for (String name : names) {
            final int index = name.indexOf(".xml");
            if (index < 0) {
                continue;
            }
            final String _projectName = name.substring(0, index);
            projectName = projectName == null ? _projectName : (projectName.length() > _projectName.length() ? _projectName : projectName);
        }

        if (projectName == null || projectName.length() <= 0) {
            return null;
        }

        final String operatorIndicatorFilename = projectName + ".xml";
        final String jHotDrawFilename = projectName + JHOTDRAW_RESERVE_WORD + ".xml";

        for (String name : names) {
            if (!name.equalsIgnoreCase(operatorIndicatorFilename) && !name.equalsIgnoreCase(jHotDrawFilename)) {
                return null;
            }
        }
        return projectName;
    }

    public PreInstallStatus getPreInstallStatus(File zipFile) {
        final String projectName = IndicatorProjectManager.getProjectName(zipFile);
        if (projectName == null) {
            return PreInstallStatus.Unsafe;
        }
        return this.contains(projectName) ? PreInstallStatus.Collision : PreInstallStatus.Safe;
    }

    private Object readResolve() {
        /* For backward compatible */
        if (this.preferredOperatorIndicatorType == null) {
            this.preferredOperatorIndicatorType = OperatorIndicator.Type.AlertIndicator;
        }

        /* When we load IndicatorProjectManager from cloud, we may face the problem
         * where directory is no longer valid.
         * This is the hacking way to solve above mentioned problem.
         */
        if (this.directory.endsWith("indicator")) {
            this.directory = org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "indicator";
        }
        else if (this.directory.endsWith("module")) {
            this.directory = org.yccheok.jstock.gui.Utils.getUserDataDirectory() + "module";
        }
        else {
            log.error("Something goes wrong during directory initialization");
            assert(false);
        }

        return this;
    }

    public boolean export(String project, File zipFile) {
        // Create a buffer for reading the files
        final byte[] buf = new byte[1024];
        ZipOutputStream out = null;
        try {
            // Create the ZIP file
            out = new ZipOutputStream(new FileOutputStream(zipFile));

            final List<String> filenames = new ArrayList<String>();
            filenames.add(this.getOperatorIndicatorFilename(project));
            filenames.add(this.getJHotDrawFilename(project));
            // Compress the files
            for (String filename : filenames) {
                FileInputStream in = null;
                try {
                    in = new FileInputStream(filename);
                    // Add ZIP entry to output stream.
                    // I do not want full path. I just want filename.
                    out.putNextEntry(new ZipEntry(new File(filename).getName()));

                    // Transfer bytes from the file to the ZIP file
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                finally {
                    // Complete the entry
                    org.yccheok.jstock.file.Utils.closeEntry(out);
                    org.yccheok.jstock.file.Utils.close(in);
                }
            }
        } catch (IOException ex) {
            log.error(null, ex);
            return false;
        }
        finally {
            org.yccheok.jstock.file.Utils.close(out);
        }
        return true;
    }

    /* For backward compatible. Make it final in later version. */
    private OperatorIndicator.Type preferredOperatorIndicatorType;

    private static final Log log = LogFactory.getLog(IndicatorProjectManager.class);

    private static final String JHOTDRAW_RESERVE_WORD = "-jhotdraw";

    private final List<String> projects = new ArrayList<String>();
    /* When we load IndicatorProjectManager from cloud, we may face the problem
     * where directory is no longer valid.
     * This is the hacking way to solve above mentioned problem.
     */
    private String directory;
}
