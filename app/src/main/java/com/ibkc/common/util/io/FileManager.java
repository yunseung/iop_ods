package com.ibkc.common.util.io;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;

import com.ibkc.common.Common;
import com.ibkc.common.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileManager {

    public enum FolderType {
        FOLDER_INTERNAL_ROOT,
        FOLDER_INTERNAL_CACHE,
        FOLDER_INTERNAL_FILES,
        //FOLDER_INTERNAL_OHTER,

        FOLDER_EXTERNAL_ROOT,
        FOLDER_EXTERNAL_CACHE,
        FOLDER_EXTERNAL_FILES,
        //FOLDER_EXTERNAL_OHTER,

        FOLDER_OHTER, //이때만 풀패스 경로를 받는다
    }

    public enum FolderMode {
        EXISTED_ONLY,     //이미 존재하는 폴더
        CREATE_IF_NEEDED,    //폴더가 없으면 만들고 있으면 가져옴. 상위폴더까지 모두 생성
        CREATE_WITH_DELETE,     //폴더존재 상관없이 새로 만든다. 하위폴더까지 모두 삭제후 생성
    }

    public enum FileMode {
        EXISTED_ONLY,     //이미 존재하는 파일
        CREATE_IF_NEEDED,    //파일이 없으면 만들고 있으면 가져옴
        CREATE_WITH_DELETE,     //파일존재 상관없이 새로 만든다
    }

    private static final String EXTERNAL_FORDER_NAME = "HKCapital";
    private static final String EXTERNAL_FORDER_CACHE_NAME = "cache";
    private static final String EXTERNAL_FORDER_FILES_NAME = "files";

    /**
     * 디렉토리가 없으면 생성하고, 있으면 반환한다 타입에 따른 분류
     *
     * @param context
     * @param type    디렉토리 유형, 디렉토리 반환 타입 디폴트는 CREATE_IF_NEEDED
     * @return FOLDER_OHTER는 NULL로 반환
     */
    public static File getDir(Context context, FolderType type) {
        return getDir(context, "", type, FolderMode.CREATE_IF_NEEDED);
    }

    /**
     * 디렉토리가 없으면 생성하고, 있으면 반환한다 타입에 따른 분류
     *
     * @param context
     * @param type    디렉토리 유형
     * @param mode    디렉토리 반환 유형
     * @return FOLDER_OHTER는 NULL로 반환
     */
    public static File getDir(Context context, FolderType type, FolderMode mode) {
        return getDir(context, "", type, mode);
    }

    /**
     * 디렉토리가 없으면 생성하고, 있으면 반환한다 타입에 따른 분류
     *
     * @param context
     * @param dirPath 디렉터리 이후 부터의 패스를 받는다, FOLDER_OHTER 일때는 풀패스를 입력한다
     * @param type    디렉토리 유형
     * @param mode    디렉토리 반환 유형
     * @return FOLDER_OHTER의 경우 dirPath가 없을시 NULL
     */
    public static File getDir(Context context, String dirPath, FolderType type, FolderMode mode) {
        File rootDir = null;
        //String internalRootDirPath = "/data/data/" + context.getPackageName() + "/";
        //String externalRootDirPath = Environment.getExternalStorageDirectory().getPath() + "/" + EXTERNAL_FORDER_NAME+ "/";

        switch (type) {
            case FOLDER_INTERNAL_ROOT:
                //폴더 경로 유효성 검사
                if (StringUtils.isTrimEmpty(dirPath)) {
                    rootDir = new File(getInternalRootDirPath(context));
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);

                } else {
                    rootDir = new File(getInternalRootDirPath(context) + dirPath);
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);
                }
                break;

            case FOLDER_INTERNAL_CACHE:
                if (StringUtils.isTrimEmpty(dirPath)) {
                    rootDir = context.getCacheDir();
                } else {
                    rootDir = new File(context.getCacheDir().getPath() + dirPath);
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);
                }
                break;

            case FOLDER_INTERNAL_FILES:
                if (StringUtils.isTrimEmpty(dirPath)) {
                    rootDir = context.getFilesDir();
                } else {
                    rootDir = new File(context.getFilesDir().getPath() + dirPath);
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);
                }
                break;

