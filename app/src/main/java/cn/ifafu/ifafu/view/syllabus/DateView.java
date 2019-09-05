package cn.ifafu.ifafu.view.syllabus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

import cn.ifafu.ifafu.util.DensityUtils;

/**
 * Created by woolsen
 */
public class DateView extends LinearLayout {

    //rootView's width and height
    private int mWidth;
    private int mHeight;
    // 列宽
    private float mColItemWidth = DensityUtils.dp2px(getContext(), 50);
    // 列数
    private int mColCount = 7;

    // 是否显示分割线
    private boolean mShowVerticalDivider = true;
    // 绘制分割线
    private Paint mLinePaint;
    private Path mLinePath = new Path();

    // onCreate时首次绘制
    private boolean mFirstDraw;

    private DateItem[] dateItems = new DateItem[7];

    private String[] weekdays = {
            "周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    private String[] dateTexts;
    private String[] weekdayTexts = getWeekdayText(Calendar.SUNDAY, true);

    public DateView(@NonNull Context context) {
        this(context, null);
    }

    public DateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.LTGRAY);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawSplitLine(canvas);
        super.dispatchDraw(canvas);
        if (!mFirstDraw) {
            initDateItemView();
            mFirstDraw = true;
        }
    }

    private void initDateItemView() {
        removeAllViews();
        if (dateTexts == null) {
            dateTexts = new String[mColCount];
        }
        for (int i = 0; i < mColCount; i++) {
            String dateText = null;
            if (i < dateTexts.length) {
                dateText = dateTexts[i];
            }
            String weekdayText = null;
            if (i < weekdayTexts.length) {
                weekdayText = weekdayTexts[i];
            }
            realAddDateItemView(i, weekdayText, dateText);
        }
    }

    private void realAddDateItemView(int index, String weekdayText, String dateText) {
        DateItem itemView = new DateItem(getContext());
        if (weekdayText != null) {
            itemView.setWeekdayText(weekdayText);
        }
        if (dateText != null) {
            itemView.dateTV.setVisibility(VISIBLE);
            itemView.setDateText(dateText);
        } else {
            itemView.dateTV.setVisibility(GONE);
        }
        LayoutParams params = new LayoutParams(
                (int) (mColItemWidth + 0.5F), mHeight);
        itemView.setLayoutParams(params);
        dateItems[index] = itemView;
        addView(itemView);
    }

    public void setDateTexts(String[] dateTexts) {
        this.dateTexts = dateTexts;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        weekdayTexts = getWeekdayText(firstDayOfWeek, true);
    }

    public void redraw() {
        initDateItemView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
        mWidth = w;
        mColItemWidth = 1.0F * mWidth / mColCount;
    }

    private void drawSplitLine(Canvas canvas) {
        //垂直分割线
        if (mShowVerticalDivider) {
            for (int i = 0; i <= mColCount; i++) {
                mLinePath.reset();
                mLinePath.moveTo((int) (i * mColItemWidth + 0.5F), 0);
                mLinePath.lineTo((int) (i * mColItemWidth + 0.5F), mHeight);
                canvas.drawPath(mLinePath, mLinePaint);
            }
        }
    }

    public void setShowVerticalDivider(boolean isShow) {
        mShowVerticalDivider = isShow;
    }

    /**
     * 获取星期文本
     *
     * @param firstDayOfWeek {@link Calendar}
     * @param weekend        是否包含周末
     * @return String[]
     */
    public String[] getWeekdayText(int firstDayOfWeek, boolean weekend) {
        String[] result = weekend ? new String[7] : new String[5];
        int indexOfOffset = 7 + firstDayOfWeek - Calendar.SUNDAY;
        for (int i = 0, j = 0; i < 7; i++) {
            int t = (i + indexOfOffset) % 7;
            if (weekend || t != 0 && t != 6) {
                result[j++] = weekdays[t];
            }
        }
        return result;
    }

    class DateItem extends LinearLayout {

        private TextView weekdayTV;
        private TextView dateTV;
        private boolean today = false;

        private final int weekdayColor = Color.BLACK;
        private final int dateColor = Color.BLACK;

        public DateItem(Context context) {
            super(context);
            weekdayTV = createTextView(true, 12, weekdayColor);
            dateTV = createTextView(false, 10, dateColor);
            this.setOrientation(LinearLayout.VERTICAL);
            this.setGravity(Gravity.CENTER);
            this.addView(weekdayTV);
            this.addView(dateTV);
        }

        public void setWeekdayText(String weekdayTV) {
            this.weekdayTV.setText(weekdayTV);
        }

        public void setDateText(String dateTV) {
            this.dateTV.setText(dateTV);
        }

        private TextView createTextView(boolean bold, int size, int color) {
            TextView textView = new TextView(getContext());
            textView.setTextColor(color);
            textView.getPaint().setFakeBoldText(bold);
            textView.setTextSize(size);
            textView.setGravity(Gravity.CENTER);
            return textView;
        }
    }
}
