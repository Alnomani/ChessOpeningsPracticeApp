package com.chessgame.chessopeningslearning.controller;
import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;


record Move(Point origin, Point destination, ChessPiece capturedPiece){}
record BoardState(ArrayList<CellState> boardState, List<String> capturedByWhite, List<String> capturedByBlack){}

class ChessBoard
{
    static final int boardSize = 8;
    static final String letters = "abcdefgh";
    static final String startFenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    static final boolean WHITE = false;
    static final boolean BLACK = true;
    private boolean blacksTurn = false;
    private ChessPiece[][] boardState;
    private String historyString = "";
    private String previousMove = "";
    private int moveCount = 0;
    private int currentHistoryIndex = 0;
    
    private Map<Boolean, List<ChessPiece>> pieceLists = new Hashtable<>();
    private Map<Boolean, List<ChessPiece>> capturedLists = new Hashtable<>();
    private Map<Boolean, List<String>> capturedListsImgPaths = new Hashtable<>();
    private Map<Boolean, ChessPiece> kingPositions = new Hashtable<>();
    private Map<String, String> imgPaths = new Hashtable<>();
    private List<Move> history = new ArrayList<Move>();
    

    ChessBoard()
    {
        boardState = new ChessPiece[boardSize][boardSize];
        initializePieceLists();
        stringToBoard(startFenString);
        populateImgPathsMap();
    }

