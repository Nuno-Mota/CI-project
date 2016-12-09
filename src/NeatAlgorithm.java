import race.TorcsConfiguration;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class NeatAlgorithm {

    static int     _numberOfInputs;
    static int     _numberOfOutputs;
    static int     _populationSize;
    static boolean _loadGenFromFile;
    /**********************
     * NEAT's main method *     //This is the one to be run when training the optimal RNN. Otherwise comment
     **********************/

    private static void loadNaProperties(){
        try (InputStream in = new FileInputStream("neat.properties")) {
            Properties prop = new Properties();
            prop.load(in);
            in.close();
            _numberOfInputs     = Integer.parseInt(prop.getProperty("numberOfInputs"));
            _numberOfOutputs    = Integer.parseInt(prop.getProperty("numberOfOutputs"));
            _populationSize     = Integer.parseInt(prop.getProperty("populationSize"));
            _loadGenFromFile    = Boolean.parseBoolean(prop.getProperty("loadGenomeFromFile"));
        } catch (IOException e) {
            System.err.println("Something went wrong while reading in the properties.");
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        loadNaProperties();
        Neat neat;

        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));

        //Set path to memory
        String _pathnameSingleDriver = "src/memory/Single_Driver/Full_Generations/GenBoop.java_serial";
        String _pathnameSingleDriverAndOpponents = "src/memory/Single_Driver_and_Opponents/lastGeneration.mem";
        String _pathnameTeamsAndOpponents = "src/memory/Teams_and_Opponents/lastGeneration.mem";

        File savedGeneration = new File(_pathnameSingleDriver);
        if (savedGeneration.exists() && !savedGeneration.isDirectory() && _loadGenFromFile) {   //If there is a population in memory load it
            try {
                FileInputStream fis   = new FileInputStream(savedGeneration);
                ObjectInputStream ois = new ObjectInputStream(fis);
                neat = (Neat)ois.readObject();
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

            neat.propagateInnovationsTable();
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
