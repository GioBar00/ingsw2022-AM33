package it.polimi.ingsw.model.islands;

import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.enums.Tower;

import java.util.ArrayList;
import java.util.EnumSet;

public class IslandsManager {
    private ArrayList<IslandGroup> islandGroups;

    public IslandsManager() {
        islandGroups = new ArrayList<>(12);
        // initialize islandGroups
        for(int i = 0; i < 12; i++) {
            islandGroups.add(new IslandGroup());
        }
    }

    /**
     * method to access an IslandGroup with a specific index
     * @param index of the IslandGroup
     * @return IslandGroup
     */
    public IslandGroup getIslandGroup(int index){
        return islandGroups.get(index);
    }

    /**
     * method returns the size of the ArrayList islandsGroups
     * @return size
     */
    public int size(){
        return islandGroups.size();
    }

    /**
     * method to access the Tower of a specific IslandGroup
     * @param index of the IslandGroup
     * @return tower of the IslandGroup
     */
    public Tower getTower(int index){
        return islandGroups.get(index).getTower();
    }

    /**
     * method to set the Tower of a specific IslandGroup
     * @param tower: the new tower that will be set on the IslandGroup
     * @param index of the IslandGroup
     */
    public void setTower(Tower tower, int index){
        islandGroups.get(index).setTower(tower);
    }

    /**
     * method to add a student on a specific Island inside a specific IslandGroup
     * @param index_island: index of the Island
     * @param s: type of student to be added
     * @param index_group: index of the IslandGroup
     */
    public void addStudent(int index_island, StudentColor s, int index_group){
        islandGroups.get(index_group).addStudent(index_island,s);
    }

    /**
     * method to calculate the influence that a Player has on a specific IslandGroup, considering towers
     * @param playerTower: type of Tower of the Player
     * @param professors: EnumSet of the professors currently controlled by the Player
     * @param index of the IslandGroup
     * @return the influence
     */
    public int calcInfluence(Tower playerTower, EnumSet<StudentColor> professors, int index){
        return islandGroups.get(index).calcInfluence(playerTower, professors);
    }

    /**
     * method to calculate the influence that a Player has on a specific IslandGroup
     * @param professors: EnumSet of the professors currently controlled by the Player
     * @param index of the IslandGroup
     * @return the influence
     */
    public int calcInfluence(EnumSet<StudentColor> professors, int index){
        return islandGroups.get(index).calcInfluence(professors);
    }

    /**
     * method to check if a IslandGroup needs to be merged with the ones on its left or right; if so, the method proceeds
     * to call for the merge and subsequently removes the IslandGroup merged with the one just considered
     * @param index of the IslandGroup
     */
    public void checkMerge(int index) {
        int left, right;

        if(index == 0){
            left = 12;
        } else {
            left = index - 1;
        }

        if(index == 12){
            right = 0;
        } else {
            right = index + 1;
        }

        if(islandGroups.get(left).getTower().equals(islandGroups.get(index).getTower())){
            islandGroups.get(index).mergeWith(islandGroups.get(left));
            islandGroups.remove(left);
        }

        if(islandGroups.get(right).getTower().equals(islandGroups.get(index).getTower())){
            islandGroups.get(index).mergeWith(islandGroups.get(right));
            islandGroups.remove(right);
        }
    }
}
