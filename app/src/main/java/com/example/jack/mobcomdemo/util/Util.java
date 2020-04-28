package com.example.jack.mobcomdemo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	private static final String DATE_FORMAT = "yyyyMMdd HH:mm:ss.SSS";

	public static final String getDatetime() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(new Date());
	}
}
