package com.example.jack.mobcomdemo.entity;

import com.example.jack.mobcomdemo.util.DemoResHelper;
import com.mob.MobSDK;

public class UiSettings extends BaseEntity {
	public static final int DEFAULT_TITLE_TEXT_COLOR_ID = DemoResHelper.getColorRes(MobSDK.getContext(), "smssdk_common_black");
	public static final int DEFAULT_TITLE_TEXT_SIZE_DP = DemoResHelper.getDimenDpSize(DemoResHelper.getDimenRes("smssdk_authorize_text_size_l"));
	public static final int DEFAULT_TITLE_TEXT_ID = DemoResHelper.getStringRes(MobSDK.getContext(), "smssdk_authorize_dialog_title");

	private int titleTextId;
	private int titleTextColorId;
	private int titleTextSizeDp;

	private int backgroundImgId;

	private int logoImgId;
	private int logoWidth;
	private int logoHeight;

	private int positiveBtnImgId;
	private int positiveBtnTextId;
	private int positiveBtnTextColorId;
	private int positiveBtnTextSize;
	private int positiveBtnWidth;
	private int positiveBtnHeight;

	private UiSettings(Builder builder) {
		this.titleTextId = builder.titleTextId;
		this.titleTextColorId = builder.titleTextColorId;
		this.logoImgId = builder.logoImgId;
		this.positiveBtnImgId = builder.positiveBtnImgId;
		this.positiveBtnTextId = builder.positiveBtnTextId;
		this.positiveBtnTextColorId = builder.positiveBtnTextColorId;
		this.backgroundImgId = builder.backgroundImgId;
		this.logoWidth = builder.logoWidth;
		this.logoHeight = builder.logoHeight;
		this.positiveBtnTextSize = builder.positiveBtnTextSize;
		this.positiveBtnWidth = builder.positiveBtnWidth;
		this.positiveBtnHeight = builder.positiveBtnHeight;
		this.titleTextSizeDp = builder.titleTextSizeDp;
	}

	public int getTitleTextId() {
		return titleTextId;
	}

	public int getTitleTextColorId() {
		return titleTextColorId;
	}

	public int getLogoImgId() {
		return logoImgId;
	}

	public int getPositiveBtnImgId() {
		return positiveBtnImgId;
	}

	public int getPositiveBtnTextId() {
		return positiveBtnTextId;
	}

	public int getPositiveBtnTextColorId() {
		return positiveBtnTextColorId;
	}

	public int getBackgroundImgId() {
		return backgroundImgId;
	}

	public int getLogoWidth() {
		return logoWidth;
	}

	public int getLogoHeight() {
		return logoHeight;
	}

	public int getPositiveBtnTextSize() {
		return positiveBtnTextSize;
	}

	public int getPositiveBtnWidth() {
		return positiveBtnWidth;
	}

	public int getPositiveBtnHeight() {
		return positiveBtnHeight;
	}

	public int getTitleTextSizeDp() {
		return titleTextSizeDp;
	}

	public static class Builder extends BaseEntity {
		// 标题文字资源id
		private int titleTextId = -1;
		// 标题文字颜色资源id
		private int titleTextColorId = 0xff330f33;
		// 标题文字大小（单位dp）
		private int titleTextSizeDp = -1;

		private int backgroundImgId = -1;

		private int logoImgId = -1;
		private int logoWidth = -1;
		private int logoHeight = -1;

		private int positiveBtnImgId = -1;
		private int positiveBtnTextId = -1;
		private int positiveBtnTextColorId = 0xffffffff;
		private int positiveBtnTextSize = -1;
		private int positiveBtnWidth = -1;
		private int positiveBtnHeight = -1;

		public UiSettings build() {
			return new UiSettings(this);
		}

		public UiSettings buildDefault() {
			Builder builder = new Builder()
					.setTitleTextId(DEFAULT_TITLE_TEXT_ID)
					.setTitleTextColorId(DEFAULT_TITLE_TEXT_COLOR_ID)
					.setTitleTextSizeDp(DEFAULT_TITLE_TEXT_SIZE_DP);
			return new UiSettings(builder);
		}

		/**
		 * 设置标题文字资源id
		 *
		 * @param titleTextId
		 * @return
		 */
		public Builder setTitleTextId(int titleTextId) {
			this.titleTextId = titleTextId;
			return this;
		}

		/**
		 * 设置标题文字颜色资源id
		 *
		 * @param titleTextColorId
		 * @return
		 */
		public Builder setTitleTextColorId(int titleTextColorId) {
			this.titleTextColorId = titleTextColorId;
			return this;
		}

		public Builder setLogoImgId(int logoImgId) {
			this.logoImgId = logoImgId;
			return this;
		}

		public Builder setPositiveBtnImgId(int positiveBtnImgId) {
			this.positiveBtnImgId = positiveBtnImgId;
			return this;
		}

		public Builder setPositiveBtnTextId(int positiveBtnTextId) {
			this.positiveBtnTextId = positiveBtnTextId;
			return this;
		}

		public Builder setPositiveBtnTextColorId(int positiveBtnTextColorId) {
			this.positiveBtnTextColorId = positiveBtnTextColorId;
			return this;
		}

		public Builder setBackgroundImgId(int backgroundImgId) {
			this.backgroundImgId = backgroundImgId;
			return this;
		}

		public Builder setLogoWidth(int logoWidth) {
			this.logoWidth = logoWidth;
			return this;
		}

		public Builder setLogoHeight(int logoHeight) {
			this.logoHeight = logoHeight;
			return this;
		}

		public Builder setPositiveBtnTextSize(int positiveBtnTextSize) {
			this.positiveBtnTextSize = positiveBtnTextSize;
			return this;
		}

		public Builder setPositiveBtnWidth(int positiveBtnWidth) {
			this.positiveBtnWidth = positiveBtnWidth;
			return this;
		}

		public Builder setPositiveBtnHeight(int positiveBtnHeight) {
			this.positiveBtnHeight = positiveBtnHeight;
			return this;
		}

		/**
		 * 设置标题文字大小（单位dp）
		 *
		 * @param titleTextSize
		 * @return
		 */
		public Builder setTitleTextSizeDp(int titleTextSize) {
			this.titleTextSizeDp = titleTextSize;
			return this;
		}
	}
}
