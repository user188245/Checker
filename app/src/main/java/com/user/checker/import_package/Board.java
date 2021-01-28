package com.user.checker.import_package;



import android.app.Activity;
import android.content.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import com.user.checker.CheckerBoard;
import com.user.checker.CheckerWidget;
import com.user.checker.MultiGameActivity;
import com.user.checker.NetworkManager;
import com.user.checker.SingleGameActivity;
import com.user.checker.import_package.Sprite.Type;
import com.user.checker.models.CommModel;

public class Board {

    private Activity viewFamily;

    public CheckerBoard attachedLayout;

    public MovableList movablelist;

    private int turnCount;

    private boolean onGame = true;

    private int nowSequence;

    private int capacity;

    private Player turn;

    public Player multi_player = Player.White;

    public boolean isMulti = false;

    private Sprite board[][];

    private ArrayList<MoveSequenceValue> moveSequence;

    public Context context;

    public LinkedList<Sprite> movableSprite;

    public Board(Context context, CheckerBoard attachedLayout,  Activity activity) {
        this(context,attachedLayout,new ArrayList<MoveSequenceValue>(),activity);
    }

    public Board(Context context, CheckerBoard attachedLayout, ArrayList<MoveSequenceValue> moveSequence, Activity activity){
        this.context = context;
        this.attachedLayout = attachedLayout;
        this.attachedLayout.board = this;
        turnCount = 0;
        turn = Player.White;
        movableSprite = new LinkedList<>();
        board = createNewBoard();
        viewFamily = activity;
        if(activity instanceof SingleGameActivity) {
            isMulti = false;
            viewFamily = activity;
        }
        else if(activity instanceof MultiGameActivity) {
            isMulti = true;
            viewFamily = activity;
        }else{
            throw new RuntimeException("Unknown game type");
        }

        movablelist = new MovableList(turn);
        this.moveSequence = moveSequence;
        nowSequence = 0;
        capacity = moveSequence.size();
        refreshMovableSprite();
        notifyView();
    }

    public Sprite[][] getBoard(){
        return board;
    }

    public Player getTurn() {
        return turn;
    }

    public ArrayList<MoveSequenceValue> getMoveSequence() {
        return moveSequence;
    }

    public class MovableList{
        private boolean isCatchable;

        public MovableList(Player player){
            isCatchable = false;
            for(int i=0; i<board.length; i++){
                for(int j=1-i%2; j<board[i].length; j=j+2){
                    if(board[i][j].getOwner() != null){
                        if(board[i][j].getOwner().equals(player)){
                            board[i][j].refreshMovableField(i,j,Board.this);
                            if(board[i][j].isCatchable())
                                isCatchable = true;
                        }
                    }
                }
            }
        }
        public ArrayList<Index> getMovableList(int i,int j){
            try{
                if(isCatchable){
                    if(board[i][j].isCatchable())
                        return board[i][j].getMovableList();
                    else
                        return new ArrayList<Index>();
                }else{
                    return board[i][j].getMovableList();
                }

            }catch(NullPointerException e){
                return null;
            }

        }

    }

    public enum Player{
        Red,
        White;
        public Player getEnemy(){
            return (this.equals(Red))?White:Red;
        }
    }

    public enum Direction{
        LeftUp,
        RightUp,
        LeftDown,
        RightDown;

        public int get_i_val(){
            return (equals(LeftDown)||equals(RightDown))?1:-1;
        }

        public int get_j_val(){
            return (equals(RightUp)||equals(RightDown))?1:-1;
        }


    }




    private Sprite[][] createNewBoard() {
        Sprite[][] board = new Sprite[8][8];
        for(int i=0; i<board.length; i++){
            Arrays.fill(board[i], new Sprite(context,Type.Void,null,-1,-1, this));
            for(int j=1-i%2; j<board[i].length; j=j+2){
                if(i<3) {
                    board[i][j] = new Sprite(context, Type.Normal, Player.Red, i, j, this);
                    movableSprite.add(board[i][j]);
                }
                else if(i>4) {
                    board[i][j] = new Sprite(context, Type.Normal, Player.White, i, j, this);
                    movableSprite.add(board[i][j]);
                }
            }
        }
        return board;
    }

    private int leftMovableSprite = 12;

    public void refreshMovableSprite(){
        leftMovableSprite = 0;
        for(Sprite s : movableSprite) {
            if(!s.getOwner().equals(turn)){
                s.getWidget().isMovable = true;
            }else {
                ArrayList<Index> movables = movablelist.getMovableList(s.index.x, s.index.y);
                if (movables != null && movables.size() > 0) {
                    s.getWidget().isMovable = true;
                    leftMovableSprite++;
                }
                else
                    s.getWidget().isMovable = false;
            }
            s.getWidget().invalidate();
        }
    }

    public void move(int i1, int j1, int i2, int j2){
        move(i1, j1, i2, j2, true);
    }


