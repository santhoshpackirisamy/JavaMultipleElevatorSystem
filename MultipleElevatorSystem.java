import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;
import static java.lang.Math.abs;

public class MultipleElevatorSystem
{
    public static void main(String[] args)
    {
        System.out.println("******************Welcome To Mylift******************");
        Elevator elevator = Elevator.getInstance();
    }
}

class Elevator
{
    private static Elevator elevator = null;
    private static ArrayList<AllElevator> AllTypeElevatorInstance = new ArrayList<AllElevator>();
    private static ArrayList<OddElevator> OddTypeElevatorInstance = new ArrayList<OddElevator>();
    private static ArrayList<EvenElevator> EvenTypeElevatorInstance = new ArrayList<EvenElevator>();
    private int currentFloor = 0;
    Thread requestProcessorThread;

    private Elevator()
    {
        Thread requestListenerThread = new Thread(new RequestListener(),"RequestListenerThread");

        Thread requestProcessorThread1 = new Thread(new RequestProcessor(),"Lift1");
        Thread requestProcessorThread2 = new Thread(new RequestProcessor(),"Lift2");
        Thread requestProcessorThread3 = new Thread(new RequestProcessor(),"Lift3");
        Thread requestProcessorThread4 = new Thread(new RequestProcessor(),"Lift4");

        AllElevator elevator1 = new AllElevator(Type.ALL,requestProcessorThread1);
        AllElevator elevator2 = new AllElevator(Type.ALL,requestProcessorThread2);
        OddElevator elevator3 = new OddElevator(Type.ODD,requestProcessorThread3);
        EvenElevator elevator4 = new EvenElevator(Type.EVEN,requestProcessorThread4);

        AllTypeElevatorInstance.add(elevator1);
        AllTypeElevatorInstance.add(elevator2);
        OddTypeElevatorInstance.add(elevator3);
        EvenTypeElevatorInstance.add(elevator4);

        requestListenerThread.start();
        requestProcessorThread1.start();
        requestProcessorThread2.start();
        requestProcessorThread3.start();
        requestProcessorThread4.start();
    };


    static Elevator getInstance()
    {
        if(elevator == null)
        {
            elevator = new Elevator();
        }
        return elevator;
    }


