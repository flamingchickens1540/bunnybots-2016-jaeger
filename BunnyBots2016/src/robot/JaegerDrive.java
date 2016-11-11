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

public class JaegerDrive {
	public static void setup() throws ExtendedMotorFailureException {
		
		FloatOutput leftDriveFront = FRC.talon(3);
    	FloatOutput leftDriveMiddle = FRC.talon(0);
    	FloatOutput leftDriveBack = FRC.talon(9).negate(); // Nobody knows why motor 9 is reversed
    	FloatOutput rightDriveFront = FRC.talon(1);
    	FloatOutput rightDriveMiddle = FRC.talon(4);
    	FloatOutput rightDriveBack = FRC.talon(8);
    	
    	FloatInput driveRampingConstant = JaegerMain.mainTuning.getFloat("Drive Ramping Constant", .02f);
    	FloatOutput leftDrive = leftDriveFront.combine(leftDriveMiddle).combine(leftDriveBack).negate().addRamping(driveRampingConstant.get(), FRC.constantPeriodic);
    	FloatOutput rightDrive = rightDriveFront.combine(rightDriveMiddle).combine(rightDriveBack).addRamping(driveRampingConstant.get(),FRC.constantPeriodic);
    	
    	BooleanOutput activateShift = FRC.solenoid(0).combine(FRC.solenoid(1).invert());

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
    	/**
    	FloatInput leftDriveVelocity = FRC.encoder(aChannel, bChannel, reverse, resetWhen); // Nobody knows how to get the speed of the motors either
    	FloatInput rightDriveVelocity = FRC.encoder(aChannel, bChannel, reverse, resetWhen);
    	
    	FloatInput autoShiftingLimit = JaegerMain.mainTuning.getFloat("Automatic Shifting Limit", 100);
    	BooleanInput shiftingControls = leftDriveVelocity.atLeast(autoShiftingLimit).and(rightDriveVelocity.atLeast(autoShiftingLimit)).and(shiftingOn);
    	**/
    	
    	
    	shiftingOn.send(activateShift);
    	
    	
    	//Tank Drive
		Drive.extendedTank(leftDriveControls, rightDriveControls, extended, leftDrive, rightDrive);
    	//leftDriveControls.send(leftDriveMiddle);
	}
}
