package com.soya.launcher.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ZTMIDGO on 2018/5/1.
 */

public class FileUtils {

    public static void copyAssets(AssetManager assetManager, String path, File outPath) throws IOException {
        String[] assets = assetManager.list(path);

        if (assets != null) {
            if (assets.length == 0) {
                copyFile(assetManager, path, outPath);
            } else {
                File dir = new File(outPath, path);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.v("copyAssets", "Failed to create directory " + dir.getAbsolutePath());
                    }
                }

                String[] var5 = assets;
                int var6 = assets.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String asset = var5[var7];
                    copyAssets(assetManager, path + "/" + asset, outPath);
                }
            }

        }
    }

    private static void copyFile(AssetManager assetManager, String fileName, File outPath) throws IOException {
        Log.v("copyFile", "Copy " + fileName + " to " + outPath);
        InputStream in = assetManager.open(fileName);
        OutputStream out = new FileOutputStream(outPath + "/" + fileName);
        byte[] buffer = new byte[4000];

        int read;
        while((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

        in.close();
        out.close();
    }

    public static void write(InputStream in, String filePath, String name) throws IOException {
        createPath(new File(filePath));
        int index;
        byte[] bytes = new byte[1024];
        OutputStream out = new FileOutputStream(filePath + "/" + name);
        while ((index = in.read(bytes)) != -1) {
            out.write(bytes, 0, index);
            out.flush();
        }
        in.close();
        out.close();
    }

    public static void createPath(File file){
        if (!file.exists()){
            file.mkdirs();
        }
    }

    public static String getFileSuffix(File file){
        return getFileSuffix(file.getName());
    }

    public static String getFileSuffix(String fileName){
        int index = fileName.lastIndexOf(".");
        return index == -1 ? null : fileName.substring(index);
    }

    public static String getFileNameWithoutSuffix(String fileName){
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static byte[] getBitmapBytes(Bitmap bitmap, Bitmap.CompressFormat format, int quality){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(format, quality, baos);
            byte[] data = baos.toByteArray();
            baos.close();
            return data;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteFile(String filePath){
        File file = new File(filePath);
        if (file.exists()){
            file.delete();
        }
    }

    public static void deleteAllFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }

        File[] files = file.listFiles();

        if(files == null)
            return;

        for(int i = 0; i < files.length; i++){
            File f = files[i];
            if(f.isFile()){
                f.delete();
            }else{
                deleteAllFile(f);
                f.delete();
            }
        }

        file.delete();
    }

    public static File renameFile(String filePath, String newName){
        File oldFile = new File(filePath);
        String basePath = oldFile.getParent();
        File newFile = new File(basePath + "/" +newName);
        oldFile.renameTo(newFile);
        return newFile;
    }

    public static File writeFile(byte[] data, String filePath, String fileName) throws IOException {
        createPath(new File(filePath));
        File file = new File(filePath+"/"+fileName);
        FileChannel fc = new FileOutputStream(file).getChannel();
        fc.write(ByteBuffer.wrap(data));
        fc.close();
        return file;
    }

    public static byte[] readStream(InputStream response) throws IOException {
        byte[] pool = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead = 0;
        try{
            while ((bytesRead = response.read(pool)) > 0){
                out.write(pool, 0, bytesRead);
            }
            return out.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            out.close();
            response.close();
        }
        return null;
    }

    public static byte[] readChannel(String filePath){
        try{
            FileChannel fc = new FileInputStream(filePath).getChannel();
            byte[] data = new byte[(int) fc.size()];
            fc.read(ByteBuffer.wrap(data));
            fc.close();
            return data;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readAssets(Context context, String name){
        try {
            InputStream in = context.getAssets().open(name);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
            return buffer;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFile(InputStream in, String filePath) throws IOException {
        byte[] pool = new byte[1024];
        FileOutputStream out = new FileOutputStream(new File(filePath));
        int bytesRead = 0;
        try{
            while ((bytesRead = in.read(pool)) > 0){
                out.write(pool, 0, bytesRead);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            out.close();
            in.close();
        }
    }

    public static String parseRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
