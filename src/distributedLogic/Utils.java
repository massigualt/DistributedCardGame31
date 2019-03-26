package distributedLogic;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Boolean> setArraylist(int size, boolean is) {
        List<Boolean> myList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            myList.add(is);
        }
        return myList;
    }
}
