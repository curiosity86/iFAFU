package cn.ifafu.ifafu.view.syllabus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.ifafu.ifafu.util.ColorUtils;
import cn.ifafu.ifafu.util.DensityUtils;
import cn.ifafu.ifafu.view.syllabus.data.CourseBase;
import cn.ifafu.ifafu.view.syllabus.data.ToCourse;

/**
 * Created by woolsen
 */
public class CourseView extends FrameLayout {

    //rootView's width and height
    private int mWidth;
    private int mHeight;
    // 列宽度
    private float mColItemWidth;
    // 行高度
    private float mRowItemHeight;

    // CourseView horizontal margin
    private int mCourseLRMargin = 0;
    // CourseView vertical margin
    private int mCourseTBMargin = 0;
    // CourseView text horizontal Padding
    private int mCourseLRPadding = (int) DensityUtils.dp2px(getContext(), 1);
    // CourseView text vertical Padding
    private int mCourseTBPadding = (int) DensityUtils.dp2px(getContext(), 1);
    // CourseView text color
    private int mCoureseTextColor = Color.WHITE;
    // CourseView text size
    private int mCourseTextSize = 12;
    // 行数
    private int mRowCount = 12;
    // 列数
    private int mColCount = 7;

    // 周的第一天
    private int firstDayOfWeek = Calendar.SUNDAY;
    // 课程周数对应的显示X轴偏移量
    private int firstDayOfWeekOffset = 6;

    // 是否显示分割线
    private boolean mShowHorizontalLine = true;
    private boolean mShowVerticalLine = true;

    // 绘制分割线
    private Paint mLinePaint;
    private Path mLinePath = new Path();

    // onCreate时首次绘制
    private boolean mFirstDraw = true;

    private OnCourseClickListener onCourseClickListener;
    private OnCourseLongClickListener onCourseLongClickListener;

    private Map<CourseBase, View> mCourseViewMap = new TreeMap<>((courseBase, t1) -> {
        int weekdayCompare = Integer.compare(courseBase.getWeekday(), t1.getWeekday());
        if (weekdayCompare == 0) {
            return Integer.compare(courseBase.getBeginNode(), t1.getBeginNode());
        } else {
            return weekdayCompare;
        }
    });

    private int colorIndex = 0;
    private Map<String, Integer> colorMap = new HashMap<>();

    public CourseView(Context context) {
        this(context, null);
    }

    public CourseView(Context context, AttributeSet attrs) {
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
        mWidth = w;
        mColItemWidth = 1F * mWidth / mColCount;
        mRowItemHeight = 1F * mHeight / mRowCount;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawSplitLine(canvas);
        super.dispatchDraw(canvas);
        if (mFirstDraw) {
            mFirstDraw = false;
            initCourseItemView();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        l("onMeasure", "width:", widthMeasureSpec & MEASURED_SIZE_MASK, "height:", heightMeasureSpec & MEASURED_SIZE_MASK);
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setRowCount(int rowCount) {
        this.mRowCount = rowCount;
        mRowItemHeight = 1F * mHeight / mRowCount;
    }

    public void setTextSize(int textSize) {
        mCourseTextSize = textSize;
    }

    /**
     * 设置每周的第一天
     *
     * @param firstDayOfWeek {@link Calendar}
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
        firstDayOfWeekOffset = (Calendar.SUNDAY - firstDayOfWeek + 6) % 7;
    }

    /**
     * 把数组中的数据全部添加到界面
     */
    private void initCourseItemView() {
        for (Map.Entry<CourseBase, View> entry : mCourseViewMap.entrySet()) {
            if (entry.getValue() == null && mHeight != 0) {
                realAddCourseItemView(entry.getKey());
            }
        }
    }

    public <T extends ToCourse> void addCourse(T course) {
        CourseBase base = course.toCourseBase();
        if (mCourseViewMap.get(base) == null) {
            realAddCourseItemView(course.toCourseBase());
        }
    }

    public <T extends ToCourse> void addCourse(List<T> courses) {
        for (T course : courses) {
            CourseBase base = course.toCourseBase();
            if (!mCourseViewMap.containsKey(base) || mCourseViewMap.get(base) == null) {
                realAddCourseItemView(course.toCourseBase());
            }
        }
    }

    /**
     * 需调用{@link #redraw()}, 才能显示正确课表
     *
     * @param courses
     * @param <T>
     */
    public <T extends ToCourse> void setCourses(List<T> courses) {
        mCourseViewMap.clear();
        if (courses == null) return;
        for (T t : courses) {
            mCourseViewMap.put(t.toCourseBase(), null);
        }
    }

    private void realAddCourseItemView(CourseBase course) {
        View itemView = createItemView(course);
        LayoutParams params = new LayoutParams((int) (mColItemWidth + 1),
                (int) (mRowItemHeight * course.getNodeCnt() + 1));
        params.leftMargin = (int) (((course.getWeekday() + firstDayOfWeekOffset) % 7) * mColItemWidth + 0.5);
        params.topMargin = (int) ((course.getBeginNode() - 1) * mRowItemHeight + 0.5);
        itemView.setLayoutParams(params);
        addView(course, itemView);
    }

    //创建CourseItemView
    private View createItemView(final CourseBase course) {
        final FrameLayout backgroundLayout = new FrameLayout(getContext());
        // 绘制TextView
        TextView tv = new TextView(getContext());
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        tv.setLayoutParams(params);
        tv.setTextColor(mCoureseTextColor);
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

    public void setOnCourseLongClickListener(OnCourseLongClickListener listener) {
        this.onCourseLongClickListener = listener;
    }

    public void addView(CourseBase courseBase, View view) {
        addView(view);
        mCourseViewMap.put(courseBase, view);
    }

    /**
     * 修改界面设置需调用此方法重新绘制
     */
    public void redraw() {
        removeAllViews();
        for (CourseBase base : mCourseViewMap.keySet()) {
            mCourseViewMap.put(base, null);
        }
        initCourseItemView();
    }

    private void drawSplitLine(Canvas canvas) {
        //水平分割线
        if (mShowHorizontalLine) {
            for (int i = 0; i <= mRowCount; i++) {
                mLinePath.reset();
                mLinePath.moveTo(0, (int) (i * mRowItemHeight + 0.5F));
                mLinePath.lineTo(mWidth, (int) (i * mRowItemHeight + 0.5F));
                canvas.drawPath(mLinePath, mLinePaint);
            }
        }

        //垂直分割线
        if (mShowVerticalLine) {
            for (int i = 0; i <= mColCount; i++) {
                mLinePath.reset();
                mLinePath.moveTo((int) (i * mColItemWidth + 0.5F), 0);
                mLinePath.lineTo((int) (i * mColItemWidth + 0.5F), mHeight);
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

    public interface OnCourseClickListener {
        void onClick(View v, CourseBase course);
    }

    public interface OnCourseLongClickListener {
        boolean onLongClick(View v, CourseBase course);
    }

    private void l(Object... msg) {
        String code = this.toString();
        code = code.substring(code.indexOf("{") + 1, code.indexOf(" V.E"));
        StringBuilder sb = new StringBuilder(code);
        sb.append("    ");
        for (Object s : msg) {
            sb.append(s).append(" ");
        }
    }

}
