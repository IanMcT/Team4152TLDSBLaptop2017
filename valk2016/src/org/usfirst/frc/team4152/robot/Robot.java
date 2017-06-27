package org.usfirst.frc.team4152.robot;

import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.AnalogGyro;
//import edu.wpi.first.wpilibj.vision.USBCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Ultrasonic.Unit;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
/*
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageInfo;
import com.ni.vision.NIVision.ThresholdData;
*/
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//import com.ni.vision.NIVision.ImageType;

/**
* This is a demo program showing the use of the RobotDrive class, specifically it 
* contains the code necessary to operate a robot with tank drive.
*
* The VM is configured to automatically run this class, and to call the
* functions corresponding to each mode, as described in the SampleRobot
* documentation. If you change the name of this class or the package after
* creating this project, you must also update the manifest file in the resource
* directory.
*
* WARNING: While it may look like a good choice to use for your code if you're inexperienced,
* don't. Unless you know what you are doing, complex code will be much more difficult under
* this system. Use IterativeRobot or Command-Based instead if you're new.
*/
public class Robot extends SampleRobot {
   RobotDrive myRobot;  // class that handles basic drive operations
   Joystick leftStick = new Joystick(0);  // set to ID 1 in
   Talon intakeMotor = new Talon(0);
   Talon holdMotor = new Talon(1);
   Talon shootMotor = new Talon(2);
   DoubleSolenoid battleaxes = new DoubleSolenoid(0,1);
   Timer autoTime = new Timer();
   Compressor air = new Compressor(60);
   boolean dirTog = false;

   Ultrasonic ultra = new Ultrasonic(0,1,Unit.kMillimeters);
   //location
   float x = 0;
   float y = 0;
   float z = 0;
   
   //CameraServer cam = CameraServer.getInstance();
   
   //camera stuff
   //USBCamera camera = new USBCamera("cam0");
   
   double spdControl = 0.50;
   AnalogGyro gyro = new AnalogGyro(0);
   Accelerometer accel = new ADXL345_I2C(I2C.Port.kOnboard, Accelerometer.Range.k4G);
   NetworkTable camInput = NetworkTable.getTable("myContoursReport");
   
   int rightStickY = 5;
   int buttonA = 1;
   int buttonB = 2;
   int buttonX = 3;
   int buttonY = 4;
   int leftBumper = 5;
   int rightBumper = 6;
   int buttonBack = 7;
   int buttonStart = 8;
   int lsPush = 9;
   int rsPush = 10;    
   int selector = 0;
   double xDiff = 1;
   double yDiff = 1;
   
   CameraServer cam = CameraServer.getInstance();
   public void disabled()
   {
       while(isDisabled())
       {
           SmartDashboard.putString("I'm", "Alive!!!!!111");
           if(leftStick.getRawButton(buttonA))
           {
               selector = 0;
           }
           if(leftStick.getRawButton(buttonB))
           {
               selector = 1;
           }
           acceleration += accel.getX();
           distance += acceleration;
           SmartDashboard.putDouble("distance", (double)distance);

           SmartDashboard.putInt("selector", selector);
           Timer.delay(0.1);
       }
   }
   
   public Robot() {
       //myRobot = new RobotDrive(3,1,4,2); //orig con
       myRobot = new RobotDrive(3,4);
       //myRobot.setExpiration(0.1);
       
       gyro.reset();
       gyro.calibrate();
       //cam.setQuality(25);
       //cam.startAutomaticCapture();
       
       
       air.start();
       //air.stop();
       battleaxes.set(DoubleSolenoid.Value.kForward);
       Diffchangetime.start();
       
       ultra.setAutomaticMode(true);
   }
   
    
   public void RobotInit()
   {
       
   }

   double acceleration = 0;
   double distance = 0;
   
