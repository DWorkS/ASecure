/*
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