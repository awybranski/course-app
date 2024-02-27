//#20

import java.util.ArrayList;

public class DegreeList {
    private static DegreeList degreeList;
    private ArrayList<Degree> degrees;

    private DegreeList(){
        degrees = new ArrayList<Degree>();

    }
    public DegreeList getInstance(){
        if (degreeList == null) {
			degreeList = new DegreeList();
		}
		    return degreeList;
    } //not finished
    public Degree getMajor(String major){
        for (Degree degree : degrees) {
            if(degree.major().equals(major))
                return degree;
        }
        return null;
    }
}
