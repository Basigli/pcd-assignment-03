package it.unibo.message;

import it.unibo.commmon.Boid;

import java.util.List;

public interface BoidMessage {

    record Start() implements BoidMessage {}
    record Stop() implements BoidMessage {}
    record Reset() implements BoidMessage {}
    record Resume() implements BoidMessage {}
    record ComputeVelocity(List<Boid> boids, double perceptionRadius, double avoidRadius) implements BoidMessage {}
    record VelocityComputed() implements BoidMessage {}
    record UpdateVelocity(double alignmentWeight, double separationWeight, double cohesionWeight, double maxSpeed) implements BoidMessage {}
    record VelocityUpdated() implements BoidMessage {}
    record UpdatePosition(double minX, double maxX, double minY, double maxY, double width, double heigh) implements BoidMessage {}
    record PositionUpdated() implements BoidMessage {}
    record UpdateView(int framerate) implements BoidMessage {}
    record BoidsChanged(int nBoids) implements BoidMessage {}
    record SetSeparationWeight(double separationWeight) implements BoidMessage {}
    record SetCohesionWeight(double cohesionWeight) implements BoidMessage {}
    record SetAlignmentWeight(double alignmentWeight) implements BoidMessage {}
}


