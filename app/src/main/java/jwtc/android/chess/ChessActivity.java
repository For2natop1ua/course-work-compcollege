package jwtc.android.chess;

/**
 * A superclass to be used as the activity reference in normal mode.
 * main  inherit this class, and this allows chessView to assume the
 * existence of the methods described here, whether its _parent is main.
 */
public class ChessActivity extends MyBaseActivity {

    public void showSubViewMenu() {
        // intentionally empty
    }

    public void saveGame() {
        // intentionally empty
    }
}
