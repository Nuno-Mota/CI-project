import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.genome.IGenome;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResult;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import scr.Action;
import scr.SensorModel;

import java.io.IOException;
import java.io.Serializable;

public class Neat4SpeedDriver extends AbstractDriver implements Serializable {

    private boolean       _DEBUG = false;

    private NeuralNetwork _neuralNetwork = new NeuralNetwork();
    private double        _fitness;
    private double        _previousMaxDistRaced = 0;
    private int           _cyclesWithoutMovingForward = 0;
    private int           _cyclesGoingBack = 0;

    private Race          _race;


    public void setRace(Race race) { _race = race; }


    public Neat4SpeedDriver(NeuralNetwork neuralNetwork) {
        initialize();
        _neuralNetwork = neuralNetwork;
//        neuralNetwork = neuralNetwork.loadGenome();
    }

    public Neat4SpeedDriver(String path){
        initialize();
        _neuralNetwork = _neuralNetwork.loadGenome(path);
    }



    public double getFitness() { return _fitness; }


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
        return _neuralNetwork.getAcceleration();
    }

    @Override
    public double getSteering(SensorModel sensors) {
        //Double output = neuralNetwork.getOutput(sensors);
        return _neuralNetwork.getSteering();
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
    public Action defaultControl(Action action, SensorModel sensors) throws NullPointerException {
        if (action == null) {
            action = new Action();
        }

        if(_DEBUG)
            System.out.println("NEAT4SPEEDDRIVER: Checking stopping conditions and fitness");
//        System.out.println("Distance raced = " + sensors.getDistanceRaced());
//        System.out.println("Track Position = " + sensors.getTrackPosition());
//        System.out.println("Angle to Axis = " + sensors.getAngleToTrackAxis());
        if(sensors.getLastLapTime() != 0) {
            _fitness = 100 + sensors.getDistanceRaced()*(1 + 100/sensors.getLastLapTime());
            action.restartRace = true;
        }
        if(_previousMaxDistRaced >= sensors.getDistanceRaced())
            ++_cyclesWithoutMovingForward;
        else {
            _previousMaxDistRaced = sensors.getDistanceRaced();
            _cyclesWithoutMovingForward = 0;
        }
        if(Math.abs(sensors.getAngleToTrackAxis()) >= Math.PI/2)
            ++_cyclesGoingBack;
        else
            _cyclesGoingBack = 0;

        System.out.println(_cyclesWithoutMovingForward);
        if(_cyclesWithoutMovingForward > 500 || sensors.getTrackEdgeSensors()[0] == -1 || _cyclesGoingBack > 500) {
            System.out.println("Ending Race!!!!");
            _fitness = sensors.getDistanceRaced();
//            try {
//                Runtime.getRuntime().exec("killall torcs-bin");
//            } catch (IOException e) {
//                System.out.println("Couldn't kill TORCS");
//                System.exit(0);
//            }
        if(_DEBUG)
            System.out.println("NEAT4SPEEDDRIVER: Restarting race for bad conditions");

            action.restartRace = true;
            return action;
//            this.exit();
//            this.shutdown();
//            Thread.currentThread().destroy();



//            RaceResult result = new RaceResult();
//            result.setFinished(true);
//            _race.setResults(this, result);
//            _race.setTermination(Race.Termination.TICKS, 0);
//            throw new NullPointerException();
//            System.exit(0);
//            this.exit();
//            this.shutdown();
//        return action;
        }

        long startTime = 0, endTime = 0;
        if(_DEBUG)
            startTime = System.nanoTime();

        double[] outputs  = _neuralNetwork.update(sensors);

        if(_DEBUG)
            endTime = System.nanoTime();
        if(_DEBUG)
            System.out.println("Time Taken to update: " + (endTime - startTime)/1000000.0);


        action.steering   = outputs[0];
        action.accelerate = outputs[1];
        action.brake      = outputs[2];
//        action.steering   = 0;
//        action.accelerate = 1;
//        action.brake      = 0;
//
//
//        System.out.println("--------------" + getDriverName() + "--------------");
//        System.out.println("Steering: " + action.steering);
//        System.out.println("Acceleration: " + action.accelerate);
//        System.out.println("Brake: " + action.brake);
//        System.out.println("-----------------------------------------------");
        return action;
    }
}
