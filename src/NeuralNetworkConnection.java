import java.io.Serializable;

public class NeuralNetworkConnection implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private NeuralNetworkNeuron _inputNeuron;
    private NeuralNetworkNeuron _outputNeuron;
    private double              _weight;
    private boolean             _isRecurrent;



    /***************
     * Constructor *
     ***************/

    public NeuralNetworkConnection(NeuralNetworkNeuron inputNeuron, NeuralNetworkNeuron outputNeuron,
                                   double weight, boolean isRecurrent) {
        _inputNeuron  = inputNeuron;
        _outputNeuron = outputNeuron;
        _weight       = weight;
        _isRecurrent  = isRecurrent;
    }



    /***********************
     * Getters and Setters *
     ***********************/

    public NeuralNetworkNeuron getInputNeuron() {
        return _inputNeuron;
    }
    public void setInputNeuron(NeuralNetworkNeuron inputNeuron) {
        _inputNeuron = inputNeuron;
    }

    public NeuralNetworkNeuron getOutputNeuron() {
        return _outputNeuron;
    }
    public void setOutputNeuron(NeuralNetworkNeuron outputNeuron) {
        _outputNeuron = outputNeuron;
    }

    public double getWeight() {
        return _weight;
    }
    private void setWeight(double weight) {
        _weight = weight;
    }

    public boolean getIsRecurrent() {
        return _isRecurrent;
    }
    private void setIsRecurrent(boolean isRecurrent) {
        _isRecurrent = isRecurrent;
    }
}
