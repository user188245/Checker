package com.user.checker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.user.checker.import_package.Board;
import com.user.checker.import_package.Index;

import java.util.ArrayList;


public class CheckerBoard extends FrameLayout implements View.OnClickListener{

    Paint paint1;
    Paint paint2;
    Paint paint3;


    public Board board;
    public boolean isTracking = false;
    public int trackI;
    public int trackJ;

    public static int REC_LENGTH = 120;
    public static int TOTAL_LENGTH = 960;
    public boolean isReverseView = false;

    public static CheckerBoard attachNewCheckerBoard(FrameLayout frameLayout){
        CheckerBoard checkerBoard = new CheckerBoard(frameLayout.getContext(),null);
        frameLayout.addView(checkerBoard,frameLayout.getLayoutParams());
        return checkerBoard;
    }

    public CheckerBoard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.measure(MeasureSpec.EXACTLY,MeasureSpec.EXACTLY);
        this.setForegroundGravity(Gravity.CENTER);
        paint1 = new Paint();
        paint2 = new Paint();
        paint3 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        paint2.setStyle(Paint.Style.FILL);
        paint3.setStyle(Paint.Style.FILL);
        paint1.setColor(Color.argb(116,0,110,0));
        paint2.setColor(Color.argb(116,0,0,110));
        paint3.setColor(Color.LTGRAY);
    }

    private void initSize(){
        if(this.getLayoutParams() != null) {
            ViewGroup.LayoutParams params = getLayoutParams();
            TOTAL_LENGTH = Math.min(params.width, params.height);
            REC_LENGTH = TOTAL_LENGTH / 8;
            params.width = TOTAL_LENGTH;
            params.height = TOTAL_LENGTH;
        }else
            throw new RuntimeException();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        for(int i=0;i<8;i++)
            for(int j=0;j<8;j++)
                canvas.drawRect(REC_LENGTH*i,REC_LENGTH*j,REC_LENGTH*(i+1),REC_LENGTH*(j+1),(j+i)%2==0?paint1:paint2);

        if(isTracking) {
            ArrayList<Index> indexes = board.movablelist.getMovableList(trackI, trackJ);
            if(indexes != null)
                for (Index index : indexes) {
                    if(isReverseView)
                        canvas.drawCircle((7-index.y) * REC_LENGTH+REC_LENGTH/2, (7-index.x) * REC_LENGTH+REC_LENGTH/2, REC_LENGTH / 2, paint3);
                    else
                        canvas.drawCircle(index.y * REC_LENGTH+REC_LENGTH/2, index.x * REC_LENGTH+REC_LENGTH/2, REC_LENGTH / 2, paint3);
                }
        }
        super.dispatchDraw(canvas);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imageButton_redo:
                board.redo();
                break;
            case R.id.imageButton_undo:
                board.undo();
                break;
        }
    }

}
