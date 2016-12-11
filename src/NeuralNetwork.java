import scr.SensorModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private boolean             _DEBUG = false;

    private static final long serialVersionUID = -88L;
    private List<NeuralNetworkNeuron> _phenotypeNeurons = new ArrayList<>();

    private double _acceleration = 0;
    private double _breaking     = 0;
    private double _steering     = 0;

    NeuralNetwork() {}
    NeuralNetwork(List<NeuralNetworkNeuron> phenotypeNeurons) {
        _phenotypeNeurons = phenotypeNeurons;
    }

    public double getOutput(SensorModel a) {
        return 0.5;
    }

    public double getAcceleration() {
        return _acceleration;
    }

    public double getBreaking() {
        return _breaking;
    }

    public double getSteering() {
        return _steering;
    }

    //Store the state of this neural network
    public void storeGenome() {
        ObjectOutputStream out = null;
        try {
            //create the memory folder manually
            out = new ObjectOutputStream(new FileOutputStream("memory/mydriver.mem"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.writeObject(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load a neural network from memory
    public NeuralNetwork loadGenome(String path) {

        // Read from disk using FileInputStream
        FileInputStream f_in = null;
        try {
            f_in = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Read object using ObjectInputStream
        ObjectInputStream obj_in = null;
        try {
            obj_in = new ObjectInputStream(f_in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read an object
        try {
            if (obj_in != null) {
                return (NeuralNetwork) obj_in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double[] update(SensorModel sensorData) {

        //36 Inputs
        if(_DEBUG)
            System.out.println("Neural Network getting sensor data");
        double[] inputs  = {sensorData.getAngleToTrackAxis()/Math.PI, sensorData.getSpeed()/315,
                            sensorData.getTrackEdgeSensors()[2]/200, sensorData.getTrackEdgeSensors()[4]/200,
                            sensorData.getTrackEdgeSensors()[6]/200, sensorData.getTrackEdgeSensors()[8]/200,
                            sensorData.getTrackEdgeSensors()[9]/200, sensorData.getTrackEdgeSensors()[10]/200,
                            sensorData.getTrackEdgeSensors()[12]/200, sensorData.getTrackEdgeSensors()[14]/200,
                            sensorData.getTrackEdgeSensors()[16]/200, sensorData.getTrackPosition(),
                            sensorData.getLateralSpeed()/150, sensorData.getZSpeed()/150,
                            sensorData.getWheelSpinVelocity()[0]/45, sensorData.getWheelSpinVelocity()[1]/45,
                            sensorData.getWheelSpinVelocity()[2]/45, sensorData.getWheelSpinVelocity()[3]/45,
                            sensorData.getOpponentSensors()[1]/200, sensorData.getOpponentSensors()[4]/200,
                            sensorData.getOpponentSensors()[6]/200, sensorData.getOpponentSensors()[8]/200,
                            sensorData.getOpponentSensors()[9]/200, sensorData.getOpponentSensors()[10]/200,
                            sensorData.getOpponentSensors()[12]/200, sensorData.getOpponentSensors()[14]/200,
                            sensorData.getOpponentSensors()[17]/200, sensorData.getOpponentSensors()[19]/200,
                            sensorData.getOpponentSensors()[22]/200, sensorData.getOpponentSensors()[24]/200,
                            sensorData.getOpponentSensors()[26]/200, sensorData.getOpponentSensors()[27]/200,
                            sensorData.getOpponentSensors()[28]/200, sensorData.getOpponentSensors()[30]/200,
                            sensorData.getOpponentSensors()[32]/200, sensorData.getOpponentSensors()[35]/200};
        double[] outputs = {0.0, 0.0, 0.0};

        int currentNeuron = 0;
        int currentOutput = 0;

        //System.out.println(inputs.length);

        if(_DEBUG)
            System.out.println("Seting output values for input and bias neurons");

        while(_phenotypeNeurons.get(currentNeuron).getType() == 0) {
            //System.out.println("Type = " + _phenotypeNeurons.get(currentNeuron).getType());
            //System.out.println(currentNeuron);
            _phenotypeNeurons.get(currentNeuron).setOutput(inputs[currentNeuron++]);
            //++currentNeuron;
        }

        _phenotypeNeurons.get(currentNeuron).setOutput(1);
        ++currentNeuron;

        while(currentNeuron < _phenotypeNeurons.size()) {
            double sum = 0;

            if(_DEBUG)
                System.out.println("NEURAL NETWORK: Updating neuron's incoming values");
            for(NeuralNetworkConnection nnc : _phenotypeNeurons.get(currentNeuron).getIncoming()) {
                double weight = nnc.getWeight();
                double output = nnc.getInputNeuron().getOutput();
                sum += weight*output;
            }

            if(_DEBUG)
                System.out.println("NEURAL NETWORK: Applying sigmoid to incoming value");
            _phenotypeNeurons.get(currentNeuron).setOutput(sigmoid(sum, _phenotypeNeurons.get(currentNeuron).getActivationResponse()));


            if(_DEBUG)
                System.out.println("NEURAL NETWORK: Getting outputs");
            if(_phenotypeNeurons.get(currentNeuron).getType() == 1) {
                outputs[currentOutput++] = _phenotypeNeurons.get(currentNeuron).getOutput();
            }

            ++currentNeuron;
        }
        _steering      = outputs[0];
        _acceleration  = Math.abs(outputs[1]);  //Try this without abs, see what happens
        _breaking      = Math.abs(outputs[2]);

        outputs[1]     = Math.abs(outputs[1]);
        outputs[2]     = Math.abs(outputs[2]);
        return outputs;
    }


    private double sigmoid(double netInput, double activationResponse) {
        return 2/(1+Math.exp(-netInput/activationResponse))-1;
    }
}
