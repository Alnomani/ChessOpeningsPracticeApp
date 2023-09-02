package com.chessgame.chessopeningslearning.controller;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;


class Rook extends ChessPiece
{
    Rook(boolean isBlack, Point boardPos, ChessBoard chessBoardClass)
    {
        super(isBlack, boardPos, chessBoardClass);
        this.alias = (isBlack) ? "r" :  "R";
    }
    // protected has package wide access unfortunately.
    public Set<Point> getLegalMoves(boolean blockCheck)
    {
        // clear before recalculation
        this.legalPoints.clear();
        
        this.legalPoints = new HashSet<Point>();
        this.straightMovement();
        if( blockCheck)
        {
            removeMovesLeadingToCheck();
        }
        return legalPoints;
    }
}