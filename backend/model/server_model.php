<?php
    require 'model/base_model.php';
    require 'core/db.php';

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

    	function __construct(){
    		$this->state = "";
    		$this->receive = "[]";
    		$this->enemy = new PlayerInfo;
            $this->createDB();
    	}

    	function join($s_id,$s_id2){
    		if($this->db->not_connected()){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    			throw new Exception;
    		}else{
    			$row = mysqli_fetch_array($this->db->getMatchedInfo($s_id));
    			if($row['is_matched'] == 1)
    				$this->state = "already started";
    			else{
    				if(!$this->db->updateMatchState($s_id,$s_id2)){
    					$this->errorCode = 68;
    					$this->cause = "Missing room error";
    					throw new Exception;
    				}else{
    					$this->state = "OK";
    					$this->enemy = new PlayerInfo;
    					$this->enemy->s_id = $s_id;
    					$this->enemy->flag = $row['flag'];
                        $this->db->deleteQueueBySID($s_id2);
    				}
    			}
    		}
    	}

    	function matched($s_id){
    		if($this->db->not_connected()){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    			throw new Exception;
    		}else{
    			$row = mysqli_fetch_array($this->db->getMatchedInfo($s_id));
    			if($row['is_matched'] == 0 || $row['s_id2'] == null){
    				$this->state = "Ready";
    				$time = $this->getFreshTime();
                    $this->db->updateRoomTime($s_id,$time);
    			}
    			else{
    				$this->state = "OK";
    				$this->enemy = new PlayerInfo;
    				$this->enemy->s_id = $row['s_id2'];
    				$this->enemy->flag = $row['flag'];
                    $this->db->deleteQueueBySID($s_id);
    			}
    		}
    	}

    	function inqueue($s_id,$move,$chat){
    		if($this->db->not_connected()){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    			throw new Exception;
    		}else{
    			if(!$this->db->enqueueAction($s_id,$move,$chat)){
    				$this->errorCode = 621;
    				$this->cause = "Fatal Error : server db is closed.";
    				throw new Exception;
    			}
    		}
    	}

    	function getqueue($s_id){
    		if($this->db->not_connected()){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    			throw new Exception;
    		}else{
    			$queue = Array();
    			$result = $this->db->getqueueAction($s_id);
    			while($row = mysqli_fetch_array($result)){
    				$queue[] = array("move"=>$row['move'] ,"target"=>"", "chat"=>$row['chat']);
    			}
    			$this->db->deleteQueueBySID($s_id);
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
