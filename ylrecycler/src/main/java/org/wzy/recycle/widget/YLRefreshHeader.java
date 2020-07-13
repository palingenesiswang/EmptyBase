package org.wzy.recycle.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.wzy.recycle.R;
import org.wzy.recycle.listener.YLRefreshHeaderListener;

import java.util.Date;


/**
 *
 */

public class YLRefreshHeader extends LinearLayout implements YLRefreshHeaderListener {

    private static final String XR_REFRESH_KEY = "XR_REFRESH_KEY";
    private static final String XR_REFRESH_TIME_KEY = "XR_REFRESH_TIME_KEY";
    private LinearLayout mContainer;

    private int mState = STATE_NORMAL;

    private ImageView mArrowImageView;
    private TextView mHintTextView;

    //    private Animation mRotateUpAnim;
//    private Animation mRotateDownAnim;
//    private TranslateAnimation translateAnimation;
    private AnimationDrawable animationDrawable;


    public int mMeasuredHeight;

    private String customRefreshPsKey = null;

    public void destroy() {

//        if (mRotateUpAnim != null) {
//            mRotateUpAnim.cancel();
//            mRotateUpAnim = null;
//        }
//        if (mRotateDownAnim != null) {
//            mRotateDownAnim.cancel();
//            mRotateDownAnim = null;
//        }
    }

    public YLRefreshHeader(Context context) {
        super(context);
        initView();
    }

    /**
     */
    public YLRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setRefreshTimeVisible(boolean show) {
//        if (mHeaderRefreshTimeContainer != null)
//            mHeaderRefreshTimeContainer.setVisibility(show ? VISIBLE : GONE);
    }

    public void setXrRefreshTimeKey(String keyName) {
        if (keyName != null) {
            customRefreshPsKey = keyName;
        }
    }

    @SuppressLint("InflateParams")
    private void initView() {
        // 初始情况，设置下拉刷新view高度为0
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.yl_header, null);

        mArrowImageView = mContainer.findViewById(R.id.iv_arrow);
        mHintTextView = mContainer.findViewById(R.id.tv_text);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        animationDrawable = (AnimationDrawable) mArrowImageView.getDrawable();
//        translateAnimation = new TranslateAnimation(0f, 1080.0f, 0f, 0f);
//        translateAnimation.setDuration(1000);
//        translateAnimation.setRepeatCount(0);
//        AccelerateInterpolator interpolator = new AccelerateInterpolator();
//        translateAnimation.setInterpolator(interpolator);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }


    public void setArrowImageView(int resid) {
        mArrowImageView.setImageResource(resid);
    }

    public void setState(int state) {
        if (state == mState) return;

        switch (state) {
            case STATE_NORMAL:  //  下拉刷新
                animationDrawable.stop();
//                if (mState == STATE_RELEASE_TO_REFRESH) {
//                    mArrowImageView.startAnimation(mRotateDownAnim);
//                }
//                if (mState == STATE_REFRESHING) {
//                    mArrowImageView.clearAnimation();
//                }
                mHintTextView.setText(R.string.listview_header_hint_normal);
                break;
            case STATE_RELEASE_TO_REFRESH:  //  释放立即刷新
                animationDrawable.start();
//                if (mState != STATE_RELEASE_TO_REFRESH) {
//                    mArrowImageView.clearAnimation();
//                    mArrowImageView.startAnimation(mRotateUpAnim);
                mHintTextView.setText(R.string.listview_header_hint_release);
//                }
                break;
            case STATE_REFRESHING:  //  正在刷新
                animationDrawable.start();
//                mArrowImageView.clearAnimation();
//                mArrowImageView.startAnimation(translateAnimation);
                mHintTextView.setText(R.string.refreshing);
                break;
            case STATE_DONE:    //  刷新完成
                animationDrawable.stop();
                mHintTextView.setText(R.string.refresh_done);
                break;
            default:
        }

        mState = state;
    }

    public int getState() {
        return mState;
    }

    private long getLastRefreshTime() {
        String spKeyName = XR_REFRESH_KEY;
        if (customRefreshPsKey != null) {
            spKeyName = customRefreshPsKey;
        }
        @SuppressLint("WrongConstant") SharedPreferences s =
                getContext()
                        .getSharedPreferences(spKeyName, Context.MODE_APPEND);
        return s.getLong(XR_REFRESH_TIME_KEY, new Date().getTime());
    }

    private void saveLastRefreshTime(long refreshTime) {
        String spKeyName = XR_REFRESH_KEY;
        if (customRefreshPsKey != null) {
            spKeyName = customRefreshPsKey;
        }
        @SuppressLint("WrongConstant") SharedPreferences s =
                getContext().getSharedPreferences(spKeyName, Context.MODE_APPEND);
        s.edit().putLong(XR_REFRESH_TIME_KEY, refreshTime).apply();
    }

    @Override
    public void refreshComplete() {
//        mHeaderTimeView.setText(friendlyTime(getLastRefreshTime()));
        saveLastRefreshTime(System.currentTimeMillis());
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 200);
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    @Override
    public void onMove(float delta) {
        if (getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if (getVisibleHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight) {
            //return;
        }
        if (mState != STATE_REFRESHING) {
            smoothScrollTo(0);
        }

        if (mState == STATE_REFRESHING) {
            int destHeight = mMeasuredHeight;
            smoothScrollTo(destHeight);
        }

        return isOnRefresh;
    }

    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setState(STATE_NORMAL);
            }
        }, 500);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }


    public static String friendlyTime(Date time) {
        return friendlyTime(time.getTime());
    }

    public static String friendlyTime(long time) {
        //获取time距离当前的秒数
        int ct = (int) ((System.currentTimeMillis() - time) / 1000);

        if (ct == 0) {
            return "刚刚";
        }

        if (ct > 0 && ct < 60) {
            return ct + "秒前";
        }

        if (ct >= 60 && ct < 3600) {
            return Math.max(ct / 60, 1) + "分钟前";
        }
        if (ct >= 3600 && ct < 86400)
            return ct / 3600 + "小时前";
        if (ct >= 86400 && ct < 2592000) { //86400 * 30
            int day = ct / 86400;
            return day + "天前";
        }
        if (ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + "月前";
        }
        return ct / 31104000 + "年前";
    }
}
