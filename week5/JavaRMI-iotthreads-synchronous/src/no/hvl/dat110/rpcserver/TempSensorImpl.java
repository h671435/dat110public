package no.hvl.dat110.rpcserver;

import no.hvl.dat110.rpcinterface.TempSensorInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * For demonstration purpose in dat110 course
 */

public class TempSensorImpl extends UnicastRemoteObject implements TempSensorInterface {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int temperatur = 0;

    public TempSensorImpl()
            throws RemoteException {
        super();
    }

    // implement the remote methods defined in the interface here

    public int getTemperatur() throws RemoteException {
        return temperatur;
    }

    public void setTemperatur(int tmp) throws RemoteException {
        temperatur = tmp;
    }

}