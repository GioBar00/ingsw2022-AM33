package it.polimi.ingsw.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

class Bag {
    LinkedList<StudentColor> students;

    public Bag() {
        students = new LinkedList<>();
    }

    void addStudents(Collection<StudentColor> newStudents){
        students.addAll(newStudents);
    }

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

    boolean isEmpty(){
        return students.isEmpty();
    }

    void empty(){
        students.clear();
    }
}
