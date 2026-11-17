package org.firstinspires.ftc.teamcode.Mechanisms.Helpers;

import static org.firstinspires.ftc.teamcode.Mechanisms.Helpers.Rotations.getRotation;

import android.icu.text.Transliterator;

import org.firstinspires.ftc.robotcore.external.navigation.Position;

public class CameraPosition
{
    public double right, forward, up;
    public double pitch, yaw, roll;

    public CameraPosition forward(double forward) {
        this.forward = forward;
        return this;
    }
    public CameraPosition right(double right) {
        this.right = right;
        return this;
    }
    public CameraPosition up(double up) {
        this.up = up;
        return this;
    }
    public CameraPosition yaw(double yaw) {
        this.yaw = yaw;
        return this;
    }
    public CameraPosition pitch(double pitch) {
        this.pitch = pitch;
        return this;
    }
    public CameraPosition roll(double roll) {
        this.roll = roll;
        return this;
    }

    public Vector3 getPosition()
    {return new Vector3(right, forward, up);}


    public Matrix3 getCameraRobotRotation()
    {
        Matrix3 cameraAxes = new Matrix3();

        cameraAxes.m = new double[][] {
                {1, 0, 0},
                {0, 0, 1},
                {0, -1, 0}
        };

        return getRotation(yaw, pitch, roll).multiply(cameraAxes);
    }

    // In limelight camera space, x is right, y is down, z is forward/outward
    public Vector3 fromLimelightPosition(Position limelightPoint)
    {
        return new Vector3(
                limelightPoint.x,
                limelightPoint.z,
                -limelightPoint.y
        );
    }

    public Vector3 getRobotTagPosition(Vector3 cameraPoint)
    {
        // Rotate into robot space
        Vector3 rotated = getRotation(yaw, pitch, roll).multiply(cameraPoint);

        // Translate into robot space
        return rotated.add(getPosition());
    }
}
