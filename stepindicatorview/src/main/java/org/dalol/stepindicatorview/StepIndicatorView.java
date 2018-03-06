/*
 * Copyright (c) 2018 Filippo Engidashet.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.dalol.stepindicatorview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Filippo Engidashet <filippo.eng@gmail.com>
 * @version 1.0.0
 * @since Tuesday, 06/03/2018 at 10:56.
 */

public class StepIndicatorView extends View {

    private static final int DEFAULT_STEP_RADIUS = 12;
    private static final int DEFAULT_STEP_COUNT = 4;
    private static final int DEFAULT_CURRENT_STEP_COLOR = 0xFF976FFF;
    private static final int DEFAULT_NEXT_STEP_COLOR = 0xFFD8D0FF;

    private int mIndicatorRadius;

    private int mCurrentStepPosition;
    private int mStepsCount = 1;

    private int mCurrentStepColor;
    private int mNextStepColor;

    private int centerY;
    private int startX;
    private int endX;
    private int stepDistance;

    private Paint mPaint;

    private ViewPagerOnChangeListener mViewPagerChangeListener;

    private Bitmap mTickIcon;

    private boolean mAllTicked;

    public StepIndicatorView(Context context) {
        super(context);
        init(context, null);
    }

    public StepIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StepIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StepIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        initAttributes(context, attributeSet);

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(ViewUtils.toPixel(2, context));
        setMinimumHeight(mIndicatorRadius * 3);
    }

    private void initAttributes(Context context, AttributeSet attributeSet) {
        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.StepIndicatorView, 0, 0);
        if (attr == null) {
            return;
        }

        try {
            mIndicatorRadius = (int) attr.getDimension(R.styleable.StepIndicatorView_siRadius, ViewUtils.toPixel(DEFAULT_STEP_RADIUS, context));
            mStepsCount = attr.getInt(R.styleable.StepIndicatorView_siStepCount, DEFAULT_STEP_COUNT);
            mCurrentStepColor = attr.getColor(R.styleable.StepIndicatorView_siCurrentStepColor, DEFAULT_CURRENT_STEP_COLOR);
            mNextStepColor = attr.getColor(R.styleable.StepIndicatorView_siNextStepColor, DEFAULT_NEXT_STEP_COLOR);
        } finally {
            attr.recycle();
        }
        mTickIcon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_tick), mIndicatorRadius * 2, mIndicatorRadius * 2, true);
    }

    public void setStepsCount(int stepsCount) {
        this.mStepsCount = stepsCount;
        invalidate();
    }

    public void setCurrentStepPosition(int currentStepPosition) {
        mCurrentStepPosition = currentStepPosition;
        mAllTicked = false;
        invalidate();
    }

    public void setupWithViewPager(@NonNull ViewPager viewPager) {
        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }
        if (mViewPagerChangeListener == null) {
            mViewPagerChangeListener = new ViewPagerOnChangeListener(this);
        }

        setStepsCount(adapter.getCount());
        viewPager.addOnPageChangeListener(mViewPagerChangeListener);
        if (adapter.getCount() > 0) {
            final int curItem = viewPager.getCurrentItem();
            if (mCurrentStepPosition != curItem) {
                setCurrentStepPosition(curItem);
                invalidate();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStepsCount <= 1) {
            setVisibility(GONE);
            return;
        }
        super.onDraw(canvas);
        int pointX = startX;

        for (int i = 0; i < mStepsCount - 1; i++) {
            if (i < mCurrentStepPosition) {
                mPaint.setColor(mCurrentStepColor);
                canvas.drawLine(pointX, centerY, pointX + stepDistance, centerY, mPaint);
            } else {
                mPaint.setColor(mNextStepColor);
                canvas.drawLine(pointX, centerY, pointX + stepDistance, centerY, mPaint);
            }
            pointX = pointX + stepDistance;
        }

        pointX = startX;
        for (int i = 0; i < mStepsCount; i++) {
            if (i < mCurrentStepPosition) {
                //draw previous step
                canvas.drawBitmap(mTickIcon, pointX - mIndicatorRadius, centerY - mIndicatorRadius, mPaint);
            } else if (i == mCurrentStepPosition) {
                if (mAllTicked) {
                    canvas.drawBitmap(mTickIcon, pointX - mIndicatorRadius, centerY - mIndicatorRadius, mPaint);
                } else {
                    //draw current step
                    mPaint.setColor(mCurrentStepColor);
                    canvas.drawCircle(pointX, centerY, mIndicatorRadius, mPaint);
                    mPaint.setColor(Color.WHITE);
                    canvas.drawCircle(pointX, centerY, mIndicatorRadius / 4, mPaint);
                }

            } else {
                //draw next step
                mPaint.setColor(mNextStepColor);
                canvas.drawCircle(pointX, centerY, mIndicatorRadius, mPaint);
            }
            pointX = pointX + stepDistance;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, getPaddingTop() + getPaddingBottom() + (mIndicatorRadius * 3));
        centerY = getHeight() / 2;
        startX = getPaddingLeft() + mIndicatorRadius * 2;
        endX = getWidth() - (mIndicatorRadius * 2) - getPaddingRight();
        stepDistance = (endX - startX) / (mStepsCount - 1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerY = getHeight() / 2;
        startX = mIndicatorRadius * 2;
        endX = getWidth() - (mIndicatorRadius * 2);
        stepDistance = (endX - startX) / (mStepsCount - 1);
        invalidate();
    }

    public void setAllTicked() {
        mAllTicked = true;
        invalidate();
    }

    private class ViewPagerOnChangeListener extends ViewPager.SimpleOnPageChangeListener {

        private final StepIndicatorView mStepIndicator;

        ViewPagerOnChangeListener(StepIndicatorView stepIndicator) {
            mStepIndicator = stepIndicator;
        }

        @Override
        public void onPageSelected(int position) {
            mStepIndicator.setCurrentStepPosition(position);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.radius = this.mIndicatorRadius;
        ss.currentStepPosition = this.mCurrentStepPosition;
        ss.stepsCount = this.mStepsCount;
        ss.currentColor = this.mCurrentStepColor;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mIndicatorRadius = ss.radius;
        mCurrentStepPosition = ss.currentStepPosition;
        mStepsCount = ss.stepsCount;
        mCurrentStepColor = ss.currentColor;
    }

    static class SavedState extends BaseSavedState {
        int radius;
        int currentStepPosition;
        int stepsCount;
        int backgroundColor;
        int stepColor;
        int currentColor;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            radius = in.readInt();
            currentStepPosition = in.readInt();
            stepsCount = in.readInt();
            backgroundColor = in.readInt();
            stepColor = in.readInt();
            currentColor = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(radius);
            dest.writeInt(currentStepPosition);
            dest.writeInt(stepsCount);
            dest.writeInt(backgroundColor);
            dest.writeInt(stepColor);
            dest.writeInt(currentColor);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}