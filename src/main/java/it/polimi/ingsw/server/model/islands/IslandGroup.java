package it.polimi.ingsw.server.model.islands;

import it.polimi.ingsw.network.messages.views.IslandGroupView;
import it.polimi.ingsw.network.messages.views.IslandView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class IslandGroup{
    /**
     * array of the islands that are part of the group
     */
    private final ArrayList<Island> islands = new ArrayList<>();
    /**
     * boolean value to specify whether the card is blocked (herbalist card effect)
     */
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
    Tower getTower(){
        return islands.get(0).getTower();
    }

    /**
     * method to set the tower of the IslandGroup
     * @param tower: new tower to be set on the island
     */
    void setTower(Tower tower){
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
     * method to add a student to the island inside the islandGroup that has the least amount of students
     * @param s type of student
     */
    public void addStudent(StudentColor s){
        // chooses the island with the least amount of student
        int indexMin = 0;
        for(int i = 1; i < islands.size(); i++){
            if(islands.get(i).getNumStudents() < islands.get(indexMin).getNumStudents()){
                indexMin = i;
            }
        }

        islands.get(indexMin).addStudent(s);
    }

    /**
     * calculates the influence that a specific player has on the island group, inclunding towers
     * @param playerTower tower of the Player
     * @param professors list of professors currently controlled by the player
     * @return influence of the Player on the IslandGroup
     */
    int calcInfluence(Tower playerTower, EnumSet<StudentColor> professors){
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

    /**
     * merges the current IslandGroup with another one
     * @param ig1 other IslandGroup to merge with the current one
     */
    void mergeWith(IslandGroup ig1){
        // add second island group to the first island group
        this.islands.addAll(ig1.islands);
        // the model will then call a function to delete ig1
    }

    /**
     * @return the islandGroup current View
     */
    public IslandGroupView getIslandGroupView(){
        List<IslandView> islandView = new ArrayList<>();
        for (Island i: islands) {
            islandView.add(i.getIslandView());
        }
        return new IslandGroupView(islandView, isBlocked);
    }
}
