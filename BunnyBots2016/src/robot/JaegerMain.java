package robot;

import ccre.channel.BooleanOutput;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.ctrl.binding.ControlBindingCreator;
import ccre.frc.FRC;
import ccre.frc.FRCApplication;
import ccre.instinct.AutonomousModeOverException;
import ccre.instinct.InstinctModule;
import ccre.log.Logger;
import ccre.tuning.TuningContext;

/**
 * This is the core class of a CCRE project. The CCRE launching system will make
 * sure that this class is loaded, and will have set up everything else before
 * loading it. If you change the name, use Eclipse's rename functionality. If
 * you don't, you will have to change the name in Deployment.java.
 *
 * Make sure to set {@link #TEAM_NUMBER} to your team  number.
 */

public class JaegerMain implements FRCApplication {
	
	public static final ControlBindingCreator controlBinding = FRC.controlBinding();
	public static final TuningContext mainTuning = new TuningContext("MainTuning").publishSavingEvent();
    /**
     * This is where you specify your team number. It is used to find your
     * roboRIO when you download code.
     */
    public static final int TEAM_NUMBER = 1540;
    
    @Override
    public void setupRobot() throws ExtendedMotorFailureException {

    	JaegerDrive.setup();
    	IntakeArm.setup();
    
     
    
    FRC.registerAutonomous(new InstinctModule() {
        @Override
        protected void autonomousMain() throws AutonomousModeOverException, InterruptedException {
        	
        	//Swing arm up and the claw around
        	IntakeArm.armBaseMotor.set(-.7f);
        	IntakeArm.armClawMotor.set(.7f);
        	waitForTime(700);
        	IntakeArm.armBaseMotor.set(0);
        	IntakeArm.armClawMotor.set(0);
        	
        	//Swing back down and point claw into the intake area
        	IntakeArm.armBaseMotor.set(.7f);
        	IntakeArm.armClawMotor.set(-.7f);
        	waitForTime(700);
        	IntakeArm.armBaseMotor.set(0);
        	IntakeArm.armClawMotor.set(0);
        	
        	//Intake the bunny
        	IntakeArm.armIntakeMotor.set(.7f);
        	waitForTime(1500);
        	IntakeArm.armIntakeMotor.set(0);
        	
        	//Drive until the optical sensor is tripped
        	JaegerDrive.leftDrive.set(.75f);
        	JaegerDrive.rightDrive.set(.75f);
            waitUntil(5000, FRC.digitalInput(9, FRC.constantPeriodic));
        	JaegerDrive.leftDrive.set(0);
        	JaegerDrive.rightDrive.set(0);
        	
        	//Swing arm back up and the claw around
        	IntakeArm.armBaseMotor.set(-.7f);
        	IntakeArm.armClawMotor.set(.7f);
        	waitForTime(700);
        	IntakeArm.armBaseMotor.set(0);
        	IntakeArm.armClawMotor.set(0);
        	
        	//Release the bunny into the trash can
        	IntakeArm.armIntakeMotor.set(-1f);
        	waitForTime(2000);
        	IntakeArm.armIntakeMotor.set(0);
        	
        }
    });
    }
}
