package frc.robot.subsystems;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {

    public static enum ShooterState {
        IDLE, AMP, SPEAKER, SPIT, OFF
    }

    private ShooterState state = ShooterState.IDLE;

    private final SparkFlex topMotor;
    private final SparkFlex bottomMotor;
    private final RelativeEncoder topEncoder;
    private final RelativeEncoder bottomEncoder;

    private final SparkMaxConfig shooterConfig;

    private final GenericEntry sb_topRpm;
    private final GenericEntry sb_bottomRpm;
    private final GenericEntry sb_targetState;

    public ShooterSubsystem() {

        // motors
        topMotor = new SparkFlex(ShooterConstants.shooter_motorTopId, SparkFlex.MotorType.kBrushless);
        bottomMotor = new SparkFlex(ShooterConstants.shooter_motorBottomId, SparkFlex.MotorType.kBrushless);

        // encoders
        topEncoder = topMotor.getEncoder();
        bottomEncoder = bottomMotor.getEncoder();

        // config
        shooterConfig = new SparkMaxConfig();
        shooterConfig.smartCurrentLimit(ShooterConstants.shooter_currentLimit);
        shooterConfig.inverted(false);

        topMotor.configure(shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        bottomMotor.configure(shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        // shuffleboard
        var tab = Shuffleboard.getTab("Dev");

        sb_topRpm = tab.add("Shooter Top RPM", 0)
            .withWidget(BuiltInWidgets.kGraph)
            .withSize(2,1)
            .withPosition(0,0)
            .getEntry();

        sb_bottomRpm = tab.add("Shooter Bottom RPM", 0)
            .withWidget(BuiltInWidgets.kGraph)
            .withSize(2,1)
            .withPosition(2,0)
            .getEntry();

        sb_targetState = tab.add("Shooter State", "")
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(2,1)
            .withPosition(0,2)
            .getEntry();
    }

    @Override
    public void periodic() {
        sb_topRpm.setDouble(topEncoder.getVelocity());
        sb_bottomRpm.setDouble(bottomEncoder.getVelocity());
        sb_targetState.setString(state.toString());
    }

    /** Converts shooter state to percent output */
    private double speedForState(ShooterState s) {
        switch(s) {
            case AMP:
                return ShooterConstants.shooter_speedAmp;
            case SPEAKER:
                return ShooterConstants.shooter_speedSpeaker;
            case SPIT:
                return ShooterConstants.shooter_speedSpit;
            case IDLE:
                return ShooterConstants.shooter_speedIdle;
            case OFF:
            default:
                return 0.0;
        }
    }

    /** Set shooter state and apply motor output */
    public void setState(ShooterState newState) {
        state = newState;
        double speed = speedForState(state);
        topMotor.set(speed);
        bottomMotor.set(speed);
    }

    public void stop() {
        setState(ShooterState.OFF);
    }

    public boolean atSpeed(double targetRpm) {
        return Math.abs(topEncoder.getVelocity() - targetRpm) < ShooterConstants.shooter_rpmTolerance
            && Math.abs(bottomEncoder.getVelocity() - targetRpm) < ShooterConstants.shooter_rpmTolerance;
    }
}
