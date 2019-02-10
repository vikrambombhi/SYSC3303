package assignment_2;

import assignment_2.Assemble.Ingredient;

// Chef is given a ingredient but must wait for the other two ingredients it needs to complete a sandwich
// when the chef has all three ingredients avavilable it will make and eat a sandwich(clearing the assembly area)
public class Chef implements Runnable {
    private Assemble assembler;
    private Ingredient ingredient;

    Chef(Assemble assemble, Ingredient ingredient) {
        this.assembler = assemble;
        this.ingredient = ingredient;
    }

    @Override
    public void run() {
        synchronized (assembler) {
            // Continue building and eatting sandwichs till shutdown signal is sent
            while(true) {
                try {
                    buildAndEat();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private boolean checkIngredients() {
        switch (ingredient) {
            case BREAD:
                return (assembler.contains(Ingredient.PEANUT_BUTTER) && assembler.contains(Ingredient.JAM));
            case PEANUT_BUTTER:
                return (assembler.contains(Ingredient.BREAD) && assembler.contains(Ingredient.JAM));
            case JAM:
                return (assembler.contains(Ingredient.BREAD) && assembler.contains(Ingredient.PEANUT_BUTTER));
        }
        return false;
    }

    private void buildAndEat() throws InterruptedException {
        // Check if chef has enough ingredients to make a sandwich
        while (!checkIngredients()) {
            try {
                // Wait for new ingredients
                System.out.println(ingredient + " Chef: Im waiting for supplies");
                assembler.wait();
            } catch (InterruptedException e) {
                System.out.println(ingredient + " Chef: Exiting");
                throw(e);
            }
        }

        // The chef has enough ingredients to make a sandwich
        System.out.println(ingredient + " Chef: Built and ate sandwich\n");
        // Assemble and eat the sandwich, clearing the assemble area
        assembler.clean();
        // Let all threads know the sandwich was made and eatten
        assembler.notifyAll();
    }

}
