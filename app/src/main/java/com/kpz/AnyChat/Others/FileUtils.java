package com.kpz.AnyChat.Others;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.kpz.AnyChat.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Comparator;
import java.util.Properties;


/**
 * 文件工具类
 * Created by Yang on 2015/8/27 027.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static boolean mkDirs(String dir) {
        File dirFile = new File(dir);
        return dirFile.exists() || dirFile.mkdirs();
    }

    public static boolean createFile(String filePath) {
        return createFile(false, filePath);
    }

    public static boolean createFile(boolean isForce, String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists() && isForce) {
                file.delete();
            }
            if (!file.exists()) {
                return file.createNewFile();
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 打开或创建文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static File openOrCreateFile(String filePath) throws IOException {
        return openOrCreateFile(filePath, false);
    }

    public static File openOrCreateFile(String filePath, boolean isForce) throws IOException {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        String dir = filePath.substring(0, filePath.lastIndexOf(File.separator));
        if (!mkDirs(dir)) {
            Log.i(TAG, "目录不存在，创建失败：" + dir);
            return null;
        }
        if (!createFile(isForce, filePath)) {
            return null;
        }
        return new File(filePath);
    }

    /**
     * 加载属性参数
     *
     * @param context
     * @param filePath
     * @return
     */
    public static Properties loadProperties(Context context, String filePath) {
        Properties props = new Properties();
        try {
            props.load(context.getAssets().open(filePath));
            return props;
        } catch (Exception e) {
            Log.e(TAG, "Could not find the properties file.");
            e.printStackTrace();
        }
        return props;
    }

    /**
     * 保存文件
     *
     * @param is
     * @param filePath
     * @return
     */
    public static File save(InputStream is, String filePath) {
        try {
            File file = openOrCreateFile(filePath);

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[8 * 1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            is.close();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 复制拷贝文件
     *
     * @param oriFilePath
     * @param filePath
     * @return
     */
    public static File copy(String oriFilePath, String filePath) {
        try {
            File src = new File(oriFilePath);
            if (!src.exists()) {
                return null;
            }
            return save(new FileInputStream(src), filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据扩展信息获取mimeType
     *
     * @param extension
     * @return
     */
    public static String getMimeTypeFromExtension(String extension) {
        String ret = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (TextUtils.isEmpty(ret)) {
            if ("amr".equals(extension)) {
                ret = "audio/amr-wb";
            } else {
                ret = "*/*";
            }
        }
        return ret;

    }

    /**
     * 打开文件视图
     *
     * @param context
     * @param path
     */
    public static void openAttachFile(Context context, String path) {
        File file = new File(path);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String fileExt = path.substring(path.lastIndexOf(".") + 1);
        String type = getMimeTypeFromExtension(fileExt);
        intent.setDataAndType(/* uri */Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "openAttachFile failed !! \n" + e.toString());
            ToastUtil.showShort(R.string.vim_fail_openFile, fileExt);
        }
    }

    /**
     * 内容写入文件中
     *
     * @param content
     */
    public synchronized static void write(String content, String filePath) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(new File(filePath), true)));
            writer.write(new String(content.getBytes(), "UTF-8"));
            writer.write("\r\n");
            writer.flush();
            content = null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
                writer = null;
            }

        }
    }

    // todo:
    public static String readAllContent(String filePath) {

        StringBuilder result = new StringBuilder();
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {

            try {
                FileInputStream input = new FileInputStream(file);
//                int length = input.available();
                byte[] bytes = new byte[1024 * 8];
                int len = -1;
                while ((len = input.read(bytes)) != -1) {
                    result.append(Base64.decode(bytes, 0, len, 0)); // todo: --- 编码？？？
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result.delete(0, result.length());
            } catch (IOException e) {
                e.printStackTrace();
                result.delete(0, result.length());
            } finally {
                return result.toString();
            }
        } else {
            return "";
        }

    }

    public static void openFile(Context context, String filePath, String fileName) {
        if (TextUtils.isEmpty(filePath))
            return;
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory())
            return;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showShort(String.format(context.getString(R.string.vim_fail_openFile), file.getName()));
        }
    }


    public static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".chm", "application/x-chm"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    public static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
    /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "")
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * 选择文件
     *
     * @param activity
     */
    public static void selectFile(Activity activity, int type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), type);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        String photoPath = "";
        if (context == null || uri == null) {
            return photoPath;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if ("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[]{split[1]};
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        } else {
            photoPath = getDataColumn(context, uri, null, null);
        }
        return photoPath;
    }


    /**
     * @version 1.0.0
     * @description 对文件进行时间排序
     */
    static class ComparatorByLastModified implements Comparator<File> {
        public int compare(File f1, File f2) {
            long diff = f2.lastModified() - f1.lastModified();
            if (diff > 0)
                return 1;
            else if (diff == 0)
                return 0;
            else
                return -1;
        }

        public boolean equals(Object obj) {
            return true;
        }
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (isExist(filePath)) {
            file.delete();
        }
    }

    public static boolean isFilePath(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        } else {
            File file = new File(path);
            return file.exists() && file.isFile();
        }
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return null;
    }
}
