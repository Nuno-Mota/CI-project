import race.TorcsConfiguration;

import java.io.*;
import java.util.List;

public class NeatAlgorithm {

    /**********************
     * NEAT's main method *     //This is the one to be run when training the optimal RNN. Otherwise comment
     **********************/

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        int _numberOfInputs  = 12;
        int _numberOfOutputs = 3;
        int _populationSize  = 50;
        Neat neat;

        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));

        File savedGeneration = new File("src/memory/lastGeneration.mem");

        if (savedGeneration.exists() && !savedGeneration.isDirectory()) {   //If there is a population in memory load it
            try {
                FileInputStream fis   = new FileInputStream(savedGeneration);
                ObjectInputStream ois = new ObjectInputStream(fis);

                int numberOfInputs   = (int)ois.readObject();
                int numberOfOutputs  = (int)ois.readObject();
                int populationSize   = (int)ois.readObject();
                int generationNumber = (int)ois.readObject();
                List<NeatGenome> currentPopulation = (List<NeatGenome>)ois.readObject();
                //TODO: Needs checking!!
                neat = new Neat(numberOfInputs, numberOfOutputs, populationSize, generationNumber, currentPopulation);


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
