import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Species implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private InnovationsTable     _innovationsTable = InnovationsTable.getInstance();

    private List<NeatGenome>    _individuals = new ArrayList<NeatGenome>();
    private int                 _speciesID;
    private double              _bestFitness = 0;
    private double              _averageSpeciesFitness;
    private int                 _numberOfGenerationsWithNoImprovement = -1;
    private int                 _ageOfSpecies = -1;
    private int                 _spawnsRequired;
    private double              _speciesFitness;

    /**********************
     *     Parameters     *
     **********************/
    private static  int         _youngSpeciesThreshold;
    private static  int         _oldSpeciesThreshold;
    private static  double      _youngFitnessBonus;
    private static  double      _olderFitnessPenalty;    //TODO: variable penalty




    /***************
     * Constructor *
     ***************/

    public Species(List<NeatGenome> individuals) {
        readSpeciesProperties();
        _speciesID = _innovationsTable.getNewSpeciesID();
        _individuals = individuals;
    }

    public void readSpeciesProperties(){
        try (InputStream in = new FileInputStream("neat.properties")) {
            Properties prop = new Properties();
            prop.load(in);
            in.close();
            _youngSpeciesThreshold   = Integer.parseInt(prop.getProperty("youngSpeciesThreshold"));
            _oldSpeciesThreshold     = Integer.parseInt(prop.getProperty("oldSpeciesThreshold"));
            _youngFitnessBonus       = Double.parseDouble(prop.getProperty("youngFitnessBonus"));
            _olderFitnessPenalty     = Double.parseDouble(prop.getProperty("olderFitnessPenalty"));

        } catch (IOException e) {
            System.err.println("Something went wrong while reading in the properties.");
        }
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
    public void setIndividuals(List<NeatGenome> individuals) {
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
    public void setBestFitness(double bestFitness) {
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
    public void increaseNumberOfGenerationsWithNoImprovement() { ++_numberOfGenerationsWithNoImprovement; }
    public void setNumberOfGenerationsWithNoImprovement(int numberOfGenerationsWithNoImprovement) {
        _numberOfGenerationsWithNoImprovement = numberOfGenerationsWithNoImprovement;
    }

    public int getAgeOfSpecies() {
        return _ageOfSpecies;
    }
    public void increaseAgeOfSpecies() { ++_ageOfSpecies; }
    public void setAgeOfSpecies(int ageOfSpecies) {
        _ageOfSpecies = ageOfSpecies;
    }

    public int getSpawnsRequired() {
        return _spawnsRequired;
    }
    public void setSpawnsRequired(int spawnsRequired) {
        _spawnsRequired = spawnsRequired;
    }

    public double getSpeciesFitness() {
        return _speciesFitness;
    }
    private void setSpeciesFitness(double speciesFitness) {
        _speciesFitness = speciesFitness;
    }



    /*****************************
     * Fitness & Spawn Functions *
     *****************************/

    //Checked. Seems to be fine
    public void adjustFitness() {
        _speciesFitness = 0;

        for(NeatGenome ng : _individuals) {
            double fitness = ng.getFitness();

            if(_ageOfSpecies < _youngSpeciesThreshold)
                fitness *= _youngFitnessBonus;

            if(_ageOfSpecies > _oldSpeciesThreshold)
                fitness *= _olderFitnessPenalty;


            ng.setAdjustedFitness(fitness/_individuals.size());  //TODO: We might penalize very good species with this too much
            _speciesFitness += ng.getAdjustedFitness();
        }
    }




    //Checked. Seems to be fine
    public void calculateSpawnsRequired() {
        double totalSpawn = 0;
        for(NeatGenome ng : _individuals)
            totalSpawn += ng.getAmountToSpawn();

        _spawnsRequired = (int)Math.round(totalSpawn);
    }
}
