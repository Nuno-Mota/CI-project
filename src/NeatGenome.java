import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NeatGenome implements Serializable{

    /**********************
     * Internal Variables *
     **********************/

    private int                  _genomeID;
    private List<NeuronGene>     _neurons           = new ArrayList<NeuronGene>();
    private List<ConnectionGene> _connections       = new ArrayList<ConnectionGene>();
    private NeuralNetwork        _neuralNetwork;
    private double               _fitness;
    private double               _adjustedFitness;
    private double               _amountToSpawn;
    private int                  _numberOfInputs;       //Check how useful these are
    private int                  _numberOfOutputs;      //Check how useful these are
    private int                  _speciesID;            //For display's purpose. Check how useful these are

//    private List<ConnectionGene> _possibleConnections          = new ArrayList<ConnectionGene>();
//    private List<ConnectionGene> _possibleRecurrentConnections = new ArrayList<ConnectionGene>();
//    private Random               _rand                         = new Random();
//    private final int            _biasSTDEV                    = 4;                    //Check for good value?
//    private final int            _weightSTDEV                  = 2;                    //Check for good value?


    /****************
     * Constructors *
     ****************/

    public NeatGenome() {}

    public NeatGenome(int genomeID, int numberOfInputs, int numberOfOutputs) {
        _genomeID        = genomeID;
        _numberOfInputs  = numberOfInputs;
        _numberOfOutputs = numberOfOutputs;


        //TODO
    }
//    public NeatGenome(int inputNodesNumber, int outputNodesNumber) { //For initialization
//        for (int i = 0; i < inputNodesNumber; ++i) {
//            _neurons.add(new NeuronGene(0, 0, _rand.nextGaussian()*_biasSTDEV, null, null));
//        }
//        for (int i = 0; i < outputNodesNumber; ++i) {
//            _neurons.add(new NeuronGene(2, 0, _rand.nextGaussian()*_biasSTDEV, null, null));
//        }
//
//        for (NeuronGene ng: _neurons) {
//            if (ng.getType() == 0) {
//                for (NeuronGene ngCheck: _neurons) {
//                    if (ngCheck.getType() == 2) {
//                        ng.addPossibleOutgoing(ngCheck);
//                        _possibleConnections.add(new ConnectionGene(ng, ngCheck, _rand.nextGaussian()*_weightSTDEV, true, 0));
//                    }
//                }
//            }
//            else {
//                for (NeuronGene ngCheck: _neurons) {
//                    if (ngCheck.getType() == 0)
//                        ng.addPossibleIncoming(ngCheck);
//                }
//            }
//        }
//
//        //Enables any kind of recurrent connection.
//        for (NeuronGene ng: _neurons)
//            for (NeuronGene ngCheck: _neurons)
//                _possibleRecurrentConnections.add(new ConnectionGene(ng, ngCheck, _rand.nextGaussian()*_weightSTDEV, true, 0));
//
//
//
//        int maxPossibleNumberOfConnections = _possibleConnections.size();
//        int initialNumberOfConnections = ThreadLocalRandom.current().nextInt(1, maxPossibleNumberOfConnections + 1);
//
//        for (int i = 0; i < initialNumberOfConnections; ++i) {
//            addConnection();
//        }
//
//
//        int maxPossibleNumberOfRecurrentConnections = _possibleRecurrentConnections.size();
//        int initialNumberOfRecurrentConnections = ThreadLocalRandom.current().nextInt(0, maxPossibleNumberOfRecurrentConnections + 1);
//
//        for (int i = 0; i < initialNumberOfRecurrentConnections; ++i) {
//            addConnection();
//        }
//    }
//
//    public NeatGenome(List<NeuronGene> nodes, List<ConnectionGene> connections) { //To load from memory, or after mating?
//        _neurons = nodes;
//        _connections = connections;
//    }



    /***********************
     * Getters and Setters *
     ***********************/

    private int getGenomeID() { return _genomeID; }
    private void setGenomeID(int genomeID) { _genomeID = genomeID; }

    public List<NeuronGene> getNeurons() { return _neurons; }
    private void setNeurons(List<NeuronGene> neurons) { _neurons = neurons; }

    public List<ConnectionGene> getConnections() { return _connections; }
    private void setConnections(List<ConnectionGene> connections) { _connections = connections; }

    private NeuralNetwork getNeuralNetwork() { return _neuralNetwork; }
    private void setNeuralNetwork(NeuralNetwork neuralNetwork) { _neuralNetwork = neuralNetwork; }

    private double getFitness() { return _fitness; }
    private void setFitness(double fitness) { _fitness = fitness; }

    private double getAdjustedFitness() { return _adjustedFitness; }
    private void setAdjustedFitness(double adjustedFitness) { _adjustedFitness = adjustedFitness; }

    private double getAmountToSpawn() { return _amountToSpawn; }
    private void setAmountToSpawn(double amountToSpawn) { _amountToSpawn = amountToSpawn; }

    private int getNumberOfInputs() { return _numberOfInputs; }
    private void setNumberOfInputs(int numberOfInputs) { _numberOfInputs = numberOfInputs; }

    private int getNumberOfOutputs() { return _numberOfOutputs; }
    private void setNumberOfOutputs(int numberOfOutputs) { _numberOfOutputs = numberOfOutputs; }

    private int getSpeciesID() { return _speciesID; }
    private void setSpeciesID(int speciesID) { _speciesID = speciesID; }



    /********************
     * Mutation methods *
     ********************/

    public void addConnection(double mutationRate, double chanceOfLooped,
                              int numberOfTriesToFindLoop, int numberOfTriesToAddConnection) {

        if (Math.random() > mutationRate)
            return;


        int inputNeuronID = -1;
        int outputNeuronID = -1;
        boolean isRecurrent = false;



        if (Math.random() > chanceOfLooped) {
            while (numberOfTriesToFindLoop-- != 0){
                int neuronNumber = ThreadLocalRandom.current().nextInt(_numberOfInputs + 1, _neurons.size() - 1);

                if (!_neurons.get(neuronNumber).getIsRecurrent() &&
                     _neurons.get(neuronNumber).getType() != 0 &&
                     _neurons.get(neuronNumber).getType() != 3) {

                    inputNeuronID = outputNeuronID = _neurons.get(neuronNumber).getNeuronID();
                    _neurons.get(neuronNumber).setIsRecurrent(true);
                    isRecurrent = true;
                    numberOfTriesToFindLoop = 0;
                }
            }
        }
        else {
            while (numberOfTriesToAddConnection-- != 0) {
                int inputNeuronNumber = ThreadLocalRandom.current().nextInt(0, _neurons.size() - 1);
                int outputNeuronNumber = ThreadLocalRandom.current().nextInt(_numberOfInputs + 1, _neurons.size() - 1);

                if (_neurons.get(outputNeuronNumber).getType() == 3)
                    continue;

                if (!(duplicateConnection(_neurons.get(inputNeuronNumber).getNeuronID(), _neurons.get(outputNeuronNumber).getNeuronID()) &&
                    _neurons.get(inputNeuronNumber).getNeuronID() == _neurons.get(outputNeuronNumber).getNeuronID()))
                    numberOfTriesToAddConnection = 0;

                else
                    inputNeuronID = outputNeuronID = -1;
            }
        }



        if (inputNeuronID == -1 || outputNeuronID == -1)
            return;
    }

//    private void addConnection() {   //THIS LOOKS VERY INEFFICIENT!!!! Maybe create at start a list of possible connections
//        int breakCycle = 0;
//        List<Integer> testedIndexes = new ArrayList<Integer>();
//        NeuronGene ngToConnect;
//
//
//        while (testedIndexes.size() != _neurons.size()) {
//            Integer index = _rand.nextInt(_neurons.size());
//
//            if (!testedIndexes.contains(index)) {
//                NeuronGene ng = _neurons.get(index);
//
//                if (ng.getPossibleIncoming() != null && ng.getPossibleOutgoing() != null) {
//                    if (ThreadLocalRandom.current().nextInt(1, 2 + 1) == 1)
//                        ngToConnect = ng.getPossibleIncoming().get(_rand.nextInt(ng.getPossibleIncoming().size()));
//                    else
//                        ngToConnect = ng.getPossibleOutgoing().get(_rand.nextInt(ng.getPossibleOutgoing().size()));
//                }
//                else if (ng.getPossibleIncoming() == null && ng.getPossibleOutgoing() != null)
//                    ngToConnect = ng.getPossibleOutgoing().get(_rand.nextInt(ng.getPossibleOutgoing().size()));
//                else if (ng.getPossibleIncoming() != null && ng.getPossibleOutgoing() == null)
//                    ngToConnect = ng.getPossibleIncoming().get(_rand.nextInt(ng.getPossibleIncoming().size()));
//                else {
//                    testedIndexes.add(index);
//                    breakCycle = 1;
//                }
//
//
//                if (breakCycle == 0) {
//                    _connections.add(new ConnectionGene());
//                }
//
//
//            }
//        }
//    }
}


