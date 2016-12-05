import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NeatGenome implements Serializable{

    /**********************
     * Internal Variables *
     **********************/

    private InnovationsTable     _innovationsTable = InnovationsTable.getInstance();
    private static int           _globalGenomeID   = 0;

    private int                  _genomeID;
    private List<NeuronGene>     _neurons          = new ArrayList<>();
    private List<ConnectionGene> _connections      = new ArrayList<>();
    private NeuralNetwork        _neuralNetwork;
    private double               _fitness;
    private double               _adjustedFitness;
    private double               _amountToSpawn;
    private int                  _numberOfInputs;
    private int                  _numberOfOutputs;
    private int                  _speciesID;                          //For display's purpose. Check how useful these are

    private Random               _rand             = new Random();
    private final int            _weightSTDEV      = 2;               //Check for good value?

    private double           _activationResponseMeanValue = 200;  //TODO: check what is a proper value
    private final double     _activationResponseSTDEV     = 10;  //TODO: check what is a proper value



    /****************
     * Constructors *
     ****************/

    //Checked. Seems to be fine     //TODO:Missing adding connections and neurons to innovation's table
    public NeatGenome(int numberOfInputs, int numberOfOutputs) {
        _genomeID        = _globalGenomeID++;
        _numberOfInputs  = numberOfInputs;
        _numberOfOutputs = numberOfOutputs;


        //Create Input neurons
        for(int i = 0; i < _numberOfInputs; ++i)
            _neurons.add(new NeuronGene(i, 0, false, _rand.nextGaussian()*_activationResponseSTDEV + _activationResponseMeanValue, i/1.0, 0));

        //Create Bias Neuron (one neuron is enough for all. Just add connections as needed)
        _neurons.add(new NeuronGene(_numberOfInputs, 3, false, 4.9, 1.3, 0));

        //Create Output neurons
        for(int i = 0; i < _numberOfOutputs; ++i)
            _neurons.add(new NeuronGene(i + _numberOfInputs + 1, 1, false, _rand.nextGaussian()*_activationResponseSTDEV + _activationResponseMeanValue, i/1.0, 1));


        //Create possible Incoming and Outgoing connections for all Neurons
        createPossibleListsForEachNeuron(_neurons, _connections);


        //For each input neuron create connections with every output neuron
        for(int i = 0; i < _numberOfInputs; ++i)
            for(int j = _numberOfInputs; j < _numberOfInputs + _numberOfOutputs; ++j)
                addNewConnection(_neurons.get(i).getNeuronID(), _neurons.get(j).getNeuronID(), false);
    }




    //TODO: Add constructor that randomizes initial connections




    //Checked by crossover. Seems to be fine
    public NeatGenome(List<NeuronGene> neurons, List<ConnectionGene> connections,
                      int numberOfInputs, int numberOfOutputs) {
        _genomeID        = _globalGenomeID++;
        _numberOfInputs  = numberOfInputs;
        _numberOfOutputs = numberOfOutputs;

        //Neurons are already unique when generated in crossover
        _neurons = neurons;


        //Make sure the connections point to the correct neurons in _neurons
        for(ConnectionGene cg : connections) {
            for(NeuronGene ng : _neurons) {
                if (cg.getInputNeuron().getNeuronID() == ng.getNeuronID())
                    cg.setInputNeuron(ng);
                if (cg.getOutputNeuron().getNeuronID() == ng.getNeuronID())
                    cg.setOutputNeuron(ng);
            }
        }

        createPossibleListsForEachNeuron(_neurons, _connections);
    }




    //Checked. Seems to be fine
    public NeatGenome(NeatGenome genomeToBeCopied) {
        _genomeID        = _globalGenomeID++;
        _numberOfInputs  = genomeToBeCopied.getNumberOfInputs();
        _numberOfOutputs = genomeToBeCopied.getNumberOfOutputs();

        //Create new instances of the neurons of the genomeToBeCopied
        for(NeuronGene ng : genomeToBeCopied.getNeurons())
            _neurons.add(new NeuronGene(ng));


        //Create new instances of the connections of the genomeToBeCopied and make sure they point to the
        //correct neurons in _neurons
        for(ConnectionGene cg : genomeToBeCopied.getConnections()) {
            ConnectionGene copiedConnection = new ConnectionGene(cg);
            for(NeuronGene ng : _neurons) {
                if (copiedConnection.getInputNeuron().getNeuronID() == ng.getNeuronID())
                    copiedConnection.setInputNeuron(ng);
                if (copiedConnection.getOutputNeuron().getNeuronID() == ng.getNeuronID())
                    copiedConnection.setOutputNeuron(ng);
            }
            _connections.add(copiedConnection);
        }

        createPossibleListsForEachNeuron(_neurons, _connections);
    }



    /***********************
     * Getters and Setters *
     ***********************/

    private int getGenomeID() { return _genomeID; }
    private void setGenomeID(int genomeID) { _genomeID = genomeID; }

    public List<NeuronGene> getNeurons() { return _neurons; }
    private void setNeurons(List<NeuronGene> neurons) { _neurons = neurons; }

    public List<ConnectionGene> getConnections() { return _connections; }
    private void setConnections(List<ConnectionGene> connections) { _connections = connections; }

    public NeuralNetwork getNeuralNetwork() { return _neuralNetwork; }
    private void setNeuralNetwork(NeuralNetwork neuralNetwork) { _neuralNetwork = neuralNetwork; }

    public double getFitness() { return _fitness; }
    private void setFitness(double fitness) { _fitness = fitness; }

    public double getAdjustedFitness() { return _adjustedFitness; }
    public void setAdjustedFitness(double adjustedFitness) { _adjustedFitness = adjustedFitness; }

    public double getAmountToSpawn() { return _amountToSpawn; }
    private void setAmountToSpawn(double amountToSpawn) { _amountToSpawn = amountToSpawn; }

    public int getNumberOfInputs() { return _numberOfInputs; }
    private void setNumberOfInputs(int numberOfInputs) { _numberOfInputs = numberOfInputs; }

    public int getNumberOfOutputs() { return _numberOfOutputs; }
    private void setNumberOfOutputs(int numberOfOutputs) { _numberOfOutputs = numberOfOutputs; }

    public int getSpeciesID() { return _speciesID; }
    private void setSpeciesID(int speciesID) { _speciesID = speciesID; }



    /********************
     * Mutation methods *
     ********************/

    //Checked. Seems to be fine
    public void mutateWeights() {

        double lower = 0.1;     //Lowest value of chance of mutation occurring
        double upper = 0.75;    //Highest value of chance of mutation occurring

        for(ConnectionGene cg : _connections)
            if(Math.random() <= Math.random() * (upper - lower) + lower)
                cg.mutateWeight();
    }




    //Checked. Seems to be fine
    public void mutateActivationResponses() {

        double lower = 0.1;     //Lowest value of chance of mutation occurring
        double upper = 0.3;     //Highest value of chance of mutation occurring

        for(NeuronGene ng : _neurons)
            if(Math.random() <= Math.random() * (upper - lower) + lower)
                ng.mutateActivationResponse();
    }




    //Checked. Seems to be fine
    public void disableConnection(int numberOfTriesToDisableConnection) {

        double lower = 0.05;    //Lowest value of chance of a random connection being disabled
        double upper = 0.10;    //Highest value of chance of a random connection being disabled
        int connectionIndex;

        if(Math.random() <= Math.random() * (upper - lower) + lower) {
            while(numberOfTriesToDisableConnection-- != 0) {
                connectionIndex = ThreadLocalRandom.current().nextInt(0, _connections.size());
                if(_connections.get(connectionIndex).getIsEnabled()) {
                    _connections.get(connectionIndex).setIsEnabled(false);
                    numberOfTriesToDisableConnection = 0;
                }
            }
        }
    }




    //Checked. Seems to be fine
    public void enableConnection(int numberOfTriesToEnableConnection) {

        double lower = 0.3;    //Lowest value of chance of a random connection being disabled
        double upper = 0.4;    //Highest value of chance of a random connection being disabled
        int connectionIndex;

        if(Math.random() <= Math.random() * (upper - lower) + lower) {
            while(numberOfTriesToEnableConnection-- != 0) {
                connectionIndex = ThreadLocalRandom.current().nextInt(0, _connections.size());
                if(!_connections.get(connectionIndex).getIsEnabled()) {
                    _connections.get(connectionIndex).setIsEnabled(true);
                    numberOfTriesToEnableConnection = 0;
                }
            }
        }
    }




    //Checked by addConnection from adding new connection in the mutation process. Seems to be fine
    public void addConnection(double mutationRate, double chanceOfLooped,
                              int numberOfTriesToFindLoop, int numberOfTriesToAddConnection) {

        if (Math.random() > mutationRate)
            return;


        int inputNeuronID   = -1;
        int outputNeuronID  = -1;
        boolean isRecurrent = false;



        if (Math.random() <= chanceOfLooped) {
            while (numberOfTriesToFindLoop-- != 0){


                //ignores input and bias neurons, which shouldn't be recurrently looped
                int neuronNumber = ThreadLocalRandom.current().nextInt(_numberOfInputs + 1, _neurons.size() - 1);

                if (!_neurons.get(neuronNumber).getIsRecurrent()/* &&
                     _neurons.get(neuronNumber).getType() != 0   &&
                     _neurons.get(neuronNumber).getType() != 3*/) {

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
                //ignores input and bias neurons, which shouldn't have incoming connections
                int outputNeuronNumber = ThreadLocalRandom.current().nextInt(_numberOfInputs + 1, _neurons.size() - 1);
                inputNeuronID = _neurons.get(inputNeuronNumber).getNeuronID();
                outputNeuronID = _neurons.get(outputNeuronNumber).getNeuronID();

                /*if (_neurons.get(outputNeuronNumber).getType() == 3)
                    continue;*/

                //Makes sure the neurons selected don't have already a connection between them
                //Also makes sure that they aren't the same neuron, in order to avoid a looped connection
                if (!(isDuplicateConnection(inputNeuronID, outputNeuronID) || inputNeuronID == outputNeuronID))
                    numberOfTriesToAddConnection = 0;

                else
                    inputNeuronID = outputNeuronID = -1;
            }
        }

        if (inputNeuronID == -1 || outputNeuronID == -1)
            return;

        addNewConnection(inputNeuronID, outputNeuronID, isRecurrent);
    }




    //Checked. seems to be fine
    public void addNeuron(double mutationRate, int numberOfTriesToFindOldLink) {

        if (Math.random() > mutationRate)
            return;


        int chosenConnectionNumber      = 0;
        boolean connectionFound         = false;
        final int hiddenNeuronThreshold = _numberOfInputs + 1 + _numberOfOutputs + 5;   //should change with network size?



        if(_connections.size() <= hiddenNeuronThreshold) {      //Bias towards older connections for small networks
            while(numberOfTriesToFindOldLink-- != 0) {
                if(_connections.size() > 2) {
                    int maxConnectionIndexNumber = _connections.size() - 1 - (int)Math.sqrt(_connections.size());
                    chosenConnectionNumber  = ThreadLocalRandom.current().nextInt(0, maxConnectionIndexNumber);
                }
                else
                    chosenConnectionNumber  = ThreadLocalRandom.current().nextInt(0, _connections.size());

                int inputNeuronType     = _connections.get(chosenConnectionNumber).getInputNeuron().getType();

                if(_connections.get(chosenConnectionNumber).getIsEnabled() &&
                        _connections.get(chosenConnectionNumber).getIsRecurrent() &&   //Why not add neurons to recurrent connections?
                        inputNeuronType != 3) {

                    connectionFound = true;
                    numberOfTriesToFindOldLink = 0;
                }
            }

            if(!connectionFound)
                return;
        }
        else {
            while(!connectionFound) {
                int maxConnectionNumber = _connections.size() - 1;
                chosenConnectionNumber = ThreadLocalRandom.current().nextInt(0, maxConnectionNumber);
                int inputNeuronType = _connections.get(chosenConnectionNumber).getInputNeuron().getType();

                if(_connections.get(chosenConnectionNumber).getIsEnabled() &&
                        _connections.get(chosenConnectionNumber).getIsRecurrent() &&
                        inputNeuronType != 3)
                    connectionFound = true;
            }
        }

        int numberOfTimesDisabled = _connections.get(chosenConnectionNumber).getNumberOfTimesDisabled();
        _connections.get(chosenConnectionNumber).setIsEnabled(false);
        _connections.get(chosenConnectionNumber).setNumberOfTimesDisabled(numberOfTimesDisabled + 1);
        double originalWeight     = _connections.get(chosenConnectionNumber).getWeight();
        int inputNeuronID         = _connections.get(chosenConnectionNumber).getInputNeuron().getNeuronID();
        int outputNeuronID        = _connections.get(chosenConnectionNumber).getOutputNeuron().getNeuronID();

        double newDepth = (getNeuron(inputNeuronID).getPositionY() + getNeuron(outputNeuronID).getPositionY())/2;
        double newWidth = (getNeuron(inputNeuronID).getPositionX() + getNeuron(outputNeuronID).getPositionX())/2;

        Innovation newInnovation = new Innovation(0, -1, inputNeuronID,
                outputNeuronID, -1, 2, numberOfTimesDisabled);
        int innovationID = _innovationsTable.getInnovationID(newInnovation);


        //TODO: make sure that this part works properly! Not according to the book!!!!
        double activationResponse = 3;  //TODO: check what a proper value is


        int neuronID;

        if(innovationID < 0) {
            _innovationsTable.addInnovation(newInnovation);
            neuronID = _innovationsTable.getGlobalNeuronNumber();
        }
        else
            neuronID = _innovationsTable.getNeuronID(innovationID);


        NeuronGene newNeuron = new NeuronGene(neuronID, 2, false, activationResponse, newWidth,
                newDepth);
        _neurons.add(newNeuron);
        createPossibleLists(newNeuron, _neurons, _connections);


        pushToPossibleLists(inputNeuronID, newNeuron, outputNeuronID);
        NeuronGene inputNeuron   = getNeuron(inputNeuronID);
        NeuronGene outputNeuron  = getNeuron(outputNeuronID);



        double weight;
        //ADD INCOMING CONNECTION
        newInnovation = new Innovation(1, -1, inputNeuronID, neuronID, -1, 4, 0);
        innovationID = _innovationsTable.getInnovationID(newInnovation);

        weight = 1;

        if (innovationID < 0) {
            _innovationsTable.addInnovation(newInnovation); //addInnovation corrects innovation number automatically
            int innovationNumber = _innovationsTable.getGlobalInnovationNumber();
            _connections.add(new ConnectionGene(inputNeuron, newNeuron, weight, true, false, innovationNumber, 0));
        }
        else
            _connections.add(new ConnectionGene(inputNeuron, newNeuron, weight, true, false, innovationID, 0));

        //ADD OUTGOING CONNECTION
        newInnovation = new Innovation(1, -1, neuronID, outputNeuronID, -1, 4, 0);
        innovationID = _innovationsTable.getInnovationID(newInnovation);

        weight = originalWeight;

        if (innovationID < 0) {
            _innovationsTable.addInnovation(newInnovation); //addInnovation corrects innovation number automatically
            int innovationNumber = _innovationsTable.getGlobalInnovationNumber();
            _connections.add(new ConnectionGene(newNeuron, outputNeuron, weight, true, false, innovationNumber, 0));
        }
        else
            _connections.add(new ConnectionGene(newNeuron, outputNeuron, weight, true, false, innovationID, 0));

    }



    /******************************
     * Auxiliary Mutation methods *
     ******************************/

    //Checked. Seems to be fine
    private boolean isDuplicateConnection(int inputNeuronID, int outputNeuronID) {
        for (ConnectionGene cg: _connections) {
            if (cg.getInputNeuron().getNeuronID() == inputNeuronID &&
                cg.getOutputNeuron().getNeuronID() == outputNeuronID)
                return true;
        }
        return false;
    }




    //Checked by first neatGenome constructor and addConnection(from the mutation process of adding new connections). Seems to be fine
    public void addNewConnection(int inputNeuronID, int outputNeuronID, boolean isRecurrent) {

        //Create a possibly new innovation. Useful for determining previous existence
        Innovation newInnovation = new Innovation(1, -1, inputNeuronID, outputNeuronID, -1, 4, 0);
        int innovationID = _innovationsTable.getInnovationID(newInnovation);

        //Corrects possible incoming and outgoing lists for each referenced neuron
        NeuronGene inputNeuron  = getNeuron(inputNeuronID);
        NeuronGene outputNeuron = getNeuron(outputNeuronID);
        inputNeuron.getPossibleOutgoing().remove(outputNeuron);//TODO: Check null pointer exception validity
        outputNeuron.getPossibleIncoming().remove(inputNeuron);

        //Assigns weight of the connection
        double weight = _rand.nextGaussian()*_weightSTDEV;

        //Determines if the connection is recurrent by determining if the connection is not "pointing" in the output's direction
        if (inputNeuron.getPositionY() > outputNeuron.getPositionY())
            isRecurrent = true;

        //If it is indeed a new innovation (which it is if "-1" is returned by "_innovationsTable.getInnovationID(newInnovation);")
        //it must be registered in the innovations table, and the new connectionGene added to the current connections of this genome.
        //Else just add the new connection to the current connections of this genome
        if (innovationID < 0) {
            _innovationsTable.addInnovation(newInnovation); //addInnovation corrects innovation number automatically
            int innovationNumber = _innovationsTable.getGlobalInnovationNumber();
            _connections.add(new ConnectionGene(inputNeuron, outputNeuron, weight, true, isRecurrent, innovationNumber, 0));
        }
        else
            _connections.add(new ConnectionGene(inputNeuron, outputNeuron, weight, true, isRecurrent, innovationID, 0));
    }




    //Checked. Seems to be fine
    private NeuronGene getNeuron(int neuronID) {
        for (NeuronGene ng: _neurons)
            if (ng.getNeuronID() == neuronID)
                return ng;

        return null;
    }




    //Checked. Seems to be fine
    public void createPossibleListsForEachNeuron(List<NeuronGene> neurons, List<ConnectionGene> connections) {
        for(NeuronGene ng : neurons)
            createPossibleLists(ng, neurons, connections);
//        for(NeuronGene ng : neurons) {
//            if(ng.getType() == 3) {
//                for(NeuronGene ngTest : neurons) {
//                    if(ngTest.getType() != 3) {
//                        boolean add = true;
//                        for(ConnectionGene cg : connections)
//                            if(cg.getInputNeuron().getNeuronID()  == ng.getNeuronID() &&
//                                    cg.getOutputNeuron().getNeuronID() == ngTest.getNeuronID())
//                                add = false;
//                        if(add)
//                            ng.getPossibleOutgoing().add(ngTest);
//                    }
//                }
//            }
//            else {
//                for(NeuronGene ngTest : neurons) {
//                    boolean add = true;
//                    if (ngTest.getType() != 3) {
//                        for (ConnectionGene cg : connections)
//                            if (cg.getInputNeuron().getNeuronID() == ng.getNeuronID() &&
//                                    cg.getOutputNeuron().getNeuronID() == ngTest.getNeuronID())
//                                add = false;
//                        if (add)
//                            ng.getPossibleOutgoing().add(ngTest);
//                    }
//
//                    add = true;
//                    for (ConnectionGene cg : connections)
//                        if (cg.getInputNeuron().getNeuronID() == ngTest.getNeuronID() &&
//                                cg.getOutputNeuron().getNeuronID() == ng.getNeuronID())
//                            add = false;
//                    if (add)
//                        ng.getPossibleIncoming().add(ngTest);
//                }
//            }
//        }
    }




    //Checked. Seems to be fine
    private void createPossibleLists(NeuronGene ng, List<NeuronGene> neurons, List<ConnectionGene> connections) {
        for(NeuronGene ngCheck : neurons) {
            if(!(ng.getType() == 0 || ng.getType() ==3)) {             //Input and Bias neurons can't have incoming connections
                boolean add = true;
                for(ConnectionGene cg : connections)
                    if(cg.getInputNeuron().getNeuronID()  == ngCheck.getNeuronID() &&   //If connection exists, don't add
                            cg.getOutputNeuron().getNeuronID() == ng.getNeuronID())
                        add = false;
                if(add)
                    ng.getPossibleIncoming().add(ngCheck);
            }
            if(!(ngCheck.getType() == 0 || ngCheck.getType() ==3)) {   //Neurons can't have outgoing connections toInput or Bias neurons
                boolean add = true;
                for(ConnectionGene cg : connections)
                    if(cg.getInputNeuron().getNeuronID() == ng.getNeuronID() &&    //If connection exists, don't add
                            cg.getOutputNeuron().getNeuronID() == ngCheck.getNeuronID())
                        add = false;
                if(add)
                    ng.getPossibleOutgoing().add(ngCheck);
            }
        }
    }




    //Checked. Seems to be fine
    private void pushToPossibleLists(int inputNeuronID, NeuronGene newNeuron, int outputNeuronID) {
        for(NeuronGene ng: _neurons) {
            if(ng.getNeuronID() != inputNeuronID)
                ng.getPossibleOutgoing().add(newNeuron);
            if(ng.getNeuronID() != outputNeuronID && ng.getType() != 0 && ng.getType() != 3)
                ng.getPossibleIncoming().add(newNeuron);
        }
    }



    /****************************
     * Spawn Amount Calculation *
     ****************************/

    //Checked. Seems to be fine
    public void calculateSpawns(double averagePopulationFitness) {
        _amountToSpawn = _adjustedFitness/averagePopulationFitness;
    }



    /******************************
     * Phenotype Creation Methods *
     ******************************/

    //Checked. Seems to be fine
    public void createPhenotype() {
        List<NeuralNetworkNeuron> phenotypeNeurons = new ArrayList<>();

        for(NeuronGene ng : _neurons)
            phenotypeNeurons.add(new NeuralNetworkNeuron(ng));

        for(ConnectionGene cg : _connections){
            if (cg.getIsEnabled()){
                NeuralNetworkNeuron inputNeuron = getNeuralNetworkNeuron(cg.getInputNeuron().getNeuronID(), phenotypeNeurons);
                NeuralNetworkNeuron outputNeuron = getNeuralNetworkNeuron(cg.getOutputNeuron().getNeuronID(), phenotypeNeurons);
                double weight = cg.getWeight();
                boolean isRecurrent = cg.getIsRecurrent();
                NeuralNetworkConnection newConnection = new NeuralNetworkConnection(inputNeuron, outputNeuron, weight, isRecurrent);

                inputNeuron.addOutgoing(newConnection);
                outputNeuron.addIncoming(newConnection);
            }
        }

        _neuralNetwork = new NeuralNetwork(phenotypeNeurons);
    }




    //Checked. Seems to be fine
    private NeuralNetworkNeuron getNeuralNetworkNeuron(int neuronID, List<NeuralNetworkNeuron> phenotypeNeurons) {
        for (NeuralNetworkNeuron nnn: phenotypeNeurons) {
            if (nnn.getNeuronID() == neuronID)
                return nnn;
        }
        return null;
    }
}


