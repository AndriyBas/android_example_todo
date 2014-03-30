package com.oyster.DBandContentProviderEx;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * @author bamboo
 * @since 3/29/14 10:12 AM
 */
public class NowLayout extends LinearLayout
        implements ViewTreeObserver.OnGlobalLayoutListener {

    public NowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayoutObserver();
    }

    public NowLayout(Context context) {
        super(context);
        initLayoutObserver();

    }

    private void initLayoutObserver() {
        setOrientation(LinearLayout.VERTICAL);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);

        final int heightPx = getContext().getResources().getDisplayMetrics().heightPixels;

        boolean inversed = false;
        final int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            int[] location = new int[2];

            child.getLocationOnScreen(location);

            if (location[1] > heightPx) {
                break;
            }

            int animationResourse = inversed ? R.anim.now_style_slide_up_right : R.anim.now_style_slide_up_left;

            child.startAnimation(AnimationUtils.loadAnimation(getContext(),
                    animationResourse));

            inversed = !inversed;
//            if(!inversed) {
//                child.startAnimation(AnimationUtils.loadAnimation(getContext(),
//                        R.anim.now_style_slide_up_left));
//            } else {
//                child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.now_style_slide_up_right));
//            }
        }

    }
}
