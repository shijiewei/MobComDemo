package com.example.jack.mobcomdemo.companent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {
	//画笔
	private Paint mPaint;
	//圆形图片的半径
	private int mRadius;
	//图片的宿放比例
	private float mScale;

	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//由于是圆形，宽高应保持一致
		int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
		mRadius = size / 2;
		setMeasuredDimension(size, size);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint = new Paint();
		Drawable drawable = getDrawable();
		if (null != drawable) {
			Bitmap bitmap;
			if (Build.VERSION.SDK_INT >= 26 && drawable instanceof AdaptiveIconDrawable) {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
				Canvas cvs = new Canvas(bitmap);
				drawable.setBounds(0, 0, cvs.getWidth(), cvs.getHeight());
				drawable.draw(cvs);
			} else {
				bitmap = ((BitmapDrawable) drawable).getBitmap();
			}
			//初始化BitmapShader，传入bitmap对象
			BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			//计算缩放比例
			mScale = (mRadius * 2.0f) / Math.min(bitmap.getHeight(), bitmap.getWidth());
			Matrix matrix = new Matrix();
			matrix.setScale(mScale, mScale);
			bitmapShader.setLocalMatrix(matrix);
			mPaint.setShader(bitmapShader);
			//画圆形，指定好坐标，半径，画笔
			canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
		} else {
			super.onDraw(canvas);
		}
	}
}
