package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class LectQuizInitiationController {
	private MainActivity activity;
	private String course;
	private String quiz;
	
	private EditText courseIDText;
	private EditText quizNameText;
	private Spinner timeSpinner;
	private Button startQuizBtn;
	public LectQuizInitiationController(MainActivity activity,String course,String quiz)
	{
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_quizinitiationview);
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
		
		courseIDText.setText(course);
		quizNameText.setText(quiz);
		
		startQuizBtn.setOnClickListener(new startQuizBtnListener());
	}
	class startQuizBtnListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String selectedTimePeriodString = timeSpinner.getSelectedItem().toString().split("\\s+")[0];
			int selectedTimePeriodInt = Integer.valueOf(selectedTimePeriodString);
			startQuiz(selectedTimePeriodInt);
		}

		private void startQuiz(int quizPeriod) {
			// TODO Auto-generated method stub
			try {
				String zipFile = activity.getFilelist().getCanonicalPath()+"/"+course+"/Quizzes/"+
						quiz + "/Form/" + quiz+".zip";
		        String srcDir = activity.getFilelist().getCanonicalPath()+"/"+course+"/Quizzes/"+
		        		quiz + "/Form";     
				byte[] buffer = new byte[1024];
				FileOutputStream fos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos);
				File dir = new File(srcDir);
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
						while ((length = fis.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}
						zos.closeEntry();
						// close the InputStream
						fis.close();
						}

				}
				// close the ZipOutputStream
				zos.close();
				
		    	FileInputStream fileInputStream=null;
		    	String time = String.valueOf(quizPeriod);
		        File file = new File(zipFile);
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
			    	char shit = String.valueOf(fileSize).charAt(k);
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
		}
		
	}
}