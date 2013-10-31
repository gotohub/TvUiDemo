package io.viva.tv.app.widget.adapter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

public class ReflectingImageAdapter extends AbsCoverFlowAdapter
{
  private final AbsCoverFlowAdapter linkedAdapter;
  private float reflectionGap;
  private float imageReflectionRatio;

  public void setWidthRatio(float paramFloat)
  {
    this.imageReflectionRatio = paramFloat;
  }

  public ReflectingImageAdapter(AbsCoverFlowAdapter paramAbsCoverFlowAdapter)
  {
    this.linkedAdapter = paramAbsCoverFlowAdapter;
  }

  public void setReflectionGap(float paramFloat)
  {
    this.reflectionGap = paramFloat;
  }

  public float getReflectionGap()
  {
    return this.reflectionGap;
  }

  protected Bitmap createBitmap(int paramInt)
  {
    return createReflectedImages(this.linkedAdapter.getItem(paramInt));
  }

  public Bitmap createReflectedImages(Bitmap paramBitmap)
  {
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    Matrix localMatrix = new Matrix();
    localMatrix.preScale(1.0F, -1.0F);
    Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, 0, (int)(j * this.imageReflectionRatio), i, (int)(j - j * this.imageReflectionRatio), localMatrix, false);
    Bitmap localBitmap2 = Bitmap.createBitmap(i, (int)(j + j * this.imageReflectionRatio), Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap2);
    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, null);
    Paint localPaint1 = new Paint();
    localPaint1.setColor(17170445);
    localCanvas.drawBitmap(localBitmap1, 0.0F, j + this.reflectionGap, null);
    Paint localPaint2 = new Paint();
    LinearGradient localLinearGradient = new LinearGradient(0.0F, paramBitmap.getHeight(), 0.0F, localBitmap2.getHeight() + this.reflectionGap, 1895825407, 16777215, Shader.TileMode.CLAMP);
    localPaint2.setShader(localLinearGradient);
    localPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    localCanvas.drawRect(0.0F, j, i, localBitmap2.getHeight() + this.reflectionGap, localPaint2);
    return localBitmap2;
  }

  public int getCount()
  {
    return this.linkedAdapter.getCount();
  }
}

/* Location:           C:\Users\Administrator\Desktop\AliTvAppSdk.jar
 * Qualified Name:     com.yunos.tv.app.widget.ReflectingImageAdapter
 * JD-Core Version:    0.6.2
 */