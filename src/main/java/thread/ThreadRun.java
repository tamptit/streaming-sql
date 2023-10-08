package thread;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadRun {

    public static void main(String[] args) throws InterruptedException {

        AtomicBoolean activeFlag = new AtomicBoolean();// default false
        AtomicInteger timeCount =  new AtomicInteger();
        String nameImput="";
        ThreadRun tr = new ThreadRun();
        long timeCurrent = System.currentTimeMillis();
        XinChao xc = new XinChao(activeFlag, nameImput);
//        Hello h = new Hello(xc, roundRobinThread, timeCount);
//        h.start();
//        Hello h2 = new Hello(xc, roundRobinThread, timeCount);
//        h2.start();
        Greeting gt = new Greeting(activeFlag, nameImput);
        gt.start();
        xc.start();
    }

    public synchronized void runXinChao(XinChao xc) throws InterruptedException {
        System.out.println("XinChao waiting ");
        wait();
        System.out.println("XinChao ... ");
    }

    public synchronized void runHello(Thread h) throws InterruptedException {
        System.out.println("Hi ...!! ");
        notify();
    }
}
