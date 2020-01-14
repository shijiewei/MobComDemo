package com.example.jack.mobcomdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jack.mobcomdemo.entity.UiSettings;
import com.example.jack.mobcomdemo.ui.PrivacyDialog;
import com.example.jack.mobcomdemo.util.Const;
import com.example.jack.mobcomdemo.util.DemoResHelper;
import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.mob.PrivacyPolicy;
import com.mob.RHolder;
import com.mob.commons.authorize.DeviceAuthorizer;
import com.mob.commons.dialog.entity.InternalPolicyUi;
import com.mob.commons.dialog.entity.MobPolicyUi;
import com.mob.tools.utils.UIHandler;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private Button btn1;
	private Button policyTv;
	private Button policyWb;
	private Button openPolicyBtn;
	private Button openPermissionBtn;
	private Button isForbBtn;
	private PrivacyPolicy policyUrl;
	private PrivacyPolicy policyTxt;
	private Button toggleDialogDevSwitchBtn;
	private Button toggleDialogDevStyleBtn;
	private Button toggleDialogSdkContentBtn;
	private boolean dialogDevSwitch = true;
	private boolean dialogDevStyleDefault = true;
	private boolean dialogSdkContentDefault = false;
	private InternalPolicyUi internalPolicyUi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkPermissions();
		initView();
		getPolicy(false, MobSDK.POLICY_TYPE_URL, MobSDK.POLICY_TYPE_TXT);
