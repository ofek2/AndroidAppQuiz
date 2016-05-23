package com.example.onlinequizchecker;

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    public int width;
    public  int height;
    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
	private Paint mPaint;
	private String picPath;
	private boolean cleaning; 
    public DrawingView(Context c,AttributeSet a) {
        super(c,a);
        context=c;
        cleaning = false;
        initView();
    }
    private void initView()
    {
    	setDrawingCacheEnabled(true);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(2f);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;      
        height = h;
//        if(picPath!=null){
//        	
//        	mBitmap = BitmapFactory.decodeFile(picPath);
//        	mCanvas = new Canvas();
//        	picPath = null;
//        }
//        else
//        {
        if(cleaning)
        {
        	mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    		mCanvas = new Canvas(mBitmap);
    	
        }
    		else
    		{
		        if(picPath!= null)
		        {
			        mBitmap = BitmapFactory.decodeFile(picPath);
			    	mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
			    	mCanvas = new Canvas(mBitmap);
			    	mCanvas.drawBitmap(mBitmap, 0,0, mPaint);
		        }
		        else
		        {
		        	mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		        	mCanvas = new Canvas(mBitmap);
		        }
    		}
//        
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
    public void clearDrawing()
    {

    setDrawingCacheEnabled(false);
    cleaning = true;
    onSizeChanged(width, height, width, height);
    cleaning=false;
    invalidate();
    setDrawingCacheEnabled(true);
    }

    public void saveDrawing(String path)
    {
    	Bitmap whatTheUserDrewBitmap = getDrawingCache();
    	FileOutputStream out = null;
    	try {
    	    out = new FileOutputStream(path);
    	    whatTheUserDrewBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); 
    	} catch (Exception e) {
    	    e.printStackTrace();
    	} finally {
    	    try {
    	        if (out != null) {
    	            out.close();
    	        }
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}
    }
    public void setPicture(String picturePath)
    {
    	picPath = picturePath;
    	onSizeChanged(width, height, width, height);
    	
    }
}
