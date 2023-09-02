package com.chessgame.chessopeningslearning.controller;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

class Bishop extends ChessPiece
{
    Bishop(boolean isBlack, Point boardPos, ChessBoard chessBoardClass)
    {
        super(isBlack, boardPos, chessBoardClass);
        this.alias = (isBlack) ? "b" :  "B";   
    }
    public Set<Point> getLegalMoves(boolean blockCheck)
    {
        // clear before recalculation
        this.legalPoints.clear();
        this.legalPoints = new HashSet<Point>();
        this.diagonalMovement();
        if( blockCheck)
        {
            removeMovesLeadingToCheck();
        }
        //filter out moves that lead to check
        
        return legalPoints;
    }
}

