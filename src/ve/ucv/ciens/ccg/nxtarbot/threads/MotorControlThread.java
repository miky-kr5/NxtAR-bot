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
package ve.ucv.ciens.ccg.nxtarbot.threads;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.Motor;
import ve.ucv.ciens.icaro.libnxtarcontrol.DecodedControlAction;
import ve.ucv.ciens.icaro.libnxtarcontrol.NxtARControlProtocol;
import ve.ucv.ciens.icaro.libnxtarcontrol.UserActionListener;

/**
 * <p>Class to control the motors on the robot based on instructions received via
 * Bluetooth.</p>
 */
public class MotorControlThread extends Thread implements UserActionListener{
	private boolean done;
	private final DataInputStream inputStream;
	private final NxtARControlProtocol controlProtocol;

	/**
	 * Create a new MotorControlThread.
	 * 
	 * @param inputStream The stream to receive motor commands from.
	 * @throws NullPointerException If inputStream is null.
	 */
	public MotorControlThread(final DataInputStream inputStream) throws NullPointerException{
		if(inputStream == null)
			throw new NullPointerException("Input stream is null.");

		this.done = false;
		this.inputStream = inputStream;
		this.controlProtocol = new NxtARControlProtocol(this.inputStream);
		this.controlProtocol.registerUserActionListener(this);
	}

	/**
	 * <p>Marks this thread as ready to finish cleanly.</p>
	 */
	public void finish(){
		this.done = true;
		this.controlProtocol.removeUserActionListener(this);
	}

	/**
	 * <p>Receive and process motor control instructions using the protocol library.</p>
	 */
	@Override
	public void run(){
		while(!done){
			try{
				controlProtocol.readAndExecuteMessage();
			}catch(IOException io){
				// On disconnection, terminate.
				done = true;
			}
		}
	}

	@Override
	public void onListenerRegistered() {
		System.out.println("Registered.");
	}

	@Override
	public void onUserAction1(DecodedControlAction.Motor motorFlag, int speed) {
		// Rotate 90 degrees.
		System.out.println("USER_1 :: ROTATE 90");
		Motor.B.rotate(-120, false);
	}

	@Override
	public void onUserAction2(DecodedControlAction.Motor motorFlag, int speed) { }

	@Override
	public void onUserAction3(DecodedControlAction.Motor motorFlag, int speed) { }

	@Override
	public void onListenerRemoved() {
		System.out.println("Remove.");
	}
}
