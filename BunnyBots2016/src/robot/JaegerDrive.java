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
import ccre.ctrl.StateMachine;
import ccre.frc.FRC;
import ccre.frc.FRCApplication;
import ccre.instinct.AutonomousModeOverException;
import ccre.instinct.InstinctModule;
import ccre.timers.PauseTimer;

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
	public static BooleanOutput activateShift = FRC.solenoid(0).combine(FRC.solenoid(1));
	
	public static void setup() throws ExtendedMotorFailureException {
		 
		

    	FloatInput leftDriveControls = JaegerMain.controlBinding.addFloat("Drive Left Axis").deadzone(0.2f);
    	FloatInput rightDriveControls = JaegerMain.controlBinding.addFloat("Drive Right Axis").deadzone(0.2f);
    	FloatInput leftTrigger = JaegerMain.controlBinding.addFloat("Left Trigger").deadzone(0.2f);
    	FloatInput rightTrigger = JaegerMain.controlBinding.addFloat("Right Trigger").deadzone(0.2f);
    	BooleanInput toggleShifting = JaegerMain.controlBinding.addBoolean("Toggle Shifting");
    	//BooleanInput shiftingControls = JaegerMain.controlBinding.addBoolean("shiftingControls");
    	
    	
    	FloatInput extended = leftTrigger.minus(rightTrigger);
  
    	//Shifting
    	StateMachine gearStates = new StateMachine(0,
				"lowGear",
				"highGear");
    	
    	BooleanCell shiftingOn = new BooleanCell(true); 
    	toggleShifting.onPress(shiftingOn.eventToggle());
    	
    	FloatInput leftDriveVelocity = FRC.encoder(0, 1, false, FRC.startTele).derivative(); // Nobody knows how to get the speed of the motors either!
    	FloatInput rightDriveVelocity = FRC.encoder(2, 3, true, FRC.startTele).derivative();
    	Cluck.publish("Left Drive Velocity", leftDriveVelocity);
    	Cluck.publish("Right Drive Velocity", rightDriveVelocity);
    	
    	BooleanCell canShiftBack = new BooleanCell(false);
    	
    	FloatInput lowSpeedShiftingLimit = JaegerMain.mainTuning.getFloat("Low Speed Shifting Limit", 550);
    	FloatInput highSpeedShiftingLimit = JaegerMain.mainTuning.getFloat("High Speed Shifting Limit", 150);
    	
    	//leftDriveVelocity.absolute().atLeast(lowSpeedShiftingLimit).onPress(gearStates.getStateSetEvent("highGear"));
    	
    	PauseTimer shiftingTimer = new PauseTimer(1000);
		gearStates.getIsState("highGear").and(canShiftBack).onPress(shiftingTimer);
		shiftingTimer.triggerAtStart(canShiftBack.eventSet(false));
		shiftingTimer.triggerAtEnd(canShiftBack.eventSet(true));
		
		//leftDriveVelocity.absolute().atLeast(highSpeedShiftingLimit).and(canShiftBack).send(gearStates.getStateSetEvent("lowGear"));
    	
    	gearStates.getIsState("lowGear").send(activateShift);
    	Cluck.publish("Is in Low Gear", gearStates.getIsState("lowGear"));
    	
    	//Tank Drive
		Drive.extendedTank(leftDriveControls, rightDriveControls, extended, leftDrive, rightDrive);
    	//leftDriveControls.send(leftDriveMiddle);
		
		
	}
	public class Test implements FRCApplication {
	    public void setupRobot() {

	        FRC.registerAutonomous(new InstinctModule() { 
	            @Override
	            protected void autonomousMain() throws AutonomousModeOverException, InterruptedException {
	            	System.out.println("hello");
	            	leftDrive.set(.5f);
	            	//rightDrive.set(1);
	                waitUntil(FRC.digitalInput(9, FRC.constantPeriodic));
	                leftDrive.set(0);
	                //rightDrive.set(0);
	                //waitForTime(1000);
	            }
	        });
	    }
	}
}
