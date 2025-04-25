package com.example.submarines;

public class Shape extends Location {

    protected int width;
    protected int height;

    public Shape(int x, int y, int width, int height)
    {
        super(x,y);
        this.width = width;
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }


}
