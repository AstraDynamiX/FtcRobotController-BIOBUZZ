package org.firstinspires.ftc.teamcode.Mathematics;

public class ShotCalculator {

    public static final Vector3d gravitationalAcceleration = new Vector3d(0,0, -9.81);

    public static Vector3d computeInitialShotTargetVelocity(Vector3d start, Vector3d apex){
        if(start.getZ() >= apex.getZ()){
            return new Vector3d(Double.NaN, Double.NaN, Double.NaN);
        }
        Vector3d velocity;
        double apexTime = Math.sqrt(apex.substract(start).dotProduct(gravitationalAcceleration) * (-2.0) / gravitationalAcceleration.dotProduct(gravitationalAcceleration));
        velocity = apex.substract(start).scalarMultiplication(1/apexTime).substract(gravitationalAcceleration.scalarMultiplication(0.5).scalarMultiplication(apexTime));
        return velocity;
    }

    
}
