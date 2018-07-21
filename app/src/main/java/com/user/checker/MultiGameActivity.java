package com.user.checker;

import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.user.checker.import_package.Board;
import com.user.checker.models.BaseModel;
import com.user.checker.models.CommModel;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

public class MultiGameActivity extends AppCompatActivity implements View.OnClickListener{
    public ImageView imageView_multi_enemyStone,imageView_multi_myStone,imageView_multi_turn;
    public TextView textView_multi_enemyName,textView_multi_enemySid,textView_multi_myName,textView_multi_mySid;
    public TextView textView_multi_status;
    public TextView textView_multi_chattingOpen,textView_multi_chattingClose;
    public EditText editText_multi_chattingArea,editText_multi_chattingInput;
    public Button button_multi_chattingInput;
    public LinearLayout linearLayout_multi_board,linearLayout_multi_chattingBoard;

    private Intent intent;

    public boolean isHost;
    String s_id;
    String s_id2;

    public CheckerBoard checkerBoard;
    public NetworkManager networkManager;
    public Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_game);
        init();
    }

    private void initNetwork() {
        networkManager = new NetworkManager(this,isHost,handler);
        networkManager.start();
    }

    private void init() {

        imageView_multi_enemyStone = (ImageView) findViewById(R.id.imageView_multi_enemyStone);
        imageView_multi_myStone = (ImageView) findViewById(R.id.imageView_multi_myStone);
        imageView_multi_turn = (ImageView) findViewById(R.id.imageView_multi_turn);
        textView_multi_enemyName = (TextView) findViewById(R.id.textView_multi_enemyName);
        textView_multi_enemySid = (TextView) findViewById(R.id.textView_multi_enemySid);
        textView_multi_myName = (TextView) findViewById(R.id.textView_multi_myName);
        textView_multi_mySid = (TextView) findViewById(R.id.textView_multi_mySid);
        textView_multi_status = (TextView) findViewById(R.id.textView_multi_status);
        textView_multi_chattingOpen = (TextView) findViewById(R.id.textView_multi_chattingOpen);
        textView_multi_chattingClose = (TextView) findViewById(R.id.textView_multi_chattingClose);
        editText_multi_chattingArea = (EditText) findViewById(R.id.editText_multi_chattingArea);
        editText_multi_chattingInput = (EditText) findViewById(R.id.editText_multi_chattingInput);
        button_multi_chattingInput = (Button) findViewById(R.id.button_multi_chattingInput);
        linearLayout_multi_board = (LinearLayout) findViewById(R.id.linearLayout_multi_board);
        linearLayout_multi_chattingBoard = (LinearLayout) findViewById(R.id.linearLayout_multi_chattingBoard);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textView_multi_chattingOpen:
                linearLayout_multi_chattingBoard.setVisibility(View.VISIBLE);
                break;
            case R.id.textView_multi_chattingClose:
                linearLayout_multi_chattingBoard.setVisibility(View.INVISIBLE);
                break;
            case R.id.button_multi_chattingInput:
                if(!editText_multi_chattingInput.getText().toString().isEmpty()) {
                    try {
                        if (!networkManager.state.equals(NetworkManager.State.Connected) || networkManager.s_id2.isEmpty()) {
                            editText_multi_chattingArea.append("<Disconnected Player>:" + editText_multi_chattingInput.getText() + "\n");
                        } else {
                            editText_multi_chattingArea.append("<" + networkManager.playerName + ">:" + editText_multi_chattingInput.getText() + "\n");
                            networkManager.msgQueue.add(new CommModel(0, networkManager.s_id2, "<" + networkManager.playerName + ">:" + editText_multi_chattingInput.getText()));
                        }
                    } catch (Exception e) {
                    }
                    editText_multi_chattingInput.setText("");
                }
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(networkManager != null) {
            networkManager.interrupt();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            linearLayout_multi_board.setGravity(Gravity.CENTER);
            CheckerBoard.TOTAL_LENGTH = Math.min(linearLayout_multi_board.getWidth(), linearLayout_multi_board.getHeight());
            CheckerBoard.REC_LENGTH = CheckerBoard.TOTAL_LENGTH / 8;
            if(checkerBoard == null){
                linearLayout_multi_board.setLayoutParams(new LinearLayout.LayoutParams(CheckerBoard.TOTAL_LENGTH,CheckerBoard.TOTAL_LENGTH));
                checkerBoard = new CheckerBoard(this,null);
                linearLayout_multi_board.addView(checkerBoard,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

                intent = getIntent();
                s_id = intent.getStringExtra("s_id");
                s_id2 = "";
                isHost=intent.getBooleanExtra("isHost",false);
                if(isHost)
                    Toast.makeText(getApplicationContext(),"당신의 s_id :" + s_id + ", 호스트로 입장.",Toast.LENGTH_SHORT).show();
                else{
                    s_id2 = intent.getStringExtra("s_id2");
                    Toast.makeText(getApplicationContext(),"당신의 s_id :" + s_id + ", 게스트로 입장.",Toast.LENGTH_SHORT).show();
                }
                textView_multi_myName.setText("player : 플레이어");
                textView_multi_mySid.setText("s_id : " + s_id);
                initNetwork();
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }
}
