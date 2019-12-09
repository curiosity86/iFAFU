package cn.ifafu.ifafu.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

import cn.ifafu.ifafu.R;

public class ProgressDialog extends Dialog {

    private TextView loadingTV;

    private String mText;

    public ProgressDialog(@NonNull Context context) {
        this(context, R.style.Dialog_Loading);
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.progress_dialog);
        setCanceledOnTouchOutside(false);
        loadingTV = findViewById(R.id.tv_progress_text);
        ProgressBar progressBar = findViewById(R.id.pb_progress);
        Sprite fadingCircle = new CubeGrid();
        fadingCircle.setColor(Color.WHITE);
        progressBar.setIndeterminateDrawable(fadingCircle);
    }

    public void setText(String text) {
        loadingTV.setText(text);
    }

    public void setText(@StringRes int stringRes) {
        loadingTV.setText(stringRes);
    }

    @Override
    public void show() {
        super.show();
    }

    public void show(String text) {
        if (!text.equals(mText)) {
            mText = text;
            loadingTV.setText(text);
        }
        super.show();
    }
}
