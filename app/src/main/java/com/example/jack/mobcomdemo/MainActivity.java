package com.example.jack.mobcomdemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.jack.mobcomdemo.entity.UiSettings;
import com.example.jack.mobcomdemo.ui.PrivacyDialog;
import com.example.jack.mobcomdemo.util.Const;
import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.mob.PrivacyPolicy;
import com.mob.commons.COMMON;
import com.mob.commons.authorize.DeviceAuthorizer;
import com.mob.commons.dialog.entity.InternalPolicyUi;
import com.mob.tools.MobLog;
import com.mob.tools.utils.DeviceHelper;
import com.mob.tools.utils.SmltHelper;
import com.mob.tools.utils.UIHandler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements View.OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkPermissions();
		initView();
		getPolicy(false, MobSDK.POLICY_TYPE_URL, MobSDK.POLICY_TYPE_TXT);
		startService(new Intent(this, MyService.class));
		testMobCommon();
		log("defaultInputMethodAppName: " + getDefaultInputMethodAppName(getDefaultInputMethodPkgName(this)));
		getInputMethodList();

		testAlbumCount();
		Log.e("jackieee", "isProxy: " + isWifiProxy(this));
		Log.e("jackieee", "ip: " + getIp());
		Log.e("jackieee", "wifiGateway: " + getWifiGateway());
		Log.e("jackieee", "gateway: " + getGateway());
		Log.e("jackieee", "gatewayForStatic: " + getGatewayForStatic());
		getDeviceMemo();
		log("Device mem usage: " + DeviceHelper.getInstance(this).getDeviceMemUsage());
		getAppMemory();
		getCpuUsage();
		log("getProcessCpuRate: " + getProcessCpuRate());
		log("getCpuRate: " + getCpuRate());
		log("isCpu64: " + isCPU64());
		testSimulatorHelper();
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
				String duid = DeviceAuthorizer.authorize(new COMMON());
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

	/**
	 * 获取默认输入法
	 *
	 * 不需要 任何权限
	 *
	 * android 11 测试可行
	 *
	 * @param context
	 * @return
	 */
	private String getDefaultInputMethodPkgName(Context context) {
		String mDefaultInputMethodPkg = null;

		String mDefaultInputMethodCls = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.DEFAULT_INPUT_METHOD);
		//输入法类名信息
		log("mDefaultInputMethodCls=" + mDefaultInputMethodCls);
		if (!TextUtils.isEmpty(mDefaultInputMethodCls)) {
			//输入法包名
			mDefaultInputMethodPkg = mDefaultInputMethodCls.split("/")[0];
			log("mDefaultInputMethodPkg=" + mDefaultInputMethodPkg);
		}
		return mDefaultInputMethodPkg;
	}

	private String getDefaultInputMethodAppName(String pkg) {
		return DeviceHelper.getInstance(this.getApplication()).getAppName(pkg);
	}

	/**
	 * 获取输入法列表
	 *
	 * 不需要 任何 权限
	 *
	 * android 11 测试可行
	 */
	public void getInputMethodList(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		List<InputMethodInfo> methodList = imm.getInputMethodList();
		for(InputMethodInfo mi : methodList ) {
			CharSequence name = mi.loadLabel(getPackageManager());
			log("getInputMethodList. name: "+ name + ", pkg: " + mi.getPackageName());
		}
	}

	private void testAlbumCount() {
		// 需要 READ_EXTERNAL_STORAGE 权限
		int cnt = DeviceHelper.getInstance(this).getAlbumCount();
		Log.e("jackieee", "cnt: " + cnt);
	}

	/**
	 * 判断设备 是否使用代理上网
	 *
	 * 不需要 任何 权限
	 *
	 * android 11 测试可行
	 *
	 * */
	private boolean isWifiProxy(Context context) {
		final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
		String proxyAddress;
		int proxyPort;
		if (IS_ICS_OR_LATER) {
			proxyAddress = System.getProperty("http.proxyHost");
			String portStr = System.getProperty("http.proxyPort");
			proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
		} else {
			proxyAddress = android.net.Proxy.getHost(context);
			proxyPort = android.net.Proxy.getPort(context);
		}
		return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
	}

	/**
	 * 获取设备IP（wifi/cellular）
	 *
	 * 需要 INTERNET 权限，无权限时获取不到值，不抛异常
	 *
	 * android 11 测试可行
	 *
	 * https://www.jianshu.com/p/be244fb85a4e
	 * @return
	 */
	private String getIp() {
		try {
			if (DeviceHelper.getInstance(this).checkPermission("android.permission.INTERNET")) {
				Enumeration<NetworkInterface> enNetI = NetworkInterface.getNetworkInterfaces();
				if (enNetI != null) {
					while (enNetI.hasMoreElements()) {
						NetworkInterface netI = enNetI.nextElement();
						Enumeration<InetAddress> enumIpAddr = netI.getInetAddresses();
						if (enumIpAddr != null) {
							while (enumIpAddr.hasMoreElements()) {
								InetAddress inetAddress = enumIpAddr.nextElement();
								if (inetAddress != null && inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
									return inetAddress.getHostAddress();
								}
							}
						}
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return "";
	}

	/**
	 *
	 * 获取设备网关地址
	 *
	 * https://blog.csdn.net/shaoenxiao/article/details/81285464
	 *
	 * 需要权限 ACCESS_WIFI_STATE，无权限会抛异常
	 *
	 * android 11 测试可行
	 *
	 * @return
	 */
	private String getWifiGateway() {
		String ip = null;
		try {
			if (DeviceHelper.getInstance(this).checkPermission("android.permission.ACCESS_WIFI_STATE")) {
				WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
				DhcpInfo info=wifiManager.getDhcpInfo();
				int gateway=info.gateway;
				ip=intToIp(gateway);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return ip;
	}

	private String intToIp(int addr) {
		return ((addr & 0xFF) + "." +
				((addr >>>= 8) & 0xFF) + "." +
				((addr >>>= 8) & 0xFF) + "." +
				((addr >>>= 8) & 0xFF));
	}

	/**
	 * 根据adb shell命令获取getprop中的信息(WIFI或dhcp下)
	 *
	 * 权限需求 未确认
	 *
	 * android 11 测试不可行
	 *
	 * @return
	 */
	public String getGateway() {
		BufferedReader bufferedReader = null;
		String str2 = "";
		String str3 = "getprop dhcp.eth0.gateway";
		Process exec;
		BufferedReader bufferedReader2 = null;
		try {
			exec = Runtime.getRuntime().exec(str3);
			try {
				bufferedReader2 = new BufferedReader(new InputStreamReader(exec.getInputStream()));
			} catch (Throwable th3) {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (exec != null) {
					exec.exitValue();
				}
			}
			try {
				str3 = bufferedReader2.readLine();
				if (str3 != null) {
					TextUtils.isEmpty(str3);
				}
				try {
					bufferedReader2.close();
				} catch (IOException iOException222) {
					iOException222.printStackTrace();
				}
				if (exec != null) {
					try {
						exec.exitValue();
					} catch (Exception e5) {
					}
				}
			} catch (IOException e6) {
				str3 = str2;
				if (bufferedReader2 != null) {
					bufferedReader2.close();
				}
				if (exec != null) {
					exec.exitValue();
				}
				return str3;
			}
		} catch (IOException e62) {
			bufferedReader2 = null;
			exec = null;
			str3 = str2;
			if (bufferedReader2 != null) {
				try {
					bufferedReader2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (exec != null) {
				exec.exitValue();
			}
			return str3;
		} catch (Throwable th4) {
			exec = null;
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (exec != null) {
				exec.exitValue();
			}
		}
		return str3;
	}

	/**
	 * 根据adb shell命令获取网关信息（static下）
	 *
	 * 不需要 任何 权限
	 *
	 * android 11 测试可行
	 *
	 * @return
	 */
	public String getGatewayForStatic() {
		BufferedReader bufferedReader = null;
		String result="";
		String str2 = "";
		String str3 = "ip route list table 0";
		Process exec;
		BufferedReader bufferedReader2 = null;
		try {
			exec = Runtime.getRuntime().exec(str3);
			try {
				bufferedReader2 = new BufferedReader(new InputStreamReader(exec.getInputStream()));
			} catch (Throwable th3) {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (exec != null) {
					exec.exitValue();
				}
			}
			try {
				while ((str2 = bufferedReader2.readLine()) != null) {
					if (str2.contains("default via")) {
						str2= str2.trim();
						String[] strings=str2.split("\\s+");
						if (strings.length>3){
							result= strings[2];
						}
						break;
					}
				}
				try {
					bufferedReader2.close();
				} catch (IOException iOException222) {
					iOException222.printStackTrace();
				}
				if (exec != null) {
					try {
						exec.exitValue();
					} catch (Exception e5) {
					}
				}
			} catch (IOException e6) {
				if (bufferedReader2 != null) {
					bufferedReader2.close();
				}
				if (exec != null) {
					exec.exitValue();
				}
				return result;
			}
		} catch (IOException e62) {
			bufferedReader2 = null;
			exec = null;
			if (bufferedReader2 != null) {
				try {
					bufferedReader2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (exec != null) {
				exec.exitValue();
			}
			return result;
		} catch (Throwable th4) {
			exec = null;
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (exec != null) {
				exec.exitValue();
			}
		}
		return result;
	}

	/**
	 * 获取设备内存信息
	 *
	 * 不需要 任何 权限
	 *
	 * Android 11 测试可行
	 */
	private void getDeviceMemo() {
		DeviceHelper device = DeviceHelper.getInstance(this);
		ActivityManager am = (ActivityManager) device.getSystemServiceSafe(ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		// 设备可用内存（单位Byte）
		long avail = mi.availMem;
		// 设备总内存（单位Byte）
		long total = mi.totalMem;
		boolean low = mi.lowMemory;
		long threshold = mi.threshold;
		log("DeviceMemory. avail: " + avail);
		log("DeviceMemory. total: " + total);
		log("DeviceMemory. low: " + low);
		log("DeviceMemory. threshold: " + threshold);
	}

	/**
	 *
	 * 获取设备内存信息
	 *
	 * 不需要 任何 权限
	 *
	 * Android 11 测试可行
	 *
	 * https://www.cnblogs.com/helloandroid/articles/2210334.html
	 *
	 * MemTotal: 总内存大小。
	 * MemFree: LowFree与HighFree的总和，被系统留着未使用的内存。
	 * MemAvailable：可用内存
	 * Buffers: 用来给文件做缓冲大小。
	 * Cached: 被高速缓冲存储器（cache memory）用的内存的大小（等于diskcache minus SwapCache）。
	 * SwapCached:被高速缓冲存储器（cache memory）用的交换空间的大小。已经被交换出来的内存，仍然被存放在swapfile中，用来在需要的时候很快的被替换而不需要再次打开I/O端口。
	 * Active: 在活跃使用中的缓冲或高速缓冲存储器页面文件的大小，除非非常必要，否则不会被移作他用。
	 * Inactive: 在不经常使用中的缓冲或高速缓冲存储器页面文件的大小，可能被用于其他途径。
	 * SwapTotal: 交换空间的总大小。
	 * SwapFree: 未被使用交换空间的大小。
	 * Dirty: 等待被写回到磁盘的内存大小。
	 * Writeback: 正在被写回到磁盘的内存大小。
	 * AnonPages：未映射页的内存大小。
	 * Mapped: 设备和文件等映射的大小。
	 * Slab: 内核数据结构缓存的大小，可以减少申请和释放内存带来的消耗。
	 * SReclaimable:可收回Slab的大小。
	 * SUnreclaim：不可收回Slab的大小（SUnreclaim+SReclaimable＝Slab）。
	 * PageTables：管理内存分页页面的索引表的大小。
	 * NFS_Unstable:不稳定页表的大小。
	 *
	 * @return
	 */
	private String getDeviceTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}

			initial_memory = Long.valueOf(arrayOfString[1]) * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB或GB，内存大小规格化
	}

	/**
	 * 获取应用内存信息
	 *
	 * 不需要 任何 权限
	 *
	 * Android 11 测试可行
	 */
	private void getAppMemory() {
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//最大分配内存
		int memory = activityManager.getMemoryClass();
		log("app memory: "+memory);
		//最大分配内存获取方法2（单位Byte）
//		float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0/ (1024 * 1024));
		// 格式化为KB/MB/GB
		String maxMemory = Formatter.formatFileSize(getBaseContext(), Runtime.getRuntime().maxMemory());
		//当前分配的总内存（单位Byte）
//		float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0/ (1024 * 1024));
		String totalMemory = Formatter.formatFileSize(getBaseContext(), Runtime.getRuntime().totalMemory());
		//剩余内存（单位Byte）
//		float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0/ (1024 * 1024));
		String freeMemory = Formatter.formatFileSize(getBaseContext(), Runtime.getRuntime().freeMemory());
		log("app maxMemory: "+maxMemory);
		log("app totalMemory: "+totalMemory);
		log("app freeMemory: "+freeMemory);
	}

	/**
	 *
	 * 获取cpu事情率
	 *
	 * 测试不可行
	 */
	private void getCpuUsage() {
		try {
			String Result;
			Process p=Runtime.getRuntime().exec("top -n 1");

			BufferedReader br=new BufferedReader(new InputStreamReader
					(p.getInputStream ()));
			while((Result=br.readLine())!=null)
			{
				if(Result.trim().length()<1){
					continue;
				}else{
					StringBuffer tv = new StringBuffer();
					String[] CPUusr = Result.split("%");
					tv.append("USER:"+CPUusr[0]+"\n");
					String[] CPUusage = CPUusr[0].split("User");
					String[] SYSusage = CPUusr[1].split("System");
					tv.append("CPU:"+CPUusage[1].trim()+" length:"+CPUusage[1].trim().length()+"\n");
					tv.append("SYS:"+SYSusage[1].trim()+" length:"+SYSusage[1].trim().length()+"\n");
					tv.append(Result+"\n");
					log("cpu usage: " + tv);
					break;
				}
			}
		} catch (Throwable t) {
			MobLog.getInstance().w(t);
		}
	}

	/**
	 *
	 * 获取cpu使用率
	 *
	 * 8.0开始不可行
	 *
	 * @return
	 */
	public static float getProcessCpuRate()
	{

		float totalCpuTime1 = getTotalCpuTime();
		float processCpuTime1 = getAppCpuTime();
		try
		{
			Thread.sleep(360);  //sleep一段时间
		}
		catch (Exception e)
		{
		}

		float totalCpuTime2 = getTotalCpuTime();
		float processCpuTime2 = getAppCpuTime();

		float cpuRate = 100 * (processCpuTime2 - processCpuTime1) / (totalCpuTime2 - totalCpuTime1);//百分比

		return cpuRate;
	}

	/**
	 *
	 * 8.0开始不可用：FileNotFoundException（Permission Denied）
	 *
	 * @return
	 */
	// 获取系统总CPU使用时间
	public static long getTotalCpuTime()
	{
		long totalCpu = -1;
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			String[] cpuInfos = load.split(" ");
			totalCpu = Long.parseLong(cpuInfos[2])
					+ Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
					+ Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
					+ Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		return totalCpu;
	}

	/**
	 * 获取应用占用的CPU时间
	 *
	 * 8.0开始不可行
 	 */
	public static long getAppCpuTime()
	{
		String[] cpuInfos = null;
		try
		{
			int pid = android.os.Process.myPid();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/" + pid + "/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		long appCpuTime = Long.parseLong(cpuInfos[13])
				+ Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
				+ Long.parseLong(cpuInfos[16]);
		return appCpuTime;
	}

	/**
	 *
	 * 获取cpu使用率
	 *
	 * 8.0开始不允许读取/proc/stat文件
	 *
	 * https://www.jianshu.com/p/6bf564f7cdf0
	 * https://blog.csdn.net/aahuangjianjun/article/details/82622350
	 *
	 * 通过获取cpu一行的数据，即可进行CPU占用率的计算。我们会用到的数据有:
	 - user(21441),从系统启动开始累计到当前时刻，处于用户态的运行时间，不包含nice值为负的进程。
	 - nice(3634),从系统启动开始累计到当前时刻，nice值为负的进程所占用的CPU时间。
	 - system(13602),从系统启动开始累计到当前时刻，处于核心态的运行时间。
	 - idle(818350),从系统启动开始累计到当前时刻，除IO等待时间以外的其它等待时间。
	 - iowait(3535),从系统启动开始累计到当前时刻，IO等待时间。
	 - irq(2),从系统启动开始累计到当前时刻，硬中断时间。
	 - softirq(99),从系统启动开始累计到当前时刻，软中断时间。
	 总的CPU占用率的计算方法为：采样两个足够短的时间间隔的CPU快照，
	 CPU占用率 = 100*((totalTime2-totalTime1)-(idle2-idle1))/(totalTime2-totalTime1)。
	 * */
	public float getCpuRate(){
		float cpuRate = -1;
		try {
			//采样第一次cpu信息快照
			Map<String,String> map1 = getMap();
			//总的CPU时间totalTime = user+nice+system+idle+iowait+irq+softirq
			long totalTime1 =getTime(map1);
			System.out.println(totalTime1+"...........................totalTime1.");
			//获取idleTime1
			long idleTime1 = Long.parseLong(map1.get("idle"));
			System.out.println(idleTime1 + "...................idleTime1");
			//间隔360ms
			try {
				Thread.sleep(360);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//采样第二次cpu信息快照
			Map<String,String> map2 = getMap();
			long totalTime2 = getTime(map2);
			System.out.println(totalTime2+"............................totalTime2");
			//获取idleTime1
			long idleTime2 = Long.parseLong(map2.get("idle"));
			System.out.println(idleTime2+"................idleTime2");

			//得到cpu的使用率
			cpuRate = 100*((totalTime2-totalTime1)-(idleTime2-idleTime1))/(totalTime2-totalTime1);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return cpuRate;
	}

	//得到cpu信息
	public long getTime(Map<String,String> map){
		long totalTime = -1;
		try {
			if (map != null && !map.isEmpty()) {
				totalTime = Long.parseLong(map.get("user")) + Long.parseLong(map.get("nice"))
						+ Long.parseLong(map.get("system")) + Long.parseLong(map.get("idle"))
						+ Long.parseLong(map.get("iowait")) + Long.parseLong(map.get("irq"))
						+ Long.parseLong(map.get("softirq"));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return totalTime;
	}

	/**
	 * 采样CPU信息快照的函数，返回Map类型
	 *
	 * 测试失败：Permission Denied，8.0开始不允许读取/proc/stat文件
	 * @return
	 */
	public  Map<String,String> getMap(){
		String[] cpuInfos = null;
		//读取cpu信息文件
		BufferedReader br = null;
		Map<String,String> map = new HashMap<>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")));
			String load = br.readLine();
			br.close();
			cpuInfos = load.split(" ");
			if (cpuInfos != null && cpuInfos.length > 0) {
				map.put("user",cpuInfos[2]);
				map.put("nice",cpuInfos[3]);
				map.put("system",cpuInfos[4]);
				map.put("idle",cpuInfos[5]);
				map.put("iowait",cpuInfos[6]);
				map.put("irq",cpuInfos[7]);
				map.put("softirq",cpuInfos[8]);
			}
		} catch (FileNotFoundException e) {
			System.out.println("文件未找到");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("线程异常");
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 判断CPU位数（32/64）
	 *
	 * 不需要 任何 权限
	 *
	 * Android 11 测试可行
	 *
	 * @return
	 */
	public static boolean isCPU64(){
		boolean result = false;
		String mProcessor = null;
		List<String > list = null;
		try {
			mProcessor = getFieldFromCpuinfo("Processor");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (mProcessor != null) {
			// D/CpuUtils: isCPU64 mProcessor = AArch64 Processor rev 4 (aarch64)
			Log.d(TAG, "isCPU64 mProcessor = " + mProcessor);
			//list =  Arrays.asList(mProcessor.split("\\s"));
			if (mProcessor.contains("aarch64")) {
				result = true;
			}
		}

		return result;
	}


	/*  cat /proc/cpuinfo
		processor       : 0
		Processor       : AArch64 Processor rev 4 (aarch64)
		model name      : AArch64 Processor rev 4 (aarch64)
		BogoMIPS        : 26.00
		Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
		CPU implementer : 0x41
		CPU architecture: 8
		CPU variant     : 0x0
		CPU part        : 0xd03
		CPU revision    : 4
	*/
	public static String getFieldFromCpuinfo(String field) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));
		Pattern p = Pattern.compile(field + "\\s*:\\s*(.*)");

		try {
			String line;
			while ((line = br.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					return m.group(1);
				}
			}
		} finally {
			br.close();
		}

		return null;
	}

	private void testSimulatorHelper() {
		SmltHelper helper = new SmltHelper();
		helper.checkBaseband(this.getApplicationContext());
		helper.checkBoard(getApplicationContext());
	}

	private void log(String msg) {
		Log.e("jackieee", msg);
	}
}
