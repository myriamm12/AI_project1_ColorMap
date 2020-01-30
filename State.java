

class State {
    //static int numberOfNodes = 11;
    int[] nodes;

    State(int[] nodes) {
        this.nodes = nodes;
    }

    State() {
        nodes = new int[Solver.numberOfNodes];
    }
}