    public void move(int i1, int j1, int i2, int j2, boolean isKeepMode){
        if(!board[i1][j1].isBlock())
            return;
        if(!turn.equals(board[i1][j1].getOwner()))
            return;
        boolean catchable = movablelist.isCatchable;
        boolean isUpgraded = false;
        if(movablelist.getMovableList(i1, j1).isEmpty()){
            if(catchable)
                setStatusText("You must catch enemy's stone before move");
            return;
        }
        if(!movablelist.getMovableList(i1, j1).contains(new Index(i2,j2)))
            return;
        boolean turnChangeMode = false;
        board[i1][j1].moveTo(i2,j2);
        Sprite temp = board[i1][j1];
        board[i1][j1] = board[i2][j2];
        board[i2][j2] = temp;
        if(isMulti){
            NetworkManager networkManager = ((MultiGameActivity)viewFamily).networkManager;
            putInQueue(networkManager.msgQueue,networkManager.s_id2,i1,i2,j1,j2);
        }
        if(board[i2][j2].getType().equals(Type.Normal) && (turn.equals(Player.White) && i2 == 0 || turn.equals(Player.Red) && i2 == 7)){
            board[i2][j2].upgrade();
            isUpgraded = true;
        }
        Type enemyType = null;
        if(board[i2][j2].isCatchable()){
            Sprite enemy = board[(i1+i2)/2][(j1+j2)/2];
            enemyType = enemy.getType();
            enemy.kill();
            movablelist = new MovableList(turn);
            if(!movablelist.isCatchable){
                turnChangeMode = true;
            }else{
                setStatusText("You can catch enemies one more time");
            }
        }
        else{
            turnChangeMode = true;
        }
        if(isKeepMode){
            if(nowSequence < capacity){
                capacity = nowSequence;
            }
            MoveSequenceValue v = new MoveSequenceValue(new Index(i1,j1),new Index(i2,j2),turn,catchable,isUpgraded,enemyType);
            moveSequence.add(nowSequence++,v);
            capacity++;
            while(capacity < moveSequence.size())
                moveSequence.remove(capacity);
        }
        if(turnChangeMode){
            turnChange();
        }
        movablelist = new MovableList(turn);
        refreshMovableSprite();
        if(leftMovableSprite == 0){
            setStatusText(turn.getEnemy() + "won!, Game is over");
            this.onGame = false;
        }
    }

    private void turnChange(){
        turn = turn.getEnemy();
        turnCount++;
        notifyView();
    }

    public void undo(){
        if(moveSequence.isEmpty() || nowSequence == 0){
            return;
        }
        MoveSequenceValue v = moveSequence.get(--nowSequence);
        int i1 = v.afterMove.x;
        int j1 = v.afterMove.y;
        int i2 = v.beforeMove.x;
        int j2 = v.beforeMove.y;
        if(!turn.equals(v.player))
            turnCount--;
        if(v.enemyRemoved) {
            board[(i1 + i2) / 2][(j1 + j2) / 2] = new Sprite(context, v.enemyType, v.player.getEnemy(), (i1 + i2) / 2, (j1 + j2) / 2, this);
            movableSprite.add(board[(i1 + i2) / 2][(j1 + j2) / 2]);
        }
        if(v.isUpgraded){
            board[i1][j1].downgrade();
        }
        this.turn = v.player;
        board[i1][j1].moveTo(i2,j2);
        Sprite temp = board[i1][j1];
        board[i1][j1] = board[i2][j2];
        board[i2][j2] = temp;
        movablelist = new MovableList(turn);
        this.onGame = true;
        refreshMovableSprite();
        notifyView();
    }

    public void redo(){
        if(moveSequence.isEmpty() || nowSequence == capacity){
            return;
        }
        MoveSequenceValue v = moveSequence.get(nowSequence++);
        move(v.beforeMove.x,v.beforeMove.y,v.afterMove.x,v.afterMove.y,false);
        notifyView();
    }

    public void notifyView(){
        if(onGame)
            setStatusText(turn.name() + " Turn");
        if(isMulti){
            MultiGameActivity multiGameActivity = (MultiGameActivity)viewFamily;
            multiGameActivity.imageView_multi_turn.setImageResource(CheckerWidget.getImage(turn));
        }else{

            SingleGameActivity singleGameActivity = (SingleGameActivity)viewFamily;
            if(moveSequence.isEmpty() || nowSequence == capacity)
                singleGameActivity.imageButton_redo.setAlpha(0.2f);
            else
                singleGameActivity.imageButton_redo.setAlpha(1f);
            if(moveSequence.isEmpty() || nowSequence == 0)
                singleGameActivity.imageButton_undo.setAlpha(0.2f);
            else
                singleGameActivity.imageButton_undo.setAlpha(1f);

            singleGameActivity.imageView_turn.setImageResource(CheckerWidget.getImage(turn));
            singleGameActivity.textView_turnCount.setText(String.valueOf(turnCount));
        }
    }

    private void setStatusText(String text){
        if(!this.isMulti){
            ((SingleGameActivity)this.viewFamily).textView_status.setText(text);
        }else{
            ((MultiGameActivity)this.viewFamily).textView_multi_status.setText(text);

        }
    }

    private void putInQueue(Queue<CommModel> queue,String s_id2,int i1,int i2,int j1,int j2){
        CommModel commModel = new CommModel(i1+j1*8+i2*64+j2*512,s_id2,"");
        queue.add(commModel);
    }


}

