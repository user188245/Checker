<?php
    class BaseModel {
        function set_cause($param1,$param2){
            $this->cause = "first param is $param1 and second param is $param2";
        }

        function createDB(){
            $this->db = new Database;
        }

        protected $db = null;
        public $errorCode = 0;
        public $cause;
    }
?>
