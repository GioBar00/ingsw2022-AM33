package it.polimi.ingsw.client;

import it.polimi.ingsw.client.CLI.CLI;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.IslandGroupView;
import it.polimi.ingsw.network.messages.views.IslandView;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.EnumMap;

import static org.fusesource.jansi.Ansi.ansi;

public class TestClass {
    static BufferedReader stdIn;
    static GameView gameView;

    static String nickname = "0";
    public static void main(String[] args) {
        UI userInterface = new CLI();
        userInterface.showStartScreen();
    }

    private static String printIsland(IslandGroupView islandGroup, int index, int motherNature){
        StringBuilder text = new StringBuilder("│ " + "N° " + index + "    ");
        EnumMap < StudentColor, Integer> students = new EnumMap<>(StudentColor.class);
        for(StudentColor s : StudentColor.values()){
            students.put(s,0);
        }
        if(index < 10)
            text.append(" ");
        int i = 0;
        for(IslandView iV : islandGroup.getIslands()){
            for(StudentColor s : StudentColor.values()){
                EnumMap<StudentColor, Integer> islStud = iV.getStudents();
                if(islStud.containsKey(s))
                    students.replace(s, students.get(s) + iV.getStudents().get(s));
            }
            i++;
        }
        int num;
        for(StudentColor s : StudentColor.values()){
            num = students.get(s);
            if( num > 0){
                if(!s.equals(StudentColor.PINK)){
                    text.append(" @|").append(s.toString().toLowerCase()).append(" ").append(num).append("|@ ");
                }
                else{
                    text.append(" @|magenta").append(" ").append(num).append("|@ ");
                }
                if(num< 10)
                    text.append("  ");
                else if(num < 100)
                    text.append(" ");
            }
            else text.append("     ");
        }

        Tower tower = islandGroup.getIslands().get(0).getTower();

        if(tower != null){
            if(tower.equals(Tower.BLACK))
                text.append("  @|black ").append(i).append(" BLACK tower(s)  |@");
            else if (tower.equals(Tower.WHITE))
                text.append("  ").append(i).append(" WHITE tower(s)  ");
            else text.append("  @|white ").append(i).append(" GREY tower(s)   |@");
        }else{
           text.append("                    ");
        }

        if(islandGroup.isBlocked()) {
            text.append(" @|red is blocked|@ ");
        }
        else  text.append("            ");

        if(index == motherNature){
            text.append("@|green  MotherNature |@");
        }
        else text.append("              ");

        text.append("│");
        return text.toString();
    }

    private static ArrayList<String> details(){
        ArrayList<String> text = new ArrayList<>();
        text.add("Game Phase " + gameView.getPhase().toString());
        text.add("Current Player " + gameView.getCurrentPlayer());
        if(gameView.getMode().equals(GameMode.EXPERT)){
            text.add("Coins available   @|yellow " + gameView.getReserve() + "|@");
            text.add("Your coins        @|yellow " + gameView.getPlayerCoins().get(nickname) + "|@");
        }
        return text;
    }

    private String AssistantCards(){
        return null;
    }

    private String CharacterCards(){
        return null;
    }
}
