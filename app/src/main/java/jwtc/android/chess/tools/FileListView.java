package jwtc.android.chess.tools;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import jwtc.android.chess.MyBaseActivity;
import jwtc.android.chess.iconifiedlist.*;

public class FileListView extends ListActivity {

    private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyBaseActivity.prepareWindowSettings(this);

        MyBaseActivity.makeActionOverflowMenuShown(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (this.directoryEntries.size() > position) {
            String selectedFileString = this.directoryEntries.get(position).getText();
            if (selectedFileString.equals(".")) {
            }
        }

    }
}