    public synchronized void getBestFloor(int floor)
    {
        int floordifference = 100;
        OddElevator Oddbestelevator = null;
        EvenElevator Evenbestelevator = null;
        AllElevator Allbestelevator = null;

        if(floor%2==0)
        {
            EvenElevator elevatoriterator;

            for(int i=0;i<Elevator.EvenTypeElevatorInstance.size();i++)
            {
                int currentfloor=0;
                elevatoriterator = Elevator.EvenTypeElevatorInstance.get(i);
                currentfloor = elevatoriterator.getCurrentFloor();
                if(floor > currentfloor && elevatoriterator.getDirection() == Direction.UP)
                {
                    int tempfloordifference = floor-currentfloor;
                    if(tempfloordifference < floordifference)
                    {
                        floordifference = tempfloordifference;
                        Evenbestelevator = elevatoriterator;
                    }
                }
                else if (floor < currentfloor && elevatoriterator.getDirection() == Direction.DOWN)
                {
                    int tempfloordifference = currentfloor-floor;
                    if(tempfloordifference < floordifference)
                    {
                        floordifference = tempfloordifference;
                        Evenbestelevator = elevatoriterator;
                    }
                }
                else if( (elevatoriterator.getRequestProcessorThread().getState() == Thread.State.WAITING))
                {
                    int tempfloordifference = abs(currentfloor-floor);
                    if(tempfloordifference < floordifference)
                    {
                        floordifference = tempfloordifference;
                        Evenbestelevator = elevatoriterator;
                    }
                }
            }
        }
        else
        {
            OddElevator elevatoriterator;

            for(int i=0;i<Elevator.OddTypeElevatorInstance.size();i++)
            {
                int currentfloor=0;
                elevatoriterator = Elevator.OddTypeElevatorInstance.get(i);
                currentfloor = elevatoriterator.getCurrentFloor();
                if(floor > currentfloor && elevatoriterator.getDirection() == Direction.UP)
                {
                    int tempfloordifference = floor-currentfloor;
                    if(tempfloordifference < floordifference)
                    {
                        floordifference = tempfloordifference;
                        Oddbestelevator = elevatoriterator;
                    }
                }
                else if (floor < currentfloor && elevatoriterator.getDirection() == Direction.DOWN)
                {
                    int tempfloordifference = currentfloor-floor;
                    if(tempfloordifference < floordifference)
                    {
                        floordifference = tempfloordifference;
                        Oddbestelevator = elevatoriterator;
                    }
                }
                else if( (elevatoriterator.getRequestProcessorThread().getState() == Thread.State.WAITING))
                {
                    int tempfloordifference = abs(currentfloor-floor);
                    if(tempfloordifference < floordifference)
                    {
                        floordifference = tempfloordifference;
                        Oddbestelevator = elevatoriterator;
                    }
                }
            }
        }


        AllElevator elevatoriterator;

        for(int i=0;i<Elevator.AllTypeElevatorInstance.size();i++)
        {
            int curflr=0;
            elevatoriterator = Elevator.AllTypeElevatorInstance.get(i);
            curflr = elevatoriterator.getCurrentFloor();
            if(elevatoriterator.getRequestSet().isEmpty() && elevatoriterator.getType() == Type.ALL)
            {
                int tempfloordifference = abs(curflr-floor);
                if(tempfloordifference*(1.5) < floordifference)
                {
                    floordifference = tempfloordifference;
                    Allbestelevator = elevatoriterator;
                }
            }
        }

        if(Allbestelevator==null && Oddbestelevator==null && Evenbestelevator==null)
        {
            for(int i=0;i<Elevator.AllTypeElevatorInstance.size();i++)
            {
                elevatoriterator = Elevator.AllTypeElevatorInstance.get(i);
                if(elevatoriterator.getRequestProcessorThread().getState() == Thread.State.WAITING)
                {
                    Allbestelevator = elevatoriterator;
                }
            }
        }

        if(Allbestelevator != null)
        {
            System.out.println(Allbestelevator.getRequestProcessorThread().getName()+ " is best Elevator");
            Allbestelevator.setRequestSet(floor);
        }
        else
        {
            if(Oddbestelevator != null)
            {
                System.out.println(Oddbestelevator.getRequestProcessorThread().getName()+ " is best Elevator");
                Oddbestelevator.setRequestSet(floor);
            }
            else if(Evenbestelevator != null)
            {
                System.out.println(Evenbestelevator.getRequestProcessorThread().getName()+ " is best Elevator");
                Evenbestelevator.setRequestSet(floor);
            }
        }

    }

    public synchronized void addFloor(int floor,Thread requestProcessorThread)
    {
        getBestFloor(floor);

        if(requestProcessorThread.getState() == Thread.State.WAITING){
            notify();
        }else{
            requestProcessorThread.interrupt();
        }
    }

    public static ArrayList<AllElevator> getAllElevator()
    {
        return AllTypeElevatorInstance;
    }

    public static ArrayList<OddElevator> getOddElevator()
    {
        return OddTypeElevatorInstance;
    }

    public static ArrayList<EvenElevator> getEvenElevator()
    {
        return EvenTypeElevatorInstance;
    }

}

class AllElevator  implements ElevatorOperation
{
    public TreeSet requestSet = new TreeSet();
    private int currentFloor = 0;
    private Direction direction = Direction.UP;
    private Type Type;
    private Thread requestProcessorThread;

    public AllElevator(Type type,Thread requestProcessorThread) {
        Type = type;this.requestProcessorThread=requestProcessorThread;
    }

    @Override
    public Thread getRequestProcessorThread()
    {
        return requestProcessorThread;
    }

