package com.example.jack.mobcomdemo.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jack.mobcomdemo.OnDialogListener;
import com.example.jack.mobcomdemo.companent.CircleImageView;
import com.example.jack.mobcomdemo.entity.UiSettings;
import com.example.jack.mobcomdemo.util.Const;
import com.example.jack.mobcomdemo.util.DemoResHelper;
import com.example.jack.mobcomdemo.util.SPHelper;
import com.mob.tools.MobLog;
import com.mob.tools.utils.ResHelper;

import java.lang.reflect.Method;

public class AuthorizeDialog extends AlertDialog {
	private static final String TAG = "AuthorizeDialog";
	private View view;
	private Context context;
	private int width;
	private TextView titleTv;
	private CircleImageView logoIv;
	private CheckBox doNotAskCb;
	private TextView rejectTv;
	private TextView acceptTv;
	private boolean checked = false;
	private OnDialogListener listener;
	private UiSettings uiSettings;

	public AuthorizeDialog(Context context, OnDialogListener onDialogListener) {
		this(context, new UiSettings.Builder().buildDefault(), onDialogListener);
	}

	public AuthorizeDialog(Context context, UiSettings uiSettings, OnDialogListener onDialogListener) {
		super(context, ResHelper.getStyleRes(context, "smssdk_DialogStyle"));
		this.context = context;
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
		view = inflater.inflate(ResHelper.getLayoutRes(context, "authorize_dialog"), null);
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
		this.doNotAskCb = (CheckBox) view.findViewById(ResHelper.getIdRes(context, "smssdk_authorize_dialog_do_not_ask_cb"));
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

		this.acceptTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (doNotAskCb != null) {
					checked = doNotAskCb.isChecked();
					if (checked) {
						SPHelper.getInstance().setNotShowAgain();
					}
				}
				if (isShowing()) {
					dismiss();
				}
				if (listener != null) {
					listener.onAgree(checked);
				}
			}
		});

		this.rejectTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (doNotAskCb != null) {
					checked = doNotAskCb.isChecked();
					if (checked) {
						SPHelper.getInstance().setNotShowAgain();
					}
				}
				if (isShowing()) {
					dismiss();
				}
				if (listener != null) {
					listener.onDisagree(checked);
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
}

