package com.lee.gallery3d;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lee.gallery3d.utils.BitmapUtil;
import com.lee.gallery3d.widget.GalleryFlow;

import java.util.ArrayList;

public class GalleryFlowActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GalleryFlow mGallery = null;
    private ArrayList<BitmapDrawable> mBitmaps = new ArrayList<BitmapDrawable>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mGallery = (GalleryFlow) findViewById(R.id.gallery_flow);
        findViewById(R.id.space_confirm_btn).setOnClickListener(this);
        findViewById(R.id.max_zoom_confirm_btn).setOnClickListener(this);
        findViewById(R.id.max_rotate_angle_confirm_btn).setOnClickListener(this);
        mGallery.setOnItemClickListener(this);

        generateBitmaps();

        mGallery.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //设置adapter需要在gallery绘制完成后，不然gallery的高度为0
                mGallery.setAdapter(new GalleryAdapter());
                mGallery.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void onSpaceBtnClick(View v) {
        EditText editText = (EditText) findViewById(R.id.space_edittext);
        String text = editText.getText().toString();

        try {
            int spacing = Integer.parseInt(text);
            if (spacing >= -60 && spacing <= 60) {
                mGallery.setSpacing(spacing);
                ((GalleryAdapter) mGallery.getAdapter()).notifyDataSetChanged();
            } else {
                Toast.makeText(this,
                        getResources().getString(R.string.gallery_space_text_hint),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMaxZoomBtnClick(View v) {
        EditText editText = (EditText) findViewById(R.id.max_zoom_edittext);
        String text = editText.getText().toString();

        try {
            int maxZoom = Integer.parseInt(text);
            if (maxZoom >= GalleryFlow.MAX_ZOOM && maxZoom <= GalleryFlow.MIN_ZOOM) {
                mGallery.setMaxZoom(maxZoom);
                ((GalleryAdapter) mGallery.getAdapter()).notifyDataSetChanged();
            } else {
                Toast.makeText(this,
                        getResources().getString(R.string.gallery_max_zoom_text_hint),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMaxAngleBtnClick(View v) {
        EditText editText = (EditText) findViewById(R.id.max_rotate_angle_edittext);
        String text = editText.getText().toString();

        try {
            int maxRotationAngle = Integer.parseInt(text);
            if (maxRotationAngle >= GalleryFlow.MAX_ROTATION_ANGLE && maxRotationAngle <= GalleryFlow.MIN_ROTATION_ANGLE) {
                mGallery.setMaxRotationAngle(maxRotationAngle);
                ((GalleryAdapter) mGallery.getAdapter()).notifyDataSetChanged();
            } else {
                Toast.makeText(this,
                        getResources().getString(R.string.gallery_max_rotate_angle_text_hint),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateBitmaps() {
        int[] ids =
                {
                        R.drawable.a,
                        R.drawable.b,
                        R.drawable.c,
                        R.drawable.d,
                        R.drawable.e,
                        R.drawable.f,
                        R.drawable.g,
                        R.drawable.h,
                        R.drawable.i,
                        R.drawable.j,
                        R.drawable.k,
                        R.drawable.l,
                };

        for (int id : ids) {
            Bitmap bitmap = createReflectedBitmapById(id);
            if (null != bitmap) {
                BitmapDrawable drawable = new BitmapDrawable(bitmap);
                drawable.setAntiAlias(true);
                mBitmaps.add(drawable);
            }
        }
    }

    private Bitmap createReflectedBitmapById(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap reflectedBitmap = BitmapUtil.createReflectedBitmap(bitmap);

            return reflectedBitmap;
        }

        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, i + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.space_confirm_btn:
                onSpaceBtnClick(v);
                break;
            case R.id.max_zoom_confirm_btn:
                onMaxZoomBtnClick(v);
                break;
            case R.id.max_rotate_angle_confirm_btn:
                onMaxAngleBtnClick(v);
                break;
        }
    }

    private class GalleryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBitmaps.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = new MyImageView(GalleryFlowActivity.this);
                Gallery.LayoutParams lp = new Gallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mGallery.getHeight() * 3 / 5);
                convertView.setLayoutParams(lp);
            }

            ImageView imageView = (ImageView) convertView;
            imageView.setImageDrawable(mBitmaps.get(position));

            return imageView;
        }
    }

    private class MyImageView extends ImageView {
        public MyImageView(Context context) {
            this(context, null);
        }

        public MyImageView(Context context, AttributeSet attrs) {
            super(context, attrs, 0);
            init();
        }

        public MyImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        private void init() {
            float density = getResources().getDisplayMetrics().density;
            setPadding(0, (int) (8 * density), 0, 0);

            setScaleType(ScaleType.FIT_CENTER);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }
    }
}
