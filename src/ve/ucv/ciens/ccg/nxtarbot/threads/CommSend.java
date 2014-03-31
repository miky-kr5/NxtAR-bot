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

import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

public class CommSend extends Thread {
	private boolean done;
	private byte msg;
	private LightSensor light;
	private DataOutputStream oStream;

	public CommSend(DataOutputStream oStream){
		light = new LightSensor(SensorPort.S1);
		done = false;
		msg = 0;
		this.oStream = oStream;
	}

	@Override
	public void run(){
		while(!done){
			try{
				if((msg = (byte)light.getLightValue()) < 40){
					oStream.writeByte(msg);
					oStream.flush();
				}
			}catch(IOException io){
				done = true;
			}
		}
	}
}
