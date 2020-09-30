import java.util.Comparator;

public class PersonByIDComparator implements Comparator<Person> {

    @Override
    public int compare(Person o1, Person o2) {
        if(o1.id > o2.id)
            return 1;
        else
            return -1;
    }
}
