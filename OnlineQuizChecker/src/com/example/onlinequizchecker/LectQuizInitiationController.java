package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
			int selectedTimePeriodInt = Integer.valueOf(selectedTimePeriodString);
			for (int i = 0; i < LectStudentRegListController.students.size(); i++) {
				if(LectStudentRegListController.listview.isItemChecked(i))
					studentsInClass.add(LectStudentRegListController.students.get(i));
			}
			startQuiz(selectedTimePeriodInt);
		}

		private void startQuiz(int quizPeriod) {
			// TODO Auto-generated method stub
			long timeToStartTheQuiz =System.currentTimeMillis()+10000; //Ten seconds
			
			

			try {
				String destinationZipFilePath = activity.getFilelist().getCanonicalPath()+"/"+course+"/Quizzes/"+
						quiz + "/Form/" + quiz+".zip";
		        String directoryToBeZipped = activity.getFilelist().getCanonicalPath()+"/"+course+"/Quizzes/"+
		        		quiz + "/Form";     
				byte[] buffer = new byte[4096];
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
		        
		        // ---------------Added for time sync----------------	
		        long currentTime = System.currentTimeMillis();
			    String mSecondesToWait = String.valueOf(timeToStartTheQuiz-currentTime);
			    // --------------------------------------------------	
			    
		        byte[] bFile = new byte[fileSize+
		                                String.valueOf(fileSize).length()+
		                                course.length()+
		                                quiz.length()+
		                                time.length()+
		                                mSecondesToWait.length()+
		                                5];
		        
		        byte[] readFile = new byte[fileSize];
		            //convert file into array of bytes
			    fileInputStream = new FileInputStream(file);
//			    fileInputStream.read(bFile);
			    fileInputStream.read(readFile);
			    fileInputStream.close();
			    file.delete();
			    int bIndex;
			    //Store quiz name
			    for (bIndex = 0; bIndex < quiz.length(); bIndex++) {
					bFile[bIndex] = (byte)quiz.charAt(bIndex);
				}
			    bFile[bIndex] = (byte)'-';
			    bIndex++;
			    //Store course name
			    for (int m = 0; m < course.length(); m++) {
					bFile[bIndex] = (byte)course.charAt(m);
					bIndex++;
				}
			    bFile[bIndex] = (byte)'-';
			    bIndex++;
			    //Store quiz time period
			    for (int j = 0; j < time.length(); j++) {
					bFile[bIndex] = (byte)time.charAt(j);
					bIndex++;
				}
			    bFile[bIndex] = (byte)'-';
			    bIndex++;
			    
			 // ---------------Added for time sync----------------	
			    //Store time to wait for the quiz initiation
			  
			    for (int j = 0; j < mSecondesToWait.length(); j++) {
					bFile[bIndex] = (byte)mSecondesToWait.charAt(j);
					bIndex++;
				}
			    bFile[bIndex] = (byte)'-';
			    bIndex++;
				// --------------------------------------------------
			    //Store quiz file size
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
				for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
					 ServerBT.mConnThreads.get(i).getMmOutStream().write(bFile);
				}
				// ---------------Added for time sync----------------	
				new LectQuizProgressController(activity,quizPeriod,10000);
				// --------------------------------------------------
			}catch(Exception e){
	        	e.printStackTrace();
	        }
			
//			new LectQuizProgressController(activity);
		}
		
	}
}
