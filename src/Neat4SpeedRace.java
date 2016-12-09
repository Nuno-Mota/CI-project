import cicontest.algorithm.abstracts.AbstractRace;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResult;
import cicontest.torcs.race.RaceResults;
import scr.Controller;

import java.io.Serializable;

public class Neat4SpeedRace extends AbstractRace implements Serializable {

    private boolean             _DEBUG = true;

    public int[] runQualification(DefaultDriverGenome[] drivers, boolean withGUI){
        Neat4SpeedDriver[] driversList = new Neat4SpeedDriver[drivers.length + 1 ];
        for(int i=0; i<drivers.length; i++){
            driversList[i].loadGenome(drivers[i]);
        }
        return runQualification(driversList, withGUI);
    }


    public int[] runRace(Neat4SpeedDriver[] drivers, boolean withGUI){
        int size = Math.min(10, drivers.length);
        Neat4SpeedDriver[] driversList = new Neat4SpeedDriver[size];
        if(_DEBUG)
            System.out.println("NEAT4SPEEDRACE: Initializing drivers");
        for(int i=0; i<size; i++)
            driversList[i] = drivers[i];
        if(_DEBUG)
            System.out.println("NEAT4SPEEDRACE: Initializing race");
        int[] results;
        results = runRace(driversList, withGUI, true);
        if(_DEBUG)
            System.out.println("NEAT4SPEEDRACE: Finished race");
        return results;
    }

    @Override
    public int[] runRace(Driver[] drivers, boolean withGUI, boolean randomOrder) {
        int[] fitness = new int[drivers.length];
        if(drivers.length > 10) {
            throw new RuntimeException("Only 10 drivers are allowed in a RACE");
        } else {
            Race race = new Race();
            race.setTrack(this.tracktype, this.track);
            race.setTermination(Race.Termination.LAPS, this.laps);
            race.setStage(Controller.Stage.RACE);
            Neat4SpeedDriver NEATDriver = (Neat4SpeedDriver)drivers[0];
            drivers[0] = NEATDriver;
            Driver[] results = drivers;
            int i = drivers.length;

            for(int var8 = 0; var8 < i; ++var8) {
                Driver driver = results[var8];
                race.addCompetitor(driver);
            }

            if(randomOrder) {
                race.shuffleOrder();
            }

            RaceResults var10;
            if(withGUI) {
                var10 = race.runWithGUI();
            } else {
                var10 = race.run();
            }

            for(i = 0; i < drivers.length; ++i) {
                fitness[i] = ((RaceResult)var10.get(drivers[i])).getPosition();
            }

            this.printResults(drivers, var10);
            return fitness;
        }
    }


    public void showBest(){
//        if(DriversUtils.getStoredGenome() == null ){
//            System.err.println("No best-genome known");
//            return;
//        }
//
//        DefaultDriverGenome best = (DefaultDriverGenome) DriversUtils.getStoredGenome();
//        Neat4SpeedDriver driver = new Neat4SpeedDriver(null); //TODO: create NeuralNetwork to pass to driver
//        driver.loadGenome(best);
//
//        Neat4SpeedDriver[] driversList = new Neat4SpeedDriver[1];
//        driversList[0] = driver;
//        runQualification(driversList, true);
    }

    public void showBestRace(){
//        if(DriversUtils.getStoredGenome() == null ){
//            System.err.println("No best-genome known");
//            return;
//        }
//
//        DefaultDriver[] driversList = new DefaultDriver[1];
//
//        for(int i=0; i<10; i++){
//            DefaultDriverGenome best = (DefaultDriverGenome) DriversUtils.getStoredGenome();
//            DefaultDriver driver = new DefaultDriver();
//            driver.loadGenome(best);
//            driversList[i] = driver;
//        }
//
//        runRace(driversList, true, true);
    }

    public void raceBest(){
//
//        if(DriversUtils.getStoredGenome() == null ){
//            System.err.println("No best-genome known");
//            return;
//        }
//
//        Driver[] driversList = new Driver[10];
//        for(int i=0; i<10; i++){
//            DefaultDriverGenome best = (DefaultDriverGenome) DriversUtils.getStoredGenome();
//            Neat4SpeedDriver driver = new Neat4SpeedDriver(null); //TODO: Creat NeuralNetwork to pass to driver
//            driver.loadGenome(best);
//            driversList[i] = driver;
//        }
//        driversList[0] = new Human();
//        runRace(driversList, true, true);
    }
}
