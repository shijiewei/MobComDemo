package com.mob.commons;

import com.mob.MobSDK;
import com.mob.commons.MobProduct;

public class COMMON implements MobProduct {
	@Override
	public String getProductTag() {
		return "COMMON";
	}

	@Override
	public int getSdkver() {
		return MobSDK.SDK_VERSION_CODE;
	}
}
