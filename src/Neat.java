import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Neat implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private boolean             _DEBUG = false;
    private String[][]          _tracks = {{"wheel-2", "road"}, {"spring", "road"}, {"street-1", "road"},
                                           {"alpine-1", "road"}, {"e-track-1", "road"}, {"e-track-2", "road"},
                                           {"ole-road-1", "road"}, {"dirt-2", "dirt"}, {"dirt-4", "dirt"},
                                           {"dirt-6", "dirt"}, {"mixed-1", "dirt"}, {"mixed-2", "dirt"},
                                           {"f-speedway", "oval"}};
//    private String[][]          _tracks = {{"wheel-2", "road"}};//FOR TESTING
    private InnovationsTable    _innovationsTable = InnovationsTable.getInstance();

    private int                 _numberOfInputs;
    private int                 _numberOfOutputs;
    private int                 _populationSize;
    private int                 _generationNumber         = 1;
    private List<NeatGenome>    _currentPopulation        = new ArrayList<>();
    private List<Species>       _currentSpecies           = new ArrayList<>();
    private double              _averagePopulationFitness;
    private NeatGenome          _bestPerformingMember;
    private boolean             _file1                    = true;



    /**********************
     *     Parameters     *
     **********************/
    private double              _compatibilityThreshold;
    private int                 c1,
                                c2,
                                c3 ;
    private double              _probabilityOfMating;
    private double              _bestOfSpeciesPercentage;
    private double              _matingSTDEV;
    private int                 _maxGenWithoutImprovment;





    /****************
     * Constructors *
     ****************/

    public Neat(int numberOfInputs, int numberOfOutputs, int populationSize) {
        _numberOfInputs    = numberOfInputs;
        _numberOfOutputs   = numberOfOutputs;
        _populationSize    = populationSize;
        readNeatProperties();
        createPopulation();
    }




    public Neat (int numberOfInputs, int numberOfOutputs, int populationSize, int generationNumber,
                 List<NeatGenome> currentPopulation) {
        _numberOfInputs    = numberOfInputs;
        _numberOfOutputs   = numberOfOutputs;
        _populationSize    = populationSize;
        _generationNumber  = generationNumber;
        _currentPopulation = currentPopulation;
        readNeatProperties();
    }

    public void readNeatProperties(){
        try (InputStream in = new FileInputStream("neat.properties")) {
            Properties prop = new Properties();
            prop.load(in);
            in.close();
            _compatibilityThreshold        = Double.parseDouble(prop.getProperty("compatibilityThreshold"));
            c1                             = Integer.parseInt(prop.getProperty("c1"));
            c2                             = Integer.parseInt(prop.getProperty("c2"));
            c3                             = Integer.parseInt(prop.getProperty("c3"));
            _probabilityOfMating           = Double.parseDouble(prop.getProperty("probabilityOfMating"));
            _bestOfSpeciesPercentage       = Double.parseDouble(prop.getProperty("bestOfSpeciesPercentage"));
            _matingSTDEV                   = Double.parseDouble(prop.getProperty("matingSTDEV"));
            _maxGenWithoutImprovment       = Integer.parseInt(prop.getProperty("maxGenWithoutImprovment"));
        } catch (IOException e) {
            System.err.println("Something went wrong while reading in the properties.");
        }
    }


    /***********************
     * Getters and Setters *
     ***********************/

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

    public List<Species> getCurrentSpecies() {
        return _currentSpecies;
    }
    private void setCurrentSpecies(List<Species> currentSpecies) {
        _currentSpecies = currentSpecies;
    }

    public double getBestOfSpeciesPercentage() {
        return _bestOfSpeciesPercentage;
    }

    public double getProbabilityOfMating() {
        return _probabilityOfMating;
    }

    public double getAveragePopulationFitness() {
        return _averagePopulationFitness;
    }
    private void setAveragePopulationFitness(double averagePopulationFitness) {
        _averagePopulationFitness = averagePopulationFitness;
    }

    public NeatGenome getBestPerformingMember() {
        return _bestPerformingMember;
    }
    private void setBestPerformingMember(NeatGenome bestPerformingMember) {
        _bestPerformingMember = bestPerformingMember;
    }

    public void toggleFile() {
        _file1 = !_file1;
    }



    /******************
     * NEAT's methods *
     ******************/

    public void epoch() {

        if(_generationNumber > 1)
            createNewGeneration();


        generatePhenotypes();
        estimateFitness();
        defineSpecies();
        saveRelevantData();
    }




    //Checked. Seems to be fine
    public void createPopulation() {
        System.out.println("Creating initial population.");

        for(int i = 0; i < _populationSize; ++i)
            _currentPopulation.add(new NeatGenome(_numberOfInputs, _numberOfOutputs, _innovationsTable));

        System.out.println("Initial population created.");
    }




    //Checked. Seems to be fine
    public void createNewGeneration() {
        //System.out.println("Creating next generation. This will be generation number " + _generationNumber + ".");


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

        NeatGenome best = _currentPopulation.get(0);

        int k = 0;
        while(k < _currentSpecies.size()) {
            if(_currentSpecies.get(k).getNumberOfGenerationsWithNoImprovement() >= _maxGenWithoutImprovment) {
                for(NeatGenome ng : _currentSpecies.get(k).getIndividuals()) {
                    _currentPopulation.remove(ng);
                }
                _currentSpecies.remove(k);
            }
            else
                ++k;
        }

        while(currentNumberOfOffspring < _populationSize) {
            maybeMoreSpawnsRequired = false;
            for(Species sp : _currentSpecies) {
                if(currentNumberOfOffspring < _populationSize) {
                    //Just serves the purpose of re-adding the best element of each species
                    //to the next generation, without having them suffer mutations
                    if(speciesIterator == 0) {
                        if(_DEBUG)
                            System.out.println("Select Best of Species");
                        List<NeatGenome> newSpeciesPop = new ArrayList<>();
                        newSpeciesPop.add(sp.getIndividuals().get(0));
                        clearedSpecies.add(newSpeciesPop);
                        ++currentNumberOfOffspring;
                        maybeMoreSpawnsRequired = true;
                    }
                    //Selects parents, within the species, to generate one new offspring,
                    // if species still requires new spawns
                    else {
                        amountToSpawn = sp.getSpawnsRequired();

                        if(amountToSpawn > 0) {//Check whether new spawns are required
                            if(_DEBUG)
                                System.out.println("Spawn New Individuals");
                            maybeMoreSpawnsRequired = true;
                            size = sp.getIndividuals().size();

                            if(size == 0) {//Size should never be 0. I'M NOT EXPECTING THE PROGRAM TO GO INTO THIS IF(), EVER
                                System.out.println("SOMETHING WENT WRONG. AT LEAST ONE SPECIES HAS SIZE 0!!! (function create newGeneration)");
                                System.exit(0);
                            }
                            else if(size == 1) {//adds (again) the best and only element of the species, but this will be mutated
                                newPopulation.add(new NeatGenome(sp.getIndividuals().get(0), _innovationsTable));
                                ++currentNumberOfOffspring;
                            }
                            else {
                                if(_DEBUG)
                                    System.out.println("Selecting max index for parent selection");

                                //gets the index (approx) of the element corresponding to _bestOfSpeciesPercentage % of the species
                                //so that better performing elements have a higher chance of mating
                                meanIndx = (int)(size * _bestOfSpeciesPercentage);

                                //adds gaussian noise, so that lower performing members have a chance of mating with better performing ones
                                max      = (int) Math.abs(rand.nextGaussian() * size * _matingSTDEV) + meanIndx + 1;

                                //attributes a value to the maxIndx, effectively determining which elements of
                                //the species have a chance of mating
                                if(max >= 4)
                                    maxIndx = ThreadLocalRandom.current().nextInt(3, max);
                                else
                                    maxIndx = 2;        //maxIndx needs to be at least 2 to select different parents

                                //makes sure that the gaussian noise add doesn't create a maxIndx out
                                //of range of the population arrayList
                                if(maxIndx > size)      //size is always greater or equal to 2
                                    maxIndx = size;

                                //Selects first potential parent
                                parent1Indx = ThreadLocalRandom.current().nextInt(0, maxIndx);

                                //There's a chance that the first potential parent doesn't mate at all and
                                //just gets added to the new population (with future possible mutations)
                                if(rand.nextDouble() > _probabilityOfMating)
                                    newPopulation.add(new NeatGenome(sp.getIndividuals().get(parent1Indx), _innovationsTable));
                                else {
                                    parent2Indx = parent1Indx;

                                    if(_DEBUG)
                                        System.out.println("Selecting 2nd parent");
                                    if(_DEBUG)
                                        System.out.println("MaxIndx = " + maxIndx);
                                    if(_DEBUG)
                                        System.out.println("Number Of Individuals = " + sp.getIndividuals().size());

                                    //selects the second parent, making sure that it is not the same
                                    while(parent1Indx == parent2Indx)
                                        parent2Indx = ThreadLocalRandom.current().nextInt(0, maxIndx);

                                    if(_DEBUG)
                                        System.out.println("Going to crossover selected parents");

                                    newPopulation.add(crossover(sp.getIndividuals().get(parent1Indx), sp.getIndividuals().get(parent2Indx)));
                                }
                                ++currentNumberOfOffspring;
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
            //required, use tournament method to select parents to generate new members. Allows for any genome to mate
            if(!maybeMoreSpawnsRequired) {
                if(_DEBUG)
                    System.out.println("TOURNAMENT");
                while(currentNumberOfOffspring < _populationSize) {
                    NeatGenome temp1 = _currentPopulation.get(ThreadLocalRandom.current().nextInt(0, _currentPopulation.size()));
                    NeatGenome lastTemp1 = _currentPopulation.get(ThreadLocalRandom.current().nextInt(0, _currentPopulation.size()));

                    if(_DEBUG)
                        System.out.println("Selecting parents for tournament");

                    for (int i = 0; i < 5; ++i) {
                        NeatGenome temp2 = _currentPopulation.get(ThreadLocalRandom.current().nextInt(0, _currentPopulation.size()));

                        if (temp2.getAdjustedFitness() > temp1.getAdjustedFitness()) {
                            lastTemp1 = temp1;
                            temp1 = temp2;
                        }
                    }

                    if(_DEBUG)
                        System.out.println("Crossover for tournament");

                    newPopulation.add(crossover(lastTemp1, temp1));
                    ++currentNumberOfOffspring;
                }
            }
        }



        //Mutate the new elements. The old best elements of each species are left as they were, in case they were
        //already the best choice of the population. This way the fitness of the best member of the population
        //never decreases.
        for(NeatGenome ng : newPopulation) {

            if(_DEBUG)
                System.out.println("Mutate Weights");

            //Function that randomly mutates weights
            ng.mutateWeights();

            if(_DEBUG)
                System.out.println("Mutate Activation Response");

            //Function that randomly mutates ActivationResponses
            ng.mutateActivationResponses();

            if(_DEBUG)
                System.out.println("Disable Random Connection");

            //Function that has a chance of disabling a random enabled connection
            int numberOfTriesToDisableConnection = ng.getConnections().size()/(_numberOfInputs+1+_numberOfOutputs) + 1;
            ng.disableConnection(numberOfTriesToDisableConnection);

            if(_DEBUG)
                System.out.println("Enable Random Disconnected Connection");

            //Function that has a chance of enabling a random disabled connection
            int numberOfTriesToEnableConnection = ng.getConnections().size()/(_numberOfInputs+1) + 1;
            ng.enableConnection(numberOfTriesToEnableConnection);

            if(_DEBUG)
                System.out.println("Add Connection Mutation");

            //Function that has a chance of adding a new (possibly looped) connection.
            //input and bias neurons shouldn't have looped connections
            int    numberOfTriesToFindLoop      = ng.getNeurons().size() - _numberOfInputs -1;
            int    numberOfTriesToAddConnection = ng.getNeurons().size()/3; //parameter 3 was sort of random... Change as required
            ng.addConnection(numberOfTriesToFindLoop, numberOfTriesToAddConnection);

            if(_DEBUG)
                System.out.println("Add Neuron Mutation");

            //Function that has a chance of adding a new neuron to an existing connection.
            int    numberOfTriesToFindOldConnection = ng.getConnections().size()/2;//_numberOfInputs*_numberOfOutputs;?
            ng.addNeuron(numberOfTriesToFindOldConnection);
        }


        _currentPopulation = newPopulation;

        //Finally, add the best genomes of the previous generation (which are unchanged) to the new population and
        //set them as the species representatives for speciation after fitness estimation.
        int currentSpeciesIndex = 0;
        for(List<NeatGenome> lng : clearedSpecies) {
            _currentSpecies.get(currentSpeciesIndex++).setIndividuals(lng);
            _currentPopulation.add(lng.get(0));
        }
        System.out.println("Generation number " + _generationNumber + " created.");
    }




    //Checked. Seems to be fine
    public void generatePhenotypes() {
        for(NeatGenome ng : _currentPopulation)
            ng.createPhenotype();
    }




    public void estimateFitness() {
        System.out.println("Starting fitness estimation.");
        PrintStream original = System.out;
        int i;

        System.out.println("NUMBER OF SPECIES = " + _currentSpecies.size());

        Neat4SpeedDriver[] drivers = new Neat4SpeedDriver[1];
        for(NeatGenome ng : _currentPopulation) {
            ng.setFitness(0);
            System.out.println("\nGenomeID = " + ng.getGenomeID());
            System.out.println("Starting Fitness = " + ng.getFitness());
            if(_DEBUG)
                System.out.println("Genome size = " + ng.getConnections().size());
            if(_DEBUG)
                System.out.println("NEAT: starting race");

            for(i = 0; i < _tracks.length; ++i) {
                //Start a race
                System.setOut(new NullPrintStream());
                Neat4SpeedRace race = new Neat4SpeedRace();
                race.setTrack(_tracks[i][0], _tracks[i][1]);
                race.laps = 1;
                System.setOut(original);

                drivers[0] = new Neat4SpeedDriver(ng.getNeuralNetwork());


                //for speedup set withGUI to false
                System.setOut(new NullPrintStream());
                WorkAround myWorkAround = new WorkAround(race, drivers, false);
                Thread t = new Thread(myWorkAround);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    if(_DEBUG)
                        System.out.println("Closing Torcs");
                }
                System.setOut(original);

                System.out.println("FITNESS for track " + _tracks[i][0] + " = " + drivers[0].getFitness());
                ng.setFitness(ng.getFitness() + drivers[0].getFitness());
            }
            System.out.println("TotalFitness = " + ng.getFitness());
        }
        System.out.println("Fitness estimation finished.");
    }




    //Checked. Seems to be fine
    public void defineSpecies() {
//        if (_generationNumber == 1)
//            System.out.println("Defining population species.");
//        else
//            System.out.println("Redefining population species.");

        speciate();

        System.out.println("\nBEST FITNESS = " + getBestPerformingMember().getFitness());
        System.out.println("BEST GENOME = " + getBestPerformingMember().getGenomeID());

//        if (_generationNumber == 1)
//            System.out.println("Population species defined.");
//        else
//            System.out.println("Population species redefined.");
    }



    /*******************************************
     * Create New Generation Auxiliary Methods *
     *******************************************/

    //Checked. Seems to be fine
    public NeatGenome crossover (NeatGenome parent1, NeatGenome parent2) {

        int best;
        if(_DEBUG)
            System.out.println("CROSSOVER: determine the best parent");
        //If parents have the same fitness select the longest one.
        //If they also have the same length select randomly
        //TODO: should pick the shortest one?
        if (parent1.getFitness() == parent2.getFitness()) {
            if (parent1.getConnections().size() == parent2.getConnections().size())
                best = ThreadLocalRandom.current().nextInt(1, 3);
            else
                best = (parent1.getConnections().size() > parent2.getConnections().size()) ? 1 : 2;
        }
        //Else select the most fit parent
        else
            best = (parent1.getFitness() > parent2.getFitness()) ? 1 : 2;


        //Neurons and connections arrayLists for the offspring
        List<NeuronGene>     babyNeurons     = new ArrayList<>();
        List<ConnectionGene> babyConnections = new ArrayList<>();


        //Iterators that go over the corresponding parent genes
        int iterator1 = 0;
        int iterator2 = 0;


        ConnectionGene selectedGene = null;

        if(_DEBUG)
            System.out.println("CROSSOVER: parent1 size = " + parent1.getConnections().size());
        if(_DEBUG)
            System.out.println("CROSSOVER: parent2 size = " + parent2.getConnections().size());
        //While there are still genes to be parsed in at least one of the parents"
        while(!(iterator1 >= parent1.getConnections().size() &&
                iterator2 >= parent2.getConnections().size())) {


            //If we have gone over all genes of parent1, but there are still genes of parent 2 left we
            //can add them if parent 2 has the best fitness between both parents
            if(iterator1 >= parent1.getConnections().size()) {     //TODO: check if problems arise. Check book
                if(_DEBUG)
                    System.out.println("CROSSOVER: parent1 out of genes");
                if(best == 2)
                    selectedGene = new ConnectionGene(parent2.getConnections().get(iterator2));
                ++iterator2;
            }
            //If we have gone over all genes of parent2, but there are still genes of parent 1 left we
            //can add them if parent 1 has the best fitness between both parents
            else if(iterator2 >= parent2.getConnections().size()) {     //TODO: check if problems arise. Check book
                if(_DEBUG)
                    System.out.println("CROSSOVER: parent2 out of genes");
                if(best == 1)
                    selectedGene = new ConnectionGene(parent1.getConnections().get(iterator1));
                ++iterator1;
            }
            //If parent 1 has disjoint genes with a lower innovation number than the next gene of parent 2 go
            //over the next disjoint gene of parent 1 and add it if parent 1 has the best fitness between both parents
            else if(parent1.getConnections().get(iterator1).getInnovationN() <
                    parent2.getConnections().get(iterator2).getInnovationN()) {
                if(_DEBUG)
                    System.out.println("CROSSOVER: Parent1 lower innovation number");
                if(best == 1)
                    selectedGene = new ConnectionGene(parent1.getConnections().get(iterator1));
                ++iterator1;
            }
            //If parent2 has disjoint genes with a lower innovation number than the next gene of parent 1 go
            //over the next disjoint gene of parent 2 and add it if parent 2 has the best fitness between both parents
            else if(parent1.getConnections().get(iterator1).getInnovationN() >
                    parent2.getConnections().get(iterator2).getInnovationN()) {
                if(_DEBUG)
                    System.out.println("CROSSOVER: Parent2 lower innovation number");
                if(best == 2)
                    selectedGene = new ConnectionGene(parent1.getConnections().get(iterator1));
                ++iterator2;
            }
            //If parent 1's gene has the same innovation number of parent 2's gene, select one of them randomly
            //TODO: maybe implment a weighted average between the parents gene's values?
            else if(parent1.getConnections().get(iterator1).getInnovationN() ==
                    parent2.getConnections().get(iterator2).getInnovationN()) {
                if(_DEBUG)
                    System.out.println("CROSSOVER: Same gene");
                if(_DEBUG)
                    System.out.println("CROSSOVER: Same gene actually goes in");
                int choice = ThreadLocalRandom.current().nextInt(1, 3);

                if(choice == 1)
                    selectedGene = new ConnectionGene(parent1.getConnections().get(iterator1));
                else
                    selectedGene = new ConnectionGene(parent2.getConnections().get(iterator2));

                ++iterator1;
                ++iterator2;
            }
            else {
                System.out.println("CROSSOVER: Shouldn't happen");
                System.exit(0);
            }

            if(_DEBUG) {
                System.out.println(iterator1);
                System.out.println(iterator2);
                System.out.println("CROSSOVER: baby connections");
            }

            //Add selected gene, if it wasn't already added before
            if(babyConnections.size() == 0)
                babyConnections.add(selectedGene);
            else
                if(babyConnections.get(babyConnections.size() - 1).getInnovationN() != selectedGene.getInnovationN())
                    babyConnections.add(selectedGene);


            if(_DEBUG)
                System.out.println("CROSSOVER: adding baby neurons to connection");
            addBabyNeuron(selectedGene.getInputNeuron(), babyNeurons);
            addBabyNeuron(selectedGene.getOutputNeuron(), babyNeurons);
        }

        return new NeatGenome(babyNeurons, babyConnections, parent1.getNumberOfInputs(), parent1.getNumberOfOutputs(), _innovationsTable);
    }




    //Checked. Seems to be fine
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



    /********************************
     * Speciation Auxiliary Methods *
     ********************************/

    //Checked. Seems to be fine
    private void speciate() {

        //boolean _DEBUG = false;
        boolean createNewSpecies;

        if(_DEBUG) {
            for(Species sp : _currentSpecies) {
                System.out.println("\nElements of species " + sp.getSpeciesID() + " before copying");
                System.out.println("Species Age = " + sp.getAgeOfSpecies());
                for(NeatGenome ng : sp.getIndividuals()) {
                    System.out.println("GenomeID = " + ng.getGenomeID());
                    System.out.println("Fitness = " + ng.getFitness());
                }
            }
        }

        NeatGenome best = _currentPopulation.get(0);

        for(NeatGenome ng : _currentPopulation)
            if(ng.getFitness() > best.getFitness())
                best = ng;

        for(NeatGenome ng: _currentPopulation) {
            if(_currentSpecies.size() == 0) {
                List<NeatGenome> membersOfSpecies = new ArrayList<>();
                membersOfSpecies.add(ng);
                Species newSpecies = new Species(membersOfSpecies, _innovationsTable);
                newSpecies.setBestFitness(ng.getFitness());
                _currentSpecies.add(newSpecies);
            }
            else {
                createNewSpecies = true;
                for(Species species : _currentSpecies) {
                    if(compatible(ng, species.getRepresentative())) {
                        if(!species.getIndividuals().contains(ng))
                            species.getIndividuals().add(ng);
                        createNewSpecies = false;
                        break;
                    }
                }

                if(createNewSpecies) {
                    List<NeatGenome> membersOfSpecies = new ArrayList<>();
                    membersOfSpecies.add(ng);
                    Species newSpecies = new Species(membersOfSpecies, _innovationsTable);
                    newSpecies.setBestFitness(ng.getFitness());
                    _currentSpecies.add(newSpecies);
                }
            }
        }

        for(Species sp : _currentSpecies) {
            for(NeatGenome ng : sp.getIndividuals()) {
                for(Species spCheck : _currentSpecies) {
                    if(sp.getSpeciesID() != spCheck.getSpeciesID() && spCheck.getIndividuals().contains(ng))
                        spCheck.getIndividuals().remove(ng);
                }
            }
        }

        int k = 0;
        while(k < _currentSpecies.size()) {
            if(_currentSpecies.get(k).getIndividuals().size() == 0)
                _currentSpecies.remove(k);
            else
                ++k;
        }

        for(Species sp : _currentSpecies) {

            if(_DEBUG){
                System.out.println("\nElements of species " + sp.getSpeciesID());
                for(NeatGenome ng : sp.getIndividuals()) {
                    System.out.println("GenomeID = " + ng.getGenomeID());
                    System.out.println("Fitness = " + ng.getFitness());
                }
            }

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

            if(_DEBUG) {
                System.out.println("\nOrdered elements of species " + sp.getSpeciesID());
                for(NeatGenome ng : sp.getIndividuals()) {
                    System.out.println("GenomeID = " + ng.getGenomeID());
                    System.out.println("Fitness = " + ng.getFitness());
                }
            }


            if(sp.getBestFitness() >= sp.getIndividuals().get(0).getFitness() &&
               !(best.getGenomeID() == sp.getIndividuals().get(0).getGenomeID()))
                sp.increaseNumberOfGenerationsWithNoImprovement();
            else {
                sp.setNumberOfGenerationsWithNoImprovement(0);
                sp.setBestFitness(sp.getIndividuals().get(0).getFitness());
            }
            sp.increaseAgeOfSpecies();
            sp.adjustFitness();
        }

        _averagePopulationFitness = 0;
        for(NeatGenome ng : _currentPopulation)
            _averagePopulationFitness += ng.getAdjustedFitness();
        _averagePopulationFitness = _averagePopulationFitness/_currentPopulation.size();

        for(NeatGenome ng : _currentPopulation)
            ng.calculateSpawns(_averagePopulationFitness);

        for(Species sp : _currentSpecies)
            sp.calculateSpawnsRequired();

        _bestPerformingMember = _currentPopulation.get(0);
        for(NeatGenome ng : _currentPopulation)
            if(ng.getFitness() > _bestPerformingMember.getFitness())
                _bestPerformingMember = ng;
    }




    //Checked. Seems to be fine
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

            if (i2 == representative.getConnections().size()) {
                ++i1;
                ++excessGenes;
                continue;
            }


            i1InnovN = elementToCompare.getConnections().get(i1).getInnovationN();
            i2InnovN = representative.getConnections().get(i2).getInnovationN();

            if (i1InnovN == i2InnovN) {
                ++matchingGenes;
                weightDifference += Math.abs(elementToCompare.getConnections().get(i1).getWeight()
                        - representative.getConnections().get(i2).getWeight());
                ++i1;
                ++i2;
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



    /*********************
     * Save Data Methods *
     *********************/

    public void saveRelevantData() {
        System.out.println("Saving data. DO NOT STOP PROGRAM NOW!!!!!");





        double averageFitness = 0;
        double averageNumberOfConnections = 0;
        double averageNumberOfNeurons = 0;

        int maxNumberOfConnections = 0;
        double fitnessOfMaxNumberOfConnections = 0;
        int minNumberOfConnections = 0;
        double fitnessOfMinNumberOfConnections = 0;

        int maxNumberOfNeurons = 0;
        double fitnessOfMaxNumberOfNeurons = 0;
        int minNumberOfNeurons = 0;
        double fitnessOfMinNumberOfNeurons = 0;


        NeatGenome best = _currentPopulation.get(0);
        maxNumberOfConnections = best.getConnections().size();
        minNumberOfConnections = best.getConnections().size();
        maxNumberOfNeurons = best.getNeurons().size();
        minNumberOfNeurons = best.getNeurons().size();

        for(NeatGenome ng : _currentPopulation) {
            if(ng.getFitness() > best.getFitness())
                best = ng;
            if(ng.getConnections().size() > maxNumberOfConnections) {
                maxNumberOfConnections = ng.getConnections().size();
                fitnessOfMaxNumberOfConnections = ng.getFitness();
            }
            if(ng.getConnections().size() < minNumberOfConnections) {
                maxNumberOfConnections = ng.getConnections().size();
                fitnessOfMinNumberOfConnections = ng.getFitness();
            }
            if(ng.getNeurons().size() > maxNumberOfNeurons) {
                maxNumberOfNeurons = ng.getNeurons().size();
                fitnessOfMaxNumberOfNeurons = ng.getFitness();
            }
            if(ng.getNeurons().size() < minNumberOfNeurons) {
                minNumberOfNeurons = ng.getNeurons().size();
                fitnessOfMinNumberOfNeurons = ng.getFitness();
            }
            averageFitness += ng.getFitness();
            averageNumberOfConnections += ng.getConnections().size();
            averageNumberOfNeurons += ng.getNeurons().size();
        }
        averageFitness /= _currentPopulation.size();
        averageNumberOfConnections /= _currentPopulation.size();
        averageNumberOfNeurons /= _currentPopulation.size();


        String filename;

        filename = "src/memory/Single_Driver/Best_of_each_Generation/bestOfGen" + _generationNumber + ".java_serial";
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.writeObject(best.getNeuralNetwork());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        filename = "src/memory/Single_Driver/Statistics/data.txt";
        try(FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outWriter = new PrintWriter(bw)) {
            String info;
            info = _generationNumber + " " + _populationSize + " " + _currentSpecies.size() +
                   " " + best.getFitness() + " " + averageFitness + " " + averageNumberOfConnections +
                   " " + averageNumberOfNeurons + " " + maxNumberOfConnections + " " + fitnessOfMaxNumberOfConnections +
                   " " + minNumberOfConnections + " " + fitnessOfMinNumberOfConnections +
                   " " + maxNumberOfNeurons + " " + fitnessOfMaxNumberOfNeurons +
                   " " + minNumberOfNeurons + " " + fitnessOfMinNumberOfNeurons;
            outWriter.println(info);
        } catch (IOException e) {
            System.out.println("Error appending to statistics file: IOException.");
        }


        ++_generationNumber;

        if (_file1)
            filename = "src/memory/Single_Driver/Full_Generations/GenBeep.java_serial";
        else
            filename = "src/memory/Single_Driver/Full_Generations/GenBoop.java_serial";
        toggleFile();



        //Store the state of neat
        out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.writeObject(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Data saved. YOU CAN NOW STOP THE PROGRAM!");
    }




    public void propagateInnovationsTable() {
        for(NeatGenome ng : _currentPopulation)
            ng.setInnovationsTable(_innovationsTable);
        for(Species sp : _currentSpecies)
            sp.setInnovationsTable(_innovationsTable);
    }
}
