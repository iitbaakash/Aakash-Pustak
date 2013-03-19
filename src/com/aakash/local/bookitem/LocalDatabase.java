package com.aakash.local.bookitem;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aakash.online.bookitem.OnlineBookItem;

public class LocalDatabase extends SQLiteOpenHelper {
	
	private static final String dbname = "pustakManager";
	private static final int dbversion = 1;

	public LocalDatabase(Context context) {
		super(context, dbname, null, dbversion);	
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(String.format("CREATE TABLE booklist (id INTEGER PRIMARY KEY, bookid INTEGER, book TEXT, class TEXT, classsort INTEGER, subject TEXT, inquickread INTEGER)"));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 db.execSQL("DROP TABLE IF EXISTS booklist");
		 onCreate(db);
	}
	
	public void insert(String query) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(query);
		db.close();
	}
	
	public ArrayList<String> getColumnValues(String table, String columnName, int distinct, String sortColumn, String sort) {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<String> colVal = new ArrayList<String>();
		String query;
		if(distinct == 1) {
			query = "SELECT DISTINCT "+ columnName +" FROM "+ table +" ORDER BY " + sortColumn + " " + sort;
		}
		else {
			query = "SELECT "+ columnName +" FROM "+ table;
		}
		
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		
		if(c.getCount() == 0) return colVal;

		do  {
			String s = c.getString(c.getColumnIndex(columnName));
			colVal.add(capitalize(s));
		} while(c.moveToNext());
		c.close();
		db.close();
		return colVal;
	}
	
	
	public List<BookItem> getBookItemByClass(String classNum) {
        List<BookItem> bookList = new ArrayList<BookItem>();
        String selectQuery = "SELECT  bookid,book,class,subject,classsort,inquickread FROM booklist WHERE class='"+ classNum +"' ORDER BY id DESC";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        if (cursor.moveToFirst()) {
            do {
            	BookItem book = new BookItem();
            	book.setId(cursor.getInt(0));
            	book.setTitle(cursor.getString(1));
            	book.setClassName(cursor.getString(2));
            	book.setSubject(cursor.getString(3));
            	book.setClassNo(cursor.getInt(4));
            	book.setInQuickRead(cursor.getInt(5));
                bookList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
 
        return bookList;
    }
	
	public void addLocalBook(OnlineBookItem b) {
		insert("INSERT INTO booklist (bookid, book, class, subject, classsort, inquickread) VALUES ("+b.getId()+", '"+b.getTitle()+"', '"+b.getClassName()+"', '"+b.getSubject()+"', "+b.getClassNo()+", 0)");
	}
	
	public void addToQuickRead(BookItem b) {
		insert("UPDATE booklist SET inquickread=1 WHERE book='"+b.getTitle()+"'");
		b.setInQuickRead(1);
	}
	
	public void removeFromQuickRead(BookItem b) {
		insert("UPDATE booklist SET inquickread=0 WHERE book='"+b.getTitle()+"'");
		b.setInQuickRead(0);
	}
	
	public void removeBook(BookItem b) {
		insert("DELETE FROM booklist WHERE book='"+b.getTitle()+"'");
	}
	
	public List<BookItem> getQuickReadItems() {
		List<BookItem> qrs = new ArrayList<BookItem>();
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT bookid,book,class,subject FROM booklist WHERE inquickread=1 ORDER BY id DESC";
		
		Cursor cursor = db.rawQuery(query, null);
		 
        if (cursor.moveToFirst()) {
            do {
            	BookItem book = new BookItem();
            	book.setId(cursor.getInt(0));
            	book.setTitle(cursor.getString(1));
            	book.setClassName(cursor.getString(2));
            	book.setSubject(cursor.getString(3));
            	qrs.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
		return qrs;
	}
	
	public boolean checkBookExists(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT book FROM booklist WHERE bookid="+id;
		Cursor cursor = db.rawQuery(query, null);
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
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
