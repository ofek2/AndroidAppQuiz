package com.example.onlinequizchecker;

import java.io.FileOutputStream;
import java.io.IOException;
import android.annotation.SuppressLint;
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

/**
 * The Class DrawingView.
 * This class is used for drawing answers during the quiz.
 */
public class DrawingView extends View {

    /** The width. */
    public int width;
    
    /** The height. */
    public  int height;
    
    /** The bitmap. */
    private Bitmap  mBitmap;
    
    /** The canvas. */
    private Canvas  mCanvas;
    
    /** The path. */
    private Path    mPath;
    
    /** The bitmap paint. */
    private Paint   mBitmapPaint;
    
    /** The context. */
    Context context;
    
    /** The circle paint. */
    private Paint circlePaint;
    
    /** The circle path. */
    private Path circlePath;
	
	/** The paint. */
	private Paint mPaint;
	
	/** The picture path. */
	private String picPath;
	
	/** The cleaning. */
	private boolean cleaning; 
    
    /**
     * Instantiates a new drawing view.
     *
     * @param c the c
     * @param a the a
     */
    public DrawingView(Context c,AttributeSet a) {
        super(c,a);
        context=c;
        cleaning = false;
        initView();
    }
    
    /**
     * Inits the view.
     */
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
    
    /* (non-Javadoc)
     * @see android.view.View#onSizeChanged(int, int, int, int)
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;      
        height = h;
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

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    /** The m y. */
    private float mX, mY;
    
    /** The Constant TOUCH_TOLERANCE. */
    private static final float TOUCH_TOLERANCE = 4;

    /**
     * Touch_start.
     *
     * @param x the x
     * @param y the y
     */
    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /**
     * Touch_move.
     *
     * @param x the x
     * @param y the y
     */
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

    /**
     * Touch_up.
     */
    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    /* (non-Javadoc)
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @SuppressLint("ClickableViewAccessibility")
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
    
    /**
     * Clear drawing.
     */
    public void clearDrawing()
    {

    setDrawingCacheEnabled(false);
    cleaning = true;
    onSizeChanged(width, height, width, height);
    cleaning=false;
    invalidate();
    setDrawingCacheEnabled(true);
    }

    /**
     * Save drawing.
     *
     * @param path the path
     */
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
    
    /**
     * Sets the picture.
     *
     * @param picturePath the new picture
     */
    public void setPicture(String picturePath)
    {
    	picPath = picturePath;
    	onSizeChanged(width, height, width, height);
    	
    }
}