//			case FOLDER_INTERNAL_OHTER:
//				if(StringUtil.isTrimEmpty(dirPath)) return rootDir = null;
//				rootDir = new File(dirPath);
//				break;

            case FOLDER_EXTERNAL_ROOT:
                if (StringUtils.isTrimEmpty(dirPath)) {
                    rootDir = new File(getExternalRootDirPath(context));
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);
                } else {
                    rootDir = new File(getExternalRootDirPath(context) + dirPath);
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);
                }
                break;

            case FOLDER_EXTERNAL_CACHE:
                if (StringUtils.isTrimEmpty(dirPath)) {
                    rootDir = new File(new File(Environment.getExternalStorageDirectory(), EXTERNAL_FORDER_NAME), EXTERNAL_FORDER_CACHE_NAME);
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);
                } else {
                    File temp = new File(new File(Environment.getExternalStorageDirectory(), EXTERNAL_FORDER_NAME), EXTERNAL_FORDER_CACHE_NAME);
                    rootDir = new File(temp.getPath() + dirPath);
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);
                }
                break;

            case FOLDER_EXTERNAL_FILES:
                if (StringUtils.isTrimEmpty(dirPath)) {
                    rootDir = new File(new File(Environment.getExternalStorageDirectory(), EXTERNAL_FORDER_NAME), EXTERNAL_FORDER_FILES_NAME);
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);
                } else {
                    File temp = rootDir = new File(new File(Environment.getExternalStorageDirectory(), EXTERNAL_FORDER_NAME), EXTERNAL_FORDER_FILES_NAME);
                    rootDir = new File(temp.getPath() + dirPath);
                    rootDir.setExecutable(false, true);
                    rootDir.setReadable(true);
                    rootDir.setWritable(false, true);
                }

                break;

