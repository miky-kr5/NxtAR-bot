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

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import ve.ucv.ciens.ccg.nxtarbot.threads.MotorControlThread;
import ve.ucv.ciens.ccg.nxtarbot.threads.SensorReportThread;

/**
 * Core class for the robot module of NxtAR.
 */
public class NxtAR_bot{
	private static DataOutputStream dataOutputStream;
	private static DataInputStream dataInputStream;
	private static NXTConnection bluetoothConnection;
	private static MotorControlThread recvThread;
	private static SensorReportThread sendThread;

	/**
	 * <p>Finishes the communication threads anc closes the Bluetooth data streams, 
	 * then quits the application.</p>
	 */
	private static void quit(){
		if(recvThread != null) recvThread.finish();
		if(sendThread != null) sendThread.finish();

		if(bluetoothConnection != null){
			try{
				dataOutputStream.close();
				dataInputStream.close();
			}catch(IOException io){
				System.out.println(io.getMessage());
			}
			bluetoothConnection.close();
		}

		System.exit(0);
	}

	/**
	 * <p>Application entry point.</p>
	 * <p>Resets the motors, calibrates the light sensor and starts the
	 * networking threads.</p>
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args){
		// Set a listener to force quit if the ESCAPE button is pressed.
		Button.ESCAPE.addButtonListener(new QuitButtonListener());

		// Reset the rotation counts of the motors.
		Motor.A.resetTachoCount();
		Motor.B.resetTachoCount();
		Motor.C.resetTachoCount();

		// Start the light sensor and calibrate it.
		LightSensor lightSensor = new LightSensor(SensorPort.S1);
		lightSensor.setFloodlight(false);

		System.out.println("Point at dark\nand press ENTER");
		Button.ENTER.waitForPress();
		lightSensor.calibrateLow();
		System.out.println("--/--");

		System.out.println("Point at light\nand press ENTER");
		Button.ENTER.waitForPress();
		lightSensor.calibrateHigh();
		System.out.println("--/--");

		// Connect with a Bluetooth device in raw mode. Then get the connection
		// streams.
		bluetoothConnection = Bluetooth.waitForConnection();
		bluetoothConnection.setIOMode(NXTConnection.RAW);
		dataOutputStream = bluetoothConnection.openDataOutputStream();
		dataInputStream = bluetoothConnection.openDataInputStream();

		System.out.println("Connected");

		// Start the networking threads and wait for them to finish. 
		sendThread = new SensorReportThread(dataOutputStream, lightSensor);
		recvThread = new MotorControlThread(dataInputStream);

		recvThread.start();
		sendThread.start();

		try{
			recvThread.join();
			sendThread.join();
		}catch(InterruptedException i){ }

		quit();
	}

	/**
	 * <p>Force quit button listener.</p>
	 */
	private static class QuitButtonListener implements ButtonListener{
		/**
		 * Force quit.
		 */
		@Override
		public void buttonPressed(Button b){
			System.exit(0);
			//quit();
		}

		/**
		 * Do nothing.
		 */
		@Override
		public void buttonReleased(Button b){ }
	}
}
