<?php
    class Database{
        private $host = "localhost";
        private $user = "admin";
        private $password = "admin";
        private $database = "admin";
        private $private = "e509186dab3609982e91afec24e94dea";
        private $db = null;

        function __construct(){
            $this->db = mysqli_connect($this->host,$this->user,$this->password,$this->database);
        }

        function not_connected(){
            return mysqli_connect_errno($this->db);
        }

        function getPrivate(){
            return $this->private;
        }

        function getUser($s_id){
            return mysqli_query($this->db, "SELECT * FROM user WHERE s_id = '$s_id'");
        }

        function getMatchedRoom($s_id){
            return mysqli_query($this->db, "SELECT room_id,s_id FROM room WHERE is_matched = 0 AND s_id != '$s_id' ORDER BY room_id");
        }

        function getMatchedInfo($s_id){
            return mysqli_query($this->db, "SELECT is_matched,s_id2,flag FROM room WHERE s_id = '$s_id'");
        }

        function deleteRoomBySID($s_id){
            return mysqli_query($this->db, "DELETE FROM room WHERE s_id = '$s_id'");
        }

        function deleteRoomByExpiredDate(){
            return mysqli_query($this->db, "DELETE FROM room WHERE expiration_date <= CURRENT_TIMESTAMP");
        }

        function deleteQueueBySID($s_id){
            return mysqli_query($this->db, "DELETE FROM game_queue where s_id = '$s_id'");
        }

        function createRoom($s_id,$time,$flag){
            return mysqli_query($this->db, "INSERT INTO room VALUES (0, '$s_id', '$time', 0, null, $flag)");
        }

        function createSID($s_id){
            return mysqli_query($this->db, "INSERT INTO user VALUES ('$s_id',CURRENT_TIMESTAMP)");
        }

        function enqueueAction($s_id,$move,$chat){
            return mysqli_query($this->db, "INSERT INTO game_queue VALUES ('$s_id', '$move', '$chat')");
        }

        function getqueueAction($s_id){
            return mysqli_query($this->db, "SELECT move,chat FROM game_queue WHERE s_id = '$s_id'");
        }

        function updateMatchState($s_id,$s_id2){
            return mysqli_query($this->db, "UPDATE room SET is_matched = '1', s_id2 = '$s_id2' WHERE s_id = '$s_id'");
        }

        function updateRoomTime($s_id,$time){
            return mysqli_query($this->db, "UPDATE room SET expiration_date = '$time' WHERE s_id = '$s_id'");
        }
    }

?>
