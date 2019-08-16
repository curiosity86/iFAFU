package cn.ifafu.ifafu.view.syllabus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.ifafu.ifafu.util.DensityUtils;

/**
 * Created by woolsen
 */
public class SideView extends LinearLayout {

    //rootView's width and height
    private int mWidth;
    private int mHeight;

    // Item高度
    private float mRowItemHeight = DensityUtils.dp2px(getContext(), 55);
    // 行数
    private int mRowCount = 12;

    private final int timeColor = 0xFFAAAAAA;
    private final int nodeNumColor = 0xFF157EFB;

    // 是否显示分割线
    private boolean mShowHorizontalLine = true;
    // 绘制分割线
    private Paint mLinePaint;
    private Path mLinePath = new Path();

    // 上课时间
    private String[] timeTexts;
    private SideItem[] sideItems;

    // onCreate时首次绘制
    private boolean mFirstDraw;

    public SideView(Context context) {
        this(context, null);
    }

    public SideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(VERTICAL);
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
            initSideItemView();
            mFirstDraw = true;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
        mWidth = w;
        mRowItemHeight = 1.0F * mHeight / mRowCount;
    }

    private void initSideItemView() {
        removeAllViews();
        // 防止越界
        if (sideItems == null || sideItems.length < mRowCount) {
            sideItems = new SideItem[mRowCount];
        }
        for (int i = 0; i < mRowCount; i++) {
            String time = null;
            if (timeTexts != null && i < timeTexts.length) {
                time = timeTexts[i];
            }
            realAddSideItemView(i, String.valueOf(i + 1), time);
        }
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
    }

    private void realAddSideItemView(int index, String indexT, String time) {
        SideItem itemView = new SideItem(getContext());
        if (time != null) {
            itemView.setTimeText(time);
        }
        itemView.setIndexText(indexT);
        LayoutParams params = new LayoutParams(mWidth, (int) (mRowItemHeight + 0.5F));
        itemView.setLayoutParams(params);
        addView(itemView);
        sideItems[index] = itemView;
    }

    public void setRowCount(int rowCount) {
        this.mRowCount = rowCount;
    }

    public void setBeginTimeTexts(String[] timeTexts) {
        this.timeTexts = timeTexts;
    }

    public String[] getBeginTimeTexts() {
        return timeTexts;
    }

    class SideItem extends RelativeLayout {

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

        private void setTimeTextColor(int color) {
            timeTV.setTextColor(color);
        }

        private void setIndexTextColor(int color) {
            indexTV.setTextColor(color);
        }

        public void setTimeText(String time) {
            timeTV.setText(time);
        }

        public void setIndexText(String nodeNum) {
            indexTV.setText(nodeNum);
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

}
