package io.viva.tv.app.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;

public class MediaCoverFlow extends CoverFlow {
	private static final String TAG = "MediaCoverFlow";
	private static final boolean DEBUG = false;

	public MediaCoverFlow(Context context) {
		super(context);
	}

	public MediaCoverFlow(Context context, AttributeSet attrs) {
		super(context, attrs, 16842864);
	}

	public MediaCoverFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected boolean getChildStaticTransformation(View child, Transformation t) {
		transformChidMatrix(child, t);
		return true;
	}

	private void transformChidMatrix(View child, Transformation t) {
		int childCenter = getCenterOfView(child);
		int childWidth = child.getWidth();
		int rotationAngle = 0;
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);
		if (Math.abs(childCenter - this.mCoveflowCenter) <= childWidth + this.mSpacing) {
			transformImageBitmap(child, t, 0);
		} else {
			if (this.mCoveflowCenter - childCenter > 0) {
				rotationAngle = (int) ((this.mCoveflowCenter - childCenter - (childWidth + this.mSpacing)) / childWidth * this.mMaxRotationAngle / 2.0F);
			} else {
				rotationAngle = (int) ((this.mCoveflowCenter - childCenter + (childWidth + this.mSpacing)) / childWidth * this.mMaxRotationAngle / 2.0F);
			}

			if (Math.abs(rotationAngle) > this.mMaxRotationAngle) {
				rotationAngle = rotationAngle < 0 ? -this.mMaxRotationAngle : this.mMaxRotationAngle;
			}

			transformImageBitmap(child, t, rotationAngle);

			int childDistance = Math.abs(childCenter - this.mCoveflowCenter);

			float distanceRotate = 2 * (childWidth + this.mSpacing);
			float distanceShift = 3 * (childWidth + this.mSpacing);

			float shiftArea = childWidth / 1.6F;

			if (this.mCoveflowCenter - childCenter > 0) {
				if ((childDistance >= distanceRotate) && (childDistance < distanceShift)) {
					Matrix matrix = t.getMatrix();

					float radio = 1.0F * (childDistance - distanceRotate) / (childWidth + this.mSpacing);

					matrix.postTranslate(shiftArea * radio, 0.0F);
				} else if (childDistance >= distanceShift) {
					Matrix matrix = t.getMatrix();
					matrix.postTranslate(shiftArea, 0.0F);
				}
			} else if ((childDistance >= distanceRotate) && (childDistance <= distanceShift)) {
				Matrix matrix = t.getMatrix();
				float radio = 1.0F * (childDistance - distanceRotate) / (childWidth + this.mSpacing);

				matrix.postTranslate(-shiftArea * radio, 0.0F);
			} else if (childDistance >= distanceShift) {
				Matrix matrix = t.getMatrix();
				matrix.postTranslate(-shiftArea, 0.0F);
			}
		}
	}
}