   @SuppressWarnings("deprecation")
    public void autonomous()
   {
       distance = 0;
       autoTime.reset();
       gyro.reset();
       autoTime.start();
       
       //The low bar double shuffle
       //(yes all autos are going to have random joke names.
       if(selector == 0)
       {
           //first obsticle
           /*while (autoTime.get() < 5)
           {
               SmartDashboard.putString("hurr", "durr");
           }*/
           
           battleaxes.set(DoubleSolenoid.Value.kForward); //arm down
           while(autoTime.get() < 4) //forward first time
           {
               SmartDashboard.putDouble("timer", autoTime.get());
               myRobot.tankDrive(0.8, 0.8); //untested, for low bar arm end
               //myRobot.tankDrive(-1, -1); //This was for rockwall going backwards
               acceleration += accel.getX();
               distance += acceleration;
               SmartDashboard.putDouble("distance", (double)distance);
               Timer.delay(0.005);
           }
           battleaxes.set(DoubleSolenoid.Value.kForward); //arms up for turn
           
           //turn
           
           /*
           float turnValue = (float)gyro.getAngle() + 30;
           do 
           {
               if(gyro.getAngle() < turnValue - 1)
               {
                   myRobot.arcadeDrive(0,-0.4);
               }
               else if(gyro.getAngle() > turnValue + 1)
               {
                   myRobot.arcadeDrive(0,0.4);
               }
               else if((turnValue + 1 > gyro.getAngle()) && (turnValue - 1 < gyro.getAngle()))
               {
                   Timer.delay(0.5);
               }
               Timer.delay(0.005);
           } while(!(turnValue + 1 > gyro.getAngle()) && !(turnValue - 1 < gyro.getAngle()));
           */
       }
       //Obstacle X = F*A*S*T2
       //for things like moat, rockwall, etc
       if(selector == 1)
       {
           battleaxes.set(DoubleSolenoid.Value.kForward); //bring arms up
           while(autoTime.get() < 3)
           {
               myRobot.tankDrive(0.4, -0.4);
           }
           while(autoTime.get() < 4)
           {
               myRobot.tankDrive(0.7, -0.7);
           }
           
       }
       while(selector == 2 && autoTime.get() < 3)
       {
           myRobot.arcadeDrive(-0.7,0);
       }
       while(selector == 3 && autoTime.get() < 1)
       {
           myRobot.tankDrive(0.5, -0.5);
           Timer.delay(0.3);
           myRobot.tankDrive(0.2, -0.2);
           battleaxes.set(DoubleSolenoid.Value.kReverse);
           Timer.delay(0.15);
           battleaxes.set(DoubleSolenoid.Value.kForward);
           Timer.delay(0.5);
           myRobot.tankDrive(0.6, -0.6);
           Timer.delay(0.2);
           
       }
       
       
       SmartDashboard.putDouble("Xmove", (double)x);
       SmartDashboard.putDouble("Ymove", (double)y);
       SmartDashboard.putDouble("Zmove", (double)z);
       autoTime.stop();
   }
   
   
   
   Timer Diffchangetime = new Timer();
   
   public void diffChange()
   {
       if(Diffchangetime.get() > 1)
       {
           if(leftStick.getRawButton(leftBumper) && leftStick.getRawButton(buttonBack))
           {
               if(xDiff == 1)
                   xDiff = 0.8;
               else if(xDiff == 0.8)
                   xDiff = 0.6;
               else if(xDiff == 0.6)
                   xDiff = 1;
               
               Diffchangetime.reset();
               SmartDashboard.putDouble("xDiff", xDiff);
           }
           
           if(leftStick.getRawButton(rightBumper) && leftStick.getRawButton(buttonBack))
           {
               if(yDiff == 1)
                   yDiff = 0.8;
               else if(yDiff == 0.8)
                   yDiff = 0.6;
               else if(yDiff == 0.6)
                   yDiff = 1;
               
               Diffchangetime.reset();

               SmartDashboard.putDouble("yDiff", yDiff);
           }
       }
   }
   /**
    * Runs the motors with tank steering.
    */
   @SuppressWarnings("deprecation")
    public void operatorControl() {
       //myRobot.setSafetyEnabled(true);
       air.stop();
       //cam.setQuality(20);
       //cam.startAutomaticCapture();
       SmartDashboard.putString("start", "true");
       while (isOperatorControl() && isEnabled()) {
           SmartDashboard.putInt("POV", leftStick.getPOV());
           SmartDashboard.putDouble("Sensor range", ultra.getRangeMM());
           System.out.println(ultra.getDistanceUnits());
           directionToggle();
           if(dirTog == false)
               myRobot.arcadeDrive(leftStick.getY() * yDiff, -leftStick.getX() * xDiff);
           else
               myRobot.arcadeDrive(-leftStick.getY() * yDiff, -leftStick.getX() * xDiff);
           SmartDashboard.putBoolean("move switch", dirTog);
           intakeMotor.set(0);
           holdMotor.set(0); 
           
           shootMotor.set(0);
           leftTrigButton();
           rightTrigButton();
           compressToggle();
           spdControl();
           axeToggle();
           camReset();
           diffChange();
           //roboSwitch();
           //air.stop();
           //Beta Testing
           getPOV();
           getGyro();
           Timer.delay(0.005);        // wait for a motor update time
       }
   }
   
