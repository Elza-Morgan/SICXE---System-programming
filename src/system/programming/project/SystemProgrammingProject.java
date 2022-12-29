/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.programming.project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SystemProgrammingProject {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        ArrayList<String> Labels = new ArrayList<>();
        ArrayList<String> References = new ArrayList<>();
        ArrayList<String> Instructions = new ArrayList<>();
        ArrayList<String> LocationCounter = new ArrayList<>();
        ArrayList<String> ObjectCode = new ArrayList<>();
        ArrayList<String> HTR = new ArrayList<>();

        FileReader read = new FileReader("C:\\Users\\zizoj\\OneDrive\\Desktop\\SICXE- System programming\\inSICXE.txt");

        BufferedReader buffer = new BufferedReader(read);
        String lineFromFile;
        boolean checkLastElement = false;
        while ((lineFromFile = buffer.readLine()) != null) {
            lineFromFile = lineFromFile.trim();
            String[] arr = lineFromFile.split("\\s+");
            for (int i = 0; i < arr.length; i++) {
                if (arr[0].equalsIgnoreCase("end")) {
                    checkLastElement = true;
                    break;
                }
            }
            if (!checkLastElement) {
                switch (arr.length) {
                    case 3:
                        Labels.add(arr[0]);
                        Instructions.add(arr[1]);
                        References.add(arr[2]);
                        break;
                    case 2:
                        Labels.add("#");
                        Instructions.add(arr[0]);
                        References.add(arr[1]);
                        break;
                    case 1:
                        Labels.add("#");
                        Instructions.add(arr[0]);
                        References.add(" ");
                        break;
                    default:
                        break;
                }
            } else {
                Labels.add("#");
                Instructions.add(arr[0]);
                References.add(arr[1]);
            }
        }

        /*These two are added compulsry because first excution always takes
        the first given address*/
        LocationCounter.add(0, ("0000" + References.get(0)).substring(References.get(0).length()));
        LocationCounter.add(1, ("0000" + References.get(0)).substring(References.get(0).length()));

        /*This main address is used to hold the address in order to do it function
        depend on directives*/
        String mainAddress = LocationCounter.get(0).trim();
        converter.initialize();
        for (int i = 2, j = 1; i < Instructions.size(); i++, j++) {
            int address = 0;
            if (Instructions.get(j).equalsIgnoreCase("RESW")) {
                address = Integer.parseInt(References.get(j).trim());
                address = address * 3;

                int temp = Integer.parseInt(mainAddress.trim(), 16);

                temp = temp + address;
                mainAddress = Integer.toHexString(temp).trim();
                LocationCounter.add(i, ("0000" + mainAddress).substring(mainAddress.length()));
            } else if (Instructions.get(j).equalsIgnoreCase("BYTE")) {
                String byyte = References.get(j);

                if (byyte.charAt(0) == 'C') {
                    int CharSize = 0;
                    CharSize = byyte.length() - 3;
                    int temp = Integer.parseInt(mainAddress.trim(), 16);

                    temp = temp + CharSize;

                    mainAddress = Integer.toHexString(temp).trim();
                    LocationCounter.add(i, ("0000" + mainAddress).substring(mainAddress.length()));
                } else if (byyte.charAt(0) == 'X') {
                    int HexaSize = 0;
                    HexaSize = (byyte.length() - 3) / 2;

                    int temp = Integer.parseInt(mainAddress.trim(), 16);

                    temp = temp + HexaSize;

                    mainAddress = Integer.toHexString(temp).trim();
                    LocationCounter.add(i, ("0000" + mainAddress).substring(mainAddress.length()));
                }
            } else if (Instructions.get(j).equalsIgnoreCase("RESB")) {
                String byyte = References.get(j).trim();
                int byyte1 = Integer.parseInt(byyte);

                int temp = Integer.parseInt(mainAddress.trim(), 16);

                temp = temp + byyte1;
                mainAddress = Integer.toHexString(temp).trim();
                LocationCounter.add(i, ("0000" + mainAddress).substring(mainAddress.length()));

            } else if (Instructions.get(j).equalsIgnoreCase("WORD")) {
                String word = References.get(j).trim();
                String arr[] = word.split(",");

                int wordSize = arr.length * 3;
                int temp = Integer.parseInt(mainAddress.trim(), 16);
                temp = temp + wordSize;
                mainAddress = Integer.toHexString(temp).trim();
                LocationCounter.add(i, ("0000" + mainAddress).substring(mainAddress.length()));

            } else {

                //for searching the whole table in class converter
                for (int z = 0; z < converter.OPTAB.length; z++) {

                    if (Instructions.get(j).trim().equalsIgnoreCase("BASE")) {
                        LocationCounter.add(i, ("0000" + mainAddress).substring(mainAddress.length()));
                        break;
                    } else if (Instructions.get(j).trim().equalsIgnoreCase(converter.OPTAB[z][0])) {
                        int opcode = Integer.parseInt(converter.OPTAB[z][1]);
                        int temp = Integer.parseInt(mainAddress.trim(), 16);
                        temp = temp + opcode;
                        mainAddress = Integer.toHexString(temp).trim();
                        LocationCounter.add(i, ("0000" + mainAddress).substring(mainAddress.length()));
                        break;
                    } else if (Instructions.get(j).trim().charAt(0) == '+') {
                        int temp = Integer.parseInt(mainAddress.trim(), 16);
                        temp = temp + 4;
                        mainAddress = Integer.toHexString(temp).trim();
                        LocationCounter.add(i, ("0000" + mainAddress).substring(mainAddress.length()));
                        break;
                    }
                }
            }
        }

        /*This function is used to make the object code*/
        ObjectCode.add(0, " ");
        converter.initialize();
        String Registers[] = new String[]{"A", "X", "L", "B", "S", "T", "F"};
        for (int i = 1; i < Instructions.size(); i++) {
            String concat = "";
            String opcode = "";
            String address = "";
            String tempInstruction = Instructions.get(i).trim();
            String FlagBits[] = new String[]{"0", "0", "0", "0", "0", "0"};
            String binaryOpcode = "";
            boolean checkFormat4 = false;
            String format = "";
            boolean check = false;
            boolean checkRSUB = false;
            String arr[] = References.get(i).split(",");
            
            
            if (tempInstruction.charAt(0) == '+') {
                tempInstruction = tempInstruction.substring(1);
                checkFormat4 = true;
            }

            /*j counter to search in list*/
            for (int j = 0; j < converter.OPTAB.length; j++) {
                if (tempInstruction.equalsIgnoreCase(converter.OPTAB[j][0])) {
                    opcode = converter.OPTAB[j][2];
                    format = converter.OPTAB[j][1];
                    if (converter.OPTAB[j][0].equalsIgnoreCase("RSUB")) {
                        FlagBits[0] = "1";
                        FlagBits[1] = "1";
                        String str = Integer.toBinaryString(Integer.parseInt(opcode, 16));
                        str = ("00000000" + str).substring(str.length());
                        str = str.substring(0, str.length() - 2);
                        str = str + String.join("", FlagBits);
                        int bin = Integer.parseInt(str, 2);
                        opcode = Integer.toString(bin, 16) + "000";
                        checkRSUB = true;
                    }
                    check = true;
                    break;
                }
            }

            if (check) {
                if (!checkRSUB) {
                    if (checkFormat4 || format.equals("3")) {
                        //opcode 6bits nixbpe addr/disp
                        String binary = Integer.toBinaryString(Integer.parseInt(opcode, 16));
                        binaryOpcode = ("00000000" + binary).substring(binary.length());
                        binaryOpcode = binaryOpcode.substring(0, binaryOpcode.length() - 2);
                        if (checkFormat4) {
                            FlagBits[5] = "1";
                        }
                        if (References.get(i).charAt(0) == '#') {
                            FlagBits[0] = "0";
                            FlagBits[1] = "1";
                        } else if (References.get(i).charAt(0) == '@') {
                            FlagBits[0] = "1";
                            FlagBits[1] = "0";
                        } else {
                            FlagBits[0] = "1";
                            FlagBits[1] = "1";
                        }

                        for (int j = 1; j < Labels.size(); j++) {
                            
                            if (arr[0].charAt(0) == '#'){
                                arr[0] = arr[0].substring(1);
                                boolean found = false;
                                for (int y = 0; y < Labels.size(); y++) {
                                    if (arr[0].equalsIgnoreCase(Labels.get(y))) {
//                                        int tempAddress = Integer.parseInt(LocationCounter.get(y), 16) - Integer.parseInt(LocationCounter.get(i + 1), 16);
//                                        if (checkPC(tempAddress)) {
//                                            address = Integer.toHexString(tempAddress);
//                                            address = ("000" + address).substring(address.length());
//                                            FlagBits[4] = "1";
//                                        } else {
//                                                tempAddress = Integer.parseInt(LocationCounter.get(y), 16) - Integer.parseInt(getBaseLocation(Instructions, References, Labels, LocationCounter), 16);
//                                                if (tempAddress >= 0 && tempAddress <= 4095) {
//                                                    address = Integer.toHexString(tempAddress);
//                                                    address = ("000" + address).substring(address.length());
//                                                    FlagBits[3] = "1";
//                                                }  
//                                        }
                                        found = true;
                                        break;
                                    }
                                }
                                if (found == false) {
                                    address = Integer.toHexString(Integer.parseInt(arr[0]));
                                    if (checkFormat4) {
                                        address = ("00000" + address).substring(address.length());
                                    } else {
                                        address = ("000" + address).substring(address.length());
                                    }
                                }
                            }
                            else if (arr[0].charAt(0) == '@') {
                                arr[0] = arr[0].substring(1);
                            }
                            if(arr.length==2){
                                FlagBits[2]="1";
                            }
                            if (arr[0].equalsIgnoreCase(Labels.get(j).trim())){ 
                                if (checkFormat4 == false) {
                                    //displacment for format 3
                                    int tempAddress = Integer.parseInt(LocationCounter.get(j), 16) - Integer.parseInt(LocationCounter.get(i + 1), 16);
                                    if (tempAddress <= 2047 && tempAddress >= -2048) {
                                        address = Integer.toHexString(tempAddress);
                                        address = ("000" + address).substring(address.length());
                                        FlagBits[4] = "1";

                                    } else {
                                        int tempAddressBase = 0;
                                        tempAddressBase = Integer.parseInt(LocationCounter.get(j), 16) - Integer.parseInt(getBaseLocation(Instructions, References, Labels, LocationCounter), 16);
                                               if (tempAddressBase >= 0 && tempAddressBase <= 4095) {
                                                    address = Integer.toHexString(tempAddressBase);
                                                    address = ("000" + address).substring(address.length());
//                                                    System.out.println(address);
                                                    FlagBits[3] = "1";
                                                }    
                                        
                                    }
                                    //address for format 4
                                } else {
                                    for (int k = 1; k < Labels.size(); k++) {
                                        if (arr[0].equalsIgnoreCase(Labels.get(k))) {
                                            address = LocationCounter.get(k);
                                            address = ("00000" + address).substring(address.length());
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    } else if (format.equals("2") || format.equals("1")) {
                        if (format.equals("2")) {
                            boolean R1Done = false;
                            boolean R2Done = false;
                            String R1 = "";
                            String R2 = "";
                            for (int m = 0; m < Registers.length; m++) {
                                if (arr[0].trim().equalsIgnoreCase(Registers[m])) {
                                    R1 = String.valueOf(m);
                                    R1Done = true;
                                    address = address + R1;
                                    break;
                                }
                            }
                            if (!R1Done) {
                                address = address + 0;
                            }
                            if (arr.length == 2) {

                                for (int m = 0; m < Registers.length; m++) {
                                    if (arr[1].trim().equalsIgnoreCase(Registers[m])) {
                                        R2 = String.valueOf(m);
                                        R2Done = true;
                                        address = address + R2;
                                        break;
                                    }
                                }
                            }
                            if (!R2Done) {
                                address = address + 0;
                            }
                        }
                    }
                }
                String x = binaryOpcode + String.join("", FlagBits);
                x = Integer.toHexString(Integer.parseInt(x, 2));
                x = ("000" + x).substring(x.length());
                if (format.equals("2") || checkRSUB) {
                    concat = opcode + address;
                } else {
                    concat = x + address;
                }
                ObjectCode.add(i, concat);
            } else {
                if(Instructions.get(i).equalsIgnoreCase("BASE")){
                    ObjectCode.add(i," ");
                }else if (Instructions.get(i).equalsIgnoreCase("RESW")
                        || Instructions.get(i).equalsIgnoreCase("RESB")
                        || Instructions.get(i).equalsIgnoreCase("END")) {
                    ObjectCode.add(i, "NoObjectCode");
                } else if (Instructions.get(i).equalsIgnoreCase("WORD")) {
                    String word = References.get(i).trim();

                    String arr2[] = word.split(",");
                    for (int h = 0; h < arr2.length; h++) {
                        int temp = Integer.parseInt(arr2[h].trim());

                        String temp1 = Integer.toHexString(temp).trim();

                        arr2[h] = ("000000" + temp1).substring(temp1.length());
                    }
                    String concatTemp = "";
                    for (int n = 0; n < arr2.length; n++) {
                        concatTemp = concatTemp + arr2[n];
                    }
                    ObjectCode.add(i, concatTemp);

                } else if (Instructions.get(i).equalsIgnoreCase("BYTE")) {

                    String Byte = References.get(i);

                    if (Byte.charAt(0) == 'C') {
                        int number = 0;
                        String concatenation = "";
                        for (int h = 2; h < Byte.length() - 1; h++) {
                            number = Byte.charAt(h);
                            String temp = Integer.toHexString(number);
                            concatenation = concatenation + temp;
                        }
                        ObjectCode.add(i, concatenation);
                    }

                    if (Byte.charAt(0) == 'X') {
                        String concatenation = "";
                        for (int h = 2; h < Byte.length() - 1; h++) {

                            concatenation = concatenation + Byte.charAt(h);
                        }
                        ObjectCode.add(i, concatenation);
                    }
                }
            }
        }
        //for printing all of the table
        printAllTable(Labels, Instructions, References, LocationCounter, ObjectCode);

        //for printing the symbol table
//      printSymbolTable(LocationCounter, Labels);
        /*This is used to print the object code*/
//     for(int i=0;i<ObjectCode.size();i++){
//          System.out.println(ObjectCode.get(i));
//        }
        /*HTR after the object code*/
        hteRecord(HTR,LocationCounter,Instructions,Labels,ObjectCode,References);
        //references and objectcode
//           printReferenceAndObject(References, ObjectCode);
    }

    public static boolean checkPC(int tempAddress) {
        if (tempAddress <= 2047 && tempAddress >= -2048) {
            return true;
        } else {
            return false;
        }
    }

    public static String getBaseLocation(ArrayList<String> Instructions,
            ArrayList<String> References, ArrayList<String> Labels,ArrayList<String> LocationCounter) {
       
        String location="";
        for (int k = 0; k < Instructions.size(); k++) {
            if (Instructions.get(k).equalsIgnoreCase("BASE")) {
                for (int k1 = 0; k1 < Labels.size(); k1++) {
                    if (References.get(k).equalsIgnoreCase(Labels.get(k1))) {
                        
                            location = LocationCounter.get(k1);
                            
                    }
                }
            }
        }
        return location;
    }

    /*This is used to print the orginal table from the file a long with the 
    lables*/
    public static void printAllTable(ArrayList<String> Labels, ArrayList<String> Instructions,
            ArrayList<String> References, ArrayList<String> LocationCounter, ArrayList<String> ObjectCode) {
        /*THis prints the subheading of the table*/
        System.out.format("%-20s%-15s%-15s%-15s%s%n", "Location Counter", "Labels", "Instructions", "References", "ObjectCode");

        for (int i = 0; i < Labels.size(); i++) {
            System.out.format("%-20s%-15s%-15s%-15s%s%n",
                    LocationCounter.get(i).trim(), Labels.get(i),
                    Instructions.get(i), References.get(i), ObjectCode.get(i));
        }
    }

    /*this function is used to print the symbol table*/
    public static void printSymbolTable(ArrayList<String> LocationCounter,
            ArrayList<String> Labels) {
        System.out.format("%-30s%-24s%n", "Labels", "Location Counter");

        for (int i = 0; i < LocationCounter.size(); i++) {
            if (Labels.get(i).trim().charAt(0) != '#') {
                System.out.printf("%-30s%-24s%n", Labels.get(i), LocationCounter.get(i).trim());
            }
        }
    }

    public static void printReferenceAndObject(ArrayList<String> References,
            ArrayList<String> ObjectCode){
        ObjectCode.add(18, " ");
        System.out.format("%-30s%-24s%n", "Reference", "Object Code");

        for (int i = 0; i < References.size(); i++){

            System.out.format("%-30s%-24s%n", References.get(i).trim(), ObjectCode.get(i));
        }
    }

    public static String padding(String input, int length) {
        String output = "000000" + input;
        return output.substring(output.length() - length).toUpperCase();
    }

    public static String getLength(String firstLoc, String lastLoc){
        int decFirstloc = Integer.parseInt(firstLoc.trim(), 16);
        int decLastloc = Integer.parseInt(lastLoc.trim(), 16);
        int decProgramLength = decLastloc - decFirstloc;
        String hexProgramLength = Integer.toHexString(decProgramLength);
        return hexProgramLength;
    }

    public static void hteRecord(ArrayList<String> HTR, ArrayList<String> LocationCounter, ArrayList<String> Instructions,
             ArrayList<String> Labels, ArrayList<String> ObjectCode, ArrayList<String> References) {
        HTR.add(0, "H " + Labels.get(0).trim() + " " + padding(References.get(0).trim(), 6) + " " + padding(getLength(LocationCounter.get(0).trim(), LocationCounter.get(LocationCounter.size() - 1)), 6));
        int j = 1;
        String T = "";
        String temp = "";
        int cnt = 0;
        for (int i = 1; i < LocationCounter.size(); i++) {
            if ((ObjectCode.get(i).equals("NoObjectCode") && cnt > 0) || cnt == 10 || Instructions.get(i).equalsIgnoreCase("END")) {

                T += " " + padding(getLength(T.substring(2), LocationCounter.get(i).trim()), 2) + temp;

                HTR.add(j, T);
                j++;
                T = temp = "";
                cnt = 0;
            }
            if (!ObjectCode.get(i).equals("NoObjectCode")) {
                if(!ObjectCode.get(i).equals(" ")){
                
                
                if (cnt == 0) {
                    T = "T " + padding(LocationCounter.get(i).trim(), 6);
                }
                if (cnt < 10) {
                    temp += " " + ObjectCode.get(i);
                    cnt++;
                }
            }else{
                    cnt++;
                }
            }
        }
        
        String M ="";
                for(int i = 1 ;i<Instructions.size();i++){
                    if(Instructions.get(i).charAt(0)=='+' && References.get(i).charAt(0)!='#'){
                        M ="M " +  ("000000" + Integer.toHexString((Integer.parseInt(LocationCounter.get(i),16)+1))).substring(Integer.toHexString((Integer.parseInt(LocationCounter.get(i),16)+1)).length())
                                +" 05 " + Labels.get(0);
                        HTR.add(j,M);
                        j++;
                    }
                }
        HTR.add(j, "E" + " " + padding(LocationCounter.get(0), 6));
        for (int h = 0; h < HTR.size(); h++) {
            System.out.println(HTR.get(h));
        }
    }
}
