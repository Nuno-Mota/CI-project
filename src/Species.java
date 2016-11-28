import java.util.ArrayList;
import java.util.List;

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
}
