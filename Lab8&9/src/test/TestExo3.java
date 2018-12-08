package test;
import message.HelloMessage;

public class TestExo3 {

    public static void runTest(){
        String[] test = new String[17];

        test[0] = "Hello;Bob";
        test[1] = "Bye;Bob;42;60;0";
        test[2] = "HELLO;Bob;42;60;0";
        test[3] = "Hello;9Bob;42;60;0";
        test[4] = "HELLO;B_b;42;60;0";
        test[5] = "HELLO;Bob;hey;60;0";
        test[6] = "HELLO;Bob;-1;60;0";
        test[7] = "HELLO;Bob;42;nope;0";
        test[8] = "HELLO;Bob;42;-1;0";
        test[9] = "HELLO;Bob;42;666;0";
        test[10] = "HELLO;Bob;42;60;fail";
        test[11] = "HELLO;Bob;42;60;-1";
        test[12] = "HELLO;Bob;42;60;1337";
        test[13] = "HELLO;Bob;42;60;2;Flavien;Le;Bg";
        test[14] = "HELLO;Bob;42;60;3;Antoine;Encore;Plus_";
        test[15] = "HELLO;Bob;42;60;3;Pan;Jou;Reuh";

        int max = 300;
        test[16] = "HELLO;Bob;42;60;"+max;
        for (int i =0; i<300; i++){
            test[16] += ";" + "peer" + i;
        }


        HelloMessage messager;

        for (String message : test) {
            try{
                System.out.println("Test for: '" + message + "'...");

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                messager = new HelloMessage(message);
                System.out.println(messager.toString());
                System.out.println();
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