    @Override
    public Type getType()
    {
        return Type;
    }

    @Override
    public int getCurrentFloor()
    {
        return currentFloor;
    }

    @Override
    public Direction getDirection()
    {
        return direction;
    }

    @Override
    public TreeSet getRequestSet()
    {
        return requestSet;
    }

    @Override
    public void setRequestSet(int floor) {
        requestSet.add(floor);
    }

    @Override
    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    @Override
    public void setCurrentFloor(int currentFloor) throws InterruptedException
    {
        if (this.currentFloor > currentFloor) {
            setDirection(Direction.DOWN);
        } else {
            setDirection(Direction.UP);
        }
        this.currentFloor = currentFloor;

        System.out.println(requestProcessorThread.getName() + " Floor : " + currentFloor);

        Thread.sleep( 1500);
    }

    @Override
    public synchronized int nextFloor()
    {
        Integer floor = null;

        if (direction == Direction.UP) {
            if (requestSet.ceiling(currentFloor) != null) {
                floor = (Integer) requestSet.ceiling(currentFloor);
            } else {
                floor = (Integer) requestSet.floor(currentFloor);
            }
        } else {
            if (requestSet.floor(currentFloor) != null) {
                floor = (Integer) requestSet.floor(currentFloor);
            } else {
                floor = (Integer) requestSet.ceiling(currentFloor);
            }
        }

        if (floor == null) {
            try {
                int elevatorwaitingcount = 0;
                for (int i = 0; i < Elevator.getAllElevator().size(); i++) {
                    if ((Elevator.getAllElevator().get(i).requestSet.isEmpty())) {
                        elevatorwaitingcount++;
                    }
                }
                for (int i = 0; i < Elevator.getOddElevator().size(); i++) {
                    if ((Elevator.getOddElevator().get(i).requestSet.isEmpty())) {
                        elevatorwaitingcount++;
                    }
                }
                for (int i = 0; i < Elevator.getOddElevator().size(); i++) {
                    if ((Elevator.getAllElevator().get(i).requestSet.isEmpty())) {
                        elevatorwaitingcount++;
                    }
                }
                if (elevatorwaitingcount == (Elevator.getAllElevator().size()+Elevator.getOddElevator().size()+Elevator.getEvenElevator().size())) {
                    for (int i = 0; i < Elevator.getAllElevator().size(); i++) {
                        System.out.println(Elevator.getAllElevator().get(i).getRequestProcessorThread().getName() + " Waiting at Floor :" + Elevator.getAllElevator().get(i).getCurrentFloor());
                    }
                    for (int i = 0; i < Elevator.getOddElevator().size(); i++) {
                        System.out.println(Elevator.getOddElevator().get(i).getRequestProcessorThread().getName() + " Waiting at Floor :" + Elevator.getOddElevator().get(i).getCurrentFloor());
                    }
                    for (int i = 0; i < Elevator.getEvenElevator().size(); i++) {
                        System.out.println(Elevator.getEvenElevator().get(i).getRequestProcessorThread().getName() + " Waiting at Floor :" + Elevator.getEvenElevator().get(i).getCurrentFloor());
                    }
                }

                for(int i=0;i<Elevator.getAllElevator().size();i++) {
                    if ((!Elevator.getAllElevator().get(i).requestSet.isEmpty()) && Elevator.getAllElevator().get(i).getRequestProcessorThread().getState() == Thread.State.WAITING) {

                        Elevator.getAllElevator().get(i).getRequestProcessorThread().interrupt();
                    }
                }
                for(int i=0;i<Elevator.getOddElevator().size();i++) {
                    if ((!Elevator.getOddElevator().get(i).requestSet.isEmpty()) && Elevator.getOddElevator().get(i).getRequestProcessorThread().getState() == Thread.State.WAITING) {

                        Elevator.getOddElevator().get(i).getRequestProcessorThread().interrupt();
                    }
                }
                for(int i=0;i<Elevator.getEvenElevator().size();i++) {
                    if ((!Elevator.getEvenElevator().get(i).requestSet.isEmpty()) && Elevator.getEvenElevator().get(i).getRequestProcessorThread().getState() == Thread.State.WAITING) {

                        Elevator.getEvenElevator().get(i).getRequestProcessorThread().interrupt();
                    }
                }
                if(this.getRequestProcessorThread().getState() != Thread.State.WAITING)
                {

                    wait();
                }
            } catch (InterruptedException ignored) {
            }
        }

        return (floor == null) ? -1 : floor;
    }



}

