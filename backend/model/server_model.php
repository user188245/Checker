<?php
    include 'model/base_model.php';
    include 'core/db.php';
    include 'core/data.php';

    class CommModel {
    	public $move = 0;
    	public $target = "";
    	public $chat = "";
    }

    class PlayerInfo {
    	public $flag = 0;
    	public $s_id = "";
    }

    class ServerModel extends BaseModel{
    	public $state;
    	public $receive;
    	public $enemy;

    	private $host = "localhost";
    	private $user = "admin";
    	private $password = "admin";
    	private $database = "admin";
    	private $db;

    	function __construct(){
    		$this->state = "";
    		$this->receive = "[]";
    		$this->enemy = new PlayerInfo;
    	}

    	function join($s_id,$s_id2){
    		$this->db = mysqli_connect($this->host,$this->user,$this->password,$this->database);
    		if(mysqli_connect_errno($this->db)){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    			throw new Exception;
    		}else{
    			$result = mysqli_query($this->db, "select is_matched,flag from room where s_id = '$s_id'");
    			$row = mysqli_fetch_array($result);
    			if($row['is_matched'] == 1)
    				$this->state = "already started";
    				else{
    					$result = mysqli_query($this->db, "update room set is_matched = '1', s_id2 = '$s_id2' where s_id = '$s_id'");
    					if(!$result){
    						$this->errorCode = 68;
    						$this->cause = "Missing room error";
    						throw new Exception;
    					}else{
    						$this->state = "OK";
    						$this->enemy = new PlayerInfo;
    						$this->enemy->s_id = $s_id;
    						$this->enemy->flag = $row['flag'];
    						mysqli_query($this->db, "DELETE FROM game_queue where s_id = '$s_id2'");
    						mysqli_query($this->db, "ALTER TABLE game_queue AUTO_INCREMENT = 1");
    				}

    			}
    		}
    	}

    	function matched($s_id){
    		$this->db = mysqli_connect($this->host,$this->user,$this->password,$this->database);
    		if(mysqli_connect_errno($this->db)){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    			throw new Exception;
    		}else{

    			$result = mysqli_query($this->db, "select is_matched,s_id2,flag from room where s_id = '$s_id'");
    			$row = mysqli_fetch_array($result);
    			if($row['is_matched'] == 0 || $row['s_id2'] == null){
    				$this->state = "Ready";
    				$time = $this->getFreshTime();
    				mysqli_query($this->db, "UPDATE room set expiration_date = '$time' where s_id = '$s_id'");
    			}
    			else{
    				$this->state = "OK";
    				$this->enemy = new PlayerInfo;
    				$this->enemy->s_id = $row['s_id2'];
    				$this->enemy->flag = $row['flag'];
    				mysqli_query($this->db, "DELETE FROM game_queue where s_id = '$s_id'");
    				mysqli_query($this->db, "ALTER TABLE game_queue AUTO_INCREMENT = 1");
    			}
    		}
    	}

    	function inqueue($s_id,$move,$chat){
    		$this->db = mysqli_connect($this->host,$this->user,$this->password,$this->database);
    		if(mysqli_connect_errno($this->db)){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    			throw new Exception;
    		}else{
    			if(!mysqli_query($this->db, "INSERT INTO game_queue VALUES ('$s_id', '$move', '$chat', null)")){
    				$this->errorCode = 621;
    				$this->cause = "Fatal Error : server db is closed.";
    				throw new Exception;
    			}
    		}
    	}

    	function getqueue($s_id){
    		$this->db = mysqli_connect($this->host,$this->user,$this->password,$this->database);
    		if(mysqli_connect_errno($this->db)){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    			throw new Exception;
    		}else{
    			$queue = Array();
    			$result = mysqli_query($this->db, "SELECT move,chat from game_queue where s_id = '$s_id' order by queue_index");
    			while($row = mysqli_fetch_array($result)){
    				$queue[] = array("move"=>$row['move'] ,"target"=>"", "chat"=>$row['chat']);
    			}
    			mysqli_query($this->db, "DELETE FROM game_queue where s_id = '$s_id'");
    			mysqli_query($this->db, "ALTER TABLE game_queue AUTO_INCREMENT = 1");
    			$this->state = "OK";
    			$this->receive = json_encode($queue);
    		}
    	}

    	function getFreshTime(){
    		$datetime = time() + 10;
    		$date_result = date_create();
    		date_timestamp_set($date_result, $datetime);
    		$time = date_format($date_result, "Y-m-d H:i:s");
    		return $time;
    	}
    }

?>
