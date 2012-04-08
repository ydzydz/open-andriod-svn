package com.valleytg.oasvn.android.ui.activity;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.model.Connection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddRepository extends Activity {
	
	/**
	 * Application
	 */
	OASVNApplication app;

	Button btnSave;
	Button btnBack;
	EditText name;
	EditText url;
	EditText username;
	EditText password;
	EditText folder;
	
	/**
	 * working connection
	 */
	Connection thisConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addrepos);
	
		// get the application
        this.app = (OASVNApplication)getApplication();
		
		btnSave = (Button) findViewById(R.id.add_save);
        btnBack = (Button) findViewById(R.id.add_cancel);
        
        name = (EditText) findViewById(R.id.add_name);
		url = (EditText) findViewById(R.id.add_url);
		username = (EditText) findViewById(R.id.add_username);
		password = (EditText) findViewById(R.id.add_password);
		folder = (EditText) findViewById(R.id.add_folder);
        
        // check to see if we are editing a cannection
        if(app.getCurrentConnection() != null) {
        	thisConnection = app.getCurrentConnection();
        	
        	name.setText(app.getCurrentConnection().getName());
        	url.setText(app.getCurrentConnection().getTextURL());
        	username.setText(app.getCurrentConnection().getUsername());
        	password.setText(app.getCurrentConnection().getPassword());
        	folder.setText(app.getCurrentConnection().getFolder());
        }
        else {
	        // initialize the connection
	        thisConnection = new Connection();
        }
        
        // button listeners     
        this.btnBack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AddRepository.this.finish();
			}
		});
        
        this.btnSave.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// do the save
				AddRepository.this.save();
				AddRepository.this.finish();
			}
		});
	}

	private void save() {
		
		thisConnection.setName(name.getText().toString());
		thisConnection.setUrl(url.getText().toString());
		thisConnection.setUsername(username.getText().toString());
		thisConnection.setPassword(password.getText().toString());
		thisConnection.setFolder(folder.getText().toString());
		
		thisConnection.saveToLocalDB(this.app);
	}
	
}
