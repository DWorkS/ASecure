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

package dev.dworks.apps.asecure.misc;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.view.View;

public class Utils {
	public static final String BUNDLE_OPERATOR = "bundle_operator";
	public static final String BUNDLE_SIM_NUMBER = "bundle_sim_number";
	public static final String BUNDLE_MESSAGE = "bundle_message";
	public static final String SYNC_LAST = "sync_last";
	
	public static Bitmap drawViewOntoBitmap(View paramView) {
		Bitmap localBitmap = Bitmap.createBitmap(paramView.getWidth(),
				paramView.getHeight(), Bitmap.Config.RGB_565);
		paramView.draw(new Canvas(localBitmap));
		return localBitmap;
	}
	
	public static Bundle getMessageDetails(Context context){
		Bundle bundle = new Bundle();
        String messageToSend = "";
        int lac = 0;
        int cid = 0;
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = telephonyManager.getNetworkOperator();
        String operatorName = telephonyManager.getSimOperatorName();
        String simSerial = telephonyManager.getSimSerialNumber();
        String imeiNumber = telephonyManager.getDeviceId();
        GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
        if(null != location){
        	lac = location.getLac();
        	cid = location.getCid();
        }
        messageToSend += "Operator: '"+ operatorName+ "'";
        messageToSend += "\nSIM serial: '"+ simSerial+ "'";
        messageToSend += "\nIMEI number: '"+ imeiNumber+ "'";
        if(lac != 0){
        	messageToSend += "\nLAC:'"+ lac + "'";	
        }
        if(cid != 0){
        	messageToSend += "\nCID: '"+ cid + "'";	
        }
        if (!TextUtils.isEmpty(networkOperator)) {
            String mcc = networkOperator.substring(0, 3);
            String mnc = networkOperator.substring(3);
            messageToSend += "\nMCC: '"+ mcc + "'";
            messageToSend += "\nMNC: '"+ mnc + "'";
        }

        messageToSend +="\nhttp://goo.gl/M6jTxP";
        bundle.putString(Utils.BUNDLE_SIM_NUMBER, simSerial);
        bundle.putString(Utils.BUNDLE_MESSAGE, messageToSend);
        return bundle;
	}
	
	public static boolean openIntent(Context context, Intent intent) {
	    final PackageManager packageManager = context.getPackageManager();
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if(list.size() > 0) {
            return true;
		}
        else{
			return false;
		}	
	}	
	
}