package com.aakash.online.bookitem;

public class OnlineBookItem {
	int _classNo, _id;
	String _title, _class, _subject, _repo;
	
	public OnlineBookItem() {
	}
	
	public OnlineBookItem(int id, String title, String classN, String subject, int classNo, String repo) {
		this._id = id;
		this._title = title;
		this._class = classN;
		this._subject = subject;
		this._classNo = classNo;
		this._repo = repo;
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
	
	public void setRepo(String repo) {
		this._repo = repo;
	}
	
	
	public int getId() {
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
	
	public String getRepo() {
		return this._repo;
	}
	
}
