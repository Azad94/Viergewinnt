//package experiment2;


import java.util.Arrays;


/**
 *
 * @author hailm, hanemann
 */
public class Tools {

     /**
      * convert a long into a byte array
      * @param long the long value to be converted 
      */
     public static byte[] longToBytes(long val) {

        byte[] b = new byte[8];
		
		for (int i = 0, j = 56; i < 8; i++, j -= 8)
			b[i] = (byte) (val >> j);

		return b;
    }

    /**
     * convert a byte array into a long
     * @param byte[] the byte array to be converted
     */
    public static long bytesToLong( byte[] b ){
	
		long ret = 0;
		
		for (int i = 0, j = 56; i < 8; i++, j -= 8)
			ret += ((long)(b[i] & 0xFF) << j);

        return ret;
    }

    /**
     * convent an int to a byte array
     * @param int the int to be converted
     */
    public static byte[] intToBytes(int val) {

        byte[] b = new byte[4];
		
		for (int i = 0, j = 24; i < 4; i++, j -= 8)
			b[i] = (byte) (val >> j);

		return b;
    }

    /**
     * convert a byte array to an int, works for up to a length of 4
     * @param byte[] the byte array to be converted
     */
    public static int bytesToInt( byte[] b ){
        
		int ret = 0;
		
		for (int i = 0, j = 24; i < 4; i++, j -= 8) {
			if (b.length >= i+1) { //should work also if length is smaller than 4
                          ret += ((int)(b[i] & 0xFF) << j);
                        }
                }
			
        return ret;
    }

    /**
     * concatenate byte arrays to a single byte array
     * @param byte[]... the byte arrays to be concatenated
     */
    public static byte[] concatByteArrays(byte[]... byteArrays){

        int totalLength = 0;

        for (int i = 0; i < byteArrays.length; i++){
            totalLength += byteArrays[i].length;
        }

        byte b[] = new byte[totalLength];

        int insertPos = 0;
        for (int i = 0; i < byteArrays.length; i++){
            System.arraycopy(byteArrays[i], 0, b, insertPos, byteArrays[i].length);
            insertPos += byteArrays[i].length;
        }

        return b;
    }

    /**
     * get a part of a byte array from the start with a certain number of bytes
     * @param int the number of bytes to be kept
     * @param byte[] the byte array from which a subset should be returned
     */
    static byte[] subBytes(int number, byte[] source){
        return subBytes(0, number, source);
    }

    /**
     * get a part of a byte array from the byte number "start" with a certain number of bytes
     * @param int the position of the first byte to be returned
     * @param int the number of bytes to be returned
     * @param byte[] the byte array from which a subset should be returned
     */
    static byte[] subBytes(int start, int number, byte[] source){

        byte[] b = new byte[number];
        
        System.arraycopy(source, start, b, 0, number);
        
        return b;
        
    }
    
    /** 
     * convert a byte array to a String to show 0s and 1s in it 
     * @param byte[] the byte array to be shown
     */
    static String showBits(byte[] byteArray) {
      char[] charArray = new char[byteArray.length * 9];
      int k = 1;
      for (int i=0; i < byteArray.length; i++) {
         charArray[i * 9] = ' ';
        k = 1;
        for (int j=1; j < 9; j++) {
          if ((byteArray[i] & k) == 0) {
            charArray[i * 9 + j] = '0';
          } else {
            charArray[i * 9 + j] = '1'; 
          }
          k *= 2;    
        }
      }
      return new String(charArray);
    } 


    /**
     * compare two byte arrays and return the number of bit errors
     * @param byte[] the one byte array to be compared
     * @param byte[] the other byte array to be compared
     */
    static int countBitErrorsInByteArray(byte[] original, byte[] received){
      int totalBitErrors = 0;
      if (original.length != received.length) return -1; //cannot compare to byte arrays with different length
      for (int i=0; i < original.length; i++) {
        totalBitErrors += countBitErrorsInByte(original[i],received[i]);
      }
      return totalBitErrors;
    }

    /**
     * compare two bytes and return the number of bit errors
     * @param byte the one byte to be compared
     * @param byte the other byte to be compared
     */
    static int countBitErrorsInByte(byte original, byte received){
           
       int bitErrorCount = 0;
       for (int i=0; i < 8; i++) {
         if ((original & 1) != (received & 1)) bitErrorCount++;
         original >>= 1;
         received >>= 1;
       }
       return bitErrorCount;  
    }

    /**
     * private function that calculates a byte with parities to eight input bytes
     * @param byte[] a byte array with eight bytes
     */
    static private byte calculateParityByte(byte[] original, int offset) {
      if (original.length != 8) {
        System.err.println ("Funktion calculateParityByte erwartet ein Byte Array mit 8 Bytes als Eingabe");
        System.exit(0);
      }
      byte parity = 0;
      int factor = 1;
      for (int i=0; i < 8; i++){
        int countOnes = 0;
        for (int j=1; j < 256;j *= 2){ //calculate number of ones in byte i
          if ((original[offset + i] & j) != 0) countOnes++;
        }
        parity += (countOnes % 2) * factor; //add parity bit for byte i at position i
        factor *= 2;
      }
      return parity;
    }
    
    /**
     * adds additional bytes containing parities, adds additional dummy bytes if required
     * @param byte[] byte array to which additional parity bytes will be added
     */
    static byte[] addParityBytes(byte[] array) {
      if (array.length % 8 != 0) {
        array = addDummyBytes(array, array.length + 8 - (array.length % 8), false);
      }
      byte[] parityByte = new byte[array.length/8];
      for (int i=0; i < parityByte.length; i++) {
        parityByte[i] = calculateParityByte(array, i*8);
      }      
      return concatByteArrays(array, parityByte);
    }

