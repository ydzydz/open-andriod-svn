package com.valleytg.oasvn.android.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class LocalBrowse extends ListActivity {

	private List<String> item = null;

	private List<String> path = null;

	private String root="/";

	private TextView myPath;

	 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// needs a layout of its own
		setContentView(R.layout.localbrowse);
		

		myPath = (TextView)findViewById(R.id.path);
		
		
		getDir(root);
	}

	// I was doing something here formating the following two methods, must have gotten them off the web.

	private void getDir(String dirPath) {
		myPath.setText("Location: " + dirPath);
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();
		
		if(!dirPath.equals(root)) {
			item.add(root);
			path.add(root);
			item.add("../");
			path.add(f.getParent());
		}
		
		for(int i=0; i < files.length; i++) {
			File file = files[i];
			path.add(file.getPath());
			if(file.isDirectory()) {
				item.add(file.getName() + "/");
			}
			else {
				item.add(file.getName());
		
			}
		}
		
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.row, item); 
		setListAdapter(fileList);
	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(path.get(position));
		if (file.isDirectory()) {
			if(file.canRead())
				getDir(path.get(position));
			else
			{
				new AlertDialog.Builder(this).setIcon(R.drawable.folder).setTitle("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
			}
		}
		else {
			new AlertDialog.Builder(this).setIcon(R.drawable.folder).setTitle("[" + file.getName() + "]").setPositiveButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
				
				}
			}).show();
		}
	}

}