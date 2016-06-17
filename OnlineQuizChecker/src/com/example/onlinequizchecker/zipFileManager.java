package com.example.onlinequizchecker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * The Class zipFileManager.
 */
public class zipFileManager {
	
	/**
	 * Instantiates a new zip file manager.
	 */
	public zipFileManager(){
		
	}
	
	/**
	 * Creates the zip file.
	 *
	 * @param fileToZip the file to zip
	 * @param zipFilePath the zip file path
	 */
	public static void createZipFile(File fileToZip,String zipFilePath)
	{
		 FileOutputStream fos;
		try {
			boolean check;
			fos = new FileOutputStream(zipFilePath);
			File[] files = fileToZip.listFiles();
			ZipOutputStream zos = new ZipOutputStream(fos);
			addDirToZipArchive(zos,fileToZip,null);
            files = fileToZip.listFiles();
            for (int i = 0; i < files.length; i++) {
				if(!files[i].getName().endsWith(".zip"))
					check = files[i].delete();
			}
			zos.flush();
			fos.flush();
			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	/**
	 * Adds the dir to zip archive.
	 *
	 * @param zos the zos
	 * @param fileToZip the file to zip
	 * @param parentDirectoryName the parent directory name
	 * @throws Exception the exception
	 */
	public static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parentDirectoryName) throws Exception {
	    if (fileToZip == null || !fileToZip.exists()) {
	        return;
	    }

	    String zipEntryName = fileToZip.getName();
//	    if (parentDirectoryName!=null && !parentDirectoryName.isEmpty()) {
//	        zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
//	    }

	    if (fileToZip.isDirectory()) {
	        System.out.println("+" + zipEntryName);
	        if(fileToZip.getName().equals(Constants.APP_NAME))
	        	zipEntryName = null;
	        for (File file : fileToZip.listFiles()) {
	        	if(!file.getName().endsWith(".zip"))
	        		addDirToZipArchive(zos, file, zipEntryName);
	        }
	    } else {
	        System.out.println("   " + zipEntryName);
	        byte[] buffer = new byte[1024];
	        FileInputStream fis = new FileInputStream(fileToZip);
	        zos.putNextEntry(new ZipEntry(zipEntryName));
	        int length;
	        while ((length = fis.read(buffer)) > 0) {
	            zos.write(buffer, 0, length);
	        }
	        zos.closeEntry();
	        fis.close();
	    }
	}
	
	/**
	 * Un zip it.
	 *
	 * @param zipFilePath the zip file path
	 * @param outputFolder the output folder
	 */
	public static  void unZipIt(String zipFilePath, String outputFolder){

		File srcFile = new File(zipFilePath);
		
		// create a directory with the same name to which the contents will be extracted
		String zipPath = outputFolder;
		File temp = new File(zipPath);
		temp.mkdir();
		
		ZipFile zipFile = null;
		
		try {
			
			zipFile = new ZipFile(srcFile);
			
			// get an enumeration of the ZIP file entries
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			
			while (e.hasMoreElements()) {
				
				ZipEntry entry = e.nextElement();
				
				File destinationPath = new File(zipPath, entry.getName());
				 
				//create parent directories
				destinationPath.getParentFile().mkdirs();
				
				// if the entry is a file extract it
				if (entry.isDirectory()) {
					continue;
				}
				else {
					
					System.out.println("Extracting file: " + destinationPath);
					
					BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

					int b;
					byte buffer[] = new byte[1024];

					FileOutputStream fos = new FileOutputStream(destinationPath);
					
					BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

					while ((b = bis.read(buffer, 0, 1024)) != -1) {
						bos.write(buffer, 0, b);
					}
					
					bos.close();
					bis.close();
					
				}
				
			}
			
		}
		catch (IOException ioe) {
			System.out.println("Error opening zip file" + ioe);
		}
		 finally {
			 try {
				 if (zipFile!=null) {
					 zipFile.close();
				 }
				 srcFile.delete();
			 }
			 catch (IOException ioe) {
					System.out.println("Error while closing zip file" + ioe);
			 }
		 }
       }    
	
}
