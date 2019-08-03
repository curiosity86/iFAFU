package cn.woolsen.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.IntDef;
import androidx.appcompat.widget.AppCompatTextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.woolsen.android.R;

public class DrawableTextView extends AppCompatTextView {

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    private int mDrawableHeight, mDrawableWidth;

    private Drawable mDrawable;

    private int mDrawablePosition;

    @IntDef(value = {LEFT, TOP, RIGHT, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PositionAttr{}

    public DrawableTextView(Context context) {
        this(context, null);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
        mDrawableWidth = a.getDimensionPixelSize(R.styleable.DrawableTextView_drawable_width, 0);
        mDrawableHeight = a.getDimensionPixelSize(R.styleable.DrawableTextView_drawable_height, 0);
        mDrawablePosition = a.getInt(R.styleable.DrawableTextView_drawable_loc, LEFT);
        mDrawable = a.getDrawable(R.styleable.DrawableTextView_drawable_src);
        a.recycle();
        drawDrawable();
    }

    private void drawDrawable() {
        if (mDrawable != null) {
            mDrawable.setBounds(0 ,0, mDrawableWidth, mDrawableHeight);
            switch (mDrawablePosition) {
                case LEFT:
                    this.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
                    break;
                case TOP:
                    this.setCompoundDrawablesWithIntrinsicBounds(null, mDrawable, null, null);
                    break;
                case RIGHT:
                    this.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);
                    break;
                case BOTTOM:
                    this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, mDrawable);
                    break;
            }
        }
    }

    public void setDrawable(Drawable drawable, @PositionAttr int position) {
        mDrawable = drawable;
        mDrawablePosition = position;
        drawDrawable();
    }

}
