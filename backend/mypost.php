<?php
	include 'error.php';
	include 'model/base_model.php';

	//Get
	if ($_SERVER ['REQUEST_METHOD'] === 'GET') {
		header ( 'Content-type: application/json' );
		echo json_encode ( array (
				'state' => 'rejected'
		) );
	//Post
	} else if ($_SERVER ['REQUEST_METHOD'] === 'POST') {
		header ( 'Content-type: application/json' );

		$receive_data = new BaseModel;
		try{
			$request_json = json_encode($_POST);
			$sendParam = json_decode($request_json,true);
			$receiveParam = json_decode($sendParam['data'],true);

			$receive_data->set_cause($receiveParam['param1'],$receiveParam['param2']);

		}catch(Exception $e){
			$receive_data->cause = $e->getMessage();
			$receive_data->errorCode = -1;
		}
		echo json_encode($receive_data);


	} else {
		header ( 'Content-type: application/json' );
		echo json_encode ( $request );
	}
	exit ();
?>
