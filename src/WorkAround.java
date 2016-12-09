public class WorkAround implements Runnable {   //TODO: THIS CLASS IS ACTUALLY NEVER USED! FOUND A BETTER WAY AROUND
                                                //TODO: Didn't even work properly, to be honest....

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
