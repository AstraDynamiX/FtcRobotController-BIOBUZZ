package org.firstinspires.ftc.teamcode.Mechanisms.test;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

public class Slides extends SubsystemBase {
    private int upPos; ///Todo: change //in ticks
    private int downPos; ///Todo: change //in ticks
    private int middlePos; ///Todo: change //in ticks

    private double freePos; ///Todo: change
    private double stopPos; ///Todo: change

    private final MotorGroup slidesMotor;
    private final ServoEx stopperServo;

    public enum SLIDES_STATE{
        UP,
        DOWN,
        MIDDLE
    }
    private SLIDES_STATE slidesState = SLIDES_STATE.DOWN;

    public enum STOPPER_STATE{
        FREE,
        STOP
    }
    private STOPPER_STATE stopperState = STOPPER_STATE.STOP;

    public Slides(HardwareMap hardwareMap){
        slidesMotor = new MotorGroup(
                new MotorEx(hardwareMap, "slides_left"),
                new MotorEx(hardwareMap, "slides_right").setInverted(true)
        );
        slidesMotor.setRunMode(Motor.RunMode.PositionControl);
        slidesMotor.setPositionTolerance(10);
        slidesMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        stopperServo = new ServoEx(hardwareMap, "stopper_s");
    }

    public void initialize(){
        this.up();
        this.stopperFree();
    }

    public void up(){
        slidesState = SLIDES_STATE.UP;
    }
    public void middle(){
        slidesState = SLIDES_STATE.MIDDLE;
    }
    public void down(){
        slidesState = SLIDES_STATE.DOWN;
    }

    public void stopperDown(){
        stopperState = STOPPER_STATE.STOP;
    }
    public void stopperFree(){
        stopperState = STOPPER_STATE.FREE;
    }

    public SLIDES_STATE getSlidesState() {return slidesState;}
    public STOPPER_STATE getStopperState(){return stopperState;}

    public void normal(){
        switch (slidesState){
            case UP:{
                slidesMotor.setTargetPosition(upPos);
            }
            case MIDDLE:{
                slidesMotor.setTargetPosition(middlePos);
            }
            case DOWN:{
                slidesMotor.setTargetPosition(downPos);
            }
        }

        switch (stopperState){
            case STOP:{
                stopperServo.set(stopPos);
            }
            case FREE:{
                stopperServo.set(freePos);
            }
        }
    }
}
