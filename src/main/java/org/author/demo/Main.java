package org.author.demo;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Програма дешифрування");
        System.out.println("Лабораторна робота №3\n");

        try {
            // читаємо таблицю
            System.out.print("Файл з таблицею (Enter = table.txt): ");
            String tableFile = scan.nextLine();
            if (tableFile.equals("")) {
                tableFile = "table.txt";
            }

            Map<String, Character> table = new HashMap<>();

            // завантажуємо таблицю з файлу
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(tableFile), "UTF-8")
            );
            String line;
            while ((line = br.readLine()) != null) {
                // НЕ робимо trim() для всього рядка!

                // пропускаємо пусті рядки та коментарі
                if (line.trim().equals("") || line.trim().startsWith("#")) {
                    continue;
                }

                // розбираємо рядок типу а: 98,85
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    // НЕ робимо trim для букви, бо може бути пробіл!
                    String bukvaStr = parts[0];

                    // перевіряємо чи не пустий
                    if (bukvaStr.length() == 0) {
                        continue;
                    }

                    char bukva = bukvaStr.charAt(0);
                    String[] kody = parts[1].split(",");

                    for (int i = 0; i < kody.length; i++) {
                        String kod = kody[i].trim();
                        if (kod.length() > 0) {
                            table.put(kod, bukva);
                        }
                    }
                }
            }
            br.close();
            System.out.println("Таблиця завантажена, символів: " + table.size());

            // читаємо криптограму
            System.out.print("Файл з криптограмою (Enter = ciphertext.txt): ");
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
            System.out.println("Криптограма завантажена, довжина: " + crypta.length());

            // дешифруємо
            System.out.println("\nДешифрую...");
            String result = "";
            int nevidomyh = 0;

            for (int i = 0; i < crypta.length(); i = i + 2) {
                if (i + 2 <= crypta.length()) {
                    String kod = crypta.substring(i, i + 2);

                    if (table.containsKey(kod)) {
                        result = result + table.get(kod);
                    } else {
                        result = result + "?";
                        nevidomyh++;
                        System.out.println("Невідомий код: " + kod);
                    }
                }
            }

            if (nevidomyh > 0) {
                System.out.println("Увага: знайдено " + nevidomyh + " невідомих кодів");
            } else {
                System.out.println("Всі коди розпізнані успішно!");
            }

            System.out.println("Готово!\n");
            System.out.println("Результат:");
            System.out.println(result);

            // зберігаємо у файл
            System.out.print("\nФайл для збереження (Enter = plaintext.txt): ");
            String outFile = scan.nextLine();
            if (outFile.equals("")) {
                outFile = "plaintext.txt";
            }

            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8")
            );
            bw.write(result);
            bw.close();
            System.out.println("Збережено в " + outFile);

            // аналіз частот
            System.out.print("\nЗробити аналіз частот? (y/n): ");
            String ans = scan.nextLine();

            if (ans.equals("y") || ans.equals("")) {
                System.out.println("\nПідрахунок частот...");

                // рахуємо частоту букв
                Map<Character, Integer> chastota = new HashMap<>();
                for (int i = 0; i < result.length(); i++) {
                    char c = result.charAt(i);
                    if (chastota.containsKey(c)) {
                        chastota.put(c, chastota.get(c) + 1);
                    } else {
                        chastota.put(c, 1);
                    }
                }

                // сортуємо
                List<Map.Entry<Character, Integer>> list = new ArrayList<>(chastota.entrySet());
                list.sort(new Comparator<Map.Entry<Character, Integer>>() {
                    public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) {
                        return o2.getValue() - o1.getValue();
                    }
                });

                System.out.println("\nТабл. 2. Частота літер у відкритому тексті");
                System.out.println("==========================================");
                System.out.println("Літера\t\tЧастота, %");
                System.out.println("------------------------------------------");
                for (Map.Entry<Character, Integer> e : list) {
                    char bukva = e.getKey();
                    double procent = (e.getValue() * 100.0) / result.length();

                    if (bukva == ' ') {
                        System.out.printf("пробіл\t\t%.2f%%\n", procent);
                    } else if (bukva == '?') {
                        System.out.printf("?\t\t%.2f%% (невідомі)\n", procent);
                    } else {
                        System.out.printf("%c\t\t%.2f%%\n", bukva, procent);
                    }
                }
                System.out.println("==========================================");

                // рахуємо частоту кодів
                Map<String, Integer> chastotaKodov = new HashMap<>();
                for (int i = 0; i < crypta.length(); i = i + 2) {
                    if (i + 2 <= crypta.length()) {
                        String kod = crypta.substring(i, i + 2);
                        if (chastotaKodov.containsKey(kod)) {
                            chastotaKodov.put(kod, chastotaKodov.get(kod) + 1);
                        } else {
                            chastotaKodov.put(kod, 1);
                        }
                    }
                }

                // сортуємо коди
                List<Map.Entry<String, Integer>> list2 = new ArrayList<>(chastotaKodov.entrySet());
                list2.sort(new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        return o2.getValue() - o1.getValue();
                    }
                });

                System.out.println("\nТабл. 3. Частота символів у криптограмі");
                System.out.println("==========================================");
                System.out.println("Символ\t\tЧастота, %");
                System.out.println("------------------------------------------");
                int vsogo = crypta.length() / 2;
                for (Map.Entry<String, Integer> e : list2) {
                    double procent = (e.getValue() * 100.0) / vsogo;
                    System.out.printf("%s\t\t%.2f%%\n", e.getKey(), procent);
                }
                System.out.println("==========================================");

                // зберігаємо аналіз у файл
                BufferedWriter fw2 = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream("frequency_analysis.txt"), "UTF-8")
                );

                fw2.write("АНАЛІЗ ЧАСТОТ СИМВОЛІВ\n\n");

                fw2.write("Табл. 2. Частота літер у відкритому тексті\n");
                fw2.write("Літера\tЧастота, %\n");
                for (Map.Entry<Character, Integer> e : list) {
                    double procent = (e.getValue() * 100.0) / result.length();
                    if (e.getKey() == ' ') {
                        fw2.write("пробіл\t" + String.format("%.2f", procent) + "\n");
                    } else {
                        fw2.write(e.getKey() + "\t" + String.format("%.2f", procent) + "\n");
                    }
                }

                fw2.write("\nТабл. 3. Частота символів у криптограмі\n");
                fw2.write("Символ\tЧастота, %\n");
                for (Map.Entry<String, Integer> e : list2) {
                    double procent = (e.getValue() * 100.0) / vsogo;
                    fw2.write(e.getKey() + "\t" + String.format("%.2f", procent) + "\n");
                }

                fw2.write("\nВисновок: Частоти символів у криптограмі суттєво відрізняються\n");
                fw2.write("від частот у відкритому тексті завдяки використанню шифру\n");
                fw2.write("пропорційної заміни. Найчастіші символи розподілені більш рівномірно.\n");

                fw2.close();

                System.out.println("\nАналіз збережено в frequency_analysis.txt");
            }

            System.out.println("\nПрограма завершена!");

        } catch (Exception e) {
            System.out.println("Помилка: " + e.getMessage());
            e.printStackTrace();
        }

        scan.close();
    }
}