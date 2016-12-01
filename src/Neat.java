import java.util.ArrayList;
import java.util.List;

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



    /****************
     * Constructors *
     ****************/

    public Neat(int numberOfInputs, int numberOfOutputs, int populationSize) {
        _numberOfInputs  = numberOfInputs;
        _numberOfOutputs = numberOfOutputs;
        _populationSize  = populationSize;
        setCurrentPopulation(createPopulation());
    }

    public Neat (int numberOfInputs, int numberOfOutputs, int populationSize, int generationNumber, List<NeatGenome> currentPopulation) {
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



    /******************
     * NEAT's methods *
     ******************/

    public void createNextGeneration() {
        System.out.println("Creating next generation. Generation number " + _generationNumber + 1 + ".");//Check +1 part
        //TODO
        _generationNumber++;
        System.out.println("Generation number " + _generationNumber + " created.");
    }

    public List<NeatGenome> createPopulation() {
        System.out.println("Creating initial population.");
        //TODO
        System.out.println("Initial population created.");
        return null;    //TODO
    }

    public void defineSpecies() {

        if (_generationNumber == 1)
            System.out.println("Defining population species.");
        else
            System.out.println("Redefining population species.");
        //TODO
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
        _currentSpecies = new ArrayList<>();

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
    }


    private boolean compatible(NeatGenome elementToCompare, NeatGenome representative) {
        int i1 = 0;
        int i2 = 0;
        int disjointGenes = 0;
        int exessGenes = 0;
        double matchingGenes = 0;
        double weightDifference = 0.0;
        int i1InnovN;
        int i2InnovN;
        double maxGenes;

        while (elementToCompare.getConnections().size() > i1
                || representative.getConnections().size() > i2) {

            if (i1 == elementToCompare.getConnections().size()) {
                ++i2;
                ++exessGenes;
                continue;
            }

            if (i2 == elementToCompare.getConnections().size()) {
                ++i1;
                ++exessGenes;
                continue;
            }


            i1InnovN = elementToCompare.getConnections().get(i1).getInnovationN();
            i2InnovN = representative.getConnections().get(i2).getInnovationN();

            if (i1InnovN == i2InnovN) {
                ++i1;
                ++i2:
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

        double score = ((c1 * exessGenes / maxGenes) + (c2 * disjointGenes / maxGenes)
                + (c3 * weightDifference / matchingGenes));

        return score < _compatibilityThreshold;}
}
