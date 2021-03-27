package jwtc.android.chess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.Locale;


public class start extends Activity {

    public static final String TAG = "start";
    private static String _ssActivity = "";
    private ListView _list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences getData = getSharedPreferences("ChessPlayer", Context.MODE_PRIVATE);
        String myLanguage  	= getData.getString("localelanguage", "");

        Locale current = getResources().getConfiguration().locale;
        String language = current.getLanguage();
        if(myLanguage.equals("")){    // locale language not used yet? then use device default locale
            myLanguage = language;
        }

        Locale locale = new Locale(myLanguage);    // myLanguage is current language
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.start);

        if (getIntent().getBooleanExtra("RESTART", false)) {
            finish();
            Intent intent = new Intent(this, start.class);
            startActivity(intent);
        }

        _list = (ListView)findViewById(R.id.ListStart);
        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _ssActivity = parent.getItemAtPosition(position).toString();
                try {
                    Intent i = new Intent();
                    Log.i("start", _ssActivity);
                    if (_ssActivity.equals(getString(R.string.start_play))) {
                        i.setClass(start.this, main.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(i);
                    } else if (_ssActivity.equals(getString(R.string.start_about))) {
                        i.setClass(start.this, HtmlActivity.class);
                        i.putExtra(HtmlActivity.HELP_MODE, "about");
                        startActivity(i);
                    } else if (_ssActivity.equals(getString(R.string.start_globalpreferences))) {
                        i.setClass(start.this, ChessPreferences.class);
                        startActivityForResult(i, 0);
                    } else if (_ssActivity.equals(getString(R.string.menu_help))) {
                        i.setClass(start.this, HtmlActivity.class);
                        i.putExtra(HtmlActivity.HELP_MODE, "help");
                        startActivity(i);
                    }
                } catch (Exception ex) {
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            Log.i(TAG, "finish and restart");

            Intent intent = new Intent(this, start.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("RESTART", true);
            startActivity(intent);

        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        SharedPreferences getData = getSharedPreferences("ChessPlayer", Context.MODE_PRIVATE);
        if (getData.getBoolean("RESTART", false)) {
            finish();
            Intent intent = new Intent(this, start.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            SharedPreferences.Editor editor = getData.edit();
            editor.putBoolean("RESTART", false);
            editor.apply();

            startActivity(intent);
        }
    }

    public static String get_ssActivity(){
        return _ssActivity;
    }
}
