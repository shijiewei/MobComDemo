package com.example.jack.mobcomdemo.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jack.mobcomdemo.OnDialogListener;
import com.example.jack.mobcomdemo.PolicyActivity;
import com.example.jack.mobcomdemo.companent.CircleImageView;
import com.example.jack.mobcomdemo.entity.UiSettings;
import com.example.jack.mobcomdemo.util.Const;
import com.example.jack.mobcomdemo.util.DemoResHelper;
import com.example.jack.mobcomdemo.util.SPHelper;
import com.mob.MobSDK;
import com.mob.PrivacyPolicy;
import com.mob.tools.MobLog;
import com.mob.tools.utils.ResHelper;

import java.lang.reflect.Method;

public class PrivacyDialog extends AlertDialog {
	private static final String TAG = "PrivacyDialog";
	private View view;
	private Context context;
	private int width;
	private TextView titleTv;
	private TextView pricacyTv;
	private CircleImageView logoIv;
	private TextView rejectTv;
	private TextView acceptTv;
	private OnDialogListener listener;
	private UiSettings uiSettings;
	private PrivacyPolicy policyTxt;
	private PrivacyPolicy policyUrl;

	public PrivacyDialog(Context context, PrivacyPolicy policyTxt, PrivacyPolicy policyUrl, OnDialogListener onDialogListener) {
		this(context, policyTxt, policyUrl, new UiSettings.Builder().buildDefault(), onDialogListener);
	}

	public PrivacyDialog(Context context, PrivacyPolicy policyTxt, PrivacyPolicy policyUrl, UiSettings uiSettings, OnDialogListener onDialogListener) {
		super(context, ResHelper.getStyleRes(context, "smssdk_DialogStyle"));
		this.context = context;
		this.policyTxt = policyTxt;
		this.policyUrl = policyUrl;
		this.uiSettings = uiSettings;
		this.listener = onDialogListener;
		int orientation = this.context.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			double deviceHeight = getScreenHeight(this.context);
			width = (int) (deviceHeight * 0.7);
		} else {
			double deviceWidth = getScreenWidth(this.context);
			width = (int) (deviceWidth * 0.7);
		}
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		LayoutInflater inflater = LayoutInflater.from(this.context);
		view = inflater.inflate(ResHelper.getLayoutRes(context, "privacy_dialog"), null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams
				.WRAP_CONTENT, 0);
		setContentView(view, params);
		initView();
		initEvents();
	}

	@Override
	public void show() {
		if (SPHelper.getInstance().isNotShowAgain()) {
			if (listener != null) {
				listener.onNotShow();
			}
		} else {
			super.show();
		}
	}

	private void initView() {
		this.titleTv = (TextView) view.findViewById(ResHelper.getIdRes(context, "smssdk_authorize_dialog_title_tv"));
		this.logoIv = (CircleImageView) view.findViewById(ResHelper.getIdRes(context, "smssdk_authorize_dialog_logo_iv"));
		this.pricacyTv = (TextView) view.findViewById(ResHelper.getIdRes(context, "smssdk_privacy_dialog_privacy_tv"));
		this.acceptTv = (TextView) view.findViewById(ResHelper.getIdRes(context, "smssdk_authorize_dialog_accept_tv"));
		this.rejectTv = (TextView) view.findViewById(ResHelper.getIdRes(context, "smssdk_authorize_dialog_reject_tv"));

		if (uiSettings != null) {
			this.titleTv.setText(DemoResHelper.getStringSafe(
					uiSettings.getTitleTextId(), UiSettings.DEFAULT_TITLE_TEXT_ID));
			this.titleTv.setTextColor(DemoResHelper.getColorSafe(
					uiSettings.getTitleTextColorId(), UiSettings.DEFAULT_TITLE_TEXT_COLOR_ID));
			int titleTextSizeDp = uiSettings.getTitleTextSizeDp();
			if (titleTextSizeDp <= 0) {
				titleTextSizeDp = UiSettings.DEFAULT_TITLE_TEXT_SIZE_DP;
			}
			this.titleTv.setTextSize(titleTextSizeDp);
		}
	}

	private void initEvents() {
		int iconId = DemoResHelper.getIconIdSafe(-1);
		if (iconId == -1) {
			this.logoIv.setVisibility(View.GONE);
		} else {
			this.logoIv.setImageResource(iconId);
		}

		this.pricacyTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				gotoPolicyActivity(MobSDK.POLICY_TYPE_TXT);
			}
		});

		this.acceptTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isShowing()) {
					dismiss();
				}
				if (listener != null) {
					// 隐私协议框中，参数"doNotAskAgain"无意义
					listener.onAgree(false);
				}
			}
		});

		this.rejectTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isShowing()) {
					dismiss();
				}
				if (listener != null) {
					// 隐私协议框中，参数"doNotAskAgain"无意义
					listener.onDisagree(false);
				}
			}
		});
	}

	private int getScreenWidth(Context context) {
		return getScreenSize(context)[0];
	}

	private int getScreenHeight(Context context) {
		return getScreenSize(context)[1];
	}

	private int[] getScreenSize(Context context) {
		WindowManager windowManager;
		try {
			windowManager = (WindowManager)context.getSystemService("window");
		} catch (Throwable var6) {
			MobLog.getInstance().w(var6,Const.FORMAT, "AuthorizeDialog", "getScreenSize", "get SCreenSize Exception");
			windowManager = null;
		}

		if(windowManager == null) {
			return new int[]{0, 0};
		} else {
			Display display = windowManager.getDefaultDisplay();
			if(Build.VERSION.SDK_INT < 13) {
				DisplayMetrics t1 = new DisplayMetrics();
				display.getMetrics(t1);
				return new int[]{t1.widthPixels, t1.heightPixels};
			} else {
				try {
					Point t = new Point();
					Method method = display.getClass().getMethod("getRealSize", new Class[]{Point.class});
					method.setAccessible(true);
					method.invoke(display, new Object[]{t});
					return new int[]{t.x, t.y};
				} catch (Throwable var5) {
					MobLog.getInstance().w(var5, Const.FORMAT, "AuthorizeDialog", "getScreenSize", "get SCreenSize Exception");
					return new int[]{0, 0};
				}
			}
		}
	}

	private void gotoPolicyActivity(int type) {
		Intent intent = new Intent(context, PolicyActivity.class);
		if (type == MobSDK.POLICY_TYPE_URL) {
			intent.putExtra(PolicyActivity.EXTRA_POLICY_TYPE, MobSDK.POLICY_TYPE_URL);
			intent.putExtra(PolicyActivity.EXTRA_POLICY_OBJECT, policyUrl);
		} else if (type == MobSDK.POLICY_TYPE_TXT) {
			intent.putExtra(PolicyActivity.EXTRA_POLICY_TYPE, MobSDK.POLICY_TYPE_TXT);
			intent.putExtra(PolicyActivity.EXTRA_POLICY_OBJECT, policyTxt);
		}
		context.startActivity(intent);
	}
}

