package org.firstinspires.ftc.teamcode.Mechanisms.test;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

//singleton Robot class
public class Robot extends com.seattlesolvers.solverslib.command.Robot{
    private static final Robot instance = new Robot();
    public static Robot getInstance() {
        return instance;
    }

    public enum OP_MODE_TYPE{
        AUTO,
        TELEOP
    }

    public enum AllianceColor {
        BLUE(1), RED(-1);

        private final int multiplier;

        AllianceColor(int multiplier) {
            this.multiplier = multiplier;
        }

        public int getMultiplier() {
            return multiplier;
        }
    }

    public Shooter shooter;
    public Slides slides;
    public Intake intake;
    public FlowerMechanism flowerMechanism;
    public VoltageSensor voltageSensor;
    public ElapsedTime voltageTimer;
    private double cachedVoltage;
    public static double VOLTAGE_SENSOR_POLLING_RATE = 5; // Hertz

    public void init(HardwareMap hardwareMap, OP_MODE_TYPE opModeType){
        shooter = new Shooter(hardwareMap);
        slides = new Slides(hardwareMap);
        intake = new Intake(hardwareMap);
        flowerMechanism = new FlowerMechanism(hardwareMap);

        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        if (opModeType.equals(OP_MODE_TYPE.AUTO)) {
            initHasMovement();
        }
    }

    public void initHasMovement() {
        //drive.init(); ///Todo: ceva de genul, acum nu e
        //camera.init(); ///Todo: ceva de genul, acum nu e
        intake.initialize();
        shooter.initialize();
        slides.initialize();
        flowerMechanism.initialize();
    }

    public double getVoltage() {
//        return 12;
        // this is chopped for loop times
        if (voltageTimer == null) {
            cachedVoltage = voltageSensor.getVoltage();
            voltageTimer = new ElapsedTime();
            voltageTimer.reset();
        } else if (voltageTimer.milliseconds() > (1.0 / VOLTAGE_SENSOR_POLLING_RATE) * 1000) {
            cachedVoltage = voltageSensor.getVoltage();
        }
        if (Double.isNaN(cachedVoltage) || cachedVoltage <= 0) {
            cachedVoltage = 12;
        }
        return cachedVoltage;
    }

}
