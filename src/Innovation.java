import java.io.Serializable;

public class Innovation implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private int _innovationType;            // 0 -> Neuron;     1 -> Connection
    private int _innovationID;
    private int _inputNeuronID;
    private int _outputNeuronID;
    private int _neuronID;
    private int _neuronType;                // 0 = input, 1 = output, 2 = hidden, 3 = bias, 4 = none
    private int _numberOfTimesDisabled;     // Indicates the total number of times the connection,
                                            // that the Neuron was added to, has been disabled



    /****************
     * Constructors *
     ****************/

    public Innovation() {}

    public Innovation(int innovationType, int innovationID, int inputNeuronID,
                      int outputNeuronID, int neuronID, int neuronType,
                      int numberOfTimesDisabled) {
        _innovationType        = innovationType;
        _innovationID          = innovationID;
        _inputNeuronID         = inputNeuronID;
        _outputNeuronID        = outputNeuronID;
        _neuronID              = neuronID;
        _neuronType            = neuronType;
        _numberOfTimesDisabled = numberOfTimesDisabled;
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
    public void setNeuronID(int neuronID) { _neuronID = neuronID; }

    public int getNeuronType() { return _neuronType; }
    private void setNeuronType(int neuronType) { _neuronType = neuronType; }

    public int getNumberOfTimesDisabled() {
        return _numberOfTimesDisabled;
    }
    private void setNumberOfTimesDisabled(int numberOfTimesDisabled) {
        _numberOfTimesDisabled = numberOfTimesDisabled;
    }
}