   double aggroTurnMod = 0.5;
   double aggroTurnChange = 0.25;
   
   public void aggroTurn()
   {
	   if(leftStick.getPOV() != -1)
	   {
		   if(leftStick.getPOV() > 180)
		   {
			   
		   }
		   else
		   {
			   
		   }
	   }
   }
   
   public void getGyro()
   {
	   SmartDashboard.putDouble("gyro", gyro.getAngle());
	   SmartDashboard.putDouble("Xturn", -leftStick.getX());
   }
   
   public void getPOV()
   {
	   SmartDashboard.putInt("POV", leftStick.getPOV());
   }
   
   
   public void roboSwitch()
   {
       if(isDisabled())
       {
           
       }
   }
   
   public void camReset()
   {
       /*if(leftStick.getRawButton(buttonA))
       {
           cam.setQuality(25);
           cam.startAutomaticCapture();
       }*/
   }
   
   @SuppressWarnings("null")
    public void spdControl()
   {
       if(leftStick.getRawAxis(rightStickY) > 0.1 || leftStick.getRawAxis(rightStickY) < -0.1)
       {
           spdControl += -leftStick.getRawAxis(rightStickY)/1000;
       }
       /*
       if(leftStick.getRawButton(buttonA))
           visionProcess();
       */
       SmartDashboard.putDouble("SPD", spdControl);
   }
    
   boolean axetf = false;
   
   public void axeToggle()
   {
       
       if(leftStick.getRawButton(buttonB) && battleaxes.get() == DoubleSolenoid.Value.kReverse && axetf == false)
       {
           battleaxes.set(DoubleSolenoid.Value.kForward);
           axetf = true;
       }
       else if(leftStick.getRawButton(buttonB) && battleaxes.get() == DoubleSolenoid.Value.kForward && axetf == false)
       {
           battleaxes.set(DoubleSolenoid.Value.kReverse);
           axetf = true;
       }
       if(!leftStick.getRawButton(buttonB))
       {
           axetf = false;
       }
       SmartDashboard.putBoolean("axes switch", axetf);
       SmartDashboard.putString("axe const", battleaxes.get().toString());
   }
   
   public void leftTrigButton()
   {
       if(leftStick.getRawButton(rightBumper))
       {
           intakeMotor.set(-1);
           holdMotor.set(-1.0);
           shootMotor.set(1.0);
       }
   }
   
   
   public void rightTrigButton()
   {
       
       if(leftStick.getRawButton(5))
       {
           holdMotor.set(1);
           shootMotor.set(-1);
           intakeMotor.set(0.05);
           Timer.delay(0.4);
           holdMotor.set(-1);
           shootMotor.set(-spdControl);
           Timer.delay(1);
           intakeMotor.set(-1);
           Timer.delay(0.25);
       }
       
   }
   
   boolean dirswitch = false;
   
   public void directionToggle()
   {
       if(leftStick.getRawButton(buttonY) && dirTog == false && dirswitch == false)
       {
           dirTog = true;
           dirswitch = true;
           //Timer.delay(0.2);
       }
       else if(leftStick.getRawButton(buttonY) && dirTog == true && dirswitch == false)
       {
           dirTog = false;
           dirswitch = true;
           //Timer.delay(0.2);
       }
       if(!leftStick.getRawButton(buttonY))
       {
           dirswitch = false;
       }
   }
   
   public void compressToggle()
   {
           if(leftStick.getRawButton(lsPush))
           {
               air.start();
           }
           if(leftStick.getRawButton(rsPush))
           {
               air.stop();
           }
   }
}
