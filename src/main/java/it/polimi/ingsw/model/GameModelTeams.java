package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.GamePreset;
import it.polimi.ingsw.enums.GameState;
import it.polimi.ingsw.enums.Tower;
import it.polimi.ingsw.model.player.SchoolBoard;


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
     * @return if the player was added successfully.
     */
    @Override
    public boolean addPlayer(String nickname) {
        if (gameState != GameState.UNINITIALIZED)
            return false;

        int towerNumber;

        if(playersManager.getPlayers().size() < 2) {
            towerNumber = preset.getTowersNumber();
        } else towerNumber = 0;

        // TEAM WHITE > before being added to the game, the size of already existing players is an even number
        if(playersManager.getPlayers().size() % 2 == 0) {
            return playersManager.addPlayer(nickname, Tower.WHITE, towerNumber, preset.getEntranceCapacity());
        }
        // TEAM BLACK > before being added to the game, the size of already existing players is an odd number
        return playersManager.addPlayer(nickname, Tower.BLACK, towerNumber, preset.getEntranceCapacity());

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
        Tower oldTower = islandsManager.getTower(islandGroupIndex);

        if (newTower != oldTower) {
            int teamLeaderIndex;
            if (newTower.equals(Tower.BLACK)) {
                teamLeaderIndex = 1;
            } else teamLeaderIndex = 0;

            SchoolBoard newSchoolBoard = playersManager.getSchoolBoard(playersManager.getPlayers().get(teamLeaderIndex));

            int size = islandsManager.getIslandGroup(islandGroupIndex).size();
            if (oldTower != null) {
                SchoolBoard oldSchoolBoard = playersManager.getSchoolBoard(playersManager.getPlayers().get((teamLeaderIndex + 1) % 2));
                oldSchoolBoard.addTowers(size);
            }
            islandsManager.setTower(newTower, islandGroupIndex);
            if (!newSchoolBoard.removeTowers(size)) {
                roundManager.setWinner(newTower);
                gameState = GameState.ENDED;
            }
        }
    }
}
