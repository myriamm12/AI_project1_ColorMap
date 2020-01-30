


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

class Solver {
    private final static int mode0 = 1;
    private final static int mode1 = 2;
    private final static int mode2 = 3;
    private final static int mode3 = 4;
    private static int num;
    private static int numberOfGenerations = 100;
    private static int populationSize = 1000;
    private static int tornumentSize = 5;
    private static double mutationRate = 0.02;
    static int numberOfNodes = 11;


    ////////////////////////////////////////simulated annealing algorithm//////////////////////////////////////
    private SimAnnealingResult simulatedAnnealing(Problem problem) {
        int counter = 0;

        SimAnnealingResult simResult = new SimAnnealingResult();
        simResult.time = 0;
        State current = problem.randomInitialState();
        simResult.viewedNodes++;
        boolean expanded = false;  //control expanded nodes

        //loop until system has been cooled enough
        double temp = 10;///////////////////////////////////////initial temp
        while (true) {
            simResult.time++;
            counter++;
            State neighbour = null;
            if (!expanded) {
                simResult.expandedNodes++;
            }
            expanded = false;
                    neighbour = problem.getRandomChild(current, false);
                    if (temp <= 0) {
                        System.out.println("temperature reached 0");
                        //return ;
                        neighbour = null;

                        break;
                    }

            if (problem.getValue(neighbour) < problem.getValue(current)) { //neighbour is worse
                        if (!goingOrNot(counter,problem.getValue(neighbour),problem.getValue(current),temp,num)) {
                            neighbour = current;
                            expanded = true;
                        }
                    }
                    simResult.viewedNodes++;

            if (neighbour == null) {
                simResult.bestState = current;
                simResult.value = problem.getValue(current);
                return simResult;
            }
            current = neighbour;

            //cooling temp
            double coolTemp = funcT(num,counter);
           temp -= coolTemp;
          //  System.out.println("counter" + counter);
            System.out.println("cooltemp  " + coolTemp);

        }
        return simResult;////
    }

    private boolean goingOrNot(int counter, int neighbourEnergy, int currentEnergy, double temp, int num) {

        double probability;
        if(temp == 10){
            return false;
        }
        else if (counter == 0)
            return false;
        else {
            temp = funcT(num,counter);
            int deltaE = neighbourEnergy - currentEnergy ;
            probability = Math.exp(deltaE/temp);
            int aRandomNumber = new Random().nextInt(100) + 1;
            return aRandomNumber <= probability;
        }
    }

    private double funcT(int mode , int counter) {
        double result = 0;
        switch (mode) {
            case mode0:
                result = funcT1(counter);
                break;

            case mode1:
                result = funcT2(counter);
                break;

            case mode2:
                result = funcT3(counter);
                break;

            case mode3:
                result = funcT4(counter);
                break;

        }
        return result;
    }


    private double funcT1(int k){//T0 = 1  ALPHA = 0.9
        float alpha = (float) 0.9;
        return Math.pow(alpha,k);

    }
    private double funcT2(int k) { //TO = 1 ALPHA = 20
        double numinator = 1 + Math.log10(1+k);
        return 20/numinator;
    }

    private double funcT3(int k) {//T0 = 1 ALPHA = 1
        double numinator1 = 1+k;
        return 1/numinator1;
    }

    private double funcT4(int k) {//T0 = 1 ALPHA = 0.05
        double numinator2= 1 + 0.05*Math.pow(k,2);
        return 1/numinator2;
    }





 ////////////////////////////////////////genetic algorithm//////////////////////////////////////

    private ArrayList<GeneticsResult> genetics(Problem problem, int populationSize, int tornumentSize, double mutationRate) {
        int generationNumber = 0;
        State[] chromosomes = problem.generateNStates(populationSize);
        ArrayList<GeneticsResult> geneticsResults = new ArrayList<GeneticsResult>();
        int counterOfGeneration = 0;
        while (geneticsResults.isEmpty() || (geneticsResults.get(geneticsResults.size() - 1).averageValue > 1.05 && counterOfGeneration < numberOfGenerations)) {
                ++counterOfGeneration;
            int[] values = new int[chromosomes.length];
            for (int i = 0; i < chromosomes.length; i++) {
                values[i] = problem.getValue(chromosomes[i]);  //getting fitness on chromosomes of the population
            }
            int parentsNumber = (int) Math.ceil(populationSize * 1.0 / tornumentSize); //getting number of best parent 100/4
            State[] parents = new State[parentsNumber];
            int x = 0;
            int parentIndex = 0;

            //choose best parents
            while (x < values.length) { //apply tornomentq on population /repeat while loop for polpulationSize times
                int y = (x + tornumentSize - 1 < values.length - 1) ? x + tornumentSize - 1 : values.length - 1;
                int bestNodeIndex = findBestNode(values, x, y);  //find index of best chromos
                parents[parentIndex++] = chromosomes[bestNodeIndex]; //filling parents array
                x += tornumentSize; //loop be andaze 25 bar tekrar mishe
            }

            GeneticsResult geneticsResult = new GeneticsResult();
            geneticsResult.bestValue = findBestChromosome(problem, chromosomes);
            geneticsResult.worstValue = findWorstChromosome(problem, chromosomes);
            geneticsResult.averageValue = populationAverageValue(problem, chromosomes);
            geneticsResult.generationNumber = generationNumber;
            geneticsResults.add(geneticsResult);
            //System.out.println(geneticsResult.bestValue);
            //new Generation
            State[] newGeneration = new State[populationSize];
            Random random = new Random();
            int count = 0;
            while (count < populationSize) {
                State parent1 = parents[random.nextInt(parents.length)];
                State parent2 = parents[random.nextInt(parents.length)];
                if (parent1 != parent2) {
                    newGeneration[count++] = crossOver(parent1, parent2);
                }
            }
            int mutatedGenomes = (int) (numberOfNodes * mutationRate * populationSize);    ///11 is number of nodes of genome
            mutation(newGeneration, mutatedGenomes );
            generationNumber++;

            chromosomes = newGeneration;
        }
        return geneticsResults;
    }

