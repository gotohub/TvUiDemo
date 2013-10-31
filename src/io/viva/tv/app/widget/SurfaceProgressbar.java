package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceProgressbar extends SurfaceView
{
  static final String TAG = "SurfaceProgressbar";
  Matrix matrix = new Matrix();
  BitmapDrawable mDrawable;
  Rect mDrawablePadding = new Rect();
  SurfaceHolder mSfh = getHolder();
  int mDigree = 0;
  boolean mIsStop = false;

  public SurfaceProgressbar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  public SurfaceProgressbar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public SurfaceProgressbar(Context paramContext)
  {
    super(paramContext);
  }

  protected synchronized void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    setMeasuredDimension(this.mDrawable.getBitmap().getWidth() + getPaddingLeft() + getPaddingRight(), this.mDrawable.getBitmap().getHeight() + getPaddingBottom() + getPaddingTop());
  }

  public void setProgressResId(int paramInt)
  {
    this.mDrawable = ((BitmapDrawable)getResources().getDrawable(paramInt));
    this.mDrawable.getPadding(this.mDrawablePadding);
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    start();
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    stop();
  }

  private void stop()
  {
    this.mIsStop = true;
  }

  private void start()
  {
    this.mIsStop = false;
    new Thread()
    {
      public void run()
      {
        while (!SurfaceProgressbar.this.mIsStop)
        {
          Canvas localCanvas = SurfaceProgressbar.this.mSfh.lockCanvas();
          if (localCanvas == null)
          {
            try
            {
              Thread.sleep(10L);
            }
            catch (InterruptedException localInterruptedException1)
            {
              localInterruptedException1.printStackTrace();
            }
          }
          else
          {
            localCanvas.save();
            int i = SurfaceProgressbar.this.getLeft();
            int j = SurfaceProgressbar.this.getTop();
            int k = SurfaceProgressbar.this.getRight();
            int m = SurfaceProgressbar.this.getBottom();
            localCanvas.translate(SurfaceProgressbar.this.getPaddingLeft(), SurfaceProgressbar.this.getPaddingTop());
            localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            int n = i + SurfaceProgressbar.this.getPaddingLeft() + SurfaceProgressbar.this.mDrawablePadding.left;
            int i1 = k - SurfaceProgressbar.this.getPaddingRight() - SurfaceProgressbar.this.mDrawablePadding.right;
            int i2 = j + SurfaceProgressbar.this.getPaddingTop() + SurfaceProgressbar.this.mDrawablePadding.top;
            int i3 = m - SurfaceProgressbar.this.getPaddingBottom() - SurfaceProgressbar.this.mDrawablePadding.bottom;
            SurfaceProgressbar.this.matrix.setRotate(SurfaceProgressbar.this.mDigree, (n + i1) / 2 - i, (i2 + i3) / 2 - j);
            SurfaceProgressbar.this.mDigree += 10;
            SurfaceProgressbar.this.mDrawable.setBounds(n - i, i2 - j, i1 - i, i3 - j);
            localCanvas.setMatrix(SurfaceProgressbar.this.matrix);
            SurfaceProgressbar.this.mDrawable.draw(localCanvas);
            localCanvas.restore();
            SurfaceProgressbar.this.mSfh.unlockCanvasAndPost(localCanvas);
            try
            {
              Thread.sleep(30L);
            }
            catch (InterruptedException localInterruptedException2)
            {
              localInterruptedException2.printStackTrace();
            }
          }
        }
      }
    }
    .start();
  }
}

/* Location:           C:\Users\Administrator\Desktop\AliTvAppSdk.jar
 * Qualified Name:     com.yunos.tv.app.widget.SurfaceProgressbar
 * JD-Core Version:    0.6.2
 */