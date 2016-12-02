package robot;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.BooleanOutput;
import ccre.channel.EventOutput;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.Drive;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.frc.FRC;
import ccre.frc.FRCApplication;
import ccre.instinct.AutonomousModeOverException;
import ccre.instinct.InstinctModule;

public class JaegerDrive {
	static FloatOutput leftDriveFront = FRC.talon(3);
	static FloatOutput leftDriveMiddle = FRC.talon(0);
	static FloatOutput leftDriveBack = FRC.talon(9).negate(); // Nobody knows why motor 9 is reversed
	static FloatOutput rightDriveFront = FRC.talon(1);
	static FloatOutput rightDriveMiddle = FRC.talon(4);
	static FloatOutput rightDriveBack = FRC.talon(8);
	
	static FloatInput driveRampingConstant = JaegerMain.mainTuning.getFloat("Drive Ramping Constant", .02f);
	public static FloatOutput leftDrive = leftDriveFront.combine(leftDriveMiddle).combine(leftDriveBack).negate().addRamping(driveRampingConstant.get(), FRC.constantPeriodic);
	public static FloatOutput rightDrive = rightDriveFront.combine(rightDriveMiddle).combine(rightDriveBack).addRamping(driveRampingConstant.get(),FRC.constantPeriodic);
	public static BooleanOutput activateShift = FRC.solenoid(0).combine(FRC.solenoid(1).invert());
	
	public static void setup() throws ExtendedMotorFailureException {
		
		

    	FloatInput leftDriveControls = JaegerMain.controlBinding.addFloat("Drive Left Axis").deadzone(0.2f);
    	FloatInput rightDriveControls = JaegerMain.controlBinding.addFloat("Drive Right Axis").deadzone(0.2f);
    	FloatInput leftTrigger = JaegerMain.controlBinding.addFloat("Left Trigger").deadzone(0.2f);
    	FloatInput rightTrigger = JaegerMain.controlBinding.addFloat("Right Trigger").deadzone(0.2f);
    	BooleanInput toggleShifting = JaegerMain.controlBinding.addBoolean("Toggle Shifting");
    	//BooleanInput shiftingControls = JaegerMain.controlBinding.addBoolean("shiftingControls");
    	
    	
    	FloatInput extended = leftTrigger.minus(rightTrigger);
  
    	//Shifting
    	BooleanCell shiftingOn = new BooleanCell(true); 
    	toggleShifting.onPress(shiftingOn.eventToggle());
    	Cluck.publish("Is in Low Gear", shiftingOn);
    	
    	FloatInput leftDriveVelocity = FRC.encoder(0, 1, false, FRC.startTele).derivative(); // Nobody knows how to get the speed of the motors either!
    	FloatInput rightDriveVelocity = FRC.encoder(2, 3, true, FRC.startTele).derivative();
    	Cluck.publish("Left Drive Velocity", leftDriveVelocity);
    	Cluck.publish("Right Drive Velocity", rightDriveVelocity);
    	
    	FloatInput autoShiftingLimit = JaegerMain.mainTuning.getFloat("Automatic Shifting Limit", 100);
    	BooleanInput shiftingControls = leftDriveVelocity.atLeast(autoShiftingLimit).and(rightDriveVelocity.atLeast(autoShiftingLimit)).and(shiftingOn);
    	
    	shiftingOn.send(activateShift);
    	
    	
    	//Tank Drive
		Drive.extendedTank(leftDriveControls, rightDriveControls, extended, leftDrive, rightDrive);
    	//leftDriveControls.send(leftDriveM iddle);
		
		
	}
	public class Test implements FRCApplication {
	    public void setupRobot() {

	        FRC.registerAutonomous(new InstinctModule() {
	            @Override
	            protected void autonomousMain() throws AutonomousModeOverException, InterruptedException {
	            	System.out.println("hello");
	            	leftDrive.set(-1);
	            	rightDrive.set(-1);
	                waitForTime(1000);
	                leftDrive.set(0);
	                rightDrive.set(0);
	            }
	        });
	    }
	}
}