class OddElevator  implements ElevatorOperation
{

    public TreeSet requestSet = new TreeSet();
    private int currentFloor = 0;
    private Direction direction = Direction.UP;
    private Type Type;
    private Thread requestProcessorThread;

    public OddElevator(Type type,Thread requestProcessorThread) {
        Type = type;this.requestProcessorThread=requestProcessorThread;
    }

    @Override
    public Thread getRequestProcessorThread()
    {
        return requestProcessorThread;
    }

    @Override
    public Type getType()
    {
        return Type;
    }

    @Override
    public int getCurrentFloor()
    {
        return currentFloor;
    }

    @Override
    public Direction getDirection()
    {
        return direction;
    }

    @Override
    public TreeSet getRequestSet()
    {
        return requestSet;
    }

    @Override
    public void setRequestSet(int floor) {
        requestSet.add(floor);
    }

    @Override
    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    @Override
    public void setCurrentFloor(int currentFloor) throws InterruptedException
    {
        if (this.currentFloor > currentFloor) {
            setDirection(Direction.DOWN);
        } else {
            setDirection(Direction.UP);
        }
        this.currentFloor = currentFloor;

        System.out.println(requestProcessorThread.getName() + " Floor : " + currentFloor);

        Thread.sleep( 1500);
    }

    @Override
    public synchronized int nextFloor()
    {
        Integer floor = null;

        if (direction == Direction.UP) {
            if (requestSet.ceiling(currentFloor) != null) {
                floor = (Integer) requestSet.ceiling(currentFloor);
            } else {
                floor = (Integer) requestSet.floor(currentFloor);
            }
        } else {
            if (requestSet.floor(currentFloor) != null) {
                floor = (Integer) requestSet.floor(currentFloor);
            } else {
                floor = (Integer) requestSet.ceiling(currentFloor);
            }
        }

        if (floor == null) {
            try {
                int elevatorwaitingcount = 0;
                for (int i = 0; i < Elevator.getAllElevator().size(); i++) {
                    if ((Elevator.getAllElevator().get(i).requestSet.isEmpty())) {
                        elevatorwaitingcount++;
                    }
                }
                for (int i = 0; i < Elevator.getOddElevator().size(); i++) {
                    if ((Elevator.getOddElevator().get(i).requestSet.isEmpty())) {
                        elevatorwaitingcount++;
                    }
                }
                for (int i = 0; i < Elevator.getOddElevator().size(); i++) {
                    if ((Elevator.getAllElevator().get(i).requestSet.isEmpty())) {
                        elevatorwaitingcount++;
                    }
                }
                if (elevatorwaitingcount == (Elevator.getAllElevator().size()+Elevator.getOddElevator().size()+Elevator.getEvenElevator().size())) {
                    for (int i = 0; i < Elevator.getAllElevator().size(); i++) {
                        System.out.println(Elevator.getAllElevator().get(i).getRequestProcessorThread().getName() + " Waiting at Floor :" + Elevator.getAllElevator().get(i).getCurrentFloor());
                    }
                    for (int i = 0; i < Elevator.getOddElevator().size(); i++) {
                        System.out.println(Elevator.getOddElevator().get(i).getRequestProcessorThread().getName() + " Waiting at Floor :" + Elevator.getOddElevator().get(i).getCurrentFloor());
                    }
                    for (int i = 0; i < Elevator.getEvenElevator().size(); i++) {
                        System.out.println(Elevator.getEvenElevator().get(i).getRequestProcessorThread().getName() + " Waiting at Floor :" + Elevator.getEvenElevator().get(i).getCurrentFloor());
                    }
                }

                for(int i=0;i<Elevator.getAllElevator().size();i++) {
                    if ((!Elevator.getAllElevator().get(i).requestSet.isEmpty()) && Elevator.getAllElevator().get(i).getRequestProcessorThread().getState() == Thread.State.WAITING) {

                        Elevator.getAllElevator().get(i).getRequestProcessorThread().interrupt();
                    }
                }
                for(int i=0;i<Elevator.getOddElevator().size();i++) {
                    if ((!Elevator.getOddElevator().get(i).requestSet.isEmpty()) && Elevator.getOddElevator().get(i).getRequestProcessorThread().getState() == Thread.State.WAITING) {

                        Elevator.getOddElevator().get(i).getRequestProcessorThread().interrupt();
                    }
                }
                for(int i=0;i<Elevator.getEvenElevator().size();i++) {
                    if ((!Elevator.getEvenElevator().get(i).requestSet.isEmpty()) && Elevator.getEvenElevator().get(i).getRequestProcessorThread().getState() == Thread.State.WAITING) {

                        Elevator.getEvenElevator().get(i).getRequestProcessorThread().interrupt();
                    }
                }
                if(this.getRequestProcessorThread().getState() != Thread.State.WAITING)
                {

                        wait();
                }
            } catch (InterruptedException ignored) {
            }
        }

        return (floor == null) ? -1 : floor;
    }



}

