package com.laewoong.menuarrowanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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

    private Path mSrcPath;
    private Path mCurPath;

    private Paint mPaint;

    private AnimatorSet mToArrowAnimator;
    private AnimatorSet mToMenuAnimator;

    private int mCanvasWidth;
    private int mCanvasHeight;

    private boolean mIsAnimate;
    private boolean mIsArrowStatue;

    private float mCurPathLength;

    private float MENU_UNDER_LENGTH;
    private float ARROW_LENGTH;

    private float MENU_UNDER_START_VALUE;
    private float MENU_UNDER_END_VALUE;
    private float MENU_OVER_START_VALUE;
    private float MENU_OVER_END_VALUE;
    private float ARROW_START_VALUE;
    private float ARROW_END_VALUE;

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

        setBackground(null);

        mCurPath = new Path();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);

        mIsAnimate = false;
        mIsArrowStatue = false;

        mCurPathLength = 0f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCanvasWidth = w;
        mCanvasHeight = h;

        makeMenuArrowPath();
        initPathStatue(mIsArrowStatue);
        initToArrowAnimator();
        initToMenuAnimator();
    }

    private void initPathStatue(boolean isArrowStatus) {

        mIsArrowStatue = isArrowStatus;

        mCurPath.reset();
        PathMeasure pm = new PathMeasure(mSrcPath, false);

        if(isArrowStatus) {
            pm.getSegment(ARROW_START_VALUE, ARROW_END_VALUE, mCurPath,true);
        }
        else {

            pm.getSegment(MENU_UNDER_START_VALUE, MENU_UNDER_END_VALUE, mCurPath,true);
            pm.getSegment(MENU_OVER_START_VALUE, MENU_OVER_END_VALUE, mCurPath,true);
        }
    }

    private void makeMenuArrowPath() {

        PathMeasure pathMeasure = new PathMeasure();
        mSrcPath = new Path();

        final float LEFT_CIRCLE_DIAMETER  = ((float) mCanvasHeight)*(2f/3f);
        final float RIGHT_CIRCLE_DIAMETER = mCanvasHeight;

        final float LEFT_CIRCLE_RADIUS    = LEFT_CIRCLE_DIAMETER/2f;
        final float RIGHT_CIRCLE_RADIUS   = RIGHT_CIRCLE_DIAMETER/2f;

        final float MENU_BAR_OVER_LENGTH  = mCanvasWidth -LEFT_CIRCLE_RADIUS-RIGHT_CIRCLE_RADIUS;

        float cX = LEFT_CIRCLE_RADIUS + (MENU_BAR_OVER_LENGTH/2f);
        float offset = cX*0.2f;

        RectF leftRound = new RectF();
        leftRound.set(0, 0, LEFT_CIRCLE_DIAMETER, LEFT_CIRCLE_DIAMETER);

        RectF rightRound = new RectF();
        rightRound.set(mCanvasWidth -RIGHT_CIRCLE_DIAMETER, 0, mCanvasWidth, mCanvasHeight);

        MENU_UNDER_START_VALUE = offset;
        MENU_UNDER_END_VALUE = MENU_UNDER_START_VALUE + (cX-LEFT_CIRCLE_RADIUS);

        mSrcPath.moveTo(cX + offset, LEFT_CIRCLE_DIAMETER);

        mSrcPath.arcTo(leftRound, 90, 180);
        pathMeasure.setPath(mSrcPath, false);
        MENU_OVER_START_VALUE = pathMeasure.getLength();
        MENU_OVER_END_VALUE = MENU_OVER_START_VALUE + MENU_BAR_OVER_LENGTH;

        mSrcPath.arcTo(rightRound, 270, 180);

        mSrcPath.lineTo(cX, mCanvasHeight);
        pathMeasure.setPath(mSrcPath, false);
        ARROW_START_VALUE = pathMeasure.getLength();
        mSrcPath.lineTo(LEFT_CIRCLE_RADIUS, mCanvasHeight /2f);
        mSrcPath.lineTo(cX, -offset);
        pathMeasure.setPath(mSrcPath, false);
        ARROW_END_VALUE = pathMeasure.getLength() - offset;

        PathMeasure pm = new PathMeasure(mSrcPath, false);
        final float pathLength = pm.getLength();

        MENU_UNDER_LENGTH   = cX - LEFT_CIRCLE_DIAMETER/2f;
        ARROW_LENGTH        = ARROW_END_VALUE - ARROW_START_VALUE;
    }

    private void initToArrowAnimator() {

        ObjectAnimator arrowAnimator = ObjectAnimator.ofFloat(this, "arrowPhase", MENU_UNDER_END_VALUE, ARROW_END_VALUE);
        arrowAnimator.setInterpolator(new OvershootInterpolator(TENSION));

        ObjectAnimator pathLengthAnimator = ObjectAnimator.ofFloat(this, "pathLength", MENU_UNDER_LENGTH, ARROW_LENGTH);

        mToArrowAnimator = new AnimatorSet();
        mToArrowAnimator.playTogether(arrowAnimator, pathLengthAnimator);
        mToArrowAnimator.setDuration(mAnimationDuration);
        mToArrowAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimate = false;
            }
        });
    }

    private void initToMenuAnimator() {

        ObjectAnimator menuAnimator = ObjectAnimator.ofFloat(this, "menuPhase", ARROW_START_VALUE, MENU_UNDER_START_VALUE);
        menuAnimator.setInterpolator(new OvershootInterpolator(TENSION));

        ObjectAnimator pathLengthAnimator = ObjectAnimator.ofFloat(this, "pathLength", ARROW_LENGTH, MENU_UNDER_LENGTH);
        pathLengthAnimator.setInterpolator(new DecelerateInterpolator(1.5f));

        mToMenuAnimator = new AnimatorSet();
        mToMenuAnimator.playTogether(menuAnimator, pathLengthAnimator);
        mToMenuAnimator.setDuration(mAnimationDuration);
        mToMenuAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimate = false;
            }
        });
    }

    public void setArrowPhase(float endPhase) {

        float startD = endPhase-mCurPathLength;
        float endD = endPhase;

        mCurPath.reset();

        PathMeasure pm = new PathMeasure(mSrcPath, false);
        pm.getSegment(startD, endD, mCurPath,true);

        if(endD < MENU_OVER_END_VALUE)
        {
            if(startD < MENU_OVER_START_VALUE)
            {
                pm.getSegment(MENU_OVER_START_VALUE, MENU_OVER_END_VALUE, mCurPath,true);
            }
            else
            {
                pm.getSegment( endD, MENU_OVER_END_VALUE, mCurPath,true);
            }
        }

        invalidate();
    }

    public void setMenuPhase(float startPhase) {

        float startD = startPhase;
        float endD = startPhase+mCurPathLength;

        mCurPath.reset();

        PathMeasure pm = new PathMeasure(mSrcPath, false);
        pm.getSegment(startD, endD, mCurPath,true);

        if(endD < MENU_OVER_START_VALUE)
        {
            pm.getSegment(MENU_OVER_START_VALUE, MENU_OVER_END_VALUE, mCurPath,true);
        }
        else if (startD < MENU_OVER_END_VALUE)
        {
            pm.getSegment( startD, MENU_OVER_END_VALUE, mCurPath,true);
        }

        invalidate();
    }

    public void setPathLength(float length) {
        mCurPathLength = length;
    }

    public boolean isArrowStatus() {

        return mIsArrowStatue;
    }

    public void changeStatus(boolean checked) {

        if(mIsArrowStatue == checked) {
            return;
        }

        mIsArrowStatue = checked;

        if (isAttachedToWindow() && isLaidOut()) {

            if(checked) {
                mToArrowAnimator.start();
            }
            else {
                mToMenuAnimator.start();
            }

        } else {
            // Immediately move the thumb to the new position.
            initPathStatue(checked);
        }
    }

    public void toggle() {

        changeStatus(!isArrowStatus());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect newRect = canvas.getClipBounds();
        newRect.inset(-mCanvasWidth, -mCanvasHeight);  //make the rect larger
        canvas.clipRect (newRect, Region.Op.REPLACE);

        canvas.drawPath(mCurPath, mPaint);
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
