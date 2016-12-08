import cicontest.algorithm.abstracts.AbstractAlgorithm;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import race.TorcsConfiguration;

import java.io.File;

public class NEAT4SpeedDriverAlgorithm extends AbstractAlgorithm {
    Neat4SpeedDriver[] drivers = new Neat4SpeedDriver[1];
    int[] results = new int[1];

    public Class<? extends Driver> getDriverClass() {
        return DefaultDriver.class;
    }

    public void run(boolean continue_from_checkpoint) {
        if (!continue_from_checkpoint) {
            //init NN
            Neat4SpeedDriver genome = new Neat4SpeedDriver("src/memory/Single_Driver/Best_of_each_Generation/bestOfGen7.java_serial");
            drivers[0] = genome;

            //Start a race
            Neat4SpeedRace race = new Neat4SpeedRace();
            race.setTrack("spring", "road");
            race.laps = 1;

            //for speedup set withGUI to false
             results = race.runRace(drivers, true);

            // Save genome/nn
            //DriversUtils.storeGenome(drivers[0]);
        }
        // create a checkpoint this allows you to continue this run later
        DriversUtils.createCheckpoint(this);
        //DriversUtils.clearCheckpoint();
    }



    /************************
     * Driver's main method *     //This is the one to be run when racing. Otherwise comment for training?
     ************************/

    public static void main(String[] args) {

        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));
        /*
		 *
		 * Start without arguments to run the algorithm
		 * Start with -continue to continue a previous run
		 * Start with -show to show the best found
		 * Start with -show-race to show a race with 10 copies of the best found
		 * Start with -human to race against the best found
		 *
		 */
        NEAT4SpeedDriverAlgorithm algorithm = new NEAT4SpeedDriverAlgorithm();
        DriversUtils.registerMemory(algorithm.getDriverClass());
        if (args.length > 0 && args[0].equals("-show")) {
            new Neat4SpeedRace().showBest();
        } else if (args.length > 0 && args[0].equals("-show-race")) {
            new Neat4SpeedRace().showBestRace();
        } else if (args.length > 0 && args[0].equals("-human")) {
            new Neat4SpeedRace().raceBest();
        } else if (args.length > 0 && args[0].equals("-continue")) {
            if (DriversUtils.hasCheckpoint()) {
                DriversUtils.loadCheckpoint().run(true);
            } else {
                algorithm.run();
            }
        } else {
            algorithm.run();
        }
    }
}
