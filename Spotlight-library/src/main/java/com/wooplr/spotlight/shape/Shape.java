package com.wooplr.spotlight.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.wooplr.spotlight.target.Target;

/**
 * Created by Jacob on 11/29/17.
 */

public class Shape {

    private Target target;
    private int radius;
    private Point point;
    private int padding = 20;

    public Shape(Target target, int padding) {
        this.target = target;
        this.padding = padding;
        point = getFocusPoint();
        calculateRadius(padding);
    }

    public void draw(Canvas canvas, Paint eraser, int padding) {

    }

    private Point getFocusPoint() {
        return target.getPoint();
    }

    public void reCalculateAll() {

    }

    private void calculateRadius(int padding) {

    }

    public int getRadius() {
        return radius;
    }

    public Point getPoint() {
        return point;
    }
}
