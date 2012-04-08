package com.valleytg.oasvnlite.android.util;

import android.database.Cursor;

import com.valleytg.oasvnlite.android.application.OASVNApplication;
import com.valleytg.oasvnlite.android.model.OASVNModelLocalDB;

public class Settings extends OASVNModelLocalDB {
	private String rootFolder = "";
	
	public Settings() {
		super("setting");
		
		//Settings id will always be 1
		this.setLocalDBId(1);

	}
	
	private static class SettingsHolder { 
        public static final Settings instance = new Settings();
	}
	
	public static Settings getInstance() {
		return SettingsHolder.instance;
	}
	
	@Override
	public void saveToLocalDB(OASVNApplication app) {
		values.put("root_folder", this.getRootFolder());
		
		super.saveToLocalDB(app);
	}

	@Override
	public void setData(Cursor results) {
		try {
			this.setRootFolder(results.getString(results.getColumnIndex("root_folder")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.setData(results);
	}
	
	public Settings(String rootFolder) {
		this.setRootFolder(rootFolder);
	}

	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

	public String getRootFolder() {
		return rootFolder;
	}

	
}
