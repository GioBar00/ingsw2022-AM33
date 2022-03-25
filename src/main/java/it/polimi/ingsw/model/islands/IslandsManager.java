package it.polimi.ingsw.model.islands;

import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.enums.Tower;

import java.util.ArrayList;
import java.util.EnumSet;

public class IslandsManager {
    private final ArrayList<IslandGroup> islandGroups;

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
    public int getNumIslandGroups(){
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
     * @param s: type of student to be added
     * @param index_group: index of the IslandGroup
     * @param index_island: index of the Island
     */
    public void addStudent(StudentColor s, int index_group, int index_island){
        islandGroups.get(index_group).addStudent(index_island, s);
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
     * the method check is the IslandGroup(index - 1) can be merged with IslandGroup(index);
     * if the merge can be done, the method calls for it
     * @param index of the current IslandGroup
     * @return true if the merge happens, false otherwise
     */
    public boolean checkMergePrevious(int index){
        int previous;

        if(index == 0){
            previous = islandGroups.size() - 1;
        } else {
            previous = index - 1;
        }

        return mergeIslandGroups(index, previous);
    }

    /**
     * the method check is the IslandGroup(index + 1) can be merged with IslandGroup(index);
     * if the merge can be done, the method calls for it
     * @param index of the current IslandGroup
     * @return true if the merge happens, false otherwise
     */
    public boolean checkMergeNext(int index){
        int next;

        if(index == islandGroups.size() - 1){
            next = 0;
        } else {
            next = index + 1;
        }

        return mergeIslandGroups(index, next);
    }

    private boolean mergeIslandGroups(int index, int index2) {
        if(islandGroups.get(index2).getTower() != null){
            if(islandGroups.get(index2).getTower().equals(islandGroups.get(index).getTower())){
                islandGroups.get(index).mergeWith(islandGroups.get(index2));
                islandGroups.remove(index2);
                return true;
            }
        }

        return false;
    }
}
