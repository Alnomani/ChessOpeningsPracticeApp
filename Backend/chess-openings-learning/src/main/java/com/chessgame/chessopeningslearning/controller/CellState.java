package com.chessgame.chessopeningslearning.controller;

class CellState
{
    private int x;
    private int y;
    private String imagePath;
    private String alias;
    private boolean empty;
    CellState(int x, int y, String imagePath, String alias, boolean isEmpty)
    {
        this.x = x;
        this.y = y;
        this.imagePath = imagePath;
        this.alias = alias;
        this.empty = isEmpty;
    }

    public int getX() 
    {
        return x;
    }
    public int getY() 
    {
        return y;
    }
    public String getImagePath() 
    {
        return imagePath;
    }
    public String getAlias() 
    {
        return alias;
    }
    public boolean isEmpty() 
    {
        return empty;
    }

}
