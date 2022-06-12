package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.enums.Color;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.views.*;
import it.polimi.ingsw.server.model.enums.*;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.server.model.enums.StudentColor.*;
import static it.polimi.ingsw.server.model.enums.StudentColor.YELLOW;

class CLIPrinter {

    private final String os;

    final Map<String, String> colors;

    private final String green = Color.GREEN.getName();
    private final String reset = Color.RESET.getName();
    private final String red = Color.RED.getName();
    private final String yellow = Color.YELLOW.getName();
    private final String blue = Color.BLUE.getName();
    private final String magenta = Color.MAGENTA.getName();
    private final String cyan = Color.CYAN.getName();
    private final String white = Color.WHITE.getName();



    CLIPrinter(Map<String, String> colors) {
        os = System.getProperty("os.name");
        this.colors = colors;
    }

    void clearTerminal() {
        if (os.contains("Windows")) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Runtime.getRuntime().exec("clear");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\033[H\033[2J");
        System.out.flush();

    }

    void printGameName() {
        clearTerminal();
        System.out.println(colors.get(yellow) + "███████╗██████╗ ██╗ █████╗ ███╗   ██╗████████╗██╗   ██╗███████╗");
        System.out.println("██╔════╝██╔══██╗██║██╔══██╗████╗  ██║╚══██╔══╝╚██╗ ██╔╝██╔════╝");
        System.out.println("█████╗  ██████╔╝██║███████║██╔██╗ ██║   ██║    ╚████╔╝ ███████╗");
        System.out.println("██╔══╝  ██╔══██╗██║██╔══██║██║╚██╗██║   ██║     ╚██╔╝  ╚════██║");
        System.out.println("███████╗██║  ██║██║██║  ██║██║ ╚████║   ██║      ██║   ███████║ ");
        System.out.println("╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝   ╚══════╝ " + colors.get(reset));
    }

    EnumSet<StudentColor> fromIntegersToEnums(Set<Integer> choices) {
        List<StudentColor> ret = new ArrayList<>();
        for (int i : choices) {
            ret.add(StudentColor.retrieveStudentColorByOrdinal(i));
        }
        return EnumSet.copyOf(ret);
    }

    <T extends Enum<T>> String buildSequence(EnumSet<T> choices) {
        StringBuilder text = new StringBuilder("[ ");
        for (T w : choices) {
            text.append(w.toString());
            text.append(" | ");
        }
        text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "]");
        return text.toString();
    }

    String buildSequence(Set<Integer> choices) {
        StringBuilder text = new StringBuilder("[ ");
        List<Integer> sorted = choices.stream().sorted().toList();
        for (Integer i : sorted) {
            text.append(i).append(" | ");
        }
        text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "]");
        return text.toString();
    }

    void printMove(MoveActionRequest move) {
        StringBuilder text = printCommonMoveParts(move);
        Set<Integer> choices = move.getToIndexesSet();
        if (choices != null)
            text.append(" ").append(buildSequence(choices));
        System.out.println(text);
    }

    private StringBuilder printCommonMoveParts(MoveActionRequest move) {
        Set<Integer> choices;
        StringBuilder text = new StringBuilder("Move from " + move.getFrom().toString());
        choices = move.getFromIndexesSet();
        if (choices != null)
            if (move.getFrom() == MoveLocation.ENTRANCE)
                text.append(" ").append(buildSequence(choices));
            else {
                text.append(" ").append(buildSequence(fromIntegersToEnums(choices)));
            }
        text.append(" to ").append(move.getTo().toString());
        return text;
    }

    void printSwap(MoveActionRequest move) {
        StringBuilder text = printCommonMoveParts(move);
        Set<Integer> choices = move.getToIndexesSet();
        if (choices != null && !choices.isEmpty()) {
            if (move.getTo() == MoveLocation.ENTRANCE || move.getTo() == MoveLocation.ISLAND) {
                text.append(" ").append(buildSequence(choices));
            } else {
                text.append(" ").append(buildSequence(fromIntegersToEnums(choices)));
            }
        }
        System.out.println(text);
    }

