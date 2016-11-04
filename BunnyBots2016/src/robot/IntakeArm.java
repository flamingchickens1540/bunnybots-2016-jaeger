package robot;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.EventCell;
import ccre.channel.EventOutput;
import ccre.channel.FloatInput;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.ctrl.StateMachine;
import ccre.ctrl.ExtendedMotor.OutputControlMode;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;
import ccre.instinct.InstinctModule;
import ccre.log.Logger;
import ccre.timers.PauseTimer;



public class IntakeArm {
	
	public static void setup() throws ExtendedMotorFailureException {
		FloatInput armRampingConstant = JaegerMain.mainTuning.getFloat("Arm Ramping Constant", .02f);
		
		FloatOutput armBaseMotor = FRC.talonCAN(1).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		FloatOutput armClawMotor = FRC.talonCAN(2).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		FloatOutput armIntakeMotor = FRC.talonCAN(3).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		
		FloatInput armBaseController = JaegerMain.controlBinding.addFloat("Arm Base Axis").deadzone(0.2f);
    	FloatInput armClawController = JaegerMain.controlBinding.addFloat("Arm Claw Axis").deadzone(0.2f);
    	FloatInput armIntakeController = JaegerMain.controlBinding.addFloat("Arm Intake Axis").deadzone(0.2f);
    	
    	armBaseController.send(armBaseMotor);
    	armClawController.send(armClawMotor);
    	armIntakeController.send(armIntakeMotor);
		
	}	
}