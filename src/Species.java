import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Species {

    /**********************
     * Internal Variables *
     **********************/

    private List<NeatGenome> _individuals = new ArrayList<NeatGenome>();



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
    private void setIndividuals(List<NeatGenome> individuals) {
        _individuals = individuals;
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
}
