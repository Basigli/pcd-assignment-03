package it.unibo.commmon;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import it.unibo.message.BoidActor;
import it.unibo.message.BoidMessage;
import it.unibo.message.BoidsResponse;
import it.unibo.message.GetBoids;

import java.util.ArrayList;
import java.util.List;

public class BoidsModel extends AbstractBehavior<BoidMessage>{
    
    private final List<ActorRef<BoidMessage>> boids;
    private double separationWeight;

    private double alignmentWeight; 
    private double cohesionWeight; 
    private final double width;
    private final double height;
    private final double maxSpeed;
    private final double perceptionRadius;
    private final double avoidRadius;

    public static Behavior<BoidMessage> create(int nboids,
                                               double initialSeparationWeight,
                                               double initialAlignmentWeight,
                                               double initialCohesionWeight,
                                               double width,
                                               double height,
                                               double maxSpeed,
                                               double perceptionRadius,
                                               double avoidRadius) {
        return Behaviors.setup(context -> new BoidsModel(context,
                nboids,
                initialSeparationWeight,
                initialAlignmentWeight,
                initialCohesionWeight,
                width,
                height,
                maxSpeed,
                perceptionRadius,
                avoidRadius));
    }

    private BoidsModel(ActorContext<BoidMessage> context,
                       int nboids,
                       double initialSeparationWeight,
                       double initialAlignmentWeight,
                       double initialCohesionWeight,
                       double width,
                       double height,
                       double maxSpeed,
                       double perceptionRadius,
                       double avoidRadius){
        super(context);
        separationWeight = initialSeparationWeight;
        alignmentWeight = initialAlignmentWeight;
        cohesionWeight = initialCohesionWeight;
        this.width = width;
        this.height = height;
        this.maxSpeed = maxSpeed;
        this.perceptionRadius = perceptionRadius;
        this.avoidRadius = avoidRadius;
    	boids = new ArrayList<>();
        generateBoids(nboids);
    }

    private void generateBoids(int nboids) {
        for (int i = 0; i < nboids; i++) {
            P2d pos = new P2d(-width/2 + Math.random() * width, -height/2 + Math.random() * height);
            V2d vel = new V2d(Math.random() * maxSpeed/2 - maxSpeed/4, Math.random() * maxSpeed/2 - maxSpeed/4);
            ActorRef<BoidMessage> boid = getContext().spawn(Boid.create(pos, vel), "boid-" + i);
            boids.add(boid);
        }
    }

    @Override
    public Receive<BoidMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetBoids.class, this::getBoids)
                .build();
    }

    public synchronized void setNboids(int nboids) {
        int currentNboids = this.boids.size();

        if (nboids > currentNboids)
            generateBoids(nboids - currentNboids);
        else {
            System.out.println("removing " + (currentNboids -  nboids) + " items");
            if ((currentNboids - nboids) > 0) {
                boids.subList(0, (currentNboids - nboids)).clear();
            }
        }
    }
    public int getNboids() {return boids.size();}


    public Behavior<BoidMessage> getBoids(GetBoids message){
        message.replyTo.tell(new BoidsResponse(new ArrayList<>(boids)));
        return this;
    }


    public double getMinX() {
    	return -width/2;
    }

    public double getMaxX() {
    	return width/2;
    }

    public double getMinY() {
    	return -height/2;
    }

    public double getMaxY() {
    	return height/2;
    }
    
    public double getWidth() {
    	return width;
    }
 
    public double getHeight() {
    	return height;
    }

    public synchronized void setSeparationWeight(double value) {
    	this.separationWeight = value;
    }

    public synchronized void setAlignmentWeight(double value) {
    	this.alignmentWeight = value;
    }

    public synchronized void setCohesionWeight(double value) {
    	this.cohesionWeight = value;
    }

    public synchronized double getSeparationWeight() {
    	return separationWeight;
    }

    public synchronized double getCohesionWeight() {
    	return cohesionWeight;
    }

    public synchronized double getAlignmentWeight() {
    	return alignmentWeight;
    }
    
    public double getMaxSpeed() {
    	return maxSpeed;
    }

    public double getAvoidRadius() {
    	return avoidRadius;
    }

    public double getPerceptionRadius() {
    	return perceptionRadius;
    }


}
