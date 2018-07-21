package com.user.checker;

import android.os.Handler;
import android.widget.Toast;

import com.user.checker.import_package.Board;
import com.user.checker.models.CommModel;
import com.user.checker.models.ReceivePacket;
import com.user.checker.models.SendPacket;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class NetworkManager extends Thread {

    Board board;
    Socket socket;
    public Queue<CommModel> msgQueue;
    int flag;
    public String s_id;
    public String s_id2 = "";

    String playerName = "";

    String disconnectMsg = "(연결해제)";

    SendPacket sendPacket;
    ReceivePacket receivePacket;

    PrintStream printStream;
    InputStream inputStream;

    private int connection_fail_count = 0;

    public enum State {
        Waiting,
        Ready,
        Connected,
        Terminated
    }

    public State state;
    public final static String SERVER_IP = "118.36.72.159";
    public final static int SERVER_FORT = 8100;
    MultiGameActivity multiGameActivity;
    boolean isHost;
    Handler mainHandler;


    public NetworkManager(MultiGameActivity multiGameActivity, boolean isHost, Handler mainHandler) {
        this.multiGameActivity = multiGameActivity;
        this.s_id = multiGameActivity.s_id;
        this.s_id2 = multiGameActivity.s_id2;
        this.isHost = isHost;
        this.mainHandler = mainHandler;
        this.state = State.Waiting;
        msgQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        try {
            socket = new Socket(SERVER_IP, SERVER_FORT);


            while (!this.state.equals(State.Terminated) || connection_fail_count > 8) {
                if (state.equals(State.Waiting)) {
                    if(isHost) {
                        sendPacket = new SendPacket(s_id, "is_matched", null);
                        receivePacket = sendMsg(sendPacket);
                        if (receivePacket.state.equals("Ready"))
                            statusNotify("플레이어를 기다리는 중입니다...");
                        else if (receivePacket.state.equals("OK")) {
                            flag = receivePacket.enemy.flag;
                            s_id2 = receivePacket.enemy.s_id;
                            state = State.Ready;
                        }
                    }else{
                        CommModel commModel = new CommModel(0,s_id2,"");
                        sendPacket = new SendPacket(s_id, "join",commModel);
                        receivePacket = sendMsg(sendPacket);
                        if(receivePacket.state.equals("")||receivePacket.state.equals("already started")) {
                            disconnectMsg = "이미 시작되거나 유효하지 않은 방";
                            multiGameActivity.finish();
                        }else if(receivePacket.state.equals("OK")){
                            statusNotify("호스트플레이어를 깨우는 중입니다.");
                            flag = (receivePacket.enemy.flag==1)?0:1;
                            s_id2 = receivePacket.enemy.s_id;
                            state = State.Ready;
                        }else
                            multiGameActivity.finish();
                    }
                }else if(state.equals(State.Ready)){
                    if(isHost){
                        if(board == null)
                            mainHandler.post(new PlayerNotifier(s_id2,flag));
                        state = State.Connected;
                    }else{
                        CommModel commModel = new CommModel(0,s_id2,"");
                        sendPacket = new SendPacket(s_id,"send",commModel);
                        receivePacket = sendMsg(sendPacket);
                        if(!receivePacket.receive.isEmpty()) {
                            mainHandler.post(new PlayerNotifier(s_id2, flag));
                            state = State.Connected;
                        }else{
                            connection_fail_count++;
                        }
                    }
                }else if (state.equals(State.Connected)) {
                    if(connection_fail_count > 4) {
                        disconnectMsg = "상대방과의 연결이 끊겼습니다.";
                        multiGameActivity.finish();
                    }
                    else
                        if(msgQueue.isEmpty())
                            communication(new CommModel(0,s_id2,""));
                        else
                            while(!msgQueue.isEmpty()) {
                                CommModel c = msgQueue.remove();
                                communication(c);
                            }

                }
                Thread.sleep(300);
            }
            Thread.sleep(60000);
            multiGameActivity.finish();
        }catch(InterruptedException e1) {
            e1.printStackTrace();
            final String cause = disconnectMsg==null?"(연결해제)":disconnectMsg;
            try {
                socket.close();
                mainHandler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(multiGameActivity.getApplicationContext(),"접속종료 : " + cause,Toast.LENGTH_SHORT).show();}
                });
            } catch (IOException e) {}
        }catch(ConnectException e){
            multiGameActivity.finish();
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    private ReceivePacket sendMsg(SendPacket sendPacket) throws IOException, JSONException, InterruptedException {
        printStream = new PrintStream(socket.getOutputStream());
        String send = sendPacket.jsonSerialize();
        String receive = "";
        printStream.print(send);
        printStream.flush();
        for(int conn_count = 0; conn_count<100000; conn_count++){
            inputStream = socket.getInputStream();
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            receive = new String(b);
            if(!receive.isEmpty()){
                return new ReceivePacket(receive);
            }
            Thread.sleep(300);
        }
        throw new IOException();
    }

    public void statusNotify(String msg){
        mainHandler.post(new StatusNotifier(msg));
    }

    public class StatusNotifier implements Runnable{
        public String msg;
        private StatusNotifier(String msg){
            this.msg = msg;
        }
        @Override
        public void run() {
            multiGameActivity.textView_multi_status.setText(msg);
        }
    }

    public class PlayerNotifier implements Runnable{
        public String s_id;
        public int flag;

        public PlayerNotifier(String s_id, int flag) {
            this.s_id = s_id;
            this.flag = flag;
        }

        @Override
        public void run() {
            Board.Player player;
            if(flag == 1) {
                player = Board.Player.Red;
                multiGameActivity.checkerBoard.isReverseView = true;
            }
            else {
                player = Board.Player.White;
                multiGameActivity.checkerBoard.isReverseView = false;
            }

            board = new Board(multiGameActivity.getApplicationContext(),multiGameActivity.checkerBoard,multiGameActivity);
            board.multi_player = player;
            playerName = player.name() + " 플레이어";
            multiGameActivity.imageView_multi_myStone.setImageResource(CheckerWidget.getImage(player));
            multiGameActivity.imageView_multi_enemyStone.setImageResource(CheckerWidget.getImage(player.getEnemy()));
            multiGameActivity.imageView_multi_turn.setImageResource(CheckerWidget.getImage(board.getTurn()));
            multiGameActivity.textView_multi_myName.setText("player : " + player.name() + " 플레이어");
            multiGameActivity.textView_multi_enemyName.setText("player : " + player.getEnemy().name() + " 플레이어");
            multiGameActivity.textView_multi_enemySid.setText("s_id : " + s_id2);
        }
    }

    private void communication(CommModel c) throws JSONException, InterruptedException, IOException {
        sendPacket = new SendPacket(s_id, "send",c);
        receivePacket = sendMsg(sendPacket);
        if(receivePacket.receive.isEmpty())
            connection_fail_count++;
        else
            while(!receivePacket.receive.isEmpty()){
                CommModel commModel = receivePacket.receive.remove();
                mainHandler.post(new ActionNotifier(commModel));
                connection_fail_count = 0;
            }
    }

    public class ActionNotifier implements Runnable{
        CommModel commModel;

        public ActionNotifier(CommModel commModel) {
            this.commModel = commModel;
        }

        @Override
        public void run() {
            if(commModel.move!=0){
                int move = commModel.move;
                int i1 = move % 8;
                move = (int)(move / 8);
                int j1 = move % 8;
                move = (int)(move / 8);
                int i2 = move % 8;
                move = (int)(move / 8);
                int j2 = move % 8;
                board.move(i1,j1,i2,j2);
            }
            if(!commModel.chat.isEmpty()){
                multiGameActivity.editText_multi_chattingArea.append(commModel.chat + "\n");
            }
        }
    }

}
