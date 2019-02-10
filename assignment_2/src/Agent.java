package assignment_2;

import assignment_2.Assemble.Ingredient;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;

// Agent places two random ingredients in the assembly area and waits for a chef to make a sandwich
public class Agent implements Runnable {
    private Assemble assembler;
    private ArrayList<Thread> chefs;
    private int count = 0;

    Agent(Assemble assemble, ArrayList<Thread> chefs) {
        this.assembler = assemble;
        this.chefs = chefs;
    }

    @Override
    public void run() {
        synchronized (assembler) {
            for (; count < 20; count++) {
                supplyFood();
            }
        }
        System.out.println("Agent: Done making 20 sandwichs\n");
        stopChefs();
    }

    private void supplyFood() {
        // Run when the table is empty
        while (!assembler.isEmpty()) {
            try {
                System.out.println("Agent: Waiting for chefs...");
                assembler.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        // Generate two random ingredients
        Ingredient ingredient1 = getIngredient();
        Ingredient ingredient2 = getIngredient();
        // Avoid 2 of the same supplies
        while(ingredient1 == ingredient2) {
            ingredient1 = getIngredient();
        };

        System.out.println("Agent: putting " + ingredient1 + " in assembly area");
        assembler.place(ingredient1);
        System.out.println("Agent: putting " + ingredient2 + " in assembly area");
        assembler.place(ingredient2);

        // Let chefs know there are new ingredients available
        assembler.notifyAll();
    }

    private Ingredient getIngredient() {
        int rand = ThreadLocalRandom.current().nextInt(0, 3);
        switch(rand) {
            case 0:
                return Ingredient.BREAD;
            case 1:
                return Ingredient.PEANUT_BUTTER;
            default:
                return Ingredient.JAM;
        }
    }

    private void stopChefs() {
        for (Thread chef: chefs) {
            chef.interrupt();
        }
    }
}
