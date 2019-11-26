package com.example.jack.mobcomdemo.companent;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebViewClient;

public class PolicyWebView extends BaseWebView {
	public PolicyWebView(Context context) {
		super(context);
		setWebViewClient(new WebViewClient());
	}

	public PolicyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWebViewClient(new WebViewClient());
	}

	public PolicyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setWebViewClient(new WebViewClient());
	}
}
