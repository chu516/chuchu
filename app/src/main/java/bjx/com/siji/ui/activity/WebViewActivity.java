package bjx.com.siji.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import bjx.com.siji.R;
import bjx.com.siji.widget.CircleProgressBar;


/**
 * Created by jqc on 2017/9/28.
 */

public class WebViewActivity extends BaseActivity {

    ImageView mIVBack;
    TextView mTVTitle;
    WebView mWebView;
    CircleProgressBar progress1;
    String mUrl;
    String mParam;
    String backToMain;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_webview);
        mIVBack = getViewById(R.id.back);
        mTVTitle = getViewById(R.id.title);
        mWebView = getViewById(R.id.webView);
        progress1 = getViewById(R.id.progress1);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    progress1.setVisibility(View.INVISIBLE);
                } else {
                    // 加载中
                    if (progress1.getVisibility() != View.VISIBLE)
                        progress1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, final String message, JsResult result) {
                if (message.trim().equals("200")) {
                    new AlertDialog.Builder(WebViewActivity.this)
                            .setTitle("提示")
                            .setMessage("操作成功")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if("1".equals(backToMain)){
                                        startActivity(new Intent(mApp,MainActivity.class));
                                    }
                                    finish();
                                }
                            })
                            .show();
                } else {
                    new AlertDialog.Builder(WebViewActivity.this)
                            .setTitle("提示")
                            .setMessage(message)
                            .setPositiveButton("确定", null)
                            .show();
                }
                result.confirm();//这里必须调用，否则页面会阻塞造成假死
                return true;
            }
        });
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setJavaScriptEnabled(true);//支持js
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//允许js弹出窗
    }

    @Override
    protected void setListener() {
        mIVBack.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        mUrl = getIntent().getStringExtra("url");
        mTVTitle.setText(getIntent().getStringExtra("title"));
        if (mUrl == null || mUrl.equals("")) {
            mUrl = "about:blank";
        }
        mParam = getIntent().getStringExtra("param");
        if (mParam != null && !mParam.equals("")) {
            mWebView.postUrl(mUrl, mParam.getBytes());
        } else {
            mWebView.loadUrl(mUrl);
        }
        backToMain=getIntent().getStringExtra("backToMain");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if("1".equals(backToMain)){
                    startActivity(new Intent(mApp,MainActivity.class));
                }
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //重写onKeyDown，当浏览网页，WebView可以后退时执行后退操作。
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
