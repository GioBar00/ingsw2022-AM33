package it.polimi.ingsw.server.model.islands;

import it.polimi.ingsw.network.messages.views.IslandGroupView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * A class that contains all the information about the islands and the islands themselves
 */
public class IslandsManager {

    /**
     * array of all islandGroups that are part of the game (at the very beginning they are 12)
     */
    private final ArrayList<IslandGroup> islandGroups;

    /**
     * Public constructor of the class. By default, the number of islands contained are 12
     */
    public IslandsManager() {
        islandGroups = new ArrayList<>(12);
        // initialize islandGroups
        for (int i = 0; i < 12; i++) {
            islandGroups.add(new IslandGroup());
        }
    }

    /**
     * method to access an IslandGroup with a specific index
     *
     * @param index of the IslandGroup
     * @return IslandGroup
     */
    public IslandGroup getIslandGroup(int index) {
        return islandGroups.get(index);
    }

    /**
     * method returns the size of the ArrayList islandsGroups
     *
     * @return size
     */
    public int getNumIslandGroups() {
        return islandGroups.size();
    }

    /**
     * method to access the Tower of a specific IslandGroup
     *
     * @param index of the IslandGroup
     * @return tower of the IslandGroup
     */
    public Tower getTower(int index) {
        return islandGroups.get(index).getTower();
    }

    /**
     * method to set the Tower of a specific IslandGroup
     *
     * @param tower: the new tower that will be set on the IslandGroup
     * @param index  of the IslandGroup
     */
    public void setTower(Tower tower, int index) {
        islandGroups.get(index).setTower(tower);
    }

    /**
     * method to add a student on a specific Island inside a specific IslandGroup if the indexes are valid.
     *
     * @param s:           type of student to be added
     * @param index_group: index of the IslandGroup
     * @return if the student was added successfully.
     */
    public boolean addStudent(StudentColor s, int index_group) {
        if (index_group < 0 || index_group >= islandGroups.size())
            return false;

        islandGroups.get(index_group).addStudent(s);
        return true;
    }

    /**
     * method to calculate the influence that a Player has on a specific IslandGroup, considering towers
     *
     * @param playerTower: type of Tower of the Player
     * @param professors:  EnumSet of the professors currently controlled by the Player
     * @param index        of the IslandGroup
     * @return the influence
     */
    public int calcInfluence(Tower playerTower, EnumSet<StudentColor> professors, int index) {
        return islandGroups.get(index).calcInfluence(playerTower, professors);
    }

    /**
     * method to calculate the influence that a Player has on a specific IslandGroup
     *
     * @param professors: EnumSet of the professors currently controlled by the Player
     * @param index       of the IslandGroup
     * @return the influence
     */
    public int calcInfluence(EnumSet<StudentColor> professors, int index) {
        return islandGroups.get(index).calcInfluence(professors);
    }

    /**
     * the method check is the IslandGroup(index - 1) can be merged with IslandGroup(index);
     * if the merge can be done, the method calls for it
     *
     * @param index of the current IslandGroup
     * @return true if the merge happens, false otherwise
     */
    public boolean checkMergePrevious(int index) {
        int previous;

        if (index == 0) {
            previous = islandGroups.size() - 1;
        } else {
            previous = index - 1;
        }

        return mergeIslandGroups(previous, index);
    }

    /**
     * the method check is the IslandGroup(index + 1) can be merged with IslandGroup(index);
     * if the merge can be done, the method calls for it
     *
     * @param index of the current IslandGroup
     * @return true if the merge happens, false otherwise
     */
    public boolean checkMergeNext(int index) {
        int next;

        if (index == islandGroups.size() - 1) {
            next = 0;
        } else {
            next = index + 1;
        }

        return mergeIslandGroups(index, next);
    }

    /**
     * the method merges two islandGroups together
     *
     * @param index1 of the first islandGroup
     * @param index2 of the second islandGroup
     * @return true if the merge was successful
     */
    private boolean mergeIslandGroups(int index1, int index2) {
        if (islandGroups.get(index2).getTower() != null) {
            if (islandGroups.get(index2).getTower().equals(islandGroups.get(index1).getTower())) {
                islandGroups.get(index1).mergeWith(islandGroups.get(index2));
                islandGroups.remove(index2);
                return true;
            }
        }
        return false;
    }

    /**
     * @return the islandsView
     */
    public ArrayList<IslandGroupView> getIslandsView() {
        ArrayList<IslandGroupView> islandsView = new ArrayList<>();
        for (IslandGroup ig : islandGroups) {
            islandsView.add(ig.getIslandGroupView());
        }
        return islandsView;
    }
}
