package com.example.deathgull.sungka_project;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CreditsActivity extends Activity {
    private static final float title_scale = 20;
    private static final float contributors_scale = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        float screen_height = size.y;

        // back end
        TextView title_backend = ((TextView) findViewById(R.id.title_backend));
        TextView dev_dovydas = ((TextView) findViewById(R.id.dev_dovydas));
        TextView dev_oliver = ((TextView) findViewById(R.id.dev_oliver));

        title_backend.setTextSize(screen_height / title_scale);
        dev_dovydas.setTextSize(screen_height / contributors_scale);
        dev_oliver.setTextSize(screen_height / contributors_scale);

        // front end
        TextView title_frontend = ((TextView) findViewById(R.id.title_frontend));
        TextView dev_martin = ((TextView) findViewById(R.id.dev_martin));
        TextView dev_tim = ((TextView) findViewById(R.id.dev_tim));

        title_frontend.setTextSize(screen_height / title_scale);
        dev_martin.setTextSize(screen_height / contributors_scale);
        dev_tim.setTextSize(screen_height / contributors_scale);

        // testing
        TextView title_testing = ((TextView) findViewById(R.id.title_testing));
        TextView dev_max = ((TextView) findViewById(R.id.dev_max));

        title_testing.setTextSize(screen_height / title_scale);
        dev_max.setTextSize(screen_height / contributors_scale);

        // contributors
        TextView title_contributors = ((TextView) findViewById(R.id.title_contributors));
        TextView contributors_laimonas = ((TextView) findViewById(R.id.contributor_laimonas));
        TextView contributors_julija = ((TextView) findViewById(R.id.contributor_julija));

        title_contributors.setTextSize(screen_height / title_scale);
        contributors_laimonas.setTextSize(screen_height / contributors_scale);
        contributors_julija.setTextSize(screen_height / contributors_scale);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_credits, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
