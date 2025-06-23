package it.unibo.message;

public class UpdateView implements BoidMessage {
    public int framerate;
    public UpdateView(int framerate) {
        this.framerate = framerate;
    }
}
