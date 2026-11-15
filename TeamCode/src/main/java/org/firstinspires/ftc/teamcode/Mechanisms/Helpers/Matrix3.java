package org.firstinspires.ftc.teamcode.Mechanisms.Helpers;

public class Matrix3
{
    public double[][] m = new double[3][3];

    public Matrix3 multiply(Matrix3 other)
    {
        Matrix3 result = new Matrix3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++)
                {result.m[i][j] += m[i][k] * other.m[k][j];}
            }
        }
        return result;
    }

    public Vector3 multiply(Vector3 v)
    {
        return new Vector3(
                m[0][0]*v.right + m[0][1]*v.forward + m[0][2]*v.up,
                m[1][0]*v.right + m[1][1]*v.forward + m[1][2]*v.up,
                m[2][0]*v.right + m[2][1]*v.forward + m[2][2]*v.up
        );
    }
}