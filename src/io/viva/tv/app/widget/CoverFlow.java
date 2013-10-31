package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;

public class CoverFlow extends AbsCoverFlow {
	private static final String TAG = "CoverFlow";
	private static final boolean DEBUG = false;
	private static final int DEFAULT_MID_ITEM_COUNT = 3;
	private int mMidItemCount = DEFAULT_MID_ITEM_COUNT;

	public CoverFlow(Context paramContext) {
		super(paramContext);
	}

	public CoverFlow(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet, 16842864);
	}

	public CoverFlow(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected boolean getChildStaticTransformation(View paramView, Transformation paramTransformation) {
		transformChidMatrix(paramView, paramTransformation);
		return true;
	}

	public void setMidItemCount(int paramInt) {
		if (paramInt <= 0)
			paramInt = 1;
		if ((paramInt & 0x1) != 1)
			paramInt++;
		this.mMidItemCount = paramInt;
	}

	public int getMidItemCount() {
		return this.mMidItemCount;
	}

	private void transformChidMatrixMidItemCount1(View paramView, Transformation paramTransformation) {
		int i = getCenterOfView(paramView);
		int j = paramView.getWidth();
		int k = 0;
		paramTransformation.clear();
		paramTransformation.setTransformationType(Transformation.TYPE_MATRIX);
		if (i == this.mCoveflowCenter) {
			transformImageBitmap(paramView, paramTransformation, 0);
		} else {
			if (this.mCoveflowCenter - i > 0)
				k = (int) ((this.mCoveflowCenter - i) / j * this.mMaxRotationAngle / 2.0F);
			else
				k = (int) ((this.mCoveflowCenter - i) / j * this.mMaxRotationAngle / 2.0F);
			if (Math.abs(k) > this.mMaxRotationAngle)
				k = k < 0 ? -this.mMaxRotationAngle : this.mMaxRotationAngle;
			transformImageBitmap(paramView, paramTransformation, k);
			int m = Math.abs(i - this.mCoveflowCenter);
			float f1 = 1 * (j + this.mSpacing);
			float f2 = 2 * (j + this.mSpacing);
			float f3 = j / 1.6F;
			Matrix localMatrix;
			float f4;
			if (this.mCoveflowCenter - i > 0) {
				if ((m >= f1) && (m < f2)) {
					localMatrix = paramTransformation.getMatrix();
					f4 = 1.0F * (m - f1) / (j + this.mSpacing);
					localMatrix.postTranslate(f3 * f4, 0.0F);
				} else if (m >= f2) {
					localMatrix = paramTransformation.getMatrix();
					f4 = 1.0F * (m - f1) / (j + this.mSpacing);
					localMatrix.postTranslate(f3 * f4, 0.0F);
				}
			} else if ((m >= f1) && (m <= f2)) {
				localMatrix = paramTransformation.getMatrix();
				f4 = 1.0F * (m - f1) / (j + this.mSpacing);
				localMatrix.postTranslate(-f3 * f4, 0.0F);
			} else if (m >= f2) {
				localMatrix = paramTransformation.getMatrix();
				f4 = 1.0F * (m - f1) / (j + this.mSpacing);
				localMatrix.postTranslate(-f3 * f4, 0.0F);
			}
		}
	}

	private void transformChidMatrixMidItemCount3(View paramView, Transformation paramTransformation) {
		int i = getCenterOfView(paramView);
		int j = paramView.getWidth();
		int k = 0;
		paramTransformation.clear();
		paramTransformation.setTransformationType(Transformation.TYPE_MATRIX);
		int m = j + this.mSpacing;
		if (Math.abs(i - this.mCoveflowCenter) <= m) {
			transformImageBitmap(paramView, paramTransformation, 0);
		} else {
			if (this.mCoveflowCenter - i > 0)
				k = (int) ((this.mCoveflowCenter - i - m) / j * this.mMaxRotationAngle / 2.0F);
			else
				k = (int) ((this.mCoveflowCenter - i + m) / j * this.mMaxRotationAngle / 2.0F);
			if (Math.abs(k) > this.mMaxRotationAngle)
				k = k < 0 ? -this.mMaxRotationAngle : this.mMaxRotationAngle;
			transformImageBitmap(paramView, paramTransformation, k);
			int n = Math.abs(i - this.mCoveflowCenter);
			float f1 = 2 * (j + this.mSpacing);
			float f2 = 3 * (j + this.mSpacing);
			float f3 = j / 1.6F;
			Matrix localMatrix;
			float f4;
			if (this.mCoveflowCenter - i > 0) {
				if ((n >= f1) && (n < f2)) {
					localMatrix = paramTransformation.getMatrix();
					f4 = 1.0F * (n - f1) / (j + this.mSpacing);
					localMatrix.postTranslate(f3 * f4, 0.0F);
				} else if (n >= f2) {
					localMatrix = paramTransformation.getMatrix();
					localMatrix.postTranslate(f3, 0.0F);
				}
			} else if ((n >= f1) && (n <= f2)) {
				localMatrix = paramTransformation.getMatrix();
				f4 = 1.0F * (n - f1) / (j + this.mSpacing);
				localMatrix.postTranslate(-f3 * f4, 0.0F);
			} else if (n >= f2) {
				localMatrix = paramTransformation.getMatrix();
				localMatrix.postTranslate(-f3, 0.0F);
			}
		}
	}

	private void transformChidMatrixMidItemCount5(View paramView, Transformation paramTransformation) {
		int i = getCenterOfView(paramView);
		int j = paramView.getWidth();
		int k = 0;
		paramTransformation.clear();
		paramTransformation.setTransformationType(Transformation.TYPE_MATRIX);
		int m = 2 * (j + this.mSpacing);
		if (Math.abs(i - this.mCoveflowCenter) <= m) {
			transformImageBitmap(paramView, paramTransformation, 0);
		} else {
			if (this.mCoveflowCenter - i > 0)
				k = (int) ((this.mCoveflowCenter - i - m) / j * this.mMaxRotationAngle / 2.0F);
			else
				k = (int) ((this.mCoveflowCenter - i + m) / j * this.mMaxRotationAngle / 2.0F);
			if (Math.abs(k) > this.mMaxRotationAngle)
				k = k < 0 ? -this.mMaxRotationAngle : this.mMaxRotationAngle;
			transformImageBitmap(paramView, paramTransformation, k);
			int n = Math.abs(i - this.mCoveflowCenter);
			float f1 = 3 * (j + this.mSpacing);
			float f2 = 4 * (j + this.mSpacing);
			float f3 = j / 1.6F;
			Matrix localMatrix;
			float f4;
			if (this.mCoveflowCenter - i > 0) {
				if ((n >= f1) && (n < f2)) {
					localMatrix = paramTransformation.getMatrix();
					f4 = 1.0F * (n - f1) / (j + this.mSpacing);
					localMatrix.postTranslate(f3 * f4, 0.0F);
				} else if (n >= f2) {
					localMatrix = paramTransformation.getMatrix();
					localMatrix.postTranslate(f3, 0.0F);
				}
			} else if ((n >= f1) && (n <= f2)) {
				localMatrix = paramTransformation.getMatrix();
				f4 = 1.0F * (n - f1) / (j + this.mSpacing);
				localMatrix.postTranslate(-f3 * f4, 0.0F);
			} else if (n >= f2) {
				localMatrix = paramTransformation.getMatrix();
				localMatrix.postTranslate(-f3, 0.0F);
			}
		}
	}

	private void transformChidMatrix(View paramView, Transformation paramTransformation) {
		if (this.mMidItemCount == 5) {
			transformChidMatrixMidItemCount5(paramView, paramTransformation);
		} else if (this.mMidItemCount == 3) {
			transformChidMatrixMidItemCount3(paramView, paramTransformation);
		} else {
			transformChidMatrixMidItemCount1(paramView, paramTransformation);
		}
	}
}