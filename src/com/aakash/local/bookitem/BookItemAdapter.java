package com.aakash.local.bookitem;

import java.util.List;

import nl.siegmann.epublib.domain.Book;
import android.content.Context;
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
	Book book;
	private Context context;
	private final BookItem[] bookList;
	
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
			
			TextView bookTitle = (TextView) convertView.findViewById(R.id.bookTitle);
			TextView bookSubject = (TextView) convertView.findViewById(R.id.bookSubject);
			bookTitle.setText(capitalize(bookList[position].getTitle()));
			bookSubject.setText(capitalize(bookList[position].getSubject()));
			
			ImageView cover = (ImageView) convertView.findViewById(R.id.bookCover);
			String filePath = PUSTHAK_PATH + bookList[position].getClassName() + "/" + bookList[position].getSubject() + "/"+ bookList[position].getTitle() + "/" + bookList[position].getTitle() + ".epub";
			Log.d("File path", filePath);
			//EpubReader epubReader = new EpubReader();
			
			//try {
				//Book importedBook = epubReader.readEpubLazy(filePath, "UTF-8", Arrays.asList(MediatypeService.mediatypes));
				//byte[] thumbNail = null;
		    		/*if ( importedBook.getCoverImage() != null ) {    			
		    			thumbNail = resizeImage(book.getCoverImage().getData());
		    			importedBook.getCoverImage().close();
		    			cover.setImageBitmap( BitmapFactory.decodeByteArray(thumbNail, 0, thumbNail.length));
		    		}
		    		else {*/
		    			cover.setImageDrawable(context.getResources().getDrawable(R.drawable.cover));
		    		//}
			/*} catch (IOException e) {
				e.printStackTrace();
			}*/
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
		int newHeight = THUMBNAIL_HEIGHT;

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

}
