package org.firstinspires.ftc.teamcode.Mechanisms.commands;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Mechanisms.test.*;

import java.util.concurrent.TimeUnit;

public class ShootPolen extends CommandBase {
    private final Robot robot;
    private ElapsedTime timer;

    public ShootPolen() {
        robot = Robot.getInstance();
        this.timer = new ElapsedTime();
        addRequirements(robot.shooter, robot.slides);
    }

    @Override
    public void initialize(){
        if (!robot.shooter.isSpunUp()){robot.shooter.spinUp();}
        robot.slides.stopperDown();
        robot.slides.up();
        timer.reset();
    }

    @Override
    public void execute(){
        if (timer.now(TimeUnit.MILLISECONDS) >=  10){
            for(int i=1; i<5; i++){
                robot.shooter.setSlotType(i, Shooter.Ball_type.POLEN);
            }
        }
    }

}
