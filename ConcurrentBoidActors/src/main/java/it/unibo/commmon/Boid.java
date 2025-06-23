package it.unibo.commmon;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import it.unibo.message.*;

import java.util.ArrayList;
import java.util.List;

public class Boid extends AbstractBehavior<BoidMessage> {

    private P2d pos;
    private V2d vel;
    V2d separation;
    V2d alignment;
    V2d cohesion;

    public static Behavior<BoidMessage> create(P2d pos, V2d vel) {
        return Behaviors.setup(context -> new Boid(context, pos, vel));
    }

    private Boid(ActorContext<BoidMessage> context, P2d pos, V2d vel) {
    	super(context);
        this.pos = pos;
    	this.vel = vel;
    }
    
    public P2d getPos() {
    	return pos;
    }

    public V2d getVel() {
    	return vel;
    }


    @Override
    public Receive<BoidMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(ComputeVelocity.class, this::computeVelocity)
                .onMessage(UpdateVelocity.class, this::updateVelocity)
                .onMessage(UpdatePosition.class, this::updatePos)
                .onMessage(GetPosition.class, this::getPosition)
                .build();
    }

    public Behavior<BoidMessage> getPosition(GetPosition message) {
        message.replyTo.tell(new BoidPositionResponse(this.pos));
        return this;
    }

    public Behavior<BoidMessage> computeVelocity(ComputeVelocity message) {
        /* change velocity vector according to separation, alignment, cohesion */
        var model = message.model;
        List<Boid> nearbyBoids = getNearbyBoids(model);
        separation = calculateSeparation(nearbyBoids, model);
        alignment = calculateAlignment(nearbyBoids);
        cohesion = calculateCohesion(nearbyBoids);
        return this;
    }

    public Behavior<BoidMessage> updateVelocity(UpdateVelocity message) {
        var model = message.model;
        vel = vel.sum(alignment.mul(model.getAlignmentWeight()))
                .sum(separation.mul(model.getSeparationWeight()))
                .sum(cohesion.mul(model.getCohesionWeight()));

        /* Limit speed to MAX_SPEED */
        double speed = vel.abs();
        if (speed > model.getMaxSpeed()) {
            vel = vel.getNormalized().mul(model.getMaxSpeed());
        }
        return this;
    }

    public Behavior<BoidMessage> updatePos(UpdatePosition message) {
        /* Update position */
        var model = message.model;
        pos = pos.sum(vel);

        /* environment wrap-around */

        if (pos.x < model.getMinX()) pos = pos.sum(new V2d(model.getWidth(), 0));
        if (pos.x >= model.getMaxX()) pos = pos.sum(new V2d(-model.getWidth(), 0));
        if (pos.y < model.getMinY()) pos = pos.sum(new V2d(0, model.getHeight()));
        if (pos.y >= model.getMaxY()) pos = pos.sum(new V2d(0, -model.getHeight()));
        return this;
    }


    private List<Boid> getNearbyBoids(BoidsModel model) {
    	var list = new ArrayList<Boid>();
        for (Boid other : model.getBoids()) {
        	if (other != this) {
        		P2d otherPos = other.getPos();
        		double distance = pos.distance(otherPos);
        		if (distance < model.getPerceptionRadius()) {
        			list.add(other);
        		}
        	}
        }
        return list;
    }
    
    private V2d calculateAlignment(List<Boid> nearbyBoids) {
        double avgVx = 0;
        double avgVy = 0;
        if (nearbyBoids.size() > 0) {
	        for (Boid other : nearbyBoids) {
	        	V2d otherVel = other.getVel();
	            avgVx += otherVel.x;
	            avgVy += otherVel.y;
	        }	        
	        avgVx /= nearbyBoids.size();
	        avgVy /= nearbyBoids.size();
	        return new V2d(avgVx - vel.x, avgVy - vel.y).getNormalized();
        } else {
        	return new V2d(0, 0);
        }
    }

    private V2d calculateCohesion(List<Boid> nearbyBoids) {
        double centerX = 0;
        double centerY = 0;
        if (nearbyBoids.size() > 0) {
	        for (Boid other: nearbyBoids) {
	        	P2d otherPos = other.getPos();
	            centerX += otherPos.x;
	            centerY += otherPos.y;
	        }
            centerX /= nearbyBoids.size();
            centerY /= nearbyBoids.size();
            return new V2d(centerX - pos.x, centerY - pos.y).getNormalized();
        } else {
        	return new V2d(0, 0);
        }
    }
    
    private V2d calculateSeparation(List<Boid> nearbyBoids, BoidsModel model) {
        double dx = 0;
        double dy = 0;
        int count = 0;
        for (Boid other: nearbyBoids) {
        	P2d otherPos = other.getPos();
    	    double distance = pos.distance(otherPos);
    	    if (distance < model.getAvoidRadius()) {
    	    	dx += pos.x - otherPos.x;
    	    	dy += pos.y - otherPos.y;
    	    	count++;
    	    }
    	}
        if (count > 0) {
            dx /= count;
            dy /= count;
            return new V2d(dx, dy).getNormalized();
        } else {
        	return new V2d(0, 0);
        }
    }
}
