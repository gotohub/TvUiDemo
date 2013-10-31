package io.viva.tv.app.widget.adapter;

import io.viva.tv.app.widget.AbsSpinner;
import io.viva.tv.app.widget.AbsSpinner.LayoutParams;
import io.viva.tv.lib.DataCache;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public abstract class AbsCoverFlowAdapter extends BaseAdapter {
	private static final String TAG = AbsCoverFlowAdapter.class.getSimpleName();
	private float width = 0.0F;
	private float height = 0.0F;
	private final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();
	private static final int MAX_COUNT = 20;
	protected final DataCache<Integer, Bitmap> mCache = new DataCache<Integer, Bitmap>(MAX_COUNT);
	Matrix matrix = new Matrix();

	public synchronized void setWidth(float paramFloat) {
		this.width = paramFloat;
	}

	public synchronized void setHeight(float paramFloat) {
		this.height = paramFloat;
	}

	public final Bitmap getItem(int paramInt) {
		Bitmap localBitmap1 = (Bitmap) this.mCache.objectForKey(Integer.valueOf(paramInt));
		if (localBitmap1 != null)
			return localBitmap1;
		Bitmap localBitmap2 = createBitmap(paramInt);
		this.mCache.putObjectForKey(Integer.valueOf(paramInt), localBitmap2);
		return localBitmap2;
	}

	protected abstract Bitmap createBitmap(int paramInt);

	public final synchronized long getItemId(int paramInt) {
		return paramInt;
	}

	public final synchronized ImageView getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		ImageView localImageView;
		if (paramView == null) {
			Context context = paramViewGroup.getContext();
			Log.v(TAG, "Creating Image view at position: " + paramInt + ":" + this);
			localImageView = new ImageView(context);
			localImageView.setLayoutParams(new AbsSpinner.LayoutParams((int) this.width, (int) this.height));
		} else {
			Log.v(TAG, "Reusing view at position: " + paramInt + ":" + this);
			localImageView = (ImageView) paramView;
		}
		localImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		Bitmap bitmap = getItem(paramInt);
		BitmapDrawable localBitmapDrawable = new BitmapDrawable(bitmap);
		BitmapShader localBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		Paint localPaint = localBitmapDrawable.getPaint();
		localPaint.setAntiAlias(true);
		this.matrix.setScale(this.width / bitmap.getWidth(), this.height / bitmap.getHeight(), 0.5F, 0.5F);
		localBitmapShader.setLocalMatrix(this.matrix);
		localPaint.setShader(localBitmapShader);
		localImageView.setImageDrawable(localBitmapDrawable);
		return localImageView;
	}
}