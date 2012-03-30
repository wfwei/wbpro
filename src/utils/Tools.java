package utils;

import java.util.Random;

public class Tools {

	public static int myrand(int small,int big){
		int r = 0;
		Random rand = new Random();
		return rand.nextInt(big-small)+small;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
