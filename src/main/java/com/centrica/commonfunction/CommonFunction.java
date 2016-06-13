/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centrica.commonfunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author dandy
 */
public class CommonFunction {

    public static boolean stringIsEmpty(String a) {
        if(a == null) return true;
        return a.trim().isEmpty();
    }
    
    public static boolean stringEquals(String a, String b) {
        if (a.isEmpty()) {
            return false;
        }
        if (b.isEmpty()) {
            return false;
        }
        if (a.toLowerCase().trim().equals("null")) {
            return false;
        }
        if (b.toLowerCase().trim().equals("null")) {
            return false;
        }
        return a.toLowerCase().trim().equals(b.toLowerCase().trim());
    }
    
    public static boolean stringContains(String source, String b) {
        return source.toLowerCase().trim().contains(b.toLowerCase().trim());
    }

    public static String readProperty(String propertyName) throws Exception {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            return prop.getProperty(propertyName);

        } catch (IOException ex) {
            ex.printStackTrace();
            throw (new Exception("IO Exception"));
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw (new Exception("IO Exception"));
                }
            }
        }
    }

    public static void generateCsvFile(String fileName, String content, boolean isAppend) throws Exception {
        try {
            FileWriter writer = new FileWriter(fileName,isAppend);
            
            writer.append(content);
            writer.append("\n");
            //generate whatever data you want
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw (new Exception("IO Exception"));
        }
    }
    
    public static String getCleanedString(String str){
        return str.trim().toLowerCase();
    }
    
    public static String[] getFilenamesInFolder(String folderName){
        File dir = new File(folderName);
        File[] directoryListing = dir.listFiles();
        String[] fileNames = new String[directoryListing.length];
        for(int i=0;i<directoryListing.length;i++){
            fileNames[i] = directoryListing[i].getName();
        }
        return fileNames;
    }
}