    private void mutation(State[] states, int mutationNumber) {
        int index = numberOfNodes * states.length;
        int counter = 0;
        Random random = new Random();

        HashSet<Integer> numbers = new HashSet<Integer>(mutationNumber);


        while (counter < mutationNumber) {
            int temp = random.nextInt(index);

            if (!numbers.contains(temp)) {
                numbers.add(temp);
                int chromosomeIndex = temp / numberOfNodes;  //which chromosome
                int whichNode = temp % numberOfNodes;
                states[chromosomeIndex].nodes[whichNode] = (states[chromosomeIndex].nodes[whichNode] % Problem.numberOfColor) + 1; //which genome of chromosome
                counter++;
            }
        }
    }

    private State crossOver(State s1, State s2) {
        int[] newArray = new int[s1.nodes.length];
        for (int i = 0; i < numberOfNodes; i++) {
            newArray[i] = (i < numberOfNodes/2) ? s1.nodes[i] : s2.nodes[i];
        }
        return new State(newArray);
    }


    private double populationAverageValue(Problem problem, State[] states) {
        int sum = 0;
        for (State state : states) sum += problem.getValue(state);
        return sum * 1.0 / states.length;
    }

    private int findBestChromosome(Problem problem, State[] states) {
        int minValue = problem.getValue(states[0]);
        for (int i = 1; i < states.length; i++) {
            if (minValue < problem.getValue(states[i])) {
                minValue = problem.getValue(states[i]);
            }
        }
        return minValue;
    }

    private int findWorstChromosome(Problem problem, State[] states) {
        int maxValue = problem.getValue(states[0]);
        for (int i = 1; i < states.length; i++) {
            if (maxValue > problem.getValue(states[i])) {
                maxValue = problem.getValue(states[i]);
            }
        }
        return maxValue;
    }

    private int findBestNode(int[] values, int from, int to) {
        int minIndex = from;
        int minValue = values[from];

        for (int i = from + 1; i <= to; i++) {
            if (values[i] < minValue) {
                minIndex = i;
                minValue = values[i];
            }
        }
        return minIndex;
    }

    public static void main(String[] args) {
        Solver solver = new Solver();
        String algorithm;
        System.out.println("Select your algorithm(genetic|simulated)");
        Scanner in = new Scanner(System.in);
        algorithm = in.next();

        if(algorithm.equals("genetic")) {
            ArrayList<GeneticsResult> geneticsResults = solver.genetics(new Problem(), populationSize, tornumentSize, mutationRate);
            System.out.println("number of generations: " + geneticsResults.size());
            System.out.println("number of populationSize: " + populationSize);
            System.out.println("number of tornumentSize: " + tornumentSize);
            System.out.println("number of mutationSize: " + mutationRate);


            for(int i = 0; i < geneticsResults.size(); i++){
                System.out.println("generation number " + geneticsResults.get(i).generationNumber);
                System.out.println("worst: " + geneticsResults.get(i).worstValue);
                System.out.println("average: " + geneticsResults.get(i).averageValue);
                System.out.println("best: " + geneticsResults.get(i).bestValue);
                System.out.println("*****************************");
            }
        }

        if(algorithm.equals("simulated")){
            System.out.println("Enter your Annealing schedule,please(1|2|3|4) :");
            Scanner getin = new Scanner(System.in);
            num = getin.nextInt();
            SimAnnealingResult simAnnealingResult = new Solver().simulatedAnnealing(new Problem());
            switch (num) {
                case mode0:
                    System.out.println("Selected cooling function T(k) = t0*a^k");
                    break;

                case mode1:
                    System.out.println("Selected cooling function T(k) = t0/(1 + a*Log(1 + k))");
                    break;

                case mode2:
                    System.out.println("Selected cooling function T(k) = t0/(1 + a*k)");
                    break;

                case mode3:
                    System.out.println("Selected cooling function T(k) = t0/(1 + a*k^2)");
                    break;

            }
            System.out.println("*****************************");
            System.out.println("number of viewed nodes: " + simAnnealingResult.viewedNodes);

            System.out.println("number of expanded nodes: " + simAnnealingResult.expandedNodes);
          //  System.out.println("value: " + simAnnealingResult.value);
           // System.out.println("best state: " + simAnnealingResult.bestState);
           // System.out.println("time: " + simAnnealingResult.time);
        }

    }

}
