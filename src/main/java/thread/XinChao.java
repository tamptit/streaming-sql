package thread;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@RequiredArgsConstructor
public class XinChao extends Thread{

    private AtomicBoolean flagActive;
    private AtomicInteger timeCount;
    private String nameInput;
    long timeStart;

    public XinChao(AtomicBoolean flag, String nameInput) {
        this.flagActive = flag;
        this.nameInput = nameInput;
    }

    public void run(){
        while(true){
//            if (timeCount.get() > 0 && timeCount.get() % 2 == 0){
            if (flagActive.get()){
                Scanner sc = new Scanner(System.in);
                nameInput = sc.nextLine();
                flagActive.set(false);
//                System.out.println("xin chao VietNam ");
//                try {
//                    Thread.sleep(1000L);
//                    timeCount.getAndAdd(1);
//                    flagActive.set(false);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

}
