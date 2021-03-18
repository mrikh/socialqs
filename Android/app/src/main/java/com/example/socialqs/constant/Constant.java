package com.example.socialqs.constant;

import java.io.File;
import java.util.ArrayList;

public class Constant {

    public static String[] videoExtensions = {".mp4",".ts",".mkv",".mov",
            ".3gp",".mv2",".m4v",".webm",".mpeg1",".mpeg2",".mts",".ogm",
            ".bup", ".dv",".flv",".m1v",".m2ts",".mpeg4",".vlc",".3g2",
            ".avi",".mpeg",".mpg",".wmv",".asf"};

    public static ArrayList<File> allMediaList = new ArrayList<>();

    public static int CAMERA_PERMISSION = 101;

    public static int FILE_ACCESS_PERMISSION = 102;

    public static int CAPTURE_PROFILE_IMAGE = 103;
}