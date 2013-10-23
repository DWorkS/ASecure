/* * Copyright 2013 Hari Krishna Dulipudi * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *      http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package dev.dworks.apps.asecure;import java.text.SimpleDateFormat;import java.util.Calendar;import android.content.ContentUris;import android.content.Intent;import android.database.Cursor;import android.os.Bundle;import android.support.v4.app.LoaderManager;import android.support.v4.content.CursorLoader;import android.support.v4.content.Loader;import android.support.v4.widget.SimpleCursorAdapter;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.ListView;import android.widget.TextView;import dev.dworks.apps.asecure.entity.SecureSIM;import dev.dworks.apps.asecure.entity.SecureSIM.SecureSIMColumns;import dev.dworks.libs.actionbarplus.SherlockListPlusFragment;public class SecureSIMListFragment extends SherlockListPlusFragment        implements LoaderManager.LoaderCallbacks<Cursor> {	private static final String TAG = "SecureSIMListFragment";	private SimpleCursorAdapter mAdapter;	private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");	private static final String[] PROJECTION = {	  		SecureSIMColumns._ID,	  		SecureSIMColumns.NAME,	  		SecureSIMColumns.SIM_NUMBER,	  		SecureSIMColumns.MOBILE_NUMBER,	  		SecureSIMColumns.CREATED_AT	  };	private static final int COLUMN_ID = 0;	    private static final String[] FROM_COLUMNS = new String[]{    		SecureSIMColumns.NAME,    		SecureSIMColumns.SIM_NUMBER,    		SecureSIMColumns.CREATED_AT    };    private static final int[] TO_FIELDS = new int[]{            R.id.name,            R.id.category,            R.id.date};        public SecureSIMListFragment() {}    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setHasOptionsMenu(true);    }        @Override    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    	return inflater.inflate(R.layout.fragment_secure_sim_list, container, false);    }    @Override    public void onViewCreated(View view, Bundle savedInstanceState) {        super.onViewCreated(view, savedInstanceState);        mAdapter = new SimpleCursorAdapter(                getActivity(),       // Current context                R.layout.item_secure_sim_list,  // Layout for individual rows                null,                // Cursor                FROM_COLUMNS,        // Cursor columns to use                TO_FIELDS,           // Layout fields to use                0                    // No flags        );        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
        		if (view.getId() == R.id.date) {
        			final long created_at = cursor.getLong(i);
        			final Calendar calendar = Calendar.getInstance();
        			calendar.setTimeInMillis(created_at);
        			String date = sdf.format(calendar.getTime());
					((TextView)view).setText(date);
                    return true;
                }
                return false;
            }
        });
		setListAdapter(mAdapter);
        setEmptyText(getText(R.string.empty_list));
        setListShown(false);
        if(savedInstanceState == null){
        	getLoaderManager().initLoader(0, null, this);
        }
		
    	super.onActivityCreated(savedInstanceState);
    }
    @Override    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {        return new CursorLoader(getActivity(),  // Context                SecureSIM.CONTENT_URI, // URI                PROJECTION,                // Projection                null,                           // Selection                null,                           // Selection args                SecureSIMColumns.CREATED_AT + " asc"); // Sort    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    	setEmptyText(getText(R.string.empty_list));
        mAdapter.changeCursor(cursor);
    	setListShownNoAnimation(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }
    
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
 
/*		Bundle b = null;
		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			ActivityOptionsCompat options = ActivityOptionsCompat .makeThumbnailScaleUpAnimation(view, Utils.drawViewOntoBitmap(view), 0, 0);
			b = options.toBundle();
		}*/
		Cursor cursor = mAdapter.getCursor();
		if(null == cursor){
			return;
		}
        int uniqueId = cursor.getInt(COLUMN_ID);
        if (uniqueId == 0) {
            Log.e(TAG, "Attempt to launch entry with null link");
            return;
        }

        Log.i(TAG, "Opening URL: " + uniqueId);
        Intent i = new Intent(getActivity(), SIMAddEditActivity.class);
        i.setData(ContentUris.withAppendedId(SecureSIM.CONTENT_URI, uniqueId));
        startActivity(i);
        //ActivityCompat.startActivity(getActivity(), i, b);
    }
}