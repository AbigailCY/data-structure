public class TriangleDrawer{
    public static void main(String[] args) {
//        int SIZE = Integer.parseInt(args[0]);

        int col = 0;
        int row = 0;
        int SIZE = 10;

        while (row < SIZE) {


            while (col <= row) {

                System.out.print('*');
                col = col + 1;
            }
            System.out.println();
            col = 0;
            row = row + 1;
      }

    }
}
