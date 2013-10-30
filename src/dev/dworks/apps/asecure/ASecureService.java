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

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import dev.dworks.apps.asecure.entity.SecureSIM;
import dev.dworks.apps.asecure.entity.SecureSIM.SecureSIMColumns;
import dev.dworks.apps.asecure.misc.Utils;

public class ASecureService extends IntentService {

    private static final String[] PROJECTION = new String[]{
        SecureSIMColumns._ID,
        SecureSIMColumns.NAME,
        SecureSIMColumns.SIM_NUMBER,
        SecureSIMColumns.MOBILE_NUMBER,
        SecureSIMColumns.NOTIFY_NUMBER1,
        SecureSIMColumns.NOTIFY_NUMBER2,
        SecureSIMColumns.NOTIFY_NUMBER3,
        SecureSIMColumns.CREATED_AT
    };

    public ASecureService() {
		super("SecureService");
	}
    
	public ASecureService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		SmsManager smsManager = SmsManager.getDefault();

		Bundle bundle = Utils.getMessageDetails(getApplicationContext());
		String simSerial =  bundle.getString(Utils.BUNDLE_SIM_NUMBER);
        String messageToSend = bundle.getString(Utils.BUNDLE_MESSAGE);
		//TODO: add lot long
        List<String> messages = smsManager.divideMessage(messageToSend);

		if(TextUtils.isEmpty(simSerial)){
        	return;
        }
		Cursor cursor = getContentResolver().query(
				SecureSIM.CONTENT_URI,
				PROJECTION,
				SecureSIMColumns.SIM_NUMBER + " = ? ",
				new String[]{simSerial},
				null);

		Cursor cursorSIM = getContentResolver().query(
				SecureSIM.CONTENT_URI,
				PROJECTION,
				null,
				null,
				null);
		if(null != cursor && null != cursorSIM){
			if(cursor.getCount() == 0){
				while (cursorSIM.moveToNext()) {
					String notify_number1 = cursorSIM.getString(cursorSIM.getColumnIndex(SecureSIMColumns.NOTIFY_NUMBER1));
					String notify_number2 = cursorSIM.getString(cursorSIM.getColumnIndex(SecureSIMColumns.NOTIFY_NUMBER2));
					String notify_number3 = cursorSIM.getString(cursorSIM.getColumnIndex(SecureSIMColumns.NOTIFY_NUMBER3));
					String notify_numbers[] = {notify_number1, notify_number2, notify_number3};
					for (String notify_number : notify_numbers) {
						if(!TextUtils.isEmpty(notify_number)){
					        for (String message : messages) {
					        	smsManager.sendTextMessage(notify_number, null, message, null, null);
					        }					
						}	
					}	
				}
			}
			cursorSIM.close();
		}
	}
}