package org.usfirst.frc.team4152.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
							//front left, back left, front right, back right.
	RobotDrive myRobot = new RobotDrive(0,1);
	Joystick stick = new Joystick(0);
	Joystick rightStick = new Joystick(1);
	//driving modes
	final boolean arcadeDrive = true;
	final boolean tankDrive = false;
	
	////DriveAccel Variables////
	double driveAccelMultiIncrease = 0.15;
	double driveAccelMultiInit = 0.4;
	double driveAccelMulti = driveAccelMultiInit;
	Timer driveTimeAccel = new Timer();
	
	////DumpLoad System////
	//DigitalInput SwitchDumpAxisExceed = new DigitalInput(5);
	//DigitalInput SwitchHoldAxisExceed = new DigitalInput(6);
	Spark dumpMotor = new Spark(4);
	Spark loadingMotor = new Spark(2);
	
	////CLIMB////
	Spark climbMotor = new Spark(5);
	
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
    
    ////Encoder Values////
	Encoder leftEncoder = new Encoder(1, 0, false);
	Encoder rightEncoder = new Encoder(4, 3 , false);
    final double wheelCircumference = 6 * Math.PI;
    
    ////Rio Vision////
    CameraServer rioVision = CameraServer.getInstance();
    
    ////Sensors////
    Accelerometer accel = new BuiltInAccelerometer();
    Gyro gyro = new AnalogGyro(0);
    int gyroOffset = -21;
    DigitalInput gearButton = new DigitalInput(6);
    
    //communications
    I2C piCom;
    
    
    //temp motors
    Talon leftTalon = new Talon(8);
    Talon rightTalon = new Talon(9);
    
    //Values for turning methods
    final double pivotCircumference = 25.5*Math.PI;
	final double pivotDegree = pivotCircumference / 360;
	final double speed = 0.48;//was 0.4
	final double preStop = speed / 4;
    
	//loading system
	double loadingPower = 0;
	
	public Robot() {
		myRobot.setExpiration(0.1);
	}

	@Override
	public void robotInit() {
		
		
		
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
		DougExitAuto.start();
		gyro.reset();
		SmartDashboard.putDouble("Gyro angle", gyro.getAngle());
		leftEncoder.reset();
		rightEncoder.reset();
		leftEncoder.setDistancePerPulse(wheelCircumference/2036);
		rightEncoder.setDistancePerPulse(wheelCircumference/2036);
		//turnRight(180);
		drive(75, 0.5);
		//drive(170, 0.6);
		/*while(!gearButton.get())
		{
			Timer.delay(0.001);/*Do Not Change(Causes Inability to Drive)*/
		//}
		/*Timer.delay(2);
		drive(12, -0.5);
		turnRight(45);
		drive(58, 0.5);
		turnLeft(45);
		drive(120, 0.7);*/
		/*while(!gearButton.get())
		{
			Timer.delay(0.001);
		}*/
		//turnRight(90);
		//turnRightVariableSpeed(90);
		
		SmartDashboard.putString("Gyro", gyro.getAngle() + ", 9000");
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	@Override
	public void operatorControl() {
		//move initialization lines to robotinit
		double speedController = 1;// stick.getRawAxis(2);
		leftEncoder.setDistancePerPulse(wheelCircumference/2036);
		rightEncoder.setDistancePerPulse(wheelCircumference/2036);
		myRobot.setSafetyEnabled(true);
		driveTimeAccel.start();
		leftEncoder.reset();
		rightEncoder.reset();
		rioVision.startAutomaticCapture();
		//remove while loop and replace with an if statement
		//this function is called repeatedly and we don't need to
		//nest an infinite loop in it.
		while (isOperatorControl() && isEnabled()) {
			speedController =1;// stick.getRawAxis(2);
			SmartDashboard.putBoolean("BoolTest", true);
			SmartDashboard.putInt("OpticEncoder", leftEncoder.get());
			SmartDashboard.putDouble("distance driven left wheels", leftEncoder.getDistance()*-1);
			SmartDashboard.putDouble("distance driven right wheels", rightEncoder.getDistance());
			SmartDashboard.putDouble("Speed Controller", speedController*100);
			//Move Value, Rotate value
			if(arcadeDrive) myRobot.arcadeDrive(stick.getRawAxis(1) * driveAccelMulti * speedController, -stick.getRawAxis(0) * speedController * -driveAccelMulti);
			else if(tankDrive) myRobot.tankDrive(stick.getRawAxis(1), rightStick.getRawAxis(1));
			driveAccel();
			dumpBalls();
			loadingSystem();
			climbSystem();
			Timer.delay(0.005); // wait for a motor update time
		}
	}
	
	/**
	 * used to control the climbing mechanism. Unused now.
	 */
	public void climbSystem()
	{
		if(stick.getRawButton(buttonB))
		{
			climbMotor.set(-1);
		}
		else if(Math.abs(stick.getRawAxis(5)) >= 0.33)
		{
			climbMotor.set(Math.abs(stick.getRawAxis(5))*-1);
		}
		else
		{
			climbMotor.set(0);
		}
		
	}
	
	/**
	 * used to control teh hopper dumper, now the climbing mechanism.
	 */
	public void dumpBalls()
	{
		if(stick.getRawButton(buttonY) /*&& SwitchDumpAxisExceed.get() == false*/)
		{
			leftTalon.set(1);
			rightTalon.set(-1);
		} 
		else if(stick.getRawButton(buttonA)/* && SwitchHoldAxisExceed.get() == false*/)
		{
			leftTalon.set(0.4);
			rightTalon.set(-0.4);
		}
		else
		{
			leftTalon.set(0);
			rightTalon.set(0);
		}
	}
	

	
	/**
	 * controls the loading system for the "fuel" balls.
	 */
	public void loadingSystem()
	{
		SmartDashboard.putDouble("LOAD POWAR", loadingPower);
		if(stick.getRawButton(rightBumper))
		{
			loadingMotor.set(-1);
			//System.out.println("right Bumper pushed");
		}
		else if(stick.getRawButton(leftBumper))
		{
			loadingMotor.set(1);
		}
		else
		{
			loadingMotor.set(0);
		}
	}
	
	/**
	 * used to accelerate the motors at a smooth speed.
	 */
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
	
	//Distance in inches
	/**
	 * Drives the robot at full speed for distance amount of inches.
	 * @param distance
	 */
	public void drive(double distance){
		drive(distance, 1);
	}
	
	//remove later
	Timer DougExitAuto = new Timer();
	
	//arcade drive -> -speed, encoder difference?
	//use encoder difference as amount to correct
	//difference = left - right
	/**
	 * Drives the robot distance amount of inches, at speed.
	 * @param distance
	 * @param speed
	 */
	public void drive(double distance, double speed){
		rightEncoder.reset();
		gyro.reset();
		Timer.delay(0.2);
		while(Math.abs(rightEncoder.getDistance())<distance && DougExitAuto.get() < 15)
		{
			SmartDashboard.putNumber("gyro", gyro.getAngle());
			SmartDashboard.putNumber("left encoder", leftEncoder.getDistance());
			if(gyro.getAngle() > 0) myRobot.arcadeDrive(-speed, 0.3);
			else if(gyro.getAngle() < 0) myRobot.arcadeDrive(-speed, -0.3);
			else myRobot.arcadeDrive(-speed, 0);
		}
		SmartDashboard.putDouble("distance driven left wheels", leftEncoder.getDistance()*-1);
		SmartDashboard.putDouble("distance driven right wheels", rightEncoder.getDistance());
		
		/*while(accel.getX() > 0.2)
		{
			SmartDashboard.putDouble("accel x", accel.getX());
			SmartDashboard.putDouble("accel z", accel.getZ());
			SmartDashboard.putDouble("accel y", accel.getY());
			//if(accel.getX() > 0.2) return;
			myRobot.arcadeDrive(speed,0);
		}*/
		
	}
	
	
	/**
	 * Turns the robot right.
	 * @param degrees
	 */
	public void turnRight(double degrees){
		gyroOffset = (int) -(Math.pow((degrees/29), 1.25)+4);
		boolean doneTurning = false;
		gyro.reset();
		Timer.delay(0.01);
		degrees = (degrees + gyroOffset) *100;
		int adjust = 0, swap = 1;
		
		while(!doneTurning){
			int gyroAngle = (int) (gyro.getAngle()*100);
			
			if(gyroAngle < degrees && adjust < 3){
				if(swap != 1) adjust++;
				if(adjust >=1){
					myRobot.arcadeDrive(0, -0.45);
				}else{
					myRobot.arcadeDrive(0, -speed);
				}				swap = 1;
			}else if(gyroAngle > degrees && adjust < 3){
				if(swap != 2) adjust++;
				if(adjust >=1) myRobot.arcadeDrive(0, 0.45);
				else myRobot.arcadeDrive(0, speed);
				swap = 2;
			}else{
				doneTurning = true;
			}
			
			SmartDashboard.putString("Gyro", gyroAngle + ", " + degrees);
			Timer.delay(0.001);
		} 
	}
	
	/**
	 * Use turnLeft(degrees) instead.
	 * @param degrees
	 */
	public void turnRightVariableSpeed(double degrees){
		leftEncoder.reset();
		double pivotDegrees = pivotDegree * degrees;
		double percentage = leftEncoder.getDistance() / pivotDegrees;
		while(Math.abs(leftEncoder.getDistance()) < pivotDegrees){
			if(percentage > 0.50)
			{
				myRobot.arcadeDrive(0,-1);
			}
			else if( percentage > 0.10)
			{
				myRobot.arcadeDrive(0,-0.75);
			}
			else
			{
				myRobot.arcadeDrive(0, -0.5);
			}
		}
	}
	
	/**
	 * Turns the robot Left.
	 * @param degrees
	 */
	//test the method to see if the move of the gyro changed anything.
	public void turnLeft(double degrees){
		gyroOffset = (int) -(Math.pow((degrees/29), 1.25)+4);
		boolean doneTurning = false;
		gyro.reset();
		Timer.delay(0.01);
		degrees = (degrees + gyroOffset) *100;
		int adjust = 0, swap = 1;
		
		while(!doneTurning){
			int gyroAngle = (int) (gyro.getAngle()*100);
			
			if(gyroAngle > -degrees && adjust < 3){
				if(swap != 1) adjust++;
				if(adjust >=1){
					myRobot.arcadeDrive(0, 0.45);
				}else{
					myRobot.arcadeDrive(0, speed);
				}				
				swap = 1;
			}else if(gyroAngle < -degrees && adjust < 3){
				if(swap != 2) adjust++;
				if(adjust >=1) myRobot.arcadeDrive(0, -0.45);
				else myRobot.arcadeDrive(0, -speed);
				swap = 2;
			}else{
				doneTurning = true;
			}
			
			SmartDashboard.putString("Gyro", gyroAngle + ", " + degrees);
			Timer.delay(0.001);
		}
	}
	
	
	/**
	 * Runs during test mode
	 */
	@Override
	public void test() {
		gyro.reset();
		while(true)	SmartDashboard.putNumber("gyro value", gyro.getAngle());
	}
}