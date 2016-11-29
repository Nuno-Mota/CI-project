import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InnovationsTable implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private int                     _globalInnovationNumber = -1;
    private List<Innovation>        _innovations            = new ArrayList<>();
    private static InnovationsTable _instance               = null;



    /***************
     * Constructor *
     ***************/

    private InnovationsTable() {
        // Exists only to defeat instantiation.
    }



    /***********************
     * Getters and Setters *
     ***********************/

    public int getGlobalInnovationNumber() { return _globalInnovationNumber; }
    private void setGlobalInnovationNumber(int globalInnovationNumber) { _globalInnovationNumber = globalInnovationNumber; }

    public List<Innovation> getInnovations() { return _innovations; }
    public int getInnovationID(Innovation innovation) {
        for (Innovation i: _innovations) {
            if (i.getInnovationType() == innovation.getInnovationType() &&
                i.getOutputNeuronID() == innovation.getOutputNeuronID() &&
                i.getInputNeuronID() == innovation.getInputNeuronID()) {
                return i.getInnovationID();
            }
        }
        return -1;  //Return -1 if innovation does not exist
    }
    public void addInnovation(Innovation innovation) {
        innovation.setInnovationID(++_globalInnovationNumber);
        _innovations.add(innovation);
    }
    private void setInnovations(List<Innovation> innovations) { _innovations = innovations; }

    public static InnovationsTable getInstance() {
        if(_instance == null) {
            _instance = new InnovationsTable();
        }
        return _instance;
    }
}
