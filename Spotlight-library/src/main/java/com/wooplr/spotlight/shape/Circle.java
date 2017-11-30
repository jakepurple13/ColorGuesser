package com.wooplr.spotlight.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.wooplr.spotlight.target.Target;

/**
 * Created by jitender on 10/06/16.
 */

public class Circle extends Shape {

    private Target target;
    private int radius;
    private Point circlePoint;
    private int padding = 20;

    public Circle(Target target, int padding) {
        super(target, padding);
        this.target = target;
        this.padding = padding;
        circlePoint = getFocusPoint();
        calculateRadius(padding);
    }

    public void draw(Canvas canvas, Paint eraser, int padding) {
        calculateRadius(padding);
        circlePoint = getFocusPoint();
        //canvas.drawCircle(circlePoint.x, circlePoint.y, radius, eraser);
        //canvas.drawRoundRect(circlePoint.x-radius, circlePoint.y-radius, circlePoint.x+radius, circlePoint.y+radius, radius, radius, eraser);
        //canvas.drawRect(circlePoint.x-radius, circlePoint.y-radius, circlePoint.x+radius, circlePoint.y+radius, eraser);
        //canvas.drawRect(target.getRect(), eraser);
        RectF rectF = new RectF(target.getRect());
        canvas.drawRoundRect(rectF, radius, radius, eraser);
    }

    private Point getFocusPoint() {

        return target.getPoint();
    }

    public void reCalculateAll() {
        calculateRadius(padding);
        circlePoint = getFocusPoint();
    }

    private void calculateRadius(int padding) {
        int side;
        //int minSide = Math.min(target.getRect().width() / 2, target.getRect().height() / 2);
        //int maxSide = Math.max(target.getRect().width() / 2, target.getRect().height() / 2);
        int minSide = Math.min(target.getRect().width() / 2, target.getRect().height() / 2);
        int maxSide = Math.max(target.getRect().width() / 2, target.getRect().height() / 2);
        side = (minSide + maxSide) / 2;
        radius = side + padding;
    }

    public int getRadius() {
        return radius;
    }

    public Point getPoint() {
        return circlePoint;
    }

}
