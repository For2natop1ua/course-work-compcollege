package jwtc.android.chess;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;



public class ChessPreferences extends MyPreferenceActivity  implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private static String TAG = "ChessPreferences";
	private Uri _uriNotification;
	private int _colorScheme;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager pm = getPreferenceManager();
        pm.setSharedPreferencesName("ChessPlayer");
        
        final SharedPreferences prefs = pm.getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        _colorScheme = prefs.getInt("ColorScheme", 0);
		_uriNotification = Uri.parse(prefs.getString("NotificationUri", ""));
        
        addPreferencesFromResource(R.xml.globalprefs);

		prefs.registerOnSharedPreferenceChangeListener(this);

        Preference prefColor = (Preference) findPreference("colorSchemeHandle");
        prefColor.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		
        		final String[] items = getResources().getStringArray(R.array.colorschemes);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(ChessPreferences.this);
				builder.setTitle(R.string.title_pick_colorscheme);
				builder.setSingleChoiceItems(items, _colorScheme, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	
				    	_colorScheme = item;
			        	editor.putInt("ColorScheme", _colorScheme);

						editor.commit();
				    	
				    	dialog.dismiss();
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
				
				return true;
        	}
        });


		setResult(0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

		if(s.equals("localelanguage")) {
			Log.i(TAG, s);
			setResult(1);
		}
	}
}

