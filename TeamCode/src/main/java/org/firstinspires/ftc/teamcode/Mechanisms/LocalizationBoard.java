package org.firstinspires.ftc.teamcode.Mechanisms;

import static java.lang.Math.sqrt;

import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

public class LocalizationBoard
{
    public CameraPosition cameraPosition;

    private Limelight3A limelight;
    private GoBildaPinpointDriver pinpoint;

    private double prevFieldX = 0;
    private double prevFieldY = 0;
    private double prevHeading = 0;
    private double prevTagX = 0;
    private double prevTagY = 0;
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

        double tagX = 999999;
        double tagY = 999999;
        double distance = 999999;
        double bearing = 999999;

        Pose2D pos = pinpoint.getPosition();
        double curFieldX = pos.getX(DistanceUnit.INCH);
        double curFieldY = pos.getY(DistanceUnit.INCH);
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
            Pose3D tagPose = detection.getFiducialResults().get(0).getTargetPoseCameraSpace();

            tagX = tagPose.getPosition().x;
            tagY = tagPose.getPosition().y;
        }
        // Use odometry for positioning
        else if (odometryAvailable)
        {
            // Calculate displacement in field plane first, then rotate in robot plane
            // so that rotations during movement don't mess up the values
            double dtFieldX = curFieldX - prevFieldX;
            double dtFieldY = curFieldY - prevFieldY;
            double prevHeadingCos = Math.cos(prevHeading);
            double prevHeadingSin = Math.sin(prevHeading);
            double dtRobotX = dtFieldX * prevHeadingCos + dtFieldY * prevHeadingSin;
            double dtRobotY = -dtFieldX * prevHeadingSin + dtFieldY * prevHeadingCos;
            // Tag coordinates in previous plane (aka what they would be if the robot hadn't rotated)
            double prevPlaneTagX = prevTagX - dtRobotX;
            double prevPlaneTagY = prevTagY - dtRobotY;

            double dtHeading = curHeading - prevHeading;
            double dtHeadingCos = Math.cos(dtHeading);
            double dtHeadingSin = Math.sin(dtHeading);
            // Tag coordinates rotated to match this frame's plane
            tagX = prevPlaneTagX * dtHeadingCos - prevPlaneTagY * dtHeadingSin;
            tagY = prevPlaneTagX * dtHeadingSin + prevPlaneTagY * dtHeadingCos;
        }

        if ((tagX + tagY) < 999999)
        {
            distance = Math.hypot(tagY, tagX);
            bearing = Math.atan2(tagY, tagX);

            prevTagX = tagX;
            prevTagY = tagY;
        }

        prevFieldX = curFieldX;
        prevFieldY = curFieldY;
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