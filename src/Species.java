import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Species implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private static int           _globalSpeciesID   = 0;

    private List<NeatGenome>    _individuals = new ArrayList<NeatGenome>();
    private int                 _speciesID;
    private double              _bestFitness = 0;
    private double              _averageSpeciesFitness;
    private int                 _numberOfGenerationsWithNoImprovement = -1;
    private int                 _ageOfSpecies = -1;
    private int                 _spawnsRequired;
    private double              _speciesFitness;

    private static final int    _youngSpeciesThreshold = 30;
    private static final int    _oldSpeciesThreshold   = 400;
    private static final double _youngFitnessBonus     = 1.4;
    private static final double _olderFitnessPenalty   = 0.9;    //TODO: variable penalty




    /***************
     * Constructor *
     ***************/

    public Species(List<NeatGenome> individuals) {
        _speciesID = ++_globalSpeciesID;
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
