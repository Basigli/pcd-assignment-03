package it.unibo.commmon;

public interface BoidsSimulator {
    void attachView(BoidsView view);
    void notifyStarted();

    void notifyStopped();
    void notifyResumed();
    void notifyResetted();

    void notifyBoidsChanged();

}
