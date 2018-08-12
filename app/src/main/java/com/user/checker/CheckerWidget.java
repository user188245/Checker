package com.user.checker;


        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Paint;
        import android.support.annotation.Nullable;
        import android.util.AttributeSet;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import com.user.checker.import_package.Board;
        import com.user.checker.import_package.Sprite;

public class CheckerWidget extends View implements View.OnTouchListener{

    private CheckerBoard checkerBoard;


    private int REC_LENGTH = 0;

    //i,j -> initX,initY함수이다
    private int convertIndex(int i){
        if(checkerBoard.isReverseView)
            return REC_LENGTH*(7-i);
        else
            return REC_LENGTH*i;
    }

    public boolean isMovable = false;

    public final static int RED_KING_IMG = R.drawable.red_king;
    public final static int RED_NORMAL_IMG = R.drawable.red_normal;
    public final static int WHITE_KING_IMG = R.drawable.white_king;
    public final static int WHITE_NORMAL_IMG = R.drawable.white_normal;

    private static Bitmap RED_KING_BITMAP;
    private static Bitmap RED_NORMAL_BITMAP;
    private static Bitmap WHITE_KING_BITMAP;
    private static Bitmap WHITE_NORMAL_BITMAP;

    public static int getImage(Board.Player player){
        if(player.equals(Board.Player.White))
            return WHITE_NORMAL_IMG;
        else
            return RED_NORMAL_IMG;
    }

    //객체 비트맵
    Bitmap img = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round);

    //컬러필터용
    Paint paint;

    //고정위치좌표 - x,y는 각각 i,j에 Functional Dependent 하다. decode(i,j) = x,y 라는 함수를 어떻게 만들지?
    public int initX = 0;
    public int initY = 0;

    //임시좌표 - 이는 내가 건들 필요도 없다. 가만 냅두자.

    //상위소속 스프라이트
    private Sprite sprite;

    //Board[i][j]에 마스터 Sprite가 존재함을 의미, 이것에 따라 고정위치 좌표가 바뀐다.
    int i = 0;
    int j = 0;

    //스프라이트로 부터 생성되는 생성자


    public CheckerWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckerWidget(Context context, @Nullable AttributeSet attrs, Sprite superSprite) {
        super(context,attrs);
        this.sprite = superSprite;
        this.REC_LENGTH = CheckerBoard.REC_LENGTH;
        checkerBoard = this.sprite.getBoard().attachedLayout;
        this.setOnTouchListener(this);
        paint = new Paint();
        paint.setAlpha(150);
    }

    private Bitmap buildRedKing(){
        return (RED_KING_BITMAP != null)?RED_KING_BITMAP:(RED_KING_BITMAP = BitmapFactory.decodeResource(getResources(), RED_KING_IMG));
    }
    private Bitmap buildRedNormal(){
        return (RED_NORMAL_BITMAP != null)?RED_NORMAL_BITMAP:(RED_NORMAL_BITMAP = BitmapFactory.decodeResource(getResources(), RED_NORMAL_IMG));
    }
    private Bitmap buildWhiteKing(){
        return (WHITE_KING_BITMAP != null)?WHITE_KING_BITMAP:(WHITE_KING_BITMAP = BitmapFactory.decodeResource(getResources(), WHITE_KING_IMG));
    }
    private Bitmap buildWhiteNormal(){
        return (WHITE_NORMAL_BITMAP != null)?WHITE_NORMAL_BITMAP:(WHITE_NORMAL_BITMAP = BitmapFactory.decodeResource(getResources(), WHITE_NORMAL_IMG));
    }

    // x,y로 객체의 좌표를 이동시킴.( 짧은 시간, dt초 동안 n->∞ 인 n번 그려진다고 생각하자. )
    @Override
    protected void onDraw(Canvas canvas) {
        if(isMovable) {
            canvas.drawBitmap(img, 0, 0, null);
        }
        else
            canvas.drawBitmap(img,0,0,paint);
    }

    private float point_x, point_y;
    private int move_x, move_y;

    public void notifyInitIndex(){
        if(img != null && !img.isRecycled())
            img.recycle();
        this.i = sprite.index.x;
        this.j = sprite.index.y;
        if(sprite.getOwner().equals(Board.Player.White)){
            if(sprite.getType().equals(Sprite.Type.Normal))
                this.img = Bitmap.createScaledBitmap(buildWhiteNormal(),REC_LENGTH,REC_LENGTH,true);
            else
                this.img = Bitmap.createScaledBitmap(buildWhiteKing(),REC_LENGTH,REC_LENGTH,true);
        }else {
            if (sprite.getType().equals(Sprite.Type.Normal))
                this.img = Bitmap.createScaledBitmap(buildRedNormal(),REC_LENGTH,REC_LENGTH,true);
            else
                this.img = Bitmap.createScaledBitmap(buildRedKing(),REC_LENGTH,REC_LENGTH,true);
        }
        initY = this.convertIndex(i);
        initX = this.convertIndex(j);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.getLayoutParams();
        params.leftMargin = initX;
        params.topMargin = initY;
        this.setLayoutParams(params);
    }

    //디스트로이 반드시 객체소멸시 recycle()이라는 메소드를 사용해야함.
    public void destroy(){
        try {
            this.img.recycle();
            this.checkerBoard.removeView(this);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Board b = sprite.getBoard();
        if (sprite.getOwner().equals(b.getTurn()) && (!b.isMulti || b.multi_player.equals(b.getTurn()))) {
            float x = event.getRawX();
            float y = event.getRawY();

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                point_x = x;
                point_y = y;
                checkerBoard.isTracking = true;
                checkerBoard.trackI = i;
                checkerBoard.trackJ = j;
                move_x = params.leftMargin;
                move_y = params.topMargin;
                this.bringToFront();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float weightX = point_x - x;
                float weightY = point_y - y;
                params.leftMargin = move_x - (int) weightX;
                params.topMargin = move_y - (int) weightY;
                v.setLayoutParams(params);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                checkerBoard.isTracking = false;
                if(checkerBoard.isReverseView)
                    sprite.getBoard().move(i, j, 7- (params.topMargin + REC_LENGTH / 2) / REC_LENGTH, 7- (params.leftMargin + REC_LENGTH / 2) / REC_LENGTH);
                else
                    sprite.getBoard().move(i, j, (params.topMargin + REC_LENGTH / 2) / REC_LENGTH, (params.leftMargin + REC_LENGTH / 2) / REC_LENGTH);
                params.leftMargin = initX;
                params.topMargin = initY;
                v.setLayoutParams(params);
            }
            //갱신.
            v.invalidate();
            return true;
        }
        return false;
    }
}
