package org.firstinspires.ftc.teamcode.Mechanisms.test;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import java.util.concurrent.TimeUnit;

public class Intake extends SubsystemBase {
    double downPos; ///Todo: change
    double polenOnlyPos; ///Todo: change
    double outtakePos; ///Todo: change
    double restPos; ///Todo: change

    double intakeVelocity = 1; //between -1 and 1 ///Todo: change
    double outtakeVelocity = -1; //between -1 and 1 ///Todo: change

    ElapsedTime timer = new ElapsedTime();
    double min_time = 100; // in milliseconds

    private final MotorEx intakeMotor;
    private final ServoEx intakeServo;

    public enum INTAKE_STATE{
        DOWN,
        REST,
        OUTTAKE,
        POLEN_ONLY
    }
    INTAKE_STATE intakeState = INTAKE_STATE.REST;

    public Intake(HardwareMap hardwareMap){
        this.intakeMotor = new MotorEx(hardwareMap, "int_m");
        intakeMotor.setRunMode(Motor.RunMode.RawPower);
        intakeMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        this.intakeServo = new ServoEx(hardwareMap, "int_s");
    }

    public void initialize(){
        this.down();
    }

    private void setState(INTAKE_STATE state){
        this.intakeState = state;
        timer.reset();
    }
    public void down(){this.setState(INTAKE_STATE.DOWN);}
    public void rest(){this.setState(INTAKE_STATE.REST);}
    public void polenOnly(){this.setState(INTAKE_STATE.POLEN_ONLY);}
    public void outtake(){this.setState(INTAKE_STATE.OUTTAKE);}

    public void normal(){
        switch(this.intakeState){
            case REST:{
                intakeServo.set(restPos);
            }
            case DOWN:{
                intakeServo.set(downPos);
                if(timer.now(TimeUnit.MILLISECONDS) > min_time){
                    intakeMotor.setVelocity(intakeVelocity);
                }
            }
            case POLEN_ONLY:{
                intakeServo.set(polenOnlyPos);
                if(timer.now(TimeUnit.MILLISECONDS) > min_time){
                    intakeMotor.setVelocity(intakeVelocity);
                }
            }
            case OUTTAKE:{
                intakeServo.set(outtakePos);
                if(timer.now(TimeUnit.MILLISECONDS) > min_time){
                    intakeMotor.setVelocity(outtakeVelocity);
                }
            }
        }
    }
}
