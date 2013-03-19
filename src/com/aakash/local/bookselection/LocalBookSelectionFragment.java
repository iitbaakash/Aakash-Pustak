package com.aakash.local.bookselection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.aakash.local.bookitem.BookItem;
import com.aakash.local.bookitem.BookItemAdapter;
import com.aakash.local.bookitem.LocalDatabase;
import com.aakash.pustak.R;
import com.aakash.pustak.ReadBook;

@SuppressLint("UseSparseArrays")
public class LocalBookSelectionFragment extends Fragment {
	GridView gridView;
	LocalDatabase db;
	String title;
	List<BookItem> books;
	BookItemAdapter bAdapter;
	Map<Integer, BookItem> menuMap = new HashMap<Integer, BookItem>();
	public static Fragment newInstance(String title) {
		LocalBookSelectionFragment f = new LocalBookSelectionFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_swipeytab, null);
		gridView = (GridView) root.findViewById(R.id.bookGrid);
		
		title = getArguments().getString("title");
		
		db = new LocalDatabase(getActivity());
		
		books = db.getBookItemByClass(title.toLowerCase());
		int i = 0;
		for(BookItem x : books) menuMap.put(i++, x);
		bAdapter = new BookItemAdapter(root.getContext(), books); 
		gridView.setAdapter(bAdapter);
		
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				TextView t = (TextView) v.findViewById(R.id.bookTitle);
				String bookText = (String) t.getText();
				
				TextView sub = (TextView) v.findViewById(R.id.bookSubject);
				String subText = (String) sub.getText();

				Intent read = new Intent(v.getContext(), ReadBook.class);
				read.putExtra("book", bookText);
				read.putExtra("class", title.toLowerCase());
				read.putExtra("subject", subText);
				startActivity(read);
			}
		});
		
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            	Log.d("Longclick", "works");
            	registerForContextMenu(gridView);
            	return false;
            }

        });
		return root;
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	if (v.getId() == R.id.bookGrid) { 
    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    	    TextView t = (TextView) v.findViewById(R.id.bookTitle);
			String bookText = (String) t.getText();
			
			int inqr = books.get(info.position).getInQuickRead();
			
    		menu.setHeaderTitle(bookText);
    		String[] menuItems = null; 
    		if(inqr == 0) menuItems = getResources().getStringArray(R.array.qrAddLongmenu);
    		else if(inqr == 1) menuItems = getResources().getStringArray(R.array.qrRemLongmenu);
    		for (int i = 0; i<menuItems.length; i++) {
    			menu.add(Menu.NONE, i, i, menuItems[i]);
			}
    	}
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	    int menuItemIndex = item.getItemId();
	    int inqr = books.get(info.position).getInQuickRead();
	    Log.d("MenuItemId", info.position + "");
	    String[] menuItems = null; 
		if(inqr == 0) menuItems = getResources().getStringArray(R.array.qrAddLongmenu);
		else if(inqr == 1) menuItems = getResources().getStringArray(R.array.qrRemLongmenu);
		String menuItemName = menuItems[menuItemIndex];
		
		Log.d("MenuItem", menuItemName);
		if(menuItemName.equals("Add to Quick Read")) {
			db.addToQuickRead(menuMap.get(info.position));
			Log.d("MenuItem", "AQR");
			
		}
		else if(menuItemName.equals("Delete")) {
			db.removeBook(menuMap.get(info.position));
			Log.d("MenuItem", "Delete");
		}
		else if(menuItemName.equals("Remove from Quick Read")) {
			db.removeFromQuickRead(menuMap.get(info.position));
			Log.d("MenuItem", "RQR");
		}
		
		books = db.getBookItemByClass(title.toLowerCase());
		int i = 0;
		for(BookItem x : books) menuMap.put(i++, x);
		bAdapter.notifyDataSetChanged();
    	return true;
    }
    

}
