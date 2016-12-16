import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetworkNeuron implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private int                           _neuronID;
    private int                           _type;        // Type:  0 = input, 1 = output, 2 = hidden, 3 = bias, 4 = none
    private boolean                       _isRecurrent;
    private double                        _sumActivation;
    private double                        _activationResponse;
    private double                        _output                  = 0;
    private double                        _positionX, _positionY;
    private List<NeuralNetworkConnection> _incoming                = new ArrayList<>();
    private List<NeuralNetworkConnection> _outgoing                = new ArrayList<>();



    /***************
     * Constructor *
     ***************/

    public NeuralNetworkNeuron(NeuronGene genotype) {
        _neuronID           = genotype.getNeuronID();
        _type               = genotype.getType();
        _isRecurrent        = genotype.getIsRecurrent();
        _activationResponse = genotype.getActivationResponse();
        _positionX          = genotype.getPositionX();
        _positionY          = genotype.getPositionY();
    }



    /***********************
     * Getters and Setters *
     ***********************/

    public int getNeuronID() {
        return _neuronID;
    }
    private void setNeuronID(int neuronID) {
        _neuronID = neuronID;
    }

    public int getType() {
        return _type;
    }
    private void setType(int type) {
        _type = type;
    }

    public boolean getIsRecurrent() {
        return _isRecurrent;
    }
    public void setIsRecurrent(boolean isRecurrent) {
        _isRecurrent = isRecurrent;
    }

    public double getSumActivation() {
        return _sumActivation;
    }
    private void setSumActivation(double sumActivation) {
        _sumActivation = sumActivation;
    }

    public double getActivationResponse() {
        return _activationResponse;
    }
    private void setActivationResponse(double activationResponse) {
        _activationResponse = activationResponse;
    }

    public double getOutput() {
        return _output;
    }
    public void setOutput(double output) {
        _output = output;
    }

    public double getPositionX() {
        return _positionX;
    }
    private void setPositionX(double positionX) {
        _positionX = positionX;
    }

    public double getPositionY() {
        return _positionY;
    }
    private void setPositionY(double positionY) {
        _positionY = positionY;
    }

    public List<NeuralNetworkConnection> getIncoming() {
        return _incoming;
    }
    public void addIncoming(NeuralNetworkConnection newIncoming) { _incoming.add(newIncoming); }
    public void setIncoming(List<NeuralNetworkConnection> incoming) {
        _incoming = incoming;
    }

    public List<NeuralNetworkConnection> getOutgoing() {
        return _outgoing;
    }
    public void addOutgoing(NeuralNetworkConnection newOutgoing) { _outgoing.add(newOutgoing); }
    public void setOutgoing(List<NeuralNetworkConnection> outgoing) {
        _outgoing = outgoing;
    }
}
