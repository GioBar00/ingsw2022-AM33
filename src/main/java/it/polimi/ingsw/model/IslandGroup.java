package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.EnumSet;

public class IslandGroup {
    private final ArrayList<Island> islands = new ArrayList<>();
    boolean isBlocked;

    IslandGroup() {
        islands.add(new Island());
        isBlocked = false;
    }

    int size(){
        return islands.size();
    }

    /**
     * Tower can be null (if island never conquered)
     * @return current Tower
     */
    Tower getTower(){
        return islands.get(0).getTower();
    }

    void setTower(Tower tower){
        for (Island i : islands) i.setTower(tower);
    }

    void addStudent(int index, StudentColor s){
        islands.get(index).addStudent(s);
    }

    // calculates the influence that a specific player has on the island group, inclunding towers
    int calcInfluence(Tower playerTower, EnumSet<StudentColor> professors){
        int infl;

        infl = calcInfluence(professors);

        // adds addictional influence if the player controls the towers present on the island
        if (getTower() != null && this.getTower().equals(playerTower)) {
            infl = infl + islands.size();
        }

        return infl;
    }

    // calculates the influence that a specific players has on the island group
    int calcInfluence(EnumSet<StudentColor> professors){
        int infl = 0;

        // calculates the number of students on which the player has influence, considering all the islands of the gruop
        for(Island i: islands) {
            for (StudentColor p : professors) {
                infl = infl + i.getNumStudents(p);
            }
        }

        return infl;
    }

    // merges two island groups into one
    void mergeWith(IslandGroup ig1){
        // add second island group to the first island group
        this.islands.addAll(ig1.islands);
        // the model will then call a function to delete ig1
    }
}
