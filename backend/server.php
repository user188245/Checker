<?php

	include 'error.php';
	include 'model/server_model.php';

	set_time_limit(0);

	$address = "118.36.72.75";
	$port = 8100;
	$max_clients = 30;

	$client = array();

	$sock = socket_create(AF_INET,SOCK_STREAM,0);
	socket_bind($sock, $address, $port) or die('failed to patch address');

	socket_listen($sock);

	echo 'Server on';

	while (true) {
		$socks = array();
		$socks[] = $sock;
		foreach($client as $c){
			$socks[] = $c;
		}
		$writer = null;
		$except = null;
		$time  = null;
		$select = socket_select($socks,$writer,$except,$time);
		echo "[num : $select]\n";
		if($select== 0){
			continue;
		}
		if (in_array($sock, $socks)){
			if (count($client) <= $max_clients){
				echo "new client approched\n";
				$client[] = socket_accept($sock);
			}else{
				echo "client limit\n";
			}
			$key = array_search($sock, $socks);
			unset($socks[$key]);
		}

		foreach ($socks as $socket){
			$input = null;
			$output = new ServerModel;
			try{
				if($socket)
				$input = socket_read($socket,1024);
				if($input == null){
					echo "one client was left.\n";
					$key = array_search($socket, $client);
					unset($client[$key]);
				}
				else{
					echo "$input\n";
					try{
						$deinput = json_decode($input,true);
						if($deinput != null){
							$s_id = $deinput['s_id'];
							$purpose = $deinput['purpose'];
							$data = $deinput['data'];
							str_replace($data, "", "\\");

							$dedata = json_decode($data,true);
							echo "received s_id : $s_id\n";
							echo "received purpose : $purpose\n";
							echo "received data : $data\n";
							if($dedata != null){
								$move = $dedata['move'];
								$chat = $dedata['chat'];
								$target = $dedata['target'];
							}
							switch($purpose){
								case "is_matched":
									echo "purpose-matched detected\n";
									$output->matched($s_id);
									break;
								case "join":
									echo "purpose-join detected\n";
									$output->join($target, $s_id);
									break;
								case "send":
									echo "purpose-send detected\n";
									$output->state = "void";
									$output->inqueue($s_id, $move, $chat);
									$output->getqueue($target);
									break;
								case "exit":
									echo "one client was left:\n";
									$key = array_search($socket, $client);
									unset($client[$key]);
									break;
							}
						}else{
							$output->errorCode = 300;
							$output->cause = "json phasing failed.";
							socket_write($socket, json_encode($output));
							continue;
						}

					}catch(Exception $e){
						echo $e->getMessage();
						echo "\n";
						echo $e->getLine();
						echo "\n";
						socket_write($socket, json_encode($output));
						continue;
					}
					$output->errorCode = 0;
					echo "output : ";
					echo json_encode($output);
					echo "\n";
					socket_write($socket, json_encode($output));
				}
			}catch(Exception $e){
				echo "one client was left.\n";
				echo "interrupted at ";
				echo $e->getLine();
				echo " line\n";
				$key = array_search($socket, $client);
				unset($client[$key]);
			}

		}

	}

	socket_close($sock);
?>
