<?php
	include 'error.php';
	include 'model/sid_model.php';

	//Get
	if ($_SERVER ['REQUEST_METHOD'] === 'GET') {
		$receive_data = new SidModel;
		header('Content-type: application/json');
		echo json_encode(array(
				'state' => 'rejected'
		));
	//Post
} else if($_SERVER ['REQUEST_METHOD'] === 'POST') {
		header('Content-type: application/json');
		$receive_data = new SidModel;
		try{
			$request_json = json_encode($_POST);
			$sendParam = json_decode($request_json,true);
			$receiveParam = json_decode($sendParam['data'],true);
			$receive_data->insert_sid($receiveParam['privateKey']);

		}catch(Exception $e){
			echo "error at : ";
			echo $e->getLine();
			echo "\n";
			$receive_data->s_id = "";
			$receive_data->cause = $e->getMessage();
			$receive_data->errorCode = -1;
		}
		echo json_encode($receive_data);


	} else {
		header('Content-type: application/json');
		echo json_encode(array(
				'state' => 'rejected'
		));
	}
	exit ();
?>
