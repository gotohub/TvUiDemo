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

	public static Bitmap getBitmapByResource(Context context, int id) {
		Resources resources = context.getResources();

		Drawable drawable = resources.getDrawable(id);
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

		resources = null;
		drawable = null;

		return bitmap;
	}

	public static void saveImageToSdcard(String path, Bitmap bitmap) {
		if ((path == null) || ("".equals(path))) {
			return;
		}
		File file = new File(path);
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		if (bitmap == null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = w / width;
		float scaleHeight = h / height;
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return newbmp;
	}

	public static float px2dip(Context context, int px) {
		return (int) (px / getDensity(context) + 0.5F);
	}

	private static DisplayMetrics getDm(Context context) {
		if (dm == null) {
			WindowManager mWm = (WindowManager) context.getSystemService("window");
			Display display = mWm.getDefaultDisplay();
			dm = new DisplayMetrics();
			display.getMetrics(dm);
		}
		return dm;
	}

	public static float getDensity(Context context) {
		return getDm(context).density;
	}

	public static Bitmap getScaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		float scaleWidth = newWidth / width;
		float scaleHeight = newHeight / height;
		if (scaleWidth <= 0.0F) {
			scaleWidth = 1.0F;
		}
		if (scaleHeight <= 0.0F) {
			scaleHeight = 1.0F;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		int color = -12434878;
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(-12434878);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap createReflectBitmap(int yShift, int reflectedImageHeight, Bitmap originalImage) {
		int width = originalImage.getWidth();
		Matrix matrix = new Matrix();
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, reflectedImageHeight, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		matrix.reset();
		matrix.preScale(1.0F, -1.0F);
		matrix.postTranslate(0.0F, originalImage.getHeight() + yShift);
		canvas.drawBitmap(originalImage, matrix, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0.0F, 0.0F, 0.0F, bitmapWithReflection.getHeight(), -1862270977, 16777215, Shader.TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

		canvas.drawRect(0.0F, 0.0F, width, bitmapWithReflection.getHeight(), paint);

		return bitmapWithReflection;
	}

	public static Bitmap createReflectBitmap(int reflectedImageHeight, Bitmap originalImage) {
		int width = originalImage.getWidth();
		Matrix matrix = new Matrix();
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, reflectedImageHeight, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		matrix.reset();
		matrix.preScale(1.0F, -1.0F);
		matrix.postTranslate(0.0F, originalImage.getHeight());
		canvas.drawBitmap(originalImage, matrix, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0.0F, 0.0F, 0.0F, bitmapWithReflection.getHeight(), 1895825407, 16777215, Shader.TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

		canvas.drawRect(0.0F, 0.0F, width, bitmapWithReflection.getHeight(), paint);

		return bitmapWithReflection;
	}

	public static Bitmap decodeBitmap(Resources res, int resId, int targetWidth, int targetHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inDither = false;
		BitmapFactory.decodeResource(res, resId, options);
		return BitmapFactory.decodeResource(res, resId, getScaleOptions(targetWidth, targetHeight, options));
	}

	private static BitmapFactory.Options getScaleOptions(int targetWidth, int targetHeight, BitmapFactory.Options options) {
		options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inTempStorage = new byte[16384];
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inDither = false;
		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options, true);
		} catch (Exception localException) {
		}
		return options;
	}

	public static Bitmap getOverLapImage(Bitmap source, Bitmap faceBitmap, Paint paint) {
		int targetWidth = source.getWidth();
		int targetHeight = source.getHeight();

		Bitmap newBitmap = source.copy(Bitmap.Config.RGB_565, true);
		source.recycle();
		source = null;
		Canvas canvas = new Canvas(newBitmap);

		int sourceWidth = faceBitmap.getWidth();
		int sourceHeight = faceBitmap.getHeight();

		float scaleWidth = targetWidth / sourceWidth;
		float scaleHeight = targetHeight / sourceHeight;
		Matrix matrix = new Matrix();
		matrix.setScale(scaleWidth, scaleHeight);
		canvas.drawBitmap(faceBitmap, matrix, paint);

		return newBitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;

		if ((height > reqHeight) || (width > reqWidth)) {
			int heightRatio = Math.round(height / reqHeight);
			int widthRatio = Math.round(width / reqWidth);
			float totalPixels = width * height;
			float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}
}