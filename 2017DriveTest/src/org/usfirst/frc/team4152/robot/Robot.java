package org.usfirst.frc.team4152.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {
	
	////RobotDrive System////
	RobotDrive myRobot = new RobotDrive(0, 1, 2, 3);
	Joystick stick = new Joystick(0);
	Encoder wheelTurn = new Encoder(1, 2, false);
	
	////DriveAccel Variables////
	double driveAccelMultiIncrease = 0.15;
	double driveAccelMultiInit = 0.4;
	double driveAccelMulti = driveAccelMultiInit;
	Timer driveTimeAccel = new Timer();
	
	////DumpLoad System////
	DigitalInput SwitchDumpAxisExceed = new DigitalInput(3);
	DigitalInput SwitchHoldAxisExceed = new DigitalInput(4);
	Spark DumpMotor = new Spark(10);
	Spark loadingMotor = new Spark(7);
	
	//stores button values in memory, makes the code easier to understand
    final int buttonA = 1;
    final int buttonB = 2;
    final int buttonX = 3;
    final int buttonY = 4;
    final int leftBumper = 5;
    final int rightBumper = 6;
    final int buttonBack = 7;
    final int buttonStart = 8;
    final int lsPush = 9;
    final int rsPush = 10;
    double wheelCircumference = 6 * Math.PI;

	public Robot() {
		myRobot.setExpiration(0.1);
	}

	@Override
	public void robotInit() {
		wheelTurn.setDistancePerPulse(wheelCircumference/2036);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * if-else structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomous() {
		wheelTurn.reset();
		drive(120);
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	@Override
	public void operatorControl() {
		myRobot.setSafetyEnabled(true);
		driveTimeAccel.start();
		wheelTurn.reset();
		while (isOperatorControl() && isEnabled()) {
			SmartDashboard.putBoolean("BoolTest", true);
			SmartDashboard.putInt("OpticEncoder", wheelTurn.get());
			SmartDashboard.putDouble("distance driven(their method)", wheelTurn.getDistance());
			SmartDashboard.putDouble("distance driven(our method)", getDriveDistance());
			myRobot.arcadeDrive(-stick.getRawAxis(0) * driveAccelMulti * 0.6, stick.getRawAxis(1) * driveAccelMulti);
			driveAccel();
			DumpBalls();
			loadingWhatever();
			Timer.delay(0.005); // wait for a motor update time
		}
	}
	
	public void DumpBalls()
	{
		if(stick.getRawButton(buttonX) && SwitchDumpAxisExceed.get() == false)
		{
			DumpMotor.set(0.5);
		} 
		else if(stick.getRawButton(buttonY) && SwitchHoldAxisExceed.get() == false)
		{
			DumpMotor.set(-0.5);
		}
		else
		{
			DumpMotor.set(0);
		}
	}
	
	double loadingPower = 0;
	
	public void loadingWhatever()
	{
		SmartDashboard.putDouble("LOAD POWAR", loadingPower);
		if(stick.getRawButton(buttonA))
		{
			loadingMotor.set(loadingPower);
		}
		else
		{
			loadingMotor.set(0);
		}
		if(stick.getRawButton(buttonB) && loadingPower > -0.9)
		{
			loadingPower += -0.1;
			Timer.delay(0.2);
		}
		else if(stick.getRawButton(buttonX) && loadingPower < 0.9)
		{
			loadingPower += 0.1;
			Timer.delay(0.2);
		}
	}
	
	public void driveAccel()
	{
		SmartDashboard.putDouble("Axis0", stick.getRawAxis(0));
		SmartDashboard.putDouble("Axis1", stick.getRawAxis(1));
		SmartDashboard.putDouble("DriveMulti", driveAccelMulti);
		//checks if axis 0 exceeds range of 0.1 in either direction, then same for axis 1. After those checks it checks the timer to see if it exceeds 0.1 seconds. Axis 0 and Axis 1 exist as an OR due to one being able to equal 0 and one equal 1 or -1. Timer is an AND due to it being required during check to not jump up by a delay of 0.005.
		if((stick.getRawAxis(0) < -0.1 || stick.getRawAxis(0) > 0.1) || (stick.getRawAxis(1) < -0.1 || stick.getRawAxis(1) > 0.1))
		{
			if(driveAccelMulti < 0.9 && driveTimeAccel.get() > 0.075)
			{
				driveAccelMulti += driveAccelMultiIncrease;
				driveTimeAccel.reset();
			}
		}
		else
		{
			driveAccelMulti = driveAccelMultiInit;
		}
	}
	public void drive(double distance)
	{
		double driven = 0;
		while(wheelTurn.getDistance()<distance)
		{
			myRobot.arcadeDrive(-1, 0);
		}
	}
	
	/**
	 * Runs during test mode
	 */
	@Override
	public void test() {
	}
	
	//Returns total distance driven in inches
	public double getDriveDistance(){
		return wheelCircumference * wheelTurn.get()/2036;
	}
}
