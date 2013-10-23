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

package dev.dworks.apps.asecure.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import dev.dworks.apps.asecure.entity.SecureSIM;
import dev.dworks.apps.asecure.entity.SecureSIM.SecureSIMColumns;

public class SecureProvider extends ContentProvider {
    SecureDatabase mDatabaseHelper;

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = SecureContract.CONTENT_AUTHORITY;

    /**
     * URI ID for route: /securesims
     */
    public static final int SECURESIMS = 1;

    /**
     * URI ID for route: /securesims/{ID}
     */
    public static final int SECURESIMS_ID = 2;
    
    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "securesims", SECURESIMS);
        sUriMatcher.addURI(AUTHORITY, "securesims/*", SECURESIMS_ID);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new SecureDatabase(getContext());
        return true;
    }

    /**
     * Determine the mime type for secure SIM returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SECURESIMS:
                return SecureSIM.CONTENT_TYPE;
            case SECURESIMS_ID:
                return SecureSIM.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     *
     * <p>Currently supports returning all secure SIMs (/securesim) and individual secure SIM by ID
     * (/securesim/{ID}).
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        String id = "";
        Cursor cursor = null;
        Context context = getContext();
        switch (uriMatch) {
            case SECURESIMS_ID:
                // Return a single tax calculation, by ID.
                id = uri.getLastPathSegment();
                builder.where(SecureSIMColumns._ID + "=?", id);
            case SECURESIMS:
                // Return all known tax calculations.
                builder.table(SecureSIM.TABLE_NAME)
                       .where(selection, selectionArgs);
                cursor = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                cursor.setNotificationUri(context.getContentResolver(), uri);
                return cursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Insert a new secure SIM into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        long id;
        switch (match) {
            case SECURESIMS:
                id = db.insertOrThrow(SecureSIM.TABLE_NAME, null, values);
                result = Uri.parse(SecureSIM.CONTENT_URI + "/" + id);
                break;
            case SECURESIMS_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    /**
     * Delete an secure SIM by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case SECURESIMS:
                count = builder.table(SecureSIM.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case SECURESIMS_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(SecureSIM.TABLE_NAME)
                       .where(SecureSIMColumns._ID + "=?", id)
                       .where(selection, selectionArgs)
                       .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * Update an tax calculation in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case SECURESIMS:
                count = builder.table(SecureSIM.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case SECURESIMS_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(SecureSIM.TABLE_NAME)
                        .where(SecureSIMColumns._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }
}