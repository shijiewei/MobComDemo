package com.example.jack.mobcomdemo.companent;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class BaseWebView extends WebView {
	public BaseWebView(Context context) {
		super(context);
		initSettings();
	}

	public BaseWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSettings();
	}

	public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initSettings();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			removeJavascriptInterface("searchBoxJavaBridge_");
			removeJavascriptInterface("accessibility");
			removeJavascriptInterface("accessibilityTraversal");
		}
	}

	private void initSettings() {
		setVerticalScrollBarEnabled(false);
		setHorizontalScrollBarEnabled(false);

		WebSettings settings = getSettings();
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setUseWideViewPort(true);
		settings.setJavaScriptEnabled(true);
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

		//小米2的兼容问题
		if (Build.MODEL != null && Build.MODEL.toLowerCase().startsWith("mi 2") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		//TODO
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setWebContentsDebuggingEnabled(true);
		}
	}

}