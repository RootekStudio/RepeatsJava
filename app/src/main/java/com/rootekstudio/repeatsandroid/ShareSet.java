package com.rootekstudio.repeatsandroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ShareSet
{
    public static void zip(List<String> files, File zipFile)
    {
        BufferedInputStream origin = null;
        try
        {
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            byte data[] = new byte[1024];

            for (int i = 0; i < files.size(); i++)
            {
                String file = files.get(i);
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, 1024);

                ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/")));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
            }

            origin.close();
            out.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
