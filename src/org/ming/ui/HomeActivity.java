
package org.ming.ui;

import org.ming.R;
import org.ming.logic.IMusicActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.TabHost;

public class HomeActivity extends TabActivity implements IMusicActivity {
    private Handler handler;

    private LayoutInflater mInflater;

    private TabHost mTabHost;

    @Override
    public void init() {

    }

    @Override
    public void refresh(Object... param) {
        // TODO Auto-generated method stub

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.home);
        this.handler = new Handler() {
            public void handleMessage(Message paramAnonymousMessage) {

            }
        };
        initTab();
    }

    public void initTab() {
        this.mInflater = LayoutInflater.from(this);
        this.mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        this.mTabHost.setup(getLocalActivityManager());

        Intent localIntent1 = new Intent(this, OnlineMusicActivity.class);
        this.mTabHost.addTab(createTabSpec("TAB_ONLINE", R.layout.tab_online_music_layout,
                localIntent1));

        Intent localIntent2 = new Intent(this, LocalMusicActivity.class);
        this.mTabHost.addTab(createTabSpec("TAB_LOCAL", R.layout.tab_local_music_layout,
                localIntent2));

        Intent localIntent3 = new Intent(this, MyMiGuActivity.class);
        this.mTabHost
                .addTab(createTabSpec("TAB_MIGU", R.layout.tab_mine_music_layout, localIntent3));
        this.mTabHost.setCurrentTab(0);
    }

    private TabHost.TabSpec createTabSpec(String paramString, int paramInt, Intent paramIntent) {
        TabHost.TabSpec localTabSpec = this.mTabHost.newTabSpec(paramString);
        localTabSpec.setIndicator(this.mInflater.inflate(paramInt, null));
        localTabSpec.setContent(paramIntent);
        return localTabSpec;
    }
}
