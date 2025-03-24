package com.example.submarines.helpers;

import android.graphics.Canvas;

import com.example.submarines.model.Shape;

public interface ShapeDrawingStrategy {

    // אינטרפייס כלומר ממשק של ציור צורות
    void draw(Shape shape, Canvas canvas);
}
