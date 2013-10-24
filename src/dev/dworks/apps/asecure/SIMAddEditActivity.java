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

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import dev.dworks.apps.asecure.entity.SecureSIM;
import dev.dworks.apps.asecure.entity.SecureSIM.SecureSIMColumns;
import dev.dworks.apps.asecure.misc.Utils;
import dev.dworks.libs.actionbarplus.SherlockFragmentActivityPlus;

public class SIMAddEditActivity extends SherlockFragmentActivityPlus 
		implements OnClickListener {
	
	private static final int NOTIFY_CONTACT1 = 101;
	private static final int NOTIFY_CONTACT2 = 102;
	private static final int NOTIFY_CONTACT3 = 103;
	private EditText notify_number1;
	private ImageButton notify_number1_select;
	private EditText notify_number2;
	private ImageButton notify_number2_select;
	private EditText notify_number3;
	private ImageButton notify_number3_select;
	private TextView number;
	private TextView operator;
	private String operatorName;
	private CheckBox send_location;
	@SuppressWarnings("unused")
	private CheckBox send_sms;
	private SIMQueryHandler simQueryHandler;
	private String simSerial;
	private Uri uri;

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_sim_add_edit);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initControls();
		initData();
	}
	
	private void initControls() {
		number = ((TextView) findViewById(R.id.number));
		operator = ((TextView) findViewById(R.id.operator));
		notify_number1 = ((EditText) findViewById(R.id.notify_number1));
		notify_number2 = ((EditText) findViewById(R.id.notify_number2));
		notify_number3 = ((EditText) findViewById(R.id.notify_number3));
		notify_number1_select = ((ImageButton) findViewById(R.id.notify_number1_select));
		notify_number2_select = ((ImageButton) findViewById(R.id.notify_number2_select));
		notify_number3_select = ((ImageButton) findViewById(R.id.notify_number3_select));
		send_sms = ((CheckBox) findViewById(R.id.send_sms));
		send_location = ((CheckBox) findViewById(R.id.send_location));
		notify_number1_select.setOnClickListener(this);
		notify_number2_select.setOnClickListener(this);
		notify_number3_select.setOnClickListener(this);
	}

	private void initData() {
		if (getIntent().getAction() == Intent.ACTION_INSERT) {
			Bundle localBundle = getIntent().getExtras();
			operatorName = localBundle.getString(Utils.BUNDLE_OPERATOR);
			simSerial = localBundle.getString(Utils.BUNDLE_SIM_NUMBER);
		} else {
			uri = getIntent().getData();
			simQueryHandler = new SIMQueryHandler(getContentResolver());
			simQueryHandler.startQuery(0, null, uri, null, null, null, null);
		}
		operator.setText(operatorName);
		number.setText(simSerial);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.sim_add_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		case R.id.action_cancel:
			finish();
			break;
		case R.id.action_save:
			save();
			finish();
			break;
		case R.id.action_delete:
			delete();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem localMenuItem = menu.findItem(R.id.action_delete);
		if (localMenuItem != null) {
			localMenuItem.setVisible(uri != null);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	

	private boolean isValid() {
		boolean valid = true;
		String notify1 = notify_number1.getText().toString();
		String notify2 = notify_number2.getText().toString();
		String notify3 = notify_number3.getText().toString();
		String notify_numbers[] = {notify1, notify2, notify3};
		for (String notify : notify_numbers) {
			if(!TextUtils.isEmpty(notify) && !PhoneNumberUtils.isGlobalPhoneNumber(notify)){
				valid = false;
				break;
			}
		}
		return valid;
	}

	private void save() {
		if (!isValid()) {
			return;
		}
		ContentValues localContentValues = new ContentValues();
		localContentValues.put(SecureSIMColumns.NAME, operatorName);
		localContentValues.put(SecureSIMColumns.SIM_NUMBER, simSerial);
		localContentValues.put(SecureSIMColumns.NOTIFY_NUMBER1, notify_number1.getText().toString());
		localContentValues.put(SecureSIMColumns.NOTIFY_NUMBER2, notify_number2.getText().toString());
		localContentValues.put(SecureSIMColumns.NOTIFY_NUMBER3, notify_number3.getText().toString());
		localContentValues.put(SecureSIMColumns.SEND_SMS, true);
		localContentValues.put(SecureSIMColumns.SEND_LOCATION, send_location.isChecked());
		
		if (uri != null) {
			localContentValues.put(SecureSIMColumns.MODIFIED_AT, Calendar.getInstance().getTimeInMillis());
			getContentResolver().update(uri, localContentValues, null, null);
		} else {
			long created = Calendar.getInstance().getTimeInMillis();
			localContentValues.put(SecureSIMColumns.CREATED_AT, created);
			localContentValues.put(SecureSIMColumns.MODIFIED_AT, created);
			getContentResolver().insert(SecureSIM.CONTENT_URI, localContentValues);
		}
	}

	private void delete() {
		int row = getContentResolver().delete(uri, null, null);
		if(row == 1){
			finish();
		}else{
			Toast.makeText(this, "Couldnt delete record/", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		String mobile = null;
		if(arg1 == RESULT_OK){
			Uri uri = arg2.getData();
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst())
					mobile = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
				cursor.close();
			}
		}
		
		switch (arg0) {
		case NOTIFY_CONTACT1:
			notify_number1.setText(mobile);
			break;
		case NOTIFY_CONTACT2:
			notify_number2.setText(mobile);
			break;
		case NOTIFY_CONTACT3:
			notify_number3.setText(mobile);
			break;
		}
	}

	@Override
	public void onClick(View paramView) {
		int request = 0;
		switch (paramView.getId()) {
		case R.id.notify_number1_select:
			request = NOTIFY_CONTACT1;
			break;

		case R.id.notify_number2_select:
			request = NOTIFY_CONTACT2;
			break;

		case R.id.notify_number3_select:
			request = NOTIFY_CONTACT3;
			break;
		}
		
		Intent localIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		localIntent.setType(Phone.CONTENT_TYPE);
		startActivityForResult(localIntent, request);
	}


	@SuppressLint({ "HandlerLeak" })
	private class SIMQueryHandler extends AsyncQueryHandler {

		public SIMQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			super.onQueryComplete(token, cookie, cursor);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					operatorName = cursor.getString(cursor.getColumnIndex(SecureSIMColumns.NAME));
					simSerial = cursor.getString(cursor.getColumnIndex(SecureSIMColumns.SIM_NUMBER));
					operator.setText(operatorName);
					number.setText(simSerial);
					notify_number1.setText(cursor.getString(cursor.getColumnIndex(SecureSIMColumns.NOTIFY_NUMBER1)));
					notify_number2.setText(cursor.getString(cursor.getColumnIndex(SecureSIMColumns.NOTIFY_NUMBER2)));
					notify_number3.setText(cursor.getString(cursor.getColumnIndex(SecureSIMColumns.NOTIFY_NUMBER3)));
					boolean checked = cursor.getInt(cursor.getColumnIndex(SecureSIMColumns.SEND_LOCATION)) == 1;
					send_location.setChecked(checked);
				}
				cursor.close();
			}
		}
	}
}