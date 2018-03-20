package com.laewoong.menuarrowanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by laewoong on 2018. 3. 20..
 *
 * https://laewoong.github.io/
 */

public class MenuArrowAnimationButton extends AppCompatButton {

    private long mAnimationDuration;

    private Path mToMenuPath;
    private Path mToArrowPath;

    private Path mCurPath;
    private Paint mPaint;

    private int mCanvasWidth;
    private int mCanvasHeight;

    private boolean mIsAnimate;
    private boolean mIsArrowStatue;

    private float mPhase;
    private float TOTAL_PATH_LENGTH;

    private float mCX;
    private float mOffSet;

    private float mCurPathLength;

    private float MENU_UNDER_LENGTH;
    private float ARROW_LENGTH;
    private float MENU_OVER_LENGTH;

    private float START_MENU_BAR_VALUE;
    private float END_MENU_BAR_VALUE;

    private static final float TENSION = 1.0f;

    public MenuArrowAnimationButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuArrowAnimationButton);
        try {
            if (a != null) {
                mPaint.setStrokeWidth(a.getFloat(R.styleable.MenuArrowAnimationButton_strokeWidth, 10f));
                mPaint.setColor(a.getColor(R.styleable.MenuArrowAnimationButton_strokeColor, 0xff43479f));
                mAnimationDuration = a.getInt(R.styleable.MenuArrowAnimationButton_duration, 1500);
            }
        } finally {
            if (a != null) a.recycle();
        }
    }

    private void init() {

        setBackground(null); // TODO: process when bg be declared in xml.

        mToMenuPath = new Path();
        mToArrowPath = new Path();
        mCurPath = new Path();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);

        mIsAnimate = false;
        mIsArrowStatue = false;

        mPhase = 0f;
        TOTAL_PATH_LENGTH = 0f;
        mCurPathLength = 0f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCanvasWidth = w;
        mCanvasHeight = h;

        makeMenuArrowPath();
        initPathStatue(mIsArrowStatue);
    }

    private void initPathStatue(boolean isArrowStatus) {

        mIsArrowStatue = isArrowStatus;

        mCurPath.reset();
        PathMeasure pm = new PathMeasure();

        if(isArrowStatus)
        {
            pm.setPath(mToMenuPath, false);

            START_MENU_BAR_VALUE = TOTAL_PATH_LENGTH * 0.27f;
            END_MENU_BAR_VALUE = START_MENU_BAR_VALUE + MENU_OVER_LENGTH;

            float startD = pm.getLength() - ARROW_LENGTH;

            float endD = startD + ARROW_LENGTH;

            pm.getSegment(startD, endD, mCurPath,true);
        }
        else {

            pm.setPath(mToArrowPath, false);

            START_MENU_BAR_VALUE = TOTAL_PATH_LENGTH * 0.242f;
            END_MENU_BAR_VALUE = START_MENU_BAR_VALUE + MENU_OVER_LENGTH;

            pm.getSegment(0f, MENU_UNDER_LENGTH, mCurPath,true);
            pm.getSegment(START_MENU_BAR_VALUE, END_MENU_BAR_VALUE, mCurPath,true);
        }
    }

    private void makeMenuArrowPath() {

        Path srcPath = new Path();

        final float LEFT_CIRCLE_DIAMETER  = ((float) mCanvasHeight)*(2f/3f);
        final float RIGHT_CIRCLE_DIAMETER = mCanvasHeight;

        final float LEFT_CIRCLE_RADIUS    = LEFT_CIRCLE_DIAMETER/2f;
        final float RIGHT_CIRCLE_RADIUS   = RIGHT_CIRCLE_DIAMETER/2f;

        mCX = LEFT_CIRCLE_DIAMETER + ((mCanvasWidth -LEFT_CIRCLE_DIAMETER-RIGHT_CIRCLE_DIAMETER)/2f);
        mOffSet = mCX*0.2f;

        srcPath.moveTo(mCX + mOffSet, LEFT_CIRCLE_DIAMETER);
        RectF leftRound = new RectF();
        leftRound.set(0, 0, LEFT_CIRCLE_DIAMETER, LEFT_CIRCLE_DIAMETER);
        srcPath.arcTo(leftRound, 90, 180);

        srcPath.lineTo(mCanvasWidth -RIGHT_CIRCLE_DIAMETER, 0);

        leftRound.set(mCanvasWidth -RIGHT_CIRCLE_DIAMETER, 0, mCanvasWidth, mCanvasHeight);
        srcPath.arcTo(leftRound, 270, 180);

        srcPath.lineTo(mCX, mCanvasHeight);
        srcPath.lineTo(LEFT_CIRCLE_RADIUS, mCanvasHeight /2f);
        srcPath.lineTo(mCX, -mOffSet);

        PathMeasure pm = new PathMeasure(srcPath, false);
        final float pathLength = pm.getLength();

        TOTAL_PATH_LENGTH   = pathLength - mOffSet;
        MENU_OVER_LENGTH    = mCanvasWidth -(LEFT_CIRCLE_RADIUS + RIGHT_CIRCLE_RADIUS);
        MENU_UNDER_LENGTH   = mCX - LEFT_CIRCLE_DIAMETER/2f;
        ARROW_LENGTH        = TOTAL_PATH_LENGTH * 0.21f;

        pm.getSegment(0, TOTAL_PATH_LENGTH, mToMenuPath, true);
        pm.getSegment(mOffSet, pathLength, mToArrowPath, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect newRect = canvas.getClipBounds();
        newRect.inset(-mCanvasWidth, -mCanvasHeight);  //make the rect larger
        canvas.clipRect (newRect, Region.Op.REPLACE);

        canvas.drawPath(mCurPath, mPaint);
    }

    public void setArrowPhase(float phase) {

        mPhase = phase;

        PathMeasure pm = new PathMeasure();
        pm.setPath(mToArrowPath, false);

        final float length = TOTAL_PATH_LENGTH*0.21f;
        float startD = phase - mCurPathLength;
        if(startD < 0) {
            startD  = 0;
        }

        mCurPath.reset();
        pm.getSegment(phase, phase + mCurPathLength, mCurPath,true);

        if((phase + mCurPathLength) < END_MENU_BAR_VALUE)
        {
            if(phase < START_MENU_BAR_VALUE)
            {
                pm.getSegment(START_MENU_BAR_VALUE, END_MENU_BAR_VALUE, mCurPath,true);
            }
            else
            {
                pm.getSegment( phase, END_MENU_BAR_VALUE, mCurPath,true);
            }

        }

        invalidate();
    }

    public float getArrowPhase() {
        return mPhase;
    }

    public void setMenuPhase(float phase) {
        mPhase = phase;

        PathMeasure pm = new PathMeasure();
        pm.setPath(mToMenuPath, false);

        mCurPath.reset();
        pm.getSegment(phase, phase + mCurPathLength, mCurPath,true);

        if(phase < START_MENU_BAR_VALUE)
        {
            pm.getSegment(START_MENU_BAR_VALUE, END_MENU_BAR_VALUE, mCurPath,true);
        }
        else if (phase < END_MENU_BAR_VALUE)
        {
            pm.getSegment( phase, END_MENU_BAR_VALUE, mCurPath,true);
        }

        invalidate();
    }

    public float getMenuPhase() {
        return mPhase;
    }


    public boolean isArrowStatus() {

        return mIsArrowStatue;
    }

    public void toggle() {

        changeStatus(!isArrowStatus());
    }

    public void changeStatus(boolean checked) {

        if(mIsArrowStatue == checked) {
            return;
        }

        mIsArrowStatue = checked;

        if (isAttachedToWindow() && isLaidOut()) {
            animateMenuArrow(checked);
        } else {
            // Immediately move the thumb to the new position.
            initPathStatue(checked);
        }
    }

    public void setPathLength(float length) {
        mCurPathLength = length;
    }

    public float getPathLength() {
        return mCurPathLength;
    }

    private void animateMenuArrow(boolean isArrowStatus)
    {
        makeMenuArrowPath();

        AnimatorSet set = new AnimatorSet();

        if(isArrowStatus)
        {
            START_MENU_BAR_VALUE = TOTAL_PATH_LENGTH * 0.242f;
            END_MENU_BAR_VALUE = START_MENU_BAR_VALUE + MENU_OVER_LENGTH;

            final float startD = -(mOffSet*2);

            ObjectAnimator arrowAnimator = ObjectAnimator.ofFloat(this, "arrowPhase", startD, TOTAL_PATH_LENGTH - ARROW_LENGTH -mOffSet);
            arrowAnimator.setInterpolator(new OvershootInterpolator(TENSION));

            ObjectAnimator pathLengthAnimator = ObjectAnimator.ofFloat(this, "pathLength", MENU_UNDER_LENGTH *2, ARROW_LENGTH);

            set.playTogether(arrowAnimator, pathLengthAnimator);
            set.setDuration(mAnimationDuration);

        }
        else
        {
            START_MENU_BAR_VALUE = TOTAL_PATH_LENGTH * 0.27f;
            END_MENU_BAR_VALUE = START_MENU_BAR_VALUE + MENU_OVER_LENGTH;

            PathMeasure measure = new PathMeasure(mToMenuPath, false);

            float startD = measure.getLength() - ARROW_LENGTH;

            float endD = mOffSet;

            ObjectAnimator menuAnimator = ObjectAnimator.ofFloat(this, "menuPhase", startD, endD);
            menuAnimator.setInterpolator(new OvershootInterpolator(TENSION));

            ObjectAnimator pathLengthAnimator = ObjectAnimator.ofFloat(this, "pathLength", ARROW_LENGTH, MENU_UNDER_LENGTH);
            pathLengthAnimator.setInterpolator(new DecelerateInterpolator(1.5f));

            set.playTogether(menuAnimator, pathLengthAnimator);
            set.setDuration(mAnimationDuration);
        }

        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimate = false;
            }
        });
        set.start();
    }

    @Override
    public boolean performClick() {

        if(mIsAnimate == true) {
            return true;
        }

        toggle();

        final boolean handled = super.performClick();

        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }
}
