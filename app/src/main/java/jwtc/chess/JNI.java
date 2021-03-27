package jwtc.chess;

import jwtc.chess.board.BoardConstants;

public class JNI {

	public JNI(){
		
	}
	
	public void newGame(){
		reset();
		putPiece(BoardConstants.a8, BoardConstants.ROOK, BoardConstants.BLACK);
		putPiece(BoardConstants.b8, BoardConstants.KNIGHT, BoardConstants.BLACK);
		putPiece(BoardConstants.c8, BoardConstants.BISHOP, BoardConstants.BLACK);
		putPiece(BoardConstants.d8, BoardConstants.QUEEN, BoardConstants.BLACK);
		putPiece(BoardConstants.e8, BoardConstants.KING, BoardConstants.BLACK);
		putPiece(BoardConstants.f8, BoardConstants.BISHOP, BoardConstants.BLACK);
		putPiece(BoardConstants.g8, BoardConstants.KNIGHT, BoardConstants.BLACK);
		putPiece(BoardConstants.h8, BoardConstants.ROOK, BoardConstants.BLACK);
		putPiece(BoardConstants.a7, BoardConstants.PAWN, BoardConstants.BLACK);
		putPiece(BoardConstants.b7, BoardConstants.PAWN, BoardConstants.BLACK);
		putPiece(BoardConstants.c7, BoardConstants.PAWN, BoardConstants.BLACK);
		putPiece(BoardConstants.d7, BoardConstants.PAWN, BoardConstants.BLACK);
		putPiece(BoardConstants.e7, BoardConstants.PAWN, BoardConstants.BLACK);
		putPiece(BoardConstants.f7, BoardConstants.PAWN, BoardConstants.BLACK);
		putPiece(BoardConstants.g7, BoardConstants.PAWN, BoardConstants.BLACK);
		putPiece(BoardConstants.h7, BoardConstants.PAWN, BoardConstants.BLACK);
		
		putPiece(BoardConstants.a1, BoardConstants.ROOK, BoardConstants.WHITE);
		putPiece(BoardConstants.b1, BoardConstants.KNIGHT, BoardConstants.WHITE);
		putPiece(BoardConstants.c1, BoardConstants.BISHOP, BoardConstants.WHITE);
		putPiece(BoardConstants.d1, BoardConstants.QUEEN, BoardConstants.WHITE);
		putPiece(BoardConstants.e1, BoardConstants.KING, BoardConstants.WHITE);
		putPiece(BoardConstants.f1, BoardConstants.BISHOP, BoardConstants.WHITE);
		putPiece(BoardConstants.g1, BoardConstants.KNIGHT, BoardConstants.WHITE);
		putPiece(BoardConstants.h1, BoardConstants.ROOK, BoardConstants.WHITE);
		putPiece(BoardConstants.a2, BoardConstants.PAWN, BoardConstants.WHITE);
		putPiece(BoardConstants.b2, BoardConstants.PAWN, BoardConstants.WHITE);
		putPiece(BoardConstants.c2, BoardConstants.PAWN, BoardConstants.WHITE);
		putPiece(BoardConstants.d2, BoardConstants.PAWN, BoardConstants.WHITE);
		putPiece(BoardConstants.e2, BoardConstants.PAWN, BoardConstants.WHITE);
		putPiece(BoardConstants.f2, BoardConstants.PAWN, BoardConstants.WHITE);
		putPiece(BoardConstants.g2, BoardConstants.PAWN, BoardConstants.WHITE);
		putPiece(BoardConstants.h2, BoardConstants.PAWN, BoardConstants.WHITE);
		
		setCastlingsEPAnd50(1, 1, 1, 1, -1, 0);
		
		commitBoard();
	}
	
	

