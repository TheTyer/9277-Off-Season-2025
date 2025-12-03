package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;

public class ShooterCmd extends Command {

    private final ShooterSubsystem shooterSubsystem;
    private final ShooterSubsystem.ShooterState targetState;

    /**
     * Command to set the shooter to a specific state while the button is held.
     * @param shooterSubsystem The ShooterSubsystem instance
     * @param targetState The desired ShooterState (AMP, SPEAKER, SPIT, etc.)
     */
    public ShooterCmd(ShooterSubsystem shooterSubsystem, ShooterSubsystem.ShooterState targetState) {
        this.shooterSubsystem = shooterSubsystem;
        this.targetState = targetState;

        addRequirements(shooterSubsystem);
    }

    @Override
    public void initialize() {
        shooterSubsystem.setState(targetState);
    }

    @Override
    public void execute() {
        // Keep motors running at the desired state
        shooterSubsystem.setState(targetState);
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the shooter when button released
        shooterSubsystem.stop();
    }

    @Override
    public boolean isFinished() {
        // Never finishes on its own; runs while button held
        return false;
    }
}
