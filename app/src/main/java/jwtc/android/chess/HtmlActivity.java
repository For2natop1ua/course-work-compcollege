package jwtc.android.chess;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class HtmlActivity extends MyBaseActivity {

	public static final String TAG = "HtmlActivity";
	public static String HELP_MODE = "HELP_MODE";

	private WebView _webview;
	private String _lang;
	private TextView _TVversionName;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.help);

		this.makeActionOverflowMenuShown();
		// html assets localization
		 if (Locale.getDefault().getLanguage().equals("uk")) {
			 _lang = "uk";
		 }else if (Locale.getDefault().getLanguage().equals("ru")) {
			_lang = "ru";
		} else {
			_lang = "en";
		}
		_webview = (WebView) findViewById(R.id.WebViewHelp);

		_TVversionName = (TextView) findViewById(R.id.textVersionName);
		_TVversionName.setText(getString(R.string.version_number, BuildConfig.VERSION_NAME)); // "Version:  " + BuildConfig.VERSION_NAME

	}

	@Override
	protected void onResume() {
		super.onResume();

		final Intent intent = getIntent();

		Bundle extras = intent.getExtras();
		if (extras != null) {
			String s = extras.getString(HELP_MODE);

			_TVversionName.setVisibility(s.equals("about") ? View.VISIBLE : View.GONE);

			_webview.loadUrl("file:///android_asset/" + s + "-" + _lang
					+ ".html");
		}
	}
}