    private static String repeatChar(int count, String with) 
    {
        return new String(new char[count]).replace("\0", with);
    }
    private void initializePieceLists()
    {
        pieceLists.put(BLACK, new ArrayList<ChessPiece>());
        pieceLists.put(WHITE, new ArrayList<ChessPiece>());
        capturedLists.put(BLACK, new ArrayList<ChessPiece>());
        capturedLists.put(WHITE, new ArrayList<ChessPiece>());
        capturedListsImgPaths.put(BLACK, new ArrayList<String>());
        capturedListsImgPaths.put(WHITE, new ArrayList<String>());
    }
    private void populateImgPathsMap()
    {
        String[] pieceChars = {"b","k","q","p","n","r"};
        for (String pieceChar : pieceChars) 
        {
            imgPaths.put(pieceChar, "/assets/Chess_" + pieceChar+ ".png");
            imgPaths.put(pieceChar.toUpperCase(), 
            "/assets/Chess_w" + pieceChar.toUpperCase() +".png");
        }
    }
    private void updateHistoryString(ChessPiece piece, Point destination)
    {
        if(piece == null) return;
        if(blacksTurn)
        {
            moveCount++;
            historyString += previousMove + piece.getAlias() 
                          +  letters.charAt(destination.y) + (destination.x+1) + "\n";
        }
        previousMove = moveCount + ". " +piece.getAlias() 
                    +  letters.charAt(destination.y) + (destination.x+1) + " ";
        System.out.println(historyString);
    }
    private void updateHistory(Point origin, Point destination, ChessPiece capturedPiece)
    {
        if((history.size() - currentHistoryIndex) > 1)
        {
            System.out.println(String.format("Clearing excess from %d to %d", currentHistoryIndex,  history.size()));
            history.subList(currentHistoryIndex+1, history.size()).clear();
        }
        history.add(new Move(origin, destination, capturedPiece));
    }
    private void capturePiece(ChessPiece capturedPiece)
    {
        if(capturedPiece != null)
        {
            capturedLists.get(capturedPiece.isBlack).add(capturedPiece);
            String imgPath = imgPaths.get(capturedPiece.getAlias());
            capturedListsImgPaths.get(capturedPiece.isBlack).add(imgPath);
            pieceLists.get(capturedPiece.isBlack).remove(capturedPiece);
        }
    }
    private void unCapturePiece(ChessPiece capturedPiece)
    {
        if(capturedPiece != null)
        {
            capturedLists.get(capturedPiece.isBlack).remove(capturedPiece);
            int lastIndex = capturedListsImgPaths.get(capturedPiece.isBlack).size()-1;
            capturedListsImgPaths.get(capturedPiece.isBlack).remove(lastIndex);
            pieceLists.get(capturedPiece.isBlack).add(capturedPiece);
        }
    }
    private void stringToBoard(String fenString)
    {
        Boolean isBlack = false;
        fenString = replaceNumbersInFenString(fenString);
        String[] characterArray = fenString.split("/");
        for (int row = 0; row < 8; row++) 
        {
            for (int col = 0; col < 8; col++) 
            {
                char currentCharacter = characterArray[row].charAt(col);
                isBlack = Character.isUpperCase(currentCharacter) ? false : true;

                boardState[row][col] = getPieceClassFromAlias(currentCharacter, isBlack,
                                                                new Point(row, col));
                if(currentCharacter == 'k' || currentCharacter == 'K')
                {
                    kingPositions.put(isBlack, boardState[row][col]);
                }
                if(boardState[row][col] != null)
                {
                    pieceLists.get(isBlack).add(boardState[row][col]);
                }
            }
        }
    }
    private String replaceNumbersInFenString(String fenString)
    {
        char[] characterArray = fenString.toCharArray();
        for (char character : characterArray) 
        {
            if(Character.isDigit(character))
            {
                String emptyCharacters = repeatChar(Character.getNumericValue(character),"e");
                fenString = fenString.replace(String.valueOf(character), emptyCharacters);
            }
        }
        return fenString;
    }
    private boolean invalidMoveRequest(Point origin, Point destination)
    {
        return !inBounds(origin) ||
               !inBounds(destination) ||
               boardState[origin.x][origin.y] == null ||
               boardState[origin.x][origin.y].isBlack != blacksTurn;
    }
    private boolean inBounds(Point pos)
    {
        return pos.x > -1 && pos.x < 8 && pos.y > -1 && pos.y < 8;
    }
    private ChessPiece getPieceClassFromAlias(Character alias, boolean isBlack, Point position)
    {
        switch(Character.toLowerCase(alias))
        {
            case 'p':
                return new Pawn(isBlack, position, this);
            case 'b':
                return new Bishop(isBlack, position, this);
            case 'r':
                return new Rook(isBlack, position, this);
            case 'q':
                return new Queen(isBlack, position, this);
            case 'k':
                return new King(isBlack, position, this);
            case 'n':
                return new Knight(isBlack, position, this);
        }
        return null;
    }
    private ArrayList<CellState> getListBoardState()
    {
        String imagePath = null;
        String alias = null;
        boolean isEmpty = false;
        
        ArrayList<CellState> listBoardState = new ArrayList<CellState>();
        for (int row = 0; row < boardState.length; row++) {
            for (int col = 0; col < boardState.length; col++) 
            {
                if(boardState[row][col] == null)
                {
                    alias = null;
                    isEmpty = true;
                    imagePath = null;
                }else
                {
                    alias = boardState[row][col].getAlias();
                    isEmpty = false;
                    imagePath = this.imgPaths.get(alias);
                }

                listBoardState.add
                (
                    new CellState(
                                  row, col, imagePath, 
                                  alias,
                                  isEmpty
                                )
                );
            }
        }
        return listBoardState;
    }

