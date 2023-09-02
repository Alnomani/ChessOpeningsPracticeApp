package com.chessgame.chessopeningslearning.controller;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

class Pawn extends ChessPiece
{
    Point cameFrom = null;
    Set<Point> enpassentSquares;
    private int startRow;
    private String movementDirection;
    private String captureDirection;

    Pawn(boolean isBlack, Point boardPos, ChessBoard chessBoardClass)
    {
        super(isBlack, boardPos, chessBoardClass);
        this.alias = (isBlack) ? "p" :  "P";
        this.startRow = (isBlack) ? 1 :  6;
        this.movementDirection = (isBlack) ? "down" :  "up";
        this.captureDirection = (isBlack) ? "Lower" :  "Upper";
    }
    public Point getCameFrom()
    {
        return cameFrom;
    }
    @Override public ChessPiece moveTo(Point destination)
    {
        Set<Point> legalMoves = getLegalMoves(true);
        ChessPiece capturedPiece = null;
        if(legalMoves.contains(destination))
        {
            cameFrom = boardPos;
            if(enpassentSquares.contains(destination))
            {
                Point enpCapturePos = addPoints(destination, straightPoints.get(movementDirection));
                boardState[enpCapturePos.x][enpCapturePos.y] = null;
            }
            capturedPiece = changePosition(destination);
        }
        return capturedPiece;
    }

    public Set<Point> getLegalMoves(boolean blockCheck)
    {
        legalPoints = new HashSet<Point>();
        enpassentSquares = new HashSet<Point>();

        // Add points given corresponding conditions.
        addNormalCapturablePositions();
        addEnpassentablePositions();
        addMovePosition();
        addDoubleMovePosition();

        if(blockCheck) // prevents infinte loops
        {
            removeMovesLeadingToCheck();
        }
        return legalPoints;
    }

    private void addDoubleMovePosition()
    {
        if(boardPos.x == startRow)
        {
            Point movePosition = addPoints(boardPos, straightPoints.get(movementDirection), 2);
            if(boardState[movePosition.x][movePosition.y] == null)
            {
                this.legalPoints.add(movePosition);
            }
        }
    }
    private void addMovePosition()
    {
        Point movePosition = addPoints(boardPos, straightPoints.get(movementDirection));
        if(inBounds(movePosition) && boardState[movePosition.x][movePosition.y] == null)
        {
            this.legalPoints.add(movePosition);
        }
    }
    private void addNormalCapturablePositions()
    {
        Point leftCapturePoint = addPoints(boardPos, diagPoints.get("left" + captureDirection));
        Point rightCapturePoint = addPoints(boardPos, diagPoints.get("right" + captureDirection));
        addPositionIfCapturable(leftCapturePoint);
        addPositionIfCapturable(rightCapturePoint);
    }
    private void addEnpassentablePositions()
    {
        Point leftPoint = addPoints(boardPos, straightPoints.get("left"));
        Point rightPoint = addPoints(boardPos, straightPoints.get("right"));
        addEnpassentPosition(leftPoint);
        addEnpassentPosition(rightPoint);
    } 

    private void addPositionIfCapturable(Point position)
    {
        if(inBounds(position) )
        {
            ChessPiece currentPiece = boardState[position.x][position.y];
            if(isOpponentsPiece(currentPiece))
            {
                this.legalPoints.add(position);
            }
        }
    }

    private boolean isOpponentsPiece(ChessPiece piece)
    {
        if(piece != null && piece.isBlack != this.isBlack)
        {
            return true;
        }
        return false;
    }

    private boolean isOpponentsPawn(ChessPiece piece)
    {
        return isOpponentsPiece(piece) && piece instanceof Pawn;
    }

    private boolean cameFromStartPosition(ChessPiece pawn)
    {
        Point cameFrom = ((Pawn)pawn).getCameFrom();
        if(cameFrom != null)
        {
            int columnDistance = Math.abs(cameFrom.x - pawn.boardPos.x);
            if(columnDistance > 1)
            {
                return true;
            }
        }
        return false;
    }

    private void addEnpassentPosition(Point position)
    {
        if(inBounds(position))
        {
            ChessPiece pieceAtPosition = boardState[position.x][position.y];
            if(isOpponentsPawn(pieceAtPosition) &&
               cameFromStartPosition(pieceAtPosition))
            {
                Point offset = addPoints(position, straightPoints.get(movementDirection));
                legalPoints.add(offset);
                enpassentSquares.add(offset);
            }
        }
    }


}