package cn.ifafu.ifafu.view.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.github.ybq.android.spinkit.style.Wave;

import cn.ifafu.ifafu.R;

public class ProgressDialog extends Dialog {

    private TextView textView;

    private ProgressBar progressBar;

    private String mText;

    public ProgressDialog(@NonNull Context context) {
        this(context, 0);
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_progress);
        setCancelable(false);
        textView = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.pb_progress);
        Sprite fadingCircle = new ThreeBounce();
        fadingCircle.setColor(context.getColor(R.color.ifafu_blue));
        progressBar.setIndeterminateDrawable(fadingCircle);
    }

    @Override
    public void show() {
        super.show();
    }

    public void show(String text) {
        if (!text.equals(mText)) {
            mText = text;
            textView.setText(text);
        }
        super.show();
    }
}
