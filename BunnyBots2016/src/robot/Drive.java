package robot;

import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.frc.FRC;

public class Drive {
	public static void setup() throws ExtendedMotorFailureException {
		
		FloatOutput leftDriveFront = FRC.talonCAN(4).simpleControl();
    	FloatOutput leftDriveMiddle = FRC.talonCAN(5).simpleControl();
    	FloatOutput leftDriveBack = FRC.talonCAN(6).simpleControl();
    	FloatOutput rightDriveFront = FRC.talonCAN(1).simpleControl();
    	FloatOutput rightDriveMiddle = FRC.talonCAN(2).simpleControl();
    	FloatOutput rightDriveBack = FRC.talonCAN(3).simpleControl();
    	
    	FloatInput rampingConstant = JaegerMain.mainTuning.getFloat("Drive Ramping Constant", .02f);
    	FloatOutput leftDrive = leftDriveFront.combine(leftDriveMiddle).combine(leftDriveBack).negate().addRamping(rampingConstant.get(), FRC.constantPeriodic);
    	FloatOutput rightDrive = rightDriveFront.combine(rightDriveMiddle).combine(rightDriveBack).addRamping(rampingConstant.get(),FRC.constantPeriodic);

    	FloatInput driveLeftAxis = JaegerMain.controlBinding.addFloat("Drive Left Axis").deadzone(0.2f);
    	FloatInput driveRightAxis = JaegerMain.controlBinding.addFloat("Drive Right Axis").deadzone(0.2f);
    	
    	//Tank Drive
    	driveLeftAxis.send(leftDrive);
    	driveRightAxis.send(rightDrive);
		
	}

}
