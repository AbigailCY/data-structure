import java.util.*;

public class AddingMachine {



	public static void main (String[] args) {


		Scanner scanner = new Scanner(System.in);
		boolean isPreviousZero = false;
		int total = 0;
		int subtotal = 0;
		int input;
		int MAXIMUM_NUMBER_OF_INPUTS = 100;
        int[] listOfInputs = new int[MAXIMUM_NUMBER_OF_INPUTS];
        int index = 0;


        while (true) {
			input = scanner.nextInt();
			listOfInputs[index] = input;

			if (input == 0) {
				if (isPreviousZero) {
					System.out.println("total " + total);

					for (int a = 0; a <= index; a++) {
						if (listOfInputs[a] == 0){
							continue;
						}
						System.out.println(listOfInputs[a]);
					}
					return;

				} else {

					System.out.println("subtotal " + subtotal);
					total += subtotal;
					subtotal = 0;
					isPreviousZero = true;
				}
			}
			subtotal += input;
			index ++;
			if (input != 0) {
				isPreviousZero = false;
			}

		}
	}

}
