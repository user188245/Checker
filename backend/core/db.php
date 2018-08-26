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

        function deleteRoomBySID($s_id){
            mysqli_query($this->db, "DELETE from room WHERE s_id = '$s_id'");
            mysqli_query($this->db, "ALTER TABLE room AUTO_INCREMENT = 1");
        }

        function deleteRoomByExpiredDate(){
            return mysqli_query($this->db, "DELETE FROM room WHERE expiration_date <= CURRENT_TIMESTAMP");
        }

        function createRoom($s_id,$time,$flag){
            return mysqli_query($this->db, "INSERT INTO room VALUES (0, '$s_id', '$time', 0, null, $flag)");
        }

        function createSID($s_id){
            mysqli_query($this->db, "INSERT INTO user VALUES ('$s_id',CURRENT_TIMESTAMP)");
        }

    }

?>
