package no.hvl.dat110.ds.middleware;


/**
 * @author tdoy
 * For demo/teaching purpose at dat110 class
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import no.hvl.dat110.ds.middleware.iface.OperationType;
import no.hvl.dat110.ds.middleware.iface.ProcessInterface;
import no.hvl.dat110.ds.util.Util;

public class Process extends UnicastRemoteObject implements ProcessInterface {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<Message> queue;                    // queue for this process
    private int processID;                            // id of this process
    private double balance = 1000;                    // default balance (each replica has the same). Our goal is to keep the balance consistent
    private Map<String, Integer> replicas;            // list of other processes including self known to this process

    protected Process(int id) throws RemoteException {
        super();
        processID = id;
        queue = new ArrayList<Message>();
        replicas = Util.getProcessReplicas();
    }

    private void updateDeposit(double amount) throws RemoteException {

        balance += amount;
    }

    private void updateInterest(double interest) throws RemoteException {

        double intvalue = balance * interest;
        balance += intvalue;
    }

    private void updateWithdrawal(double amount) throws RemoteException {

        balance -= amount;
    }


    private void sortQueue() {
        // TODO
        // sort the queue by the clock (unique time stamped given by the sequencer)
        for (int i = 0; i < queue.size(); i++) {
            for (int j = 0; j < queue.size(); j++) {
                if (queue.get(i).getClock() < queue.get(j).getClock()) {
                    Message temp = queue.get(i);
                    queue.set(i, queue.get(j));
                    queue.set(j, temp);
                }
            }
        }
    }

    // client initiated method
    @Override
    public void requestInterest(double interest) throws RemoteException {
        // TODO
        // make a new message instance and set the following:
        Message message = new Message();
        // set the type of message - interest
        message.setOptype(OperationType.INTEREST);
        // set the process ID
        message.setProcessID(processID);
        // set the interest
        message.setInterest(interest);

        // send the message to the sequencer by calling the sendMessageToSequencer
        sendMessageToSequencer(message);
    }

    // client initiated method
    @Override
    public void requestDeposit(double amount) throws RemoteException {
        // TODO
        // make a new message instance and set the following
        Message message = new Message();
        // set the type of message - deposit
        message.setOptype(OperationType.DEPOSIT);
        // set the process ID
        message.setProcessID(processID);
        // set the deposit amount
        message.setDepositamount(amount);

        // send the message to the sequencer
        sendMessageToSequencer(message);
    }

    // client initiated method
    @Override
    public void requestWithdrawal(double amount) throws RemoteException {
        // TODO
        // make a new message instance and set the following
        Message message = new Message();
        // set the type of message - withdrawal
        message.setOptype(OperationType.WITHDRAWAL);
        // set the process ID
        message.setProcessID(processID);
        // set the withdrawal amount
        message.setWithdrawamount(amount);

        // send the message to the sequencer
        sendMessageToSequencer(message);
    }

    private void sendMessageToSequencer(Message message) throws RemoteException {
        // get the port for the sequencer: use Util class
        Map<String, Integer> replicar = Util.getProcessReplicas();
        int sequencerPort = replicas.get("sequencer");

        // get the sequencer stub: use Util class
        ProcessInterface sequencerStub = Util.getProcessStub("sequencer", sequencerPort);

        // using the sequencer stub, call the remote onMessageReceived method to send the message to the sequencer
        sequencerStub.onMessageReceived(message);
    }

    public void applyOperation() throws RemoteException {
        // iterate over the queue
        for (Message message : queue) {
            // for each message in the queue, check the operation type
            OperationType opType = message.getOptype();
            // call the appropriate update method for the operation type and pass the value to be updated
            switch (opType) {
                case INTEREST:
                    updateInterest(message.getInterest());
                    break;
                case WITHDRAWAL:
                    updateWithdrawal(message.getWithdrawamount());
                    break;
                case DEPOSIT:
                    updateDeposit(message.getDepositamount());
                    break;
            }
        }

        Util.printClock(this);
    }

    @Override
    public void onMessageReceived(Message message) throws RemoteException {
        // TODO
        // upon receipt of a message, add message to the queue
        // check the ordering limit, if equal to queue size, start to process the following:
        // sort the queue according to time stamped by the sequencer
        // apply operation and commit
        // clear the queue

    }

    @Override
    public double getBalance() throws RemoteException {
        return balance;
    }

    @Override
    public int getProcessID() throws RemoteException {
        return processID;
    }

    @Override
    public List<Message> getQueue() throws RemoteException {
        return queue;
    }

}
