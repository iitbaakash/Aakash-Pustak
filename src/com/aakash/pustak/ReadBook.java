package com.aakash.pustak;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.slidingmenu.lib.app.SlidingActivity;

public class ReadBook extends SlidingActivity {
	
	Book book;
	private String PUSTHAK_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pustak/";
	String booktitle, bookclass, booksubject;
	WebView wv;
	String abspath;
	ProgressDialog loadingBookDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		booktitle = getIntent().getStringExtra("book");
		bookclass = getIntent().getStringExtra("class");
		booksubject = getIntent().getStringExtra("subject");
		
		loadingBookDialog = new ProgressDialog(this);
		loadingBookDialog.setCancelable(false);
		loadingBookDialog.setIndeterminate(false);
		loadingBookDialog.setMessage("Loading Book...");
		setTitle(booktitle);
		Log.d("Book Title", booktitle);
		Log.d("Book Class", bookclass);
		Log.d("Book Subject", booksubject);
		setContentView(R.layout.activity_read_book);
		
		setBehindContentView(R.layout.toclists);
		
		this.setSlidingActionBarEnabled(true);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.actionbar_home_width);
		getSlidingMenu().setBehindScrollScale(0.25f);
		getSlidingMenu().setActivated(true);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		updateContentView();
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		toggle();
		super.onPostCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.read_book, menu);
		return true;
	}
	
	public static class TocEntry {
		private String title;
		private String href;
		
		public TocEntry(String title, String href) {
			this.title = title;
			this.href = href;
		}
		
		public String getHref() {
			return href;
		}
		
		public String getTitle() {
			return title;
		}
	}
	
	public List<TocEntry> getTableOfContents() {
		if ( this.book == null ) {
			return null;
		}
		List<TocEntry> result = new ArrayList<TocEntry>();
		flatten( book.getTableOfContents().getTocReferences(), result, 0 );
		return result;
	}
	
	private void flatten( List<TOCReference> refs, List<TocEntry> entries, int level ) {
		if ( refs == null || refs.isEmpty() ) {
			return;
		}
		for ( TOCReference ref: refs ) {
			String title = "";
			for ( int i = 0; i < level; i ++ ) {
				title += "-";
			}			
			title += ref.getTitle();
			if ( ref.getResource() != null ) {
				entries.add( new TocEntry(title, ref.getCompleteHref() ));
			}
			flatten( ref.getChildren(), entries, level + 1 );
		}
	}
	
	public void updateContentView() {
		try {
			wv = (WebView) findViewById(R.id.bookContentWeb);
			abspath = PUSTHAK_PATH+bookclass+"/"+booksubject+"/"+booktitle+"/";
		   	File filePath = new File(abspath+"/"+booktitle+".epub");   
		   	InputStream epubInputStream = new BufferedInputStream(new FileInputStream(filePath));
		   	book = (new EpubReader()).readEpub(epubInputStream);
		   	DownloadResource(abspath);
		   	
		   	final List<TocEntry> tocList = getTableOfContents();
	    	if ( tocList == null || tocList.isEmpty() ) {
	    		return;
	    	}
	    	final String[] items = new String[ tocList.size() ];
	    	for ( int i=0; i < items.length; i++ ) {
	    		items[i] = tocList.get(i).getTitle();
	    		Log.d("Toc Item", items[i]);
	    	}	    	

	    	ListView tocView = (ListView) findViewById(R.id.toclists);
	    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, android.R.id.text1, items);
	    	tocView.setAdapter(adapter);
	    	
	    	tocView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				    Log.d("Pressed", position + "");
				    toggle();
					new LoadEpubChapter().execute(position);
				}
			});
		   			
	    	String html = "<html><head><title>"+book.getTitle()+"</title><body><img src='../"+ book.getCoverImage().getHref() +"'></body></html>";
		   	if(new File(abspath + book.getCoverImage().getHref()).exists()) Log.d("File", "true");
		  
		   	WebSettings settings = wv.getSettings();
		   	settings.setJavaScriptEnabled(true);
		   	settings.setBuiltInZoomControls(true);
		   	
		   	wv.loadDataWithBaseURL("file://" + abspath + "Text/", html, "text/html", "UTF-8", null);

		 }
		 catch (IOException e) {
		   	Log.e("epublib", e.getMessage());
		 }
	}
	
	private void DownloadResource(String directory) {
		 try {
			 Resources rst = book.getResources();
			 Collection<Resource> clrst = rst.getAll();
			 Iterator<Resource> itr = clrst.iterator();
			 while (itr.hasNext()) {
				 Resource rs = itr.next();
				 if ((rs.getMediaType() == MediatypeService.JPG) || (rs.getMediaType() == MediatypeService.PNG) || (rs.getMediaType() == MediatypeService.GIF) || rs.getMediaType() == MediatypeService.CSS || rs.getMediaType() == MediatypeService.TTF || rs.getMediaType() == MediatypeService.OPENTYPE || rs.getMediaType() == MediatypeService.WOFF)  {
					 String res = rs.getHref();
					 File paths = null;
					 if(res.contains("Images/")) { 
						paths = new File(directory+File.separator+"Images");
					 	paths.mkdirs();
					 }
					 if(res.contains("Fonts/")) {
						 paths = new File(directory+File.separator+"Fonts");
					 	 paths.mkdirs();
					 }
					 if(res.contains("Styles/")) {
					 paths = new File(directory+File.separator+"Styles");
					 paths.mkdirs();
					 }
					 File oppath1 = new File(directory+File.separator+res);
					 
					 if(oppath1.exists() == false) {
						 oppath1.createNewFile();
						 FileOutputStream fos1 = new FileOutputStream(oppath1);
						 fos1.write(rs.getData());
					 	 fos1.close();
					 }
				 } 
			 }
		 } 
		 catch (IOException e) {
			 Log.e("error", e.getMessage());
		 }
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class LoadEpubChapter extends AsyncTask<Integer, Void, Void> {
		
		String html = null, line;
		StringBuilder string = new StringBuilder();
		Resource r;
		@Override
		protected void onPreExecute() {
			loadingBookDialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Integer... args) {
			r = book.getSpine().getResource(args[0]);
			try {
		        InputStream is = r.getInputStream();
		        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		        try {
		            while ((line = reader.readLine()) != null) {
		                html =   string.append(line + "\n").toString();
		            }
		        } 
		        catch (IOException e) {
		        }

		    } 
			catch (IOException e) {
		    }
			Log.d("ChapterContent", html);
			wv.loadDataWithBaseURL("file://" + abspath + "Text/", html, "text/html", "UTF-8", null);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			loadingBookDialog.dismiss();
			super.onPostExecute(result);
		}
	}

}
