import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
    private static ArrayList<AllElevator> ElevatorInstance;
    private int currentFloor = 0;
    Thread requestProcessorThread;

    protected Elevator()
    {
        Thread requestListenerThread = new Thread(new RequestListener(),"RequestListenerThread");

        Thread requestProcessorThread1 = new Thread(new RequestProcessor(),"Lift1");
        Thread requestProcessorThread2 = new Thread(new RequestProcessor(),"Lift2");
        Thread requestProcessorThread3 = new Thread(new RequestProcessor(),"Lift3");
        Thread requestProcessorThread4 = new Thread(new RequestProcessor(),"Lift4");

        AllElevator elevator1 = new AllElevator();
        AllElevator elevator2 = new AllElevator();
        OddElevator elevator3 = new OddElevator();
        EvenElevator elevator4 = new EvenElevator();

        ElevatorInstance.add(elevator1);
        ElevatorInstance.add(elevator2);

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


    public synchronized void addFloor(int floor)
    {
        AllElevator elevatoriterator;
        AllElevator bestelevator = null;
        int floordifference = 100;

        for(int i=0;i<Elevator.ElevatorInstance.size();i++)
        {
            int currentfloor=0;
            elevatoriterator = Elevator.ElevatorInstance.get(i);
            currentfloor = elevatoriterator.getCurrentFloor();
            if(floor > currentfloor && elevatoriterator.getDirection() == Direction.UP && ( (floor%2==0? Type.EVEN : Type.ODD) == elevatoriterator.getType() ) )
            {
                int tempfloordifference = floor-currentfloor;
                if(tempfloordifference < floordifference)
                {
                    floordifference = tempfloordifference;
                    bestelevator = elevatoriterator;
                }
            }
            else if (floor < currentfloor && elevatoriterator.getDirection() == Direction.DOWN && ( (floor%2==0? Type.EVEN : Type.ODD) == elevatoriterator.getType() ))
            {
                int tempfloordifference = currentfloor-floor;
                if(tempfloordifference < floordifference)
                {
                    floordifference = tempfloordifference;
                    bestelevator = elevatoriterator;
                }
            }
            else if( (elevatoriterator.getRequestProcessorThread().getState() == Thread.State.WAITING) && ( (floor%2==0? Type.EVEN : Type.ODD) == elevatoriterator.getType() ))
            {
                int tempfloordifference = abs(currentfloor-floor);
                if(tempfloordifference < floordifference)
                {
                    floordifference = tempfloordifference;
                    bestelevator = elevatoriterator;
                }
            }
        }

        if(bestelevator==null)
        {
            for(int i=0;i<Elevator.ElevatorInstance.size();i++)
            {
                elevatoriterator = Elevator.ElevatorInstance.get(i);
                if(elevatoriterator.getRequestProcessorThread().getState() == Thread.State.WAITING)
                {
                    bestelevator = elevatoriterator;
                }
            }
        }

        System.out.println(bestelevator.getRequestProcessorThread().getName()+ " is best Elevator");

        if(requestProcessorThread.getState() == Thread.State.WAITING){
            notify();
        }else{
            requestProcessorThread.interrupt();
        }

    }
}

class AllElevator extends Elevator implements ElevatorOperation
{

    @Override
    public Thread getRequestProcessorThread()
    {
        return null;
    }

    @Override
    public int getCurrentFloor() {
        return 0;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Direction getDirection() {
        return null;
    }


}

class OddElevator extends Elevator implements ElevatorOperation
{
    @Override
    public Thread getRequestProcessorThread()
    {
        return null;
    }

    @Override
    public int getCurrentFloor() {
        return 0;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Direction getDirection() {
        return null;
    }


}

class EvenElevator extends Elevator implements ElevatorOperation
{
    @Override
    public Thread getRequestProcessorThread()
    {
        return null;
    }

    @Override
    public int getCurrentFloor() {
        return 0;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Direction getDirection() {
        return null;
    }


}


class RequestProcessor implements Runnable
{
    @Override
    public void run()
    {
        while (true)
        {
            System.out.println("hi");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
            System.out.println("hi");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
}
