package cn.ifafu.ifafu.view.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TintTypedArray;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.util.DensityUtils;

public class WToolbar extends RelativeLayout {

    private ImageButton mNavButtonView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;

    private int mTitleTextAppearance;
    private int mSubtitleTextAppearance;

    private CharSequence mTitleText;
    private CharSequence mSubtitleText;

    private ColorStateList mTitleTextColor;
    private ColorStateList mSubtitleTextColor;

    private LinearLayout mTitleLinearLayout;

    private OnClickListener mTitleClickListener;
    private OnClickListener mSubtitleClickListener;

    public WToolbar(Context context) {
        this(context, null);
    }

    public WToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.wToolbarStyle);
    }

    public WToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.WToolbar, defStyleAttr, 0);

        if (a.hasValue(R.styleable.WToolbar_elevation)) {
            setElevation(a.getDimension(R.styleable.WToolbar_elevation, 0));
        }

        mTitleTextAppearance = a.getResourceId(R.styleable.WToolbar_titleTextAppearance, 0);
        mSubtitleTextAppearance = a.getResourceId(R.styleable.WToolbar_subtitleTextAppearance, 0);

        final CharSequence title = a.getText(R.styleable.WToolbar_title);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        final CharSequence subtitle = a.getText(R.styleable.WToolbar_subtitle);
        if (!TextUtils.isEmpty(subtitle)) {
            setSubtitle(subtitle);
        }

        final Drawable navIcon = a.getDrawable(R.styleable.WToolbar_navigationIcon);
        if (navIcon != null) {
            setNavigationIcon(navIcon);
        }
        final CharSequence navDesc = a.getText(R.styleable.WToolbar_navigationContentDescription);
        if (!TextUtils.isEmpty(navDesc)) {
            setNavigationContentDescription(navDesc);
        }
    }

    public CharSequence getTitle() {
        return mTitleText;
    }

    public void setTitle(@StringRes int resId) {
        setTitle(getContext().getText(resId));
    }

    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            if (mTitleTextView == null) {
                final Context context = getContext();
                mTitleTextView = new AppCompatTextView(context);
                LayoutParams lp = (LayoutParams) generateDefaultLayoutParams();
                mTitleTextView.setLayoutParams(lp);
                mTitleTextView.setSingleLine();
                mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                mTitleTextView.setOnClickListener(mTitleClickListener);
                if (mTitleTextAppearance != 0) {
                    mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
                }
                if (mTitleTextColor != null) {
                    mTitleTextView.setTextColor(mTitleTextColor);
                }
                ensureTitleLinearLayout();
                mTitleLinearLayout.addView(mTitleTextView, 0);
            }
            mTitleTextView.setText(title);
            mTitleText = title;
        }
    }

    public CharSequence getSubtitle() {
        return mSubtitleText;
    }

    public void setSubtitle(@StringRes int resId) {
        setSubtitle(getContext().getText(resId));
    }

    public void setSubtitle(CharSequence subtitle) {
        if (!TextUtils.isEmpty(subtitle)) {
            ensureSubtitleTextView();
            mSubtitleTextView.setText(subtitle);
            mSubtitleText = subtitle;
        }
    }

    public void setTitleTextColor(@ColorInt int color) {
        setTitleTextColor(ColorStateList.valueOf(color));
    }

    public void setTitleTextColor(@NonNull ColorStateList color) {
        mTitleTextColor = color;
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(color);
        }
    }

    public void setTitleClickListener(OnClickListener listener) {
        if (mTitleTextView != null) {
            mTitleTextView.setOnClickListener(listener);
        }
        mTitleClickListener = listener;
    }

    public void setSubtitleTextColor(@ColorInt int color) {
        setSubtitleTextColor(ColorStateList.valueOf(color));
    }

    public void setSubtitleTextColor(@NonNull ColorStateList color) {
        mSubtitleTextColor = color;
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextColor(color);
        }
    }

    public void ensureSubtitleTextView() {
        if (mSubtitleTextView == null) {
            final Context context = getContext();
            mSubtitleTextView = new AppCompatTextView(context);
            mSubtitleTextView.setSingleLine();
            mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            mSubtitleTextView.setOnClickListener(mSubtitleClickListener);
            LayoutParams lp = (LayoutParams) generateDefaultLayoutParams();
            lp.addRule(CENTER_IN_PARENT);
            mSubtitleTextView.setLayoutParams(lp);
            if (mSubtitleTextAppearance != 0) {
                mSubtitleTextView.setTextAppearance(context, mSubtitleTextAppearance);
            }
            if (mSubtitleTextColor != null) {
                mSubtitleTextView.setTextColor(mSubtitleTextColor);
            }
            ensureTitleLinearLayout();
            mTitleLinearLayout.addView(mSubtitleTextView);
        }
    }

    @Nullable
    public final TextView getTitleTextView() {
        return mTitleTextView;
    }

    @Nullable
    public final TextView getSubtitleTextView() {
        return mSubtitleTextView;
    }

    public void setSubtitleClickListener(OnClickListener listener) {
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setOnClickListener(listener);
        }
        mSubtitleClickListener = listener;
    }

    public void setSubtitleDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top,
                                             @Nullable Drawable end, @Nullable Drawable bottom) {
        ensureSubtitleTextView();
        mSubtitleTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
    }

    public void setNavigationContentDescription(@Nullable CharSequence description) {
        if (!TextUtils.isEmpty(description)) {
            ensureNavButtonView();
        }
        if (mNavButtonView != null) {
            mNavButtonView.setContentDescription(description);
        }
    }

    @Nullable
    public Drawable getNavigationIcon() {
        return mNavButtonView != null ? mNavButtonView.getDrawable() : null;
    }

    public void setNavigationIcon(@Nullable Drawable icon) {
        if (icon != null) {
            ensureNavButtonView();
            addView(mNavButtonView);
        }
        if (mNavButtonView != null) {
            mNavButtonView.setImageDrawable(icon);
        }
    }

    public void setNavigationOnClickListener(OnClickListener listener) {
        ensureNavButtonView();
        mNavButtonView.setOnClickListener(listener);
    }

    private void ensureNavButtonView() {
        if (mNavButtonView == null) {
            mNavButtonView = new AppCompatImageButton(getContext());
            int dp24 = (int) DensityUtils.dp2px(getContext(), 36);
            LayoutParams lp = new LayoutParams(dp24, dp24);
            lp.addRule(CENTER_VERTICAL);
            lp.addRule(ALIGN_PARENT_START);
            lp.setMarginStart((int) DensityUtils.dp2px(getContext(), 8));
            int dp4 = (int) DensityUtils.dp2px(getContext(), 4);
            mNavButtonView.setPadding(dp4, dp4, dp4, dp4);
            mNavButtonView.setLayoutParams(lp);
        }
    }

    private void ensureTitleLinearLayout() {
        if (mTitleLinearLayout == null) {
            mTitleLinearLayout = new LinearLayout(getContext());
            mTitleLinearLayout.setOrientation(LinearLayout.VERTICAL);
            mTitleLinearLayout.setGravity(Gravity.CENTER);
            LayoutParams lp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.addRule(CENTER_IN_PARENT);
            mTitleLinearLayout.setLayoutParams(lp);
            addView(mTitleLinearLayout);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        ((LayoutParams) params).addRule(CENTER_VERTICAL);
        super.addView(child, index, params);
    }
}
