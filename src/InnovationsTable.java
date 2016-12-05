import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InnovationsTable implements Serializable {

    /**********************
     * Internal Variables *
     **********************/

    private int                     _globalInnovationNumber = -1;
    private int                     _globalNeuronNumber     = -1;
    private List<Innovation>        _innovations            = new ArrayList<>();

    private static InnovationsTable _instance               = null;



    /***************
     * Constructor *
     ***************/

    private InnovationsTable() {
        // Exists only to defeat instantiation.
    }


    public static InnovationsTable getInstance() {
        if(_instance == null) {
            _instance = new InnovationsTable();
        }
        return _instance;
    }



    /***********************
     * Getters and Setters *
     ***********************/

    public int getGlobalInnovationNumber() { return _globalInnovationNumber; }
    private void setGlobalInnovationNumber(int globalInnovationNumber) { _globalInnovationNumber = globalInnovationNumber; }

    public int getGlobalNeuronNumber() { return _globalNeuronNumber; }
    private void setGlobalNeuronNumber(int globalNeuronNumber) { _globalNeuronNumber = globalNeuronNumber; }

    public List<Innovation> getInnovations() { return _innovations; }
    //Checked. Seems to be fine
    public int getInnovationID(Innovation innovation) {
        for (Innovation i: _innovations) {
            if (i.getInnovationType()        == innovation.getInnovationType() &&
                i.getInputNeuronID()         == innovation.getInputNeuronID()  &&
                i.getOutputNeuronID()        == innovation.getOutputNeuronID() &&
                i.getNumberOfTimesDisabled() == innovation.getNumberOfTimesDisabled())
                return i.getInnovationID();
        }
        return -1;  //Return -1 if innovation does not exist
    }
    public void addInnovation(Innovation innovation) {
        innovation.setInnovationID(++_globalInnovationNumber);
        if(innovation.getInnovationType() == 0)
            innovation.setNeuronID(++_globalNeuronNumber);
        _innovations.add(innovation);
    }
    private void setInnovations(List<Innovation> innovations) { _innovations = innovations; }

    public int getNeuronID(int innovationID) { return _innovations.get(innovationID).getNeuronID(); }
}
