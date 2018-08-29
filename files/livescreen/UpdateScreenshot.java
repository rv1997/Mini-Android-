package me.pc.remotecontrolpc.livescreen;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;

import me.pc.remotecontrolpc.MainActivity;
import me.pc.remotecontrolpc.FileAPI;
import me.pc.remotecontrolpc.CallbackReceiver;


public abstract class UpdateScreenshot extends AsyncTask<Void, Void, String> implements CallbackReceiver {
    @Override
    protected String doInBackground(Void... voids) {
        FileOutputStream fos = null;
        String path = new FileAPI().getExternalStoragePath();
        path = path + "/RemoteControlPC/screenshot.png";

        File file = new File(path);
        File dirs = new File(file.getParent());
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        try {
            if (MainActivity.clientSocket != null) {
                if (MainActivity.objectInputStream == null) {
                    MainActivity.objectInputStream = new ObjectInputStream(
                            MainActivity.clientSocket.getInputStream());
                }
                fos = new FileOutputStream(file);
                byte buffer[] = new byte[4096];
                int fileSize = (int) MainActivity.objectInputStream.readObject();
                int read = 0;
                int remaining = fileSize;
                while ((read = MainActivity.objectInputStream.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                    remaining -= read;
                    fos.write(buffer, 0, read);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    protected void onPostExecute(String path) {
        receiveData(path);
    }
}
