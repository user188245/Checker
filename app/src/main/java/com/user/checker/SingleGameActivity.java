package com.user.checker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.user.checker.import_package.Board;
import com.user.checker.import_package.MoveSequenceValue;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SingleGameActivity extends AppCompatActivity implements View.OnClickListener{


    CheckerBoard board0;

    public LinearLayout linearLayout_board;
    public ImageButton imageButton_undo,imageButton_redo;
    public ImageView imageView_turn;
    public TextView textView_turnCount,textView_status;
    public Button button_exit,button_save,button_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_game);
        init();
    }

    private void init() {
        linearLayout_board = (LinearLayout) findViewById(R.id.linearLayout_board);
        imageButton_undo = (ImageButton) findViewById(R.id.imageButton_undo);
        imageButton_redo = (ImageButton) findViewById(R.id.imageButton_redo);
        imageView_turn = (ImageView) findViewById(R.id.imageView_turn);
        textView_turnCount = (TextView) findViewById(R.id.textView_turnCount);
        textView_status = (TextView) findViewById(R.id.textView_status);
        button_exit = (Button) findViewById(R.id.button_exit);
        button_save = (Button) findViewById(R.id.button_save);
        button_load = (Button) findViewById(R.id.button_load);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_exit:
                finish();
                break;
            case R.id.button_save:
                saveFIle();
                break;
            case R.id.button_load:
                loadFile();
                break;
        }
    }

    private void saveFIle(){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(getFilesDir() + "checkers.chk");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(board0.board.getMoveSequence());
            objectOutputStream.close();
            fileOutputStream.close();
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }  catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadFile(){
        try {
            FileInputStream fileInputStream = new FileInputStream(getFilesDir() + "checkers.chk");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            ArrayList<MoveSequenceValue> moveSequence = (ArrayList<MoveSequenceValue>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            if (moveSequence == null || moveSequence.size() == 0)
                return;
            linearLayout_board.removeView(board0);
            board0 = new CheckerBoard(getApplicationContext(), null);
            linearLayout_board.addView(board0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            Board board = new Board(board0.getContext(), board0, moveSequence, this);
            imageButton_undo.setOnClickListener(board0);
            imageButton_redo.setOnClickListener(board0);
            for (int i = 0; i < moveSequence.size(); i++)
                board.redo();
            System.gc();
            Toast.makeText(getApplicationContext(), "Loaded", Toast.LENGTH_SHORT).show();
        } catch (EOFException e) {
            Toast.makeText(getApplicationContext(), "No file exists!", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException | IOException e) {
            Toast.makeText(getApplicationContext(), "Couldn't load the files", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            linearLayout_board.setGravity(Gravity.CENTER);
            CheckerBoard.TOTAL_LENGTH = Math.min(linearLayout_board.getWidth(), linearLayout_board.getHeight());
            CheckerBoard.REC_LENGTH = CheckerBoard.TOTAL_LENGTH / 8;
            if(board0 == null){
                linearLayout_board.setLayoutParams(new LinearLayout.LayoutParams(CheckerBoard.TOTAL_LENGTH,CheckerBoard.TOTAL_LENGTH));
                board0 = new CheckerBoard(getApplicationContext(),null);
                linearLayout_board.addView(board0,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                Board board = new Board(board0.getContext(),board0,this);
                imageButton_undo.setOnClickListener(board0);
                imageButton_redo.setOnClickListener(board0);
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }
}
