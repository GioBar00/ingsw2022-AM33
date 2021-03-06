package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.StudentColor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The Bag class is used to store the students that are remaining in the game.
 */
class Bag {

    /**
     * students currently in the bag
     */
    final LinkedList<StudentColor> students;

    /**
     * Public constructor for a Bag
     */
    public Bag() {
        students = new LinkedList<>();
    }

    /**
     * adds a group of students to the bag
     *
     * @param newStudents group of students
     */
    void addStudents(List<StudentColor> newStudents) {
        students.addAll(newStudents);
    }

    /**
     * extracts a random student from the bag
     *
     * @return student extracted, null if empty
     */
    StudentColor popRandomStudent() {
        int extracted;
        StudentColor temp;

        if (students.size() != 0) {
            extracted = ThreadLocalRandom.current().nextInt(0, students.size());
        } else return null;

        temp = students.get(extracted);
        students.remove(extracted);

        return temp;
    }

    /**
     * calculates whether the Bag is empty or not
     *
     * @return true if the Bag is empty, false otherwise
     */
    boolean isEmpty() {
        return students.isEmpty();
    }

    /**
     * removes all the students from the bag.
     */
    void empty() {
        students.clear();
    }
}
