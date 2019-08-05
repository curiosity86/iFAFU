package cn.ifafu.ifafu.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.StringRes;

import cn.ifafu.ifafu.R;

public class LoadingDialog extends AlertDialog {

    private TextView loadingTV;

    public LoadingDialog(Context context) {
        super(context, R.style.Dialog_Loading);
        setContentView(R.layout.dialog_loading);
        loadingTV = findViewById(R.id.tv_loading_text);
    }

    public void show(String text) {
        loadingTV.setText(text);
        super.show();
    }

    public LoadingDialog setText(String text) {
        loadingTV.setText(text);
        return this;
    }

    public LoadingDialog setText(@StringRes int stringRes) {
        loadingTV.setText(stringRes);
        return this;
    }

    public LoadingDialog cancelable(boolean flag) {
        this.setCancelable(flag);
        return this;
    }
}
