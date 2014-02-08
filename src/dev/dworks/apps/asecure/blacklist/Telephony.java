/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (c) 2012-2013 The Linux Foundation. All rights reserved.
 *
 * Not a Contribution.
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

package dev.dworks.apps.asecure.blacklist;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The Telephony provider contains data related to phone operation.
 *
 * @hide
 */
public final class Telephony {
    private static final String TAG = "Telephony";

    // Constructor
    public Telephony() {
    }


    /**
     * Contains phone numbers that are blacklisted
     * for phone and/or message purposes.
     */
    public static final class Blacklist implements BaseColumns {
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =
                Uri.parse("content://blacklist");

        /**
         * The content:// style URL for filtering this table by number.
         * When using this, make sure the number is correctly encoded
         * when appended to the Uri.
         */
        public static final Uri CONTENT_FILTER_BYNUMBER_URI =
                Uri.parse("content://blacklist/bynumber");

        /**
         * The content:// style URL for filtering this table on phone numbers
         */
        public static final Uri CONTENT_PHONE_URI =
                Uri.parse("content://blacklist/phone");

        /**
         * The content:// style URL for filtering this table on message numbers
         */
        public static final Uri CONTENT_MESSAGE_URI =
                Uri.parse("content://blacklist/message");

        /**
         * Query parameter used to match numbers by regular-expression like
         * matching. Supported are the '*' and the '.' operators.
         * <p>
         * TYPE: boolean
         */
        public static final String REGEX_KEY = "regex";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "number ASC";

        /**
         * The phone number as the user entered it.
         * <P>Type: TEXT</P>
         */
        public static final String NUMBER = "number";

        /**
         * Whether the number contains a regular expression pattern
         * <P>Type: BOOLEAN (read only)</P>
         */
        public static final String IS_REGEX = "is_regex";

        /**
         * Blacklisting mode for phone calls
         * <P>Type: INTEGER (int)</P>
         */
        public static final String PHONE_MODE = "phone";

        /**
         * Blacklisting mode for messages
         * <P>Type: INTEGER (int)</P>
         */
        public static final String MESSAGE_MODE = "message";
    }
}
