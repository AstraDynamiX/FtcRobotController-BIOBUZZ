package org.firstinspires.ftc.teamcode.Mechanisms.Helpers;

public class Vector3
{
    public double right, forward, up;

    public Vector3(double right, double forward, double up)
    {
        this.right = right;
        this.forward = forward;
        this.up = up;
    }

    public Vector3 add(Vector3 other)
    {
        return new Vector3(
                right + other.right,
                forward + other.forward,
                up + other.up
        );
    }

    public Vector3 subtract(Vector3 other)
    {
        return new Vector3(
                right - other.right,
                forward - other.forward,
                up - other.up
        );
    }
}