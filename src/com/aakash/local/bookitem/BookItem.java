package com.aakash.local.bookitem;

public class BookItem {
	int _inquickread, _classNo, _id;
	String _title, _class, _subject;
	
	public BookItem() {
	}
	
	public BookItem(String title, String classN, String subject, int classNo, int quickread) {
		this._title = title;
		this._class = classN;
		this._subject = subject;
		this._classNo = classNo;
		this._inquickread = quickread;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public void setTitle(String title) {
		this._title = title;
	}
	
	public void setClassName(String classN) {
		this._class = classN;
	}
	
	public void setSubject(String subject) {
		this._subject = subject;
	}
	
	public void setClassNo(int no) {
		this._classNo = no;
	}
	
	public void setInQuickRead(int qr) {
		this._inquickread = qr;
	}
	
	
	public int getId() {
		return this._id;
	}
	
	public Integer getInteger() {
		return this._id;
	}
	
	public String getTitle() {
		return this._title;
	}
	
	public String getClassName() {
		return this._class;
	}
	
	public String getSubject() {
		return this._subject;
	}
	
	public int getClassNo() {
		return this._classNo;
	}
	
	public int getInQuickRead() {
		return this._inquickread;
	}
	
}
