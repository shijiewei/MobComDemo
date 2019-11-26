package com.example.jack.mobcomdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.jack.mobcomdemo.companent.PolicyWebView;
import com.mob.MobSDK;
import com.mob.PrivacyPolicy;

public class PolicyActivity extends Activity {
	public static final String EXTRA_POLICY_TYPE = "extra_policy_type";
	public static final String EXTRA_POLICY_OBJECT = "extra_policy_object";

	private TextView titleTv;
	private TextView policyTv;
	private ScrollView policyContainerSv;
	private PolicyWebView policyWv;
	private int type;
	private PrivacyPolicy policy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_policy);
		initView();
		initData();
	}

	private void initView() {
		titleTv = findViewById(R.id.page_policy_title_tv);
		policyTv = findViewById(R.id.page_policy_tv);
		policyContainerSv = findViewById(R.id.page_policy_container_sv);
		policyWv = findViewById(R.id.page_policy_wv);
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			type = intent.getIntExtra(EXTRA_POLICY_TYPE, -1);
			policy = (PrivacyPolicy) intent.getSerializableExtra(EXTRA_POLICY_OBJECT);
			if (policy != null) {
				titleTv.setText(policy.getTitle());
				if (type == MobSDK.POLICY_TYPE_TXT) {
					policyTv.setText(Html.fromHtml(policy.getContent()));
				} else if (type == MobSDK.POLICY_TYPE_URL) {
					policyContainerSv.setVisibility(View.GONE);
					policyTv.setVisibility(View.GONE);
					policyWv.setVisibility(View.VISIBLE);
					policyWv.loadUrl(policy.getContent());
				}
			}
		}
	}
}
