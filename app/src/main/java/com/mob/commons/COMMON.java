package com.mob.commons;

import com.mob.MobSDK;
import com.mob.commons.MobProduct;
import com.mob.tools.MobLog;

public class COMMON implements MobProduct {

	public COMMON() {
		MobLog.getInstance().e("init COMMON");
	}

	@Override
	public String getProductTag() {
		return "COMMON";
	}

	@Override
	public int getSdkver() {
		return MobSDK.SDK_VERSION_CODE;
	}
}
