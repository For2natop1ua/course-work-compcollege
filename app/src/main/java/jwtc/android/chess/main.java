package jwtc.android.chess;

import jwtc.chess.*;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.database.Cursor;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import android.view.GestureDetector;

public class main extends ChessActivity implements GestureDetector.OnGestureListener {

    public static final String TAG = "main";

    /**
     * instances for the view and game of chess *
     */
    private ChessView _chessView;
    private SaveGameDlg _dlgSave;
    private String[] _itemsMenu;
    private long _lGameID;
    private float _fGameRating;

    private Uri _uriNotification;


    public static final int REQUEST_SETUP = 1;
    public static final int REQUEST_OPEN = 2;
    public static final int REQUEST_OPTIONS = 3;
    public static final int REQUEST_NEWGAME = 4;

    private GestureDetector _gestureDetector;

    private boolean _skipReturn;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _uriNotification = null;

        setContentView(R.layout.main);

        this.makeActionOverflowMenuShown();


        _chessView = new ChessView(this);


        _lGameID = 0;
        _fGameRating = 2.5F;
        _dlgSave = null;

        _gestureDetector = new GestureDetector(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_flip:
                _chessView.flipBoard();
                return true;
            case R.id.action_options:
                intent = new Intent();
                intent.setClass(main.this, options.class);
                intent.putExtra("requestCode", REQUEST_OPTIONS);
                startActivityForResult(intent, REQUEST_OPTIONS);
                return true;
            case R.id.action_clock:
                AlertDialog.Builder builder = new AlertDialog.Builder(main.this);
                builder.setTitle(getString(R.string.title_menu));
                String sTime = getString(R.string.choice_clock_num_minutes);
                final String[] itemsMenu = new String[]{"-", String.format(sTime, 2), String.format(sTime, 5), String.format(sTime, 10), String.format(sTime, 30), String.format(sTime, 60)};
                builder.setItems(itemsMenu, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss();
                        if (item == 0)
                            _chessView._lClockTotal = 0;
                        else if (item == 1)
                            _chessView._lClockTotal = 120000;
                        else if (item == 2)
                            _chessView._lClockTotal = 300000;
                        else if (item == 3)
                            _chessView._lClockTotal = 600000;
                        else if (item == 4)
                            _chessView._lClockTotal = 1800000;
                        else if (item == 5)
                            _chessView._lClockTotal = 3600000;
                        _chessView.resetTimer();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.action_help:
                Intent i = new Intent();
                i.setClass(main.this, HtmlActivity.class);
                i.putExtra(HtmlActivity.HELP_MODE, "help_play");
                startActivity(i);
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void showSubViewMenu() {

        _itemsMenu = new String[]{
                getString(R.string.menu_subview_cpu),
                getString(R.string.menu_subview_captured),
                getString(R.string.menu_subview_seek),
                getString(R.string.menu_subview_moves),
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(main.this);
        builder.setTitle(getString(R.string.menu_subview_title));

        builder.setItems(_itemsMenu, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();
                _chessView.toggleControl(item);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {

        int c = (event.getUnicodeChar());
        Log.i("main", "onKeyDown " + keyCode + " = " + (char)c);
        if(keyCode == KeyEvent.KEYCODE_MENU){
            return true;
        }

        // preference is to skip a carriage return
        if(_skipReturn && (char)c == '\r'){
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {

        Log.i("main", "onResume");

        SharedPreferences prefs = this.getPrefs();


        _skipReturn = prefs.getBoolean("skipReturn", true);


        String sPGN = "";
        String sFEN = prefs.getString("FEN", null);

        String sTmp = prefs.getString("NotificationUri", null);
        if (sTmp == null) {
            _uriNotification = null;
        } else {
            _uriNotification = Uri.parse(sTmp);
        }

        final Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Uri uri = intent.getData();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            _lGameID = 0;
            Log.i("onResume", "action send with type " + type);
            if ("application/x-chess-pgn".equals(type)) {
                sPGN = intent.getStringExtra(Intent.EXTRA_TEXT);
                if(sPGN != null) {
                    sPGN = sPGN.trim();
                    loadPGN(sPGN);
                }
            } else {
                sFEN = intent.getStringExtra(Intent.EXTRA_TEXT);
                if(sFEN != null) {
                    sFEN = sFEN.trim();
                    loadFEN(sFEN);
                }
            }
        } else if (uri != null) {
            _lGameID = 0;
            sPGN = "";
            Log.i("onResume", "opening " + uri.toString());
            InputStream is;
            try {
                is = getContentResolver().openInputStream(uri);
                byte[] b = new byte[4096];
                int len;

                while ((len = is.read(b)) > 0) {
                    sPGN += new String(b);
                }
                is.close();

                sPGN = sPGN.trim();

                loadPGN(sPGN);

            } catch (Exception e) {
                Log.e("onResume", "Failed " + e.toString());
            }
        } else if (sFEN != null) {
            // default, from prefs
            Log.i("onResume", "Loading FEN " + sFEN);
            _lGameID = 0;
            loadFEN(sFEN);
        } else {
            _lGameID = prefs.getLong("game_id", 0);
            if (_lGameID > 0) {
                Log.i("onResume", "loading saved game " + _lGameID);
                loadGame();
            } else {
                sPGN = prefs.getString("game_pgn", null);
                Log.i("onResume", "pgn: " + sPGN);
                loadPGN(sPGN);
            }
        }

        _chessView.OnResume(prefs);

        _chessView.updateState();

        super.onResume();
    }


    @Override
    protected void onPause() {
        //Debug.stopMethodTracing();

        if (_lGameID > 0) {
            ContentValues values = new ContentValues();

            values.put(PGNColumns.DATE, _chessView.getDate().getTime());
            values.put(PGNColumns.WHITE, _chessView.getWhite());
            values.put(PGNColumns.BLACK, _chessView.getBlack());
            values.put(PGNColumns.PGN, _chessView.exportFullPGN());
            values.put(PGNColumns.RATING, _fGameRating);
            values.put(PGNColumns.EVENT, _chessView.getPGNHeadProperty("Event"));

            saveGame(values, false);
        }
        SharedPreferences.Editor editor = this.getPrefs().edit();
        editor.putLong("game_id", _lGameID);
        editor.putString("game_pgn", _chessView.exportFullPGN());
        editor.putString("FEN", null); // 
        if (_uriNotification == null)
            editor.putString("NotificationUri", null);
        else
            editor.putString("NotificationUri", _uriNotification.toString());
        _chessView.OnPause(editor);

        editor.commit();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        _chessView.OnDestroy();
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("main", "onActivityResult");

        if (requestCode == REQUEST_SETUP) {
            if (resultCode == RESULT_OK) {
                //data));
                _chessView.clearPGNView();
            }
        } else if (requestCode == REQUEST_OPEN) {
            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();
                try {
                    _lGameID = Long.parseLong(uri.getLastPathSegment());
                } catch (Exception ex) {
                    _lGameID = 0;
                }
                SharedPreferences.Editor editor = this.getPrefs().edit();
                editor.putLong("game_id", _lGameID);
                editor.putInt("boardNum", 0);
                editor.putString("FEN", null);
                editor.putInt("playMode", _chessView.HUMAN_HUMAN);
                editor.putBoolean("playAsBlack", false);
                editor.commit();
            }
        } else if (requestCode == REQUEST_NEWGAME) {

            if (resultCode == RESULT_OK) {
                newGame();
            }
        }
    }



    private void loadFEN(String sFEN) {
        if (sFEN != null) {
            Log.i("loadFEN", sFEN);
            _chessView.updateState();
        }
    }

    private void loadPGN(String sPGN) {
        if (sPGN != null) {
            if (false == _chessView.loadPGN(sPGN)) {
                doToast(getString(R.string.err_load_pgn));
            }
            _chessView.updateState();
        }
    }

    private void newGame() {
        _lGameID = 0;
        _chessView.newGame();
        SharedPreferences.Editor editor = this.getPrefs().edit();
        editor.putString("FEN", null);
        editor.putInt("boardNum", 0);
        editor.putString("game_pgn", null);
        editor.putLong("game_id", _lGameID);
        editor.commit();
    }



    private void loadGame() {
        if (_lGameID > 0) {
            Uri uri = ContentUris.withAppendedId(MyPGNProvider.CONTENT_URI, _lGameID);
            Cursor c = managedQuery(uri, PGNColumns.COLUMNS, null, null, null);
            if (c != null && c.getCount() == 1) {

                c.moveToFirst();

                _lGameID = c.getLong(c.getColumnIndex(PGNColumns._ID));
                String sPGN = c.getString(c.getColumnIndex(PGNColumns.PGN));
                _chessView.loadPGN(sPGN);

                _chessView.setPGNHeadProperty("Event", c.getString(c.getColumnIndex(PGNColumns.EVENT)));
                _chessView.setPGNHeadProperty("White", c.getString(c.getColumnIndex(PGNColumns.WHITE)));
                _chessView.setPGNHeadProperty("Black", c.getString(c.getColumnIndex(PGNColumns.BLACK)));
                _chessView.setDateLong(c.getLong(c.getColumnIndex(PGNColumns.DATE)));

                _fGameRating = c.getFloat(c.getColumnIndex(PGNColumns.RATING));
            } else {
                _lGameID = 0; // probably deleted
            }
        } else {
            _lGameID = 0;
        }
    }

    public void saveGame() {
        String sEvent = _chessView.getPGNHeadProperty("Event");
        if (sEvent == null)
            sEvent = getString(R.string.savegame_event_question);
        String sWhite = _chessView.getWhite();
        if (sWhite == null)
            sWhite = getString(R.string.savegame_white_question);
        String sBlack = _chessView.getBlack();
        if (sBlack == null)
            sBlack = getString(R.string.savegame_black_question);

        Date dd = _chessView.getDate();
        if (dd == null)
            dd = Calendar.getInstance().getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(dd);

        if (_dlgSave == null)
            _dlgSave = new SaveGameDlg(this);
        _dlgSave.setItems(sEvent, sWhite, sBlack, cal, _chessView.exportFullPGN(), _fGameRating, _lGameID > 0);
        _dlgSave.show();
    }

    public void saveGame(ContentValues values, boolean bCopy) {

        SharedPreferences.Editor editor = this.getPrefs().edit();
        editor.putString("FEN", null);
        editor.commit();

        _chessView.setPGNHeadProperty("Event", (String) values.get(PGNColumns.EVENT));
        _chessView.setPGNHeadProperty("White", (String) values.get(PGNColumns.WHITE));
        _chessView.setPGNHeadProperty("Black", (String) values.get(PGNColumns.BLACK));
        _chessView.setDateLong((Long) values.get(PGNColumns.DATE));

        _fGameRating = (Float) values.get(PGNColumns.RATING);
        //

        if (_lGameID > 0 && (bCopy == false)) {
            Uri uri = ContentUris.withAppendedId(MyPGNProvider.CONTENT_URI, _lGameID);
            getContentResolver().update(uri, values, null, null);
        } else {
            Uri uri = MyPGNProvider.CONTENT_URI;
            Uri uriInsert = getContentResolver().insert(uri, values);
            Cursor c = managedQuery(uriInsert, new String[]{PGNColumns._ID}, null, null, null);
            if (c != null && c.getCount() == 1) {
                c.moveToFirst();
                _lGameID = c.getLong(c.getColumnIndex(PGNColumns._ID));
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        _gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        int Xdiff = (int) motionEvent.getX() - (int) motionEvent1.getX();
        int Ydiff = (int) motionEvent.getY() - (int) motionEvent1.getY();

        if (Xdiff < -150) {
            _chessView.next();
        }

        if (Xdiff > 150) {
            _chessView.previous();
        }

        if (Ydiff > 150 || Ydiff < -150) {
            _chessView.flipBoard();
        }
        return true;
    }

}