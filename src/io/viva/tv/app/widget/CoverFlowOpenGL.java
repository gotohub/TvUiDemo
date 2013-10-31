package io.viva.tv.app.widget;

import io.viva.tv.lib.DataCache;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public class CoverFlowOpenGL extends GLSurfaceView implements
		GLSurfaceView.Renderer {
	private static final String TAG = "CoverFlowOpenGL";
	private static final int TOUCH_MINIMUM_MOVE = 5;
	private static final int IMAGE_SIZE = 256;
	private static final int MAX_TILES = 32;
	private static final float SCALE = 0.4F;
	private static final float SPREAD_IMAGE = 0.14F;
	private static final float FLANK_SPREAD = 0.4F;
	private static final float FRICTION = 10.0F;
	private static final float MAX_SPEED = 6.0F;
	private static final float POS_SCALE = 10.0F;
	private static final float IMAGE_SCALE = 0.38F;
	private final Object mutex = new Object();

	private boolean readyToShowFocus = false;

	private static final float[] GVertices = { -1.0F, -1.0F, 0.0F, 1.0F, -1.0F,
			0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F };

	private static final float[] GTextures = { 0.0F, 1.0F, 1.0F, 1.0F, 0.0F,
			0.0F, 1.0F, 0.0F };
	private GL10 mGLContext;
	private FloatBuffer mVerticesBuffer;
	private FloatBuffer mTexturesBuffer;
	private float[] mMatrix;
	private float mOffset;
	private int mLastOffset;
	private RectF mTouchRect;
	private int mWidth;
	private boolean mTouchMoved;
	private float mTouchStartPos;
	private float mTouchStartX;
	private float mTouchStartY;
	private float mStartOffset;
	private long mStartTime;
	private float mStartSpeed;
	private float mDuration;
	private Runnable mAnimationRunnable;
	private VelocityTracker mVelocity;
	private boolean mStopBackgroundThread;
	private CoverFlowListener mListener;
	private DataCache<Integer, CoverFlowRecord> mCache;
	private FocusView mFocusRect;
	private int mCenterTiles;
	private int mLeftTiles;
	private boolean mHasMirror;
	private float mScale;
	private float mAngle;
	private Rect mCenterBound;

	public CoverFlowOpenGL(Context context) {
		this(context, null);
	}

	public CoverFlowOpenGL(Context context, AttributeSet attr) {
		super(context);

		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		setRenderer(this);
		setRenderMode(0);

		getHolder().setFormat(-3);

		setZOrderOnTop(true);
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.mCache = new DataCache(32);
		this.mLastOffset = 0;
		this.mOffset = 0.0F;

		this.mCenterTiles = 1;
		this.mLeftTiles = 1;
		this.mHasMirror = false;
		this.mScale = 1.0F;
		this.mAngle = 1.0F;
		this.mCenterBound = new Rect();
	}

	public void ProjectToScreen(GL10 gl, float x, float y, float z, float[] ret) {
		int[] viewport = new int[4];
		float[] model = new float[16];
		float[] proj = new float[16];

		Log.d("SP", "x = " + x + ",y = " + y);

		gl.glGetIntegerv(2978, viewport, 0);

		((GL11) gl).glGetFloatv(2982, model, 0);
		((GL11) gl).glGetFloatv(2983, proj, 0);

		GLU.gluProject(x, y, z, model, 0, proj, 0, viewport, 0, ret, 0);
		Log.d("SP", "x = " + ret[0] + ",y = " + ret[1]);
	}

	public void setCoverFlowListener(CoverFlowListener listener) {
		this.mListener = listener;
	}

	private float checkValid(float off) {
		int max = this.mListener.getCount(this) - 1;
		if (off < 0.0F)
			return 0.0F;
		if (off > max) {
			return max;
		}
		return off;
	}

	public void setSelection(int position) {
		endAnimation();
		this.mOffset = position;
		requestRender();
	}

	private void addOverlayView() {
		ViewGroup parent = (ViewGroup) getParent();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(0, 0);
		this.mFocusRect = new FocusView(getContext());
		this.mFocusRect.setLayoutParams(params);
		this.mFocusRect.setZOrderOnTop(true);
		parent.addView(this.mFocusRect);
	}

	private void showOverlayView() {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				this.mCenterBound.width(), this.mCenterBound.height());
		params.setMargins(this.mCenterBound.left, this.mCenterBound.top,
				this.mCenterBound.right, this.mCenterBound.bottom);
		this.mFocusRect.setLayoutParams(params);
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		addOverlayView();
	}

	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		if (gainFocus)
			this.mFocusRect.setVisibility(0);
		else {
			this.mFocusRect.setVisibility(4);
		}
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 21:
			return switchCover(true);
		case 22:
			return switchCover(false);
		case 23:
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private boolean switchCover(boolean isLeft) {
		if ((isLeft) && (Math.abs(this.mOffset) <= 0.1F))
			return false;
		if ((!isLeft) && (this.mOffset >= this.mListener.getCount(this) - 1))
			return false;
		endAnimation();
		if (isLeft)
			this.mStartOffset = (this.mOffset - 0.5F);
		else
			this.mStartOffset = (this.mOffset + 0.5F);
		double speed = -400.0D;
		if (isLeft)
			speed = -speed;
		speed = speed / this.mWidth * 10.0D;
		if (speed > 6.0D)
			speed = 6.0D;
		else if (speed < -6.0D) {
			speed = -6.0D;
		}
		startAnimation(-speed);
		return true;
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case 0:
			touchBegan(event);
			return true;
		case 2:
			touchMoved(event);
			return true;
		case 1:
			touchEnded(event);
			return true;
		}
		return false;
	}

	private float calculatePos(float x) {
		return x / this.mWidth * 10.0F - 5.0F;
	}

	private void touchBegan(MotionEvent event) {
		endAnimation();

		float x = event.getX();
		this.mTouchStartX = x;
		this.mTouchStartY = event.getY();
		this.mStartTime = System.currentTimeMillis();
		this.mStartOffset = this.mOffset;

		this.mTouchMoved = false;

		this.mTouchStartPos = calculatePos(x);
		this.mTouchStartPos /= 2.0F;

		this.mVelocity = VelocityTracker.obtain();
		this.mVelocity.addMovement(event);
	}

	private void touchMoved(MotionEvent event) {
		float pos = calculatePos(event.getX());
		pos /= 2.0F;

		if (!this.mTouchMoved) {
			float dx = Math.abs(event.getX() - this.mTouchStartX);
			float dy = Math.abs(event.getY() - this.mTouchStartY);

			if ((dx < 5.0F) && (dy < 5.0F)) {
				return;
			}
			this.mTouchMoved = true;
		}

		this.mOffset = checkValid(this.mStartOffset + this.mTouchStartPos - pos);

		requestRender();
		this.mVelocity.addMovement(event);
	}

	private void touchEnded(MotionEvent event) {
		float pos = calculatePos(event.getX());
		pos /= 2.0F;

		if (this.mTouchMoved) {
			this.mStartOffset += this.mTouchStartPos - pos;
			this.mStartOffset = checkValid(this.mStartOffset);
			this.mOffset = this.mStartOffset;

			this.mVelocity.addMovement(event);

			this.mVelocity.computeCurrentVelocity(1000);
			double speed = this.mVelocity.getXVelocity();
			speed = speed / this.mWidth * 10.0D;
			if (speed > 6.0D)
				speed = 6.0D;
			else if (speed < -6.0D) {
				speed = -6.0D;
			}
			startAnimation(-speed);
		} else if (this.mTouchRect.contains(event.getX(), event.getY())) {
			this.mListener.topTileClicked(this, (int) (this.mOffset + 0.01D));
		}
	}

	private void startAnimation(double speed) {
		if (this.mAnimationRunnable != null) {
			return;
		}
		double delta = speed * speed / 20.0D;
		if (speed < 0.0D) {
			delta = -delta;
		}
		double nearest = this.mStartOffset + delta;
		nearest = Math.floor(nearest + 0.5D);
		nearest = checkValid((float) nearest);

		this.mStartSpeed = ((float) Math.sqrt(Math.abs(nearest
				- this.mStartOffset) * 10.0D * 2.0D));
		if (nearest < this.mStartOffset) {
			this.mStartSpeed = (-this.mStartSpeed);
		}
		this.mDuration = Math.abs(this.mStartSpeed / 10.0F);
		this.mStartTime = AnimationUtils.currentAnimationTimeMillis();

		this.mAnimationRunnable = new Runnable() {
			public void run() {
				CoverFlowOpenGL.this.driveAnimation();
			}
		};
		post(this.mAnimationRunnable);
	}

	private void driveAnimation() {
		float elapsed = (float) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime) / 1000.0F;
		if (elapsed >= this.mDuration) {
			endAnimation();
		} else {
			updateAnimationAtElapsed(elapsed);
			post(this.mAnimationRunnable);
		}
	}

	private void endAnimation() {
		if (this.mAnimationRunnable != null) {
			this.mOffset = ((float) Math.floor(this.mOffset + 0.5D));
			this.mOffset = checkValid(this.mOffset);

			requestRender();

			removeCallbacks(this.mAnimationRunnable);
			this.mAnimationRunnable = null;
		}
	}

	private void updateAnimationAtElapsed(float elapsed) {
		if (elapsed > this.mDuration) {
			elapsed = this.mDuration;
		}
		float delta = Math.abs(this.mStartSpeed) * elapsed - 10.0F * elapsed
				* elapsed / 2.0F;
		if (this.mStartSpeed < 0.0F) {
			delta = -delta;
		}
		this.mOffset = checkValid(this.mStartOffset + delta);
		requestRender();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		this.mCache.clear();

		this.mGLContext = gl;
		this.mVerticesBuffer = makeFloatBuffer(GVertices);
		this.mTexturesBuffer = makeFloatBuffer(GTextures);
	}

	private void notifyShowFocusView() {
		Runnable r = new Runnable() {
			public void run() {
				CoverFlowOpenGL.this.showOverlayView();
				synchronized (CoverFlowOpenGL.this.mutex) {
					CoverFlowOpenGL.this.readyToShowFocus = true;
					CoverFlowOpenGL.this.mutex.notify();
				}
			}
		};
		getHandler().post(r);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		this.mWidth = w;

		float imagew = w * 0.45F / 0.4F / 2.0F;
		float imageh = h * 0.45F / 0.4F / 2.0F;
		this.mTouchRect = new RectF(w / 2 - imagew, h / 2 - imageh, w / 2
				+ imagew, h / 2 + imageh);

		gl.glViewport(0, 0, w, h);

		float ratio = w / h;
		gl.glMatrixMode(5889);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio * 0.4F, ratio * 0.4F, -0.4F, 0.4F, 1.0F, 3.0F);

		calcCenterRect(gl);
		notifyShowFocusView();
	}

	public void clearTileCache() {
		this.mCache.clear();
	}

	public void onDrawFrame(GL10 gl) {
		gl.glMatrixMode(5888);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);

		gl.glDisable(2929);
		gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		gl.glClear(16640);

		gl.glEnableClientState(32884);
		gl.glEnableClientState(32888);

		draw(gl);
	}

	private void draw(GL10 gl) {
		this.mStopBackgroundThread = true;
		gl.glPushMatrix();

		gl.glVertexPointer(3, 5126, 0, this.mVerticesBuffer);
		gl.glTexCoordPointer(2, 5126, 0, this.mTexturesBuffer);
		gl.glEnable(3553);

		gl.glEnable(3042);
		gl.glBlendFunc(770, 771);

		float offset = this.mOffset;
		int i = 0;
		int max = this.mListener.getCount(this) - 1;
		int mid = (int) Math.floor(offset + 0.5D);
		int iStartPos = mid - this.mLeftTiles;

		if (iStartPos < 0) {
			iStartPos = 0;
		}

		for (i = iStartPos; i < mid; i++) {
			drawTile(i, i - offset, gl);
		}

		int iEndPos = mid + this.mLeftTiles;
		if (iEndPos > max)
			iEndPos = max;
		for (i = iEndPos; i >= mid; i--) {
			drawTile(i, i - offset, gl);
		}

		if (this.mLastOffset != (int) offset) {
			this.mListener.tileOnTop(this, (int) offset);
			this.mLastOffset = ((int) offset);
		}

		gl.glPopMatrix();
		this.mStopBackgroundThread = false;
	}

	private void calcCenterRect(GL10 gl) {
		gl.glPushMatrix();
		gl.glMatrixMode(5888);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
		gl.glVertexPointer(3, 5126, 0, this.mVerticesBuffer);
		gl.glTexCoordPointer(2, 5126, 0, this.mTexturesBuffer);
		gl.glEnable(3553);

		gl.glEnable(3042);
		gl.glBlendFunc(770, 771);
		float sc = 0.38F * this.mScale;
		gl.glScalef(sc, sc, 1.0F);

		float[] ret = new float[4];
		ProjectToScreen(gl, this.mVerticesBuffer.get(0),
				this.mVerticesBuffer.get(1), this.mVerticesBuffer.get(2), ret);
		this.mCenterBound.left = ((int) ret[0] - 5);
		this.mCenterBound.top = ((int) ret[1] - 5);
		ProjectToScreen(gl, this.mVerticesBuffer.get(9),
				this.mVerticesBuffer.get(10), this.mVerticesBuffer.get(11), ret);
		this.mCenterBound.right = ((int) ret[0] + 5);
		this.mCenterBound.bottom = ((int) ret[1] + 5);
		gl.glPopMatrix();
	}

	private void drawTile(int position, float off, GL10 gl) {
		CoverFlowRecord fcr = getTileAtIndex(position, gl);
		if ((fcr != null) && (fcr.mTexture != 0)) {
			float trans = off * 0.14F;
			float f = off * 0.4F;
			if (f > 0.4F)
				f = 0.4F;
			else if (f < -0.4F) {
				f = -0.4F;
			}

			float sc = 0.38F * this.mScale;
			trans += f * 1.0F;

			gl.glPushMatrix();
			gl.glBindTexture(3553, fcr.mTexture);

			gl.glTranslatef(trans, 0.0F, 0.0F);
			gl.glScalef(sc, sc, 1.0F);

			gl.glRotatef(
					(float) (-f * this.mAngle * 180.0F / 3.141592653589793D),
					0.0F, 1.0F, 0.0F);
			gl.glDrawArrays(5, 0, 4);

			gl.glTranslatef(0.0F, -2.0F, 0.0F);
			gl.glScalef(1.0F, -1.0F, 1.0F);
			gl.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			gl.glDrawArrays(5, 0, 4);
			gl.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			gl.glPopMatrix();
		}
	}

	private CoverFlowRecord getTileAtIndex(int position, GL10 gl) {
		synchronized (this) {
			CoverFlowRecord fcr = (CoverFlowRecord) this.mCache
					.objectForKey(Integer.valueOf(position));
			if (fcr == null) {
				long bitmapDuration = 0L;
				Bitmap bm = this.mListener.getImage(this, position);

				if (bm == null) {
					return null;
				}
				int texture = imageToTexture(bm, gl);

				fcr = new CoverFlowRecord(texture, gl);
				this.mCache.putObjectForKey(Integer.valueOf(position), fcr);
			}
			return fcr;
		}
	}

	private static Bitmap createTextureBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap bm = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
		Canvas cv = new Canvas(bm);
		if ((width > 256) || (height > 256)) {
			Rect src = new Rect(0, 0, width, height);

			float scale = 1.0F;
			if (width > height)
				scale = 256.0F / width;
			else
				scale = 256.0F / height;
			width = (int) (width * scale);
			height = (int) (height * scale);
			float left = (256 - width) / 2.0F;
			float top = (256 - height) / 2.0F;
			RectF dst = new RectF(left, top, left + width, top + height);

			cv.drawBitmap(bitmap, src, dst, new Paint());
		} else {
			float left = (256 - width) / 2.0F;
			float top = (256 - height) / 2.0F;
			cv.drawBitmap(bitmap, left, top, new Paint());
		}

		return bm;
	}

	private int imageToTexture(Bitmap bitmap, GL10 gl) {
		int[] texture = new int[1];
		gl.glGenTextures(1, texture, 0);
		gl.glBindTexture(3553, texture[0]);

		Bitmap bm = createTextureBitmap(bitmap);
		bitmap.recycle();
		GLUtils.texImage2D(3553, 0, bm, 0);
		bm.recycle();

		gl.glTexParameterf(3553, 10241, 9728.0F);
		gl.glTexParameterf(3553, 10240, 9729.0F);
		gl.glTexParameterf(3553, 10242, 33071.0F);
		gl.glTexParameterf(3553, 10243, 33071.0F);

		gl.glTexEnvf(8960, 8704, 8448.0F);

		return texture[0];
	}

	public void preLoadCache(final int startIndex, final int endIndex) {
		this.mStopBackgroundThread = false;
		if (this.mGLContext != null)
			new Thread(new Runnable() {
				public void run() {
					int start = startIndex;
					if (start < 0) {
						start = 0;
					}
					int max = CoverFlowOpenGL.this.mListener
							.getCount(CoverFlowOpenGL.this);
					int end = endIndex > max ? max : endIndex;

					for (int i = start; (i < end)
							&& (!CoverFlowOpenGL.this.mStopBackgroundThread); i++)
						CoverFlowOpenGL.this.getTileAtIndex(i,
								CoverFlowOpenGL.this.mGLContext);
				}
			}).run();
	}

	private static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	public static abstract interface CoverFlowListener {
		public abstract int getCount(CoverFlowOpenGL paramCoverFlowOpenGL);

		public abstract Bitmap getImage(CoverFlowOpenGL paramCoverFlowOpenGL,
				int paramInt);

		public abstract void tileOnTop(CoverFlowOpenGL paramCoverFlowOpenGL,
				int paramInt);

		public abstract void topTileClicked(
				CoverFlowOpenGL paramCoverFlowOpenGL, int paramInt);
	}

	public static class CoverFlowRecord {
		private int mTexture;
		private GL10 gl;

		public CoverFlowRecord(int texture, GL10 gl) {
			this.mTexture = texture;
			this.gl = gl;
		}

		protected void finalize() throws Throwable {
			if (this.mTexture != 0) {
				this.gl.glDeleteTextures(1, new int[] { this.mTexture }, 0);
			}

			super.finalize();
		}
	}

	class FocusView extends SurfaceView implements SurfaceHolder.Callback {
		SurfaceHolder holder;

		public FocusView(Context context) {
			super(context);
			this.holder = getHolder();
			this.holder.addCallback(this);
			this.holder.setFormat(-3);
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		public void surfaceCreated(SurfaceHolder holder) {
			new Thread(new MyThread()).start();
		}

		protected void onAttachedToWindow() {
			super.onAttachedToWindow();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
		}

		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			super.onLayout(changed, left, top, right, bottom);
		}

		class MyThread implements Runnable {
			MyThread() {
			}

			public void run() {
				synchronized (CoverFlowOpenGL.this.mutex) {
					while (!CoverFlowOpenGL.this.readyToShowFocus) {
						try {
							CoverFlowOpenGL.this.mutex.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}

					Canvas canvas = CoverFlowOpenGL.FocusView.this.holder
							.lockCanvas(null);

					Drawable border = CoverFlowOpenGL.FocusView.this
							.getContext().getResources()
							.getDrawable(2114191425);

					border.setBounds(0, 0,
							CoverFlowOpenGL.this.mCenterBound.width(),
							CoverFlowOpenGL.this.mCenterBound.height());
					border.draw(canvas);

					CoverFlowOpenGL.FocusView.this.holder
							.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