class EvenElevator  implements ElevatorOperation
{

    public TreeSet requestSet = new TreeSet();
    private int currentFloor = 0;
    private Direction direction = Direction.UP;
    private Type Type;
    private Thread requestProcessorThread;

    public EvenElevator(Type type,Thread requestProcessorThread) {
        Type = type;this.requestProcessorThread=requestProcessorThread;
    }

    @Override
    public Thread getRequestProcessorThread()
    {
        return requestProcessorThread;
    }

    @Override
    public Type getType()
    {
        return Type;
    }

    @Override
    public int getCurrentFloor()
    {
        return currentFloor;
    }

    @Override
    public Direction getDirection()
    {
        return direction;
    }

    @Override
    public TreeSet getRequestSet()
    {
        return requestSet;
    }

    @Override
    public void setRequestSet(int floor) {
        requestSet.add(floor);
    }

    @Override
    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    @Override
    public void setCurrentFloor(int currentFloor) throws InterruptedException
    {
        if (this.currentFloor > currentFloor) {
            setDirection(Direction.DOWN);
        } else {
            setDirection(Direction.UP);
        }
        this.currentFloor = currentFloor;

        System.out.println(requestProcessorThread.getName() + " Floor : " + currentFloor);

        Thread.sleep( 1500);
    }