//			case FOLDER_EXTERNAL_OHTER:
//				if(StringUtil.isTrimEmpty(dirPath)) return rootDir = null;
//				rootDir = new File(dirPath);
//				break;

            case FOLDER_OHTER:
                if (StringUtils.isTrimEmpty(dirPath)) return rootDir = null;
                rootDir = new File(dirPath);
                rootDir.setExecutable(false, true);
                rootDir.setReadable(true);
                rootDir.setWritable(false, true);
                break;

            default:
                rootDir = null;
                break;
        }

        switch (mode) {
            case EXISTED_ONLY:
                if (rootDir != null) {
                    if (!rootDir.exists()) {
                        rootDir = null;
                        if(Common.debug)
                            Log.d("test1234", "Directory is not Exists");
                    }
                }
                break;
            case CREATE_IF_NEEDED:
                if (rootDir != null) {
                    if (!rootDir.exists()) {
                        rootDir.setExecutable(false, true);
                        rootDir.setReadable(true);
                        rootDir.setWritable(false, true);
                        if (!rootDir.mkdirs()) {
                            rootDir = null;
                            if(Common.debug)
                                Log.d("test1234", "Create not Directory");
                        }
                    }
                }
                break;
            case CREATE_WITH_DELETE:
                break;

            default:
                rootDir = null;
                break;
        }

        return rootDir;
    }

    /**
     * 내부 저장소 루트 경로반환
     *
     * @param context
     * @return
     */
    public static String getInternalRootDirPath(Context context) {
        String internalRootDirPath = "/data/data/" + context.getPackageName() + "/";
        return internalRootDirPath;
    }

    /**
     * 외부 저장소 루트 경로반환
     *
     * @param context
     * @return
     */
    public static String getExternalRootDirPath(Context context) {
        String externalRootDirPath = Environment.getExternalStorageDirectory().getPath() + "/" + EXTERNAL_FORDER_NAME + "/";
        return externalRootDirPath;
    }

    public static String getExternalDirPath(Context context, FolderType type) {
        String externalRootDirPath = Environment.getExternalStorageDirectory().getPath() + "/" + EXTERNAL_FORDER_NAME + "/";
        String result = null;
        switch (type) {
            case FOLDER_INTERNAL_ROOT:
                result = getInternalRootDirPath(context);
                break;
            case FOLDER_INTERNAL_CACHE:
                result = context.getCacheDir().getAbsolutePath();
                break;
            case FOLDER_INTERNAL_FILES:
                result = context.getFilesDir().getAbsolutePath();
                break;
            case FOLDER_EXTERNAL_ROOT:
                result = externalRootDirPath;
                break;
            case FOLDER_EXTERNAL_CACHE:
                result = externalRootDirPath + EXTERNAL_FORDER_CACHE_NAME;
                break;
            case FOLDER_EXTERNAL_FILES:
                result = externalRootDirPath + EXTERNAL_FORDER_FILES_NAME;
                break;
            case FOLDER_OHTER:
                break;
        }
        return result;
    }

    /**
     * 파일을 반환한다
     *
     * @param context
     * @param filePath 파일 경로는 필수, 풀 패스가 아닌 /파일이름 형태로 적는다.
     * @param type     폴더가 없는경우 생성할수 있도록 타입을 지정한다 폴더 모드 디폴트는 CREATE_IF_NEEDED
     * @param fileMode 파일 반환 유형
     * @return
     */
    public static File getFile(Context context, String filePath, FolderType type, FileMode fileMode) {
        return getFile(context, filePath, type, FolderMode.CREATE_IF_NEEDED, fileMode);
    }

    /**
     * 파일이 없으면 생성해서 반환하거나, 있으면 그냥 가져온다. 타입에 따른 분류
     *
     * @param context
     * @param filePath   파일 경로는 필수, 풀 패스가 아닌 /파일이름 형태로 적는다.
     * @param type       폴더가 없는경우 생성할수 있도록 타입을 지정한다.
     * @param folderMode 폴더 반환 유형
     * @param fileMode   파일 반환 유형
     * @return
     */
    public synchronized static File getFile(Context context, String filePath, FolderType type, FolderMode folderMode, FileMode fileMode) {
        File nfile = null;
        File dir = null;
        String dirPath = "";

        if (StringUtils.isTrimEmpty(filePath)) return nfile = null;
        if (filePath.contains("/") && filePath.substring(0, filePath.lastIndexOf("/")).length() > 0) {
            dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
            dir = getDir(context, dirPath, type, folderMode);
        } else {
            dir = getDir(context, type, folderMode);
        }

        if (dir != null) {
            filePath = filePath.substring(filePath.lastIndexOf("/"));
            nfile = new File(dir.getPath() + filePath);
            if(Common.debug)
                Log.d("test1234", "path --> " + nfile.getPath());
        }

        switch (fileMode) {
            case EXISTED_ONLY:
                if (nfile != null) {
                    if (!nfile.exists()) {
                        nfile = null;
                        if(Common.debug)
                            Log.d("test1234", "file is not Exists");
                    }
                }
                break;

            case CREATE_IF_NEEDED:
                if (nfile != null) {
                    if (!nfile.exists()) {
                        try {
                            nfile.createNewFile();
                        } catch (IOException e) {
                            nfile = null;
                            if(Common.debug)
                                Log.d("test1234", "CREATE_IF_NEEDED--> " + "Create not file");
                        }
                    }
                }
                break;

            case CREATE_WITH_DELETE:
                if (nfile != null) {
                    if (nfile.exists()) {
                        nfile.delete();
                        if(Common.debug)
                            Log.d("test1234", "CREATE_ONLY--> " + "file delete");
                    }

                    try {
                        nfile.createNewFile();
                    } catch (IOException e) {
                        nfile = null;
                        if(Common.debug)
                            Log.d("test1234", "CREATE_ONLY--> " + "Create not file");
                    }
                }
                break;

            default:
                nfile = null;
                break;
        }

        return nfile;
    }

    /**
     * 해당디렉토리안에 위치한 파일을 삭제한다.
     *
     * @param path
     * @return
     */
    public synchronized static boolean deleteFileByPath(String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 해당 파일을 삭제한다.
     *
     * @param f
     * @return
     */
    public synchronized static boolean deleteFile(File f) {
        boolean ret = false;
        if (f.exists()) {
            ret = f.delete();
        }
        return ret;
    }

    /**
     * 내부저장소 모든 디렉토리, 파일 삭제
     *
     * @param context
     * @return
     */
    public synchronized static boolean deleteInternalRootdir(Context context) {
        File rootdir = getDir(context, FolderType.FOLDER_INTERNAL_ROOT, FolderMode.EXISTED_ONLY);
        if (rootdir != null) {
            return deleteAllFileWithFolder(context, rootdir);
        } else {
            return false;
        }
    }

    /**
     * 외부저장소 모든 파일 삭제
     *
     * @param context
     * @return
     */
    public synchronized static boolean deleteExternalRootdir(Context context) {
        File rootdir = getDir(context, FolderType.FOLDER_EXTERNAL_ROOT, FolderMode.EXISTED_ONLY);
        if (rootdir != null) {
            return deleteAllFileWithFolder(context, rootdir);
        } else {
            return false;
        }
    }

    /**
     * 해당디렉토리의 모든파일과 해당디렉토리를 지운다.
     *
     * @param context
     * @param targetFolder
     * @return
     */
    public synchronized static boolean deleteAllFileWithFolder(Context context, File targetFolder) {
        if (targetFolder.isDirectory()) {
            for (File child : targetFolder.listFiles()) {
                deleteAllFileWithFolder(context, child);
            }
        }

        boolean deleteFolder = targetFolder.delete();
        return deleteFolder;
    }

    /**
     * 해당디렉토리에 있는 파일들을 삭제한다
     *
     * @param dir getInternalCacheDir(context), getInternalFilesDir(context) ...
     * @return 삭제 성공여부
     */
    public synchronized static boolean deleteFiles(File dir) {
        File filedir = dir;
        File[] files = filedir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * 인풋스트림을 파일로 저장한다.
     * UI쓰레드에서 사용불가
     *
     * @param input
     * @param file
     * @return
     */
    public static synchronized File streamToFile(InputStream input, File file) {
        OutputStream output;
        try {
            output = new FileOutputStream(file);

            byte data[] = new byte[1024];
            int count = 0;

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return file;
    }

    public static byte[] convertFiletoByteArray(File file) {
        byte[] result = null;

        FileInputStream ios = null;
        ByteArrayOutputStream ous = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }

            result = ous.toByteArray();
        } catch (FileNotFoundException e) {
            result = null;
            Common.printException(e);
        } catch (IOException e) {
            result = null;
            Common.printException(e);
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
                Common.printException(e);
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
                Common.printException(e);
            }
        }
        return result;
    }

    /**
     * 파일을 복사한다.
     *
     * @param srcFile
     * @param destFile
     * @return false if fail
     */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * 스트림으로부터 파일을 복사한다
     *
     * @param inputStream
     * @param destFile
     * @return
     */
    public synchronized static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                    Common.printException(e);
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * String을 파일로 저장한다.
     * UI쓰레드에서 사용불가
     * String데이터가 큰경우 문제가 발생할수있다.
     *
     * @param file
     * @return
     */
    public static File stringToFile(String inputString, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(inputString.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Common.printException(e);
                }
            }
        }
        return file;
    }


    /**
     * 압축을 해제 한다
     *
     * @param zip_file
     * @param directory
     */
    public static boolean extractZipFiles(String zip_file, String directory) {
        boolean result = false;

        byte[] data = new byte[2048];
        ZipEntry entry = null;
        ZipInputStream zipstream = null;
        FileOutputStream out = null;

        if (!(directory.charAt(directory.length() - 1) == '/'))
            directory += "/";

        File destDir = new File(directory);
        boolean isDirExists = destDir.exists();
        destDir.setExecutable(false, true);
        destDir.setReadable(true);
        destDir.setWritable(false, true);
        boolean isDirMake = destDir.mkdirs();

        Log.d("FileManager", "extractZipFiles isDirMake/isDirExists = " + isDirMake + "/" + isDirExists + "/" + destDir.getAbsolutePath());

        try {
            zipstream = new ZipInputStream(new FileInputStream(zip_file));

            while ((entry = zipstream.getNextEntry()) != null) {
                int read = 0;
                File entryFile = new File(directory + entry.getName());

                if (!entryFile.exists()) {
                    boolean isFileMake = entryFile.createNewFile();
                    Log.d("FileManager", "extractZipFiles createNewFile = " + isFileMake + "/" + entryFile.getAbsolutePath());
                }

                out = new FileOutputStream(entryFile);
                while ((read = zipstream.read(data, 0, 2048)) != -1)
                    out.write(data, 0, read);

                zipstream.closeEntry();
            }

            result = true;
        } catch (FileNotFoundException e) {
            Common.printException(e);
            result = false;
        } catch (IOException e) {
            Common.printException(e);
            result = false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Common.printException(e);
                }
            }

            if (zipstream != null) {
                try {
                    zipstream.close();
                } catch (IOException e) {
                    Common.printException(e);
                }
            }
        }

        return result;
    }

    /**
     * 압축파일을 원하는 위치에 풀어준다.
     * @param context
     * @param file
     * @return
     */
