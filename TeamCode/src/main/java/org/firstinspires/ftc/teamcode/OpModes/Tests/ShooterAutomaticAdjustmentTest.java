package org.firstinspires.ftc.teamcode.OpModes.Tests;

import static org.firstinspires.ftc.teamcode.Mechanisms.Extra.GRAV_ACCEL;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.teamcode.Mechanisms.LocalizationBoard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Configurable
@TeleOp(group = "tests")
public class ShooterAutomaticAdjustmentTest extends OpMode
{
    public static double GOAL_HEIGHT = 42; //in TODO
    private final double LAUNCH_HEIGHT = 13.4; //TODO
    public static double LAUNCH_ANGLE_DEGREES = 15; //TODO
    private double LAUNCH_ANGLE;
    //public static double GOAL_ANGLE_DEGREES = 15;
    //private double GOAL_ANGLE;

    private final double FLYWHEEL_RADIUS = 1.89; //in TODO
    public static double FLYWHEEL_KP = 11.2;
    public static double FLYWHEEL_KI = 14;

    LocalizationBoard CamBoard = new LocalizationBoard();
    private FileWriter writer;

    private DcMotor intake;
    private MotorEx leftFlywheel;
    private MotorEx rightFlywheel;

    private double distanceToGoal = 0;
    private double ballExitSpeed = 999999;
    private double flywheelRotSpeed = 0;

    private boolean motorControl = false;
    private boolean servoControl = false;


    @Override
    public void init()
    {
        CamBoard.init(hardwareMap, 8);

        intake = hardwareMap.get(DcMotor.class, "intake");
        leftFlywheel = initMotor(hardwareMap, true, "leftFlywheel");
        rightFlywheel = initMotor(hardwareMap, false, "rightFlywheel");

        File file = new File("/sdcard/FIRST/autoAdjustmentData.csv");

        try {
            writer = new FileWriter(file, false);
            writer.write("distanceToGoal,ballExitSpeed\n"); // header
        }
        catch (IOException e)
        {RobotLog.e("Failed to open file: " + e.getMessage());}
    }

    private MotorEx initMotor(HardwareMap hwMap, boolean inverted, String name)
    {
        MotorEx motor;
        motor = new MotorEx(hwMap, name, Motor.GoBILDA.BARE);
        motor.setRunMode(MotorEx.RunMode.VelocityControl);
        motor.setVeloCoefficients(FLYWHEEL_KP, FLYWHEEL_KI, 0);
        motor.setInverted(inverted);
        return motor;
    }

    @Override
    public void start() {CamBoard.start();}

    @Override
    public void loop()
    {
        LAUNCH_ANGLE = Math.toRadians(LAUNCH_ANGLE_DEGREES);
        //GOAL_ANGLE = Math.toRadians(GOAL_ANGLE_DEGREES);

        distanceToGoal = CamBoard.GetAprilTag("range");
        if (distanceToGoal == 999999) return;

        ballExitSpeed = distanceToGoal / Math.cos(LAUNCH_ANGLE) *
                Math.sqrt( GRAV_ACCEL / (2 * (distanceToGoal * Math.tan(LAUNCH_ANGLE) - GOAL_HEIGHT)) );

        // TODO calculate the relationship between ballExitSpeed and flywheelSpeed

        if (gamepad1.xWasPressed() && writer != null)
        {
            try {
                writer.write(
                        distanceToGoal + "," +
                        flywheelRotSpeed + "\n"
                );
                writer.flush();
            }
            catch (IOException e)
            {RobotLog.e("Write failed: " + e.getMessage());}
        }

        if (gamepad1.aWasPressed())
        {motorControl = !motorControl;}

        if (gamepad1.bWasPressed())
        {servoControl = !servoControl;}

        if (motorControl)
        {
            //Divide by 2*pi*radius to get RPS then multiply by TPR to get TPS (ticks per second)
            leftFlywheel.setVelocity(flywheelRotSpeed);
            rightFlywheel.setVelocity(flywheelRotSpeed);

            intake.setPower(-0.8);
        }
        else
        {
            leftFlywheel.setVelocity(0);
            rightFlywheel.setVelocity(0);
            intake.setPower(0);
        }
    }

    @Override
    public void stop()
    {
        CamBoard.stop();

        if (writer != null)
        {
            try { writer.flush(); writer.close(); }
            catch (IOException e) { RobotLog.e("Close failed: " + e.getMessage()); }
        }
    }

}
