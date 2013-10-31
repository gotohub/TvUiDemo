package io.viva.tv.app.widget.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ImageUtils {
	private static Paint paint;
	private static DisplayMetrics dm = null;

	public static Bitmap getBitmapByResource(Context paramContext, int paramInt) {
		Resources localResources = paramContext.getResources();
		Drawable localDrawable = localResources.getDrawable(paramInt);
		Bitmap localBitmap = ((BitmapDrawable) localDrawable).getBitmap();
		localResources = null;
		localDrawable = null;
		return localBitmap;
	}

	public static void saveImageToSdcard(String paramString, Bitmap paramBitmap) {
		if ((null == paramString) || ("".equals(paramString))) {
			return;
		}
		File localFile = new File(paramString);
		FileOutputStream localFileOutputStream = null;
		try {
			if (!localFile.exists()) {
				localFile.createNewFile();
			}
			localFileOutputStream = new FileOutputStream(localFile);
			paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, localFileOutputStream);
		} catch (FileNotFoundException localFileNotFoundException) {
			localFileNotFoundException.printStackTrace();
		} catch (IOException localIOException3) {
			localIOException3.printStackTrace();
		} finally {
			if (localFileOutputStream != null) {
				try {
					localFileOutputStream.flush();
					localFileOutputStream.close();
				} catch (IOException localIOException5) {
					localIOException5.printStackTrace();
				}
			}
		}
	}

	public static Bitmap zoomBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2) {
		if (paramBitmap == null) {
			return null;
		}
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		Matrix localMatrix = new Matrix();
		float f1 = paramInt1 / i;
		float f2 = paramInt2 / j;
		localMatrix.postScale(f1, f2);
		Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
		return localBitmap;
	}

	public static float px2dip(Context paramContext, int paramInt) {
		return (int) (paramInt / getDensity(paramContext) + 0.5F);
	}

	private static DisplayMetrics getDm(Context paramContext) {
		if (dm == null) {
			WindowManager localWindowManager = (WindowManager) paramContext.getSystemService("window");
			Display localDisplay = localWindowManager.getDefaultDisplay();
			dm = new DisplayMetrics();
			localDisplay.getMetrics(dm);
		}
		return dm;
	}

	public static float getDensity(Context paramContext) {
		return getDm(paramContext).density;
	}

	public static Bitmap getScaleBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2) {
		Bitmap localBitmap = paramBitmap;
		int i = localBitmap.getWidth();
		int j = localBitmap.getHeight();
		float f1 = paramInt1 / i;
		float f2 = paramInt2 / j;
		if (f1 <= 0.0F) {
			f1 = 1.0F;
		}
		if (f2 <= 0.0F) {
			f2 = 1.0F;
		}
		Matrix localMatrix = new Matrix();
		localMatrix.postScale(f1, f2);
		return Bitmap.createBitmap(localBitmap, 0, 0, i, j, localMatrix, true);
	}

	public static Bitmap drawableToBitmap(Drawable paramDrawable) {
		int i = paramDrawable.getIntrinsicWidth();
		int j = paramDrawable.getIntrinsicHeight();
		Bitmap localBitmap = Bitmap.createBitmap(i, j, paramDrawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);
		paramDrawable.setBounds(0, 0, i, j);
		paramDrawable.draw(localCanvas);
		return localBitmap;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap paramBitmap, float paramFloat) {
		Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap);
		Paint localPaint = new Paint();
		Rect localRect = new Rect(0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
		RectF localRectF = new RectF(localRect);
		localPaint.setAntiAlias(true);
		localCanvas.drawARGB(0, 0, 0, 0);
		localPaint.setColor(-12434878);
		localCanvas.drawRoundRect(localRectF, paramFloat, paramFloat, localPaint);
		localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		localCanvas.drawBitmap(paramBitmap, localRect, localRect, localPaint);
		return localBitmap;
	}

	public static Bitmap createReflectBitmap(int paramInt1, int paramInt2, Bitmap paramBitmap) {
		int i = paramBitmap.getWidth();
		Matrix localMatrix = new Matrix();
		Bitmap localBitmap = Bitmap.createBitmap(i, paramInt2, Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap);
		localMatrix.reset();
		localMatrix.preScale(1.0F, -1.0F);
		localMatrix.postTranslate(0.0F, paramBitmap.getHeight() + paramInt1);
		localCanvas.drawBitmap(paramBitmap, localMatrix, null);
		Paint localPaint = new Paint();
		LinearGradient localLinearGradient = new LinearGradient(0.0F, 0.0F, 0.0F, localBitmap.getHeight(), -1862270977, 16777215, Shader.TileMode.CLAMP);
		localPaint.setShader(localLinearGradient);
		localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		localCanvas.drawRect(0.0F, 0.0F, i, localBitmap.getHeight(), localPaint);
		return localBitmap;
	}

	public static Bitmap createReflectBitmap(int paramInt, Bitmap paramBitmap) {
		int i = paramBitmap.getWidth();
		Matrix localMatrix = new Matrix();
		Bitmap localBitmap = Bitmap.createBitmap(i, paramInt, Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap);
		localMatrix.reset();
		localMatrix.preScale(1.0F, -1.0F);
		localMatrix.postTranslate(0.0F, paramBitmap.getHeight());
		localCanvas.drawBitmap(paramBitmap, localMatrix, null);
		Paint localPaint = new Paint();
		LinearGradient localLinearGradient = new LinearGradient(0.0F, 0.0F, 0.0F, localBitmap.getHeight(), 1895825407, 16777215, Shader.TileMode.CLAMP);
		localPaint.setShader(localLinearGradient);
		localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		localCanvas.drawRect(0.0F, 0.0F, i, localBitmap.getHeight(), localPaint);
		return localBitmap;
	}

	public static Bitmap decodeBitmap(Resources paramResources, int paramInt1, int paramInt2, int paramInt3) {
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		localOptions.inDither = false;
		BitmapFactory.decodeResource(paramResources, paramInt1, localOptions);
		return BitmapFactory.decodeResource(paramResources, paramInt1, getScaleOptions(paramInt2, paramInt3, localOptions));
	}

	private static BitmapFactory.Options getScaleOptions(int paramInt1, int paramInt2, BitmapFactory.Options paramOptions) {
		paramOptions.inSampleSize = calculateInSampleSize(paramOptions, paramInt1, paramInt2);
		paramOptions.inJustDecodeBounds = false;
		paramOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		paramOptions.inTempStorage = new byte[16384];
		paramOptions.inPurgeable = true;
		paramOptions.inInputShareable = true;
		paramOptions.inDither = false;
		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(paramOptions, true);
		} catch (Exception localException) {
		}
		return paramOptions;
	}

	public static Bitmap getOverLapImage(Bitmap paramBitmap1, Bitmap paramBitmap2, Paint paramPaint) {
		int i = paramBitmap1.getWidth();
		int j = paramBitmap1.getHeight();
		Bitmap localBitmap = paramBitmap1.copy(Bitmap.Config.RGB_565, true);
		paramBitmap1.recycle();
		paramBitmap1 = null;
		Canvas localCanvas = new Canvas(localBitmap);
		int k = paramBitmap2.getWidth();
		int m = paramBitmap2.getHeight();
		float f1 = i / k;
		float f2 = j / m;
		Matrix localMatrix = new Matrix();
		localMatrix.setScale(f1, f2);
		localCanvas.drawBitmap(paramBitmap2, localMatrix, paramPaint);
		return localBitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2) {
		int i = paramOptions.outHeight;
		int j = paramOptions.outWidth;
		int k = 1;
		if ((i > paramInt2) || (j > paramInt1)) {
			int m = Math.round(i / paramInt2);
			int n = Math.round(j / paramInt1);
			float f1 = j * i;
			float f2 = paramInt1 * paramInt2 * 2;
			while (f1 / (k * k) > f2) {
				k++;
			}
		}
		return k;
	}
}