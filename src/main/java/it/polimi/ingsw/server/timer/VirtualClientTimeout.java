package it.polimi.ingsw.server.timer;

import it.polimi.ingsw.server.controllers.CallableTimerTask;
import it.polimi.ingsw.server.controllers.VirtualClient;

import java.util.TimerTask;

/**
 * Class for specific TimerTask
 */
public class VirtualClientTimeout extends TimerTask {

    /**
     * reference at a Virtual Controller
     */
    CallableTimerTask vc;

    /**
     * Min time before the task action
     */
    int maxRetries;

    /**
     * Counter value
     */
    public static int i = 0;

    /**
     * Standard Constructor of the class
     * @param vc is an object which implements a CallableTimerTask interface
     * @param maxRetries the value of the clock
     */
    public VirtualClientTimeout(VirtualClient vc, int maxRetries){
        this.vc = vc;
        this.maxRetries = maxRetries;
    }

    /**
     * Override method from TimerTask
     * When the clock ends call method endOfTime
     */
    public void run()
    {
        boolean stop = i > maxRetries;
        i++;
        if (stop) {
            this.cancel();
            vc.endOfTime();
        }
    }
}
