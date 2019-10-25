package com.fxl.guetcoursetable.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.fxl.guetcoursetable.MainActivity;

/**
 * Created by fxl on 17-3-13.
 */

public class ScrollBounceView extends ScrollView {
    private int mMaxOverDistance = 50;

    public ScrollBounceView(Context context) {
        super(context);
    }

    public ScrollBounceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollBounceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY,
                                   int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY,
                                   boolean isTouchEvent) {
//        initView();
        return super.overScrollBy(deltaX, deltaY,
                scrollX, scrollY,
                scrollRangeX, scrollRangeY,
                maxOverScrollX, mMaxOverDistance,
                isTouchEvent);
    }

    private void initView() {
        DisplayMetrics metrics = MainActivity.getContext().getResources().getDisplayMetrics();
        float density = metrics.density;
        mMaxOverDistance = (int) (density * mMaxOverDistance);
    }
}
