package com.example.jack.mobcomdemo.util;

import com.mob.MobSDK;
import com.mob.commons.MobProduct;

public class Const {
	public static final String FORMAT = "[COMMON][%s][%s] %s";	// [SMSSDK][CLASS][METHOD] MSG
	public static final String PERMISSION_CONTACT = "android.permission.READ_CONTACTS";
	public static final MobProduct PRODUCT = new MobProduct() {
		@Override
		public String getProductTag() {
			return "COMMON";
		}

		@Override
		public int getSdkver() {
			return MobSDK.SDK_VERSION_CODE;
		}
	};
}
