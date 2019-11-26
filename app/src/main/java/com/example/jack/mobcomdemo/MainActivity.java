package com.example.jack.mobcomdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mob.MobSDK;
import com.mob.PrivacyPolicy;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private Button btn1;
	private Button policyTv;
	private Button policyWb;
	private PrivacyPolicy policyUrl;
	private PrivacyPolicy policyTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkPermissions();
		initView();
		getPolicy(false, MobSDK.POLICY_TYPE_URL, MobSDK.POLICY_TYPE_TXT);
//		startService(new Intent(this, MyService.class));
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.btn1: {
				TestPage testPage = new TestPage();
				testPage.show(MainActivity.this, null);
				break;
			}
			case R.id.btn_policy_txt: {
				if (policyTxt != null) {
					gotoPolicyActivity(MobSDK.POLICY_TYPE_TXT);
				} else {
					getPolicy(true, MobSDK.POLICY_TYPE_TXT);
				}
				break;
			}
			case R.id.btn_policy_url: {
				if (policyUrl != null) {
					gotoPolicyActivity(MobSDK.POLICY_TYPE_URL);
				} else {
					getPolicy(true, MobSDK.POLICY_TYPE_URL);
				}
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/* 检查使用权限 */
	protected void checkPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			try {
				PackageManager pm = getPackageManager();
				PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
				ArrayList<String> list = new ArrayList<String>();
				for (String p : pi.requestedPermissions) {
					if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
						list.add(p);
					}
				}
				if (list.size() > 0) {
					String[] permissions = list.toArray(new String[list.size()]);
					if (permissions != null) {
						requestPermissions(permissions, 1);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void initView() {
		btn1 = findViewById(R.id.btn1);
		btn1.setOnClickListener(this);
		policyTv = findViewById(R.id.btn_policy_txt);
		policyTv.setOnClickListener(this);
		policyWb = findViewById(R.id.btn_policy_url);
		policyWb.setOnClickListener(this);
	}

	private void getPolicy(final boolean autoJump, final int... types) {
		for (final int type : types) {
			if (type == MobSDK.POLICY_TYPE_URL) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						policyUrl = MobSDK.getPrivacyPolicy(MobSDK.POLICY_TYPE_URL);
						if (autoJump && policyUrl != null) {
							gotoPolicyActivity(MobSDK.POLICY_TYPE_URL);
						}
					}
				}).start();
			} else if (type == MobSDK.POLICY_TYPE_TXT) {
				MobSDK.getPrivacyPolicyAsync(MobSDK.POLICY_TYPE_TXT, new PrivacyPolicy.OnPolicyListener() {
					@Override
					public void onComplete(PrivacyPolicy data) {
						policyTxt = data;
						if (autoJump) {
							gotoPolicyActivity(MobSDK.POLICY_TYPE_TXT);
						}
					}

					@Override
					public void onFailure(Throwable t) {
							Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	private void gotoPolicyActivity(int type) {
		Intent intent = new Intent(MainActivity.this, PolicyActivity.class);
		if (type == MobSDK.POLICY_TYPE_URL) {
			intent.putExtra(PolicyActivity.EXTRA_POLICY_TYPE, MobSDK.POLICY_TYPE_URL);
			intent.putExtra(PolicyActivity.EXTRA_POLICY_OBJECT, policyUrl);
		} else if (type == MobSDK.POLICY_TYPE_TXT) {
			intent.putExtra(PolicyActivity.EXTRA_POLICY_TYPE, MobSDK.POLICY_TYPE_TXT);
			intent.putExtra(PolicyActivity.EXTRA_POLICY_OBJECT, policyTxt);
		}
		startActivity(intent);
	}
}
