package no.hvl.dat110.display;


import no.hvl.dat110.rpcinterface.TempSensorInterface;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DisplayDevice extends Thread {


	public void run() {

		System.out.println("Display started...");
        try {
            // Get a reference to the registry using the port
            Registry registry = LocateRegistry.getRegistry(9091);

            // Look up the registry for the remote object (TempSensorInterface) using the name TempSensorInterface.REMOTE_IFACE_NAME
            TempSensorInterface tsi = (TempSensorInterface) registry.lookup("TempSensorInterface.REMOTE_IFACE_NAME");

            // loop 10 times and read the temp value from the TemperatureSensor each time
            for (int i = 0; i < 10; i++) {
                // get the temperature value by calling the getTemperature remote method via the object of TempSensorInterface
                int sum = tsi.getTemperatur();

                // print the temperature value to console
                System.out.println((i + 1) + ". " + sum);
                Thread.sleep(500);
            }

        } catch (AccessException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
