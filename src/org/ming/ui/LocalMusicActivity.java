package org.ming.ui;

import org.ming.R;

import android.app.ListActivity;
import android.os.Bundle;

public class LocalMusicActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.local_music);
    }
}
