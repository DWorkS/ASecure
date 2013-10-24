package dev.dworks.apps.asecure.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class SwipeLinearLayout extends LinearLayout {

	public SwipeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	return true;
    }
}