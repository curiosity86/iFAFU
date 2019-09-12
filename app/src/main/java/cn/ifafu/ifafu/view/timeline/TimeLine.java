package cn.ifafu.ifafu.view.timeline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintTypedArray;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.util.DensityUtils;

public class TimeLine extends View {

    private Paint mLinePaint;
    private Paint mPointPaint;
    private TextPaint mTextPaint;

    private Rect mDateRect;

    private List<TimeAxis> mTimeAxisList = new ArrayList<>();

    private float mTextSize;
    private int mTextColor;
    private int mLineColor;
    private int mMaxPointCount;

    public TimeLine(Context context) {
        this(context, null);
    }

    public TimeLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.TimeLine,
                defStyleAttr, 0);

        mDateRect = new Rect();

        mMaxPointCount = a.getInt(R.styleable.TimeLine_max_count, 4);
        mLineColor = a.getColor(R.styleable.TimeLine_line_color, 0xFF000000);
        mTextColor = a.getColor(R.styleable.TimeLine_text_color, 0xFF000000);
        mTextSize = a.getDimension(R.styleable.TimeLine_text_size, DensityUtils.dp2px(context, 12));

        //虚线
        mLinePaint = new Paint();
        mLinePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(3);
        DashPathEffect effect = new DashPathEffect(new float[]{8, 8}, 0);
        mLinePaint.setPathEffect(effect);

        //文本
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(DensityUtils.sp2px(context, 10f));

        //圆点
        mPointPaint = new TextPaint();
        mPointPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
            int width = getWidth();
            float centerHeight = getHeight() / 2F;

            mLinePaint.setColor(mLineColor);
            mPointPaint.setColor(mLineColor);
            mTextPaint.setColor(mTextColor);
            mTextPaint.setTextSize(mTextSize);

            canvas.drawColor(0);

            //绘制虚线
            canvas.drawLine(0, centerHeight, width, centerHeight, mLinePaint);

            if (mTimeAxisList.isEmpty()) {
                return;
            }

            mTextPaint.getTextBounds(mTimeAxisList.get(0).getDate(), 0,
                    mTimeAxisList.get(0).getDate().length(), mDateRect);
            float dateTextWidth = mDateRect.width() >> 1;
            float dateTextHeight = mDateRect.height();
            float perPointInterval = 1F * width / mMaxPointCount;
            float pointX = ((int) perPointInterval) >> 1;
            float dp7 = DensityUtils.dp2px(getContext(), 7);

            for (int i = 0; i < mMaxPointCount && i < mTimeAxisList.size(); i++) {
                TimeAxis timeAxis = mTimeAxisList.get(i);
                if (timeAxis.getDay() < 0) {
                    return;
                }
                //绘制时间文本
                @SuppressLint("DefaultLocale")
                String name = MessageFormat.format("{0} {1}天",
                        timeAxis.getName(), timeAxis.getDay() > 0? timeAxis.getDay() : "今");
                mTextPaint.getTextBounds(name, 0, name.length(), mDateRect);
                int nameTextWidth = mDateRect.width();
                canvas.drawText(name, pointX - (nameTextWidth >> 1), centerHeight + dp7 + dateTextHeight, mTextPaint);
                //绘制日期文本
                canvas.drawText(timeAxis.getDate(), pointX - dateTextWidth, centerHeight - dp7, mTextPaint);
                //绘制圆点
                canvas.drawCircle(pointX, centerHeight, 10, mPointPaint);
                pointX += perPointInterval;
            }
    }

    public TimeLine setLineColor(@ColorInt int color) {
        mLineColor = color;
        return this;
    }

    public TimeLine setTextColor(@ColorInt int color) {
        mTextColor = color;
        return this;
    }

    public TimeLine setMaxPointCnt(int maxPointCnt) {
        mMaxPointCount = maxPointCnt;
        return this;
    }

    public TimeLine addTimeAxis(TimeAxis timeAxis) {
        mTimeAxisList.add(timeAxis);
        return this;
    }

    public TimeLine setTimeAxisList(List<TimeAxis> timeAxisList) {
        mTimeAxisList = timeAxisList;
        return this;
    }

}
