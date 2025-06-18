package it.unibo.message;

public class UpdateView implements BoidMessage {
    int framerate;
    public UpdateView(int framerate) {
        this.framerate = framerate;
    }
}
