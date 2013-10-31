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

public class ReflectImageView extends ImageView
  implements FocusedRelativeLayout.ScalePostionInterface
{
  private String TAG = "ReflectImageView";
  private int mReflectHight = 0;
  public int reflectionGap = 2;
  public int filmPostion = 0;
  public ImageView imageView = null;
  public GridView gridView = null;
  private String text = "";
  private int textSize = 24;
  public boolean isShow = false;

  public ReflectImageView(Context paramContext)
  {
    super(paramContext);
  }

  public ReflectImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public ReflectImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    Paint localPaint = new Paint();
    localPaint.setColor(-1);
    localPaint.setShadowLayer(1.0F, 2.0F, 2.0F, -16777216);
    localPaint.setTextSize(this.textSize);
    int i = 0;
    int j = 0;
    int k = (int)localPaint.measureText(getText());
    if (getHeight() > 300)
    {
      j = (getWidth() - k) / 2;
      i = getHeight() - this.mReflectHight - this.textSize;
    }
    else
    {
      j = getWidth() - k - 20;
      i = (getHeight() - this.mReflectHight + this.textSize) / 2;
    }
    paramCanvas.drawText(getText(), j, i, localPaint);
  }

  public void setText(String paramString)
  {
    this.text = paramString;
  }

  public String getText()
  {
    return this.text;
  }

  public void setImageResource(int paramInt)
  {
    Bitmap localBitmap = BitmapFactory.decodeResource(getResources(), paramInt);
    int i = getLayoutParams().height;
    int j = getLayoutParams().width;
    if ((i > localBitmap.getHeight()) || (j > localBitmap.getWidth()))
      setImageBitmap(ImageUtils.getScaleBitmap(localBitmap, j, i));
    else
      super.setImageResource(paramInt);
  }

  public void setImageResource(int paramInt1, int paramInt2)
  {
    this.mReflectHight = paramInt2;
    Bitmap localBitmap = BitmapFactory.decodeResource(getResources(), paramInt1);
    CreateReflectBitmap(localBitmap, paramInt2);
  }

  public void setImageBitmap(Bitmap paramBitmap, int paramInt)
  {
    this.mReflectHight = paramInt;
    CreateReflectBitmap(paramBitmap, paramInt);
  }

  private void CreateReflectBitmap(Bitmap paramBitmap, int paramInt)
  {
    int i = getLayoutParams().height - paramInt - this.reflectionGap;
    int j = getLayoutParams().width;
    Bitmap localBitmap1 = null;
    if ((i > paramBitmap.getHeight()) || (j > paramBitmap.getWidth()))
      localBitmap1 = ImageUtils.getScaleBitmap(paramBitmap, j, i);
    else
      localBitmap1 = paramBitmap;
    int k = localBitmap1.getWidth();
    int m = localBitmap1.getHeight();
    Matrix localMatrix = new Matrix();
    localMatrix.preScale(1.0F, -1.0F);
    Bitmap localBitmap2 = Bitmap.createBitmap(localBitmap1, 0, paramInt, k, m - paramInt, localMatrix, false);
    Bitmap localBitmap3 = Bitmap.createBitmap(k, m + paramInt, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap3);
    localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, null);
    Paint localPaint1 = new Paint();
    localPaint1.setColor(0);
    localCanvas.drawRect(0.0F, m, k, m + this.reflectionGap, localPaint1);
    localCanvas.drawBitmap(localBitmap2, 0.0F, m + this.reflectionGap, null);
    localBitmap2.recycle();
    Paint localPaint2 = new Paint();
    LinearGradient localLinearGradient = new LinearGradient(0.0F, localBitmap1.getHeight(), 0.0F, localBitmap3.getHeight() + this.reflectionGap, 1895825407, 16777215, Shader.TileMode.CLAMP);
    localPaint2.setShader(localLinearGradient);
    localPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    localCanvas.drawRect(0.0F, m, k, localBitmap3.getHeight() + this.reflectionGap, localPaint2);
    setImageBitmap(localBitmap3);
  }

  public Rect getScaledRect(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    Rect localRect = new Rect();
    getGlobalVisibleRect(localRect);
    int i = 0;
    if ((getScaleX() == 1.0F) && (getScaleY() == 1.0F) && (paramBoolean))
    {
      int j = localRect.right - localRect.left;
      int k = localRect.bottom - localRect.top;
      localRect.left = ((int)(localRect.left + (1.0D - paramFloat1) * j / 2.0D));
      localRect.top = ((int)(localRect.top + (1.0D - paramFloat2) * k / 2.0D));
      localRect.right = ((int)(localRect.left + j * paramFloat1));
      localRect.bottom = ((int)(localRect.top + k * paramFloat2));
      i = (int)(this.mReflectHight * paramFloat2 + this.reflectionGap * paramFloat2 + 0.5D);
      localRect.bottom -= i;
      return localRect;
    }
    i = (int)(this.mReflectHight * getScaleY() + this.reflectionGap * getScaleY() + 0.5D);
    localRect.left = localRect.left;
    localRect.top = localRect.top;
    localRect.right = localRect.right;
    localRect.bottom -= i;
    Log.d(this.TAG, "scaleXValue=" + paramFloat1 + ",bottom=" + localRect.bottom + ",top=" + localRect.top + ",left" + localRect.left);
    return localRect;
  }

  public boolean getIfScale()
  {
    return true;
  }
}

/* Location:           C:\Users\Administrator\Desktop\AliTvAppSdk.jar
 * Qualified Name:     com.yunos.tv.app.widget.ReflectImageView
 * JD-Core Version:    0.6.2
 */