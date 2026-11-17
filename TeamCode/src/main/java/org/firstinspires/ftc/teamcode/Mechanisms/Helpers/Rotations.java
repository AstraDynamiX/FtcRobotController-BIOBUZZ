package org.firstinspires.ftc.teamcode.Mechanisms.Helpers;

public class Rotations
{
    public static Matrix3 aroundRightAxis(double angle)
    {
        double c = Math.cos(angle);
        double s = Math.sin(angle);

        Matrix3 r = new Matrix3();

        r.m = new double[][] {
                {1, 0, 0},
                {0, c, -s},
                {0, s, c}
        };

        return r;
    }

    public static Matrix3 aroundForwardAxis(double angle)
    {
        double c = Math.cos(angle);
        double s = Math.sin(angle);

        Matrix3 r = new Matrix3();

        r.m = new double[][] {
                {c, -s, 0},
                {s, c, 0},
                {0, 0, 1}
        };

        return r;
    }

    public static Matrix3 aroundUpAxis(double angle)
    {
        double c = Math.cos(angle);
        double s = Math.sin(angle);

        Matrix3 r = new Matrix3();

        r.m = new double[][] {
                {c, 0, s},
                {0, 1, 0},
                {-s, 0, c}
        };

        return r;
    }

    public static Matrix3 getRotation(double yaw, double pitch, double roll)
    {
        return Rotations.aroundUpAxis(yaw)
                .multiply(Rotations.aroundRightAxis(pitch))
                .multiply(Rotations.aroundForwardAxis(roll));
    }

    public static Vector3 toEuler(Matrix3 rotation)
    {

        double pitch = Math.asin(
                Math.max(-1, Math.min(1, rotation.m[2][1]))
        );

        double yaw = Math.atan2(
                -rotation.m[0][1],
                rotation.m[1][1]
        );

        double roll = Math.atan2(
                -rotation.m[2][0],
                rotation.m[2][2]
        );

        return new Vector3(yaw, pitch, roll);
    }
}