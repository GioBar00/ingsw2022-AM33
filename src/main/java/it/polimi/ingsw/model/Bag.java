package it.polimi.ingsw.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

class Bag {
    LinkedList<StudentColor> students;

    public Bag() {
        students = new LinkedList<>();
    }

    /**
     * adds a group of students to the bag
     * @param newStudents group of students
     */
    void addStudents(Collection<StudentColor> newStudents){
        students.addAll(newStudents);
    }

    /**
     * extracts a random student from the bag
     * @return student extracted
     * @throws NoSuchElementException in case that there aren't anymore students in the bag
     */
    StudentColor popRandomStudent() throws NoSuchElementException{
        int extracted;
        StudentColor temp;

        if(students.size() != 0) {
            extracted = ThreadLocalRandom.current().nextInt(0, students.size());
        } else throw new NoSuchElementException();

        temp = students.get(extracted);
        students.remove(extracted);

        return temp;
    }

    /**
     * calculates whether the Bag is empty or not
     * @return true if the Bag is empty, false otherwise
     */
    boolean isEmpty(){
        return students.isEmpty();
    }

    void empty(){
        students.clear();
    }
}
