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

import lejos.nxt.BasicMotorPort;
import lejos.nxt.Battery;
import lejos.nxt.Motor;
import ve.ucv.ciens.ccg.nxtarbot.protocol.MotorMasks;

/**
 * <p>Class to control the motors on the robot based on isntructions received via
 * Bluetooth.</p>
 * 
 * <p>The instructions are codified on a very simple protocol:<p>
 * <ul>
 *     <li> Every protocol packet is exactly two bytes long.</li>
 *     <li> The lowest order byte codifies the motor to control and the rotation
 *          direction of the same on it's highest four bits. The lower four bits
 *          indicate that the central motor should return to it's position of
 *          origin. This four bits can also be used for future expansions.
 *     <li> The highest order byte, a number between 0 and 100, contains the
 *          power to send to the motor.</li> 
 * </ul>
 */
public class MotorControlThread extends Thread{
	private boolean done;
	private DataInputStream inputStream;

	/**
	 * Create a new MotorControlThread.
	 * 
	 * @param inputStream The stream to receive motor commands from.
	 * @throws NullPointerException If inputStream is null.
	 */
	public MotorControlThread(DataInputStream inputStream) throws NullPointerException{
		if(inputStream == null)
			throw new NullPointerException("Input stream is null.");

		done = false;
		this.inputStream = inputStream;
	}

	/**
	 * <p>Marks this thread as ready to finish cleanly.</p>
	 */
	public void finish(){
		done = true;
	}

	/**
	 * <p>Receive and process motor control instructions via Bluetooth.</p>
	 */
	@Override
	public void run(){
		boolean motorA, motorB, motorC, recenterMotorB;
		int direction, rotation, tacho;
		byte[] message = new byte[2];

		while(!done){
			try{
				// Read the two bytes indicated by the protocol.
				message[0] = inputStream.readByte();
				message[1] = inputStream.readByte();

				// Decode the instruction parameters.
				recenterMotorB = (message[0] & MotorMasks.RECENTER)  > 0 ? true : false;
				motorA = (message[0] & MotorMasks.MOTOR_A) > 0 ? true : false;
				motorB = (message[0] & MotorMasks.MOTOR_B) > 0 ? true : false;
				motorC = (message[0] & MotorMasks.MOTOR_C) > 0 ? true : false;
				direction = (message[0] & MotorMasks.DIRECTION) > 0 ? BasicMotorPort.FORWARD : BasicMotorPort.BACKWARD;

				if(motorA){
					// Set motor A to run at specified speed.
					Motor.A.setSpeed(message[1] * Battery.getVoltage());
					if(direction == BasicMotorPort.FORWARD)
						Motor.A.forward();
					else if(direction == BasicMotorPort.BACKWARD)
						Motor.A.backward();
				}

				if(motorB){
					// Set motor B to run at specified speed.
					Motor.B.setSpeed(message[1] * Battery.getVoltage());
					if(direction == BasicMotorPort.FORWARD)
						Motor.B.forward();
					else if(direction == BasicMotorPort.BACKWARD)
						Motor.B.backward();
				}

				if(motorC){
					// Set motor C to run at specified speed.
					Motor.C.setSpeed(message[1] * Battery.getVoltage());
					if(direction == BasicMotorPort.FORWARD)
						Motor.C.forward();
					else if(direction == BasicMotorPort.BACKWARD)
						Motor.C.backward();
				}

				if(recenterMotorB){
					// Return motor B to it's origin. Rotate in the direction
					// that implies less movement.
					tacho = Motor.B.getTachoCount() % 360;
					rotation = tacho > 180 ? 360 - tacho : -(tacho);
					Motor.B.rotate(rotation);
					Motor.B.resetTachoCount();
				}

			}catch(IOException io){
				// On disconnection terminate.
				done = true;
			}
		}
	}
}
