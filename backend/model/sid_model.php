<?php
    require 'core/db.php';
    require 'model/base_model.php';

    class SidModel extends BaseModel {

    	public $s_id = "";

        function __construct(){
            $this->createDB();
        }

    	function get_new_sid(){
    		return md5(time() + mt_rand());
    	}

    	function insert_sid($private_key){
    		if($private_key != $this->db->getPrivate()){
    			$this->cause = "Invalid verification code";
    			$this->errorCode = 200;
    		}else{
    			if($this->db->not_connected()){
    				$this->errorCode = 1000;
    				$this->cause = "DB Connection failed";
    			}else{
    				$this->s_id = $this->get_new_sid();
    				if(!$this->db->createSID($this->s_id)){
                        $this->errorCode = 1080;
        				$this->cause = "DB Connection failed";
    				}
    			}
    		}
    	}
    }

?>
