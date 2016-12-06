import race.TorcsConfiguration;

import java.io.*;
import java.util.List;

public class NeatAlgorithm {

    /**********************
     * NEAT's main method *     //This is the one to be run when training the optimal RNN. Otherwise comment
     **********************/

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        int     _numberOfInputs  = 12;
        int     _numberOfOutputs = 3;
        int     _populationSize  = 5;
        Neat    neat;
        boolean _loadGenFromFile = false ;

        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));

        //Set path to memory
        String _pathnameSingleDriver = "memory/mydriver1.mem";
        String _pathnameSingleDriverAndOpponents = "src/memory/Single_Driver_and_Opponents/lastGeneration.mem";
        String _pathnameTeamsAndOpponents = "src/memory/Teams_and_Opponents/lastGeneration.mem";

        File savedGeneration = new File(_pathnameSingleDriver);
        if (savedGeneration.exists() && !savedGeneration.isDirectory() && _loadGenFromFile) {   //If there is a population in memory load it
            try {
                FileInputStream fis   = new FileInputStream(savedGeneration);
                ObjectInputStream ois = new ObjectInputStream(fis);
                neat = (Neat) ois.readObject();
                System.out.println("Loaded population from file.");
                ois.close();
                fis.close();
                

            } catch (FileNotFoundException e) {
                System.out.println("Error opening generation file: File does not exist.");
                return;
            } catch (IOException e) {
                System.out.println("Error opening generation file: IOException.");
                return;
            } catch (ClassNotFoundException e) {
                System.out.println("Error reading from generation file: Downcast class does not exist.");
                return;
            }
        }
        else
            neat = new Neat(_numberOfInputs, _numberOfOutputs, _populationSize);
            //neat = new Neat(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));



        while (true) {
            //TODO: CHECKING ZONE 0
            neat.epoch();
            //neat.saveRelevantData();
        }
    }
}
