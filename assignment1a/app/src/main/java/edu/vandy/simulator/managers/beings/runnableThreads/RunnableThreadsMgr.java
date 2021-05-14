package edu.vandy.simulator.managers.beings.runnableThreads;

import java.util.ArrayList;
import java.util.List;

import edu.vandy.simulator.managers.beings.BeingManager;

/**
 * This BeingManager implementation manually creates a pool of Java
 * threads that run the being simulations.
 */
public class RunnableThreadsMgr
        extends BeingManager<SimpleBeingRunnable> {
    /**
     * Used for Android debugging.
     */
    private final static String TAG =
            RunnableThreadsMgr.class.getName();

    /**
     * The list of beings (implemented as concurrent Java threads)
     * that are attempting to acquire palantiri for gazing.
     */
    public List<Thread> mBeingThreads;

    /**
     * A factory method that returns a new SimpleBeingRunnable instance.
     *
     * @return A new SimpleBeingRunnable instance
     */
    @Override
    public SimpleBeingRunnable newBeing() {
        // Return a new SimpleBeingRunnable instance.
        // TODO -- you fill in here replacing this statement with your solution.
        return new SimpleBeingRunnable(this);
    }

    /**
     * This entry point method is called by the Simulator framework to
     * start the being gazing simulation.
     */
    @Override
    public void runSimulation() {
        // Call a method to create and start a thread for each being.
        // TODO -- you fill in here.
        beginBeingThreads();

        // Call a method that creates and starts a thread that's then
        //  used to wait for all the being threads to finish and
        //  return that thread to the caller.
        // TODO -- you fill in here.
        Thread waiter = createAndStartWaiterForBeingThreads();

        // Block until the waiter thread has finished.
        // TODO -- you fill in here.
        try {
            waiter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Manually create/start a list of threads that represent the
     * beings in .this simulation.
     */
    void beginBeingThreads() {
        // All STUDENTS:
        // Call the BeingManager.getBeings() method to iterate through
        // the beings, create a new Thread object for each one, and
        // add it to the list of mBeingThreads.
        //
        // GRADUATE STUDENTS:
        // Set an "UncaughtExceptionHandler" for each being thread
        // that calls the BeingManager.error() method to indicate an
        // unexpected exception "ex" occurred for thread "thr".
        // Undergraduates do not need to set this exception handler
        // (though they are free to do to if they choose).
        //
        // TODO -- you fill in here.
        mBeingThreads = new ArrayList<>();
        /*getBeings()
                .stream()
                .map(Thread::new)
                .map(
                        (Thread i)->{
                            i.setUncaughtExceptionHandler((t,e)->error(e));
                            return null;
                        }
                    )
                .collect(Collectors.toList());*/
        for (Runnable runnable : getBeings()) {
            Thread thr = new Thread(runnable);
            thr.setUncaughtExceptionHandler((t, e) -> error(e));
            mBeingThreads.add(thr);
        }


        // Start all the threads in the List of Threads.
        // TODO -- you fill in here.
        mBeingThreads.forEach(Thread::start);

    }

    /**
     * Create and start a thread that can be used to wait for all the
     * being threads to finish and return that thread to the caller.
     *
     * @return Thread that is waiting for all the beings to complete.
     */
    public Thread createAndStartWaiterForBeingThreads() {
        // Create a Java thread that waits for all being threads to
        // finish gazing.  If an interruption or exception occurs
        // while waiting for the threads to finish, call the
        // BeingManager.error() helper method with the exception in
        // the catch clause, which trigger the simulator to generate a
        // shutdownNow() request.
        // TODO -- you fill in here.
        Thread waiter = new Thread(() -> {
            try {
                for (Thread mBeingThread : mBeingThreads) {
                    mBeingThread.join();
                }
            } catch (InterruptedException ex) {
                error(ex);
            }
        });

        // Start running the thread.
        // TODO -- you fill in here.
        waiter.start();

        // Return the thread.
        // TODO -- you fill in here replacing this statement with your solution.
        return waiter;
    }

    /**
     * Called to run to error the simulation and should only return
     * after all threads have been terminated and all resources
     * cleaned up.
     */
    @Override
    public void shutdownNow() {
        // No special handling required for this manager since the all
        // beings will have already been asked to stop by the base
        // class before calling this method.
    }
}

