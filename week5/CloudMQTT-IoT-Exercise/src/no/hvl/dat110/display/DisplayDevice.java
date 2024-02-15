package no.hvl.dat110.display;


import no.hvl.dat110.mqtt.brokerclient.Config;
import no.hvl.dat110.mqtt.brokerclient.MQTTSubClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class DisplayDevice extends Thread {
		
	
	public void run() {
		
		System.out.println("Display started");
		// Make a new instance of MQTTSubClient
        try {
            MQTTSubClient mqttsc = new MQTTSubClient();
			// use the sub instance to get the temperature
			mqttsc.messageArrived(Config.topic, new MqttMessage());

			// Do the display in the MQTTSubClient
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
