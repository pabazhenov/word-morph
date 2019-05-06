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
import java.util.concurrent.Callable;

/**
 *
 * @author Bazhenov_PA
 */
public class WordMorph implements Callable<String> {

    private File dictfile;
    private String charset;
    private String root;

    WordMorph(String theroot, File file, String chrst) {
        this.dictfile = file;
        this.charset = chrst;
        this.root = theroot;
    }

    public String getTheMorphs() throws IOException {
        BufferedReader br = Files.newBufferedReader(this.dictfile.toPath(), Charset.forName(this.charset));
        System.out.println("Opening file: " + this.dictfile.toPath());
        String strLine;
        String endings = "";
        String morphs = "";
        while ((strLine = br.readLine()) != null) {
            //Read endings of the word
            if (strLine.startsWith("{")) {
                endings = new Dictionary().parseEndings(strLine);
            } else {
                // Read root of the word
                if (this.root.equals(strLine)) {
                    String[] endingsarray = endings.split(",");
                    for (String ending : endingsarray) {
                        morphs += strLine + ending + ",";
                    }
                } 
            }
        }
        br.close();
        return morphs;
    }

    @Override
    public String call() throws Exception {
        return getTheMorphs();
    }
}
