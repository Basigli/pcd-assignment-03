package it.unibo.message;

public interface BoidMessage {

    record Start() implements BoidMessage {}
    record Stop() implements BoidMessage {}
    record Reset() implements BoidMessage {}
    record Resume() implements BoidMessage {}
    record ComputeVelocity() implements BoidMessage {}
    record VelocityComputed() implements BoidMessage {}
    record UpdateVelocity() implements BoidMessage {}
    record VelocityUpdated() implements BoidMessage {}
    record UpdatePosition() implements BoidMessage {}
    record PositionUpdated() implements BoidMessage {}
    record UpdateView(int framerate) implements BoidMessage {}
    record BoidsChanged(int nBoids) implements BoidMessage {}
}