	public final boolean initFEN(final String sFEN){
		// rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1
		
		reset();
		try { 
				
			String s;
			int pos = 0, i = 0, iAdd;
			while(pos < 64 && i < sFEN.length()){
				iAdd = 1;
				s = sFEN.substring(i, i+1);
				if(s.equals("k")){
					putPiece(pos, BoardConstants.KING, BoardConstants.BLACK);
				} else if(s.equals("K")){
					putPiece(pos, BoardConstants.KING, BoardConstants.WHITE);
				} else if(s.equals("q")){
					putPiece(pos, BoardConstants.QUEEN, BoardConstants.BLACK);
				} else if(s.equals("Q")){
					putPiece(pos, BoardConstants.QUEEN, BoardConstants.WHITE);
				} else if(s.equals("r")){
					putPiece(pos, BoardConstants.ROOK, BoardConstants.BLACK);
				} else if(s.equals("R")){
					putPiece(pos, BoardConstants.ROOK, BoardConstants.WHITE);
				} else if(s.equals("b")){
					putPiece(pos, BoardConstants.BISHOP, BoardConstants.BLACK);
				} else if(s.equals("B")){
					putPiece(pos, BoardConstants.BISHOP, BoardConstants.WHITE);
				} else if(s.equals("n")){
					putPiece(pos, BoardConstants.KNIGHT, BoardConstants.BLACK);
				} else if(s.equals("N")){
					putPiece(pos, BoardConstants.KNIGHT, BoardConstants.WHITE);
				} else if(s.equals("p")){
					putPiece(pos, BoardConstants.PAWN, BoardConstants.BLACK);
				} else if(s.equals("P")){
					putPiece(pos, BoardConstants.PAWN, BoardConstants.WHITE);
				} else if(s.equals("/")){
					iAdd = 0;
				} else {
					iAdd = Integer.parseInt(s);
				}
				pos += iAdd;
				i++;
			}
			i++;// skip space
			if(i < sFEN.length()){
				int wccl = 0, wccs = 0, bccl = 0, bccs = 0, colA = 0, colH = 7, ep = -1, r50 = 0, turn;
				String[] arr = sFEN.substring(i).split(" ");
				if(arr.length > 0){
					if(arr[0].equals("w")){
						turn = BoardConstants.WHITE;
					} else {
						turn = BoardConstants.BLACK;
					}
					if(arr.length > 1){
						if(arr[1].indexOf("k") != -1){
							bccs = 1;
						}
						if(arr[1].indexOf("q") != -1){
							bccl = 1;
						} 
						if(arr[1].indexOf("K") != -1){
							wccs = 1;
						}
						if(arr[1].indexOf("Q") != -1){
							wccl = 1;
						}
						
						if(arr.length > 2){
							if(false == arr[2].equals("-")){
								ep = Pos.fromString(arr[2]);
							}
							if(arr.length > 3){
								r50 = Integer.parseInt(arr[3]);
							}
						}
						setCastlingsEPAnd50(wccl, wccs, bccl, bccs, ep, r50);
						
						setTurn(turn);
						commitBoard();
						
						return true;
					}
				}
				
			}
		} catch (Exception ex){
			//Log.e("initFEN", ex.toString());
			return false;
		}
		return false;
	}
	
	
	/*
0 NNxxx
1 NxNxx
2 NxxNx
3 NxxxN
4 xNNxx
5 xNxNx
6 xNxxN
7 xxNNx
8 xxNxN
9 xxxNN
	*/
	
	public native void destroy();
	public native int requestMove(int from, int to);
	public native int move(int move);
	public native void undo();
	public native void reset();
	public native void putPiece(int pos, int piece, int turn);
    public native void searchMove(int secs);
    public native void searchDepth(int depth);
    public native int getMove();
    public native int peekSearchDone();
    public native int peekSearchBestMove(int ply);
    public native int peekSearchBestValue();
    public native int peekSearchDepth();
    public native int getEvalCount();
    public native void setPromo(int piece);
    public native int getState();
    public native int isEnded();
    public native void setCastlingsEPAnd50(int wccl, int wccs, int bccl, int bccs, int ep, int r50);
    public native int getNumBoard();
    public native int getTurn();
    public native void commitBoard();
    public native void setTurn(int turn);
    public native int getMoveArraySize();
    public native int getMoveArrayAt(int i);
    public native int pieceAt(int turn, int pos);
    public native String getMyMoveToString();
    public native int getMyMove();
    public native int isAmbiguousCastle(int from, int to);
    public native int doCastleMove(int from, int to);
    public native String toFEN();
    public native void interrupt();
    public native int getNumCaptured(int turn, int piece);
    
    static {
        System.loadLibrary("chess-jni");
    }
}
