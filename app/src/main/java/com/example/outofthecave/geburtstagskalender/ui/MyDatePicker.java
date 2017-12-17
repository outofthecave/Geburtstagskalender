package com.example.outofthecave.geburtstagskalender.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;

/**
 * {@link DatePicker} with some bug fixes:
 * <ul>
 *     <li>https://stackoverflow.com/a/34189814</li>
 * </ul>
 */
public class MyDatePicker extends DatePicker {
    public MyDatePicker(Context context) {
        super(context);
    }

    public MyDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyDatePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }

        return false;
    }
}
