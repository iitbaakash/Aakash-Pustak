package com.aakash.online.bookitem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aakash.local.bookitem.LocalDatabase;

public class OnlineDatabase {
	
	LocalDatabase lDB;
	Context context;
	private static final String BASE_URL = "http://aravindsrivats.com/pustak/actions.php";
	public OnlineDatabase(Context context) {
		this.context = context;
		lDB = new LocalDatabase(context);
    }
	
	public ArrayList<String> getClassList() {
		ArrayList<String> result = new ArrayList<String>();
		try {
			result = new GetClasses().execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public List<OnlineBookItem> getBookItemByClass(String classNum) {
        List<OnlineBookItem> bookList = new ArrayList<OnlineBookItem>();
        //String selectQuery = "SELECT  id,book,class,subject,classsort,inquickread FROM booklist WHERE class='"+ classNum +"' ORDER BY id DESC";
        try {
			bookList = new GetBooksByClass(classNum).execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
        return bookList;
    }
	
	public boolean checkBookExists(int id) {
		if(lDB.checkBookExists(id)) return true;
		else return false;
	}
	
	
	public String capitalize(String input) {
		StringBuilder b = new StringBuilder(input);
		int i = 0;
		do {
		  b.replace(i, i + 1, b.substring(i,i + 1).toUpperCase());
		  i =  b.indexOf(" ", i) + 1;
		} while (i > 0 && i < b.length());
		return b.toString();
	}
	
	
	class GetClasses extends AsyncTask<Void, Void, ArrayList<String>> {
		InputStream is = null;
		String resStr = null;
		ArrayList<String> lists = new ArrayList<String>();
	    protected ArrayList<String> doInBackground(Void... urls) {
	    	try{
	            HttpClient httpclient = new DefaultHttpClient();
	            ArrayList<NameValuePair> postParameters;
	            
	            HttpPost httppost = new HttpPost(BASE_URL);
	            
	            postParameters = new ArrayList<NameValuePair>();
		        postParameters.add(new BasicNameValuePair("id", "1"));
		        
		        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
	            HttpResponse response = httpclient.execute(httppost);
	            HttpEntity entity = response.getEntity();
	            is = entity.getContent();
	    	}
	    	catch(Exception e){
	            Log.e("log_tag", "Error in http connection "+e.toString());
	    	}

	    	try{
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line+",");
	            }
	            is.close();
	            resStr = sb.toString();
	    	}
	    	catch(Exception e){
	    		Log.e("log_tag", "Error converting result "+e.toString());
	    	}
	     
	    	String[] strs = resStr.split(",");
	    	for(String s : strs) {
	    		lists.add(capitalize(s));
	    	}
	    	return lists;
	    }

		@Override
		protected void onPostExecute(ArrayList<String> result) {
		}
	}
	
	class GetBooksByClass extends AsyncTask<Void, Void, ArrayList<OnlineBookItem>> {
		InputStream is = null;
		ArrayList<OnlineBookItem> bookList = new ArrayList<OnlineBookItem>();
		String classNum = null;
		public GetBooksByClass(String s) {
			classNum = s;
		}
		
		protected ArrayList<OnlineBookItem> doInBackground(Void... arg0) {
			try{
		        HttpClient httpclient = new DefaultHttpClient();
		        ArrayList<NameValuePair> postParameters;
		        HttpPost httppost = new HttpPost(BASE_URL);
		        
		        postParameters = new ArrayList<NameValuePair>();
		        postParameters.add(new BasicNameValuePair("id", "2"));
		        postParameters.add(new BasicNameValuePair("classNum", classNum));
		        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
			}
			catch(Exception e){
		        Log.e("log_tag", "Error in http connection "+e.toString());
			}

			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		        		Log.d("Read line", line);
		                String [] sp = line.split(",");
		                OnlineBookItem book = new OnlineBookItem();
		            	book.setId(Integer.parseInt(sp[0]));
		            	book.setTitle(sp[1]);
		            	book.setClassName(sp[2]);
		            	book.setSubject(sp[3]);
		            	Log.d("ClassNumber", Integer.parseInt(sp[4])+"");
		            	book.setClassNo(Integer.parseInt(sp[4]));
		            	book.setRepo(sp[5].replace("<br />", ""));
		                bookList.add(book);
		                
		        }
		        is.close();
			}
			catch(Exception e){
				Log.e("log_tag", "Error converting result "+e.toString());
			}
			return bookList;
		}
		@Override
		protected void onPostExecute(ArrayList<OnlineBookItem> result) {
		}
	}
}
