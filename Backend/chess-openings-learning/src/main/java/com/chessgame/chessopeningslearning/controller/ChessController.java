package com.chessgame.chessopeningslearning.controller;

import java.util.List;
import java.util.Set;
import java.awt.Point;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class ChessController {
    private ChessBoard mainChessBoard;
    ChessController()
    {
        mainChessBoard = new ChessBoard();
    }

    @CrossOrigin("/http://localhost:4200/")
    @GetMapping("/start")
    public BoardState getBoardState()
    {
        return mainChessBoard.getBoardStateInstance();
    }

    @GetMapping("/getHistory")
    public List<Move> returnHistory()
    {
        return mainChessBoard.getHistory();
    }

    @PostMapping("/getLegalMoves")
    public Set<Point> sendLegalMoves(@RequestBody CellState entity) 
    {
        return mainChessBoard.getLegalMoves(entity.getX(),entity.getY());
    }

    @PostMapping("/move")
    public BoardState sendBoardStateNMove(@RequestBody  Point[] entity) 
    {
        mainChessBoard.handleMoveRequest(entity[0], entity[1]);
        return getBoardState();
    }

    @PostMapping("/reset")
    public BoardState sendBoardStateNreset() 
    {
        mainChessBoard.resetBoard();
        return getBoardState();
    }

    @PostMapping("/undoMove")
    public BoardState handleUndoMove() 
    {
        mainChessBoard.undoMoves(-1);
        return getBoardState();
    }

    
}
