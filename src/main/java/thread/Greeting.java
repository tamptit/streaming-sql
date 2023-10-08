package thread;

import java.util.concurrent.atomic.AtomicBoolean;

public class Greeting extends Thread{

    private AtomicBoolean flagActive;
    private String nameInput;

    public Greeting(AtomicBoolean flag, String nameInput) {
        this.flagActive = flag;
        this.nameInput = nameInput;
    }

    @Override
    public void run() {
        if (!flagActive.get()){
            System.out.println("Moi nhap ten: ");
            try {
                flagActive.set(true);
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true){
                System.out.println("Xin chao " + this.nameInput);
            }
        }
    }
}
