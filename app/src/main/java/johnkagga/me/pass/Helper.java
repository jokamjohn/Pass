package johnkagga.me.pass;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by jokamjohn on 12/21/2015.
 */
public  class Helper {

    /**
     * Creating a an Alert Dialog
     * @param title String
     * @param message String
     */
    public static void alertDialog(Context context,String title,String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null);

        //Create the Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
