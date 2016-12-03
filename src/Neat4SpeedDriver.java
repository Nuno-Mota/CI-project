import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.genome.IGenome;
import scr.Action;
import scr.SensorModel;

public class Neat4SpeedDriver extends AbstractDriver {


    private NeuralNetwork _neuralNetwork;

    public Neat4SpeedDriver(NeuralNetwork neuralNetwork) {
        initialize();
        _neuralNetwork = neuralNetwork;
//        neuralNetwork = neuralNetwork.loadGenome();
    }

    private void initialize() {
        this.enableExtras(new AutomatedClutch());
        this.enableExtras(new AutomatedGearbox());
        //   this.enableExtras(new AutomatedRecovering());
        this.enableExtras(new ABS());
    }

    @Override
    public void loadGenome(IGenome genome) {
        if (genome instanceof DefaultDriverGenome) {
            DefaultDriverGenome myGenome = (DefaultDriverGenome) genome;
        } else {
            System.err.println("Invalid Genome assigned");
        }
    }

    @Override
    public double getAcceleration(SensorModel sensors) {
        //double[] sensorArray = new double[4];
        //double output = neuralNetwork.getOutput(sensors);
        return 1;
    }

    @Override
    public double getSteering(SensorModel sensors) {
        //Double output = neuralNetwork.getOutput(sensors);
        return 0.5;
    }

    @Override
    public String getDriverName() {
        return "NEAT4Speed Controller";
    }

    @Override
    public Action controlWarmUp(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlQualification(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlRace(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action defaultControl(Action action, SensorModel sensors) {
        if (action == null) {
            action = new Action();
        }

        double[] outputs  = _neuralNetwork.update(sensors);
        action.steering   = outputs[0];
        action.accelerate = outputs[1];
        action.brake      = outputs[2];

//        action.steering = DriversUtils.alignToTrackAxis(sensors, 0.5);
//        if (sensors.getSpeed() > 60.0D) {
//            action.accelerate = 0.0D;
//            action.brake = 0.0D;
//        }
//
//        if (sensors.getSpeed() > 70.0D) {
//            action.accelerate = 0.0D;
//            action.brake = -1.0D;
//        }
//
//        if (sensors.getSpeed() <= 60.0D) {
//            action.accelerate = (80.0D - sensors.getSpeed()) / 80.0D;
//            action.brake = 0.0D;
//        }
//
//        if (sensors.getSpeed() < 30.0D) {
//            action.accelerate = 1.0D;
//            action.brake = 0.0D;
//        }
        System.out.println("--------------" + getDriverName() + "--------------");
        System.out.println("Steering: " + action.steering);
        System.out.println("Acceleration: " + action.accelerate);
        System.out.println("Brake: " + action.brake);
        System.out.println("-----------------------------------------------");
        return action;
    }
}
