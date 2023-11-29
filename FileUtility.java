package org.example.app.servlets;

import jakarta.servlet.http.Part;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

class FileUtility {
    private final String IMAGE_FILE_PATH = "D:\\Work\\Java\\Test JavaFX\\demoweb\\web\\images\\";
    private final String TEMP_FILE_PATH = "D:\\Work\\Java\\Test JavaFX\\demoweb\\temp\\";

    private static FileUtility instance;

    private FileUtility() {
//        deleteDirectory(new File(TEMP_FILE_PATH));
    }

    static FileUtility getInstance() {
        if (instance == null) return new FileUtility();
        else return instance;
    }
    public void createDirectory(){
        File temp = new File(TEMP_FILE_PATH);
        if (!temp.exists()) temp.mkdir();
    }
    private void deleteDirectory(File directory) {
        System.out.println("deleteDir");
        if (directory.exists()&& directory.isDirectory() && directory.list().length>0) {
            for (File f : Objects.requireNonNull(directory.listFiles())) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                }
                f.delete();
            }
        }
    }

    String loadLocalImage(Part filePart, final String fileName) {
        String name = "";
        if ((filePart == null) || (filePart.getSubmittedFileName().length() == 0)) {
            // Element "filePart" doesn't exist or no file has been selected there or the selected file is not an image
            return name;
        }
        String extension = parseExtension(filePart.getSubmittedFileName());
        if (fileName.length() == 0)
            name = parseName(filePart.getSubmittedFileName());
        else
            name = fileName + "." + extension;

        name = saveFileFromPart(filePart, IMAGE_FILE_PATH, name);
        if (name.length() == 0) return name;

        File file = new File(IMAGE_FILE_PATH + name);
        if (!isImage(file)) {
            file.delete();
            name = "";
            return name;
        }
//        file.renameTo(new File(IMAGE_FILE_PATH, name));
        return name;
    }

    String loadRemoteImage(String url, final String fileName) {
        System.out.println("loadRemoteImage");
        String name = "";
        if (url == null || url.length() == 0)
            return name;
        if (fileName.length() == 0)
            name = parseName(url);
        else name = fileName + "." + parseExtension(url);
        System.out.println(name);
//        String tempName = saveFileFromUrl(url, TEMP_FILE_PATH, name);
//        System.out.println(tempName);
//        File file = new File(TEMP_FILE_PATH + name);
//        if (tempName.length() == 0) {
//            file.delete();
//            return tempName;
//        }
//        if (!isImage(file)) {
//            file.delete();
//            return "";
//        }
//        file.renameTo(new File(IMAGE_FILE_PATH + name));
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOS = new FileOutputStream(IMAGE_FILE_PATH + name)) {
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
        } catch (IOException e) {
            System.out.println("exc1: "+e.getMessage());
            name="";
            return name;
        }
        File file = new File(IMAGE_FILE_PATH + name);
        try {
            Image img = ImageIO.read(file);
            if (img == null || img.getHeight(null) <= 0 || img.getWidth(null) <= 0) {
                System.out.println("The file is not an image");
                file.delete();
                return "";
            }
        } catch (Exception e) {
            System.out.println("exc2: "+e.getMessage());
            file.delete();
            return "";
        }
//        file.renameTo(new File(IMAGE_FILE_PATH + name));
        return name;
    }

    String loadTempImage(Part filePart, String oldFile) {
        String name = "";
        if ((filePart == null) || (filePart.getSubmittedFileName().length() == 0) || (!filePart.getContentType().contains("image"))) {
            // Element "filePart" doesn't exist or no file has been selected there or the selected file is not an image
            return name;
        }
        name = parseName(filePart.getSubmittedFileName());
        if (name.equalsIgnoreCase(oldFile))
            return "";

        name = saveFileFromPart(filePart, TEMP_FILE_PATH, name);
        if (name.length() == 0) return name;

        File file = new File(TEMP_FILE_PATH + name);
        if (!isImage(file)) {
            file.delete();
            name = "";
        }
        if (oldFile.length() > 0) {
            deleteTempFile(oldFile);
        }
        return name;
    }

    String restoreTempImage(String tempFileName, String fileName) {
        if (tempFileName.length()>0) {
            File f = new File(TEMP_FILE_PATH, tempFileName);
            if (!f.exists())
                return "";
            String name = (fileName.length() > 0) ? (fileName + "." + parseExtension(tempFileName)) : tempFileName;
            f.renameTo(new File(IMAGE_FILE_PATH + name));
            return name;
        }
        return "";
    }

    void deleteTempFile(String tempFileName) {
        File f = new File(TEMP_FILE_PATH + tempFileName);
        if (f.exists() && !f.isDirectory()) f.delete();
    }

    private String saveFileFromUrl(String url, String path, final String fname) {
        if (fname.length() == 0) {
            System.out.println("saveFileFromUrl ERROR: file name is empty");
            return "";
        }
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOS = new FileOutputStream(path + fname)) {
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
        } catch (IOException e) {
            System.out.println("exc1: " + e.getMessage());
            return "";
        }
        return fname;
    }

    private String saveFileFromPart(Part p, String path, final String fname) {
        if (fname.length() == 0) {
            System.out.println("saveFileFromPart ERROR: file name is empty");
            return "";
        }
        try {
            p.write(path + fname);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения файла " + path + fname);
            System.out.println(e.getMessage());
            File f = new File(path + fname);
            if (f.exists()) f.delete();
            return "";
        }
        return fname;
    }

    private boolean isImage(File f) {
        try {
            Image img = ImageIO.read(f);
            if (img == null || img.getHeight(null) <= 0 || img.getWidth(null) <= 0) {
                System.out.println("The file is not an image");
                return false;
            } else return true;
        } catch (Exception e) {
            System.out.println("exc2: " + e.getMessage());
            return false;
        }
    }

    static String parseExtension(String url) {
        Optional<String> extension = Optional.ofNullable(url)
                .filter(f -> f.contains(".")).map(f -> f.substring(url.lastIndexOf(".") + 1));
        return extension.isPresent() ? extension.get() : "";
    }

    static String parseName(String url) {
        int i = url.lastIndexOf('/');
        return i<0 ? url : url.substring(i+1);
    }
}
