package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class LectQuizInitiationController {
	private MainActivity activity;
	private LectQuizSelectionController previousController;
	public static String course;
	public static String quiz;
	
	private EditText courseIDText;
	private EditText quizNameText;
	private Spinner timeSpinner;
	private Button startQuizBtn;
	private Button backBtn;
	public static ArrayList<String> studentsInClass;
	public static int selectedTimePeriodInt = 0;
	public LectQuizInitiationController(MainActivity activity,LectQuizSelectionController previousController,String course,String quiz)
	{
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_quizinitiationview);
		this.previousController = previousController;
		this.course = course;
		this.quiz = quiz;
		initComponents();
	}
	private void initComponents() {
		// TODO Auto-generated method stub
		courseIDText = (EditText)activity.findViewById(R.id.courseIDTxt);
		quizNameText = (EditText)activity.findViewById(R.id.quizNameTxt);
		timeSpinner = (Spinner)activity.findViewById(R.id.timeSpinner);
		startQuizBtn = (Button)activity.findViewById(R.id.startQuizBtn);
		backBtn = (Button)activity.findViewById(R.id.backBtnQuizInit);
		selectedTimePeriodInt = 0;
		courseIDText.setText(course);
		quizNameText.setText(quiz);
		studentsInClass = new ArrayList<>();
		startQuizBtn.setOnClickListener(new startQuizBtnListener());
		backBtn.setOnClickListener(new backBtnListener());
	}
	class backBtnListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			previousController.retrieveView();
			
		}
		
	}
	class startQuizBtnListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String selectedTimePeriodString = timeSpinner.getSelectedItem().toString().split("\\s+")[0];
			selectedTimePeriodInt = Integer.valueOf(selectedTimePeriodString);
			for (int i = 0; i < LectStudentRegListController.students.size(); i++) {
				if(LectStudentRegListController.listview.isItemChecked(i))
					studentsInClass.add(LectStudentRegListController.students.get(i));
			}
			if(!LectMessageHandler.lecturerServiceStarted)
			{
				LectMessageHandler.lecturerServiceStarted = true;
				Intent intent = new Intent(activity,ClosingService.class);
				activity.startService(intent);
			}
			startQuiz(selectedTimePeriodInt);
		}

        private byte[] toByteArray(CharSequence charSequence) {
            if (charSequence == null) {
              return null;
            }
            byte[] bytesArray = new byte[charSequence.length()];
            for (int i = 0; i < bytesArray.length; i++) {
            	bytesArray[i] = (byte) charSequence.charAt(i);
            }

            return bytesArray;
        }
        
		private void startQuiz(int quizPeriod) {
			// TODO Auto-generated method stub
//			new LectQuizProgressController(activity,quizPeriod);

			try {
				String destinationZipFilePath = activity.getFilelist().getCanonicalPath()+"/"+course+"/Quizzes/"+
						quiz + "/Form/" + quiz+".zip";
		        String directoryToBeZipped = activity.getFilelist().getCanonicalPath()+"/"+course+"/Quizzes/"+
		        		quiz + "/Form";     
				byte[] buffer = new byte[1024];
				FileOutputStream fos = new FileOutputStream(destinationZipFilePath);
				ZipOutputStream zos = new ZipOutputStream(fos);
				File dir = new File(directoryToBeZipped);
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().equals(quiz+".html")||
							files[i].getName().startsWith("Question"))
						{
						System.out.println("Adding file: " + files[i].getName());
						FileInputStream fis = new FileInputStream(files[i]);
						// begin writing a new ZIP entry, positions the stream to
						// the start of the entry data
						zos.putNextEntry(new ZipEntry(files[i].getName()));
						int length;
						while ((length = fis.read(buffer)) > -1) {
							zos.write(buffer, 0, length);
						}
						zos.closeEntry();
						// close the InputStream
						fis.close();
						}

				}
				// close the ZipOutputStream
				zos.close();
				
//		        zipProtectedFile.createZipFileFromSpecificFiles(activity.zipFilesPassword, quiz, destinationZipFilePath, directoryToBeZipped);
		        
		    	FileInputStream fileInputStream=null;
		    	String time = String.valueOf(quizPeriod);
		        File file = new File(destinationZipFilePath);
		        int fileSize = (int) file.length();		       
		        byte[] bFile = new byte[fileSize+
		                                String.valueOf(fileSize).length()+
		                                course.length()+
		                                quiz.length()+
		                                time.length()+
		                                4];
		        
		        byte[] readFile = new byte[fileSize];
		            //convert file into array of bytes
			    fileInputStream = new FileInputStream(file);
//			    fileInputStream.read(bFile);
			    fileInputStream.read(readFile);
			    fileInputStream.close();
			    file.delete();
			    int bIndex;
			    for (bIndex = 0; bIndex < quiz.length(); bIndex++) {
					bFile[bIndex] = (byte)quiz.charAt(bIndex);
				}
			    bFile[bIndex] = (byte)'-';
			    bIndex++;
			    
			    for (int m = 0; m < course.length(); m++) {
					bFile[bIndex] = (byte)course.charAt(m);
					bIndex++;
				}
			    bFile[bIndex] = (byte)'-';
			    bIndex++;
			    
			    for (int j = 0; j < time.length(); j++) {
					bFile[bIndex] = (byte)time.charAt(j);
					bIndex++;
				}
			    bFile[bIndex] = (byte)'-';
			    bIndex++;
			    for (int k = 0; k < String.valueOf(fileSize).length(); k++) {
					bFile[bIndex] = (byte)String.valueOf(fileSize).charAt(k);
					bIndex++;
				}
			    bFile[bIndex] = (byte)'-';
			    bIndex++;
			    for (int l = 0; l < fileSize; l++) {
					bFile[bIndex] = readFile[l];
					bIndex++;
				}
//			    int sendedBytes = 0;
//			    while(bFile.length>sendedBytes)
//			    {
					for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
//						byte[] bytesToSend = new byte[1024];					
//						if (bFile.length - sendedBytes > 1024) 
//						{
//							for (int j = 0; j < 1024; j++) {
//								bytesToSend[j] = bFile[sendedBytes];
//								sendedBytes++;
//							}
//						} 
//						else
//						{
//							for (int j = 0; j < bFile.length - sendedBytes; j++) {
//								bytesToSend[j] = bFile[sendedBytes];
//								sendedBytes++;
//							}
//						}
						 ServerBT.mConnThreads.get(i).getMmOutStream().write(bFile);
//						ServerBT.mConnThreads.get(i).getMmOutStream().write(bytesToSend);
					}
//					for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
//						int b;
//						while ((b = ServerBT.mConnThreads.get(i).getMmInStream().available())>0) {
//							;
//						}
//					}
//					for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
//						 ServerBT.mConnThreads.get(i).getMmOutStream().write(toByteArray("Start Quiz"));
//					}	
//			    }
			    
//			    for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
//					ServerBT.mConnThreads.get(i).getMmOutStream().
//					write(toByteArray(adapter.getItem(selectedIndex)));
//				}	
			    
//			    for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
//					ServerBT.mConnThreads.get(i).getMmOutStream().write(bFile);
//				}	
			    
			   
			}catch(Exception e){
	        	e.printStackTrace();
	        }
			
//			new LectQuizProgressController(activity);
		}
		
	}
}
