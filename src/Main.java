//201RDB171 Eduards Ēvele 9. grupa
import java.util.*;
import java.io.*;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Database db = new Database("db.csv");
        if(!db.load()) {
            System.out.println("error");
            return;
        }
        loop:while(true) {
            System.out.print(">>>");
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
    private final String fname;
    Database(String name) {
        this.fname = name;
    }
    //Visu ierakstu saraksts
    ArrayList<Record> database = new ArrayList<>();

    boolean load() {
        FileReader fin;
        try {
            fin = new FileReader(fname);
            Scanner scan = new Scanner(fin);
            String s;
            while (scan.hasNextLine()) {
                s = scan.nextLine();
                this.update(s, true, false);
            }
            scan.close();
            fin.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    void export() {
        FileWriter fout;
        PrintWriter out;
        try {
            fout = new FileWriter(fname);
            out = new PrintWriter(fout);
            for (Record record : database) {
                out.println(record.id + ";" + record.city + ";" + record.date + ";" + record.days + ";" + record.price + ";" + record.vehicle);
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
        System.out.println("------------------------------------------------------------\n" +
                "ID  City                 Date         Days     Price Vehicle\n" +
                "------------------------------------------------------------");
        for (Record record : list) {
            String output = "";
            output += record.id + spaces(1);
            output += record.city + spaces(21 - record.city.length());
            output += record.date + spaces(1);
            output += spaces(6 - Integer.toString(record.days).length()) + record.days;
            output += spaces(10 - String.format("%.2f", record.price).length()) + String.format("%.2f", record.price) + spaces(1);
            output += record.vehicle + spaces(7 - record.vehicle.length());
            System.out.println(output);
        }
        System.out.println("------------------------------------------------------------");
    }
    private String spaces(int number) {
        return " ".repeat(Math.max(0, number));
    }

    void sort() {
        Collections.sort(database);
        System.out.println("sorted");
    }

    void find(String data) {
        try {
            double price = Double.parseDouble(data);
            if(price < 0){
                throw new Exception();
            }
            ArrayList<Record> found = new ArrayList<>();
            for (Record record : database) {
                if (price >= record.price) {
                    found.add(record);
                }
            }
            this.print(found);
        } catch (Exception e) {
            System.out.println("wrong price");
        }
    }

    void avg() {
        double avg = 0;
        for (Record record : database) {
            avg += record.price;
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
        int days = 0;
        double price = 0;
        StringBuilder city = new StringBuilder(data[1]);
        String date = data[2];
        String vehicle = data[5].toUpperCase();
        //ID pārbaude
        for (int i = 0; i < 6; i++) {
            data[i] = data[i].trim();
        }
        if (!data[0].matches("[1-9][0-9]{2}")) {
            System.out.println("wrong id");
            return;
        }
        int id = Integer.parseInt(data[0]);
        boolean exists = false;
        Record current = null;
        for (Record record : database) {
            if (id == record.id) {
                exists = true;
                current = record;
            }
        }
        if (exists && insertNew) {
            System.out.println("wrong id");
            return;
        } else if (!exists && !insertNew) {
            System.out.println("wrong id");
            return;
        }
        //Pilsētas nosaukuma formātēšana
        if(insertNew) {
            String[] cityArr = city.toString().split("\\s+");
            city = new StringBuilder();
            if (cityArr.length == 1) {
                city.append(cityArr[0].substring(0, 1).toUpperCase());
                city.append(cityArr[0].substring(1).toLowerCase());
            } else {
                for (int i = 0; i < cityArr.length; i++) {
                    city.append(cityArr[i].substring(0, 1).toUpperCase());
                    city.append(cityArr[i].substring(1).toLowerCase());
                    if (i != cityArr.length - 1) {
                        city.append(" ");
                    }
                }
            }
        }
        //Datuma pārbaude
        if (insertNew && data[2].isEmpty()) {
            System.out.println("wrong date");
            return;
        }
        if (!date.isEmpty()) {
            if (!date.matches("[0-9]{2}/[0-9]{2}/[1-9][0-9]{3}")) {
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
        if (!data[3].isEmpty()) {
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
        if (!data[4].isEmpty()) {
            if (new Scanner(data[4]).hasNextDouble() && new Scanner(data[4]).nextDouble() > 0) {
                price = Double.parseDouble(data[4]);
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
        if (!vehicle.isEmpty()) {
            if (!(vehicle.equals("BUS") || vehicle.equals("TRAIN") || vehicle.equals("PLANE") || vehicle.equals("BOAT"))) {
                System.out.println("wrong vehicle");
                return;
            }
        }
        //Ieraksta izveide vai rediģēšana
        if(insertNew) {
            database.add(new Record(id, city.toString(), date, days, price, vehicle));
            if(showMessage) {
                System.out.println("added");
            }
        }
        else {
            if(!data[1].isEmpty()) current.city = city.toString();
            if(!data[2].isEmpty()) current.date = date;
            if(!data[3].isEmpty()) current.days = days;
            if(!data[4].isEmpty()) current.price = price;
            if(!data[5].isEmpty()) current.vehicle = vehicle;
            System.out.println("edited");
        }
    }
}
//Ieraksta klase
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
    //Metode, kas ļauj kārtot ierakstus pēc datumiem
    public int compareTo(Record o) {
        int d1 = Integer.parseInt(this.date.split("/")[0]), d2 = Integer.parseInt(o.date.split("/")[0]);
        int m1 = Integer.parseInt(this.date.split("/")[1]), m2 = Integer.parseInt(o.date.split("/")[1]);
        int y1 = Integer.parseInt(this.date.split("/")[2]), y2 = Integer.parseInt(o.date.split("/")[2]);
        if (y1 <= y2) {
            if(y1 == y2) {
                if(m1 > m2) {
                    return 1;
                }
                else if(m1 == m2){
                    return Integer.compare(d1, d2);
                }
                else {
                    return -1;
                }
            }
            else {
                return -1;
            }
        } else {
            return 1;
        }
    }
}