import java.io.Serializable;

public class ConnectionGene implements Serializable{

    /**********************
     * Internal Variables *
     **********************/

    private NeuronGene _inputNode;
    private NeuronGene _outputNode;
    private double     _weight;
    private boolean    _isEnabled;
    private boolean    _isRecurrent;
    private int        _innovationN;



    /***************
     * Constructor *
     ***************/

    public ConnectionGene(NeuronGene inputNode, NeuronGene outputNode, double weight,
                          boolean isEnabled, boolean isRecurrent, int innovationN) {
        _inputNode   = inputNode;
        _outputNode  = outputNode;
        _weight      = weight;
        _isEnabled   = isEnabled;
        _isRecurrent = isRecurrent;
        _innovationN = innovationN;
    }



    /***********************
     * Getters and Setters *
     ***********************/

    public NeuronGene getInputNode() {
        return _inputNode;
    }
    private void setInputNode(NeuronGene inputNode) {
        _inputNode = inputNode;
    }

    public NeuronGene getOutputNode() {
        return _outputNode;
    }
    private void setOutputNode(NeuronGene outputNode) {
        _outputNode = outputNode;
    }

    public double getWeight() {
        return _weight;
    }
    private void setWeight(double weight) {
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



    /********************************
     * Innovation Number Comparison *
     ********************************/

    public boolean hasSmallerInnovationNumber(ConnectionGene toCompare) {
        return _innovationN < toCompare.getInnovationN();
    }
}