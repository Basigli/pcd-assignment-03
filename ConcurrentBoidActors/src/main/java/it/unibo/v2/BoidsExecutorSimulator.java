package it.unibo.v2;



import it.unibo.commmon.BoidsModel;
import it.unibo.commmon.BoidsSimulator;
import it.unibo.commmon.BoidsView;
import it.unibo.commmon.Flag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;


public class BoidsExecutorSimulator implements BoidsSimulator {
    private ExecutorService executor;
    private final int WORKERS_NUMBER = Runtime.getRuntime().availableProcessors() + 1;
    private BoidsModel model;
    private Optional<BoidsView> view;

    private List<Callable<Void>> computeVelocityTasks = new ArrayList<>();
    private List<Callable<Void>> updateVelocityTasks = new ArrayList<>();
    private List<Callable<Void>> updatePositionTasks = new ArrayList<>();
    private static final int FRAMERATE = 25; //25
    private int framerate;
    private long t0 = System.currentTimeMillis();

    private Flag resetFlag;
    private Flag pauseFlag;
    public BoidsExecutorSimulator(BoidsModel model) {
        this.model = model;
        this.view = Optional.empty();
        this.resetFlag = new Flag();
        this.pauseFlag = new Flag();
    }

    private void waitForCompletion(List<Future<Void>> futures) throws ExecutionException, InterruptedException {
        for (Future<Void> future : futures) {
            future.get();
        }
    }
    private void createTasks() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
        this.executor = Executors.newFixedThreadPool(WORKERS_NUMBER);
        var boids = this.model.getBoids();
        boids.forEach(boid -> {
            computeVelocityTasks.add(new ComputeVelocityTask(boid, model));
            updateVelocityTasks.add(new UpdateVelocityTask(boid, model));
            updatePositionTasks.add(new UpdatePositionTask(boid, model));
        });
    }

    @Override
    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
        this.pauseFlag.set();
    }

    @Override
    public synchronized void notifyStarted() {
        pauseFlag.reset();

    }

    public void runSimulation() {
        createTasks();
        while(true) {
            if (resetFlag.isSet()) {
                resetFlag.reset();
                pauseFlag.set();
                int nBoids = model.getNboids();
                this.model.setNboids(0);
                this.model.setNboids(nBoids);
                this.createTasks();
            }
            if (!this.pauseFlag.isSet()){
                try {
                    waitForCompletion(executor.invokeAll(computeVelocityTasks));
                    waitForCompletion(executor.invokeAll(updateVelocityTasks));
                    waitForCompletion(executor.invokeAll(updatePositionTasks));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
                this.updateView();
            }
        }
    }

    @Override
    public synchronized void notifyStopped() {
        pauseFlag.set();
    }

    @Override
    public synchronized void notifyResumed() {
        pauseFlag.reset();
    }

    @Override
    public synchronized void notifyResetted() {
        resetFlag.set();
    }

    @Override
    public synchronized void notifyBoidsChanged() {
        createTasks();
    }

    private void updateView(){
        if (view.isPresent()) {
            view.get().update(framerate);
            var t1 = System.currentTimeMillis();
            var dtElapsed = t1 - t0;
            var frameratePeriod = 1000/FRAMERATE;

            if (dtElapsed < frameratePeriod) {
                try {
                    Thread.sleep(frameratePeriod - dtElapsed);
                } catch (Exception ex) {}
                framerate = FRAMERATE;
            } else {
                framerate = (int) (1000/dtElapsed);
            }
        }
        t0 = System.currentTimeMillis();
    }
}
