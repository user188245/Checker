package com.user.checker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView textView_single,textView_multi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        textView_single = (TextView) findViewById(R.id.textView_single);
        textView_multi = (TextView) findViewById(R.id.textView_multi);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.textView_single:
                intent = new Intent(MainActivity.this,SingleGameActivity.class);
                startActivity(intent);
                break;
            case R.id.textView_multi:
                intent = new Intent(MainActivity.this,Lobby.class);
                startActivity(intent);
                break;
        }
    }
}
