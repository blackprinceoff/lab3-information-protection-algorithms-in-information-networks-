package org.author.demo;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Програма дешифрування");

        try {
            // таблиця заміни
            System.out.print("Файл з таблицею: ");
            String tableFile = scan.nextLine();
            if (tableFile.equals("")) {
                tableFile = "table.txt";
            }

            Map<String, Character> table = new HashMap<>();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(tableFile), "UTF-8")
            );
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }

                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    char bukva = parts[0].charAt(0);
                    String[] kody = parts[1].split(",");

                    for (int i = 0; i < kody.length; i++) {
                        String kod = kody[i].trim();
                        table.put(kod, bukva);
                    }
                }
            }
            br.close();
            System.out.println("OK, завантажено " + table.size() + " символів");

            // криптограма
            System.out.print("Файл з криптограмою: ");
            String cipherFile = scan.nextLine();
            if (cipherFile.equals("")) {
                cipherFile = "ciphertext.txt";
            }

            BufferedReader br2 = new BufferedReader(
                    new InputStreamReader(new FileInputStream(cipherFile), "UTF-8")
            );
            String crypta = "";
            while ((line = br2.readLine()) != null) {
                crypta += line;
            }
            br2.close();

            // дешифрую
            System.out.println("Дешифрую...");
            String result = "";

            for (int i = 0; i < crypta.length(); i = i + 2) {
                String kod = crypta.substring(i, i + 2);

                if (table.containsKey(kod)) {
                    result = result + table.get(kod);
                } else {
                    result = result + "?";
                }
            }

            System.out.println("\n--- Результат ---");
            System.out.println(result);

            // зберігаємо
            System.out.print("Куди зберегти результат: ");
            String outFile = scan.nextLine();
            if (outFile.equals("")) {
                outFile = "plaintext.txt";
            }

            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8")
            );
            bw.write(result);
            bw.close();
            System.out.println("Збережено!");

            // аналіз
            System.out.print("\nАналіз частот (y/n)? ");
            String ans = scan.nextLine();

            if (ans.equals("y") || ans.equals("")) {

                // частота букв
                Map<Character, Integer> freq = new HashMap<>();
                for (int i = 0; i < result.length(); i++) {
                    char c = result.charAt(i);
                    if (freq.containsKey(c)) {
                        freq.put(c, freq.get(c) + 1);
                    } else {
                        freq.put(c, 1);
                    }
                }

                // сортуємо
                List<Map.Entry<Character, Integer>> list = new ArrayList<>(freq.entrySet());
                list.sort(new Comparator<Map.Entry<Character, Integer>>() {
                    public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) {
                        return o2.getValue() - o1.getValue();
                    }
                });

                System.out.println("\nТабл. 2. Частота літер у відкритому тексті");
                System.out.println("Літера\tЧастота");
                for (Map.Entry<Character, Integer> e : list) {
                    char c = e.getKey();
                    double p = (e.getValue() * 100.0) / result.length();

                    if (c == ' ') {
                        System.out.printf("пробіл\t%.2f%%\n", p);
                    } else {
                        System.out.printf("%c\t%.2f%%\n", c, p);
                    }
                }

                // частота кодів
                Map<String, Integer> freqKod = new HashMap<>();
                for (int i = 0; i < crypta.length(); i = i + 2) {
                    String kod = crypta.substring(i, i + 2);
                    if (freqKod.containsKey(kod)) {
                        freqKod.put(kod, freqKod.get(kod) + 1);
                    } else {
                        freqKod.put(kod, 1);
                    }
                }

                List<Map.Entry<String, Integer>> list2 = new ArrayList<>(freqKod.entrySet());
                list2.sort(new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        return o2.getValue() - o1.getValue();
                    }
                });

                System.out.println("\nТабл. 3. Частота символів у криптограмі");
                System.out.println("Код\tЧастота");
                int total = crypta.length() / 2;
                for (Map.Entry<String, Integer> e : list2) {
                    double p = (e.getValue() * 100.0) / total;
                    System.out.printf("%s\t%.2f%%\n", e.getKey(), p);
                }

                // в файл
                BufferedWriter fw = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream("frequency_analysis.txt"), "UTF-8")
                );

                fw.write("Аналіз частот\n\n");
                fw.write("Табл. 2. Частота літер у відкритому тексті\n");
                fw.write("Літера\tЧастота, %\n");
                for (Map.Entry<Character, Integer> e : list) {
                    double p = (e.getValue() * 100.0) / result.length();
                    if (e.getKey() == ' ') {
                        fw.write("пробіл\t" + String.format("%.2f", p) + "\n");
                    } else {
                        fw.write(e.getKey() + "\t" + String.format("%.2f", p) + "\n");
                    }
                }

                fw.write("\nТабл. 3. Частота символів у криптограмі\n");
                fw.write("Символ\tЧастота, %\n");
                for (Map.Entry<String, Integer> e : list2) {
                    double p = (e.getValue() * 100.0) / total;
                    fw.write(e.getKey() + "\t" + String.format("%.2f", p) + "\n");
                }

                fw.write("\nВисновок: частоти відрізняються через пропорційну заміну\n");
                fw.close();

                System.out.println("\nЗбережено в frequency_analysis.txt");
            }

            System.out.println("Готово!");

        } catch (Exception e) {
            System.out.println("Помилка! " + e.getMessage());
        }

        scan.close();
    }
}