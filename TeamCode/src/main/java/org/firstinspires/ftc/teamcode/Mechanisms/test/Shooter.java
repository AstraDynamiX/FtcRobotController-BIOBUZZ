package org.firstinspires.ftc.teamcode.Mechanisms.test;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

public class Shooter extends SubsystemBase {
    private double polenPos; ///Todo: change
    private double nectarPos; ///Todo: change
    private double spinUpVel; ///Todo: change
    private double targetVel;
    private double velocityTolerance;


    private final MotorGroup flywheelMotor;
    private final ServoEx slot1Servo;
    private final ServoEx slot2Servo;
    private final ServoEx slot3Servo;
    private final ServoEx slot4Servo;
    enum Ball_type{
        POLEN,
        NECTAR
    }

    public Shooter(HardwareMap hardwareMap){
        slot1Servo = new ServoEx(hardwareMap, "slot1");
        slot2Servo = new ServoEx(hardwareMap, "slot2");
        slot3Servo = new ServoEx(hardwareMap, "slot3");
        slot4Servo = new ServoEx(hardwareMap, "slot4");

        flywheelMotor = new MotorGroup(
                new MotorEx(hardwareMap, "fly_left"),
                new MotorEx(hardwareMap, "fly_right").setInverted(true)
        );
    }

    private void setType(ServoEx servo, Ball_type type){
        switch (type){
            case POLEN: {servo.set(polenPos);}
            case NECTAR: {servo.set(nectarPos);}
        }
    }
    public void setSlotType(int slotNumber, Ball_type type){
        switch(slotNumber){
            case 1: {this.setType(slot1Servo, type);}
            case 2: {this.setType(slot2Servo, type);}
            case 3: {this.setType(slot3Servo, type);}
            case 4: {this.setType(slot4Servo, type);}
        }
    }

    public void spinUp(){
        flywheelMotor.set(spinUpVel);
    }

    public boolean isSpunUp(double tolerance){
        return Math.abs(flywheelMotor.getVelocity() - spinUpVel) <= tolerance;
    }
    public boolean isSpunUp(){
        return Math.abs(flywheelMotor.getVelocity() - spinUpVel) <= velocityTolerance;
    }

    public void setFlywheelVelocity(double targetVelocity){
        this.targetVel = targetVelocity;
    }
    public boolean isAtTargetVelocity(double tolerance){
        return Math.abs(flywheelMotor.getVelocity() - spinUpVel) <= tolerance;
    }
    public boolean isAtTargetVelocity(){
        return Math.abs(flywheelMotor.getVelocity() - spinUpVel) <= velocityTolerance;
    }

    public void shoot() {
        ///Todo: create
    }

    public void initialize(HardwareMap hardwareMap){
        flywheelMotor.setRunMode(Motor.RunMode.VelocityControl);

        flywheelMotor.setVeloCoefficients(0.05, 0.01, 0.31); ///Todo: change
        double[] coeffs = flywheelMotor.getVeloCoefficients();
        double kP = coeffs[0];
        double kI = coeffs[1];
        double kD = coeffs[2];

        flywheelMotor.setFeedforwardCoefficients(0.92, 0.47); ///Todo: change
        double[] ffCoeffs = flywheelMotor.getFeedforwardCoefficients();
        double kS = ffCoeffs[0];
        double kV = ffCoeffs[1];

        flywheelMotor.setFeedforwardCoefficients(0.92, 0.47, 0.3); ///Todo: change
        ffCoeffs = flywheelMotor.getFeedforwardCoefficients();
    }

    public void normal(){
        flywheelMotor.set(targetVel);
    }
}
