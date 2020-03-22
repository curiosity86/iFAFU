package cn.ifafu.ifafu.view.syllabus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.util.ColorUtils;
import cn.ifafu.ifafu.util.DensityUtils;
import cn.ifafu.ifafu.view.syllabus.listener.OnCourseClickListener;
import cn.ifafu.ifafu.view.syllabus.listener.OnCourseLongClickListener;

/**
 * Created by woolsen
 */
public class SyllabusView extends LinearLayout {

    //rootView's width and height
    private int mWidth;
    private int mHeight;

    private int mColItemWidth; // 列宽度
    private int mRowItemHeight; // 行高度

    // DateView
    private int mDateHeight;
    private int mDateTextColor = 0xFF000000;
    private DateItem[] dateItems;
    private TextView cornerTV;
    private String[] weekdays = {
            "周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    // SideView
    private int mSideWidth;
    private int mSideTextColor = 0xFF000000;
    private boolean mShowTimeTexts; // 是否显示上课时间
    private List<String> timeTexts; // 上课时间
    private String[] dateTexts;

    // CourseView
    private int mCourseLRMargin = 0; // horizontal margin
    private int mCourseTBMargin = 0; // vertical margin
    private int mCourseLRPadding; // text horizontal Padding
    private int mCourseTBPadding; // text vertical Padding
    private int mCourseTextColor; // text color
    private int mCourseTextSize; // text size

    private LinearLayout mSideLayout; //侧边栏布局

    private int mRowCount = 12;// 行数

    private int mColCount = 7;// 列数

    private List<? extends ToCourseBase> courses;
    private Map<CourseBase, View> courseToView = new HashMap<>();
    private FrameLayout[] mCourseLayouts; //课程列布局数组，下标对应weekday

    private int firstDayOfWeek = Calendar.SUNDAY; // 周的第一天

    private boolean mShowHorizontalDivider = true;// 是否显示分割线
    private boolean mShowVerticalDivider = true;

    // 绘制分割线
    private Paint mLinePaint;
    private Path mLinePath = new Path();

    private boolean mFirstDraw = true; // onCreate时首次绘制


    private OnCourseClickListener onCourseClickListener;
    private OnCourseLongClickListener onCourseLongClickListener;

    private int colorIndex = 0;
    private Map<String, Integer> colorMap = new HashMap<>();

//    private int today = -1; //-1表示非本周
//    private int todayBackground;

    private static final String TAG = "SyllabusView2";

    public SyllabusView(Context context) {
        this(context, null);
    }

    public SyllabusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        initPaint();
        dateItems = new DateItem[8];
        mCourseLayouts = new FrameLayout[8];

        mSideWidth = (int) DensityUtils.dp2px(context, 24);
        mDateHeight = (int) DensityUtils.dp2px(context, 36);

        Log.d(TAG, "SyllabusView2: mSideWidth = " + mSideWidth);

        mShowTimeTexts = true;

        mCourseLRPadding = (int) DensityUtils.dp2px(getContext(), 1);
        mCourseTBPadding = (int) DensityUtils.dp2px(getContext(), 1);
        mCourseTextSize = 12;
        mCourseTextColor = Color.WHITE;

    }

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.LTGRAY);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
        mWidth = w;
        mColItemWidth = (mWidth - mSideWidth) / mColCount;
        mSideWidth = mWidth - mColItemWidth * mColCount;
        mRowItemHeight = (mHeight - mDateHeight) / mRowCount;
        mDateHeight = mHeight - mRowItemHeight * mRowCount;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawSplitLine(canvas);
        super.dispatchDraw(canvas);
        if (mFirstDraw) {

            //初始化侧边栏
            mSideLayout = new LinearLayout(getContext());
            mSideLayout.setOrientation(LinearLayout.VERTICAL);
            LayoutParams params = new LayoutParams(mSideWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mSideLayout, 0, params);
            initSideItemView();

            //初始化课表
            for (int i = 1; i <= 7; i++) {
                FrameLayout layout = new FrameLayout(getContext());
                mCourseLayouts[i] = layout;
                addView(layout, mColItemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mDateHeight);
            for (int i = 1; i <= 7; i++) {
                dateItems[i] = new DateItem(getContext());
                dateItems[i].setWeekdayText(weekdays[i - 1]);
                dateItems[i].setDateColor(mDateTextColor);
                dateItems[i].setWeekdayColor(mDateTextColor);
                dateItems[i].setLayoutParams(params2);
//                if (i == today) {
//                    dateItems[i].setBackgroundColor(todayBackground);
//                }
            }
            initCourseItemView();

            mFirstDraw = false;
        }
    }

    private void initSideItemView() {
        mSideLayout.removeAllViews();
        ensureCornerText();
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mDateHeight);
        mSideLayout.addView(cornerTV, 0, params);
        for (int i = 0; i < mRowCount; i++) {
            String time = timeTexts != null && i < timeTexts.size() ? timeTexts.get(i) : null;
            realAddSideItemView(String.valueOf(i + 1), time);
        }
    }

    /**
     * 把数组中的数据全部添加到界面
     */
    private void initCourseItemView() {
        for (int i = 1; i <= 7; i++) {
            if (mCourseLayouts[i] != null) {
                mCourseLayouts[i].removeAllViews();
                mCourseLayouts[i].addView(dateItems[i]);
            }
        }
        if (dateTexts != null) {
            int length = dateTexts.length < 7 ? dateTexts.length : 7;
            for (int i = 0; i < length; i++) {
                dateItems[i + 1].setDateText(dateTexts[i]);
            }
        }
        courseToView.clear();
        if (courses == null || courses.isEmpty()) return;
        for (ToCourseBase course : courses) {
            courseToView.put(course.toCourseBase(), null);
        }
        for (Map.Entry<CourseBase, View> entry : courseToView.entrySet()) {
            if (entry.getValue() == null && mHeight != 0) {
                realAddCourseItemView(entry.getKey());
            }
        }
    }

    private void realAddSideItemView(String indexText, String time) {
        SideItem itemView = new SideItem(getContext())
                .setIndexText(indexText)
                .setIndexTextColor(mSideTextColor)
                .isShowTimeText(mShowTimeTexts)
                .setTimeTextColor(mSideTextColor);
        if (time != null) {
            itemView.setTimeText(time);
        }
        mSideLayout.addView(itemView, mSideWidth, mRowItemHeight);
    }

    private void realAddCourseItemView(CourseBase course) {
        View itemView = createCourseItemView(course);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mRowItemHeight * course.getNodeCnt());
        params.topMargin = (course.getBeginNode() - 1) * mRowItemHeight + mDateHeight;
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        Log.d(TAG, "topMargin: " + params.topMargin + ", mRowItemHeight: " + mRowItemHeight);
        mCourseLayouts[course.getWeekday()].addView(itemView, params);
        courseToView.put(course, itemView);
    }

    //创建CourseItemView
    private View createCourseItemView(final CourseBase course) {
        final FrameLayout backgroundLayout = new FrameLayout(getContext());
        // 绘制TextView
        TextView tv = new TextView(getContext());
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        tv.setLayoutParams(params);
        tv.setTextColor(mCourseTextColor);
        tv.setPadding(mCourseLRPadding, mCourseTBPadding, mCourseLRPadding, mCourseTBPadding);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mCourseTextSize);
        tv.setGravity(Gravity.CENTER);
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
        params.setMargins(mCourseLRMargin, mCourseTBMargin, mCourseLRMargin, mCourseTBMargin);
        tv.setLayoutParams(params);
        tv.setText(course.getText());
        // background
        if (course.getColor() == -1) {
            if (colorMap.containsKey(course.getText())) {
                backgroundLayout.setBackgroundColor(colorMap.get(course.getText()));
            } else {
                backgroundLayout.setBackgroundColor(ColorUtils.colorList[colorIndex]);
                colorMap.put(course.getText(), ColorUtils.colorList[colorIndex]);
                colorIndex = (colorIndex + 1) % ColorUtils.colorList.length;
            }
        } else {
            backgroundLayout.setBackgroundColor(course.getColor());
        }
        // 初始化点击事件
        initEvent(backgroundLayout, course);
        backgroundLayout.addView(tv);
        return backgroundLayout;
    }

    private void ensureCornerText() {
        if (cornerTV == null) {
            cornerTV = new TextView(getContext());
            cornerTV.setTextColor(mDateTextColor);
            cornerTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            cornerTV.setGravity(Gravity.CENTER);
            cornerTV.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            cornerTV.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    public void setCornerText(String text) {
        ensureCornerText();
        cornerTV.setText(text);
    }

    public void setBeginTimeTexts(List<String> timeTexts) {
        this.timeTexts = timeTexts;
    }

    public void setDateTexts(String[] dateTexts) {
        this.dateTexts = dateTexts;
        if (mFirstDraw) return;
        if (dateTexts != null) {
            int length = dateTexts.length < 7 ? dateTexts.length : 7;
            for (int i = 0; i < length; i++) {
                dateItems[i + 1].setDateText(dateTexts[i]);
            }
        }
    }

    public String[] getDateTexts() {
        return dateTexts;
    }

    public void setSideTextColor(int color) {
        mSideTextColor = color;
    }

    public int getSideTextColor() {
        return mSideTextColor;
    }

    public int getDateTextColor() {
        return mDateTextColor;
    }

    public void setDateTextColor(int mDateTextColor) {
        this.mDateTextColor = mDateTextColor;
    }

    public void setShowTimeTexts(boolean isShow) {
        mShowTimeTexts = isShow;
        if (mSideLayout == null) return;
        for (int i = 0; i < mSideLayout.getChildCount(); i++) {
            View view = mSideLayout.getChildAt(i);
            if (view instanceof SideItem) {
                ((SideItem) view).isShowTimeText(isShow);
            }
        }
    }

    public boolean isShowTimeTexts() {
        return mShowTimeTexts;
    }

    public void setShowHorizontalDivider(boolean isShow) {
        mShowHorizontalDivider = isShow;
    }

    public boolean isShowHorizontalDivider() {
        return mShowHorizontalDivider;
    }

    public void setShowVerticalDivider(boolean isShow) {
        mShowVerticalDivider = isShow;
    }

    public boolean isShowVerticalDivider() {
        return mShowVerticalDivider;
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setRowCount(int rowCount) {
        this.mRowCount = rowCount;
        mRowItemHeight = mHeight / mRowCount;
    }

    public int getRowCount() {
        return mRowCount;
    }

    public void setCourseTextSize(int textSize) {
        mCourseTextSize = textSize;
        for (View layout : courseToView.values()) {
            if (layout != null) {
                View view = ((ViewGroup) layout).getChildAt(0);
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                }
            }
        }
    }

    public int getCourseTextSize() {
        return mCourseTextSize;
    }

    /**
     * 设置每周的第一天
     *
     * @param firstDayOfWeek {@link Calendar}
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    /**
     * 需调用{@link #redraw()}, 才能显示正确课表
     *
     * @param courses
     * @param <T>
     */
    public <T extends ToCourseBase> void replaceCourseData(List<T> courses) {
        this.courses = courses;
        if (mFirstDraw) return;
        for (FrameLayout mCourseLayout : mCourseLayouts) {
            if (mCourseLayout != null) {
                mCourseLayout.removeAllViews();
            }
        }
        initCourseItemView();
    }

    private void initEvent(View v, final CourseBase course) {
        v.setOnClickListener(v1 -> {
            if (onCourseClickListener != null) {
                onCourseClickListener.onClick(v1, course);
            }
        });
        v.setOnLongClickListener(v12 -> {
            if (onCourseLongClickListener != null) {
                return onCourseLongClickListener.onLongClick(v12, course);
            }
            return false;
        });
    }

    public void setOnCourseClickListener(OnCourseClickListener listener) {
        this.onCourseClickListener = listener;
    }

    public OnCourseClickListener getOnCourseClickListener() {
        return onCourseClickListener;
    }

    private void drawSplitLine(Canvas canvas) {
        //水平分割线
        if (mShowHorizontalDivider) {
            for (int i = 0; i <= mRowCount; i++) {
                mLinePath.reset();
                int y = i * mRowItemHeight + mDateHeight;
                mLinePath.moveTo(0, y);
                mLinePath.lineTo(mWidth, y);
                canvas.drawPath(mLinePath, mLinePaint);
            }
        }

        //垂直分割线
        if (mShowVerticalDivider) {
            for (int i = 0; i < mColCount; i++) {
                mLinePath.reset();
                int x = i * mColItemWidth + mSideWidth;
                mLinePath.moveTo(x, 0);
                mLinePath.lineTo(x, mHeight);
                canvas.drawPath(mLinePath, mLinePaint);
            }
        }
    }

    public void setTextMargin(int textLRMargin, int textTBMargin) {
        this.mCourseLRMargin = textLRMargin;
        this.mCourseTBMargin = textTBMargin;
    }

    public void setTextPadding(int textLRPadding, int textTBPadding) {
        this.mCourseLRPadding = textLRPadding;
        this.mCourseTBPadding = textTBPadding;
    }

    public void redraw() {
        if (mFirstDraw) return;
        initCourseItemView();
        initSideItemView();
    }

    private class SideItem extends RelativeLayout {

        private TextView timeTV;
        private TextView indexTV;

        public SideItem(Context context) {
            super(context);
            // 添加时间TextView
            timeTV = drawTextView(8, ALIGN_PARENT_TOP, CENTER_HORIZONTAL);
            this.addView(timeTV);
            // 添加节数TextView
            indexTV = drawTextView(14, CENTER_IN_PARENT);
            this.addView(indexTV);
        }

        private SideItem setTimeTextColor(int color) {
            timeTV.setTextColor(color);
            return this;
        }

        private SideItem setIndexTextColor(int color) {
            indexTV.setTextColor(color);
            return this;
        }

        public SideItem setTimeText(String time) {
            timeTV.setText(time);
            return this;
        }

        public SideItem setIndexText(String nodeNum) {
            indexTV.setText(nodeNum);
            return this;
        }

        public SideItem isShowTimeText(boolean show) {
            if (show) {
                timeTV.setVisibility(VISIBLE);
            } else {
                timeTV.setVisibility(GONE);
            }
            return this;
        }

        private TextView drawTextView(int textSize, int... verb) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(textSize);
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            for (int v : verb) {
                lp.addRule(v);
            }
            textView.setLayoutParams(lp);
            return textView;
        }

    }

    private class DateItem extends LinearLayout {

        private TextView weekdayTV;
        private TextView dateTV;

        public DateItem(Context context) {
            super(context);
            weekdayTV = createTextView(true, 12);
            dateTV = createTextView(false, 10);
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

        public void setWeekdayColor(int weekdayColor) {
            weekdayTV.setTextColor(weekdayColor);
        }

        public void setDateColor(int dateColor) {
            dateTV.setTextColor(dateColor);
        }

        private TextView createTextView(boolean bold, int size) {
            TextView textView = new TextView(getContext());
            textView.getPaint().setFakeBoldText(bold);
            textView.setTextSize(size);
            textView.setGravity(Gravity.CENTER);
            return textView;
        }
    }
}
