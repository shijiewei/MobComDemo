package com.example.jack.mobcomdemo;

public interface OnDialogListener {
	void onAgree(boolean doNotAskAgain);
	void onDisagree(boolean doNotAskAgain);
	/**
	 * 由于用户曾经勾选过『不再提示』，因此不显示弹出框
	 */
	void onNotShow();
}
