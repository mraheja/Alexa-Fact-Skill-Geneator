package alexafinesse;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AlexaFinesse {

    public static void main(String[] args) throws Exception {
        Scanner inp = new Scanner(System.in);

        System.out.println("ENTER NAME");
        String name = inp.nextLine();
        System.out.println("ENTER FACTS");
        ArrayList<String> facts = new ArrayList();
        String t;
        while (!"END".equals(t = inp.nextLine())) {
            facts.add(t);
        }

        System.out.println("CREATING DIRECTORIES...");
        String path = "Fun " + name;
        File src = new File("auto");
        File dest = new File(path);
        if (!dest.exists()) {
            copyFolder(src, dest);
        }

        System.out.println("MODIFYING INFORMATION FILES...");
        BufferedReader br = new BufferedReader(new FileReader(path + "/" + "launch.txt"));
        StringBuilder sb = new StringBuilder();

        while ((t = br.readLine()) != null) {
            sb.append(t.replace("[NAME]", name.toLowerCase())).append("\n");
        }

        PrintWriter out = new PrintWriter(path + "/" + "launch.txt");
        out.print(sb.toString());
        out.close();

        br = new BufferedReader(new FileReader(path + "/" + "interaction.txt"));
        sb = new StringBuilder();

        while ((t = br.readLine()) != null) {
            sb.append(t.replace("[NAME]", name.toLowerCase())).append("\n");
        }

        out = new PrintWriter(path + "/" + "interaction.txt");
        out.print(sb.toString());
        out.close();


        System.out.println("CREATING MAIN FILE...");
        br = new BufferedReader(new FileReader(path + "/" + "index.js"));
        sb = new StringBuilder();

        while ((t = br.readLine()) != null) {
            if (t.contains("STARTFACTS")) {
                for (int i = 0; i < facts.size(); i++) {
                    String e = facts.get(i);
                    sb.append("\"").append(e).append("\"");
                    if(i != facts.size()-1){
                        sb.append(",");
                    }
                    sb.append("\n");
                }
            } else {
                sb.append(t.replace("[NAME]", name.toLowerCase())).append("\n");
            }
        }

        out = new PrintWriter(path + "/" + "index.js");
        out.print(sb.toString());
        out.close();

        System.out.println("ZIPPING FOLDER...");
        zipDirectory(dest, path + ".zip");

        System.out.println("COMPLETE");

    }

    static void copyFolder(File src, File dest)
            throws IOException {

        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    static List<String> filesListInDir = new ArrayList<String>();

    static void zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                //System.out.println("Zipping "+filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile())
                filesListInDir.add(file.getAbsolutePath());
            else
                populateFilesList(file);
        }
    }

    static void zipSingleFile(File file, String zipFileName) {
        try {
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            //add a new Zip Entry to the ZipOutputStream
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            //Close the zip entry to write to zip file
            zos.closeEntry();
            //Close resources
            zos.close();
            fis.close();
            fos.close();
            System.out.println(file.getCanonicalPath() + " is zipped to " + zipFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
