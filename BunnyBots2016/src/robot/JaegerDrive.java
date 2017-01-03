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
	
	private static EventOutput split(BooleanInput cond, EventOutput t, EventOutput f) {
        return () -> {
            if (cond.get()) {
                t.event();
            } else {
                f.event();
            }
        };
    }
	
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
		
		leftDrive.setWhen(0, FRC.startTele);
    	rightDrive.setWhen(0, FRC.startTele);
    	
		BooleanCell isShifted = new BooleanCell(false);
		
		//Controls
		FloatInput fineTurningControls = JaegerMain.controlBinding.addFloat("Fine Turning Axis").deadzone(0.2f);
    	FloatInput leftDriveControls = JaegerMain.controlBinding.addFloat("Drive Left Axis").deadzone(0.2f);
    	FloatInput rightDriveControls = JaegerMain.controlBinding.addFloat("Drive Right Axis").deadzone(0.2f);
    	FloatInput extendedForwards = JaegerMain.controlBinding.addFloat("Drive Forwards").deadzone(0.2f);
    	FloatInput extendedBackwards = JaegerMain.controlBinding.addFloat("Drive Backwards").deadzone(0.2f);
    	
    	BooleanInput shiftControls = JaegerMain.controlBinding.addBoolean("Shift Controls");
    	
    	FloatInput extended = extendedForwards.minus(extendedBackwards);
    
  
    	//Shifting
    	shiftControls.onPress(split(isShifted,isShifted.eventSet(false),isShifted.eventSet(true)));
    	isShifted.onPress(activateShift.eventSet(false));
    	//isShifted.onRelease(activateShift.eventSet(true));
    	Cluck.publish("isInHighGear", isShifted.asInput());
    	
    	//Tank Drive
		Drive.extendedTank(leftDriveControls.minus(fineTurningControls.multipliedBy(.7f)), rightDriveControls.plus(fineTurningControls.multipliedBy(.7f)), extended, leftDrive, rightDrive);
		
	}
}
