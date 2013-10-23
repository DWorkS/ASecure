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

package dev.dworks.apps.asecure.entity;

import static dev.dworks.apps.asecure.provider.SecureDatabase.COMMA_SEP;
import static dev.dworks.apps.asecure.provider.SecureDatabase.TYPE_BOOLEAN;
import static dev.dworks.apps.asecure.provider.SecureDatabase.TYPE_INTEGER;
import static dev.dworks.apps.asecure.provider.SecureDatabase.TYPE_TEXT;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import dev.dworks.apps.asecure.provider.SecureContract;


public class SecureSIM{
	
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.asecure.securesims";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.asecure.securesim";
    public static final Uri CONTENT_URI =
            SecureContract.BASE_CONTENT_URI.buildUpon().appendPath(SecureContract.PATH_SECURESIMS).build();

    public static final String TABLE_NAME = "securesim";
    
	public static final class SecureSIMColumns implements BaseColumns {
	    public static final String NAME = "name";
	    public static final String SIM_NUMBER = "sim_number";
	    public static final String MOBILE_NUMBER = "mobile_number";
	    public static final String NOTIFY_NUMBER1 = "notify_number1";
	    public static final String NOTIFY_NUMBER2 = "notify_number2";
	    public static final String NOTIFY_NUMBER3 = "notify_number3";
	    public static final String SEND_SMS = "send_sms";
	    public static final String SEND_LOCATION = "send_location";
	    public static final String SEND_MAIL = "send_mail";
	    public static final String CREATED_AT = "created_at";
	    public static final String MODIFIED_AT = "modified_at";
	}
	
    /** SQL statement to create "securesim" table. */
    public static final String SQL_CREATE_SECURESIMS =
            "CREATE TABLE IF NOT EXISTS " + SecureSIM.TABLE_NAME + " (" +
                    SecureSIMColumns._ID + " INTEGER PRIMARY KEY," +
                    SecureSIMColumns.NAME + TYPE_TEXT + COMMA_SEP +
                    SecureSIMColumns.SIM_NUMBER + TYPE_TEXT + COMMA_SEP +
                    SecureSIMColumns.MOBILE_NUMBER + TYPE_TEXT + COMMA_SEP +
                    SecureSIMColumns.NOTIFY_NUMBER1 + TYPE_TEXT + COMMA_SEP +
                    SecureSIMColumns.NOTIFY_NUMBER2 + TYPE_TEXT + COMMA_SEP +
                    SecureSIMColumns.NOTIFY_NUMBER3 + TYPE_TEXT + COMMA_SEP +
                    SecureSIMColumns.SEND_SMS + TYPE_BOOLEAN + COMMA_SEP +
                    SecureSIMColumns.SEND_LOCATION + TYPE_BOOLEAN + COMMA_SEP +
                    SecureSIMColumns.SEND_MAIL + TYPE_BOOLEAN + COMMA_SEP +
                    SecureSIMColumns.CREATED_AT + TYPE_INTEGER + COMMA_SEP +
                    SecureSIMColumns.MODIFIED_AT + TYPE_INTEGER +
                    ")";
    
    /** SQL statement to drop "securesim" table. */
    public static final String SQL_DELETE_SECURESIMS =
            "DROP TABLE IF EXISTS " + SecureSIM.TABLE_NAME;

}