// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.concurrent.PriorityBlockingQueue;

import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.GamePad;
import frc.robot.Constants.Laptop;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.ScaledArcadeDriveCommand;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ExampleSubsystem;
//import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private DriveSubsystem m_driveSubsystem = null;
  private IntakeSubsystem m_IntakeSubsystem = null;
  private ArmSubsystem m_ArmSubsystem = null;
  //private SensorArraySubsystem m_sensorArray = null;
  private Compressor m_pcmCompressor = null;
  private Solenoid m_exampleSolenoidPCM = null;
  private boolean m_solenoidState = false;
  
  // Operator interface
  private Joystick m_gamePad = null;
  private JoystickButton m_JSRightButton; 
  
  // Cameras
  private UsbCamera m_camera1;

  //private PhotonCamera m_photonCamera;

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Subsystems (comment out to exclude a subsystem from the robot)
    m_driveSubsystem = new DriveSubsystem();
    m_ArmSubsystem = new ArmSubsystem();
    m_IntakeSubsystem = new IntakeSubsystem();

    //m_sensorArray = new SensorArraySubsystem();
    //m_pcmCompressor = new Compressor(0, CanID.kLeftLeader);
   // m_exampleSolenoidPCM = new Solenoid(PneumaticsModuleType.CTREPCM, 1);
    // Controllers (comment out to exclude a controller from the laptop)
    m_gamePad = new Joystick(Laptop.UsbPort.kGamePad);

    // Cameras (comment out to exclude a camera from the robot);
    //m_camera1 = CameraServer.startAutomaticCapture(0);
    if (m_camera1 != null) {
      m_camera1.setResolution(320, 240);
    }

   // m_photonCamera = new PhotonCamera("photonvision/Microsoft_LifeCam_HD-3000");

    // Configure the trigger bindings
    configureButtonBindings();
    configureDefaultCommands();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureButtonBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
    //m_driverController.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());
    if (m_IntakeSubsystem != null && m_gamePad != null){
      new JoystickButton(m_gamePad, GamePad.Button.kA)
        .whileTrue(Commands.run(()-> m_IntakeSubsystem.IntakeGo(0.8), m_IntakeSubsystem))
        .onFalse(Commands.run(() -> m_IntakeSubsystem.IntakeStop(), m_IntakeSubsystem))
      ;

      new JoystickButton(m_gamePad, GamePad.Button.kB)
        .whileTrue(Commands.run(()-> m_IntakeSubsystem.IntakeGo(-0.4), m_IntakeSubsystem))
        .onFalse(Commands.run(() -> m_IntakeSubsystem.IntakeStop(), m_IntakeSubsystem))
      ;
    }

    if (m_ArmSubsystem != null && m_gamePad != null){
      
      new JoystickButton(m_gamePad, GamePad.Button.kX)
        .whileTrue(Commands.run(()-> m_ArmSubsystem.ArmForward(0.8),m_ArmSubsystem)) 
      ;

      new JoystickButton(m_gamePad, GamePad.Button.kY)
        .whileTrue(Commands.run(()-> m_ArmSubsystem.ArmBackward(-0.4), m_ArmSubsystem))
      ;
    }
  
    
  ;
  }
  
  
  private void configureDefaultCommands() {

    if (m_driveSubsystem != null && m_gamePad != null) {
      m_driveSubsystem.setDefaultCommand(new ScaledArcadeDriveCommand(m_driveSubsystem, 
        () -> -m_gamePad.getRawAxis(GamePad.LeftStick.kUpDown)* 0.75, 
        () -> m_gamePad.getRawAxis(GamePad.LeftStick.kLeftRight) * 0.75
      ));
    }
  }
  
  
  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }
}