    @Override
    public synchronized int nextFloor()
    {
        Integer floor = null;

        if (direction == Direction.UP) {
            if (requestSet.ceiling(currentFloor) != null) {
                floor = (Integer) requestSet.ceiling(currentFloor);
            } else {
                floor = (Integer) requestSet.floor(currentFloor);
            }
        } else {
            if (requestSet.floor(currentFloor) != null) {
                floor = (Integer) requestSet.floor(currentFloor);
            } else {
                floor = (Integer) requestSet.ceiling(currentFloor);
            }
        }

        if (floor == null) {
            try {
                int elevatorwaitingcount = 0;
                for (int i = 0; i < Elevator.getAllElevator().size(); i++) {
                    if ((Elevator.getAllElevator().get(i).requestSet.isEmpty())) {
                        elevatorwaitingcount++;
                    }
                }
                for (int i = 0; i < Elevator.getOddElevator().size(); i++) {
                    if ((Elevator.getOddElevator().get(i).requestSet.isEmpty())) {
                        elevatorwaitingcount++;
                    }
                }
                for (int i = 0; i < Elevator.getOddElevator().size(); i++) {
                    if ((Elevator.getAllElevator().get(i).requestSet.isEmpty())) {
                        elevatorwaitingcount++;
                    }
                }
                if (elevatorwaitingcount == (Elevator.getAllElevator().size()+Elevator.getOddElevator().size()+Elevator.getEvenElevator().size())) {
                    for (int i = 0; i < Elevator.getAllElevator().size(); i++) {
                        System.out.println(Elevator.getAllElevator().get(i).getRequestProcessorThread().getName() + " Waiting at Floor :" + Elevator.getAllElevator().get(i).getCurrentFloor());
                    }
                    for (int i = 0; i < Elevator.getOddElevator().size(); i++) {
                        System.out.println(Elevator.getOddElevator().get(i).getRequestProcessorThread().getName() + " Waiting at Floor :" + Elevator.getOddElevator().get(i).getCurrentFloor());
                    }
                    for (int i = 0; i < Elevator.getEvenElevator().size(); i++) {
                        System.out.println(Elevator.getEvenElevator().get(i).getRequestProcessorThread().getName() + " Waiting at Floor :" + Elevator.getEvenElevator().get(i).getCurrentFloor());
                    }
                }

                for(int i=0;i<Elevator.getAllElevator().size();i++) {
                    if ((!Elevator.getAllElevator().get(i).requestSet.isEmpty()) && Elevator.getAllElevator().get(i).getRequestProcessorThread().getState() == Thread.State.WAITING) {
                        Elevator.getAllElevator().get(i).getRequestProcessorThread().interrupt();
                    }
                }
                for(int i=0;i<Elevator.getOddElevator().size();i++) {
                    if ((!Elevator.getOddElevator().get(i).requestSet.isEmpty()) && Elevator.getOddElevator().get(i).getRequestProcessorThread().getState() == Thread.State.WAITING) {
                        Elevator.getOddElevator().get(i).getRequestProcessorThread().interrupt();
                    }
                }
                for(int i=0;i<Elevator.getEvenElevator().size();i++) {
                    if ((!Elevator.getEvenElevator().get(i).requestSet.isEmpty()) && Elevator.getEvenElevator().get(i).getRequestProcessorThread().getState() == Thread.State.WAITING) {
                        Elevator.getEvenElevator().get(i).getRequestProcessorThread().interrupt();
                    }
                }
                if(this.getRequestProcessorThread().getState() != Thread.State.WAITING)
                {

                    wait();
                }
            } catch (InterruptedException ignored) {
            }
        }

        return (floor == null) ? -1 : floor;
    }


}


class RequestProcessor implements Runnable
{

