package io.viva.tv.app.widget;

import io.viva.tv.app.widget.utils.ImageUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;


public class ReflectImageView extends ImageView implements FocusedRelativeLayout.ScalePostionInterface {
	private String TAG = "ReflectImageView";
	private int mReflectHight = 0;
	public int reflectionGap = 2;
	public int filmPostion = 0;
	public ImageView imageView = null;
	public GridView gridView = null;
	private String text = "";
	private int textSize = 24;
	public boolean isShow = false;

	public ReflectImageView(Context context) {
		super(context);
	}

	public ReflectImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReflectImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setColor(-1);
		paint.setShadowLayer(1.0F, 2.0F, 2.0F, -16777216);
		paint.setTextSize(this.textSize);
		int hight = 0;
		int width = 0;
		int textW = (int) paint.measureText(getText());

		if (getHeight() > 300) {
			width = (getWidth() - textW) / 2;
			hight = getHeight() - this.mReflectHight - this.textSize;
		} else {
			width = getWidth() - textW - 20;
			hight = (getHeight() - this.mReflectHight + this.textSize) / 2;
		}
		canvas.drawText(getText(), width, hight, paint);
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public void setImageResource(int resId) {
		Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), resId);
		int imageViewHight = getLayoutParams().height;
		int imageViewWidth = getLayoutParams().width;
		if ((imageViewHight > originalBitmap.getHeight()) || (imageViewWidth > originalBitmap.getWidth()))
			setImageBitmap(ImageUtils.getScaleBitmap(originalBitmap, imageViewWidth, imageViewHight));
		else
			super.setImageResource(resId);
	}

	public void setImageResource(int resId, int reflectHight) {
		this.mReflectHight = reflectHight;
		Bitmap originalImage = BitmapFactory.decodeResource(getResources(), resId);
		CreateReflectBitmap(originalImage, reflectHight);
	}

	public void setImageBitmap(Bitmap bitmap, int reflectHight) {
		this.mReflectHight = reflectHight;
		CreateReflectBitmap(bitmap, reflectHight);
	}

	private void CreateReflectBitmap(Bitmap originalBitmap, int reflectHight) {
		int scaleHight = getLayoutParams().height - reflectHight - this.reflectionGap;
		int scaleWidth = getLayoutParams().width;
		Bitmap originalImage = null;
		if ((scaleHight > originalBitmap.getHeight()) || (scaleWidth > originalBitmap.getWidth()))
			originalImage = ImageUtils.getScaleBitmap(originalBitmap, scaleWidth, scaleHight);
		else {
			originalImage = originalBitmap;
		}
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1.0F, -1.0F);
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, reflectHight, width, height - reflectHight, matrix, false);
		Bitmap bitmap4Reflection = Bitmap.createBitmap(width, height + reflectHight, Bitmap.Config.ARGB_8888);
		Canvas canvasRef = new Canvas(bitmap4Reflection);
		canvasRef.drawBitmap(originalImage, 0.0F, 0.0F, null);
		Paint deafaultPaint = new Paint();
		deafaultPaint.setColor(0);
		canvasRef.drawRect(0.0F, height, width, height + this.reflectionGap, deafaultPaint);
		canvasRef.drawBitmap(reflectionImage, 0.0F, height + this.reflectionGap, null);
		reflectionImage.recycle();
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0.0F, originalImage.getHeight(), 0.0F, bitmap4Reflection.getHeight() + this.reflectionGap, 1895825407, 16777215, Shader.TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvasRef.drawRect(0.0F, height, width, bitmap4Reflection.getHeight() + this.reflectionGap, paint);

		setImageBitmap(bitmap4Reflection);
	}

	public Rect getScaledRect(float scaleXValue, float scaleYValue, boolean isScaled) {
		Rect firstRect = new Rect();
		getGlobalVisibleRect(firstRect);
		int imgReflectH = 0;
		if ((getScaleX() == 1.0F) && (getScaleY() == 1.0F) && (isScaled)) {
			int imgW = firstRect.right - firstRect.left;
			int imgH = firstRect.bottom - firstRect.top;

			firstRect.left = ((int) (firstRect.left + (1.0D - scaleXValue) * imgW / 2.0D));
			firstRect.top = ((int) (firstRect.top + (1.0D - scaleYValue) * imgH / 2.0D));
			firstRect.right = ((int) (firstRect.left + imgW * scaleXValue));
			firstRect.bottom = ((int) (firstRect.top + imgH * scaleYValue));

			imgReflectH = (int) (this.mReflectHight * scaleYValue + this.reflectionGap * scaleYValue + 0.5D);

			firstRect.bottom -= imgReflectH;

			return firstRect;
		}

		imgReflectH = (int) (this.mReflectHight * getScaleY() + this.reflectionGap * getScaleY() + 0.5D);
		firstRect.left = firstRect.left;
		firstRect.top = firstRect.top;
		firstRect.right = firstRect.right;
		firstRect.bottom -= imgReflectH;

		Log.d(this.TAG, "scaleXValue=" + scaleXValue + ",bottom=" + firstRect.bottom + ",top=" + firstRect.top + ",left" + firstRect.left);

		return firstRect;
	}

	public boolean getIfScale() {
		return true;
	}
}