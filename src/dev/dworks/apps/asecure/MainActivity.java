/*
 * Copyright 2013 Hari Krishna Dulipudi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.dworks.apps.asecure;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import dev.dworks.apps.asecure.entity.SecureSIM;
import dev.dworks.apps.asecure.entity.SecureSIM.SecureSIMColumns;
import dev.dworks.apps.asecure.misc.Utils;
import dev.dworks.libs.actionbarplus.app.ActionBarActivityPlus;
import dev.dworks.libs.actionbarplus.widget.SwipeDismissTouchListener;

public class MainActivity extends ActionBarActivityPlus implements
		OnClickListener {

	private static final String[] PROJECTION = {
	  		SecureSIMColumns._ID,
	  		SecureSIMColumns.NAME,
	  		SecureSIMColumns.SIM_NUMBER,
	  		SecureSIMColumns.MOBILE_NUMBER,
	  		SecureSIMColumns.CREATED_AT,
		  };
	private SharedPreferences mSharedPreferences = null;
	private TextView number;
	private TextView operator;
	private String operatorName;
	private String password;
	private ImageButton register;
	private Animation shake;
	private SIMQueryHandler simQueryHandler;
	private String simSerial;
	private Button dismiss;
	private View dismiss_content;
	private boolean showWelcome;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_main);
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		password = mSharedPreferences.getString("LoginPasswordPref", "");
		showWelcome = mSharedPreferences.getBoolean("WelcomePref", true);
		if (paramBundle == null){
			showLoginDialog();
		}
		Fragment localFragment = Fragment.instantiate(this, SecureSIMListFragment.class.getName(), new Bundle());
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, localFragment).commit();
		initControls();
		setNewData();
	}

	private void initControls() {
		shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		number = ((TextView) findViewById(R.id.number));
		operator = ((TextView) findViewById(R.id.operator));
		register = ((ImageButton) findViewById(R.id.register));
		register.setOnClickListener(this);
		dismiss = ((Button) findViewById(R.id.dismiss));
		dismiss_content = findViewById(R.id.dismiss_content);
		dismiss.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss_content.setVisibility(View.GONE);
				mSharedPreferences.edit().putBoolean("WelcomePref", false).commit();
			}
		});
		dismiss_content.setVisibility(showWelcome ? View.VISIBLE : View.GONE);
		dismiss_content.setOnTouchListener(new SwipeDismissTouchListener(dismiss_content, null, 
				new SwipeDismissTouchListener.DismissCallbacks() {
					
					@Override
					public void onDismiss(View view, Object token) {
						dismiss_content.setVisibility(View.GONE);
						mSharedPreferences.edit().putBoolean("WelcomePref", false).commit();
					}
					
					@Override
					public boolean canDismiss(Object token) {
						return true;
					}
				}));
	}

	private void setNewData() {
		TelephonyManager localTelephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Activity.TELEPHONY_SERVICE);
		operatorName = localTelephonyManager.getSimOperatorName();
		simSerial = localTelephonyManager.getSimSerialNumber();
		if (TelephonyManager.SIM_STATE_READY == localTelephonyManager.getSimState()) {
			operator.setText(operatorName);
			number.setText(simSerial);
			simQueryHandler = new SIMQueryHandler(getContentResolver());
			simQueryHandler.startQuery(0, null, 
					SecureSIM.CONTENT_URI,
					PROJECTION,
					SecureSIMColumns.SIM_NUMBER + " = ? ",
					new String[]{simSerial},
					null);
		}
		else{
			operator.setText("Unknown");
			number.setText("-");
			register.setImageResource(0);
			register.setOnClickListener(null);			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about:
			Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
			startActivity(aboutIntent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		setNewData();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		simQueryHandler.cancelOperation(0);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register:
			Intent i = new Intent(this, SIMAddEditActivity.class);
			i.setAction(Intent.ACTION_INSERT);
			Bundle bundle = new Bundle();
			bundle.putString(Utils.BUNDLE_OPERATOR, operatorName);
			bundle.putString(Utils.BUNDLE_SIM_NUMBER, simSerial);
			i.putExtras(bundle);
			startActivity(i);
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private class SIMQueryHandler extends AsyncQueryHandler {

		public SIMQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			super.onQueryComplete(token, cookie, cursor);

			if (null != cursor) {
				if (cursor.moveToFirst()) {
					String operatorName = cursor.getString(cursor.getColumnIndex(SecureSIMColumns.NAME));
					String simSerial = cursor.getString(cursor.getColumnIndex(SecureSIMColumns.SIM_NUMBER));
					operator.setText(operatorName);
					number.setText(simSerial);
					register.setImageResource(R.drawable.ic_registered);
					register.setOnClickListener(null);
					register.setEnabled(false);
				} else {
					register.setOnClickListener(MainActivity.this);
					register.setImageResource(R.drawable.ic_add);
					register.setEnabled(true);
				}
				cursor.close();
			}
		}
	}

	private void showLoginDialog(){
		final SharedPreferences.Editor editor  = mSharedPreferences.edit();
    	final boolean passwordSet = !TextUtils.isEmpty(password);
    	final String setPassword = password;
    			
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View loginView = layoutInflater.inflate(R.layout.dialog_login, null);
        TextView header = (TextView) loginView.findViewById(R.id.login_header);
        final EditText password = (EditText) loginView.findViewById(R.id.password);
        final EditText password_repeat = (EditText) loginView.findViewById(R.id.password_repeat);
        
        final Button login = (Button) loginView.findViewById(R.id.login_button);
        //Button cancel = (Button) loginView.findViewById(R.id.cancel_button);

        if(!passwordSet){
        	password_repeat.setVisibility(View.VISIBLE);
        	header.setVisibility(View.VISIBLE);
        }
        else{
        	password.setVisibility(View.GONE);
        	password_repeat.setVisibility(View.VISIBLE);
        	password_repeat.setHint(R.string.login_pwd);        	
        }
    	header.setText(!passwordSet ? getString(R.string.login_message) : getString(R.string.msg_login));        
        
        final Dialog dialog = new Dialog(this, R.style.Theme_Asecure_DailogLogin);
        dialog.setContentView(loginView);
        dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
        dialog.show();        

        password_repeat.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login_button || actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_DONE) {
                	login.performClick();
                    return true;
                }
				return false;
			}
		});
        login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(passwordSet){
					final String passwordString = password_repeat.getText().toString();
	            	if(TextUtils.isEmpty(passwordString) || passwordString.compareTo(setPassword) != 0){
	            		password_repeat.startAnimation(shake);
	            		password_repeat.setError(getString(R.string.msg_wrong_password));
	                	return;
	            	}
				}
				else{
					final String passwordString = password_repeat.getText().toString();
					final String passwordRepeatString = password_repeat.getText().toString();
	            	if(TextUtils.isEmpty(passwordString)){
	                	password.startAnimation(shake);
	                	password.setError(getString(R.string.msg_pwd_empty));
	                	return;
	            	}
	            	if(TextUtils.isEmpty(passwordRepeatString)){
	            		password_repeat.startAnimation(shake);
	            		password_repeat.setError(getString(R.string.msg_pwd_empty));
	            		return;
	            	}
	            	else if(passwordString.compareTo(passwordRepeatString) != 0){
	            		password_repeat.startAnimation(shake);
	            		password_repeat.setError(getString(R.string.msg_pwd_dont_match));
	            		return;	            		
	            	}
	            	editor.putString("LoginPasswordPref", password.getText().toString());
	            	editor.commit();
				}
        		dialog.dismiss();
			}});

/*        cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				finish();
			}});*/
	}
}