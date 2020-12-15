package com.example.jack.mobcomdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jack.mobcomdemo.entity.UiSettings;
import com.example.jack.mobcomdemo.ui.PrivacyDialog;
import com.example.jack.mobcomdemo.util.Const;
import com.example.jack.mobcomdemo.util.Util;
import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.mob.PrivacyPolicy;
import com.mob.commons.COMMON;
import com.mob.commons.authorize.DeviceAuthorizer;
import com.mob.commons.dialog.entity.InternalPolicyUi;
import com.mob.tools.MobLog;
import com.mob.tools.utils.Data;
import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.ReflectHelper;
import com.mob.tools.utils.Strings;
import com.mob.tools.utils.UIHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private DeviceHelper device;
	private Button btn1;
	private Button policyTv;
	private Button policyWb;
	private Button openPolicyBtn;
	private Button openPermissionBtn;
	private Button isForbBtn;
	private Button isMobBtn;
	private PrivacyPolicy policyUrl;
	private PrivacyPolicy policyTxt;
	private Button loopRequestIsAuthBtn;
	private Button isAuthBtn;
	private Button makeSdkErrBtn;
	private Button makeCrashBtn;
	private Button deviceKeyBtn;
	private Button isGpVerBtn;
	private Button isGpAvailableBtn;

	@SuppressLint("MissingPermission")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		device = DeviceHelper.getInstance(this);

		checkPermissions();
		initView();
		startService(new Intent(this, MyService.class));
		testMobCommon();
		tempTestHere();
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
			case R.id.btn_permission_dialog: {
				openResubmitDialog();
				break;
			}
			case R.id.btn_isforb: {
				invokeIsForb();
				break;
			}
			case R.id.btn_isMob: {
				invokeIsMob();
				break;
			}
			case R.id.btn_isAuth: {
				invokeIsAuth();
				break;
			}
			case R.id.btn_loop_isAuth: {
				loopRequestIsAuth();
				break;
			}
			case R.id.btn_make_sdk_err: {
				makeSdkErr();
				break;
			}
			case R.id.btn_make_crash: {
				makeCrash();
				break;
			}
			case R.id.btn_get_device_key: {
				getDeviceKey();
				break;
			}
			case R.id.btn_is_gp_ver: {
				isGppVer();
				break;
			}
			case R.id.btn_is_gp_available: {
				isGpAvailable();
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
				String[] requestedPermissions = pi.requestedPermissions;
				if (requestedPermissions != null && requestedPermissions.length > 0) {
					for (String p : pi.requestedPermissions) {
						if (Const.PERMISSION_CONTACT.equals(p)) {
							continue;
						}
						if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
							list.add(p);
						}
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
		isMobBtn = findViewById(R.id.btn_isMob);
		isMobBtn.setOnClickListener(this);
		loopRequestIsAuthBtn = findViewById(R.id.btn_loop_isAuth);
		loopRequestIsAuthBtn.setOnClickListener(this);
		isAuthBtn = findViewById(R.id.btn_isAuth);
		isAuthBtn.setOnClickListener(this);
		openPermissionBtn = findViewById(R.id.btn_permission_dialog);
		openPermissionBtn.setOnClickListener(this);
		makeSdkErrBtn = findViewById(R.id.btn_make_sdk_err);
		makeSdkErrBtn.setOnClickListener(this);
		makeCrashBtn = findViewById(R.id.btn_make_crash);
		makeCrashBtn.setOnClickListener(this);
		deviceKeyBtn = findViewById(R.id.btn_get_device_key);
		deviceKeyBtn.setOnClickListener(this);
		isGpVerBtn = findViewById(R.id.btn_is_gp_ver);
		isGpVerBtn.setOnClickListener(this);
		isGpAvailableBtn = findViewById(R.id.btn_is_gp_available);
		isGpAvailableBtn.setOnClickListener(this);
	}

	private void getPolicy(final boolean autoJump, final int... types) {
		for (final int type : types) {
			if (type == MobSDK.POLICY_TYPE_URL) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 指定隐私协议语言类型，为空或不指定时，默认根据系统语言选择
						Locale locale = getResources().getConfiguration().locale;
						policyUrl = MobSDK.getPrivacyPolicy(MobSDK.POLICY_TYPE_URL, locale);
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
				// 指定隐私协议语言类型，为空或不指定时，默认根据系统语言选择
				Locale locale = getResources().getConfiguration().locale;
				MobSDK.getPrivacyPolicyAsync(MobSDK.POLICY_TYPE_TXT, locale, new PrivacyPolicy.OnPolicyListener() {
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
		// 模拟sdk接口生成duid()（但其实没必要，sdk最先调用的肯定是isForb接口，其内部已经有锁控制了）
		new Thread(new Runnable() {
			@Override
			public void run() {
//				String duid = DeviceAuthorizer.authorize(new COMMON());
//				Log.d(TAG, "Got duid: " + duid);
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

	@Deprecated
	private void openResubmitDialog() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isForb = MobSDK.isForb();
				Log.d(TAG, "isForb: " + isForb);
				if (!isForb) {
					// 设置二次确认框的开发者自定义UI元素
					InternalPolicyUi internalPolicyUi = new InternalPolicyUi.Builder().build();
					MobSDK.canIContinueBusiness(new COMMON(), internalPolicyUi, new OperationCallback<Boolean>() {
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

	private void invokeIsMob() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final boolean isMob = MobSDK.isMob();
				Log.d(TAG, "Got isMob: " + isMob);
				UIHandler.sendEmptyMessage(0, new Handler.Callback() {
					@Override
					public boolean handleMessage(Message message) {
						Toast.makeText(MainActivity.this, "isMob: " + isMob, Toast.LENGTH_SHORT).show();
						return false;
					}
				});
			}
		}).start();
	}

	private int invokeIsAuth() {
		int isAuth = MobSDK.isAuth();
		Log.d(TAG, "Got isAuth: " + isAuth);
		String msg;
		switch (isAuth) {
			case 2: {
				msg = "2:不走隐私合规";
				break;
			}
			case 1: {
				msg = "1:已同意隐私";
				break;
			}
			case 0: {
				msg = "0:授权状态未知";
				break;
			}
			default: {
				msg = "-1:不同意隐私";
				break;
			}
		}
		Toast.makeText(MainActivity.this, "isAuth：" + msg, Toast.LENGTH_SHORT).show();
		return isAuth;
	}

	private void loopRequestIsAuth() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				boolean isForb = MobSDK.isForb();
				Log.d(TAG, "isForb: " + isForb);
				if (!isForb) {
					final Handler handler = new Handler(Looper.myLooper());
					Runnable task = new Runnable() {
						@Override
						public void run() {
							int isAuth = invokeIsAuth();
							switch (isAuth) {
								case 2: {
									// 2:不走隐私合规
								}
								case 1: {
									// 1:已同意隐私
									onContinue();
									handler.removeCallbacks(this);
									break;
								}
								case 0: {
									// 0:授权状态未知
									handler.postDelayed(this, 3000);
									break;
								}
								default: {
									// -1:不同意隐私
									onDisturb();
									handler.removeCallbacks(this);
									break;
								}
							}
						}
					};
					handler.post(task);
				}
				Looper.loop();
			}
		}).start();
	}

	private void makeSdkErr() {
		MobLog.getInstance().sdkErr("test sdk error: " + Util.getDatetime());
	}

	private void makeCrash() {
		throw new RuntimeException("test crash: " + Util.getDatetime());
	}

	private void getDeviceKey() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String adid = null;
				String adidHash = null;
				String deviceKey = null;
				try {
					adid = device.getAdvertisingID();
					if (!TextUtils.isEmpty(adid)) {
						byte[] bytes = Data.SHA1(adid);
						adidHash = Data.byteToHex(bytes);
					}
					deviceKey = device.getDeviceKey();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
				final String msg = "adid: " + adid + "\nadidHash: " + adidHash + "\ndeviceKey: " + deviceKey;
				Log.d(TAG, msg);
				UIHandler.sendEmptyMessage(0, new Handler.Callback() {
					@Override
					public boolean handleMessage(Message message) {
						Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
						return false;
					}
				});
			}
		}).start();
	}

	/**
	 * 判断公共库是否GP版本
	 * 使用注意：
	 * 1. 必须使用反射调用，防止"新版本SDK+老公共库"情况下（定制版公共库通常更新较慢），发生crash
	 */
	private boolean isGppVer() {
		boolean isGppVer = false;
		try {
			String mobSDKClass = ReflectHelper.importClass("com.mob.MobSDK");
			isGppVer = ReflectHelper.invokeStaticMethod(mobSDKClass, "isGppVer");
			Toast.makeText(this, "isGppVer: " + isGppVer, Toast.LENGTH_SHORT).show();
			Log.d(TAG, ">>>>> Has isGppVer <<<<<");
		} catch (Throwable t) {
			Toast.makeText(this, "Method [isGppVer] not found", Toast.LENGTH_SHORT).show();
			Log.d(TAG, ">>>>> No isGppVer <<<<<");
		}
		return isGppVer;
	}

	private void isGpAvailable() {
		try {
			String mobSDKClass = ReflectHelper.importClass("com.mob.MobSDK");
			Boolean val = ReflectHelper.invokeStaticMethod(mobSDKClass, "isGpAvailable");
			String msg = val == null ? "未知" : String.valueOf(val.booleanValue());
			Toast.makeText(this, "isGpAvailable: " + msg, Toast.LENGTH_SHORT).show();
			Log.d(TAG, ">>>>> Has isGpAvailable <<<<<");
		} catch (Throwable t) {
			Toast.makeText(this, "Method [isGpAvailable] not found", Toast.LENGTH_SHORT).show();
			Log.d(TAG, ">>>>> No isGpAvailable <<<<<");
		}
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

	private void log(String msg) {
		Log.e("jackieee", msg);
	}

	/**
	 * 临时的测试代码可以放在这里
	 */
	private void tempTestHere() {
		new Thread(new Runnable() {
			@Override
			public void run() {
			}
		}).start();
	}

	private void testDeviceHelper() {
		new Thread() {
			public void run() {
				try {
					System.out.println("wenjun Strings size: " + Strings.STRINGS.size());
					for (String item : Strings.STRINGS) {
						System.out.println("wenjun Strings item: " + item);
					}

					DeviceHelper deviceHelper = DeviceHelper.getInstance(getApplicationContext());

					Method[] list = DeviceHelper.class.getDeclaredMethods();
					for (Method method : list) {
						boolean isStatic = Modifier.isStatic(method.getModifiers());
						method.setAccessible(true);
						Class<?>[] types = method.getParameterTypes();
						Object result = null;
						if (types == null || types.length == 0) {
							try {
								result = method.invoke(deviceHelper);
							} catch (Throwable t) {
								t.printStackTrace();
							}
						} else {
							Object[] objects = new Object[types.length];
							for (int i = 0; i < types.length; i++) {
								objects[i] = null;
							}
							try {
								result = method.invoke(deviceHelper, objects);
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
						if (result == null) {
							System.out.println("wenjun method 1.1 = " + method.getName() + ", isStatic = " + isStatic);
						} else {
							System.out.println("wenjun method 1 = " + method.getName() + ", isStatic = " + isStatic + ", result = " + result);
						}
					}

				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}.start();
	}
}
