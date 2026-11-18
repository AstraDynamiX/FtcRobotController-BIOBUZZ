package org.firstinspires.ftc.teamcode.Mechanisms.test;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

public class FlowerMechanism extends SubsystemBase {
    double downPos; ///Todo: change
    double upPos; ///Todo: change

    private final ServoEx flowerServo;

    public enum FLOWER_STATE{
        UP,
        DOWN
    }
    FLOWER_STATE flowerState = FLOWER_STATE.UP;

    public FlowerMechanism(HardwareMap hardwareMap){
        this.flowerServo = new ServoEx(hardwareMap, "flower_s");
    }

    public void up(){
        flowerState = FLOWER_STATE.UP;
    }

    public void down(){
        flowerState = FLOWER_STATE.DOWN;
    }

    public void initialize(){
        this.up();
    }

    public void normal(){
        switch(flowerState){
            case UP:{
                flowerServo.set(upPos);
            }
            case DOWN:{
                flowerServo.set(downPos);
            }
        }
    }
}
