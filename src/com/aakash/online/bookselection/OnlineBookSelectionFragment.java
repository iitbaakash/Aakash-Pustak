package com.aakash.online.bookselection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.aakash.local.bookitem.LocalDatabase;
import com.aakash.online.bookitem.OnlineBookItem;
import com.aakash.online.bookitem.OnlineBookItemAdapter;
import com.aakash.online.bookitem.OnlineDatabase;
import com.aakash.pustak.R;

public class OnlineBookSelectionFragment extends Fragment {
	GridView gridView;
	private String PUSTHAK_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pustak/";
	OnlineDatabase db;
	String title;
	ProgressDialog downloadDialog;
	Map<String, OnlineBookItem> obMap = new HashMap<String, OnlineBookItem>();
	
	public static Fragment newInstance(String title) {
		OnlineBookSelectionFragment f = new OnlineBookSelectionFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_swipeytab, null);
		gridView = (GridView) root.findViewById(R.id.bookGrid);
		
		
		downloadDialog = new ProgressDialog(root.getContext());
        downloadDialog.setCancelable(false);
        downloadDialog.setIndeterminate(false);
        downloadDialog.setMax(100);
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setMessage("Downloading...");
		
		title = getArguments().getString("title");
		
		db = new OnlineDatabase(getActivity());
		
		List<OnlineBookItem> books = db.getBookItemByClass(title.toLowerCase());
		Log.d("title", title.toLowerCase());
		for(OnlineBookItem i : books) obMap.put(i.getTitle(), i);
		
		OnlineBookItemAdapter bAdapter = new OnlineBookItemAdapter(root.getContext(), books); 
		gridView.setAdapter(bAdapter);
		
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				TextView t = (TextView) v.findViewById(R.id.bookTitle);
				String bookText = (String) t.getText();
				
				if(db.checkBookExists(obMap.get(bookText).getId())) {
					Toast.makeText(v.getContext(), "Book already downloaded!", Toast.LENGTH_SHORT).show();
				}
				else {
					new DownloadTask(v, obMap.get(bookText)).execute();
				}

				/*Intent read = new Intent(v.getContext(), ReadBook.class);
				read.putExtra("book", bookText);
				read.putExtra("class", title.toLowerCase());
				read.putExtra("subject", subText);
				startActivity(read);*/
			}
		});
		return root;
	}
	
	class DownloadTask extends AsyncTask<Void, Integer, Void> {
		String zipPath = null;
		String zip = null;
		OnlineBookItem down = new OnlineBookItem();
		View display;
		LocalDatabase lDB = new LocalDatabase(getActivity());
		public DownloadTask(View v, OnlineBookItem b) {
			display = v;		
			down.setClassNo(b.getClassNo());
			down.setRepo(b.getRepo());
			down.setSubject(b.getSubject());
			down.setTitle(b.getTitle());
			down.setClassName(b.getClassName());
			down.setId(b.getId());
			downloadDialog.show();
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
		try {
			URL url = new URL(down.getRepo());
			Log.d("repo", down.getRepo());
			URLConnection conexion = url.openConnection();
			conexion.connect();
			int lenghtOfFile = conexion.getContentLength();

			InputStream input = new BufferedInputStream(url.openStream(), 8192);
			File outputPath = new File(PUSTHAK_PATH + down.getClassName() + "/" + down.getSubject() + "/" + down.getTitle() +"/");
			if(!outputPath.exists()) outputPath.mkdirs();
			FileOutputStream output = new FileOutputStream(PUSTHAK_PATH + down.getClassName() + "/" + down.getSubject() + "/" + down.getTitle() +"/" + down.getTitle() + ".epub");
			
			 byte[] buffer = new byte[1024];
             int len1 = 0;
             long total = 0;
             int percent = 0;
             while ((len1 = input.read(buffer)) > 0) {
                 total += len1;
                 percent = (int)((total*100)/lenghtOfFile);
                 if(percent%5 == 0) publishProgress(percent);
                 output.write(buffer, 0, len1);
                 Log.d("percent", total + "");
             }
             Log.d("percent", total + "");
			output.flush();
			output.close();
			input.close();
		} 
		catch (Exception e) {
			Log.e("AsyncTaskDownload", e.getMessage());
		}
		return null;
		}
		protected void onProgressUpdate(Integer ... progress) {
			downloadDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Void unused) {
			downloadDialog.dismiss();
			lDB.addLocalBook(down);
			Toast.makeText(display.getContext(), "Downloaded successfully!", Toast.LENGTH_SHORT).show();
			
		}
	}
}


