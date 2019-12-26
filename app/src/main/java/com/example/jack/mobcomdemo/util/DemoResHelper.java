package com.example.jack.mobcomdemo.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.mob.MobSDK;
import com.mob.tools.MobLog;
import com.mob.tools.utils.ResHelper;

public class DemoResHelper extends ResHelper {
	private static String TAG = "SmsResHelper";
	private static Resources resources;

	public static int getDimenRes(String resName) {
		return getResId(MobSDK.getContext(), "dimen", resName);
	}

	public static int getDimen(int id) {
		if (resources == null) {
			resources = MobSDK.getContext().getResources();
		}
		return (int) resources.getDimension(id);
	}

	public static int getDimenSafe(int id, int def) {
		if (id > 0) {
			try {
				return getDimen(id);
			} catch (Resources.NotFoundException e) {
				MobLog.getInstance().w(Const.FORMAT, TAG, "getDimenSafe", "Dimen resource not found. id: " + id);
			}
		}
		return getDimen(def);
	}

	public static int getIconIdSafe(int def) {
		PackageManager pm = MobSDK.getContext().getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(MobSDK.getContext().getPackageName(), 0);
			return ai.icon;
		} catch (PackageManager.NameNotFoundException e) {
			MobLog.getInstance().w(Const.FORMAT, TAG, "getIconIdSafe", "No icon found");
		}
		try {
			return ResHelper.getBitmapRes(MobSDK.getContext(), "ic_launcher");
		} catch (Throwable t) {
			MobLog.getInstance().w(Const.FORMAT, TAG, "getIconIdSafe", "No icon named 'ic_launcher' found");
			return def;
		}
	}

	public static int getDimenDpSize(int id) {
		return pxToDip(MobSDK.getContext(), getDimenPixelSize(id));
	}

	public static int getDimenDpSizeSafe(int id, int def) {
		if (id > 0) {
			try {
				return getDimenDpSize(id);
			} catch (Resources.NotFoundException e) {
				MobLog.getInstance().w(Const.FORMAT, TAG, "getDimenDpSizeSafe", "Dimen resource not found. id: " + id);
			}
		}
		return getDimenDpSize(def);
	}

	public static int getDimenPixelSize(int id) {
		if (resources == null) {
			resources = MobSDK.getContext().getResources();
		}
		return resources.getDimensionPixelSize(id);
	}

	public static int getColor(int id) {
		if (resources == null) {
			resources = MobSDK.getContext().getResources();
		}
		return resources.getColor(id);
	}

	public static int getColorSafe(int id, int def) {
		if (id > 0) {
			try {
				return getColor(id);
			} catch (Resources.NotFoundException e) {
				MobLog.getInstance().w(Const.FORMAT, TAG, "getColorSafe", "Color resource not found. id: " + id);
			}
		}
		return getColor(def);
	}

	public static String getString(int id) {
		if (resources == null) {
			resources = MobSDK.getContext().getResources();
		}
		return resources.getString(id);
	}

	public static String getStringSafe(int id, int def) {
		if (id > 0) {
			try {
				return getString(id);
			} catch (Throwable t) {
				MobLog.getInstance().w(Const.FORMAT, TAG, "getStringSafe", "String resource not found. id: " + id);
			}
		}
		return getString(def);
	}
}
