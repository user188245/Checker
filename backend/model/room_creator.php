<?php
    include 'core/db.php';
    include 'model/base_model.php';

    class RoomCreator extends BaseModel{

        function __construct(){
            $this->createDB();
        }

    	function insert_room($s_id){
            if($this->db->not_connected()){
    			$this->errorCode = 1000;
    			$this->cause = "DB Connection failed";
    		}else{
                $result = $this->db->getUser($s_id);
    			$datetime = time() + 10;
    			$date_result = date_create();
    			date_timestamp_set($date_result, $datetime);
    			$time = date_format($date_result, "Y-m-d H:i:s");
    			$flag = rand(0,1);
                $this->db->deleteRoomBySID($s_id);
    			if(mysqli_fetch_array($result) == null){
    				$this->errorCode = 620;
    				$this->cause = "Invalid s_id. Please reinstall this application.";
    			}
                else if(!$this->db->createRoom($s_id,$time,$flag)){
    				$this->errorCode = 621;
    				$this->cause = "Fatal Error : Request failed. Check if(s_id is valid/room is already created/db index is max.)";
    			}
    		}
    	}
    }

?>
