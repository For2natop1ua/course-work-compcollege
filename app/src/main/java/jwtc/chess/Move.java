package jwtc.chess;

// Move. Wrapper class for the integer representation of a move
// The positional values "from" and "to" are shifted into the integer.
// Also en-passant, castling, hit, first pawn move, promotion and promotion piece
// are part of the move.

public class Move
{
	// mask for a position [0-63], 6 bits.
	private static final int MASK_POS = 0x3F;
	// mask for a boolean [false=0, true=1], 1 bit
	private static final int MASK_BOOL = 1;
	
	// shift values
	private static final int SHIFT_TO = 6;
	private static final int SHIFT_EP = 13;
	// short castling OO
	private static final int SHIFT_OO = 14;
	// long castling OOO
	private static final int SHIFT_OOO = 15;
	private static final int SHIFT_HIT = 16;
	// with this move the opponent king is checked
	private static final int SHIFT_CHECK = 18;
	// the piece the pawn is promoted to
	private static final int SHIFT_PROMOTIONPIECE = 20;
	
	// returns the integer representation of the simpelest move, from
	// position @from to position @to

	// returns "from" of the move
	public static final int getFrom(final int move)
	{
		return move & MASK_POS;
	}
	public static final int getTo(final int move)
	{
		return (move >> SHIFT_TO) & MASK_POS;
	}
	public static final boolean isEP(final int move)
	{
		return ((move >> SHIFT_EP) & MASK_BOOL) == MASK_BOOL;
	}
	public static final boolean isOO(final int move)
	{
		return ((move >> SHIFT_OO) & MASK_BOOL) == MASK_BOOL;
	}
	public static final boolean isOOO(final int move)
	{
		return ((move >> SHIFT_OOO) & MASK_BOOL) == MASK_BOOL;
	}
	public static final boolean isHIT(final int move)
	{
		return ((move >> SHIFT_HIT) & MASK_BOOL) == MASK_BOOL;
	}
	public static final boolean isCheck(final int move)
	{
		return ((move >> SHIFT_CHECK) & MASK_BOOL) == MASK_BOOL;
	}
	public static final int getPromotionPiece(final int move)
	{
		return move >> SHIFT_PROMOTIONPIECE;
	}

	// returns pgn alike string representation of the move - not full pgn format because then more information is needed
	public static final String toDbgString(final int move)
	{
		if(Move.isOO(move))
			return "O-O";
		if(Move.isOOO(move))
			return "O-O-O";
		return "[" + Pos.toString(Move.getFrom(move)) + (Move.isHIT(move) ? "x" : "-") + Pos.toString(Move.getTo(move)) + (Move.isCheck(move) ? "+" : "") + (Move.isEP(move) ? " ep" : "") + "]";
	}
	
}
