public class WorkAround implements Runnable {

    /**********************
     * Internal Variables *
     **********************/

    private Neat4SpeedRace        _race;
    private Neat4SpeedDriver[]    _drivers;
    private boolean               _withGUI;



    /****************
     * Constructors *
     ****************/


    public WorkAround (Neat4SpeedRace race, Neat4SpeedDriver[] drivers, boolean withGUI) {
        _race    = race;
        _drivers = drivers;
        _withGUI = withGUI;
    }



    /**************
     * Run method *
     **************/

    public void run() {
        _race.runRace(_drivers, _withGUI);
    }
}
