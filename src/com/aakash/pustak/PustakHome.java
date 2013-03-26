package com.aakash.pustak;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aakash.local.bookitem.BookItem;
import com.aakash.local.bookitem.LocalDatabase;
import com.aakash.local.bookselection.LocalBookSelectionSwipe;
import com.aakash.online.bookitem.OnlineDatabase;
import com.aakash.online.bookselection.OnlineBookSelectionSwipe;
import com.aakash.online.netwoks.DetectConnection;
import com.devsmart.android.ui.HorizontalListView;

public class PustakHome extends Activity {
	private String PUSTHAK_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pustak/";
	private static final String BASE_URL = "http://aravindsrivats.com/pustak/actions.php";
	ImageButton getBooks, openBooks;
	Intent selectClass;
	OnClickListener getListener, openListener;
	LocalDatabase db;
	OnlineDatabase odb;
	DetectConnection detect;
	BookItem[] quickreads = null;
	ProgressDialog loadingDialog;
	HorizontalListView listview;
	Context dialogContext;
	
	ArrayList<String> classList = new ArrayList<String>();
	String[] list;
	
	List<BookItem> qrs;
	@SuppressLint("UseSparseArrays")
	Map<Integer, BookItem> qrMap = new HashMap<Integer, BookItem>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dialogContext = this;
		setContentView(R.layout.activity_pustak_home);
		listview = (HorizontalListView) findViewById(R.id.listview);
		listview.setAdapter(mAdapter);
		
		File checkPustakPath = new File(PUSTHAK_PATH);
		
		if(!checkPustakPath.exists()) checkPustakPath.mkdirs();
		
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setCancelable(false);
		loadingDialog.setIndeterminate(false);
		loadingDialog.setMessage("Loading...");
		
		getBooks = (ImageButton) this.findViewById(R.id.getbooks);
        openBooks = (ImageButton) this.findViewById(R.id.openbooks);
        
        detect = new DetectConnection(this);
        
        openListener = new OnClickListener() {
        	public void onClick(View v) {
        		classList = db.getColumnValues("booklist", "class", 1, "classsort", "ASC");
        		list = classList.toArray(new String[classList.size()]);	
        		if(list.length == 0) {
        			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(dialogContext);
                	Log.d("Alert no local", "Shown");
        			dlgAlert.setMessage("You don't have any books in your local repository. Use the Get Books feature to download books.");
        			dlgAlert.setTitle("No books found");
        			dlgAlert.setPositiveButton("OK", null);
        			dlgAlert.setCancelable(false);
        			dlgAlert.create().show();
        		}
        		else {	
        			selectClass = new Intent(v.getContext(), LocalBookSelectionSwipe.class);
        			startActivity(selectClass);
        		}
        	}
        };
        
        getListener = new OnClickListener() {
        	public void onClick(View v) {
        		Boolean netState = detect.isInternetConnected();
        		Log.d("netstate", netState.toString());
        		if(netState) {
        			new GetClasses().execute();
        		}
        		else {
        			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(dialogContext);
        			Log.d("Alert no online", "Shown");
        			dlgAlert.setMessage("You need to be connected to the internet to access the online repository.");
        			dlgAlert.setTitle("Network Connection Required");
        			dlgAlert.setPositiveButton("OK", null);
        			dlgAlert.setCancelable(false);
        			dlgAlert.create().show();
        		}
        	}
        };
        
        getBooks.setOnClickListener(getListener);
        openBooks.setOnClickListener(openListener);
        
        db = new LocalDatabase(getBaseContext());
        //db.insert("INSERT INTO booklist (bookid, book, class, subject, classsort, inquickread) VALUES (78664, 'Beehive', 'class 9', 'english', 9, 1)");

        qrs = new ArrayList<BookItem>();
        qrs = db.getQuickReadItems();
        quickreads = qrs.toArray(new BookItem[qrs.size()]);
        int x = 0;
        for (BookItem i : qrs) qrMap.put(x++, i);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		qrs = db.getQuickReadItems();
		quickreads = qrs.toArray(new BookItem[qrs.size()]);
        int x = 0;
        for (BookItem i : qrs) qrMap.put(x++, i);
		mAdapter.notifyDataSetChanged();
	}
	
	
	private BaseAdapter mAdapter = new BaseAdapter() {

		private OnClickListener mOnButtonClicked = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageButton img = (ImageButton) v;
				Integer id = img.getId();
				BookItem q = qrMap.get(id);
				Intent read = new Intent(getBaseContext(), ReadBook.class);
				read.putExtra("book", q.getTitle());
				read.putExtra("class", q.getClassName().toLowerCase());
				read.putExtra("subject", q.getSubject());
				startActivity(read);
				Toast.makeText(getBaseContext(), q.getTitle(), Toast.LENGTH_SHORT).show();
				
			}
		};
		

		@Override
		public int getCount() {
			return quickreads.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
			
			ImageButton imageButton = (ImageButton) retval.findViewById(R.id.clickbutton);
			imageButton.setId(quickreads[position].getInteger());
			imageButton.setOnClickListener(mOnButtonClicked);
			return retval;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_pustak_home, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        // Handle item selection
        switch (item.getItemId()) {
        	
        case R.id.menu_openBooks:
        	launchFileManager();
        	return true;        	
        	
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);

    	if ( resultCode == RESULT_OK && data != null) {
    		// obtain the filename
    		Uri fileUri = data.getData();
    		if (fileUri != null) {
    			String filePath = fileUri.getPath();
    			Log.d(" Load book Filepath", filePath);
    			if (filePath != null) {
    				loadNewBook(filePath);  
    			}
    		}
    	}	
	}
	
	public void loadNewBook(String file) {
		Intent readExt = new Intent(this, ReadBook.class);
		readExt.putExtra("file", file);
		readExt.putExtra("load", "external");
		startActivity(readExt);
	}
	
	/*class LoadBookLists extends AsyncTask<Void, Void, Void> {	
		String html = null, line;
		StringBuilder string = new StringBuilder();
		Resource r;
		String[] list;
		@Override
		protected void onPreExecute() {
			loadingDialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... args) {
			
			list = classList.toArray(new String[classList.size()]);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			loadingDialog.dismiss();
			selectClass = new Intent(dialogContext, OnlineBookSelectionSwipe.class);
			selectClass.putExtra("classList", list);
			startActivity(selectClass);
			super.onPostExecute(result);
		}
	}*/
	
	class GetClasses extends AsyncTask<Void, Void, ArrayList<String>> {
		InputStream is = null;
		String resStr = null;
		ArrayList<String> lists = new ArrayList<String>();
		String[] list;
		
		@Override
		protected void onPreExecute() {
			loadingDialog.show();
			super.onPreExecute();
		}
		
		@Override
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
			loadingDialog.dismiss();
			selectClass = new Intent(dialogContext, OnlineBookSelectionSwipe.class);
			list = result.toArray(new String[result.size()]);
			Log.d("home-list", list[0]);
			selectClass.putExtra("classList", list);
			startActivity(selectClass);
		}
	}
	
	private void launchFileManager() {
    	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
        	startActivityForResult(intent, 2);
        } catch (ActivityNotFoundException e) {
        	Toast.makeText(this, "No file manager avaliable.", Toast.LENGTH_SHORT).show();
        }
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
	}