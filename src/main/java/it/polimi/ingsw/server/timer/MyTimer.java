package it.polimi.ingsw.server.timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class which implements a Timer
 */
public class MyTimer {
    /**
     * Timer attribute of the class
     */
    private final Timer timer;

    /**
     * Task related to a timer
     */
    private final TimerTask task;

    /**
     * Standard constructor of the MyTimer class
     * @param task TimerTask related to the clock
     */
    public MyTimer(TimerTask task) {
        timer = new Timer();
        this.task = task;
    }

    /**
     * Method called for starting the timer
     */
    public void startTimer() {
        timer.schedule(task, 0);
    }

    /**
     * Method called for stop the task and delete it from the timer tasks list
     */
    public void stopTask(){
        task.cancel();
        timer.purge();
    }

    /**
     * Method used for cancel the timer
     */
    public void killTimer(){
        timer.cancel();
    }
}
