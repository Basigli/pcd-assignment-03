package it.unibo.commmon;

import java.util.ArrayList;
import java.util.List;

public class BoidsModel {
    
    private final List<Boid> boids;
    private double separationWeight;

    private double alignmentWeight; 
    private double cohesionWeight; 
    private final double width;
    private final double height;
    private final double maxSpeed;
    private final double perceptionRadius;
    private final double avoidRadius;

    public BoidsModel(int nboids,  
    						double initialSeparationWeight, 
    						double initialAlignmentWeight, 
    						double initialCohesionWeight,
    						double width, 
    						double height,
    						double maxSpeed,
    						double perceptionRadius,
    						double avoidRadius){
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

            boids.add(new Boid(pos, vel));
        }
    }

    public void setNboids(int nboids) {
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


    public List<Boid> getBoids(){
        return new ArrayList<>(boids);
    }
    public List<Boid> getBoids(int lowerBound, int upperBound){
        return new ArrayList<>(boids.subList(lowerBound, upperBound));
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

    public void setSeparationWeight(double value) {
    	this.separationWeight = value;
    }

    public void setAlignmentWeight(double value) {
    	this.alignmentWeight = value;
    }

    public void setCohesionWeight(double value) {
    	this.cohesionWeight = value;
    }

    public double getSeparationWeight() {
    	return separationWeight;
    }

    public double getCohesionWeight() {
    	return cohesionWeight;
    }

    public double getAlignmentWeight() {
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
