package com.example.onlinequizchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.example.onlinequizchecker.LectStudentRegListController.itemListener;
import com.example.onlinequizchecker.LectStudentRegListController.quizSelectionBtnListener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LectQuizSelectionController {
	private MainActivity activity;
	private ArrayList<String> quizzes;
	private Button chooseQuizBtn;
	private Button viewQuizBtn;
	private ListView listview;
	private String course;
	private int selectedIndex=0;
	private ArrayAdapter<String> adapter;
	public LectQuizSelectionController(MainActivity activity,String course)
	{
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_quizselectionview);
		chooseQuizBtn = (Button)this.activity.findViewById(R.id.chooseQuizBtn);
		chooseQuizBtn.setOnClickListener(new chooseQuizBtnListener());
		viewQuizBtn = (Button)this.activity.findViewById(R.id.viewQuizBtn);
		viewQuizBtn.setOnClickListener(new viewQuizBtnListener());
		listview = (ListView)this.activity.findViewById(R.id.quizzesListView);
		this.course = course;
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		quizzes = getQuizzesListFromDB();
		populateList(quizzes);
	}

	private ArrayList<String> getQuizzesListFromDB() {
		// TODO Auto-generated method stub
		ArrayList<String> quizzes = new ArrayList<>();
		File filelist;
		filelist = activity.getFilelist();
		try {
			File quizzesFolder = new File(filelist.getCanonicalPath()+"/"+course+"/Quizzes");
			if(quizzesFolder.list()!=null)
			for(int i=0;i<quizzesFolder.list().length;i++)
			{
				quizzes.add(quizzesFolder.list()[i]);
			}
			else{
				//Do something to notice the user that his quizzes folder is empty
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return quizzes;
	}

	private void populateList(ArrayList<String> quizzes) {
		// TODO Auto-generated method stub
		listview.setChoiceMode(listview.CHOICE_MODE_SINGLE);
		listview.setTextFilterEnabled(true);
		adapter = new ArrayAdapter<String>(this.activity,
				android.R.layout.simple_list_item_single_choice, quizzes);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new itemListener());
	}
	class itemListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			selectedIndex = position;	
		}

	}
	class chooseQuizBtnListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
		
			// Send quiz to students
			try {
				String zipFile = activity.getFilelist().getCanonicalPath()+"/"+course+"/Quizzes/"+
						adapter.getItem(selectedIndex) + "/Form/" + adapter.getItem(selectedIndex)+".zip";
		        String srcDir = activity.getFilelist().getCanonicalPath()+"/"+course+"/Quizzes/"+
						adapter.getItem(selectedIndex) + "/Form";     
				byte[] buffer = new byte[1024];
				FileOutputStream fos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos);
				File dir = new File(srcDir);
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (!files[i].getName().equals("quiz.html")&&
							!files[i].getName().startsWith("Question"))
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
		        
		        File file = new File(zipFile);
		        
		        byte[] bFile = new byte[(int) file.length()];
		        
		            //convert file into array of bytes
			    fileInputStream = new FileInputStream(file);
			    fileInputStream.read(bFile);
			    fileInputStream.close();
			    file.delete();
			    for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
					ServerBT.mConnThreads.get(i).getMmOutStream().
					write(toByteArray(adapter.getItem(selectedIndex)));
				}	
			    
//			    for (int i = 0; i < ServerBT.mConnThreads.size(); i++) {
//					ServerBT.mConnThreads.get(i).getMmOutStream().write(bFile);
//				}	
			    
			   
			}catch(Exception e){
	        	e.printStackTrace();
	        }
		}
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
    
	class viewQuizBtnListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
			new LectViewQuizController(activity, course, adapter.getItem(selectedIndex));
		}
		
	}
}
