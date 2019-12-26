package com.example.jack.mobcomdemo.entity;

import com.mob.tools.proguard.EverythingKeeper;
import com.mob.tools.utils.Hashon;

import java.io.Serializable;

/**
 * Serializable: Let SPDB can cache this object directly
 */
public class BaseEntity implements Serializable, EverythingKeeper {
	public String toJSONString() {
		return new Hashon().fromObject(this);
	}
}
