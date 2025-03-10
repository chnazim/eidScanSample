package ae.emiratesid.idcard.toolkit.sample.task;

import android.nfc.Tag;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ae.emiratesid.idcard.toolkit.CardReader;
import ae.emiratesid.idcard.toolkit.ToolkitException;
import ae.emiratesid.idcard.toolkit.sample.ConnectionController;
import ae.emiratesid.idcard.toolkit.sample.logger.Logger;

public class ReaderConnectionTask extends AsyncTask<Void, Integer, Integer> {

    private final WeakReference<ReaderConnectionTask.ConnectToolkitListener> weakReference;
    private boolean isConnectFlag = true;
    private String message = "No message";
    private Tag tag;

    public ReaderConnectionTask(ReaderConnectionTask.ConnectToolkitListener listener, boolean isConnectFlag) {
        this.weakReference = new WeakReference<>(listener);
        this.isConnectFlag = isConnectFlag;
    }//ReaderConnectionTask
    public ReaderConnectionTask(ReaderConnectionTask.ConnectToolkitListener listener, boolean isConnectFlag , Tag tag) {
        this(listener,isConnectFlag);
        this.tag =  tag;

    }//ReaderConnectionTask

    @Override
    protected Integer doInBackground(Void... voids) {
        Logger.d("--------->-1");
        if (isConnectFlag) {
            try {
                CardReader cardReader =  null;
                Logger.d("--------->0");
                if(tag == null) {
                    cardReader = ConnectionController.initConnection();
                    Logger.d("--------->2");
                }//
                else{
                    Logger.d("--------->3");
                    cardReader =  ConnectionController.initConnection(tag);

                }
                if ( cardReader != null && cardReader.isConnected() ) {
                    Logger.d("doInBackground() connect successful ");
                    return 0;
                }//if()
                else {
                    message = "Connection failed . Couldn't fetch reader handle";
                    return 1;
                }//else
            } catch (ToolkitException e) {
                e.printStackTrace();
                message = e.getMessage();
                Logger.e("CardReaderConnectionTask():::" + e.getMessage());
                return 1;
            }//
        }//if()
        return 0;
    }//doInBackground()

    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        Logger.d("CardReaderConnectionTask::onPostExecute() " + status);
        if(weakReference != null && weakReference.get() != null)
            weakReference.get().onToolkitConnected(status, isConnectFlag, message);
    }

    public interface ConnectToolkitListener {
        void onToolkitConnected(int status, boolean isConnectFlag, String message);
    }//ConnectToolkitListener()
}
