package it.polimi.ingsw.model.islands;

import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.enums.Tower;

import java.util.ArrayList;
import java.util.EnumSet;

public class IslandGroup{
    private final ArrayList<Island> islands = new ArrayList<>();
    private boolean isBlocked;

    public IslandGroup() {
        islands.add(new Island());
        isBlocked = false;
    }

    /**
     * method to access the islands of the IslandGroup
     * @return the ArrayList of islands of the IslandGroup
     */
    public ArrayList<Island> getIslands() {
        return islands;
    }

    /**
     * method to access the current size of the IslandGroup
     * @return the size
     */
    public int size(){
        return islands.size();
    }

    /**
     * method to access the tower of the IslandGroup; Tower can be null (if island never conquered)
     * @return current Tower
     */
    public Tower getTower(){
        return islands.get(0).getTower();
    }

    /**
     * method to set the tower of the IslandGroup
     * @param tower: new tower to be set on the island
     */
    public void setTower(Tower tower){
        for (Island i : islands) i.setTower(tower);
    }

    /**
     * get if island group is blocked.
     * @return if is blocked.
     */
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     * set island group blocked.
     * @param blocked is blocked.
     */
    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    /**
     * method to add a student on a specific island of the IslandGroup
     * @param index of the island
     * @param s type of student
     */
    public void addStudent(int index, StudentColor s){
        islands.get(index).addStudent(s);
    }

    /**
     * calculates the influence that a specific player has on the island group, inclunding towers
     * @param playerTower tower of the Player
     * @param professors list of professors currently controlled by the player
     * @return influence of the Player on the IslandGroup
     */
    public int calcInfluence(Tower playerTower, EnumSet<StudentColor> professors){
        int infl;

        infl = calcInfluence(professors);

        // adds addictional influence if the player controls the towers present on the island
        if (getTower() != null && this.getTower().equals(playerTower)) {
            infl = infl + islands.size();
        }

        return infl;
    }

    /**
     * calculates the influence that a specific players has on the island group
     * @param professors list of professors currently controlled by the player
     * @return influence of the Player on the IslandGroup
     */
    public int calcInfluence(EnumSet<StudentColor> professors){
        int infl = 0;

        // calculates the number of students on which the player has influence, considering all the islands of the gruop
        for(Island i: islands) {
            for (StudentColor p : professors) {
                infl = infl + i.getNumStudents(p);
            }
        }

        return infl;
    }

    /**
     * merges the current IslandGroup with another one
     * @param ig1 other IslandGroup to merge with the current one
     */
    public void mergeWith(IslandGroup ig1){
        // add second island group to the first island group
        this.islands.addAll(ig1.islands);
        // the model will then call a function to delete ig1
    }
}
