import javax.xml.crypto.Data;
import java.util.*;
import java.io.*;
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        Database db = new Database("src/db.csv");
        db.load();
        while(true) {
            System.out.print(">>");
            String input = sc.nextLine();
            if(input.equals("exit")) {
                break;
            }
            String[] command = input.split("\\s+", 2);
            switch (command[0]) {
                case "add":
                    if(command.length == 2) {
                        db.add(command[1], true);
                    }
                    else {
                        System.out.println("wrong field count");
                    }
                    break;
                case "print":
                    db.print(db.database);
                    break;
                case "del":
                    if(command.length == 2) {
                        db.del(command[1]);
                    }
                    else {
                        System.out.println("wrong id");
                    }
                    break;
                case "sort":
                    db.sort();
                    break;
                case "find":
                    if(command.length == 2) {
                        db.find(command[1]);
                    }
                    else {
                        System.out.println("wrong id");
                    }
                    break;
                case "avg":
                    db.avg();
                    break;
                case "edit":
                    if(command.length == 2) {
                        db.edit(command[1]);
                    }
                    else {
                        System.out.println("wrong field count");
                    }
                    break;
                default:
                    System.out.println("wrong command");
            }
        }
        //db.export();
        sc.close();
    }
}
class Database {
    private String spaces(int number) {
        String spaces = "";
        for(int i = 0; i < number; i++) {
            spaces += " ";
        }
        return  spaces;
    }
    //Visu ierakstu saraksts
    ArrayList<Record> database = new ArrayList<Record>();
    //Record top;
    private String fname;
    Database(String name) {
        this.fname = name;
    }
    void load() {
        FileReader fin;
        try {
            fin = new FileReader(fname);
            Scanner sc = new Scanner(fin);
            String s;
            while (sc.hasNextLine()) {
                s = sc.nextLine();
                this.add(s, false);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.print("error");
        }
    }
    void export() {
        FileWriter fout;
        PrintWriter out;
        try {
            fout = new FileWriter(fname);
            out = new PrintWriter(fout);
            for (int i = 0; i < database.size(); i++) {
                out.println(database.get(i).id + ";" + database.get(i).city + ";" + database.get(i).date + ";" + database.get(i).days + ";" + database.get(i).price + ";" + database.get(i).vehicle);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void add(String data, boolean isNew) {
        if(validate(data, true)) {
            if(isNew) {
                System.out.println("added");
            }
        }
    }
    void edit(String data) {
        if(validate(data, false)) {
            System.out.println("edited");
        }
    }
    void del(String data) {
        try {
            int id = Integer.parseInt(data);
            if(id > 99 && id < 1000) {
                boolean exists = false;
                int i = 0;
                while(i < database.size()) {
                    if(database.get(i).id == id) {
                        exists = true;
                        break;
                    }
                    i++;
                }
                if(exists) {
                    database.remove(i);
                    System.out.println("deleted");
                }
                else {
                    System.out.println("wrong id");
                }
            }
            else {
                System.out.println("wrong id");
            }
        }catch (Exception e) {
            System.out.println("wrong id");
        }
    }
    void print(ArrayList<Record> list) {
        String hr = "";
        for(int i = 0; i < 60; i++) {
            hr += "-";
        }
        String label = "ID" + spaces(2);
        label += "City" + spaces(17);
        label += "Date" + spaces(7);
        label += spaces(2) + "Days";
        label += spaces(5) + "Price Vehicle";

        System.out.println(hr);
        System.out.println(label);
        System.out.println(hr);
        for(int i = 0; i < list.size(); i++){
            String output = "";
            output += list.get(i).id + spaces(1);
            output += list.get(i).city + spaces(21 - list.get(i).city.length());
            output += list.get(i).date + spaces(1);
            output += spaces(6 - Integer.toString(list.get(i).days).length()) + list.get(i).days;
            output += spaces(10 - String.format("%.2f",list.get(i).price).length()) + String.format("%.2f",list.get(i).price) + spaces(1);
            output += list.get(i).vehicle + spaces(7 - list.get(i).vehicle.length());
            System.out.println(output);
        }
        System.out.println(hr);
    }
    void sort() {
        Collections.sort(database);
        System.out.println("sorted");
    }
    void find(String data) {
        try {
            double price = Double.parseDouble(data);
            ArrayList<Record> found = new ArrayList<Record>();
            for(int i = 0; i < database.size(); i++) {
                if (price >=  database.get(i).price) {
                    found.add(database.get(i));
                }
            }
            this.print(found);
        }catch (Exception e) {
            System.out.println("wrong price");
        }
    }
    void avg() {
        double avg = 0;
        for(int i = 0; i < database.size(); i++) {
            avg += database.get(i).price;
        }
        avg = avg / database.size();
        System.out.printf("average=%.2f\n", avg);
    }
    boolean validate(String data, boolean newRecord) {
        boolean validation = true;
        while(true) {
            String[] fields = data.split(";", 6);
            int id, days;
            double price;
            if (fields.length != 6) {
                System.out.println("wrong field count");
                return false;
            }
            //id validation
            try {
                id = Integer.parseInt(fields[0].trim());
            } catch (Exception e) {
                System.out.println("wrong id");
                return false;
            }
            boolean exists = false;
            for (int i = 0; i < database.size(); i++) {
                if (database.get(i).id == id) {
                    exists = true;
                }
            }
            if (!(id >= 100 && id <= 1000 && (exists ^ newRecord))) {
                System.out.println("wrong id");
                validation = false;
            }
            //city formation
            String city = fields[1].trim();
            String[] cityArr = city.split("\\s+");
            city = "";
            if (cityArr.length == 1) {
                city += cityArr[0].substring(0, 1).toUpperCase();
                city += cityArr[0].substring(1).toLowerCase();
            } else {
                for (int i = 0; i < cityArr.length; i++) {
                    city += cityArr[i].substring(0, 1).toUpperCase();
                    city += cityArr[i].substring(1).toLowerCase();
                    if (i != cityArr.length - 1) {
                        city += " ";
                    }
                }
            }
            //date validation
            String date = fields[2].trim();
            if (date.isEmpty() && newRecord) {
                System.out.println("wrong date");
                return false;
            }
            if (date.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
                int y = Integer.parseInt(date.substring(6, 10));
                int m = Integer.parseInt(date.substring(3, 5));
                int d = Integer.parseInt(date.substring(0, 2));
                //System.out.println(y + " " + m + " " + d);
                if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12) {

                    if (d >= 1 && d <= 31) {
                        return true;
                    } else {
                        System.out.println("wrong date");
                        return false;
                    }
                } else if (m == 4 || m == 6 || m == 9 || m == 11) {
                    if (d >= 1 && d <= 30) {
                        return true;
                    } else {
                        System.out.println("wrong date");
                        return false;
                    }
                } else if (m == 2) {
                    if (y % 4 == 0 && d >= 1 && d <= 29) {
                        return true;
                    } else if (y % 4 == 0 && d >= 1 && d <= 28) {
                        return true;
                    }
                } else {
                    //System.out.println("yes");
                    System.out.println("wrong date");
                    return false;
                }
            } else {
                System.out.println("wrong date");
                return false;
            }
            //days validation
            if (fields[3].trim().isEmpty() && newRecord) {
                System.out.println("wrong day count");
                return false;
            }
            try {
                days = Integer.parseInt(fields[3].trim());
                if (days < 0) {
                    System.out.println("wrong day count");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("wrong day count");
                return false;
            }
            //price validation
            if (fields[4].trim().isEmpty() && newRecord) {
                System.out.println("wrong price");
                return false;
            }
            try {
                price = Double.parseDouble(fields[4].trim());
                if (price < 0) {
                    System.out.println("wrong price");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("wrong price");
                return false;
            }
            //vehicle validation
            String vehicle = fields[5].trim();
            if (vehicle.isEmpty() && newRecord) {
                System.out.println("wrong vehicle");
                return false;
            }
            if (!(vehicle.equals("PLANE") || vehicle.equals("BUS") || vehicle.equals("BOAT")) || vehicle.equals("TRAIN")) {
                System.out.println("wrong vehicle");
                return false;
            }
        }
        System.out.println(validation);
        if(validation) {
            if(newRecord) {
                database.add(new Record(id, city, date, days, price, vehicle));
                System.out.println("yes");
            }
            else {
                int i = 0;
                while(i < database.size()) {
                    if(database.get(i).id == id) {
                        break;
                    }
                    i++;
                }
                if(!fields[1].isEmpty()){database.get(i).city = city;}
                if(!fields[2].isEmpty()){database.get(i).date = date;}
                if(!fields[3].isEmpty()){database.get(i).days = days;}
                if(!fields[4].isEmpty()){database.get(i).price = price;}
                if(!fields[5].isEmpty()){database.get(i).vehicle = vehicle;}
            }
        }
        return validation;
    }
}
class Record implements Comparable<Record> {
    int id, days;
    String city, date, vehicle;
    double price;
    Record(int id, String city, String date, int days, double price, String vehicle) {
        this.id = id;
        this.city = city;
        this.date = date;
        this.days = days;
        this.price = price;
        this.vehicle = vehicle;
    }

    public int compareTo(Record o) {
        int d1 = Integer.parseInt(this.date.split("/")[0]), d2 = Integer.parseInt(o.date.split("/")[0]);
        int m1 = Integer.parseInt(this.date.split("/")[1]), m2 = Integer.parseInt(o.date.split("/")[1]);
        int y1 = Integer.parseInt(this.date.split("/")[2]), y2 = Integer.parseInt(o.date.split("/")[2]);
        if(y1 > y2) {
            return 1;
        }
        else if(y1 == y2) {
            if(m1 > m2) {
                return 1;
            }
            else if(m1 == m2){
                if(d1 > d2) {
                    return 1;
                }
                else if(d1 == d2) {
                    return 0;
                }
                else  {
                    return -1;
                }
            }
            else {
                return -1;
            }
        }
        else {
            return -1;
        }
    }
}