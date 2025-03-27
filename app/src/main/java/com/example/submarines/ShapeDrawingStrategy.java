package com.example.submarines;

import android.graphics.Canvas;

public interface ShapeDrawingStrategy {

    // אינטרפייס כלומר ממשק של ציור צורות
    void draw(Shape shape, Canvas canvas);
}
