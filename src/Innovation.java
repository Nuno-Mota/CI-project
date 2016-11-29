import java.io.Serializable;

public class Innovation implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private int _innovationType;    // 0 -> Neuron;     1 -> Connection
    private int _innovationID;
    private int _inputNeuronID;
    private int _outputNeuronID;
    private int _neuronID;
    private int _neuronType;        // 0 = input, 1 = output, 2 = hidden, 3 = bias, 4 = none



    /****************
     * Constructors *
     ****************/

    public Innovation() {}

    public Innovation(int innovationType, int innovationID, int inputNeuronID,
                       int outputNeuronID, int neuronID, int neuronType) {
        _innovationType = innovationType;
        _innovationID   = innovationID;
        _inputNeuronID  = inputNeuronID;
        _outputNeuronID = outputNeuronID;
        _neuronID       = neuronID;
        _neuronType     = neuronType;
    }



    /***********************
     * Getters and Setters *
     ***********************/

    public int getInnovationType() { return _innovationType; }
    private void setInnovationType(int innovationType) { _innovationType = innovationType; }

    public int getInnovationID() { return _innovationID; }
    public void setInnovationID(int innovationID) { _innovationID = innovationID; }

    public int getInputNeuronID() { return _inputNeuronID; }
    private void setInputNeuronID(int inputNeuronID) { _inputNeuronID = inputNeuronID; }

    public int getOutputNeuronID() { return _outputNeuronID; }
    private void setOutputNeuronID(int outputNeuronID) { _outputNeuronID = outputNeuronID; }

    public int getNeuronID() { return _neuronID; }
    private void setNeuronID(int neuronID) { _neuronID = neuronID; }

    public int getNeuronType() { return _neuronType; }
    private void setNeuronType(int neuronType) { _neuronType = neuronType; }
}
