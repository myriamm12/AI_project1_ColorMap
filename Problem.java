

import java.util.ArrayList;
import java.util.Random;

class Problem {
    static int numberOfColor = 3;

    private class Edge {
        int node1;
        int node2;

        public Edge(int node1, int node2) {
            this.node1 = node1;
            this.node2 = node2;
        }
    }

    ArrayList<Edge> edges;

    public Problem() {
        edges = new ArrayList<Edge>(22);
        edges.add(new Edge(1, 3));
        edges.add(new Edge(1, 4));
        edges.add(new Edge(1, 9));
        edges.add(new Edge(1, 11));
        edges.add(new Edge(2, 11));
        edges.add(new Edge(2, 3));
        edges.add(new Edge(2, 6));
        edges.add(new Edge(2, 5));
        edges.add(new Edge(3, 7));
        edges.add(new Edge(4, 7));
        edges.add(new Edge(4, 5));
        edges.add(new Edge(5, 9));
        edges.add(new Edge(5, 8));
        edges.add(new Edge(6, 7));
        edges.add(new Edge(6, 9));
        edges.add(new Edge(7, 8));
        edges.add(new Edge(7, 10));
        edges.add(new Edge(8, 11));
        edges.add(new Edge(9, 10));
        edges.add(new Edge(10, 11));
    }

    int getValue(State state) {
        int counter = 0;
        for (Edge anEdge : edges) {
            counter = (state.nodes[anEdge.node1 - 1] == state.nodes[anEdge.node2 - 1]) ? counter + 1 : counter;
        }
        return counter/Solver.numberOfNodes;
    }



    private ArrayList<State> getChildren(State s) {
        ArrayList<State> children = new ArrayList<State>(22);
        for (int i = 0; i < 11; i++) {
            int[] newState1 = s.nodes.clone();
            int[] newState2 = s.nodes.clone();
            newState1[i] = (newState1[i] % numberOfColor) + 1;
            newState2[i] = (newState2[i] + 1) % numberOfColor + 1;
            State newState1s = new State(newState1);
            State newState2s = new State(newState2);
            children.add(newState1s);
            children.add(newState2s);


        }
        return children;
    }

//returns a random chromosome
    State getRandomChild(State s, boolean goodChild) {
        int currentValue = getValue(s);
        ArrayList<State> allChildren = getChildren(s);
        if (!goodChild)
            return allChildren.get(new Random().nextInt(allChildren.size()));
        ArrayList<State> goodChildren = new ArrayList<State>();
        for (State state : allChildren) {
            if (getValue(state) < currentValue)
                goodChildren.add(state);
        }
        return (goodChildren.isEmpty()) ? null : goodChildren.get(new Random().nextInt(goodChildren.size()));
    }


//coloring a chromosome with random colors
    State randomInitialState() {
        State state = new State();
        Random r = new Random();
        for (int i = 0; i < state.nodes.length; i++) {
            state.nodes[i] = r.nextInt(numberOfColor) + 1;
        }
        return state;
    }
//generating a random color
    State[] generateNStates(int n) {
        State[] states = new State[n];
        for (int i = 0; i < n; i++) {
            states[i] = randomInitialState();
        }
        return states;
    }

}
