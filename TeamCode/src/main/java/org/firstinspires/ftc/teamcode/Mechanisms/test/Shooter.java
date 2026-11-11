package org.firstinspires.ftc.teamcode.Mechanisms.test;

import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

public class Shooter extends SubsystemBase {
    private double polenPos; ///Todo: change
    private double nectarPos; ///Todo: change


    private MotorGroup flywheelMotor;
    private ServoEx slot1Servo;
    private ServoEx slot2Servo;
    private ServoEx slot3Servo;
    private ServoEx slot4Servo;
    enum Ball_type{
        POLEN,
        NECTAR
    }


    private void setType(ServoEx servo, Ball_type type){
        switch (type){
            case POLEN: {servo.set(polenPos);}
            case NECTAR: {servo.set(nectarPos);}
        }
    }
    public void setSlotType(int slotNumber, Ball_type type){
        switch(slotNumber)
    }
}
