package com.marco.constant;

import cn.bmob.v3.BmobObject;

public class Filename extends BmobObject{
    private String filename;
    private NurseUser user;
    private String realname;
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public NurseUser getUser() {
		return user;
	}
	public void setUser(NurseUser user) {
		this.user = user;
	}
}
