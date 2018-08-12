package com.user.checker.import_package;

/**
 * Created by user on 2017-06-04.
 */

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;

import com.user.checker.CheckerBoard;
import com.user.checker.CheckerWidget;
import com.user.checker.import_package.Board.Direction;
import com.user.checker.import_package.Board.Player;

public class Sprite implements Cloneable{
    private Player owner;
    private Type type;
    public Index index;
    public Context context;

    public CheckerWidget getWidget() {
        return widget;
    }

    private CheckerWidget widget;
    private Board board;
    private MovableField movableField = null;




    @Override
    protected Sprite clone(){
        return new Sprite(context,type,owner,index.x,index.y, board);
    }

    public void refreshMovableField(int i, int j, Board board){
        movableField = new MovableField(i,j,board);
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isCatchable(){
        return movableField.isCatchable;
    }

    public ArrayList<Index> getMovableList(){
        return movableField.list;
    }

    public Type getType() {
        return type;
    }

    public void upgrade(){
        type = Type.King;
        widget.notifyInitIndex();
    }

    public void downgrade(){
        type = Type.Normal;
        widget.notifyInitIndex();
    }

    public void kill(){
        board.movableSprite.remove(this);
        this.widget.destroy();
        this.widget = null;
        index = new Index(-1,-1);
        type = Type.Void;
        owner = null;
    }

    public Board getBoard() {
        return board;
    }

    public enum Type{
        Normal,
        King,
        Void
    }

    public boolean isBlock(){
        return type.equals(Type.Normal)||type.equals(Type.King);
    }

    @Override
    public String toString(){
        switch(type){
            case Normal:
                if(owner.equals(Player.Red))
                    return "o";
                else
                    return "x";
            case King:
                if(owner.equals(Player.Red))
                    return "O";
                else
                    return "X";
            default:
                return "_";
        }
    }

    public void moveTo(int i, int j){
        index.x = i;
        index.y = j;
        widget.notifyInitIndex();
    }

    public Sprite(Context context, Type type, Player player, int i, int j, Board board){

        this.context = context;
        this.type = type;
        this.owner = player;
        this.board = board;
        this.index = new Index(i,j);
        if(!this.type.equals(Type.Void)) {
            this.widget = new CheckerWidget(context, null, this);
            this.board.attachedLayout.addView(this.widget,new ViewGroup.LayoutParams(CheckerBoard.REC_LENGTH,CheckerBoard.REC_LENGTH));
            this.widget.notifyInitIndex();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }


    public class MovableField{

        private ArrayList<Index> list;

        private boolean isCatchable;

        public MovableField(int i,int j,Board board){
            isCatchable = false;
            Sprite[][] field = board.getBoard();
            ArrayList<Index> list = new ArrayList<>();
            if(field[i][j].getType().equals(Type.Normal)){

                if(field[i][j].getOwner().equals(Player.White)){
                    boolean a = isCatchable(i,j,board,Direction.LeftUp,list);
                    boolean b = isCatchable(i,j,board,Direction.RightUp,list);
                    if(!(a||b)){
                        addMovable(i,j,board,Direction.LeftUp,list);
                        addMovable(i,j,board,Direction.RightUp,list);
                    }
                }
                else{
                    boolean a = isCatchable(i,j,board,Direction.LeftDown,list);
                    boolean b = isCatchable(i,j,board,Direction.RightDown,list);
                    if(!(a||b)){
                        addMovable(i,j,board,Direction.LeftDown,list);
                        addMovable(i,j,board,Direction.RightDown,list);
                    }
                }
            }
            else if(field[i][j].getType().equals(Type.King)){
                boolean a = isCatchable(i,j,board,Direction.LeftUp,list);
                boolean b = isCatchable(i,j,board,Direction.RightUp,list);
                boolean c = isCatchable(i,j,board,Direction.LeftDown,list);
                boolean d = isCatchable(i,j,board,Direction.RightDown,list);
                if(!(a||b||c||d)){
                    addMovable(i,j,board,Direction.LeftUp,list);
                    addMovable(i,j,board,Direction.RightUp,list);
                    addMovable(i,j,board,Direction.LeftDown,list);
                    addMovable(i,j,board,Direction.RightDown,list);
                }
            }
            this.list = list;
        }
        private boolean isCatchable(int i, int j,Board b, Direction d, ArrayList<Index> list){
            Sprite[][] board = b.getBoard();
            int modI = d.get_i_val();
            int modJ = d.get_j_val();
            if(i+modI*2>=0 && i+modI*2<8 && j+modJ*2>=0 && j+modJ*2<8 &&
                    board[i][j].isBlock() && board[i+modI][j+modJ].isBlock() && !board[i+modI*2][j+modJ*2].isBlock() &&
                    !board[i][j].getOwner().equals(board[i+modI][j+modJ].getOwner())){
                list.add(new Index(i+modI*2,j+modJ*2));
                this.isCatchable = true;
                return true;
            }

            return false;
        }
        private void addMovable(int i, int j,Board b, Direction d, ArrayList<Index> list){
            Sprite[][] board = b.getBoard();
            int modI = d.get_i_val();
            int modJ = d.get_j_val();
            if(i+modI>=0 && i+modI<8 && j+modJ>=0 && j+modJ<8 &&
                    board[i][j].isBlock() && !board[i+modI][j+modJ].isBlock()){
                list.add(new Index(i+modI,j+modJ));
            }

        }

    }


}
