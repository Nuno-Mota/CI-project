import java.io.Serializable;
import java.util.Random;

public class ConnectionGene implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private NeuronGene _inputNeuron;
    private NeuronGene _outputNeuron;
    private double     _weight;
    private boolean    _isEnabled;
    private boolean    _isRecurrent;
    private int        _innovationN;
    private int        _numberOfTimesDisabled = 0;



    /****************
     * Constructors *
     ****************/

    public ConnectionGene(NeuronGene inputNeuron, NeuronGene outputNeuron, double weight,
                          boolean isEnabled, boolean isRecurrent, int innovationN,
                          int numberOfTimesDisabled) {
        _inputNeuron           = inputNeuron;
        _outputNeuron          = outputNeuron;
        _weight                = weight;
        _isEnabled             = isEnabled;
        _isRecurrent           = isRecurrent;
        _innovationN           = innovationN;
        _numberOfTimesDisabled = numberOfTimesDisabled;
    }




    //Checked. Seems to be fine.
    public ConnectionGene(ConnectionGene connectionToBeCopied) {
        _inputNeuron           = new NeuronGene(connectionToBeCopied.getInputNeuron());
        _outputNeuron          = new NeuronGene(connectionToBeCopied.getOutputNeuron());
        _weight                = connectionToBeCopied.getWeight();
        _isEnabled             = connectionToBeCopied.getIsEnabled();
        _isRecurrent           = connectionToBeCopied.getIsRecurrent();
        _innovationN           = connectionToBeCopied.getInnovationN();
        _numberOfTimesDisabled = connectionToBeCopied.getNumberOfTimesDisabled();
    }



    /***********************
     * Getters and Setters *
     ***********************/

    public NeuronGene getInputNeuron() {
        return _inputNeuron;
    }
    public void setInputNeuron(NeuronGene inputNeuron) {
        _inputNeuron = inputNeuron;
    }

    public NeuronGene getOutputNeuron() {
        return _outputNeuron;
    }
    public void setOutputNeuron(NeuronGene outputNeuron) {
        _outputNeuron = outputNeuron;
    }

    public double getWeight() {
        return _weight;
    }
    public void setWeight(double weight) {
        _weight = weight;
    }

    public boolean getIsEnabled() {
        return _isEnabled;
    }
    public void setIsEnabled(boolean isEnabled) {
        _isEnabled = isEnabled;
    }

    public boolean getIsRecurrent() {
        return _isRecurrent;
    }
    private void setIsRecurrent(boolean isRecurrent) {
        _isRecurrent = isRecurrent;
    }

    public int getInnovationN() {
        return _innovationN;
    }
    private void setInnovationN(int innovationN) {
        _innovationN = innovationN;
    }

    public int getNumberOfTimesDisabled() {
        return _numberOfTimesDisabled;
    }
    public void setNumberOfTimesDisabled(int numberOfTimesDisabled) {
        _numberOfTimesDisabled = numberOfTimesDisabled;
    }
}