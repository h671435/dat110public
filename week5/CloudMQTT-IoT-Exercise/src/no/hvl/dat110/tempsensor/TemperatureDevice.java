package no.hvl.dat110.tempsensor;


import no.hvl.dat110.mqtt.brokerclient.MQTTPubClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class TemperatureDevice extends Thread {

	private TemperatureSensor sn;
	
	public TemperatureDevice() {
		this.sn = new TemperatureSensor();
	}
	
	public void run() {
		
		System.out.println("temperature device started");
		//call MQTTPubClient (create a new instance) and make connection
		MQTTPubClient mqttpc = new MQTTPubClient();
		mqttpc.connect();

		// loop 10 times to read temp values
		for (int i = 0; i < 10; i++) {
			// read the temp from the TemperatureSensor
			int temp = sn.read();

			// use the MQTTPubClient instance object to publish the temp to the MQTT Broker
            try {
                mqttpc.publish("" + temp);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

	}
	
}
