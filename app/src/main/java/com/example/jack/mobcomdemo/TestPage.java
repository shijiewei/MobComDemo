package com.example.jack.mobcomdemo;

import android.view.KeyEvent;
import android.view.View;

import com.mob.tools.utils.ResHelper;

public class TestPage extends BasePage {
	private static final String TAG = "TestPage";

	@Override
	protected int getContentViewId() {
		return ResHelper.getLayoutRes(getContext(), "page_test");
	}

	@Override
	protected void getTitleStyle(TitleStyle titleStyle) {
		titleStyle.titleResName = "page_test_title_sms";
	}

	@Override
	protected void onViewCreated() {
		initView();
	}

	private void initView() {

	}

	@Override
	protected void onViewClicked(View v) {

	}

	@Override
	protected boolean onLeftEvent() {
		backToHomepage();
		return true;
	}

	@Override
	public boolean onKeyEvent(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
			backToHomepage();
			return true;
		}
		return super.onKeyEvent(keyCode, event);
	}

	private void backToHomepage() {
		finish();
	}
}
