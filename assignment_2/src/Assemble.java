package assignment_2;

import java.util.ArrayList;
import java.util.HashSet;

// Asseble holds the assembly area for chefs and agents to work on.
// Agents place ingredients in the assembly area and chefs assemble/eat sandwichs
// Only 3 ingredients can fit in the assembly area at once
public class Assemble {
    // Use enum to standardize ingredients between chefs and agents
    public enum Ingredient { BREAD, PEANUT_BUTTER, JAM };
    private HashSet<Ingredient> assemblyArea;

    Assemble() {
        assemblyArea = new HashSet<Ingredient>();
    }

    public void clean() {
        assemblyArea.clear();
    }

    public boolean isEmpty() {
        return assemblyArea.size() == 0;
    }

    public void place(Ingredient i) {
        assemblyArea.add(i);
    }

    public boolean contains(Ingredient i) {
        return assemblyArea.contains(i);
    }

    public void run() {
        // Create threads for the chefs and assign them all a ingredient
        ArrayList<Thread> chefs = new ArrayList<Thread>();
        chefs.add(new Thread(new Chef(this, Ingredient.BREAD)));
        chefs.add(new Thread(new Chef(this, Ingredient.PEANUT_BUTTER)));
        chefs.add(new Thread(new Chef(this, Ingredient.JAM)));
        // Start chefs
        for (Thread chef: chefs) {
            chef.start();
            System.out.println(chef);
        }

        // Create a thread of the agent
        Thread agent = new Thread(new Agent(this, chefs));
        // Start the agent
        agent.start();
    }

    public static void main(String[] args) {
        Assemble assemble = new Assemble();
        assemble.run();
    }
}
