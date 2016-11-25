import java.lang.Math;

public class NodeGene{
    // 0 = input, 1 = hidden, 2 = output
    int type;
    double activation;
    double bias;
    NodeGene[] incoming, outgoing;
    int incomingCount, outgoingCount;

    public NodeGene(int type, NodeGene incoming, NodeGene outgoing){
        this.incoming = new NodeGene[1000];
        this.incoming[0] = incoming;
        this.outgoing = new NodeGene[1000];
        this.outgoing[0] = outgoing;
        this.incomingCount = 1;
        this.outgoingCount = 1;
        this.activation = 0;
        this.bias = 1;

    }

    // activation of this node
    public double activation() {

        return 0.0;
    }

    public double getOutput(){
        return (1.0/ (1.0+Math.exp(-4.9 * this.activation)));
    }
}
