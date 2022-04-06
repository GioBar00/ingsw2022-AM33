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
     * the method adds the to-be player to the lobby and waits for the client to choose a preferred team before adding
     * it as a player
     * @param nickname unique identifier of a player
     * @return if the player was added successfully.
     */
    @Override
    public boolean addPlayer(String nickname) {
        if (gameState != GameState.UNINITIALIZED)
            return false;

        return playersManager.addToLobby(nickname);
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
        String leaderNick;
        int teamLeaderIndex = 0;

        if (newTower != oldTower){
            leaderNick = playersManager.getTeams().get(newTower).get(0).getNickname();
            for(int i = 0; i < playersManager.getPreset().getPlayersNumber(); i++){
                if (leaderNick.equals(playersManager.getPlayers().get(i).getNickname())){
                    if (playersManager.getSchoolBoard(playersManager.getPlayers().get(i)).getNumTowers() != 0){
                        teamLeaderIndex = i;
                        break;
                    }
                }
            }

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


    /**
     * the method changes the team to which the player belongs; if the player didn't previously belong to any team,
     * it's added as a new member
     * @param nickname of the player
     * @param tower of the new team
     * @return true if the change was successful
     */
    @Override
    public boolean changeTeam(String nickname, Tower tower) {
        return playersManager.changeTeam(nickname, tower);
    }
}
