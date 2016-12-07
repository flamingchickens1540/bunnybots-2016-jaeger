package robot;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.BooleanOutput;
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
	
	public static FloatOutput armBaseMotor;
	public static FloatOutput armClawMotor;
	public static FloatOutput armIntakeMotor;
	 
	public static void setup() throws ExtendedMotorFailureException {
		FloatInput armRampingConstant = JaegerMain.mainTuning.getFloat("Arm Ramping Constant", .02f);
		
		armBaseMotor = FRC.talonCAN(2).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		armClawMotor = FRC.talonCAN(1).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		armIntakeMotor = FRC.talonCAN(4).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		
		FloatOutput gunSpinupMotor = FRC.talonCAN(3).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		
		BooleanOutput gunPiston = FRC.solenoid(1);
		
		BooleanInput toggleGunPiston = JaegerMain.controlBinding.addBoolean("Toggle Gun Piston");
		
		FloatInput armBaseController = JaegerMain.controlBinding.addFloat("Arm Base Axis").deadzone(0.2f);
    	FloatInput armClawController = JaegerMain.controlBinding.addFloat("Arm Claw Axis").deadzone(0.2f);
    	FloatInput armIntakeController = JaegerMain.controlBinding.addFloat("Arm Intake Axis").deadzone(0.2f);
    	FloatInput armOuttakeController = JaegerMain.controlBinding.addFloat("Arm Outtake Axis").deadzone(0.2f);
    	FloatInput gunSpinupController = JaegerMain.controlBinding.addFloat("Gun Wheel Controller").deadzone(0.2f);
    	
    	
    	armBaseController.multipliedBy(0.5f).send(armBaseMotor);
    	armClawController.multipliedBy(0.5f).send(armClawMotor);
    	armIntakeController.minus(armOuttakeController).send(armIntakeMotor);
    	toggleGunPiston.send(gunPiston);
    	
    	gunSpinupController.send(gunSpinupMotor);
		
	}	
}