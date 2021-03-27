package jwtc.android.chess;

import jwtc.chess.GameControl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class options extends MyBaseActivity {

    public static final String TAG = "options";

    private CheckBox _checkAutoFlip, _checkMoves;
    private Spinner _spinLevel, _spinLevelPly;
    private Button _butCancel, _butOk;
    private RadioButton _radioTime, _radioPly, _radioWhite, _radioBlack, _radioAndroid, _radioHuman;

    private static boolean _sbFlipTopPieces, _sbPlayAsBlack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.options);

        this.makeActionOverflowMenuShown();

        setTitle(R.string.title_options);

        _radioAndroid = (RadioButton) findViewById(R.id.rbAndroid);
        _radioAndroid.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _radioHuman.setChecked(!isChecked);
                _checkAutoFlip.setEnabled(false);
                _radioPly.setEnabled(true);
                _radioTime.setEnabled(true);
                _spinLevel.setEnabled(true);
                _spinLevelPly.setEnabled(true);
            }
        });
        _radioHuman = (RadioButton) findViewById(R.id.rbHuman);
        _radioHuman.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _radioAndroid.setChecked(!_radioHuman.isChecked());
                _checkAutoFlip.setEnabled(true);
                _radioPly.setEnabled(false);
                _radioTime.setEnabled(false);
                _spinLevel.setEnabled(false);
                _spinLevelPly.setEnabled(false);
            }
        });

        _checkAutoFlip = (CheckBox) findViewById(R.id.CheckBoxOptionsAutoFlip);
        _checkMoves = (CheckBox) findViewById(R.id.CheckBoxOptionsShowMoves);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.levels_time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        _spinLevel = (Spinner) findViewById(R.id.SpinnerOptionsLevel);
        _spinLevel.setPrompt(getString(R.string.title_pick_level));
        _spinLevel.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapterPly = ArrayAdapter.createFromResource(this, R.array.levels_ply, android.R.layout.simple_spinner_item);
        adapterPly.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinLevelPly = (Spinner) findViewById(R.id.SpinnerOptionsLevelPly);
        _spinLevelPly.setPrompt(getString(R.string.title_pick_level));
        _spinLevelPly.setAdapter(adapterPly);

        _radioTime = (RadioButton) findViewById(R.id.RadioOptionsTime);
        _radioTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _radioPly.setChecked(!_radioTime.isChecked());
            }
        });
        _radioPly = (RadioButton) findViewById(R.id.RadioOptionsPly);
        _radioPly.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _radioTime.setChecked(!_radioPly.isChecked());
            }
        });

        _radioWhite = (RadioButton) findViewById(R.id.rbWhite);
        _radioWhite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _radioBlack.setChecked(!_radioWhite.isChecked());
            }
        });
        _radioBlack = (RadioButton) findViewById(R.id.rbBlack);
        _radioBlack.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _radioWhite.setChecked(!_radioBlack.isChecked());
            }
        });

        _butCancel = (Button) findViewById(R.id.ButtonOptionsCancel);
        _butCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        _butOk = (Button) findViewById(R.id.ButtonOptionsOk);
        _butOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                SharedPreferences.Editor editor = options.this.getPrefs().edit();

                editor.putInt("levelMode", _radioTime.isChecked() ? GameControl.LEVEL_TIME : GameControl.LEVEL_PLY);
                editor.putInt("level", _spinLevel.getSelectedItemPosition() + 1);
                editor.putInt("levelPly", _spinLevelPly.getSelectedItemPosition() + 1);
                editor.putInt("playMode", _radioAndroid.isChecked() ? GameControl.HUMAN_PC : GameControl.HUMAN_HUMAN);
                editor.putBoolean("autoflipBoard", _checkAutoFlip.isChecked());
                editor.putBoolean("showMoves", _checkMoves.isChecked());
                editor.putBoolean("playAsBlack", _radioBlack.isChecked());

                editor.apply();

                    setResult(RESULT_OK);
                    finish();
                }
        });
    }


    @Override
    protected void onResume() {

        final Intent intent = getIntent();

        if (intent.getExtras().getInt("requestCode") == main.REQUEST_NEWGAME) {
            setTitle(R.string.menu_new);
        }

        SharedPreferences prefs = this.getPrefs();

        _radioAndroid.setChecked(prefs.getInt("playMode", GameControl.HUMAN_PC) == GameControl.HUMAN_PC);
        _radioHuman.setChecked(!_radioAndroid.isChecked());

        _checkAutoFlip.setChecked(prefs.getBoolean("autoflipBoard", false));
        _checkAutoFlip.setEnabled(_radioHuman.isChecked());
        _checkMoves.setChecked(prefs.getBoolean("showMoves", true));

        _radioBlack.setChecked(prefs.getBoolean("playAsBlack", false));
        _radioWhite.setChecked(!_radioBlack.isChecked());

        _radioTime.setChecked(prefs.getInt("levelMode", GameControl.LEVEL_TIME) == GameControl.LEVEL_TIME);
        _radioPly.setChecked(prefs.getInt("levelMode", GameControl.LEVEL_TIME) == GameControl.LEVEL_PLY);

        _spinLevel.setSelection(prefs.getInt("level", 2) - 1);
        _spinLevelPly.setSelection(prefs.getInt("levelPly", 2) - 1);

        super.onResume();
    }

    @Override
    protected void onPause() {

        _sbFlipTopPieces = (_radioHuman.isChecked() && !_checkAutoFlip.isChecked());
        _sbPlayAsBlack = _radioBlack.isChecked();

        super.onPause();
    }

    public static boolean is_sbFlipTopPieces(){
        return _sbFlipTopPieces;
    }

    public static boolean is_sbPlayAsBlack(){
        return _sbPlayAsBlack;
    }

}
