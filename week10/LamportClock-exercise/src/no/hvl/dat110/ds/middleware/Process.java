package no.hvl.dat110.ds.middleware;

import java.rmi.AccessException;

/**
 * @author tdoy
 * Based on Section 6.2: Distributed Systems - van Steen and Tanenbaum (2017)
 * For demo/teaching purpose at dat110 class
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import no.hvl.dat110.ds.middleware.iface.OperationType;
import no.hvl.dat110.ds.middleware.iface.ProcessInterface;
import no.hvl.dat110.ds.util.LamportClock;
import no.hvl.dat110.ds.util.Util;

public class Process extends UnicastRemoteObject implements ProcessInterface {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<Message> queue;                    // queue for this process
    private int processID;
    private LamportClock clock;                        // lamport clock
    private double balance = 1000;                    // default balance
    private Map<String, Integer> replicas;            // list of other processes including self known to this process

    protected Process(int id) throws RemoteException {
        super();
        processID = id;
        clock = new LamportClock();
        queue = Collections.synchronizedList(new ArrayList<Message>());
        replicas = Util.getProcessReplicas();
    }

    private void updateDeposit(double amount) throws RemoteException {
        clock.increment();
        balance += amount;
    }

    private void updateInterest(double interest) throws RemoteException {
        clock.increment();
        double intvalue = balance * interest;
        balance += intvalue;
    }

    private void updateWithdrawal(double amount) throws RemoteException {
        clock.increment();
        balance -= amount;
    }

    private void sortQueue() {
        Collections.sort(queue, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                if (m1.getClock() != m2.getClock()) {
                    return m1.getClock() - m2.getClock();
                } else {
                    return m1.getProcessID() - m2.getProcessID();
                }
            }
        });
    }

    // client initiated method
    @Override
    public void requestInterest(double interest) throws RemoteException {
        //
        clock.increment();
        Message message = new Message(clock.getClock(), processID);    // set the timestamp of message & process ID
        message.setOptype(OperationType.INTEREST);                    // set the type of message - deposit or interest
        message.setInterest(interest);                                // add interest to calculate as part of message

        queue.add(message);                            // add to queue

        // multicast clock + message to other processes
        multicastMessage(message);

    }

    // client initiated method
    @Override
    public void requestDeposit(double amount) throws RemoteException {

        // implement - use idea from requestInterest()
        clock.increment();
        Message message = new Message(clock.getClock(), processID);
        message.setOptype(OperationType.DEPOSIT);
        message.setDepositamount(amount);

        queue.add(message);

        multicastMessage(message);
    }

    // client initiated method
    @Override
    public void requestWithdrawal(double amount) throws RemoteException {

        // implement - use idea from requestInterest()
        clock.increment();
        Message message = new Message(clock.getClock(), processID);
        message.setOptype(OperationType.WITHDRAWAL);
        message.setWithdrawamount(amount);

        queue.add(message);

        multicastMessage(message);

    }

    @Override
    public void onAcknowledgementReceived(Message message) throws RemoteException {

        int index = exist(message);
        if (index != -1) {
            queue.get(index).setAcknowledged(true);
        }
    }

    public void applyOperation() throws RemoteException {
        // implement

        // sort the queue
        sortQueue();

        // iterate over the queue
        for (Message message : queue) {
            // for each message in the queue, check if it is acknowledged
            if (message.isAcknowledged()) {
                // check for the operationtype in the message
                switch (message.getOptype()) {
                    // call the appropriate update method for the operation type and pass the value
                    case DEPOSIT:
                        updateDeposit(message.getDepositamount());
                        break;
                    case INTEREST:
                        updateInterest(message.getInterest());
                        break;
                    case WITHDRAWAL:
                        updateWithdrawal(message.getWithdrawamount());
                        break;
                }
            }

        }
    }

    @Override
    public void onMessageReceived(Message message) throws RemoteException {

        if (exist(message) == -1)
            queue.add(message);                            // upon receipt of a message, add message to queue

        // implement the remaining

        // sort the queue according to timestamp and processID
        sortQueue();
        // check the clock of the sending process
        // get the clock that is higher and update the local clock using clock.adjustClock()
        // increment the local clock
        if (message.getClock() < clock.getClock()) {
            clock.increment();
        } else if (message.getClock() > clock.getClock()) {
            clock.adjustClock(message.getClock());
            clock.increment();
        }
        // multicast acknowledgement to other processes including self
        multicastAcknowledgement(message);
    }

    private void multicastAcknowledgement(Message message) throws AccessException, RemoteException {

        // implement

        // iterate over the replicas

        // for each replica, use Util.getProcessStub to get the replica stub (remote object)

        // call onAcknowledgementReceived() for each remote replica

    }

    // multicast message to other processes including self
    private void multicastMessage(Message message) throws AccessException, RemoteException {

        // implement

        // iterate over the replicas
        for (Map.Entry<String, Integer> entry : replicas.entrySet()) {
            String replicaName = entry.getKey();
            Integer replicaId = entry.getValue();

            // for each replica, use Util.getProcessStub to get the replica stub (remote object)

            // call onMessageReceived() for each remote replica
            ProcessInterface replicaStub = Util.getProcessStub(replicaName, replicaId);
            replicaStub.onMessageReceived(message);
        }
    }

    private int exist(Message message) {
        for (int i = 0; i < queue.size(); i++) {
            Message c = queue.get(i);
            if (message.getOptype() == c.getOptype() && message.getClock() == c.getClock() && message.getProcessID()
                    == c.getProcessID())
                return i;
        }

        return -1;
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

    @Override
    public void doLocalEvent() throws RemoteException {
        // to simulate new events
        try {
            Thread.sleep(500);
            clock.increment();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

    }


}
