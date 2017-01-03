package robot;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.BooleanOutput;
import ccre.channel.EventCell;
import ccre.channel.EventOutput;
import ccre.channel.FloatCell;
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
	
	private static EventOutput split(BooleanInput cond, EventOutput t, EventOutput f) {
        return () -> {
            if (cond.get()) {
                t.event();
            } else {
                f.event();
            }
        };
    }
	
	public static FloatOutput armBaseMotor; 
	public static FloatOutput armClawMotor;
	public static FloatOutput armIntakeMotor;
	public static FloatOutput gunSpinupMotor;
	
	public static BooleanOutput gunSolenoid = FRC.solenoid(2);
	
	 
	public static void setup() throws ExtendedMotorFailureException {
		
		PauseTimer fireTimer = new PauseTimer(100);
		
		FloatInput armRampingConstant = JaegerMain.mainTuning.getFloat("Arm Ramping Constant", .02f);
		
		armBaseMotor = FRC.talonCAN(2).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		armClawMotor = FRC.talonCAN(1).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		armIntakeMotor = FRC.talonCAN(4).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		gunSpinupMotor = FRC.talonCAN(3).simpleControl().addRamping(armRampingConstant.get(), FRC.constantPeriodic);
		
		armBaseMotor.setWhen(0, FRC.startTele);
		armClawMotor.setWhen(0, FRC.startTele);
		armIntakeMotor.setWhen(0, FRC.startTele);
		gunSpinupMotor.setWhen(0, FRC.startTele);
		
		BooleanInput toggleGunSolenoid = JaegerMain.controlBinding.addBoolean("Shooter");
		BooleanInput toggleGunMotor = JaegerMain.controlBinding.addBoolean("Gun Motor");
		
		FloatInput armBaseController = JaegerMain.controlBinding.addFloat("Arm Pivot").deadzone(0.2f);
    	FloatInput armClawController = JaegerMain.controlBinding.addFloat("Claw Pivot").deadzone(0.2f);
    	FloatInput armIntakeController = JaegerMain.controlBinding.addFloat("Succ").deadzone(0.2f);
    	FloatInput armOuttakeController = JaegerMain.controlBinding.addFloat("Spit").deadzone(0.2f);
    	
    	armBaseController.multipliedBy(0.5f).send(armBaseMotor);
    	armClawController.multipliedBy(1f).send(armClawMotor);
    	armIntakeController.minus(armOuttakeController).send(armIntakeMotor);
    	toggleGunSolenoid.and(toggleGunMotor).onPress(fireTimer);
    	
    	
    	fireTimer.triggerAtStart(gunSolenoid.eventSet(true));
    	fireTimer.triggerAtEnd(gunSolenoid.eventSet(false));
    	
    	toggleGunMotor.onPress(gunSpinupMotor.eventSet(.4f));
    	toggleGunMotor.onRelease(gunSpinupMotor.eventSet(0f));
		
	}	
}