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

public class MotorControlThread extends Thread{
	private boolean done;
	private DataInputStream inputStream;

	public MotorControlThread(DataInputStream inputStream){
		done = false;
		this.inputStream = inputStream;
	}

	public void finish(){
		done = true;
	}

	@Override
	public void run(){
		boolean motorA, motorB, motorC, recenterMotorB;
		int direction, rotation, tacho;
		byte[] message = new byte[2];

		while(!done){
			try{
				message[0] = inputStream.readByte();
				message[1] = inputStream.readByte();

				recenterMotorB = (message[0] & MotorMasks.RECENTER)  > 0 ? true : false;
				motorA = (message[0] & MotorMasks.MOTOR_A) > 0 ? true : false;
				motorB = (message[0] & MotorMasks.MOTOR_B) > 0 ? true : false;
				motorC = (message[0] & MotorMasks.MOTOR_C) > 0 ? true : false;
				direction = (message[0] & MotorMasks.DIRECTION) > 0 ? BasicMotorPort.FORWARD : BasicMotorPort.BACKWARD;

				if(motorA){
					Motor.A.setSpeed(message[1] * Battery.getVoltage());
					if(direction == BasicMotorPort.FORWARD)
						Motor.A.forward();
					else if(direction == BasicMotorPort.BACKWARD)
						Motor.A.backward();
				}

				if(motorB){
					Motor.B.setSpeed(message[1] * Battery.getVoltage());
					if(direction == BasicMotorPort.FORWARD)
						Motor.B.forward();
					else if(direction == BasicMotorPort.BACKWARD)
						Motor.B.backward();
				}

				if(motorC){
					Motor.C.setSpeed(message[1] * Battery.getVoltage());
					if(direction == BasicMotorPort.FORWARD)
						Motor.C.forward();
					else if(direction == BasicMotorPort.BACKWARD)
						Motor.C.backward();
				}

				if(recenterMotorB){
					tacho = Motor.B.getTachoCount() % 360;
					rotation = tacho > 180 ? 360 - tacho : -(tacho);
					Motor.B.rotate(rotation);
					Motor.B.resetTachoCount();
				}

			}catch(IOException io){
				done = true;
			}
		}
	}
}
