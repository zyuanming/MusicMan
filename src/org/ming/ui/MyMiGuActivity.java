
package org.ming.ui;

import org.ming.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MyMiGuActivity extends TabActivity {

    private LayoutInflater mInflater;

    private TabHost mTabHost;

    private int[] mTabTitleID = {
            R.string.title_cancel_online_music_atdcontroller,
            R.string.title_business_order_activity, R.string.title_music_management_activity
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_migu);
        initTab();
    }

    private void initTab() {
        this.mInflater = LayoutInflater.from(this);
        this.mTabHost = ((TabHost) findViewById(16908306));
        this.mTabHost.setup(getLocalActivityManager());
        this.mTabHost.setCurrentTab(0);
        this.mTabHost.getTabWidget().setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                Toast.makeText(MyMiGuActivity.this.getApplicationContext(), "click", 0).show();
            }
        });
        int i = 0;
        if (i >= this.mTabTitleID.length)
            return;
        Intent localIntent;
        // if (this.mTabTitleID[i] == 2131165448)
        // localIntent = new Intent(this, MyMiGuMyCollectActivity.class);
        while (true) {
            TabHost.TabSpec localTabSpec = this.mTabHost.newTabSpec("TAB_TITLE" + i);
            View localView = this.mInflater.inflate(2130903145, null);
            localTabSpec.setIndicator(localView);
//            localTabSpec.setContent(localIntent);
            this.mTabHost.addTab(localTabSpec);
            ((TextView) localView.findViewById(2131034455)).setText(this.mTabTitleID[i]);
            i++;
            break;
            // if (this.mTabTitleID[i] == 2131165449)
            // localIntent = new Intent(this,
            // MyMiGuMusicManagementActivity.class);
            // else
            // localIntent = new Intent(this,
            // MyMiGuBusinessOrderActivity.class);
        }
    }
}
