package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "XDriveTeleop", group = "7079")
public class XDriveTeleop extends OpMode {
    faltechBotXDrive robotXDrive = new faltechBotXDrive();
    DriveBrainXDrive driveBrain;
    Utility Utility;
    double fixedHeading = 0;



    @Override
    public void init() {
        robotXDrive.init(hardwareMap);
        driveBrain = new DriveBrainXDrive(robotXDrive, this);
    }

    @Override
    public void loop() {

        boolean T_Mode = false;
        Utility.deadStick(gamepad1.left_stick_x);
        Utility.deadStick(gamepad1.left_stick_y);
        Utility.deadStick(gamepad1.right_stick_x);
        double forward = 0.5 * gamepad1.left_stick_y;
        double strafe = -0.5 * gamepad1.left_stick_x;
        double rotate = -0.5 * gamepad1.right_stick_x;
        if(gamepad1.right_trigger==0.00){telemetry.addData("T-Mode Off", T_Mode);}
        if(gamepad1.right_trigger>0.00){
            T_Mode = true;
            telemetry.addData("T-Mode On", T_Mode);
            if(strafe>forward){
                forward = 0;
                strafe = strafe;
            }
            if(forward>strafe){
                strafe = 0;
                forward = forward;
            }
        }
        if(gamepad1.right_bumper){
          forward = forward*2;
          strafe = strafe*2;
          rotate = rotate*2;
        }
        double currentHeading = robotXDrive.getHeading(AngleUnit.DEGREES);
        telemetry.addData("Our Heading", currentHeading);
        if (gamepad1.left_trigger == 0.00){
            fixedHeading = currentHeading;
            if(gamepad1.a){
                fixedHeading += 180;
            }
        }
        if (gamepad1.left_trigger > 0.00){

            double headingError = Utility.wrapDegrees360(fixedHeading-currentHeading);
            if (headingError>45){
                headingError = 45;
            }
            if(headingError < -45){
                headingError = -45;
            }
            if (Math.abs(headingError)<.5){
                headingError = 0;
            }

            double rotationCorrection = headingError*0.3/45.00;
            if (rotationCorrection>0 && rotationCorrection<0.1){
                rotationCorrection = 0.1;
            }
            if (rotationCorrection<0&&rotationCorrection>-0.1){
                rotationCorrection = -0.1;
            }
            rotate = rotationCorrection + rotate;
            telemetry.addData("Fixed Heading", fixedHeading);
            telemetry.addData("Heading Error", headingError);
            telemetry.addData("Rotation Correction", rotationCorrection);

        }

        robotXDrive.setDrive(forward, strafe, rotate, 1);


       // if (gamepad1.a) {
         //   driveBrain.gyroDrive(.5, 10, 10);
           // driveBrain.gyroTurn(.5, 10);
        //
        // }
    }
}
