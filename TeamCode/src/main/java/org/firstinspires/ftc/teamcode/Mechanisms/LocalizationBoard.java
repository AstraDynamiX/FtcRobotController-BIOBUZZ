package org.firstinspires.ftc.teamcode.Mechanisms;

import static org.firstinspires.ftc.teamcode.Mechanisms.Helpers.Rotations.getRotation;

import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.Mechanisms.Helpers.CameraPosition;
import org.firstinspires.ftc.teamcode.Mechanisms.Helpers.Matrix3;
import org.firstinspires.ftc.teamcode.Mechanisms.Helpers.Vector3;

public class LocalizationBoard
{
    public CameraPosition cameraPosition;

    private Limelight3A limelight;
    private GoBildaPinpointDriver pinpoint;

    private double prevFieldForward = 0;
    private double prevFieldRight = 0;
    private double prevHeading = 0;
    private double prevTagForward = 0;
    private double prevTagRight = 0;
    private boolean odometryAvailable = false;
    private boolean useCamera = false;


    public void init(HardwareMap hwMap)
    {
        limelight = hwMap.get(Limelight3A.class, "limelight");
    }

    public void init(HardwareMap hwMap, Follower follower)
    {
        init(hwMap);

        PinpointLocalizer localizer =
                (PinpointLocalizer) follower.getPoseTracker().getLocalizer();
        pinpoint = localizer.getPinpoint();

        odometryAvailable = true;
    }

    public void start()
    {
        limelight.start();
    }

    public void stop()
    {
        limelight.stop();
        limelight.close();
    }

    public double GetAprilTagPosition(String dimension)
    {
        useCamera = true;
        LLResult detection = null;

        double tagForward = 999999;
        double tagRight = 999999;
        double distance = 999999;
        double bearing = 999999;

        Pose2D pos = pinpoint.getPosition();
        double curFieldForward = pos.getX(DistanceUnit.INCH);
        double curFieldRight = pos.getY(DistanceUnit.INCH);
        double curHeading = pos.getHeading(AngleUnit.RADIANS);

        // Determine whether camera sees tag or not
        if (limelight == null) useCamera = false;
        else
        {
            detection = limelight.getLatestResult();
            if (!detection.isValid()) useCamera = false;
        }

        // Use camera for positioning
        if (useCamera)
        {
            Pose3D cameraTag = detection.getFiducialResults().get(0).getTargetPoseCameraSpace();
            Position cameraTagPosition = cameraTag.getPosition();
            YawPitchRollAngles cameraTagOrientation = cameraTag.getOrientation();

            Matrix3 cameraTagRotation = getRotation(cameraTagOrientation.getYaw(), cameraTagOrientation.getPitch(), cameraTagOrientation.getRoll());

            Vector3 robotTagPosition = cameraPosition.getRobotTagPosition(cameraPosition.fromLimelightPosition(cameraTagPosition));

            tagForward = robotTagPosition.forward;
            tagRight = robotTagPosition.right;

            Matrix3 robotTagRotation = cameraPosition.getCameraRobotRotation().multiply(cameraTagRotation);
        }
        // Use odometry for positioning
        else if (odometryAvailable)
        {
            // Calculate displacement in field plane first, then rotate in robot plane
            // so that rotations during movement don't mess up the values
            double dtFieldForward = curFieldForward - prevFieldForward;
            double dtFieldRight = curFieldRight - prevFieldRight;
            double prevHeadingCos = Math.cos(prevHeading);
            double prevHeadingSin = Math.sin(prevHeading);
            double dtRobotForward = dtFieldForward * prevHeadingCos + dtFieldRight * prevHeadingSin;
            double dtRobotRight = -dtFieldForward * prevHeadingSin + dtFieldRight * prevHeadingCos;
            // Tag coordinates in previous plane (aka what they would be if the robot hadn't rotated)
            double prevPlaneTagForward = prevTagForward - dtRobotForward;
            double prevPlaneTagRight = prevTagRight - dtRobotRight;

            double dtHeading = curHeading - prevHeading;
            double dtHeadingCos = Math.cos(dtHeading);
            double dtHeadingSin = Math.sin(dtHeading);
            // Tag coordinates rotated to match this frame's plane
            tagForward = prevPlaneTagForward * dtHeadingCos - prevPlaneTagRight * dtHeadingSin;
            tagRight = prevPlaneTagForward * dtHeadingSin + prevPlaneTagRight * dtHeadingCos;
        }

        if ((tagForward + tagRight) < 999999)
        {
            distance = Math.hypot(tagRight, tagForward);
            bearing = Math.atan2(tagRight, tagForward);

            prevTagForward = tagForward;
            prevTagRight = tagRight;
        }

        prevFieldForward = curFieldForward;
        prevFieldRight = curFieldRight;
        prevHeading = curHeading;

        switch (dimension)
        {
            case "range": return distance;
            case "bearing": return bearing;
            default: return 999999;
        }
    }

    //Returns all dimensions in an array
    public double[] GetAprilTagPosition()
    {
        return new double[] {
                GetAprilTagPosition("range"),
                GetAprilTagPosition("bearing"),
        };
    }

    public void SetAprilTagId(int aprilTagId) {limelight.pipelineSwitch(aprilTagId);}
    
    public boolean IsUsingCamera()
    {
        return useCamera;
    }
    public double GetIMUHeading()
    {
        return Math.toDegrees(prevHeading);
    }

}