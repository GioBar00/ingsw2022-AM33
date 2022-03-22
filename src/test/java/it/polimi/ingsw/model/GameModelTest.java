package it.polimi.ingsw.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.naming.NoPermissionException;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {

    GameModel model;

    @Test
    void addNewPlayer() {

    }

    @Test
    void initializeGame() {
    }

    @Test
    void checkTotalNumStudents() throws NoPermissionException {
        model = new GameModel(2);
        model.addPlayer("p1");
        model.addPlayer("p2");
        model.initializeGame();
        assert model.bag != null;
        int numStud = model.bag.students.size();
        assert model.players != null;
        for (Player p: model.players) {
            numStud += p.getSchoolBoard().getStudentsInEntrance().stream().filter(Objects::nonNull).toArray().length;
            for (StudentColor s: StudentColor.values())
                numStud += p.getSchoolBoard().getStudentsInHall(s);
        }
        assert model.clouds != null;
        for (Cloud c: model.clouds)
            numStud += c.getStudents().stream().filter(Objects::nonNull).toArray().length;
        assert model.islandGroups != null;
        for (IslandGroup ig: model.islandGroups)
            for (Island i: ig.islands)
                numStud += i.getNumStudents();
        assertEquals(130, numStud);
    }
}