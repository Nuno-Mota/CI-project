import java.util.Random;

public class ConnectionGene{
    // St. deviation of the distribution that initializes weights.
    final double std = 4;
    // The constant factor by which we 'mutate' a weight.
    final double weightMutConstant = 1.2;
    static int innovationCount = 0;
    Random rand = new Random();
    NodeGene in;
    NodeGene out;
    double weight;
    boolean enabled;
    int innovationN;

    public ConnectionGene(NodeGene in, NodeGene out, boolean randomWeight, boolean enabled) {
        this.in = in;
        this.out = out;
        this.enabled = enabled;
        this.innovationN = innovationCount;
        innovationCount++;

        if(randomWeight){
            weight = rand.nextGaussian() * std;
        } else{
            weight = 1;
        }
    }

    public void randomWeight(){
        weight = rand.nextGaussian() * std;
    }

    public void multiplyWeight(){
        weight *= weightMutConstant;
    }
}
