/*
 * Copyright (C) 2014 Miguel Angel Astor Romero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ve.ucv.ciens.ccg.nxtarbot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import ve.ucv.ciens.ccg.nxtarbot.threads.CommRecv;
import ve.ucv.ciens.ccg.nxtarbot.threads.CommSend;

public class NxtAR_bot{
	private static DataOutputStream dataOutputStream;
	private static DataInputStream dataInputStream;
	private static NXTConnection bluetoothConnection;
	private static CommRecv recv;
	private static CommSend send;

	public static void main(String[] args){
		bluetoothConnection = Bluetooth.waitForConnection();
		bluetoothConnection.setIOMode(NXTConnection.RAW);
		dataOutputStream = bluetoothConnection.openDataOutputStream();
		dataInputStream = bluetoothConnection.openDataInputStream();

		System.out.println("Connected");

		send = new CommSend(dataOutputStream);
		recv = new CommRecv(dataInputStream);

		recv.start();
		send.start();

		try{
			recv.join();
			send.join();
		}catch(InterruptedException i){ }

		try{
			dataOutputStream.close();
			dataInputStream.close();
		}catch(IOException io){
			System.out.println(io.getMessage());
		}
		bluetoothConnection.close();
	}
}
