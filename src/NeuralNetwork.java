import scr.SensorModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private static final long serialVersionUID = -88L;
    private List<NeuralNetworkNeuron> _phenotypeNeurons = new ArrayList<>();

    NeuralNetwork(List<NeuralNetworkNeuron> phenotypeNeurons) {
        _phenotypeNeurons = phenotypeNeurons;
    }

    public double getOutput(SensorModel a) {
        return 0.5;
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
    public NeuralNetwork loadGenome() {

        // Read from disk using FileInputStream
        FileInputStream f_in = null;
        try {
            f_in = new FileInputStream("memory/mydriver.mem");
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

        //12 Inputs
        //TODO: add opponents to input, add lateral speed?, add zspeed?, wheelspinvel
        double[] inputs  = {sensorData.getAngleToTrackAxis(), sensorData.getSpeed(),
                            sensorData.getTrackEdgeSensors()[2], sensorData.getTrackEdgeSensors()[4],
                            sensorData.getTrackEdgeSensors()[6], sensorData.getTrackEdgeSensors()[8],
                            sensorData.getTrackEdgeSensors()[9], sensorData.getTrackEdgeSensors()[10],
                            sensorData.getTrackEdgeSensors()[12], sensorData.getTrackEdgeSensors()[14],
                            sensorData.getTrackEdgeSensors()[16], sensorData.getTrackPosition()};
        double[] outputs = {0.0, 0.0, 0.0};

        int currentNeuron = 0;
        int currentOutput = 0;


        while(_phenotypeNeurons.get(currentNeuron).getType() == 0)
            _phenotypeNeurons.get(currentNeuron).setOutput(inputs[currentNeuron++]);

        //TODO: Set the bias nerons output to 1
        //_phenotypeNeurons.get(currentNeuron).setOutput(inputs[currentNeuron++]);

        while(currentNeuron < _phenotypeNeurons.size()) {
            double sum = 0;

            for(NeuralNetworkConnection nnc : _phenotypeNeurons.get(currentNeuron).getIncoming()) {
                double weight = nnc.getWeight();
                double output = nnc.getInputNeuron().getOutput();
                sum += weight*output;
            }

            _phenotypeNeurons.get(currentNeuron).setOutput(sigmoid(sum, _phenotypeNeurons.get(currentNeuron).getActivationResponse()));

            if(_phenotypeNeurons.get(currentNeuron).getType() == 1)
                outputs[currentOutput++] = _phenotypeNeurons.get(currentNeuron).getOutput();

            ++currentNeuron;
        }

        return outputs;
    }


    private double sigmoid(double netInput, double activationResponse) {
        return 1/(1+Math.exp(-netInput/activationResponse));
    }
}
