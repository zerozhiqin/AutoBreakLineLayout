package dev.misono.autobreaklinelayout;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import dev.misono.autobreaklinelayout.simpleinject.VInject;
import dev.misono.autobreaklinelayout.simpleinject.VInjects;
import dev.misono.breaklinelayout.BreakLineLayout;


public class MainActivity extends ActionBarActivity {

    @VInject(R.id.break_line_layout)
    BreakLineLayout breakLineLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VInjects.injectIntoActivity(this);

        int[] colors = {
                0xFF20476C,
                0xFF4CB1BD,
                0xFFFEB872,
                0xFFD67D55,
        };

        String[] names = {
                "Wojciech", "Szczesny", "David", "Ospina", "Mathieu", "Debuchy", "Nacho", "Monereal",
                "Kueran", " Gibbs", "Gabriel", "Alex", "Oxlade-Chamberlain", "Abou Diaby",
                "Jack Wilshere", "Mikel Arteta", "Alexis", "Serge Gnabry", "Danny Welbeck"
                , "Olivier Giroud"

        };
        for (int i = 0; i < names.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(names[i]);
            textView.setTextColor(0xFFFFFFFF);
            textView.setBackgroundColor(colors[i % colors.length]);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(16, 16, 16, 16);

            BreakLineLayout.LayoutParams blllp = new BreakLineLayout.LayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT
            );
            blllp.setMargins(10, 10, 10, 10);
            textView.setLayoutParams(blllp);

            breakLineLayout.addView(textView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sharewidth) {
            breakLineLayout.setShareWidth(!breakLineLayout.isShareWidth());
            return true;
        } else if (id == R.id.action_show_1) {
            breakLineLayout.setShowLines(1);
            return true;
        }else if (id == R.id.action_show_3) {
            breakLineLayout.setShowLines(3);
            return true;
        }else if (id == R.id.action_show_max) {
            breakLineLayout.setShowAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
