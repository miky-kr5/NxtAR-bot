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

/**
 * <p>Class to report the values read from the different sensors through a
 * Bluetooth stream.</p>
 */
public class SensorReportThread extends Thread{
	private boolean done;
	private LightSensor lightSensor;
	private DataOutputStream outputStream;

	/**
	 * <p>Create a new SensorReportThread.</p>
	 * 
	 * @param outputStream The stream to send the data to.
	 * @param lightSensor An initialized and calibrated light sensor.
	 */
	public SensorReportThread(DataOutputStream outputStream, LightSensor lightSensor) throws NullPointerException{
		if(outputStream == null)
			throw new NullPointerException("Output stream is null.");

		if(lightSensor == null)
			throw new NullPointerException("Sensor is null.");

		this.lightSensor = lightSensor;
		done = false;
		this.outputStream = outputStream;
	}

	/**
	 * <p>Marks this thread as ready to finish cleanly.</p>
	 */
	public void finish(){
		done = true;
	}

	/**
	 * <p>Writes the values read from the light sensor to the output stream.</p>
	 */
	@Override
	public void run(){
		byte message = 0;

		while(!done){
			try{
				message = (byte)lightSensor.getLightValue();
				outputStream.writeByte(message);
				outputStream.flush();
			}catch(IOException io){
				// On disconnection terminate.
				done = true;
			}
		}
	}
}