//		startService(new Intent(this, MyService.class));
		testMobCommon();
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
			case R.id.btn_privacy_dialog: {
				openPrivacyDialog();
				break;
			}
			case R.id.btn_permission_dialog_dev_switch: {
				toggleDialogDevSwitch();
				break;
			}
			case R.id.btn_permission_dialog_dev_style: {
				toggleDialogDevStyle();
				break;
			}
			case R.id.btn_permission_dialog_sdk_content: {
				toggleDialogSdkContent();
				break;
			}
			case R.id.btn_permission_dialog: {
				openResubmitDialog();
				break;
			}
			case R.id.btn_isforb: {
				invokeIsForb();
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
					if (Const.PERMISSION_CONTACT.equals(p)) {
						continue;
					}
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
		openPolicyBtn = findViewById(R.id.btn_privacy_dialog);
		openPolicyBtn.setOnClickListener(this);
		isForbBtn = findViewById(R.id.btn_isforb);
		isForbBtn.setOnClickListener(this);
		toggleDialogDevSwitchBtn = findViewById(R.id.btn_permission_dialog_dev_switch);
		toggleDialogDevSwitchBtn.setOnClickListener(this);
		toggleDialogDevStyleBtn = findViewById(R.id.btn_permission_dialog_dev_style);
		toggleDialogDevStyleBtn.setOnClickListener(this);
		toggleDialogSdkContentBtn = findViewById(R.id.btn_permission_dialog_sdk_content);
		toggleDialogSdkContentBtn.setOnClickListener(this);
		openPermissionBtn = findViewById(R.id.btn_permission_dialog);
		openPermissionBtn.setOnClickListener(this);
	}

	private void getPolicy(final boolean autoJump, final int... types) {
		for (final int type : types) {
			if (type == MobSDK.POLICY_TYPE_URL) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						policyUrl = MobSDK.getPrivacyPolicy(MobSDK.POLICY_TYPE_URL);
						if (policyUrl != null) {
							if (autoJump) {
								gotoPolicyActivity(MobSDK.POLICY_TYPE_URL);
							}
						} else {
							UIHandler.sendEmptyMessage(0, new Handler.Callback() {
								@Override
								public boolean handleMessage(Message msg) {
									Toast.makeText(MainActivity.this,
											"type: url" + "\nCan not get privacy policy", Toast.LENGTH_SHORT).show();
									return false;
								}
							});
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
						Toast.makeText(MainActivity.this,
								"type: txt" + "\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
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

	private void testMobCommon() {
		// 模拟sdk接口调用isForb()
		invokeIsForb();
		// 设置二次确认框默认内容
		toggleDialogSdkContent();
		// 模拟sdk接口生成duid()（但其实没必要，sdk最先调用的肯定是isForb接口，其内部已经有锁控制了）
		new Thread(new Runnable() {
			@Override
			public void run() {
				String duid = DeviceAuthorizer.authorize(Const.PRODUCT);
				Log.d(TAG, "Got duid: " + duid);
			}
		}).start();
	}

	private void openPrivacyDialog() {
		OnDialogListener onDialogListener = new OnDialogListener() {
			@Override
			public void onAgree(boolean doNotAskAgain) {
				Log.d(TAG, "隐私协议弹框操作：同意");
				submitPrivacyGrantResult(true);
			}

			@Override
			public void onDisagree(boolean doNotAskAgain) {
				Log.d(TAG, "隐私协议弹框操作：拒绝");
				submitPrivacyGrantResult(false);
			}

			@Override
			public void onNotShow() {
				Toast.makeText(MainActivity.this, "Do not show privacy dialog since user selection", Toast.LENGTH_SHORT).show();
			}
		};
		UiSettings uiSettings = new UiSettings.Builder().setTitleTextId(R.string.smssdk_privacy_dialog_title).build();
		PrivacyDialog dialog = new PrivacyDialog(this, policyTxt, policyUrl, uiSettings, onDialogListener);
		dialog.show();
	}

	private void toggleDialogDevSwitch() {
		dialogDevSwitch = !dialogDevSwitch;
		// 开发者设置是否允许弹出二次确认框(默认true)
		// 需在使用SDK接口前调用，否则不生效
		MobSDK.setAllowDialog(dialogDevSwitch);
		Toast.makeText(this, "开发者开关已打开：" + dialogDevSwitch, Toast.LENGTH_SHORT).show();
	}

	private void toggleDialogDevStyle() {
		MobPolicyUi.Builder mobPolicyUi = new MobPolicyUi.Builder();
		dialogDevStyleDefault = !dialogDevStyleDefault;
		if (dialogDevStyleDefault) {
			// 开发者自定义弹窗样式（默认）
			mobPolicyUi
					.setBackgroundColorId(R.color.smssdk_common_white)
					.setPositiveBtnColorId(R.color.smssdk_common_main_color)
					.setNegativeBtnColorId(R.color.smssdk_common_white);
		} else {
			// 开发者自定义弹窗样式（自定义）
			mobPolicyUi
					.setBackgroundColorId(R.color.smssdk_test_color)
					.setPositiveBtnColorId(R.color.smssdk_common_text_gray)
					.setNegativeBtnColorId(R.color.smssdk_common_main_color);
		}
		Toast.makeText(this, "使用默认样式：" + dialogDevStyleDefault, Toast.LENGTH_SHORT).show();
		// 需在使用SDK接口前调用，否则不生效
		MobSDK.setPolicyUi(mobPolicyUi.build());
	}

	private void toggleDialogSdkContent() {
		InternalPolicyUi.Builder internalPolicyUiBuilder = new InternalPolicyUi.Builder();
		dialogSdkContentDefault = !dialogSdkContentDefault;
		if (dialogSdkContentDefault) {
			internalPolicyUiBuilder
					.setTitleText(DemoResHelper.getString(DemoResHelper.getStringRes(
							MainActivity.this, "mobdemo_authorize_dialog_title")))
					.setContentText(DemoResHelper.getString(DemoResHelper.getStringRes(
							MainActivity.this, "mobdemo_authorize_dialog_content")));
		} else {
			internalPolicyUiBuilder
					.setTitleText(DemoResHelper.getString(DemoResHelper.getStringRes(
							MainActivity.this, "mobdemo_authorize_dialog_title2")))
					.setContentText(DemoResHelper.getString(DemoResHelper.getStringRes(
							MainActivity.this, "mobdemo_authorize_dialog_content2")));
		}
		internalPolicyUi = internalPolicyUiBuilder.build();
		Toast.makeText(this, "使用默认内容：" + dialogSdkContentDefault, Toast.LENGTH_SHORT).show();
	}

	private void openResubmitDialog() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isForb = MobSDK.isForb();
				Log.d(TAG, "isForb: " + isForb);
				if (!isForb) {

					// 设置二次确认框的必要资源文件
					RHolder.getInstance()
							.setActivityThemeId(DemoResHelper.getStyleRes(MainActivity.this, "mobcommon_TranslucentTheme"))
							.setDialogThemeId(DemoResHelper.getStyleRes(MainActivity.this, "mobcommon_DialogStyle"))
							.setDialogLayoutId(DemoResHelper.getLayoutRes(MainActivity.this, "mob_authorize_dialog"));
					MobSDK.canIContinueBusiness(Const.PRODUCT, internalPolicyUi, new OperationCallback<Boolean>() {
						@Override
						public void onComplete(final Boolean data) {
							Log.d(TAG, "canIContinueBusiness: onComplete(), " + data);
							if (data) {
								onContinue();
							} else {
								onDisturb();
							}
						}

						@Override
						public void onFailure(Throwable t) {
							Log.d(TAG, "canIContinueBusiness: onFailure()", t);
							onDisturb();
						}
					});
				}
			}
		}).start();
	}

	private void requestContactPermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			String[] permissions = {Const.PERMISSION_CONTACT};
			this.requestPermissions(permissions, 1);
		}
	}

	private void submitPrivacyGrantResult(boolean granted) {
		MobSDK.submitPolicyGrantResult(granted, new OperationCallback<Void>() {
			@Override
			public void onComplete(Void data) {
				Log.d(TAG, "隐私协议授权结果提交：成功");
			}

			@Override
			public void onFailure(Throwable t) {
				Log.d(TAG, "隐私协议授权结果提交：失败");
			}
		});
	}

	private void invokeIsForb() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final boolean isForb = MobSDK.isForb();
				Log.d(TAG, "Got isForb: " + isForb);
				UIHandler.sendEmptyMessage(0, new Handler.Callback() {
					@Override
					public boolean handleMessage(Message message) {
						Toast.makeText(MainActivity.this, "isForb: " + isForb, Toast.LENGTH_SHORT).show();
						return false;
					}
				});
			}
		}).start();
	}

	private void onContinue() {
		UIHandler.sendEmptyMessage(0, new Handler.Callback() {
			@Override
			public boolean handleMessage(Message message) {
				Toast.makeText(MainActivity.this, "SDK业务：继续", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}

	private void onDisturb() {
		UIHandler.sendEmptyMessage(0, new Handler.Callback() {
			@Override
			public boolean handleMessage(Message message) {
				Toast.makeText(MainActivity.this, "SDK业务：终止", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}
}
