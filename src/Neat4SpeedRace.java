import cicontest.algorithm.abstracts.AbstractRace;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;

import java.io.Serializable;

public class Neat4SpeedRace extends AbstractRace implements Serializable {

    private boolean             _DEBUG = false;

    public int[] runQualification(DefaultDriverGenome[] drivers, boolean withGUI){
        Neat4SpeedDriver[] driversList = new Neat4SpeedDriver[drivers.length + 1 ];
        for(int i=0; i<drivers.length; i++){
            driversList[i] = new Neat4SpeedDriver(null);//TODO: create NeuralNetwork to pass to driver
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
        return runRace(driversList, withGUI, true);
    }



    public void showBest(){
        if(DriversUtils.getStoredGenome() == null ){
            System.err.println("No best-genome known");
            return;
        }

        DefaultDriverGenome best = (DefaultDriverGenome) DriversUtils.getStoredGenome();
        Neat4SpeedDriver driver = new Neat4SpeedDriver(null); //TODO: create NeuralNetwork to pass to driver
        driver.loadGenome(best);

        Neat4SpeedDriver[] driversList = new Neat4SpeedDriver[1];
        driversList[0] = driver;
        runQualification(driversList, true);
    }

    public void showBestRace(){
        if(DriversUtils.getStoredGenome() == null ){
            System.err.println("No best-genome known");
            return;
        }

        DefaultDriver[] driversList = new DefaultDriver[1];

        for(int i=0; i<10; i++){
            DefaultDriverGenome best = (DefaultDriverGenome) DriversUtils.getStoredGenome();
            DefaultDriver driver = new DefaultDriver();
            driver.loadGenome(best);
            driversList[i] = driver;
        }

        runRace(driversList, true, true);
    }

    public void raceBest(){

        if(DriversUtils.getStoredGenome() == null ){
            System.err.println("No best-genome known");
            return;
        }

        Driver[] driversList = new Driver[10];
        for(int i=0; i<10; i++){
            DefaultDriverGenome best = (DefaultDriverGenome) DriversUtils.getStoredGenome();
            Neat4SpeedDriver driver = new Neat4SpeedDriver(null); //TODO: Creat NeuralNetwork to pass to driver
            driver.loadGenome(best);
            driversList[i] = driver;
        }
        driversList[0] = new Human();
        runRace(driversList, true, true);
    }
}
