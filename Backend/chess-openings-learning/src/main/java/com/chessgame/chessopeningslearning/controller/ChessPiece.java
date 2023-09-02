package com.chessgame.chessopeningslearning.controller;
import java.awt.Point;
import java.util.Map;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

abstract class ChessPiece
{   
    protected String alias;
    protected boolean isBlack;
    protected Point boardPos;
    protected Set<Point> legalPoints;
    final static Map<String, Point> diagPoints;
    final static Map<String, Point> straightPoints;
    final static Map<String, Point> knightPoints;

    static ChessBoard chessBoardClass;
    static ChessPiece[][] boardState;
    static
    {
        diagPoints = new Hashtable<>();
        straightPoints = new Hashtable<>();
        knightPoints = new Hashtable<>();
        // Diagonal Points
        diagPoints.put("leftUpper", new Point(-1, -1));
        diagPoints.put("rightLower", new Point(1, 1));
        diagPoints.put("rightUpper", new Point(-1, 1));
        diagPoints.put("leftLower", new Point(1, -1));

        // Straight Points
        straightPoints.put("up", new Point(-1, 0));
        straightPoints.put("down", new Point(1, 0));
        straightPoints.put("right", new Point(0, 1));
        straightPoints.put("left", new Point(0, -1));

        // Knight Points
        knightPoints.put("kUpperLeftH", new Point(-2,-1));
        knightPoints.put("kUpperLeftL", new Point(-1, -2));
        knightPoints.put("kLowerLeftL", new Point(2, -1));
        knightPoints.put("kLowerLeftH", new Point(1, -2));

        knightPoints.put("kLowerRightL", new Point(2, 1));
        knightPoints.put("kLowerRightH", new Point(1, 2));
        knightPoints.put("kUpperRightH", new Point(-2, 1));
        knightPoints.put("kUpperRightL", new Point(-1, 2));
    }

    abstract Set<Point> getLegalMoves(boolean blockCheck);
    ChessPiece(boolean isBlack, Point boardPos, ChessBoard chessBoardClass)
    {
        ChessPiece.chessBoardClass = chessBoardClass;
        ChessPiece.boardState = chessBoardClass.getBoardState();
        this.boardPos = boardPos;
        this.isBlack = isBlack;
        legalPoints = new HashSet<Point>();
    }
    
    public ChessPiece moveTo(Point destination)
    {
        Set<Point> legalMoves = getLegalMoves(true);
        if(legalMoves.contains(destination))
        {
            return changePosition(destination);
        }
        return null;
    }
    protected ChessPiece changePosition(Point destination)
    {
        ChessPiece currentPiece = this;
        Point origin = currentPiece.boardPos;
        currentPiece.boardPos = destination;
        ChessPiece pieceAtDestination = boardState[destination.x][destination.y];
        boardState[destination.x][destination.y] = currentPiece;
        boardState[origin.x][origin.y] = null;
        return pieceAtDestination;
    }

    protected void addOneOffPositions(Map<String, Point> positions)
    {
        // Points movement has a reach of 1 around the piece.
        for (Map.Entry<String, Point> keyValuePair : positions.entrySet()) 
        {
            Point nextPosition = addPoints(boardPos, keyValuePair.getValue());
            // System.out.println("nextPosition");
            // System.out.println(nextPosition);
            // System.out.println(boardPos);
            if(inBounds(nextPosition) &&
               (holdsOpponentsPiece(nextPosition) || positionIsEmpty(nextPosition)))
            {
                this.legalPoints.add(nextPosition);
            }
        }
    }

    protected boolean positionIsEmpty(Point position)
    {
        return boardState[position.x][position.y] == null;
    }
    protected void addPointsAlongDirection(Point direction)
    {
        // follow direction until out of bounds or another piece is reached
        Point nextPosition = addPoints(boardPos, direction);
        while(inBounds(nextPosition))
        {   
            if(holdsAllysPiece(nextPosition)) return;
            if(holdsOpponentsPiece(nextPosition))
            {
                legalPoints.add(nextPosition);
                return;
            }
            // space is empty and can be moved to.
            legalPoints.add(nextPosition);
            // continue along direction
            nextPosition = addPoints(nextPosition, direction);
        } 
    }
    
    protected boolean holdsOpponentsPiece(Point position)
    {
         ChessPiece pieceAtPosition = boardState[position.x][position.y];
         return pieceAtPosition != null && pieceAtPosition.isBlack != this.isBlack;
    }
    protected boolean holdsAllysPiece(Point position)
    {
         ChessPiece pieceAtPosition = boardState[position.x][position.y];
         return pieceAtPosition != null && pieceAtPosition.isBlack == this.isBlack;
    }

    protected void removeMovesLeadingToCheck()
    {
        Set<Point> copy = new HashSet<>(legalPoints);
        Point origin = boardPos;
        int prevX = boardPos.x;
        int prevY = boardPos.y;
        ChessPiece oldPiece = null;
        ChessPiece prevOldPiece = null;
        int numLegalPoints = legalPoints.size();
        assert(boardState[prevX][prevY] != null);
        for(Point legalPosition : copy)
        {
            // place piece at legalpoint then remove if own king is checked as a consequence.
            // this prevents moving a pinned piece or moving a piece into check.
            oldPiece = boardState[legalPosition.x][legalPosition.y];
            
            boardState[prevX][prevY].boardPos = legalPosition;                    // update boardposition
            boardState[legalPosition.x][legalPosition.y] = boardState[prevX][prevY]; // place piece at position
            boardState[prevX][prevY] = prevOldPiece;                           // restore piece at old position

            chessBoardClass.removeFromPiecesList(oldPiece);
            chessBoardClass.addToPiecesList(prevOldPiece);
            //chessBoardClass.drawBoardInConsole(null);

            if(chessBoardClass.kingChecked(isBlack).size() > 0)
            {
                legalPoints.remove(legalPosition);
            }
            prevOldPiece = oldPiece;
            prevX = legalPosition.x;
            prevY = legalPosition.y;
        }
        if(numLegalPoints > 0)
        {

            boardState[origin.x][origin.y] = boardState[prevX][prevY];
            boardState[origin.x][origin.y].boardPos = origin;
            boardState[prevX][prevY] = prevOldPiece;
            chessBoardClass.addToPiecesList(prevOldPiece);
        }

    }

    protected void diagonalMovement()
    {
        addPointsAlongDirection(diagPoints.get("leftUpper"));
        addPointsAlongDirection(diagPoints.get("leftLower"));

        addPointsAlongDirection(diagPoints.get("rightLower"));
        addPointsAlongDirection(diagPoints.get("rightUpper"));
    }
    protected void straightMovement()
    {

        addPointsAlongDirection(straightPoints.get("up"));
        addPointsAlongDirection(straightPoints.get("down"));

        addPointsAlongDirection(straightPoints.get("right"));
        addPointsAlongDirection(straightPoints.get("left"));
    }
    protected boolean inBounds(Point pos)
    {
        return pos.x > -1 && pos.x < 8 && pos.y > -1 && pos.y < 8;
    }
    public String getAlias()
    {
        return this.alias;
    }
    public Point addPoints(Point a, Point b)
    {
        return new Point(a.x + b.x, a.y + b.y);
    }
    // multiple point b by multiplier before adding to a
    public Point addPoints(Point a, Point b, int multiplier)
    {
        Point c = new Point(b.x*multiplier, b.y*multiplier);
        return addPoints(a, c);
    }
}