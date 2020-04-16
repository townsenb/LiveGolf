package com.example.livegolf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


public class DrawView extends View {

    private Paint playerPaint = new Paint();

    public int angle = 0;
    private int width = 770;
    private int height = 1175;
    private int teeX = 383;
    private int teeY = 1045;
    private int x = teeX;
    private int y = teeY;

    private ArrayList<Integer> xArray;
    private ArrayList<Integer> yArray;

    Bitmap background;

    private void init() {
        Bitmap orig = BitmapFactory.decodeResource(getResources(),R.drawable.golfcourse);
        background = Bitmap.createScaledBitmap(
                orig, 770, 1175, false);
        playerPaint.setColor(Color.WHITE);
        playerPaint.setStrokeWidth(5);
        playerPaint.setAntiAlias(true);

        xArray = new ArrayList<Integer>();
        yArray = new ArrayList<Integer>();
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
        canvas.drawBitmap(background,0,0,null);
        canvas.drawLine(x,y,x+angle,0, playerPaint);
        canvas.drawCircle(x,y,7, playerPaint);
        playerPaint.setAntiAlias(true);
        drawHistory(canvas);
        drawBorders(canvas);
    }

    public void updateAngle(int angleOffset){
        angle += angleOffset;
    }

    public void updatePosition(int x, int y){
        this.x = x;
        this.y = y;
        xArray.add(x);
        yArray.add(y);
    }

    public void resetHole(){
        angle = 0;
        x = teeX;
        y = teeY;
        xArray.clear();
        yArray.clear();
        xArray.add(x);
        yArray.add(y);
    }

    private void drawHistory(Canvas canvas){
        Paint historyPaint = new Paint();
        historyPaint.setColor(Color.GRAY);
        historyPaint.setStrokeWidth(3);
        historyPaint.setAntiAlias(true);

        if(xArray.size() >=2){
            for(int i=1;i<xArray.size();i++){
                canvas.drawLine(xArray.get(i-1),yArray.get(i-1),xArray.get(i),yArray.get(i),historyPaint);
            }
        }
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