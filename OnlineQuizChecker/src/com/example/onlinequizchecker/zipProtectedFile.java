package com.example.onlinequizchecker;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class zipProtectedFile {
	public void createZipFile(String password,String destinationZipFilePath,String directoryToBeZipped)
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

             System.out.println("Password protected Zip file of Directory "
                    +directoryToBeZipped+" have been created at "+ destinationZipFilePath);

      } catch (ZipException e) {
             e.printStackTrace();
      }
	}
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
