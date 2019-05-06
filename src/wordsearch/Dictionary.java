/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wordsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Dictionary {

    private File[] dict;
    private String charset;
    private int NumberOfThreadPool;

    Dictionary() {
        File dictdirectory = new File("dictionary");
        this.dict = dictdirectory.listFiles();
        this.charset = "cp1251";
        this.NumberOfThreadPool = 1;
    }
    void setNumberOfThreadPool(int k) {
        this.NumberOfThreadPool = k;
    }
    String parseEndings(String str) {
        String endings = str;
        endings = endings.substring(1, endings.indexOf("} "));
        endings = endings.replaceAll("#", "");
        while (endings.contains("{") && endings.contains("}")) {
            String tempstring = "";
            String[] temparray;
            String letter = endings.substring(endings.indexOf("{") - 1, endings.indexOf("{"));
            temparray = endings.substring(endings.indexOf("{") + 1, endings.indexOf("}")).split(",");
            for (String s : temparray) {
                tempstring += letter + s + ",";
            }
            tempstring = tempstring.substring(0, tempstring.length() - 1);
            String tempreplace = endings.substring(endings.indexOf("{") - 1, endings.indexOf("}") + 1);
            endings = endings.replace(tempreplace, tempstring);
        }
        return endings;
    }

    String findRootOfTheWord(String theword) throws IOException {
        String result = "";
        /* Так посоны посоны, дело такое:
            Мы знаем искомое слово
            Надо получить: Слово без окончания
            Пример входных данных: Лягушка, Клавиатура, Папуас, Орхидея
            Пример выходных данных: Лягушк, Клаиватур, Папуас, Орхидея
            Алгоритм:
            Мы идем по файлам словаря и целенаправленно ищем наше слово
            (т.е. открываем файл, берем окончания, берем слова и складываем, пытаясь получить
            нужное слово)
            Когда слово найдено, мы возвращаем слово без окончания (которое есть в файле)
            А далее ебошим словоформы
         */
        BufferedReader br = null;
        for (File dictfile : this.dict) {
            br = Files.newBufferedReader(dictfile.toPath(), Charset.forName(this.charset));
            String strLine;
            String endings = "";
            while ((strLine = br.readLine()) != null) {
                //Read endings of the word
                if (strLine.startsWith("{")) {
                    endings = parseEndings(strLine);
                } else {
                    String[] endingsarray = endings.split(",");
                    for (String ending : endingsarray) {
                        if (theword.equals(strLine + ending)) {
                            result = strLine;
                        }
                    }
                }
            }
            br.close();
        }
        return result;
    }

    String getMorphs(String theword) throws IOException, InterruptedException, ExecutionException {
        String morphs = "";
        String theroot = findRootOfTheWord(theword);
        ExecutorService em = Executors.newFixedThreadPool(this.NumberOfThreadPool);
        List<Future<String>> MorphList = new ArrayList<Future<String>>();
        for (File dictfile : this.dict) {
            Future<String> future = em.submit(new WordMorph(theroot, dictfile, this.charset));
            MorphList.add(future);
        }
        for (Future<String> element : MorphList) {
                morphs += element.get();
        }
        em.shutdown();
        morphs = morphs.substring(0, morphs.length() - 1);
        return morphs;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // TODO code application logic here
        String theword = "орхидея";
        theword = theword.toLowerCase();
        System.out.println(new Dictionary().getMorphs(theword));
        System.out.println("de finalle");
    }
}