    @Override
    public synchronized void run()
    {
        while (true)
        {
            ArrayList<AllElevator> AllTypeElevatorInstance;
            ArrayList<OddElevator> OddTypeElevatorInstance;
            ArrayList<EvenElevator> EvenTypeElevatorInstance;

            AllTypeElevatorInstance = Elevator.getAllElevator();
            OddTypeElevatorInstance = Elevator.getOddElevator();
            EvenTypeElevatorInstance = Elevator.getEvenElevator();

            OddElevator Oddbestelevator = null;
            EvenElevator Evenbestelevator = null;
            AllElevator Allbestelevator = null;

            for(int i=0;i<OddTypeElevatorInstance.size();i++)
            {
                if (OddTypeElevatorInstance.get(i).getRequestProcessorThread().getState() != Thread.State.WAITING) {
                    Oddbestelevator = OddTypeElevatorInstance.get(i);
                }
            }

            for(int i=0;i<EvenTypeElevatorInstance.size();i++)
            {
                if (EvenTypeElevatorInstance.get(i).getRequestProcessorThread().getState() != Thread.State.WAITING) {
                    Evenbestelevator = EvenTypeElevatorInstance.get(i);
                }
            }

            if(Oddbestelevator==null && Evenbestelevator==null)
            {
                for(int i=0;i<AllTypeElevatorInstance.size();i++)
                {
                    if (AllTypeElevatorInstance.get(i).getRequestProcessorThread().getState() != Thread.State.WAITING) {
                        Allbestelevator = AllTypeElevatorInstance.get(i);
                    }
                }
            }

            if(Allbestelevator==null && Oddbestelevator==null && Evenbestelevator==null)
            {
                Allbestelevator = AllTypeElevatorInstance.get(0);
            }


            if(Allbestelevator!=null)
            {
                int nextfloor = Allbestelevator.nextFloor();
                int currentFloor = Allbestelevator.getCurrentFloor();
                try{
                    if (nextfloor >= 0) {
                        if (currentFloor > nextfloor) {
                            while (currentFloor > nextfloor) {
                                Allbestelevator.setCurrentFloor(--currentFloor);
                            }
                        } else {
                            while (currentFloor < nextfloor) {
                                Allbestelevator.setCurrentFloor(++currentFloor);
                            }
                        }
                        System.out.println(Allbestelevator.getRequestProcessorThread().getName() + " Welcome to Floor : " + Allbestelevator.getCurrentFloor());
                        Allbestelevator.requestSet.remove(Allbestelevator.getCurrentFloor());
                    }
                }catch(InterruptedException e){
                    if(Allbestelevator.getCurrentFloor() != nextfloor){
                        Allbestelevator.getRequestSet().add(nextfloor);
                    }
                }
            }
            else
            {
                if(Oddbestelevator!=null)
                {
                    int nextfloor = Oddbestelevator.nextFloor();
                    int currentFloor = Oddbestelevator.getCurrentFloor();
                    try{
                        if (nextfloor >= 0) {
                            if (currentFloor > nextfloor) {
                                while (currentFloor > nextfloor) {
                                    if (currentFloor==1)
                                    {
                                        currentFloor--;
                                    }
                                    else
                                    {
                                        currentFloor = currentFloor-2;
                                    }
                                    Oddbestelevator.setCurrentFloor(currentFloor);
                                }
                            } else {
                                while (currentFloor < nextfloor) {
                                    if (currentFloor==0)
                                    {
                                        currentFloor++;
                                    }
                                    else
                                    {
                                        currentFloor = currentFloor+2;
                                    }
                                    Oddbestelevator.setCurrentFloor(currentFloor);
                                }
                            }
                            System.out.println(Oddbestelevator.getRequestProcessorThread().getName() + " Welcome to Floor : " + Oddbestelevator.getCurrentFloor());
                            Oddbestelevator.requestSet.remove(Oddbestelevator.getCurrentFloor());
                        }
                    }catch(InterruptedException e){
                        if(Oddbestelevator.getCurrentFloor() != nextfloor){
                            Oddbestelevator.getRequestSet().add(nextfloor);
                        }
                    }
                }
                else
                {
                    if(Evenbestelevator!=null) {
                        int nextfloor = Evenbestelevator.nextFloor();
                        int currentFloor = Evenbestelevator.getCurrentFloor();
                        try {
                            if (nextfloor >= 0) {
                                if (currentFloor > nextfloor) {
                                    while (currentFloor > nextfloor) {
                                        if (currentFloor<=2)
                                        {
                                            currentFloor--;
                                        }
                                        else
                                        {
                                            currentFloor = currentFloor-2;
                                        }
                                        Evenbestelevator.setCurrentFloor(currentFloor);
                                    }
                                } else {
                                    while (currentFloor < nextfloor) {
                                        if (currentFloor<2)
                                        {
                                            currentFloor++;
                                        }
                                        else
                                        {
                                            currentFloor = currentFloor+2;
                                        }
                                        Evenbestelevator.setCurrentFloor(currentFloor);
                                    }
                                }
                                System.out.println(Evenbestelevator.getRequestProcessorThread().getName() + " Welcome to Floor : " + Evenbestelevator.getCurrentFloor());
                                Evenbestelevator.requestSet.remove(Evenbestelevator.getCurrentFloor());
                            }
                        } catch (InterruptedException e) {
                            if (Evenbestelevator.getCurrentFloor() != nextfloor) {
                                Evenbestelevator.getRequestSet().add(nextfloor);
                            }
                        }
                    }
                }
            }
        }
    }
}

