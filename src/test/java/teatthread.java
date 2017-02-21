/**
 * Created by Administrator on 2017/2/9.
 */
public class teatthread {
    public static void main(String[] args) {
        System.out.println("idd1:"+Thread.currentThread().getId());
//        Thread a=new Thread(new Runnable() {
//            public void run() {
//                System.out.println("idd2:"+Thread.currentThread().getId());
//                new Thread(new Runnable() {
//                    public void run() {
//                        System.out.println("idd3:"+Thread.currentThread().getId());
//                    }
//                }).run();
//            }
//        });
//        a.run();
//
//
//        new Thread(new Runnable() {
//            public void run() {
//                System.out.println("idd5:"+Thread.currentThread().getId());
//            }
//        }).run();
//
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("idd4:"+Thread.currentThread().getId());
        teatthread tt=new teatthread();
        tt.testrun();
    }
    Thread a;
    Thread b;

    public void testrun(){
         a=new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(a.isDaemon());
                System.out.println(a.isAlive());
                System.out.println(a.getState());
                System.out.println("hi"+Thread.currentThread().getId());
                long iid=a.getId();
                System.out.println(iid);


            }
        });
         b=new Thread();
        for(int i=0;i<20;i++){
//            a.run();
            a.start();
        }

        b.run();
        System.out.println(a.getId());
        System.out.println(b.getId());
        System.out.println(Thread.currentThread().getId());
    }
}
