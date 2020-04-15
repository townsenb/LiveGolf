package com.example.livegolf;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;




public class DrawView extends View {

    private Canvas canvas;
    private Paint paint = new Paint();

    public int angle = 0;
    private int width = 770;
    private int height = 1175;
    private int teeX = 383;
    private int teeY = 1045;
    private int x = teeX;
    private int y = teeY;

    Bitmap background;

    private void init() {
        Bitmap orig = BitmapFactory.decodeResource(getResources(),R.drawable.golfcourse);
        background = Bitmap.createScaledBitmap(
                orig, 770, 1175, false);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.canvas = canvas;
        canvas.drawBitmap(background,0,0,null);
        canvas.drawLine(x,y,width/2+angle,0,paint);
        paint.setAntiAlias(true);

    }

    public void updateAngle(int angleOffset){
        angle += angleOffset;
    }

    public void updatePosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void resetHole(){
        angle = 0;
        x = teeX;
        y = teeY;
    }

    private void drawBorders(Canvas canvas){
        Paint border = new Paint();
        border.setColor(Color.BLACK);
        border.setStrokeWidth(7);
        int x = 3;
        canvas.drawLine(x, x, width-x, x, border);
        canvas.drawLine(x, x, x, height-x, border);
        canvas.drawLine(x, height-x, width-x, height-x, border);
        canvas.drawLine(width-x, height-x, width-x, x, border);
    }

}