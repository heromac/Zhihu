package com.jari.zhihu.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by hero on 2016/2/27 0027.
 */
public class CircleImageView extends ImageView {

    private final Paint mainPaint;
    private final Paint circlePaint;

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = createCircleImage() ;
        canvas.drawBitmap(bitmap, 0, 0, mainPaint);
    }


    private Bitmap createCircleImage(){
        int width = getMeasuredWidth() ;
        Bitmap targetBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888) ;
        BitmapDrawable drawable = (BitmapDrawable)getDrawable() ;

        Canvas canvas = new Canvas(targetBitmap) ;
        canvas.drawCircle(width / 2, width / 2, width / 2, circlePaint);

        circlePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)) ;

        canvas.drawBitmap(drawable.getBitmap(), drawable.getBounds(), new Rect(0,0,getMeasuredWidth(),getMeasuredHeight()), circlePaint);

        return targetBitmap ;
    }
}
