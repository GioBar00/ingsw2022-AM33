package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.GamePreset;
import it.polimi.ingsw.enums.Tower;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.SchoolBoard;

import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;

class GameModelTeams extends GameModel {
    GameModelTeams() {
        super(GamePreset.FOUR);
    }

    /**
     * the method adds players to the playersManager and forms teams based on the order of arrival of the clients:
     * - the first and third player will be part of TEAM WHITE
     * - the second and fourth player will be part of TEAM BLACK
     * in each team the first player to be added will be the leader (whose SchoolBoard will have 8 towers),
     * the latter will have no towers but refer to the color of the Tower of the team;
     * @param nickname unique identifier of a player
     * @throws NoPermissionException if game not in UNINITIALIZED state or no more available slots for players
     * @throws NameAlreadyBoundException if another player with same nickname is already in the game
     */
    @Override
    public void addPlayer(String nickname) throws NoPermissionException, NameAlreadyBoundException {
        int towerNumber;

        if(playersManager.getPlayers().size() < 2){
            towerNumber = preset.getTowersNumber();
        } else towerNumber = 0;

        // TEAM WHITE > before being added to the game, the size of already existing players is an even number
        if(playersManager.getPlayers().size() % 2 == 0){
           playersManager.addPlayer(nickname, Tower.WHITE, towerNumber, preset.getEntranceCapacity());
        } else { // TEAM BLACK > before being added to the game, the size of already exsisting players is an odd number
            playersManager.addPlayer(nickname, Tower.BLACK, towerNumber, preset.getEntranceCapacity());
        }
    }

    /**
     * the method is called after calculating the influence, whenever there is a change in the team that
     * holds the most influence on the IslandGroup; the method search for the right Team Leader that will
     * have to swap their team's towers with the one already on the island
     * @param islandGroupIndex index of the IslandGroup considered
     * @param newTower to be put on the IslandGroup
     */
    @Override
    void swapTowers(int islandGroupIndex, Tower newTower) {
        Player newTeamLeader;
        int teamLeaderIndex = 0;
        SchoolBoard oldSchoolBoard = null;
        SchoolBoard newSchoolBoard;
        Tower oldTower = islandsManager.getTower(islandGroupIndex);
        int size = islandsManager.getIslandGroup(islandGroupIndex).size();

        /*
        if (newTower.equals(Tower.WHITE)) {
            teamLeaderIndex = 0;
        }*/
        if (newTower.equals(Tower.BLACK)) {
                teamLeaderIndex = 1;
        }

        newTeamLeader = playersManager.getPlayers().get(teamLeaderIndex);
        newSchoolBoard = playersManager.getSchoolBoard(newTeamLeader);

        if (oldTower != null) {
            if (!newTower.equals(oldTower)) {
                if (teamLeaderIndex == 0) {
                    oldSchoolBoard = playersManager.getSchoolBoard(playersManager.getPlayers().get(1));
                } else {
                    oldSchoolBoard = playersManager.getSchoolBoard(playersManager.getPlayers().get(0));
                }
            }

        }

        islandsManager.setTower(newTower, islandGroupIndex);

        if (oldSchoolBoard != null) {
            try {
                oldSchoolBoard.addTowers(size);
            } catch (LimitExceededException ignored) {
            }

            try {
                newSchoolBoard.removeTowers(size);
            } catch (LimitExceededException e) {
                roundManager.setWinner(newTeamLeader);
            }
        }
    }
}
