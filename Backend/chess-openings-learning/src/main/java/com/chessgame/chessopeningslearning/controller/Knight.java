package com.chessgame.chessopeningslearning.controller;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

class Knight extends ChessPiece
{
    Knight(boolean isBlack, Point boardPos, ChessBoard chessBoardClass)
    {
        super(isBlack, boardPos, chessBoardClass);
        this.alias = (isBlack) ? "n" :  "N";
    }
    // protected has package wide access unfortunately.
    public Set<Point> getLegalMoves(boolean blockCheck)
    {
        // clear before recalculation
        this.legalPoints.clear();
        // check how objects get passed in java
        this.legalPoints = new HashSet<Point>();
        this.addOneOffPositions(knightPoints);
        if( blockCheck)
        {
            removeMovesLeadingToCheck();
        }
        return legalPoints;
    }
}