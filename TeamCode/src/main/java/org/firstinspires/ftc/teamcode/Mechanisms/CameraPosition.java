package org.firstinspires.ftc.teamcode.Mechanisms;

public class CameraPosition
{
    public double x, y, z;
    public double yaw, pitch, roll;

    public CameraPosition x(double x) {
        this.x = x;
        return this;
    }
    public CameraPosition y(double y) {
        this.y = y;
        return this;
    }
    public CameraPosition z(double z) {
        this.z = z;
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
}
