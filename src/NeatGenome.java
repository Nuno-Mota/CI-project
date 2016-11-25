/**
 * Created by alex on 24-11-16.
 */
import java.util.Random;

public class NeatGenome {

    final double addConnectionProb = 0.3;
    final double addNodeProb = 0.15;
    final double weightMutationProb = 0.8;
    // If a weight is mutated, it is multiplied by a constant factor
    // with this probability. Otherwise, it is randomly reinitialized.
    final double constMutProb = 0.9;
    // A gene gets disabled with this probability, if it was disabled
    // in both parents.
    final double geneDisabledProb = 0.75;
    final double interspeciesMatingRate = 0.001;
    // Probability that an offspring is produced without crossover.
    final double noCrossoverProb = 0.25;

    int nodeCount;
    int connectionCount;

    Random rand = new Random();

    NodeGene[] nodes;
    ConnectionGene[] connections;

    public NeatGenome(int inputs, int outputs){

    }

    // Perform weight mutation, connection insertion and node insertion
    public void mutate(){
        // Iterate over all connections to mutate weights.
        for (int i = 0; i < nodeCount; i++){
            if (rand.nextDouble() < weightMutationProb){
                if (rand.nextDouble() < constMutProb) {
                    connections[i].multiplyWeight();
                } else{
                    connections[i].randomWeight();
                }
            }
        }

        // Add a new connection. Currently, a node in the input or output
        // layer cannot have a connection with itself.
        if(rand.nextDouble() < addConnectionProb){
            int in, out;
            boolean haveConnection;
            do {
                haveConnection = false;
                in = rand.nextInt(nodeCount);
                out = rand.nextInt(nodeCount);
                for (NodeGene n : nodes[in].incoming){
                    if (n == nodes[out]){
                        haveConnection = true;
                        break;
                    }
                }
            }while( haveConnection ||
                    (nodes[in].type == nodes[out].type &&
                        (nodes[in].type == 0 || nodes[in].type == 2) ));
            //TODO: adjust the constructor call, once the constructor of NodeGene is done.
            connections[connectionCount] = new ConnectionGene(nodes[in], nodes[out], true, true);
        }

        //Add a new node
        if (rand.nextDouble() < addNodeProb){
            ConnectionGene connection;
            // No node can be added in a connection of a node with itself.
            do{
                connection = connections[rand.nextInt(connectionCount)];
            }while(connection.in == connection.out);
            //TODO: make a new node. Connect the in and out nodes of the original connections to the new node (weights: 1 and random). Disable old connection.
        }
    }


}


