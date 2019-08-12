package cn.ifafu.ifafu.mvp.other;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jaeger.library.StatusBarUtil;

import java.util.Locale;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.util.GlobalLib;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setTransparent(this);
        Toolbar toolbar = findViewById(R.id.tb_about);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView aboutAppSubName = findViewById(R.id.aboutAppSubName);

        aboutAppSubName.setText(String.format(
                Locale.getDefault(), getString(R.string.app_sub_name),
                GlobalLib.getLocalVersionName(this)));

        findViewById(R.id.gotoGroupAbout).setOnClickListener(this);
        findViewById(R.id.btn_goto_qq_group).setOnClickListener(this);
        findViewById(R.id.btn_goto_weibo).setOnClickListener(this);
        findViewById(R.id.btn_goto_email).setOnClickListener(this);
    }

    private void linkTo(String url) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_goto_qq_group:
                linkTo("https://jq.qq.com/?_wv=1027&k=5BwhG6k");
                break;
            case R.id.btn_goto_weibo:
                linkTo("https://weibo.com/u/5363314862");
                break;
            case R.id.btn_goto_email:
                String email = "support@ifafu.cn";
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    cm.setPrimaryClip(ClipData.newPlainText("Label", email));
                    Toast.makeText(this, R.string.success_copy_email, Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(email));
                startActivity(Intent.createChooser(intent, "选择发送应用"));
                break;
        }
    }
}
