package com.chessgame.chessopeningslearning.controller;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

class King extends ChessPiece
{
    private boolean hasMoved = false;
    King(boolean isBlack, Point boardPos, ChessBoard chessBoardClass)
    {
        super(isBlack, boardPos, chessBoardClass);
        this.alias = (isBlack) ? "k" :  "K";
    }
    @Override public ChessPiece moveTo(Point destination)
    {
        Set<Point> legalMoves = getLegalMoves(true);
        ChessPiece pieceAtDestination = null;
        if(legalMoves.contains(destination))
        {
            Point origin = boardPos;
            if(!hasMoved)
            {
                tryToMoveRookToRightCastlePos(origin, destination);
                tryToMoveRookToLeftCastlePos(origin, destination);
            }
            pieceAtDestination = changePosition(destination);
            hasMoved = true;
        }
        return pieceAtDestination;
    }
    private void tryToMoveRookToRightCastlePos(Point origin, Point destination)
    {
        Point rPos = addPoints(origin, straightPoints.get("right"));
        Point rightCastlePos = addPoints(origin, straightPoints.get("right"), 2);
        Point rRookPos = addPoints(origin, straightPoints.get("right"), 3);
        if(destination.equals(rightCastlePos))
        {
            // move rook the correct position when castling short
            boardState[rPos.x][rPos.y] = boardState[rRookPos.x][rRookPos.y];
            boardState[rRookPos.x][rRookPos.y] = null;
        }
    }
    private void tryToMoveRookToLeftCastlePos(Point origin, Point destination)
    {
        Point lPos = addPoints(origin, straightPoints.get("left"));
        Point leftCastlePos = addPoints(origin, straightPoints.get("left"), 2);
        Point lRookPos = addPoints(origin, straightPoints.get("left"), 4);
        if(destination.equals(leftCastlePos))
        {
            // move rook the correct position when castling long
            boardState[lPos.x][lPos.y] = boardState[lRookPos.x][lRookPos.y];
            boardState[lRookPos.x][lRookPos.y] = null;
        }
    }
    // protected has package wide access unfortunately.
    public Set<Point> getLegalMoves(boolean blockCheck)
    {
        // clear before recalculation
        this.legalPoints.clear();
        this.legalPoints = new HashSet<Point>();
        this.addOneOffPositions(straightPoints);
        this.addOneOffPositions(diagPoints);
        
        Point leftPos = addPoints(boardPos, straightPoints.get("left"));
        Point leftCPos = addPoints(leftPos, straightPoints.get("left"));
        Point leftC2Pos = addPoints(leftCPos, straightPoints.get("left"));
        Point rookPos = addPoints(leftC2Pos, straightPoints.get("left"));
        if(!hasMoved)
        {
            // add legal move for castling.
            if(boardState[leftPos.x][leftPos.y] == null &&
               boardState[leftCPos.x][leftCPos.y] == null &&
               boardState[leftC2Pos.x][leftC2Pos.y] == null &&
               boardState[rookPos.x][rookPos.y] instanceof Rook) 
            {
                System.out.println("adding left castling points");
                legalPoints.add(leftCPos);
                legalPoints.add(leftC2Pos);
            }

            Point rightPos = addPoints(boardPos, straightPoints.get("right"));
            Point rightCPos = addPoints(rightPos, straightPoints.get("right"));
            rookPos = addPoints(rightCPos, straightPoints.get("right"));

            if(boardState[rightPos.x][rightPos.y] == null &&
               boardState[rightCPos.x][rightCPos.y] == null &&
               boardState[rookPos.x][rookPos.y] instanceof Rook) 
            {
                System.out.println("adding right castling point");
                legalPoints.add(rightCPos);
            }

        }

        if( blockCheck)
        {
            removeMovesLeadingToCheck();
        }
        // if castling positions have not been removed, it means you don't have to castle through check.
        // ToDO: fix problems with castling. Can still castle through check long, undo doesn't return the rook
        // and undo doesn't reset hasMoved parameter.

        if(legalPoints.contains(leftCPos) && legalPoints.contains(leftC2Pos))
        {
            legalPoints.remove(leftC2Pos); // just one extra legal square suffices for castling
        }

        return legalPoints;
    }
}
