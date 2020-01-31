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
        Elevator.getInstance();
    }
}

class Elevator
{
    private static Elevator elevator = null;
    private static ArrayList<AllElevator> AllTypeElevatorInstance;
    private static ArrayList<OddElevator> OddTypeElevatorInstance;
    private static ArrayList<EvenElevator> EvenTypeElevatorInstance;
    private int currentFloor = 0;
    Thread requestProcessorThread;

    protected Elevator()
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

class AllElevator extends Elevator implements ElevatorOperation
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

        Thread.sleep(500);
    }


}

class OddElevator extends Elevator implements ElevatorOperation
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

        Thread.sleep(500);
    }



}

class EvenElevator extends Elevator implements ElevatorOperation
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

        Thread.sleep(500);
    }



}


class RequestProcessor implements Runnable
{

    @Override
    public void run()
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

            Elevator elevator = new Elevator();

            if(Allbestelevator!=null)
            {
//                int nextfloor = Allbestelevator.nextFloor();
                int nextfloor = 0;
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
//                    System.out.println("added "+nextfloor);
                        Allbestelevator.getRequestSet().add(nextfloor);
                    }
                }
            }
            else
            {
                if(Oddbestelevator!=null)
                {

                }
                else
                {

                }
            }
        }
    }
}

class RequestListener implements Runnable
{
    @Override
    public void run()
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

                Elevator elevator = new Elevator();

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
}