    /**
     * extend a byte array to a given length
     * @param byte[] original byte array
     * @param int new length that the byte array should have after the addition of the dummy bytes
     * @param boolean option whether the dummy bytes should be added to the left or to the right
     */
    static byte[] addDummyBytes(byte[] original, int newLength, boolean additionToLeft) {
      int numberDummyBytes = newLength - original.length;
      byte[] dummyByte = new byte[numberDummyBytes];
      for (int i=0; i < dummyByte.length; i++) {
        dummyByte[i] = 0;
      }
      if (additionToLeft) {
        return (concatByteArrays(dummyByte,original));
      } //addition to right
      return (concatByteArrays(original,dummyByte));
    }

    /**
     * add two duplicates of its contents to a byte array (enable a majority vote on the receiver side)
     * @param byte[] original byte array
     */
    static byte[] addTwoDuplicates(byte[] original) {
      byte[] triple = new byte[original.length * 3];
      for (int i=0; i < original.length; i++) {
        triple[i] = original[i];
        triple[original.length+i] = original[i];
        triple[2 * original.length+i] = original[i];     
      }
      return triple;
    } 

    /**
     * in array with redundancy (two duplicates of each byte) do a majority vote and
     * return array without redundancy
     * @param byte[] triple byte array
     */
    static byte[] doMajorityVote(byte[] triple) {
      if (triple.length % 3 != 0) {
        System.err.println ("Funktion doMajorityVote erwartet Byte Array mit durch drei teilbarer Anzahl Bytes.");
        System.exit(0);
      }
      byte[] reconstruct = new byte[triple.length / 3];
      for (int i=0; i < reconstruct.length; i++) {
	reconstruct[i] = 0;
        for (int j=1; j < 256; j *=2) { //culculate number of 1s at three locations for each position
            int countOnes = 0;
          if ((triple[i] & j) != 0)  countOnes++;
          if ((triple[i+ reconstruct.length] & j) != 0)  countOnes++;
          if ((triple[i+ reconstruct.length*2] & j) != 0)  countOnes++;
          if (countOnes >= 2) {
            reconstruct[i] += j;
          }
	}     
      }
      return reconstruct;
    }

    /**
     * in array with redundancy (two duplicates of each byte) do a majority vote and
     * count the number of corrected bit errors (estimate that correction is right)
     * @param byte[] triple byte array
     */
    static int countMajorityVote(byte[] triple) {
      if (triple.length % 3 != 0) {
        System.err.println ("Funktion countMajorityVote erwartet Byte Array mit durch drei teilbarer Anzahl Bytes.");
        System.exit(0);
      }
      int countCorrections = 0;
      int length = triple.length/3; 
      for (int i=0; i < length; i++) {
        for (int j=1; j < 256; j *=2) { //culculate number of 1s at three locations for each position       
	  int countOnes = 0;
          if ((triple[i] & j) != 0)  countOnes++;
          if ((triple[i+ length] & j) != 0)  countOnes++;
          if ((triple[i+ length*2] & j) != 0)  countOnes++;
          if ((countOnes == 1) || (countOnes == 2)) {
            countCorrections += 1;
          }
	}     
      }
      return countCorrections;
    }


    public static void main(String[] args){

        //example comparison of two Integers
        int sourceInt = Integer.parseInt(args[0]);
        int sinkInt = Integer.parseInt(args[1]);
        byte[] sourceByte = intToBytes(sourceInt);
        byte[] sinkByte = intToBytes(sinkInt);
        int totalBitErrors = 0;
        int bitErrorsInByte = 0;
        System.out.println("sourceInt als Bits dargestellt:" +  showBits(sourceByte));
        System.out.println("sinkInt als Bits dargestellt:  " + showBits(sinkByte));
	    System.out.println("Test der Conversionsfunktion: 1. Argument " + bytesToInt (sourceByte) + 
           ", 2. Argument "+ bytesToInt (sinkByte));
        System.out.println("Anzahl der Bitfehler:");
        for (int i=0; i < sourceByte.length; i++) {
          bitErrorsInByte = countBitErrorsInByte(sourceByte[i],sinkByte[i]); 
          totalBitErrors += bitErrorsInByte;
          System.out.println ("Bitfehler in Byte " + i + ": " + bitErrorsInByte);
        } 
        System.out.println ("Bitfehler insgesamt: " + totalBitErrors);
        System.out.println ("Bitfehler insgesamt (anderer Funktionsaufruf): " + countBitErrorsInByteArray(sourceByte,sinkByte));

        //example of an additional calculation of a parity byte
        byte[] test = new byte[1]; test[0] = (byte)5;
        byte[] parityCheckedBytes = new byte[8];
        parityCheckedBytes = addParityBytes(test);
        System.out.println ("Doppeltes 1. Argument mit Parity Byte: " + showBits(parityCheckedBytes));
        

        //test addition of redundancy
        byte[] withRedundancy = addTwoDuplicates(sourceByte);
        withRedundancy[7] = 3;
        withRedundancy[10] = 3;
        withRedundancy[11] = 3;
        System.out.println("Verdreifachung des 1. Arguments (Addition Bitfehler): " + showBits(withRedundancy));
        byte[] withoutRedundancy = doMajorityVote(withRedundancy);
	    System.out.println("Mehrheitsentscheid: " + showBits(withoutRedundancy));
        System.out.println("Korrekturen im Mehrheitsentscheid: " + countMajorityVote(withRedundancy));
    }
}
