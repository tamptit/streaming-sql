package thread;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
@Setter
public class Hello extends Thread{

    private AtomicBoolean flagActive;
    private AtomicInteger timeCount;
    int i = 0;

    private XinChao xc;

    public Hello() {
    }

    public Hello(XinChao xc, AtomicBoolean flag, AtomicInteger timeCount) {
        this.flagActive = flag;
        this.xc = xc;
        this.timeCount = timeCount;
    }

    public Hello(AtomicInteger timeStart) {
        this.timeCount = timeStart;
    }

    @Override
    public void run() {
        xc.setTimeCount(this.timeCount);
        xc.start();
        flagActive = xc.getFlagActive();
        while(true){
//            if (timeCount.get() == 0 || timeCount.get() % 2 != 0){
            if (!flagActive.get()){
                System.out.println("hello ban Nam ");
                try {
                    Thread.sleep(500L);
                    timeCount.getAndAdd(1);
                    flagActive.set(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }



}
