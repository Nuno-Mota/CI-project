import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuronGene implements Serializable{

    /**********************
     * Internal Variables *
     **********************/

    private int              _neuronID;
    private int              _type;             // 0 = input, 1 = output, 2 = hidden, 3 = bias, 4 = none
    private boolean          _isRecurrent;
    private double           _activationResponse;
    private double           _positionX, _positionY;
    private List<NeuronGene> _possibleIncoming = new ArrayList<>();
    private List<NeuronGene> _possibleOutgoing = new ArrayList<>();

    private Random           _rand                        = new Random();
    private double           _activationResponseMeanValue = 3;  //TODO: check what is a proper value
    private final double     _activationResponseSTDEV     = 1;  //TODO: check what is a proper value



    /***************
     * Constructor *
     ***************/

    //Checked. Seems to be fine
    public NeuronGene(int neuronID, int type, boolean isRecurrent,
                      double activationResponse, double positionX, double positionY) {
        _neuronID           = neuronID;
        _type               = type;
        _isRecurrent        = isRecurrent;
        _activationResponse = activationResponse;
        _positionX          = positionX;
        _positionY          = positionY;
    }




    //Checked. Seems to be fine
    public NeuronGene(NeuronGene neuronToBeCopied) {
        _neuronID           = neuronToBeCopied.getNeuronID();
        _type               = neuronToBeCopied.getType();
        _isRecurrent        = neuronToBeCopied.getIsRecurrent();
        _activationResponse = neuronToBeCopied.getActivationResponse();
        _positionX          = neuronToBeCopied.getPositionX();
        _positionY          = neuronToBeCopied.getPositionY();
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

    public double getActivationResponse() {
        return _activationResponse;
    }
    private void setActivationResponse(double activationResponse) {
        _activationResponse = activationResponse;
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

    public List<NeuronGene> getPossibleIncoming() {
        return _possibleIncoming;
    }
    public void addPossibleIncoming(NeuronGene newPossibleIncoming) { _possibleIncoming.add(newPossibleIncoming); }
    public void setPossibleIncoming(List<NeuronGene> possibleIncoming) {
        _possibleIncoming = possibleIncoming;
    }

    public List<NeuronGene> getPossibleOutgoing() {
        return _possibleOutgoing;
    }
    public void addPossibleOutgoing(NeuronGene newPossibleOutgoing) { _possibleOutgoing.add(newPossibleOutgoing); }
    public void setPossibleOutgoing(List<NeuronGene> possibleOutgoing) {
        _possibleOutgoing = possibleOutgoing;
    }



    /********************************
     * Activation Response Mutation *
     ********************************/

    //Checked. Seems to be fine
    public void mutateActivationResponse() {
        if(Math.random() <= 0.1)                                //10% chance of getting an entirely new value
            _activationResponse = _rand.nextGaussian()*_activationResponseSTDEV + _activationResponseMeanValue;
        else                                                    //80% chance of adding noise to the current weight value
            _activationResponse += 0.5*_rand.nextGaussian()*_activationResponseSTDEV;
    }
}
