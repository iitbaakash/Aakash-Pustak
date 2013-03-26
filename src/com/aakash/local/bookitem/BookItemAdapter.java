package com.aakash.local.bookitem;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aakash.pustak.R;

public class BookItemAdapter extends BaseAdapter {
	
	private String PUSTHAK_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pustak/";
	//private int THUMBNAIL_HEIGHT = 150;
	private Context context;
	private final BookItem[] bookList;
	ProgressDialog pDialog;
	TextView bookTitle, bookSubject;
	ImageView cover;
	
	public BookItemAdapter(Context con, List<BookItem> books) {
		this.context = con;
		this.bookList = books.toArray(new BookItem[books.size()]);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = new View(context);
			convertView = inflater.inflate(R.layout.bookitem, parent, false);
			
			bookTitle = (TextView) convertView.findViewById(R.id.bookTitle);
			bookSubject = (TextView) convertView.findViewById(R.id.bookSubject);
			cover = (ImageView) convertView.findViewById(R.id.bookCover);
			
			bookTitle.setText(capitalize(bookList[position].getTitle()));
			bookSubject.setText(capitalize(bookList[position].getSubject()));
			
			
			String filePath = PUSTHAK_PATH + bookList[position].getClassName() + "/" + bookList[position].getSubject() + "/"+ bookList[position].getTitle() + "/" + bookList[position].getTitle() + ".epub";
			Log.d("File path", filePath);
			cover.setImageDrawable(context.getResources().getDrawable(R.drawable.cover));
			
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Loading");
			pDialog.setCancelable(false);
		}
		return convertView;
	}
 
	@Override
	public int getCount() {
		return bookList.length;
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
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
	
	
	
	class LoadBookLists extends AsyncTask<BookItem, Void, Void> {
		@Override
		protected void onPreExecute() {
			//pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(BookItem... params) {

			
			/*Log.d("Pass", "1");
			try {
				FileInputStream epubInputStream = new FileInputStream(filePath); 
				EpubReader epubReader = new EpubReader();
				Book importedBook = epubReader.readEpub(epubInputStream);
				Log.d("Pass", "2");
				byte[] thumbNail = null;
		    	if (importedBook.getCoverImage() != null ) {
		    		Log.d("Pass", "3");
		    		thumbNail = resizeImage(importedBook.getCoverImage().getData());
		    		Log.d("Pass", "4");
		    		importedBook.getCoverImage().close();
		    		cover.setImageBitmap( BitmapFactory.decodeByteArray(thumbNail, 0, thumbNail.length));
		    		Log.d("Pass", "5");
		    	}
		    	else {*/
		    		cover.setImageDrawable(context.getResources().getDrawable(R.drawable.cover));
		    		Log.d("Pass", "6");
		    		/*	}
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			//pDialog.dismiss();
			Log.d("Pass", "7");
			super.onPostExecute(result);
		}
		
	}
	
	
	private byte[] resizeImage( byte[] input ) {
		
		if ( input == null ) {
			return null;
		}
				
		Bitmap bitmapOrg = BitmapFactory.decodeByteArray(input, 0, input.length);

		if ( bitmapOrg == null ) {
			return null;
		}
		
		int height = bitmapOrg.getHeight();
		int width = bitmapOrg.getWidth();
		int newHeight = 150;

		float scaleHeight = ((float) newHeight) / height;

		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleHeight, scaleHeight);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
				width, height, matrix, true);

		bitmapOrg.recycle();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		resizedBitmap.compress(CompressFormat.PNG, 0, bos);            

		resizedBitmap.recycle();

		return bos.toByteArray();            
	}

}