    public void undoMoves(int offset)
    {
        if(history.size() < 1) return;
        Point origin;
        Point destination;
        int startIndex = currentHistoryIndex;
        currentHistoryIndex += offset;
        if(startIndex < 0) return;
        System.out.println(String.format("From %d to %d", startIndex, currentHistoryIndex));
        for(int i = startIndex; i > currentHistoryIndex; i--)
        {
            System.out.println(history.get(i));
            origin = history.get(i).origin();
            destination = history.get(i).destination();
            boardState[origin.x][origin.y] = boardState[destination.x][destination.y];
            boardState[origin.x][origin.y].boardPos = origin;
            boardState[destination.x][destination.y] = history.get(i).capturedPiece();
            unCapturePiece(history.get(i).capturedPiece());
            blacksTurn = !blacksTurn;
            drawBoardInConsole(null);
        }
    }
    public void resetBoard()
    {
        boardState = new ChessPiece[boardSize][boardSize];
        boolean white = false;
        boolean black = true;
        pieceLists.get(white).clear();
        capturedLists.get(white).clear(); 
        capturedListsImgPaths.get(white).clear(); 

        pieceLists.get(black).clear(); 
        capturedLists.get(black).clear();
        capturedListsImgPaths.get(black).clear();

        historyString = "";
        history.clear();
        moveCount = 0;

        kingPositions.clear();
        blacksTurn = false;
        stringToBoard(startFenString);
    }
    public void removeFromPiecesList(ChessPiece piece)
    {
        if(piece != null)
        {
            pieceLists.get(piece.isBlack).remove(piece);
        }
    }
    public void addToPiecesList(ChessPiece piece)
    {
        if(piece != null)
        {
            pieceLists.get(piece.isBlack).add(piece);
        }

    }

    public boolean handleMoveRequest(Point origin, Point destination)
    {
        if(invalidMoveRequest(origin, destination)) return false;

        System.out.println("Trying to move " + boardState[origin.x][origin.y].getAlias() 
                         + " from " + origin +" to "+ destination);

        ChessPiece capturedPiece = boardState[origin.x][origin.y].moveTo(destination);
        System.out.println(capturedPiece);
        capturePiece(capturedPiece);
        updateHistoryString(boardState[origin.x][origin.y],  destination);
        updateHistory(origin, destination, capturedPiece);
        drawBoardInConsole(null);
        blacksTurn = !blacksTurn;
        currentHistoryIndex = history.size()-1;
        return true;
    }
    public boolean isCheckMated(boolean isBlack)
    {
        List<ChessPiece> pieces = pieceLists.get(isBlack);
        for (ChessPiece chessPiece : pieces) 
        {
            if(!chessPiece.getLegalMoves(false).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
    public List<Move> getHistory()
    {
        return history;
    }
    public Map<Boolean, ChessPiece> getKingPositions()
    {
        return kingPositions;
    }
    public Set<ChessPiece> kingChecked(boolean isBlack)
    {
        Set<ChessPiece> checkingPieces = new HashSet<ChessPiece>();
        List<ChessPiece> opposingPieces = pieceLists.get(!isBlack);
        for (ChessPiece chessPiece : opposingPieces) 
        {
            if(chessPiece.getLegalMoves(false).contains(kingPositions.get(isBlack).boardPos))
            {
                checkingPieces.add(chessPiece);
            }
        }
        return checkingPieces;
    }
    public String drawBoardInConsole(Set<Point> legalMoves)
    {
        String output = "";
        String token = "";
        output += "  _ _ _ _ _ _ _ _";
        System.out.println("  _ _ _ _ _ _ _ _");
        for (int row = 0; row < boardSize; row++) 
        {
            System.out.print(row + "|");
            output += row + "|";
            for (int col = 0; col < boardSize; col++) 
            {
                if(boardState[row][col] != null)
                {
                    // Display alias at location
                    token = boardState[row][col].getAlias();
                    System.out.print(token + '|' );
                    output += token + '|' ;
                }
                else
                {
                    // Display empty square
                    System.out.print("_|");
                    output += "_|";
                }
            }
            System.out.print("\n");
            output += "\n";
        }
        System.out.println("  0 1 2 3 4 5 6 7");
        output += "  0 1 2 3 4 5 6 7";
        return output;
    }
    public Set<Point> getLegalMoves(int x, int y)
    {
        return boardState[x][y].getLegalMoves(true);
    }
    public BoardState getBoardStateInstance()
    {
        return new BoardState(getListBoardState(), 
                              capturedListsImgPaths.get(WHITE),
                              capturedListsImgPaths.get(BLACK));
    }  
    public ChessPiece[][] getBoardState()
    {
        return boardState;
    }
}
