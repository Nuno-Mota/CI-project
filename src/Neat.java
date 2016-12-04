import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Neat {

    /**********************
     * Internal Variables *
     **********************/

    private int                 _globalInnovationNumber = 0;
    private int                 _numberOfInputs;
    private int                 _numberOfOutputs;
    private int                 _populationSize;
    private int                 _generationNumber  = 1;
    private List<NeatGenome>    _currentPopulation = new ArrayList<>();
    private List<Species>       _currentSpecies    = new ArrayList<>();
    private double              _compatibilityThreshold = 10;
    private int                 c1 = 1, c2 = 1, c3 = 5;
    private static final double _likelihoodOfBestMating = 0.35;
    private static final double _probabilityOfMating = 0.85;



    /****************
     * Constructors *
     ****************/

    public Neat(int numberOfInputs, int numberOfOutputs, int populationSize) {
        _numberOfInputs    = numberOfInputs;
        _numberOfOutputs   = numberOfOutputs;
        _populationSize    = populationSize;
        createPopulation();
    }

    public Neat (int numberOfInputs, int numberOfOutputs, int populationSize, int generationNumber,
                 List<NeatGenome> currentPopulation) {
        _numberOfInputs    = numberOfInputs;
        _numberOfOutputs   = numberOfOutputs;
        _populationSize    = populationSize;
        _generationNumber  = generationNumber;
        _currentPopulation = currentPopulation;
    }



    /***********************
     * Getters and Setters *
     ***********************/

    public int getGlobalInnovationNumber() {
        return _globalInnovationNumber;
    }
    private void setGlobalInnovationNumber(int globalInnovationNumber) {
        _globalInnovationNumber = globalInnovationNumber;
    }

    public int getNumberOfInputs() {
        return _numberOfInputs;
    }
    private void setNumberOfInputs(int numberOfInputs) {
        _numberOfInputs = numberOfInputs;
    }

    public int getNumberOfOutputs() {
        return _numberOfOutputs;
    }
    private void setNumberOfOutputs(int numberOfOutputs) {
        _numberOfOutputs = numberOfOutputs;
    }

    public int getPopulationSize() {
        return _populationSize;
    }
    private void setPopulationSize(int populationSize) {
        _populationSize = populationSize;
    }

    public int getGenerationNumber() {
        return _generationNumber;
    }
    private void setGenerationNumber(int generationNumber) {
        _generationNumber = generationNumber;
    }

    public List<NeatGenome> getCurrentPopulation() {
        return _currentPopulation;
    }
    private void setCurrentPopulation(List<NeatGenome> currentPopulation) {
        _currentPopulation = currentPopulation;
    }

    public double getLikelihoodOfBestMating() {
        return _likelihoodOfBestMating;
    }

    public double getProbabilityOfMating() {
        return _probabilityOfMating;
    }



    /******************
     * NEAT's methods *
     ******************/

    //Checking
    public void createNewGeneration() {
        ++_generationNumber;
        System.out.println("Creating next generation. This will be generation number " + _generationNumber + ".");

        //BFS alternative that automatically adds members from every species, without increasing population size.
        int currentNumberOfOffspring = 0;
        int speciesIterator = 0;
        List<NeatGenome> newPopulation = new ArrayList<>();
        List<List<NeatGenome>> clearedSpecies = new ArrayList<>();   //Species with just the representative

        int     amountToSpawn;
        int     size;
        int     meanIndx;
        int     maxIndx;
        int     max;
        int     parent1Indx;
        int     parent2Indx;
        Random  rand = new Random();
        boolean maybeMoreSpawnsRequired;

        while(currentNumberOfOffspring < _populationSize) {
            maybeMoreSpawnsRequired = false;
            for(Species sp : _currentSpecies) {
                if(currentNumberOfOffspring < _populationSize) {
                    if(speciesIterator == 0) {
                        List<NeatGenome> newSpeciesPop = new ArrayList<>();
                        newSpeciesPop.add(new NeatGenome(sp.getIndividuals().get(0)));
                        clearedSpecies.add(newSpeciesPop);
                        ++currentNumberOfOffspring;
                        maybeMoreSpawnsRequired = true;
                    }
                    else {
                        amountToSpawn = sp.getSpawnsRequired();

                        if(amountToSpawn > 0) {
                            maybeMoreSpawnsRequired = true;
                            size = sp.getIndividuals().size();

                            if(size == 0) {         //Size should never be 0
                                System.out.println("SOMETHING WENT WRONG. AT LEAST ONE SPECIES HAS SIZE 0!!! (function create newGeneration)");
                                System.exit(0);
                            }
                            else if(size == 1) {
                                newPopulation.add(sp.getIndividuals().get(0));
                                ++currentNumberOfOffspring;
                            }
                            else {
                                meanIndx = (int) (size * _likelihoodOfBestMating);     //TODO: check if this works properly
                                max      = (int) rand.nextGaussian() * meanIndx + (int) (size * 0.5) + 1;
                                if(max > 4)
                                    maxIndx  = ThreadLocalRandom.current().nextInt(3, max);
                                else
                                    maxIndx = 2;        //maxIndx needs to be at least 2 to select different parents

                                if(maxIndx < (int) Math.round(size * _likelihoodOfBestMating))
                                    maxIndx = (int) Math.round(size * _likelihoodOfBestMating);
                                if(maxIndx > size)      //size is always greater or equal to 2
                                    maxIndx = size;

                                parent1Indx = ThreadLocalRandom.current().nextInt(0, maxIndx);

                                if(rand.nextDouble() > _probabilityOfMating)
                                    newPopulation.add(new NeatGenome(sp.getIndividuals().get(parent1Indx)));
                                else {
                                    parent2Indx = parent1Indx;
                                    while(parent1Indx == parent2Indx)
                                        parent2Indx = ThreadLocalRandom.current().nextInt(0, maxIndx);

                                    newPopulation.add(crossover(sp.getIndividuals().get(parent1Indx), sp.getIndividuals().get(parent2Indx)));
                                    ++currentNumberOfOffspring;
                                }
                            }
                            sp.setSpawnsRequired(sp.getSpawnsRequired() - 1);
                        }
                    }
                }
                else
                    break;      //gets out of the foreach that goes over species
            }

            ++speciesIterator;

            //If after each species generated all required spawns there is still a smaller population than
            //required, use tournament method to select parents to generate new members
            if(!maybeMoreSpawnsRequired) {
                while(currentNumberOfOffspring < _populationSize) {
                    NeatGenome temp1 = _currentPopulation.get(ThreadLocalRandom.current().nextInt(0, _currentPopulation.size()));
                    NeatGenome lastTemp1 = _currentPopulation.get(ThreadLocalRandom.current().nextInt(0, _currentPopulation.size()));

                    for (int i = 0; i < 5; ++i) {
                        NeatGenome temp2 = _currentPopulation.get(ThreadLocalRandom.current().nextInt(0, _currentPopulation.size()));

                        if (temp2.getAdjustedFitness() > temp1.getAdjustedFitness()) {
                            lastTemp1 = temp1;
                            temp1 = temp2;
                        }
                    }

                    newPopulation.add(crossover(lastTemp1, temp1));
                    ++currentNumberOfOffspring;
                }
            }
        }



        //Mutate the new elements. The old bast elements of each species are left as they were, in case they were
        //already the best choice of the population. This way the fitness of the best member of the population
        //never decreases.
        for(NeatGenome ng : newPopulation) {

            //Function that randomly mutates weights
            ng.mutateWeights();

            //Function that randomly mutates ActivationResponses
            ng.mutateActivationResponses();

            //Function that has a chance of adding a new (possibly looped) connection.
            double addConnectionChance          = 0.90;
            double chanceOfLoopedConnection     = 0.15;
            int    numberOfTriesToFindLoop      = ng.getNeurons().size() - _numberOfInputs -1;
            int    numberOfTriesToAddConnection = ng.getNeurons().size()/3;
            ng.addConnection(addConnectionChance, chanceOfLoopedConnection, numberOfTriesToFindLoop, numberOfTriesToAddConnection);

            //Function that has a chance of adding a new neuron to an existing connection.
            double addNeuronChance                  = 0.70;
            int    numberOfTriesToFindOldConnection = _numberOfInputs*_numberOfOutputs;
            ng.addNeuron(addNeuronChance, numberOfTriesToFindOldConnection);
        }


        //Finally, add the best genomes of the previous generation (which are unchanged) to the new population and
        //set them as the species representatives for speciation after fitness estimation.
        int currentSpeciesIndex = 0;
        for(List<NeatGenome> lng : clearedSpecies) {
            _currentSpecies.get(currentSpeciesIndex).setIndividuals(lng);
            _currentPopulation.add(lng.get(0));
        }




        //DFS alternative that might skip members of the last species and increase population size.
        /*int currentNumberOffspring = 0;
        for(Species sp : _currentSpecies) {
            List<NeatGenome> newSpeciesPop = new ArrayList<>();

            newSpeciesPop.add(new NeatGenome(sp.getIndividuals().get(0)));

            int amountToSpawn = sp.getSpawnsRequired();
            int size = sp.getIndividuals().size();
            int meanIndx = (int)(size*_likelihoodOfBestMating);     //TODO: check if this works properly
            int maxIndx;
            Random _rand = new Random();


            for(int i = 1; i < amountToSpawn; ++i) {
                if (currentNumberOffspring > _populationSize)
                    break;

                maxIndx = ThreadLocalRandom.current().nextInt(1, (int)_rand.nextGaussian()*meanIndx+(int)(size*0.2));
                if(maxIndx > size-1)
                    maxIndx = size-1;
                if(maxIndx < size*_likelihoodOfBestMating)
                    maxIndx = (int)Math.round(size*_likelihoodOfBestMating);

                int parent1Indx = ThreadLocalRandom.current().nextInt(0, maxIndx);

                if(_rand.nextDouble() > _probabilityOfMating || size == 1) {
                    newSpeciesPop.add(new NeatGenome(sp.getIndividuals().get(parent1Indx)));
                }
                else {
                    int parent2Indx = parent1Indx;
                    while(parent1Indx == parent2Indx)
                        parent2Indx = ThreadLocalRandom.current().nextInt(0, maxIndx);

                    newSpeciesPop.add(sp.crossover(sp.getIndividuals().get(parent1Indx), sp.getIndividuals().get(parent2Indx)));
                }
                ++currentNumberOffspring;
            }
            sp.setIndividuals(newSpeciesPop);
        }

        List<NeatGenome> newPopulation = new ArrayList<>();

        for(Species sp : _currentSpecies)
            for(NeatGenome ng : sp.getIndividuals())
                newPopulation.add(ng);

        while(newPopulation.size() < _populationSize) {
            NeatGenome temp1 = _currentPopulation.get(ThreadLocalRandom.current().nextInt(0, _currentPopulation.size()));
            for(int i = 0; i < 5; ++i) {
                NeatGenome temp2 = _currentPopulation.get(ThreadLocalRandom.current().nextInt(0, _currentPopulation.size()));

                if(temp2.getAdjustedFitness() > temp1.getAdjustedFitness())
                    temp1 = temp2;
            }

            newPopulation.add(new NeatGenome(temp1));
        }*/



        System.out.println("Generation number " + _generationNumber + " created.");
    }




    //Checked. Seems to be fine
    public void createPopulation() {
        System.out.println("Creating initial population.");

        for(int i = 0; i < _populationSize; ++i)
            _currentPopulation.add(new NeatGenome(_numberOfInputs, _numberOfOutputs));

        System.out.println("Initial population created.");
    }





    public void defineSpecies() {

        if (_generationNumber == 1)
            System.out.println("Defining population species.");
        else
            System.out.println("Redefining population species.");

        speciate();

        if (_generationNumber == 1)
            System.out.println("Population species defined.");
        else
            System.out.println("Population species redefined.");
    }

    public void estimateFitness() {
        System.out.println("Starting fitness estimation.");
        //TODO
        System.out.println("Fitness estimation finished.");
    }

    public void saveRelevantData() {
        System.out.println("DO NOT STOP PROGRAM NOW!");
        System.out.println("Saving relevant data.");
        //TODO
        System.out.println("Relevant data saved.");
        System.out.println("PROGRAM CAN NOW BE STOPPED!");
    }


    private void speciate() {
        for(NeatGenome ng: _currentPopulation) {
            if(_currentSpecies.size() == 0) {
                List<NeatGenome> membersOfSpecies = new ArrayList<>();
                membersOfSpecies.add(ng);
                _currentSpecies.add(new Species(membersOfSpecies));
            }
            else {
                for(Species species : _currentSpecies) {
                    if(compatible(ng, species.getRepresentative())) {
                        species.getIndividuals().add(ng);
                        break;
                    }
                }
                List<NeatGenome> membersOfSpecies = new ArrayList<>();
                membersOfSpecies.add(ng);
                _currentSpecies.add(new Species(membersOfSpecies));
            }
        }

        for(Species sp : _currentSpecies) {
            //TODO: implement better sorter than bubblesort... Check if bubblesort works at all...
            int size = sp.getIndividuals().size();
            boolean sort = true;
            NeatGenome temp;
            int i, j=1;

            while(sort) {
                sort = false;
                for(i = 0; i < size - j; ++i) {
                    if(sp.getIndividuals().get(i).getFitness() < sp.getIndividuals().get(i+1).getFitness()) {
                        temp = sp.getIndividuals().remove(i);
                        sp.getIndividuals().add(i+1, temp);
                        sort = true;
                    }
                }
                ++j;
            }

            sp.adjustFitness();
        }

        double averagePopulationFitness = 0;
        for(NeatGenome ng : _currentPopulation)
            averagePopulationFitness += ng.getAdjustedFitness();
        averagePopulationFitness = averagePopulationFitness/_currentPopulation.size();

        for(NeatGenome ng : _currentPopulation)
            ng.calculateSpawns(averagePopulationFitness);

        for(Species sp : _currentSpecies)
            sp.calculateSpawnsRequired();
    }


    private boolean compatible(NeatGenome elementToCompare, NeatGenome representative) {
        int i1 = 0;
        int i2 = 0;
        int disjointGenes = 0;
        int excessGenes = 0;
        double matchingGenes = 0;
        double weightDifference = 0.0;
        int i1InnovN;
        int i2InnovN;
        double maxGenes = 1;

        while (elementToCompare.getConnections().size() > i1
                || representative.getConnections().size() > i2) {

            if (i1 == elementToCompare.getConnections().size()) {
                ++i2;
                ++excessGenes;
                continue;
            }

            if (i2 == elementToCompare.getConnections().size()) {
                ++i1;
                ++excessGenes;
                continue;
            }


            i1InnovN = elementToCompare.getConnections().get(i1).getInnovationN();
            i2InnovN = representative.getConnections().get(i2).getInnovationN();

            if (i1InnovN == i2InnovN) {
                ++i1;
                ++i2;
                ++matchingGenes;
                weightDifference += Math.abs(elementToCompare.getConnections().get(i1).getWeight()
                        - representative.getConnections().get(i2).getWeight());
                continue;
            } else if (i1 < i2) {
                ++i1;
                ++disjointGenes;
            } else {
                ++i2;
                ++disjointGenes;
            }
            maxGenes = elementToCompare.getConnections().size() > representative.getConnections().size()
                     ? elementToCompare.getConnections().size() : representative.getConnections().size();
        }

        double score = ((c1 * excessGenes / maxGenes) + (c2 * disjointGenes / maxGenes)
                + (c3 * weightDifference / matchingGenes));

        return score < _compatibilityThreshold;
    }


    public void epoch() {

        if(_generationNumber > 1)
            //TODO: CHECKING ZONE 0
            createNewGeneration();

        generatePhenotypes();
        estimateFitness();

        defineSpecies();
    }


    public void generatePhenotypes() {
        //TODO
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

        NeatGenome newGenome = new NeatGenome(babyNeurons, babyConnections, parent1.getNumberOfInputs(), parent1.getNumberOfOutputs());
        newGenome.createPossibleListsForEachNeuron(newGenome.getNeurons(), newGenome.getConnections());
        return newGenome;
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
}