    String[] buildTeamLobby(TeamsView teamsView) {
        String[] view = new String[7];
        view[0] = "";
        for (int i = 0; i < 15; i++) {
            view[0] = view[0] + " ";
        }
        view[0] = view[0] + colors.get(blue) + "BLACK TEAM";
        for (int i = 0; i < 43; i++) {
            view[0] = view[0] + " ";
        }
        view[0] = view[0] + colors.get(yellow) + " LOBBY";
        for (int i = 0; i < 43; i++) {
            view[0] = view[0] + " ";
        }
        view[0] = view[0] + colors.get(cyan) + "WHITE TEAM ";
        StringBuilder sup = new StringBuilder("┌──────────────────────────────────────┐");
        String sup2 = "          ";
        view[1] = colors.get(blue) + sup + " " + sup2 + colors.get(yellow) + sup + " " + sup2;
        view[1] = view[1] + colors.get(cyan) + sup + " ";

        List<String> black = teamsView.getTeams().get(Tower.BLACK);
        List<String> white = teamsView.getTeams().get(Tower.WHITE);
        List<String> lobby = teamsView.getLobby();

        sup = new StringBuilder();
        sup.append(" ".repeat(37));

        for (int i = 0; i < 4; i++) {
            view[2 + i] = colors.get(blue) + "│ ";
            if (black.size() >= i + 1) {
                String name = black.get(i);
                int size = name.length();
                view[2 + i] = view[2 + i] + colors.get(blue) + name + " ";
                for (int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            } else {
                view[2 + i] = view[2 + i] + sup;
            }
            view[2 + i] = view[2 + i] + colors.get(blue) + "│ ";
            view[2 + i] = view[2 + i] + sup2;

            view[2 + i] = view[2 + i] + colors.get(yellow) + "│ ";
            if (lobby.size() >= i + 1) {
                String name = lobby.get(i);
                int size = name.length();
                view[2 + i] = view[2 + i] + colors.get(yellow) + name + " ";
                for (int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            } else {
                view[2 + i] = view[2 + i] + sup;
            }
            view[2 + i] = view[2 + i] + colors.get(yellow) + "│ ";

            view[2 + i] = view[2 + i] + sup2;

            view[2 + i] = view[2 + i] + colors.get(cyan) + "│ ";
            if (white.size() >= i + 1) {
                String name = white.get(i);
                int size = name.length();
                view[2 + i] = view[2 + i] + colors.get(cyan) + name + " ";
                for (int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            } else {
                view[2 + i] = view[2 + i] + sup;
            }
            view[2 + i] = view[2 + i] + colors.get(cyan) + "│ ";
        }

        sup = new StringBuilder("└──────────────────────────────────────┘");
        sup2 = "          ";
        view[6] = colors.get(blue) + sup + " " + sup2 + colors.get(yellow) + sup + " " + sup2;
        view[6] = view[6] + colors.get(cyan) + sup + " " + colors.get(reset);
        return view;
    }

    ArrayList<String> getSchoolBoardLines(SchoolBoardView sbv, GamePreset preset) {
        ArrayList<String> schoolBoardLines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        int indexEntrance = 0;
        int countTower = 0;
        // CONTORNO SOPRA
        stringBuilder.append(colors.get(yellow)).append("╔");
        for (int i = 0; i < 92; i++) {
            if ((i == 14) || (i == 77) || (i == 67)) {
                stringBuilder.append(("╦"));
            } else {
                stringBuilder.append(("═"));
            }
        }
        stringBuilder.append("╗").append(colors.get(reset));
        schoolBoardLines.add(0, stringBuilder.toString());

        // INTERNO RIGA PER RIGA
        for (int rows = 0; rows < 5; rows++) {
            stringBuilder.delete(0, stringBuilder.length());

            // ENTRANCE
            stringBuilder.append(colors.get(yellow)).append("║");
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(colors.get(white)).append("░");
            }
            if (indexEntrance < preset.getEntranceCapacity() && sbv.getEntrance().get(indexEntrance) != null) {
                appendStudent(sbv.getEntrance().get(indexEntrance), stringBuilder);
            } else
                stringBuilder.append("   ");
            indexEntrance++;
            for (int i = 0; i < 4; i++) {
                stringBuilder.append(colors.get(white)).append("░");
            }
            if (indexEntrance < preset.getEntranceCapacity() && sbv.getEntrance().get(indexEntrance) != null) {
                appendStudent(sbv.getEntrance().get(indexEntrance), stringBuilder);
            } else
                stringBuilder.append("   ");
            indexEntrance++;
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(colors.get(white)).append("░");
            }

            // HALL
            stringBuilder.append(colors.get(yellow)).append("║");
            appendHall(rows, stringBuilder, sbv.getStudentsHall());


            // PROFESSORS
            stringBuilder.append(colors.get(yellow)).append("║");
            for (int i = 0; i < 3; i++) {
                stringBuilder.append(colors.get(white)).append("░");
            }
            StudentColor currentProfLine = studentColorByRow(rows);
            if (sbv.getProfessors().contains(currentProfLine)) {
                switch (currentProfLine) {
                    case RED -> stringBuilder.append(colors.get(red)).append(" ■ ");
                    case BLUE -> stringBuilder.append(colors.get(blue)).append(" ■ ");
                    case GREEN -> stringBuilder.append(colors.get(green)).append(" ■ ");
                    case MAGENTA -> stringBuilder.append(colors.get(magenta)).append(" ■ ");
                    case YELLOW -> stringBuilder.append(colors.get(yellow)).append(" ■ ");
                }
            } else {
                stringBuilder.append("   ");
            }
            for (int i = 0; i < 3; i++) {
                stringBuilder.append(colors.get(white)).append("░");
            }

            // TOWERS
            stringBuilder.append(colors.get(yellow)).append("║");
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(colors.get(white)).append("░");
            }
            if (countTower < sbv.getNumTowers()) {
                appendTower(sbv.getTower(), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            countTower++;
            for (int i = 0; i < 4; i++) {
                stringBuilder.append(colors.get(white)).append("░");
            }
            if (countTower < sbv.getNumTowers()) {
                appendTower(sbv.getTower(), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            countTower++;
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(colors.get(white)).append("░");
            }
            stringBuilder.append(colors.get(yellow)).append("║");
            schoolBoardLines.add(rows + 1, stringBuilder.toString());
        }

        // CONTORNO SOTTO
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(colors.get(yellow)).append("╚");
        for (int i = 0; i < 92; i++) {
            if ((i == 14) || (i == 77) || (i == 67)) {
                stringBuilder.append("╩");
            } else {
                stringBuilder.append("═");
            }
        }
        stringBuilder.append("╝").append(colors.get(reset));
        schoolBoardLines.add(6, stringBuilder.toString());

        // NOMI
        schoolBoardLines.add(7, "    Entrance                         Hall                            Professors    Towers" + " ".repeat(5));
        return schoolBoardLines;
    }

    ArrayList<String> getIslandsLines(List<IslandGroupView> views, int motherNatureIndex) {
        ArrayList<String> islandsLines = new ArrayList<>(7);
        StringBuilder stringBuilder = new StringBuilder();


        appendWater(stringBuilder);
        islandsLines.add(0, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        // top border
        stringBuilder.append(colors.get(blue)).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get(green)).append("╔");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                stringBuilder.append(colors.get(green)).append("═══════");
                if (i == igv.getIslands().size() - 1)
                    stringBuilder.append(colors.get(green)).append("╗");
                else
                    stringBuilder.append(colors.get(green)).append("╦");
            }
            stringBuilder.append(colors.get(blue)).append(" ░ ");
        }


        // first line (tower + blocks)
        islandsLines.add(1, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(colors.get(blue)).append("░");
        int islandIndex = 0;
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get(green)).append("║");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                Tower tower = igv.getIslands().get(i).getTower();
                if (tower == null)
                    stringBuilder.append("   ");
                else
                    appendTower(tower, stringBuilder);
                if (i == 0 && islandIndex == motherNatureIndex)
                    stringBuilder.append(colors.get(green)).append("©");
                else
                    stringBuilder.append(" ");
                appendBlock(igv.isBlocked(), stringBuilder);
                stringBuilder.append(colors.get(green)).append("║");
            }
            stringBuilder.append(colors.get(blue)).append(" ░ ");
            islandIndex++;
        }