class RequestListener implements Runnable
{
    @Override
    public synchronized void run()
    {
        while (true)
        {
            String floorNumberStr = null;
            try
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                floorNumberStr = bufferedReader.readLine();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (isValidFloorNumber(floorNumberStr))
            {
                System.out.println("User Pressed : " + floorNumberStr);

                ArrayList<AllElevator> AllTypeElevatorInstance;
                ArrayList<OddElevator> OddTypeElevatorInstance;
                ArrayList<EvenElevator> EvenTypeElevatorInstance;

                AllTypeElevatorInstance = Elevator.getAllElevator();
                OddTypeElevatorInstance = Elevator.getOddElevator();
                EvenTypeElevatorInstance = Elevator.getEvenElevator();

                OddElevator Oddbestelevator = null;
                EvenElevator Evenbestelevator = null;
                AllElevator Allbestelevator = null;

                for(int i=0;i<OddTypeElevatorInstance.size();i++)
                {
                    if (OddTypeElevatorInstance.get(i).getRequestProcessorThread().getState() != Thread.State.WAITING) {
                        Oddbestelevator = OddTypeElevatorInstance.get(i);
                    }
                }

                for(int i=0;i<EvenTypeElevatorInstance.size();i++)
                {
                    if (EvenTypeElevatorInstance.get(i).getRequestProcessorThread().getState() != Thread.State.WAITING) {
                        Evenbestelevator = EvenTypeElevatorInstance.get(i);
                    }
                }

                if(Oddbestelevator==null && Evenbestelevator==null)
                {
                    for(int i=0;i<AllTypeElevatorInstance.size();i++)
                    {
                        if (AllTypeElevatorInstance.get(i).getRequestProcessorThread().getState() != Thread.State.WAITING) {
                            Allbestelevator = AllTypeElevatorInstance.get(i);
                        }
                    }
                }

                if(Allbestelevator==null && Oddbestelevator==null && Evenbestelevator==null)
                {
                    Allbestelevator = AllTypeElevatorInstance.get(0);
                }

                Elevator elevator = Elevator.getInstance();

                if(Allbestelevator!=null)
                {
                    elevator.addFloor(Integer.parseInt(floorNumberStr),Allbestelevator.getRequestProcessorThread());
                    Allbestelevator.getRequestProcessorThread().interrupt();
                }
                else
                {
                    if(Oddbestelevator!=null)
                    {
                        elevator.addFloor(Integer.parseInt(floorNumberStr),Oddbestelevator.getRequestProcessorThread());
                        Oddbestelevator.getRequestProcessorThread().interrupt();
                    }
                    else
                    {
                        elevator.addFloor(Integer.parseInt(floorNumberStr),Evenbestelevator.getRequestProcessorThread());
                        Evenbestelevator.getRequestProcessorThread().interrupt();
                    }
                }
            }
            else
            {
                System.out.println("Floor Request Invalid : " + floorNumberStr);
            }
        }
    }

    private boolean isValidFloorNumber(String s) {
        return (s != null) && s.matches("\\d{1,2}");
    }
}

enum Direction {
    UP, DOWN
}

enum Type {
    ALL, ODD , EVEN
}


interface ElevatorOperation
{
    public Thread getRequestProcessorThread();
    public int getCurrentFloor();
    public Type getType();
    public Direction getDirection();
    public TreeSet getRequestSet();
    public void setRequestSet(int floor);
    public void setDirection(Direction direction);
    public void setCurrentFloor(int currentFloor) throws InterruptedException;
    public int nextFloor();
}
