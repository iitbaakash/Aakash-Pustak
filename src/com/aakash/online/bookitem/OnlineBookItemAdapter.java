package com.aakash.online.bookitem;

import java.util.List;

import nl.siegmann.epublib.domain.Book;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aakash.pustak.R;

public class OnlineBookItemAdapter extends BaseAdapter {
	//private String PUSTHAK_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pustak/";
	Book book;
	private Context context;
	private final OnlineBookItem[] bookList;
	public OnlineBookItemAdapter(Context con, List<OnlineBookItem> books) {
		this.context = con;
		this.bookList = books.toArray(new OnlineBookItem[books.size()]);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = new View(context);
			convertView = inflater.inflate(R.layout.bookitem, parent, false);
			
			TextView bookTitle = (TextView) convertView.findViewById(R.id.bookTitle);
			TextView bookSubject = (TextView) convertView.findViewById(R.id.bookSubject);
			bookTitle.setText(capitalize(bookList[position].getTitle()));
			bookSubject.setText(capitalize(bookList[position].getSubject()));
			
			//ImageView cover = (ImageView) convertView.findViewById(R.id.bookCover);
			//File filePath = new File(PUSTHAK_PATH + bookList[position].getClassName() + "/" + bookList[position].getSubject() + "/"+ bookList[position].getTitle() + "/" + bookList[position].getTitle() + ".epub");
			//if(filePath.exists()) Log.d("File exists", "true");
			//InputStream epubInputStream = new BufferedInputStream(new FileInputStream(filePath));
			//book = (new EpubReader()).readEpub(epubInputStream);
			//new LoadImages(cover).execute(book);
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
	
	/*private byte[] resizeImage( byte[] input ) {
		
		if ( input == null ) {
			return null;
		}
				
		Bitmap bitmapOrg = BitmapFactory.decodeByteArray(input, 0, input.length);

		if ( bitmapOrg == null ) {
			return null;
		}
		
		int height = bitmapOrg.getHeight();
		int width = bitmapOrg.getWidth();
		int newHeight = 60;

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

	}*/
	
	/*private class LoadImages extends AsyncTask<Book, Void, Bitmap> {
		ImageView coverI;
		public LoadImages(ImageView v) {
			this.coverI = v;
		}
		@Override
		protected Bitmap doInBackground(Book... books) {
			Bitmap bit = null;
			if ( book.getCoverImage() != null ) {
				byte[] coverImg = null;
				try {
					coverImg = resizeImage(book.getCoverImage().getData());
				} catch (IOException e) {
					e.printStackTrace();
				}
				bit = BitmapFactory.decodeByteArray(coverImg, 0, coverImg.length);
			}
			return bit;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			this.coverI.setImageBitmap(result);		
		}
	}*/

}