//	public static ArrayList<String> unZipFile(Context context, File file, String destPath){
//		ArrayList<String> file_names = new ArrayList<String>();
//		try {
//			ZipFile zipFile = new ZipFile(file);
//
//		    zipFile.extractAll(destPath);
//
//		    @SuppressWarnings("unchecked")
//			List<FileHeader> fileHeaders = zipFile.getFileHeaders();
//
//		    for(int i=0; i < fileHeaders.size(); i++){
//		    	file_names.add(fileHeaders.get(i).getFileName());
//		    }
//
//		} catch (ZipException e) {
//			e.printStackTrace();
//			return null;
//		}
//		return file_names;
//	}

    /**
     * 파일확장자를 가져온다.(소문자로 변환)
     *
     * @param path
     * @return
     */
    public static String getFileExtention(String path) {
        if (path == null) {
            return null;
        }
        String fileExtension = path.toLowerCase().substring(path.lastIndexOf(".") + 1, path.length());

        return fileExtension;
    }

    /**
     * @return 0이면 지원하지않는 파일포멧, 1이면
     */
    public static boolean fileExtensionCheck(String filePath, String Extention) {
        if (!StringUtils.isEmpty(filePath)) {
            String fileExtension = filePath.toLowerCase().substring(filePath.lastIndexOf(".") + 1, filePath.length());

            if (StringUtils.equalDataNull(fileExtension, Extention)) return true;
        }

        return false;
    }


    /**
     * Crash 발생시 파일 생성
     *
     * @param context
     * @param name    파일 이름
     * @return
     */
    public static File getFileForCrashSave(Context context, String name) {
        return getFile(context, "/crash/" + name + ".txt", FolderType.FOLDER_EXTERNAL_FILES, FolderMode.CREATE_IF_NEEDED, FileMode.CREATE_IF_NEEDED);
    }

    /**
     * provider uri 로부터 파일 생성하기
     *
     * @param context
     * @param uri     content provider uri
     * @return
     */
    public static File getFileFromUri(Context context, Uri uri) {
        File file = null;
        Cursor cursor = null;
        try {
            if (uri == null) {
                return null;
            }
            if ("file".equals(uri.getScheme())) {
                return new File(uri.getPath());
            }

            cursor = context.getContentResolver().query(uri, null, null, null, null);
            int nameIdx = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            String name = cursor.getString(nameIdx);
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is != null) {
                file = FileManager.getFile(context,
                        "/temp/" + name,
                        FolderType.FOLDER_INTERNAL_CACHE,
                        FolderMode.CREATE_IF_NEEDED,
                        FileMode.CREATE_IF_NEEDED);

                if (file != null) {
                    FileManager.copyToFile(is, file);
                }
            }
        } catch (FileNotFoundException e) {
            Common.printException(e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return file;
    }
}
