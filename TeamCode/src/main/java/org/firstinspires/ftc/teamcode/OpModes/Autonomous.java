package org.firstinspires.ftc.teamcode.OpModes;

import static org.firstinspires.ftc.teamcode.Mechanisms.Extra.NormalizeAngle;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Mechanisms.LaunchBoard;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.List;

class AutoStep
{
    PathChain path;
    Runnable action;
    double speed;
    double delay;
    double actionDelay;

    public AutoStep(PathChain path, Runnable action, double speed, double delay, double actionDelay)
    {
        this.path = path;
        this.action = action;
        this.speed = speed;
        this.delay = delay;
        this.actionDelay = actionDelay;
    }
}

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous")
public class Autonomous extends OpMode
{
    private final double SHOOT_DELAY = 3000; // Wait for flywheel rev
    private final double SHOOT_TIME = 3000; // Wait for shooting before starting path
    private final double INTAKE_SPEED = 0.5;

    private final double FIELD_LENGTH = 142; // in pedroPathing coordinates

    LaunchBoard LaunchBoard = new LaunchBoard();

    private Follower follower;

    ElapsedTime opModeTimer = new ElapsedTime();
    ElapsedTime timer = new ElapsedTime();


    //Red bottom
    private final Pose startBottom = new Pose(56, 9, Math.toRadians(90));
    private final Pose leftWallBottom = new Pose(24, 36, Math.toRadians(90));
    private final Pose loadingBottom = new Pose(11, 104, Math.toRadians(90));

    //Red top
    private final Pose loadingTop = new Pose(loadingBottom.getX(), loadingBottom.getY() ,Math.toRadians(270));
    private final Pose goalTop = new Pose(56, 108, Math.toRadians(270));

    private Pose currentPose;

    List<AutoStep> steps = new ArrayList<>();
    int stepIndex = 0;
    private boolean stepStarted = false;
    private boolean pathStarted = false;
    private boolean actionStarted = false;

    // Trajectory selection booleans
    boolean redAlliance = false;
    boolean bottomTrajectory = true;
    boolean confirmed = false;


    @Override
    public void init()
    {
        opModeTimer.reset();
        follower = Constants.createFollower(hardwareMap);

        steps.clear();
        stepIndex = 0;
        stepStarted = false;
        pathStarted = false;
        actionStarted = false;
    }

    @Override
    public void init_loop()
    {
        if (confirmed)
        {telemetry.addData("CONFIRMED", "");}
        else
        {
            if (gamepad1.bWasPressed())
            {redAlliance = !redAlliance;}

            if (gamepad1.xWasPressed())
            {bottomTrajectory = !bottomTrajectory;}

            if (gamepad1.aWasPressed())
            {
                confirmed = true;
                LaunchBoard.init(hardwareMap, redAlliance);
            }

            telemetry.addData("", "() - change alliance, [] - change trajectory, X - confirm");
        }

        telemetry.addData("ALLIANCE", (redAlliance) ? "red" : "blue");
        telemetry.addData("TRAJECTORY", (bottomTrajectory) ? "bottom" : "top");

        telemetry.addData("","");
    }

    @Override
    public void start()
    {
        opModeTimer.reset();
        //LaunchBoard.start();

        if (bottomTrajectory)
        {
            StartRoutine(startBottom);

            AddRoutineStep(startBottom, () -> LaunchBoard.Rev(), 1,  SHOOT_DELAY, 0);
            AddRoutineStep(leftWallBottom, () -> LaunchBoard.Shoot(), 1, SHOOT_TIME, 0);
            AddRoutineStep(loadingBottom, () -> LaunchBoard.Idle(), 1, 0 , 0);
        }
        else
        {
            StartRoutine(MirrorPose(startBottom, false));

            AddRoutineStep(MirrorPose(startBottom, false), () -> LaunchBoard.Rev(), 1, SHOOT_DELAY, 0);
            AddRoutineStep(goalTop, () -> LaunchBoard.Shoot(), 1, SHOOT_TIME, 0);
            AddRoutineStep(loadingTop, () -> LaunchBoard.Idle(), 1, 0, 0);
        }
    }

    @Override
    public void loop()
    {
        follower.update();
        RunScheduler();
        LaunchBoard.UpdateLaunch(false, 475);

        telemetry.addData("NEXT TARGET:", "%2.f, %2.f, %2.f",
                follower.getPose().getX(),
                follower.getPose().getY(),
                follower.getPose().getHeading()
        );

        if (opModeTimer.seconds() > 30) {requestOpModeStop();}
    }

    @Override
    public void stop()
    {
        //LaunchBoard.stop();
    }


    private Pose MirrorPose(Pose pose, boolean vertical)
    {
        if (vertical)
        {
            return new Pose(
                    FIELD_LENGTH - pose.getX(),
                    pose.getY(),
                    NormalizeAngle(-pose.getHeading())
            );
        }
        else
        {
            return new Pose(
                    pose.getX(),
                    FIELD_LENGTH - pose.getY(),
                    NormalizeAngle(Math.PI - pose.getHeading())
            );
        }
    }


    private void AddStep(Pose start, Pose end, Runnable action, double speed, double delay, double actionDelay)
    {
        PathChain path = follower.pathBuilder()
                .addPath(new BezierLine(start, end))
                .setLinearHeadingInterpolation(start.getHeading(), end.getHeading())
                .build();

        steps.add(new AutoStep(path, action, speed, delay, actionDelay));
    }


    private Pose AlliancePose(Pose redPose)
    {
        return (redAlliance) ? (redPose) : (MirrorPose(redPose, true));
    }

    /** "Routine" means path chain that follows this structure:
     *  - the end pose of a step is the start pose of the next step
     *  - only the poses from one alliance (side of the field) is used
     */
    private void StartRoutine(Pose start)
    {
        currentPose = AlliancePose(start);
        follower.setPose(currentPose);
    }

    private void AddRoutineStep(Pose end, Runnable action, double speed, double delay, double actionDelay)
    {
        Pose nextPose = AlliancePose(end);
        AddStep(currentPose, nextPose, action, speed, delay, actionDelay);
        currentPose = nextPose;
    }


    private void RunScheduler() {

        if (stepIndex >= steps.size())
        {
            LaunchBoard.Idle();
            return;
        }
        AutoStep step = steps.get(stepIndex);

        //Reset timer after each new step
        if (!stepStarted)
        {
            timer.reset();
            stepStarted = true;
        }

        if (!pathStarted && timer.milliseconds() > step.delay)
        {
            pathStarted = true;
            follower.setMaxPower(step.speed);
            follower.followPath(step.path, false);
        }

        //Run action after specified time
        if (!actionStarted && timer.milliseconds() > step.actionDelay)
        {
            if (step.action != null) step.action.run();
            actionStarted = true;
        }

        //Run next path after the previous one is finished
        if (!follower.isBusy() && pathStarted && actionStarted)
        {
            stepIndex++;
            stepStarted = false;
            pathStarted = false;
            actionStarted = false;
        }
    }

}
