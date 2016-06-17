package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * The Class zipProtectedFile.
 */
public class zipProtectedFile {
	
	/**
	 * Creates the zip file.
	 *
	 * @param password the password
	 * @param destinationZipFilePath the destination zip file path
	 * @param directoryToBeZipped the directory to be zipped
	 */
	public static void createZipFile(String password,String destinationZipFilePath,String directoryToBeZipped)
	{
		 try {
             
             // --------Encryption zipParameters (for password protection)--------
             // Create ZipParameters
             ZipParameters zipParameters = new ZipParameters();

             // Set how you want to encrypt files
             zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
             zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

             // Set encryption of files to true
             zipParameters.setEncryptFiles(true);

             // Set encryption method
             zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
             // Set key strength
             zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

             // Set password
             zipParameters.setPassword(password);

             
             
             
             // --------------------CREATE ZIP file - Zip DIRECTORY-------------

             // Create ZIP file
             ZipFile zipFile = new ZipFile(destinationZipFilePath);

             // pass (Directory to be Zipped) and ZIP parameters
             //for Zip file to be created
             zipFile.addFolder(directoryToBeZipped, zipParameters);
             File[] files = new File(directoryToBeZipped).listFiles();
             for (int i = 0; i < files.length; i++) {
				if(!files[i].getName().endsWith(".zip"))
					files[i].delete();
			}
             
             System.out.println("Password protected Zip file of Directory "
                    +directoryToBeZipped+" have been created at "+ destinationZipFilePath);

      } catch (ZipException e) {
             e.printStackTrace();
      }
	}
	
	/**
	 * Creates the zip file from specific files.
	 *
	 * @param password the password
	 * @param studentId the student id
	 * @param destinationZipFilePath the destination zip file path
	 * @param directoryToBeZipped the directory to be zipped
	 */
	public static void createZipFileFromSpecificFiles(String password,CharSequence studentId,String destinationZipFilePath,String directoryToBeZipped){
		 try {
			 
             // --------Encryption zipParameters (for password protection)--------
             // Create ZipParameters
             ZipParameters zipParameters = new ZipParameters();

             // Set how you want to encrypt files
             zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
             zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

             // Set encryption of files to true
             zipParameters.setEncryptFiles(true);

             // Set encryption method
             zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
             // Set key strength
             zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

             // Set password
             zipParameters.setPassword(password);

             // ---------------------------CREATE ZIP file-------------

             // Create ZIP file
             ZipFile zipFile = new ZipFile(destinationZipFilePath);

             // Create list of files to be added to ZIP file
             ArrayList<File> list = new ArrayList<File>();
             File dir = new File(directoryToBeZipped);
			 File[] files = dir.listFiles();
				
			 //Add SPECIFIC  files to list
		         
             for (int i = 0; i < files.length; i++) {
//					if (files[i].getName().equals(studentId+".html")|| files[i].getName().startsWith("Question"))
					if(!files[i].getName().endsWith(".zip"))
					{
						System.out.println("Adding file: " + files[i].getName());
					    list.add(files[i]);
					}
				}
           

             // pass (list of files to be added to ZIP file) and ZIP parameters
             //for Zip file to be created
             zipFile.addFiles(list, zipParameters);
             for (int i = 0; i < files.length; i++) {
                 if(!files[i].getName().endsWith(".zip"))
                     files[i].delete();
             }
             System.out.println("Password protected Zip file of specific files "
                          + "have been created at "  + destinationZipFilePath);

      } catch (ZipException e) {
             e.printStackTrace();
      }
	}
	
	/**
	 * Unzip file.
	 *
	 * @param password the password
	 * @param sourceZipFilePath the source zip file path
	 * @param extractedZipFilePath the extracted zip file path
	 */
	public static void unzipFile(String password,String sourceZipFilePath ,String extractedZipFilePath)
	{
		 try {

             ZipFile zipFile = new ZipFile(sourceZipFilePath);

             // check if file was encrypted, if encrypted 
//then decrypt it using using password
             if (zipFile.isEncrypted()) {
                   zipFile.setPassword(password);
             }

             //Now Extract ZIP file
             zipFile.extractAll(extractedZipFilePath);
             
             System.out.println(sourceZipFilePath + " have been extracted at "
                          + extractedZipFilePath);

      } catch (ZipException e) {
             e.printStackTrace();
      }
	}
	
}