        // second line (blue, red and green students)
        islandsLines.add(2, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(colors.get(blue)).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get(green)).append("║ ");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                appendStudentNumber(BLUE, igv.getIslands().get(i).getStudents().get(StudentColor.BLUE), stringBuilder);
                appendStudentNumber(RED, igv.getIslands().get(i).getStudents().get(StudentColor.RED), stringBuilder);
                appendStudentNumber(GREEN, igv.getIslands().get(i).getStudents().get(GREEN), stringBuilder);
                stringBuilder.append(colors.get(green)).append("║ ");
            }
            stringBuilder.append(colors.get(blue)).append("░ ");
        }

        // third line (pink and yellow students)
        islandsLines.add(3, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(colors.get(blue)).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get(green)).append("║ ");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                appendStudentNumber(MAGENTA, igv.getIslands().get(i).getStudents().get(MAGENTA), stringBuilder);
                appendStudentNumber(YELLOW, igv.getIslands().get(i).getStudents().get(YELLOW), stringBuilder);
                stringBuilder.append("  ");
                stringBuilder.append(colors.get(green)).append("║ ");
            }
            stringBuilder.append(colors.get(blue)).append("░ ");
        }


        // bottom border
        islandsLines.add(4, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(colors.get(blue)).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get(green)).append("╚");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                stringBuilder.append(colors.get(green)).append("═══════");
                if (i == igv.getIslands().size() - 1)
                    stringBuilder.append(colors.get(green)).append("╝");
                else
                    stringBuilder.append(colors.get(green)).append("╩");
            }
            stringBuilder.append(colors.get(blue)).append(" ░ ");
        }

        // water layer
        islandsLines.add(5, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        appendWater(stringBuilder);
        islandsLines.add(6, stringBuilder.toString());


        return islandsLines;
    }

    ArrayList<String> getCardLines(CharacterCardView characterCardView) {
        ArrayList<String> cardLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        // top border
        sb.append(colors.get(red)).append("╔═══════════╗").append(colors.get(reset));

        // name of the card
        cardLines.add(0, sb.toString());
        sb.delete(0, sb.length());
        sb.append(colors.get(red)).append("║ ").append(colors.get(reset));
        sb.append(colors.get(red)).append(characterCardView.getType().name()).append(colors.get(reset));

        int spacesLeft = 10 - characterCardView.getType().name().length();
        sb.append(" ".repeat(Math.max(0, spacesLeft)));
        sb.append(colors.get(red)).append("║").append(colors.get(reset));

        // cost of the card
        cardLines.add(1, sb.toString());
        sb.delete(0, sb.length());
        sb.append(colors.get(red)).append("║ ").append(colors.get(reset));

        int total = characterCardView.getOriginalCost() + characterCardView.getAdditionalCost();
        sb.append(colors.get(white)).append(characterCardView.getOriginalCost()).append("+").append(characterCardView.getAdditionalCost()).append(" = ").append(total).append(colors.get(reset));
        sb.append(colors.get(red)).append("   ║").append(colors.get(reset));

        // addictionals (students or blocks)
        cardLines.add(2, sb.toString());
        sb.delete(0, sb.length());

        // if the card has students
        if (characterCardView.getStudent() != null) {
            sb.append(colors.get(red)).append("║ ").append(colors.get(reset));

            int studCount = 0;
            for (StudentColor sc : characterCardView.getStudent().keySet()) {
                for (int i = 0; i < characterCardView.getStudent().get(sc); i++) {
                    appendStudent(sc, sb);
                    if (studCount == 2) {
                        sb.append(colors.get(red)).append(" ║").append(colors.get(reset));

                        cardLines.add(3, sb.toString());
                        sb.delete(0, sb.length());

                        sb.append(colors.get(red)).append("║ ").append(colors.get(reset));
                    }
                    studCount++;
                }
            }
            if (studCount == 5) {
                sb.append("   ");
                studCount++;
            }
            if (studCount == 4) {
                sb.append("      ");
            }
            sb.append(colors.get(red)).append(" ║").append(colors.get(reset));

            // if the card can manage blocks
        } else if (characterCardView.getType().equals(CharacterType.HERBALIST)) {
            sb.append(colors.get(red)).append("║ ").append(colors.get(reset));

            sb.append(colors.get(white)).append("blocks: ").append(characterCardView.getNumBlocks()).append(colors.get(reset));

            sb.append(colors.get(red)).append(" ║").append(colors.get(reset));

            cardLines.add(3, sb.toString());
            sb.delete(0, sb.length());

            sb.append(colors.get(red)).append("║           ║").append(colors.get(reset));
            // if the card has no addictional
        } else {
            sb.append(colors.get(red)).append("║           ║").append(colors.get(reset));

            cardLines.add(3, sb.toString());
            sb.delete(0, sb.length());

            sb.append(colors.get(red)).append("║           ║").append(colors.get(reset));
        }

        // bottom border
        cardLines.add(4, sb.toString());
        sb.delete(0, sb.length());

        sb.append(colors.get(red)).append("╚═══════════╝").append(colors.get(reset));
        cardLines.add(5, sb.toString());
        sb.delete(0, sb.length());

        return cardLines;
    }

    ArrayList<String> getCloudsLines(List<CloudView> cloudViews) {
        ArrayList<String> cloudsLines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        // top border
        stringBuilder.append(" ");
        for (CloudView ignored : cloudViews) {
            stringBuilder.append(colors.get(cyan)).append("╔══════╗").append(colors.get(reset));
            stringBuilder.append(" ");
        }

        // first line of students
        cloudsLines.add(0, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        for (CloudView cv : cloudViews) {
            stringBuilder.append(" ");
            stringBuilder.append(colors.get(cyan)).append("║").append(colors.get(reset));
            if (cv.getStudents().get(0) == null)
                stringBuilder.append("   ");
            else
                appendStudent(cv.getStudents().get(0), stringBuilder);
            if (cv.getStudents().get(1) == null)
                stringBuilder.append("   ");
            else
                appendStudent(cv.getStudents().get(1), stringBuilder);
            stringBuilder.append(colors.get(cyan)).append("║").append(colors.get(reset));
        }
        stringBuilder.append(" ");

        // second line of students
        cloudsLines.add(1, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(" ");
        for (CloudView cv : cloudViews) {
            stringBuilder.append(colors.get(cyan)).append("║").append(colors.get(reset));

            if (cv.getStudents().size() == 3) {
                if (cv.getStudents().get(2) == null)
                    stringBuilder.append("   ");
                else appendStudent(cv.getStudents().get(2), stringBuilder);
                stringBuilder.append("   ");
            } else {
                if (cv.getStudents().get(2) == null)
                    stringBuilder.append("   ");
                else appendStudent(cv.getStudents().get(2), stringBuilder);
                if (cv.getStudents().size() == 4)
                    if (cv.getStudents().get(3) == null)
                        stringBuilder.append("   ");
                    else appendStudent(cv.getStudents().get(3), stringBuilder);
            }
            stringBuilder.append(colors.get(cyan)).append("║").append(colors.get(reset));
            stringBuilder.append(" ");
        }

        // bottom border
        cloudsLines.add(2, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(" ");
        for (CloudView ignored : cloudViews) {
            stringBuilder.append(colors.get(cyan)).append("╚══════╝").append(colors.get(reset));
            stringBuilder.append(" ");
        }
        cloudsLines.add(3, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        return cloudsLines;
    }

    private void appendStudent(StudentColor studentColor, StringBuilder s) {
        switch (studentColor) {
            case RED -> s.append(colors.get(red)).append(" © ").append(colors.get(reset));
            case BLUE -> s.append(colors.get(blue)).append(" © ").append(colors.get(reset));
            case GREEN -> s.append(colors.get(green)).append(" © ").append(colors.get(reset));
            case MAGENTA -> s.append(colors.get(magenta)).append(" © ").append(colors.get(reset));
            case YELLOW -> s.append(colors.get(yellow)).append(" © ").append(colors.get(reset));
        }
    }

    private void appendTower(Tower tower, StringBuilder s) {
        switch (tower) {
            case WHITE -> s.append(colors.get(reset)).append(" W ").append(colors.get(reset));
            case BLACK -> s.append(colors.get(reset)).append(" B ").append(colors.get(reset));
            case GREY -> s.append(colors.get(reset)).append(" G ").append(colors.get(reset));
        }
    }

    private void appendBorder(StudentColor studentColor, StringBuilder s) {
        switch (studentColor) {
            case RED -> s.append(colors.get(red)).append("░░").append(colors.get(reset));
            case BLUE -> s.append(colors.get(blue)).append("░░").append(colors.get(reset));
            case GREEN -> s.append(colors.get(green)).append("░░").append(colors.get(reset));
            case MAGENTA -> s.append(colors.get(magenta)).append("░░").append(colors.get(reset));
            case YELLOW -> s.append(colors.get(yellow)).append("░░").append(colors.get(reset));
        }
    }

    private void appendHall(int row, StringBuilder s, EnumMap<StudentColor, Integer> hallView) {
        StudentColor studentColor = studentColorByRow(row);
        appendBorder(studentColor, s);
        for (int j = 0; j < 10; j++) {
            if (j < hallView.get(studentColor)) {
                appendStudent(studentColor, s);
            } else {
                s.append("   ");
            }
            appendBorder(studentColor, s);
        }
    }

    private StudentColor studentColorByRow(int row) {
        StudentColor studentColor = null;
        switch (row) {
            case 0 -> studentColor = StudentColor.GREEN;
            case 1 -> studentColor = StudentColor.RED;
            case 2 -> studentColor = StudentColor.YELLOW;
            case 3 -> studentColor = StudentColor.MAGENTA;
            case 4 -> studentColor = StudentColor.BLUE;
        }
        return studentColor;
    }

    private void appendBlock(boolean isBlocked, StringBuilder s) {
        if (isBlocked)
            s.append(colors.get(red)).append(" X ").append(colors.get(reset));
        else
            s.append("   ");
    }

    private void appendStudentNumber(StudentColor studentColor, int num, StringBuilder s) {
        if (num == 0)
            s.append("  ");
        else
            switch (studentColor) {
                case RED -> s.append(colors.get(red)).append(num).append(" ").append(colors.get(reset));
                case BLUE -> s.append(colors.get(blue)).append(num).append(" ").append(colors.get(reset));
                case GREEN -> s.append(colors.get(green)).append(num).append(" ").append(colors.get(reset));
                case MAGENTA -> s.append(colors.get(magenta)).append(num).append(" ").append(colors.get(reset));
                case YELLOW -> s.append(colors.get(yellow)).append(num).append(" ").append(colors.get(reset));
            }
    }

    private void appendWater(StringBuilder s) {
        s.append(colors.get(blue));
        s.append("░".repeat(144));
        s.append(colors.get(reset));
    }

    String getFirstLine(String nickname, GameView gameView) {
        String text = "Eriantys       Player : " + nickname;
        text = text + "   Current Player : " + gameView.getCurrentPlayer();
        if (gameView.getMode().equals(GameMode.EXPERT)) {
            text = text + "  Coins available : " + gameView.getReserve();
            text = text + " Your Coin(s) : " + gameView.getPlayerCoins().get(nickname);
        }
        return text;
    }
}
