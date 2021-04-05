import javax.annotation.processing.SupportedSourceVersion;
import javax.xml.crypto.Data;
import java.util.*;
import java.io.*;
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        Database db = new Database("src/db.csv");
        db.load();
        loop:while(true) {
            System.out.print(">>");
            String input = sc.nextLine();
            String[] command = input.split("\\s+", 2);
            switch (command[0]) {
                case "add":
                    if(command.length == 2) {
                        db.update(command[1], true, true);
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
                        db.update(command[1], false, true);
                    }
                    else {
                        System.out.println("wrong field count");
                    }
                    break;
                case "exit":
                    db.export();
                    break loop;
                default:
                    System.out.println("wrong command");
            }
        }
        sc.close();
    }
}
class Database {
    private String spaces(int number) {
        String spaces = "";
        for (int i = 0; i < number; i++) {
            spaces += " ";
        }
        return spaces;
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
                this.update(s, true, false);
            }
            sc.close();
            fin.close();
        } catch (Exception e) {
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
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void del(String data) {
        try {
            int id = Integer.parseInt(data);
            if (id > 99 && id < 1000) {
                boolean exists = false;
                int i = 0;
                while (i < database.size()) {
                    if (database.get(i).id == id) {
                        exists = true;
                        break;
                    }
                    i++;
                }
                if (exists) {
                    database.remove(i);
                    System.out.println("deleted");
                } else {
                    System.out.println("wrong id");
                }
            } else {
                System.out.println("wrong id");
            }
        } catch (Exception e) {
            System.out.println("wrong id");
        }
    }

    void print(ArrayList<Record> list) {
        String hr = "";
        for (int i = 0; i < 60; i++) {
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
        for (int i = 0; i < list.size(); i++) {
            String output = "";
            output += list.get(i).id + spaces(1);
            output += list.get(i).city + spaces(21 - list.get(i).city.length());
            output += list.get(i).date + spaces(1);
            output += spaces(6 - Integer.toString(list.get(i).days).length()) + list.get(i).days;
            output += spaces(10 - String.format("%.2f", list.get(i).price).length()) + String.format("%.2f", list.get(i).price) + spaces(1);
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
            for (int i = 0; i < database.size(); i++) {
                if (price >= database.get(i).price) {
                    found.add(database.get(i));
                }
            }
            this.print(found);
        } catch (Exception e) {
            System.out.println("wrong price");
        }
    }

    void avg() {
        double avg = 0;
        for (int i = 0; i < database.size(); i++) {
            avg += database.get(i).price;
        }
        avg = avg / database.size();
        System.out.printf("average=%.2f\n", avg);
    }

    void update(String input, boolean insertNew, boolean showMessage) {
        String[] data = input.split(";", 6);
        if (data.length != 6) {
            System.out.println("wrong field count");
            return;
        }
        for (int i = 0; i < 6; i++) {
            data[i] = data[i].trim();
        }
        int id = 0, days = 0;
        double price = 0;
        String city = data[1], date = data[2], vehicle = data[5];
        //ID pārbaude
        for (int i = 0; i < 6; i++) {
            data[i] = data[i].trim();
        }
        if (!data[0].matches("[1-9]{1}[0-9]{2}")) {
            System.out.println("wrong id");
            return;
        }
        id = Integer.parseInt(data[0]);
        boolean exists = false;
        Record current = null;
        for (int i = 0; i < database.size(); i++) {
            if (id == database.get(i).id) {
                exists = true;
                current = database.get(i);
            }
            //System.out.println(id + " " + database.get(i).id + " " + (boolean)(id == database.get(i).id));
        }
        if (exists && insertNew) {
            System.out.println("wrong id");
            return;
        } else if (!exists && !insertNew) {
            System.out.println("wrong id");
            return;
        }
        //Pilsētas formātēšana
        if(insertNew) {
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
        }
        //Datuma pārbaude
        if (insertNew && data[2].isEmpty()) {
            System.out.println("wrong date");
            return;
        }
        if (insertNew) {
            if (!date.matches("[0-9]{2}/[0-9]{2}/[1-9]{1}[0-9]{3}")) {
                System.out.println("wrong date");
                return;
            }
            int d = Integer.parseInt(data[2].substring(0, 2));
            int m = Integer.parseInt(data[2].substring(3, 5));
            int y = Integer.parseInt(data[2].substring(6, 10));
            if (d > 31 || d < 1 || m < 1 || m > 12) {
                System.out.println("wrong date");
                return;
            } else if ((m == 4 || m == 6 || m == 9 || m == 11) && d > 30) {
                System.out.println("wrong date");
                return;
            } else if (m == 2 && (y % 4 == 0 && d > 29 || y % 4 != 0 && d > 28)) {
                System.out.println("wrong date");
                return;
            }
        }
        //Dienu skaita pārbaude
        if (insertNew && data[3].isEmpty()) {
            System.out.println("wrong day count");
            return;
        }
        if (insertNew) {
            if (new Scanner(data[3]).hasNextInt() && new Scanner(data[3]).nextInt() > 0) {
                days = Integer.parseInt(data[3]);
            } else {
                System.out.println("wrong day count");
                return;
            }
        }
        //Cenas pārbaude
        if (insertNew && data[4].isEmpty()) {
            System.out.println("wrong price");
            return;
        }
        if (insertNew) {
            if (new Scanner(data[4]).hasNextDouble() && new Scanner(data[4]).nextDouble() > 0) {
                price = Double.parseDouble(data[3]);
            } else {
                System.out.println("wrong price");
                return;
            }
        }
        //Transportlīdzekļa pārbaude
        if (insertNew && vehicle.isEmpty()) {
            System.out.println("wrong vehicle");
            return;
        }
        if (insertNew) {
            if (!(vehicle.equals("BUS") || vehicle.equals("TRAIN") || vehicle.equals("PLANE") || vehicle.equals("BOAT"))) {
                System.out.println("wrong vehicle");
                return;
            }
        }
        //
        if(insertNew) {
            database.add(new Record(id, city, date, days, price, vehicle));
            if(showMessage) {
                System.out.println("added");
            }
        }
        else {
            if(!data[1].isEmpty()) current.city = city;
            if(!data[2].isEmpty()) current.date = date;
            if(!data[3].isEmpty()) current.days = days;
            if(!data[4].isEmpty()) current.price = price;
            if(!data[5].isEmpty()) current.vehicle = vehicle;
            System.out.println("edited");
        }
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
        Date dt = new Date(y1, m1, d1);
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