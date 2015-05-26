package com.lee.gallery3d.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

public class GalleryFlow extends Gallery {
    public static final int MAX_ROTATION_ANGLE = 60;
    public static final int MIN_ROTATION_ANGLE = -60;
    public static final int MAX_ZOOM = -120;
    public static final int MIN_ZOOM = 120;
    //The camera class is used to 3D transformation matrix.
    private Camera mCamera = new Camera();
    //The max rotation angle.
    private int rotationAngle = 60;
    //The max zoom value (Z axis).
    private int zoom = -90;
    //The center of the gallery.
    private int coveflowCenter = 0;

    public GalleryFlow(Context context) {
        this(context, null);
    }

    public GalleryFlow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryFlow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Enable set transformation.
        this.setStaticTransformationsEnabled(true);
        // Enable set the children drawing order.
        this.setChildrenDrawingOrderEnabled(true);
    }

    public int getMaxRotationAngle() {
        return rotationAngle;
    }

    public void setMaxRotationAngle(int maxRotationAngle) {
        rotationAngle = maxRotationAngle;
    }

    public int getMaxZoom() {
        return zoom;
    }

    public void setMaxZoom(int maxZoom) {
        zoom = maxZoom;
    }

    /**
     * 参考文章http://blog.csdn.net/snowcoldy/article/details/8140167
     * @param childCount
     * @param i
     * @return
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        /**
         * getFirstVisiblePosition()第一个可见item的postion
         * getSelectedItemPosition()当前选中的item的postion
         */
        // 当前选中的item在所有可见视图中的索引(第一个可见视图的索引为0)
        int selectedIndex = getSelectedItemPosition() - getFirstVisiblePosition();

        if (selectedIndex < 0) {
            return i;
        }

        if (i < selectedIndex) {
            //在选中item左边的item还是按照自然顺序显示
            return i;
        } else if (i >= selectedIndex) {
            //在选中item右边的item还是按照颠倒的顺序显示
            return childCount - 1 - i + selectedIndex;
        } else {
            return i;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        coveflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private int getCenterOfView(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        super.getChildStaticTransformation(child, t);

        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth();

        int rotationAngle = 0;
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);

        // If the child is in the center, we do not rotate it.
        if (childCenter == coveflowCenter) {
            transformImageBitmap(child, t, 0);
        } else {
            // Calculate the rotation angle.
            rotationAngle = (int) (((float) (coveflowCenter - childCenter) / childWidth) * this.rotationAngle);

            // Make the angle is not bigger than maximum.
            if (Math.abs(rotationAngle) > this.rotationAngle) {
                rotationAngle = (rotationAngle < 0) ? -this.rotationAngle : this.rotationAngle;
            }

            transformImageBitmap(child, t, rotationAngle);
        }

        if (android.os.Build.VERSION.SDK_INT > 15) {
            child.invalidate();
        }

        return true;
    }

    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    /**
     * 参考文章http://www.open-open.com/lib/view/open1350309480336.html
     * @param child
     * @param t
     * @param rotationAngle
     */
    private void transformImageBitmap(View child, Transformation t, int rotationAngle) {
        mCamera.save();

        final Matrix imageMatrix = t.getMatrix();
        final int imageHeight = child.getHeight();
        final int imageWidth = child.getWidth();
        final int rotation = Math.abs(rotationAngle);

        // Zoom on Z axis.
        mCamera.translate(0, 0, zoom);

        if (rotation < this.rotationAngle) {
            float zoomAmount = (float) (zoom + rotation * 1.5f);
            mCamera.translate(0, 0, zoomAmount);
        }

        // Rotate the camera on Y axis.
        mCamera.rotateY(rotationAngle);
        // Get the matrix from the camera, in fact, the matrix is S (scale) transformation.
        mCamera.getMatrix(imageMatrix);

        // The matrix final is T2 * S * T1, first translate the center point to (0, 0), 
        // then scale, and then translate the center point to its original point.
        // T * S * T

        // S * T1
        imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
        // (T2 * S) * T1
        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));

        mCamera.restore();
    }
}
