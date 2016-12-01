import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Species {

    /**********************
     * Internal Variables *
     **********************/

    private static final int    _youngSpeciesThreshold = 30;
    private static final int    _oldSpeciesThreshold = 400;
    private static final double _youngFitnessBonus = 1.4;
    private static final double _olderFitnessPenalty = 0.9;    //TODO: variable penalty
    private List<NeatGenome>    _individuals = new ArrayList<NeatGenome>();
    private int                 _speciesID;
    private double              _bestFitness;
    private double              _averageSpeciesFitness;
    private int                 _numberOfGenerationsWithNoImprovement;
    private int                 _ageOfSpecies = 0;
    private double              _spawnsRequired;
    private double              _speciesFitness;




    /***************
     * Constructor *
     ***************/

    public Species(List<NeatGenome> individuals) {
        _individuals = individuals;
    }



    /***********************
     * Getters and Setters *
     ***********************/

    public List<NeatGenome> getIndividuals() {
        return _individuals;
    }
    public NeatGenome getRepresentative() {
        return _individuals.get(0);
    }
    private void setIndividuals(List<NeatGenome> individuals) {
        _individuals = individuals;
    }

    public int getSpeciesID() {
        return _speciesID;
    }
    private void setSpeciesID(int speciesID) {
        _speciesID = speciesID;
    }

    public double getBestFitness() {
        return _bestFitness;
    }
    private void setBestFitness(double bestFitness) {
        _bestFitness = bestFitness;
    }

    public double getAverageSpeciesFitness() {
        return _averageSpeciesFitness;
    }
    private void setAverageSpeciesFitness(double averageSpeciesFitness) {
        _averageSpeciesFitness = averageSpeciesFitness;
    }

    public int getNumberOfGenerationsWithNoImprovement() {
        return _numberOfGenerationsWithNoImprovement;
    }
    private void setNumberOfGenerationsWithNoImprovement(int numberOfGenerationsWithNoImprovement) {
        _numberOfGenerationsWithNoImprovement = numberOfGenerationsWithNoImprovement;
    }

    public int getAgeOfSpecies() {
        return _ageOfSpecies;
    }
    private void setAgeOfSpecies(int ageOfSpecies) {
        _ageOfSpecies = ageOfSpecies;
    }

    public double getSpawnsRequired() {
        return _spawnsRequired;
    }
    private void setSpawnsRequired(double spawnsRequired) {
        _spawnsRequired = spawnsRequired;
    }

    public double getSpeciesFitness() {
        return _speciesFitness;
    }
    private void setSpeciesFitness(double speciesFitness) {
        _speciesFitness = speciesFitness;
    }



    /********************
     * Mating Functions *
     ********************/


    public NeatGenome crossover (NeatGenome parent1, NeatGenome parent2) {

        int best;

        if (parent1.getFitness() == parent2.getFitness()) {
            if (parent1.getConnections().size() == parent2.getConnections().size())
                best = ThreadLocalRandom.current().nextInt(1, 3);
            else
                best = (parent1.getConnections().size() > parent2.getConnections().size()) ? 1 : 2;
        }
        else
            best = (parent1.getFitness() > parent2.getFitness()) ? 1 : 2;


        List<NeuronGene>     babyNeurons     = new ArrayList<>();
        List<ConnectionGene> babyConnections = new ArrayList<>();


        int iterator1 = 0;
        int iterator2 = 0;


        ConnectionGene selectedGene = null;

        while(!(iterator1 == parent1.getConnections().size() &&
                iterator2 == parent2.getConnections().size())) {

            if(iterator1 >= parent1.getConnections().size()) {     //TODO: check if problems arise. Check book
                if(best == 2)
                    selectedGene = new ConnectionGene(parent2.getConnections().get(iterator2));
                ++iterator2;
            }
            else if(iterator2 >= parent2.getConnections().size()) {     //TODO: check if problems arise. Check book
                if(best == 1)
                    selectedGene = new ConnectionGene(parent1.getConnections().get(iterator1));
                iterator1++;
            }
            else if(parent1.getConnections().get(iterator1).getInnovationN() <
                    parent2.getConnections().get(iterator2).getInnovationN()) {
                if(best == 1)
                    selectedGene = new ConnectionGene(parent1.getConnections().get(iterator1));
                ++iterator1;
            }
            else if(parent1.getConnections().get(iterator1).getInnovationN() >
                    parent2.getConnections().get(iterator2).getInnovationN()) {
                if(best == 2)
                    selectedGene = new ConnectionGene(parent1.getConnections().get(iterator1));
                iterator1++;
            }
            else if(iterator1 == iterator2) {
                int choice = ThreadLocalRandom.current().nextInt(1, 3);

                if(choice == 1)
                    selectedGene = new ConnectionGene(parent1.getConnections().get(iterator1));
                else
                    selectedGene = new ConnectionGene(parent2.getConnections().get(iterator2));

                ++iterator1;
                ++iterator2;
            }

            if (babyConnections.size() == 0)
                babyConnections.add(selectedGene);
            else
            if (babyConnections.get(babyConnections.size() - 1).getInnovationN() != selectedGene.getInnovationN())
                babyConnections.add(selectedGene);


            addBabyNeuron(selectedGene.getInputNeuron(), babyNeurons);
            addBabyNeuron(selectedGene.getOutputNeuron(), babyNeurons);
        }

        createPossibleListsForEachNeuron(babyNeurons, babyConnections);
        return new NeatGenome(babyNeurons, babyConnections, parent1.getNumberOfInputs(), parent1.getNumberOfOutputs());
    }


    private void addBabyNeuron(NeuronGene babyNeuronGene, List<NeuronGene> babyNeurons) {
        for(NeuronGene ng : babyNeurons)
            if(ng.getNeuronID() == babyNeuronGene.getNeuronID())
                return;

        int i = 0;
        for(NeuronGene ng : babyNeurons) {
            if(ng.getNeuronID() > babyNeuronGene.getNeuronID()) {
                babyNeurons.add(i, babyNeuronGene);
                return;
            }
            ++i;
        }
        babyNeurons.add(babyNeuronGene);
    }


    private void createPossibleListsForEachNeuron(List<NeuronGene> neurons, List<ConnectionGene> connections) {
        for(NeuronGene ng : neurons) {
            if(ng.getType() == 3) {
                for(NeuronGene ngTest : neurons) {
                    if(ngTest.getType() != 3) {
                        boolean add = true;
                        for(ConnectionGene cg : connections)
                            if(cg.getInputNeuron().getNeuronID()  == ng.getNeuronID() &&
                                    cg.getOutputNeuron().getNeuronID() == ngTest.getNeuronID())
                                add = false;
                        if(add)
                            ng.getPossibleOutgoing().add(ngTest);
                    }
                }
            }
            else {
                for(NeuronGene ngTest : neurons) {
                    boolean add = true;
                    if (ngTest.getType() != 3) {
                        for (ConnectionGene cg : connections)
                            if (cg.getInputNeuron().getNeuronID() == ng.getNeuronID() &&
                                    cg.getOutputNeuron().getNeuronID() == ngTest.getNeuronID())
                                add = false;
                        if (add)
                            ng.getPossibleOutgoing().add(ngTest);
                    }

                    add = true;
                    for (ConnectionGene cg : connections)
                        if (cg.getInputNeuron().getNeuronID() == ngTest.getNeuronID() &&
                                cg.getOutputNeuron().getNeuronID() == ng.getNeuronID())
                            add = false;
                    if (add)
                        ng.getPossibleIncoming().add(ngTest);
                }
            }
        }
    }



    /*********************
     * Fitness Functions *
     *********************/


    public void adjustFitness() {
        _speciesFitness = 0;

        for(NeatGenome ng : _individuals) {
            double fitness = ng.getFitness();

            if(_ageOfSpecies < _youngSpeciesThreshold)
                fitness *= _youngFitnessBonus;

            if(_ageOfSpecies > _oldSpeciesThreshold)
                fitness *= _olderFitnessPenalty;


            ng.setAdjustedFitness(fitness/_individuals.size());
            _speciesFitness += ng.getAdjustedFitness();
        }
    }
}
