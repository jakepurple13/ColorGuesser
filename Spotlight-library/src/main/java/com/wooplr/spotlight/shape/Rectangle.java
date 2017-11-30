package com.wooplr.spotlight.shape;

/**
 * Created by Jacob on 11/29/17.
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.wooplr.spotlight.target.Target;

public class Rectangle extends Shape {

    private Target target;
    private int radius;
    private Point rectPoint;
    private int padding = 20;


    public Rectangle(Target target, int padding) {
        super(target, padding);
        this.target = target;
        this.padding = padding;
        rectPoint = getFocusPoint();
        calculateRadius(padding);
    }

    public void draw(Canvas canvas, Paint eraser, int padding) {
        calculateRadius(padding);
        rectPoint = getFocusPoint();
        canvas.drawRect(rectPoint.x-radius, rectPoint.y-radius, rectPoint.x+radius, rectPoint.y+radius, eraser);
    }

    private Point getFocusPoint() {

        return target.getPoint();
    }

    public void reCalculateAll() {
        calculateRadius(padding);
        rectPoint = getFocusPoint();
    }

    private void calculateRadius(int padding) {
        int side;
        int minSide = Math.min(target.getRect().width() / 2, target.getRect().height() / 2);
        int maxSide = Math.max(target.getRect().width() / 2, target.getRect().height() / 2);
        side = (minSide + maxSide) / 2;
        radius = side + padding;
    }

    public int getRadius() {
        return radius;
    }

    public Point getPoint() {
        return rectPoint;
    }

}
