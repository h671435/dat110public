package no.hvl.dat110.tempsensor;


import no.hvl.dat110.rpcinterface.TempSensorInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TemperatureDevice extends Thread {

	private TemperatureSensor sn;
	
	public TemperatureDevice() {
		this.sn = new TemperatureSensor();
	}
	
	public void run() {
		
		System.out.println("temperature device started");

		try {
			// Get a reference to the registry using the port
			Registry registry = LocateRegistry.getRegistry(9091);

			// Look up the registry for the remote object (TempSensorInterface) using the name TempSensorInterface.REMOTE_IFACE_NAME
			TempSensorInterface tsi = (TempSensorInterface) registry.lookup("TempSensorInterface.REMOTE_IFACE_NAME");

			// loop 10 times and read the temp value from the TemperatureSensor each time
			int temperatur = 0;
			for (int i = 0; i < 10; i++) {
				temperatur = sn.read();

				// set the temperature value by calling the setTemperature remote method via the object of TempSensorInterface
				tsi.setTemperatur(temperatur);
				Thread.sleep(500);
			}

		} catch (RemoteException | InterruptedException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
