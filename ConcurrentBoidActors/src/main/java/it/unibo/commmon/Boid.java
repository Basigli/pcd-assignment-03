package it.unibo.commmon;

import java.util.ArrayList;
import java.util.List;

public class Boid {

    private P2d pos;
    private V2d vel;
    V2d separation;
    V2d alignment;
    V2d cohesion;

    public Boid(P2d pos, V2d vel) {
    	this.pos = pos;
    	this.vel = vel;
    }
    
    public P2d getPos() {
    	return pos;
    }

    public V2d getVel() {
    	return vel;
    }

    public void computeVelocity(List<Boid> boids, double perceptionRadius, double avoidRadius) {
        /* change velocity vector according to separation, alignment, cohesion */
        List<Boid> nearbyBoids = getNearbyBoids(boids, perceptionRadius);
        separation = calculateSeparation(nearbyBoids, avoidRadius);
        alignment = calculateAlignment(nearbyBoids);
        cohesion = calculateCohesion(nearbyBoids);
    }

    public void updateVelocity(double alignmentWeight, double separationWeight, double cohesionWeight, double maxSpeed) {
        vel = vel.sum(alignment.mul(alignmentWeight))
                .sum(separation.mul(separationWeight))
                .sum(cohesion.mul(cohesionWeight));

        /* Limit speed to MAX_SPEED */
        double speed = vel.abs();
        if (speed > maxSpeed) {
            vel = vel.getNormalized().mul(maxSpeed);
        }
    }

    public void updatePos(double minX, double maxX, double minY, double maxY, double width, double height) {

        /* Update position */
        pos = pos.sum(vel);
        /* environment wrap-around */
        if (pos.x < minX) pos = pos.sum(new V2d(width, 0));
        if (pos.x >= maxX) pos = pos.sum(new V2d(-width, 0));
        if (pos.y < minY) pos = pos.sum(new V2d(0, height));
        if (pos.y >= maxY) pos = pos.sum(new V2d(0, -height));
    }

    private List<Boid> getNearbyBoids(List<Boid> boids, double perceptionRadius) {
    	var list = new ArrayList<Boid>();
        for (Boid other : boids) {
        	if (other != this) {
        		P2d otherPos = other.getPos();
        		double distance = pos.distance(otherPos);
        		if (distance < perceptionRadius) {
        			list.add(other);
        		}
        	}
        }
        return list;
    }
    
    private V2d calculateAlignment(List<Boid> nearbyBoids) {
        double avgVx = 0;
        double avgVy = 0;
        if (!nearbyBoids.isEmpty()) {
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
        if (!nearbyBoids.isEmpty()) {
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

    private V2d calculateSeparation(List<Boid> nearbyBoids, double avoidRadius) {
        double dx = 0;
        double dy = 0;
        int count = 0;
        for (Boid other: nearbyBoids) {
        	P2d otherPos = other.getPos();
    	    double distance = pos.distance(otherPos);
    	    if (distance < avoidRadius) {
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
