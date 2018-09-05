<?php
    require 'core/db.php';
    require 'model/base_model.php';

    class RoomModel extends BaseModel{
    	public $rooms = "[]";

    	function __construct(){
    		$this->rooms = array();
            $this->createDB();
    	}

    	function get_room($s_id){
    		if($this->db->not_connected()){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    		}else{
    			$result = mysqli_fetch_array($this->db->getUser($s_id));
    			if($result == null){
    				$this->errorCode = 620;
    				$this->cause = "Invalid s_id. Please reinstall this application.";
    			}
    			else if(!$this->db->deleteRoomByExpiredDate()){
    				$this->errorCode = 621;
    				$this->cause = "Fatal Error : You tried to input invalid server_ip or port. Check client state if it is iligal version.";
    			}else{
    				$result = $this->db->getMatchedRoom($s_id);
    				while($row = mysqli_fetch_array($result)){
    					$this->rooms[] = array("room_id"=>$row['room_id'] , "s_id"=>$row['s_id']);
    				}
    			}
    		}
    	}
    }

?>
